package org.openelis.ui.widget;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class PortalPanel extends AbsolutePanel implements RequiresResize, ProvidesResize {

	
	public PortalPanel() {
		getElement().getStyle().setOverflow(Overflow.AUTO);
	}

	@Override
	public void onResize() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

}
