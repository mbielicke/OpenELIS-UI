package org.openelis.ui.widget.datetimepicker;

import org.openelis.ui.common.Datetime;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.CalCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.services.CalendarService;
import org.openelis.ui.widget.calendar.CalendarImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
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
 * This widget makes up the calendar view of the DatetimePicker
 */
public class Calendar extends Composite {
    
    @UiTemplate("Calendar.ui.xml")
    interface CalendarUiBinder extends UiBinder<Widget,Calendar> {};
    public static final CalendarUiBinder uiBinder = GWT.create(CalendarUiBinder.class);
    
    @UiField
    protected DivElement body,time,timeDisplay,monthName,calPrevButton,calNextButton;
    
    /**
     * Currently selected cell by the user
     */
    DivElement selectedCell;
    
    /**
     * Two dimensional array of datetimes representing currently diplayed calendar
     */
    Datetime[][] dates;
    
    /**
     * Current date and selected times 
     */
    Datetime currentTime,today,selected;
    
    /**
     * Components that make up a Datetime that can be set by a user
     */
    int year,month,date,hour,minute,displayMonth,displayYear;
    
    CalCSS css;
    
    public Calendar() {
        initWidget(uiBinder.createAndBindUi(this));
        css = UIResources.INSTANCE.cal();
        css.ensureInjected();

        DOM.sinkEvents((Element)calPrevButton.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)calPrevButton.cast(), new EventListener() {    
            @Override
            public void onBrowserEvent(Event event) {
                CalendarImpl cal = CalendarImpl.getInstance();
                cal.set(displayYear, displayMonth, 1, 0, 0, 0);
                cal.add(CalendarImpl.MONTH, -1);
                changeCalendar(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, cal.getTime()));
            }
        });

        DOM.sinkEvents((Element)calNextButton.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)calNextButton.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                CalendarImpl cal = CalendarImpl.getInstance();
                cal.set(displayYear, displayMonth, 1, 0, 0, 0);
                cal.add(CalendarImpl.MONTH, 1);
                changeCalendar(Datetime.getInstance(Datetime.YEAR, Datetime.DAY, cal.getTime()));
            }
        });
        
        DOM.sinkEvents((Element)body.cast(), Event.ONCLICK);
        DOM.setEventListener((Element)body.cast(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                String cell = ((Element)event.getEventTarget().cast()).getAttribute("cell");
                if(cell != null) {
                    String[] indexes = cell.split(",");
                        
                    selected = dates[Integer.valueOf(indexes[0])][Integer.valueOf(indexes[1])];
                        
                    date = selected.get(Datetime.DAY);
                    
                    if(month != selected.get(Datetime.MONTH)) {
                        setDate(selected);
                        return;
                    }
                        
                    if(selectedCell != null)
                        selectedCell.removeClassName(css.cal_select());
                        
                    selectedCell = (DivElement)event.getEventTarget().cast();
                    selectedCell.addClassName(css.cal_select());
                }
            }
        });
        
        try {
            currentTime = CalendarService.get().getCurrentDatetime(Datetime.YEAR, Datetime.MINUTE);
        }catch(Exception e) {
            e.printStackTrace();
            currentTime = Datetime.getInstance(Datetime.YEAR,Datetime.MINUTE);
        }
                        
        today = Datetime.getInstance(Datetime.YEAR, Datetime.DAY,currentTime.getDate());
        
        selected = today;
        
        setDate(today);
        
        changeTime(currentTime.get(Datetime.HOUR),currentTime.get(Datetime.MINUTE));
    }
    
    public void setDate(Datetime datetime) {
        
        year = datetime.get(Datetime.YEAR);
        month = datetime.get(Datetime.MONTH);
        date = datetime.get(Datetime.DAY);
        
        changeCalendar(datetime);
    }
    
    protected void changeCalendar(Datetime datetime) {
        
        int year,month;
        
        displayYear = datetime.get(Datetime.YEAR);
        displayMonth = datetime.get(Datetime.MONTH);
        
        CalendarImpl cal = CalendarImpl.getInstance();
        cal.set(CalendarImpl.YEAR, displayYear);
        cal.set(CalendarImpl.MONTH,displayMonth);
        cal.set(CalendarImpl.DAY_OF_MONTH,1);
        cal.set(CalendarImpl.HOUR, 0);
        cal.set(CalendarImpl.MINUTE,0);
        cal.set(CalendarImpl.SECOND,0);

        
        monthName.setInnerText(getMonthYearDisplay(displayMonth, displayYear));
        /*
         * Calculate the Date to be used for cell[0][0]
         */
        if(cal.get(CalendarImpl.DAY_OF_WEEK) > 0)
            cal.add(CalendarImpl.DATE, -cal.get(CalendarImpl.DAY_OF_WEEK));
        //else
        //    cal.add(CalendarImpl.DATE, -7);
        
        dates = new Datetime[6][7];
        
        /*
         * Loop through for each cell adding 1 day to the begin date and add the needed style 
         * for each cell based on the date of that cell.
         */
        for(int i = 0; i < 6; i++) {
            DivElement row = (DivElement)body.getChild(2*i+1); 
            for(int j = 0; j < 7; j++) {
                dates[i][j] = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,cal.getTime());
                DivElement cell = (DivElement)row.getElementsByTagName("div").getItem(j);
                cell.setInnerText(String.valueOf(cal.get(CalendarImpl.DATE)));
                
                
                if(cal.get(CalendarImpl.MONTH) != displayMonth) {
                    cell.addClassName(css.cal_outside());
                    if(j == 0 || j ==6)
                        cell.removeClassName(css.cal_weekend());
                }else
                    cell.removeClassName(css.cal_outside());
                
                if(dates[i][j].equals(today))
                    cell.addClassName(css.cal_today());
                else
                    cell.removeClassName(css.cal_today());
                
                if(dates[i][j].equals(selected)) {
                    cell.addClassName(css.cal_select());
                    selectedCell = cell;
                }else 
                    cell.removeClassName(css.cal_select());
                
                cal.add(CalendarImpl.DATE,1);
            }
        }
    }
    
    /**
     * Changes the month and year and displyed by the calendar
     * @param month
     * @param year
     */
    protected void changeMonth(int month, int year) {
        CalendarImpl cal = CalendarImpl.getInstance();
        cal.set(year, month, date);
        changeCalendar(Datetime.getInstance(Datetime.YEAR,Datetime.DAY,cal.getTime()));
    }
    
    /**
     * Changes the year of the calendar using the currently set month
     * @param year
     */
    protected void changeYear(int year) {
        CalendarImpl cal = CalendarImpl.getInstance();
        cal.set(year, month, date);
        changeCalendar(Datetime.getInstance(Datetime.YEAR,Datetime.DAY,cal.getTime()));   
    }
    
    /**
     * Changes the time set and displayed by this calendar
     * @param hour
     * @param minute
     */
    protected void changeTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        timeDisplay.setInnerText(hour+":"+(minute < 10 ? "0"+minute : minute));
        
    }

    /**
     * Returns the correct localized month name for the displayed month form resource files
     * @param month
     * @param year
     * @return
     */
    private String getMonthYearDisplay(int month, int year) {
        String monthText = "";
                
        switch(month) {
            case 0 :
                monthText = Messages.get().cal_month0();
                break;
            case 1 : 
                monthText = Messages.get().cal_month1();
                break;
            case 2 : 
                monthText = Messages.get().cal_month2();
                break;
            case 3 : 
                monthText = Messages.get().cal_month3();
                break;
            case 4 : 
                monthText = Messages.get().cal_month4();
                break;
            case 5 : 
                monthText = Messages.get().cal_month5();
                break;
            case 6 : 
                monthText = Messages.get().cal_month6();
                break;
            case 7 : 
                monthText = Messages.get().cal_month7();
                break;
            case 8 : 
                monthText = Messages.get().cal_month8();
                break;
            case 9 : 
                monthText = Messages.get().cal_month9();
                break;
            case 10 : 
                monthText = Messages.get().cal_month10();
                break;
            case 11 : 
                monthText = Messages.get().cal_month11();
                break;
        }
        
        return monthText + " " + (year + 1900);
    }

}
