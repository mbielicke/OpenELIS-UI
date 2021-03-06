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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

import org.openelis.ui.common.Util;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.TableCSS;
import org.openelis.ui.widget.Balloon;
import org.openelis.ui.widget.Balloon.Options;
import org.openelis.ui.widget.Balloon.Placement;
import org.openelis.ui.widget.CSSUtils;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.HasBalloon;
import org.openelis.ui.widget.HasExceptions;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.ScreenWidgetInt;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.BeforeRowAddedEvent;
import org.openelis.ui.widget.table.event.BeforeRowAddedHandler;
import org.openelis.ui.widget.table.event.BeforeRowDeletedEvent;
import org.openelis.ui.widget.table.event.BeforeRowDeletedHandler;
import org.openelis.ui.widget.table.event.CellClickedEvent;
import org.openelis.ui.widget.table.event.CellClickedHandler;
import org.openelis.ui.widget.table.event.CellDoubleClickedEvent;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.CellMouseOverEvent;
import org.openelis.ui.widget.table.event.FilterEvent;
import org.openelis.ui.widget.table.event.FilterHandler;
import org.openelis.ui.widget.table.event.HasBeforeCellEditedHandlers;
import org.openelis.ui.widget.table.event.HasBeforeRowAddedHandlers;
import org.openelis.ui.widget.table.event.HasBeforeRowDeletedHandlers;
import org.openelis.ui.widget.table.event.HasCellClickedHandlers;
import org.openelis.ui.widget.table.event.HasCellEditedHandlers;
import org.openelis.ui.widget.table.event.HasFilterHandlers;
import org.openelis.ui.widget.table.event.HasRowAddedHandlers;
import org.openelis.ui.widget.table.event.HasRowDeletedHandlers;
import org.openelis.ui.widget.table.event.HasUnselectionHandlers;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used by screens and widgets such as AutoComplete and Dropdown
 * to display information in a Table grid format
 * 
 * @author tschmidt
 * 
 */
