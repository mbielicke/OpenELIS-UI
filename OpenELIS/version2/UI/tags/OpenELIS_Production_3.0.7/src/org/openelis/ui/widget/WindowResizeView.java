package org.openelis.ui.widget;

import org.openelis.ui.widget.Window.Caption;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class WindowResizeView extends ResizeComposite implements WindowViewInt {
    
    @UiTemplate("Window.ui.xml")
    interface WindowUiBinder extends UiBinder<Widget, WindowResizeView> {
    };
    
    public static final WindowUiBinder uiBinder = GWT.create(WindowUiBinder.class);
    
    @UiField
    protected Caption                  cap;
    @UiField
    protected DockLayoutPanel          outer;
    @UiField
    protected LayoutPanel              top, bottom;
    @UiField
    protected Label                    status;
    @UiField
    protected FocusPanel               statusImg,close, collapse, maximize,resizer,
                                       north,south,east,west;
    @UiField
    protected LayoutPanel              body,inner;
    @UiField
    protected Grid                     statusContainer;

    public WindowResizeView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public Caption getCap() {
        return cap;
    }

    public Widget getOuter() {
        return outer;
    }

    public LayoutPanel getTop() {
        return top;
    }

    public LayoutPanel getBottom() {
        return bottom;
    }

    public Label getStatus() {
        return status;
    }

    public FocusPanel getStatusImg() {
        return statusImg;
    }

    public FocusPanel getClose() {
        return close;
    }

    public FocusPanel getCollapse() {
        return collapse;
    }

    public FocusPanel getMaximize() {
        return maximize;
    }

    public FocusPanel getResizer() {
        return resizer;
    }

    public FocusPanel getNorth() {
        return north;
    }

    public FocusPanel getSouth() {
        return south;
    }

    public FocusPanel getEast() {
        return east;
    }

    public FocusPanel getWest() {
        return west;
    }

    public LayoutPanel getBody() {
        return body;
    }
    
    public LayoutPanel getInner() {
        return inner;
    }

    public Grid getStatusContainer() {
        return statusContainer;
    }
    
}
