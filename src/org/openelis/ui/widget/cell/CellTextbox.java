package org.openelis.ui.widget.cell;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.CSSUtils;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.table.Container;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

public class CellTextbox<V> extends Cell<V> implements CellEditor<V> {
	
	TextBox<V> editor;
	boolean editing;
	

	public CellTextbox() {
		editor = new TextBox<V>();
		editor.setEnabled(true);
		editor.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				finishEditing();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void add(Widget w) {
		if(w instanceof TextBox)
			editor = (TextBox<V>)w;
	}
	
	@Override
	public boolean isEditing() {
		return editing;
	}

	@Override
	public void startEditing(V value, Container container, NativeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void startEditing(V value) {
		startEditing(getElement().getParentElement(),value);		
	}
	
	public void startEditing(Element container, V value) {
		if(editing)
			return;
		editor.setValue(value);
		sizeEditor(editor,container);
		setEditor(editor);
		container.removeAllChildren();
		container.appendChild(getElement());
		editor.setFocus(true);		
		editing = true;
	}

	@Override
	public void startEditingQuery(QueryData qd, Container container,
			NativeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object finishEditing() {
		V value = editor.getValue();
		remove(editor);
		render(value);
		editing = false;
		return value;
	}

	@Override
	public boolean ignoreKey(int keyCode) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public SafeHtml asHtml(V value) {
		if(editor == null)
			return new SafeHtmlBuilder().appendEscaped(DataBaseUtil.toString(value)).toSafeHtml();
        editor.setQueryMode(false);
        if(editor.getHelper().isCorrectType(value))
        	return new SafeHtmlBuilder().appendEscaped(editor.getHelper().format(value)).toSafeHtml();
        else
        	return new SafeHtmlBuilder().appendEscaped(DataBaseUtil.toString(value)).toSafeHtml();
	}

	@Override
	public Widget getEditor() {
		return editor;
	}

	
	protected void setEditor(Widget editor) {
		getElement().removeAllChildren();
		setWidget(editor);
	}
	
	protected void sizeEditor(Widget editor, Element element) {
		editor.setWidth(getWidth(element)+"px");
		editor.setHeight(getHeight(element)+"px");
	}
	
	private double getWidth(Element element) {
		double width = element.getClientWidth();
		width -= CSSUtils.getAddedPaddingWidth(element);
		width -= CSSUtils.getAddedBorderWidth(editor.getElement());
		return width;
	}
	
	private double getHeight(Element element) {
		double height = element.getClientHeight();
		height -= CSSUtils.getAddedPaddingHeight(element);
		height -= CSSUtils.getAddedBorderHeight(editor.getElement());
		return height;
	}
}
