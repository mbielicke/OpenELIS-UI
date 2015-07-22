package org.openelis.ui.widget.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.ui.widget.IntegerHelper;
import org.openelis.ui.widget.Label;

import com.google.gwt.core.client.GWT;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestLabelCell {
    
    LabelCell cell;
    Label<String> editor;
    
    @Before
    public void preArrange() {
        editor = GWT.create(Label.class);
        cell = new LabelCell(editor);
    }
    
    @Test
    public void display() {        
        assertEquals("value",cell.display("value"));
    }
    
    @Test 
    public void display_wrongType() {        
        assertEquals("123",cell.display(new Integer(123)));
    }
    
    @Test
    public void bulkRender() {
        assertEquals(("<td>value</td>"),cell.bulkRender("value").asString());
    }
    
    @Test
    public void validate() {
        assertTrue(cell.validate("value").isEmpty());       
    }
    
    @Test 
    public void validate_invalidValue() {
        Label<Integer> editor = GWT.create(Label.class);
        cell = new LabelCell(editor);
        when(editor.getHelper()).thenReturn(new IntegerHelper());
        assertFalse(cell.validate("12d").isEmpty());
    }
    
    @Test 
    public void validate_null() {
        assertTrue(cell.validate(null).isEmpty());
    }

}
