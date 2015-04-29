package org.openelis.ui.widget.cell;

import java.util.ArrayList;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
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
		if(cell.cell instanceof EditableCell) {
			DOM.setEventListener(cell.cell.getElement().getParentElement(), new EventListener() {
				@Override
				public void onBrowserEvent(Event event) {
					if(event.getTypeInt() == Event.ONCLICK)
						((CellEditor)cell.cell).startEditing(cell.getValue(data));
				}
			});
			DOM.sinkEvents(cell.cell.getElement().getParentElement(),Event.ONCLICK);
			
			((EditableCell)cell.cell).addFinishedEditingHandler(new FinishedEditingEvent.Handler() {
				
				@Override
				public void onFinishEditing(FinishedEditingEvent event) {
					try {
						((EditableCell)event.getSource()).finishEditing();
					} catch (Exception e) {
						
					}
				}
			});
		}
	}
	
	private void render() {
		for(CellDataProvider<T,?> cell : cells) {
			cell.render(data);
		}
	}
	
}
