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
 * This widget allows the user to select a year to be displayed in the 
 * calendar view of the DatetimePicker.
 */
public class Year extends Composite {
    
    @UiTemplate("Year.ui.xml")
    interface YearUiBinder extends UiBinder<Widget, Year>{};
    public static final YearUiBinder uiBinder = GWT.create(YearUiBinder.class);
    
    @UiField
    protected DivElement body,previous,next,yearName;
    
    @UiField
    protected InputElement done;
    
    /**
     * Selected year and beginning year of the select range
     */
    int year,year0;
    
    CalCSS css = UIResources.INSTANCE.cal();
    
    public Year() {
        initWidget(uiBinder.createAndBindUi(this));
        css.ensureInjected();
        
        DOM.sinkEvents((Element)previous.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)previous.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                drawYears(year0-12);
            }
        });
        
        DOM.sinkEvents((Element)next.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)next.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                drawYears(year0+12);
            }
        });
        
        DOM.sinkEvents((Element)body.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)body.cast(), new EventListener() {  
            @Override
            public void onBrowserEvent(Event event) {
                Element target = (Element)event.getEventTarget().cast();
                String cell = target.getAttribute("cell");
                
                if(cell != null) {
                    year = Integer.valueOf(target.getInnerText())-1900;
                    setSelection();
                    done.click();
                }
                
            }
        });
    }
    
    /**
     * Sets the current year to be selected and draws the initial year range
     * @param year
     */
    protected void setYear(int year) {
        this.year = year;
        drawYears(year-6);
    }
    
    /**
     * Draws the 12 years to be displayed by the widget starting with the year passed
     * @param year0
     */
    protected void drawYears(int year0) {
        this.year0 = year0;
        
        NodeList cells = body.getElementsByTagName("div");
        
        for(int i = 0; i < cells.getLength(); i++) {
            ((Element)cells.getItem(i).cast()).setInnerText(String.valueOf((year0+i+1900)));
        }
        
        yearName.setInnerText(String.valueOf(year0+1900)+" - "+String.valueOf(year0+11+1900));
        
        setSelection();
    }
    
    /**
     * Sets the current year selection based on the user click
     */
    private void setSelection() {
       
        NodeList cells = body.getElementsByTagName("div");
        
        for(int i = 0; i < cells.getLength(); i++) {
            if(String.valueOf(year+1900).equals(((Element)cells.getItem(i).cast()).getInnerText()))
                ((Element)cells.getItem(i).cast()).addClassName(css.cal_select());
            else
                ((Element)cells.getItem(i).cast()).removeClassName(css.cal_select());
        }
    }
    

}
