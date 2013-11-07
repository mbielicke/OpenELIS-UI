package org.openelis.ui.screen;

import org.openelis.ui.common.FieldErrorException;
import org.openelis.ui.common.TableFieldErrorException;
import org.openelis.ui.event.DataChangeEvent;
import org.openelis.ui.event.StateChangeEvent;
import org.openelis.ui.screen.Screen.Validation;
import org.openelis.ui.screen.Screen.Validation.Status;
import org.openelis.ui.widget.Balloon;
import org.openelis.ui.widget.HasExceptions;
import org.openelis.ui.widget.Queryable;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

public abstract class ScreenHandler<T> implements ValueChangeHandler<T>, StateChangeEvent.Handler, DataChangeEvent.Handler {
	
	public Widget widget;
	
	public void onValueChange(ValueChangeEvent<T> event) {
		
	}

	public void onDataChange(DataChangeEvent event) {
	    if(widget instanceof Screen)
	        ((Screen)widget).fireDataChange();
		
	}

	public void onStateChange(StateChangeEvent event) {
	    if(widget instanceof Screen)
	        ((Screen)widget).setState(event.getState());

	}
	
	public Widget onTab(boolean shift) {
		return null;
	}

	
	public Object getQuery() {
	    if(widget instanceof Screen) 
	        return ((Screen)widget).getQueryFields();
	    else if(widget instanceof Queryable)
			if(((Queryable)widget).isQueryMode())
				return ((Queryable)widget).getQuery();
		
		return null;
	}
	
	public void isValid(Validation validation) {
	    HasExceptions he;
	
	    if(widget instanceof HasExceptions) {
	        he = (HasExceptions)widget;
	        if(he.hasExceptions()) {
	            if(Balloon.isWarning(he))
	                validation.setStatus(Validation.Status.WARNINGS);
	            else
	                validation.setStatus(Status.ERRORS);
	        }
	    }
	}
	
	public void showError(Exception ex) {
		TableFieldErrorException tableE;
		FieldErrorException      fieldE;
	
        if (ex instanceof TableFieldErrorException) {
            tableE = (TableFieldErrorException) ex;
            ((Table)widget).addException(tableE.getRowIndex(),((Table)widget).getColumnByName(tableE.getFieldName()),tableE);
        } else if (ex instanceof FieldErrorException) {
            fieldE = (FieldErrorException)ex;                                      
            ((HasExceptions)widget).addException(fieldE);
        }
	}
	
	public void clearError() {
	    if(widget instanceof Screen) 
	        ((Screen)widget).clearErrors();
		else if(widget instanceof HasExceptions) 
			((HasExceptions)widget).clearExceptions();
	}

}
