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

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Exceptions;
import org.openelis.ui.common.Util;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.event.BeforeGetMatchesEvent;
import org.openelis.ui.event.BeforeGetMatchesHandler;
import org.openelis.ui.event.GetMatchesEvent;
import org.openelis.ui.event.GetMatchesHandler;
import org.openelis.ui.event.HasBeforeGetMatchesHandlers;
import org.openelis.ui.event.HasGetMatchesHandlers;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.messages.UIMessages;
import org.openelis.ui.resources.DropdownCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Balloon.Placement;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.Row;
import org.openelis.ui.widget.table.Table;
import org.openelis.ui.widget.table.event.CellClickedEvent;
import org.openelis.ui.widget.table.event.CellClickedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.NativeHorizontalScrollbar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is used by OpenELIS Screens to display and input values in forms
 * and in table cells as an AutoComplete list selector. This class exteds TextBox
 * which implements the ScreenWidgetInt and we override this implementation
 * where needed.
 * 
 * @param <T>
 */
public class AutoComplete extends Composite implements ScreenWidgetInt,
													   Queryable,
													   Focusable,
													   HasBlurHandlers,
													   HasFocusHandlers,
													   HasValue<AutoCompleteValue>,
													   HasHelper<String>,
													   HasExceptions,
													   HasGetMatchesHandlers,
													   HasBeforeGetMatchesHandlers, 
													   HasBalloon {
	@UiTemplate("Select.ui.xml")
	interface ACUiBinder extends UiBinder<Widget, AutoComplete>{};
	public static final ACUiBinder uiBinder = GWT.create(ACUiBinder.class);

    /**
     * Used for AutoComplete display
     */

	@UiField
    protected LayoutPanel                           display;
	@UiField
    protected Button                                button;
    protected Table                                 table;
    protected PopupPanel                            popup;
    protected int                                   cellHeight = 19, delay = 350, itemCount = 10, width;
    protected Timer                                 timer;
    protected boolean                               required,queryMode,showingOptions;
    protected String                                prevText;
    
    @UiField
    protected TextBase                              textbox;
    
    protected AutoCompleteValue                     value;
    
    /**
     * Exceptions list
     */
    protected Exceptions                           exceptions;
    protected Balloon.Options                      options;
    
    final AutoComplete                             source;

    /**
     * Instance of the Renderer interface. Initially set to the DefaultRenderer
     * implementation.
     */
    protected Renderer                              renderer  = new DefaultRenderer();
    
    protected WidgetHelper<String>                  helper    = new StringHelper();
    
    protected DropdownCSS                           css;
    
    protected String                                dropHeight = "150px", dropWidth;
    

    /**
     * Default no-arg constructor
     */
    public AutoComplete() {
    	source = this;
        /*
         * Final instance of the private class KeyboardHandler
         */
        final KeyboardHandler keyHandler = new KeyboardHandler();

        initWidget(uiBinder.createAndBindUi(this));

        /*
         * Set the focus style when the Focus event is fired Externally
         */
        addFocusHandler(new FocusHandler() {
        	public void onFocus(FocusEvent event) {
        		if(isEnabled()) {
        		    display.addStyleName(css.Focus());
        			selectAll();
        		}
        	}
        });

        /*
         * Removes the focus style when the Blur event is fires externally
         */
        addBlurHandler(new BlurHandler() {
        	public void onBlur(BlurEvent event) {
        		display.removeStyleName(css.Focus());
        		textbox.setSelectionRange(0, 0);
        		finishEditing();
        	}
        });
        
        /*
         * Registers the keyboard handling this widget
         */
        addHandler(keyHandler, KeyDownEvent.getType());
        addHandler(keyHandler, KeyPressEvent.getType());
        
        /*
         * Timer is defined here once and scheduled to run when needed.  The timer is used to try and not 
         * make the query request for suggestions until the user has stopped typing to avoid firing multiple
         * request that will not be used
         */
        timer = new Timer() {
            public void run() {
            	BeforeGetMatchesEvent bgme;
                String text;
                
                text = textbox.getText();
                
            	bgme = BeforeGetMatchesEvent.fire(source, textbox.getText());
            	if(bgme != null && bgme.isCancelled())
            		return;
                
                GetMatchesEvent.fire(source, text);
            }
        };
        
        exceptions = new Exceptions();
        
        setCSS(UIResources.INSTANCE.dropdown());
        
        setPopupContext(new Table.Builder(10).column(new Column.Builder(10).build()).build());
        
        setWidth("150px");
        
    }
    
    @UiHandler("textbox")
    protected void onFocus(FocusEvent event) {
    	FocusEvent.fireNativeEvent(event.getNativeEvent(),this);
    }

    @UiHandler("textbox")
    protected void onBlur(BlurEvent event) {
        Item<Integer> item;

        
        if(!showingOptions && isEnabled() && !queryMode) {
        	if("".equals(textbox.getText()) && getValue() != null){
        		setValue(null,"",true);
        	}else {
        		item = getSelectedItem();

        		if (item != null)
        			setValue(item.key, renderer.getDisplay(item),item.getData(),true);
        	}
        }
        
        if(!showingOptions)
        	BlurEvent.fireNativeEvent(event.getNativeEvent(), this);
    }
    
    @UiHandler("button")
    protected void buttonClick(ClickEvent evet) {
    	BeforeGetMatchesEvent bgme;
    	
    	if(queryMode)
    	    return;
    	
    	bgme = BeforeGetMatchesEvent.fire(this, textbox.getText());
    	if(bgme != null && bgme.isCancelled())
    		return;
    	
    	textbox.setFocus(true);
    	
        GetMatchesEvent.fire(this, textbox.getText());
    }
    
    @UiHandler("button")
    protected void onMouseDown(MouseDownEvent event) {
        if(!queryMode)
            showingOptions = true;
    }
    
    /**
     * This method will display the table set as the PopupContext for this
     * Dropdown. Will create the Popup and initialize the first time if null. We
     * also call scrollToVisible() on the table to make sure the selected value
     * is in the current table view.
     */
    protected void showPopup() {
        if(queryMode)
            return;
        
    	showingOptions = true;
        final LayoutPanel layout = new LayoutPanel();
        
        if (popup == null) {
            popup = new PopupPanel(true);
            popup.setStyleName(css.Popup());
            
            popup.setPreviewingAllNativeEvents(false);
            popup.addCloseHandler(new CloseHandler<PopupPanel>() {
                public void onClose(CloseEvent<PopupPanel> event) {
                	showingOptions = false;
                	setDisplay();
                	setFocus(true);
                }
            });
           
            layout.add(table);
            
            popup.setWidget(layout);
        }

        /**
         * Draw and show outside of viewable so table will size correctly and 
         * and showRelative will work when close to browser border.
         */
        popup.setPopupPosition(-1000, -1000);
        popup.show();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                int modelHeight = table.getModel() != null ? table.getModel().size() * cellHeight : 0;
                
                if(table.hasHeader())
                    modelHeight += 20;
                
                int width = dropWidth == null ? getOffsetWidth() : Util.stripUnits(dropWidth);
                
                if(table.getTotalColumnWidth() > width)
                    modelHeight += NativeHorizontalScrollbar.getNativeScrollbarHeight() + 1;
                
                popup.setSize(width+"px", 
                               Util.stripUnits(dropHeight) > modelHeight ? modelHeight+"px" : dropHeight);
                
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
        textbox.setText(renderer.getDisplay(getSelectedItem()));
    }

    @Override
    public void setWidth(String w) {        
        width = Util.stripUnits(w);

        if (display != null)
            display.setWidth(width+"px");
        
        if(table != null)
            table.setWidth((width)+"px");
    }
    
    public int getWidth() {
        return width;
    }
    
    @Override
    public void setHeight(String height) {
        display.setHeight(height);
        button.setHeight(height);
    }
    
    /**
     * Method sets the number of suggestions to show at once before scrolling.
     * @param itemCount
     */
    public void setVisibleItems(int itemCount) {
        this.itemCount = itemCount;
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
     * Dropdown. Will set the isDropdown flag in the Table so the correct
     * styling is used.
     * 
     * @param tree
     */
    @UiChild(limit=1,tagname="popup")
    public void setPopupContext(Table tableDef) {
        this.table = tableDef;
        table.setCSS(UIResources.INSTANCE.dropTable());
        table.addStyleName(UIResources.INSTANCE.dropTable().Single());

        // table.setFixScrollbar(false);
        table.setRowHeight(16);
        table.setEnabled(true);
        
        /*
         * This handler will will cancel the selection if the item has been
         * disabled.
         */
        table.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
            @SuppressWarnings("unchecked")
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                if ( !((Item<Integer>)table.getModel().get(event.getItem())).isEnabled())
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
        
        /*
         * We close the popup on CellClick instead of selectionso that the display
         * can be set on selection of use of keyboard.
         */
        table.addCellClickedHandler(new CellClickedHandler() {
			public void onCellClicked(CellClickedEvent event) {
				popup.hide();
			}
		});
    }
    
    /**
     * Returns the Table being used by this Autocomplete to show matches. Note
     * that Autocomplete always uses a table even for one column.
     * @return
     */
    public Table getPopupContext() {
        return table;
    }

    /**
     * Sets the data model for the PopupContext of this widget.
     * 
     * @param model
     */
    public void setModel(ArrayList<Item<Integer>> model) {
        assert table != null;
        
        table.setModel(model);
        table.selectRowAt(0);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            
            @Override
            public void execute() {
                table.onResize();
                
            }
        });
        
    }

    /**
     * Returns the model used in the table
     * 
     * @return
     */
    public ArrayList<Item<Integer>> getModel() {
        return table.getModel();
    }

    /**
     * Sets the selected row using its overall index in the model. This method
     * will also cause a ValueChangeEvent to be fired.
     * 
     * @param index
     */
    public void setSelectedIndex(int index) {
        table.selectRowAt(index);
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
    public Item<Integer> getSelectedItem() {
        if(table == null || table.getModel() == null || getSelectedIndex() < 0)
            return null;
        return table.getRowAt(getSelectedIndex());
    }

    /**
     * Returns the string currently displayed in the textbox portion of the
     * widget.
     * 
     * @return
     */
    public String getDisplay() {
        return textbox.getText();
    }

    /**
     * Sets the amount of time the widget should wait in milliseconds before
     * firing a a GetMatchesEvent.
     * 
     * @param delay
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    // ********** Methods Overridden in the ScreenWidetInt ****************

    /**
     * Method overridden from TextBox to enable the button and table as well as
     * the textbox.
     */
    @Override
    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
        table.setEnabled(enabled);
        if (enabled)
            sinkEvents(Event.ONKEYDOWN | Event.ONKEYPRESS);
        else
            unsinkEvents(Event.ONKEYDOWN | Event.ONKEYPRESS);
        textbox.setReadOnly(!enabled);
    }
    
    @Override
    public void setQueryMode(boolean query) {

    	if (queryMode != query) {
    		value = null;
    		textbox.setText("");
    		table.unselectAll();
    	}
        
    	queryMode = query;
    	
    	if(query)
    		unsinkEvents(Event.ONKEYDOWN | Event.ONKEYPRESS);
    	else if(isEnabled())
    		sinkEvents(Event.ONKEYDOWN | Event.ONKEYPRESS);
    }

    /**
     * Method used to set an Autocomplete from OnDataChangeEvents from the
     * screen.
     * 
     * @param value
     * @param display
     */
    public void setValue(Integer value, String display) {
        setValue(value != null ? new AutoCompleteValue(value,display) : null,false);
    }
    
    public void setValue(Integer value, String display, boolean fireEvents) {
    	setValue(value != null ? new AutoCompleteValue(value,display) : null,fireEvents);
    }
    
    public void setValue(Integer value, String display, Object data, boolean fireEvents) {
        setValue(value != null ? new AutoCompleteValue(value,display,data) : null,fireEvents);
    }
    
    public void setValue(AutoCompleteValue av) {
        setValue(av,false);
    }

    /**
     * Overridden method to set the T value of this widget. Will fire a value
     * change event if fireEvents is true and the value is changed from its
     * current value
     */
    @Override
    public void setValue(AutoCompleteValue value, boolean fireEvents) {

        if(!Util.isDifferent(this.value == null ? null : this.value, value))
            return;
        
        table.setModel(null);

        if (value != null) {
            textbox.setText(value.getDisplay());
        } else {
            table.selectRowAt( -1);
            textbox.setText("");
        }

        this.value = value;

        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    };

    /**
     * Overridden method of TextBox to check if the Dropdown is valid
     */
    public void finishEditing() {
        Item<Integer> item;
        clearValidateExceptions();
        
        if(isEnabled()) {
        	if(queryMode)
        		validateQuery();
        	else{
  
        	    if(textbox.getText().isEmpty()) { 
        	        setValue(null,true);
        	    }else {
        	        item = getSelectedItem();
        		    if (item != null)
        		        setValue(item.key, renderer.getDisplay(item),item.getData(),true);
        	    }
        		
        		if (required && value == null) 
        		    addValidateException(new Exception(getMessages().exc_fieldRequired()));
        	   
        		Balloon.checkExceptionHandlers(this);
        	}
        }
    }
    
    /**
     * This method is called by the GetMatchesHandler to show the results of the
     * matching
     * 
     * @param model
     */
    public void showAutoMatches(ArrayList<Item<Integer>> model) {
        setModel(model);
        /*
         * Call showPopup only if the textbox still has focus. Otherwise the
         * user has moved on from the widget so the first entry in the results
         * will be selected and displayed
         */
        if (getStyleName().indexOf(css.Focus()) > -1 && model != null && model.size() > 0) {
            showPopup();
        } else  if(popup != null) {
        	popup.hide();
        	finishEditing();
        } else { 
        	finishEditing();
        }
    }
    
	/**
	 * This method will register the passed BeforeGetMatchesHandler to this widget.
	 * @param handler
	 * @return
	 */
    public HandlerRegistration addBeforeGetMatchesHandler(BeforeGetMatchesHandler handler) {
		return addHandler(handler,BeforeGetMatchesEvent.getType());
	}

    /**
     * This method will register the passed GetMatchesHandler to this widget.
     */
    public HandlerRegistration addGetMatchesHandler(GetMatchesHandler handler) {
        return addHandler(handler, GetMatchesEvent.getType());
    }

    // ********** Table Keyboard Handling ****************************

    protected class KeyboardHandler implements KeyDownHandler, KeyPressHandler {
        /**
         * This method handles all key down events for this table
         */
        public void onKeyDown(KeyDownEvent event) {
            
            if(queryMode)
                return;
            
            prevText = textbox.getText();
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
            String text;
            BeforeGetMatchesEvent bgme;
            
            if(queryMode)
                return;
           
            switch (event.getNativeEvent().getKeyCode()) {
                case KeyCodes.KEY_DOWN:
                    if (popup != null && popup.isShowing()) { 
                        table.selectRowAt(findNextActive(table.getSelectedRow()));
                    }
                    break;
                case KeyCodes.KEY_UP:
                    if (popup != null && popup.isShowing()) { 
                        table.selectRowAt(findPrevActive(table.getSelectedRow()));
                    }
                    break;
                case KeyCodes.KEY_ENTER:
                    if (popup == null || !popup.isShowing()) {
                        text = textbox.getText();
                        
                        bgme = BeforeGetMatchesEvent.fire(source, text);
                        if(bgme != null && bgme.isCancelled())
                            return;
                        
                        GetMatchesEvent.fire(source, text);
                    } else
                        popup.hide();
                    event.stopPropagation();
                    break;
                case KeyCodes.KEY_TAB:
                    break;
                default:
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        
                        @Override
                        public void execute() {
                            String text = textbox.getText();

                            timer.cancel();
                            /*
                             * Will hit this if backspaced to clear textbox. Call
                             * setSelected 0 so that if user tabs off the value is
                             * selected correctly
                             */
                            if (DataBaseUtil.isEmpty(text)){    
                                setSelectedIndex( -1);
                                if(popup != null)
                                    popup.hide();
                            }else if(!text.equals(prevText))
                                timer.schedule(delay);
                        }
                    });

                    
             }
        }

        /**
         * Method to find the next selectable item in the Dropdown
         * 
         * @param index
         * @return
         */
        @SuppressWarnings("rawtypes")
		private int findNextActive(int index) {
            int next;

            next = index + 1;
            while (next < table.getRowCount() && !((Item)table.getModel().get(next)).isEnabled())
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
        @SuppressWarnings("rawtypes")
		private int findPrevActive(int index) {
            int prev;

            prev = index - 1;
            while (prev > -1 && !((Item)table.getModel().get(prev)).isEnabled())
                prev-- ;

            if (prev > -1)
                return prev;

            return index;
        }
    }

	@Override
	public AutoCompleteValue getValue() {
		return value;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<AutoCompleteValue> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}
	
	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}
	
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addDomHandler(handler, KeyUpEvent.getType());
    }

	// ********** Implementation of HasException interface ***************
	/**
	 * Convenience method to check if a widget has exceptions so we do not need
	 * to go through the cost of merging the logical and validation exceptions
	 * in the getExceptions method.
	 * 
	 * @return
	 */
	public boolean hasExceptions() {
		if (getValidateExceptions() != null)
			return true;

		if (!queryMode && required && getValue() == null) {
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

	protected void addValidateException(Exception error) {
		exceptions.addValidateException(error);

	}

	/**
	 * Combines both exceptions list into a single list to be displayed on the
	 * screen.
	 */
	public ArrayList<Exception> getValidateExceptions() {
		return exceptions.getValidateExceptions();
	}

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

	public void clearEndUserExceptions() {
		exceptions.clearEndUserExceptions();
		Balloon.checkExceptionHandlers(this);
	}

	public void clearValidateExceptions() {
		exceptions.clearValidateExceptions();
		Balloon.checkExceptionHandlers(this);
	}
	
	@Override
	public void addExceptionStyle() {
		if(Balloon.isWarning(this))
			addStyleName(css.InputWarning());
		else
			addStyleName(css.InputError());
	}

	@Override
	public void removeExceptionStyle() {
    	removeStyleName(css.InputError());
    	removeStyleName(css.InputWarning());
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler) {
		return addHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return addHandler(handler, BlurEvent.getType());
	}

	@Override
	public int getTabIndex() {
		return -1;
	}

	@Override
	public void setAccessKey(char key) {
		
	}

	@Override
	public void setFocus(boolean focused) {
		textbox.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		
	}

    /**
     * Returns a single QueryData object representing the query string entered
     * by the user. The Helper class is used here to create the correct
     * QueryData object for the passed type T.
     */
    public Object getQuery() {
    	Object query = null;
    	
    	if(queryMode)
    		query = helper.getQuery(textbox.getText());
    	else if (getValue() != null) {
    		query = new QueryData(QueryData.Type.INTEGER,String.valueOf(getValue().getId()));
    	}
        
    	return query;
    }
    
    /**
     * Sets a query string to this widget when loaded from a table model
     */
    public void setQuery(QueryData qd) {
        if(qd != null)
            textbox.setText(qd.getQuery());
        else
            textbox.setText("");
    }
    
    /**
     * Method used to determine if widget is currently in Query mode
     */
    public boolean isQueryMode() {
    	return queryMode;
    }

    /**
     * Method used to validate the inputed query string by the user.
     */
    public void validateQuery() {
        try {
            clearValidateExceptions();
            helper.validateQuery(textbox.getText());
        } catch (Exception e) {
            addValidateException(e);
        }
        Balloon.checkExceptionHandlers(this);
    }


	@Override
	public boolean isEnabled() {
		return !textbox.isReadOnly();
	}

	@Override
	public void setHelper(WidgetHelper<String> helper) {
		this.helper = helper;
	}

	@Override
	public WidgetHelper<String> getHelper() {
		return helper;
	}
	
	public void selectAll() {
		textbox.selectAll();
	}
	
    /**
     * Set the text case for input.
     */
    public void setCase(TextBase.Case textCase) {
    	textbox.setCase(textCase);
    }
    
    
    public void setRequired(boolean required) {
    	this.required = required;
    }
    
    /**
     * Public Interface used to provide rendering logic for the Dropdown display
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
            if(row == null)
                return "";
            return row.getCells().get(0).toString();
        }
    }
    
    public void setCSS(DropdownCSS css) {
    	css.ensureInjected();
    	this.css = css;
    	
        //button.setLeftIcon(css.SelectButton());
        display.setStyleName(css.SelectBox());
        textbox.setStyleName(css.SelectText());
    }
    
    /**
     * Will set the width of the popup with results
     * @param width
     */
    public void setDropWidth(String width) {
        dropWidth = width;
    }

    /**
     * Will set the height of the popup with results
     * @param height
     */
    public void setDropHeight(String height) {
        dropHeight = height;
    }

    public void setTip(String text) {
        if(text != null) {
            if(options == null) 
                options = new Balloon.Options(this);
            options.setTip(text);
         }else if(text == null && options != null) {
            options.destroy();
            options = null;
        }
    }
    
    public void setTipPlacement(Placement placement) {
        if(options == null)
            options = new Balloon.Options(this);
        
        options.setPlacement(placement);
    }
            
    @UiChild(tagname="balloonOptions",limit=1)
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