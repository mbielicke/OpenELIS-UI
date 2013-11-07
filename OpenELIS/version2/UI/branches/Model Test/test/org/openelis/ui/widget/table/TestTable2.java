package org.openelis.ui.widget.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class TestTable2 extends ResizeComposite {

    private static TestTable2UiBinder uiBinder = GWT.create(TestTable2UiBinder.class);

    interface TestTable2UiBinder extends UiBinder<Widget, TestTable2> {
    }
    
    @UiField
    Table test;
    
    @UiField
    LayoutPanel panel;
    
    public TestTable2() {
        initWidget(uiBinder.createAndBindUi(this));
    }

}
