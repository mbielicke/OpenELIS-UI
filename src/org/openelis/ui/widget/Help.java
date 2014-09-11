package org.openelis.ui.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class Help extends FocusPanel implements ClickHandler {
	
	protected String text;
	protected Widget widget;
	protected HelpBalloon.Placement placement = HelpBalloon.Placement.TOP;
	protected static HelpBalloon balloon = new HelpBalloon();
	
	public Help() {
		addClickHandler(this);
	}

	@Override
	public void onClick(ClickEvent event) {
		if(text != null)
			balloon.show(this, placement, text);
		else if(widget != null)
			balloon.show(this, placement, widget);
	}
	
	@UiChild(tagname="helpPanel")
	public void setPanel(Widget widget) {
		this.widget = widget;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setPlacement(HelpBalloon.Placement placement) {
		this.placement = placement;
	}
	
	public void setBalloonWidth(String width) {
		balloon.setWidth(width);
	}
	
	public void setBalloonHeight(String height) {
		balloon.setHeight(height);
	}

}
