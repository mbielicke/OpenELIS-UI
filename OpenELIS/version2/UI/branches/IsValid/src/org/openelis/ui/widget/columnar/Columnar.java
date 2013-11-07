package org.openelis.ui.widget.columnar;

import java.util.ArrayList;

import org.openelis.ui.common.Util;
import org.openelis.ui.widget.CSSUtils;
import org.openelis.ui.widget.table.CellEditor;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.LabelCell;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class Columnar extends FocusPanel implements RequiresResize {
    
    protected View view;
    
    protected int  lineHeight, totalColumnWidth, viewWidth = -1;
    
    protected ArrayList<DataItem> model;
    
    protected ArrayList<Line>    lines;
    
    protected short[] xForColumn, columnForX;
    
    protected HandlerRegistration visibleHandler;
    
    protected Columnar source = this;
    
    protected boolean hasHeader;
    
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
            return ((LayoutPanel)getParent()).getWidgetContainerElement(this).getOffsetWidth() - CSSUtils.getAddedBorderWidth(getElement());
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

}
