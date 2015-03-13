package org.openelis.ui.widget.cell;

import org.openelis.ui.widget.Label;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

public class CellLabel<V> extends Cell<V> {
	
	protected Label<V> editor;
	
	public CellLabel(Label<V> editor) {
		this.editor = editor; 
	}
	
	public CellLabel() {
		editor = new Label<V>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void add(Widget w) {
		if(w instanceof Label)
			editor = (Label<V>)w;
		
	}

	@Override
	public SafeHtml asHtml(V value) {
		return new SafeHtmlBuilder().appendEscaped(editor.getHelper().format(value)).toSafeHtml();
	}

}
