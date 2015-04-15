package org.openelis.ui.widget.celltable.event;

import org.openelis.ui.widget.celltable.FlexTable;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;


public class CellMouseOverEvent extends MouseEvent<CellMouseOverEvent.Handler> {
    
    private static Type<CellMouseOverEvent.Handler> TYPE;
    
    private int row;
    private int col;
    private int x;
    private int y;
    
    public CellMouseOverEvent(int row, int col,int x, int y) {
        this.row = row;
        this.col = col;
        this.x = x;
        this.y = y;
    }

    public static CellMouseOverEvent fire(FlexTable source, int row, int col, int x, int y) {
        if(TYPE != null) {
            CellMouseOverEvent event = new CellMouseOverEvent(row,col,x,y);
            source.fireEvent(event);
            return event;
        }
        return null;
    }
    
    @Override
    public com.google.gwt.event.dom.client.DomEvent.Type<Handler> getAssociatedType() {
        return (Type) TYPE;
    }
    
    public static Type<CellMouseOverEvent.Handler> getType() {
        if(TYPE == null) 
            TYPE = new Type<CellMouseOverEvent.Handler>(null, null);
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        if(handler.row < 0 || (handler.row == row && col == handler.col))
            handler.onCellMouseOver(this);
    }
    
    public static abstract class Handler implements EventHandler {
        public int row = -1,col = -1;
        
        public Handler() {
            
        }
        
        public Handler(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        public abstract void onCellMouseOver(CellMouseOverEvent event);
       
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
