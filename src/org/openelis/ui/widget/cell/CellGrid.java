package org.openelis.ui.widget.cell;

import java.util.HashMap;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;

public class CellGrid  extends com.google.gwt.user.client.ui.FlexTable {
    
    HashMap<String, HandlerRegistration> registeredCells = new HashMap<String,HandlerRegistration>();
            
    int row,column,x,y;
  
    public CellGrid() {
        super();
        
        sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONCLICK);
        
    }
    
    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        
        if(getRowCount() == 0)
            return;            
        
        Element td = super.getEventTargetCell(event);
        
        if(td == null)
            return;
        
        row = TableRowElement.as(td.getParentElement()).getSectionRowIndex();
        column = TableCellElement.as(td).getCellIndex();
        
        switch(event.getTypeInt()) {
            case Event.ONMOUSEOVER :
                CellMouseOverEvent.fire(this, row, column, event.getClientX(), event.getClientY());
                break;
            case Event.ONMOUSEOUT :
                CellMouseOutEvent.fire(this, row, column, event.getClientX(), event.getClientY());
                break;
                
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