public class Table extends FocusPanel implements ScreenWidgetInt, Queryable,
                                     HasBeforeSelectionHandlers<Integer>,
                                     HasSelectionHandlers<Integer>,
                                     HasUnselectionHandlers<Integer>, HasBeforeCellEditedHandlers,
                                     RequiresResize, HasCellEditedHandlers,
                                     HasBeforeRowAddedHandlers, HasRowAddedHandlers,
                                     HasBeforeRowDeletedHandlers, HasRowDeletedHandlers,
                                     HasCellClickedHandlers, HasValue<ArrayList<? extends Row>>,
                                     HasExceptions, Focusable, FocusHandler, HasFilterHandlers, HasBalloon {

    /**
     * Cell that is currently being edited.
     */
    protected int                         editingRow = -1, editingCol = -1;

    /**
     * Table dimensions
     */
    protected int                         rowHeight, visibleRows = 10, viewWidth = -1,
                    totalColumnWidth;

    /**
     * Model used by the Table
     */
    protected ArrayList<Row>              model, modelView, modelSort;
    protected HashMap<Object, RowIndexes> rowIndex;
    
    protected Timer                       balloonTimer;

    /**
     * Columns used by the Table
     */
    protected ArrayList<Column>           columns;

    /**
     * List of selected Rows by index in the table
     */
    protected ArrayList<Integer>          selections = new ArrayList<Integer>(5);

    /**
     * Exception lists for the table
     */
    protected HashMap<Row, HashMap<Integer, ArrayList<Exception>>> endUserExceptions,
                    validateExceptions;

    /**
     * Table state values
     */
    protected boolean                                              enabled, multiSelect, editing,
                    hasFocus, queryMode, hasHeader, hasMenu, unitTest, fixScrollBar = true, ctrlDefault;

    /**
     * Enum representing the state of when the scroll bar should be shown.
     */
    public enum Scrolling {
        ALWAYS, AS_NEEDED, NEVER
    };

    /**
     * Fields to hold state of whether the scroll bars are shown
     */
    protected Scrolling           verticalScroll, horizontalScroll;

    /**
     * Reference to the View composite for this widget.
     */
    protected ViewInt             view;

    /**
     * Arrays for determining relative X positions for columns
     */
    protected short[]             xForColumn, columnForX;

    /**
     * Drag and Drop controllers
     */
    protected TableDragController dragController;
    protected TableDropController dropController;

    protected HandlerRegistration visibleHandler;
    
    protected CellTipProvider    tipProvider;
    
    protected Options     toolTip;

    /**
     * Indicates direction for the Sort
     */
    public static final int       SORT_ASCENDING = 1, SORT_DESCENDING = -1;

    protected Logger              logger         = Logger.getLogger("Widget");
    
    protected Table source = this;
    
    protected int tipRow, tipCol;

    public static class Builder {
        final int         visibleRows;
        int               rowHeight        = 16;
        Integer           width;
        boolean           multiSelect, hasHeader, hasMenu, fixScroll = true;
        Scrolling         verticalScroll   = Scrolling.ALWAYS;
        Scrolling         horizontalScroll = Scrolling.ALWAYS;
        ArrayList<Column> columns          = new ArrayList<Column>(5);

        public Builder(int visibleRows) {
            this.visibleRows = visibleRows;
        }

        public Builder rowHeight(int rowHeight) {
            this.rowHeight = rowHeight;
            return this;
        }

        public Builder multiSelect(boolean multiSelect) {
            this.multiSelect = multiSelect;
            return this;
        }

        public Builder verticalScroll(Scrolling vertical) {
            this.verticalScroll = vertical;
            return this;
        }

        public Builder horizontalScroll(Scrolling horizontal) {
            this.horizontalScroll = horizontal;
            return this;
        }

        public Builder hasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
            return this;
        }
        
        public Builder hasMenu(boolean hasMenu) {
        	this.hasMenu = hasMenu;
        	return this;
        }

        public Builder column(Column col) {
            columns.add(col);
            return this;
        }

        public Builder width(Integer width) {
            this.width = width;
            return this;
        }

        public Builder fixScrollbar(boolean fixScroll) {
            this.fixScroll = fixScroll;
            return this;
        }

        public Table build() {
            return new Table(this);
        }
    }

    public Table() {
        rowHeight = 16;
        fixScrollBar = true;
        multiSelect = false;
        columns = new ArrayList<Column>(5);
        view = new StaticView(this);
        setWidget(view);
        setKeyHandling();
    }

    public Table(Builder builder) {

        rowHeight = builder.rowHeight;
        visibleRows = builder.visibleRows;
        multiSelect = builder.multiSelect;
        verticalScroll = builder.verticalScroll;
        horizontalScroll = builder.horizontalScroll;
        fixScrollBar = builder.fixScroll;
        hasHeader = builder.hasHeader;
        hasMenu = builder.hasMenu;
        view = new StaticView(this);
        setWidget(view);

        if (builder.width != null)
            setWidth(builder.width.intValue());

        setColumns(builder.columns);
        setKeyHandling();
    }
    
    public void setInfiniteView() {
        view = new InfiniteView(this);
        setWidget(view);
    }
 
    private void setKeyHandling() {
        /*
         * This Handler takes care of all key events on the table when editing
         * and when only selection is on
         */
        addDomHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                int row, col, keyCode;

                if ( !isEnabled())
                    return;
                keyCode = event.getNativeEvent().getKeyCode();
                row = editingRow;
                col = editingCol;

                if (isEditing() && getColumnAt(col).getCellEditor().ignoreKey(keyCode))
                    return;

                switch (keyCode) {
                    case (KeyCodes.KEY_TAB):

                        // Ignore if no cell is currently being edited
                        if ( !editing)
                            break;

                        // Tab backwards if shift pressed otherwise tab forward
                        if ( !event.isShiftKeyDown()) {
                            while (true) {
                                col++ ;
                                if (col >= getColumnCount()) {
                                    col = 0;
                                    row++ ;
                                    if (row >= getRowCount()) {
                                        setFocus(true);
                                        break;
                                    }

                                }
                                if (startEditing(row, col, event.getNativeEvent())) {
                                    event.preventDefault();
                                    event.stopPropagation();
                                    break;
                                }
                            }
                        } else {
                            while (true) {
                                col-- ;
                                if (col < 0) {
                                    col = getColumnCount() - 1;
                                    row-- ;
                                    if (row < 0) {
                                        setFocus(true);
                                        break;
                                    }
                                }
                                if (startEditing(row, col, event.getNativeEvent())) {
                                    event.preventDefault();
                                    event.stopPropagation();
                                    break;
                                }
                            }
                        }

                        break;
                    case (KeyCodes.KEY_DOWN):
                        // If Not editing select the next row below the current
                        // selection
                        if ( !isEditing()) {
                            if (isAnyRowSelected()) {
                                row = getSelectedRow();
                                while (true) {
                                    row++ ;
                                    if (row >= getRowCount())
                                        break;

                                    selectRowAt(row, event.getNativeEvent());
                                    
                                    if (isRowSelected(row))
                                        break;
                                }
                            }
                            break;
                        }
                        // If editing set focus to the same col cell in the next
                        // selectable row below
                        while (true) {
                            row++ ;
                            if (row >= getRowCount())
                                break;
                            if (startEditing(row, col, event.getNativeEvent())) {
                                event.stopPropagation();
                                event.preventDefault();
                                break;
                            }
                        }
                        break;
                    case (KeyCodes.KEY_UP):
                        // If Not editing select the next row above the current
                        // selection
                        if ( !isEditing()) {
                            if (isAnyRowSelected()) {
                                row = getSelectedRow();
                                while (true) {
                                    row-- ;
                                    if (row < 0)
                                        break;

                                    selectRowAt(row, event.getNativeEvent());

                                    if (isRowSelected(row))
                                        break;
                                }
                            }
                            break;
                        }
                        // If editing set focus to the same col cell in the next
                        // selectable row above
                        while (true) {
                            row-- ;
                            if (row < 0)
                                break;
                            if (startEditing(row, col, event.getNativeEvent())) {
                                event.stopPropagation();
                                event.preventDefault();
                                break;
                            }
                        }
                        break;
                    case (KeyCodes.KEY_ENTER):
                        // If editing just finish and return
                        if (isEditing()) {
                            finishEditing();
                            return;
                        }
                    
                        if(getRowCount() == 0)
                            return;

                        // If not editing and a row is selected, focus on first
                        // editable cell
                        if ( !isAnyRowSelected())
                            row = 0;
                        else
                            row = getSelectedRow();
                        col = 0;
                        while (col < getColumnCount()) {
                            if (startEditing(row, col, event.getNativeEvent()))
                                break;
                            col++ ;
                        }
                        break;
                }
            }
        }, KeyDownEvent.getType());

        addDomHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                //removeStyleName(UIResources.INSTANCE.text().Focus());
            }
        }, BlurEvent.getType());

        addDomHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                //addStyleName(UIResources.INSTANCE.text().Focus());
            }
        }, FocusEvent.getType());
    }

    // ********* Table Definition Methods *************
    /**
     * Returns the currently used Row Height for the table layout
     */
    public int getRowHeight() {
        return rowHeight;
    }
    
    public int getCellHeight() {
        return view.rowHeight();
    }

    /**
     * Sets the Row Height to be used in the table layout.
     * 
     * @param rowHeight
     */
    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
        //view.firstAttach = true;
        layout();
    }

    /**
     * Returns how many physical rows are used in the table layout.
     * 
     * @return
     */
    public int getVisibleRows() {
        return visibleRows;
    }

    /**
     * Sets how many physical rows are used in the table layout.
     * 
     * @param visibleRows
     */
    public void setVisibleRows(int visibleRows) {
        this.visibleRows = visibleRows;
        layout();
    }

    /**
     * Returns the data model currently being displayed by this table. The
     * return value is parameterized so specific models can be used that extend
     * the basic Row such as Item in AutoCompete and Dropdown
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Row> ArrayList<T> getModel() {
        return (ArrayList<T>)model;
    }

    /**
     * Sets the data model to be displayed by this table. The model parameter is
     * parameterized so specific models can be used that extend the basic Row
     * such as Item in AutoCompete and Dropdown
     * 
     * @param model
     */
    @SuppressWarnings("unchecked")
    public void setModel(ArrayList<? extends Row> model) {
        finishEditing();
        unselectAll();
        
        this.model = (ArrayList<Row>)model;
        modelView = this.model;
        rowIndex = null;
        
        checkExceptions();

        // Clear any filter choices that may have been in force before model
        // changed
        for (Column col : columns) {
            if (col.getFilter() != null)
                col.getFilter().unselectAll();
        }
        
        if(queryMode) {
            renderView(-1,1);
        }else{
            ((StaticView)view).bulkRender();
        
            if(endUserExceptions != null) 
                ((StaticView)view).bulkExceptions(endUserExceptions);
        }
        

    }

    /**
     * This method will pull all filters in force from the columns and apply
     * them to the table model.
     */
    public void applyFilters() {
        ArrayList<Filter> filters;

        filters = new ArrayList<Filter>();
        for (Column col : columns) {
            if (col.getFilter() != null && col.isFiltered)
                filters.add(col.getFilter());
        }

        applyFilters(filters);

        fireFilterEvent();
    }

    /**
     * This method will filter the table by the filter list that is passed as
     * param
     */
    public void applyFilters(ArrayList<Filter> filters) {
        boolean include;

        if (model == null)
            return;

        finishEditing();

        /*
         * if no filters are in force revert modelView back to model and return;
         */
        if (filters == null || filters.size() == 0) {
            modelView = model;
            rowIndex = null;
            ((StaticView)view).bulkRender();
            
            if(hasExceptions()) 
                ((StaticView)view).bulkExceptions(endUserExceptions);
            
            if(isAnyRowSelected()) {
                for(Integer index : selections) 
                    ((StaticView)view).applySelectionStyle(index);
            }
            
            return;
        }

        /*
         * Reset the modelView and the rowIndex hash
         */
        modelView = new ArrayList<Row>();
        rowIndex = new HashMap<Object, RowIndexes>();
        for (int i = 0; i < model.size(); i++ )
            rowIndex.put(model.get(i), new RowIndexes(i, -1));

        /*
         * Run through model and filter out rows
         */
        for (int i = 0; i < model.size(); i++ ) {
            include = true;
            for (Filter filter : filters) {
                if (filter != null && filter.isFilterSet() &&
                    !filter.include(model.get(i).getCell(filter.getColumn()))) {
                    include = false;
                    break;
                }
            }
            if (include) {
                modelView.add(model.get(i));
                rowIndex.get(model.get(i)).view = modelView.size() - 1;
            }
        }

        /*
         * If no rows were filtered reset the modelView back to model
         */
        if (modelView.size() == model.size()) {
            modelView = model;
            rowIndex = null;
        }

        // if ( !scrollToVisible(0))
        ((StaticView)view).bulkRender();
        
        if(hasExceptions()) 
            ((StaticView)view).bulkExceptions(endUserExceptions);
        
        if(isAnyRowSelected()) {
            for(Integer index : selections) 
                ((StaticView)view).applySelectionStyle(convertModelIndexToView(index));
        }
    }

    /**
     * This method will take the passed view index and return the corresponding
     * original model index of the row.
     * 
     * @param index
     * @return
     */
    public int convertViewIndexToModel(int index) {
        int i = index;

        if (rowIndex != null && index >= 0)
            i = rowIndex.get(modelView.get(index)).model;

        return i;
    }
    
    public int convertModelIndexToView(int modelIndex) {
    	if (rowIndex != null && modelIndex >=0) {
    		return convertModelIndexToView(model.get(modelIndex));
    	}
    	return modelIndex;
    }

    /**
     * This method will take the passed model index of a row and return the
     * corresponding view index for the row. If the model row is currently not
     * in the view then the a value of -1 will be returned.
     * 
     * @param modelIndex
     * @return
     */
    public int convertModelIndexToView(Row row) {
    	int i = -1;
        RowIndexes rowInd;

        if (rowIndex != null) {
            rowInd = rowIndex.get(row);
            if (rowInd != null) {
                i = rowInd.view;
            }
        } else {
        	i = model.indexOf(row);
        }

        return i;
    }

    /**
     * This method will adjust the RowIndexes when a row is added to or removed
     * from the table when a view is applied.
     * 
     * @param modelIndex
     * @param row
     * @param adj
     */
    private void adjustRowIndexes(int modelIndex, int row, int adj) {
        RowIndexes r;

        if (rowIndex == null)
            return;

        for (int i = row; i < modelView.size(); i++ )
            rowIndex.get(modelView.get(i)).view += adj;

        for (int i = modelIndex; i < model.size(); i++ ) {
            r = rowIndex.get(model.get(i));
            if (r != null)
                r.model += adj;
        }

        for (int i = 0; i < selections.size(); i++ ) {
            if (selections.get(i) >= row)
                selections.set(i, selections.get(i) + adj);
        }
    }

    /**
     * This method will apply the passed sort and sort direction passed to the
     * table model.
     * 
     * @param sort
     * @param desc
     */
    public void applySort(int col, int dir, Comparator<? super Row> comp) {
        /*
         * Setup the modelView as its own object if not already
         */
        if (modelView == model) {
            modelView = new ArrayList<Row>();
            rowIndex = new HashMap<Object, RowIndexes>();
            for (int i = 0; i < model.size(); i++ ) {
                modelView.add(model.get(i));
                rowIndex.put(model.get(i), new RowIndexes(i, -1));
            }
        }

        Collections.sort(modelView, new Sort<Row>(col, dir, comp));

        /*
         * Set the view index of the hash based on the sort
         */
        for (int i = 0; i < modelView.size(); i++ )
            rowIndex.get(modelView.get(i)).view = i;

        ((StaticView)view).bulkRender();
        
        if(hasExceptions()) 
            ((StaticView)view).bulkExceptions(endUserExceptions);
        
        if(isAnyRowSelected()) {
            for(Integer index : selections) 
                ((StaticView)view).applySelectionStyle(convertModelIndexToView(index));
        }
    }

    /**
     * Returns the current size of the held model. Returns zero if a model has
     * not been set.
     * 
     * @return
     */
    public int getRowCount() {
        if (modelView == null)
            return 0;

        try {
            return modelView.size();
        }catch(Exception e) {
            return 0;
        }
    }

    /**
     * Used to determine the table has more than one row currently selected.
     * 
     * @return
     */
    public boolean isMultipleRowsSelected() {
        return selections.size() > 1;
    }

    /**
     * Used to determine if the table currently allows multiple selection.
     * 
     * @return
     */
    public boolean isMultipleSelectionAllowed() {
        return multiSelect;
    }

    /**
     * Used to put the table into Multiple Selection mode.
     * 
     * @param multiSelect
     */
    public void setAllowMultipleSelection(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    /**
     * Returns the current Vertical Scrollbar view rule set.
     * 
     * @return
     */
    public Scrolling getVerticalScroll() {
        return verticalScroll;
    }

    /**
     * Sets the current Vertical Scrollbar view rule.
     * 
     * @param verticalScroll
     */
    public void setVerticalScroll(String verticalScroll) {
        this.verticalScroll = Scrolling.valueOf(verticalScroll);
        layout();

    }

    /**
     * Returns the current Horizontal Scrollbar view rule set
     * 
     * @return
     */
    public Scrolling getHorizontalScroll() {
        return horizontalScroll;
    }

    /**
     * Sets the current Horizontal Scrollbar view rule.
     * 
     * @param horizontalScroll
     */
    public void setHorizontalScroll(String horizontalScroll) {
        this.horizontalScroll = Scrolling.valueOf(horizontalScroll);
        layout();
    }

    /**
     * Sets a flag to set the size of the table to always set room aside for
     * scrollbars defaults to true
     * 
     * @param fixScrollBar
     */
    public void setFixScrollbar(boolean fixScrollBar) {
        this.fixScrollBar = fixScrollBar;
    }

    /**
     * Returns the flag indicating if the table reserves space for the scrollbar
     * 
     * @return
     */
    public boolean getFixScrollbar() {
        return fixScrollBar;
    }

    /**
     * Sets the width of the table view
     * 
     * @param width
     */
    public void setWidth(int width) {
        this.viewWidth = width;
        layout();

    }

    /**
     * Method overridden from Composite to call setWidth(int) so that the width
     * can be adjusted.
     */
    @Override
    public void setWidth(String width) {
        setWidth(Util.stripUnits(width));
    }

    /**
     * Returns the currently set view width for the Table
     * 
     * @return
     */
    public int getWidth() {
        return viewWidth;
    }

    /**
     * Returns the view width of the table minus the the width of the scrollbar
     * if the scrollbar is visible or if space has been reserved for it
     * 
     * @return
     */
    protected int getWidthWithoutScrollbar() {
        if (viewWidth < 0 && totalColumnWidth == 0 && getParent() != null) {
            if(getParent() instanceof LayoutPanel)
                return ((LayoutPanel)getParent()).getWidgetContainerElement(this).getOffsetWidth() - CSSUtils.getAddedBorderWidth(getElement());
            else
                return getParent().getOffsetWidth() - CSSUtils.getAddedBorderWidth(getElement());
        }
        return (viewWidth == -1 ? totalColumnWidth : viewWidth) - CSSUtils.getAddedBorderWidth(getElement());
    }

    /**
     * Returns the width of the all the column widths added together which is
     * the physical width of the table
     * 
     * @return
     */
    public int getTotalColumnWidth() {
        return totalColumnWidth;
    }

    /**
     * Returns the number of columns used in this Table
     * 
     * @return
     */
    public int getColumnCount() {
        if (columns != null)
            return columns.size();
        return 0;
    }

    /**
     * Returns the column at the passed index
     * 
     * @param index
     * @return
     */
    public Column getColumnAt(int index) {
        return columns.get(index);
    }

    /**
     * Returns column by the name passed
     * 
     * @param index
     * @return
     */
    public int getColumnByName(String name) {
        for (int i = 0; i < columns.size(); i++ ) {
            if (columns.get(i).name.equals(name))
                return i;
        }
        return -1;
    }

    /**
     * This method can be used to determine the index of the column in the
     * display of the table
     * 
     * @param col
     * @return
     */
    public int getColumn(Column col) {
        return columns.indexOf(col);
    }

    /**
     * This method will replace the column at the passed index into the table
     * 
     * @param index
     * @param col
     */
    public void setColumnAt(int index, Column col) {
        col.setTable(this);
        columns.set(index, col);
        layout();
    }

    /**
     * This method will return the widget used to render/edit the cell contents
     * from the cell definition of the column.
     * 
     * @param index
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Widget> T getColumnWidget(int index) {
        return (T) (index > -1 ? getColumnAt(index).getCellEditor().getWidget() : null);
    }

    /**
     * This method will return the column used in the table by it's name
     * 
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Widget> T getColumnWidget(String name) {
        return (T)getColumnWidget(getColumnByName(name));
    }

    /**
     * Returns the X coordinate on the Screen of the Column passed.
     * 
     * @param index
     * @return
     */
    protected int getXForColumn(int index) {
        if (xForColumn != null && index >= 0 && index < xForColumn.length)
            return xForColumn[index];
        return -1;
    }

    /**
     * Returns the Column for the current mouse x position passed in the header
     * 
     * @param x
     * @return
     */
    protected int getColumnForX(int x) {
        if (columnForX != null && x >= 0 && x < columnForX.length)
            return columnForX[x];
        return -1;
    }

    /**
     * Sets whether the table as a header or not.
     */
    public void setHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    /**
     * Used to determine if table has header
     * 
     * @return
     */
    public boolean hasHeader() {
        return hasHeader;
    }
    
    public void setMenu(boolean hasMenu){
    	this.hasMenu = hasMenu;
    }
    
    public boolean hasMenu() {
    	return hasMenu;
    }

    /**
     * Sets the list columns to be used by this Table
     * 
     * @param columns
     */
    public void setColumns(ArrayList<Column> columns) {
        this.columns = columns;

        if (columns != null) {
            for (Column column : columns)
                column.setTable(this);
        }

        layout();
    }

    /**
     * Creates and Adds a Column at the end of the column list with passed name
     * and header label in the params.
     * 
     * @param name
     *        Name of the column for reference
     * @param label
     *        Label used in Table header.
     * @return The newly created and added column
     */
    public Column addColumn(String name, String label) {
        return addColumnAt(columns.size(), name, label, 75);
    }

    /**
     * Creates and adds a new column to the table.
     * 
     * @return
     */
    public Column addColumn() {
        return addColumn("", "");
    }

    public void addColumn(Column col) {
        addColumnAt(columns.size(), col);
    }

    /**
     * Creates and inserts a new Column int the table at the specified index
     * using the name and label passed.
     * 
     * @param index
     *        Index in the Column list where to insert the new Column
     * @param name
     *        Name used in the Column as a reference to the Column.
     * @param label
     *        Label used in the Table header.
     * @return The newly created and added Column.
     */
    public Column addColumnAt(int index, String name, String label, int width) {
        Column column;

        column = new Column.Builder(width).name(name).label(label).build();
        addColumnAt(index, column);
        column.setTable(this);
        return column;
    }

    /**
     * Creates and adds a new Column at passed index
     * 
     * @param index
     *        Index in the Column list where to insert the new Column.
     * @return The newly created and added column.
     */
    public Column addColumnAt(int index) {
        return addColumnAt(index, "", "", 75);
    }

    public void addColumnAt(int index, Column column) {
        columns.add(index, column);
        column.setTable(this);
        if (model != null) {
            for (Row row : model)
                row.cells.add(index, null);
        }
        computeColumnsWidth();
        view.addColumn(index);
    }

    /**
     * Removes the column in the table and passed index.
     * 
     * @param index
     */
    public Column removeColumnAt(int index) {
        Column col;

        col = columns.remove(index);
        if (model != null) {
            for (Row row : model)
                row.cells.remove(index);
        }
        computeColumnsWidth();
        view.removeColumn(index);

        return col;
    }

    /**
     * Removes all columns from the table.
     */
    public void removeAllColumns() {
        columns.clear();
        layout();
    }

    /**
     * Creates a new blank Row and adds it to the bottom of the Table model.
     * 
     * @return
     */
    public <T extends Row> T addRow() {
        return addRow(getRowCount(), null);
    }

    /**
     * Creates a new blank Row and inserts it in the table model at the passed
     * index.
     * 
     * @param index
     * @return
     */
    public <T extends Row> T addRowAt(int index) {
        return addRow(index, null);
    }

    /**
     * Adds the passed Row to the end of the Table model.
     * 
     * @param row
     * @return
     */
    public <T extends Row> T addRow(T row) {
        return addRow(getRowCount(), row);
    }

    /**
     * Adds the passed Row into the Table model at the passed index.
     * 
     * @param index
     * @param row
     * @return
     */
    public <T extends Row> T addRowAt(int index, T row) {
        return (T)addRow(index, row);
    }

    /**
     * Private method called by all public addRow methods to handle event firing
     * and add the new row to the model.
     * 
     * @param index
     *        Index where the new row is to be added.
     * @param row
     *        Will be null if a Table should create a new blank Row to add
     *        otherwise the passed Row will be added.
     * @return Will return null if this action is canceled by a
     *         BeforeRowAddedHandler, otherwise the newly created Row will be
     *         returned or if a Row is passed to the method it will echoed back.
     */
    @SuppressWarnings("unchecked")
    private <T extends Row> T addRow(int index, T row) {
        int modelIndex;

        finishEditing();

        if (row == null)
            row = (T)new Row(columns.size());

        if ( !fireBeforeRowAddedEvent(index, row))
            return null;

        /* if a model has not been set need to create an empty model */
        if (model == null)
            setModel(new ArrayList<Row>());

        /* Add row to model and then to view */
        if (rowIndex != null) {
            modelIndex = convertViewIndexToModel(index);
            model.add(modelIndex, row);
            rowIndex.put(row, new RowIndexes(modelIndex, index));
            adjustRowIndexes(modelIndex + 1, index, 1);
        }

        modelView.add(index, row);

        view.addRow(index);

        fireRowAddedEvent(index, row);

        return row;

    }

    /**
     * Method will delete a row from the model at the specified index and
     * refersh the view.
     * 
     * @param index
     * @return
     */
    public <T extends Row> T removeRowAt(int index) {
        int modelIndex;
        T row;

        finishEditing();

        unselectRowAt(index);
        
        row = getRowAt(index);

        if ( !fireBeforeRowDeletedEvent(index, row))
            return null;

        if(balloonTimer != null)
        	balloonTimer.cancel();
        
        if (rowIndex != null) {
            modelIndex = convertViewIndexToModel(index);
            model.remove(modelIndex);
            rowIndex.remove(row);
            adjustRowIndexes(modelIndex, index + 1, -1);
        }
        modelView.remove(index);

        view.removeRow(index);
        
        if(endUserExceptions != null) {
            endUserExceptions.remove(row);
            if(endUserExceptions.size() == 0)
                endUserExceptions = null;
        }
        
        
        if(validateExceptions != null) {
            validateExceptions.remove(row);
            if(validateExceptions.size() == 0)
                validateExceptions = null;
        }

        fireRowDeletedEvent(index, row);

        return row;
    }

    /**
     * Set the model for this table to null and redraws
     */
    public void removeAllRows() {
        finishEditing();
        unselectAll();
        model = null;
        modelView = null;
        rowIndex = null;
        view.removeAllRows();
        clearExceptions();
    }

    /**
     * Returns the Row at the specified index in the model
     * 
     * @param row
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Row> T getRowAt(int row) {
        if (row < 0 || row >= getRowCount())
            return null;
        return (T)modelView.get(row);
    }

    // ************ Selection Methods ***************

    /**
     * Returns an array of indexes of the currently selected row
     */
    public Integer[] getSelectedRows() {
        return selections.toArray(new Integer[] {});
    }
    
    public void setCtrlKeyDefault(boolean ctrl) {
        this.ctrlDefault = ctrl;
    }

    /**
     * Selects the row at the passed index. Selection can be canceled by a
     * BeforeSelecionHandler. If selection is allowed, then a SelectionEvent
     * will be fired to all registered handlers, and the selected row will be
     * scrolled in the visible view.
     * 
     * @param index
     */
    public void selectRowAt(int index) {
        selectRowAt(index, null);
    }

    /**
     * Selects the row at the passed index. Selection can be canceled by a
     * BeforeSelecionHandler. If selection is allowed, then a SelectionEvent
     * will be fired to all registered handlers, and the selected row will be
     * scrolled in the visible view. If the table allows multiple selection the
     * row will be added to the current list of selections.
     * 
     * @param index
     */
    protected void selectRowAt(int row, NativeEvent event) {
        
        if(row < 0) {
            unselectAll();
            return;
        }
        
        /*
         * If multiple selection is allowed check event for ctrl or shift keys.
         * If none apply the logic will fall throw to normal selection.
         */
        if (isMultipleSelectionAllowed()) {
            if (ctrlDefault || (event != null && Event.getTypeInt(event.getType()) == Event.ONCLICK)) {
                multiSelect(row,event);
                return;
            }
        }   
        
        if(isRowSelected(row))
            return;
       
        if (event == null || fireBeforeSelectionEvent(row)) {
            unselectAll();
            
            finishEditing();

            selections.add(row);

            view.applySelectionStyle(row);

            if (event != null)
                   fireSelectionEvent(row);
            
            scrollToVisible(row);
        }

    }
    
    private void multiSelect(int row, NativeEvent event) {
        int startSelect, endSelect, minSelected, maxSelected, i;
        boolean ctrlKey, shiftKey, selected = false;
        
        startSelect = row;
        endSelect = row;
        
        ctrlKey = ctrlDefault ? ctrlDefault : event.getCtrlKey();
        shiftKey = event != null ? event.getShiftKey() : false;
        
        if (ctrlKey) {
            if (isRowSelected(row)) {
                unselectRowAt(row, event);
                return;
            }
        } else if (shiftKey) {
            if ( !isAnyRowSelected()) {
                startSelect = 0;
                endSelect = row;
            } else {
                Collections.sort(selections);
                minSelected = Collections.min(selections);
                maxSelected = Collections.max(selections);
                if (minSelected > row) {
                    startSelect = row;
                    endSelect = minSelected;
                } else if (row > maxSelected) {
                    startSelect = maxSelected;
                    endSelect = row;
                } else {
                    i = 0;
                    while (selections.get(i + 1) < row)
                        i++ ;
                    startSelect = selections.get(i);
                    endSelect = row;
                }
            }
            unselectAll(event);
        }else
            unselectAll(event);
        
        for (i = startSelect; i <= endSelect && i > -1; i++ ) {
            if ( !selections.contains(i)) {
                if (event == null || fireBeforeSelectionEvent(i)) {

                    selected = true;

                    finishEditing();

                    selections.add(i);

                    view.applySelectionStyle(i);

                    if (event != null)
                        fireSelectionEvent(i);
                }
            }
        }
        
        if (selected)
            scrollToVisible(endSelect);
    }

    /**
     * This method will select all rows in the table if the table allows
     * Multiple Selection at the time it is called. No selections events will be
     * fired.
     */
    public void selectAll() {
        if (isMultipleSelectionAllowed()) {
            selections = new ArrayList<Integer>();
            for (int i = 0; i < getRowCount(); i++ ) {
                selections.add(i);
                ((StaticView)view).applySelectionStyle(i);
            }
        }
    }

    /**
     * Unselects the row from the selection list. This method does nothing if
     * the passed index is not currently a selected row, otherwise the row will
     * be unselected.
     * 
     * @param index
     */
    public void unselectRowAt(int index) {
        unselectRowAt(index, null);
    }

    /**
     * Unselects the row from the selection list. This method does nothing if
     * the passed index is not currently a selected row, otherwise the row will
     * be unselected and an UnselectEvent will be fired to all registered
     * handlers
     * 
     * @param index
     */
    protected void unselectRowAt(int index, NativeEvent event) {

        if (selections.contains(index)) {
            finishEditing();
            if (event != null)
                fireUnselectEvent(index);
            selections.remove(new Integer(index));
            view.applyUnselectionStyle(index);
        }
    }

    public void unselectAll() {
        unselectAll(null);
    }

    /**
     * Clears all selections from the table.
     */
    protected void unselectAll(NativeEvent event) {
        int count = selections.size();
        for (int i = 0; i < count; i++ )
            unselectRowAt(selections.get(0), event);

    }

    /**
     * Returns the selected index of the first row selected
     * 
     * @return
     */
    public int getSelectedRow() {
        return selections.size() > 0 ? selections.get(0) : -1;
    }

    /**
     * Used to determine if the passed row index is currently in the selection
     * list.
     * 
     * @param index
     * @return
     */
    public boolean isRowSelected(int index) {
        return selections.contains(index);
    }

    /**
     * Used to determine if any row in the table is selected
     * 
     * @return
     */
    public boolean isAnyRowSelected() {
        return selections.size() > 0;
    }

    // ********* Event Firing Methods ********************

    /**
     * Private method that will fire a BeforeSelectionEvent for the passed
     * index. Returns false if the selection is canceled by registered handler
     * and true if the selection is allowed.
     */
    private boolean fireBeforeSelectionEvent(int index) {
        BeforeSelectionEvent<Integer> event = null;

        if ( !queryMode)
            event = BeforeSelectionEvent.fire(this, index);

        return event == null || !event.isCanceled();
    }

    /**
     * Private method that will fire a SelectionEvent for the passed index to
     * notify all registered handlers that row at the passed index was selected.
     * Returns true as a default.
     * 
     * @param index
     * @return
     */
    private boolean fireSelectionEvent(int index) {

        if ( !queryMode)
            SelectionEvent.fire(this, index);

        return true;
    }

    /**
     * Private method that will fire an UnselectionEvent for the passed index.
     * Returns false if the unselection was canceled by a registered handler and
     * true if the unselection is allowed.
     * 
     * @param index
     * @return
     */
    private void fireUnselectEvent(int index) {

        if ( !queryMode)
            UnselectionEvent.fire(this, index);
    }

    /**
     * Private method that will fire a BeforeCellEditedEvent for a cell in the
     * table. Returns false if the cell editing is canceled by a registered
     * handler and true if the user is allowed to edit the cell.
     * 
     * @param row
     * @param col
     * @param val
     * @return
     */
    private boolean fireBeforeCellEditedEvent(int row, int col, Object val) {
        BeforeCellEditedEvent event = null;

        if ( !queryMode)
            event = BeforeCellEditedEvent.fire(this, row, col, val);

        return event == null || !event.isCancelled();
    }

    /**
     * Private method that will fire a CellEditedEvent after the value of a cell
     * is changed by a user input. Returns true as default.
     * 
     * @param index
     * @return
     */
    private boolean fireCellEditedEvent(int row, int col) {

        if ( !queryMode)
            CellEditedEvent.fire(this, row, col);

        return true;
    }

    /**
     * Private method that fires a BeforeRowAddedEvent for the passed index and
     * Row. Returns false if the addition is canceled by a registered handler
     * and true if the addition is allowed.
     * 
     * @param index
     * @param row
     * @return
     */
    private boolean fireBeforeRowAddedEvent(int index, Row row) {
        BeforeRowAddedEvent event = null;

        if ( !queryMode)
            event = BeforeRowAddedEvent.fire(this, index, row);

        return event == null || !event.isCancelled();
    }

    /**
     * Private method that fires a RowAddedEvent for the passed index and Row to
     * all registered handlers. Returns true as a default.
     * 
     * @param index
     * @param row
     * @return
     */
    private boolean fireRowAddedEvent(int index, Row row) {

        if ( !queryMode)
            RowAddedEvent.fire(this, index, row);

        return true;

    }

    /**
     * Private method that fires a BeforeRowDeletedEvent for the passed index
     * and Row. Returns false if the deletion is canceled by a registered
     * handler and true if the deletion is allowed.
     * 
     * @param index
     * @param row
     * @return
     */
    private boolean fireBeforeRowDeletedEvent(int index, Row row) {
        BeforeRowDeletedEvent event = null;

        if ( !queryMode)
            event = BeforeRowDeletedEvent.fire(this, index, row);

        return event == null || !event.isCancelled();
    }

    /**
     * Private method that fires a RowDeletedEvent for the passed index and Row
     * to all registered handlers. Returns true as a default.
     * 
     * @param index
     * @param row
     * @return
     */
    private boolean fireRowDeletedEvent(int index, Row row) {

        if ( !queryMode)
            RowDeletedEvent.fire(this, index, row);

        return true;
    }

    protected boolean fireCellClickedEvent(int row, int col, boolean ctrlKey, boolean shiftKey) {
        CellClickedEvent event = null;

        if ( !queryMode)
            event = CellClickedEvent.fire(this, row, col, ctrlKey, shiftKey);

        return event == null || !event.isCancelled();

    }
    
    protected void fireCellDoubleClickedEvent(int row, int col) {
        if (!queryMode)
            CellDoubleClickedEvent.fire(this, row, col);
    }

    /**
     * Fires a Filter event after this table has been filtered and the new model
     * is displayed.
     */
    protected void fireFilterEvent() {
        FilterEvent.fire(this);
    }

    // ********* Edit Table Methods *******************
    /**
     * Used to determine if a cell is currently being edited in the Table
     */
    public boolean isEditing() {
        return editing;
    }

    /**
     * Sets the value of a cell in Table model.
     * 
     * @param row
     * @param col
     * @param value
     */
    public <T> void setValueAt(int row, int col, T value) {
        Column column;
        ArrayList<Exception> exceptions;

        finishEditing();
        modelView.get(row).setCell(col, value);

        column = getColumnAt(col);
        
        exceptions = column.getCellRenderer().validate(value);
        
        if(!queryMode) {
            if (column.isRequired() && value == null) {
                if(exceptions == null)
                    exceptions = new ArrayList<Exception>();
                exceptions.add(new Exception(Messages.get().exc_fieldRequired()));
            }
        }
        
        setValidateException(row,col,exceptions);

        refreshCell(row, col);
    }

    /**
     * Sets a row in the model at the passed index and refreshes the view.
     * 
     * @param index
     * @param row
     */
    public <T extends Row> void setRowAt(int index, T row) {
        finishEditing();
        modelView.set(index, row);
        renderView(index, index);
    }

    /**
     * Returns the value of a cell in the model.
     * 
     * @param row
     * @param col
     * @return
     */
    public <T> T getValueAt(int row, int col) {
        if (modelView == null || row >= modelView.size())
            return null;
        return (T)modelView.get(row).getCell(col);
    }

    /**
     * Method to put a cell into edit mode. If a cell can not be edited than
     * false will be returned
     * 
     * @param row
     * @param col
     * @return
     */
    public boolean startEditing(int row, int col) {
        return startEditing(row, col, null);
    }

    /**
     * Method that sets focus to a cell in the Table and readies it for user
     * input. event is passed to this method by view clickhandler to be able to
     * check for multiple selection logic
     * 
     * @param row
     * @param col
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected boolean startEditing(final int row, final int col, final NativeEvent event) {

        /*
         * Return out if the table is not enable or the passed cell is already
         * being edited
         */
        if ( !isEnabled() || (row == editingRow && col == editingCol)) {
            if(columns.get(col).getCellEditor() instanceof CheckBoxCell && 
               Event.getTypeInt(event.getType()) == Event.ONCLICK)
                ClickEvent.fireNativeEvent(event, ((CheckBox)getColumnWidget(col)).getCheck());
            return false;
        }

        finishEditing();

        selectRowAt(row, event);

        // Check if the row was able to be selected, if not return.
        if ( !isRowSelected(row))
            return false;

        // Check if column is editable otherwise return false
        if ( !getColumnAt(col).hasEditor())
            return false;

        // Fire before cell edited event to allow user the chance to cancel
        if ( !fireBeforeCellEditedEvent(row, col, getValueAt(row, col)))
            return false;

        /*
         * Set editing attribute values.
         */
        editingRow = row;
        editingCol = col;
        editing = true;

        view.startEditing(row, col, getValueAt(row, col), event);

        return true;
    }

    public void finishEditing() {
        finishEditing(true);
    }

    /**
     * Method called to complete editing of any cell in the table. Method does
     * nothing a cell is not currently being edited.
     */
    public void finishEditing(boolean keepFocus) {
        Object newValue, oldValue;
        int row, col;

        /*
         * Return out if not currently editing.
         */
        if ( !editing)
            return;

        /*
         * Reset editing attribute values
         */
        editing = false;
        row = editingRow;
        col = editingCol;
        editingRow = -1;
        editingCol = -1;

        /*
         * Retrieve new value form cell editor, store value in the model, and
         * render the cell
         */
        newValue = view.finishEditing(row, col);
        oldValue = getValueAt(row, col);
        setValueAt(row, col, newValue);
        // modelView.get(row).setCell(col, newValue);
        // refreshCell(row, col);

        /*
         * fire a cell edited event if the value of the cell was changed
         */
        if (Util.isDifferent(newValue, oldValue)) {
            fireCellEditedEvent(row, col);
        }

        /*
         * Call setFocus(true) so that the KeyHandler will receive events when
         * no cell is being edited
         */
        if (keepFocus)
            setFocus(true);
    }

    /**
     * Returns the current row where cell is being edited
     * 
     * @return
     */
    public int getEditingRow() {
        return editingRow;
    }

    /**
     * Returns the current column where cell is being edited
     * 
     * @return
     */
    public int getEditingCol() {
        return editingCol;
    }

    // ********* Draw Scroll Methods ****************
    /**
     * Scrolls the table in the required direction to make sure the passed index
     * is visible. Or if the index passed is in the view range refresh the row
     * to make sure that the latest data is shown (i.e. row Added before scroll
     * size is hit).
     */
    public boolean scrollToVisible(int index) {
        return view.scrollToVisible(index);
    }

    /**
     * Method to scroll the table by the specified number of rows. A negative
     * value will cause the table to scroll up and a positive to scroll down.
     * 
     * @param rows
     */
    public void scrollBy(int rows) {
        view.scrollBy(rows);
    }

    /**
     * Redraws the table when any part of its physical definition is changed.
     */
    public void layout() {
        computeColumnsWidth();
        view.layout();
    }

    /**
     * Method called when a column width has been set to resize the table
     * columns
     */
    protected void resize() {
        computeColumnsWidth();

        if ( !isAttached()) {
            layout();
            return;
        }

        finishEditing();

        if (hasHeader)
            view.getHeader().resize();

        view.resize();
    }

    /**
     * Method will have to view re-compute its visible rows and refresh the view
     * 
     * @param startR
     * @param endR
     */
    protected void renderView(int startR, int endR) {
        view.adjustScrollBarHeight();
        view.renderView(startR, endR);
    }

    /**
     * Method computes the XForColumn and ColumForX arrays and set the
     * totoalColumnWidth
     */
    protected void computeColumnsWidth() {
        int from, to;

        //
        // compute total width
        //
        totalColumnWidth = 0;
        int xmark = 0;
        xForColumn = new short[getColumnCount()];
        for (int i = 0; i < getColumnCount(); i++ ) {
            if (getColumnAt(i).isDisplayed()) {
                xForColumn[i] = (short)xmark;
                xmark += getColumnAt(i).getWidth();
                totalColumnWidth += getColumnAt(i).getWidth();
            }
        }
        //
        // mark the array
        //
        from = 0;
        columnForX = new short[xmark];
        for (int i = 0; i < getColumnCount(); i++ ) {
            if (getColumnAt(i).isDisplayed()) {
                to = from + getColumnAt(i).getWidth();
                while (from < to && from + 1 < xmark)
                    columnForX[from++ ] = (short)i;
            }
        }
    }

    /**
     * redraws data in the cell passed
     * 
     * @param row
     * @param col
     */
    protected void refreshCell(int row, int col) {
        view.renderCell(row, col);
    }

    // ************* Implementation of ScreenWidgetInt *************

    /**
     * Sets whether this table allows selection
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

    }

    /**
     * Used to determine if the table is enabled for selection.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the Focus style to the Table
     */
    public void addFocusStyle(String style) {
        addStyleName(style);
    }

    /**
     * Removes the Focus style from the Table
     */
    public void removeFocusStyle(String style) {
        removeStyleName(style);
    }

    public void onFocus(FocusEvent event) {
        /*
         * Widget focused;
         * 
         * focused = ((ScreenPanel)event.getSource()).getFocused();
         * 
         * if(focused == null ||
         * !DOM.isOrHasChild(getElement(),focused.getElement()))
         * finishEditing(false);
         */
    }

    // ********** Implementation of Queryable *******************
    /**
     * Returns a list of QueryData objects for all Columns in the table that
     * have values and will participate in the query.
     */
    public Object getQuery() {
        ArrayList<QueryData> qds;
        QueryData qd;

        if ( !queryMode)
            return null;

        qds = new ArrayList<QueryData>();

        for (int i = 0; i < getColumnCount(); i++ ) {
            qd = (QueryData)getValueAt(0, i);
            if (qd != null) {
                qd.setKey(getColumnAt(i).name);
                qds.add(qd);
            }
        }
        return qds.toArray(new QueryData[] {});
    }

    /**
     * Stub method for Queryable method
     */
    public void setQuery(QueryData query) {
        // Do nothing
    }

    /**
     * Puts the table into and out of query mode.
     */
    public void setQueryMode(boolean query) {

        ArrayList<Row> model;
        Row row;

        if (query == queryMode)
            return;

        this.queryMode = query;
        if (query) {
            model = new ArrayList<Row>();
            row = new Row(getColumnCount());
            model.add(row);
            setModel(model);
        } else
            setModel(null);
    }

    /**
     * Method to determine if Table is in QueryMode
     * 
     * @return
     */
    public boolean getQueryMode() {
        return queryMode;
    }

    /**
     * Stub method from Queryable Interface
     */
    public void validateQuery() {

    }

    /**
     * Method used to determine if widget is currently in Query mode
     */
    public boolean isQueryMode() {
        return queryMode;
    }

    /**
     * Convenience method to check if a widget has exceptions so we do not need
     * to go through the cost of merging the logical and validation exceptions
     * in the getExceptions method.
     * 
     * @return
     */
    public boolean hasExceptions(int row, int col) {
        Row key;

        key = getRowAt(row);
        return (endUserExceptions != null && (endUserExceptions.containsKey(key) && endUserExceptions.get(key)
                                                                                                     .containsKey(col))) ||
               (validateExceptions != null && (validateExceptions.containsKey(key) && validateExceptions.get(key)
                                                                                                        .containsKey(col)));
    }

    public <T extends Row> void addException(T row, int col, Exception error) {
        ArrayList<Exception> exceptions;
        int r;
  
        exceptions = getEndUserExceptionList(row, col);
        
        if(exceptions.contains(error))
            return;
        
        exceptions.add(error);
  
        if(model == null)
        	return;
        
        if (rowIndex != null && rowIndex.containsKey(row)) {
            r = rowIndex.get(row).view;
        } else {
            r = model.indexOf(row);
        }
        view.renderView(r, r);
    }

    /**
     * Adds a manual Exception to the widgets exception list.
     */
    public void addException(int row, int col, Exception error) {
        ArrayList<Exception> exceptions;
        
        exceptions = getEndUserExceptionList(getRowAt(row), col);
        
        if(exceptions.contains(error))
            return;
        
        exceptions.add(error);
        
        renderView(row, row);
    }

    /**
     * Method to add a validation exception to the passed cell.
     * 
     * @param row
     * @param col
     * @param error
     */
    protected void setValidateException(int rw, int col, ArrayList<Exception> errors) {

        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;
        HashMap<Integer, ArrayList<Exception>> rowExceptions;
        Row row;

        row = getRowAt(rw);

        // If hash is null and errors are passed as null, nothing to reset so
        // return
        if (validateExceptions == null && (errors == null || errors.isEmpty()))
            return;

        // If hash is not null, but errors passed is null then make sure the
        // passed cell entry removed
        if (validateExceptions != null && (errors == null || errors.isEmpty())) {
            if (validateExceptions.containsKey(row)) {
                rowExceptions = validateExceptions.get(row);
                rowExceptions.remove(col);
                if (rowExceptions.isEmpty())
                    validateExceptions.remove(row);
            }
            return;
        }

        // If list is null we need to create the Hash to add the errors
        if (validateExceptions == null) {
            validateExceptions = new HashMap<Row, HashMap<Integer, ArrayList<Exception>>>();
            cellExceptions = new HashMap<Integer, ArrayList<Exception>>();

            validateExceptions.put(row, cellExceptions);
        }

        if (cellExceptions == null) {
            if ( !validateExceptions.containsKey(row)) {
                cellExceptions = new HashMap<Integer, ArrayList<Exception>>();
                validateExceptions.put(row, cellExceptions);
            } else
                cellExceptions = validateExceptions.get(row);
        }

        cellExceptions.put(col, errors);
    }

    /**
     * Gets the ValidateExceptions list to be displayed on the screen.
     */
    public ArrayList<Exception> getValidateExceptions(int row, int col) {
        if (validateExceptions != null) {
            if (validateExceptions.containsKey(getRowAt(row)))
                return validateExceptions.get(getRowAt(row)).get(col);
        }
        return null;
    }

    /**
     * Method used to get the set list of user exceptions for a cell.
     * 
     * @param row
     * @param col
     * @return
     */
    public ArrayList<Exception> getEndUserExceptions(int row, int col) {
        if (endUserExceptions != null) {
            if (endUserExceptions.containsKey(getRowAt(row)))
                return endUserExceptions.get(getRowAt(row)).get(col);
        }
        return null;
    }

    /**
     * Clears all manual and validate exceptions from the widget.
     */
    public void clearExceptions() {
        if (endUserExceptions != null || validateExceptions != null) {
            endUserExceptions = null;
            validateExceptions = null;
            view.renderExceptions( -1, -1);
        }
    }

    public void clearEndUserExceptions() {
        if (endUserExceptions != null) {
            endUserExceptions = null;
            view.renderExceptions( -1, -1);
        }
    }

    public void clearValidateExceptions() {
        if (validateExceptions != null) {
            validateExceptions = null;
            view.renderExceptions( -1, -1);
        }
    }

    public void clearExceptions(Row row, int col) {
        if (rowIndex != null && rowIndex.containsKey(row))
            clearExceptions(rowIndex.get(row).model, col);
        else
            clearExceptions(model.indexOf(row), col);
    }

    /**
     * Clears all exceptions from the table cell passed
     * 
     * @param row
     * @param col
     */
    public void clearExceptions(int row, int col) {
        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;
        Row key;

        key = getRowAt(row);
        if (endUserExceptions != null) {
            cellExceptions = endUserExceptions.get(key);
            if (cellExceptions != null) {
                cellExceptions.remove(col);
                if (cellExceptions.size() == 0)
                    endUserExceptions.remove(key);
            }
        }

        if (validateExceptions != null) {
            cellExceptions = validateExceptions.get(key);
            if (cellExceptions != null) {
                cellExceptions.remove(col);
                if (cellExceptions.size() == 0)
                    validateExceptions.remove(key);
            }
        }

        view.renderExceptions(row, row);

    }

    public <T extends Row> void clearEndUserExceptions(T row, int col) {

        if (rowIndex != null && rowIndex.containsKey(row))
            clearEndUserExceptions(rowIndex.get(row).model, col);
        else
            clearEndUserExceptions(model.indexOf(row), col);
    }

    /**
     * Clears all exceptions from the table cell passed
     * 
     * @param row
     * @param col
     */
    public void clearEndUserExceptions(int row, int col) {
        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;
        Row key;

        key = getRowAt(row);
        if (endUserExceptions != null) {
            cellExceptions = endUserExceptions.get(key);
            if (cellExceptions != null) {
                cellExceptions.remove(col);
                if (cellExceptions.size() == 0)
                    endUserExceptions.remove(key);
            }
        }

        view.renderExceptions(row, row);

    }

    /**
     * Method will get the list of the exceptions for a cell and will create a
     * new list if no exceptions are currently on the cell.
     * 
     * @param row
     * @param col
     * @return
     */
    private ArrayList<Exception> getEndUserExceptionList(Row row, int col) {
        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;
        ArrayList<Exception> list = null;

        if (endUserExceptions == null)
            endUserExceptions = new HashMap<Row, HashMap<Integer, ArrayList<Exception>>>();

        cellExceptions = endUserExceptions.get(row);

        if (cellExceptions == null) {
            cellExceptions = new HashMap<Integer, ArrayList<Exception>>();
            endUserExceptions.put(row, cellExceptions);
        }

        list = cellExceptions.get(col);

        if (list == null) {
            list = new ArrayList<Exception>();
            cellExceptions.put(col, list);
        }

        return list;

    }

    /**
     * Method will get the list of the exceptions for a cell and will create a
     * new list if no exceptions are currently on the cell.
     * 
     * @param row
     * @param col
     * @return
     */
    private ArrayList<Exception> getValidateExceptionList(int row, int col) {
        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;
        ArrayList<Exception> list;
        Row key;

        key = getRowAt(row);
        if (validateExceptions == null)
            validateExceptions = new HashMap<Row, HashMap<Integer, ArrayList<Exception>>>();

        cellExceptions = validateExceptions.get(key);

        if (cellExceptions == null) {
            cellExceptions = new HashMap<Integer, ArrayList<Exception>>();
            validateExceptions.put(key, cellExceptions);
        }

        list = cellExceptions.get(col);

        if (list == null) {
            list = new ArrayList<Exception>();
            cellExceptions.put(col, list);
        }

        return list;

    }

    /**
     * Method to draw the balloon display of the exceptions for a cell
     * 
     * @param row
     * @param col
     * @param x
     * @param y
     */
    protected void drawExceptions(final int row, final int col, final int x, final int y) {
        if (row == editingRow && col == editingCol)
            return;

        balloonTimer = new Timer() {
            public void run() {
                Balloon.drawExceptions(getEndUserExceptions(row, col),
                                       getValidateExceptions(row, col),
                                       view.table().getCellFormatter().getElement(row, col),
                                       x,
                                       y);
            }
        };
        balloonTimer.schedule(500);
    }

    // ******************** Drag and Drop methods
    // ****************************************
    /**
     * Method will enable the rows in the table to be dragged. This must be
     * called before the model is first set.
     */
    public void enableDrag() {
        assert model == null : "Drag must be set before model is loaded";

        dragController = new TableDragController(this, RootPanel.get());
    }

    /**
     * Method will enable this table to receive drop events from a drag
     */
    public void enableDrop() {
        dropController = new TableDropController(this);
    }

    /**
     * Adds a DropController as a drop target for rows from this table
     * 
     * @param target
     */
    public void addDropTarget(DropController target) {
        dragController.registerDropController(target);
    }

    /**
     * Removes a DropController as a drop target for rows from this table
     * 
     * @param target
     */
    public void removeDropTarget(DropController target) {
        dragController.unregisterDropController(target);
    }

    /**
     * Returns the TableDragController for this Table.
     * 
     * @return
     */
    public TableDragController getDragController() {
        return dragController;
    }

    /**
     * Returns the TableDropController for this Table.
     * 
     * @return
     */
    public TableDropController getDropController() {
        return dropController;
    }

    // ********* Registration of Handlers ******************
    /**
     * Registers a BeforeSelectionHandler to this Table
     */
    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler) {
        return addHandler(handler, BeforeSelectionEvent.getType());
    }

    /**
     * Registers a SelectionHandler to this Table
     */
    public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    /**
     * Registers an UnselectionHandler to this Table
     */
    public HandlerRegistration addUnselectionHandler(UnselectionHandler<Integer> handler) {
        return addHandler(handler, UnselectionEvent.getType());
    }

    /**
     * Registers a BeforeCellEditedHandler to this Table
     */
    public HandlerRegistration addBeforeCellEditedHandler(BeforeCellEditedHandler handler) {
        return addHandler(handler, BeforeCellEditedEvent.getType());
    }

    /**
     * Registers a CellEditedHandler to this Table
     */
    public HandlerRegistration addCellEditedHandler(CellEditedHandler handler) {
        return addHandler(handler, CellEditedEvent.getType());
    }

    /**
     * Registers a BeforeRowAddedHandler to this Table
     */
    public HandlerRegistration addBeforeRowAddedHandler(BeforeRowAddedHandler handler) {
        return addHandler(handler, BeforeRowAddedEvent.getType());
    }

    /**
     * Registers a RowAddedHandler to this Table
     */
    public HandlerRegistration addRowAddedHandler(RowAddedHandler handler) {
        return addHandler(handler, RowAddedEvent.getType());
    }

    /**
     * Registers a BeforeRowDeletedHandler to this Table
     */
    public HandlerRegistration addBeforeRowDeletedHandler(BeforeRowDeletedHandler handler) {
        return addHandler(handler, BeforeRowDeletedEvent.getType());
    }

    /**
     * Registers a RowDeletedHandler to this Table
     */
    public HandlerRegistration addRowDeletedHandler(RowDeletedHandler handler) {
        return addHandler(handler, RowDeletedEvent.getType());
    }

    /**
     * Register a CellClickedHandler to this Table
     */
    public HandlerRegistration addCellClickedHandler(CellClickedHandler handler) {
        return addHandler(handler, CellClickedEvent.getType());
    }
    
    public HandlerRegistration addCellDoubleClickedHandler(CellDoubleClickedEvent.Handler handler) {
        return addHandler(handler, CellDoubleClickedEvent.getType());
    }


    /**
     * Register a FilterHandler to this Table
     */
    public HandlerRegistration addFilterHandler(FilterHandler handler) {
        return addHandler(handler, FilterEvent.getType());
    }

    /**
     * This method will check the model to make sure that all required cells
     * have values
     */
    public void validate() {
        boolean render = false;
        ArrayList<Exception> exceptions;
        Exception exception;

        finishEditing();
        
        if(queryMode)
            return;

        for (int col = 0; col < getColumnCount(); col++ ) {
            if (getColumnAt(col).isRequired()) {
                for (int row = 0; row < getRowCount(); row++ ) {
                    if (getValueAt(row, col) == null) {
                        exceptions = getValidateExceptionList(row, col);
                        exception = new Exception(Messages.get().exc_fieldRequired());
                        if(!exceptions.contains(exception)) {
                            exceptions.add(exception);
                            setValidateException(row, col, exceptions);
                            render = true;
                        }
                    }
                }
            }
        }

        if (render)
            view.renderExceptions( -1, -1);
    }

    /**
     * Returns the model as part of the HasValue interface
     */
    public ArrayList<? extends Row> getValue() {
        return getModel();
    }

    /**
     * Sets the model as part of the HasValue interface
     */
    public void setValue(ArrayList<? extends Row> value) {
        setValue(value, false);
    }

    /**
     * Sets the model and will fire ValueChangeEvent if fireEvents is true as
     * part of the HasValue interface
     */
    public void setValue(ArrayList<? extends Row> value, boolean fireEvents) {
        setModel(value);

        if (fireEvents)
            ValueChangeEvent.fire(this, value);

    }

    /**
     * Handler Registration for ValueChangeEvent
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ArrayList<? extends Row>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void addException(Exception exception) {

    }

    public void addExceptionStyle() {

    }

    public void checkExceptions() {
        if(endUserExceptions != null) {
            for(Row row : endUserExceptions.keySet()) {
                if(model == null || !model.contains(row))
                    endUserExceptions.remove(row);
            }
            
            if(endUserExceptions.size() == 0) 
                endUserExceptions = null;
        }
        
        if(validateExceptions != null) {
            for(Row row : validateExceptions.keySet()) {
                if(model == null || !model.contains(row))
                    validateExceptions.remove(row);
            }
            
            if(validateExceptions.size() == 0) 
                validateExceptions = null;
        }
        
    }
    
    public ArrayList<Exception> getEndUserExceptions() {
        ArrayList<Exception> exceptions;
        
        if(endUserExceptions == null)
            return null;
        
        exceptions = new ArrayList<Exception>();
        
        for(HashMap<Integer,ArrayList<Exception>> row : endUserExceptions.values()) {
            for(ArrayList<Exception> excs : row.values()) {
                exceptions.addAll(excs);
            }
        }
        
        return exceptions;
    }

    public ArrayList<Exception> getValidateExceptions() {
        ArrayList<Exception> exceptions;
        
        if(validateExceptions == null)
            return null;
        
        exceptions = new ArrayList<Exception>();
        
        for(HashMap<Integer,ArrayList<Exception>> row : validateExceptions.values()) {
            for(ArrayList<Exception> excs : row.values()) {
                exceptions.addAll(excs);
            }
        }
        
        return exceptions;
    }

    public boolean hasExceptions() {
        validate();
        return (endUserExceptions != null && !endUserExceptions.isEmpty()) ||
               (validateExceptions != null && !validateExceptions.isEmpty());
    }

    public void removeExceptionStyle() {

    }

    /**
     * This private inner class is used to map Row indexes from the model to a
     * sorted or filtered view
     */
    private class RowIndexes {
        protected int model, view;

        protected RowIndexes(int model, int view) {
            this.model = model;
            this.view = view;
        }
    }

    /**
     * Private inner class that implements Comparator<Row> interface and will
     * sort the table model using the Collections.sort() method
     */

    private class Sort<T extends Row> implements Comparator<T> {
        int        col, dir;

        @SuppressWarnings("rawtypes")
        Comparator comparator;

        @SuppressWarnings("rawtypes")
        public Sort(int col, int dir, Comparator comparator) {
            this.col = col;
            this.dir = dir;
            this.comparator = comparator;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public int compare(Row o1, Row o2) {
            Comparable c1,c2;
            
            c1 = o1.getCell(col);
            c2 = o2.getCell(col);
            
            if (comparator != null)
                return dir * comparator.compare(c1, c2);
            
            if(c1 == null && c2 == null)
                return 0;
            else if(c1 != null && c2 != null)
                return dir * ((Comparable)o1.getCell(col)).compareTo((Comparable)o2.getCell(col));
            else{
                if(c1 == null && c2 != null)
                    return 1;
                else 
                    return -1;
            }           
        };
    }

    public class UniqueFilter implements Filter {
        int                           column;
        ArrayList<FilterChoice>       choices;
        HashMap<Object, FilterChoice> values;

        public ArrayList<FilterChoice> getChoices(ArrayList<? extends Row> model) {
            Object value;
            FilterChoice choice;
            CellRenderer renderer;

            if (values == null) {
                values = new HashMap<Object, FilterChoice>();
                choices = new ArrayList<FilterChoice>();
            }

            renderer = getColumnAt(column).getCellRenderer();
            if (values.isEmpty() && renderer instanceof CheckBoxCell) {
            	choice = new FilterChoice();
            	choice.setDisplay("Checked");
            	choice.setValue("Y");
            	choice.setSelected(false);
            	choices.add(choice);
            	values.put("Y",choice);
            	choice = new FilterChoice();
            	choice.setDisplay("Unchecked");
            	choice.setSelected(false);
            	choice.setValue("N");
                choices.add(choice);
                values.put("N",choice);
            } else {
            	for (Row row : model) {
            		value = row.getCell(column);
            		if ( !values.containsKey(value)) {
            			choice = new FilterChoice();
            			values.put(value, choice);
            			choice.setValue(value);
            			choice.setDisplay(renderer.display(value));
            			choices.add(choice);
            		}
            	}
            }
            
            return choices;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public int getColumn() {
            return column;
        }

        public boolean include(Object value) {
            return values == null || values.get(value).selected;
        }

        public void unselectAll() {
            for (FilterChoice choice : choices)
                choice.setSelected(false);
        }

        public boolean isFilterSet() {
            if (choices == null)
                return false;

            for (FilterChoice choice : choices) {
                if (choice.isSelected())
                    return true;
            }
            return false;
        }
    }

    @Override
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTabIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setAccessKey(char key) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus(boolean focused) {
        super.setFocus(focused);

    }

    @Override
    public void setTabIndex(int index) {
        // TODO Auto-generated method stub

    }

    @Override
    public void add(IsWidget w) {
        assert w instanceof Column;

        ((Column)w).setTable(this);
        addColumn((Column)w);
    }

    public void onResize() {
        Element parent;
        
        if(!isAttached())
            return;
            
        parent = (Element) (getParent() instanceof LayoutPanel ? ((LayoutPanel)getParent()).getWidgetContainerElement(this)
                                                            : getParent().getElement());
       
        int width = parent.getOffsetWidth();
        int height = parent.getOffsetHeight();
         
        view.setSize(width+"px", height+"px");
        view.onResize();
        
        
    }
    
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        onResize();
    }
    
    public void setTipProvider(CellTipProvider tipProvider) {
        this.tipProvider = tipProvider;
        
        if(toolTip == null)
            setBalloonOptions(new Options());
            
    }

    @Override
    public Options getBalloonOptions() {
        return toolTip;
    }

    @Override
    public void setBalloonOptions(Options options) {
        toolTip = options;
        
        options.setPlacement(Placement.MOUSE);
        
        view.table().addCellMouseOverHandler(new CellMouseOverEvent.Handler() {
            
            @Override
            public void onCellMouseOver(CellMouseOverEvent event) {
                tipRow = event.getRow();
                tipCol = event.getCol();
                final int x,y;
                
                Element td = view.table().getCellFormatter().getElement(event.getRow(), event.getCol());
                
                y = td.getAbsoluteTop();
                x = td.getAbsoluteLeft() + (td.getOffsetWidth()/2);
                
                if(!hasExceptions(tipRow, tipCol)) {
                    balloonTimer = new Timer() {
                        public void run() {
                            Balloon.show((HasBalloon)source, x, y);
                        }
                    };
                    balloonTimer.schedule(500);
                }
                
            }
        });
        
       options.setTipProvider(new Balloon.TipProvider<Object>() {
        
           @Override
           public Object getTip(HasBalloon target) {
               
               if(tipProvider != null)
                   return tipProvider.getTip(tipRow, tipCol);
               
               return "No Tip Provider set";
           }
           
       });
    }
    
    public void setCSS(TableCSS css) {
        css.ensureInjected();
        view.setCSS(css);
    }
    
    public void insertBefore(Table bef) {
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(bef);
        hp.add(((StaticView)view).scrollView.getWidget());
        ((StaticView)view).scrollView.setWidget(hp);
    }
   

}
