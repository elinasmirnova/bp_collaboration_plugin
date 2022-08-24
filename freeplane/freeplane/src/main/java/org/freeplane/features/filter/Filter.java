/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Dimitry Polivaev
 *
 *  This file author is Dimitry Polivaev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.filter;

import java.util.Objects;
import java.util.WeakHashMap;

import javax.swing.Icon;

import org.freeplane.core.extension.IExtension;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.features.filter.condition.ICondition;
import org.freeplane.features.filter.hidden.NodeVisibility;
import org.freeplane.features.filter.hidden.NodeVisibilityConfiguration;
import org.freeplane.features.map.MapModel;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.mode.Controller;

/**
 * @author Dimitry Polivaev
 */
public class Filter implements IExtension {
    
    public static Filter createTransparentFilter() {
		final ResourceController resourceController = ResourceController.getResourceController();
		return new Filter(null, false, resourceController.getBooleanProperty("filter.showAncestors"), resourceController.getBooleanProperty("filter.showDescendants"), false, null);
	}

	static class FilterInfoAccessor {
	    private final WeakHashMap<NodeModel, FilterInfo> filterInfos = new WeakHashMap<>();

	    FilterInfo getFilterInfo(NodeModel node) {
	        return filterInfos.computeIfAbsent(node, x -> new FilterInfo());
	    }

	}

	static public Filter createFilter(final ICondition condition, final boolean areAncestorsShown,
            final boolean areDescendantsShown, final boolean appliesToVisibleNodesOnly, Filter baseFilter) {
		return new Filter(condition, false, areAncestorsShown, areDescendantsShown, appliesToVisibleNodesOnly, baseFilter);
	}

	final private ICondition condition;
	final int options;

	private FilterInfoAccessor accessor;
    private final boolean hidesMatchingNodes;
    private final boolean appliesToVisibleNodesOnly;
    private final Filter baseFilter;

	public Filter(final ICondition condition, final boolean hidesMatchingNodes, final boolean areAncestorsShown,
	              final boolean areDescendantsShown, final boolean appliesToVisibleNodesOnly, Filter baseFilter) {
		super();
		this.condition = condition;
        this.hidesMatchingNodes = hidesMatchingNodes;
        this.appliesToVisibleNodesOnly = appliesToVisibleNodesOnly;
		this.accessor = new FilterInfoAccessor();

		int options = FilterInfo.FILTER_SHOW_AS_MATCHED;
		if (areAncestorsShown) {
			options += FilterInfo.FILTER_SHOW_AS_ANCESTOR;
		}
		if (areDescendantsShown) {
			options += FilterInfo.FILTER_SHOW_AS_DESCENDANT;
		}
		this.options = options;
		this.baseFilter = baseFilter;
	}

	void addFilterResult(final NodeModel node, final int flag) {
		getFilterInfo(node).add(flag);
	}

	protected boolean appliesToVisibleNodesOnly() {
		return appliesToVisibleNodesOnly;
	}

	static private Icon filterIcon;

	void displayFilterStatus() {
		if (filterIcon == null) {
			filterIcon = ResourceController.getResourceController().getIcon("/images/filter.svg");
		}
		if (getCondition() != null) {
			Controller.getCurrentController().getViewController().addStatusInfo("filter", null, filterIcon);
		}
		else {
			Controller.getCurrentController().getViewController().removeStatus("filter");
		}
	}

	public void calculateFilterResults(final MapModel map) {
	    this.accessor = new FilterInfoAccessor();
		final NodeModel root = map.getRootNode();
		resetFilter(root);
		checkNode(root);
		if (filterChildren(root, false, false)) {
			addFilterResult(root, FilterInfo.FILTER_SHOW_AS_ANCESTOR);
		}
	}

	public void calculateFilterResults(final NodeModel root) {
	    this.accessor = new FilterInfoAccessor();
	    applyFilter(root, false, false, false);
	}

	private boolean applyFilter(final NodeModel node,
	                            final boolean hasMatchingAncestor, final boolean hasHiddenAncestor,
	                            boolean hasMatchingDescendant) {
		final boolean conditionSatisfied =  (condition == null || condition.checkNode(node));
		final boolean matchesCombinedFilter;
		if(appliesToVisibleNodesOnly()) {
		    matchesCombinedFilter = conditionSatisfied  && baseFilter.isVisible(node);
		}
		else {
		    matchesCombinedFilter = conditionSatisfied;
		}
		resetFilter(node);
		if (hasMatchingAncestor) {
			addFilterResult(node, FilterInfo.FILTER_SHOW_AS_DESCENDANT);
		}
		if (matchesCombinedFilter) {
			hasMatchingDescendant = true;
			addFilterResult(node, FilterInfo.FILTER_SHOW_AS_MATCHED);
		}
		else {
			addFilterResult(node, FilterInfo.FILTER_SHOW_AS_HIDDEN);
		}
		boolean childrenHaveMatchingAncestor = hasMatchingAncestor || matchesCombinedFilter && ! node.isRoot();
        if (filterChildren(node, childrenHaveMatchingAncestor, 
		        !matchesCombinedFilter
		        || hasHiddenAncestor)) {
		    addFilterResult(node, FilterInfo.FILTER_SHOW_AS_ANCESTOR);
			hasMatchingDescendant = true;
		}
		return hasMatchingDescendant;
	}

