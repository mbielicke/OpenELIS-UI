package org.openelis.ui.widget.table;

import java.util.HashMap;

import org.openelis.ui.widget.table.event.CellMouseOutEvent;
import org.openelis.ui.widget.table.event.CellMouseOverEvent;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

public class FlexTable extends com.google.gwt.user.client.ui.FlexTable {
    
    HashMap<String, HandlerRegistration> registeredCells = new HashMap<String,HandlerRegistration>();
    
    public FlexTable() {
        super();
        
        sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONCLICK);
    }
    
    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        
        Element td = super.getEventTargetCell(event);
        Element tr = td == null ? null : DOM.getParent(td);
        Element body = td == null ? null : DOM.getParent(tr);
        int row = TableRowElement.as(td.getParentElement()).getSectionRowIndex();
        int column = TableCellElement.as(td).getCellIndex();
        
        if(registeredCells.containsKey(row+":"+column)) {
            switch(event.getTypeInt()) {
                case Event.ONMOUSEOVER :
                    CellMouseOverEvent.fire(this, row, column, event.getClientX(), event.getClientY());
                    break;
                case Event.ONMOUSEOUT :
                    CellMouseOutEvent.fire(this, row, column, event.getClientX(), event.getClientY());
                    break;
            }
        }
        
    }
	
	public int getRowForEvent(NativeEvent event) {
	    
	    Element td = getEventTargetCell(Event.as(event));
	    if (td == null) {
	      return -1;
	    }

	    return TableRowElement.as(td.getParentElement()).getSectionRowIndex();
	}
	
	public int getColForEvent(NativeEvent event) {
	    Element td = getEventTargetCell(Event.as(event));
	    if (td == null) {
	      return -1;
	    }

	    return TableCellElement.as(td).getCellIndex();
	}
	
	public HandlerRegistration addCellMouseOverHandler(CellMouseOverEvent.Handler handler) {
	    HandlerRegistration handlerReg = addHandler(handler, CellMouseOverEvent.getType());
	    registeredCells.put(handler.row+":"+handler.col,handlerReg);
	    return handlerReg;
	}
	
	public void removeHandler(int row, int col) {
	    if(registeredCells.containsKey(row+":"+col))
	        registeredCells.remove(row+":"+col).removeHandler();
	}
	
	public HandlerRegistration addCellMouseOutHandler(CellMouseOutEvent.Handler handler) {
	    return addHandler(handler, CellMouseOutEvent.getType());
	}
	

}
