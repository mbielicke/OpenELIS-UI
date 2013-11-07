package org.openelis.gwt.widget;

import java.util.ArrayList;

import org.openelis.gwt.resources.UIResources;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabLayoutPanel extends com.google.gwt.user.client.ui.TabLayoutPanel {
    
    protected PickupDragController drag;
    protected SimpleDropController drop;
    protected ArrayList<Window> popouts;
    protected boolean closing;
    
    public TabLayoutPanel(double barHeight, Unit barUnit) {
        super(barHeight, barUnit);

    }
    
    public void setPopoutBrowser(final Browser browser) {
        drag = new PickupDragController(RootPanel.get(),false);
        drag.setBehaviorDragProxy(true);
        drag.setBehaviorDragStartSensitivity(10);
        
        for(int i = 0; i < getWidgetCount(); i++) {
            drag.makeDraggable(getTabWidget(i));
        }
        
        popouts = new ArrayList<Window>();
        drop = new SimpleDropController(browser) {
            @Override
            public void onDrop(DragContext context) {
                super.onDrop(context);
                
                if(context.desiredDraggableX > 0) {
                    final Window win = new Window();
                    final int index = getTabWidgetIndex(context.draggable);
                    win.setName(((TabWidget)context.draggable).getText());
                    win.setSize(getOffsetWidth()+"px",getOffsetHeight()+"px");
                    getWidget(index).setVisible(true);
                    win.setContent(getWidget(index));
                    LayoutPanel holder = new LayoutPanel();
                    holder.setStyleName(UIResources.INSTANCE.tabpanel().angled());
                    insert(holder,context.draggable,index);
                    selectTab(index);
                    browser.addWindow(win,"modules",context.desiredDraggableX,context.desiredDraggableY);
                    popouts.add(win);
                    win.addCloseHandler(new CloseHandler<WindowInt>() {
                    
                        @Override
                        public void onClose(CloseEvent<WindowInt> event) {
                            if (isAttached() && !closing) {
                                Widget tab = getTabWidget(index);
                                remove(index);
                                insert(win.getContent(), tab, index);
                                forceLayout();
                                selectTab(index);
                                popouts.remove(win);
                            }
                        }
                    });
                }
                
            }
        };
        drag.registerDropController(drop);
        
    }
    
    @Override
    public void add(Widget child, Widget tab) {
        if(drag != null)
            drag.makeDraggable(tab);
        super.add(child, tab);
    }
    
    
    public int getTabWidgetIndex(Widget tab) {
        for(int i = 0; i < getWidgetCount(); i++) {
            if(tab == getTabWidget(i))
                return i;
        }
        return -1;
    }
    
    public void close() {
        closing = true;
        for(Window window : popouts) 
            window.close();
    }
    
    public void setTabInError(int index) {
        ((TabWidget)getTabWidget(index)).setTabInError();
    }

    public void setTabHasData(int index) {
        ((TabWidget)getTabWidget(index)).setTabHasData();
    }

    public void removeTabInError(int index) {
        ((TabWidget)getTabWidget(index)).removeTabInError();
    }

    public void removeTabHasData(int index) {
        ((TabWidget)getTabWidget(index)).removeTabHasData();
    }
  

}
