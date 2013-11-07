package org.openelis.ui.widget.model;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ModelCellUpdated<T> extends GwtEvent<ModelCellUpdated.Handler<T>> {

    Type<Handler<T>> type;
    
    T value;
    int r,c;
    
    public ModelCellUpdated(int r,int c, T value) {
        this.r = r;
        this.c = c;
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }

    public int getRow() {
        return r;
    }
    
    public int getCol() {
        return c;
    }
    
    @Override
    public Type<Handler<T>> getAssociatedType() {
        if(type == null)
            type = new Type<Handler<T>>();
        return type;
    }
    
    public static <V> Type<Handler<V>> getType() {
        return new Type<Handler<V>>();
    }

    @Override
    protected void dispatch(ModelCellUpdated.Handler<T> handler) {
        handler.onModelCellUpdated(this);
    }
    
    public interface Handler<T> extends EventHandler {
        void onModelCellUpdated(ModelCellUpdated<T> event);
    }
}
