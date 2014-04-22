package org.openelis.ui.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import net.lightoze.gwt.i18n.client.LocaleFactory;
import net.lightoze.gwt.i18n.server.LocaleProxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.UIMessages;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestDoubleHelper {
    
    DoubleHelper helper;
    UIMessages messages;
    
    @Before
    public void init() {
        LocaleProxy.initialize();
        messages = LocaleFactory.get(UIMessages.class, "en");
        
        helper = new DoubleHelper() {
            @Override
            protected UIMessages getMessages() {
                return messages;
            }
        };
    }
    
    @Test
    public void getQueryNull() {
        assertNull(helper.getQuery(null));
        assertNull(helper.getQuery(""));
    }
    
    @Test
    public void getQueryValid() {
        QueryData qd = helper.getQuery("54.65");
        
        assertNotNull(qd);
        assertEquals(QueryData.Type.DOUBLE,qd.getType());
        assertEquals("54.65",qd.getQuery());
        assertNull(qd.getKey());   
    }
    
    @Test
    public void getValueNull() throws Exception {
        assertNull(helper.getValue(null));
        assertNull(helper.getValue(""));
    }
    
    @Test 
    public void getValue() throws Exception {
        assertEquals(new Double(54.65),helper.getValue("54.65"));
    }
    
    @Test(expected=Exception.class)
    public void getValueException() throws Exception {
        helper.getValue("abc");
    }
    
    @Test
    public void validateQueryNull() {

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
    public void formatNull() {
        assertEquals("",helper.format(null));
    }
    
    @Test 
    public void format() {
        assertEquals("54.65",helper.format(new Double(54.65)));
    }
    
    @Test
    public void isCorrectType() {
        assertTrue(helper.isCorrectType(null));
        assertTrue(helper.isCorrectType(new Double(4)));
        assertFalse(helper.isCorrectType("4"));
    }
    
    @Test 
    public void validate() {
        assertTrue(helper.validate(new Double(4)).isEmpty());
        assertEquals(1,helper.validate("4").size());
    } 
    
    
   
    
    
    
    

    
}
