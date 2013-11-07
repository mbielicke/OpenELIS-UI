package org.openelis.ui.widget.model;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ModelRowRemoved extends GwtEvent<ModelRowRemoved.Handler> {

    static Type<Handler> type;
    
    int index;
    
    public ModelRowRemoved(int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    @Override
    public Type<Handler> getAssociatedType() {
        return type;
    }
    
    public static Type<Handler> getType() {
        if(type == null)
            type = new Type<Handler>();
        return type;
    }

    @Override
    protected void dispatch(ModelRowRemoved.Handler handler) {
        handler.onModelRowRemoved(this);
    }
    
    public interface Handler extends EventHandler {
        void onModelRowRemoved(ModelRowRemoved event);
    }
}
