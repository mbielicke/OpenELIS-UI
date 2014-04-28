/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.ui.widget.web;

import java.util.ArrayList;

import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.widget.Confirm;
import org.openelis.ui.widget.WindowInt;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is an implementation of the ScreenWindowInt to be used by the Web portal of OpenELIS
 *
 */
public class WebWindow extends ResizeComposite implements WindowInt {
	
    @UiTemplate("WebWindow.ui.xml")
    interface WebWindowUiBinder extends UiBinder<Widget, WebWindow>{};
    private static WebWindowUiBinder uiBinder = GWT.create(WebWindowUiBinder.class);
    
	protected AbsolutePanel glass;
	
	@UiField
	protected AbsolutePanel title;
	
	@UiField
	protected LayoutPanel  content;
	protected Widget screen;
	private Confirm confirm;
	private Label name;
	
	/**
	 * No-Arg constructor that sets up the skeleton of the Window.
	 */
	public WebWindow() {
	    
	    initWidget(uiBinder.createAndBindUi(this));
	    
		name = new Label();
		
		title.setStyleName("crumbline");
		name.setStyleName("webLabel");
		content.setStyleName("ContentPanel");
		title.add(name);
						
	}

	/**
	 * Stub Method implemented to satisfy interface
	 */
	public HandlerRegistration addBeforeClosedHandler(
			BeforeCloseHandler<WindowInt> handler) {
		//Do Nothing
		return null;
	}

	/**
	 * Stub method implemented to satisfy interface
	 */
	public void fireEvent(GwtEvent<?> event) {
		//Do Nothing
	}

	/**
	 * Method is called when the windows content is changed through user actions
	 */
	public void setContent(Widget screen) {
		content.clear();
		content.add(screen);
		this.screen = screen;
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            
            @Override
            public void execute() {
                onResize();
            }
        });
	}

	/**
	 * Sets the text in the name label of the screen
	 */
	public void setName(String nme) {
		name.setText(nme);
	}

	/**
	 * Stub method implemented to satisfy interface
	 */
	public void close() {
		// Do Nothing
	}

	/**
	 * Stub method implemented to satisfy interface
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/**
	 * Stub method implemented to satisfy interface
	 */
	public void setMessagePopup(ArrayList<Exception> exceptions,
			String style) {
	}

	/**
	 * Stub method implemented to satisfy interface
	 */
	public void clearMessagePopup(String style) {
	}

	/**
	 * Stub method implemented to satisfy interface
	 */
	public void setStatus(String text, String style) {
	}

	/**
	 * Lays a glass panel of the content of the window to disallow user interaction
	 */
	public void lockWindow() {
    	if(glass == null) {
    		glass = new AbsolutePanel();
    		glass.setStyleName("GlassPanel");
    		glass.setHeight(content.getOffsetHeight()+"px");
    		glass.setWidth(content.getOffsetWidth()+"px");
    		RootPanel.get().add(glass, content.getAbsoluteLeft(),content.getAbsoluteTop());
    	}
	}

	/**
	 * Removes the glass panel to allow the user to interact with the screen.
	 */
	public void unlockWindow() {
    	if(glass != null) {
    		glass.removeFromParent();
    		glass = null;
    	}
	}

	/**
	 * Pops up a busy confirmation window with only a spinning icon and no text
	 */
	public void setBusy() {
		setBusy("");
	}

	/**
	 * Pops up a busy confirmation window with a spinngin icon and and the passed message
	 */
	public void setBusy(String message) {
		confirm = new Confirm(Confirm.Type.BUSY,null,message,null);
		confirm.show();
	}

	/**
	 * Hides the confirmation dialog if showing
	 */
	public void clearStatus() {
		if(confirm != null)
			confirm.hide();
	}

	/**
	 * Hides the confirmation dialog if showing
	 */
	public void setDone(String message) {
		clearStatus();
	}

	/**
	 * Pops up an Error confirmation with the passed message
	 */
	public void setError(String message) {
	    clearStatus();
		confirm = new Confirm(Confirm.Type.ERROR,"Error",message,"OK");
		confirm.show();
	}
	
	/**
	 * Stub method implemented to satisfy interface
	 */
	public void setProgress(int percent) {

	}

	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<WindowInt> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void makeDragable(DragController controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionGlass() {
		// TODO Auto-generated method stub
		
	}


    @Override
    public Widget getContent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTabIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setAccessKey(char key) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFocus(boolean focused) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTabIndex(int index) {
        // TODO Auto-generated method stub   
    }
    
    @Override
    public void onResize() {
        super.onResize();
        if(!(screen instanceof RequiresResize))
            screen.setSize(getOffsetWidth()+"px", getOffsetHeight()+"px");
    }
}
