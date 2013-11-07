package org.openelis.ui.widget;

import org.openelis.ui.widget.CollapsePanel;
import org.openelis.ui.resources.CollapseCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class OpenELISLayout extends Composite {
	
	protected DockLayoutPanel dock;
	protected CollapsePanel collapse;
	protected Widget content;
	protected Screen panel;

	protected CollapseCSS css;
	
	public OpenELISLayout() {
		css = UIResources.INSTANCE.collapse();
		css.ensureInjected();
		
		dock = new DockLayoutPanel(Unit.PX);
		collapse = new CollapsePanel(false);
		collapse.setStyleName(css.LeftSidePanel());
		dock.addWest(collapse,10);
		initWidget(dock);
		
		collapse.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				//dock.setPixelSize(width+event.getWidth()+10,height);
				dock.setWidgetSize(collapse, event.getWidth()+10);
				panel.setWidth((panel.getWidth()+event.getWidth()+10)+"px");
				//content.setWidth((content.getOffsetWidth()+event.getWidth()+10)+"px");
			}
		});
	}
	
	public void addLeft(Widget widget) {
		collapse.setContent(widget);
	}
	
	public void addContent(Widget content) {
		this.content = content;
		dock.add(content);
	}
	
	public void setViewPanel(Screen panel) {
		this.panel = panel;
	}
	
	/*
	public void setWidth(int width) {
		this.width = width;
		dock.setPixelSize(width,height);
	}
	
	public void setHeight(int height) {
		this.height = height;
		dock.setPixelSize(width, height);
	}
	*/
	
}
