package org.openelis.ui.widget;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class ResizePanel extends LayoutPanel {
	
	public ResizePanel() {
		final ResizePanel source = this;
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				source.setSize((Window.getClientWidth()-source.getAbsoluteLeft()-16)+"px",
						
						       (Window.getClientHeight()-source.getAbsoluteTop()-10)+"px");
				source.onResize();
			}
		});
	}

}
