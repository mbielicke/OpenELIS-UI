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

import org.openelis.ui.resources.UIResources;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class RadioPanel extends ResizeComposite implements HasBeforeSelectionHandlers<Integer>,
                                               HasSelectionHandlers<Integer> {

    protected DockLayoutPanel        dock;
    public DeckLayoutPanel           deck;
    protected FlowPanel              flow;
    protected ArrayList<RadioButton> radios;

    private RadioPanel               source = this;

    @UiConstructor
    public RadioPanel(Direction dir) {

        UIResources.INSTANCE.tabpanel().ensureInjected();

        dock = new DockLayoutPanel(Unit.PX);
        deck = new DeckLayoutPanel();
        flow = new FlowPanel();

        setRadioDirection(dir);

        dock.add(deck);

        radios = new ArrayList<RadioButton>();

        initWidget(dock);
    }

    @UiChild(tagname = "deck")
    public void add(Widget widget) {
        insert(widget, deck.getWidgetCount());
    }

    public void insert(Widget widget, int before) {
        RadioButton radio;

        deck.insert(widget, before);

        radio = new RadioButton("radios");

        radios.add(radio);

        flow.add(radio);

        radio.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int index = radios.indexOf(event.getSource());

                if ( !BeforeSelectionEvent.fire(source, index).isCanceled()) {
                    deck.showWidget(radios.indexOf(event.getSource()));
                    SelectionEvent.fire(source, index);
                }

            }
        });

        if (radios.size() == 1) {
            deck.showWidget(0);
            radio.setValue(true);
        }
    }

    public void setRadioDirection(Direction dir) {
        switch (dir) {
            case NORTH:
                dock.addNorth(flow, 20);
                dock.getWidgetContainerElement(flow).setAttribute("align", "center");
            case WEST:
                dock.addWest(flow, 20);
                dock.getWidgetContainerElement(flow).setAttribute("valign", "middle");
            case EAST:
                dock.addEast(flow, 20);
                dock.getWidgetContainerElement(flow).setAttribute("valign", "middle");
            default:
                dock.addSouth(flow, 20);
                dock.getWidgetContainerElement(flow).setAttribute("align", "center");
        }
    }

    public void show(Widget widget) {
        show(radios.indexOf(widget));
    }

    public void show(int index) {
        deck.showWidget(index);
        radios.get(index).setValue(true);
    }

    public boolean isShowing(int index) {
        return deck.getVisibleWidgetIndex() == index;
    }

    public boolean isShowing(Widget widget) {
        return deck.getVisibleWidget() == widget;
    }

    public int getWidgetCount() {
        return deck.getWidgetCount();
    }

    public Widget getWidget(int index) {
        return deck.getWidget(index);
    }

    public int getWidgetIndex(Widget widget) {
        return deck.getWidgetIndex(widget);
    }

    public boolean remove(int index) {
        flow.remove(radios.remove(index));
        return deck.remove(index);
    }

    public boolean remove(Widget widget) {
        flow.remove(radios.remove(getWidgetIndex(widget)));
        return deck.remove(widget);
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler) {
        return addHandler(handler, BeforeSelectionEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

}
