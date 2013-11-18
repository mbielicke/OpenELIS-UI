package org.openelis.ui.widget.table;

import java.util.Date;

import org.openelis.ui.common.Datetime;
import org.openelis.ui.widget.DateHelper;
import org.openelis.ui.widget.DoubleHelper;
import org.openelis.ui.widget.IntegerHelper;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.table.TextBoxCell;

import com.google.gwt.junit.client.GWTTestCase;

public class TextBoxCellUnit extends GWTTestCase {

	TextBox<String> stringEditor;
	TextBox<Integer> intEditor;
	TextBox<Double> doubleEditor;
	TextBox<Datetime> dateEditor;
	TextBoxCell test; 

	
	@Override
	public String getModuleName() {
		return "org.openelis.ui.Widget";
	}
	
	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		stringEditor = new TextBox<String>();
		
		intEditor = new TextBox<Integer>();
		intEditor.setHelper(new IntegerHelper());
		
		doubleEditor = new TextBox<Double>();
		doubleEditor.setHelper(new DoubleHelper());
		
		dateEditor = new TextBox<Datetime>();
		dateEditor.setHelper(new DateHelper());
		
	}
	
	
	public void testDisplay() {
		test = new TextBoxCell(stringEditor);
		
	    assertEquals("",test.display(null));
	    assertEquals("5",test.display(5));
	    assertEquals("sdf",test.display("sdf"));
	    
	    test = new TextBoxCell(intEditor);
	    
	    assertEquals("",test.display(null));
	    assertEquals("5",test.display(5));
	    assertEquals("sdf",test.display("sdf"));
	    assertEquals("5.0",test.display(5.0));
	    
	    test = new TextBoxCell(doubleEditor);
	    
	    assertEquals("",test.display(null));
	    assertEquals("5",test.display(5));
	    assertEquals("5.0",test.display(5.0));
	    assertEquals("sdf",test.display("sdf"));
	    
	    test = new TextBoxCell(dateEditor);
	    
	    assertEquals("",test.display(null));
	    assertEquals("2012-05-15",test.display(Datetime.getInstance(Datetime.YEAR,Datetime.DAY,new Date("2012/05/15"))));
	    assertEquals("5",test.display(5));
	    assertEquals("5.0",test.display(5.0));
	    assertEquals("sdf",test.display("sdf"));
	}
	
	public void testValidate() {
		test = new TextBoxCell(intEditor);
		
		assertTrue(test.validate(5).isEmpty());
		assertFalse(test.validate("5").isEmpty());
		
		
		test = new TextBoxCell(doubleEditor);
		
		assertTrue(test.validate(5.0).isEmpty());
		assertFalse(test.validate("5.0").isEmpty());
		
		test = new TextBoxCell(dateEditor);
		
		assertTrue(test.validate(Datetime.getInstance()).isEmpty());
		assertFalse(test.validate("334f335").isEmpty());
	}
	
	public void testFinishEditing() {
		test = new TextBoxCell(stringEditor);
		
		stringEditor.setText("asdasd");
		assertEquals("asdasd",test.finishEditing());
		
		test = new TextBoxCell(intEditor);
		
		intEditor.setText("5");
		assertEquals(5,test.finishEditing());
		intEditor.setText("asdasd");
		assertEquals("asdasd",test.finishEditing());
		
		test = new TextBoxCell(doubleEditor);
		doubleEditor.setText("5.0");
		assertEquals(5.0,test.finishEditing());
		doubleEditor.setText("asdasd");
		assertEquals("asdasd",test.finishEditing());
		
		test = new TextBoxCell(dateEditor);
		dateEditor.setText("2012-05-15");
		assertEquals(Datetime.getInstance(Datetime.YEAR,Datetime.DAY,new Date("2012/05/15")),test.finishEditing());
		dateEditor.setText("asdasd");
		assertEquals("asdasd",test.finishEditing());
		
	}
	
	
	
	
	

}