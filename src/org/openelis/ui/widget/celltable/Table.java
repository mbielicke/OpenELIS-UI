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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.openelis.ui.common.Util;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.widget.Balloon;
import org.openelis.ui.widget.HasExceptions;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.ScreenWidgetInt;
import org.openelis.ui.widget.celltable.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.celltable.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.celltable.event.BeforeRowAddedEvent;
import org.openelis.ui.widget.celltable.event.BeforeRowAddedHandler;
import org.openelis.ui.widget.celltable.event.BeforeRowDeletedEvent;
import org.openelis.ui.widget.celltable.event.BeforeRowDeletedHandler;
import org.openelis.ui.widget.celltable.event.CellClickedEvent;
import org.openelis.ui.widget.celltable.event.CellClickedHandler;
import org.openelis.ui.widget.celltable.event.CellEditedEvent;
import org.openelis.ui.widget.celltable.event.CellEditedHandler;
import org.openelis.ui.widget.celltable.event.FilterEvent;
import org.openelis.ui.widget.celltable.event.FilterHandler;
import org.openelis.ui.widget.celltable.event.HasBeforeCellEditedHandlers;
import org.openelis.ui.widget.celltable.event.HasBeforeRowAddedHandlers;
import org.openelis.ui.widget.celltable.event.HasBeforeRowDeletedHandlers;
import org.openelis.ui.widget.celltable.event.HasCellClickedHandlers;
import org.openelis.ui.widget.celltable.event.HasCellEditedHandlers;
import org.openelis.ui.widget.celltable.event.HasFilterHandlers;
import org.openelis.ui.widget.celltable.event.HasRowAddedHandlers;
import org.openelis.ui.widget.celltable.event.HasRowDeletedHandlers;
import org.openelis.ui.widget.celltable.event.HasUnselectionHandlers;
import org.openelis.ui.widget.celltable.event.RowAddedEvent;
import org.openelis.ui.widget.celltable.event.RowAddedHandler;
import org.openelis.ui.widget.celltable.event.RowDeletedEvent;
import org.openelis.ui.widget.celltable.event.RowDeletedHandler;
import org.openelis.ui.widget.celltable.event.UnselectionEvent;
import org.openelis.ui.widget.celltable.event.UnselectionHandler;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
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
public class Table<T> extends FocusPanel implements ScreenWidgetInt, Queryable,
                                     HasBeforeSelectionHandlers<Integer>,
                                     HasSelectionHandlers<Integer>,
                                     HasUnselectionHandlers<Integer>, HasBeforeCellEditedHandlers,
                                     RequiresResize, HasCellEditedHandlers,
                                     HasBeforeRowAddedHandlers, HasRowAddedHandlers,
                                     HasBeforeRowDeletedHandlers, HasRowDeletedHandlers,
                                     HasCellClickedHandlers, HasValue<List<T>>,
                                     HasExceptions, Focusable, FocusHandler, HasFilterHandlers {

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
    protected List<T>              model, modelView, modelSort;
    protected HashMap<Object, RowIndexes> rowIndex;

    /**
     * Columns used by the Table
     */
    protected ArrayList<Column<T>>           columns;

    /**
     * List of selected Rows by index in the table
     */
    protected ArrayList<Integer>          selections = new ArrayList<Integer>(5);

    /**
     * Exception lists for the table
     */
    protected HashMap<T, HashMap<Integer, ArrayList<Exception>>> endUserExceptions,
                    validateExceptions;

    /**
     * Table state values
     */
    protected boolean                                              enabled, multiSelect, editing,
                    hasFocus, queryMode, hasHeader, unitTest, fixScrollBar = true;

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

    /**
     * Indicates direction for the Sort
     */
    public static final int       SORT_ASCENDING = 1, SORT_DESCENDING = -1;

    protected Logger              logger         = Logger.getLogger("Widget");

    public Table() {
        rowHeight = 20;
        fixScrollBar = true;
        multiSelect = false;
        columns = new ArrayList<Column<T>>(5);
        view = new StaticView(this);
        setWidget(view);
        setKeyHandling();
    }
    
    public void setInfiniteView() {
        view = new View(this);
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

                if (isEditing() && getColumnAt(col).getCellEditor(row).ignoreKey(keyCode))
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
                            if (startEditing(row, col, event.getNativeEvent()))
                                break;
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
                            if (startEditing(row, col, event.getNativeEvent()))
                                break;
                        }
                        break;
                    case (KeyCodes.KEY_ENTER):
                        // If editing just finish and return
                        if (isEditing()) {
                            finishEditing();
                            return;
                        }

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
            }
        }, BlurEvent.getType());

    }

    // ********* Table Definition Methods *************
    /**
     * Returns the currently used Row Height for the table layout
     */
    public int getRowHeight() {
        return rowHeight;
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
    public List<T> getModel() {
        return model;
    }

    /**
     * Sets the data model to be displayed by this table. The model parameter is
     * parameterized so specific models can be used that extend the basic Row
     * such as Item in AutoCompete and Dropdown
     * 
     * @param model
     */
    @SuppressWarnings("unchecked")
    public void setModel(List<T> model) {
        finishEditing();
        unselectAll();
        this.model = model;
        modelView = this.model;
        rowIndex = null;

        // Clear any filter choices that may have been in force before model
        // changed
        for (Column<T> col : columns) {
            if (col.getFilter() != null)
                col.getFilter().unselectAll();
        }

        // if ( !scrollToVisible(0))
        renderView( -1, -1);

    }

    /**
     * This method will pull all filters in force from the columns and apply
     * them to the table model.
     */
    public void applyFilters() {
        ArrayList<Filter> filters;

        filters = new ArrayList<Filter>();
        for (Column<T> col : columns) {
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
            renderView( -1, -1);
            return;
        }

        /*
         * Reset the modelView and the rowIndex hash
         */
        modelView = new ArrayList<T>();
        rowIndex = new HashMap<Object, RowIndexes>();
        for (int i = 0; i < model.size(); i++ )
            rowIndex.put(model.get(i), new RowIndexes(i, -1));

        /*
         * Run through model and filter out rows
         */
        for (int i = 0; i < model.size(); i++ ) {
            include = true;
            for (Filter filter : filters) {
                if (filter != null && filter.isFilterSet()) {// &&
                    //!filter.include(model.get(i).getCell(filter.getColumn()))) {
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
        renderView( -1, -1);
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

    /**
     * This method will take the passed model index of a row and return the
     * corresponding view index for the row. If the model row is currently not
     * in the view then the a value of -1 will be returned.
     * 
     * @param modelIndex
     * @return
     */
    public int convertModelIndexToView(int modelIndex) {
        int i = modelIndex;
        RowIndexes rowInd;

        if (rowIndex != null && modelIndex >= 0) {
            rowInd = rowIndex.get(model.get(modelIndex));
            if (rowInd != null)
                i = rowInd.view;
            else
                i = -1;
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
    public void applySort(int col, int dir, Comparator<T> comp) {
        /*
         * Setup the modelView as its own object if not already
         */
        if (modelView == model) {
            modelView = new ArrayList<T>();
            rowIndex = new HashMap<Object, RowIndexes>();
            for (int i = 0; i < model.size(); i++ ) {
                modelView.add(model.get(i));
                rowIndex.put(model.get(i), new RowIndexes(i, -1));
            }
        }

        Collections.sort(modelView, new Sort(col, dir, comp));

        /*
         * Set the view index of the hash based on the sort
         */
        for (int i = 0; i < modelView.size(); i++ )
            rowIndex.get(modelView.get(i)).view = i;

        renderView( -1, -1);
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

        return modelView.size();
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
        if (viewWidth < 0 && totalColumnWidth == 0 && getParent() != null)
            return ((LayoutPanel)getParent()).getWidgetContainerElement(this).getOffsetWidth();
        return viewWidth == -1 ? totalColumnWidth : viewWidth;
    }

    /**
     * Returns the width of the all the column widths added together which is
     * the physical width of the table
     * 
     * @return
     */
    protected int getTotalColumnWidth() {
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
    public Column<T> getColumnAt(int index) {
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
    public int getColumn(Column<T> col) {
        return columns.indexOf(col);
    }

    /**
     * This method will replace the column at the passed index into the table
     * 
     * @param index
     * @param col
     */
    public void setColumnAt(int index, Column<T> col) {
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
    public <W extends Widget> W getColumnWidget(int index) {
        return (W) (index > -1 ? getColumnAt(index).getCellEditor( -1).getWidget() : null);
    }

    /**
     * This method will return the column used in the table by it's name
     * 
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public <W extends Widget> W getColumnWidget(String name) {
        return (W)getColumnWidget(getColumnByName(name));
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

    /**
     * Sets the list columns to be used by this Table
     * 
     * @param columns
     */
    public void setColumns(ArrayList<Column<T>> columns) {
        this.columns = columns;

        if (columns != null) {
            for (Column<T> column : columns)
                column.setTable(this);
        }

        layout();
    }

    public void addColumn(Column<T> col) {
        addColumnAt(columns.size(), col);
    }


    public void addColumnAt(int index, Column<T> column) {
        columns.add(index, column);
        column.setTable(this);
        view.addColumn(index);
        //layout();
    }

    /**
     * Removes the column in the table and passed index.
     * 
     * @param index
     */
    public Column<T> removeColumnAt(int index) {
        Column<T> col;

        col = columns.remove(index);
        view.removeColumn(index);
        //layout();

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
     * Adds the passed Row to the end of the Table model.
     * 
     * @param row
     * @return
     */
    public void addRow(T row) {
        addRow(getRowCount(), row);
    }

    /**
     * Adds the passed Row into the Table model at the passed index.
     * 
     * @param index
     * @param row
     * @return
     */
    public void addRowAt(int index, T row) {
        addRow(index, row);
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
    private void addRow(int index, T row) {
    	assert row != null;
    	
        int modelIndex;

        finishEditing();


        if ( !fireBeforeRowAddedEvent(index, row))
            return;

        /* if a model has not been set need to create an empty model */
        if (model == null)
            setModel(new ArrayList<T>());

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

    }

    /**
     * Method will delete a row from the model at the specified index and
     * refersh the view.
     * 
     * @param index
     * @return
     */
    public T removeRowAt(int index) {
        int modelIndex;
        T row;

        finishEditing();

        unselectRowAt(index);
        
        row = getRowAt(index);

        if ( !fireBeforeRowDeletedEvent(index, row))
            return null;

        if (rowIndex != null) {
            modelIndex = convertViewIndexToModel(index);
            model.remove(modelIndex);
            rowIndex.remove(row);
            adjustRowIndexes(modelIndex, index + 1, -1);
        }
        modelView.remove(index);

        view.removeRow(index);

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
    }

    /**
     * Returns the Row at the specified index in the model
     * 
     * @param row
     * @return
     */
    @SuppressWarnings("unchecked")
    public T getRowAt(int row) {
        if (row < 0 || row >= getRowCount())
            return null;
        return modelView.get(row);
    }

    // ************ Selection Methods ***************

    /**
     * Returns an array of indexes of the currently selected row
     */
    public Integer[] getSelectedRows() {
        return selections.toArray(new Integer[] {});
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
        boolean ctrlKey, shiftKey, selected = false;
        int startSelect, endSelect, minSelected, maxSelected, i;

        startSelect = row;
        endSelect = row;

        /*
         * If multiple selection is allowed check event for ctrl or shift keys.
         * If none apply the logic will fall throw to normal selection.
         */
        if (isMultipleSelectionAllowed()) {
            if (event != null && Event.getTypeInt(event.getType()) == Event.ONCLICK) {
                ctrlKey = event.getCtrlKey();
                shiftKey = event.getShiftKey();

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
            }
        } else {
            unselectAll(event);
        }

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
            for (int i = 0; i < getRowCount(); i++ )
                selections.add(i);
            renderView( -1, -1);
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
    private boolean fireBeforeRowAddedEvent(int index,T row) {
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
    private boolean fireRowAddedEvent(int index, T row) {

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
    private boolean fireBeforeRowDeletedEvent(int index, T row) {
        BeforeRowDeletedEvent event = null;

        if ( !queryMode)
            event = BeforeRowDeletedEvent.fire(this, index, row);

        return event == null || event.isCancelled();
    }

    /**
     * Private method that fires a RowDeletedEvent for the passed index and Row
     * to all registered handlers. Returns true as a default.
     * 
     * @param index
     * @param row
     * @return
     */
    private boolean fireRowDeletedEvent(int index,T row) {

        if ( !queryMode)
            RowDeletedEvent.fire(this, index, row);

        return true;
    }

    protected boolean fireCellClickedEvent(int row, int col, boolean ctrlKey, boolean shiftKey,boolean isDouble) {
        CellClickedEvent event = null;

        if ( !queryMode)
            event = CellClickedEvent.fire(this, row, col, ctrlKey, shiftKey,isDouble);

        return event == null || !event.isCancelled();

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
    public <V> void setValueAt(int row, int col, V value) {
        Column<T> column;
        ArrayList<Exception> exceptions;

		finishEditing();
		//modelView.get(row).setCell(col, value);
		
		column = getColumnAt(col);
		
		((FieldUpdater<T,V>)column.getFieldUpdater()).update(col, getRowAt(row), value);
		
		exceptions = getColumnAt(col).getCellRenderer().validate(value);
		
		if(column.isRequired() && value == null)
			exceptions.add(new Exception(Messages.get().exc_fieldRequired()));
		
		setValidateException(row,col,exceptions);
		
		refreshCell(row, col);
    }

    /**
     * Sets a row in the model at the passed index and refreshes the view.
     * 
     * @param index
     * @param row
     */
    public  void setRowAt(int index, T row) {
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
    public <V> V getValueAt(int row, int col) {
		if (modelView == null || row >= modelView.size())
			return null;
		return (V)getColumnAt(col).getFieldGetter().getValue(col,getRowAt(row));
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
        if ( !isEnabled() || (row == editingRow && col == editingCol))
            return false;

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
    
    public void refreshRow(int r) {
    	renderView(r,r);
    }

    /**
     * Method computes the XForColumn and ColumForX arrays and set the
     * totoalColumnWidth
     */
    private void computeColumnsWidth() {
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
            }
            totalColumnWidth += getColumnAt(i).getWidth();
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

        ArrayList<T> model;
        T row;

        if (query == queryMode)
            return;

        this.queryMode = query;
        if (query) {
            model = new ArrayList<T>();
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
        T key;

        key = getRowAt(row);
        return (endUserExceptions != null && (endUserExceptions.containsKey(key) && endUserExceptions.get(key)
                                                                                                     .containsKey(col))) ||
               (validateExceptions != null && (validateExceptions.containsKey(key) && validateExceptions.get(key)
                                                                                                        .containsKey(col)));
    }

    public void addException(T row, int col, Exception error) {
        if (rowIndex != null && rowIndex.containsKey(row))
            addException(rowIndex.get(row).model, col, error);
        else
            addException(model.indexOf(row), col, error);
    }

    /**
     * Adds a manual Exception to the widgets exception list.
     */
    public void addException(int row, int col, Exception error) {
        getEndUserExceptionList(row, col).add(error);
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
        T row;

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
            validateExceptions = new HashMap<T, HashMap<Integer, ArrayList<Exception>>>();
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
        T key;

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
        T key;

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
    private ArrayList<Exception> getEndUserExceptionList(int row, int col) {
        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;
        ArrayList<Exception> list = null;
        T key;

        key = getRowAt(row);
        if (endUserExceptions == null)
            endUserExceptions = new HashMap<T, HashMap<Integer, ArrayList<Exception>>>();

        cellExceptions = endUserExceptions.get(key);

        if (cellExceptions == null) {
            cellExceptions = new HashMap<Integer, ArrayList<Exception>>();
            endUserExceptions.put(key, cellExceptions);
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
        T key;

        key = getRowAt(row);
        if (validateExceptions == null)
            validateExceptions = new HashMap<T, HashMap<Integer, ArrayList<Exception>>>();

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
    protected void drawExceptions(int row, int col, int x, int y) {
        if (row == editingRow && col == editingCol)
            return;

        Balloon.drawExceptions(getEndUserExceptions(row, col),
                                       getValidateExceptions(row, col),
                                       x,
                                       y);
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

        finishEditing();

        for (int col = 0; col < getColumnCount(); col++ ) {
            if (getColumnAt(col).isRequired()) {
                for (int row = 0; row < getRowCount(); row++ ) {
                    if (getValueAt(row, col) == null) {
                        exceptions = getValidateExceptionList(row, col);
                        exceptions.add(new Exception(Messages.get().exc_fieldRequired()));
                        setValidateException(row, col, exceptions);
                        render = true;
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
    public List<T> getValue() {
        return getModel();
    }

    /**
     * Sets the model as part of the HasValue interface
     */
    public void setValue(List<T> value) {
        setValue(value, false);
    }

    /**
     * Sets the model and will fire ValueChangeEvent if fireEvents is true as
     * part of the HasValue interface
     */
    public void setValue(List<T> value, boolean fireEvents) {
        setModel(value);

        if (fireEvents)
            ValueChangeEvent.fire(this, value);

    }

    /**
     * Handler Registration for ValueChangeEvent
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<T>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void addException(Exception exception) {

    }

    public void addExceptionStyle() {

    }

    public ArrayList<Exception> getEndUserExceptions() {
        return null;
    }

    public ArrayList<Exception> getValidateExceptions() {
        return null;
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
            if (comparator != null)
                return dir * comparator.compare(o1.getCell(col), o2.getCell(col));
            return dir * ((Comparable)o1.getCell(col)).compareTo((Comparable)o2.getCell(col));
        };
    }

    public class UniqueFilter implements Filter {
        int                           column;
        ArrayList<FilterChoice>       choices;
        HashMap<Object, FilterChoice> values;

        public ArrayList<FilterChoice> getChoices(List<? extends Row> model) {
            Object value;
            FilterChoice choice;
            CellRenderer renderer;

            if (values == null) {
                values = new HashMap<Object, FilterChoice>();
                choices = new ArrayList<FilterChoice>();
            }

            renderer = getColumnAt(column).getCellRenderer( -1);
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
        // TODO Auto-generated method stub

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

    public void onResize(int width, int height) {
        setWidth(width - 16);

        int rows;

        if (view.rowHeight() > 0)
            rows = height / view.rowHeight();
        else
            rows = height / rowHeight;

        if (rows != getVisibleRows())
            setVisibleRows(rows);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                setHeight(view.asWidget().getOffsetHeight() + "px");
            }
        });

    }

    public void onResize() {
        Element parent = getParent() instanceof LayoutPanel ? ((LayoutPanel)getParent()).getWidgetContainerElement(this)
                                                            : getParent().getElement();

        int width = parent.getOffsetWidth();
        int height = parent.getOffsetHeight();
         
        if(view instanceof StaticView) {
            
            //view.setHeight(height+"px");
            ((StaticView)view).inner.setSize(width+"px", height+"px");
            setWidth(width);
            //layout();
            return;
        }
        
        if (view.rowHeight() <= 0) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                @Override
                public void execute() {
                    onResize();
                }
            });
            return;
        }



        if(height > 0) {
            if (hasHeader)
                height -= view.getHeader().getOffsetHeight();
            if (horizontalScroll != Scrolling.NEVER)
                height -= ((View)view).scrollBarHeight;

            onResize(width, height);
        }
    }

}
