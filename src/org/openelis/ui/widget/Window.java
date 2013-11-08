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
package org.openelis.ui.widget;

import java.util.ArrayList;

import org.openelis.ui.common.Warning;
import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.event.BeforeCloseHandler;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.resources.WindowCSS;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
//import org.openelis.ui.screen.Screen;

public class Window extends FocusPanel implements WindowInt, RequiresResize, ProvidesResize {
    /*
    @UiTemplate("Window.ui.xml")
    interface WindowUiBinder extends UiBinder<Widget, Window> {
    };
    
    public static final WindowUiBinder uiBinder = GWT.create(WindowUiBinder.class);
    
    
    @UiField
    protected Caption                  cap;
    @UiField
    protected DockLayoutPanel          outer;
    @UiField
    protected LayoutPanel              top, bottom;
    */
    protected VerticalPanel            messagePanel;
    protected PopupPanel               pop;
    /*
    @UiField
    protected Label                    status;
    @UiField
    protected FocusPanel               statusImg,close, collapse, maximize,resizer,
                                       north,south,east,west;
    @UiField
    protected LayoutPanel              body;
    @UiField
    protected Grid                     statusContainer;
    */
    protected AbsolutePanel            glass,parent;
    protected HTML                     label;

    protected PickupDragController     dragController;
    protected HandlerRegistration      moveHandler, mouseUpHandler;
    protected int                      offX, offY;
    
    protected FocusPanel               resizeWindow;

    protected int                      userWidth, userHeight,userLeft,userTop;
    protected boolean                  maximized,resize;
    
    protected MouseDownHandler         mouseDownHandler;
    protected Widget                   dragSource;
    
    /**
     * The Screen or panel that is displayed by this window.
     */
    protected Widget                   content;

    protected WindowCSS                css;

    protected Window                   source   = this;
    
    protected WindowViewInt            view;

    public Window() {
        this(true);
    }
    
