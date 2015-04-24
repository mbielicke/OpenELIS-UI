package org.openelis.ui.widget.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openelis.ui.util.TestingUtil.verifyEnabled;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.IntegerHelper;
import org.openelis.ui.widget.StringHelper;
import org.openelis.ui.widget.TextBox;

import com.google.gwt.core.shared.GWT;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestTextBoxCell {
    
    TextBoxCell cell;
    TextBox<String> editor;
    Container container; 
    
    @Before
    public void preArrange() {
        editor = GWT.create(TextBox.class);
        container = GWT.create(Container.class);
        when(editor.getHelper()).thenReturn(new StringHelper());
        cell = new TextBoxCell(editor);
    }
    
    @Test
    public void setEditor() {
        assertEquals(editor,cell.editor);
        verifyEnabled(editor);
    }
    
    @Test 
    public void display_wrongType() {        
        assertEquals("123",cell.display(new Integer(123)));
        verify(editor).setQueryMode(false);
    }
    
    @Test
    public void startEditing() {        
        cell.startEditing("value",container,null);
        
        verify(editor).setValue("value");
    }
    
    @Test
    public void startEditing_wrongType() {
        cell.startEditing(new Integer(123), container, null);
        
        verify(editor).setText("123");
    }
    
    @Test
    public void startEditing_null() {
        cell.startEditing(null,container,null);
        
        verify(editor).setValue(null);
    }
    
    @Test
    public void validate() {
        assertTrue(cell.validate("value").isEmpty());       
    }
    
    @Test 
    public void validate_invalidValue() {
        assertFalse(cell.validate(new Integer(123)).isEmpty());
    }
    
    @Test 
    public void validate_null() {
        assertTrue(cell.validate(null).isEmpty());
    }
    
    @Test
    public void validate_query() {
        cell.query = true;
        QueryData qd = new QueryData(QueryData.Type.STRING,"n*");
        
        assertTrue(cell.validate(qd).isEmpty());
    }
    
    @Test 
    public void validate_queryNull() {
        cell.query = true;
       
        assertTrue(cell.validate(null).isEmpty());
    }
    
    @Test
    public void validate_queryInvalid() {
        cell.query = true;
        QueryData qd = new QueryData(QueryData.Type.STRING,"n* |");
        
        assertFalse(cell.validate(qd).isEmpty());
    }
    
    @Test
    public void finishEditing() {
        when(editor.getText()).thenReturn("value");
        assertEquals("value",cell.finishEditing());
    }
    
    @Test
    public void finishEditing_invalid() {
        TextBox<Integer> editor = GWT.create(TextBox.class);
        cell.setEditor(editor);
        when(editor.getHelper()).thenReturn(new IntegerHelper());
        when(editor.getText()).thenReturn("12d");
        
        assertEquals("12d",cell.finishEditing());
        verify(editor, times(2)).getText();        
    }
    
    @Test
    public void finishEditing_query() {
        cell.query = true;
        
        cell.finishEditing();
        
        verify(editor).getQuery();
    }
    
    @Test
    public void startEditingQuery() {
        cell.startEditingQuery(any(QueryData.class), container,null);
        
        assertTrue(cell.query);
        verify(editor).setQueryMode(true);
        verify(editor).setQuery(any(QueryData.class));
        verify(container).setEditor(editor);
    }

}
