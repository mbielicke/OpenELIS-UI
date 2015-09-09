/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.ui.widget.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import net.lightoze.gwt.i18n.client.LocaleFactory;
import net.lightoze.gwt.i18n.server.LocaleProxy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.UIMessages;
import org.openelis.ui.widget.DateHelper;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;

/**
 * This class will Unit Test the Calendar widget
 */
@RunWith(GwtMockitoTestRunner.class)
public class TestCalendar {
    
    Calendar calendar;    
    UIMessages messages;
    SimpleEventBus bus;
    
    @Before
    public void preArrange() {
        bus = new SimpleEventBus();
        
        LocaleProxy.initialize();
        messages = LocaleFactory.get(UIMessages.class, "en");
        
        calendar = new Calendar() {
            public String style = "";
            @Override
            protected UIMessages getMessages() {
                return messages;
            }
            @Override
            public void fireEvent(GwtEvent<?> event) {
                bus.fireEvent(event);
                super.fireEvent(event);
            }

            @Override
            public void setStyleName(String style) {
                this.style = style;
                super.setStyleName(style);
            }
            
            @Override
            public void addStyleName(String style) {
                if(!this.style.contains(style))
                    this.style += " "+style;
                super.addStyleName(style);
            }
            
            @Override
            public void removeStyleName(String style) {
                this.style = this.style.replace(style,"");
                super.removeStyleName(style);
            }
            
            @Override
            public String getStyleName() {
                return style;
            }
        };
        
        calendar.helper = new DateHelper() {
            @Override
            protected UIMessages getMessages() {
                return messages;
            }
        };
        
       
    }
    
    @Test
    public void setEnabled() {
        calendar.setEnabled(true);
        
        verify(calendar.textbox).enforceMask(true);
        verify(calendar.textbox).setReadOnly(false);
    }
    
    @Test
    public void setEnabled_false() {
        calendar.setEnabled(false);
        
        verify(calendar.textbox).enforceMask(false);
        verify(calendar.textbox).setReadOnly(true);
    }
    
    @Test
    public void setDefaultMask_YD() {
        calendar.setPrecision(Datetime.YEAR, Datetime.DAY);
        
        verify(calendar.textbox).setMask(messages.gen_dateMask());
    }
    
    @Test
    public void setDefaultMask_YM() {
        calendar.setPrecision(Datetime.YEAR, Datetime.MINUTE);
        
        verify(calendar.textbox).setMask(messages.gen_dateTimeMask());
    }
    
    @Test
    public void setDefaultMask_HM() {
        calendar.setPrecision(Datetime.HOUR,Datetime.MINUTE);
        
        verify(calendar.textbox).setMask(messages.gen_timeMask());
    }
    
    @Test
    public void setValue_null() {
        ensureValueChangeNotFired();
        calendar.setValue(null);
        
        verify(calendar.textbox).setText("");
        assertNull(calendar.value);
    }
    
