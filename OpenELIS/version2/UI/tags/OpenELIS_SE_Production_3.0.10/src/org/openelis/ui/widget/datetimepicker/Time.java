package org.openelis.ui.widget.datetimepicker;

import org.openelis.ui.resources.CalCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget allows the user to choose a time by hours and minutes
 * for the DatetimePicker.
 *
 */
public class Time extends Composite {
    
    @UiTemplate("Time.ui.xml")
    interface TimeUiBinder extends UiBinder<Widget, Time>{};
    public static final TimeUiBinder uiBinder = GWT.create(TimeUiBinder.class);
    
    @UiField
    protected DivElement hours,minutes,doneDiv,time,timeDisplay;
    
    @UiField
    protected InputElement done;

    /**
     * Selected hour and minute chosen by the user
     */
    int hour,minute;
    
    CalCSS css = UIResources.INSTANCE.cal();
    
    public Time() {
        initWidget(uiBinder.createAndBindUi(this));
        css.ensureInjected();
        
        DOM.sinkEvents((Element)hours.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)hours.cast(), new EventListener() {  
            @Override
            public void onBrowserEvent(Event event) {
                Element target = (Element)event.getEventTarget().cast();
                String cell = target.getAttribute("cell");
                
                if(cell != null) 
                    setHourSelection(Integer.valueOf(cell));
                
            }
        });
        
        DOM.sinkEvents((Element)minutes.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)minutes.cast(), new EventListener() {  
            @Override
            public void onBrowserEvent(Event event) {
                Element target = (Element)event.getEventTarget().cast();
                String cell = target.getAttribute("cell");
                
                if(cell != null) 
                    setMinuteSelection(Integer.valueOf(cell));
                
            }
        });
        
    }
    
    /**
     * Sets the current hour and minute set by the user
     * @param hour
     * @param minute
     */
    protected void setTime(int hour, int minute) {
        setHourSelection(hour);
        setMinuteSelection(minute);
    }
    
    /**
     * Sets the hours selection from the user click
     * @param hour
     */
    private void setHourSelection(int hour) {
        this.hour = hour;
        NodeList cells = hours.getElementsByTagName("div");
        
        for(int i = 0; i < cells.getLength(); i++) {
            if(hour == Integer.parseInt(((Element)cells.getItem(i).cast()).getInnerText()))
                ((Element)cells.getItem(i).cast()).addClassName(css.cal_select());
            else
                ((Element)cells.getItem(i).cast()).removeClassName(css.cal_select());
        }
        
        timeDisplay.setInnerText(hour+":"+(minute < 10 ? "0"+minute : minute));
    }
    
    /**
     * Sets the minutes selection from the user click
     * @param minute
     */
    private void setMinuteSelection(int minute) {
        this.minute = minute;
        NodeList cells = minutes.getElementsByTagName("div");
        
        for(int i = 0; i < cells.getLength(); i++) {
            if(minute == Integer.parseInt(((Element)cells.getItem(i).cast()).getInnerText()))
                ((Element)cells.getItem(i).cast()).addClassName(css.cal_select());
            else
                ((Element)cells.getItem(i).cast()).removeClassName(css.cal_select());
        }
        timeDisplay.setInnerText(hour+":"+(minute < 10 ? "0"+minute : minute));
    }    

}
