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
import java.util.HashMap;

import org.openelis.ui.common.Warning;
import org.openelis.ui.resources.DialogCSS;
import org.openelis.ui.resources.ToolTipCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.table.event.CellMouseOverEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class draws and places a Ballon next to widgets for showing Tool Tips
 * and Exceptions
 * 
 */
public class Balloon extends Composite {

    @UiTemplate("Balloon.ui.xml")
    interface BalloonUiBinder extends UiBinder<AbsolutePanel, Balloon> {
    }

    public static final BalloonUiBinder uiBinder = GWT.create(BalloonUiBinder.class);

    @UiField
    AbsolutePanel                                                     tip, nub, content;

    public enum Placement {
        TOP, BOTTOM, RIGHT, LEFT, MOUSE
    }

    protected Placement                                                  placement      = Placement.TOP;

    protected ToolTipCSS                                                 css            = UIResources.INSTANCE.toolTip();

    protected static final Balloon                                             balloon;
    protected static final Timer                                               tipTimer, timer;
    protected static final PopupPanel                                          pop;
    protected static HasBalloon                                          targetWidget;
    protected static Element                                             targetElement;
    protected static TipHandler                                          tipHandler;
    protected static final ExceptionHandlers                                   exceptHandler;
    protected static int                                                 x, y;
    protected static final VerticalPanel                                       exceptionPanel = new VerticalPanel();
    protected static final DialogCSS                                     dialogCss      = UIResources.INSTANCE.dialog();
    protected static final HashMap<HasExceptions, HandlerRegistration>   overHandlers;
    protected static final HashMap<HasExceptions, HandlerRegistration>   outHandlers;

    /**
     * Set up static objects need for Balloon
     */
    static {
        dialogCss.ensureInjected();
        tipTimer = new Timer() {
            @Override
            public void run() {
                
                Object tip;
                final HasBalloon hasTip = (HasBalloon)targetWidget;
                final Options options = hasTip.getBalloonOptions();

                if (options == null)
                    return;

                if (hasTip instanceof HasExceptions &&
                    ( ((HasExceptions)hasTip).getEndUserExceptions() != null || ((HasExceptions)hasTip).getValidateExceptions() != null)) {
                    return;
                } else {
                    
                    if (options.getTipProvider() != null) 
                        tip = options.getTipProvider().getTip(hasTip);
                    else if(options.getWidget() != null)
                        tip = options.getWidget();
                    else
                        tip = options.getTip();
                    
                    if(tip instanceof String)
                        balloon.setText((String)tip);
                    else
                        balloon.setContent((Widget)tip);
                    
                    balloon.setPlacement(options.getPlacement());
                }

                showBalloon(options);
            }

        };

        timer = new Timer() {
            @Override
            public void run() {
                pop.hide();
            }
        };

        balloon = new Balloon();

        pop = new PopupPanel();

        pop.setWidget(balloon);

        tipHandler = new TipHandler();

        exceptHandler = new ExceptionHandlers();

        overHandlers = new HashMap<HasExceptions, HandlerRegistration>();
        outHandlers = new HashMap<HasExceptions, HandlerRegistration>();
    }
    
    /**
     * Constructor set to private so only one can be created
     */
    private Balloon() {
        initWidget(uiBinder.createAndBindUi(this));
        css.ensureInjected();
        nub.getElement().getStyle().setPosition(Position.ABSOLUTE);
    }

