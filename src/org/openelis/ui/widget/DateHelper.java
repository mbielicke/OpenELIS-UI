package org.openelis.ui.widget;

import java.util.ArrayList;
import java.util.Date;

import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.messages.UIMessages;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * This class is used by ScreenWidgets that implement HasValue<Date> to
 * provide methods for formatting, validating and query by Date values. 
 * 
 * @author tschmidt
 * 
 */
public class DateHelper implements WidgetHelper<Datetime> {
    
    /**
     * Widget value attributes
     */
    protected String  pattern;
    protected byte    begin, end;
    
    /**
     * Public no arg constructor;
     */
    public DateHelper() {
    	setBegin(Datetime.YEAR);
    	setEnd(Datetime.DAY);
    }
    
    /**
     * Will return a new QueryData object using the passed query string and and
     * the setting the type to QueryData.Type.DATE.
     */
    public QueryData getQuery(String input) {

        // Do nothing and return if passed null or empty string
        if (input == null || "".equals(input))
            return null;

        return new QueryData(QueryData.Type.DATE,input);
    }

    /**
     * Will parse the input value from string to a Datetime and return if
     * successful otherwise an InvalidDate exception will be thrown to the
     * widget.
     */
    public Datetime getValue(String input) throws Exception {
        Date date;
        
        // If null or empty string return value as null;
        if (input == null || "".equals(input))
            return null;

                
        try {
            date =  DateTimeFormat.getFormat(pattern).parseStrict(input);
        }catch(Exception e) {
            throw new Exception(getMessages().exc_invalidDate());
        }
        
        return Datetime.getInstance(begin,end,date);
    }

    /**
     * This method will ensure the passed query string is in the correct format
     * and that the query params are all valid date values. 
     */
    public void validateQuery(String input) throws Exception {

        new QueryFieldUtil().parseDate(input, pattern);

    }

    /**
     * This method will return a string value for display applying any
     * formatting if needed.
     */
    public String format(Datetime value) {
   
        if(value == null)
            return "";
        
        if(pattern != null) 
            return DateTimeFormat.getFormat(pattern).format(value.getDate());
        
        return value.toString();
        
    }
    
    /**
     * Sets the Formatting pattern to be used when displaying the 
     * widgets value on the screen.
     * @param pattern
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Sets the begin precision for this Datetime to be returned by getValue()
     * @param begin
     */
    public void setBegin(byte begin) {
        this.begin = begin;
        setDefaultPattern();
    }
    
    /**
     * Returns the begin precision used by this helper
     * @return
     */
    public byte getBegin() {
    	return begin;
    }
    
    /**
     * Sets the end precision for this Datetime to be returned by getValue()
     * @param begin
     */
    public void setEnd(byte end) {
        this.end = end;
        setDefaultPattern();
    }
    
    /**
     * Returns the end precision used by this helper
     * @return
     */
    public byte getEnd() {
    	return end;
    }
    
    private void setDefaultPattern() {
       	if(begin > Datetime.DAY) {
    		setPattern(getMessages().gen_timePattern());
    	} else if (end < Datetime.HOUR){
    		setPattern(getMessages().gen_datePattern());
    	} else {
    		setPattern(getMessages().gen_dateTimePattern());
    	}
    }

	public boolean isCorrectType(Object value) {
		return value == null || value instanceof Datetime;
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

}
