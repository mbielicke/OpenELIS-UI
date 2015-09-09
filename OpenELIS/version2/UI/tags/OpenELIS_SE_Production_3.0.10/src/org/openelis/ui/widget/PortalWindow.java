package org.openelis.ui.widget;

import java.util.ArrayList;

import org.openelis.ui.event.BeforeCloseHandler;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class PortalWindow implements WindowInt {
	
	protected AbsolutePanel glass;
	
	protected PortalStatus status;
	protected PopupPanel popup;

	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<WindowInt> handler) {
		return null;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		
	}

	@Override
	public HandlerRegistration addBeforeClosedHandler(
			BeforeCloseHandler<WindowInt> handler) {
		return null;
	}

	@Override
	public Widget asWidget() {
		return null;
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return null;
	}

	@Override
	public int getTabIndex() {
		return 0;
	}

	@Override
	public void setAccessKey(char key) {
		
	}

	@Override
	public void setFocus(boolean focused) {
		
	}

	@Override
	public void setTabIndex(int index) {
		
	}

	@Override
	public void setMessagePopup(ArrayList<Exception> exceptions, String style) {
		
	}

	@Override
	public void clearMessagePopup(String style) {
		
	}

	@Override
	public void setStatus(String text, String style) {
		
	}

	@Override
	public void lockWindow() {
    	if(glass == null) {
    		glass = new AbsolutePanel();
    		glass.setStyleName("GlassPanel");
    		glass.setHeight(com.google.gwt.user.client.Window.getClientHeight()+"px");
    		glass.setWidth(com.google.gwt.user.client.Window.getClientWidth()+"px");
    		RootPanel.get().add(glass, 0,0);
    	}
		
	}

	@Override
	public void unlockWindow() {
		if(glass != null) {
    		glass.removeFromParent();
    		glass = null;
    	}
		
	}

	@Override
	public void setBusy() {
		setBusy("");
	}

	@Override
	public void setBusy(String message) {
		if(status == null) {
			status = new PortalStatus();
		}
		status.setBusy(message);
		showStatus(true);
	}

	private void showStatus(boolean modal) {
		if(popup == null)
			popup = new PopupPanel();
		popup.setWidget(status);
		popup.setModal(modal);
		popup.setAutoHideEnabled(!modal);
		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int width = com.google.gwt.user.client.Window.getClientWidth();
				popup.setPopupPosition((width - offsetWidth) / 2, 200);
			}
			
		});
	}
	
	@Override
	public void clearStatus() {
		if(popup != null)
			popup.hide();
	}

	@Override
	public void setDone(String message) {
		clearStatus();
	}

	@Override
	public void setError(String message) {
		if(status == null) {
			status = new PortalStatus();
		}
		status.setError(message);
		showStatus(false);
	}

	@Override
	public void setContent(Widget content) {
		
	}

	@Override
	public void setName(String name) {
		
	}

	@Override
	public void close() {
		
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public void makeDragable(DragController controller) {
		
	}

	@Override
	public void positionGlass() {
		
	}

	@Override
	public Widget getContent() {
		return null;
	}

}