	/*
	 * (non-Javadoc)
	 * @see freeplane.controller.filter.Filter#areAncestorsShown()
	 */
	public boolean areAncestorsShown() {
		return 0 != (options & FilterInfo.FILTER_SHOW_AS_ANCESTOR);
	}
	
	boolean areMatchingNodesHidden() {
	    return hidesMatchingNodes;
	}


	/*
	 * (non-Javadoc)
	 * @see freeplane.controller.filter.Filter#areDescendantsShown()
	 */
	public boolean areDescendantsShown() {
		return 0 != (options & FilterInfo.FILTER_SHOW_AS_DESCENDANT);
	}

	private boolean checkNode(final NodeModel node) {
		return condition == null || ! shouldRemainInvisible(node) && condition.checkNode(node);
	}

	private boolean shouldRemainInvisible(final NodeModel node) {
		return condition != null && appliesToVisibleNodesOnly() && !node.hasVisibleContent(baseFilter);
	}

	private boolean filterChildren(final NodeModel node,
	                               final boolean hasMatchingAncestor, final boolean hasHiddenAncestor) {
		boolean hasMatchingDescendant = false;
		for (final NodeModel child : node.getChildren()) {
			hasMatchingDescendant = applyFilter(child, hasMatchingAncestor, hasHiddenAncestor,
			    hasMatchingDescendant);
		}
		return hasMatchingDescendant;
	}

	public ICondition getCondition() {
		return condition;
	}

	public boolean canUseFilterResultsFrom(final Filter oldFilter) {
		return (! oldFilter.appliesToVisibleNodesOnly || appliesToVisibleNodesOnly)
		        && Objects.equals(condition, oldFilter.getCondition());
	}


    public void useFilterResultsFrom(Filter oldFilter) {
        accessor = oldFilter.accessor;
    }
    
	/*
	 * (non-Javadoc)
	 * @see
	 * freeplane.controller.filter.Filter#isVisible(freeplane.modes.MindMapNode)
	 */
	public boolean isVisible(final NodeModel node) {
		if(node.getExtension(NodeVisibility.class) == NodeVisibility.HIDDEN
				&& node.getMap().getRootNode().getExtension(NodeVisibilityConfiguration.class) != NodeVisibilityConfiguration.SHOW_HIDDEN_NODES)
			return false;
		if (condition == null || node.isRoot()) {
			return true;
		}
		FilterInfo filterInfo = getFilterInfo(node);
        return filterInfo.isNotChecked() || filterInfo.matches(this.options) != hidesMatchingNodes;
	}

	private void resetFilter(final NodeModel node) {
		getFilterInfo(node).reset();
	}

	public FilterInfo getFilterInfo(final NodeModel node) {
		return accessor.getFilterInfo(node);
	}

    public void showAsMatched(NodeModel node) {
        FilterInfo filterInfo = getFilterInfo(node);
        if(! filterInfo.matches(FilterInfo.FILTER_SHOW_AS_MATCHED)) {
            filterInfo.add(FilterInfo.FILTER_SHOW_AS_MATCHED);
            if(! filterInfo.matches(FilterInfo.FILTER_SHOW_AS_ANCESTOR))
                showAncestors(node);
            if(! filterInfo.matches(FilterInfo.FILTER_SHOW_AS_DESCENDANT))
                showDescendants(node);
        }
    }

    private void showAncestors(NodeModel node) {
        NodeModel parent = node.getParentNode();
        if(parent == null)
            return;
        FilterInfo filterInfo = getFilterInfo(parent);
        if(! filterInfo.matches(FilterInfo.FILTER_SHOW_AS_ANCESTOR)) {
            filterInfo.add(FilterInfo.FILTER_SHOW_AS_ANCESTOR);
            showAncestors(parent);
        }
    }

    private void showDescendants(NodeModel node) {
        for (NodeModel child : node.getChildren()) {
            FilterInfo filterInfo = getFilterInfo(child);
            filterInfo.add(FilterInfo.FILTER_SHOW_AS_DESCENDANT);
            showDescendants(child);
        }
    }
}
