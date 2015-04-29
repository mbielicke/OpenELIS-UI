package org.openelis.ui.widget.cell;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.resources.DropTableCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Grid;

public class CellSelection extends Cell<String> {
	
	private DropTableCSS css = UIResources.INSTANCE.dropTable();
	
	public CellSelection() {
		css.ensureInjected();
	}

	@Override
	public SafeHtml asHtml(String value) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        
        Grid editor = GWT.create(Grid.class);
        editor.resize(1, 2);
        editor.setCellPadding(0);
        editor.setCellSpacing(0);
        editor.getCellFormatter().setStyleName(0, 0, css.SelectionCell());
                
        editor.setText(0, 1, DataBaseUtil.toString(value));
        
        //Style the editor to not wrap words
        editor.setWidth("100%");
        editor.getCellFormatter().setWidth(0, 0, "20px");
        editor.getCellFormatter().setWidth(0, 1, "100%");
        editor.getElement().getStyle().setTableLayout(TableLayout.FIXED);
        editor.getCellFormatter().getElement(0, 1).getStyle().setOverflow(Overflow.HIDDEN);
        editor.getCellFormatter().getElement(0, 1).getStyle().setTextOverflow(TextOverflow.ELLIPSIS);
        editor.getCellFormatter().getElement(0, 1).getStyle().setWhiteSpace(WhiteSpace.PRE);
        
        builder.appendHtmlConstant(editor.getElement().getString());
        
        return builder.toSafeHtml();
	}

	@Override
	public String asString(String value) {
		return value;
	}

}
