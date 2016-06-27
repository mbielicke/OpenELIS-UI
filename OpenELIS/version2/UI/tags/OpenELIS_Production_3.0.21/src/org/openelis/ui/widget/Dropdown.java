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
package org.openelis.ui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.openelis.ui.common.Exceptions;
import org.openelis.ui.common.Util;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.messages.UIMessages;
import org.openelis.ui.resources.DropdownCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Balloon.Placement;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.SelectionCell;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.CellClickedEvent;
import org.openelis.ui.widget.table.event.CellClickedHandler;
import org.openelis.ui.widget.table.event.UnselectionEvent;
import org.openelis.ui.widget.table.event.UnselectionHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.NativeHorizontalScrollbar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used by OpenELIS Screens to display and input values in forms
 * and in table cells as a Drop down list selector. This class exteds TextBox
 * which implements the ScreenWidgetInt and we override this implementation
 * where needed.
 * 
 * @param <T>
 */
public class Dropdown<T> extends Composite implements ScreenWidgetInt, Queryable, Focusable,
                                          HasBlurHandlers, HasFocusHandlers, HasValue<T>,
                                          HasHelper<T>, HasExceptions, HasBalloon {

    @UiTemplate("Select.ui.xml")
    interface DropdownUiBinder extends UiBinder<Widget, Dropdown> {
    };

    public static final DropdownUiBinder uiBinder     = GWT.create(DropdownUiBinder.class);

    /**
     * Used for Dropdown display
     */

    protected AbsolutePanel              image;
    @UiField
    protected LayoutPanel                display;
    protected Grid                       multiHeader;
    protected VerticalPanel              vp;
    @UiField
    protected Button                     button;
    protected Button                     checkAll, uncheckAll, close;
    protected Table                      table;
    protected PopupPanel                 popup;
    protected int                        cellHeight   = 19, itemCount = 10, width, maxDisplay = 3;
    protected boolean                    required, queryMode, showingOptions, enabled;
    protected T                          value;
    protected String                     dropHeight   = "150px", dropWidth;
    protected ArrayList<Item<T>>         model;

    @UiField
    protected TextBase                   textbox;

    /**
     * Sorted list of display values for search
     */
    protected ArrayList<SearchPair>      searchText;
    protected SearchPair                 searchPair;
    protected String                     searchString = "";

    /**
     * Exceptions list
     */
    protected Exceptions                 exceptions;
    protected Balloon.Options            options;

    /**
     * HashMap to set selections by key;
     */
    protected HashMap<T, Integer>        keyHash;

    /**
     * Instance of the Renderer interface. Initially set to the DefaultRenderer
     * implementation.
     */
    protected Renderer                   renderer     = new DefaultRenderer();

    /**
     * Helper was added for compatibility when widget is used in a table. Never
     * used in the widget itself
     */
    protected WidgetHelper<T>            helper;

    protected DropdownCSS                css;

    private Dropdown<T>                  source       = this;
    
    protected KeyboardHandler keyHandler = new KeyboardHandler();

    /**
     * Default no-arg constructor
     */
    public Dropdown() {
        init();
    }

    /**
     * Creates the display for the Dropdown and sets it as the Composite widget.
     * Sets all handlers for user interaction.
     */
    public void init() {

        initWidget(uiBinder.createAndBindUi(this));

        /*
         * Image must be in a div instead of adding the style to cell itself to
         * display correctly
         */
        // image = new AbsolutePanel();
        // button.setWidget(image);

        /*
         * Set the focus style when the Focus event is fired Externally
         */
        addFocusHandler(new FocusHandler() {
            public void onFocus(FocusEvent event) {
                if (isEnabled()) {
                    display.addStyleName(css.Focus());
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            textbox.selectAll();
                        }
                    });
                    
                }
            }
        });

        /*
         * Removes the focus style when the Blue event is fires externally
         */
        addBlurHandler(new BlurHandler() {
            public void onBlur(BlurEvent event) {
                display.removeStyleName(css.Focus());

                if ( !queryMode)
                    finishEditing();
            }
        });

        keyHandler = new KeyboardHandler();
        
        addHandler(keyHandler, KeyDownEvent.getType());
        addHandler(keyHandler, KeyPressEvent.getType());

        exceptions = new Exceptions();

        setCSS(UIResources.INSTANCE.dropdown());
        
        table = GWT.create(Table.class);
        table.addColumn(new Column.Builder(10).renderer(new SelectionCell()).build());
        setPopupContext(table);
        
        setWidth("150px");
        
        setHelper((WidgetHelper)new StringHelper());
    }

    
    @UiHandler("textbox")
    protected void onFocus(FocusEvent event) {
       FocusEvent.fireNativeEvent(event.getNativeEvent(), this);
    }

    @UiHandler("textbox")
    protected void onBlur(BlurEvent event) {
        if ( !showingOptions && isEnabled())
            BlurEvent.fireNativeEvent(event.getNativeEvent(), this);
    }
    

    @UiHandler("button")
    public void onClick(ClickEvent event) {
        if (isEnabled())
            showPopup();
    }

    @UiHandler("button")
    public void onMouseDown(MouseDownEvent event) {
        if (isEnabled())
            showingOptions = true;
    }

    /**
     * This method will display the table set as the PopupContext for this
     * Select. Will create the Popup and initialize the first time if null. We
     * also call scrollToVisible() on the table to make sure the selected value
     * is in the current table view.
     */
    protected void showPopup() {
        /* Set to true for button mouse down hack */
        showingOptions = true;
        final LayoutPanel layout = GWT.create(LayoutPanel.class);

        /* create a popup instance first time it is used */
        if (popup == null) {
            popup = GWT.create(PopupPanel.class);
            popup.setAutoHideEnabled(true);
            popup.setStyleName(css.Popup());
            
            //table.setModel(model);

            /* Draw popup for Multiselect when set */
            if (queryMode) {
                uncheckAll = new Button(css.Unchecked(), getMessages().drop_uncheck());
                checkAll = new Button(css.Checked(), getMessages().drop_check());

                uncheckAll.setCss(UIResources.INSTANCE.button());
                checkAll.setCss(UIResources.INSTANCE.button());

                multiHeader = new Grid(1, 2);
                multiHeader.setCellSpacing(0);
                multiHeader.setCellPadding(0);
                multiHeader.setWidget(0, 0, checkAll);
                multiHeader.setWidget(0, 1, uncheckAll);

                // multiHeader.getCellFormatter().setHorizontalAlignment(0, 1,
                // HasAlignment.ALIGN_CENTER);
                // multiHeader.getCellFormatter().setHorizontalAlignment(0, 1,
                // HasAlignment.ALIGN_RIGHT);
                multiHeader.setWidth("100%");
                vp = new VerticalPanel();
                vp.add(multiHeader);
                layout.add(table);

                vp.add(layout);
                popup.setWidget(vp);

                /* Handler to select All items when checked */
                checkAll.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        table.selectAll();
                        setDisplay();
                    }
                });
                checkAll.setEnabled(true);

                /* Handler to unselect All items when Checked */
                uncheckAll.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        table.unselectAll();
                        setDisplay();
                    }
                });
                uncheckAll.setEnabled(true);

            } else {

                layout.add(table);

                popup.setWidget(layout);

            }

            popup.setPreviewingAllNativeEvents(false);

            /*
             * Handler for closing of popup to set focus back to false and reset
             * showingOptions
             */
            popup.addCloseHandler(new CloseHandler<PopupPanel>() {
                public void onClose(CloseEvent<PopupPanel> event) {
                    setFocus(true);
                    showingOptions = false;
                }
            });

        }

        /**
         * Draw and show outside of viewable so table will size correctly and
         * and showRelative will work when close to browser border.
         */
        popup.setPopupPosition( -1000, -1000);
        popup.show();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                int modelHeight = model.size() * cellHeight;

                if (table.hasHeader())
                    modelHeight += 19;

                int width = dropWidth == null ? getOffsetWidth() : Util.stripUnits(dropWidth);

                if (table.getTotalColumnWidth() > width)
                    modelHeight += NativeHorizontalScrollbar.getNativeScrollbarHeight() + 1;

                if (queryMode) {
                    if (multiHeader.getOffsetWidth() > width)
                        width = multiHeader.getOffsetWidth();
                    layout.setSize(width + "px",
                                   Util.stripUnits(dropHeight) > modelHeight ? modelHeight + "px"
                                                                            : dropHeight);

                }

                popup.setSize(width + "px",
                              Util.stripUnits(dropHeight) > modelHeight ? modelHeight + "px"
                                                                       : dropHeight);
                table.onResize();
                popup.showRelativeTo(source);

                /*
                 * Scroll if needed to make selection visible
                 */
                if (getSelectedIndex() > 0)
                    table.scrollToVisible(getSelectedIndex());

            }
        });
    }

    /**
     * Method called by various event handlers to set the displayed text for the
     * selected row in the table without firing value change events to the end
     * user.
     */
    protected void setDisplay() {
        StringBuffer sb;

        sb = new StringBuffer();

        if (table.isAnyRowSelected()) {
            if (table.getSelectedRows().length <= maxDisplay) {
                for (Integer sel : table.getSelectedRows()) {
                    if (sb.length() > 0)
                        sb.append(", ");
                    sb.append(renderer.getDisplay(table.getRowAt(sel)));
                }
            } else {
                sb = new StringBuffer().append(table.getSelectedRows().length + " " +
                                               getMessages().drop_optionsSelected());
            }
        }

        textbox.setText(sb.toString());
    }

    @Override
    public void setWidth(String w) {
        width = Util.stripUnits(w);
        
        if (display != null)
            display.setWidth(width + "px");

        if (table != null && dropWidth == null)
            table.setWidth((width)+ "px");
    }

    public int getWidth() {
        return width;
    }

    @Override
    public void setHeight(String height) {
        display.setHeight(height);
        button.setHeight(height);
    }

    public void setDropHeight(String height) {
        dropHeight = height;
    }

    public void setDropWidth(String width) {
        dropWidth = width;
        
        if (table != null && dropWidth != null)
            table.setWidth((dropWidth)+ "px");
    }

    public void setCase(TextBase.Case textCase) {
        textbox.setCase(textCase);
    }

    /**
     * This method sets up the key hash which is used to search for the correct
     * index to select when setting value by key.
     * 
     * @param model
     */
    protected void createKeyHash(ArrayList<Item<T>> model) {
        keyHash = new HashMap<T, Integer>();

        if (model != null)
            for (int i = 0; i < model.size(); i++ )
                keyHash.put(model.get(i).key, i);
    }

    // ******* End User Dropdown methods ***********************
    /**
     * Allows the end user to override the DefaultRenderer with a custom
     * Renderer.
     * 
     * @param renderer
     */
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Sets the Table definition to be used as the PopupContext for this
     * Dropdown.
     * 
     */
    @UiChild(limit = 1, tagname = "popup")
    public void setPopupContext(Table tableDef) {
        this.table = tableDef;
        table.setCSS(UIResources.INSTANCE.dropTable());
        table.addStyleName(UIResources.INSTANCE.dropTable().Single());

        // table.setFixScrollbar(false);
        table.setRowHeight(16);
        table.setEnabled(true);
        table.setCtrlKeyDefault(true);

        /*
         * This handler will will cancel the selection if the item has been
         * disabled.
         */
        table.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @SuppressWarnings("rawtypes")
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {

                if ( ! ((Item)table.getModel().get(event.getItem())).isEnabled())
                    event.cancel();
            }
        });

        /*
         * This handler will catch the events when the user clicks on rows in
         * the table.
         */
        table.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                setDisplay();
            }
        });

        table.addUnselectionHandler(new UnselectionHandler<Integer>() {

            @Override
            public void onUnselection(UnselectionEvent<Integer> event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                    @Override
                    public void execute() {
                        setDisplay();

                    }
                });
            }
        });

        table.addCellClickedHandler(new CellClickedHandler() {
            public void onCellClicked(CellClickedEvent event) {
                /*
                 * Keep options up if multiple selection occurs
                 */
                if (queryMode)
                    return;

                popup.hide();
            }
        });
    }

    /**
     * Method Returns the Table widget used to display the Select Options
     */
    // public Table getPopupContext() {
    // return table;
    // }

    /**
     * Sets the number of visible rows in the Table that shows the options
     * 
     * @param itemCount
     */
    public void setVisibleItems(int itemCount) {
        this.itemCount = itemCount;

        if (table != null)
            table.setVisibleRows(itemCount);
    }

    /**
     * Sets the data model for the PopupContext of this widget.
     * 
     * @param model
     */
    public void setModel(ArrayList<Item<T>> model) {

        this.model = model;
        
        table.setModel(model);
        //popup = null;

        createKeyHash(model);

        searchText = null;

        setValue(null);
        setDisplay();

    }

    /**
     * Returns the model used in the table
     * 
     * @return
     */
    public ArrayList<Item<T>> getModel() {
        return model;
    }

    /**
     * Sets the selected row using its overall index in the model. This method
     * will also not cause a ValueChangeEvent to be fired.
     * 
     * @param index
     */
    public void setSelectedIndex(int index) {
        if (index > -1) {
            setValue(getModel().get(index).key);
        } else {
            setValue(null);
            table.unselectAll();
        }
    }

    /**
     * Returns the overall index of the selected row in the model
     * 
     * @return
     */
    public int getSelectedIndex() {
        return table.getSelectedRow();
    }

    /**
     * Returns the currently selected TableDataRow in the Table
     * 
     * @return
     */
    public Item<T> getSelectedItem() {
        if (table.isAnyRowSelected())
            return getModel().get(table.getSelectedRow());
        return null;
    }

    /**
     * Returns an ArrayList<TableDataRow> of selected rows in the table.
     * 
     * @return
     */
    /*
     * public ArrayList<Item<T>> getSelectedItems() { ArrayList<Item<T>> items =
     * null;
     * 
     * 
     * items = new ArrayList<Item<T>>();
     * 
     * for(int i = 0; i < table.getSelectedRows().length; i++)
     * items.add(getModel().get(table.getSelectedRows()[i]));
     * 
     * return items.size() > 0 ? items : null; }
     */

    /**
     * Returns the string currently displayed in the textbox portion of the
     * widget.
     * 
     * @return
     */
    public String getDisplay() {
        return textbox.getText();
    }

    // ********** Methods Overridden in the ScreenWidetInt ****************

    /**
     * Method overridden from TextBox to enable the button and table as well as
     * the textbox.
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        textbox.setEnabled(enabled);
        button.setEnabled(enabled);

        if (table != null)
            table.setEnabled(enabled);

        if (enabled) {
            sinkEvents(Event.ONKEYDOWN | Event.ONKEYPRESS);
        }else{
            unsinkEvents(Event.ONKEYDOWN | Event.ONKEYPRESS);
        }

    }

    // ********* Implementation of HasValue *************************
    /**
     * Overridden method to set the T value of this widget. Will fire a value
     * change event if fireEvents is true and the value is changed from its
     * current value
     */
    @Override
    public void setValue(T value, boolean fireEvents) {
        Integer index;

        if ( !Util.isDifferent(this.value == null ? null : this.value, value))
            return;

        table.selectRowAt( (index = keyHash.get(value)) != null ? index : -1);

        searchString = "";

        setDisplay();

        this.value = value;

        if (fireEvents)
            ValueChangeEvent.fire(this, value);

    };

    /**
     * Method is called when the dropdown is blurred. It will validate and set
     * the value of the widget.
     */
    @Override
    public void finishEditing() {
        if(queryMode)  
            return;
        
        if (table.isAnyRowSelected())
            setValue(getModel().get(table.getSelectedRow()).key, true);
        else
            setValue(null, true);

        clearValidateExceptions();

        if (required && value == null)
            addValidateException(new Exception(getMessages().exc_fieldRequired()));

        Balloon.checkExceptionHandlers(this);
    }

    /**
     * Method used to return the values of the Selection
     */
    public T getValue() {
        return value;
    }

    /**
     * Method used to set the value of the Selection
     * 
     * @param value
     */
    public void setValue(T value) {
        setValue(value, false);
    }

    /**
     * Method used to set the max number of comma delimited options to show
     * 
     * @param maxDisplay
     */
    public void setMaxDisplay(int maxDisplay) {
        this.maxDisplay = maxDisplay;
    }

    // ************ Implementation of Queryable ***********************
    /**
     * Method for putting the Select into query mode.
     */
    @Override
    public void setQueryMode(boolean query) {
        if (queryMode != query) {
        	popup = null;
        	value = null;
        	textbox.setText("");
        	table.unselectAll();
        }

        queryMode = query;
        
        if (queryMode) {
            /*
             * If switching to multi select add checkbox column at position 0
             * Table will add the value to the model correctly
             */
            table.removeStyleName(UIResources.INSTANCE.dropTable().Single());
            textbox.setReadOnly(true);
            table.setCtrlKeyDefault(true);
            table.setAllowMultipleSelection(true);
        } else {
            /*
             * Remove Checkbox column if switching to single select The table
             * will remove the column from the model
             */
            table.addStyleName(UIResources.INSTANCE.dropTable().Single());
            textbox.setReadOnly(false);
            table.setAllowMultipleSelection(false);
        }
        
    }

    /**
     * Method for creating a QueryData object for this widget
     */
    @Override
    public Object getQuery() {
        QueryData qd;
        StringBuffer sb;
        ArrayList<T> values;

        values = new ArrayList<T>();
        for (Integer item : table.getSelectedRows())
            values.add( ((Item<T>)table.getRowAt(item)).getKey());

        /*
         * Return null if nothing selected
         */
        if (values.isEmpty() || (values.size() == 1 && values.get(0) == null))
            return null;

        qd = new QueryData();

        /*
         * Create the query from the selected values
         */
        sb = new StringBuffer();

        for (int i = 0; i < values.size(); i++ ) {
            if (i > 0)
                sb.append(" | ");
            sb.append(values.get(i));
            if (values.get(i) != null)

                /*
                 * Since there is no helper we need to do an instance check here
                 */
                if (values.get(i) instanceof Integer)
                    qd.setType(QueryData.Type.INTEGER);
                else
                    qd.setType(QueryData.Type.STRING);

        }

        qd.setQuery(sb.toString());

        return qd;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setQuery(QueryData qd) {
        String[] params;
        T key;

        if ( !queryMode)
            return;

        table.unselectAll();

        setDisplay();

        if (qd == null)
            return;

        if (qd.getQuery() != null && !qd.getQuery().equals("")) {
            params = qd.getQuery().split(" \\| ");
            for (int i = 0; i < params.length; i++ ) {
                if (qd.getType() == QueryData.Type.INTEGER)
                    key = (T)new Integer(params[i]);
                else
                    key = (T)params[i];

                table.selectRowAt(keyHash.get(key));
            }
        }

        setDisplay();

    }
    
    public void disableItem(T key) {
    	setItemEnabled(key,false);
    }
    
    public void enableItem(T key) {
    	setItemEnabled(key, true);
    }
    
    public void setItemEnabled(T key, boolean enabled) {
    	int index = -1;
    	Item<T> item;
    	
    	if (keyHash.containsKey(key)) {
    		index = keyHash.get(key);
    		item = getModel().get(index);
    		if (item.enabled != enabled) {
    			item.setEnabled(enabled);
    			table.setRowAt(index, item);
    		}
    	}
    }
    
   

    // *************** Search methods ******************

    /**
     * This method will perform a binary search on a sorted version of the the
     * Dropdown model
     */
    protected int findIndexByTextValue(String textValue) {
        int index = -1;
        /*
         * Force to Upper case for matching
         */
        textValue = textValue.toUpperCase();

        if (textValue.equals(""))
            return -1;

        /*
         * For first time search we need to setup the sorted array list for the
         * binary search since the display model may not in order.
         */
        if (searchText == null) {
            searchText = new ArrayList<SearchPair>();
            for (int i = 0; i < getModel().size(); i++ ) {
                if (getModel().get(i).enabled)
                    searchText.add(new SearchPair(i, renderer.getDisplay(getModel().get(i))
                                                             .toUpperCase()));
            }
            Collections.sort(searchText);
        }

        index = Collections.binarySearch(searchText,
                                         new SearchPair( -1, textValue),
                                         new MatchComparator());

        if (index < 0)
            return -1;
        else {
            // we need to do a linear search backwards to find the first entry
            // that partial matches our search
            index-- ;
            while (index >= 0 &&
                   compareValue((String)searchText.get(index).display,
                                textValue,
                                textValue.length()) == 0)
                index-- ;

            return searchText.get(index + 1).modelIndex;
        }

    }

    /**
     * Does a partial comparison of two values by adjusting for length first and
     * returns the lesser lexical value.
     * 
     * @param value
     * @param textValue
     * @param length
     * @return
     */
    private int compareValue(String value, String textValue, int length) {
        if (value.length() < length) {
            if (textValue.startsWith(value))
                return -1;
            return value.compareTo(textValue.substring(0, value.length()));
        }
        return value.substring(0, length).compareTo(textValue);
    }

    /**
     * Private class to implement partial match comparator when searching
     * 
     * @author tschmidt
     * 
     */
    private class MatchComparator implements Comparator<SearchPair> {

        public int compare(SearchPair o1, SearchPair o2) {
            return compareValue(o1.display, o2.display, o2.display.length());
        }
    }

    // ********** Table Keyboard Handling ****************************

    protected class KeyboardHandler implements KeyDownHandler, KeyPressHandler {

        /**
         * This method handles all key down events for this table
         */
        public void onKeyDown(KeyDownEvent event) {

            switch (event.getNativeKeyCode()) {
                case KeyCodes.KEY_TAB:
                    if (popup != null && popup.isShowing())
                        popup.hide();
                    break;
            }

        }

        /**
         * This method handles all keyup events for the dropdown widget.
         */
        public void onKeyPress(KeyPressEvent event) {
            int index;
            char ch;

            ch = event.getUnicodeCharCode() == 0 ? (char)event.getNativeEvent().getKeyCode()
                                                : event.getCharCode();

            switch (event.getNativeEvent().getKeyCode()) {
                case KeyCodes.KEY_DOWN:
                    table.selectRowAt(findNextActive(table.getSelectedRow()));
                    setDisplay();
                    break;
                case KeyCodes.KEY_UP:
                    table.selectRowAt(findPrevActive(table.getSelectedRow()));
                    setDisplay();
                    break;
                case KeyCodes.KEY_ENTER:
                    if (popup == null || !popup.isShowing())
                        showPopup();
                    else
                        popup.hide();
                    event.stopPropagation();
                    break;
                case KeyCodes.KEY_TAB:
                    break;
                case KeyCodes.KEY_BACKSPACE:
                    if ( !queryMode) {

                        if (searchString.length() > 1)
                            searchString = searchString.substring(0, searchString.length() - 1);
                        else {
                            searchString = "";
                            table.selectRowAt( -1);
                            return;
                        }
                    }
                default:
                    // Don't add backspace to search
                    if (!queryMode) {
                        if (ch != '\b')
                            searchString += String.valueOf(ch);

                        index = findIndexByTextValue(searchString);

                        if (index > -1) {
                            table.selectRowAt(index);
                            setDisplay();
                            textbox.setSelectionRange(searchString.length(), textbox.getText()
                                                                                    .length() -
                                                                             searchString.length());
                        } else
                            searchString = searchString.substring(0, searchString.length() - 1);
                        event.preventDefault();
                        event.stopPropagation();
                    }
            }
        }

        /**
         * Method to find the next selectable item in the Dropdown
         * 
         * @param index
         * @return
         */
        @SuppressWarnings("unchecked")
        private int findNextActive(int index) {
            int next;

            /*
             * Iterate forward until the next enabled item is found
             */
            next = index + 1;
            while (next < table.getRowCount() &&
                   ! ((Item<T>)table.getModel().get(next)).isEnabled())
                next++ ;

            if (next < table.getRowCount())
                return next;

            return index;

        }

        /**
         * Method to find the previous selectable item in the Dropdown
         * 
         * @param index
         * @return
         */
        @SuppressWarnings("unchecked")
        private int findPrevActive(int index) {
            int prev;
            
            if(index < 0)
                prev = table.getRowCount() - 1;
            else
                prev = index - 1;
            
            while (prev > -1 && ! ((Item<T>)table.getModel().get(prev)).isEnabled())
                prev-- ;

            if (prev > -1)
                return prev;

            return index;
        }
    }

    /**
     * Checks for the field required exception and will add that to the validate
     * exceptions list. Returns true if any validation or user exception has
     * been added to the widget.
     */
    public boolean hasExceptions() {
        if (getValidateExceptions() != null)
            return true;

        if ( !queryMode && required && getValue() == null) {
            addValidateException(new Exception(getMessages().exc_fieldRequired()));
            Balloon.checkExceptionHandlers(this);
        }

        return getEndUserExceptions() != null || getValidateExceptions() != null;
    }

    /**
     * Adds a manual Exception to the widgets exception list.
     */
    public void addException(Exception error) {
        exceptions.addException(error);
        Balloon.checkExceptionHandlers(this);
    }

    /**
     * Adds a validation exception to this widget
     * 
     * @param error
     */
    protected void addValidateException(Exception error) {
        exceptions.addValidateException(error);

    }

    /**
     * Returns the list of Validate Exceptions for this widget.
     */
    public ArrayList<Exception> getValidateExceptions() {
        return exceptions.getValidateExceptions();
    }

    /**
     * Returns the list of User Exceptions for this widget.
     */
    public ArrayList<Exception> getEndUserExceptions() {
        return exceptions.getEndUserExceptions();
    }

    /**
     * Clears all manual and validate exceptions from the widget.
     */
    public void clearExceptions() {
        exceptions.clearExceptions();
        removeExceptionStyle();
        Balloon.clearExceptionHandlers(this);
    }

    /**
     * Clears the list of User Exceptions for this widget.
     */
    public void clearEndUserExceptions() {
        exceptions.clearEndUserExceptions();
        Balloon.checkExceptionHandlers(this);
    }

    /**
     * Clears the list of Validation Exceptions for this widget.
     */
    public void clearValidateExceptions() {
        exceptions.clearValidateExceptions();
        Balloon.checkExceptionHandlers(this);
    }

    /**
     * Adds an exception CSS class to this widget.
     */
    public void addExceptionStyle() {
        if (Balloon.isWarning(this))
            display.addStyleName(css.InputWarning());
        else
            display.addStyleName(css.InputError());
    }

    /**
     * Removes an exception CSS class from this widget.
     */
    public void removeExceptionStyle() {
        display.removeStyleName(css.InputError());
        display.removeStyleName(css.InputWarning());
    }

    /**
     * Method only implemented to satisfy Focusable interface.
     */
    public int getTabIndex() {
        return -1;
    }

    /**
     * Method only implemented to satisfy Focusable interface.
     */
    public void setTabIndex(int index) {

    }

    /**
     * Method only implemented to satisfy Focusable interface.
     */
    public void setAccessKey(char key) {

    }

    /**
     * Sets/Loses focus to this widget based on the passed boolean.
     */
    public void setFocus(boolean focused) {
        textbox.setFocus(focused);
    }

    /**
     * Method used to validate the inputed query string by the user.
     */
    public void validateQuery() {
        // Stubbed since user can not type query in
    }

    /**
     * Method used to determine if widget is currently in Query mode
     */
    public boolean isQueryMode() {
        return queryMode;
    }

    @Override
    /**
     * Returns if widget is enabled for editing
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns true if the passed value is a key of an item in the dropdown.
     * 
     * @param value
     * @return
     */
    public boolean isValidKey(T value) {
        return keyHash.containsKey(value);
    }

    /**
     * Returns true if this field is required to have a value before submission
     * 
     * @param required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    // ************ Handler Registration methods *********************

    /**
     * The Screen will add its screenHandler here to register for the
     * onValueChangeEvent
     */
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * This Method is here so the Focus logic of ScreenPanel can be notified
     */
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return addHandler(handler, BlurEvent.getType());
    }

    /**
     * This method is here so the Focus logic of ScreenPanel can be notified
     */
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addHandler(handler, FocusEvent.getType());
    }

    /**
     * Adds a mouseover handler to the textbox for displaying Exceptions
     */
    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addDomHandler(handler, MouseOverEvent.getType());
    }

    /**
     * Adds a MouseOut handler for hiding exceptions display
     */
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return addDomHandler(handler, MouseOutEvent.getType());
    }
    
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addDomHandler(handler, KeyUpEvent.getType());
    }

    @Override
    /**
     * Sets the Helper to be used for this widget
     */
    public void setHelper(WidgetHelper<T> helper) {
        this.helper = helper;

    }

    @Override
    /**
     * Returns the Helper that is used for this widget
     */
    public WidgetHelper<T> getHelper() {
        return helper;
    }

    /**
     * Public Interface used to provide rendering logic for the Selection
     * display
     * 
     */
    public interface Renderer {
        public String getDisplay(Row row);
    }

    /**
     * Private Default implementation of the Renderer interface.
     * 
     */
    protected class DefaultRenderer implements Renderer {
        public String getDisplay(Row row) {
            return row != null && row.getCells().get(0) != null ? row.getCells().get(0).toString()
                                                               : "";
        }
    }

    public void setCSS(DropdownCSS css) {
        css.ensureInjected();
        this.css = css;
        display.setStyleName(css.SelectBox());
        textbox.setStyleName(css.SelectText());
        //button.setLeftIcon(css.SelectButton());
    }

    public void setField(String field) {
        if (field.equals("Integer"))
            setHelper((WidgetHelper<T>)new IntegerHelper());
    }

    public void setTip(String text) {
        if (text != null) {
            if (options == null)
                options = new Balloon.Options(this);
            options.setTip(text);
        } else if (text == null && options != null) {
            options.destroy();
            options = null;
        }
    }

    public void setTipPlacement(Placement placement) {
        if (options == null)
            options = new Balloon.Options(this);

        options.setPlacement(placement);
    }

    @UiChild(tagname = "balloonOptions", limit = 1)
    public void setBalloonOptions(Balloon.Options tip) {
        this.options = tip;
        options.setTarget(this);
    }

    public Balloon.Options getBalloonOptions() {
        return options;
    }

    protected UIMessages getMessages() {
        return Messages.get();
    }
}