    public Window(boolean resize) {
        this.resize = resize;
        
        if(resize)
            view = new WindowResizeView();
        else
            view = new WindowView();

        setWidget(view);


        view.getCap().addMouseDownHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                setFocus(true);
            }
        });

        label = new HTML();
        label.setText(" ");

        view.getCap().add(label);

        view.getClose().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                close();
            }
        });

        view.getCollapse().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                
                if(view.getBody().isVisible()) {
                    userHeight = getOffsetHeight();// + (css.borderWidth());
                    view.getOuter().setHeight("25px");
                }else
                    view.getOuter().setHeight((userHeight-2)+"px");
                
                if(source.resize) {
                    boolean visible = view.getBody().isVisible();
                    UIObject.setVisible(((LayoutPanel)view.getInner()).getWidgetContainerElement(view.getBody()),!visible);
                    UIObject.setVisible(((LayoutPanel)view.getInner()).getWidgetContainerElement(view.getBottom()), !visible);
                }
                view.getBody().setVisible(!view.getBody().isVisible());
                view.getBottom().setVisible(!view.getBottom().isVisible());
                
            }
        });
        
        if(resize) {
        view.getMaximize().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                
                if(!view.getBody().isVisible()) {
                    view.getOuter().setHeight((userHeight-2)+"px");
                    UIObject.setVisible(((LayoutPanel)view.getInner()).getWidgetContainerElement(view.getBody()), true);
                    UIObject.setVisible(((LayoutPanel)view.getInner()).getWidgetContainerElement(view.getBottom()), true);
                    view.getBody().setVisible(true);
                    view.getBottom().setVisible(true);
                }
                
                if(!maximized) {
                    userWidth = getOffsetWidth();
                    userHeight = getOffsetHeight();
                    userLeft = ((AbsolutePanel)getParent()).getWidgetLeft(source);
                    userTop = ((AbsolutePanel)getParent()).getWidgetTop(source);
                    setSize(getParent().getOffsetWidth()-(css.borderWidth()*2)+"px",getParent().getOffsetHeight()-(css.borderWidth()*2)+"px");
                    ((AbsolutePanel)getParent()).setWidgetPosition(source, 0, 0);
                    maximized = true;
                    view.getMaximize().setStyleName(css.RestoreButton());
                }else {
                    ((AbsolutePanel)getParent()).setWidgetPosition(source, userLeft, userTop);
                    setSize(userWidth+"px",userHeight+"px");
                    maximized = false;
                    view.getMaximize().setStyleName(css.MaximizeButton());
                }
                ((LayoutPanel)view.getBody()).onResize();
       
            }
        });
        }

        view.getStatusImg().addMouseOverHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
                if (messagePanel == null) {
                    return;
                }
                if (pop == null) {
                    pop = new PopupPanel();
                }

                Window errorWin = new Window(false);
                errorWin.setCSS(UIResources.INSTANCE.dialog());
                errorWin.setContent(messagePanel);

                pop.setWidget(errorWin);
                final int left = ((Widget)event.getSource()).getAbsoluteLeft() + 16;
                final int top = ((Widget)event.getSource()).getAbsoluteTop();
                pop.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

                    public void setPosition(int offsetWidth, int offsetHeight) {
                        pop.setPopupPosition(left, top - offsetHeight);
                        pop.show();
                    }

                });
                pop.show();
            }
        });

        view.getStatusImg().addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                if (pop != null) {
                    pop.hide();
                }
            }
        });

        view.getStatus().setText(Messages.get().window_loading());
        
        view.getStatusContainer().getCellFormatter().setWidth(0,1,"100%");

        /* Sink events and add resize Handler */
        view.getOuter().sinkEvents(Event.ONCLICK);
        view.getOuter().setWidth("auto");
        
        /* Apply style to the window elements */
        setCSS(UIResources.INSTANCE.window());
        
        if(resize) {
        resizeWindow = new FocusPanel();
        resizeWindow.setStyleName(css.ResizeWindow());
        DOM.setStyleAttribute(resizeWindow.getElement(), "zIndex", "1000");
        
        mouseDownHandler = new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                parent = (AbsolutePanel)getParent();
                resizeWindow.setWidth(getOffsetWidth() + "px");
                resizeWindow.setHeight(getOffsetHeight() + "px");
                parent.add(resizeWindow, parent.getWidgetLeft(source), parent.getWidgetTop(source));


                parent.addStyleName(css.NoSelect());
                view.getOuter().addStyleName(css.NoSelect());
                DOM.setCapture(resizeWindow.getElement());
                dragSource = (Widget)event.getSource();
                if(dragSource == view.getNorth() || dragSource == view.getSouth())
                    DOM.setStyleAttribute(resizeWindow.getElement(), "cursor", "ns-resize");
                else if (dragSource == view.getEast() || dragSource == view.getWest())
                    DOM.setStyleAttribute(resizeWindow.getElement(), "cursor", "ew-resize");
                else
                    DOM.setStyleAttribute(resizeWindow.getElement(), "cursor", "se-resize");
              }
        };
        
        view.getResizer().addMouseDownHandler(mouseDownHandler);
        
        view.getNorth().addMouseDownHandler(mouseDownHandler);
        
        ((FocusPanel)view.getEast()).addMouseDownHandler(mouseDownHandler);
        
        ((FocusPanel)view.getWest()).addMouseDownHandler(mouseDownHandler);
        
        view.getSouth().addMouseDownHandler(mouseDownHandler);
        
        resizeWindow.addMouseMoveHandler(new MouseMoveHandler() {
            public void onMouseMove(MouseMoveEvent event) {
                int width,height,x,y;
                
                if(dragSource == view.getResizer() || dragSource == view.getEast())
                    width = event.getRelativeX(parent.getElement()) - parent.getWidgetLeft(source);
                else if(dragSource == view.getWest())
                    width = (parent.getWidgetLeft(source) - event.getRelativeX(parent.getElement())) + view.getOuter().getOffsetWidth();
                else
                    width = getOffsetWidth();
                
                if(dragSource == view.getResizer() || dragSource == view.getSouth())
                    height = event.getRelativeY(parent.getElement()) - parent.getWidgetTop(source);
                else if(dragSource == view.getNorth()){
                    height = (parent.getWidgetTop(source) - event.getRelativeY(parent.getElement())) + view.getOuter().getOffsetHeight();
                }else
                    height = getOffsetHeight();
                
                if(dragSource == view.getNorth())
                    y = event.getRelativeY(parent.getElement());
                else
                    y = parent.getWidgetTop(source);
                
                if(dragSource == view.getWest())
                    x = event.getRelativeX(parent.getElement());
                else
                    x = parent.getWidgetLeft(source);
                
                parent.setWidgetPosition(resizeWindow, x, y);
                resizeWindow.setSize(width+"px", height+"px");
            }
        });

        resizeWindow.addMouseUpHandler(new MouseUpHandler() {

            @Override
            public void onMouseUp(MouseUpEvent event) {
                    parent.removeStyleName(css.NoSelect());
                    view.getOuter().removeStyleName(css.NoSelect());
                    DOM.releaseCapture(resizeWindow.getElement());
                    setSize(resizeWindow.getOffsetWidth() - (CSSUtils.getAddedBorderWidth(resizeWindow.getElement()) * 2 ) + "px",
                            resizeWindow.getOffsetHeight() - (CSSUtils.getAddedBorderHeight(resizeWindow.getElement()) * 2 ) + "px");
                    parent.setWidgetPosition(source,parent.getWidgetLeft(resizeWindow) ,parent.getWidgetTop(resizeWindow));
                    parent.remove(resizeWindow);
                    onResize();
            }
       
        });
        }

       
    }

    /**
     * Sets the Content of the Window to be displayed. If content is a
     * ScreenForm, the message widget is linked to the form.
     * 
     * @param content
     */
    public void setContent(final Widget content) {
        source.content = content;
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                ((Panel)view.getBody()).clear();
                ((Panel)view.getBody()).add(content);
                setKeyHandling();
                setDone(Messages.get().window_done());
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() { 
                    @Override
                    public void execute() {
                       onResize();
                    }
                });
                
            }
        });

    }

    public Widget getContent() {
        return content;
    }
    
    public void setContentSize(final int width, final int height) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() { 
            @Override
            public void execute() {
                int wth = width,hgt = height;
                if(resize) {
                    hgt += view.getCap().getOffsetHeight();
                    hgt += view.getBottom().getOffsetHeight();
                    hgt += CSSUtils.getAddedBorderHeight(resizeWindow.getElement()) * 2;
                    wth += CSSUtils.getAddedBorderWidth(resizeWindow.getElement()) * 2;
                    setSize(wth+"px",hgt+"px");
                } else {
                    content.setSize(width+"px", height+"px");
                }
            }
        });

    }
    
    public void setKeyHandling() {
        /**
         * This handler is added to forward the key press event if received by
         * the window down to the screen.
         */
        addDomHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                if (content instanceof KeyDownHandler)
                    KeyDownEvent.fireNativeEvent(event.getNativeEvent(), ((HasHandlers)content));
            }
        }, KeyDownEvent.getType());
    }

    public void setName(String name) {
        label.setText(name);
    }

    public void close() {
        if (getHandlerCount(BeforeCloseEvent.getType()) > 0) {
            BeforeCloseEvent<WindowInt> event = BeforeCloseEvent.fire(this, this);
            if (event != null && event.isCancelled())
                return;
        }
        removeFromParent();

        destroy();

        CloseEvent.fire(this, this);
    }

    public void destroy() {
        //view.getCap() = null;
        //outer = null;
        //bottom = null;
        //close = null;
        //content = null;
    }

    public void setMessagePopup(ArrayList<Exception> exceptions, String style) {
        view.getStatusImg().setStyleName(style);
        view.getStatusImg().sinkEvents(Event.MOUSEEVENTS);
        messagePanel = new VerticalPanel();
        for (Exception exception : exceptions) {
            HorizontalPanel hp = new HorizontalPanel();
            if (exception instanceof Warning) {
                hp.add(new Image(UIResources.INSTANCE.warn()));
                hp.setStyleName(css.warnPopupLabel());
            } else {
                hp.add(new Image(UIResources.INSTANCE.error()));
                hp.setStyleName(css.errorPopupLabel());
                style = css.InputError();
            }
            hp.add(new Label(exception.getMessage()));
            messagePanel.add(hp);
        }
    }

    public void clearMessagePopup(String style) {
        view.getStatusImg().setStyleName(style);
        view.getStatusImg().unsinkEvents(Event.MOUSEEVENTS);
    }

    public void setStatus(String text, String style) {
        view.getStatus().setText(text);
        view.getStatusImg().setStyleName(style);
        unlockWindow();
    }

    public void lockWindow() {
        if (glass == null) {
            glass = new AbsolutePanel();
            glass.setStyleName(css.GlassPanel());
            glass.setHeight(content.getOffsetHeight() + "px");
            glass.setWidth(content.getOffsetWidth() + "px");
            RootPanel.get().add(glass, content.getAbsoluteLeft(), content.getAbsoluteTop());
        }
    }

    public void unlockWindow() {
        if (glass != null) {
            glass.removeFromParent();
            glass = null;
        }
    }

    public void setBusy() {
        setStatus("", css.spinnerIcon());
        lockWindow();

    }

    public void setBusy(String message) {
        setStatus(message, css.spinnerIcon());
        lockWindow();
    }

    public void clearStatus() {
        setStatus("", "");
        unlockWindow();

    }

    public void setDone(String message) {
        setStatus(message, "");
        unlockWindow();
    }

    public void setError(String message) {
        clearMessagePopup(message);
        setStatus(message, css.ErrorPanel());
        unlockWindow();
    }

    public void positionGlass() {
        if (glass != null) {
            unlockWindow();
            lockWindow();
        }
    }

    public void setCSS(WindowCSS css) {
        this.css = css;
        css.ensureInjected();
        view.getTop().setStyleName(css.top());
        view.getCap().setStyleName(css.Caption());
        label.setStyleName(css.ScreenWindowLabel());
        view.getClose().setStyleName(css.CloseButton());
        view.getCollapse().setStyleName(css.MinimizeButton());
        if(resize)
            view.getMaximize().setStyleName(css.MaximizeButton());
        view.getBottom().setStyleName(css.StatusBar());
        view.getStatus().setStyleName(css.ScreenWindowLabel());
        view.getBody().setStyleName(css.WindowBody());
        if(resize)
            view.getOuter().setStyleName(css.WindowPanel());
        else
            view.getOuter().setStyleName(css.LegacyWindowPanel());
        if(resize) {
            view.getResizer().setStyleName(css.Resizer());
            view.getNorth().setStyleName(css.North());
            view.getEast().setStyleName(css.East());
            view.getWest().setStyleName(css.West());
            view.getSouth().setStyleName(css.South());
        }
    }

    public HandlerRegistration addCloseHandler(CloseHandler<WindowInt> handler) {
        return addHandler(handler, CloseEvent.getType());
    }

    public HandlerRegistration addBeforeClosedHandler(BeforeCloseHandler<WindowInt> handler) {
        return addHandler(handler, BeforeCloseEvent.getType());
    }


    @Override
    public void setWidth(String width) {
        view.getOuter().setWidth(width);
    }

    @Override
    public void setHeight(String height) {
        view.getOuter().setHeight(height);
    }

    public void makeDragable(DragController controller) {
        controller.makeDraggable(this, view.getCap());
    }

    /**
     * Inner class used to create the Draggable Caption portion of the Window.
     * 
     * @author tschmidt
     * 
     */
    protected static class Caption extends AbsolutePanel implements HasAllMouseHandlers {

        public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
            return addDomHandler(handler, MouseDownEvent.getType());
        }

        public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
            return addDomHandler(handler, MouseUpEvent.getType());
        }

        public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
            return addDomHandler(handler, MouseOutEvent.getType());
        }

        public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
            return addDomHandler(handler, MouseOverEvent.getType());
        }

        public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
            return addDomHandler(handler, MouseMoveEvent.getType());
        }

        public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
            return addDomHandler(handler, MouseWheelEvent.getType());
        }
    }

    @Override
    public void onResize() {
        if(resize) {
            if(maximized)
                setSize(getParent().getOffsetWidth()-(css.borderWidth()*2)+"px",getParent().getOffsetHeight()-(css.borderWidth()*2)+"px");
            ((LayoutPanel)view.getBody()).onResize();
        }
    }


}
