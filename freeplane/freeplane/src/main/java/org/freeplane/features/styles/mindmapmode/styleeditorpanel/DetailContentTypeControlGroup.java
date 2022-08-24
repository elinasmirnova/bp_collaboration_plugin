/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2016 jberry
 *
 *  This file author is jberry
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
package org.freeplane.features.styles.mindmapmode.styleeditorpanel;

import java.beans.PropertyChangeEvent;

import org.freeplane.core.resources.components.BooleanProperty;
import org.freeplane.core.resources.components.ComboProperty;
import org.freeplane.core.resources.components.IPropertyControl;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.text.DetailModel;
import org.freeplane.features.text.TextController;
import org.freeplane.features.text.mindmapmode.MTextController;

import com.jgoodies.forms.builder.DefaultFormBuilder;

/**
 * @author Joe Berry
 * Dec 1, 2016
 */
public class DetailContentTypeControlGroup implements ControlGroup {
	static final String NAME = "detailsContentType";

	private RevertingProperty mSetContentType;
	private ComboProperty mContentType;
	
	private ContentTypeChangeListener propertyChangeListener;
	
	private class ContentTypeChangeListener extends ControlGroupChangeListener {
		public ContentTypeChangeListener(final RevertingProperty mSet,final IPropertyControl mProperty) {
			super(mSet, mProperty);
		}

		@Override
		void applyValue(final boolean enabled, final NodeModel node, final PropertyChangeEvent evt) {
			MTextController.getController().setDetailsContentType(node, enabled ? mContentType.getValue() : null);
		}

		@Override
		void setStyleOnExternalChange(NodeModel node) {
		    String contentType = DetailModel.getDetailContentType(node);
			String viewContentType = TextController.getController().getDetailsContentType(node);
			
			mSetContentType.setValue(contentType != null);
			mContentType.setValue(viewContentType);
		}
        
        @Override
        void adjustForStyle(NodeModel node) {
            StylePropertyAdjuster.adjustPropertyControl(node, mSetContentType);
            StylePropertyAdjuster.adjustPropertyControl(node, mContentType);
        }
	}

	@Override
	public void setStyle(NodeModel node, boolean canEdit) {
		propertyChangeListener.setStyle(node);
	}

	@Override
	public void addControlGroup(DefaultFormBuilder formBuilder) {
        mSetContentType = new RevertingProperty();
        mContentType = new ComboProperty(NAME, MTextController.getController().getDetailContentTypes());
        propertyChangeListener = new ContentTypeChangeListener(mSetContentType, mContentType);
        mSetContentType.addPropertyChangeListener(propertyChangeListener);
        mContentType.addPropertyChangeListener(propertyChangeListener);
        mContentType.appendToForm(formBuilder);
        mSetContentType.appendToForm(formBuilder);
	}
}
