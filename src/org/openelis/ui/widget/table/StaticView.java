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
import org.openelis.ui.widget.cell.CellRenderer;
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
import com.google.gwt.event.dom.client.MouseEvent;
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
    	this.table = tbl;
        
    	header = GWT.create(Header.class);
        header.init(tbl);
       
        initWidget(uiBinder.createAndBindUi(this)); 
        
        setCSS(UIResources.INSTANCE.table());
        
        container = new Container();
        container.setStyleName(css.CellContainer());                
    }

    @UiHandler("scrollView")
    protected void handleScroll(ScrollEvent event) {
    	header.getElement().getStyle().setProperty("left",-scrollView.getHorizontalScrollPosition(),Unit.PX);
    }
    
    @UiHandler("flexTable")
    protected void handleCellMouseOut(CellMouseOutEvent event) {
    	table.cancelBalloonTimer();
        Balloon.hide();
    }
    
    @UiHandler("flexTable")
    protected void handleDoubleClick(DoubleClickEvent event) {
        int r,c;

        if (userMousedOutOfCell(event))
            return;
        
        r = flexTable.getRowForEvent(event.getNativeEvent());
        c = flexTable.getColForEvent(event.getNativeEvent());
        
        table.fireCellDoubleClickedEvent(r,c);
    }
    
    @UiHandler("flexTable")
    protected void cellClick(ClickEvent event) {
        int row,col;

        if (userMousedOutOfCell(event))
            return;
        
        row = flexTable.getRowForEvent(event.getNativeEvent());
        col = flexTable.getColForEvent(event.getNativeEvent());

        if (table.fireCellClickedEvent(row, col, event.isControlKeyDown(), event.isShiftKeyDown()))
            table.startEditing(row, col, event.getNativeEvent());
    }
    
    private boolean userMousedOutOfCell(MouseEvent<?> event) {
    	return event.getClientX() < 0;
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

        removeExtraColumns();
        sizeTable();
        setColumnStyles();
        layoutHeader();
    }
    
    private void removeExtraColumns() {
    	Node colgroup;
    	
    	if(!flexTable.getElement().hasTagName("colgroup"))
    		return;
    	 
        colgroup = flexTable.getElement().getElementsByTagName("colgroup").getItem(0);
         
        while(colgroup.getChildCount() > table.getColumnCount()) {
           colgroup.removeChild(colgroup.getChild(0));
        }
    }

    protected void sizeTable() {
        for (int c = 0; c < table.getColumnCount(); c++ ) {
            flexTable.getColumnFormatter().setWidth(c, table.getColumnAt(c).getWidth() + "px");
        }
        flexTable.setWidth(table.getTotalColumnWidth() + "px");
    }
    
    protected void setColumnStyles() {
    	for (int c = 0; c < table.getColumnCount(); c++) {
    		flexTable.getColumnFormatter().setStyleName(c, table.getColumnAt(c).getStyle());
    	}
    }
    
    protected void layoutHeader() {
        if (table.hasHeader()) {
            UIObject.setVisible(inner.getWidgetContainerElement(header), true);
            header.setVisible(true);
            header.layout();
            inner.setWidgetTopBottom(scrollView, CSSUtils.getHeight(header), Unit.PX, 0, Unit.PX);
        } else {
            UIObject.setVisible(inner.getWidgetContainerElement(header), false);
            header.setVisible(false);
            inner.setWidgetTopBottom(scrollView, 0, Unit.PX, 0, Unit.PX);
        }
    }

    protected void createRow(int row) {
        flexTable.insertRow(row);
        flexTable.getRowFormatter().getElement(row).setAttribute("height", (table.getRowHeight()+3)+"px");
        flexTable.getRowFormatter().getElement(row).setAttribute("index", "" + row);

        if (table.getDragController() != null)
            table.dragController.makeDraggable(new DragItem(table, flexTable.getRowFormatter()
                                                                            .getElement(row)));
    }

    protected void renderView(int startRow, int endRow) {
        table.finishEditing();

        if (startRow < 0) startRow = 0;
        if (endRow < 0) endRow = table.getRowCount() - 1;

        for (int row = startRow;  row <= endRow; row++ ) {
            if (row >= flexTable.getRowCount()) {
                createRow(flexTable.getRowCount());
            }
            renderRow(row);
            applyRowStyle(row);
        }
        removeExtraRows();
        adjustTableWidthForScrollbar();
    }
    
    protected void removeExtraRows() {
    	 while (flexTable.getRowCount() > table.getRowCount())
             flexTable.removeRow(table.getRowCount());
    }
    
    protected void bulkRender() {
        
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        for(int row = 0; row < table.getRowCount(); row++) {
        	bulkRenderRow(builder,row);
        }
        
        flexTable.getElement().getElementsByTagName("tbody").getItem(0).setInnerSafeHtml(builder.toSafeHtml());
        
        adjustViewPort();
    }
    
    protected void bulkRenderRow(SafeHtmlBuilder builder, int row) {
    	String style = table.getRowAt(row).getStyle(row);
    	builder.appendHtmlConstant("<tr height='"+(table.getRowHeight()+3)+"px' index='"+row+"'" + (style != null ? " class='"+style+"'>" : ">"));
    	for(int col = 0; col < table.getColumnCount(); col++) {
    		bulkRenderCell(builder, row, col);
    	}
    	builder.appendHtmlConstant("</tr>");
    }
    
    protected void bulkRenderCell(SafeHtmlBuilder builder, int row, int col) {
    	CellRenderer renderer;
    	
    	renderer = table.getColumnAt(col).getCellRenderer();
    	builder.appendHtmlConstant("<td>");
        builder.append(renderer.getHTML(table.getValueAt(row,col)));
        builder.appendHtmlConstant("</td>");
    }

    protected void addRow(int r) {
        createRow(r);

        for (int c = 0; c < table.getColumnCount(); c++ )
            renderCell(r, c);

        applyRowStyle(r);

        adjustTableWidthForScrollbar();
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
        flexTable.removeRow(r);
        adjustTableWidthForScrollbar();
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
        if(start < 0) start = 0;
        if(end < 0) end = flexTable.getRowCount() - 1;

        for (int r = start; r <= end; r++ ) {
            for (int c = 0; c < table.getColumnCount(); c++ ) {
                if (table.hasExceptions(r, c)) {
                	renderCellException(r,c);
                } else {
                	clearCellException(r,c);
                }
            }
        }
    }
    
    protected void renderCellException(int r, int c) {
    	 flexTable.getCellFormatter().addStyleName(r, c, getExceptionStyle(r, c));
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
    }
    
    protected void clearCellException(int r, int c) {
    	 flexTable.getCellFormatter().removeStyleName(r, c, css.InputWarning());
         flexTable.getCellFormatter().removeStyleName(r, c, css.InputError());
         flexTable.removeHandler(r, c);
    }
    
    protected String getExceptionStyle(int r, int c) {
    	return Balloon.isWarning(table.getEndUserExceptions(r, c), table.getValidateExceptions(r, c)) ? css.InputWarning() : css.InputError(); 
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
    	flexTable.getRowFormatter().addStyleName(r, css.Selection());
    }

    /**
     * Removes the Selection style from a table row
     * 
     * @param r
     */
    protected void applyUnselectionStyle(int r) {
    	flexTable.getRowFormatter().removeStyleName(r, css.Selection());
    }

    protected void renderRow(int r) {
    	 for (int c = 0; c < table.getColumnCount(); c++ )
             renderCell(r, c);
    }
    
    protected void renderCell(int r, int c) {
        CellRenderer renderer;

        renderer = table.getColumnAt(c).getCellRenderer();

        //if (table.getQueryMode())
          //  renderer.renderQuery(flexTable, r, c, (QueryData)table.getValueAt(r, c));
       // else
            renderer.render(flexTable.getFlexCellFormatter().getElement(r, c), table.getValueAt(r, c));

        if (table.hasExceptions(r, c)) {
        	renderCellException(r,c);
        } else {
        	clearCellException(r,c);
        }
    }

    protected void bulkExceptions(HashMap<Row,HashMap<Integer, ArrayList<Exception>>> exceptions) {
        for(Row row : exceptions.keySet()) {
            int r = table.getModel().indexOf(row);
            for(int c : exceptions.get(row).keySet()) {
            	renderCellException(r,c);
            }
        }
    }
    
    protected void startEditing(int r,int c, Object value, NativeEvent event) {
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

    protected Object finishEditing(int c) {
        return table.getColumnAt(c).getCellEditor().finishEditing();
    }

    /**
     * Method will scroll the table to make sure the passed row is included in
     * the view
     * 
     * @param r
     * @return
     */
    protected boolean scrollToVisible(int r) {
    	int horizontalScrollPosition;
    	
        if (scrollView.getMaximumVerticalScrollPosition() == 0)
            return false;

        if (isRowVisible(r))
            return false;

        horizontalScrollPosition = scrollView.getHorizontalScrollPosition();

        flexTable.getRowFormatter().getElement(r).scrollIntoView();

        // scrollIntoView moves horizontalScroll, this sets it back
        scrollView.setHorizontalScrollPosition(horizontalScrollPosition);

        return true;
    }
    
    public void scrollBy(int r) {
    	
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
        adjustViewPort();
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
        adjustViewPort();
    }
    
    protected void adjustViewPort() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {  
            @Override
            public void execute() {
            	setViewSize();
                adjustTableWidthForScrollbar();
            }
        });
    }
    
    protected void setViewSize() {
        Element svEl = inner.getWidgetContainerElement(scrollView);
        
        if (CSSUtils.getWidth(svEl) > 0) {

            scrollView.setWidth(svEl.getClientWidth() + "px");

            if (CSSUtils.getHeight(inner) > 0) {
                scrollView.setHeight(inner.getElement().getClientHeight() -  
                		             header.getElement().getClientHeight()- 
                		             CSSUtils.getAddedBorderHeight(table.getElement()) + 
                		             "px");
            }
        }
    }

    private void adjustTableWidthForScrollbar() {
    	int width = table.getOffsetWidth();
        if (scrollView.getMaximumVerticalScrollPosition() > 0)
        	width -= NativeVerticalScrollbar.getNativeScrollbarWidth();
        table.setWidth(width+"px");
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

	@Override
	Object finishEditing(int r, int c) {
		return finishEditing(c);
	}

	@Override
	void adjustScrollBarHeight() {
		// TODO Auto-generated method stub
		
	}
}
