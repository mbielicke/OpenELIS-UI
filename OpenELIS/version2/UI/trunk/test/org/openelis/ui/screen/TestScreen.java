package org.openelis.ui.screen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.FormErrorException;
import org.openelis.ui.common.ValidationErrorsList;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen.ScreenKeyHandler;
import org.openelis.ui.screen.Screen.ShortKeys;
import org.openelis.ui.screen.Screen.Validation;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.Window;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestScreen {
    
    Screen screen,tab;
    TextBox<String> text1,text2,text3;
    ScreenHandler<String> handler1,handler2,handler3,handler4;
    Window window;
    
    @Before
    public void preArrange() {        
        screen = new Screen();
        window = mock(Window.class);
        screen.setWindow(window);
        
        text1 = GWT.create(TextBox.class);
        text2 = GWT.create(TextBox.class);
        text3 = GWT.create(TextBox.class);
        tab = mock(Screen.class);
        handler1 = mock(ScreenHandler.class);
        handler2 = mock(ScreenHandler.class);
        handler3 = mock(ScreenHandler.class);
        handler4 = mock(ScreenHandler.class);
        
        screen.addScreenHandler(text1, "text1", handler1);
        screen.addScreenHandler(text2, "text2", handler2);
        screen.addScreenHandler(text3, "text3", handler3);
        screen.addScreenHandler(tab,"tab", handler4);
    }
    

    
    @Test
    public void focusNextWidget_null() {
        screen.focusNextWidget(null, true);
    }
    
    @Test
    public void focusNextWidget_forward() {
        mockTabOrder();
        when(text2.isEnabled()).thenReturn(true);
        
        screen.focusNextWidget(text1,true);
        
        verify(text2).setFocus(true);
    }
    
    @Test
    public void focusNextWidget_backward() {
        mockTabOrder();
        when(text3.isEnabled()).thenReturn(true);
        
        screen.focusNextWidget(text1, false);
        
        verify(text3).setFocus(true);
    }
    
    @Test 
    public void focusNextWidget_skip1_forward() {
        mockTabOrder();
        when(text2.isEnabled()).thenReturn(false);
        when(text3.isEnabled()).thenReturn(true);
        
        screen.focusNextWidget(text1, true);
        
        verify(text3).setFocus(true);
    }
    
    @Test
    public void focusNextWidget_skip1_backward() {
        mockTabOrder();
        when(text2.isEnabled()).thenReturn(true);
        when(text3.isEnabled()).thenReturn(false);
        
        screen.focusNextWidget(text1, false);
        
        verify(text2).setFocus(true);
    }
    
    @Test
    public void focusNextWidget_allDisabled_forward() {
        mockTabOrder();
        when(text2.isEnabled()).thenReturn(false);
        when(text3.isEnabled()).thenReturn(false);
        
        screen.focusNextWidget(text1, true);
        
        verify(text1).setFocus(true);
    }
    
    @Test
    public void focusNextWidget_allDisabled_backward() {
        mockTabOrder();
        when(text2.isEnabled()).thenReturn(false);
        when(text3.isEnabled()).thenReturn(false);
        
        screen.focusNextWidget(text1, false);
        
        verify(text1).setFocus(true);
    }
    
    @Test
    public void finishEditing() {
        screen.focused = text1;
        
        screen.finishEditing();
        
        verify(text1).finishEditing();
        verify(tab).finishEditing();
    }
    
    @Test
    public void validate() {
        assertTrue(screen.validate().getStatus() == Validation.Status.VALID);
    }
    
    @Test 
    public void validate_oneError() {
        doAnswer(createValidationAnswer(Validation.Status.ERRORS,new Exception("Something invalid")))
        .when(handler1).isValid(any(Validation.class));
        
        Validation validation = screen.validate();
        
        assertEquals(Validation.Status.ERRORS, validation.getStatus());
        assertFalse(validation.getExceptions().isEmpty());
    }
    
    @Test 
    public void validate_oneFlagged() {
        doAnswer(createValidationAnswer(Validation.Status.FLAGGED))
        .when(handler1).isValid(any(Validation.class));
        
        Validation validation = screen.validate();
        
        assertEquals(Validation.Status.FLAGGED, validation.getStatus());
    }
    
    @Test 
    public void validate_oneErrorSetOverFlagged() {
        doAnswer(createValidationAnswer(Validation.Status.FLAGGED))
        .when(handler1).isValid(any(Validation.class));

        doAnswer(createValidationAnswer(Validation.Status.ERRORS, new Exception("Something invalid")))
        .when(handler2).isValid(any(Validation.class));
        
        Validation validation = screen.validate();
        
        assertEquals(Validation.Status.ERRORS, validation.getStatus());
        assertFalse(validation.getExceptions().isEmpty());
    }
    
    @Test
    public void showErrors_fieldErrorOnly() {
        FieldErrorException fieldException = new FieldErrorException("Error","text1");
        ValidationErrorsList errors = new ValidationErrorsList();
        errors.add(fieldException);
        
        screen.showErrors(errors);
        
        verify(handler1).showError(fieldException);
        verify(window).setStatus("Please correct the errors indicated, then press Commit", screen.css.ErrorPanel());
        verify(tab,never()).showErrors(any(ValidationErrorsList.class));
    }
    
    @Test
    public void showErrors_fieldErrorAndSingleFormError() {
        FieldErrorException fieldException = new FieldErrorException("Error","text1");
        ValidationErrorsList errors = new ValidationErrorsList();
        errors.add(fieldException);
        errors.add(new FormErrorException("Form Error"));
        
        screen.showErrors(errors);
        
        verify(handler1).showError(fieldException);
        verify(window).setStatus("Form Error", screen.css.ErrorPanel());
        verify(tab,never()).showErrors(any(ValidationErrorsList.class));
    }
    
    
    
    @Test
    public void showErrors_oneFieldOneForm() {
        FieldErrorException fieldException = new FieldErrorException("Error","text1");
        ValidationErrorsList errors = new ValidationErrorsList();
        errors.add(fieldException);
        errors.add(new FormErrorException("Form Error"));
        
        screen.showErrors(errors);
        
        verify(handler1).showError(fieldException);
        verify(window).setStatus("Form Error", screen.css.ErrorPanel());
        verify(tab,never()).showErrors(any(ValidationErrorsList.class));
    }
    
    @Test
    public void showErrors_oneFieldTwoForm() {
        FieldErrorException fieldException = new FieldErrorException("Error","text1");
        ValidationErrorsList errors = new ValidationErrorsList();
        errors.add(fieldException);
        errors.add(new FormErrorException("First Form Error"));
        errors.add(new FormErrorException("Second Form Error"));
        
        screen.showErrors(errors);
        
        verify(handler1).showError(fieldException);
        verify(window).setStatus("(Error 1 of 2) First Form Error", screen.css.ErrorPanel());
        verify(window).setMessagePopup(any(ArrayList.class), anyString());
        verify(tab,never()).showErrors(any(ValidationErrorsList.class));
    }
    
    @Test
    public void showErrors_onTab() {
        FieldErrorException fieldException = new FieldErrorException("Error","NotOnMainScreen");
        ValidationErrorsList errors = new ValidationErrorsList();
        errors.add(fieldException);
        
        screen.showErrors(errors);
        
        verify(tab).showErrors(any(ValidationErrorsList.class));
    }
    
    @Test
    public void showErrors_emptyList() {
        screen.showErrors(new ValidationErrorsList());
        verify(window,never()).setStatus(anyString(), anyString());
    }
    
    @Test
    public void clearErrors() {
        screen.clearErrors();
        
        verify(handler1).clearError();
        verify(handler2).clearError();
        verify(handler3).clearError();
        verify(handler4).clearError();
        verify(window).clearStatus();
        verify(window).clearMessagePopup("");
    }
    
    @Test
    public void getQueryFields() {
        assertTrue(screen.getQueryFields().isEmpty());
    }
    
    @Test
    public void getQueryFields_singelQueryData() {
        QueryData qd = new QueryData();
        qd.setType(QueryData.Type.STRING);
        qd.setQuery("n*");
        when(handler1.getQuery()).thenReturn(qd);
        
        ArrayList<QueryData> list = screen.getQueryFields();
        
        assertEquals(1,list.size());
        assertEquals(qd,list.get(0));
        assertEquals("text1",qd.getKey());
     }
    
    @Test 
    public void getQueryFields_objectArray() {
        QueryData[] qds = new QueryData[2];
        qds[0] = new QueryData(QueryData.Type.STRING,"n*");
        qds[1] = new QueryData(QueryData.Type.STRING,"m*");
        when(handler1.getQuery()).thenReturn(qds);
        
        ArrayList<QueryData> list = screen.getQueryFields();
        
        assertEquals(2,list.size());
        assertEquals(qds[0],list.get(0));
    }
    
    @Test
    public void getQueryFields_arryList() {
        ArrayList<QueryData> qds = new ArrayList<QueryData>();
        qds.add(new QueryData(QueryData.Type.STRING,"n*"));
        qds.add(new QueryData(QueryData.Type.STRING,"m*"));
        when(handler1.getQuery()).thenReturn(qds);
        
        ArrayList<QueryData> list = screen.getQueryFields();
        
        assertEquals(2,list.size());
        assertEquals(qds.get(0),list.get(0));
    }
    
    @Test
    public void addScreenHandler() {
        assertTrue(screen.handlers.containsValue(handler1));
        assertTrue(screen.widgets.containsValue(handler1));
        assertTrue(screen.tabs.containsValue(tab));
        verify(text1).addValueChangeHandler(handler1);
        verify(text1).addFocusHandler(screen);
    }
    
    @Test
    public void fireDataChange() {
        screen.fireDataChange();
        
        verify(handler1).onDataChange(any(DataChangeEvent.class));
    }
    
    @Test 
    public void addShortcutHandler() {
        screen.addShortcut(text1, 't', ShortKeys.CTRL);
        
        assertTrue(screen.shortcuts.containsKey(new Shortcut(true,false,false,'t')));
    }
    
    @Test
    public void setState() {
        screen.setState(State.ADD);
        
        verify(handler1).onStateChange(any(StateChangeEvent.class));
        assertEquals(State.ADD,screen.state);
    }

    @Test
    public void setState_doNothing() {
        screen.state = State.DEFAULT;
        
        screen.setState(State.DEFAULT);
        
        verify(handler1,never()).onStateChange(any(StateChangeEvent.class));
    }
    
    @Test
    public void isState() {
        screen.state = State.DEFAULT;
        assertTrue(screen.isState(State.DEFAULT));
    }
    
    @Test
    public void isState_multiOption() {
        screen.state = State.DEFAULT;
        assertTrue(screen.isState(State.DEFAULT,State.ADD,State.UPDATE));
    }
    
    @Test
    public void isState_false() {
        screen.state = State.DEFAULT;
        assertFalse(screen.isState(State.ADD));
    }
    
    @Test 
    public void isState_false_multi() {
        screen.state = State.DEFAULT;
        assertFalse(screen.isState(State.ADD,State.UPDATE,State.DISPLAY));
    }
    
    @Test
    public void setBusy() {
        screen.setBusy();
        
        assertEquals(1,screen.busy);
        assertTrue(screen.isBusy());
        verify(window).lockWindow();
    }
    
    @Test
    public void setBusy_multiple() {
        screen.setBusy();
        screen.setBusy();
        
        assertEquals(2,screen.busy);
        assertTrue(screen.isBusy());
        verify(window,times(2)).lockWindow();
    }
    
    @Test 
    public void removeBusy() {
        screen.busy = 1;
        
        screen.removeBusy();
        
        assertFalse(screen.isBusy());
        assertEquals(0,screen.busy);
        verify(window).unlockWindow();
    }
    
    @Test
    public void removeBusy_multiple_stillLocked() {
        screen.busy = 2;
        
        screen.removeBusy();
        
        assertTrue(screen.isBusy());
        assertEquals(1,screen.busy);
        verify(window,never()).unlockWindow();
    }
   
    @Test 
    public void removeBusy_multiple_unlocked() {
        screen.busy = 2;
        
        screen.removeBusy();
        screen.removeBusy();
        
        assertFalse(screen.isBusy());
        assertEquals(0,screen.busy);
        verify(window).unlockWindow();
    }
    
    @Test 
    public void keyDownHandler_tab() {
        ScreenKeyHandler keyHandler = screen.new ScreenKeyHandler();
        KeyDownEvent keyDown = mock(KeyDownEvent.class);
        NativeEvent event = mock(NativeEvent.class);
        mockTabOrder();
        screen.focused = text1;
        when(keyDown.isShiftKeyDown()).thenReturn(false);
        when(keyDown.getNativeEvent()).thenReturn(event);
        when(event.getKeyCode()).thenReturn(KeyCodes.KEY_TAB);
        when(text2.isEnabled()).thenReturn(true);
        
        keyHandler.onKeyDown(keyDown);
        
        verify(text2).setFocus(true);        
    }
    
    @Test
    public void keyDownHandler_shortcut() {
        ScreenKeyHandler keyHandler = screen.new ScreenKeyHandler();
        KeyDownEvent keyDown = mock(KeyDownEvent.class);
        NativeEvent event = mock(NativeEvent.class);
        when(keyDown.getNativeEvent()).thenReturn(event);
        when(event.getKeyCode()).thenReturn((int)'t');
        when(keyDown.isAnyModifierKeyDown()).thenReturn(true);
        when(keyDown.isControlKeyDown()).thenReturn(true);
        when(keyDown.isAltKeyDown()).thenReturn(false);
        when(keyDown.isShiftKeyDown()).thenReturn(false);
        when(keyDown.getNativeKeyCode()).thenReturn((int)'t');
        when(text1.isEnabled()).thenReturn(true);
        screen.addShortcut(text1, 't', ShortKeys.CTRL);
        
        keyHandler.onKeyDown(keyDown);
        
        verify(text1).setFocus(true);     
    }
    
    private void mockTabOrder() {
        when(handler1.onTab(true)).thenReturn(text2);
        when(handler1.onTab(false)).thenReturn(text3);
        when(handler2.onTab(true)).thenReturn(text3);
        when(handler2.onTab(false)).thenReturn(text1);
        when(handler3.onTab(true)).thenReturn(text1);
        when(handler3.onTab(false)).thenReturn(text2);
    }
    
    private Answer<Void> createValidationAnswer(final Validation.Status satus, final Exception... exceptions) {
        return new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Validation validation = (Validation)invocation.getArguments()[0];
                if(exceptions != null) {
                    for(Exception exception : exceptions)
                        validation.addException(exception);
                }
                validation.setStatus(Validation.Status.ERRORS);
                return null;
            }
        };
    }
}
