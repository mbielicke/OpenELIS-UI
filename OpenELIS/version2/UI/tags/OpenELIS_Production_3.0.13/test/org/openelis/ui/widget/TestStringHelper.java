package org.openelis.ui.widget;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.UIMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestStringHelper {

    StringHelper helper;
    UIMessages messages;
    
    @Before
    public void preArrange() {
        messages = GWT.create(UIMessages.class);
        helper = new StringHelper() {
            @Override
            protected UIMessages getMessages() {
                return messages;
            }
        };
    }
    
    @Test
    public void getQuery_null() {
        assertNull(helper.getQuery(null));
        assertNull(helper.getQuery(""));
    }
    
    @Test
    public void getQuery_valid() {
        QueryData qd = helper.getQuery("n");
        
        assertNotNull(qd);
        assertEquals(QueryData.Type.STRING,qd.getType());
        assertEquals("n",qd.getQuery());
        assertNull(qd.getKey());   
    }
    
    @Test
    public void getValue_null() throws Exception {
        assertNull(helper.getValue(null));
        assertNull(helper.getValue(""));
    }
    
    @Test 
    public void getValue() throws Exception {
        assertEquals("value",helper.getValue("value"));
    }
    
    @Test
    public void validateQuery_null() {
        try {
            helper.validateQuery(null);
        }catch(Exception e) {
            fail("Should not throw exception on null");
        }
        
        try {
            helper.validateQuery("");
        }catch(Exception e) {
            fail("Should not throw exception on empty string");
        }
    }
    
    @Test
    public void format_null() {
        assertEquals("",helper.format(null));
    }
    
    @Test 
    public void format() {
        assertEquals("value",helper.format("value"));
    }
    
    @Test
    public void isCorrectType() {
        assertTrue(helper.isCorrectType(null));
        assertTrue(helper.isCorrectType("String"));
        assertFalse(helper.isCorrectType(4));
    }
    
    @Test 
    public void validate() {
        assertTrue(helper.validate("value").isEmpty());
        assertEquals(1,helper.validate(4).size());
    } 
}
