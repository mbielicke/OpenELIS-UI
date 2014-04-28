package org.openelis.ui.widget;

import org.openelis.ui.widget.table.GwtTable;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.gwt.junit.tools.GWTTestSuite;

public class GwtTestOpenELISLib extends GWTTestSuite {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Test Suite for all Lib Widgets");
		suite.addTestSuite(GwtTextBox.class);
		suite.addTestSuite(GwtDropdown.class);
		suite.addTestSuite(GwtTable.class);
		return suite;
	}
}
