package org.openelis.ui.widget.datetimepicker;

import org.openelis.ui.resources.CalCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
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
 * This widget displays a view that allows the user to change the month and 
 * year of the displayed calendar.
 *
 */
public class Month extends Composite {
    
    @UiTemplate("Month.ui.xml")
    interface MonthUiBinder extends UiBinder<Widget, Month>{};
    public static final MonthUiBinder uiBinder = GWT.create(MonthUiBinder.class);
    
    @UiField
    protected DivElement body,yearName,previous,next;
    
    @UiField
    protected InputElement done;
    
    /**
     * Selected month year by the user
     */
    int month,year;
    
    CalCSS css = UIResources.INSTANCE.cal();
    
    public Month() {
        initWidget(uiBinder.createAndBindUi(this));
        css.ensureInjected();
        
        DOM.sinkEvents((Element)previous.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)previous.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                setYear(year-1);
            }
        });
        
        DOM.sinkEvents((Element)next.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)next.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                setYear(year+1);
            }
        });
        
        DOM.sinkEvents((Element)body.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)body.cast(), new EventListener() {  
            @Override
            public void onBrowserEvent(Event event) {
                Element target = (Element)event.getEventTarget().cast();
                String cell = target.getAttribute("cell");
                
                if(cell != null) {
                    setSelection(Integer.valueOf(cell));
                    done.click();
                }
                
            }
        });
        
    }
    
    /**
     * Sets the year chosen and displayed by the user
     * @param year
     */
    protected void setYear(int year) {
        this.year = year;
        yearName.setInnerText(String.valueOf(year+1900));
    }
    
    /**
     * Sets the selected month from the user choice
     * @param month
     */
    private void setSelection(int month) {
        this.month = month;
        
        NodeList cells = body.getElementsByTagName("div");
        
        for(int i = 0; i < cells.getLength(); i++) {
            if(month == i)
                ((Element)cells.getItem(i).cast()).addClassName(css.cal_select());
            else
                ((Element)cells.getItem(i).cast()).removeClassName(css.cal_select());
        }
    }
    

}
