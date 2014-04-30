package org.openelis.ui.widget;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void setMaxLength() {
        textbox.setMaxLength(40);
        assertEquals(40,textbox.maxLength);
        verify(textbox.textbox).setMaxLength(40);
    }
    
    @Test
    public void textAlignment() {
        textbox.setTextAlignment(TextAlignment.CENTER);
        verify(textbox.textbox).setAlignment(TextAlignment.CENTER);
    }
    
    @Test
    public void setQueryMode_true() {
        textbox.setQueryMode(true);
        assertTrue(textbox.queryMode);
        verify(textbox.textbox).enforceMask(false);
        verify(textbox.textbox,never()).setMaxLength(anyInt());
        verify(textbox.textbox).setText("");
    }
    
    @Test
    public void setQueryMode_false() {
        textbox.setQueryMode(false);
        assertFalse(textbox.queryMode);
        verify(textbox.textbox,never()).enforceMask(anyBoolean());
    }
    
    @Test
    public void setQueryMode_reset() {
        textbox.setQueryMode(true);
        reset(textbox.textbox);
        textbox.setQueryMode(false);
        assertFalse(textbox.queryMode);
        verify(textbox.textbox).enforceMask(true);
        verify(textbox.textbox).setText("");
    }
    
    @Test
    public void setQueryMode_withMaxLength() {
        textbox.setMaxLength(40);
        textbox.setQueryMode(true);
        verify(textbox.textbox).setMaxLength(255);
        textbox.setQueryMode(false);
        verify(textbox.textbox,times(2)).setMaxLength(40);
    }
    
    @Test
    public void setValue() {
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
    public void setValue_eventNotFired() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                fail("Event should not have fired");
            }
        });
        
        textbox.setValue("value");
        assertEquals("value",textbox.value);
        verify(textbox.textbox).setText("value");
        textbox.setValue("value",true);
    }
    
    @Test
    public void finishEditing_doesNothing() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                fail("Event should not have fired");
            }
        });
        
        textbox.finishEditing();
        
        verify(textbox.textbox,never()).getText();
    }
    
    @Test 
    public void finishEditing_valueChanged() {        
        
        when(textbox.textbox.isEnabled()).thenReturn(true);
        when(textbox.textbox.getText()).thenReturn("value");
        
        textbox.finishEditing();
        
        verify(textbox.textbox).getText();
        assertEquals("value",textbox.value);
        assertFalse(textbox.hasExceptions());
        
    }
    
    @Test
    public void finishEditing_queryMode() {
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
    public void hasExceptions() {
        assertFalse(textbox.hasExceptions());
    }
    
    @Test
    public void hasExceptions_validateExcepts() {
        textbox.addValidateException(new Exception());
        assertTrue(textbox.hasExceptions());
    }
    
    @Test
    public void hasExceptions_userExcepts() {
        textbox.addException(new Exception());
        assertTrue(textbox.hasExceptions());
    }
    
    @Test
    public void hasExceptions_required() {
        textbox.setRequired(true);
        textbox.hasExceptions();
        assertTrue(textbox.hasExceptions());
    }
    
    @Test 
    public void hasExceptions_requiredQueryMode() {
        textbox.setRequired(true);
        textbox.setQueryMode(true);
        assertFalse(textbox.hasExceptions());
    }
    
    @Test
    public void addExceptionStyle_error() {
        textbox.addException(new Exception());
        verify(textbox.textbox).addStyleName(textbox.css.InputError());
    }
    
    @Test
    public void addExceptionStyle_warning() {
        textbox.addException(new FieldErrorWarning());
        verify(textbox.textbox).addStyleName(textbox.css.InputWarning());    
    } 
}
