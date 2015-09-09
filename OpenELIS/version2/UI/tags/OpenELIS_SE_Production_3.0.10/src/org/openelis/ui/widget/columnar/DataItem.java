package org.openelis.ui.widget.columnar;

import java.util.ArrayList;

public class DataItem {
    
    protected int width,minWidth;
    
    protected String label;
    
    protected boolean isResizable = true;
    
    protected Columnar columnar;
    
    /**
     * ArrayList of values representing column values.
     */
    protected ArrayList<Object> cells;
    
    /**
     * Additional data can be attached to the row that is not 
     * displayed in the table columns
     */
    protected Object data;
    
    public DataItem() {
        cells = new ArrayList<Object>();
    }
        
    /**
     * Constructor that sets the column values to the passed ArrayList
     * @param cells
     */
    public DataItem(ArrayList<Object> cells) {
        this.cells = cells;
    }
    
    /**
     * Constructor that creates an empty Row of the size passed
     * @param size
     */
    public DataItem(int size) {
        cells = new ArrayList<Object>(size);
        for(int i = 0; i < size; i++) {
            cells.add(null);
        }
    }
    
    /**
     * Constructor that creates a Row form the passed objects
     * @param objs
     */
    public DataItem(Object... objs) {
        cells = new ArrayList<Object>(objs.length);
        for (int i= 0; i < objs.length; i++)
            cells.add(objs[i]);
    }
    
    public DataItem(DataItem col) {
        data = col.data;
        
        cells = new ArrayList<Object>(col.cells.size());
        
        for(Object cell : col.cells)
            cells.add(cell);
    }
    
    /**
     * Returns the number of values in this row
     * @return
     */
    public int size() {
        return cells.size();
    }
    
    /**
     * Returns the ArrayList of values contained in this Row
     * @return
     */
    public ArrayList<Object> getCells() {
        return cells;
    }
    
    /**
     * Method called to set the value of a column in the Row.
     * @param index
     * @param value
     */
    public void setCell(int index, Object value) {
        cells.set(index,value);
    }

    /**
     * Method called return the value of a column in the Row.
     * @param index
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getCell(int index) {
        return (T)cells.get(index);
    }
    
    /**
     * Method called to attach a data object to this Row.
     * @param data
     */
    public void setData(Object data) {
        this.data = data;
    }
    
    /**
     * Method called to retrieve the data object attached to this Row
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T)data;
    }
    
    /**
     * Returns the width being used by this Column.
     * 
     * @return
     */
    public int getWidth() {

        int totalWidth, lastColumn;

        if (columnar == null)
            return minWidth;

        /*
         * If this is the last column calculate its width if the overall width 
         * will be less then the set width of the table
         */
        lastColumn = columnar.getDataItemCount() - 1;
        if (lastColumn >= 0 && columnar.getDataItemAt(lastColumn) == this) {
            totalWidth = columnar.getXForColumn(lastColumn);
            //if (totalWidth + width < table.getWidthWithoutScrollbar())
                int w;
                return ((((w = (columnar.getWidthWithoutScrollbar()) - totalWidth))) < width) ? width : w;
        }
     
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
        if(columnar != null)
            columnar.resize();
    }
    
    public int getMinWidth() {
        return minWidth;
    }
    
    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public boolean isResizable() {
        return isResizable;
    }
    
    public void setColumnar(Columnar clmnr) {
        this.columnar = clmnr;
    }
    
    public String getStyle() {
        return null;
    }
    

}
