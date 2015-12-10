package org.openelis.ui.widget;


import java.util.ArrayList;

import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.messages.UIMessages;

import com.google.gwt.i18n.client.NumberFormat;

/**
 * This class is used by ScreenWidgets that implement HasValue<Integer> to
 * provide methods for formatting, validating and query by Integer values. 
 * 
 * @author tschmidt
 * 
 */
public class IntegerHelper implements WidgetHelper<Integer> {

    /**
     * Widget value attributes
     */
    protected String  pattern;

	/**
	 * Public no arg constructor
	 */
	public IntegerHelper() {

	}

	/**
	 * Will return a new QueryData object using the passed query string and and
	 * the setting the type to QueryData.Type.INTEGER.
	 */
	public QueryData getQuery(String input) {
		// Do nothing and return if passed null or empty string
		if (input == null || "".equals(input))
			return null;

		return new QueryData(QueryData.Type.INTEGER,input);
	}

	/**
	 * Will parse the input value from string to a Integer and return if
	 * successful otherwise an InvalidInteger exception will be thrown to the
	 * widget.
	 */
	public Integer getValue(String input) throws Exception {

		// If null or empty string return value as null;
		if (input == null || "".equals(input))
			return null;

		// Try and parse the input regardless of pattern.
		try {
			return Integer.valueOf(input);
		} catch (Exception e) {
			// Try it again if a pattern is present. If the input doesn't match the pattern,
			// the NumberFormat will throw the exception.
			if (pattern != null) {
				try {
					return Double.valueOf(NumberFormat.getFormat(pattern).parse(input)).intValue();
				} catch (Exception e2) {
					// Do nothing to Fall through and throw the exception.
				}
			}
			throw new Exception(getMessages().exc_invalidNumeric());
		}
	}

	/**
	 * This method will ensure the passed query string is in the correct format
	 * and that the query params are all valid integer values. A list of
	 * exceptions will be returned if any or null will be returned if
	 * successful.
	 */
	public void validateQuery(String input) throws Exception {
		// Parse query and if invalid set exception and return right away.
		new QueryFieldUtil().parseInteger(input);
	}

	/**
	 * This method will return a string value for display applying any
	 * formatting if needed.
	 */
	public String format(Integer value) {
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
	
	public ArrayList<Exception> validate(Object value) {
		ArrayList<Exception> exceptions = new ArrayList<Exception>();
		
		if(!isCorrectType(value)) 
			exceptions.add(new Exception(getMessages().exc_invalidNumeric()));
		
		return exceptions;
	}
	
	public boolean isCorrectType(Object value) {
		return value == null || value instanceof Integer;
	}

	protected UIMessages getMessages() {
	    return Messages.get();
	}
}
