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
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.TableCSS;
import org.openelis.ui.widget.Balloon;
import org.openelis.ui.widget.Balloon.Options;
import org.openelis.ui.widget.Balloon.Placement;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.HasBalloon;
import org.openelis.ui.widget.HasExceptions;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.ScreenWidgetInt;
import org.openelis.ui.widget.cell.Cell;
import org.openelis.ui.widget.cell.CellCheckbox;
import org.openelis.ui.widget.cell.CellMouseOverEvent;
import org.openelis.ui.widget.table.event.BeforeRowAddedEvent;
import org.openelis.ui.widget.table.event.BeforeRowAddedHandler;
import org.openelis.ui.widget.table.event.BeforeRowDeletedEvent;
import org.openelis.ui.widget.table.event.BeforeRowDeletedHandler;
import org.openelis.ui.widget.table.event.FilterEvent;
import org.openelis.ui.widget.table.event.FilterHandler;
import org.openelis.ui.widget.table.event.HasBeforeRowAddedHandlers;
import org.openelis.ui.widget.table.event.HasBeforeRowDeletedHandlers;
import org.openelis.ui.widget.table.event.HasFilterHandlers;
import org.openelis.ui.widget.table.event.HasRowAddedHandlers;
import org.openelis.ui.widget.table.event.HasRowDeletedHandlers;
import org.openelis.ui.widget.table.event.RowAddedEvent;
import org.openelis.ui.widget.table.event.RowAddedHandler;
import org.openelis.ui.widget.table.event.RowDeletedEvent;
import org.openelis.ui.widget.table.event.RowDeletedHandler;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;



/**
 * This class is used by screens and widgets such as AutoComplete and Dropdown
 * to display information in a Table grid format
 * 
 * @author tschmidt
 * 
 */
