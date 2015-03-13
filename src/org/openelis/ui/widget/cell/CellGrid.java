package org.openelis.ui.widget.cell;

import java.util.HashMap;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * This class is is a FlexTable that has been extended to register and
 * fire events on all cells or individual cells and is the base container
 * for all cell based widgets.
 */
public class CellGrid  extends FlexTable {
    
	Timer clickTimer;
    CellGrid source = this;
    CellHandler cellHandler = new CellHandler();
   
    @Override
    public void onBrowserEvent(final Event event) {
        super.onBrowserEvent(event);
        
        if(getRowCount() == 0 && getTd(event) == null)
            return;
        
        switch(event.getTypeInt()) {
        	case Event.ONMOUSEOVER :
        		fireCellMouseOver(event);
        		break;
        	case Event.ONMOUSEOUT :
        		fireCellMouseOut(event);
        		break;
        	case Event.ONCLICK :
        		fireCellClick(event);
        		break;
        	case Event.ONDBLCLICK :
        		fireCellDoubleClick(event);
        		break;
        }
    }
    
    private void fireCellMouseOver(Event event) {
    	if (getEventTargetCell(event) != null) {
    		CellMouseOverEvent.fire(this, getRow(event), getColumn(event), event.getClientX(), event.getClientY());
    	}
    }
    	
	
    private void fireCellMouseOut(Event event) {
    	if (getEventTargetCell(event) != null) {
    		CellMouseOutEvent.fire(this, getRow(event), getColumn(event), event.getClientX(), event.getClientY());
    	}
    }
    
    private void fireCellClick(final Event event) {
    	final int row = getRow(event);
        final int column = getColumn(event);
    	final boolean ctrlKey = event.getCtrlKey();
    	final boolean shiftKey = event.getShiftKey();
    	
    	if(clickTimer != null) 
    		clickTimer.cancel();

    	clickTimer = new Timer() {
    		@Override
    		public void run() {
    			CellClickedEvent.fire(source, row, column, ctrlKey, shiftKey,event);
    		}
    	};
    	clickTimer.schedule(250);
    }
    
    public Element createCell(int row, int column) {
    	prepareCell(row,column);
    	return getFlexCellFormatter().getElement(row, column);
    }
    
    private Element getTd(Event event) {
    	return getEventTargetCell(event);
    }
    
    private int getRow(Event event) {
    	 return TableRowElement.as(getTd(event).getParentElement()).getSectionRowIndex();
    }
    
    private int getColumn(Event event) {
    	return TableCellElement.as(getTd(event)).getCellIndex();
    }
    
    private void fireCellDoubleClick(Event event) {
    	if(clickTimer != null) { 
    		clickTimer.cancel();
    		clickTimer = null;
    	}
    	CellDoubleClickedEvent.fire(this, getRow(event), getColumn(event));
    }
    
	public HandlerRegistration addCellMouseOverHandler(CellMouseOverEvent.Handler handler) {
		sinkEvents(Event.ONMOUSEOVER);
	    return addHandler(handler, CellMouseOverEvent.getType());
	}
	
	public HandlerRegistration addCellMouseOutHandler(CellMouseOutEvent.Handler handler) {
		sinkEvents(Event.ONMOUSEOUT);
	    return addHandler(handler, CellMouseOutEvent.getType());
	}
	
	public HandlerRegistration addCellClickedHandler(CellClickedHandler handler) {
		sinkEvents(Event.ONCLICK);
		return addHandler(handler,CellClickedEvent.getType());
	}
	
	public HandlerRegistration addCellDoubleClickedHandler(CellDoubleClickedEvent.Handler handler) {
		sinkEvents(Event.ONDBLCLICK);
		return addHandler(handler,CellDoubleClickedEvent.getType());
	}
	
	public HandlerRegistration addCellMouseOverHandler(final CellMouseOverEvent.Handler handler, int row, int column) {
		return cellHandler.addHandler( getFlexCellFormatter().getElement(row, column),handler);
	}
	
	public HandlerRegistration addCellMouseOutHandler(final CellMouseOutEvent.Handler handler, int row, int column) {
		return cellHandler.addHandler(getFlexCellFormatter().getElement(row, column), handler);
	}
	
	public void removeHandler(int row, int column) {
		cellHandler.removeHandler(getFlexCellFormatter().getElement(row,column));
	}
	

	class CellHandler implements EventListener { 
		
	    HashMap<Element, CellMouseOverEvent.Handler> mouseOverHandlers = new HashMap<>();
	    HashMap<Element, CellMouseOutEvent.Handler> mouseOutHandlers = new HashMap<>();
		
	   	@Override
	  	public void onBrowserEvent(Event event) {
	   		switch(event.getTypeInt()) { 
       			case Event.ONMOUSEOVER :
       				mouseOverHandlers.get(getTd(event)).onCellMouseOver(createMouseOverEvent(event));
       				break;
       			case Event.ONMOUSEOUT :
       				mouseOutHandlers.get(getTd(event)).onCellMouseOut(createMouseOutEvent(event));
       				break;
	  		}
	  	}
	    
		public HandlerRegistration addHandler(final Element td,  CellMouseOverEvent.Handler handler) {
			mouseOverHandlers.put(td, handler);
			return addHandler(td,Event.ONMOUSEOVER);
		}
		
		public HandlerRegistration addHandler(final Element td,  CellMouseOutEvent.Handler handler) {
			mouseOutHandlers.put(td, handler);
			return addHandler(td,Event.ONMOUSEOUT);
		}
		
		public void removeHandler(Element td) {
			mouseOverHandlers.remove(td);
			mouseOutHandlers.remove(td);
		}
		
		protected HandlerRegistration addHandler(final Element td, final int event) {
			DOM.sinkEvents(td, DOM.getEventsSunk(td) | event);
			DOM.setEventListener(td,this);
			return new HandlerRegistration() {
				@Override
				public void removeHandler() {
					DOM.sinkEvents(td, DOM.getEventsSunk(td) & (~event));
				}
			};
		}
				
		private CellMouseOverEvent createMouseOverEvent(Event event) {
			return new CellMouseOverEvent(getRow(event),getColumn(event),event.getClientX(),event.getClientY());
		}
		
		private CellMouseOutEvent createMouseOutEvent(Event event) {
			return new CellMouseOutEvent(getRow(event),getColumn(event),event.getClientX(),event.getClientY());
		}
	}	
}
