package org.openelis.ui.widget.celltable.event;


import org.openelis.ui.widget.celltable.FlexTable;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;


public class CellMouseOutEvent extends MouseEvent<CellMouseOutEvent.Handler> {
    
    private static Type<CellMouseOutEvent.Handler> TYPE;
    
    private int row;
    private int col;
    private int x;
    private int y;
    
    public CellMouseOutEvent(int row, int col,int x, int y) {
        this.row = row;
        this.col = col;
        this.x = x;
        this.y = y;
    }

    public static CellMouseOutEvent fire(FlexTable source, int row, int col, int x, int y) {
        if(TYPE != null) {
            CellMouseOutEvent event = new CellMouseOutEvent(row,col,x,y);
            source.fireEvent(event);
            return event;
        }
        return null;
    }
    
    @Override
    public com.google.gwt.event.dom.client.DomEvent.Type<Handler> getAssociatedType() {
        return (Type) TYPE;
    }
    
    public static Type<CellMouseOutEvent.Handler> getType() {
        if(TYPE == null) 
            TYPE = new Type<CellMouseOutEvent.Handler>(null, null);
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        if(handler.row < 0 || (handler.row == row && col == handler.col))
            handler.onCellMouseOver(this);
    }
    
    public static abstract class Handler implements EventHandler {
        public int row = -1,col = -1;
        
        public abstract void onCellMouseOver(CellMouseOutEvent event);
       
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
