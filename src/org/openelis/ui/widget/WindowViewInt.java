package org.openelis.ui.widget;

import org.openelis.ui.widget.Window.Caption;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public interface WindowViewInt extends IsWidget {

    public Caption getCap();

    public Widget getOuter();

    public Widget getTop();

    public Widget getBottom();

    public Label getStatus();

    public FocusPanel getStatusImg();

    public FocusPanel getClose();

    public FocusPanel getCollapse();

    public FocusPanel getMaximize();

    public FocusPanel getResizer();

    public FocusPanel getNorth();

    public FocusPanel getSouth();

    public Widget getEast();

    public Widget getWest();

    public Widget getBody();

    public Grid getStatusContainer();

}