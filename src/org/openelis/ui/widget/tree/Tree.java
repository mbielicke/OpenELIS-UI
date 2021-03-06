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
package org.openelis.ui.widget.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.openelis.ui.common.Util;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.TreeCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Balloon;
import org.openelis.ui.widget.HasExceptions;
import org.openelis.ui.widget.HasBalloon;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.ScreenWidgetInt;
import org.openelis.ui.widget.Balloon.Options;
import org.openelis.ui.widget.Balloon.Placement;
import org.openelis.ui.widget.table.CellEditor;
import org.openelis.ui.widget.table.CellRenderer;
import org.openelis.ui.widget.table.CellTipProvider;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;
import org.openelis.ui.widget.table.event.CellClickedEvent;
import org.openelis.ui.widget.table.event.CellClickedHandler;
import org.openelis.ui.widget.table.event.CellEditedEvent;
import org.openelis.ui.widget.table.event.CellEditedHandler;
import org.openelis.ui.widget.table.event.CellMouseOutEvent;
import org.openelis.ui.widget.table.event.CellMouseOverEvent;
import org.openelis.ui.widget.table.event.HasBeforeCellEditedHandlers;
import org.openelis.ui.widget.table.event.HasCellClickedHandlers;
import org.openelis.ui.widget.table.event.HasCellEditedHandlers;
import org.openelis.ui.widget.table.event.HasUnselectionHandlers;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;
import org.openelis.ui.widget.tree.event.BeforeNodeAddedEvent;
import org.openelis.ui.widget.tree.event.BeforeNodeAddedHandler;
import org.openelis.ui.widget.tree.event.BeforeNodeCloseEvent;
import org.openelis.ui.widget.tree.event.BeforeNodeCloseHandler;
import org.openelis.ui.widget.tree.event.BeforeNodeDeletedEvent;
import org.openelis.ui.widget.tree.event.BeforeNodeDeletedHandler;
import org.openelis.ui.widget.tree.event.BeforeNodeOpenEvent;
import org.openelis.ui.widget.tree.event.BeforeNodeOpenHandler;
import org.openelis.ui.widget.tree.event.HasBeforeNodeAddedHandlers;
import org.openelis.ui.widget.tree.event.HasBeforeNodeCloseHandlers;
import org.openelis.ui.widget.tree.event.HasBeforeNodeDeletedHandlers;
import org.openelis.ui.widget.tree.event.HasBeforeNodeOpenHandlers;
import org.openelis.ui.widget.tree.event.HasNodeAddedHandlers;
import org.openelis.ui.widget.tree.event.HasNodeClosedHandlers;
import org.openelis.ui.widget.tree.event.HasNodeDeletedHandlers;
import org.openelis.ui.widget.tree.event.HasNodeOpenedHandlers;
import org.openelis.ui.widget.tree.event.NodeAddedEvent;
import org.openelis.ui.widget.tree.event.NodeAddedHandler;
import org.openelis.ui.widget.tree.event.NodeClosedEvent;
import org.openelis.ui.widget.tree.event.NodeClosedHandler;
import org.openelis.ui.widget.tree.event.NodeDeletedEvent;
import org.openelis.ui.widget.tree.event.NodeDeletedHandler;
import org.openelis.ui.widget.tree.event.NodeOpenedEvent;
import org.openelis.ui.widget.tree.event.NodeOpenedHandler;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This class is used by screens to display information in a Tree.
 * 
 * @author tschmidt
 * 
 */
