package org.openelis.ui.widget.table;

import java.util.ArrayList;
import java.util.Comparator;

import org.openelis.ui.widget.CSSUtils;
import org.openelis.ui.widget.table.Table.UniqueFilter;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellClickedEvent;
import org.openelis.ui.widget.table.event.CellClickedHandler;
import org.openelis.ui.widget.table.event.CellDoubleClickedEvent;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.HasBeforeCellEditedHandlers;
import org.openelis.ui.widget.table.event.HasCellClickedHandlers;
import org.openelis.ui.widget.table.event.HasCellEditedHandlers;
import org.openelis.ui.widget.table.event.HasUnselectionHandlers;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.LayoutPanel;

public abstract class Controller extends FocusPanel implements HasBeforeSelectionHandlers<Integer>, 
															   HasSelectionHandlers<Integer>,
                                                               HasUnselectionHandlers<Integer>,
                                                               HasBeforeCellEditedHandlers,
                                                               HasCellEditedHandlers,
                                                               HasCellClickedHandlers {
	
	protected boolean enabled,editing,multiSelect,hasHeader,queryMode;
	protected int rowHeight, editingRow = -1, editingCol = -1, viewWidth = -1,totalColumnWidth;

	protected ArrayList<Integer> selections = new ArrayList<Integer>(5);
	
	/**
	 * Arrays for determining relative X positions for columns
	 */
	protected short[] xForColumn, columnForX;
	
	protected final void setKeyHandling() {
		/*
		 * This Handler takes care of all key events on the table when editing
		 * and when only selection is on
		 */
		addDomHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				int row, col, keyCode;

				if (!isEnabled())
					return;
				keyCode = event.getNativeEvent().getKeyCode();
				row = editingRow;
				col = editingCol;

				if (isEditing()
						&& getColumnAt(col).getCellEditor().ignoreKey(keyCode))
					return;

				switch (keyCode) {
				case (KeyCodes.KEY_TAB):

					// Ignore if no cell is currently being edited
					if (!editing)
						break;

					// Tab backwards if shift pressed otherwise tab forward
					if (!event.isShiftKeyDown()) {
						while (true) {
							col++;
							if (col >= getColumnCount()) {
								col = 0;
								row++;
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
							col--;
							if (col < 0) {
								col = getColumnCount() - 1;
								row--;
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
					if (!isEditing()) {
						if (selections.size() > 0) {
							row = selections.get(0);
							while (true) {
								row++;
								if (row >= getRowCount())
									break;

								setSelection(row, event.getNativeEvent());

								if (isRowSelected(row))
									break;
							}
						}
						break;
					}
					// If editing set focus to the same col cell in the next
					// selectable row below
					while (true) {
						row++;
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
					if (!isEditing()) {
						if (selections.size() > 0) {
							row = selections.get(0);
							while (true) {
								row--;
								if (row < 0)
									break;

								setSelection(row, event.getNativeEvent());

								if (isRowSelected(row))
									break;
							}
						}
						break;
					}
					// If editing set focus to the same col cell in the next
					// selectable row above
					while (true) {
						row--;
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

					if (getRowCount() == 0)
						return;

					// If not editing and a row is selected, focus on first
					// editable cell
					if (!(selections.size() > 0))
						row = 0;
					else
						row = selections.get(0);
					col = 0;
					while (col < getColumnCount()) {
						if (startEditing(row, col, event.getNativeEvent()))
							break;
						col++;
					}
					break;
				}
			}
		}, KeyDownEvent.getType());

		addDomHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				// removeStyleName(UIResources.INSTANCE.text().Focus());
			}
		}, BlurEvent.getType());

		addDomHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				// addStyleName(UIResources.INSTANCE.text().Focus());
			}
		}, FocusEvent.getType());
	}
	
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;

	}

	public final boolean isEnabled() {
		return enabled;
	}
	
	public final boolean isEditing() {
		return editing;
	}
	
	
	public final boolean isMultipleSelectionAllowed() {
		return multiSelect;
	}


	public final void setAllowMultipleSelection(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}
	
	public final int getWidthWithoutScrollbar() {
		if (viewWidth < 0 && totalColumnWidth == 0 && getParent() != null) {
			if (getParent() instanceof LayoutPanel)
				return ((LayoutPanel) getParent()).getWidgetContainerElement(
						this).getOffsetWidth()
						- (int) Math.ceil(CSSUtils
								.getAddedBorderWidth(getElement()));
			else
				return getParent().getOffsetWidth()
						- (int) Math.ceil(CSSUtils
								.getAddedBorderWidth(getElement()));
		}
		return (viewWidth == -1 ? totalColumnWidth : viewWidth)
				- (int) Math.ceil(CSSUtils.getAddedBorderWidth(getElement()));
	}
	
	/**
	 * Returns the currently used Row Height for the table layout
	 */
	public final int getRowHeight() {
		return rowHeight;
	}

	/**
	 * Sets the Row Height to be used in the table layout.
	 * 
	 * @param rowHeight
	 */
	public final void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
		layout();
	}
	
	public final int getTotalColumnWidth() {
		return totalColumnWidth;
	}
	
	/**
	 * Method computes the XForColumn and ColumForX arrays and set the
	 * totoalColumnWidth
	 */
	protected final void computeColumnsWidth() {
		int from, to;

		//
		// compute total width
		//
		totalColumnWidth = 0;
		int xmark = 0;
		xForColumn = new short[getColumnCount()];
		for (int i = 0; i < getColumnCount(); i++) {
			if (getColumnAt(i).isDisplayed()) {
				xForColumn[i] = (short) xmark;
				xmark += getColumnAt(i).getWidth();
			}
			totalColumnWidth += getColumnAt(i).getWidth();
		}
		//
		// mark the array
		//
		from = 0;
		columnForX = new short[xmark];
		for (int i = 0; i < getColumnCount(); i++) {
			if (getColumnAt(i).isDisplayed()) {
				to = from + getColumnAt(i).getWidth();
				while (from < to && from + 1 < xmark)
					columnForX[from++] = (short) i;
			}
		}
	}
	
	/**
	 * Returns the X coordinate on the Screen of the Column passed.
	 * 
	 * @param index
	 * @return
	 */
	public final int getXForColumn(int index) {
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
	public final int getColumnForX(int x) {
		if (columnForX != null && x >= 0 && x < columnForX.length)
			return columnForX[x];
		return -1;
	}
	
	/**
	 * Sets whether the table as a header or not.
	 */
	public final void setHeader(boolean hasHeader) {
		this.hasHeader = hasHeader;
	}

	/**
	 * Used to determine if table has header
	 * 
	 * @return
	 */
	public final boolean hasHeader() {
		return hasHeader;
	}
	
	protected final boolean fireBeforeSelectionEvent(int index) {
		BeforeSelectionEvent<Integer> event = null;

		if (!queryMode)
			event = BeforeSelectionEvent.fire(this, index);

		return event == null || !event.isCanceled();
	}
	
	protected final boolean fireSelectionEvent(int index) {

		if (!queryMode)
			SelectionEvent.fire(this, index);

		return true;
	}
	

	protected final void fireUnselectEvent(int index) {

		if (!queryMode)
			UnselectionEvent.fire(this, index);
	}
	
	protected final boolean fireBeforeCellEditedEvent(int row, int col, Object val) {
		BeforeCellEditedEvent event = null;

		if (!queryMode)
			event = BeforeCellEditedEvent.fire(this, row, col, val);

		return event == null || !event.isCancelled();
	}
	
	protected final boolean fireCellEditedEvent(int row, int col) {

		if (!queryMode)
			CellEditedEvent.fire(this, row, col);

		return true;
	}
	
	public final boolean fireCellClickedEvent(int row, int col, boolean ctrlKey,
			boolean shiftKey) {
		CellClickedEvent event = null;

		if (!queryMode)
			event = CellClickedEvent.fire(this, row, col, ctrlKey, shiftKey);

		return event == null || !event.isCancelled();

	}
	
	protected final void fireCellDoubleClickedEvent(int row, int col) {
		if (!queryMode)
			CellDoubleClickedEvent.fire(this, row, col);
	}
	
	/**
	 * Returns the current row where cell is being edited
	 * 
	 * @return
	 */
	public final int getEditingRow() {
		return editingRow;
	}

	/**
	 * Returns the current column where cell is being edited
	 * 
	 * @return
	 */
	public final int getEditingCol() {
		return editingCol;
	}
	
	/**
	 * Registers a BeforeSelectionHandler to this Table
	 */
	public final HandlerRegistration addBeforeSelectionHandler(
			BeforeSelectionHandler<Integer> handler) {
		return addHandler(handler, BeforeSelectionEvent.getType());
	}

	/**
	 * Registers a SelectionHandler to this Table
	 */
	public final HandlerRegistration addSelectionHandler(
			SelectionHandler<Integer> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	/**
	 * Registers an UnselectionHandler to this Table
	 */
	public final HandlerRegistration addUnselectionHandler(
			UnselectionHandler<Integer> handler) {
		return addHandler(handler, UnselectionEvent.getType());
	}

	/**
	 * Registers a BeforeCellEditedHandler to this Table
	 */
	public final HandlerRegistration addBeforeCellEditedHandler(
			BeforeCellEditedHandler handler) {
		return addHandler(handler, BeforeCellEditedEvent.getType());
	}

	/**
	 * Registers a CellEditedHandler to this Table
	 */
	public final HandlerRegistration addCellEditedHandler(CellEditedHandler handler) {
		return addHandler(handler, CellEditedEvent.getType());
	}
	
	/**
	 * Register a CellClickedHandler to this Table
	 */
	public final HandlerRegistration addCellClickedHandler(CellClickedHandler handler) {
		return addHandler(handler, CellClickedEvent.getType());
	}

	public final HandlerRegistration addCellDoubleClickedHandler(
			CellDoubleClickedEvent.Handler handler) {
		return addHandler(handler, CellDoubleClickedEvent.getType());
	}

	protected abstract void setSelection(int row, NativeEvent event);
	protected abstract void cancelBalloonTimer();
	protected abstract boolean startEditing(int row, int col, NativeEvent event);
	protected abstract int getColumnCount();
	protected abstract Column getColumnAt(int index);
	protected abstract void finishEditing();
	protected abstract int getRowCount();
	protected abstract <T extends Row> T getRowAt(int row);
	public abstract <T> T getValueAt(int row, int column);
	public abstract boolean isRowSelected(int row);
	public abstract boolean hasExceptions(int row, int column);
	protected abstract void drawExceptions(int row, int column, int x, int y);
	protected abstract ArrayList<Exception> getEndUserExceptions(int row, int column);
	protected abstract ArrayList<Exception> getValidateExceptions(int row, int column);
	protected abstract boolean getQueryMode();
	protected abstract <T extends Row> ArrayList<T>  getModel();
	protected abstract ViewInt view();
	protected abstract void applyFilters();
	protected abstract void applySort(int col, int dir, Comparator<? super Row> sort);
	protected abstract UniqueFilter newFilter();
	protected abstract void layout();
	protected abstract void resize();
}