public class Table<T> extends Controller implements ScreenWidgetInt, Queryable,		
												 RequiresResize,  HasBeforeRowAddedHandlers,
												 HasRowAddedHandlers, HasBeforeRowDeletedHandlers,
												 HasRowDeletedHandlers, 
												 HasExceptions, Focusable,FocusHandler, HasFilterHandlers, HasBalloon {			

	protected ArrayList<T> model,modelView;
	protected TableDataProvider<T> dataProvider;
	protected HashMap<T, RowIndexes> rowIndex;
	protected Timer balloonTimer;
	protected ArrayList<Column> columns;
	protected HashMap<T, HashMap<Integer, ArrayList<Exception>>> endUserExceptions,validateExceptions;
	protected boolean hasFocus, unitTest, ctrlDefault;
	protected TableView<T> view;
	protected CellTipProvider<?> tipProvider;
	protected Options toolTip;
	public static final int SORT_ASCENDING = 1, SORT_DESCENDING = -1;
	protected Table<T> source = this;
	protected int tipRow, tipCol;

	public static class Builder<T> {
		int rowHeight = 20;
		Integer width;
		boolean multiSelect, hasHeader;
		ArrayList<Column> columns = new ArrayList<Column>(5);

		public Builder<T> rowHeight(int rowHeight) {
			this.rowHeight = rowHeight;
			return this;
		}

		public Builder<T> multiSelect(boolean multiSelect) {
			this.multiSelect = multiSelect;
			return this;
		}

		public Builder<T> hasHeader(boolean hasHeader) {
			this.hasHeader = hasHeader;
			return this;
		}

		public Builder<T> column(Column col) {
			columns.add(col);
			return this;
		}

		public Builder<T> width(Integer width) {
			this.width = width;
			return this;
		}

		public Table<T> build() {
			return new Table<T>(this);
		}
	}

	public Table() {
		rowHeight = 20;
		multiSelect = false;
		columns = new ArrayList<Column>(5);
		view = new TableView<T>(this);
		setWidget(view);
		setKeyHandling();
	}

	public Table(Builder<T> builder) {
		rowHeight = builder.rowHeight;
		multiSelect = builder.multiSelect;
		hasHeader = builder.hasHeader;
		view = new TableView<T>(this);
		setWidget(view);

		if (builder.width != null)
			setWidth(builder.width.intValue());

		setColumns(builder.columns);
		setKeyHandling();
	}

	// ********* Table Definition Methods *************



	/**
	 * Returns the data model currently being displayed by this table. The
	 * return value is parameterized so specific models can be used that extend
	 * the basic Row such as Item in AutoCompete and Dropdown
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<T> getModel() {
		return model;
	}

	
	@SuppressWarnings("unchecked")
	public void setModel(ArrayList<T> model) {
		finishEditing();
		unselectAll();

		this.model = model;
		modelView = null;
		rowIndex = null;
		
		checkExceptions();

		// Clear any filter choices that may have been in force before model
		// changed
		for (Column col : columns) {
			if (col.getFilter() != null)
				col.getFilter().unselectAll();
		}

		if (queryMode) {
			renderView(-1, 1);
		} else {
			view.bulkRender();

			if (endUserExceptions != null)
				view.bulkExceptions(endUserExceptions);
		}	
	}
	
	public void setDataProvider(TableDataProvider<T> dataProvider) {
		this.dataProvider = dataProvider;
	}
	
	public TableDataProvider<T> getDataProvider() {
		return dataProvider;
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

		if (dataProvider == null)
			return;

		finishEditing();

		/*
		 * if no filters are in force revert modelView back to model and return;
		 */
		if (filters == null || filters.size() == 0) {
			modelView = null;
			rowIndex = null;
			view.bulkRender();

			if (hasExceptions())
				view.bulkExceptions(endUserExceptions);

			if (isAnyRowSelected()) {
				for (Integer index : selections)
					view.applySelectionStyle(index);
			}

			return;
		}

		/*
		 * Reset the modelView and the rowIndex hash
		 */
		modelView = new ArrayList<T>();
		rowIndex = new HashMap<T, RowIndexes>();
		for (int i = 0; i < model.size(); i++)
			rowIndex.put(model.get(i), new RowIndexes(i, -1));

		/*
		 * Run through model and filter out rows
		 */
		for (int i = 0; i < model.size(); i++) {
			include = true;
			for (Filter filter : filters) {
				if (filter != null
						&& filter.isFilterSet()
						&& !filter.include(getModelValue(i,filter.getColumn()))) {
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
			modelView = null;
			rowIndex = null;
		}

		// if ( !scrollToVisible(0))
		view.bulkRender();

		if (hasExceptions())
			view.bulkExceptions(endUserExceptions);

		if (isAnyRowSelected()) {
			for (Integer index : selections)
				view.applySelectionStyle(convertModelIndexToView(index));
		}
	}

	private <V> V getModelValue(int row, int col) {
		T data;
		
		data = model.get(row);
		if (dataProvider != null) {
			return (V)dataProvider.getValue(col,data);
		} else if (data instanceof Row) {
			return ((Row)data).getCell(col);
		} else {
			throw new RuntimeException("DataProvider not set for table");
		}
	}
	
	private <V> void setModelValue(int row, int col, V value) {
		T data;
		
		data = model.get(row);
		if (dataProvider != null) {
			dataProvider.setValue(col, data, value);
		} else if (data instanceof Row) {
			((Row)data).setCell(col, value);
		} else {
			throw new RuntimeException("DataProvider not set for table");
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

		for (int i = row; i < modelView.size(); i++)
			rowIndex.get(modelView.get(i)).view += adj;

		for (int i = modelIndex; i < model.size(); i++) {
			r = rowIndex.get(model.get(i));
			if (r != null)
				r.model += adj;
		}

		for (int i = 0; i < selections.size(); i++) {
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
		if (modelView == null) {
			modelView = new ArrayList<T>();
			rowIndex = new HashMap<T, RowIndexes>();
			for (int i = 0; i < model.size(); i++) {
				modelView.add(model.get(i));
				rowIndex.put(model.get(i), new RowIndexes(i, -1));
			}
		}
		
		Collections.sort(modelView, new Sort(col, dir, comp));

		/*
		 * Set the view index of the hash based on the sort
		 */
		for (int i = 0; i < modelView.size(); i++)
			rowIndex.get(modelView.get(i)).view = i;

		view.bulkRender();

		if (hasExceptions())
			view.bulkExceptions(endUserExceptions);

		if (isAnyRowSelected()) {
			for (Integer index : selections)
				view.applySelectionStyle(convertModelIndexToView(index));
		}
	}

	/**
	 * Returns the current size of the held model. Returns zero if a model has
	 * not been set.
	 * 
	 * @return
	 */
	public int getRowCount() {
		if (modelView != null) {
			try {
				return modelView.size();
			} catch (Exception e) {
				return 0;
			}
		} else if (model != null) {
			return model.size();
		} 
		return 0;
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
		setWidth(org.openelis.ui.common.Util.stripUnits(width));
	}

	/**
	 * Returns the currently set view width for the Table
	 * 
	 * @return
	 */
	//public int getWidth() {
	//	return viewWidth;
	//}
	

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
		for (int i = 0; i < columns.size(); i++) {
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
		col.setController(this);
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
		return (W) (index > -1 ? getColumnAt(index).getCellEditor().getWidget()
				: null);
	}

	/**
	 * This method will return the column used in the table by it's name
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <W extends Widget> W getColumnWidget(String name) {
		return (W) getColumnWidget(getColumnByName(name));
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
				column.setController(this);
		}

		layout();
	}

	/**
	 * Creates and Adds a Column at the end of the column list with passed name
	 * and header label in the params.
	 * 
	 * @param name
	 *            Name of the column for reference
	 * @param label
	 *            Label used in Table header.
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
	 *            Index in the Column list where to insert the new Column
	 * @param name
	 *            Name used in the Column as a reference to the Column.
	 * @param label
	 *            Label used in the Table header.
	 * @return The newly created and added Column.
	 */
	public Column addColumnAt(int index, String name, String label, int width) {
		Column column;

		column = new Column.Builder(width).name(name).label(label).build();
		addColumnAt(index, column);
		column.setController(this);
		return column;
	}

	/**
	 * Creates and adds a new Column at passed index
	 * 
	 * @param index
	 *            Index in the Column list where to insert the new Column.
	 * @return The newly created and added column.
	 */
	public Column addColumnAt(int index) {
		return addColumnAt(index, "", "", 75);
	}

	public void addColumnAt(int index, Column column) {
		columns.add(index, column);
		column.setController(this);
		if (model != null) {
			for (T row : model) {
				((Row)row).cells.add(index, null);
			}
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
            for (T row : model) {
                ((Row)row).cells.remove(index);
            }
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
	public T addRow() {
		return addRow(getRowCount(), null);
	}

	/**
	 * Creates a new blank Row and inserts it in the table model at the passed
	 * index.
	 * 
	 * @param index
	 * @return
	 */
	public T addRowAt(int index) {
		return addRow(index, null);
	}

	/**
	 * Adds the passed Row to the end of the Table model.
	 * 
	 * @param row
	 * @return
	 */
	public T addRow(T row) {
		return addRow(getRowCount(), row);
	}

	/**
	 * Adds the passed Row into the Table model at the passed index.
	 * 
	 * @param index
	 * @param row
	 * @return
	 */
	public T addRowAt(int index, T row) {
		return (T) addRow(index, row);
	}

	/**
	 * Private method called by all public addRow methods to handle event firing
	 * and add the new row to the model.
	 * 
	 * @param index
	 *            Index where the new row is to be added.
	 * @param row
	 *            Will be null if a Table should create a new blank Row to add
	 *            otherwise the passed Row will be added.
	 * @return Will return null if this action is canceled by a
	 *         BeforeRowAddedHandler, otherwise the newly created Row will be
	 *         returned or if a Row is passed to the method it will echoed back.
	 */
	@SuppressWarnings("unchecked")
	private T addRow(int index, T row) {
		int modelIndex;

		finishEditing();

		if (row == null)
			row = (T) new Row(columns.size());

		if (!fireBeforeRowAddedEvent(index, row))
			return null;

		/* if a model has not been set need to create an empty model */
		if (dataProvider == null) {
			setModel(new ArrayList<T>());
		}

		/* Add row to model and then to view */
		if (rowIndex != null) {
			modelIndex = convertViewIndexToModel(index);
			model.add(modelIndex, row);
			rowIndex.put(row, new RowIndexes(modelIndex, index));
			adjustRowIndexes(modelIndex + 1, index, 1);
			modelView.add(index, row);
		} else {
			model.add(index, row);
		}

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
	public T removeRowAt(int index) {
		int modelIndex;
		T row;

		finishEditing();

		unselectRowAt(index);

		row = getRowAt(index);

		if (!fireBeforeRowDeletedEvent(index, row))
			return null;

		if (rowIndex != null) {
			modelIndex = convertViewIndexToModel(index);
			model.remove(modelIndex);
			rowIndex.remove(row);
			adjustRowIndexes(modelIndex, index + 1, -1);
		}
		modelView.remove(index);

		view.removeRow(index);

		if (endUserExceptions != null) {
			endUserExceptions.remove(row);
			if (endUserExceptions.size() == 0)
				endUserExceptions = null;
		}

		if (validateExceptions != null) {
			validateExceptions.remove(row);
			if (validateExceptions.size() == 0)
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
		dataProvider = null;
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
	public T getRowAt(int row) {
		if (row < 0 || row >= getRowCount())
			return null;
		return model.get(convertViewIndexToModel(row));
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
		setSelection(index, null);
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
	protected void setSelection(int row, NativeEvent event) {

		if (row < 0) {
			unselectAll();
			return;
		}

		/*
		 * If multiple selection is allowed check event for ctrl or shift keys.
		 * If none apply the logic will fall throw to normal selection.
		 */
		if (isMultipleSelectionAllowed()) {
			if (ctrlDefault
					|| (event != null && Event.getTypeInt(event.getType()) == Event.ONCLICK)) {
				multiSelect(row, event);
				return;
			}
		}

		if (isRowSelected(row))
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
			if (!isAnyRowSelected()) {
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
						i++;
					startSelect = selections.get(i);
					endSelect = row;
				}
			}
			unselectAll(event);
		} else
			unselectAll(event);

		for (i = startSelect; i <= endSelect && i > -1; i++) {
			if (!selections.contains(i)) {
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
			for (int i = 0; i < getRowCount(); i++) {
				selections.add(i);
				view.applySelectionStyle(i);
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
		for (int i = 0; i < count; i++)
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
	 * Private method that fires a BeforeRowAddedEvent for the passed index and
	 * Row. Returns false if the addition is canceled by a registered handler
	 * and true if the addition is allowed.
	 * 
	 * @param index
	 * @param row
	 * @return
	 */
	private boolean fireBeforeRowAddedEvent(int index, T row) {
		BeforeRowAddedEvent event = null;

		if (!queryMode)
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

		if (!queryMode)
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

		if (!queryMode)
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
	private boolean fireRowDeletedEvent(int index, T row) {

		if (!queryMode)
			RowDeletedEvent.fire(this, index, row);

		return true;
	}

	/**
	 * Fires a Filter event after this table has been filtered and the new model
	 * is displayed.
	 */
	protected void fireFilterEvent() {
		FilterEvent.fire(this);
	}

	// ********* Edit Table Methods *******************


	public <T> void setValueAt(int row, int col, T value) {
		setValueAt(row,col,value,true);
	}
	
	/**
	 * Sets the value of a cell in Table model.
	 * 
	 * @param row
	 * @param col
	 * @param value
	 */
	protected <V> void setValueAt(int row, int col, V value, boolean refresh) {
		Column column;
		ArrayList<Exception> exceptions;

		finishEditing();
		setModelValue(row, col, value);

		column = getColumnAt(col);
		
		exceptions = null;
		if(column.getCellEditor() != null) {
			exceptions = column.getCellEditor().validate(value);
		}

		if (!queryMode) {
			if (column.isRequired() && value == null) {
				if (exceptions == null)
					exceptions = new ArrayList<Exception>();
				exceptions.add(new Exception(Messages.get().exc_fieldRequired()));
			}
		}
		
		setValidateException(row, col, exceptions);
		if(hasExceptions(row,col))
			view.renderCellException(row, col);
		else
			view.clearCellException(row, col);
		
		if (refresh) {
			refreshCell(row, col);
		}
	}

	/**
	 * Sets a row in the model at the passed index and refreshes the view.
	 * 
	 * @param index
	 * @param row
	 */
	public void setRowAt(int index, T row) {
		T oldRow;
		
		finishEditing();
		oldRow = getRowAt(index);
		if (modelView != null) {
			modelView.set(index, row);
		}
		model.set(convertViewIndexToModel(index),row);
		rowIndex.put(row, rowIndex.get(oldRow));
		rowIndex.remove(oldRow);
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
		if (model == null || row >= model.size())
			return null;
		return (V) getModelValue(convertViewIndexToModel(row),col);
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
	public boolean startEditing(final int row, final int col,
			final NativeEvent event) {

		/*
		 * Return out if the table is not enable or the passed cell is already
		 * being edited
		 */
		if (!isEnabled() || (row == editingRow && col == editingCol)) {
			if (columns.get(col).<String>getCellEditor() instanceof CellCheckbox
					&& Event.getTypeInt(event.getType()) == Event.ONCLICK)
				ClickEvent.fireNativeEvent(event,
						((CheckBox) getColumnWidget(col)).getCheck());
			return false;
		}

		finishEditing();

		setSelection(row, event);

		// Check if the row was able to be selected, if not return.
		if (!isRowSelected(row))
			return false;

		// Check if column is editable otherwise return false
		if (!getColumnAt(col).hasEditor())
			return false;

		// Fire before cell edited event to allow user the chance to cancel
		if (!fireBeforeCellEditedEvent(row, col, getValueAt(row, col)))
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
		if (!editing)
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
		
		ArrayList<Exception> exceptions =  new ArrayList<Exception>();
		newValue = null;
		try {
			newValue = view.finishEditing(row, col);
			setValidateException(row,col,null);
		} catch (ValidationErrorsList e) {
			exceptions.addAll(e.getErrorList());
		}
		oldValue = getValueAt(row, col);
		setModelValue(convertViewIndexToModel(row), col, newValue);
		
		if (!queryMode) {
			if (getColumnAt(col).isRequired() && newValue == null) {
				exceptions.add(new Exception(Messages.get().exc_fieldRequired()));
			}
		}
		
		setValidateException(row,col,exceptions);
		
		if (hasExceptions(row, col)) {
			view.renderCellException(row, col);
		} else
			view.clearCellException(row, col);
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

		if (!isAttached()) {
			layout();
			return;
		}

		finishEditing();

		if (hasHeader)
			view.getHeader().resize();

		view.sizeTable();
	}

	/**
	 * Method will have to view re-compute its visible rows and refresh the view
	 * 
	 * @param startR
	 * @param endR
	 */
	protected void renderView(int startR, int endR) {
		view.renderView(startR, endR);
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

		if (!queryMode)
			return null;

		qds = new ArrayList<QueryData>();

		for (int i = 0; i < getColumnCount(); i++) {
			qd = getColumnAt(i).getCellEditor().getQuery();
			//qd = (QueryData) getValueAt(0, i);
			if (qd != null) {
				qd.setKey(getColumnAt(i).name);
				qds.add(qd);
			}
		}
		return qds;//.toArray(new QueryData[] {});
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
			row = (T)new Row(getColumnCount());
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
		T key;

		key = getRowAt(row);
		return (endUserExceptions != null && (endUserExceptions
				.containsKey(key) && endUserExceptions.get(key)
				.containsKey(col)))
				|| (validateExceptions != null && (validateExceptions
						.containsKey(key) && validateExceptions.get(key)
						.containsKey(col)));
	}

	public void addException(T row, int col, Exception error) {
		ArrayList<Exception> exceptions;
		int r;

		exceptions = getEndUserExceptionList(row, col);

		if (exceptions.contains(error))
			return;

		exceptions.add(error);

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

		if (exceptions.contains(error))
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
	protected void setValidateException(int rw, int col,
			ArrayList<Exception> errors) {

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
			if (!validateExceptions.containsKey(row)) {
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
			view.renderExceptions(-1, -1);
		}
	}

	public void clearEndUserExceptions() {
		if (endUserExceptions != null) {
			endUserExceptions = null;
			view.renderExceptions(-1, -1);
		}
	}

	public void clearValidateExceptions() {
		if (validateExceptions != null) {
			validateExceptions = null;
			view.renderExceptions(-1, -1);
		}
	}

	public void clearExceptions(T row, int col) {
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

	public  void clearEndUserExceptions(T row, int col) {

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
	private ArrayList<Exception> getEndUserExceptionList(T row, int col) {
		HashMap<Integer, ArrayList<Exception>> cellExceptions = null;
		ArrayList<Exception> list = null;

		if (endUserExceptions == null)
			endUserExceptions = new HashMap<T, HashMap<Integer, ArrayList<Exception>>>();

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
	public void drawExceptions(final int row, final int col, final int x,
			final int y) {
		if (row == editingRow && col == editingCol)
			return;

		balloonTimer = new Timer() {
			public void run() {

				Balloon.drawExceptions(getEndUserExceptions(row, col),
						getValidateExceptions(row, col), view.grid()
								.getCellFormatter().getElement(row, col), x, y);
			}
		};
		balloonTimer.schedule(500);
	}

	// ********* Registration of Handlers ******************
	/**
	 * Registers a BeforeRowAddedHandler to this Table
	 */
	public HandlerRegistration addBeforeRowAddedHandler(
			BeforeRowAddedHandler handler) {
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
	public HandlerRegistration addBeforeRowDeletedHandler(
			BeforeRowDeletedHandler handler) {
		return addHandler(handler, BeforeRowDeletedEvent.getType());
	}

	/**
	 * Registers a RowDeletedHandler to this Table
	 */
	public HandlerRegistration addRowDeletedHandler(RowDeletedHandler handler) {
		return addHandler(handler, RowDeletedEvent.getType());
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

		if (queryMode)
			return;

		for (int col = 0; col < getColumnCount(); col++) {
			if (getColumnAt(col).isRequired()) {
				for (int row = 0; row < getRowCount(); row++) {
					if (getValueAt(row, col) == null) {
						exceptions = getValidateExceptionList(row, col);
						exception = new Exception(Messages.get()
								.exc_fieldRequired());
						if (!exceptions.contains(exception)) {
							exceptions.add(exception);
							setValidateException(row, col, exceptions);
							render = true;
						}
					}
				}
			}
		}

		if (render)
			view.renderExceptions(-1, -1);
	}

//	/**
//	 * Returns the model as part of the HasValue interface
//	 */
//	public <T> ArrayList<T> getValue() {
//		return getModel();
//	}
//
//	/**
//	 * Sets the model as part of the HasValue interface
//	 */
//	public void setValue(ArrayList<? extends Row> value) {
//		setValue(value, false);
//	}
//
//	/**
//	 * Sets the model and will fire ValueChangeEvent if fireEvents is true as
//	 * part of the HasValue interface
//	 */
//	public void setValue(ArrayList<? extends Row> value, boolean fireEvents) {
//		setModel(value);
//
//		if (fireEvents)
//			ValueChangeEvent.fire(this, value);
//
//	}

	/**
	 * Handler Registration for ValueChangeEvent
	 */
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<ArrayList<? extends Row>> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void addException(Exception exception) {

	}

	public void addExceptionStyle() {

	}

	public void checkExceptions() {
		if (endUserExceptions != null) {
			for (T row : endUserExceptions.keySet()) {
				if (dataProvider == null || !model.contains(row))
					endUserExceptions.remove(row);
			}

			if (endUserExceptions.size() == 0)
				endUserExceptions = null;
		}

		if (validateExceptions != null) {
			for (T row : validateExceptions.keySet()) {
				if (dataProvider == null || !model.contains(row))
					validateExceptions.remove(row);
			}

			if (validateExceptions.size() == 0)
				validateExceptions = null;
		}

	}

	public ArrayList<Exception> getEndUserExceptions() {
		ArrayList<Exception> exceptions;

		if (endUserExceptions == null)
			return null;

		exceptions = new ArrayList<Exception>();

		for (HashMap<Integer, ArrayList<Exception>> row : endUserExceptions
				.values()) {
			for (ArrayList<Exception> excs : row.values()) {
				exceptions.addAll(excs);
			}
		}

		return exceptions;
	}

	public ArrayList<Exception> getValidateExceptions() {
		ArrayList<Exception> exceptions;

		if (validateExceptions == null)
			return null;

		exceptions = new ArrayList<Exception>();

		for (HashMap<Integer, ArrayList<Exception>> row : validateExceptions
				.values()) {
			for (ArrayList<Exception> excs : row.values()) {
				exceptions.addAll(excs);
			}
		}

		return exceptions;
	}

	public boolean hasExceptions() {
		validate();
		return (endUserExceptions != null && !endUserExceptions.isEmpty())
				|| (validateExceptions != null && !validateExceptions.isEmpty());
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

	private class Sort implements Comparator<T> {
		int col, dir;

		@SuppressWarnings("rawtypes")
		Comparator comparator;

		@SuppressWarnings("rawtypes")
		public Sort(int col, int dir, Comparator comparator) {
			this.col = col;
			this.dir = dir;
			this.comparator = comparator;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public int compare(T o1, T o2) {
			Comparable c1, c2;
			
			if (dataProvider == null) {
				c1 = ((Row)o1).getCell(col);
				c2 = ((Row)o2).getCell(col);
			} else {
				c1 = dataProvider.getValue(col, o1);
				c2 = dataProvider.getValue(col, o2);
			}

			if (comparator != null)
				return dir * comparator.compare(c1, c2);

			if (c1 == null && c2 == null)
				return 0;
			else if (c1 != null && c2 != null)
				return dir * (c1.compareTo(c2));
			else {
				if (c1 == null && c2 != null)
					return 1;
				else
					return -1;
			}
		};
	}

	public class UniqueFilter implements Filter {
		int column;
		ArrayList<FilterChoice> choices;
		HashMap<Object, FilterChoice> values;

		public ArrayList<FilterChoice> getChoices(ArrayList<?> model) {
			Object value;
			FilterChoice choice;
			Cell renderer;

			if (values == null) {
				values = new HashMap<Object, FilterChoice>();
				choices = new ArrayList<FilterChoice>();
			}

			renderer = getColumnAt(column).getCellRenderer();
			for (int i = 0; i <  model.size(); i++) {
				value = ((Row)model.get(i)).getCell(column);
				if (!values.containsKey(value)) {
					choice = new FilterChoice();
					values.put(value, choice);
					choice.setValue(value);
					choice.setDisplay(renderer.asString(value).toString());
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
		super.setFocus(focused);

	}

	@Override
	public void setTabIndex(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(IsWidget w) {
		assert w instanceof Column;

		((Column) w).setController(this);
		addColumn((Column) w);
	}

	public void onResize() {
		Element parent;

		if (!isAttached())
			return;

		parent = (Element) (getParent() instanceof LayoutPanel ? ((LayoutPanel) getParent())
				.getWidgetContainerElement(this) : getParent().getElement());

		int width = parent.getOffsetWidth();
		int height = parent.getOffsetHeight();

		view.setSize(width + "px", height + "px");
		view.onResize();

	}

	@Override
	public void setHeight(String height) {
		super.setHeight(height);
		onResize();
	}

	public void setTipProvider(CellTipProvider tipProvider) {
		this.tipProvider = tipProvider;

		if (toolTip == null)
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

		view.grid().addCellMouseOverHandler(new CellMouseOverEvent.Handler() {

			@Override
			public void onCellMouseOver(CellMouseOverEvent event) {
				tipRow = event.getRow();
				tipCol = event.getCol();
				final int x, y;

				Element td = view.grid().getCellFormatter()
						.getElement(event.getRow(), event.getCol());

				y = td.getAbsoluteTop();
				x = td.getAbsoluteLeft() + (td.getOffsetWidth() / 2);

				if (!hasExceptions(tipRow, tipCol)) {
					balloonTimer = new Timer() {
						public void run() {
							Balloon.show((HasBalloon) source, x, y);
						}
					};
					balloonTimer.schedule(500);
				}

			}
		});

		options.setTipProvider(new Balloon.TipProvider<Object>() {

			@Override
			public Object getTip(HasBalloon target) {

				if (tipProvider != null)
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
		hp.add(view.scrollView.getWidget());
		view.scrollView.setWidget(hp);
	}

	public void cancelBalloonTimer() {
		if (balloonTimer != null)
			balloonTimer.cancel();
	}
		
	public UniqueFilter newFilter() {
		return new UniqueFilter();
	}

	@Deprecated
	public void setVisibleRows(int rows) {
		//This is here to just allow screens to compile that haven't removed this
	}
	
	@Deprecated
	public void setVerticalScroll(String scroll) {
		//This is here to just allow screens to compile that haven't removed this
	}
	
	@Deprecated
	public void setHorizontalScroll(String scroll) {
		//This is here to just allow screens to compile that haven't removed this
	}
	

}
