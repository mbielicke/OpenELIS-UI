/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.ui.widget;

import java.util.ArrayList;

import org.openelis.ui.widget.QueryFieldUtil;
import org.openelis.ui.common.DataBaseUtil;

import com.google.gwt.i18n.client.DateTimeFormat;

public class QueryFieldUtil {

    private static final long        serialVersionUID = 1L;

    protected ArrayList<String>      comparator;

    public ArrayList<String>         parameter;

    protected ArrayList<String>      logical;

    public String                    queryString;

    private static ArrayList<String> operators        = new ArrayList<String>();
    {
        operators.add("!");
        operators.add("~");
        operators.add("=");
        operators.add("(");
        operators.add(">");
        operators.add("<");
        operators.add("|");
    }

    public void parse(String value) throws Exception {
        if(value == null || "".equals(value))
            return;
        
        this.queryString = value;
        comparator = new ArrayList<String>();
        parameter = new ArrayList<String>();
        logical = new ArrayList<String>();
        while (value != null && value.length() > 0) {
            String pos = value.substring(0, 1);
            String comp = "";
            while (operators.contains(pos)) {
                comp += pos;
                value = value.substring(1);
                if (value.length() > 0)
                    pos = value.substring(0, 1);
                else
                    pos = "";
            }
            if (comp.equals("")) {
                comp = "=";
            }
            comparator.add(comp);
            String param = "";
            while ( !operators.contains(pos) && value.length() > 0) {
                if ( !pos.equals(")"))
                    param += pos;
                value = value.substring(1);
                if (value.length() > 0)
                    pos = value.substring(0, 1);
            }
            if (param.equals(""))
                param = "NULL";
            if (param.indexOf("..") > -1) {
                comp = "between ";
                comparator.set(comparator.size() - 1, comp);
            }
            if (param.indexOf("*") > -1 || param.indexOf("?") > -1) {
                comp = "like ";
                comparator.set(comparator.size() - 1, comp);
                param = param.replace('*', '%');
                int index = 0;
                while (param.indexOf("_", index) > -1) {
                    index = param.indexOf("_");

                    param = param.substring(0, index) + "\\" + param.substring(index);
                    index += 2;
                }
                // param = param.replace("_","\\_");
                param = param.replace('?', '_');
            }
            parameter.add(param);
            if (value.length() > 0) {
                if (pos.equals("|"))
                    logical.add("|");
                value = value.substring(1);
                while (value != null && value.length() > 0 && value.charAt(0) == ' ')
                    value = value.substring(1);
            }
        }
        if(comparator.size() != parameter.size() ||
           logical.size() >= parameter.size())
            throw new Exception("invalidQueryFormat");
    }

    public ArrayList<String> getComparator() {
        return comparator;
    }

    public ArrayList<String> getParameter() {
        return parameter;
    }

    public ArrayList<String> getLogical() {
        return logical;
    }

    public void validateQuery(String query) throws Exception {

    }

    public void parseDouble(String query) throws Exception {
        if(query == null || "".equals(query))
            return;
        
        parse(query);

        // Loop through query params and make sure each is a valid double value
        for (String param : parameter) {
            String[] vals = param.split("\\.\\.");
            for (int i = 0; i < vals.length; i++ ) {
                if ( !vals[i].equalsIgnoreCase("null")) {
                    try {
                        Double.parseDouble(vals[i]);
                    } catch (Exception e) {
                        throw new Exception("invalidDouble");
                    }
                }
            }
        }
    }

    public void parseInteger(String query) throws Exception {
        if(query == null || "".equals(query))
            return;
        
        parse(query);

        // Loop through query params and make sure each is a valid double value
        for (String param : parameter) {
            String[] vals = param.split("\\.\\.");
            for (int i = 0; i < vals.length; i++ ) {
                if ( !vals[i].equalsIgnoreCase("null")) {
                    try {
                        Integer.parseInt(vals[i].trim());
                    } catch (Exception e) {
                        throw new Exception("fieldNumericException");
                    }
                }
            }
        }
    }

    public void parseLong(String query) throws Exception {
        if(query == null || "".equals(query))
            return;
        
        parse(query);

        // Loop through query params and make sure each is a valid double value
        for (String param : parameter) {
            String[] vals = param.split("\\.\\.");
            for (int i = 0; i < vals.length; i++ ) {
                if ( !vals[i].equalsIgnoreCase("null")) {
                    try {
                        Long.parseLong(param);
                    } catch (Exception e) {
                        throw new Exception("fieldNumericException");
                    }
                }
            }
        }
    }

    public void parseDate(String query, String pattern) throws Exception {
        if(query == null || "".equals(query))
            return;
        
        parse(query);

        // Loop through query params and make sure each is a valid date value
        for (String param : parameter) {
            String[] dates = param.split("\\.\\.");
            for (int i = 0; i < dates.length; i++ ) {
                if ( !dates[i].equalsIgnoreCase("null")) {
                    try {
                        DateTimeFormat.getFormat(pattern).parseStrict(dates[i]);
                    } catch (Exception e) {
                        throw new Exception("invalidDateFormat");
                    }
                }
            }
        }
    }
    
    public static String parseAutocomplete(String value) throws Exception{
        ArrayList<String> list;
        QueryFieldUtil parser;
        
        if (DataBaseUtil.isEmpty(value))
            return "";
        parser = new QueryFieldUtil();
        parser.parse(value);
        list = parser.getParameter();
        if (list.size() > 0) 
            return list.get(0);
        
        return "";
    }

    public static String convert(String value) throws Exception {
    	QueryFieldUtil util;
    	StringBuilder sb;
    	
    	util = new QueryFieldUtil();
    	util.parse(value);
    	
    	sb = new StringBuilder();
    	for(int i = 0; i < util.parameter.size(); i++) {
    		sb.append(util.comparator.get(i));
    		sb.append(" ");
    		sb.append(util.parameter.get(i));
    		if(i < util.logical.size()) {
    			sb.append(" " );
    			sb.append(util.logical.get(i));
    			sb.append(" ");
    		}
    	}
    	
    	return sb.toString();
    }
}
