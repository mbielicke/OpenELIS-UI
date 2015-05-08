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
package org.openelis.ui.widget.table;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.TableCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.CSSUtils;
import org.openelis.ui.widget.DragItem;
import org.openelis.ui.widget.Balloon;
import org.openelis.ui.widget.table.event.CellMouseOutEvent;
import org.openelis.ui.widget.table.event.CellMouseOverEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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
    protected boolean                attached, sized;

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
        header = GWT.create(Header.class);
        header.init(tbl);
        
        initWidget(uiBinder.createAndBindUi(this));

        this.table = tbl;

        
        setCSS(UIResources.INSTANCE.table());
        
        container = new Container();
        container.setStyleName(css.CellContainer());


        scrollView.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
              alignHeader();
            }
        });
        
        flexTable.addCellMouseOutHandler(new CellMouseOutEvent.Handler() {
            
            @Override
            public void onCellMouseOut(CellMouseOutEvent event) {
                if(table.balloonTimer != null)
                    table.balloonTimer.cancel();
                Balloon.hide();
            }
        });
        
    }

    
    @UiHandler("flexTable")
    protected void handleDoubleClick(DoubleClickEvent event) {
        int r,c;

        // if x < 0 the user moused out of table before letting up button
        // ignore event in this case
        if (event.getClientX() < 0)
            return;
        
        r = flexTable.getRowForEvent(event.getNativeEvent());
        c = flexTable.getColForEvent(event.getNativeEvent());
        
        table.fireCellDoubleClickedEvent(r,c);
    }
    
    @UiHandler("flexTable")
    protected void cellClick(ClickEvent event) {
        int r,c;
     
        // if x < 0 the user moused out of table before letting up button
        // ignore event in this case
        if (event.getClientX() < 0)
            return;
        
        r = flexTable.getRowForEvent(event.getNativeEvent());
        c = flexTable.getColForEvent(event.getNativeEvent());
        
        // chrome does not return -x if moused out while holding button
        // but does return negative col index
        if(r < 0 || c < 0)
        	return;

        if (table.fireCellClickedEvent(r, c, event.isControlKeyDown(), event.isShiftKeyDown()))
            table.startEditing(r, c, event.getNativeEvent());
    }

    /**
     * Method that will layout the table view and is called on first time
     * attached and when attributes affecting layout are changed in the table
     */
    protected void layout() {
        
        Node colgroup;

        /*
         * If View is not attached to DOM yet get out. onAttach will call
         * layout() the first time this widget attached.
         */

        if ( !attached)
            return;
        
        colgroup = flexTable.getElement().getElementsByTagName("colgroup").getItem(0);
        
        
        if(colgroup != null) {
            while(colgroup.getChildCount() > table.getColumnCount())
                colgroup.removeChild(colgroup.getChild(0));
        }

        for (int c = 0; c < table.getColumnCount(); c++ ) {
            flexTable.getColumnFormatter().setWidth(c, table.getColumnAt(c).getWidth() + "px");
            if (table.getColumnAt(c).getStyle() != null)
                flexTable.getColumnFormatter().setStyleName(c, table.getColumnAt(c).getStyle());
        } 
        
        flexTable.setWidth(table.getTotalColumnWidth() + "px");
        flexTable.setHeight("1px");
        DOM.setStyleAttribute(flexTable.getElement(), "backgroundColor", "transparent");

        // ********** Create and attach Header **************
        if (table.hasHeader()) {
            UIObject.setVisible(inner.getWidgetContainerElement(header), true);
            header.setVisible(true);
            header.layout();
            inner.setWidgetTopBottom(scrollView, CSSUtils.getHeight(header), Unit.PX, 0, Unit.PX);
            alignHeader();
        } else {
            UIObject.setVisible(inner.getWidgetContainerElement(header), false);
            header.setVisible(false);
            inner.setWidgetTopBottom(scrollView, 0, Unit.PX, 0, Unit.PX);
        }
    }
    
    protected void alignHeader() {
    	Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
                DOM.setStyleAttribute(header.getElement(),
                        "left",
                        (0 - scrollView.getHorizontalScrollPosition()) + "px");
			}
		});
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
    protected void createRow(int rc) {
        flexTable.insertRow(rc);
        flexTable.getRowFormatter().getElement(rc).setAttribute("height", (table.getRowHeight()+3)+"px");
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

        if ( !table.fixScrollBar && !sized) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    if (flexTable.getOffsetHeight() < scrollView.getOffsetHeight())
                        table.getParent().setHeight(CSSUtils.getHeight(flexTable) + "px");
                    sized = true;
                }

            });
        }
    }
    
    protected void bulkRender() {
        CellRenderer renderer;
        String style;
        
        SafeHtmlBuilder tb = new SafeHtmlBuilder();
        
        for(int i = 0; i < table.getRowCount(); i++) {
            style = table.getRowAt(i).getStyle(i);
            tb.appendHtmlConstant("<tr height='"+(table.getRowHeight()+3)+"px' index='"+i+"'" +
                              (style != null ? " class='"+style+"'>" : ">"));
            for(int j = 0; j < table.getColumnCount(); j++) {
                renderer = table.getColumnAt(j).getCellRenderer();
                tb.append(renderer.bulkRender(table.getValueAt(i,j)));
            }
            tb.appendHtmlConstant("</tr>");
        }
       
        
        // this is in a try catch only to get by for unit testing
        try {
            flexTable.getElement().getElementsByTagName("tbody").getItem(0).setInnerSafeHtml(tb.toSafeHtml());
        }catch(Exception e) {
        }
        
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                adjustForScroll(0);
            }
        });
 
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
                    flexTable.getCellFormatter().addStyleName(r, c, Balloon.isWarning(table.getEndUserExceptions(r, c), table.getValidateExceptions(r, c)) ? css.InputWarning() : css.InputError());
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
                    flexTable.getCellFormatter().removeStyleName(r, c, css.InputWarning());
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

        style = table.getRowAt(r).getStyle(r);
        if (style != null)
            flexTable.getRowFormatter().setStyleName(r, style);
        else
        	flexTable.getRowFormatter().setStyleName(r, "");

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

        renderer = table.getColumnAt(c).getCellRenderer();

        if (table.getQueryMode())
            renderer.renderQuery(flexTable, r, c, (QueryData)table.getValueAt(r, c));
        else
            renderer.render(flexTable, r, c, table.getValueAt(r, c));

        
        if (table.hasExceptions(r, c)) {
            flexTable.getCellFormatter().addStyleName(r, c, Balloon.isWarning(table.getEndUserExceptions(r, c), table.getValidateExceptions(r, c)) ? css.InputWarning() : css.InputError());
            flexTable.addCellMouseOverHandler(new CellMouseOverEvent.Handler(r, c) {
                @Override
                public void onCellMouseOver(CellMouseOverEvent event) {
                    int x,y;
                    Element td = flexTable.getCellFormatter().getElement(event.getRow(), event.getCol());
                    
                    y = td.getAbsoluteTop();
                    x = td.getAbsoluteLeft() + (td.getOffsetWidth()/2);
                    table.drawExceptions(event.getRow(), event.getCol(),x, y);
                }

            });
        } else {
            flexTable.getCellFormatter().removeStyleName(r, c, css.InputError());
            flexTable.getCellFormatter().removeStyleName(r, c, css.InputWarning());
            flexTable.removeHandler(r, c);
        }
        

        flexTable.getCellFormatter().setVisible(r, c, table.getColumnAt(c).isDisplayed());
        
    }

    protected void bulkExceptions(HashMap<Row,HashMap<Integer, ArrayList<Exception>>> exceptions) {
        for(Row row : exceptions.keySet()) {
            int r = table.convertModelIndexToView(table.getModel().indexOf(row));
            for(int c : exceptions.get(row).keySet()) {
                flexTable.getCellFormatter().addStyleName(r, c, Balloon.isWarning(table.getEndUserExceptions(r, c), table.getValidateExceptions(r, c)) ? css.InputWarning() : css.InputError());
                flexTable.addCellMouseOverHandler(new CellMouseOverEvent.Handler(r, c) {
                    @Override
                    public void onCellMouseOver(CellMouseOverEvent event) {
                        table.drawExceptions(event.getRow(), event.getCol(), event.getX(), event.getY());
                    }

                });
            }
        }
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
    protected void startEditing(final int r, final int c, Object value, NativeEvent event) {
        container.setWidth( (table.getColumnAt(c).getWidth() - 3));
        container.setHeight( (table.getRowHeight()));
        flexTable.setWidget(r, c, container);
       

        if (table.getQueryMode())
            table.getColumnAt(c)
                 .getCellEditor()
                 .startEditingQuery((QueryData)table.getValueAt(r, c), container, event);
        else
            table.getColumnAt(c)
                 .getCellEditor()
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

        cellEditor = table.getColumnAt(c).getCellEditor();

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
                                   CSSUtils.getHeight(scrollView));
    }

    /**
     * Method is overridden from Composite so that layout() can be deferred
     * until it is attached to the DOM
     */
    @Override
    protected void onAttach() {

        super.onAttach();

        if(attached)
            return;
            
        attached = true;
        layout();
        flexTable.setVisible(false);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                flexTable.setVisible(true);
                Element svEl = inner.getWidgetContainerElement(scrollView);
                
                if (scrollView.getMaximumVerticalScrollPosition() == 0)
                    table.setWidth(CSSUtils.getWidth(svEl) - 1);
                else
                    table.setWidth(CSSUtils.getWidth(svEl) -
                                   NativeVerticalScrollbar.getNativeScrollbarWidth() - 1);
                
                if (CSSUtils.getWidth( (svEl)) > 0) {
                    table.setWidth(CSSUtils.getWidth(svEl) - 1);
                    scrollView.setWidth(CSSUtils.getWidth(svEl) -
                                        CSSUtils.getAddedBorderWidth(table.getElement()) + "px");
                }

                if (CSSUtils.getHeight(svEl) > 0) {
                    scrollView.setHeight(CSSUtils.getHeight(svEl) - CSSUtils.getHeight(header) -
                                         CSSUtils.getAddedBorderHeight(table.getElement()) + "px");

                }
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
        
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            
            @Override
            public void execute() {
                Element svEl = inner.getWidgetContainerElement(scrollView);
                

                if (CSSUtils.getWidth(svEl) > 0) {

                    scrollView.setWidth(CSSUtils.getWidth(svEl) -
                                        CSSUtils.getAddedBorderWidth(table.getElement()) + "px");

                    if (CSSUtils.getHeight(inner) > 0) {
                        int height = CSSUtils.getHeight(inner) -  CSSUtils.getHeight(header) -
                                        CSSUtils.getAddedBorderHeight(table.getElement());
                        /*
                         * This check is here only for Unit Testing.  If not done Unit test on the
                         * table will fail here with assertion check from the widget.
                         */
                        if(height > 0)
                            scrollView.setHeight(height + "px");
                    }
                    
                    if (scrollView.getMaximumVerticalScrollPosition() == 0)
                        table.setWidth(CSSUtils.getWidth(svEl) - 1);
                    else
                        table.setWidth(CSSUtils.getWidth(svEl) -
                
                                       NativeVerticalScrollbar.getNativeScrollbarWidth() - 1);
                    
                    adjustForScroll(0);

                }
                
            }
        });


    }

    private void adjustForScroll(int before) {
        if (before == 0 && scrollView.getMaximumVerticalScrollPosition() > 0)
            table.setWidth( (table.getOffsetWidth() -
                             NativeVerticalScrollbar.getNativeScrollbarWidth() - 1) +
                           "px");
        else if (before > 0 && scrollView.getMaximumVerticalScrollPosition() == 0)
            table.setWidth(table.getOffsetWidth() - 1 + "px");
    }
    
    /*
     * This public method added for unit testing 
     */
    public String getCellDisplay(int row, int col) {
        return flexTable.getText(row, col);
    }
    
    public Widget getCellWidget(int row, int col) {
        Widget wid;
        
        wid = flexTable.getWidget(row, col);
       
        if(wid == container)
            return container.editor;
        
        return wid;
       
    }

}
