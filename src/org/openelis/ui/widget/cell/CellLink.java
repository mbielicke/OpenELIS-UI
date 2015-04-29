package org.openelis.ui.widget.cell;

import org.openelis.ui.widget.Link;
import org.openelis.ui.widget.Link.Details;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class CellLink extends Cell<Link.Details> {
	
	public CellLink() {
		
	}

	@Override
	public SafeHtml asHtml(Details value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        builder.appendHtmlConstant(new Link((Link.Details)value).getElement().getString());
        
        return builder.toSafeHtml();
	}

	@Override
	public String asString(Details value) {
		return value.getUrl();
	}

}
