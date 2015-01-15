package org.openelis.ui.widget.table;

import java.util.ArrayList;
import java.util.Iterator;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.resources.DropTableCSS;
import org.openelis.ui.resources.TableCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Label;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class SelectionCell implements CellRenderer,IsWidget, HasWidgets.ForIsWidget {
    private DropTableCSS css = UIResources.INSTANCE.dropTable();
    private Column col;
    
    public SelectionCell() {
        css.ensureInjected();
    }
    
    @Override
    public String display(Object value) {
       return DataBaseUtil.toString(value);
    }

    @Override
    public void render(HTMLTable table, int row, int col, Object value) {
        Grid editor = (Grid)table.getWidget(row, col);
        
        if(editor == null) {
            editor = new Grid(1,2);
            editor.setCellPadding(0);
            editor.setCellSpacing(0);
            editor.getCellFormatter().setStyleName(0, 0, css.SelectionCell());
            table.setWidget(row, col, editor);
        }
        
        editor.setText(0, 1, display(value));
        
        //Style the editor to not wrap words
        editor.setWidth("100%");
        editor.getCellFormatter().setWidth(0, 0, "20px");
        editor.getCellFormatter().setWidth(0, 1, "100%");
        editor.getElement().getStyle().setTableLayout(TableLayout.FIXED);
        editor.getCellFormatter().getElement(0, 1).getStyle().setOverflow(Overflow.HIDDEN);
        editor.getCellFormatter().getElement(0, 1).getStyle().setTextOverflow(TextOverflow.ELLIPSIS);
        editor.getCellFormatter().getElement(0, 1).getStyle().setWhiteSpace(WhiteSpace.PRE);
    }
    
    public SafeHtml bulkRender(Object value) {
        
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        Grid editor = GWT.create(Grid.class);
        editor.resize(1, 2);
        editor.setCellPadding(0);
        editor.setCellSpacing(0);
        editor.getCellFormatter().setStyleName(0, 0, css.SelectionCell());
                
        editor.setText(0, 1, display(value));
        
        //Style the editor to not wrap words
        editor.setWidth("100%");
        editor.getCellFormatter().setWidth(0, 0, "20px");
        editor.getCellFormatter().setWidth(0, 1, "100%");
        editor.getElement().getStyle().setTableLayout(TableLayout.FIXED);
        editor.getCellFormatter().getElement(0, 1).getStyle().setOverflow(Overflow.HIDDEN);
        editor.getCellFormatter().getElement(0, 1).getStyle().setTextOverflow(TextOverflow.ELLIPSIS);
        editor.getCellFormatter().getElement(0, 1).getStyle().setWhiteSpace(WhiteSpace.PRE);
        
        builder.appendHtmlConstant("<td>"+editor.getElement().getString()+"</td>");
        
        return builder.toSafeHtml();
    }

    @Override
    public void renderQuery(HTMLTable table, int row, int col, QueryData qd) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ArrayList<Exception> validate(Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void add(Widget w) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Iterator<Widget> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(Widget w) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void add(IsWidget w) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean remove(IsWidget w) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Widget asWidget() {
        return new Label<String>("");
    }

    @Override
    public void setColumn(ColumnInt col) {
        this.col = (Column)col;
        
    }

}
