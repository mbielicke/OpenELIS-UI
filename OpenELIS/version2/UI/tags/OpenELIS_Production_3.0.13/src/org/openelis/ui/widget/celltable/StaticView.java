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
package org.openelis.ui.widget.celltable;

import org.openelis.ui.common.Util;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.TableCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.CSSUtils;
import org.openelis.ui.widget.DragItem;
import org.openelis.ui.widget.celltable.event.CellMouseOutEvent;
import org.openelis.ui.widget.celltable.event.CellMouseOverEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.NativeVerticalScrollbar;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Composite GWT widget to draw and handle logic for displaying a Table. All
 * methods are protected and only used by the Table widget itself.
 * 
 * @author tschmidt
 * 
 */
public class StaticView extends ViewInt {
    @UiTemplate("staticView.ui.xml")
    interface ViewUiBinder extends UiBinder<Widget, StaticView> {
    };

    public static final ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);

    /**
     * Reference to Table this View is used in
     */
    protected Table                  table;

    /**
     * Table used to draw Table flexTable
     */
    @UiField
    protected FlexTable              flexTable;

    /**
     * Table used to draw Header flexTable for the table
     */
    @UiField(provided = true)
    protected Header                 header;

    /**
     * Scrollable area that contains flexTable and possibly header for
     * horizontal scroll.
     */
    @UiField
    protected ScrollPanel            scrollView;

    @UiField
    protected LayoutPanel            inner;

    /**
     * Flag used to determine if the table has been attached to know when to do
     * layout for the first time.
     */
    protected boolean                attached;

    /**
     * Container to hold the widget for formatting and spacing
     */
    private Container                container;

    protected TableCSS               css;

    protected StaticView             source   = this;

    /**
     * Constructor that takes a reference to the table that will use this view
     * 
     * @param tree
     */
    public StaticView(Table tbl) {
        header = new Header(tbl);
        
        initWidget(uiBinder.createAndBindUi(this));

        this.table = tbl;

        container = new Container();

        setCSS(UIResources.INSTANCE.table());

        scrollView.addScrollHandler(new ScrollHandler() {

            @Override
            public void onScroll(ScrollEvent event) {
                DOM.setStyleAttribute(header.getElement(),
                                      "left",
                                      (0 - scrollView.getHorizontalScrollPosition()) + "px");
            }
        });

        flexTable.addCellMouseOutHandler(new CellMouseOutEvent.Handler() {
            @Override
            public void onCellMouseOut(CellMouseOutEvent event) {
                //ExceptionHelper.closePopup();
            }
        });

    }

    @UiHandler("flexTable")
    protected void handleClick(ClickEvent event) {
    	cellClick(event.getNativeEvent(),false);
    }
    
    @UiHandler("flexTable")
    protected void handleDoubleClick(DoubleClickEvent event) {
    	cellClick(event.getNativeEvent(),true);
    }
    
    protected void cellClick(NativeEvent event, boolean isDouble) {
    	int r,c;

        // if x < 0 the user moused out of table before letting up button
        // ignore event in this case
        if (event.getClientX() < 0)
            return;
        
        r = flexTable.getRowForEvent(event);
        c = flexTable.getColForEvent(event);

        if (table.fireCellClickedEvent(r, c, event.getCtrlKey(), event.getShiftKey(), isDouble))
            table.startEditing(r, c, event);
    }

    /**
     * Method that will layout the table view and is called on first time
     * attached and when attributes affecting layout are changed in the table
     */
    protected void layout() {

        /*
         * If View is not attached to DOM yet get out. onAttach will call
         * layout() the first time this widget attached.
         */

        if ( !attached)
            return;

        for (int c = 0; c < table.getColumnCount(); c++ ) {
            flexTable.getColumnFormatter().setWidth(c, table.getColumnAt(c).getWidth() + "px");
            if (table.getColumnAt(c).getStyle() != null)
                flexTable.getColumnFormatter().setStyleName(c, table.getColumnAt(c).getStyle());
        }
        flexTable.setWidth(table.getTotalColumnWidth() + "px");

        // ********** Create and attach Header **************
        if (table.hasHeader()) {
            UIObject.setVisible(inner.getWidgetContainerElement(header), true);
            header.setVisible(true);
            header.layout();
            inner.setWidgetTopBottom(scrollView, header.getOffsetHeight(), Unit.PX, 0, Unit.PX);
        } else {
            UIObject.setVisible(inner.getWidgetContainerElement(header), false);
            header.setVisible(false);
            inner.setWidgetTopBottom(scrollView, 0, Unit.PX, 0, Unit.PX);
        }
        
        if(Util.stripUnits(CSSUtils.getStyleProperty((com.google.gwt.user.client.Element)inner.getWidgetContainerElement(scrollView), "height")) > 0)
        		scrollView.setHeight(Util.stripUnits(CSSUtils.getStyleProperty((com.google.gwt.user.client.Element)inner.getWidgetContainerElement(scrollView), "height")) - 
        		CSSUtils.getAddedBorderHeight(table.getElement())+"px");
        
        

    }

    /**
     * This method is called when a column width is changed. It will resize the
     * columns to there currently set width.
     */
    protected void resize() {

        for (int c = 0; c < table.getColumnCount(); c++ )
            flexTable.getColumnFormatter().setWidth(c, table.getColumnAt(c).getWidth() + "px");
        flexTable.setWidth(table.getTotalColumnWidth() + "px");
    }

    /**
     * Will create the the necessary visible rows for the flexTable table
     * depending on what is needed at the time. If model.size() < visibleRows
     * then the number of rows created will equal model.size() else the number
     * visibleRows will be created for the flexTable table.
     */
    private void createRow(int rc) {
        flexTable.insertRow(rc);
        flexTable.getCellFormatter().setHeight(rc, 0, table.getRowHeight() + "px");
        flexTable.getRowFormatter().getElement(rc).setAttribute("index", "" + rc);

        if (table.getDragController() != null)
            table.dragController.makeDraggable(new DragItem(table, flexTable.getRowFormatter()
                                                                            .getElement(rc)));
    }

    /**
     * This method will redraw the table from the startRow to the endRow that
     * are passed in as params. Rows are passed as -1,-1 the entire view will be
     * drawn.
     * 
     * @param smr
     * @param emr
     */
    protected void renderView(int smr, int emr) {
        int r, startMax;

        //if ( !attached)
        //    return;
     
        
        table.finishEditing();

        if (smr < 0)
            smr = 0;

        if (emr < 0)
            emr = table.getRowCount() - 1;

        startMax = scrollView.getMaximumVerticalScrollPosition();

        /*
         * Create/Load Rows in the flexTable table
         */
        for (r = smr; r <= emr; r++ ) {
            /*
             * Create table row if needed
             */
            if (r >= flexTable.getRowCount()) {
                createRow(flexTable.getRowCount());
                /*
                 * ColumnFormatter is not available until first row is inserted
                 * so call resize after that
                 */
                if (r == 0)
                    resize();
            }

            for (int c = 0; c < table.getColumnCount(); c++ )
                renderCell(r, c);

            applyRowStyle(r);
        }

        while (flexTable.getRowCount() > table.getRowCount())
            flexTable.removeRow(table.getRowCount());

        adjustForScroll(startMax);
        
        if(!table.fixScrollBar) {
            if(flexTable.getOffsetHeight() < scrollView.getOffsetHeight())
                table.getParent().setHeight(flexTable.getOffsetHeight()+"px");
                		
        }
            
     
    }

    protected void addRow(int r) {
        int startMax = scrollView.getMaximumVerticalScrollPosition();

        createRow(r);

        for (int c = 0; c < table.getColumnCount(); c++ )
            renderCell(r, c);

        applyRowStyle(r);

        adjustForScroll(startMax);

    }

    protected void addColumn(int c) {
        for (int i = 0; i < flexTable.getRowCount(); i++ ) {
            flexTable.insertCell(i, c);
        }
        layout();
        for (int i = 0; i < flexTable.getRowCount(); i++ ) {
            renderCell(i, c);
        }
    }

    protected void removeColumn(int c) {
        for (int i = 0; i < flexTable.getRowCount(); i++ ) {
            flexTable.removeCell(i, c);
        }
        layout();
    }

    protected void removeRow(int r) {
        int startMax = scrollView.getMaximumVerticalScrollPosition();

        flexTable.removeRow(r);
        
        adjustForScroll(startMax);
    }

    protected void removeAllRows() {
        flexTable.removeAllRows();
    }

    protected void renderSelections(int start, int end) {

        for (int r = start; r <= end; r++ ) {
            if (table.isRowSelected(r))
                flexTable.getRowFormatter().addStyleName(r, css.Selection());
            else
                flexTable.getRowFormatter().removeStyleName(r, css.Selection());
        }

    }

    protected void renderExceptions(int start, int end) {
        start = start < 0 ? 0 : start;
        end = end < 0 ? flexTable.getRowCount() - 1 : end;

        for (int r = start; r <= end; r++ ) {
            for (int c = 0; c < table.getColumnCount(); c++ ) {
                if (table.hasExceptions(r, c)) {
                    flexTable.getCellFormatter().addStyleName(r, c, css.InputError());
                    flexTable.addCellMouseOverHandler(new CellMouseOverEvent.Handler(r, c) {

                        @Override
                        public void onCellMouseOver(CellMouseOverEvent event) {
                            table.drawExceptions(event.getRow(),
                                                 event.getCol(),
                                                 event.getX(),
                                                 event.getY());
                        }

                    });
                } else {
                    flexTable.getCellFormatter().removeStyleName(r, c, css.InputError());
                    flexTable.removeHandler(r, c);
                }

                flexTable.getCellFormatter().setVisible(r, c, table.getColumnAt(c).isDisplayed());
            }
        }
    }

    /**
     * This method will apply either a style that is set in the Row getStyle
     * method or the selection style if the row is selected
     */
    protected void applyRowStyle(int r) {
        String style;

        if (r >= table.getRowCount())
            return;

        style = null;//table.getRowAt(r).getStyle(r);
        if (style != null)
            flexTable.getRowFormatter().setStyleName(r, style);

        if (table.isRowSelected(r))
            flexTable.getRowFormatter().addStyleName(r, css.Selection());
        else
            flexTable.getRowFormatter().removeStyleName(r, css.Selection());
    }

    /**
     * Applies the selection style to a table row
     * 
     * @param r
     */
    protected void applySelectionStyle(int r) {
        int rc;

        rc = r;
        if (rc > -1)
            flexTable.getRowFormatter().addStyleName(rc, css.Selection());

    }

    /**
     * Removes the Selection style from a table row
     * 
     * @param r
     */
    protected void applyUnselectionStyle(int r) {
        int rc;

        rc = r;
        if (rc > -1)
            flexTable.getRowFormatter().removeStyleName(rc, css.Selection());

    }

    protected void renderCell(int r, int c) {
        CellRenderer renderer;

        renderer = table.getColumnAt(c).getCellRenderer(r);

        if (table.getQueryMode())
            renderer.renderQuery(flexTable, r, c, (QueryData)table.getValueAt(r, c));
        else
            renderer.render(flexTable, r, c, table.getValueAt(r, c));

        if (table.hasExceptions(r, c)) {
            flexTable.getCellFormatter().addStyleName(r, c, css.InputError());
            flexTable.addCellMouseOverHandler(new CellMouseOverEvent.Handler(r, c) {
                @Override
                public void onCellMouseOver(CellMouseOverEvent event) {
                    table.drawExceptions(event.getRow(), event.getCol(), event.getX(), event.getY());
                }

            });
        } else {
            flexTable.getCellFormatter().removeStyleName(r, c, css.InputError());
            flexTable.removeHandler(r, c);
        }

        flexTable.getCellFormatter().setVisible(r, c, table.getColumnAt(c).isDisplayed());
    }

    /**
     * Will put the passed cell into edit mode making sure the the cell is
     * compeltely visible first
     * 
     * @param r
     * @param c
     * @param value
     * @param event
     */
    protected void startEditing(int r, final int c, Object value, NativeEvent event) {
        container.setWidth( (table.getColumnAt(c).getWidth() - 3));
        container.setHeight( (table.getRowHeight() - 3));
        flexTable.setWidget(r, c, container);

        if (table.getQueryMode())
            table.getColumnAt(c)
                 .getCellEditor(r)
                 .startEditingQuery((QueryData)table.getValueAt(r, c), container, event);
        else
            table.getColumnAt(c)
                 .getCellEditor(r)
                 .startEditing(table.getValueAt(r, c), container, event);
    }

    /**
     * Returns the value of the CellEditor
     * 
     * @param r
     * @param c
     * @return
     */
    protected Object finishEditing(int r, int c) {
        CellEditor cellEditor;

        cellEditor = table.getColumnAt(c).getCellEditor(r);

        return cellEditor.finishEditing();
    }

    /**
     * This method will re-adjust the scrollbar height based on number of rows
     * in the model
     */
    protected void adjustScrollBarHeight() {

    }

    /**
     * Method will scroll the table to make sure the passed row is included in
     * the view
     * 
     * @param r
     * @return
     */
    protected boolean scrollToVisible(int r) {

        if (scrollView.getMaximumVerticalScrollPosition() == 0)
            return false;

        if (isRowVisible(r))
            return false;

        int hPos = scrollView.getHorizontalScrollPosition();

        DOM.scrollIntoView(flexTable.getRowFormatter().getElement(r));

        scrollView.setHorizontalScrollPosition(hPos);

        return true;
    }

    /**
     * Method will scroll the view up or down by the passed number of rows. Pass
     * negative value to scroll up.
     * 
     * @param n
     */
    protected void scrollBy(int n) {

    }

    /**
     * Returns true if the passed row is drawn in the current view
     * 
     * @param r
     * @return
     */
    protected boolean isRowVisible(int r) {
        Element row = flexTable.getRowFormatter().getElement(r);
        int top = row.getOffsetTop();
        int height = row.getOffsetHeight();

        return top >= scrollView.getVerticalScrollPosition() &&
               ( (top + height) <= scrollView.getVerticalScrollPosition() +
                                   scrollView.getOffsetHeight());
    }

    /**
     * Method is overridden from Composite so that layout() can be deferred
     * until it is attached to the DOM
     */
    @Override
    protected void onAttach() {

        super.onAttach();

        attached = true;
        flexTable.setVisible(false);
        layout();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                flexTable.setVisible(true);
            }
        });

    }

    /**
     * Returns the Header for this view
     * 
     * @return
     */
    protected Header getHeader() {
        return header;
    }

    /**
     * Returns the actual drawn row height on the screen
     */
    protected int getRowHeight() {
        return table.rowHeight;
    }

    public void setCSS(TableCSS css) {
        css.ensureInjected();

        for (int i = 0; i < flexTable.getRowCount(); i++ ) {
            if (flexTable.getRowFormatter().getStyleName(i).contains(this.css.Selection())) {
                flexTable.getRowFormatter().removeStyleName(i, this.css.Selection());
                flexTable.getRowFormatter().addStyleName(i, css.Selection());
            }
            for (int j = 0; j < flexTable.getCellCount(i); j++ ) {
                if (flexTable.getCellFormatter().getStyleName(i, j).contains(this.css.InputError())) {
                    flexTable.getCellFormatter().removeStyleName(i, j, this.css.InputError());
                    flexTable.getCellFormatter().addStyleName(i, j, css.InputError());
                }
                if (flexTable.getCellFormatter()
                             .getStyleName(i, j)
                             .contains(this.css.InputWarning())) {
                    flexTable.getCellFormatter().removeStyleName(i, j, this.css.InputWarning());
                    flexTable.getCellFormatter().addStyleName(i, j, css.InputWarning());
                }
            }
        }

        this.css = css;

        flexTable.setStyleName(css.Table());

    }

    @Override
    protected int rowHeight() {
        return 0;
    }

    @Override
    FlexTable table() {
        return flexTable;
    }

    @Override
    ScrollPanel scrollView() {
        return scrollView;
    }

    @Override
    TableCSS css() {
        return css;
    }

    @Override
    public void onResize() {
        super.onResize();
        if (inner.getWidgetContainerElement(scrollView).getOffsetWidth() > 0) {
            if(scrollView.getMaximumVerticalScrollPosition() == 0)
                table.setWidth(inner.getWidgetContainerElement(scrollView).getOffsetWidth() - 1);
            else
                table.setWidth(inner.getWidgetContainerElement(scrollView).getOffsetWidth()-NativeVerticalScrollbar.getNativeScrollbarWidth() - 1);
            
            scrollView.setWidth( (inner.getWidgetContainerElement(scrollView).getOffsetWidth() - CSSUtils.getAddedBorderWidth(table.getElement())) +
                                "px");
            if(inner.getWidgetContainerElement(scrollView).getOffsetHeight() > 0)
                scrollView.setHeight( (inner.getWidgetContainerElement(scrollView).getOffsetHeight() - CSSUtils.getAddedBorderHeight(table.getElement())) +
                                "px");

        }
    }
    
    private void adjustForScroll(int before) {
        if (before == 0 && scrollView.getMaximumVerticalScrollPosition() > 0)
            table.setWidth( (table.getOffsetWidth() - NativeVerticalScrollbar.getNativeScrollbarWidth() - 1) + "px");
        else if (before > 0 && scrollView.getMaximumVerticalScrollPosition() == 0)
            table.setWidth(table.getOffsetWidth() - 1 + "px");
    }

}
