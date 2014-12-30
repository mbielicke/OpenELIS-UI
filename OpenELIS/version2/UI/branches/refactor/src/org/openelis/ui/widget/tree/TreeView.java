package org.openelis.ui.widget.tree;

import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.TreeCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.table.CellRenderer;
import org.openelis.ui.widget.table.Controller;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;

public class TreeView extends org.openelis.ui.widget.table.StaticView {
	
	Tree tree;

	TreeCSS css;
	
	public TreeView(Controller controller) {
		super(controller);
		css = UIResources.INSTANCE.tree();
		css.ensureInjected();
		tree = (Tree)controller;
	}
	
	@Override
	protected void bulkRenderCell(SafeHtmlBuilder builder, int row, int col) {
		CellRenderer renderer;
		
		if (col < tree.getNodeDefinition(tree.getNodeAt(row).getType()).size()) {
            renderer = tree.getCellRenderer(row, col);
            if(col == 0) {
                Grid treeGrid = getTreeCell(tree.getNodeAt(row), row, col);
                renderer.render(treeGrid, 0, treeGrid.getCellCount(0) - 1, tree.getValueAt(row,col));
                builder.appendHtmlConstant("<td>");
                builder.appendHtmlConstant(treeGrid.getElement().getString());
                builder.appendHtmlConstant("</td>");
            }else
                builder.append(renderer.bulkRender(tree.getValueAt(row,col)));
        } else {
            builder.appendHtmlConstant("<td/>");
        }                
	}
	
	@Override
	protected void renderCell(int r, int c) {
        CellRenderer renderer;
        HTMLTable table;
        int row, col, level;
        Node node;
        
        node = tree.getNodeAt(r);
        level = tree.showRoot() ? node.getLevel() : node.getLevel() - 1;

        if (c < tree.getNodeDefinition(node.getType()).size())
            renderer = tree.getCellRenderer(r, c);
        else {
            grid.setText(r, c, "");
            grid.getCellFormatter().removeStyleName(r, c, css.InputError());
            return;
        }
        
        table = grid;
        row = r;
        col = c;
        
        if(c == 0) {
            if(grid.getCellCount(r) == 0) {
                SafeHtmlBuilder tb = new SafeHtmlBuilder();
                Grid treeGrid = getTreeCell(node, r, c);
                renderer.render(treeGrid, 0, treeGrid.getCellCount(0) - 1, tree.getValueAt(r,c));
                tb.appendHtmlConstant("<td>");
                tb.appendHtmlConstant(treeGrid.getElement().getString());
                tb.appendHtmlConstant("</td>");
                grid.setHTML(r, c, treeGrid.getElement().getString());
            } else {
                if ( !node.isLeaf()) {
                    if(level == 0)
                        grid.getCellFormatter().getElement(r, c).getElementsByTagName("td").getItem(0).getStyle().setDisplay(Display.NONE);
                    if (node.isOpen)
                        grid.getCellFormatter().getElement(r,c).getElementsByTagName("td").getItem(1).setClassName(css.treeOpenImage());
                    else
                        grid.getCellFormatter().getElement(r,c).getElementsByTagName("td").getItem(1).setClassName(css.treeClosedImage());
                }
                if(node.getImage() != null) {
                	grid.getCellFormatter().getElement(r,c).getElementsByTagName("td").getItem(2).setClassName(node.getImage());
                	grid.getCellFormatter().getElement(r,c).getElementsByTagName("td").getItem(2).getStyle().setProperty("display","table-cell");
                }else
                	grid.getCellFormatter().getElement(r,c).getElementsByTagName("td").getItem(2).getStyle().setProperty("display","none");
                grid.getCellFormatter().getElement(r,c).getElementsByTagName("td").getItem(3).setInnerSafeHtml(renderer.bulkRender(tree.getValueAt(r,c)));                
            }
        }else {
            if (tree.getQueryMode())
                renderer.renderQuery(table, row, col, (QueryData)tree.getValueAt(r, c));
            else
                renderer.render(table, row, col, tree.getValueAt(r, c));
        }
        
        if (tree.hasExceptions(r, c)) {
        	renderCellException(r,c);
        } else {
        	clearCellException(r,c);
        }
	}

    protected TreeGrid getTreeCell(Node node, int row, int col) {
        TreeGrid grid = null;
        int level;
        String image;
        Widget widget;
        AbsolutePanel lineDiv,line;

        image = node.getImage();
        level = ((Tree)table).showRoot() ? node.getLevel() : node.getLevel() - 1;

        /**
         * Seems something has changed in GWT jars where we need to create a new TreeGrid everytime to ensure that the correct tree grid is displayed
         * May need to revisit if performance is compromised. 
         */
        //grid = (widget = flexTable.getWidget(row, col)) instanceof TreeGrid ? (TreeGrid)widget : new TreeGrid(level);
        
        grid = new TreeGrid(level);
        
        // Set the new tree grid into table when drawing first time otherwise re-use grid;
        //if(widget != grid)   
            grid.setWidget(row, col, grid);
         
        if ( !node.isLeaf()) {
            if (node.isOpen)
                grid.getCellFormatter().setStyleName(0, 1, css.treeOpenImage());
            else
                grid.getCellFormatter().setStyleName(0, 1, css.treeClosedImage());
        }

        //if at top level of tree set line cell to invisible;
        if(level == 0) {
            if(node.isLeaf()) {
                grid.getCellFormatter().setWidth(0, 0, "15px");
            }else
                grid.getCellFormatter().setVisible(0, 0, false);
        } else {
            /*
             * Create div to draw lines and set cell to appropiate width
             */
            grid.getCellFormatter().setWidth(0, 0, (level*15)+"px");
            
            lineDiv = new AbsolutePanel();
            
            lineDiv.setWidth("100%");
            lineDiv.setHeight(tree.getRowHeight()+"px");
            
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
                        line.getElement().getStyle().setHeight(tree.getRowHeight()/2,Unit.PX);
                        line.getElement().getStyle().setTop(-((tree.getRowHeight()/2)-1),Unit.PX);
                    }else
                        line.getElement().getStyle().setHeight(tree.getRowHeight(), Unit.PX);
                
                    lineDiv.add(line,(i*15)+8,0);

                }
                
                //If loop is on current level then draw dash to node
                if(i+1 == level) {
                    line = new AbsolutePanel();
                    
                    line.getElement().getStyle().setWidth(5, Unit.PX);
                    line.getElement().getStyle().setBackgroundColor("black");
                    line.getElement().getStyle().setHeight(1, Unit.PX);
                    lineDiv.add(line,(i*15)+8,(tree.getRowHeight()/2));

                }
                
                i++;
            }
        }

        // Draw node image if one is set in the model
        if (image != null){
            grid.getCellFormatter().setStyleName(0, 2, image);
            grid.getCellFormatter().getElement(0, 2).getStyle().setProperty("display", "table-cell");
        }else
            grid.getCellFormatter().setVisible(0,2,false);
        
        return grid;
    }
    
    protected class TreeGrid extends Grid {
        public TreeGrid(int level) {
            super(1,4);
            addStyleName(css.TreeCell());
            setWidth("100%");
            getCellFormatter().setWidth(0,3, "100%");
            setCellPadding(0);
            setCellSpacing(0);
        }
        
        public TreeGrid(String html) {
            getElement().setInnerHTML(html);
        }
    }

}
