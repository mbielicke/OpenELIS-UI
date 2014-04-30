package org.openelis.ui.widget;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestTextBase {
    
    TextBase text;
    
    @Before
    public void preArrange() {
        text = new TextBase();
    }
    
    @Test
    public void getText() {
        when(text.box.getText()).thenReturn("MixedCase");
        assertEquals("MixedCase",text.getText());
    }
    
    @Test
    public void getText_lowerCase() {
        when(text.box.getText()).thenReturn("MixedCase");
        text.textCase = TextBase.Case.LOWER;
        assertEquals("mixedcase",text.getText());
    }
    
    @Test
    public void getText_upperCase() {
        when(text.box.getText()).thenReturn("MixedCase");
        text.textCase = TextBase.Case.UPPER;
        assertEquals("MIXEDCASE",text.getText());
    }
    
    @Test
    public void getText_clearMask() {
        text.picture = "999";
        text.enforceMask = true;
        when(text.box.getText()).thenReturn("999");
        
        assertEquals("",text.getText());
    }
    
    @Test
    public void setCase() {
        text.setCase(null);
        
        verify(text.box).removeStyleName(text.css.Upper());
        verify(text.box).removeStyleName(text.css.Lower());
        assertEquals(TextBase.Case.MIXED,text.textCase);
    }
    
    @Test
    public void setCase_upper() {
        text.setCase(TextBase.Case.UPPER);
        
        verify(text.box).addStyleName(text.css.Upper());
        verify(text.box).removeStyleName(text.css.Lower());
        assertEquals(TextBase.Case.UPPER,text.textCase);
    }
    
    @Test
    public void setCase_lower() {
        text.setCase(TextBase.Case.LOWER);
        
        verify(text.box).removeStyleName(text.css.Upper());
        verify(text.box).addStyleName(text.css.Lower());
        assertEquals(TextBase.Case.LOWER,text.textCase);
    }
    
    @Test
    public void setMask() {
        String msk = "999-999-9999";
        
        text.setMask(msk);
        
        assertEquals(msk,text.mask);
        assertTrue(text.enforceMask);
        assertEquals("   -   -    ",text.picture);
        assertNotNull(text.keyDown);
        verify(text.box).addKeyDownHandler(any(KeyDownHandler.class));
        verify(text.box).addKeyPressHandler(any(KeyPressHandler.class));
        verify(text.box).setMaxLength(msk.length());
    }
    
    @Test
    public void setMask_toNull() {
        String mask = "999-999-9999";
        text.mask = mask;
        text.picture = "   -   -    ";
        text.enforceMask = true;
        
        text.setMask(null);
        
        assertFalse(text.enforceMask);
        assertNull(text.picture);
        assertNull(text.keyDown);
        assertNull(text.keyPress);
    }
    
    @Test
    public void enforceMask_notSet() {
        text.enforceMask(true);
        
        assertFalse(text.isMaskEnforced());
        verify(text.box,never()).setMaxLength(anyInt());
    }
    
    @Test
    public void enforceMask_set() {
        text.mask = "999-999-9999";
        
        text.enforceMask(true);
        
        assertTrue(text.isMaskEnforced());
        verify(text.box).setMaxLength(12);
    }
    
    @Test
    public void enforceMask_unset() {
        text.mask = "999-999-9999";
        text.enforceMask = true;
        
        text.enforceMask(false);
        
        assertFalse(text.isMaskEnforced());
        verify(text.box).setMaxLength(255);
    }
    
    @Test
    public void maskKeyDown_notEnforced() {
        KeyDownEvent keyDown = mock(KeyDownEvent.class);
        
        text.maskKeyDown(keyDown);
        
        verify(keyDown,never()).isAnyModifierKeyDown();
    }
    
    @Test
    public void maskKeyDown_nonDisplayKey() {
        KeyDownEvent keyDown = mock(KeyDownEvent.class);
        when(keyDown.isAnyModifierKeyDown()).thenReturn(true);
        
        text.maskKeyDown(keyDown);
        
        verify(text.box,never()).getText();
    }
    
    @Test
    public void maskKeyDown_deleteKey() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("530-5");
        when(text.box.getCursorPos()).thenReturn(4);
        when(text.box.getSelectionLength()).thenReturn(0);
        
        text.maskKeyDown(keyDown(KeyCodes.KEY_DELETE));
        
        verify(text.box).setText("530- ");
    }
    
    @Test
    public void maskKeyDown_deleteOverLiteral() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("530-");
        when(text.box.getCursorPos()).thenReturn(3);
        when(text.box.getSelectionLength()).thenReturn(0);
        
        text.maskKeyDown(keyDown(KeyCodes.KEY_DELETE));
        
        verify(text.box).setText("530-");
    }
    
    @Test
    public void maskKeyDown_deleteOnLiteral() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("530-");
        when(text.box.getCursorPos()).thenReturn(2);
        when(text.box.getSelectionLength()).thenReturn(0);
        
        text.maskKeyDown(keyDown(KeyCodes.KEY_DELETE));
        
        verify(text.box).setText("53 -");
    }
    
    @Test
    public void maskKeyDown_deleteWithSelection() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("530-5");
        when(text.box.getCursorPos()).thenReturn(4);
        when(text.box.getSelectionLength()).thenReturn(5);
        
        text.maskKeyDown(keyDown(KeyCodes.KEY_DELETE));
        
        verify(text.box).setText("   - ");
        verify(text.box).setCursorPos(0);
    }
    
    @Test
    public void maskKeyDown_backSpaceKey() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("530-5");
        when(text.box.getCursorPos()).thenReturn(5);
        when(text.box.getSelectionLength()).thenReturn(0);
        
        text.maskKeyDown(keyDown(KeyCodes.KEY_BACKSPACE));
        
        verify(text.box).setText("530- ");
    }
    
    @Test
    public void maskKeyDown_backSpaceOverLiteral() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("530-");
        when(text.box.getCursorPos()).thenReturn(4);
        when(text.box.getSelectionLength()).thenReturn(0);
        
        text.maskKeyDown(keyDown(KeyCodes.KEY_BACKSPACE));
        
        verify(text.box).setText("530-");
    }
    
    @Test
    public void maskKeyDown_backSpaceOnLiteral() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("530-");
        when(text.box.getCursorPos()).thenReturn(3);
        when(text.box.getSelectionLength()).thenReturn(0);
        
        text.maskKeyDown(keyDown(KeyCodes.KEY_BACKSPACE));
        
        verify(text.box).setText("53 -");
    }
    
    @Test
    public void maskKeyDown_backSpaceWithSelection() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("530-5");
        when(text.box.getCursorPos()).thenReturn(5);
        when(text.box.getSelectionLength()).thenReturn(5);
        
        text.maskKeyDown(keyDown(KeyCodes.KEY_BACKSPACE));
        
        verify(text.box).setText("   - ");
        verify(text.box).setCursorPos(0);
    }
    
    @Test 
    public void maskKeyPress_notEnforced() {
        KeyPressEvent keyPress = mock(KeyPressEvent.class);
        
        text.maskKeyPress(keyPress);
        
        verify(keyPress,never()).isAltKeyDown();
    }
    
    @Test 
    public void maskKeyPress_noneDisplayKey() {
        String mask = "999-999-9999";
        text.setMask(mask);
        KeyPressEvent keyPress = mock(KeyPressEvent.class);
        when(keyPress.isAltKeyDown()).thenReturn(true);
        
        text.maskKeyPress(keyPress);
        
        verify(text.box,never()).getText();
    }
    
    @Test 
    public void maskKeyPress_firstValid() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("");
        when(text.box.getCursorPos()).thenReturn(0);
        
        text.maskKeyPress(keyPress('5'));
        
        verify(text.box).setText("5");
        verify(text.box).setCursorPos(1);
    }
    
    @Test 
    public void maskKeyPress_firstInValid() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("");
        when(text.box.getCursorPos()).thenReturn(0);
        
        text.maskKeyPress(keyPress('A'));
        
        verify(text.box).setText("");
        verify(text.box).setCursorPos(0);    
    }
    
    @Test
    public void maskKeyPress_insertLiteral() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("555");
        when(text.box.getCursorPos()).thenReturn(3);
        
        text.maskKeyPress(keyPress('5'));
        
        verify(text.box).setText("555-5");
        verify(text.box).setCursorPos(5);    
    }
    
    @Test
    public void maskKeyPress_pastMask() {
        String mask = "999-999-9999";
        text.setMask(mask);
        when(text.box.getText()).thenReturn("555-555-5555");
        when(text.box.getCursorPos()).thenReturn(12);
        
        text.maskKeyPress(keyPress('5'));
        
        verify(text.box,never()).setText(anyString());
        verify(text.box,never()).setCursorPos(anyInt());    
    }
    
    private KeyDownEvent keyDown(int key) {
        KeyDownEvent keyDown = mock(KeyDownEvent.class);
        NativeEvent event = mock(NativeEvent.class);
        when(keyDown.getNativeEvent()).thenReturn(event);
        when(event.getKeyCode()).thenReturn(key);
        
        return keyDown;
    }
    
    private KeyPressEvent keyPress(int key) {
        KeyPressEvent keyPress = mock(KeyPressEvent.class);
        NativeEvent event = mock(NativeEvent.class);
        when(keyPress.getNativeEvent()).thenReturn(event);
        when(event.getKeyCode()).thenReturn(key);
        
        return keyPress;
    }
    

}
