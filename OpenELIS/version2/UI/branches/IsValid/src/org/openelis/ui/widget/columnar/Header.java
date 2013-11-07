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
package org.openelis.ui.widget.columnar;

import org.openelis.ui.resources.TableCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.NativeHorizontalScrollbar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class will create a Header that is used for the Table Widget.
 * 
 * @author tschmidt
 * 
 */
public class Header extends FocusPanel {

    /**
     * Contains Header widgets and is the wrapped widget for this composite.
     */
    protected FlexTable flexTable;
    
    /**
     * Reference to the Table this header is used for.
     */
    protected Columnar   columnar;
    
    /**
     * Popuppanel used to display the resize bar.
     */
    protected PopupPanel popResize;
    
    /**
     * Position where the resize started.
     */
    protected int        startX, resizeColumn, headerHeight = 16;
    
    /**
     * Widget that used to display a then position due to resizing.
     */
    protected FocusPanel bar;
    
    /**
     * The column that is being resized.
     */
    protected boolean    resizeColStyle;
        
    /**
     * Reference to this object to be used in anonymous handlers
     */
    protected Header     source = this;
    
    protected TableCSS   css;
    
    /**
     * Constructor that takes the containing table as a parameter
     * 
     * @param table
     */
    public Header(final Columnar clmnr) {
    	css = UIResources.INSTANCE.table();
    	css.ensureInjected();
    	    	
        this.columnar = clmnr;
        flexTable = new FlexTable();
        flexTable.setStyleName(css.Header());
        setWidget(flexTable);

        /*
         * Mouse handler for determining to allow resizing or filter based on 
         * mouse position
         */
        addMouseMoveHandler(new MouseMoveHandler() {
            public void onMouseMove(MouseMoveEvent event) {
                checkForResizeFilter(event.getX());
            }
        });

        /*
         * MouseDown handler for doing resize of columns in a table
         */
        addHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                /*
                 * Initial popResize and bar the first time a resize request
                 * is received
                 */
                if (popResize == null) {
                    popResize = new PopupPanel();
                    bar = new FocusPanel();
                    bar.setWidth("1px");
                    bar.setHeight((columnar.view.scrollView.getOffsetHeight() + getOffsetHeight())+"px");
                    DOM.setStyleAttribute(bar.getElement(), "background", "red");
                    popResize.add(bar);
                    /*
                     * Move resize bar if mouse moved
                     */
                    bar.addMouseMoveHandler(new MouseMoveHandler() {
                        public void onMouseMove(MouseMoveEvent event) {
                            
                            DataItem column = columnar.getDataItemAt(resizeColumn);
                            
                            if(column.getWidth() + ((popResize.getAbsoluteLeft()+event.getX()) - startX) < column.getMinWidth()) 
                                return;
                            
                            popResize.setPopupPosition(popResize.getAbsoluteLeft() + event.getX(),
                                                       popResize.getAbsoluteTop());
                        }
                    });

                    /*
                     * Calculate new column size by comparing the startX to the last position of the 
                     * resize bar.
                     */
                    bar.addMouseUpHandler(new MouseUpHandler() {
                        public void onMouseUp(MouseUpEvent event) {
                            DataItem column;
                            int newColWidth;

                            column = columnar.getDataItemAt(resizeColumn);
                            newColWidth = column.getWidth() + (popResize.getAbsoluteLeft() - startX);
                            
                            /*
                             * Column will call table.resize() in the call to setWidth
                             */
                            column.setWidth(newColWidth);

                            if (popResize != null)
                                popResize.hide();
                            
                            DOM.releaseCapture(bar.getElement());

                        }
                    });
                }

                /*
                 * Calc the start position of the resize bar
                 */
                startX = columnar.getXForColumn(resizeColumn) +
                         columnar.getDataItemAt(resizeColumn).getWidth() - 1 + getAbsoluteLeft();
                
                popResize.setPopupPosition(startX, ((Widget)event.getSource()).getAbsoluteTop());
                popResize.show();
                if(columnar.view.scrollView.getMaximumHorizontalScrollPosition() > 0)
                    bar.setHeight(columnar.view.scrollView.getOffsetHeight() + getOffsetHeight() - NativeHorizontalScrollbar.getNativeScrollbarHeight()+"px");
                /*
                 * We set the capture of mouse events now to the resize bar itself.  This allows us
                 * to simplify the logic of dragging the bar, as well as provide smoother dragging and 
                 * allows the mouse to float outside of the header and still move the resize bar
                 */
                DOM.setCapture(bar.getElement());
               
            }
        }, MouseDownEvent.getType());
        
        layout();
    }

    /**
     * Method to draw the Header based on values set in the Columns of the
     * table.
     */
    protected void layout() {
        
        if (flexTable.getRowCount() < 1)
            flexTable.insertRow(0);
        
        renderView(-1,-1);
        

        flexTable.setWidth(columnar.getTotalColumnWidth() + "px");
        flexTable.getCellFormatter().setHeight(0, 0, headerHeight+"px");
        
        flexTable.getCellFormatter().addStyleName(0, 0, css.First());
    }
    
    /**
     * This method will render each Header cell in the header.  Pass a range of Header cells to render or pass -1,-1 
     * render all Header cells
     * @param start
     * @param end
     */
    protected void renderView(int start, int end) {
        DataItem column;
        String header;
        
        if(start < 0)
            start = 0;
        
        if(end < 0)
            end = columnar.getDataItemCount() - 1;
        
        for (int i = start; i <= end; i++ ) {
            column = columnar.getDataItemAt(i);
            if(column.getLabel() !=  null)
            	header = column.getLabel().replaceAll("\\n", "<br/>");
            else
            	header = "";
            flexTable.setHTML(0, i, header);
            flexTable.getColumnFormatter().setWidth(i, column.getWidth() + "px");
            flexTable.getCellFormatter().setVerticalAlignment(0, i, HasVerticalAlignment.ALIGN_BOTTOM);
        }
        
        while(flexTable.getCellCount(0)  > columnar.getDataItemCount())
        	flexTable.removeCell(0, flexTable.getCellCount(0) );
    }

    /**
     * Method to set the height of the header.
     * @param height
     */
    protected void setHeaderHeight(int height) {
        headerHeight = height;
    }
    
    /**
     * Resizes the header to the new column widths
     */
    protected void resize() {
        DataItem col;

        for (int i = 0; i < columnar.getDataItemCount(); i++ ) {
            col = columnar.getDataItemAt(i);
            flexTable.getColumnFormatter().setWidth(i, col.getWidth() + "px");
        }

        flexTable.setWidth(columnar.getTotalColumnWidth() + "px");
    }

    /**
     * Method to determine if the resize cursor or filter button should be shown based on
     * the cursor position in the header 
     * @param x
     */
    private void checkForResizeFilter(int x) {
        int col1, col2;

        col1 = columnar.getColumnForX(x - 4);
        col2 = columnar.getColumnForX(x + 4);

        if (col1 != col2 && col1 >= 0) {
            if (columnar.getDataItemAt(col1).isResizable()) {
                flexTable.getCellFormatter().addStyleName(0, col1, css.ResizeCol());
                if(col2 > -1)
                	flexTable.getCellFormatter().addStyleName(0, col2, css.ResizeCol());
                resizeColStyle = true;
                resizeColumn = col1;
                sinkEvents(Event.ONMOUSEDOWN);
                return;
            }
        } 

        if (resizeColStyle && col1 > -1) {
            flexTable.getCellFormatter().removeStyleName(0, col1, css.ResizeCol());
            flexTable.getCellFormatter().removeStyleName(0, col2, css.ResizeCol());
            resizeColStyle = false;
            unsinkEvents(Event.ONMOUSEDOWN);
        }
    }
    
}
