package org.openelis.ui.widget.model;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ModelRowAdded<T> extends GwtEvent<ModelRowAdded.Handler<T>> {

    Type<Handler<T>> type;
    
    T row;
    int index;
    
    public ModelRowAdded(int index, T row) {
        this.index = index;
        this.row = row;
    }
    
    public T getRow() {
        return row;
    }

    public int getIndex() {
        return index;
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
    protected void dispatch(ModelRowAdded.Handler<T> handler) {
        handler.onModelRowAdded(this);
    }
    
    public interface Handler<T> extends EventHandler {
        void onModelRowAdded(ModelRowAdded<T> event);
    }
}
