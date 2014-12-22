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
package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.table.ColumnInt;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class implements the CellRenderer and CellEditor interfaces and is used
 * to edit and render cells in a Table that is text only
 * 
 * @author tschmidt
 * 
 */
public class LabelCell<V> extends Cell<V> {
    
    /**
     * Widget used to edit the cell
     */
    private Label<V>  editor;
    
    public LabelCell() {
    	this.editor = new Label<V>();
    	
        setElement(Document.get().createDivElement());
    }
        
    /**
     * Constructor that takes the editor to be used for the cell.
     * 
     * @param editor
     */
    public LabelCell(Label<V> editor) {
        this.editor = editor;
    }
    
    
    /**
     * Gets Formatted value from editor and sets it as the cells display
     */
    public void render(HTMLTable table, int row, int col, V value) {
   		table.setText(row,col,display(value));
    }
    
    public void render(Element element, V value) {
    	element.setInnerText(display(value));
    }
    
//    public void render(T value) {
//    	render(getRenderElement(),(V)dataProvider.getValue(value));
//    }
    
    public Object getValue(V value) {
    	return value;
    }
    
    public String display(V value) {
        if(editor.getHelper().isCorrectType(value))
        	return editor.getHelper().format(value);
        else
        	return DataBaseUtil.toString(value);
    }
    
    public SafeHtml bulkRender(V value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        builder.appendHtmlConstant("<td>");
        builder.appendEscaped(display(value));
        builder.appendHtmlConstant("</td>");
        
        return builder.toSafeHtml();
    }

    public void renderQuery(HTMLTable table,
                            int frow,
                            int col,
                            QueryData qd) {
    	table.setText(frow, col, "");
        
    }

	@Override
	public ArrayList<Exception> validate(Object value) {

		 return editor.getHelper().validate(value);
	}


	@Override
	public Widget asWidget() {
		return (Widget)this;
	}	
	

    @Override
    public void setColumn(ColumnInt col) {
        // TODO Auto-generated method stub   
    }
    
    @Override
    protected void onAttach() {
    	super.onAttach();
    	Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
				setProxyElement(getElement().getParentElement());
				proxyElement.setInnerHTML(getElement().getInnerHTML());
				getElement().removeFromParent();
			}
		});
    }
        
}
