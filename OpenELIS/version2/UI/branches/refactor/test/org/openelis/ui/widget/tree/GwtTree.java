package org.openelis.ui.widget.tree;

import java.util.Arrays;

import org.junit.Ignore;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.tree.StaticView;
import org.openelis.ui.widget.tree.StaticView.TreeGrid;

import com.google.gwt.dom.client.Document;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class GwtTree extends GWTTestCase {
	
	Tree test;
	
	@Override
	public String getModuleName() {
		return "org.openelis.ui.UI";
	}
	
	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		
        Tree2 uiWidget = new Tree2();
        uiWidget.panel.setSize("350px", "100px");
        uiWidget.panel.forceLayout();

        test = uiWidget.test;
        test.setHeight("75px");
        ((StaticView)test.view).onAttach();
	}
	
	public void testSimple() {
		
	}
	
	@Ignore
	public void testSetRoot() {
		test.setRoot(null);
		assertEquals(0,test.view.table().getRowCount());
		
		Node root = new Node();
		for(int i = 0; i < 10; i++)
			root.add(new Node(null,null,null).setType("top"));
		test.setRoot(root);
		
		assertEquals(10,test.view.table().getRowCount());
		
		for(int i = 0; i < 10; i++)
			root.add(new Node(null,null,null).setType("top"));
		
		test.setRoot(root);
		
		assertEquals(20,test.view.table().getRowCount());

	
		for(int i = 0; i < 15; i++)
			root.remove(0);
		
		test.setRoot(root);
		
		assertEquals(5,test.view.table().getRowCount());
		
		test.setRoot(null);
		
		assertEquals(0,test.view.table().getRowCount());
			
	}
	
	public void testGetRowCount() {
		assertEquals(0,test.getRowCount());
		
		test.setRoot(getRoot());
		
		assertEquals(5,test.getRowCount());
				
	}
	
	public void testIsMultipleRowsSelected() {
		test.setRoot(getRoot());
		test.setAllowMultipleSelection(true);
		test.selectNodeAt(0);
		test.setSelection(1,null);
		assertEquals(true,test.isMultipleRowsSelected());
	}
	
	public void testAddColumns() {
		test.setRoot(getRoot());
		
		test.addColumn();
		
		assertEquals(4, test.getColumnCount());
		assertEquals(4, test.view.table().getCellCount(0));
		assertNull(test.getValueAt(0, 3));
		
		
		test.addColumnAt(1);
		
		assertEquals(5,test.getColumnCount());
		assertEquals(5,test.view.table().getCellCount(0));
		assertEquals("A",test.getValueAt(0, 2));
		assertEquals(null,test.getValueAt(0,1));
	}
	
	public void testRemoveColumns() {
		test.setRoot(getRoot());
		
		test.removeColumnAt(0);
		
		assertEquals(2, test.getColumnCount());
		assertEquals(2, test.getRoot().getChildAt(0).getCells().size());
		assertEquals(2, test.view.table().getCellCount(0));
		assertEquals("A",test.getValueAt(0, 0));
	}
	
	public void testAddRows() {
		test.setRoot(getRoot());
		
		test.addNode("top");
		
		assertEquals(6,test.view.table().getRowCount());
		assertEquals(6,test.getRowCount());
		
		test.addNodeAt("top",2);
		
		assertEquals(7,test.view.table().getRowCount());
		assertEquals(7,test.getRowCount());
		assertEquals("C",test.getValueAt(3,1));
		
	}
	
	public void testRemoveRows() {
		test.setRoot(getRoot());
		
		test.removeNodeAt(2);
		
		assertEquals(4,test.getRowCount());
		assertEquals(4,test.view.table().getRowCount());
		assertEquals("D",test.getValueAt(2, 1));
		
		test.removeAllNodes();
		assertEquals(0,test.getRowCount());
		assertEquals(0,test.view.table().getRowCount());
	}
	
	public void testSelection() {
		test.setRoot(getRoot());
		
		test.selectNodeAt(0);
		
		assertEquals(0,test.getSelectedNode());
		assertTrue(Arrays.deepEquals(new Integer[]{0},test.getSelectedNodes()));
		assertTrue(test.isAnyNodeSelected());
		assertTrue(test.isNodeSelected(0));
		assertTrue(test.view.table().getRowFormatter().getStyleName(0).contains(((StaticView)test.view).css.Selection()));
		
		test.selectNodeAt(1);
		assertEquals(1,test.getSelectedNode());
		assertTrue(Arrays.deepEquals(new Integer[]{1},test.getSelectedNodes()));
		assertTrue(test.isAnyNodeSelected());
		assertTrue(test.isNodeSelected(1));
		assertTrue(test.view.table().getRowFormatter().getStyleName(1).contains(((StaticView)test.view).css.Selection()));
		assertFalse(test.view.table().getRowFormatter().getStyleName(0).contains(((StaticView)test.view).css.Selection()));
		
		test.setAllowMultipleSelection(true);
		test.setSelection(2,Document.get().createClickEvent(0, 0, 0, 0, 0, true, false, false, false));
		
		assertEquals(1,test.getSelectedNode());
		assertTrue(Arrays.deepEquals(new Integer[]{1,2},test.getSelectedNodes()));
		assertTrue(test.isMultipleRowsSelected());
		assertTrue(test.isAnyNodeSelected());
		assertTrue(test.isNodeSelected(1));
		assertTrue(test.isNodeSelected(2));
		assertTrue(test.view.table().getRowFormatter().getStyleName(1).contains(((StaticView)test.view).css.Selection()));
		assertTrue(test.view.table().getRowFormatter().getStyleName(2).contains(((StaticView)test.view).css.Selection()));
		
		test.setSelection(3,Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false));
		
		assertEquals(3,test.getSelectedNode());
		assertTrue(Arrays.deepEquals(new Integer[]{3},test.getSelectedNodes()));
		assertFalse(test.isMultipleRowsSelected());
		assertTrue(test.isAnyNodeSelected());
		assertTrue(test.isNodeSelected(3));
		assertFalse(test.isNodeSelected(2));
		assertTrue(test.view.table().getRowFormatter().getStyleName(3).contains(((StaticView)test.view).css.Selection()));
		assertFalse(test.view.table().getRowFormatter().getStyleName(1).contains(((StaticView)test.view).css.Selection()));
		assertFalse(test.view.table().getRowFormatter().getStyleName(2).contains(((StaticView)test.view).css.Selection()));
	
		test.unselectAll();
		assertFalse(test.isMultipleRowsSelected());
		assertFalse(test.isAnyNodeSelected());
		assertEquals(-1,test.getSelectedNode());
		assertTrue(Arrays.deepEquals(new Integer[]{},test.getSelectedNodes()));
		assertFalse(test.view.table().getRowFormatter().getStyleName(3).contains(((StaticView)test.view).css.Selection()));
		
		test.selectAll();
		assertTrue(test.isMultipleRowsSelected());
		assertTrue(test.isAnyNodeSelected());
		assertEquals(0,test.getSelectedNode());
		assertTrue(Arrays.deepEquals(new Integer[]{0,1,2,3,4},test.getSelectedNodes()));
		assertTrue(test.view.table().getRowFormatter().getStyleName(0).contains(((StaticView)test.view).css.Selection()));
		assertTrue(test.view.table().getRowFormatter().getStyleName(1).contains(((StaticView)test.view).css.Selection()));
		assertTrue(test.view.table().getRowFormatter().getStyleName(2).contains(((StaticView)test.view).css.Selection()));
		assertTrue(test.view.table().getRowFormatter().getStyleName(3).contains(((StaticView)test.view).css.Selection()));
		assertTrue(test.view.table().getRowFormatter().getStyleName(4).contains(((StaticView)test.view).css.Selection()));
		
		test.unselectNodeAt(2);
		assertTrue(test.isMultipleRowsSelected());
		assertTrue(test.isAnyNodeSelected());
		assertEquals(0,test.getSelectedNode());
		assertTrue(Arrays.deepEquals(new Integer[]{0,1,3,4},test.getSelectedNodes()));
		assertTrue(test.view.table().getRowFormatter().getStyleName(0).contains(((StaticView)test.view).css.Selection()));
		assertTrue(test.view.table().getRowFormatter().getStyleName(1).contains(((StaticView)test.view).css.Selection()));
		assertFalse(test.view.table().getRowFormatter().getStyleName(2).contains(((StaticView)test.view).css.Selection()));
		assertTrue(test.view.table().getRowFormatter().getStyleName(3).contains(((StaticView)test.view).css.Selection()));
		assertTrue(test.view.table().getRowFormatter().getStyleName(4).contains(((StaticView)test.view).css.Selection()));
	}
	
	public void testSetValueAt() {
		test.setRoot(getRoot());
		
		test.setValueAt(0,1,"F");
		
		assertEquals("F",test.getValueAt(0,1));
		assertEquals("F",test.view.table().getText(0,1));
		
    	test.setValueAt(0, 0, "fsdfsdf");
		
    	assertEquals("fsdfsdf",test.getValueAt(0, 0));
    	assertEquals("fsdfsdf",((TreeGrid)test.view.table().getWidget(0, 0)).getText(0, 3));
    	assertTrue(test.hasExceptions(0,0));
    	assertTrue(test.view.table().getCellFormatter().getStyleName(0, 0).contains(((StaticView)test.view).css.InputError()));
    	
    	test.setValueAt(0,0,5);
    	assertEquals(5,test.getValueAt(0,0));
    	assertEquals("5",((TreeGrid)test.view.table().getWidget(0,0)).getText(0, 3));
    	assertFalse(test.hasExceptions(0,0));
    	assertFalse(test.view.table().getCellFormatter().getStyleName(0, 0).contains(((StaticView)test.view).css.InputError()));
    	
	}
	
	public void testSetRowAt() {
		test.setRoot(getRoot());
		
		test.setRowAt(2,new Node(7,"F",null).setType("top"));
		
		assertEquals(5,test.getRowCount());
		assertEquals(7,test.getValueAt(2,0));
		assertEquals("F",test.view.table().getText(2, 1));
	}
	
	public void testStartEditing() {
		
		test.setRoot(getRoot());
		
		assertFalse(test.startEditing(0, 0));
		assertFalse(test.isAnyNodeSelected());
		
		test.setEnabled(true);
		
		assertTrue(test.startEditing(0,0));
		assertEquals(0,test.getSelectedNode());
		assertEquals(test.getNodeDefinition("top").get(0).getCellEditor().getWidget(),((AbsolutePanel)((TreeGrid)test.view.table().getWidget(0, 0)).getWidget(0,3)).getWidget(0));
		
	}
	
	public void testFinishEditing() {
		test.setRoot(getRoot());
		
		test.setEnabled(true);
		
		test.startEditing(0,0);
		assertTrue(test.isEditing());
		((TextBox)test.getNodeDefinition("top").get(0).getCellEditor().getWidget()).setText("10");
		test.finishEditing();
		assertFalse(test.isEditing());
		assertEquals(10,test.getValueAt(0,0));
		
		test.startEditing(0,0);
		assertTrue(test.isEditing());
		
		((TextBox)test.getNodeDefinition("top").get(0).getCellEditor().getWidget()).setText("ASDA");
		test.finishEditing();
		assertFalse(test.isEditing());
		assertEquals("ASDA",test.getValueAt(0, 0));
		assertTrue(test.hasExceptions(0, 0));
		assertTrue(test.view.table().getCellFormatter().getStyleName(0,0).contains(((StaticView)test.view).css.InputError()));

	}
	
	public void testScrollToVisible() {
		test.setRoot(getRoot());
		
		for(int i = 0; i < 15; i++)
			test.addNodeAt("top",0);
		
		assertEquals(20,test.getRowCount());
		assertEquals(20,((StaticView)test.view).flexTable.getRowCount());
		
		assertTrue(test.scrollToVisible(15));
		
		//assertTrue(test.view.scrollView().getVerticalScrollPosition() > 0);

	    //assertTrue(((StaticView)test.view).isRowVisible(15));

	}
	
	public void testScrollBy() {
	    /*
		test.setRoot(getRoot());
		
		for(int i = 0; i < 15; i++)
			test.addNodeAt("top",0);
		
		test.scrollBy(6);
		
		// Calling renderview expliculty for test only
		test.renderView(-1, -1);
		

	    assertEquals("A",test.view.table().getText(9, 1));
	    */
	}
	
	public void testResize() {
	    /*
		test.getColumnAt(2).setWidth(200);
		assertEquals(400,test.view.table().getOffsetWidth());
		*/
	}
	
	public void testRefreshCell() {
		test.setRoot(getRoot());
		
		test.getRoot().getChildAt(0).setCell(0,10);
		test.refreshCell(0, 0);
		assertEquals("10",((TreeGrid)test.view.table().getWidget(0,0)).getText(0,3));
	}
	
	public void testQueryMode() {
		test.setRoot(getRoot());
		test.setEnabled(true);
		test.addNodeDefinition("query",test.getNodeDefinition("top"));
		test.setQueryMode(true);
		
		
		assertEquals(1,test.getRowCount());
		assertEquals(1,test.view.table().getRowCount());
		assertNull(test.getValueAt(0, 0));
		
		test.setQueryMode(false);
		
		assertEquals(0,test.getRowCount());
		assertEquals(0,test.view.table().getRowCount());
		assertNull(test.getRoot());
	}
	
	public void testGetQuery() {
		test.setRoot(getRoot());
		test.setEnabled(true);
		test.addNodeDefinition("query",test.getNodeDefinition("top"));
		test.setQueryMode(true);
		
		test.startEditing(0, 0);
		((TextBox)test.getNodeDefinition("top").get(0).getCellEditor().getWidget()).setText("> 1");
		test.finishEditing();
		
		QueryData[] qds = (QueryData[])test.getQuery();
		
		assertNotNull(qds);
		assertEquals(1,qds.length);
		assertEquals(QueryData.Type.INTEGER, qds[0].getType());
		assertEquals("> 1",qds[0].getQuery());
		
	}
	
	public void testAddException() {
		test.setRoot(getRoot());
		test.setEnabled(true);
		test.addException(0, 0, new Exception("test"));
		
		assertTrue(test.hasExceptions());
		assertTrue(test.hasExceptions(0, 0));
		assertTrue(test.view.table().getCellFormatter().getStyleName(0, 0).contains(((StaticView)test.view).css.InputError()));
	}
	
	public void testClearException() {
		testAddException();
		
		test.clearEndUserExceptions(0, 0);
		
		assertFalse(test.hasExceptions());
		assertFalse(test.hasExceptions(0,0));
		assertFalse(test.view.table().getCellFormatter().getStyleName(0,0).contains(((StaticView)test.view).css.InputError()));
	}
	
	public void testValidate() {
		test.setRoot(getRoot());
		test.getColumnAt(2).setRequired(true);
		assertTrue(test.hasExceptions());
		assertTrue(test.view.table().getCellFormatter().getStyleName(0,2).contains(((StaticView)test.view).css.InputError()));
	}
	
	public void testShowRoot() {
		test.setShowRoot(true);
		test.setRoot(getRoot());
		test.setEnabled(true);
		
		assertEquals(1,test.getRowCount());
		
		test.expand(0);
		
		assertEquals(6,test.getRowCount());
	}
	
	public Node getRoot() {
		Node root = new Node(3).setType("top");
		
		root.add(new Node(1,"A",null).setType("top"));
		root.add(new Node(2,"B",null).setType("top"));
		root.add(new Node(3,"C",null).setType("top"));
		root.add(new Node(4,"D",null).setType("top"));
		root.add(new Node(5,"E",null).setType("top"));
		
		return root;
		
	}
	

	

}
