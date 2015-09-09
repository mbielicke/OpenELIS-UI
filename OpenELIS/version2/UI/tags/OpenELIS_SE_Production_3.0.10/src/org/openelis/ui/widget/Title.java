package org.openelis.ui.widget;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Title extends ResizeComposite implements HasWidgets {
    
    @UiTemplate("TitledPanel.ui.xml")
    interface TitledPanelUiBinder extends UiBinder<Widget,Title>{};
    public static final TitledPanelUiBinder uiBinder = GWT.create(TitledPanelUiBinder.class);
    
    @UiField
    Element legend;
    
    @UiField
    LayoutPanel content;
    
    public Title() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    public void setTitle(String title) {
        legend.setInnerText(title);
    }

    @Override
    public void add(Widget w) {
        content.add(w);
        content.setWidgetTopBottom(w, 0, Unit.PX, 0, Unit.PX);
        content.setWidgetLeftRight(w, 0, Unit.PX, 0, Unit.PX);
    }
    
    @Override
    public void onResize() {
        Element parent = (Element) (getParent() instanceof LayoutPanel ? ((LayoutPanel)getParent()).getWidgetContainerElement(this)
                                                                       : getParent().getElement());
        
        int width = parent.getOffsetWidth() -10;
        int height = parent.getOffsetHeight() -25;
        
        content.setSize(width+"px", height+"px");
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Iterator<Widget> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(Widget w) {
        // TODO Auto-generated method stub
        return false;
    }
    
    
    
    

}
