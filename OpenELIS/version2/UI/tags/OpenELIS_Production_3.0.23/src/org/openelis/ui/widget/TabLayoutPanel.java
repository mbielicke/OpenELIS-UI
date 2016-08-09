package org.openelis.ui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.openelis.ui.common.Util;
import org.openelis.ui.resources.TabBarScrollerCSS;
import org.openelis.ui.resources.TabPanelCSS;
import org.openelis.ui.resources.UIResources;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class TabLayoutPanel extends com.google.gwt.user.client.ui.TabLayoutPanel implements SelectionHandler<Integer> { 
                                                                                           
    protected TabPopper popper;
    protected HashMap<Integer,Window> popouts = new HashMap<Integer,Window>();
    protected boolean closing;
    protected HashSet<Integer> needsResize = new HashSet<Integer>();
    protected double barHeight;
    protected Unit barUnit;
    protected FlowPanel tabBar;
    protected DeckLayoutPanel deck;
    protected AbsolutePanel blank;
    protected boolean visibleTabSet;
    protected TabBarScroller tabScroller;
    
    public enum TabPosition {TOP,BOTTOM,RIGHT,LEFT};
    
    TabPosition tabPos = TabPosition.TOP;
    
    protected TabPanelCSS css = UIResources.INSTANCE.tabpanel();
    protected TabBarScrollerCSS scrollCss = UIResources.INSTANCE.tabBarScroller();

    public TabLayoutPanel(double barHeight, Unit barUnit) {
        super(barHeight, barUnit);
        
        css.ensureInjected();
        scrollCss.ensureInjected();
        
        this.barHeight = barHeight;
        this.barUnit = barUnit;

        tabBar  = (FlowPanel) ((LayoutPanel)getWidget()).getWidget(0);
        deck    = (DeckLayoutPanel) ((LayoutPanel)getWidget()).getWidget(1);

        tabScroller = new TabBarScroller(tabBar);

        //Put the widget back together after adding scroller
        
        LayoutPanel panel = ((LayoutPanel)getWidget());
        panel.clear();
        panel.add(tabScroller);
        panel.setWidgetLeftRight(tabScroller, 0, Unit.PX, 0, Unit.PX);
        panel.setWidgetTopHeight(tabScroller, 0, Unit.PX, barHeight, barUnit);
        panel.setWidgetVerticalPosition(tabScroller, Alignment.END);
        panel.add(deck);
        panel.setWidgetLeftRight(deck, 0, Unit.PX, 0, Unit.PX);
        panel.setWidgetTopBottom(deck, barHeight, barUnit, 0, Unit.PX);

        blank = new AbsolutePanel();
        blank.addStyleName(css.TabContainer());
        add(blank);
        setTabVisible(0, false);
        
        addSelectionHandler(this);
    }
    
    public void setTabPosition(TabPosition tabPos) {
        this.tabPos = tabPos;
        
        LayoutPanel panel;
        Widget deck;
        
        panel = (LayoutPanel)getWidget();
        deck = panel.getWidget(1);
       
        switch(tabPos) {
            case BOTTOM : 
                positionTabsBottom(panel,deck);
                break;
            case LEFT : 
                positionTabsLeft(panel,deck);
                break;
            case RIGHT : 
                positionTabsRight(panel,deck);
                break;
            default :
        }
    }
    
    protected void positionTabsBottom(LayoutPanel panel, Widget deck) {
        panel.setWidgetLeftRight(tabBar, 0, Unit.PX, 0, Unit.PX);
        panel.setWidgetBottomHeight(tabBar, 0, Unit.PX, barHeight, barUnit);
        panel.setWidgetTopBottom(deck, 0, Unit.PX, barHeight, barUnit);
    }
    
    protected void positionTabsLeft(LayoutPanel panel, Widget deck) {
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
    }
    
    protected void positionTabsRight(LayoutPanel panel, Widget deck) {
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
    
    public void setPopoutBrowser(final Browser browser) {
        popper = new TabPopper(browser);
    }
    
    public void setTabVisible(int index, boolean visible) {
        boolean anyVisible = isAnyTabVisible();
            
        tabBar.getWidget(index).setVisible(visible); 
        
        showTabBar(anyVisible);
        if(!anyVisible || (index == getSelectedIndex() && !visible)) {
            selectTab(blank);
        }
        
        checkForScroll();
    }
    
    public boolean isAnyTabVisible() {
        for(Widget tab : tabBar) { 
            if(tab.isVisible()) 
                return true;
        }
        return false;
    }
    
    @Override
    public void add(Widget child, Widget tab) {
        if(popper != null)
            popper.makeDraggable(tab);
        
        child.addStyleName(css.TabContainer());
        
        insert(child, tab, getTabCount());
        
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
            
        checkForScroll();
    }
    
    @Override
    public boolean remove(int index) {
        boolean ret;
        
        if(popouts != null && popouts.keySet().contains(index)) 
            popouts.get(new Integer(index)).close();
        
        ret = super.remove(index);
        checkForScroll();
        return ret;
    }
    
    @Override
    public boolean remove(Widget w) {
        return remove(getWidgetIndex(w));
    }
    
    @Override
    public Widget getWidget(int index) {
        if(popouts != null && popouts.keySet().contains(index))
            return popouts.get(new Integer(index));
        return super.getWidget(index);
    }

    @Override
    public int getWidgetIndex(Widget child) {
        /* Widget is replaced with blank in the deck panel if popped out
        *  so the default method will return -1 so we need to 
        *  check the popped list first.
        */
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
    
    public void setTabNotification(int index, String text) {
        ((TabWidget)getTabWidget(index)).setNotificaton(text);
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
        UIObject.setVisible(((LayoutPanel)getWidget()).getWidgetContainerElement(tabScroller),show);
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
        
        checkForScroll();
    }
    
    @Override
    public void onSelection(SelectionEvent<Integer> event) {
        if(!needsResize.contains(event.getSelectedItem())) 
            return;
        
        needsResize.remove(new Integer(event.getSelectedItem()));
        if(getWidget(event.getSelectedItem()) instanceof RequiresResize) {
            resizeTab(event.getSelectedItem());
        }
    }
    
    protected void resizeTab(final int index) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ((RequiresResize)getWidget(index)).onResize();
            }
        });
    }
    
    protected void checkForScroll() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                tabScroller.checkForScroll();
            }
        });
    }
   
    protected class TabBarScroller extends ResizeComposite {
        
        LayoutPanel scroller;
        AbsolutePanel scrollLeft, scrollRight;
        List<Integer> tabEdges;
        
        public TabBarScroller(FlowPanel tabBar) {
            scroller = new LayoutPanel();
            scrollLeft = new AbsolutePanel();
            scrollLeft.setStyleName(scrollCss.MoveLeft());
            scroller.add(scrollLeft);
            scroller.setWidgetLeftWidth(scrollLeft, 0.0, Unit.PX, 20, Unit.PX);
            scroller.setWidgetTopBottom(scrollLeft, 5.0, Unit.PX, 0, Unit.PX);
            scroller.add(tabBar);
            scroller.setWidgetLeftRight(tabBar, 20, Unit.PX, 20, Unit.PX);
            scrollRight = new AbsolutePanel();
            scrollRight.setStyleName(scrollCss.MoveRight());
            scroller.add(scrollRight);
            scroller.setWidgetRightWidth(scrollRight, 0, Unit.PX, 20, Unit.PX);
            scroller.setWidgetTopBottom(scrollRight, 5.0, Unit.PX, 0, Unit.PX);
            scroller.setWidth("100%");
            scroller.setHeight("100%");
            scroller.setStyleName("gwt-TabLayoutPanelTabs");
            tabBar.getElement().getStyle().setWidth(6000.0, Unit.PX);
            
            scrollLeft.setVisible(false);
            scrollRight.setVisible(false);
            scroller.getWidgetContainerElement(scrollLeft).getStyle().setDisplay(Display.NONE);
            scroller.getWidgetContainerElement(scrollRight).getStyle().setDisplay(Display.NONE);
            scroller.setWidgetLeftRight(tabBar, 0,Unit.PX, 0, Unit.PX);
            
            scrollRight.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    scrollRight();
                }
            }, ClickEvent.getType());
        
            scrollLeft.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    scrollLeft();
                }
            }, ClickEvent.getType());
            
            initWidget(scroller);
        }
        
        public void scrollRight() {
            int left,lastVisibleEdge,tabEdge; 
            
            tabEdge = 0;
            left = Util.stripUnits(tabBar.getElement().getStyle().getLeft(),"px");
            lastVisibleEdge = Math.abs(left) + scroller.getWidgetContainerElement(tabBar).getOffsetWidth();
            calculateTabEdges();
            
            if(lastVisibleEdge >= tabEdges.get(tabEdges.size()-1))
                return;
   
            for(Integer edge : tabEdges) {
               if(edge > lastVisibleEdge) {
                   tabEdge = edge;
                   break;
               }
            }
            
            tabBar.getElement().getStyle().setLeft(left-(tabEdge-lastVisibleEdge),Unit.PX);
            
        }
        
        public void scrollLeft() {
            int firstVisibleEdge,tabEdge;
            
            tabEdge = 0;
            firstVisibleEdge = Math.abs(Util.stripUnits(tabBar.getElement().getStyle().getLeft(),"px"));
            
            if(firstVisibleEdge == 0)
                return;
            
            calculateTabEdges();
            Collections.reverse(tabEdges);
            for(Integer edge : tabEdges) {
                if(edge < firstVisibleEdge) {
                    tabEdge = edge;
                    break;
                }
            }
                   
            tabBar.getElement().getStyle().setLeft(-tabEdge, Unit.PX);
        }
        
        public void calculateTabEdges() {
            int tabsWidth;
            
            tabEdges = new ArrayList<Integer>(getWidgetCount());
            tabsWidth = 0;
            
            for(Widget widget : tabBar) {
                tabEdges.add(tabsWidth);
                if(widget.isVisible())
                    tabsWidth += widget.getOffsetWidth()+2;//2 pixels added for MarginRight in css 
            }
            
        }
        
        public void checkForScroll() {
            int tabsWidth = 0, barWidth;
            
            for(Widget widget : tabBar) 
                tabsWidth += widget.getOffsetWidth()+2;//2 pixels added for MarginRight in css ;
            
            barWidth = scroller.getWidgetContainerElement(tabBar).getOffsetWidth();
            
            if(tabsWidth > barWidth) {
                showScrollArrows();
            }else {
                hideScrollArrows();
            }
        }
        
        protected void showScrollArrows() {
            scrollLeft.setVisible(true);
            scrollRight.setVisible(true);
            scroller.getWidgetContainerElement(scrollLeft).getStyle().setDisplay(Display.BLOCK);
            scroller.getWidgetContainerElement(scrollRight).getStyle().setDisplay(Display.BLOCK);
            scroller.setWidgetLeftRight(tabBar, 20, Unit.PX, 20, Unit.PX);
        }
        
        protected void hideScrollArrows() {
            scrollLeft.setVisible(false);
            scrollRight.setVisible(false);
            scroller.getWidgetContainerElement(scrollLeft).getStyle().setDisplay(Display.NONE);
            scroller.getWidgetContainerElement(scrollRight).getStyle().setDisplay(Display.NONE);
            scroller.setWidgetLeftRight(tabBar, 0, Unit.PX, 0, Unit.PX);
        }
    }
    
    protected class TabPopper implements CloseHandler<WindowInt>, BeforeSelectionHandler<Integer> {
        
        Browser browser;
        PickupDragController drag;
        SimpleDropController drop;
        
        public TabPopper(Browser browser) {
            this.browser = browser;
            drag = new TabDragger(browser.browser,false);
            drag.setBehaviorDragProxy(true);
            drag.setBehaviorDragStartSensitivity(10);
            
            for(int i = 0; i < getWidgetCount(); i++) {
                makeDraggable(getTabWidget(i));
            }
           
            popouts = new HashMap<Integer,Window>();
            
            drop = new TabDropper(browser.browser);
            drag.registerDropController(drop);
            
            addBeforeSelectionHandler(this);
        }
        
        public void makeDraggable(Widget widget) {
            drag.makeDraggable(widget);
        }
        
        protected class TabDragger extends PickupDragController {

            public TabDragger(AbsolutePanel boundary, boolean byProxy) {
                super(boundary,byProxy);
            }

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
        }

        protected class TabDropper extends SimpleDropController {

            public TabDropper(AbsolutePanel boundary) {
                super(boundary);
            }

            @Override
            public void onDrop(DragContext context) {
                super.onDrop(context);
                System.out.println("drop happening");
                if(context.desiredDraggableX > 0) {
                    doDrop(context.draggable,
                           context.desiredDraggableX,
                           context.desiredDraggableY);
                }

            }
            
            protected void doDrop(Widget tab, int x, int y) {
 
                int index = getTabWidgetIndex(tab);
                final Widget wid = getWidget(index);
                
                replaceWidgetWithBlank(index,tab);

                Window win = createWindow(((TabWidget)tab).getText(), wid);
                popouts.put(new Integer(index),win);
                browser.addWindow(win,"modules",x,y);
                
                setTabPoppedOut(index);
            }
            
            protected void replaceWidgetWithBlank(int index,Widget tab) {
                boolean showing = index == getSelectedIndex();

                getWidget(index).setVisible(true);
                remove(index);

                LayoutPanel holder = new LayoutPanel();
                holder.setStyleName(UIResources.INSTANCE.tabpanel().Popped());
                insert(holder,tab,index);
                
                if(showing)
                    selectTab(index);
            }
            
            protected Window createWindow(String name, Widget content) {
                final Window win = new Window(true);
                win.setName(name);
                win.setContentSize(getOffsetWidth(),getOffsetHeight());
                win.setContent(content);
                win.addCloseHandler(popper);
                return win;
            }
        }

        @Override
        public void onClose(CloseEvent<WindowInt> event) {
            boolean isVisible;
            Window win = (Window)event.getSource();
            Widget wid = win.getContent();
            final int index = getWidgetIndex(wid);
            
            if (!isAttached() || closing)
                return;
            
            isVisible = tabBar.getWidget(index).isVisible();

            Widget tab = getTabWidget(index);
            popouts.remove(index);
            remove(index);
            insert(wid, tab, index);
            if(isVisible)
                selectTab(index);
            else
                setTabVisible(index, false);
            setTabPoppedIn(index);
            
            resizeTab(index);
        }

        @Override
        public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
            if(popouts.keySet().contains(event.getItem()))
                event.cancel();
        }
    }


}
