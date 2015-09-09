/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.ui.widget;

import java.util.HashMap;

import org.openelis.ui.resources.UIResources;
import org.openelis.ui.resources.WindowCSS;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * WindowBrowser will display Screen widgets in draggable Windows
 * in a certain portion of the screen.  It uses the ScreenWindow
 * widget to wrap the contents of a screen.  It also control the 
 * z-index of the windows displayed.
 * 
 * @author tschmidt
 *
 */
public class Browser extends ResizeComposite {
    
    /*
     * The main panel used to contain and display windows
     */
    protected AbsolutePanel browser;
    protected LayoutPanel   layout;
    
    /*
     * Hash of all currently displayed windows
     */
    protected HashMap<WindowInt,WindowValues> windows;
    protected HashMap<String,WindowInt> windowsByKey;
    
    /*
     * Integers for current zIndex and limit of windows shown
     */
    protected int index,limit ;
    
    /*
     * Drag and drop controllers
     */
    protected PickupDragController dragController;
    protected AbsolutePositionDropController dropController; 
    
    /*
     * Reference to the currently focused window
     */
    protected WindowInt focusedWindow;
        
    protected WindowCSS css;
        
    /**
     * Constructor that takes an arguments if the browser should auto-size to the window,
     * and the number of allowed screens to show at once.
     * @param size
     * @param limit
     */
    @UiConstructor
    public Browser(int limit) {
        
    	browser = new AbsolutePanel();
        browser.setWidth("100%");
        browser.setHeight("100%");
        layout = new LayoutPanel();
        windows = new HashMap<WindowInt, WindowValues>();
        windowsByKey = new HashMap<String,WindowInt>();
        
        dragController = new PickupDragController(browser,true);
        
        dropController = new AbsolutePositionDropController(browser) {
            @Override
            public void onDrop(DragContext context) {
                super.onDrop(context);
                ((WindowInt)context.draggable).positionGlass();
            }
        };
        
        this.limit = limit;
        
        layout.add(browser);
        initWidget(layout);
        
        dragController.setBehaviorDragProxy(true);
        dragController.registerDropController(dropController);
        DOM.setStyleAttribute(browser.getElement(),
                              "overflow",
                              "auto");
        
        setKeyHandling();
        
        setCSS(UIResources.INSTANCE.window());
        
        // These handlers are added to prevent the default drag/drop from happening
        
        addDomHandler(new DropHandler() {
			
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, DropEvent.getType());
        
        addDomHandler(new DragEnterHandler() {
			
			@Override
			public void onDragEnter(DragEnterEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, DragEnterEvent.getType());
        
        addDomHandler(new DragLeaveHandler() {
			
			@Override
			public void onDragLeave(DragLeaveEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		}, DragLeaveEvent.getType());
        
        addDomHandler(new DragOverHandler() {
			
			@Override
			public void onDragOver(DragOverEvent event) {
				event.stopPropagation();
				event.preventDefault();
			}
		},DragOverEvent.getType());
    }
    
    public void setKeyHandling() {
        /**
         * This handler is added to forward the key press event on to the focused window if received by the browser
         */
        addDomHandler(new KeyDownHandler() {
        	public void onKeyDown(KeyDownEvent event) {
   				KeyDownEvent.fireNativeEvent(event.getNativeEvent(), focusedWindow);        		
        	}
        },KeyDownEvent.getType());
        
    } 

    public void addWindow(WindowInt window, String key) {
        addWindow(window,key,windows.size()*25,windows.size()*25);
    }
    
    /**
     * Adds a Window directly to the browser indexing it by the passed key
     * @param window
     * @param key
     */
    public void addWindow(WindowInt window, String key, int left, int top) {
        WindowValues wv;
        
    	index++;
    	browser.add(window.asWidget(),left,top);
    	wv = new WindowValues();
    	wv.key = key+index;
    	wv.zIndex = index;
    	windows.put(window,wv);
    	windowsByKey.put(key+index,window);
    	window.addCloseHandler(new CloseHandler<WindowInt>() {
    	    public void onClose(CloseEvent<WindowInt> event) {
    	      if(windows.containsKey(event.getSource())) 
    	    	  windowsByKey.remove(windows.remove(event.getSource()).key);
    	      
    	      setFocusedWindow();
    	    }
    	});
    	
    	window.addFocusHandler(new FocusHandler() {
    	    public void onFocus(FocusEvent event) {
    	        selectScreen(windows.get(event.getSource()).key);
    	        
    	    }
    	});
    	
    	window.makeDragable(dragController);
    	setFocusedWindow();
    	focusedWindow = window;
    	window.setFocus(true);
    }
    
    /**
     * Brings the window indexed by the passed key to be the focusedWindow
     * @param key
     * @return
     */
    public boolean selectScreen(String key) {
        WindowInt wid;
        WindowValues wv;
        
    	if (windowsByKey.containsKey(key)) {
    		wid = windowsByKey.get(key);
    		wv = windows.get(wid);
    		
    		if(index != wv.zIndex){
    			index++;
    			wv.zIndex = index;
    			int top = browser.getWidgetTop(wid.asWidget());
    			int left = browser.getWidgetLeft(wid.asWidget());
  				browser.add(wid, left, top);
    			setFocusedWindow();
    		}
    		return true;
    	}
    	return false;
    }
        
    /**
     * This method will make sure that the window with the largest index is brought to the 
     * top and focused.
     */
    public void setFocusedWindow() {
    	for(WindowInt wid : windowsByKey.values()) {
    		if(windows.get(wid).zIndex != index){
    			if(wid.asWidget().getStyleName().indexOf(css.unfocused()) < 0){	
    				wid.asWidget().addStyleName(css.unfocused());
    			}
    		}else{
    			wid.asWidget().removeStyleName(css.unfocused());
    			focusedWindow = wid;
    			wid.setFocus(true);
    		}
    	}
    }
    /**
     * This method will return true if their is a window currently in the browser
     * using the key passed.
     * @param key
     * @return
     */
    public WindowInt getScreenByKey(String key) {
    	return windowsByKey.get(key);
    }
    
    private class WindowValues {
        protected String key;
        protected int zIndex;
    }
    
    public void setCSS(WindowCSS css) {
    	css.ensureInjected();
    	this.css = css;
    	
    }
    
    @Override
    public void onResize() {
        super.onResize();
        for(WindowInt window : windowsByKey.values()) {
            if(window instanceof org.openelis.ui.widget.Window) {
                ((RequiresResize)window).onResize();
            }
        }
    }
	
}
