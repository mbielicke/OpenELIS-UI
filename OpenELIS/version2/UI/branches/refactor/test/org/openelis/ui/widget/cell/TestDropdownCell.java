package org.openelis.ui.widget.cell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.openelis.ui.util.TestingUtil.verifyEnabled;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.IntegerHelper;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.cell.DropdownCell;
import org.openelis.ui.widget.table.Container;

import com.google.gwt.core.client.GWT;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestDropdownCell {
    
    DropdownCell cell;
    Dropdown<Integer> editor;
    Container container; 
    
    @Before
    public void preArrange() {
        editor = GWT.create(Dropdown.class);
        container = GWT.create(Container.class);
        when(editor.getHelper()).thenReturn(new IntegerHelper());
        cell = new DropdownCell(editor);
    }
    
    @Test
    public void setEditor() {
        assertEquals(editor,cell.editor);
        verifyEnabled(editor);
    }
    
    @Test
    public void display() {        
        when(editor.isValidKey(any(Integer.class))).thenReturn(true);
        when(editor.getDisplay()).thenReturn("Item 1");
        assertEquals("Item 1",cell.display(new Integer(1)));
        verify(editor).setQueryMode(false);
    }
    
    @Test 
    public void display_wrongType() {
        assertEquals("123",cell.display("123"));
        verify(editor).setQueryMode(false);
    }
    
    @Test
    public void display_invalidKey() {
        when(editor.isValidKey(any(Integer.class))).thenReturn(false);
        assertEquals("1",cell.display(new Integer(1)));
    }
    
    @Test
    public void display_null() {
        assertEquals("",cell.display(null));
    }
    
    @Test
    public void bulkRender() {
        when(editor.isValidKey(any(Integer.class))).thenReturn(true);
        when(editor.getDisplay()).thenReturn("Item 1");
        assertEquals("<td>Item 1</td>",cell.bulkRender(new Integer(1)).asString());
    }
    
    @Test
    public void startEditing() {        
        cell.startEditing(new Integer(1),container,null);
        verify(editor).setValue(new Integer(1));
        verify(editor).setQueryMode(false);
        verify(container).setEditor(editor);
    }
    
    @Test
    public void startEditingQuery() {
        cell.startEditingQuery(any(QueryData.class), container,null);
        
        assertTrue(cell.query);
        verify(editor).setQueryMode(true);
        verify(editor).setQuery(any(QueryData.class));
        verify(container).setEditor(editor);
    }
    
    @Test
    public void validate() {
        when(editor.isValidKey(new Integer(1))).thenReturn(true);
        assertTrue(cell.validate(new Integer(1)).isEmpty());       
    }
    
    @Test 
    public void validate_invalidValue() {
        when(editor.isValidKey(new Integer(123))).thenReturn(false);
        assertFalse(cell.validate(new Integer(123)).isEmpty());
    }
    
    @Test 
    public void validate_null() {
        assertTrue(cell.validate(null).isEmpty());
    }
    
    @Test
    public void finishEditing() {
        cell.finishEditing();
        verify(editor).finishEditing();
        verify(editor).getValue();
    }
    
    @Test
    public void finishEditing_query() {
        cell.query = true;
     
        cell.finishEditing();
        
        verify(editor).finishEditing();
        verify(editor).getQuery();
    }
    
}
