package org.openelis.ui.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.ui.common.data.QueryData;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TextBoxUnit {
    
    private TextBox<String> test;
    
    @Before
    public void init() {
        test = new TextBox<String>();
    }
    
    @Test 
    public void defaultTest() {
        test.setEnabled(true);
        assertNotNull(test.exceptions);
        verify(test.textbox).setEnabled(false);
    }
    
    @Test 
    public void queryMode() {
        test.setQueryMode(true);
        assertTrue(test.queryMode);
        verify(test.textbox).enforceMask(false);
        verify(test.textbox, atLeast(0)).setMaxLength(255);
        verify(test.textbox, atLeast(0)).setText("");
    }
    
    @Test 
    public void queryModeWthLength() {
        test.setMaxLength(30);
        test.setQueryMode(true);
        assertTrue(test.queryMode);
        verify(test.textbox).enforceMask(false);
        verify(test.textbox).setMaxLength(255);
        verify(test.textbox, atLeast(0)).setText("");
    }
    
    @Test 
    public void queryModeFalse() {
        queryMode();
        test.setQueryMode(false);
        assertFalse(test.queryMode);
        verify(test.textbox).enforceMask(true);
        verify(test.textbox,atLeast(0)).setMaxLength(test.maxLength);
        verify(test.textbox,atLeast(1)).setText("");
    }
    
    @Test 
    public void queryModeWthLengthFalse() {
        queryModeWthLength();
        test.setQueryMode(false);
        assertFalse(test.queryMode);
        verify(test.textbox).enforceMask(true);
        verify(test.textbox, atLeast(2)).setMaxLength(30);
        verify(test.textbox,atLeast(1)).setText("");
    }
    
    @Test
    public void setQuery() {
        QueryData qd;
        
        qd = new QueryData();
        qd.setQuery("abc*");
        
        test.setQuery(qd);
        
        verify(test.textbox).setText("abc*");
        
    }
    
    @Test
    public void setQueryNull() {
        test.setQuery(null);
        verify(test.textbox).setText("");
    }
    
    @Test
    public void setValue() {    
        test.setValue("value");
        assertEquals("value",test.value);
        verify(test.textbox).setText("value");
        test.setValue(null);
        assertNull(test.value);
        verify(test.textbox).setText("");
    }
    
    @Test
    public void finishEditing() {
        when(test.textbox.getText()).thenReturn("text");
        test.finishEditing();
        assertNull(test.value);
        
        when(test.textbox.isEnabled()).thenReturn(true);
        test.finishEditing();
        assertEquals("text",test.value);
    }
    
    @Test
    public void hasExceptions() {
        assertFalse(test.hasExceptions());
        
        test.setRequired(true);
        assertTrue(test.hasExceptions());
        
        test.clearValidateExceptions();
        
        test.setValue("text");
        assertFalse(test.hasExceptions());
        
        test.setValue(null);
        test.setQueryMode(true);
        assertFalse(test.hasExceptions());
        
        test.clearExceptions();
        
        test.setQueryMode(false);
        test.setValue("text");
        test.addException(new Exception());
                
        assertTrue(test.hasExceptions());
        
    }
    
    

}
