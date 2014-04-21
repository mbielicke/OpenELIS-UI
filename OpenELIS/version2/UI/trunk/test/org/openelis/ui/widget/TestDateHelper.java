package org.openelis.ui.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;

import net.lightoze.gwt.i18n.client.LocaleFactory;
import net.lightoze.gwt.i18n.server.LocaleProxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.UIMessages;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class TestDateHelper {
    
    DateHelper helper;
    UIMessages messages;
    
    @Before
    public void init() {
        LocaleProxy.initialize();
        messages = LocaleFactory.get(UIMessages.class, "en");
        
        helper = new DateHelper() {
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
        QueryData qd = helper.getQuery("2012-12-31");
        
        assertNotNull(qd);
        assertEquals(QueryData.Type.DATE,qd.getType());
        assertEquals("2012-12-31",qd.getQuery());
        assertNull(qd.getKey());   
    }
    
    @Test
    public void getValueNull() throws Exception {
        assertNull(helper.getValue(null));
        assertNull(helper.getValue(""));
    }
    
    @Test 
    public void getValue() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2014,3,17);
        assertEquals(Datetime.getInstance(helper.begin,helper.end,cal.getTime()),
                     helper.getValue("2014-04-17"));
    }
    
    @Test 
    public void getValueYM() throws Exception {
        helper.setEnd(Datetime.MINUTE);
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 3, 17, 4, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        assertEquals(Datetime.getInstance(helper.begin,helper.end,cal.getTime()),
                     helper.getValue("2014-04-17 04:30"));
    }
    
    @Test
    public void getValueHM() throws Exception {
        helper.setBegin(Datetime.HOUR);
        helper.setEnd(Datetime.MINUTE);
        Calendar cal = Calendar.getInstance();
        cal.set(0, 0, 0, 4, 30, 0);
        cal.set(Calendar.MILLISECOND,0);

        assertEquals(Datetime.getInstance(helper.begin,helper.end,cal.getTime()),
                     helper.getValue("04:30"));
    }
    
    @Test(expected=Exception.class) 
    public void getValueBadDate() throws Exception {
        helper.getValue("bad date");
    }
    
    @Test
    public void validateQueryNull() {
        try {
            helper.validate(null);
        }catch(Exception e) {
            fail("Should not throw exception on null");
        }
        
        try {
            helper.validate("");
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
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 3, 17);
        assertEquals("2014-04-17",
                     helper.format(Datetime.getInstance(helper.begin,helper.end,cal.getTime())));   
    }
    
    @Test
    public void formatYM() {
        helper.setEnd(Datetime.MINUTE);
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 3, 17, 4, 30, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        assertEquals("2014-04-17 04:30",
                     helper.format(Datetime.getInstance(helper.begin,helper.end,cal.getTime())));   
        
    }
    
    @Test
    public void formatHM() {
        helper.setBegin(Datetime.HOUR);
        helper.setEnd(Datetime.MINUTE);
        Calendar cal = Calendar.getInstance();
        cal.set(0, 0, 0, 4, 30, 0);
        cal.set(Calendar.MILLISECOND,0);
        
        assertEquals("04:30",
                     helper.format(Datetime.getInstance(helper.begin,helper.end,cal.getTime())));   
    }
    
    @Test
    public void isCorrectType() {
        assertTrue(helper.isCorrectType(null));
        assertTrue(helper.isCorrectType(Datetime.getInstance()));
        assertFalse(helper.isCorrectType(4));
    }
    
    @Test 
    public void validate() {
        assertTrue(helper.validate(Datetime.getInstance()).isEmpty());
        assertEquals(1,helper.validate(4).size());
    } 
    
    
    
    
}
