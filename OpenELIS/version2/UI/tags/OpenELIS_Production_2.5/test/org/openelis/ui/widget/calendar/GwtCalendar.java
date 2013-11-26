package org.openelis.ui.widget.calendar;

import java.util.Date;

import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.DateHelper;
import org.openelis.ui.widget.calendar.Calendar;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.DOM;

public class GwtCalendar extends GWTTestCase {

	Calendar test;
	
	@Override
	public String getModuleName() {
		return "org.openelis.ui.UI";
	}
	
	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		
		test = new Calendar();
		test.setPrecision(Datetime.YEAR,Datetime.DAY);
		
	}
	
	public void testSimple() {
		
	}
	
	public void testSetEnabled() {
		test.setEnabled(true);
		
		assertTrue(test.isEnabled());
		assertTrue(test.button.isEnabled());
		
		test.setEnabled(false);
		
		assertFalse(test.isEnabled());
		assertFalse(test.button.isEnabled());
	}
	
	public void testSetPrecision() {
		assertEquals(Datetime.YEAR, ((DateHelper)test.getHelper()).getBegin());
		assertEquals(Datetime.DAY,((DateHelper)test.getHelper()).getEnd());
		
		try {
			test.setPrecision(Datetime.DAY, Datetime.YEAR);
			fail("should have thrown assertion error");
		}catch(AssertionError e){
			
		}
	}
	
	public void testSetValue() {
		final Datetime value;
		
		value = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,new Date("2012/04/15"));
		
		test.setValue(value);
		
		assertEquals("2012-04-15",test.getText());
		assertEquals(value,test.getValue());
		
		test.setValue(null);
		
		assertEquals("",test.getText());
		assertNull(test.getValue());
		
		test.addValueChangeHandler(new ValueChangeHandler<Datetime>() {
			public void onValueChange(ValueChangeEvent<Datetime> event) {
				assertEquals(value,event.getValue());
			}
		}); 
	
		
		test.setValue(value,true);
		
	}
	
	public void testFinishEditing() {
		Datetime value = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,new Date("2012/04/15"));
		
		test.setEnabled(true);
		
		test.setText("2012-04-15");
		test.finishEditing();
		
		assertEquals("2012-04-15",test.getText());
		assertEquals(value,test.getValue());
		
		test.setText("1dasd2");
		test.finishEditing();
		
		assertTrue(test.hasExceptions());
		assertTrue(test.getStyleName().contains(test.css.InputError()));
		assertEquals("1dasd2",test.getText());
		
		test.setText("2012-04-15");
		test.finishEditing();
		
		assertFalse(test.hasExceptions());
		assertFalse(test.getStyleName().contains(test.css.InputError()));
		assertEquals("2012-04-15",test.getText());
		assertEquals(value,test.getValue());
			
	}
	
	public void testHasExceptions() {
		Datetime value = Datetime.getInstance(Datetime.YEAR,Datetime.DAY,new Date("2012/04/15"));
		
		test.setEnabled(true);
		test.setRequired(true);
		
		assertTrue(test.hasExceptions());
		assertTrue(test.getStyleName().contains(test.css.InputError()));
		
		test.setText("2012-04-15");
		test.finishEditing();
		
		assertFalse(test.hasExceptions());
		assertFalse(test.getStyleName().contains(test.css.InputError()));
		assertEquals(value,test.getValue());
	}
	
	public void testQueryMode() {
		test.setEnabled(true);
		test.setQueryMode(true);
	
		assertTrue(test.isQueryMode());
		assertFalse(test.textbox.isMaskEnforced());
		
		test.setQueryMode(false);
		
		assertFalse(test.isQueryMode());
		assertTrue(test.textbox.isMaskEnforced());
		
	}
	
	public void testGetQuery() {
		QueryData qd;
		
		test.setEnabled(true);
		test.setQueryMode(true);
		test.setText("> 2012-04-15");
		test.validateQuery();
		
		qd = (QueryData)test.getQuery();
		
		assertNotNull(qd);
		assertEquals("> 2012-04-15",qd.getQuery());
		assertEquals(QueryData.Type.DATE,qd.getType());
		
		test.setText("2012-06-01..2012-06-11");
		test.finishEditing();
		test.validateQuery();
		
		assertFalse(test.hasExceptions());
		qd = (QueryData)test.getQuery();
		assertNotNull(qd);
		assertEquals("2012-06-01..2012-06-11",qd.getQuery());
		
	}
	
	
	boolean onValueCalled;
	
	public void testBlur() {
		onValueCalled = false;
		test.addValueChangeHandler(new ValueChangeHandler<Datetime>() {
			@Override
			public void onValueChange(ValueChangeEvent<Datetime> event) {
				onValueCalled = true;
			}
		});
		
		test.setEnabled(true);
		
		test.setText("2012-07-03");
		BlurEvent.fireNativeEvent(com.google.gwt.dom.client.Document.get().createBlurEvent(), test.textbox);
		assertTrue(onValueCalled);
	}
	
	

 }
