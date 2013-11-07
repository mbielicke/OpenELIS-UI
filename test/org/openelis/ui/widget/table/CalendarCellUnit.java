package org.openelis.ui.widget.table;

import java.util.Date;

import org.openelis.ui.common.Datetime;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.CalendarCell;

import com.google.gwt.junit.client.GWTTestCase;

public class CalendarCellUnit extends GWTTestCase {

	Calendar     cal;
	CalendarCell test;
	
	@Override
	public String getModuleName() {
		return "org.openelis.ui.UI";
	}
	
	@Override
	protected void gwtSetUp() throws Exception {	
		cal = new Calendar();
		test = new CalendarCell(cal);
	}
	
	public void testDisplay() {
		assertEquals("2012-05-15",test.display(Datetime.getInstance(Datetime.YEAR,Datetime.DAY,new Date("2012/05/15"))));
		assertEquals("asdasd",test.display("asdasd"));
		assertEquals("",test.display(null));
		assertEquals("5",test.display(5));
	}
	
	public void testValidate() {
		assertTrue(test.validate(Datetime.getInstance()).isEmpty());
		assertFalse(test.validate("asdasd").isEmpty());
	}
	
	public void testFinishEditing() {
		cal.setText("2012-05-15");
		assertEquals(Datetime.getInstance(Datetime.YEAR,Datetime.DAY,new Date("2012/05/15")),test.finishEditing());
		cal.setText("asdasd");
		assertEquals("asdasd",test.finishEditing());
	}
	
	
	
	

}
