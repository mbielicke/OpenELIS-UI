package org.openelis.ui.widget.datetimepicker;

import org.openelis.ui.common.Datetime;
import org.openelis.ui.services.CalendarService;
import org.openelis.ui.widget.calendar.CalendarImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget will present the user with either a calendar or hour/minute selection screen
 * based on the precision specified through the constructor.  Call to getDatetime() will retrieve
 * the selected Datetime by the user. 
 *
 */
public class DatetimePicker extends Composite {
    
    @UiTemplate("DatetimePicker.ui.xml")
    interface DatetimePickerUiBinder extends UiBinder<Widget, DatetimePicker>{};
    public static final DatetimePickerUiBinder uiBinder = GWT.create(DatetimePickerUiBinder.class);
    
    /**
     * Deck used to present screens to the user
     */
    @UiField
    DeckPanel deck;
    
    /**
     * Shows a calendar by month for a user to select a date and has options to 
     * select month/year and time
     */
    @UiField
    Calendar calendar;
    
    /**
     * View for a user to select a month and year
     */
    @UiField
    Month month;
    
    /**
     * View for user to select a year and scroll by 12 year increments
     */
    @UiField
    Year year;
    
    /**
     * View for user to select hours and minutes
     */
    @UiField
    Time time;
    
    /**
     * Date precision for this picker
     */
    byte begin,end;
    
    
    public DatetimePicker(byte begin, byte end, Datetime datetime) {
        initWidget(uiBinder.createAndBindUi(this));
        
        this.begin = begin;
        this.end = end;

        /**
         * Set Display options based on date precision
         */
        if(begin < 3) {
            if(end < 3)
                calendar.time.getStyle().setDisplay(Display.NONE); 
            else
                time.time.getStyle().setDisplay(Display.NONE);
            
            if(datetime != null) {
                calendar.setDate(datetime);
                if(end > 2){
                    calendar.changeTime(datetime.get(Datetime.HOUR),datetime.get(Datetime.MINUTE));
                    time.setTime(datetime.get(Datetime.HOUR),datetime.get(Datetime.MINUTE));
                    
                }
            }
            
            deck.showWidget(0);
        }else if(begin > 2){
            time.done.getStyle().setDisplay(Display.NONE);
            if(datetime != null)
                time.setTime(datetime.get(Datetime.HOUR),datetime.get(Datetime.MINUTE));
            deck.showWidget(3);
        }
        
        /**
         * Wire Events for clicks in the date picker 
         */
        
        DOM.sinkEvents((Element)calendar.time.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)calendar.time.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                time.setTime(calendar.hour, calendar.minute);
                deck.showWidget(3);
            }
        });
        
        DOM.sinkEvents((Element)calendar.monthName.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)calendar.monthName.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                month.setYear(calendar.year);
                deck.showWidget(1);
            }
        });
        
        DOM.sinkEvents((Element)month.yearName.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)month.yearName.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                year.setYear(month.year);
                deck.showWidget(2);
            }
        });
        
            
        DOM.sinkEvents((Element)month.done.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)month.done.cast(), new EventListener() {  
            public void onBrowserEvent(Event event) {
                calendar.changeMonth(month.month,month.year);
                deck.showWidget(0);                
            }
        });
        
        DOM.sinkEvents((Element)year.done.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)year.done.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                calendar.changeYear(year.year);
                deck.showWidget(0);
            }
        });
        
        DOM.sinkEvents((Element)time.done.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)time.done.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                calendar.changeTime(time.hour,time.minute);
                deck.showWidget(0);
            }
        });
        
    }
    
    /**
     * Sets the selected datetime to the parameter passed
     * @param datetime
     */
    public void setDatetime(Datetime datetime) {
        if(datetime == null) {    
            try {
                datetime = CalendarService.get().getCurrentDatetime(begin, end);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        calendar.selected = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,datetime.getDate());
        
        if(begin < 3)
            calendar.setDate(datetime);
        if(end > 2) {
            calendar.changeTime(datetime.get(Datetime.HOUR), datetime.get(Datetime.MINUTE));
            time.setTime(datetime.get(Datetime.HOUR),datetime.get(Datetime.MINUTE));
        }
    }
    
    /**
     * Returns the Datetime selected by the user.
     * @return
     */
    public Datetime getDatetime() {
        CalendarImpl cal = CalendarImpl.getInstance();
        
        if(begin > 2) 
            cal.set(1900, 0, 1, time.hour, time.minute);
        else if(end > 2) 
            cal.set(calendar.year, calendar.month, calendar.date, calendar.hour, calendar.minute);
        else 
            cal.set(calendar.year, calendar.month, calendar.date);
        
        
        return Datetime.getInstance(begin,end,cal.getTime());
            
    }
    
    

}