    @Test
    public void setValue() {
        ensureValueChangeNotFired();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2014, 5, 2);
        Datetime date = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,cal.getTime());
        
        calendar.setValue(date);
        
        assertEquals(date,calendar.getValue());
        verify(calendar.textbox).setText("2014-06-02");
    }
    
    @Test
    public void setValue_unset() {
        ensureValueChangeNotFired();
        Datetime date = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,new Date("2014/06/02"));
        calendar.value = date;
        
        calendar.setValue(null);
        
        assertNull(calendar.getValue());
        verify(calendar.textbox).setText("");
    }
    
    @Test
    public void finishEditing() {
        when(calendar.textbox.getText()).thenReturn("2014-06-02");
        when(calendar.textbox.isReadOnly()).thenReturn(false);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2014, 5, 2);
        Datetime date = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,cal.getTime());
        ensureValueChangeFiredEquals(date);
        
        calendar.finishEditing(true);
        
        assertEquals(date,calendar.getValue());
        verify(calendar.textbox).setText("2014-06-02");
    }
    
    @Test 
    public void finishEditing_clearValue() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2014, 5, 2);
        Datetime date = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,cal.getTime());
        calendar.value = date;
        when(calendar.textbox.getText()).thenReturn("");
        when(calendar.textbox.isReadOnly()).thenReturn(false);
        ensureValueChangeFiredEquals(null);
        
        calendar.finishEditing(true);
        
        assertNull(calendar.getValue());
        verify(calendar.textbox).setText("");
    }
    
    @Test
    public void finishEditing_required() {
        calendar.setRequired(true);
        when(calendar.textbox.getText()).thenReturn("");
        when(calendar.textbox.isReadOnly()).thenReturn(false);
        calendar.finishEditing();
        
        assertTrue(calendar.exceptions.getValidateExceptions().size() > 0);
        assertTrue(hasErrorStyle());
    }
    
    @Test
    public void finishEditing_clearException() {
        calendar.addValidateException(new Exception());
        when(calendar.textbox.getText()).thenReturn("2014-06-02");
        when(calendar.textbox.isReadOnly()).thenReturn(false);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2014, 5, 2);
        Datetime date = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,cal.getTime());
        ensureValueChangeFiredEquals(date);
        
        calendar.finishEditing(true);
        
        assertNull(calendar.getValidateExceptions());
    }
    
    @Test
    public void validateQuery() {
        when(calendar.textbox.getText()).thenReturn("2014-06-12");
        calendar.setQueryMode(true);
        
        calendar.validateQuery();
        
        assertNull(calendar.getValidateExceptions());
    }
    
    @Test 
    public void validateQuery_invalid() {
        when(calendar.textbox.getText()).thenReturn("dfsdsd");
        calendar.setQueryMode(true);
        
        calendar.validateQuery();
        
        assertTrue(calendar.getValidateExceptions().size() > 0);
        assertTrue(hasErrorStyle());
    }
    
    @Test
    public void hasExceptions() {
        assertFalse(calendar.hasExceptions());
    }
    
    @Test
    public void hasExceptions_addRequired() {
        calendar.setRequired(true);
        
        assertTrue(calendar.hasExceptions());
        
        assertTrue(calendar.getValidateExceptions().size() > 0);
        assertTrue(hasErrorStyle());
    }    
    
    @Test
    public void clearExceptions() {
        calendar.addException(new Exception());
        calendar.addValidateException(new Exception());
        assertTrue(hasErrorStyle());
        
        calendar.clearExceptions();
        
        assertNull(calendar.getEndUserExceptions());
        assertNull(calendar.getValidateExceptions());
        assertFalse(hasErrorStyle());
    }
    
    @Test
    public void clearEndUserExceptions() {
        calendar.addException(new Exception());
        calendar.addValidateException(new Exception());
        assertTrue(hasErrorStyle());
        
        calendar.clearEndUserExceptions();
        
        assertNull(calendar.getEndUserExceptions());
        assertTrue(calendar.getValidateExceptions().size() > 0);
        assertTrue(hasErrorStyle());
    }
    
    @Test
    public void clearValidateExceptions() {
        calendar.addException(new Exception());
        calendar.addValidateException(new Exception());
        assertTrue(hasErrorStyle());
        
        calendar.clearValidateExceptions();
        
        assertTrue(calendar.getEndUserExceptions().size() > 0);
        assertNull(calendar.getValidateExceptions());
        assertTrue(hasErrorStyle());
    }
    
    @Test
    public void hasExceptions_endUserOnly() {
        calendar.addException(new Exception());
        
        assertTrue(calendar.hasExceptions());
        assertTrue(calendar.getEndUserExceptions().size() > 0);
        assertTrue(hasErrorStyle());
    }
    
    @Test
    public void setQueryMode() {
        when(calendar.textbox.isReadOnly()).thenReturn(false);
        calendar.setQueryMode(true);
        
        assertTrue(calendar.queryMode);
        verify(calendar.textbox).enforceMask(false);
        verify(calendar.textbox).setText("");
    }
    
    @Test
    public void setQueryMode_doNothing() {
        calendar.setQueryMode(false);
        
        verify(calendar.textbox,never()).setText("");
    }
    
    @Test
    public void setQueryMode_exit() {
        calendar.queryMode = true;
        when(calendar.textbox.isReadOnly()).thenReturn(false);
        calendar.setQueryMode(false);
        
        assertFalse(calendar.queryMode);
        verify(calendar.textbox).enforceMask(true);
    }
    
    @Test
    public void setQueryData() {
        QueryData qd = new QueryData();
        qd.setQuery("2014-06-03");
        
        calendar.setQuery(qd);
        
        verify(calendar.textbox).setText("2014-06-03");
    }
    
    @Test
    public void setQueryData_null() {
        calendar.setQuery(null);
        
        verify(calendar.textbox).setText("");
    }
    
    @Test
    public void onFocus() {
        FocusEvent focus = mock(FocusEvent.class);
        when(focus.getNativeEvent()).thenReturn(mock(NativeEvent.class));
        calendar.onFocus(focus);
    }
    
    @Test 
    public void onBlur() {
        BlurEvent blur = mock(BlurEvent.class);
        when(blur.getNativeEvent()).thenReturn(mock(NativeEvent.class));
        calendar.onBlur(blur);
    }
    
    private void ensureValueChangeNotFired() {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<Datetime>() {
            @Override
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                fail("Value Change should not have fired");
            }
        });
    }
    
    private void ensureValueChangeFiredEquals(final Datetime value) {
        bus.addHandler(ValueChangeEvent.getType(), new ValueChangeHandler<Datetime>() {
            @Override
            public void onValueChange(ValueChangeEvent<Datetime> event) {
                assertEquals(value,event.getValue());
            }
        });
    }
    
    private boolean hasErrorStyle() {
        return calendar.getStyleName().contains(calendar.css.InputError());
    }
    
    
    
}
