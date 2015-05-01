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

import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.TableCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Balloon;
import org.openelis.ui.widget.CSSUtils;
import org.openelis.ui.widget.cell.Cell;
import org.openelis.ui.widget.cell.CellClickedEvent;
import org.openelis.ui.widget.cell.CellDoubleClickedEvent;
import org.openelis.ui.widget.cell.CellGrid;
import org.openelis.ui.widget.cell.CellMouseOutEvent;
import org.openelis.ui.widget.cell.CellMouseOverEvent;
import org.openelis.ui.widget.cell.EditableCell;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.NativeVerticalScrollbar;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public abstract class View<T extends Controller> extends ResizeComposite {
    
	@UiTemplate("View.ui.xml")
    interface ViewUiBinder extends UiBinder<Widget, View<? extends Controller>> {
    };

    public static final ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);


    protected T controller;
    
    @UiField
    protected CellGrid grid;

    @UiField(provided = true)
    protected TableHeader header;

    @UiField
    protected ScrollPanel scrollView;

    @UiField
    protected LayoutPanel inner;

    /**
     * Flag used to determine if the table has been attached to know when to do
     * layout for the first time.
     */
    protected boolean attached, sized;

    protected TableCSS css;

    protected CellMouseOverEvent.Handler mouseOver;
    
    /**
     * Constructor that takes a reference to the table that will use this view
     * 
     * @param tree
     */
    public View(T controller) {
    	this.controller = controller;
        
    	header = GWT.create(TableHeader.class);
        header.init(controller,this);
       
        initWidget(uiBinder.createAndBindUi(this)); 
        
        setCSS(UIResources.INSTANCE.table());   
        
        mouseOver = new CellMouseOverEvent.Handler() {
            @Override
            public void onCellMouseOver(CellMouseOverEvent event) {
                int x,y;
                Element td = grid.getCellFormatter().getElement(event.getRow(), event.getCol());
                
                y = td.getAbsoluteTop();
                x = td.getAbsoluteLeft() + (td.getOffsetWidth()/2);
                controller.drawExceptions(event.getRow(), event.getCol(),x, y);
            }
        };
    }

    @UiHandler("scrollView")
    protected void handleScroll(ScrollEvent event) {
    	alignHeader();
    }
    
    @UiHandler("grid")
    protected void handleCellMouseOut(CellMouseOutEvent event) {
    	controller.cancelBalloonTimer();
        Balloon.hide();
    }
    
    @UiHandler("grid")
    protected void handleDoubleClick(CellDoubleClickedEvent event) {
        controller.fireCellDoubleClickedEvent(event.getRow(),event.getCol());
    }
    
    @UiHandler("grid")
    protected void cellClick(CellClickedEvent event) {
        if (controller.fireCellClickedEvent(event.getRow(), 
        		                            event.getCol(), 
        		                            event.isCtrlKeyDown(), 
        		                            event.isShiftKeyDown())) {
            controller.startEditing(event.getRow(), 
            		                event.getCol(), 
            		                event.getNativeEvent());
        }
    }

    /**
     * Method that will layout the table view and is called on first time
     * attached and when attributes affecting layout are changed in the table
     */
    public void layout() {
        if (attached) {
        	removeExtraColumns();
        	sizeTable();
        	setColumnStyles();
        	layoutHeader();
        }
    }
    
    private void removeExtraColumns() {
    	Node colgroup;
    	
    	if (grid.getElement().getElementsByTagName("colgroup").getLength() > 0) {
    		colgroup = grid.getElement().getElementsByTagName("colgroup").getItem(0);
    		while (colgroup.getChildCount() > controller.getColumnCount()) {
    			colgroup.removeChild(colgroup.getChild(0));
    		}
    	}
    }

    protected void sizeTable() {
        for (int c = 0; c < controller.getColumnCount(); c++ ) {
            grid.getColumnFormatter().setWidth(c, controller.getColumnAt(c).getWidth() + "px");
        }
        grid.setWidth(controller.getTotalColumnWidth() + "px");
    }
    
    protected void setColumnStyles() {
    	for (int c = 0; c < controller.getColumnCount(); c++) {
    		grid.getColumnFormatter().setStyleName(c, controller.getColumnAt(c).getStyle());
    	}
    }
    
    protected void layoutHeader() {
        if (controller.hasHeader()) {
            UIObject.setVisible(inner.getWidgetContainerElement(header), true);
            header.setVisible(true);
            header.layout();
            inner.setWidgetTopHeight(header,0, Unit.PX,header.getWidget().getElement().getOffsetHeight(),Unit.PX);
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
				header.getElement().getStyle().setProperty("left",-scrollView.getHorizontalScrollPosition(),Unit.PX);
			}
		});
    }
    
    protected void createRow(int row) {
        grid.insertRow(row);
        for(int i = 0; i < controller.getColumnCount(); i++) {
        	grid.insertCell(row,i);
        }
        grid.getRowFormatter().getElement(row).setAttribute("height", controller.getRowHeight()+"px");
        grid.getRowFormatter().getElement(row).setAttribute("index", "" + row);
    }

    public void renderView(int startRow, int endRow) {
        controller.finishEditing();

        startRow = startRow > 0 ? startRow : 0;
        endRow =  endRow > 0 ? endRow : controller.getRowCount() - 1;

        for (int row = startRow;  row <= endRow; row++ ) {
            if (row >= grid.getRowCount()) {
                createRow(grid.getRowCount());
            }
            renderRow(row);
            applyRowStyle(row);
        }
        removeExtraRows();
        adjustTableWidthForScrollbar();
    }
    
    protected void removeExtraRows() {
    	 while (grid.getRowCount() > controller.getRowCount()) {
             grid.removeRow(controller.getRowCount());
    	 }
    }
    
    public void bulkRender() {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        for (int row = 0; row < controller.getRowCount(); row++) {
        	bulkRenderRow(builder,row);
        }
        setRenderedHtml(builder.toSafeHtml());
        adjustViewPort();
    }
    
    protected void setRenderedHtml(SafeHtml html) {
    	grid.getElement().getElementsByTagName("tbody").getItem(0).setInnerSafeHtml(html);
    }
    
    protected void bulkRenderRow(SafeHtmlBuilder builder, int row) {
    	String style = null;
    	if (controller.getRowAt(row) instanceof Row) {
    		style = ((Row)controller.getRowAt(row)).getStyle(row);
    	}
    	builder.appendHtmlConstant("<tr height='"+controller.getRowHeight()+"px' index='"+row+"'" + (style != null ? " class='"+style+"'>" : ">"));
    	for(int col = 0; col < controller.getColumnCount(); col++) {
    		bulkRenderCell(builder, row, col);
    	}
    	builder.appendHtmlConstant("</tr>");
    }
    
    protected abstract void bulkRenderCell(SafeHtmlBuilder builder, int row, int col);

    protected void addRow(int r) {
        createRow(r);
        for (int c = 0; c < controller.getColumnCount(); c++) {
            renderCell(r, c);
        }
        applyRowStyle(r);
        adjustTableWidthForScrollbar();
    }

    public void addColumn(int c) {
        for (int i = 0; i < grid.getRowCount(); i++ ) {
            grid.insertCell(i, c);
        }
        layout();
        for (int i = 0; i < grid.getRowCount(); i++ ) {
            renderCell(i, c);
        }
    }

    public void removeColumn(int c) {
        for (int i = 0; i < grid.getRowCount(); i++ ) {
            grid.removeCell(i, c);
        }
        layout();
    }

    protected void removeRow(int r) {
        grid.removeRow(r);
        adjustTableWidthForScrollbar();
    }

    public void removeAllRows() {
        grid.removeAllRows();
    }

    protected void renderSelections(int start, int end) {
        for (int r = start; r <= end; r++ ) {
            if (controller.isRowSelected(r))
                grid.getRowFormatter().addStyleName(r, css.Selection());
            else
                grid.getRowFormatter().removeStyleName(r, css.Selection());
        }
    }

    public void renderExceptions(int start, int end) {
        start = start > 0 ? start : 0;
        end = end > 0 ? end : grid.getRowCount() - 1;
        for (int r = start; r <= end; r++ ) {
        	renderRowExceptions(r);
        }
    }
    
    protected void renderRowExceptions(int r) {
        for (int c = 0; c < controller.getColumnCount(); c++ ) {
            if (controller.hasExceptions(r, c)) {
            	renderCellException(r,c);
            } else {
            	clearCellException(r,c);
            }
        }
    }
    
    protected void renderCellException(int r, int c) {
    	 grid.getCellFormatter().addStyleName(r, c, getExceptionStyle(r, c));
    	 grid.addCellMouseOverHandler(mouseOver, r, c);
    }
    
    protected void clearCellException(int r, int c) {
    	 grid.getCellFormatter().removeStyleName(r, c, css.InputWarning());
         grid.getCellFormatter().removeStyleName(r, c, css.InputError());
         grid.removeHandler(r, c);
    }
    
    protected String getExceptionStyle(int r, int c) {
    	return Balloon.isWarning(controller.getEndUserExceptions(r, c), 
    							 controller.getValidateExceptions(r, c)) ? css.InputWarning() : css.InputError(); 
    }

    /**
     * This method will apply either a style that is set in the Row getStyle
     * method or the selection style if the row is selected
     */
    protected void applyRowStyle(int r) {
        String style;

        if (r >= controller.getRowCount())
            return;

        if (controller.getRowAt(r) instanceof Row) {
        	style = ((Row)controller.getRowAt(r)).getStyle(r);
        	if (style != null)
        		grid.getRowFormatter().setStyleName(r, style);
        }

        if (controller.isRowSelected(r))
            grid.getRowFormatter().addStyleName(r, css.Selection());
        else
            grid.getRowFormatter().removeStyleName(r, css.Selection());
    }

    /**
     * Applies the selection style to a table row
     * 
     * @param r
     */
    public void applySelectionStyle(int r) {
    	grid.getRowFormatter().addStyleName(r, css.Selection());
    }

    /**
     * Removes the Selection style from a table row
     * 
     * @param r
     */
    public void applyUnselectionStyle(int r) {
    	grid.getRowFormatter().removeStyleName(r, css.Selection());
    }

    protected void renderRow(int r) {
    	 for (int c = 0; c < controller.getColumnCount(); c++ )
             renderCell(r, c);
    }
    
    protected abstract void renderCell(int r, int c);


    public <V> void bulkExceptions(HashMap<V,HashMap<Integer, ArrayList<Exception>>> exceptions) {
        for(V row : exceptions.keySet()) {
            int r = controller.<V>getModel().indexOf(row);
            for(int c : exceptions.get(row).keySet()) {
            	renderCellException(r,c);
            }
        }
    }
    
    public <V> void startEditing(int r,int c, V value, NativeEvent event) {
    
        if (controller.getQueryMode()) {
            controller.getColumnAt(c)
                 .getCellEditor()
                 .startEditing(grid.getCellFormatter().getElement(r,c),(QueryData)controller.getValueAt(r, c), event);
        } else
            controller.getColumnAt(c)
                 .getCellEditor()
                 .startEditing(grid.getCellFormatter().getElement(r, c),value, event);
    }

    protected Object finishEditing(int c) throws ValidationErrorsList {
        return controller.getColumnAt(c).getCellEditor().finishEditing();
    }

    /**
     * Method will scroll the table to make sure the passed row is included in
     * the view
     * 
     * @param r
     * @return
     */
    public boolean scrollToVisible(int r) {
    	int horizontalScrollPosition;
    	
        if (scrollView.getMaximumVerticalScrollPosition() == 0)
            return false;

        if (isRowVisible(r))
            return false;

        horizontalScrollPosition = scrollView.getHorizontalScrollPosition();

        grid.getRowFormatter().getElement(r).scrollIntoView();

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
        Element row = grid.getRowFormatter().getElement(r);
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
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			
			@Override
			public void execute() {
		        layout();
		        adjustViewPort();
			}
		});
    }

    /**
     * Returns the Header for this view
     * 
     * @return
     */
    public TableHeader getHeader() {
        return header;
    }

    /**
     * Returns the actual drawn row height on the screen
     */
    protected int getRowHeight() {
        return controller.getRowHeight();
    }

    public void setCSS(TableCSS css) {
        css.ensureInjected();

        for (int i = 0; i < grid.getRowCount(); i++ ) {
            if (grid.getRowFormatter().getStyleName(i).contains(this.css.Selection())) {
                grid.getRowFormatter().removeStyleName(i, this.css.Selection());
                grid.getRowFormatter().addStyleName(i, css.Selection());
            }
            for (int j = 0; j < grid.getCellCount(i); j++ ) {
                if (grid.getCellFormatter().getStyleName(i, j).contains(this.css.InputError())) {
                    grid.getCellFormatter().removeStyleName(i, j, this.css.InputError());
                    grid.getCellFormatter().addStyleName(i, j, css.InputError());
                }
                if (grid.getCellFormatter()
                             .getStyleName(i, j)
                             .contains(this.css.InputWarning())) {
                    grid.getCellFormatter().removeStyleName(i, j, this.css.InputWarning());
                    grid.getCellFormatter().addStyleName(i, j, css.InputWarning());
                }
            }
        }

        this.css = css;

        grid.setStyleName(css.Table());

    }

	public
    CellGrid grid() {
    	return grid;
    }

    ScrollPanel scrollView() {
        return scrollView;
    }

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
                		             CSSUtils.getAddedBorderHeight(controller.getElement()) + 
                		             "px");
            }
        }
    }

    private void adjustTableWidthForScrollbar() {
    	int width = controller.getOffsetWidth();
        if (scrollView.getMaximumVerticalScrollPosition() > 0)
        	width -= NativeVerticalScrollbar.getNativeScrollbarWidth();
        controller.setWidth(width+"px");
    }
    
    /*
     * This public method added for unit testing 
     */
    public String getCellDisplay(int row, int col) {
        return grid.getText(row, col);
    }
    
    public Widget getCellWidget(int row, int col) {
        return grid.getWidget(row, col);       
    }

	public Object finishEditing(int r, int c) throws ValidationErrorsList {
		return finishEditing(c);
	}

	public void resize() {
		adjustViewPort();
		
	}
	
	
}