public class Tree extends FocusPanel implements ScreenWidgetInt, Queryable,
                                    HasBeforeSelectionHandlers<Integer>,
                                    HasSelectionHandlers<Integer>, HasUnselectionHandlers<Integer>,
                                    HasBeforeCellEditedHandlers, HasCellEditedHandlers, HasCellClickedHandlers,
                                    HasBeforeNodeAddedHandlers, HasNodeAddedHandlers,
                                    HasBeforeNodeDeletedHandlers, HasNodeDeletedHandlers,
                                    HasBeforeNodeOpenHandlers, HasNodeClosedHandlers,
                                    HasBeforeNodeCloseHandlers, HasNodeOpenedHandlers, HasBalloon,
                                    HasValue<Node>, HasExceptions, FocusHandler, RequiresResize {

    /**
     * Cell that is currently being edited.
     */
    protected int                    editingRow = -1, 
                                     editingCol = -1;

    /**
     * Table dimensions
     */
    protected int                    rowHeight, 
                                     visibleNodes = 10, 
                                     viewWidth = -1,
                                     totalColumnWidth;

    /**
     * Root for the Tree and currently displayed rows
     */
    protected Node                     root;
    protected ArrayList<Node>          modelView;
    protected HashMap<Node, NodeIndex> nodeIndex;

    /**
     * Columns used by the Tree
     */
    protected ArrayList<Column>      columns;
    protected HashMap<String, ArrayList<LeafColumn>> nodeDefs;

    /**
     * List of selected Rows by index in the displayed Tree
     */
    protected ArrayList<Integer>     selections = new ArrayList<Integer>();

    /**
     * Exception lists for the Tree
     */
    protected HashMap<Node, HashMap<Integer, ArrayList<Exception>>> endUserExceptions,
                                                                    validateExceptions;
    
    protected Timer                       balloonTimer;


    /**
     * Tree state values
     */
    protected boolean                enabled, 
                                     multiSelect,
                                     editing, 
                                     hasFocus, 
                                     queryMode, 
                                     hasHeader, 
                                     fixScrollBar = true, 
                                     showRoot;

    /**
     * Enum representing the state of when the scroll bar should be shown.
     */
    public enum Scrolling {
        ALWAYS, AS_NEEDED, NEVER
    };

    /**
     * Fields to hold state of whether the scroll bars are shown
     */
    protected Scrolling          verticalScroll, 
                                 horizontalScroll;

    /**
     * Reference to the View composite for this widget.
     */
    protected ViewInt            view;


    /**
     * Arrays for determining relative X positions for columns
     */
    protected short[]            xForColumn, columnForX;

    /**
     * Drag and Drop controllers
     */
    protected TreeDragController dragController;
    protected TreeDropController dropController;
    
    protected TreeCSS            css;
    
    protected HandlerRegistration visibleHandler;
    
    protected CellTipProvider    tipProvider;
    
    protected Options     toolTip;
    
    protected int tipRow, tipCol;
    
    protected Tree source = this;

    private Tree() {
        css = UIResources.INSTANCE.tree();
        css.ensureInjected();
        
    	columns = new ArrayList<Column>(5);
    	nodeDefs = new HashMap<String, ArrayList<LeafColumn>>();
    	rowHeight = 19;
        view = new StaticView(this);
        setWidget(view);

        /*
         * This Handler takes care of all key events on the tree when editing
         * and when only selection is on
         */
        addDomHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                int row, col, keyCode;

                if ( !isEnabled())
                    return;
                
                keyCode = event.getNativeEvent().getKeyCode();
                row = editingRow;
                col = editingCol;

                if(isEditing() && getCellEditor(row,col).ignoreKey(keyCode))
                    return;
                    
                switch (keyCode) {
                    case (KeyCodes.KEY_TAB):
                     // Ignore if no cell is currently being edited
                        if ( !editing)
                            break;

                        // Tab backwards if shift pressed otherwise tab forward
                        if ( !event.isShiftKeyDown()) {
                            while (true) {
                                col++ ;
                                if (col >= getColumnCount()) {
                                    col = 0;
                                    row++ ;
                                    if (row >= getRowCount()) {
                                    	setFocus(true);
                                    	finishEditing();
                                        break;
                                    }

                                }
                                if (startEditing(row, col, event.getNativeEvent())) {
                                	event.preventDefault();
                                    event.stopPropagation();
                                    break;
                                }
                            }
                        } else {
                            while (true) {
                                col-- ;
                                if (col < 0) {
                                    col = getColumnCount() - 1;
                                    row-- ;
                                    if (row < 0) {
                                    	setFocus(true);
                                    	finishEditing();
                                        break;
                                    }
                                }
                                if (startEditing(row, col, event.getNativeEvent())) {
                                	event.preventDefault();
                                	event.stopPropagation();
                                    break;
                                }
                            }
                        }

                        break;
                    case (KeyCodes.KEY_DOWN):
                        // If Not editing select the next row below the current
                        // selection
                        if ( !isEditing()) {
                            if (isAnyNodeSelected()) {
                                row = getSelectedNode();
                                while (true) {
                                    row++ ;
                                    if (row >= getRowCount())
                                        break;
                                    
                                    selectNodeAt(row,event.getNativeEvent());
                                    
                                    if(isNodeSelected(row))
                                    	break;
                                }
                            }
                            break;
                        }
                        // If editing set focus to the same col cell in the next
                        // selectable row below
                        while (true) {
                            row++ ;
                            if (row >= getRowCount())
                                break;
                            if (startEditing(row, col, event.getNativeEvent())) {
                                event.stopPropagation();
                                event.preventDefault();
                                break;
                            }
                        }
                        break;
                    case (KeyCodes.KEY_UP):
                        // If Not editing select the next row above the current
                        // selection
                        if ( !isEditing()) {
                            if (isAnyNodeSelected()) {
                                row = getSelectedNode();
                                while (true) {
                                    row-- ;
                                    if (row < 0)
                                        break;
                                    
                                    selectNodeAt(row,event.getNativeEvent());
                                    
                                    if(isNodeSelected(row))
                                        break;
                                }
                            }
                            break;
                        }
                        // If editing set focus to the same col cell in the next
                        // selectable row above
                        while (true) {
                            row-- ;
                            if (row < 0)
                                break;
                            if (startEditing(row, col, event.getNativeEvent())) {
                                event.stopPropagation();
                                event.preventDefault();
                                break;
                            }
                        }
                        break;
                    case (KeyCodes.KEY_ENTER):
                        // If editing just finish and return
                        if (isEditing()) {
                            finishEditing();
                            return;
                        }
                        // If not editing and a row is selected, focus on first
                        // editable cell
                        if (!isAnyNodeSelected()) 
                        	row = 0;
                        else
                            row = getSelectedNode();
                        
                        col = 0;
                        while (col < getColumnCount()) {
                            if (startEditing(row, col, event.getNativeEvent()))
                                break;
                            col++ ;
                        }
                        break;
                }
            }
        }, KeyDownEvent.getType());

    }

    // ********* Tree Definition Methods *************
    /**
     * Returns the currently used Row Height for the tree layout
     */
    public int getRowHeight() {
        return rowHeight;
    }

    /**
     * Sets the Row Height to be used in the tree layout.
     * 
     * @param rowHeight
     */
    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
        layout();
    }

    /**
     * Returns how many physical rows are used in the tree layout.
     * 
     * @return
     */
    public int getVisibleRows() {
        return visibleNodes;
    }

    /**
     * Sets how many physical rows are used in the tree layout.
     * 
     * @param visibleNodes
     */
    public void setVisibleRows(int visibleNodes) {
        this.visibleNodes = visibleNodes;
        layout();
    }

    /**
     * Returns the Root node for this tree.
     * 
     * @return
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Sets the root node for this tree. 
     * 
     * @param model
     */
    public void setRoot(Node root) {
        finishEditing();
        
        unselectAll();
        
        this.root = root;
        
        getDisplayedRows();
        
        checkExceptions();

        ((StaticView)view).bulkRender();
        
        if(hasExceptions()) 
            ((StaticView)view).bulkExceptions(endUserExceptions);
    }

    /**
     * This method is called when the root is changed to create the displayed
     * Node list for the Tree to render.
     */
    private void getDisplayedRows() {
        ArrayList<Node> children;

        modelView = new ArrayList<Node>();
        nodeIndex = new HashMap<Node,NodeIndex>();
        
        if(root == null)
        	return;
        
        if (showRoot) {
            modelView.add(root);
            nodeIndex.put(root, new NodeIndex(0));
        } else
            root.setOpen(true);

        children = getDisplayedChildren(root);
        for (Node child : children) {
            modelView.add(child);
            nodeIndex.put(child, new NodeIndex(modelView.size() - 1));
        }

    }
    

    public void checkExceptions() {
        if(endUserExceptions != null) {
            for(Node node : endUserExceptions.keySet()) {
                if(modelView == null || !modelView.contains(node))
                    endUserExceptions.remove(node);
            }
            
            if(endUserExceptions.size() == 0) 
                endUserExceptions = null;
        }
        
        if(validateExceptions != null) {
            for(Node node : validateExceptions.keySet()) {
                if(modelView == null || !modelView.contains(node))
                    validateExceptions.remove(node);
            }
            
            if(validateExceptions.size() == 0) 
                validateExceptions = null;
        }
        
    }
    
    public int getNodeViewIndex(Node node) {
        return modelView.indexOf(node);
    }

    /**
     * This is a recursive method that will return an ArrayList of descendant nodes
     * from the passed node that can be displayed in the Tree View.
     * 
     * @param node
     * @return
     */
    private ArrayList<Node> getDisplayedChildren(Node node) {
        ArrayList<Node> children = new ArrayList<Node>();

        if (node.isOpen && node.getChildCount() > 0) {
            for (Node child : node.children) {
                children.add(child);
                if ( !child.isLeaf())
                    children.addAll(getDisplayedChildren(child));
            }
        }

        return children;
    }
    
    private ArrayList<Node> getAllNodes() {
        ArrayList<Node> children;
        ArrayList<Node> nodes = new ArrayList<Node>();
        
        if(root == null)
        	return nodes;
        
        if (showRoot) 
            nodes.add(root);

        children = getChildren(root);
        for (Node child : children) 
            nodes.add(child);
        
        return nodes;
    }
    
    private ArrayList<Node> getChildren(Node node) {
        ArrayList<Node> children = new ArrayList<Node>();

        if (node.getChildCount() > 0) {
            for (Node child : node.children) {
                children.add(child);
                if ( !child.isLeaf())
                    children.addAll(getDisplayedChildren(child));
            }
        }
        return children;
    }
    
    private void adjustNodeIndexes(int row, int adj) {     
        for(int i = row; i < modelView.size(); i++) {
            nodeIndex.get(modelView.get(i)).index += adj;
        }
            
    }

    /**
     * Method used to determine if the passed node is currently in the display;
     * 
     * @param node
     * @return
     */
    public boolean isDisplayed(Node node) {
        return nodeIndex.containsKey(node) || node == root;
    }

    /**
     * Method used to toggle a node to an open or close state.  If the node is
     * not currently displayed the method will do nothing.
     * @param node
     */
    public void toggle(Node node) {
        if(isDisplayed(node))
            toggle(nodeIndex.get(node).index, null);
    }
    
    /**
     * This method will toggle a node to be open or closed depending on its
     * current state. The index passed is the current displayed index of the the
     * node.
     * 
     * @param row
     */
    public void toggle(int row) {
        toggle(row,null);
    }
    
    protected void toggle(int row, NativeEvent event) {
        if(getNodeAt(row).isOpen())
            close(row,event);
        else
            open(row,event);
    }
    
    /**
     * This method will open the passed node.  If the node is not currently displayed
     * or the node is already open, the method will do nothing.
     * @param node
     */
    public void open(Node node) {
        if(isDisplayed(node))
            open(nodeIndex.get(node).index,null);
    }
    
    /**
     * This method will open the node at the displayed index of the tree passed in.
     * After this method is called the display of the tree will change to show all displayable
     * descendant nodes under this node.  If the row is a leaf node or the node is already open 
     * the method will do nothing.  
     * @param row
     */
    public void open(int row) {
        open(row,null);
    }
    
    protected void open(int row, NativeEvent event) {
        finishEditing();
        
        if(event != null)
        	selectNodeAt(row,event);
        
        Node node;
        int pos;

        node = getNodeAt(row);

        if (node.isLeaf() || node.isOpen)
            return;
        
        if(!fireBeforeNodeOpenEvent(row))
            return;
        
        node.setOpen(true);
        
        pos = row + 1;
        
        ArrayList<Node> children = getDisplayedChildren(node);
        for (int i = 0; i < children.size(); i++ ) {
            modelView.add(pos + i, children.get(i));
            nodeIndex.put(children.get(i), new NodeIndex(pos + i));
        }
        
        if(children.size() > 0) {
            adjustNodeIndexes(pos + children.size(),children.size());
            
            //Adjust selction indexes for multiselect so children are not mistaken for selected nodes
            for(int i = 0; i < selections.size(); i++) {
                if(selections.get(i) > row)
                    selections.set(i, selections.get(i)+children.size());
            }
        }
        
        fireNodeOpenEvent(row);
        
        view.addNodes(pos, pos + children.size() -1);
        
        view.renderView(row,row);
    }
    
    /**
     * This method will close the passed node.  If the node is not currently displayed
     * or the node is already closed, the method will do nothing.
     * @param node
     */
    public void close(Node node) {
        if(isDisplayed(node))
            close(nodeIndex.get(node).index);
    }
    
    /**
     * This method will close the node at the displayed index of the tree passed in.
     * After this method is called the display of the tree will change to remove all displayed
     * descendant nodes under this node.  If the row is a leaf node or the node is already closed 
     * the method will do nothing.  
     * @param row
     */
    public void close(int row) {
        close(row,null);
    }
    
    protected void close(int row, NativeEvent event) {
        int adj = 0,children;
        
        finishEditing();
        
        if(event != null)
        	selectNodeAt(row,event);
        
        Node node;
        int pos;

        node = getNodeAt(row);

        if (node.isLeaf() || !node.isOpen)
            return;
        
        if(!fireBeforeNodeCloseEvent(row))
            return;
        
        node.setOpen(false);
        
        pos = row + 1;
        children = 0;
        while (pos < modelView.size() && node.isNodeDescendent(getNodeAt(pos))) {
            nodeIndex.remove(modelView.remove(pos));
            adj--;
            children++;
        }
       
        adjustNodeIndexes(pos,adj);
        
        fireNodeCloseEvent(row);
        
        view.removeNodes(row+1,children);
        
        view.renderView(row,row);
        
        
    }
    
    /**
     * This method will expand the tree to the level specified by the param starting at the root.
     * @param level
     */
    public void expand(int level) {
        finishEditing();
     
        expand(root,level);
     
        getDisplayedRows();

        ((StaticView)view).bulkRender();
        
        if(hasExceptions()) 
            ((StaticView)view).bulkExceptions(endUserExceptions);
        
        if(isAnyNodeSelected()) {
            for(Integer index : selections) 
                ((StaticView)view).applySelectionStyle(index);
        }
    }
    
    /**
     * Private method used to recurse through the tree setting the specified expand level
     * @param node
     * @param level
     */
    private void expand(Node node, int level) {
    	if (node.isLeaf())
    		return;
    	
    	if(node.getLevel() > level) { 
    		node.setOpen(false);
    		return;
    	}
            
    	node.setOpen(true);
    	
    	if(!node.hasChildren())
    		return;
    	
    	for(Node child : node.children) 
        	expand(child,level);
    	
    }
    
    /**
     * This method will close all nodes in the tree down to the root or if the root is not shown
     * then the root children.
     */
    public void collapse() {
    	expand(0);
    }

    /**
     * This method is used to determine if the root node is showed in the
     * display
     * 
     * @return
     */
    public boolean showRoot() {
        return showRoot;
    }

    /**
     * This method will set a flag that is used to determine if the root node
     * should be shown in the Tree display.
     * 
     * @param showRoot
     */
    public void setShowRoot(boolean showRoot) {
        this.showRoot = showRoot;
    }

    /**
     * Returns the current size of the held model. Returns zero if a model has
     * not been set.
     * 
     * @return
     */
    public int getRowCount() {
        if (modelView == null)
            return 0;

        return modelView.size();
    }

    /**
     * Used to determine the tree has more than one row currently selected.
     * 
     * @return
     */
    public boolean isMultipleRowsSelected() {
        return selections.size() > 1;
    }

    /**
     * Used to determine if the tree currently allows multiple selection.
     * 
     * @return
     */
    public boolean isMultipleSelectionAllowed() {
        return multiSelect;
    }

    /**
     * Used to put the tree into Multiple Selection mode.
     * 
     * @param multiSelect
     */
    public void setAllowMultipleSelection(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    /**
     * Returns the current Vertical Scrollbar view rule set.
     * 
     * @return
     */
    public Scrolling getVerticalScroll() {
        return verticalScroll;
    }

    /**
     * Sets the current Vertical Scrollbar view rule.
     * 
     * @param verticalScroll
     */
    public void setVerticalScroll(Scrolling verticalScroll) {
        this.verticalScroll = verticalScroll;
        layout();
    }

    /**
     * Returns the current Horizontal Scrollbar view rule set
     * 
     * @return
     */
    public Scrolling getHorizontalScroll() {
        return horizontalScroll;
    }

    /**
     * Sets the current Horizontal Scrollbar view rule.
     * 
     * @param horizontalScroll
     */
    public void setHorizontalScroll(Scrolling horizontalScroll) {
        this.horizontalScroll = horizontalScroll;
        layout();
    }

    /**
     * Sets a flag to set the size of the tree to always set room aside for
     * scrollbars defaults to true
     * 
     * @param fixScrollBar
     */
    public void setFixScrollbar(boolean fixScrollBar) {
        this.fixScrollBar = fixScrollBar;
    }

    /**
     * Returns the flag indicating if the tree reserves space for the scrollbar
     * 
     * @return
     */
    public boolean getFixScrollbar() {
        return fixScrollBar;
    }

    /**
     * Sets the width of the tree view
     * 
     * @param width
     */
    public void setWidth(int width) {
        this.viewWidth = width;
        layout();

    }

    /**
     * Method overridden from Composite to call setWidth(int) so that the width
     * can be adjusted.
     */
    @Override
    public void setWidth(String width) {
        setWidth(Util.stripUnits(width));
    }

    /**
     * Returns the currently set view width for the Tree
     * 
     * @return
     */
    public int getWidth() {
        return viewWidth;
    }

    /**
     * Returns the view width of the tree minus the the width of the scrollbar
     * if the scrollbar is visible or if space has been reserved for it
     * 
     * @return
     */
    protected int getWidthWithoutScrollbar() {
        //if (verticalScroll != Scrolling.NEVER && fixScrollBar  && viewWidth > -1)
            //return viewWidth - 18;

        return viewWidth == -1 ? totalColumnWidth : viewWidth;
    }

    /**
     * Returns the width of the all the column widths added together which is
     * the physical width of the tree
     * 
     * @return
     */
    protected int getTotalColumnWidth() {
        return totalColumnWidth;
    }

    
    /**
     * Adds a Node definition type to the tree.
     * @param key
     * @param def
     */
    public void addNodeDefinition(String key, ArrayList<LeafColumn> def) {  
    	for(LeafColumn col : def) 
    		col.setTree(this);
    	
        if(nodeDefs == null)
            nodeDefs = new HashMap<String,ArrayList<LeafColumn>>();
        nodeDefs.put(key, def);
    }
    
    /**
     * Returns the Node definition for the given type
     * @param key
     * @return
     */
    public ArrayList<LeafColumn> getNodeDefinition(String type) {
        return nodeDefs.get(type);
    }
    
    /**
     * Returns the Column in the Node definition for the passed type and index 
     * @param key
     * @param index
     * @return
     */
    public LeafColumn getNodeDefinitionAt(String type, int index) {
        return nodeDefs.get(type).get(index);
    }

    /**
     * Returns the number of columns used in this Table
     * 
     * @return
     */
    public int getColumnCount() {
        if (columns != null)
            return columns.size();
        return 0;
    }

    /**
     * Returns the column at the passed index
     * 
     * @param index
     * @return
     */
    public Column getColumnAt(int index) {
        return columns.get(index);
    }

    /**
     * Returns column by the name passed
     * 
     * @param index
     * @return
     */
    public int getColumnByName(String name) {
        for (int i = 0; i < columns.size(); i++ ) {
            if (columns.get(i).getName().equals(name))
                return i;
        }
        return -1;
    }

    /**
     * Returns the index of the passed Column
     * @param col
     * @return
     */
    public int getColumn(Column col) {
        return columns.indexOf(col);
    }

    /**
     * Returns the X coordinate on the Screen of the Column passed.
     * 
     * @param index
     * @return
     */
    protected int getXForColumn(int index) {
        if (xForColumn != null && index >= 0 && index < xForColumn.length)
            return xForColumn[index];
        return -1;
    }

    /**
     * Returns the Column for the current mouse x position passed in the header
     * 
     * @param x
     * @return
     */
    protected int getColumnForX(int x) {
        if (columnForX != null && x >= 0 && x < columnForX.length)
            return columnForX[x];
        return -1;
    }

    /**
     * Sets whether the tree as a header or not.
     */
    public void setHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    /**
     * Used to determine if tree has header
     * 
     * @return
     */
    public boolean hasHeader() {
        return hasHeader;
    }

    /**
     * Sets the list columns to be used by this Tree
     * 
     * @param columns
     */
    public void setColumns(ArrayList<Column> columns) {
        this.columns = columns;

        for (Column column : columns)
            column.setTree(this);

        layout();
    }

    /**
     * Creates and Adds a Column at the end of the column list with passed name
     * and header label in the params.
     * 
     * @param name
     *        Name of the column for reference
     * @param label
     *        Label used in Tree header.
     * @return The newly created and added column
     */
    public Column addColumn(String name, String label) {
        return addColumnAt(columns.size(), name, label);
    }

    /**
     * Creates and adds a new column to the tree
     * 
     * @return
     */
    public Column addColumn() {
        return addColumn("", "");
    }

    /**
     * Creates and inserts a new Column in the tree at the specified index
     * using the name and label passed.
     * 
     * @param index
     *        Index in the Column list where to insert the new Column
     * @param name
     *        Name used in the Column as a reference to the Column.
     * @param label
     *        Label used in the Table header.
     * @return The newly created and added Column.
     */
    public Column addColumnAt(int index, String name, String label) {
        Column column;

        column = new Column.Builder(75).name(name).label(label).build();
        column.setTree(this);
        columns.add(index, column);
        for(Node node : getAllNodes()) {
        	if(node.getCells().size() >= index)
        		node.getCells().add(index,null);
        }
        view.addColumn(index);

        return column;
    }
    
    public void addColumn(Column column) {
        columns.add(columns.size(),column);
        for(Node node : getAllNodes()) {
            if(node.getCells().size() >= columns.size())
                node.getCells().add(columns.size(),null);
        }
        layout();
    }

    /**
     * Creates and adds a new Column at passed index
     * 
     * @param index
     *        Index in the Column list where to insert the new Column.
     * @return The newly created and added column.
     */
    public Column addColumnAt(int index) {
        return addColumnAt(index, "", "");
    }

    /**
     * Removes the column in the tree and passed index.
     * 
     * @param index
     */
    public Column removeColumnAt(int index) {
        Column col;

        col = columns.remove(index);
        for(Node node : getAllNodes()) {
        	if(index < node.getCells().size())
        		node.getCells().remove(index);
        }
        view.removeColumn(index);


        return col;
    }

    /**
     * Removes all columns from the tree.
     */
    public void removeAllColumns() {
        columns.clear();
        layout();
    }

    /**
     * Creates a new blank Row and adds it to the bottom of the Tree model.
     * 
     * @return
     */
    public Node addNode(String type) {
        if(root == null)
            setRoot(new Node());
        
        return addNodeAt(type, root.getChildCount());
    }

    /**
     * Creates a new blank Row and inserts it in the tree model at the passed
     * index.
     * 
     * @param index
     * @return
     */
    public Node addNodeAt(String type,int index) {
    	Node node;
    	
    	node = new Node(getNodeDefinition(type).size()).setType(type);
    	
    	if(root == null)
    	    setRoot(new Node());
    
        return addNode(index, root, node);
    }

    /**
     * Adds the passed Row to the end of the Tree model.
     * 
     * @param row
     * @return
     */
    public Node addNode(Node node) {
        if(root == null)
            setRoot(new Node());
        
        return addNode(root.getChildCount(), root, node);
    }

    /**
     * Adds the passed Row into the Tree model at the passed index.
     * 
     * @param index
     * @param row
     * @return
     */
    public Node addNodeAt(int index, Node node) {
        return addNode(index, root, node);
    }

    public void addNodeAfter(Node selected, Node node) {
        Node parent = selected.getParent();
        
        if(selected.isLastChild())
            addNodeAt(parent,node);
        else
            addNodeAt(parent,node,selected.getChildIndex()+1);
    }
    
    public void addNodeBefore(Node selected, Node node) {
        Node parent = selected.getParent();
        
        if(selected.isFirstChild())
            addNodeAt(parent,node,0);
        else 
            addNodeAt(parent,node,selected.getChildIndex()-1);
    }
    
    /**
     * Private method called by all public addRow methods to handle event firing
     * and add the new row to the model.
     * 
     * @param index
     *        Index where the new row is to be added.
     * @param row
     *        Will be null if a Tree should create a new blank Row to add
     *        otherwise the passed Row will be added.
     * @return Will return null if this action is canceled by a
     *         BeforeRowAddedHandler, otherwise the newly created Row will be
     *         returned or if a Row is passed to the method it will echoed back.
     */
    private Node addNode(int index, Node parent, Node node) {
    	assert(node != null);
        
    	ArrayList<Node> children;
        int pos;

        finishEditing();

        if ( !fireBeforeNodeAddedEvent(index, parent, node))
            return null;
        
        unselectAll();
        
        if(parent.isOpen) {
            if(parent == root) {
                if(index == root.getChildCount())
                    pos = getRowCount();
                else
                    pos = nodeIndex.get(root.getChildAt(index)).index;
                
            }else if(parent.getChildCount() == 0)
            	pos = nodeIndex.get(parent).index +1;
            else if(index >= parent.getChildCount())
                pos = nodeIndex.get(parent.getLastChild()).index + 1;
            else
                pos = nodeIndex.get(parent.getChildAt(index)).index;
            
            parent.add(node,index);
            
            modelView.add(pos, node);
            nodeIndex.put(node,new NodeIndex(pos));

            pos++;
            children = getDisplayedChildren(node);
            for (int i = 0; i < children.size(); i++ ) {
                modelView.add(pos + i, children.get(i));
                nodeIndex.put(children.get(i), new NodeIndex(pos + i));
            }

            adjustNodeIndexes(pos+children.size(), children.size()+1);

            view.addNodes(pos-1,pos-1+children.size());
        }else
            parent.add(node,index);
        
        fireRowAddedEvent(index, parent, node);
       
        return node;
    }
    
    /**
     * Method will add the child node to the passed parent node at the end 
     * of the parents child list.
     * 
     * @param parent
     * @param child
     */
    public void addNodeAt(Node parent, Node child) {
        addNodeAt(parent,child,parent.getChildCount());
    }
    
    /**
     * Method will add the child node to the passed parent node at the position 
     * specified by the index parameter
     * @param parent
     * @param child
     * @param index
     */
    public void addNodeAt(Node parent, Node child, int index) {
        if(!isDisplayed(parent) || !parent.isOpen()) { 
            parent.add(child, index);
            view.renderView(nodeIndex.get(parent).index,nodeIndex.get(parent).index);
            fireRowAddedEvent(index, parent, child);
        }else    
            addNode(index, parent, child);
    }

    /**
     * Method will delete a row from the model at the specified index and
     * refersh the view.
     * 
     * @param index
     * @return
     */
    public Node removeNodeAt(int index) {
        int adj = 0;
        Node node,parent;
        boolean lastChild;

        finishEditing();
        
        unselectAll();

        node = getNodeAt(index);

        if ( !fireBeforeRowDeletedEvent(index, node))
            return null;

        while (index < modelView.size() && node.isNodeDescendent(modelView.get(index))) {
            nodeIndex.remove(modelView.remove(index));
            adj--;
        }

        adjustNodeIndexes(index,adj);
        
        lastChild = node.isLastChild();
        parent = node.getParent();
        parent.remove(node);

        view.removeNodes(index, 1 + (node.isOpen ? node.getChildCount() : 0));
        
        if(endUserExceptions != null) {
            endUserExceptions.remove(node);
            if(endUserExceptions.size() == 0)
                endUserExceptions = null;
        }
        
        if(validateExceptions != null) {
            validateExceptions.remove(node);
            if(validateExceptions.size() == 0)
                validateExceptions = null;
        }

        
        fireRowDeletedEvent(index, node);
         
        if (parent != root && parent.isOpen && lastChild) {
        	close(parent);
        	open(parent);
        } else {
        	if (parent != root) {
        		view.renderView(nodeIndex.get(parent).index,nodeIndex.get(parent).index);
        	}
        }

        return node;
    }
    
    /**
     * Method will remove the passed node form the tree model and refresh the 
     * view.
     * 
     * @param node
     */
    public void removeNode(Node node) {
        if (!isDisplayed(node)) {
            if (fireBeforeRowDeletedEvent(-1, node)) {
            	node.removeFromParent();
            }
            fireRowDeletedEvent(-1, node);
        } else {
            removeNodeAt(nodeIndex.get(node).index);
        }
    }

    /**
     * Set the model for this tree to null and redraws a blank view
     */
    public void removeAllNodes() {
        finishEditing();
        unselectAll();
        root = null;
        modelView = null;
        view.removeAllNodes();
    }

    /**
     * Returns the Row at the specified index in the display model
     * 
     * @param row
     * @return
     */
    public Node getNodeAt(int index) {
    	if(index < 0 || index >= getRowCount())
    		return null;
        return modelView.get(index);
    }

    // ************ Selection Methods ***************

    /**
     * Returns an array of indexes of the currently selected rows
     */
    public Integer[] getSelectedNodes() {
        return selections.toArray(new Integer[] {});
    }
    
    public ArrayList<Node> getAllSelectedNodes() {
    	ArrayList<Node> nodes;
    	
    	nodes = new ArrayList<Node>();
    	for (Integer index : selections) {
    		nodes.add(getNodeAt(index));
    	}
    	return nodes;
    }

    /**
     * Selects the row at the passed index. Selection can be canceled by a
     * BeforeSelecionHandler. If selection is allowed, then a SelectionEvent
     * will be fired to all registered handlers, and the selected row will be
     * scrolled in the visible view.
     * 
     * @param index
     */
    public void selectNodeAt(int index) {
        selectNodeAt(index, null);
    }

    /**
     * Selects the node passed to the mehtod. Selection can be canceled by a
     * BeforeSelecionHandler. If selection is allowed, then a SelectionEvent
     * will be fired to all registered handlers, and the selected row will be
     * scrolled in the visible view.  If the node is not currently displayed 
     * the selection will not occur.
     * 
     * @param index
     */
    public void selectNodeAt(Node node) {
        selectNodeAt(node,null);
    }
    
    /**
     * Selects the row at the passed index. Selection can be canceled by a
     * BeforeSelecionHandler. If selection is allowed, then a SelectionEvent
     * will be fired to all registered handlers, and the selected row will be
     * scrolled in the visible view. If addTo is passed as true and the table
     * allows multiple selection the row will be added to the current list of
     * selections.
     * 
     * @param index
     */
    public void selectNodeAt(int index, NativeEvent event) {
        if(index < 0) {
            unselectAll();
            return;
        }
    	
		/*
		 * If multiple selection is allowed check event for ctrl or shift keys.
		 * If none apply the logic will fall throw to normal selection.
		 */
		if (isMultipleSelectionAllowed()) {
		    if (event != null && Event.getTypeInt(event.getType()) == Event.ONCLICK) {
                multiSelect(index,event);
                return;
            }
		}
		 
	    if(isNodeSelected(index))
	        return;
	       
	    if (event == null || fireBeforeSelectionEvent(index)) {
	        unselectAll();
	            
	        finishEditing();

	        selections.add(index);

	        view.applySelectionStyle(index);

	        if (event != null)
	               fireSelectionEvent(index);
	            
	        scrollToVisible(index);
	    }
    }

    /**
     * Selects the node passed in to the method. Selection can be canceled by a
     * BeforeSelecionHandler. If selection is allowed, then a SelectionEvent
     * will be fired to all registered handlers, and the selected row will be
     * scrolled in the visible view. If addTo is passed as true and the table
     * allows multiple selection the row will be added to the current list of
     * selections. If the node is not currently displayed the selection will not
     * occur
     * 
     * @param index
     */
    public void selectNodeAt(Node node, NativeEvent event) {
        if(isDisplayed(node))
        	selectNodeAt(nodeIndex.get(node).index,event);
    }
    
    public void selectAll() {
		if(isMultipleSelectionAllowed()) {
			selections = new ArrayList<Integer>();
			for(int i = 0; i < getRowCount(); i++)
    			selections.add(i);
			view.selectAll();
		}
    }
    
    private void multiSelect(int node, NativeEvent event) {
        int startSelect, endSelect, minSelected, maxSelected, i;
        boolean ctrlKey, shiftKey, selected = false;
        
        startSelect = node;
        endSelect = node;
        
        ctrlKey = event.getCtrlKey();
        shiftKey = event.getShiftKey();

        if (ctrlKey) {
            if (isNodeSelected(node)) {
                unselectNodeAt(node,event);
                return;
            }
        }else if (shiftKey) {
            if (!isAnyNodeSelected()) {
                startSelect = 0;
                endSelect = node;
            } else {
                Collections.sort(selections);
                minSelected = Collections.min(selections);
                maxSelected = Collections.max(selections);
                if (minSelected > node) {
                    startSelect = node;
                    endSelect = minSelected;
                } else if (node > maxSelected) {
                    startSelect = maxSelected;
                    endSelect = node;
                } else {
                    i = 0;
                    while (selections.get(i + 1) < node)
                        i++;
                    startSelect = selections.get(i);
                    endSelect = node;
                }
            }
            unselectAll(event);
        }else
            unselectAll(event);
            
        for (i = startSelect; i <= endSelect && i > -1; i++ ) {
            if ( !selections.contains(i)) {
                if (event == null || fireBeforeSelectionEvent(i)) {

                    selected = true;

                    finishEditing();

                    selections.add(i);

                    view.applySelectionStyle(i);

                    if (event != null)
                        fireSelectionEvent(i);
                }
            }
        }
        
        if (selected)
            scrollToVisible(endSelect);
        
    }
    
    public void unselectNodeAt(int index) {
    	unselectNodeAt(index,null);
    }

    /**
     * Unselects the row from the selection list. This method does nothing if
     * the passed index is not currently a selected row, otherwise the row will
     * be unselected and an UnselectEvent will be fired to all registered
     * handlers
     * 
     * @param index
     */
    protected void unselectNodeAt(int index,NativeEvent event) {
        if (selections.contains(index)) {
            finishEditing();
            if(event != null)
            	fireUnselectEvent(index);
            selections.remove(new Integer(index));
            view.applyUnselectionStyle(index);
        }
    }
    
    /**
     * Unselects the passed node from the selection list. This method does nothing if
     * the passed index is not currently a selected row, otherwise the row will
     * be unselected and an UnselectEvent will be fired to all registered
     * handlers
     * 
     * @param index
     */
    public void unselectNodeAt(Node node) {
        if(isDisplayed(node)) 
           unselectNodeAt(nodeIndex.get(node).index,null); 
    }

    /**
     * Returns the selected index of the first row selected
     * 
     * @return
     */
    public int getSelectedNode() {
        return selections.size() > 0 ? selections.get(0) : -1;
    }

    /**
     * Used to determine if the passed row index is currently in the selection
     * list.
     * 
     * @param index
     * @return
     */
    public boolean isNodeSelected(int index) {
        return selections.contains(index);
    }
    
    /**
     * Used to determine if the passed node is currently in the selection
     * @param node
     * @return
     */
    public boolean isNodeSelected(Node node) {
        if(isDisplayed(node))
            return isNodeSelected(nodeIndex.get(node).index);
        return false;
    }

    /**
     * Used to determine if any row in the tree is selected
     * 
     * @return
     */
    public boolean isAnyNodeSelected() {
        return selections.size() > 0;
    }
    
    public void unselectAll() {
    	unselectAll(null);
    }

    /**
     * Clears all selections from the tree.
     */
    protected void unselectAll(NativeEvent event) {
        int count = selections.size();
        for (int i = 0; i < count; i++ )
            unselectNodeAt(selections.get(0),event);
    }

    // ********* Event Firing Methods ********************
    
    /**
     * Private method that will fire a BeforeNodeOpenEvent for the passed
     * index. Returns false if the open is canceled by registered handler
     * and true if the open is allowed.
     */
    private boolean fireBeforeNodeOpenEvent(int index) {
        BeforeNodeOpenEvent event = null;
        
        if(!queryMode)
            event = BeforeNodeOpenEvent.fire(this, index, getNodeAt(index));
        
        return event == null || !event.isCancelled();
    }
    
    /**
     * Private method that will fire a NodeOpenedEvent for the passed index to
     * notify all registered handlers that the Node at the passed index was opened.
     * Returns true as a default.
     * 
     * @param index
     * @return
     */
    private void fireNodeOpenEvent(int index) {
        if(!queryMode)
            NodeOpenedEvent.fire(this, index, getNodeAt(index));
    }

    /**
     * Private method that will fire a BeforeNodeCloseEvent for the passed
     * index. Returns false if the close is canceled by registered handler
     * and true if the close is allowed.
     */
    private boolean fireBeforeNodeCloseEvent(int index) {
        BeforeNodeCloseEvent event = null;
        
        if(!queryMode)
            event = BeforeNodeCloseEvent.fire(this, index, getNodeAt(index));
        
        return event == null || !event.isCancelled();
    }
    
    /**
     * Private method that will fire a NodeClosedEvent for the passed index to
     * notify all registered handlers that the Node at the passed index was closed.
     * Returns true as a default.
     * 
     * @param index
     * @return
     */
    private void fireNodeCloseEvent(int index) {
        if(!queryMode)
            NodeClosedEvent.fire(this, index, getNodeAt(index));
    }
    
    /**
     * Private method that will fire a BeforeSelectionEvent for the passed
     * index. Returns false if the selection is canceled by registered handler
     * and true if the selection is allowed.
     */
    private boolean fireBeforeSelectionEvent(int index) {
        BeforeSelectionEvent<Integer> event = null;

        if ( !queryMode)
            event = BeforeSelectionEvent.fire(this, index);

        return event == null || !event.isCanceled();
    }

    /**
     * Private method that will fire a SelectionEvent for the passed index to
     * notify all registered handlers that row at the passed index was selected.
     * Returns true as a default.
     * 
     * @param index
     * @return
     */
    private boolean fireSelectionEvent(int index) {

        if ( !queryMode)
            SelectionEvent.fire(this, index);

        return true;
    }

    /**
     * Private method that will fire an UnselectionEvent for the passed index.
     * Returns false if the unselection was canceled by a registered handler and
     * true if the unselection is allowed.
     * 
     * @param index
     * @return
     */
    public void fireUnselectEvent(int index) {

        if ( !queryMode)
            UnselectionEvent.fire(this, index);
    }

    /**
     * Private method that will fire a BeforeCellEditedEvent for a cell in the
     * table. Returns false if the cell editing is canceled by a registered
     * handler and true if the user is allowed to edit the cell.
     * 
     * @param row
     * @param col
     * @param val
     * @return
     */
    private boolean fireBeforeCellEditedEvent(int row, int col, Object val) {
        BeforeCellEditedEvent event = null;

        if ( !queryMode)
            event = BeforeCellEditedEvent.fire(this, row, col, val);

        return event == null || !event.isCancelled();
    }

    /**
     * Private method that will fire a CellEditedEvent after the value of a cell
     * is changed by a user input. Returns true as default.
     * 
     * @param index
     * @return
     */
    private boolean fireCellEditedEvent(int row, int col) {

        if ( !queryMode)
            CellEditedEvent.fire(this, row, col);

        return true;
    }

    /**
     * Private method that fires a BeforeRowAddedEvent for the passed index and
     * Row. Returns false if the addition is canceled by a registered handler
     * and true if the addition is allowed.
     * 
     * @param index
     * @param row
     * @return
     */
    private boolean fireBeforeNodeAddedEvent(int index, Node parent, Node node) {
        BeforeNodeAddedEvent event = null;

        if ( !queryMode)
            BeforeNodeAddedEvent.fire(this, index, parent, node);

        return event == null || !event.isCancelled();
    }

    /**
     * Private method that fires a RowAddedEvent for the passed index and Row to
     * all registered handlers. Returns true as a default.
     * 
     * @param index
     * @param row
     * @return
     */
    private boolean fireRowAddedEvent(int index, Node parent, Node node) {

        if ( !queryMode)
            NodeAddedEvent.fire(this, index, parent, node);

        return true;

    }

    /**
     * Private method that fires a BeforeRowDeletedEvent for the passed index
     * and Row. Returns false if the deletion is canceled by a registered
     * handler and true if the deletion is allowed.
     * 
     * @param index
     * @param row
     * @return
     */
    private boolean fireBeforeRowDeletedEvent(int index, Node row) {
        BeforeNodeAddedEvent event = null;

        if ( !queryMode)
            BeforeNodeDeletedEvent.fire(this, index, row);

        return event == null || event.isCancelled();
    }

    /**
     * Private method that fires a RowDeletedEvent for the passed index and Row
     * to all registered handlers. Returns true as a default.
     * 
     * @param index
     * @param row
     * @return
     */
    private boolean fireRowDeletedEvent(int index, Node row) {

        if ( !queryMode)
            NodeDeletedEvent.fire(this, index, row);

        return true;
    }
    
    protected boolean fireCellClickedEvent(int row, int col, boolean ctrlKey, boolean shiftKey) {
    	CellClickedEvent event = null;
    	
    	if( !queryMode) 
    		event = CellClickedEvent.fire(this, row, col, ctrlKey, shiftKey);
    	
    	return event == null || !event.isCancelled();
    }

    // ********* Edit Tree Methods *******************
    /**
     * Used to determine if a cell is currently being edited in the Tree
     */
    public boolean isEditing() {
        return editing;
    }

    /**
     * Sets the value of a cell in Tree model.
     * 
     * @param row
     * @param col
     * @param value
     */
    public void setValueAt(int row, int col, Object value) {
		LeafColumn column;
		ArrayList<Exception> exceptions;
		
        finishEditing();
        modelView.get(row).setCell(col, value);
        
		column = getNodeDefinition(modelView.get(row).getType()).get(col);
		
		exceptions = column.getCellRenderer().validate(value);
		
		if(column.isRequired() && value == null)
			exceptions.add(new Exception(Messages.get().exc_fieldRequired()));
		
		setValidateException(row,col,exceptions);
        
        refreshCell(row, col);
    }

    /**
     * Sets a row in the model at the passed index and refreshes the view.
     * 
     * @param index
     * @param row
     */
    public void setRowAt(int index, Node node) {
        Node parent, replace;
        ArrayList<Node> children;
        int pos;
        int childIndex;

        finishEditing();

        replace = getNodeAt(index);
        parent = replace.getParent();
        childIndex = parent.getIndex(replace);

        if (replace.isOpen()) {
            while (node.isNodeDescendent(modelView.get(index)))
                nodeIndex.remove(modelView.remove(index));
        }

        replace.removeFromParent();

        modelView.set(index, node);
        nodeIndex.put(node, new NodeIndex(index));

        if (node.isOpen()) {
            pos = index + 1;
            children = getDisplayedChildren(node);
            for (int i = 0; i < children.size(); i++ ) {
                modelView.add(pos + i, children.get(i));
                nodeIndex.put(children.get(i), new NodeIndex(pos + i));
            }
        }

        parent.add(node, childIndex);

        if ( !replace.isOpen() && !node.isOpen())
            renderView(index, index);
        else
            renderView(index, -1);
    }

    /**
     * Returns the value of a cell in the model.
     * 
     * @param row
     * @param col
     * @return
     */
    public <T>  T getValueAt(int row, int col) {
        if (modelView == null)
            return null;
        return modelView.get(row).getCell(col);
    }

    /**
     * Method to put a cell into edit mode. If a cell can not be edited than
     * false will be returned
     * 
     * @param row
     * @param col
     * @return
     */
    public boolean startEditing(int row, int col) {
        return startEditing(row, col, null);
    }

    /**
     * Method that sets focus to a cell in the Tree and readies it for user
     * input. event is passed to this method by view click handler to be able to
     * check for multiple selection logic
     * 
     * @param row
     * @param col
     * @return
     */
    @SuppressWarnings("rawtypes")
	protected boolean startEditing(final int row, final int col, final NativeEvent event) {

        /*
         * Return out if the tree is not enable or the passed cell is already
         * being edited
         */
        if ( !isEnabled() || (row == editingRow && col == editingCol))
            return false;

        finishEditing();

        selectNodeAt(row,event);

        // Check if the row was able to be selected, if not return.
        if ( !isNodeSelected(row))
            return false;
        
        // If a column is outside the definition of this column then return false
        if (col >= getNodeDefinition(getNodeAt(row).getType()).size())
            return false;
        
        // If a column is not editable then return false
        if (!getNodeDefinitionAt(getNodeAt(row).getType(),col).hasEditor())
            return false;

        // Fire before cell edited event to allow user the chance to cancel
        if ( !fireBeforeCellEditedEvent(row, col, getValueAt(row, col)))
            return false;

        /*
         * Set editing attribute values.
         */
        editingRow = row;
        editingCol = col;
        editing = true;

        view.startEditing(row, col, getValueAt(row, col), event);

        return true;
    }

    public void finishEditing() {
    	finishEditing(true);
    }
    
    /**
     * Method called to complete editing of any cell in the tree. Method does
     * nothing a cell is not currently being edited.
     */
    public void finishEditing(boolean keepFocus) {
        Object newValue, oldValue;
        int row, col;

        /*
         * Return out if not currently editing.
         */
        if ( !editing)
            return;

        /*
         * Reset editing attribute values
         */
        editing = false;
        row = editingRow;
        col = editingCol;
        editingRow = -1;
        editingCol = -1;

        /*
         * Retrieve new value form cell editor, store value in the model, and
         * render the cell
         */
        newValue = view.finishEditing(row, col);
        oldValue = getValueAt(row, col);
        setValueAt(row,col,newValue);
        //modelView.get(row).setCell(col, newValue);
        //refreshCell(row, col);

        /*
         * fire a cell edited event if the value of the cell was changed
         */
        if (Util.isDifferent(newValue, oldValue)) {
            fireCellEditedEvent(row, col);
        }

      
		/*
		 * Call setFocus(true) so that the KeyHandler will receive events when
		 * no cell is being edited
		 */
		if(keepFocus)
			setFocus(true);
    }

    /**
     * Returns the current row where cell is being edited
     * 
     * @return
     */
    public int getEditingRow() {
        return editingRow;
    }

    /**
     * Returns the current column where cell is being edited
     * 
     * @return
     */
    public int getEditingCol() {
        return editingCol;
    }
    
    @SuppressWarnings("rawtypes")
	protected CellEditor getCellEditor(int r, int c) {
        return getNodeDefinitionAt(getNodeAt(r).getType(), c).getCellEditor();
    }
    
    @SuppressWarnings("rawtypes")
	protected CellRenderer getCellRenderer(int r, int c) {
        return getNodeDefinitionAt(getNodeAt(r).getType(),c).getCellRenderer();
    }

    // ********* Draw Scroll Methods ****************
    /**
     * Scrolls the table in the required direction to make sure the passed index
     * is visible. Or if the index passed is in the view range refresh the row
     * to make sure that the latest data is shown (i.e. row Added before scroll
     * size is hit).
     */
    public boolean scrollToVisible(int index) {
        return view.scrollToVisible(index);
    }

    /**
     * Method to scroll the tree by the specified number of rows. A negative
     * value will cause the table to scroll up and a positive to scroll down.
     * 
     * @param rows
     */
    public void scrollBy(int rows) {
        view.scrollBy(rows);
    }

    /**
     * Redraws the tree when any part of its physical definition is changed.
     */
    protected void layout() {
        computeColumnsWidth();
        view.layout();
    }

    /**
     * Method called when a column width has been set to resize the tree
     * columns
     */
    protected void resize() {
        if ( !isAttached()){
        	layout();
            return;
        }

        finishEditing();

        computeColumnsWidth();

        if (hasHeader)
            view.getHeader().resize();

        view.resize();
    }

    /**
     * Method will have to view re-compute its visible rows and refresh the view
     * 
     * @param startR
     * @param endR
     */
    protected void renderView(int startR, int endR) {
        view.adjustScrollBarHeight();
        view.renderView(startR, endR);
    }

    /**
     * Method computes the XForColumn and ColumForX arrays and set the
     * totoalColumnWidth
     */
    private void computeColumnsWidth() {
        int from, to;

        //
        // compute total width
        //
        totalColumnWidth = 0;
        xForColumn = new short[getColumnCount()];
        for (int i = 0; i < getColumnCount(); i++ ) {
            xForColumn[i] = (short)totalColumnWidth;
            totalColumnWidth += getColumnAt(i).getWidth();
        }
        //
        // mark the array
        //
        from = 0;
        columnForX = new short[totalColumnWidth];
        for (int i = 0; i < getColumnCount(); i++ ) {
            to = from + getColumnAt(i).getWidth();
            while (from < to)
                columnForX[from++ ] = (short)i;
        }
    }

    /**
     * redraws data in the cell passed
     * 
     * @param row
     * @param col
     */
    protected void refreshCell(int row, int col) {
        view.renderCell(row, col);
    }
    
    public void refreshNode(Node node) {
        view.renderView(nodeIndex.get(node).index,nodeIndex.get(node).index);
    }

    // ************* Implementation of ScreenWidgetInt *************

    /**
     * Sets whether this tree allows selection
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

    }

    /**
     * Used to determine if the tree is enabled for selection.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the Focus style to the Tree
     */
    public void addFocusStyle(String style) {
        addStyleName(style);
    }

    /**
     * Removes the Focus style from the Tree
     */
    public void removeFocusStyle(String style) {
        removeStyleName(style);
    }
    
	public void onFocus(FocusEvent event) {
	    /*
		Widget focused;
		
		focused = ((ScreenPanel)event.getSource()).getFocused();
		
		if(focused == null || !DOM.isOrHasChild(getElement(),focused.getElement()))
			finishEditing(false);
	    */
		
	}

    // ********** Implementation of Queryable *******************
    /**
     * Returns a list of QueryData objects for all Columns in the tree that
     * have values and will participate in the query.
     */
    public Object getQuery() {
        ArrayList<QueryData> qds;
        QueryData qd;

        if ( !queryMode)
            return null;

        qds = new ArrayList<QueryData>();

        for (int i = 0; i < getColumnCount(); i++ ) {
            qd = (QueryData)getValueAt(0, i);
            if (qd != null) {
                qd.setKey(getColumnAt(i).name);
                qds.add(qd);
            }
        }
        return qds.toArray(new QueryData[] {});
    }

    /**
     * Stub method for Queryable method
     */
    public void setQuery(QueryData query) {
        // Do nothing
    }

    /**
     * Puts the tree into and out of query mode.
     */
    public void setQueryMode(boolean query) {
    	assert(getNodeDefinition("query") != null);
    	
        Node root;

        if (query == queryMode)
            return;

        this.queryMode = query;
        if (query) {
        	if(!showRoot) {
        		root = new Node();
        		root.add(new Node(getNodeDefinition("query").size()).setType("query"));
        	}else
        		root = new Node(getNodeDefinition("query").size()).setType("query");
            setRoot(root);
        } else
            setRoot(null);
    }

    /**
     * Method to determine if Tree is in QueryMode
     * 
     * @return
     */
    public boolean getQueryMode() {
        return queryMode;
    }

    /**
     * Stub method from Queryable Interface
     */
    public void validateQuery() {

    }
    
    /**
     * Method used to determine if widget is currently in Query mode
     */
    public boolean isQueryMode() {
    	return queryMode;
    }

    /**
     * Convenience method to check if a widget has exceptions so we do not need
     * to go through the cost of merging the logical and validation exceptions
     * in the getExceptions method.
     * 
     * @return
     */
    public boolean hasExceptions(int row, int col) {
        Node key;

        key = getNodeAt(row);
        return (endUserExceptions != null && (endUserExceptions.containsKey(key) && endUserExceptions.get(
                                                                                                          key)
                                                                                                     .containsKey(
                                                                                                                  col))) ||
               (validateExceptions != null && (validateExceptions.containsKey(key) && validateExceptions.get(
                                                                                                             key)
                                                                                                        .containsKey(
                                                                                                                     col)));
    }
    
    public void addException(Node node, int col, Exception error) {
        int r;
        
        getEndUserExceptionList(node,col).add(error);
        
        if(nodeIndex != null && nodeIndex.containsKey(node)) {
            r = nodeIndex.get(node).index;
            view.renderExceptions(r, r);
        }
    }

    /**
     * Adds a manual Exception to the widgets exception list.
     */
    public void addException(int row, int col, Exception error) {
        getEndUserExceptionList(getNodeAt(row), col).add(error);
        view.renderExceptions(row, row);
    }

    /**
     * Method to add a validation exception to the passed cell.
     * 
     * @param row
     * @param col
     * @param error
     */
    public void setValidateException(int row, int col, ArrayList<Exception> errors) {
        Node node;
        
        node = getNodeAt(row);
        setValidateException(node,col,errors);
    }
    
    public void setValidateException(Node node, int col, ArrayList<Exception> errors) {
        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;
        HashMap<Integer, ArrayList<Exception>> rowExceptions; 

        // If hash is null and errors are passed as null, nothing to reset so
        // return
        if (validateExceptions == null && (errors == null || errors.isEmpty()))
            return;

        // If hash is not null, but errors passed is null then make sure the
        // passed cell entry removed
        if (validateExceptions != null && (errors == null || errors.isEmpty())) {
        	if(validateExceptions.containsKey(node)) {
        		rowExceptions = validateExceptions.get(node);
        		rowExceptions.remove(col);
        		if(rowExceptions.isEmpty())
        			validateExceptions.remove(node);
        	}
            return;
        }

        // If list is null we need to create the Hash to add the errors
        if (validateExceptions == null) {
            validateExceptions = new HashMap<Node, HashMap<Integer, ArrayList<Exception>>>();
            cellExceptions = new HashMap<Integer, ArrayList<Exception>>();

            validateExceptions.put(node, cellExceptions);
        }

        if (cellExceptions == null) {
        	if(!validateExceptions.containsKey(node)) {
        		cellExceptions = new HashMap<Integer, ArrayList<Exception>>();
        		validateExceptions.put(node,cellExceptions);
        	}else
        		cellExceptions = validateExceptions.get(node);
        }
        
        cellExceptions.put(col, errors);
    }

    /**
     * Gets the ValidateExceptions list to be displayed on the screen.
     */
    public ArrayList<Exception> getValidateExceptions(int row, int col) {
        if (validateExceptions != null)
            if(validateExceptions.containsKey(getNodeAt(row)))
                return validateExceptions.get(getNodeAt(row)).get(col);
        return null;
    }

    /**
     * Method used to get the set list of user exceptions for a cell.
     * 
     * @param row
     * @param col
     * @return
     */
    public ArrayList<Exception> getEndUserExceptions(int row, int col) {
        if (endUserExceptions != null)
            if(endUserExceptions.containsKey(getNodeAt(row)))
                return endUserExceptions.get(getNodeAt(row)).get(col);
        return null;
    }

    /**
     * Clears all manual and validate exceptions from the widget.
     */
    public void clearExceptions() {
        if (endUserExceptions != null || validateExceptions != null) {
            endUserExceptions = null;
            validateExceptions = null;
            view.renderExceptions( -1, -1);
        }
    }
    
    public void clearEndUserExceptions() {
        if (endUserExceptions != null) {
            endUserExceptions = null;
            view.renderExceptions( -1, -1);
        }
    }
    
    public void clearValidateExceptions() {
        if (validateExceptions != null) {
            validateExceptions = null;
            view.renderExceptions( -1, -1);
        }
    }
    
    public void clearExceptions(int row, int col) {
        if(row < getRowCount())
            clearExceptions(getNodeAt(row),col);
    }

    /**
     * Clears all exceptions from the tree cell passed
     * 
     * @param row
     * @param col
     */
    public void clearExceptions(Node node, int col) {
        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;

        if (endUserExceptions != null) {
            cellExceptions = endUserExceptions.get(node);
            if (cellExceptions != null) {
                cellExceptions.remove(col);
                if (cellExceptions.size() == 0)
                    endUserExceptions.remove(node);
            }
        }

        if (validateExceptions != null) {
            cellExceptions = validateExceptions.get(node);
            if (cellExceptions != null) {
                cellExceptions.remove(col);
                if (cellExceptions.size() == 0)
                    validateExceptions.remove(node);
            }
        }

        if(nodeIndex != null && nodeIndex.containsKey(node))
            view.renderExceptions(nodeIndex.get(node).index,nodeIndex.get(node).index);

    }
    
    public void clearEndUserExceptions(int row, int col) {
        if(row < getRowCount())
            clearEndUserExceptions(getNodeAt(row),col);
    }

    /**
     * Clears all exceptions from the tree cell passed
     * 
     * @param row
     * @param col
     */
    public void clearEndUserExceptions(Node node, int col) {
        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;

        if (endUserExceptions != null) {
            cellExceptions = endUserExceptions.get(node);
            if (cellExceptions != null) {
                cellExceptions.remove(col);
                if (cellExceptions.size() == 0)
                    endUserExceptions.remove(node);
            }
        }

        if(nodeIndex != null && nodeIndex.containsKey(node))
            view.renderExceptions(nodeIndex.get(node).index,nodeIndex.get(node).index);

    }

    /**
     * Method will get the list of the exceptions for a cell and will create a
     * new list if no exceptions are currently on the cell.
     * 
     * @param row
     * @param col
     * @return
     */
    private ArrayList<Exception> getEndUserExceptionList(Node node, int col) {
        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;
        ArrayList<Exception> list = null;
        
        if (endUserExceptions == null)
            endUserExceptions = new HashMap<Node, HashMap<Integer, ArrayList<Exception>>>();

        cellExceptions = endUserExceptions.get(node);

        if (cellExceptions == null) {
            cellExceptions = new HashMap<Integer, ArrayList<Exception>>();
            endUserExceptions.put(node, cellExceptions);
        }

        list = cellExceptions.get(col);

        if (list == null) {
            list = new ArrayList<Exception>();
            cellExceptions.put(col, list);
        }

        return list;

    }

    /**
     * Method will get the list of the exceptions for a cell and will create a
     * new list if no exceptions are currently on the cell.
     * 
     * @param row
     * @param col
     * @return
     */
    private ArrayList<Exception> getValidateExceptionList(int row, int col) {
        Node key;

        key = getNodeAt(row);
        return getValidateExceptionList(key,col);
    }
    
    private ArrayList<Exception> getValidateExceptionList(Node key, int col) {
        HashMap<Integer, ArrayList<Exception>> cellExceptions = null;
        ArrayList<Exception> list;
        if (validateExceptions == null)
            validateExceptions = new HashMap<Node, HashMap<Integer, ArrayList<Exception>>>();

        cellExceptions = validateExceptions.get(key);

        if (cellExceptions == null) {
            cellExceptions = new HashMap<Integer, ArrayList<Exception>>();
            validateExceptions.put(key, cellExceptions);
        }

        list = cellExceptions.get(col);

        if (list == null) {
            list = new ArrayList<Exception>();
            cellExceptions.put(col, list);
        }

        return list;

    }

    /**
     * Method to draw the balloon display of the exceptions for a cell
     * 
     * @param row
     * @param col
     * @param x
     * @param y
     */
    protected void drawExceptions(int row, int col, int x, int y) {
        if (row == editingRow && col == editingCol)
            return;

        Balloon.drawExceptions(getEndUserExceptions(row, col), 
                               getValidateExceptions(row,col),
                               view.table().getCellFormatter().getElement(row, col),
                               x, y);
    }

    // ************ Drag and Drop methods **********************************
    /**
     * Method will enable the rows in the tree to be dragged. This must be
     * called before the model is first set.
     */
    public void enableDrag() {
        assert root == null : "Drag must be set before model is loaded";

        dragController = new TreeDragController(this, RootPanel.get());
    }

    /**
     * Method will enable this tree to receive drop events from a drag
     */
    public void enableDrop() {
        dropController = new TreeDropController(this);
    }

    /**
     * Adds a DropController as a drop target for rows from this tree
     * 
     * @param target
     */
    public void addDropTarget(DropController target) {
        dragController.registerDropController(target);
    }

    /**
     * Removes a DropController as a drop target for rows from this tree
     * 
     * @param target
     */
    public void removeDropTarget(DropController target) {
        dragController.unregisterDropController(target);
    }

    /**
     * Returns the TreeDragController for this Tree.
     * 
     * @return
     */
    public TreeDragController getDragController() {
        return dragController;
    }

    /**
     * Returns the TreeDropController for this Tree.
     * 
     * @return
     */
    public TreeDropController getDropController() {
        return dropController;
    }

    // ********* Registration of Handlers ******************
    /**
     * Registers a BeforeSelectionHandler to this Tree
     */
    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler) {
        return addHandler(handler, BeforeSelectionEvent.getType());
    }

    /**
     * Registers a SelectionHandler to this Tree
     */
    public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    /**
     * Registers an UnselectionHandler to this Tree
     */
    public HandlerRegistration addUnselectionHandler(UnselectionHandler<Integer> handler) {
        return addHandler(handler, UnselectionEvent.getType());
    }

    /**
     * Registers a BeforeCellEditedHandler to this Tree
     */
    public HandlerRegistration addBeforeCellEditedHandler(BeforeCellEditedHandler handler) {
        return addHandler(handler, BeforeCellEditedEvent.getType());
    }

    /**
     * Registers a CellEditedHandler to this Tree
     */
    public HandlerRegistration addCellEditedHandler(CellEditedHandler handler) {
        return addHandler(handler, CellEditedEvent.getType());
    }

    /**
     * Registers a BeforeRowAddedHandler to this Tree
     */
    public HandlerRegistration addBeforeNodeAddedHandler(BeforeNodeAddedHandler handler) {
        return addHandler(handler, BeforeNodeAddedEvent.getType());
    }

    /**
     * Registers a RowAddedHandler to this Tree
     */
    public HandlerRegistration addNodeAddedHandler(NodeAddedHandler handler) {
        return addHandler(handler, NodeAddedEvent.getType());
    }

    /**
     * Registers a BeforeRowDeletedHandler to this Tree
     */
    public HandlerRegistration addBeforeNodeDeletedHandler(BeforeNodeDeletedHandler handler) {
        return addHandler(handler, BeforeNodeDeletedEvent.getType());
    }

    /**
     * Registers a RowDeletedHandler to this Tree
     */
    public HandlerRegistration addNodeDeletedHandler(NodeDeletedHandler handler) {
        return addHandler(handler, NodeDeletedEvent.getType());
    }
    
    /**
     * Registers a BeforeNodeOpenHandler to this Tree
     */
    public HandlerRegistration addBeforeNodeOpenHandler(BeforeNodeOpenHandler handler) {
        return addHandler(handler, BeforeNodeOpenEvent.getType());
    }

    /**
     * Registers a NodeClosedHandler to this Tree
     */
    public HandlerRegistration addNodeClosedHandler(NodeClosedHandler handler) {
        return addHandler(handler, NodeClosedEvent.getType());
    }

    /**
     * Registers a BeforeNodeClosedHandler to this Tree
     */
    public HandlerRegistration addBeforeNodeCloseHandler(BeforeNodeCloseHandler handler) {
        return addHandler(handler, BeforeNodeCloseEvent.getType());
    }

    /**
     * Registers a NodeOpenEvent to this Tree
     */
    public HandlerRegistration addNodeOpenedHandler(NodeOpenedHandler handler) {
        return addHandler(handler, NodeOpenedEvent.getType());
    }
    
    /**
     * Registers a CellClickedEvent to this Tree
     */
    public HandlerRegistration addCellClickedHandler(CellClickedHandler handler) {
    	return addHandler(handler, CellClickedEvent.getType());
    }

    /**
     * This method will check the model to make sure that all required cells
     * have values
     */
    public void validate() {
        HashSet<String> required = new HashSet<>();

        finishEditing();

        for (String type : nodeDefs.keySet()) {
        	ArrayList<LeafColumn> columns = nodeDefs.get(type);
        	for (int i = 0; i < columns.size(); i++) {
        		if (columns.get(i).isRequired()) {
        			required.add(type);
        		}
        	}
        }
        
        if (required.isEmpty()) {
        	return;
        }
        
        validateNode(required, root);
        
        if(validateExceptions != null) {
        	 getDisplayedRows();
            ((StaticView)view).bulkRender();
        
            ((StaticView)view).bulkExceptions(validateExceptions);
        
            if(isAnyNodeSelected()) {
                for(Integer index : selections) 
                    ((StaticView)view).applySelectionStyle(index);
            }
    	}
    }

    public void validateNode(HashSet<String> required, Node node) {
    	if (required.contains(node.getType())) {
    		for(int i = 0; i < nodeDefs.get(node.type).size(); i++) {
    			if (nodeDefs.get(node.type).get(i).isRequired()) {
    				if(node.getCell(i) == null) {
    					ArrayList<Exception> exceptions = getValidateExceptionList(node, i);
    					exceptions.add(new Exception(Messages.get().exc_fieldRequired()));
 				        setValidateException(node, i, exceptions);
 				        node.isOpen = true;
 				        Node parent = node.getParent();
 				        while(!parent.isOpen) {
 				        	parent.isOpen = true;
 				        	parent = parent.getParent();
 				        }
    				}
    			}
    		}
    	}
    	if (node.hasChildren()) {
    		for (Node child : node.children()) {
    			validateNode(required,child);
    		}
    	}
    }

    /**
     * Returns the model as part of the HasValue interface
     */
    public Node getValue() {
        return root;
    }

    /**
     * Sets the model as part of the HasValue interface
     */
    public void setValue(Node value) {
        setValue(value, false);
    }

    /**
     * Sets the model and will fire ValueChangeEvent if fireEvents is true as
     * part of the HasValue interface
     */
    public void setValue(Node value, boolean fireEvents) {
        setRoot(value);

        if (fireEvents)
            ValueChangeEvent.fire(this, value);

    }

    /**
     * Handler Registration for ValueChangeEvent
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Node> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void addException(Exception exception) {
        // TODO Auto-generated method stub

    }

    public void addExceptionStyle() {
        // TODO Auto-generated method stub

    }

    public ArrayList<Exception> getEndUserExceptions() {
        ArrayList<Exception> exceptions;
        
        if(endUserExceptions == null)
            return null;
        
        exceptions = new ArrayList<Exception>();
        
        for(HashMap<Integer,ArrayList<Exception>> row : endUserExceptions.values()) {
            for(ArrayList<Exception> excs : row.values()) {
                exceptions.addAll(excs);
            }
        }
        
        return exceptions;
    }

    public ArrayList<Exception> getValidateExceptions() {
        ArrayList<Exception> exceptions;
        
        if(validateExceptions == null)
            return null;
        
        exceptions = new ArrayList<Exception>();
        
        for(HashMap<Integer,ArrayList<Exception>> row : validateExceptions.values()) {
            for(ArrayList<Exception> excs : row.values()) {
                exceptions.addAll(excs);
            }
        }
        
        return exceptions;
    }

    public boolean hasExceptions() {
        validate();
        return (endUserExceptions != null && !endUserExceptions.isEmpty()) ||
               (validateExceptions != null && !validateExceptions.isEmpty());
    }

    public void removeExceptionStyle() {
        // TODO Auto-generated method stub

    }
    
    private class NodeIndex {
        int index;
        
        public NodeIndex(int index) {
            this.index = index;
        }
    }
    
    @Override
    public void add(IsWidget w) {
        if(w instanceof Column) {
            ((Column)w).setTree(this);
            addColumn((Column)w);
        } else if (w instanceof Leaf) {
            addNodeDefinition(((Leaf)w).getKey(),((Leaf)w).getColumns());
        }   
    }
    
    public void onResize() {
        Element parent = (Element) (getParent() instanceof LayoutPanel ? ((LayoutPanel)getParent()).getWidgetContainerElement(this)
                                                            : getParent().getElement());

        int width = parent.getOffsetWidth();
        int height = parent.getOffsetHeight();
         
        view.setSize(width+"px", height+"px");
        view.onResize();
    }
    
    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        onResize();
    }
    
    public void setTipProvider(CellTipProvider tipProvider) {
        this.tipProvider = tipProvider;
        
        if(toolTip == null)
            setBalloonOptions(new Options());
            
    }

    @Override
    public Options getBalloonOptions() {
        return toolTip;
    }

    @Override
    public void setBalloonOptions(Options options) {
        toolTip = options;
        
        options.setPlacement(Placement.MOUSE);
        
        view.table().addCellMouseOverHandler(new CellMouseOverEvent.Handler() {
            
            @Override
            public void onCellMouseOver(CellMouseOverEvent event) {
                tipRow = event.getRow();
                tipCol = event.getCol();
                final int x,y;
                
                Element td = view.table().getCellFormatter().getElement(event.getRow(), event.getCol());
                
                y = td.getAbsoluteTop();
                x = td.getAbsoluteLeft() + (td.getOffsetWidth()/2);
                
                if(!hasExceptions(tipRow, tipCol)) {
                    balloonTimer = new Timer() {
                        public void run() {
                            Balloon.show((HasBalloon)source, x, y);
                        }
                    };
                    balloonTimer.schedule(500);
                }
                
            }
        });
        
       options.setTipProvider(new Balloon.TipProvider<Object>() {
        
           @Override
           public Object getTip(HasBalloon target) {
               
               if(tipProvider != null)
                   return tipProvider.getTip(tipRow, tipCol);
               
               return "No Tip Provider set";
           }
           
       });
    }
}
