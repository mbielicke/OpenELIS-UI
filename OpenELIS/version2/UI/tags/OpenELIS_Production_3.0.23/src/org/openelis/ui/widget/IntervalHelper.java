package org.openelis.ui.widget;

import java.util.ArrayList;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.messages.UIMessages;

/**
 * This class is used by ScreenWidgets that implement HasValue<Date> to
 * provide methods for formatting, validating and query by Date values. 
 * 
 * @author tschmidt
 * 
 */
public class IntervalHelper implements WidgetHelper<Double> {
    
    public static enum Unit {MINUTES,HOURS};
    
    public Unit unit;
    
    /**
     * Public no arg constructor;
     */
    public IntervalHelper() {
        unit = Unit.MINUTES;
    }
    
    /**
     * Will return a new QueryData object using the passed query string and and
     * the setting the type to QueryData.Type.DATE.
     */
    public QueryData getQuery(String input) {

        // Do nothing and return if passed null or empty string
        if (input == null || "".equals(input))
            return null;
        
        String value = "";
        try {
            value = String.valueOf(getValue(input));
        }catch(Exception e) {
            
        }

        return new QueryData(QueryData.Type.DOUBLE,value);
    }

    /**
     * Will parse the input value from string to a Datetime and return if
     * successful otherwise an InvalidDate exception will be thrown to the
     * widget.
     */
    public Double getValue(String input) throws Exception {
        switch(unit) {
            case MINUTES :
                return getValueAsMinutes(input);
            case HOURS :
                return getValueAsHours(input);
        }
        return null;
    }
    
    public Double getValueAsHours(String input) throws Exception {
        double hours = 0.0 ,mins = 0.0;
        String tm[];
        
        if(input == null || "".equals(input))
            return null;
        
        tm = input.split(":");
        try {
            hours = Double.parseDouble(tm[0]);
        }catch(Exception e) {
            hours = 0.0;
        }
        
        if(tm.length > 1) {
            try {
                mins = Double.parseDouble(tm[1]) / 60.0;
            }catch(Exception e) {
                mins = 0.0;
            }
        }
        
        return new Double(hours += mins);
    }
    
    public Double getValueAsMinutes(String input) throws Exception {
        double hours = 0.0 ,mins = 0.0;
        String tm[];
        
        if(input == null || "".equals(input))
            return null;
        
        tm = input.split(":");
        try {
            hours = Double.parseDouble(tm[0]) * 60;
        }catch(Exception e) {
            hours = 0.0;
        }
        
        if(tm.length > 1) {
            try {
                mins = Double.parseDouble(tm[1]);
            }catch(Exception e) {
                mins = 0.0;
            }
        }
        
        return new Double(hours += mins);
    }

    /**
     * This method will ensure the passed query string is in the correct format
     * and that the query params are all valid date values. 
     */
    public void validateQuery(String input) throws Exception {

        new QueryFieldUtil().parseDouble(String.valueOf(getValue(input)));

    }

    /**
     * This method will return a string value for display applying any
     * formatting if needed.
     */
    public String format(Double hours) {
        switch(unit) {
            case MINUTES :
                return formatMinutes(hours);
            case HOURS :
                return formatHours(hours);
        }
        return "";
    }
    
    public String formatMinutes(Double minutes) {
       int h,m;
        
        if (minutes != null && minutes.doubleValue() > 0.0) {
            h = (int)(minutes / 60);
            m = (int)(minutes % 60);
            return h + ":" + (m < 10 ? "0"+m : m);
        } 
  
        return "";      
    }
    
    public String formatHours(Double hours) {
       int h,m;
        
        if (hours != null && hours.doubleValue() > 0.0) {
            h = (int)Math.floor(hours);
            m = 0;
            if(h == 0)
                m = (int)Math.round(hours  * 60);
            else
                m = (int)Math.round((hours % h) * 60);
            return h + ":" + (m < 10 ? "0"+m : m);
        } 
  
        return "";      
    }
    
 

	public boolean isCorrectType(Object value) {
		return value == null || value instanceof Double;
	}
	
	public ArrayList<Exception> validate(Object value) {
		ArrayList<Exception> exceptions = new ArrayList<Exception>();
		
		if(!isCorrectType(value))
			exceptions.add(new Exception(getMessages().exc_invalidDate()));
		
		return exceptions;
		
	}
	
	protected UIMessages getMessages() {
	    return Messages.get();
	}

   
    @Override
    public void setPattern(String pattern) {
        
    }
    
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

}
