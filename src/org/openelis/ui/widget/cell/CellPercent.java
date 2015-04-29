package org.openelis.ui.widget.cell;

import org.openelis.ui.widget.PercentBar;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class CellPercent extends Cell<Double> {
	
	public PercentBar editor;
	
	public CellPercent() {
		setEditor(new PercentBar());
	}
	
	public CellPercent(PercentBar percentBar) {
		setEditor(percentBar);
	}
	
	public void setEditor(PercentBar editor) {
		this.editor = editor;	
	}

	@Override
	public SafeHtml asHtml(Double value) {
	    SafeHtmlBuilder builder = new SafeHtmlBuilder();
	    
	    editor.setPercent((Double)value);
	    builder.appendHtmlConstant(editor.getElement().getString());
	    
	    return builder.toSafeHtml();
	}

	@Override
	public String asString(Double value) {
		return (value * 100.0)+ "%";
	}

}
