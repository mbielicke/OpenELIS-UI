package org.openelis.ui.widget.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Table2 extends ResizeComposite {

    private static TestTable2UiBinder uiBinder = GWT.create(TestTable2UiBinder.class);

    @UiTemplate("TestTable2.ui.xml")
    interface TestTable2UiBinder extends UiBinder<Widget, Table2> {
    }
    
    @UiField
    Table test;
    
    @UiField
    LayoutPanel panel;
    
    public Table2() {
        initWidget(uiBinder.createAndBindUi(this));
    }

}
