package org.openelis.ui.widget.cell;

import java.util.ArrayList;

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
	
	public void add(CellDataProvider<T,?> cell) {
		cells.add(cell);
	}
	
	private void render() {
		for(CellDataProvider<T,?> cell : cells) {
			cell.render(data);
		}
	}
	
}
