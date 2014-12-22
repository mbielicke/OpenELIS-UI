package org.openelis.ui.widget.cell;

import java.util.ArrayList;
import java.util.Iterator;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.HasHelper;
import org.openelis.ui.widget.ScreenWidgetInt;
import org.openelis.ui.widget.table.ColumnInt;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class Cell<V> extends Widget implements CellRenderer<V>, HasWidgets {
	
	Element proxyElement;
	HasValue<V> editor;
	
	public Cell() {
		setElement(Document.get().createDivElement());
	}
	
	public Element getRenderElement() {
		if(proxyElement != null)
			return proxyElement;
		else
			return super.getElement();
	}
	
	public void render(V value) {
		getElement().setInnerText(value.toString());
	}
	
	public void setProxyElement(Element element) {
		this.proxyElement = element;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void add(Widget w) {
		if(w instanceof HasValue)
			editor = (HasValue<V>)w;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

	@Override
	public Iterator<Widget> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String display(V value) {
        if(((HasHelper<V>)editor).getHelper().isCorrectType(value))
        	return ((HasHelper<V>)editor).getHelper().format(value);
        else
        	return DataBaseUtil.toString(value);
	}

	@Override
	public SafeHtml bulkRender(V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(HTMLTable table, int row, int col, V value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Exception> validate(V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setColumn(ColumnInt col) {
		// TODO Auto-generated method stub
		
	}
	
}