    /**
     * 
     */
    private void setPlacement(final Placement placement) {

        this.placement = placement;

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                switch (placement) {
                    case TOP:
                    case MOUSE:
                        tip.setStyleName(css.ToolTip());
                        tip.addStyleName(css.TipBottom());
                        nub.setStyleName(css.Nub());
                        nub.addStyleName(css.NubBottom());
                        break;
                    case BOTTOM:
                        tip.setStyleName(css.ToolTip());
                        tip.addStyleName(css.TipTop());
                        nub.setStyleName(css.Nub());
                        nub.addStyleName(css.NubTop());
                        break;
                    case LEFT:
                        tip.setStyleName(css.ToolTip());
                        tip.addStyleName(css.TipRight());
                        nub.setStyleName(css.Nub());
                        nub.addStyleName(css.NubRight());
                        break;
                    case RIGHT:
                        tip.setStyleName(css.ToolTip());
                        tip.addStyleName(css.TipLeft());
                        nub.setStyleName(css.Nub());
                        nub.addStyleName(css.NubLeft());
                        break;
                }

            }
        });

    }

    private void setText(String text) {
        this.content.clear();
        content.getElement().setInnerText(text);
    }

    private void setContent(Widget content) {
        this.content.clear();
        this.content.getElement().setInnerText("");
        this.content.add(content);
    }

    public static void show(final HasBalloon widget, int ex, int ey) {
        show(widget,((Widget)widget).getElement(), ex, ey);
    }
    
    public static void show(final HasBalloon widget, Element element, int ex, int ey) {
        targetWidget = (HasBalloon)widget;
        targetElement = element;
        x = ex;
        y = ey;
        tipTimer.schedule(0);
    }

    public static void show(final HasBalloon widget, MouseEvent<?> mouseEvent) {
        setTarget(widget, mouseEvent);
        tipTimer.schedule( ((HasBalloon)targetWidget).getBalloonOptions().delayShow);
    }
    
    public static void show(final HasBalloon widget, Element element, MouseEvent<?> mouseEvent) {
        setTarget(widget,element,mouseEvent);
    }

    private static void setTarget(final HasBalloon widget, MouseEvent<?> mouseEvent) {
        setTarget(widget, ((Widget)widget).getElement(), mouseEvent);
    }
    
    private static void setTarget(final HasBalloon widget, Element element, MouseEvent<?> mouseEvent) {
        targetWidget = (HasBalloon)widget;
        targetElement = element;
        if (mouseEvent instanceof CellMouseOverEvent) {
            x = ((CellMouseOverEvent)mouseEvent).getX();
            y = ((CellMouseOverEvent)mouseEvent).getY();
        } else {
            x = mouseEvent.getClientX();
            y = mouseEvent.getClientY();
        }

    }

    private static void showBalloon(Balloon.Options options) {
        switch (balloon.placement) {
            case TOP:
            case BOTTOM:
            case MOUSE:
                balloon.nub.getElement().getStyle().setLeft(options.offset, Unit.PCT);
                balloon.nub.getElement().getStyle().clearTop();
                break;
            case LEFT:
            case RIGHT:
                balloon.nub.getElement().getStyle().setTop(options.offset, Unit.PCT);
                balloon.nub.getElement().getStyle().clearLeft();
                break;
        }

        balloon.nub.getElement().getStyle().setPosition(Position.ABSOLUTE);

        pop.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                int top = -1, left = -1;

                switch (balloon.placement) {
                    case TOP:
                        top = targetElement.getAbsoluteTop() - offsetHeight;
                        left = targetElement.getAbsoluteLeft();
                        break;
                    case BOTTOM:
                        top = targetElement.getAbsoluteTop() + targetElement.getOffsetHeight();
                        left = targetElement.getAbsoluteLeft();
                        break;
                    case RIGHT:
                        top = targetElement.getAbsoluteTop();
                        left = targetElement.getAbsoluteLeft() + targetElement.getOffsetWidth();
                        break;
                    case LEFT:
                        top = targetElement.getAbsoluteTop();
                        left = targetElement.getAbsoluteLeft() - offsetWidth;
                        break;
                    case MOUSE:
                        top = y - offsetHeight;
                        left = x - (offsetWidth / 2);
                }

                pop.setPopupPosition(left, top);
            }
        });

        timer.schedule(options.delayHide);
    }

    public static void hide() {
        tipTimer.cancel();
        timer.cancel();
        pop.hide();
    }

    public static TipHandler getTipHandler() {
        return tipHandler;
    }


    /**
     * Clears Exception Styles form widget
     * 
     * @param widget
     */
    private static void clearExceptionStyle(HasExceptions widget) {
        widget.removeExceptionStyle();
    }

    /**
     * Checks the exceptions on the widget and sets the appropriate style
     * 
     * @param widget
     */
    public static boolean isWarning(HasExceptions widget) {
        return isWarning(widget.getEndUserExceptions(),widget.getValidateExceptions());
    }
    
    public static boolean isWarning(ArrayList<Exception> endUser, ArrayList<Exception> valid) {
        ArrayList<Exception> exceptions = null;

        for (int i = 0; i < 2; i++ ) {
            switch (i) {
            /*
             * First iteration check EndUserException if no EndUserExceptions do
             * i++ and fall through to check validatExceptions
             */
                case 0:
                    exceptions = endUser;
                    if (exceptions == null)
                        continue;
                    break;
                /*
                 * If no validation exceptions continue out of loop
                 */
                case 1:
                    exceptions = valid;
                    if (exceptions == null)
                        continue;
            }

            for (Exception exception : exceptions) {
                if ( ! (exception instanceof Warning))
                    return false;
            }
        }

        return true;
    }

    /**
     * Adds and removes Mouse handlers for displaying Exceptions as needed
     */
    public static void checkExceptionHandlers(HasExceptions widget) {
        clearExceptionStyle(widget);
        if (widget.getEndUserExceptions() != null || widget.getValidateExceptions() != null) {
            if ( !overHandlers.containsKey(widget)) {
                overHandlers.put(widget, widget.addMouseOverHandler(exceptHandler));
                outHandlers.put(widget, widget.addMouseOutHandler(exceptHandler));
            }
            widget.addExceptionStyle();
        } else {
            if (overHandlers.containsKey(widget)) {
                overHandlers.remove(widget).removeHandler();
                outHandlers.remove(widget).removeHandler();
            }
        }

    }

    public static void clearExceptionHandlers(HasExceptions widget) {
        if (overHandlers.containsKey(widget)) {
            overHandlers.remove(widget).removeHandler();
            outHandlers.remove(widget).removeHandler();
        }
    }

    public static void drawExceptions(HasExceptions hasExceptions, final int x, final int y) {
        drawExceptions(hasExceptions.getEndUserExceptions(),
                       hasExceptions.getValidateExceptions(),
                       x,
                       y);
    }

    public static void drawExceptions(ArrayList<Exception> endUser, ArrayList<Exception> valid, int ex, int ey) {
        drawExceptions(endUser,valid,null,ex,ey);
    }
    public static void drawExceptions(ArrayList<Exception> endUser, ArrayList<Exception> valid, Element element,
                                      final int ex, final int ey) {
        ArrayList<Exception> exceptions = null;
        Grid grid;

        // Clear panel
        exceptionPanel.clear();
        grid = new Grid(0, 2);
        exceptionPanel.add(grid);

        x = ex;
        y = ey;

        // Get out if widget has no exceptions to display;
        if (endUser == null && valid == null)
            return;

        for (int i = 0; i < 2; i++ ) {
            switch (i) {
            /*
             * First iteration check EndUserException if no EndUserExceptions do
             * i++ and fall through to check validatExceptions
             */
                case 0:
                    exceptions = endUser;
                    if (exceptions == null)
                        continue;
                    break;
                /*
                 * If no validation exceptions continue out of loop
                 */
                case 1:
                    exceptions = valid;
                    if (exceptions == null)
                        continue;
            }

            for (Exception exception : exceptions) {
                grid.insertRow(0);
                if (exception instanceof Warning) {
                    grid.getCellFormatter().setStyleName(0, 0, dialogCss.WarnIcon());
                    grid.getCellFormatter().setStyleName(0, 1, dialogCss.warnPopupLabel());
                } else {
                    grid.getCellFormatter().setStyleName(0, 0, dialogCss.ErrorIcon());
                    grid.getCellFormatter().setStyleName(0, 1, dialogCss.errorPopupLabel());
                }
                grid.setText(0, 1, exception.getMessage());
            }
        }

        balloon.setContent(exceptionPanel);
        if(element == null) 
            balloon.setPlacement(Balloon.Placement.MOUSE);
        else {
            targetElement = element;
            balloon.setPlacement(Balloon.Placement.TOP);
        }
        
        Options options = new Options();
        options.setDelayHide(5000);
        showBalloon(options);
        
    }
    
    public static class TipHandler implements MouseOverHandler, MouseOutHandler {

        @Override
        public void onMouseOut(MouseOutEvent event) {
            Balloon.hide();
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            Balloon.show((HasBalloon)event.getSource(), event);
        }

    }

    protected static class ExceptionHandlers implements MouseOverHandler, MouseOutHandler {
        /**
         * Handler to hide PopupPanel when user mouses off widget
         */
        public void onMouseOut(MouseOutEvent event) {
            Balloon.hide();
        }

        /**
         * Handler to show Exceptions when a user mouses over a widget
         */
        public void onMouseOver(MouseOverEvent event) {
            HasExceptions source;

            source = (HasExceptions)event.getSource();
            
            setTarget((HasBalloon)source,event);
            
            if (source.getEndUserExceptions() != null || source.getValidateExceptions() != null) {
                drawExceptions(source, x, y);

            }
        }
    }

    public static class Options {
        private Placement               placement = Placement.TOP;
        private String                  tip       = "";
        private Widget                  widget;
        private int                     offset    = 50;
        private int                     delayShow = 500;
        private int                     delayHide = 5000;
        private TipProvider             tipProvider;
        private HandlerRegistration     overHandler, outHandler;

        public Options() {

        }

        public Options(HasBalloon target) {
            setTarget(target);
        }

        public Placement getPlacement() {
            return placement;
        }

        public void setPlacement(Placement placement) {
            this.placement = placement;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getDelayShow() {
            return delayShow;
        }

        public void setDelayShow(int delayShow) {
            this.delayShow = delayShow;
        }

        public int getDelayHide() {
            return delayHide;
        }

        public void setDelayHide(int delayHide) {
            this.delayHide = delayHide;
        }

        public void setTipProvider(TipProvider tipProvider) {
            this.tipProvider = tipProvider;
        }

        public TipProvider getTipProvider() {
            return tipProvider;
        }
        
        public void setWidget(Widget widget) {
            this.widget = widget;
        }
        
        public Widget getWidget() {
            return widget;
        }

        public void setTarget(HasBalloon target) {
            if (outHandler != null) {
                outHandler.removeHandler();
                overHandler.removeHandler();
            }

            outHandler = target.addMouseOutHandler(tipHandler);
            overHandler = target.addMouseOverHandler(tipHandler);
        }

        public void destroy() {
            outHandler.removeHandler();
            overHandler.removeHandler();
        }
    }

    public interface TipProvider<T> {
        public T getTip(HasBalloon target);
    }

}
