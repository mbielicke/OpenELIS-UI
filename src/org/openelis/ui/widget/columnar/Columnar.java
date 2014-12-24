package org.openelis.ui.widget.columnar;

import java.util.ArrayList;
import java.util.Collections;

import org.openelis.ui.common.Util;
import org.openelis.ui.widget.CSSUtils;
import org.openelis.ui.widget.table.CellEditor;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.LabelCell;
import org.openelis.ui.widget.table.event.HasUnselectionHandlers;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class Columnar extends FocusPanel implements RequiresResize,HasBeforeSelectionHandlers<Integer>,
                                                    HasSelectionHandlers<Integer>,
                                                    HasUnselectionHandlers<Integer> {
    
    protected View view;
    
    protected int  lineHeight, totalColumnWidth, viewWidth = -1;
    
    protected ArrayList<DataItem> model;
    
    protected ArrayList<Line>    lines;
    
    protected short[] xForColumn, columnForX;
    
    protected HandlerRegistration visibleHandler;
    
    protected Columnar source = this;
    
    protected boolean hasHeader,multiSelect;
    
    protected ArrayList<Integer> selections = new ArrayList<Integer>(5);
    
    public Columnar() {
        lineHeight = 16;
        lines = new ArrayList<Line>(5);
        view = new View(this);
        setWidget(view);
    }
    
    public int getLineHeight() {
        return lineHeight;
    }
    
    public int getCellHeight() {
        return view.cellHeight;
    }
    
    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        layout();
    }
    
    @SuppressWarnings("unchecked")
    public <T extends DataItem> ArrayList<T> getModel() {
        return (ArrayList<T>)model;
    }
    
    @SuppressWarnings("unchecked")
    public void setModel(ArrayList<? extends DataItem> model) {
        this.model = (ArrayList<DataItem>)model;
        for(DataItem item : model)
            item.setColumnar(this);
        layout();
        renderView(-1,-1);
    }
    
    public int getDataItemCount() {
        if(model == null)
            return 0;
        
        return model.size();
    }
    
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
            return ((LayoutPanel)getParent()).getWidgetContainerElement(this).getOffsetWidth() - (int)CSSUtils.getAddedBorderWidth(getElement());
        return (viewWidth == -1 ? totalColumnWidth : viewWidth) - (int)CSSUtils.getAddedBorderWidth(getElement());
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
    
    public int getLineCount() {
        if(lines != null)
            return lines.size();
        
        return 0;
    }
    
    public Line getLineAt(int index) {
        return lines.get(index);
    }
   
    public int getLine(Line line) {
        return lines.indexOf(line);
    }
    
    public void setLineAt(int index, Line line) {
        line.setColumnar(this);
        lines.set(index, line);
        layout();
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Widget> T getLineWidget(int index) {
        return (T) (index > -1 ? ((CellEditor)getLineAt(index).getCellRenderer()).getWidget() : null);
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
    
    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
        
        if(lines != null) {
            for (Line line : lines) 
                line.setColumnar(this);
        }
        
        layout();
    }
    
    public Line addLine(String label) {
        return addLineAt(lines.size(), label);
    }
    
    public void addLine(Line line) {
        addLineAt(lines.size(), line);
    }
    
    public Line addLineAt(int index, String label) {
        Line line;
        
        line = new Line();
        line.setCellRenderer(new LabelCell());
        line.setLabel(label);
        
        addLineAt(index, line);
        
        return line;
    }
    
    public void addLineAt(int index, Line line) {
        lines.add(index, line);
        line.setColumnar(this);
        if(model != null) {
            for(DataItem item : model) 
                item.cells.add(index, null);
        }
        view.addLine(index);
    }
    
    public Line removeColumnAt(int index) {
        Line line;
        
        line = lines.remove(index);
        if(model != null) {
            for(DataItem item : model) 
                item.cells.remove(index);
        }
        view.removeLine(index);
        
        return line;
    }
    
    public void removeAllLines() {
        lines.clear();
        layout();
    }
    
    public <T extends DataItem> T addDataItem() {
        return addDataItem(getDataItemCount(),null);
    }
    
    public <T extends DataItem> T addDataItemAt(int index) {
        return addDataItem(index,null);
    }
    
    public <T extends DataItem> T addDataItem(T item) {
        return addDataItem(getDataItemCount(),item);
    }
    
    public <T extends DataItem> T addDataItemAt(int index, T item) {
        return addDataItem(index,item);
    }
    
    @SuppressWarnings("unchecked")
    private <T extends DataItem> T addDataItem(int index, T item) {
        
        if(item == null)
            item = (T)new DataItem(lines.size());
        
        if(model == null)
            setModel(new ArrayList<DataItem>());
        
        model.add(index,item);
        
        computeColumnsWidth();
        
        view.addDataItem(index);
        
        return item;
    }
    
    public <T extends DataItem> T removeDataItemAt(int index) {
        T item;
        
        item = getDataItemAt(index);
        
        model.remove(index);
        
        computeColumnsWidth();
        
        view.removeDataItem(index);
        
        return item;
    }
    
    public void removeAllDataItems() {
        model = null;
        view.removeAllDataItems();
        computeColumnsWidth();
    }
    
    @SuppressWarnings("unchecked")
    public <T extends DataItem> T getDataItemAt(int index) {
        if(index < 0 || index > getDataItemCount())
            return null;
        return (T)model.get(index);
    }
    
    public Object getValueAt(int d, int l) {
        return model.get(d).cells.get(l);
    }
    
    public void layout() {
        computeColumnsWidth();
        view.layout();
    }
    
    protected void resize() {
        computeColumnsWidth();
        
        if( !isAttached()) {
            layout();
            return;
        }
        
        if(hasHeader)
            view.header.resize();
        
        view.resize();
    }
    
    protected void renderView(int start, int end) {
        view.renderView(start,end);
    }
    
    private void computeColumnsWidth() {
        int from, to;
        
        totalColumnWidth = 0;
        int xmark = 0;
        xForColumn = new short[getDataItemCount()];
        for(int i = 0; i < getDataItemCount(); i++) {
            xForColumn[i] = (short)xmark;
            xmark += getDataItemAt(i).getWidth();
            totalColumnWidth += getDataItemAt(i).getWidth();
        }
        
        from = 0;
        columnForX = new short[xmark];
        for(int i = 0; i < getDataItemCount(); i++) {
            to = from + getDataItemAt(i).getWidth();
            while(from < to && from + 1 < xmark)
                columnForX[from++] = (short)i;
        }
        
    }
    
    protected void refreshCell(int item, int line) {
        view.renderCell(item, line);
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
     * Returns an array of indexes of the currently selected row
     */
    public Integer[] getSelectedItems() {
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
    public void selectItemAt(int index) {
        selectItemAt(index, null);
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
    protected void selectItemAt(int index, NativeEvent event) {
        boolean ctrlKey, shiftKey, selected = false;
        int startSelect, endSelect, minSelected, maxSelected, i;

        startSelect = index;
        endSelect = index;

        /*
         * If multiple selection is allowed check event for ctrl or shift keys.
         * If none apply the logic will fall throw to normal selection.
         */
        if (isMultipleSelectionAllowed()) {
            if (event != null && Event.getTypeInt(event.getType()) == Event.ONCLICK) {
                ctrlKey = event.getCtrlKey();
                shiftKey = event.getShiftKey();

                if (ctrlKey) {
                    if (isItemSelected(index)) {
                        unselectItemAt(index, event);
                        return;
                    }
                } else if (shiftKey) {
                    if ( !isAnyItemSelected()) {
                        startSelect = 0;
                        endSelect = index;
                    } else {
                        Collections.sort(selections);
                        minSelected = Collections.min(selections);
                        maxSelected = Collections.max(selections);
                        if (minSelected > index) {
                            startSelect = index;
                            endSelect = minSelected;
                        } else if (index > maxSelected) {
                            startSelect = maxSelected;
                            endSelect = index;
                        } else {
                            i = 0;
                            while (selections.get(i + 1) < index)
                                i++ ;
                            startSelect = selections.get(i);
                            endSelect = index;
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

                    //finishEditing();

                    selections.add(i);

                    view.applySelectionStyle(i);

                    if (event != null)
                        fireSelectionEvent(i);
                }
            }
        }

        //if (selected)
            //scrollToVisible(endSelect);
    }

    /**
     * This method will select all rows in the table if the table allows
     * Multiple Selection at the time it is called. No selections events will be
     * fired.
     */
    public void selectAll() {
        if (isMultipleSelectionAllowed()) {
            selections = new ArrayList<Integer>();
            for (int i = 0; i < getDataItemCount(); i++ )
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
        unselectItemAt(index, null);
    }

    /**
     * Unselects the row from the selection list. This method does nothing if
     * the passed index is not currently a selected row, otherwise the row will
     * be unselected and an UnselectEvent will be fired to all registered
     * handlers
     * 
     * @param index
     */
    protected void unselectItemAt(int index, NativeEvent event) {

        if (selections.contains(index)) {
            //finishEditing();
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
            unselectItemAt(selections.get(0), event);

    }

    /**
     * Returns the selected index of the first row selected
     * 
     * @return
     */
    public int getSelectedItem() {
        return selections.size() > 0 ? selections.get(0) : -1;
    }

    /**
     * Used to determine if the passed row index is currently in the selection
     * list.
     * 
     * @param index
     * @return
     */
    public boolean isItemSelected(int index) {
        return selections.contains(index);
    }

    /**
     * Used to determine if any row in the table is selected
     * 
     * @return
     */
    public boolean isAnyItemSelected() {
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

        //if ( !queryMode)
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

        //if ( !queryMode)
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

        //if ( !queryMode)
            UnselectionEvent.fire(this, index);
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
    
    
    @Override
    public void add(IsWidget w) {
        assert w instanceof Line;

        ((Line)w).setColumnar(this);
        addLine((Line)w);
    }

    public void onResize() {
        Element parent = (Element) (getParent() instanceof LayoutPanel ? ((LayoutPanel)getParent()).getWidgetContainerElement(this)
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


}
