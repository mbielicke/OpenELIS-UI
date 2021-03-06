/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.ui.widget;

import java.util.ArrayList;

import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.Balloon.Placement;
import org.openelis.ui.resources.IconCSS;
import org.openelis.ui.resources.MenuCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class MenuItem extends Composite implements ScreenWidgetInt,HasMouseOverHandlers,HasMouseOutHandlers, HasBalloon {
	@UiTemplate("MenuItem.ui.xml")
	interface MenuItemUiBinder extends UiBinder<HTML, MenuItem>{};
	public static final MenuItemUiBinder uiBinder = GWT.create(MenuItemUiBinder.class);
	
	@UiField
	protected TableCellElement icon,display,description;
	@UiField
	protected TableRowElement bottomRow;
	
	protected MenuCSS css;
	protected IconCSS iconCSS;
	
	protected ArrayList<Command> commands;
	
	protected String iconClass,label;
	
	protected Balloon.Options options;

    /**
     * Flags to determine if the MenuItem should autoClose all menus and 
     * if the menuItem is enabled.
     */

	protected boolean autoClose=true, enabled;
		
	protected int eventsToSink;
	
	public MenuItem() {
		initWidget(uiBinder.createAndBindUi(this));    
		
		css = UIResources.INSTANCE.menuCss();
		css.ensureInjected();
		
		iconCSS = UIResources.INSTANCE.icon();
		iconCSS.ensureInjected();
		
        setEnabled(true);
    }
	
	public MenuItem(String icon, String display, String description) {
		this();
		setIcon(icon);
		setDisplay(display);
		setDescription(description);
	}
    
	public MenuItem(String icon, String display, String description,boolean autoClose) {
		this();
		setIcon(icon);
		setDisplay(display);
		setDescription(description);
		this.autoClose = autoClose;
	}
    /**
     * Method to enable/disable the MenuItem
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(!enabled) {
            unsinkEvents(Event.ONCLICK);
            addStyleName(css.disabled());
            addStyleName(iconCSS.Disabled());
        }else{
            sinkEvents(Event.ONCLICK);
            removeStyleName(css.disabled());
            removeStyleName(iconCSS.Disabled());
        }
    }
    
    public boolean isEnabled() {
    	return enabled;
    }
    
    /**
     * The Command passed will be executed when a user clicks on it.  This method can be called more than once
     * to add multiple commands to the MenuItem.  The Commands will be executed in the order they are added.
     * @param command
     */
    public void addCommand(final Command command) {
    	if(commands == null) {
    		commands = new ArrayList<Command>();
    		addHandler(new ClickHandler() {
    			public void onClick(ClickEvent event) {
    				execute();
    			}
    		}, ClickEvent.getType());
    	}
    	commands.add(command);
    }
    
    public void execute() {
		for(Command comm : commands) 
			comm.execute();
    }
    
    public ArrayList<Command> getCommands() {
    	return commands;
    }
    
    /**
     * Method used to determine if the All Menus should be closed when this item is clicked
     * @return
     */
    protected boolean autoClose() {
        return autoClose;
    }
        
    public void setIcon(String icon) {
        this.iconClass = icon;
    	this.icon.setAttribute("class",icon);
    }
    
    public String getIcon() {
        return iconClass;
    }
    
    public void setDisplay(String display) {
        this.label = display;
    	this.display.setInnerText(display);
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setDescription(String description) {
    	if(description != null || "".equals(description)){
    		this.description.setInnerText(description);
    		bottomRow.removeAttribute("style");
    	}else
    		bottomRow.setAttribute("style", "display:none;");
    }
    
	/**
	 * These methods were added to ensure the button will be correctly enabled or disabled 
	 * when it is first drawn.
	 */
	@Override
	public void sinkEvents(int eventBitsToAdd) {
		if(isOrWasAttached())
			super.sinkEvents(eventBitsToAdd);
		else
			eventsToSink |= eventBitsToAdd;
	}
    
	@Override
	public void unsinkEvents(int eventBitsToRemove) {
		if(isOrWasAttached())
			super.unsinkEvents(eventBitsToRemove);
		else
			eventsToSink &= ~eventBitsToRemove;
	}
    
	@Override
	protected void onAttach() {
		super.onAttach();
		super.sinkEvents(eventsToSink);
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler,MouseOverEvent.getType());
	}
	
	
    @Override
    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return addDomHandler(handler,MouseOutEvent.getType());
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
    
    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
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

    @Override
    public void finishEditing() {
        
    }

}
