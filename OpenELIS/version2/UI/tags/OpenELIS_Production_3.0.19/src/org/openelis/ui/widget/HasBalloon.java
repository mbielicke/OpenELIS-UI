package org.openelis.ui.widget;

import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;

public interface HasBalloon extends HasMouseOverHandlers, HasMouseOutHandlers{
        
    public Balloon.Options getBalloonOptions();
    
    public void setBalloonOptions(Balloon.Options options);
    

}
