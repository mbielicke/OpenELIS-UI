package org.openelis.ui.widget;

import org.openelis.ui.widget.Window.Caption;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WindowView extends Composite implements WindowViewInt{
    
    @UiTemplate("LegacyWindow.ui.xml")
    interface WindowUiBinder extends UiBinder<Widget,WindowView>{};
    
    private static final WindowUiBinder uiBinder = GWT.create(WindowUiBinder.class);
    
    @UiField
    protected Caption                  cap;
    @UiField
    protected VerticalPanel            outer;
    @UiField
    protected Grid                     top;
    @UiField
    protected Label                    status;
    @UiField
    protected FocusPanel               statusImg,close, collapse;
    @UiField
    protected AbsolutePanel            body,bottom;
    @UiField
    protected Grid                     statusContainer;

    
    public WindowView() {
        initWidget(uiBinder.createAndBindUi(this));
        top.getCellFormatter().setWidth(0, 0, "100%");
    }

    @Override
    public Caption getCap() {
        return cap;
    }

    @Override
    public Widget getOuter() {
        return outer;
    }

    @Override
    public Grid getTop() {
        return top;
    }

    @Override
    public Widget getBottom() {
        return bottom;
    }

    @Override
    public Label getStatus() {
        return status;
    }

    @Override
    public FocusPanel getStatusImg() {
        return statusImg;
    }

    @Override
    public FocusPanel getClose() {
        return close;
    }

    @Override
    public FocusPanel getCollapse() {
        return collapse;
    }

    @Override
    public FocusPanel getMaximize() {
        return null;
    }

    @Override
    public FocusPanel getResizer() {
        return null;
    }

    @Override
    public FocusPanel getNorth() {
        return null;
    }

    @Override
    public FocusPanel getSouth() {
        return null;
    }

    @Override
    public AbsolutePanel getEast() {
        return null;
    }

    @Override
    public AbsolutePanel getWest() {
        return null;
    }

    @Override
    public AbsolutePanel getBody() {
        return body;
    }

    @Override
    public Grid getStatusContainer() {
        return statusContainer;
    }

}
