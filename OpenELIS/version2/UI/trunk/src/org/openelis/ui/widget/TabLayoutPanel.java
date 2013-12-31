package org.openelis.ui.widget;

import java.util.HashMap;
import java.util.HashSet;

import org.openelis.ui.resources.TabPanelCSS;
import org.openelis.ui.resources.UIResources;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class TabLayoutPanel extends com.google.gwt.user.client.ui.TabLayoutPanel {
    
    protected PickupDragController drag;
    protected SimpleDropController drop;
    protected HashMap<Integer,Window> popouts;
    protected boolean closing;
    protected HashSet<Integer> needsResize = new HashSet<Integer>();
    protected double barHeight;
    protected Unit barUnit;
    protected FlowPanel tabBar;
    protected DeckLayoutPanel deck;
    protected AbsolutePanel blank;
    protected boolean visibleTabSet;
    
    public enum TabPosition {TOP,BOTTOM,RIGHT,LEFT};
    
    TabPosition tabPos = TabPosition.TOP;
    
    TabPanelCSS css = UIResources.INSTANCE.tabpanel();

    public TabLayoutPanel(double barHeight, Unit barUnit) {
        super(barHeight, barUnit);
        
        css.ensureInjected();
        
        this.barHeight = barHeight;
        this.barUnit = barUnit;

        tabBar  = (FlowPanel) ((LayoutPanel)getWidget()).getWidget(0);
        deck    = (DeckLayoutPanel) ((LayoutPanel)getWidget()).getWidget(1);
        
        addSelectionHandler(new SelectionHandler<Integer>() {
            
            @Override
            public void onSelection(final SelectionEvent<Integer> event) {
                if(needsResize.contains(event.getSelectedItem())) {
                    needsResize.remove(new Integer(event.getSelectedItem()));
                    if(getWidget(event.getSelectedItem()) instanceof RequiresResize)
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                ((RequiresResize)getWidget(event.getSelectedItem())).onResize();
                            }
                        });
                }
                
            }
        });
        
        blank = new AbsolutePanel();
        blank.addStyleName(css.TabContainer());
        add(blank);
        setTabVisible(0, false);
        
    }
    
    public void setTabPosition(TabPosition tabPos) {
        this.tabPos = tabPos;
        
        LayoutPanel panel = (LayoutPanel)getWidget();
        Widget deck = panel.getWidget(1);
        
       
        if(tabPos == TabPosition.BOTTOM) {
            panel.setWidgetLeftRight(tabBar, 0, Unit.PX, 0, Unit.PX);
            panel.setWidgetBottomHeight(tabBar, 0, Unit.PX, barHeight, barUnit);
            panel.setWidgetTopBottom(deck, 0, Unit.PX, barHeight, barUnit);
        }else if(tabPos == TabPosition.LEFT) {
            panel.setWidgetTopBottom(deck, 0, Unit.PX, 0, Unit.PX);
            panel.setWidgetTopBottom(tabBar, 0, Unit.PX, 0, Unit.PX);
            panel.setWidgetLeftRight(deck, barHeight, barUnit, 0, Unit.PX);
            panel.setWidgetLeftWidth(tabBar, 0, Unit.PX, barHeight, Unit.PX);
            panel.setWidgetVerticalPosition(tabBar,Alignment.BEGIN);
            tabBar.getElement().getStyle().setHeight(16834, Unit.PX);
            tabBar.getElement().getStyle().setWidth(barHeight, barUnit);
            for(int i = 0; i < getWidgetCount(); i++) {
                getTabWidget(i).getElement().getStyle().setFloat(Float.LEFT);
                ((TabWidget)getTabWidget(i)).setVertical();
            }
        }else if(tabPos == TabPosition.RIGHT) {
            panel.setWidgetTopBottom(deck, 0, Unit.PX, 0, Unit.PX);
            panel.setWidgetTopBottom(tabBar, 0, Unit.PX, 0, Unit.PX);
            panel.setWidgetLeftRight(deck,  0, Unit.PX, barHeight, barUnit);
            panel.setWidgetRightWidth(tabBar, 0, Unit.PX, barHeight, Unit.PX);
            panel.setWidgetVerticalPosition(tabBar,Alignment.BEGIN);
            tabBar.getElement().getStyle().setHeight(16834, Unit.PX);
            tabBar.getElement().getStyle().setWidth(barHeight, barUnit);
            for(int i = 0; i < getWidgetCount(); i++) {
                getTabWidget(i).getElement().getStyle().setFloat(Float.RIGHT);
                ((TabWidget)getTabWidget(i)).setVertical();
               
            }
        }
    }
    
    public void setPopoutBrowser(final Browser browser) {
        drag = new PickupDragController(browser.browser,false) {
            @Override
            public void previewDragStart() throws VetoDragException {
                super.previewDragStart();
                if(popouts.keySet().contains(getTabWidgetIndex(context.draggable)))
                    throw new VetoDragException();
                                
            }
            
            @Override
            protected Widget newDragProxy(DragContext context) {
                AbsolutePanel panel;
                
                panel = new AbsolutePanel();
                panel.setStyleName(css.TabDraggable());
                TabWidget tabW = (TabWidget)context.draggable;
                panel.add(new Label(tabW.getText()));
                
                return panel;
                
            }
            
        }; 
        
        drag.setBehaviorDragProxy(true);
        drag.setBehaviorDragStartSensitivity(10);
        
        
        for(int i = 0; i < getWidgetCount(); i++) {
            drag.makeDraggable(getTabWidget(i));
        }
        
        popouts = new HashMap<Integer,Window>();
        drop = new SimpleDropController(browser.browser) {
            @Override
            public void onDrop(DragContext context) {
                super.onDrop(context);
                
                if(context.desiredDraggableX > 0) {
                    
                    final Window win = new Window(true);
                    final int index = getTabWidgetIndex(context.draggable);
                    final Widget wid = getWidget(index);
                    
                    boolean showing = index == getSelectedIndex();
                    
                    win.setName(((TabWidget)context.draggable).getText());
                    win.setContentSize(getOffsetWidth(),getOffsetHeight());
                    
                    getWidget(index).setVisible(true);
                    remove(index);
                    
                    win.setContent(wid);
                    LayoutPanel holder = new LayoutPanel();
                    holder.setStyleName(UIResources.INSTANCE.tabpanel().Popped());
                    insert(holder,context.draggable,index);
                    browser.addWindow(win,"modules",context.desiredDraggableX,context.desiredDraggableY);
                    setTabPoppedOut(index);
                    if(showing)
                        selectTab(index);
                    popouts.put(new Integer(index),win);
                                        
                    win.addCloseHandler(new CloseHandler<WindowInt>() {
                    
                        @Override
                        public void onClose(CloseEvent<WindowInt> event) {
                            if (isAttached() && !closing) {
                                Widget tab = getTabWidget(index);
                                popouts.remove(index);
                                remove(index);
                                insert(wid, tab, index);
                                selectTab(index);
                                setTabPoppedIn(index);
                                
                                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                    
                                    @Override
                                    public void execute() {
                                        ((RequiresResize)getWidget(index)).onResize();
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
    
    public void setTabVisible(int index, boolean visible) {
        boolean anyVisible = false;
        
        tabBar.getWidget(index).setVisible(visible);
        
        for(int i = 0; i < tabBar.getWidgetCount(); i++){
            if(tabBar.getWidget(i).isVisible()) {
                anyVisible = true;
                break;
            }
        }
        
        showTabBar(anyVisible);
        
        if(!anyVisible || index == getSelectedIndex()) {
            selectTab(blank);
        }
    
        
    }
    
    @Override
    public void add(Widget child, Widget tab) {
        if(drag != null)
            drag.makeDraggable(tab);
        
        child.addStyleName(css.TabContainer());
        
        super.insert(child, tab, getTabCount());
        
        needsResize.add(getWidgetIndex(child));
        
        if(tabPos == TabPosition.LEFT || tabPos == TabPosition.RIGHT)
            ((TabWidget)tab).setVertical();
        
        if(tabPos == TabPosition.LEFT)
            tab.getElement().getStyle().setFloat(Float.LEFT);
        else if(tabPos == TabPosition.RIGHT) 
            tab.getElement().getStyle().setFloat(Float.RIGHT);
        
        
        if(tab instanceof TabWidget) 
            setTabVisible(getTabCount()-1,((TabWidget)tab).tabVisible);
        
        if(!visibleTabSet && tabBar.getWidget(getTabCount()-1).isVisible()) {
            selectTab(getTabCount() -1);
            visibleTabSet = true;
        }
            
        
        

    }
    
    @Override
    public Widget getWidget(int index) {
        if(popouts != null && popouts.keySet().contains(index))
            return popouts.get(new Integer(index));
        return super.getWidget(index);
    }

    @Override
    public int getWidgetIndex(IsWidget child) {
        
        for(int index : popouts.keySet()) 
            if(popouts.get(new Integer(index)).getContent() == child)
                return index;        
        
        return super.getWidgetIndex(child);
    }
    
    @Override
    public int getSelectedIndex() {
        int index = super.getSelectedIndex();
        
        if(index > -1) {
            if(getWidget(index) == blank)
                index = -1;
        }
        
        return index;
    }
    
    public int getTabCount() {
        return getWidgetIndex(blank) > -1 ? super.getWidgetCount() - 1 : super.getWidgetCount();
    }
    
    public int getTabWidgetIndex(Widget tab) {
        for(int i = 0; i < getTabCount(); i++) {
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
  
    public void showTabBar(boolean show) {
        UIObject.setVisible(((LayoutPanel)getWidget()).getWidgetContainerElement(tabBar),show);
        tabBar.setVisible(show);
    }
    
    public void needsResize(int index) {
        if(getSelectedIndex() != index)
            needsResize.add(index);
    }
    
    @Override
    public void onResize() {
        needsResize.clear();
        for(int i = 0; i < getTabCount(); i++)
            needsResize.add(i);
        needsResize.remove(new Integer(getSelectedIndex()));
        super.onResize();
        
    }

}
