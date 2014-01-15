/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.ui.widget.table;

import java.util.ArrayList;
import java.util.Iterator;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.CheckboxCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Check;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used by Table to edit and render columns that use CheckBox 
 * @author tschmidt
 *
 */
public class CheckCell implements CellEditor, CellRenderer, IsWidget, HasWidgets.ForIsWidget {

    /**
     * Widget used to edit the cell
     */
    private Check     editor;
    private boolean   query;
    private ColumnInt column;
    
    protected CheckboxCSS css;
    
    public CheckCell() {
    	editor = new Check();
    }
    
    /**
     * Constructor that takes the editor to be used for the cell.
     * 
     * @param editor
     */
    public CheckCell(Check editor) {
    	setEditor(editor);
    }
    
    public void setEditor(Check editor) {
        this.editor = editor;
        
        css = UIResources.INSTANCE.checkbox();
        css.ensureInjected();
        
        editor.setEnabled(true);
        editor.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				column.finishEditing();
			}
		});
    }
    
    public Object finishEditing() {

        return editor.isUnknown() ? null : new Boolean(editor.isChecked());
    }

    public ArrayList<Exception> validate(Object value) {
        return null;
    }
    
    /**
     * Returns the current widget set as this cells editor.
     */
    @SuppressWarnings("rawtypes")
	public void startEditing(Object value, Container container, NativeEvent event) {
        query = false;
        
        if(value == null)
        	editor.uncheck();
        else if(((Boolean)value) == true)
        	editor.check();
        else
        	editor.uncheck();

        if(Event.getTypeInt(event.getType()) == Event.ONCLICK) { 
            ClickEvent.fireNativeEvent(event, editor);
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            
                @Override
                public void execute() {
                    column.finishEditing();
                }
            });
        } else {
            container.setEditor(editor);
            DOM.setStyleAttribute(container.getElement(), "align", "center"); 
        }
    }
    
    @SuppressWarnings("rawtypes")
	public void startEditingQuery(QueryData qd, Container container, NativeEvent event) {        
        query = true;

        if(Event.getTypeInt(event.getType()) == Event.ONCLICK) { 
            ClickEvent.fireNativeEvent(event, editor);
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            
                @Override
                public void execute() {
                    column.finishEditing();
                }
            });
        } else {
            container.setEditor(editor);
            DOM.setStyleAttribute(container.getElement(), "align", "center"); 
        }        
    }
    
    /**
     * Gets Formatted value from editor and sets it as the cells display
     */
    public void render(HTMLTable table, int row, int col, Object value) {
        
        query = false;
        
        if(editor.getMode() == Check.Mode.TWO_STATE && value == null)
        	value = "N";
        
        render((Boolean)value,table,row,col);
    }
    
    public String display(Object value) {
        return null;
    }

    /**
     * Sets the QueryData to the editor and sets the Query string into the cell
     * text
     */
    public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
     
    }

    public boolean ignoreKey(int keyCode) {
        switch(keyCode) {
            case KeyCodes.KEY_ENTER :
                return true;
            default :
                return false;
        }
    }
    
    public Widget getWidget() {
    	return editor;
    }
    
	@Override
	public void setColumn(ColumnInt col) {
		this.column = col;
	}
	
	private void render(Boolean value, HTMLTable table, int row, int col) {
		String style;
		AbsolutePanel div;
		
        if(value == null)
        	style = css.Unknown();
        else if(value == true)
        	style = css.Checked();
        else
        	style = css.Unchecked();
        	
        div = new AbsolutePanel();
        div.setStyleName(style);
        table.setWidget(row, col, div);
        table.getCellFormatter().setHorizontalAlignment(row, col, HasAlignment.ALIGN_CENTER);
	}

	   public SafeHtml bulkRender(Object value) {
	        SafeHtmlBuilder builder = new SafeHtmlBuilder();
	        
	        builder.appendHtmlConstant("<td align='"+HasAlignment.ALIGN_CENTER.getTextAlignString()+"'>"+getCheckDiv((String)value).getElement().getString()+"</td>");
	        
	        return builder.toSafeHtml();
	    }

	    private AbsolutePanel getCheckDiv(String value) {
	        String style;
	        AbsolutePanel div;
	            
	        if(value == null)
	            style = css.Unknown();
	        else if("Y".equals(value))
	            style = css.Checked();
	        else
	            style = css.Unchecked();
	            
	        div = new AbsolutePanel();
	        div.setStyleName(style);
	        
	        return div;
	    }
	@Override
	public void add(Widget w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Widget> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void add(IsWidget w) {
		assert w instanceof Check;
		
		setEditor((Check)w);
	}

	@Override
	public boolean remove(IsWidget w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return null;
	}
  

}
