package org.openelis.ui.widget.cell;

import java.util.Iterator;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class CellView<T> extends HTMLPanel {
	
	T data;

	public CellView(SafeHtml safeHtml) {
		super(safeHtml);
	}
	
	public CellView(String string) {
		super(string);
	}
	
	public CellView(String s1, String s2) {
		super(s1,s2);
	}
		
	public void setData(T data) {
		this.data = data;
		render(this);
	}
	
	@SuppressWarnings("unchecked")
	private void render(Widget widget) {
	    if(widget instanceof Cell)
		   ((Cell<T,?>)widget).render(data);
	    
	    if(widget instanceof HasWidgets) {
	    	Iterator<Widget> iter = ((HasWidgets)widget).iterator();
	    	while(iter != null && iter.hasNext()) {
	    		render(iter.next());
	    	}
	    }
	    
	}
	
}
