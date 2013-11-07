package org.openelis.ui.widget.model;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ModelDataSet extends GwtEvent<ModelDataSet.Handler> {

    static Type<Handler> type;
        
    public ModelDataSet() {

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
    protected void dispatch(ModelDataSet.Handler handler) {
        handler.onModelDataSet(this);
    }
    
    public interface Handler extends EventHandler {
        void onModelDataSet(ModelDataSet event);
    }
}
