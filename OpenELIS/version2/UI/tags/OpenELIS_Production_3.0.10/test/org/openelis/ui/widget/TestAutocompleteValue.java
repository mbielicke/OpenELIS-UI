package org.openelis.ui.widget;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class TestAutocompleteValue {
    
    @Test
    public void equals_bothNull() {
        AutoCompleteValue av = new AutoCompleteValue();
        assertTrue(av.equals(new AutoCompleteValue()));
    }
    
    @Test
    public void equals_firstNull() {
        AutoCompleteValue av = new AutoCompleteValue();
        assertFalse(av.equals(new AutoCompleteValue(new Integer(1),"display")));
    }
    
    @Test
    public void equals_secondNull() {
        AutoCompleteValue av = new AutoCompleteValue(new Integer(1),"display");
        assertFalse(av.equals(new AutoCompleteValue()));
    }
    
    @Test
    public void equals_null() {
         AutoCompleteValue av = new AutoCompleteValue(new Integer(1),"display");
         assertFalse(av.equals(null));
    }
    
    @Test
    public void equals_wrongType() {
        AutoCompleteValue av = new AutoCompleteValue(new Integer(1), "display");
        assertFalse(av.equals(new Integer(1)));
    }
    
    @Test
    public void equals_same() {
        AutoCompleteValue av = new AutoCompleteValue(new Integer(1), "display");
        assertTrue(av.equals(new AutoCompleteValue(new Integer(1),"display")));
    }
    
    @Test 
    public void equals_different() {
        AutoCompleteValue av = new AutoCompleteValue(new Integer(1),"display");
        assertFalse(av.equals(new AutoCompleteValue(new Integer(2),"string")));
    }
}
