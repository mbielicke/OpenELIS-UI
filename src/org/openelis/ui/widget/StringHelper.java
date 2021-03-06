package org.openelis.ui.widget;

import java.util.ArrayList;

import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.messages.UIMessages;

/**
 * This class is used by ScreenWidgets that implement HasValue<String> to
 * provide methods for formatting, validating and query by String values.
 * 
 */
public class StringHelper implements WidgetHelper<String> {


    /**
     * Public no arg constructor
     */
    public StringHelper() {

    }

    /**
     * Will return a new QueryData object using the passed query string and and
     * the setting the type to QueryData.Type.STRING.
     */
    public QueryData getQuery(String input) {
        // Do nothing and return if passed null or empty string
        if (input == null || "".equals(input))
            return null;

        return new QueryData(QueryData.Type.STRING,input);
    }

    /**
     * Returns null if empty string or null value passed. Otherwise will echo
     * back the input parameter.
     */
    public String getValue(String input) throws Exception {

        // If null or empty string return value as null;
        if (input == null || "".equals(input)) 
            return null;

        return input;
    }

    /**
     * This method will ensure the passed query string is in the correct format
     * and that the query params are all valid double values.
     */
    public void validateQuery(String input) throws Exception {
        
        new QueryFieldUtil().parse(input);
    }

    /**
     * This method will return a string value for display applying any
     * formatting if needed.
     */
    public String format(String value) {

        if (value == null)
            return "";

        return value;
    }
    
    public void setPattern(String pattern) {
    	
    }

	@Override
	public boolean isCorrectType(Object value) {
		return value == null || value instanceof String;
	}
    
	public ArrayList<Exception> validate(Object value) {
		ArrayList<Exception> exceptions = new ArrayList<Exception>();
		
		if(!isCorrectType(value))
			exceptions.add(new Exception(getMessages().exc_invalidType()));
		
		return exceptions;
	}
	
	protected UIMessages getMessages() {
	    return Messages.get();
	}
}
