package org.openelis.ui.widget.cell;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class CellImage extends Cell<String> {

	public CellImage() {
		
	}

	@Override
	public SafeHtml asHtml(String value) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        builder.appendHtmlConstant("<span class='"+DataBaseUtil.toString(value)+"'/>");
        
        return builder.toSafeHtml();
	}

	@Override
	public String asString(String value) {
		return value;
	}



}
