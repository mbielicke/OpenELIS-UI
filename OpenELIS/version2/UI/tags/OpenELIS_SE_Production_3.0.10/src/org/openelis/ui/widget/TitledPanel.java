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

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LegendElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that wraps its contents in a border with a title that appears in the
 * upper left corner of the border. This is an implementation of the fieldset
 * HTML element.
 */
public class TitledPanel extends ResizeComposite implements HasWidgets {
    
    @UiTemplate("TitledPanel.ui.xml")
    interface TitledPanelUiBinder extends UiBinder<Widget,TitledPanel>{};
    public static final TitledPanelUiBinder uiBinder = GWT.create(TitledPanelUiBinder.class);
    
    @UiField
    LegendElement legend;
    
    @UiField
    LayoutPanel content;
    
    public TitledPanel() {
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
        content.onResize();
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
