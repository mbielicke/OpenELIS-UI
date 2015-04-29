package org.openelis.ui.widget.cell;

import org.openelis.ui.common.data.QueryData;

public interface QueryableCell extends CellRenderer<QueryData>, CellEditor<QueryData> {
	
	QueryData getQuery();

}
