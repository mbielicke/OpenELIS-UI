package org.openelis.ui.widget.cell;

import org.openelis.ui.common.data.QueryData;

import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;

public interface CellQuery {
	
    public SafeHtml asHtml(QueryData qd);
    
    public String asString(QueryData qd);
    
    public void render(QueryData qd);
    
    public void render(Element element, QueryData qd);
    
    public void startEditing(QueryData qd);
    
    public void startEditing(Element element, QueryData qd);
    
    public QueryData getQuery();

}
