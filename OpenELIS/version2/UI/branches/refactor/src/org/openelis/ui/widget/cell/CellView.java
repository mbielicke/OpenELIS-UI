package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;

public class CellView<T> extends HTMLPanel {
	
	T data;
	ArrayList<CellDataProvider<T,?>> cells = new ArrayList<>();

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
		render();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void add(final CellDataProvider<T,?> cell) {
		cells.add(cell);
		if(cell.cell instanceof CellEditor) {
			cell.cell.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					((CellEditor)cell.cell).startEditing(cell.getValue(data));
				}
			},ClickEvent.getType());
		}
	}
	
	private void render() {
		for(CellDataProvider<T,?> cell : cells) {
			cell.render(data);
		}
	}
	
}
