package org.openelis.ui.widget.model;

import java.util.List;


import com.google.gwt.event.shared.EventBus;

public abstract class Model<T> {
    
    List<T> data;
    
    EventBus bus;
    
    Model() {
        
    }
    
    Model(List<T> data) {
        setData(data);
    }
    
    public <V> V getCell(int r, int c) {
        return getData(data.get(r),c);
    }
    
    public <V> void setCell(int r, int c, V value) {
        setData(data.get(r),c,value);
        bus.fireEvent(new ModelCellUpdated<V>(r,c,value));
    }
    
    public void addRow(T row) {
        data.add(row);
        bus.fireEvent(new ModelRowAdded<T>(data.size()-1,row));
    }
    
    public void removeRow(int index) {
        data.remove(index);
        bus.fireEvent(new ModelRowRemoved(index));   
    }
    
    public void setData(List<T> data) {
        this.data = data;
        bus.fireEvent(new ModelDataSet());
    }
    
    public List<T> getData() {
        return data;
    }
    
    public int rowCount() {
        return data.size();
    }
    
    protected abstract int colCount();
    
    protected abstract <V> void setData(T row, int index, V value);
    
    protected abstract <V> V getData(T row, int index); 
    

}
