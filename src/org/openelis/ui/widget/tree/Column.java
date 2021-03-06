/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.ui.widget.tree;

import java.util.Iterator;

import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.table.CellEditor;
import org.openelis.ui.widget.table.CellRenderer;
import org.openelis.ui.widget.table.ColumnInt;
import org.openelis.ui.widget.table.LabelCell;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a logical class used to describe a column in a Table
 * 
 * @author tschmidt
 * 
 */
public class Column implements ColumnInt, IsWidget {

    /**
     * Reference to the Table containing this column
     */
    protected Tree        tree;
    

    /**
     * name used to reference column and label for the Header
     */
    protected String       name, label, style;

    /**
     * width - default and current width minWidth - Minimum allowed width of the
     * column
     */
    protected int          width, minWidth;

    /**
     * Boolean flags used by column
     */
    protected boolean      enabled, resizable=true, isFiltered, isSorted, isSortable, isFilterable, required;


    public static class Builder {
    	
    	String name,label,style;
    	int width,minWidth = 15;
    	boolean enabled = true,
    	        resizable = true,
    	        isSortable, isFilterable, required;
    	
    	public Builder(int width) {
    		this.width = width;
    	}
    	
    	public Builder minWidth(int minWidth) {
    		this.minWidth = minWidth;
    		return this;
    	}
    	
    	public Builder name(String name) {
    		this.name = name;
    		return this;
    	}
    	
    	public Builder label(String label) {
    		this.label = label;
    		return this;
    	}
    	
    	public Builder style(String style) {
    		this.style = style;
    		return this;
    	}
    	
    	public Builder enabled(boolean enabled) {
    		this.enabled = enabled;
    		return this;
    	}
    	
    	public Builder resizable(boolean resizable) {
    		this.resizable = resizable;
    		return this;
    	}
    	
    	public Builder isSortable(boolean sortable) {
    		this.isSortable = sortable;
    		return this;
    	}
    	
    	public Builder isFilterable(boolean filterable) {
    		this.isFilterable = filterable;
    		return this;
    	}
    	
    	public Builder required(boolean required) {
    		this.required = required;
    		return this;
    	}
    	
    	public Column build() {
    		return new Column(this);
    	}
    }

    private Column() {
    	
    }
    
    private Column(Builder builder) {
        name = builder.name;
        label = builder.label;
        enabled = builder.enabled;
        resizable = builder.resizable;
        width = builder.width;
        minWidth = builder.minWidth;
        isSortable = builder.isSortable;
        isFilterable = builder.isFilterable;
        required = builder.required;
      
    }

    /**
     * Returns the Table that this Column is used in.
     * 
     * @return
     */
    public Tree getTree() {
        return tree;
    }

    /**
     * Sets the Table that this Column is used in.
     * 
     * @param tree
     */
    public Column setTree(Tree tree) {
        this.tree = tree;
        return this;
    }

    /**
     * Returns the name of set to this Column.
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name to be used by this Column
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the string used as the header for this Column
     * 
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the string to be used as the header for this Column
     * 
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the width being used by this Column.
     * 
     * @return
     */
    public int getWidth() {

        int totalWidth, lastColumn;

        if (tree == null)
            return minWidth;

        /*
         * If this is the last column calculate its width if the overall width 
         * will be less then the set width of the table
         */
        lastColumn = tree.getColumnCount() - 1;
        if (lastColumn >= 0 && tree.getColumnAt(lastColumn) == this) {
            totalWidth = tree.getXForColumn(lastColumn);
            if (totalWidth + width < tree.getWidthWithoutScrollbar())
                return tree.getWidthWithoutScrollbar() - totalWidth;
        }
     
        return width;
    }

    /**
     * Sets the width to be used by this Column
     * 
     * @param width
     */
    public void setWidth(int width) {
        this.width = Math.max(width, minWidth);
        if(tree != null)
        	tree.resize();
    }

    /**
     * Returns the Minimum width to be used by this Column.
     * 
     * @return
     */
    public int getMinWidth() {
        return minWidth;
    }

    /**
     * Sets the minimum width to be used by this Column.
     * 
     * @param minWidth
     */
    public Column setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    /**
     * Method used to check if this Column is enabled for editing.
     * 
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Method used to enable/disable this Column for editing.
     * 
     * @param enabled
     */
    public Column setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    /**
     * Method used to allow/disallow this Column to be resized.
     * 
     * @param resizable
     */
    public Column setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    /**
     * Method used to determine if this Column is allowed to be resized.
     * 
     * @return
     */
    public boolean isResizable() {
        return resizable;
    }
    
    @Override
    public void finishEditing() {
        tree.finishEditing();
    }
    
    /**
     * Method used to set the required flag for this column
     * @param required
     */
    public Column setRequired(boolean required) {
        this.required = required;
        return this;
    }
    
    /**
     * Method to determine if this column is required to have a value set in each row
     * @return
     */
    public boolean isRequired() {
        return required;
    }

    public void setStyle(String style) {
    	this.style = style;
    }
    
    public String getStyle() {
    	return style;
    }
    
	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return null;
	}

}
