package org.openelis.ui.widget;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.ui.common.FieldErrorWarning;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestLabel {
    
    Label<String> label;
    
    @Before
    public void init() {
        label = new Label<String>();
    }
    
    @Test
    public void hasExceptions() {
        assertFalse(label.hasExceptions());
    }
    
    @Test
    public void hasExceptionsValidateExcepts() {
        label.addValidateException(new Exception());
        assertTrue(label.hasExceptions());
    }
    
    @Test
    public void hasExceptionsUserExcepts() {
        label.addException(new Exception());
        assertTrue(label.hasExceptions());
    }
    
    @Test
    public void addExceptionStyleError() {
        label.addException(new Exception());
        verify(label.label).addStyleName(label.css.InputError());
    }
    
    @Test
    public void addExceptionStyleWarning() {
        label.addException(new FieldErrorWarning());
        verify(label.label).addStyleName(label.css.InputWarning());    
    } 
}
