package org.openelis.ui.widget;

import org.openelis.ui.resources.TabPanelCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;

public class TabWidget extends FocusPanel {
    
    Grid grid;
    AbsolutePanel icon;
    
    TabPanelCSS css;
    
    @UiConstructor
    public TabWidget(String text) {
        grid = new Grid(1,2);
        icon = new AbsolutePanel();
        grid.setText(0, 0, text);
        grid.setWidget(0, 1, icon);
        grid.getCellFormatter().setWordWrap(0, 0, false);
        grid.setHeight("100%");
        
        setWidget(grid);
        
        css = UIResources.INSTANCE.tabpanel();
        css.ensureInjected();
    }
    
    public void setText(String text) {
        grid.setText(0,0,text);
    }
    
    public String getText() {
        return grid.getText(0, 0);
    }
    
    public void setTabInError() {
        icon.setStyleName(css.TabError());
    }

    public void setTabHasData() {
        icon.setStyleName(css.TabData());
    }

    public void removeTabInError() {
        icon.removeStyleName(css.TabError());
    }

    public void removeTabHasData() {
        icon.removeStyleName(css.TabData());
    }
    
    public void setPoppedOut() {
        getParent().addStyleName(css.Popped());
    }
    
    public void setPoppedIn() {
        getParent().removeStyleName(css.Popped());
    }

}
