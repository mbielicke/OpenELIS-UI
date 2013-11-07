package org.openelis.ui.widget;

import java.util.HashMap;

import org.openelis.ui.resources.UIResources;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabLayoutPanel extends com.google.gwt.user.client.ui.TabLayoutPanel {
    
    protected PickupDragController drag;
    protected SimpleDropController drop;
    protected HashMap<Integer,Window> popouts;
    protected boolean closing;
    
    public TabLayoutPanel(double barHeight, Unit barUnit) {
        super(barHeight, barUnit);
    }
    
    public void setPopoutBrowser(final Browser browser) {
        drag = new PickupDragController(browser.browser,false) {
            @Override
            public void previewDragStart() throws VetoDragException {
                super.previewDragStart();
                if(popouts.keySet().contains(getTabWidgetIndex(context.draggable)))
                    throw new VetoDragException();
                                
            }
        }; 
        
        drag.setBehaviorDragProxy(true);
        drag.setBehaviorDragStartSensitivity(10);
        
        
        for(int i = 0; i < getWidgetCount(); i++) {
            drag.makeDraggable(getTabWidget(i));
        }
        
        popouts = new HashMap<Integer,Window>();
        drop = new SimpleDropController(browser) {
            @Override
            public void onDrop(DragContext context) {
                super.onDrop(context);
                
                if(context.desiredDraggableX > 0) {
                    
                    final Window win = new Window();
                    final int index = getTabWidgetIndex(context.draggable);
                    boolean showing = index == getSelectedIndex();
                    win.setName(((TabWidget)context.draggable).getText());
                    win.setSize(getOffsetWidth()+"px",getOffsetHeight()+"px");
                    getWidget(index).setVisible(true);
                    win.setContent(getWidget(index));
                    LayoutPanel holder = new LayoutPanel();
                    holder.setStyleName(UIResources.INSTANCE.tabpanel().Popped());
                    insert(holder,context.draggable,index);
                    browser.addWindow(win,"modules",context.desiredDraggableX,context.desiredDraggableY);
                    setTabPoppedOut(index);
                    if(showing)
                        selectTab(index);
                    popouts.put(index,win);
                    win.addCloseHandler(new CloseHandler<WindowInt>() {
                    
                        @Override
                        public void onClose(CloseEvent<WindowInt> event) {
                            if (isAttached() && !closing) {
                                Widget tab = getTabWidget(index);
                                remove(index);
                                insert(win.getContent(), tab, index);
                                selectTab(index);
                                popouts.remove(win);
                                setTabPoppedIn(index);
                                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                    
                                    @Override
                                    public void execute() {
                                        forceLayout();
                                    }
                                });
                            }
                        }
                    });
                }
                
            }
        };
        drag.registerDropController(drop);
        
        addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                if(popouts.keySet().contains(event.getItem()))
                    event.cancel();
            }
        });
        
    }
    
    
    @Override
    public void add(Widget child, Widget tab) {
        if(drag != null)
            drag.makeDraggable(tab);
        super.add(child, tab);
    }
    
    @Override
    public Widget getWidget(int index) {
        if(popouts.keySet().contains(index))
            return popouts.get(index);
        return super.getWidget(index);
    }

    @Override
    public int getWidgetIndex(IsWidget child) {
        
        for(int index : popouts.keySet()) 
            if(popouts.get(index).getContent() == child)
                return index;        
        
        return super.getWidgetIndex(child);
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
        for(Window window : popouts.values()) 
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
    
    protected void setTabPoppedOut(int index) {
        ((TabWidget)getTabWidget(index)).setPoppedOut();
    }
    
    protected void setTabPoppedIn(int index) {
        ((TabWidget)getTabWidget(index)).setPoppedIn();
    }
  

}
