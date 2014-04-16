package org.openelis.ui.widget;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openelis.ui.common.FieldErrorWarning;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestTextBox {
    
    TextBox<String> textbox;
    FocusHandler focusHandler;
    BlurHandler blurHandler;
    
    SimpleEventBus bus;
    
    @Before
    public void init() {
        bus = new SimpleEventBus();
        
        textbox = new TextBox<String>() {
            @Override
            public void fireEvent(GwtEvent<?> event) {
                bus.fireEvent(event);
                super.fireEvent(event);
            }
        };

        when(textbox.textbox.addFocusHandler(any(FocusHandler.class)))
            .thenAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    focusHandler = (FocusHandler)invocation.getArguments()[0];
                    return null;
                }
            });
        
        when(textbox.textbox.addBlurHandler(any(BlurHandler.class)))
            .thenAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    blurHandler = (BlurHandler)invocation.getArguments()[0];
                    return null;
                }
            });
        
        textbox.init();
    }
    
    @Test
    public void testSetMaxLength() {
        textbox.setMaxLength(40);
        assertEquals(40,textbox.maxLength);
        verify(textbox.textbox).setMaxLength(40);
    }
    
    @Test
    public void testTextAlignment() {
        textbox.setTextAlignment(TextAlignment.CENTER);
        verify(textbox.textbox).setAlignment(TextAlignment.CENTER);
    }
    
    @Test
    public void testSetQueryMode() {
        textbox.setQueryMode(true);
        assertTrue(textbox.queryMode);
        verify(textbox.textbox).enforceMask(false);
        verify(textbox.textbox,never()).setMaxLength(anyInt());
        verify(textbox.textbox).setText("");
    }
    
    @Test
    public void testSetQueryModeFalse() {
        textbox.setQueryMode(false);
        assertFalse(textbox.queryMode);
        verify(textbox.textbox,never()).enforceMask(anyBoolean());
    }
    
    @Test
    public void testSetQueryModeReset() {
        textbox.setQueryMode(true);
        reset(textbox.textbox);
        textbox.setQueryMode(false);
        assertFalse(textbox.queryMode);
        verify(textbox.textbox).enforceMask(true);
        verify(textbox.textbox).setText("");
    }
    
    @Test
    public void testSetQueryModeWithMaxLength() {
        textbox.setMaxLength(40);
        textbox.setQueryMode(true);
        verify(textbox.textbox).setMaxLength(255);
        textbox.setQueryMode(false);
        verify(textbox.textbox,times(2)).setMaxLength(40);
    }
    
    @Test
    public void testSetValue() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                assertEquals("value",event.getValue());
                
            }
        });
        
        textbox.setValue("value",true);
        assertEquals("value",textbox.value);
        verify(textbox.textbox).setText("value");
    }
    
    @Test
    public void testSetValueEventNotFired() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                Assert.fail("Event should not have fired");
            }
        });
        
        textbox.setValue("value");
        assertEquals("value",textbox.value);
        verify(textbox.textbox).setText("value");
        textbox.setValue("value",true);
    }
    
    @Test
    public void testFinishEditingDoesNothing() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                Assert.fail("Event should not have fired");
            }
        });
        
        textbox.finishEditing();
        
        verify(textbox.textbox,never()).getText();
    }
    
    @Test 
    public void testFinishEditingValueChanged() {        
        
        when(textbox.textbox.isEnabled()).thenReturn(true);
        when(textbox.textbox.getText()).thenReturn("value");
        
        textbox.finishEditing();
        
        verify(textbox.textbox).getText();
        assertEquals("value",textbox.value);
        Assert.assertFalse(textbox.hasExceptions());
        
    }
    
    @Test
    public void testFinishEditingQueryMode() {
        textbox.setQueryMode(true);
        textbox.helper = new StringHelper() {
            @Override
            public void validateQuery(String input) throws Exception {
                assertEquals("value",input);
            }
        };
        when(textbox.textbox.isEnabled()).thenReturn(true);
        when(textbox.textbox.getText()).thenReturn("value");
        
        textbox.finishEditing();
    }
    
    @Test
    public void testHasExceptions() {
        assertFalse(textbox.hasExceptions());
    }
    
    @Test
    public void testHasExceptionsValidateExcepts() {
        textbox.addValidateException(new Exception());
        assertTrue(textbox.hasExceptions());
    }
    
    @Test
    public void testHasExceptionsUserExcepts() {
        textbox.addException(new Exception());
        assertTrue(textbox.hasExceptions());
    }
    
    @Test
    public void testHasExceptionsRequired() {
        textbox.setRequired(true);
        textbox.hasExceptions();
        assertTrue(textbox.hasExceptions());
    }
    
    @Test 
    public void testHasExceptionsRequiredQueryMode() {
        textbox.setRequired(true);
        textbox.setQueryMode(true);
        assertFalse(textbox.hasExceptions());
    }
    
    @Test
    public void testAddExceptionStyleError() {
        textbox.addException(new Exception());
        verify(textbox.textbox).addStyleName(textbox.css.InputError());
    }
    
    @Test
    public void testAddExceptionStyleWarning() {
        textbox.addException(new FieldErrorWarning());
        verify(textbox.textbox).addStyleName(textbox.css.InputWarning());    
    } 

}
