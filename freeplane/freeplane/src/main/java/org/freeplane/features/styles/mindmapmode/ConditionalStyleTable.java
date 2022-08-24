package org.freeplane.features.styles.mindmapmode;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.freeplane.core.ui.components.JComboBoxWithBorder;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.filter.FilterComposerDialog;
import org.freeplane.features.filter.FilterConditionEditor.Variant;
import org.freeplane.features.filter.condition.ASelectableCondition;
import org.freeplane.features.filter.condition.DefaultConditionRenderer;
import org.freeplane.features.styles.ConditionalStyleModel;
import org.freeplane.features.styles.IStyle;
import org.freeplane.features.styles.MapStyleModel;

class ConditionalStyleTable extends JTable {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
	private MapStyleModel styleModel;
	private DefaultConditionRenderer conditionRenderer;
    private FilterComposerDialog filterComposerDialog;
    private Variant filterComposerVariant;
	private ConditionalStyleModel conditionalStyleModel;
	
	@SuppressWarnings("serial")
    private class ConditionEditor extends AbstractCellEditor
    implements TableCellEditor{
		
		private JButton btn;
		public ConditionEditor() {
			super();
			btn = new JButton(){
				{
					setUI(BasicButtonUI.createUI(this));
				}

				@Override
                public Color getBackground() {
	                return getSelectionBackground();
                }

			};
        }

		private Object cellEditorValue;
		public Component getTableCellEditorComponent(final JTable table, final Object value, boolean isSelected, int row, int column) {
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cellEditorValue = filterComposer().editCondition((ASelectableCondition) value);
				    btn.removeActionListener(this);
				    fireEditingStopped();
				}
			});
	        return btn;
        }

		public Object getCellEditorValue() {
	        return cellEditorValue;
        }
		
		@Override
		public boolean isCellEditable(EventObject anEvent) {
			if (anEvent instanceof MouseEvent) {
				return ((MouseEvent) anEvent).getClickCount() >= 2;
			}
			return true;
		}
	}
	
	ConditionalStyleTable(MapStyleModel styleModel, ConditionalStyleModel conditionalStyleModel, TableModel tableModel, Variant filterComposerVariant) {
	    super(tableModel);
	    this.styleModel = styleModel;
		this.conditionalStyleModel = conditionalStyleModel;
        this.filterComposerVariant = filterComposerVariant;
	    setCellSelectionEnabled(false);
	    setRowSelectionAllowed(true);
	    final TableColumnModel columnModel = getColumnModel();
	    setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
//	    setSelectionBackground(DefaultConditionRenderer.SELECTED_BACKGROUND);
	    setRowHeight(UITools.getDefaultLabelFont().getSize() * 5 / 4);
		conditionRenderer = createConditionRenderer();
		columnModel.getColumn(1).setCellRenderer(conditionRenderer);
		columnModel.getColumn(1).setCellEditor(new ConditionEditor());
		final JComboBox styleBox = new JComboBoxWithBorder();
		styleBox.setEditable(false);
		columnModel.getColumn(2).setCellEditor(new DefaultCellEditor(styleBox){

			/**
             * 
             */
            private static final long serialVersionUID = 1L;
            {
            	setClickCountToStart(2);
            }

			@Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                                                         int column) {
				final Collection<IStyle> styles = ConditionalStyleTable.this.styleModel.getNodeStyles();
				final DefaultComboBoxModel boxContent = new DefaultComboBoxModel(styles.toArray());
				styleBox.setModel(boxContent);
	            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }
			
		});
		columnModel.getColumn(1).setPreferredWidth(300);
		columnModel.getColumn(2).setPreferredWidth(180);
		columnModel.getColumn(2).setCellRenderer(new DefaultStyleRenderer());
    }

	static DefaultConditionRenderer  createConditionRenderer() {
		return new DefaultConditionRenderer(TextUtils.getText("always"), true);
	}

	public MapStyleModel getStyles() {
	    return styleModel;
    }

    FilterComposerDialog filterComposer() {
        if(filterComposerDialog == null) {
            filterComposerDialog = createFilterComposer();
        }
        return filterComposerDialog;
    }

    private FilterComposerDialog createFilterComposer() {
        FilterComposerDialog filterComposerDialog = new FilterComposerDialog(filterComposerVariant, conditionalStyleModel);
        filterComposerDialog.addCondition(null);
        filterComposerDialog.setConditionRenderer(ConditionalStyleTable.createConditionRenderer());
        for(int i = 0; i < getRowCount(); i++){
            final ASelectableCondition condition = (ASelectableCondition)getValueAt(i, 1);
            filterComposerDialog.addCondition(condition);
        }
        return filterComposerDialog;
    }

}

