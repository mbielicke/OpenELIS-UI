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

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.TableCSS;
import org.openelis.ui.resources.TreeCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.CSSUtils;
import org.openelis.ui.widget.DragItem;
import org.openelis.ui.widget.Balloon;
import org.openelis.ui.widget.table.CellEditor;
import org.openelis.ui.widget.table.CellRenderer;
import org.openelis.ui.widget.table.Container;
import org.openelis.ui.widget.table.FlexTable;
import org.openelis.ui.widget.table.event.CellMouseOutEvent;
import org.openelis.ui.widget.table.event.CellMouseOverEvent;
import org.openelis.ui.widget.tree.View.TreeGrid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.NativeVerticalScrollbar;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Composite GWT widget to draw and handle logic for displaying a Table. All
 * methods are protected and only used by the Table widget itself.
 * 
 * @author tschmidt
 * 
 */
public class StaticView extends ViewInt {
    @UiTemplate("staticView.ui.xml")
    interface ViewUiBinder extends UiBinder<Widget, StaticView> {
    };

    public static final ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);

    /**
     * Reference to Table this View is used in
     */
    protected Tree                  tree;

    /**
     * Table used to draw Table flexTable
     */
    @UiField
    protected FlexTable              flexTable;

    /**
     * Table used to draw Header flexTable for the table
     */
    @UiField(provided = true)
    protected Header                 header;

    /**
     * Scrollable area that contains flexTable and possibly header for
     * horizontal scroll.
     */
    @UiField
    protected ScrollPanel            scrollView;

    @UiField
    protected LayoutPanel            inner;

    /**
     * Flag used to determine if the table has been attached to know when to do
     * layout for the first time.
     */
    protected boolean                attached, sized;
    
    protected ClickHandler           toggleHandler;
    
    protected int                    indent = 18;

    /**
     * Container to hold the widget for formatting and spacing
     */
    private Container                container;

    protected TreeCSS               css;

    protected StaticView             source   = this;

    /**
     * Constructor that takes a reference to the table that will use this view
     * 
     * @param tree
     */
    public StaticView(Tree tre) {
        header = new Header(tre);

        initWidget(uiBinder.createAndBindUi(this));

        this.tree = tre;

        toggleHandler = new ClickHandler() {
            public void onClick(ClickEvent event) {
                Cell cell = ((Grid)event.getSource()).getCellForEvent(event);
                String cellType = cell.getElement().getClassName();
                
                if ( cellType.equals(css.treeOpenImage()) || cellType.equals(css.treeClosedImage())) {
                    tree.toggle(flexTable.getRowForEvent(event.getNativeEvent()));
                    event.stopPropagation();
                }
            }
        };
        
        container = new Container();

        setCSS(UIResources.INSTANCE.tree());

        scrollView.addScrollHandler(new ScrollHandler() {

            @Override
            public void onScroll(ScrollEvent event) {
                DOM.setStyleAttribute(header.getElement(),
                                      "left",
                                      (0 - scrollView.getHorizontalScrollPosition()) + "px");
            }
        });
        
    }

    @UiHandler("flexTable")
    protected void handleClick(ClickEvent event) {
        int r, c;

        // if x < 0 the user moused out of table before letting up button
        // ignore event in this case
        if (event.getX() < 0)
            return;

        c = flexTable.getCellForEvent(event).getCellIndex();
        r = flexTable.getCellForEvent(event).getRowIndex();

        if (tree.fireCellClickedEvent(r, c, event.isControlKeyDown(), event.isShiftKeyDown()))
            tree.startEditing(r, c, event.getNativeEvent());
    }

    /**
     * Method that will layout the table view and is called on first time
     * attached and when attributes affecting layout are changed in the table
     */
    protected void layout() {
        
        com.google.gwt.dom.client.Node colgroup;

        /*
         * If View is not attached to DOM yet get out. onAttach will call
         * layout() the first time this widget attached.
         */

        if ( !attached)
            return;
        
        colgroup = flexTable.getElement().getElementsByTagName("colgroup").getItem(0);
        if(colgroup != null){
            while(colgroup.getChildCount() > tree.getColumnCount())
                colgroup.removeChild(colgroup.getChild(0));
        }

        for (int c = 0; c < tree.getColumnCount(); c++ ) {
            flexTable.getColumnFormatter().setWidth(c, tree.getColumnAt(c).getWidth() + "px");
            if (tree.getColumnAt(c).getStyle() != null)
                flexTable.getColumnFormatter().setStyleName(c, tree.getColumnAt(c).getStyle());
        }
        flexTable.setWidth(tree.getTotalColumnWidth() + "px");
        flexTable.setHeight("1px");
        DOM.setStyleAttribute(flexTable.getElement(), "backgroundColor", "transparent");

        // ********** Create and attach Header **************
        if (tree.hasHeader()) {
            UIObject.setVisible(inner.getWidgetContainerElement(header), true);
            header.setVisible(true);
            header.layout();
            inner.setWidgetTopBottom(scrollView, CSSUtils.getHeight(header), Unit.PX, 0, Unit.PX);
        } else {
            UIObject.setVisible(inner.getWidgetContainerElement(header), false);
            header.setVisible(false);
            inner.setWidgetTopBottom(scrollView, 0, Unit.PX, 0, Unit.PX);
        }
    }

    /**
     * This method is called when a column width is changed. It will resize the
     * columns to there currently set width.
     */
    protected void resize() {

        for (int c = 0; c < tree.getColumnCount(); c++ )
            flexTable.getColumnFormatter().setWidth(c, tree.getColumnAt(c).getWidth() + "px");
        flexTable.setWidth(tree.getTotalColumnWidth() + "px");
    }

    /**
     * Will create the the necessary visible rows for the flexTable table
     * depending on what is needed at the time. If model.size() < visibleRows
     * then the number of rows created will equal model.size() else the number
     * visibleRows will be created for the flexTable table.
     */
    private void createRow(int rc) {
        flexTable.insertRow(rc);
        flexTable.getCellFormatter().setHeight(rc, 0, tree.getRowHeight() + "px");
        flexTable.getRowFormatter().getElement(rc).setAttribute("index", "" + rc);

        if (tree.getDragController() != null)
            tree.dragController.makeDraggable(new DragItem(tree, flexTable.getRowFormatter()
                                                                            .getElement(rc)));
    }

    /**
     * This method will redraw the table from the startRow to the endRow that
     * are passed in as params. Rows are passed as -1,-1 the entire view will be
     * drawn.
     * 
     * @param smr
     * @param emr
     */
    protected void renderView(int smr, int emr) {
        int r, startMax;

        //if ( !attached)
        //    return;

        tree.finishEditing();

        if (smr < 0)
            smr = 0;

        if (emr < 0)
            emr = tree.getRowCount() - 1;

        startMax = scrollView.getMaximumVerticalScrollPosition();

        /*
         * Create/Load Rows in the flexTable table
         */
        for (r = smr; r <= emr; r++ ) {
            /*
             * Create table row if needed
             */
            if (r >= flexTable.getRowCount()) {
                createRow(flexTable.getRowCount());
                /*
                 * ColumnFormatter is not available until first row is inserted
                 * so call resize after that
                 */
                if (r == 0)
                    resize();
            }

            for (int c = 0; c < tree.getColumnCount(); c++ )
                renderCell(r, c);

            applyRowStyle(r);
        }

        while (flexTable.getRowCount() > tree.getRowCount())
            flexTable.removeRow(tree.getRowCount());

        adjustForScroll(startMax);

        if ( !tree.fixScrollBar && !sized) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    if (flexTable.getOffsetHeight() < scrollView.getOffsetHeight())
                        tree.getParent().setHeight(CSSUtils.getHeight(flexTable) + "px");
                    sized = true;
                }

            });
        }

    }

    protected void addNodes(int start, int end) {
        int startMax = scrollView.getMaximumVerticalScrollPosition();

        for(int r = start; r <= end; r++) {
            createRow(r);

            for (int c = 0; c < tree.getColumnCount(); c++ )
                renderCell(r, c);

            applyRowStyle(r);
        }

        adjustForScroll(startMax);

    }

    protected void addColumn(int c) {
        for (int i = 0; i < flexTable.getRowCount(); i++ ) {
            flexTable.insertCell(i, c);
        }
        layout();
        for (int i = 0; i < flexTable.getRowCount(); i++ ) {
            renderCell(i, c);
        }
    }

    protected void removeColumn(int c) {
        for (int i = 0; i < flexTable.getRowCount(); i++ ) {
            flexTable.removeCell(i, c);
        }
        layout();
    }

    protected void removeNodes(int start, int count) {
        int startMax = scrollView.getMaximumVerticalScrollPosition();
        for(int r = 0; r < count; r++) {
            flexTable.removeRow(start);
        }

        adjustForScroll(startMax);
    }

    protected void removeAllNodes() {
        flexTable.removeAllRows();
    }

    protected void renderSelections(int start, int end) {

        for (int r = start; r <= end; r++ ) {
            if (tree.isNodeSelected(r))
                flexTable.getRowFormatter().addStyleName(r, css.Selection());
            else
                flexTable.getRowFormatter().removeStyleName(r, css.Selection());
        }

    }

    protected void renderExceptions(int start, int end) {
        start = start < 0 ? 0 : start;
        end = end < 0 ? flexTable.getRowCount() - 1 : end;

        for (int r = start; r <= end; r++ ) {
            for (int c = 0; c < tree.getColumnCount(); c++ ) {
                if (tree.hasExceptions(r, c)) {
                    flexTable.getCellFormatter().addStyleName(r, c, css.InputError());
                    flexTable.addCellMouseOverHandler(new CellMouseOverEvent.Handler(r, c) {

                        @Override
                        public void onCellMouseOver(CellMouseOverEvent event) {
                            tree.drawExceptions(event.getRow(),
                                                 event.getCol(),
                                                 event.getX(),
                                                 event.getY());
                        }

                    });
                } else {
                    flexTable.getCellFormatter().removeStyleName(r, c, css.InputError());
                    flexTable.removeHandler(r, c);
                }

                //flexTable.getCellFormatter().setVisible(r, c, tree.getColumnAt(c).isDisplayed());
            }
        }
    }

    /**
     * This method will apply either a style that is set in the Row getStyle
     * method or the selection style if the row is selected
     */
    protected void applyRowStyle(int r) {
        String style;

        if (r >= tree.getRowCount())
            return;

        style = tree.getNodeAt(r).getStyle(r);
        if (style != null)
            flexTable.getRowFormatter().setStyleName(r, style);

        if (tree.isNodeSelected(r))
            flexTable.getRowFormatter().addStyleName(r, css.Selection());
        else
            flexTable.getRowFormatter().removeStyleName(r, css.Selection());
    }

    /**
     * Applies the selection style to a table row
     * 
     * @param r
     */
    protected void applySelectionStyle(int r) {
        int rc;

        rc = r;
        if (rc > -1)
            flexTable.getRowFormatter().addStyleName(rc, css.Selection());

    }

    /**
     * Removes the Selection style from a table row
     * 
     * @param r
     */
    protected void applyUnselectionStyle(int r) {
        int rc;

        rc = r;
        if (rc > -1)
            flexTable.getRowFormatter().removeStyleName(rc, css.Selection());

    }

    protected void renderCell(int r, int c) {
        CellRenderer renderer;
        HTMLTable table;
        int row, col;
        Node node;
        
        node = tree.getNodeAt(r);

        if (c < tree.getNodeDefinition(node.getType()).size())
            renderer = tree.getCellRenderer(r, c);
        else {
            flexTable.setText(r, c, "");
            flexTable.getCellFormatter().removeStyleName(r, c, css.InputError());
            return;
        }
        
        table = flexTable;
        row = r;
        col = c;
        
        if(c == 0) {
            table = getTreeCell(node,r,c);
            row = 0; 
            col = table.getCellCount(0) - 1;
        }
        
        
        if (tree.getQueryMode())
            renderer.renderQuery(table, row, col, (QueryData)tree.getValueAt(r, c));
        else
            renderer.render(table, row, col, tree.getValueAt(r, c));

        if (tree.hasExceptions(r, c)) {
            flexTable.getCellFormatter().addStyleName(r, c, Balloon.isWarning(tree.getEndUserExceptions(r,c),tree.getValidateExceptions(r, c)) ? css.InputWarning() : css.InputError());
            flexTable.addCellMouseOverHandler(new CellMouseOverEvent.Handler(r, c) {
                @Override
                public void onCellMouseOver(CellMouseOverEvent event) {
                    tree.drawExceptions(event.getRow(), event.getCol(), event.getX(), event.getY());
                }

            });
        } else {
            flexTable.getCellFormatter().removeStyleName(r, c, css.InputError());
            flexTable.getCellFormatter().removeStyleName(r, c, css.InputWarning());
            flexTable.removeHandler(r, c);
        }

        //flexTable.getCellFormatter().setVisible(r, c, tree.getColumnAt(c).isDisplayed());
    }

    /**
     * Will put the passed cell into edit mode making sure the the cell is
     * compeltely visible first
     * 
     * @param r
     * @param c
     * @param value
     * @param event
     */
    protected void startEditing(int r, final int c, Object value, NativeEvent event) {
        if(c > 0) {
            container.setWidth( (tree.getColumnAt(c).getWidth() - 3));
            container.setHeight( (tree.getRowHeight() - 3));
            flexTable.setWidget(r, c, container);
        } else {
            TreeGrid grid = (TreeGrid)flexTable.getWidget(r, c);
            int width = grid.getCellFormatter().getElement(0, 3).getOffsetWidth() - 3;
            if(width > 0)
                container.setWidth(width);
            container.setHeight(tree.getRowHeight() - 3);
            grid.setWidget(0, 3, container);
        }

        if (tree.getQueryMode())
            tree.getCellEditor(r,c)
                 .startEditingQuery((QueryData)tree.getValueAt(r, c), container, event);
        else
            tree.getCellEditor(r,c)
                 .startEditing(tree.getValueAt(r, c), container, event);
    }

    /**
     * Returns the value of the CellEditor
     * 
     * @param r
     * @param c
     * @return
     */
    protected Object finishEditing(int r, int c) {
        CellEditor cellEditor;

        cellEditor = tree.getCellEditor(r,c);

        return cellEditor.finishEditing();
    }

    /**
     * This method will re-adjust the scrollbar height based on number of rows
     * in the model
     */
    protected void adjustScrollBarHeight() {

    }

    /**
     * Method will scroll the table to make sure the passed row is included in
     * the view
     * 
     * @param r
     * @return
     */
    protected boolean scrollToVisible(int r) {

        if (scrollView.getMaximumVerticalScrollPosition() == 0)
            return false;

        if (isRowVisible(r))
            return false;

        int hPos = scrollView.getHorizontalScrollPosition();

        DOM.scrollIntoView(flexTable.getRowFormatter().getElement(r));

        scrollView.setHorizontalScrollPosition(hPos);

        return true;
    }

    /**
     * Method will scroll the view up or down by the passed number of rows. Pass
     * negative value to scroll up.
     * 
     * @param n
     */
    protected void scrollBy(int n) {

    }

    /**
     * Returns true if the passed row is drawn in the current view
     * 
     * @param r
     * @return
     */
    protected boolean isRowVisible(int r) {
        Element row = flexTable.getRowFormatter().getElement(r);
        int top = row.getOffsetTop();
        int height = row.getOffsetHeight();

        return top >= scrollView.getVerticalScrollPosition() &&
               ( (top + height) <= scrollView.getVerticalScrollPosition() +
                                   CSSUtils.getHeight(scrollView));
    }

    /**
     * Method is overridden from Composite so that layout() can be deferred
     * until it is attached to the DOM
     */
    @Override
    protected void onAttach() {

        super.onAttach();
        
        if(attached)
            return;

        attached = true;
        layout();
        flexTable.setVisible(false);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                flexTable.setVisible(true);
                Element svEl = inner.getWidgetContainerElement(scrollView);
                if (scrollView.getMaximumVerticalScrollPosition() == 0)
                    tree.setWidth(CSSUtils.getWidth(svEl) - 1);
                else
                    tree.setWidth(CSSUtils.getWidth(svEl) -
                                   NativeVerticalScrollbar.getNativeScrollbarWidth() - 1);
                
                if (CSSUtils.getWidth( (svEl)) > 0) {
                    tree.setWidth(CSSUtils.getWidth(svEl) - 2);
                    scrollView.setWidth(CSSUtils.getWidth(svEl) -
                                        CSSUtils.getAddedBorderWidth(tree.getElement()) + "px");
                }

                if (CSSUtils.getHeight(svEl) > 0) {
                    scrollView.setHeight(CSSUtils.getHeight(svEl) - CSSUtils.getHeight(header) -
                                         CSSUtils.getAddedBorderHeight(tree.getElement()) + "px");

                }

            }
        });

    }

    /**
     * Returns the Header for this view
     * 
     * @return
     */
    protected Header getHeader() {
        return header;
    }

    /**
     * Returns the actual drawn row height on the screen
     */
    protected int getRowHeight() {
        return tree.rowHeight;
    }

    public void setCSS(TreeCSS css) {
        css.ensureInjected();

        for (int i = 0; i < flexTable.getRowCount(); i++ ) {
            if (flexTable.getRowFormatter().getStyleName(i).contains(this.css.Selection())) {
                flexTable.getRowFormatter().removeStyleName(i, this.css.Selection());
                flexTable.getRowFormatter().addStyleName(i, css.Selection());
            }
            for (int j = 0; j < flexTable.getCellCount(i); j++ ) {
                if (flexTable.getCellFormatter().getStyleName(i, j).contains(this.css.InputError())) {
                    flexTable.getCellFormatter().removeStyleName(i, j, this.css.InputError());
                    flexTable.getCellFormatter().addStyleName(i, j, css.InputError());
                }
                if (flexTable.getCellFormatter()
                             .getStyleName(i, j)
                             .contains(this.css.InputWarning())) {
                    flexTable.getCellFormatter().removeStyleName(i, j, this.css.InputWarning());
                    flexTable.getCellFormatter().addStyleName(i, j, css.InputWarning());
                }
            }
        }

        this.css = css;

        flexTable.setStyleName(css.Tree());

    }

    @Override
    protected int rowHeight() {
        return 0;
    }

    @Override
    FlexTable table() {
        return flexTable;
    }

    @Override
    ScrollPanel scrollView() {
        return scrollView;
    }

    @Override
    TableCSS css() {
        return css;
    }
    
    @Override
    void selectAll() {
        for(int i = 0; i < flexTable.getRowCount(); i++) {
            applySelectionStyle(i);
        }
    }

    @Override
    public void onResize() {
        super.onResize();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            
            @Override
            public void execute() {
                Element svEl = inner.getWidgetContainerElement(scrollView);

                if (CSSUtils.getWidth(svEl) > 0) {

                    scrollView.setWidth(CSSUtils.getWidth(svEl) -
                                        CSSUtils.getAddedBorderWidth(tree.getElement()) + "px");

                    if (CSSUtils.getHeight(inner) > 0) {
                        int height = CSSUtils.getHeight(inner) - CSSUtils.getHeight(header) -
                                        CSSUtils.getAddedBorderHeight(tree.getElement());
                        /*
                         * This check is here only for Unit Testing.  If not done Unit test on the
                         * table will fail here with assertion check from the widget.
                         */
                        if(height > 0)
                            scrollView.setHeight(height + "px");
                    }
                    
                    if (scrollView.getMaximumVerticalScrollPosition() == 0)
                        tree.setWidth(CSSUtils.getWidth(svEl) - 2);
                    else
                        tree.setWidth(CSSUtils.getWidth(svEl) -
                                       NativeVerticalScrollbar.getNativeScrollbarWidth() - 2);

                }
            }
        });
        

    }

    private void adjustForScroll(int before) {
        if (before == 0 && scrollView.getMaximumVerticalScrollPosition() > 0)
            tree.setWidth( (tree.getOffsetWidth() -
                             NativeVerticalScrollbar.getNativeScrollbarWidth() - 2) +
                           "px");
        else if (before > 0 && scrollView.getMaximumVerticalScrollPosition() == 0)
            tree.setWidth(tree.getOffsetWidth() - 2 + "px");
    }
    
    protected TreeGrid getTreeCell(Node node, int row, int col) {
        TreeGrid grid = null;
        int level;
        String image;
        Widget widget;
        AbsolutePanel lineDiv,line;

        image = node.getImage();
        level = tree.showRoot() ? node.getLevel() : node.getLevel() - 1;

        /**
         * Seems something has changed in GWT jars where we need to create a new TreeGrid everytime to ensure that the correct tree grid is displayed
         * May need to revisit if performance is compromised. 
         */
        //grid = (widget = flexTable.getWidget(row, col)) instanceof TreeGrid ? (TreeGrid)widget : new TreeGrid(level);
        
        grid = new TreeGrid(level);
        
        // Set the new tree grid into table when drawing first time otherwise re-use grid;
        //if(widget != grid)   
            flexTable.setWidget(row, col, grid);
         
        if ( !node.isLeaf()) {
            if (node.isOpen)
                grid.getCellFormatter().setStyleName(0, 1, css.treeOpenImage());
            else
                grid.getCellFormatter().setStyleName(0, 1, css.treeClosedImage());
        }

        //if at top level of tree set line cell to invisible;
        if(level == 0) {
            if(node.isLeaf())
                grid.getCellFormatter().setWidth(0, 0, "15px");
            else
                grid.getCellFormatter().setVisible(0, 0, false);
        } else {
            /*
             * Create div to draw lines and set cell to appropiate width
             */
            grid.getCellFormatter().setWidth(0, 0, (level*15)+"px");
            
            lineDiv = new AbsolutePanel();
            
            lineDiv.setWidth("100%");
            lineDiv.setHeight(tree.rowHeight+"px");
            
            grid.setWidget(0,0,lineDiv);
            
            int i = 0;
            
            //Loop for drawing lines
            while (i < level) {
                
                // Check if lines lower then current node level should be drawn
                if(i+1 < level) {
                    Node parent = node.getParent();
                    while(parent != tree.root && parent.getParent().getLastChild() == parent)
                        parent = parent.getParent();
                    if(parent.getParent() == tree.root) {
                        i++;
                        continue;
                    }
                }
                
                // Draw vertical for all levels up to current level
                if(i < level) {
                    line = new AbsolutePanel();
                
                    line.getElement().getStyle().setWidth(1, Unit.PX);
                    line.getElement().getStyle().setBackgroundColor("black");
                    if(node.getParent().getLastChild() == node && i + 1 == level) {
                        line.getElement().getStyle().setHeight(tree.rowHeight/2,Unit.PX);
                        line.getElement().getStyle().setTop(-((tree.rowHeight/2)-1),Unit.PX);
                    }else
                        line.getElement().getStyle().setHeight(tree.rowHeight, Unit.PX);
                
                    lineDiv.add(line,(i*15)+8,0);

                }
                
                //If loop is on current level then draw dash to node
                if(i+1 == level) {
                    line = new AbsolutePanel();
                    
                    line.getElement().getStyle().setWidth(5, Unit.PX);
                    line.getElement().getStyle().setBackgroundColor("black");
                    line.getElement().getStyle().setHeight(1, Unit.PX);
                    lineDiv.add(line,(i*15)+8,(tree.rowHeight/2));

                }
                
                i++;
            }
        }

        // Draw node image if one is set in the model
        if (image != null)
            grid.getCellFormatter().setStyleName(0, 2, image);
        else
            grid.getCellFormatter().setVisible(0,2,false);
        
        return grid;
    }
    
    protected class TreeGrid extends Grid {
        public TreeGrid(int level) {
            super(1,4);
            addStyleName(css.TreeCell());
            setWidth("100%");
            getCellFormatter().setWidth(0,3, "100%");
            addClickHandler(toggleHandler);
            setCellPadding(0);
            setCellSpacing(0);
        }
    }


}
