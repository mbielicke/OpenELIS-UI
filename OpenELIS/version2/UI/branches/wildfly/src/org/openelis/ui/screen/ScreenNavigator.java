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
package org.openelis.ui.screen;

import java.io.Serializable;
import java.util.ArrayList;

import org.openelis.ui.common.data.Query;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.BeforeCellEditedEvent;
import org.openelis.ui.widget.table.event.BeforeCellEditedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;

/**
 * This class is used by screens to manage paged queries.
 * 
 * To use this class, you must instantiate and override three methods that
 * provide query execution, screen data fetching, and converting a list of
 * returned data to a table model.
 * 
 * <code>
 * nav = new ScreenNavigator(screenDefinition) {
 *    public void executeQuery(final Query query) {
 *       service.callList(...);
 *    }
 *    public boolean fetch(RPC entry) {
 *       return fetchById((entry==null)?null:((IdNameVO)entry).getId());
 *    }
 *    public ArrayList<TableDataRow> getModel() {
 *       result = nav.getQueryResult();
 *       model = new ArrayList<TableDataRow>();
 *       if (result != null)
 *           for (IdNameVO entry : result)
 *       return model;
 *    }
 * }
 * </code>
 * 
 * For all screen queries, call nav.setQuery(query).
 */
public abstract class ScreenNavigator<T extends Serializable> {
    protected int          selection, oldPage;
    protected boolean      selectLastRow, loadMore, noSelection, enable;
    protected ArrayList<T> result;
    protected Query        query;
    protected Table        table;
    protected Button       nextPage, prevPage;

    public ScreenNavigator(Table table, Button next, Button prev) {
        oldPage = -1;
        selection = -1;
        this.table = table;
        nextPage = next;
        prevPage = prev;
        initialize();
    }
    
    public ScreenNavigator(Table table, Button loadMore) {
    	this(table,loadMore,null);
    	this.loadMore = true;
    }

    protected void initialize() {

        if (table != null) {
            table.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
                public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                    if (enable && selection != event.getItem())
                        select(event.getItem());
                }
            });
            table.addBeforeCellEditedHandler(new BeforeCellEditedHandler() {
                public void onBeforeCellEdited(BeforeCellEditedEvent event) {
                    event.cancel();
                }
            });
        }

        if (nextPage != null) {
            nextPage.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                	noSelection = true;
                    setPage(query.getPage() + 1);
                }
            });
        }

        if (prevPage != null) {
            prevPage.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    setPage(query.getPage() - 1);
                }
            });
        }
    }

    /**
     * Set the query parameters and starts the query execution.
     */
    public void setQuery(Query query) {
        oldPage = -1;

        this.query = query;
        executeQuery(query);
    }

    public Query getQuery() {
    	return query;
    }
    
    /**
     * Set the query result list.
     * 
     * @param result
     *        should be null to indicate no records were found.
     */
    @SuppressWarnings("rawtypes")
    public void setQueryResult(ArrayList result) {
        int row;

        //
        // if next page failed, reset the query page # to the old page #
        //
        if (result == null && oldPage != -1) {
            query.setPage(oldPage);
            oldPage = -1;
            enable(true);
            return;
        }

        row = 0;        
        this.result = result;
        
        if (table != null)
            table.setModel(getModel());
        
        if(!noSelection) {
        	// we are going back a page and we want to select the last row in
        	// in the list
        	if (result != null && selectLastRow) {
        		row = result.size() - 1;
        		selectLastRow = false;
        	}

        	select(row);
        }
        noSelection = false;
        enable(true);
    }

    public ArrayList<T> getQueryResult() {
        return result;
    }

    public void setNoSelection(boolean noSelection) {
    	this.noSelection = noSelection;
    }
    
    /**
     * enable the table and next previous page buttons
     */
    public void enable(boolean enable) {
        if (nextPage != null)
            nextPage.setEnabled(enable && result != null);
        if (prevPage != null)
            prevPage.setEnabled(enable && result != null);
        this.enable = enable;
        
        if(table != null)
            table.setEnabled(enable);
    }

    /**
     * Selects the next element in the result list. If at the end of result
     * list, attempt is made to fetch the next page.
     */
    public void next() {
        if (enable) {
            if (selection != -1)
                select(selection + 1);
        }
    }

    /**
     * Selects the previous element in the result list. If the current selection
     * is at the beginning of the list, attempt is made to fetch the previous
     * page.
     */
    public void previous() {
        if (enable) {
            if (selection != -1)
                select(selection - 1);
        }
    }

    /**
     * This method is called when the screen needs to update its data to
     * represent the selection. A null RPC parameter tells the screen to clear
     * its data.
     */
    public abstract boolean fetch(T entry);

    /**
     * This method is called when a new query needs to be executed. The screen
     * implementation will need to call the setQueryResult method once the
     * result is available.
     */
    public abstract void executeQuery(Query query);

    /**
     * Returns the table data model representing the query result.
     * 
     * @return model that is used to set the atoz table; This model cannot be
     *         null.
     */
    public abstract ArrayList<Item<Integer>> getModel();

    /**
     * Select a row within the result set
     */
    protected void select(int row) {
        if (result == null || result.size() == 0) {
            try {
                fetch(null);
            } catch (Exception e) {
                // this should not happen since we are telling them
                // to clear their screen
                e.printStackTrace();
            } finally {
                selection = -1;
            }
            if (table != null)
                table.unselectRowAt(selection);
            if (nextPage != null)
                nextPage.setEnabled(false);
            if (prevPage != null)
                prevPage.setEnabled(false);
        } else if (row > result.size() - 1) {
            setPage(query.getPage() + 1);
        } else if (row < 0) {
        	selectLastRow = true;
            setPage(query.getPage() - 1);
        } else {
            if (fetch(result.get(row))) {
                selection = row;
                if (table != null)
                    table.selectRowAt(selection);
                if (nextPage != null)
                    nextPage.setEnabled(true);
                if (prevPage != null)
                    prevPage.setEnabled(true);
            } else {
                clearSelection();
            }
        }
    }
    
    public void clearSelection() {
        selection = -1;
        if (table != null)
            table.unselectRowAt(selection);
    }

    /*
     * Sets the query page # for going forward/back and executes the query.
     * SetQueryResult resets the page # if we can't go forward.
     */
    protected void setPage(int page) {
        if (page < 0)
            return;

        enable(false);

        oldPage = query.getPage();
        query.setPage(page);

        executeQuery(query);
    }
}
