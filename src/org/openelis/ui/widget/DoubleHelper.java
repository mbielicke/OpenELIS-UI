package org.openelis.ui.widget;

import java.util.ArrayList;

import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.messages.UIMessages;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.constants.NumberConstants;

/**
 * This class is used by ScreenWidgets that implement HasValue<Double> to
 * provide methods for formatting, validating and query by Double values. 
 * 
 * @author tschmidt
 * 
 */
public class DoubleHelper implements WidgetHelper<Double> {


    /**
     * Widget value attributes
     */
    protected String  pattern;
    
	/**
	 * Public no arg constructor
	 */
	public DoubleHelper() {

	}

	/**
	 * Will return a new QueryData object using the passed query string and and
	 * the setting the type to QueryData.Type.Double.
	 */
	public QueryData getQuery(String input) {
		// Do nothing and return if passed null or empty string
		if (input == null || "".equals(input))
			return null;

		return new QueryData(QueryData.Type.DOUBLE,input);

	}

	/**
	 * Will parse the input value from string to a Double and return if
	 * successful otherwise an InvalidDouble exception will be thrown to the
	 * widget.
	 */
	public Double getValue(String input) throws Exception {

		// If null or empty string return value as null;
		if (input == null || "".equals(input))
			return null;

		// Try and parse the input regardless of pattern.
		try {
			return Double.valueOf(input);
		} catch (Exception e) {
			// Try it again if a pattern is present. If the input doesn't match the pattern,
			// the NumberFormat will throw the exception.
			if (pattern != null) {
				try {
					return NumberFormat.getFormat(pattern).parse(input);
				} catch (Exception e2) {
					// Do nothing to Fall through and throw the exception.
				}
			}
			throw new Exception(getMessages().exc_invalidNumeric());
		}
	}

	/**
	 * This method will ensure the passed query string is in the correct format
	 * and that the query params are all valid double values. 
	 */
	public void validateQuery(String input) throws Exception {
        // Parse query and if invalid set exception and return right away.
        new QueryFieldUtil().parseDouble(input);
	}

	/**
	 * This method will return a string value for display applying any
	 * formatting if needed.
	 */
	public String format(Double value) {

		if (value == null)
			return "";

		if (pattern != null)
			return NumberFormat.getFormat(pattern).format(value);

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

	@Override
	public boolean isCorrectType(Object value) {
		return value == null || value instanceof Double; 
	}
	
	public ArrayList<Exception> validate(Object value) {
		ArrayList<Exception> exceptions = new ArrayList<Exception>();
		
		if(!isCorrectType(value)) 
			exceptions.add(new Exception(getMessages().exc_invalidNumeric()));
		
		return exceptions;
	}
	
	protected UIMessages getMessages() {
	    return Messages.get();
	}

}
