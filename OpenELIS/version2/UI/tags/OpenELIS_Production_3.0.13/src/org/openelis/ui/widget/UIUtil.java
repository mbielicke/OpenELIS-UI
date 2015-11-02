package org.openelis.ui.widget;

import com.google.gwt.user.client.ui.Widget;

public class UIUtil {
    
    public static boolean isRendered(Widget widget) {
        if(!widget.isAttached() || 
           !widget.isVisible() || 
           "none".equals(widget.getElement().getStyle().getDisplay())) 
            return false;
        
        return widget.getParent() != null ? isRendered(widget.getParent()) : true;
    }

}
