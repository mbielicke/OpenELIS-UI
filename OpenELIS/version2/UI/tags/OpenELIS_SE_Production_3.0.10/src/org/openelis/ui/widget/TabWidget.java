package org.openelis.ui.widget;

import org.openelis.ui.resources.TabPanelCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TabWidget extends FocusPanel {
    
    Grid grid;
    AbsolutePanel icon;
    String text;
    boolean tabVisible = true;
    Label notification;
    
    TabPanelCSS css;
    
    @UiConstructor
    public TabWidget(String text) {
        this.text = text;
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
    
    public void setTabVisible(boolean tabVisible) {
        this.tabVisible = tabVisible;  
    }
    
    public boolean getTabVisible() {
        return tabVisible;
    }
    
    public void setIcon(String css) {
        icon.setStyleName(css);
    }
    
    public void setNotificaton(String text) {
        if(text == null || text.equals("")) {
            icon.removeStyleName(css.TabNotification());
            icon.clear();
        }else {
            icon.setStyleName(css.TabNotification());
            if(notification == null) {
                notification = new Label();
                notification.setStyleName(css.TabNotificationText());
            }
            notification.setValue(text);
            icon.add(notification);
        }
    }
    
    protected void setVertical() {
        Grid vp = new Grid(getText().length(),1);
        vp.setCellPadding(0);
        vp.setCellSpacing(0);
        //grid.getCellFormatter().setWordWrap(0, 0, true);
        String text = getText();
        //String vertText = "";
        int i = 0; 
        for(char ch : text.toCharArray()) {
            //vertText += ch +"\n";
            vp.setText(i, 0, String.valueOf(ch));
            vp.getCellFormatter().setHorizontalAlignment(i, 0, HasAlignment.ALIGN_CENTER);
            i++;
        }
        //setText(vertText);
        grid.setWidget(0, 0, vp);
        DOM.setStyleAttribute(grid.getCellFormatter().getElement(0, 0),"lineHeight","10px");
    }

}
