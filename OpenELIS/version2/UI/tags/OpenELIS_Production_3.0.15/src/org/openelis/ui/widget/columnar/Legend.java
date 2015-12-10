package org.openelis.ui.widget.columnar;

import java.util.ArrayList;

import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;

public class Legend extends Table {
    
    protected Columnar columnar;
    
    
    public Legend(Columnar clmnr) {
        super();
        setHeader(true);
        addColumn();
        this.columnar = clmnr;
        layout();
    }
    
    public void layout() {
        super.layout();
        
        if(columnar == null)
            return;
        
        ArrayList<Row> model = new ArrayList<Row>();

        for(int i = 0; i < columnar.getLineCount(); i++)
            model.add(new Row(columnar.getLineAt(i).label));
        
        setModel(model);

    }
}
