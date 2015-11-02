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
public class TestIntegerHelper {
    
    IntegerHelper helper;
    UIMessages messages;
    
    @Before
    public void preArrange() {
        LocaleProxy.initialize();
        messages = LocaleFactory.get(UIMessages.class, "en");
        
        helper = new IntegerHelper() {
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
        QueryData qd = helper.getQuery("54");
        
        assertNotNull(qd);
        assertEquals(QueryData.Type.INTEGER,qd.getType());
        assertEquals("54",qd.getQuery());
        assertNull(qd.getKey());   
    }
    
    @Test
    public void getValue_null() throws Exception {
        assertNull(helper.getValue(null));
        assertNull(helper.getValue(""));
    }
    
    @Test 
    public void getValue() throws Exception {
        assertEquals(new Integer(54),helper.getValue("54"));
    }
    
    @Test(expected=Exception.class)
    public void getValue_throwException() throws Exception {
        helper.getValue("abc");
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
        assertEquals("54",helper.format(new Integer(54)));
    }
    
    @Test
    public void isCorrectType() {
        assertTrue(helper.isCorrectType(null));
        assertTrue(helper.isCorrectType(new Integer(4)));
        assertFalse(helper.isCorrectType("4"));
    }
    
    @Test 
    public void validate() {
        assertTrue(helper.validate(new Integer(4)).isEmpty());
        assertEquals(1,helper.validate("4").size());
    } 
}
