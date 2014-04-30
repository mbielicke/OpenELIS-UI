package org.openelis.ui.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class TestDatetime {
    
    @Test
    public void getDate_YD() {
        Datetime time = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.DAY,
                                             createDate(2014,3,23));

        assertEquals(createDate(2014,3,23),time.getDate());
    }
    
    @Test
    public void getDate_YM() {
        Datetime time = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.MINUTE,
                                             createTimestamp(2014,3,23,14,22));

        assertEquals(createTimestamp(2014,3,23,14,22),time.getDate());
    }
    
    @Test
    public void getDate_HM() {
        Datetime time = Datetime.getInstance(Datetime.HOUR,
                                             Datetime.MINUTE,
                                             createTime(14,22));
       
        assertEquals(createTime(14,22),time.getDate());
    }
    
    @Test
    public void before_YD_true() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,3,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,4,1));
        assertTrue(d1.before(d2));
    }
    
    @Test
    public void before_YD_false() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,4,2));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,4,1));
        assertFalse(d1.before(d2));
    }
    
    @Test
    public void before_YM_true() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2014,3,30,7,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,3,30,13,25));
        assertTrue(d1.before(d2));
    }
    
    @Test
    public void before_YM_false() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2014,3,30,13,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,3,30,7,25));
        assertFalse(d1.before(d2));
    }
    
    @Test
    public void before_HM_true() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(7,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(13,30));
        assertTrue(d1.before(d2));
    }
    
    @Test
    public void before_HM_false() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(13,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(7,30));
        assertFalse(d1.before(d2));
    }
    
    @Test
    public void after_YD_true() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,4,1));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,3,30));
        assertTrue(d1.after(d2));
    }
    
    @Test
    public void after_YD_false() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,4,1));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,4,2));
        assertFalse(d1.after(d2));
    }
    
    @Test
    public void after_YM_true() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2014,3,30,13,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,3,30,7,25));
        assertTrue(d1.after(d2));
    }
    
    @Test
    public void after_YM_false() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2014,3,30,7,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,3,30,13,25));
        assertFalse(d1.after(d2));
    }
    
    @Test
    public void after_HM_true() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(13,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(7,30));
        assertTrue(d1.after(d2));
    }
    
    @Test
    public void after_HM_false() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(7,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(13,30));
        assertFalse(d1.after(d2));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void after_null() {
        Datetime d1 = Datetime.getInstance();
        Datetime d2 = null;
        d1.after(d2);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void before_null() {
        Datetime d1 = Datetime.getInstance();
        Datetime d2 = null;
        d1.before(d2);
    }
    
    @Test
    public void equals_null() {
        assertFalse(Datetime.getInstance().equals(null));
    }
    
    @Test
    public void equals_YD_true() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,4,1));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,4,1));
        assertTrue(d1.equals(d2));
    }
    
    @Test
    public void equals_YD_false() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,4,1));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,3,30));
        assertFalse(d1.equals(d2));
    }
    
    @Test
    public void equals_YM_true() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2014,3,30,13,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,3,30,13,30));
        assertTrue(d1.equals(d2));
    }
    
    @Test
    public void equals_YM_false() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2014,3,30,7,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,4,1,7,30));
        assertFalse(d1.equals(d2));
    }
    
    @Test
    public void equals_HM_true() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(13,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(13,39));
        assertTrue(d1.equals(d2));
    }
    
    @Test
    public void equals_HM_false() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(7,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(13,39));
        assertFalse(d1.after(d2));
    }
    
    
    @Test
    public void compare_YD_neg() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,3,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,4,1));
        assertEquals(-1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_YD_pos() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,4,2));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,4,1));
        assertEquals(1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_YM_neg() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2014,3,30,7,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,3,30,13,25));
        assertEquals(-1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_YM_pos() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2104,3,30,13,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,3,30,7,25));
        assertEquals(1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_HM_neg() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(7,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(13,30));
        assertEquals(-1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_HM_pos() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(13,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(7,30));
        assertEquals(1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_YD_pos_1() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,4,1));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,3,30));
        assertEquals(1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_YD_neg_1() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,4,1));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,4,2));
        assertEquals(-1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_YM_pos_1() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2014,3,30,13,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,3,30,7,25));
        assertEquals(1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_YM_neg_1() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2014,3,30,7,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,3,30,13,25));
        assertEquals(-1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_HM_pos_1() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(13,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(7,30));
        assertEquals(1,d1.compareTo(d2));
    }
    
    @Test
    public void compareTo_HM_neg_1() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(7,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(13,30));
        assertEquals(-1,d1.compareTo(d2));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void compareTo_null() {
        Datetime d1 = Datetime.getInstance();
        Datetime d2 = null;
        d1.compareTo(d2);
    }
 
    @Test
    public void compareTo_YD_0() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.DAY, 
                                           createDate(2014,4,1));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.DAY,
                                           createDate(2014,4,1));
        assertEquals(0,d1.compareTo(d2));
    }
    
    
    @Test
    public void compareTo_YM_0() {
        Datetime d1 = Datetime.getInstance(Datetime.YEAR, 
                                           Datetime.MINUTE, 
                                           createTimestamp(2014,3,30,13,30));

        Datetime d2 = Datetime.getInstance(Datetime.YEAR,
                                           Datetime.MINUTE,
                                           createTimestamp(2014,3,30,13,30));
        assertEquals(0,d1.compareTo(d2));
    }
       
    @Test
    public void comparesTo_HM_0() {
        Datetime d1 = Datetime.getInstance(Datetime.HOUR, 
                                           Datetime.MINUTE, 
                                           createTime(13,39));

        Datetime d2 = Datetime.getInstance(Datetime.HOUR,
                                           Datetime.MINUTE,
                                           createTime(13,39));
        assertEquals(0,d1.compareTo(d2));
    }
    
    @Test
    public void add_forward_1day() {
        Datetime date = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.DAY,
                                             createDate(2014,3,30));
                
        Datetime comp = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.DAY,
                                             createDate(2014,4,1));
        
        assertEquals(comp,date.add(1));
    }
    
    @Test
    public void add_backward_1day() {
        Datetime date = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.DAY,
                                             createDate(2014,4,1));
        
        Datetime comp = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.DAY,
                                             createDate(2014,3,30));
        
        assertEquals(comp,date.add(-1));
    }
    
    @Test 
    public void add_forwardOnDaylightSavings() {
        Datetime date = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.DAY,
                                             createDate(2014,2,9));
    
        Datetime comp = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.DAY,
                                             createDate(2014,2,10));
    
        assertEquals(comp,date.add(1));
    }
    
    @Test 
    public void add_backwardOnDaylightSavings() {
        Datetime date = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.DAY,
                                             createDate(2014,10,3));
    
        Datetime comp = Datetime.getInstance(Datetime.YEAR,
                                             Datetime.DAY,
                                             createDate(2014,10,2));
    
        assertEquals(comp,date.add(-1));
    }
    
    
    
    private Date createDate(int year, int month, int date) {
        return createTimestamp(year,month,date,0,0);
    }
    
    @SuppressWarnings("deprecation")
    private Date createTime(int hour, int minute) {
        Date time = new Date();
        time.setYear(0);
        time.setMonth(0);
        time.setDate(0);
        time.setHours(hour);
        time.setMinutes(minute);
        time.setSeconds(0);
        
        return time;
    }
    
    private Date createTimestamp(int year, int month, int date, int hour,int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, date, hour, minute, 0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTime();
    }
    
}
