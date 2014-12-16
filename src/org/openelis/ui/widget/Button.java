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

import org.openelis.ui.resources.ButtonCSS;
import org.openelis.ui.resources.IconCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.Balloon.Placement;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget class implements a Button on the screen. We have not used or
 * extended GWT Button for styling and functionality that we have designed for
 * OpenELIS.
 * 
 * Setting toggle to true will make the button stay pressed until the button is
 * clicked again.
 * 
 * All buttons are defaulted to enabled true. If a button needs to be ensured to
 * be disabled on the initial state of the screen set enable="false" in the xsl.
 */
public class Button extends FocusPanel implements ScreenWidgetInt, HasBalloon {
	@UiTemplate("button.ui.xml")
	interface ButtonUiBinder extends UiBinder<HTMLPanel,Button>{};
	public static final ButtonUiBinder uiBinder = GWT.create(ButtonUiBinder.class);
	
	@UiField
	protected DivElement left,center,right,outer;
	
    protected Balloon.Options   options;
    protected boolean toggles, enabled, pressed, locked;
    protected String action;
	protected ImageSelector imageSelector;
	protected int topOffset = -1;
	
    ButtonCSS css;
    IconCSS icon;
    
    private Button source = this;

	public Button() {
		setWidget(uiBinder.createAndBindUi(this));
		setKeyHandler();
		setFocusHandler();
		setBlurHandler();
		setClickHandler();
		icon = UIResources.INSTANCE.icon();
		icon.ensureInjected();
		setCss(UIResources.INSTANCE.button());
		getElement().getStyle().setOutlineStyle(OutlineStyle.NONE);
		setEnabled(false);
	}
	
    public Button(String icon,String label) {
    	this();
    	setLeftIcon(icon);
    	setText(label);
    }
    
    public Button(String leftIcon,String label,String rightIcon) {
    	this();
    	setLeftIcon(leftIcon);
    	setText(label);
    	setRightIcon(rightIcon);
    }
    
    public Button(Image image) {
    	this();
    	setCenter(image);
    }
    
    private void setKeyHandler() {
    	addKeyUpHandler(new KeyUpHandler() {
    		public void onKeyUp(KeyUpEvent event) {
    			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
    				NativeEvent clickEvent = com.google.gwt.dom.client.Document.get()
    						.createClickEvent(
    								0,
    								getAbsoluteLeft(),
    								getAbsoluteTop(),
    								-1,
    								-1,
    								event.isControlKeyDown(),
    								event.isAltKeyDown(),
    								event.isShiftKeyDown(),
    								event.isMetaKeyDown());
    				ClickEvent.fireNativeEvent(clickEvent, source);
    				event.stopPropagation();
    				event.preventDefault();
    			}
    		}
    	});
    }
    
    private void setClickHandler() {
        addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
           		if (toggles)
           			setPressed(!pressed);
           		outer.removeClassName(css.Focus());
           		if(imageSelector != null)
           			imageSelector.selectImage(source);
            }
        });
    }
    
    private void setFocusHandler() {
    	addFocusHandler(new FocusHandler() {
    		@Override
    		public void onFocus(FocusEvent event) {
    			if(isEnabled())
    				outer.addClassName(css.Focus());
    		}
    	});
    }
    
    private void setBlurHandler() {
       addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                outer.removeClassName(css.Focus());
            }
        });
    }
	
	public void setLeftText(String text) {
		setButtonElement(left,createTextElement(text));
	}
	
	@Deprecated
	public void setLeftIcon(String icon) {
		if(icon != null && !"".equals(icon))
			setButtonElement(left,createIconDiv(icon));
	}
		
	@UiChild(limit=1,tagname="left")
	public void setLeft(Widget widget) {
		setButtonElement(left,widget.getElement());
	}
	
	public void setText(String text) {
		setButtonElement(center,createTextElement(text));
	}
	
	@Deprecated
	public void setIcon(String icon) {
		if(icon != null && !"".equals(icon))
			setButtonElement(center,createIconDiv(icon));
	}
	
	public void setImage(ImageResource imageResource) {
		getImageSelector().setImage(new Image(imageResource));
	}
	
	@UiChild(limit=1,tagname="center")
	public void setCenter(Widget widget) {
		setButtonElement(center,widget.getElement());
	}
	
	public void setRightText(String text) {
		setButtonElement(right,createTextElement(text));
	}
	
	@Deprecated
	public void setRightIcon(String icon) {
		if(icon != null && !"".equals(icon))
			setButtonElement(right, createIconDiv(icon));
	}
	
	@UiChild(limit=1,tagname="right")
	public void setRight(Widget widget) {
		setButtonElement(right,widget.getElement());
	}
	
	public void setTopText(String text) {
		DivElement div = createTextElement(text);
		center.insertFirst(div);
	}
	
	public void setBottomText(String text) {
		DivElement div = createTextElement(text);
		center.appendChild(div);
	}
	
	public void setTopOffset(int topOffset) {
		this.topOffset = topOffset;
		center.getStyle().setTop(topOffset, Unit.PX);
	}
	
	private void setButtonElement(final DivElement div, Element element) {
		div.getStyle().setDisplay(Display.BLOCK);
		div.getStyle().setPosition(Position.RELATIVE);
		div.appendChild(element);

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				calcOffsetToCenter(outer,div);
			}
		});
		
	}
	
	public void setDisabledImage(ImageResource imageResource) {
		getImageSelector().setDisabled(new Image(imageResource));
	}
	
	public void setPressedImage(ImageResource imageResource) {
		getImageSelector().setPressed(new Image(imageResource));
	}
	
	public void setLockedImage(ImageResource imageResource) {
		getImageSelector().setLocked(new Image(imageResource));
	}
	
	public void setPixelSize(int width, int height) {
		getImageSelector().setPixelSize(width, height);
	}
	
	private DivElement createTextElement(String text) {
		DivElement label = Document.get().createDivElement();
		label.setInnerText(text);
		label.getStyle().setTextAlign(TextAlign.CENTER);
		return label;
	}
	
	private ImageSelector getImageSelector() {
		if(imageSelector == null) {
			imageSelector = new ImageSelector();
			setCenter(imageSelector);
		}
		return imageSelector;
	}
	
	private DivElement createIconDiv(String style) {
		DivElement div;
		
		div = Document.get().createDivElement();
		div.addClassName(style);
		return div;
	}
	
    public void setPressed(boolean pressed) {
        if(!toggles)
        	return;

        this.pressed = pressed;
        
        if (pressed){
            outer.addClassName(css.Pressed());
        }else {
            outer.removeClassName(css.Pressed());
        }
        
        if(imageSelector != null)
        	imageSelector.selectImage(this);
    }
    
    public void lock() {
        unsinkEvents(Event.ONCLICK | Event.ONKEYDOWN);
        locked = true;
        if(imageSelector != null)
        	imageSelector.selectImage(this);
    }
    
    public void unlock() {
        sinkEvents(Event.ONCLICK | Event.ONKEYDOWN);
        locked = false;
        if(imageSelector != null)
        	imageSelector.selectImage(this);
    }
    

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		
		outer.removeClassName(css.Pressed());
		
		if (enabled) {
			unlock();
			outer.removeClassName(css.Disabled());
			outer.removeClassName(icon.Disabled());
		} else {
			lock();
			outer.addClassName(css.Disabled());
			outer.addClassName(icon.Disabled());
		}
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isLocked() {
	    return locked;
	}
	
    public boolean isPressed() {
        return pressed;
    }
    
    public void setToggles(boolean toggles) {
        this.toggles = toggles;
    }
    
    public void addFocusStyle(String style) {
        outer.addClassName(css.Focus());
    }

    public void removeFocusStyle(String style) {
        outer.removeClassName(css.Focus());
    }
    
    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
    
	@Override
	public void finishEditing() {
		// Null implementation for interface
	}
	
	public void setCss(ButtonCSS css) {
		if(!isEnabled() && this.css != null) {
			outer.removeClassName(this.css.Disabled());
			outer.removeClassName(icon.Disabled());
		}
		
		if(pressed && this.css != null)
			outer.removeClassName(this.css.Pressed());
	
		this.css = css;
	
		css.ensureInjected();
		outer.setClassName(css.Button());
	   	
	   	if(!isEnabled())
	   		outer.addClassName(css.Disabled());
	   	
	   	if(pressed)
	   		outer.addClassName(css.Pressed());
	}
	
	public void setCSS(ButtonCSS css) {
		setCss(css);
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
    
    public void setWidth(String width) {
    	outer.getStyle().setProperty("width", width);
    }
    
    public void setHeight(String height) {
    	outer.getStyle().setProperty("height",height);
    }
    
    public static class ImageSelector extends SimplePanel {
    	
    	private Image image,disabledImage,pressedImage,lockedImage;
    	private int width=-1,height=-1;
    	
    	protected void selectImage(Button button) {
    		if(!button.enabled && disabledImage != null)
    			setWidget(disabledImage);
    		else if(button.pressed && pressedImage != null)
    			setWidget(pressedImage);
    		else if(button.locked && lockedImage != null)
    			setWidget(lockedImage);
    		else if(image != null)
    			setWidget(image);
    	}
    	
    	public void setPixelSize(int width, int height) {
    		this.width = width;
    		this.height = height;
    		
    		if(image != null)
    			image.setPixelSize(width, height);
    		if(disabledImage != null)
    			disabledImage.setPixelSize(width, height);
    		if(pressedImage != null)
    			pressedImage.setPixelSize(width, height);
    		if(lockedImage != null)
    			lockedImage.setPixelSize(width, height);
    	}
    	
    	@UiChild(limit=1,tagname="image")
    	public void setImage(Image image) {
    		this.image = image;
    		setImageProperties(image);
    		setWidget(image);
    	}
    	
    	@UiChild(limit=1,tagname="disabled")
    	public void setDisabled(Image image) {
    		this.disabledImage = image;
    		setImageProperties(image);
    	}
    	
    	@UiChild(limit=1,tagname="pressed")
    	public void setPressed(Image image) {
    		this.pressedImage = image;
    		setImageProperties(image);
    	}
    	
    	@UiChild(limit=1,tagname="locked")
    	public void setLocked(Image locked) {
    		this.lockedImage = image;
    		setImageProperties(image);
    	}	
    	
    	private void setImageProperties(Image image) {
    		image.getElement().getStyle().setDisplay(Display.BLOCK);
    		image.getElement().getStyle().setProperty("margin", "auto");
    		if(width > 0)
    			image.setPixelSize(width, height);
    	}
    }
    
    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
    	return addHandler(handler,ClickEvent.getType());
    }
    
    private void calcOffsetToCenter(final Element div, final Element content) {
    	Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand() {
			@Override
			public boolean execute() {
				if(isDrawn()) { 
					content.getStyle().setTop(calcTop(div), Unit.PX);
					return false;
				}
				return true;
			}
			
			private boolean isDrawn() {
				return isAttached() && div.getClientHeight() > 0 && content.getClientHeight() <= div.getClientHeight();
			}
			
			private double calcTop(Element div) {
				return (topOffset > 0)  ? topOffset : (div.getClientHeight() - content.getClientHeight()) / 2.0;
			}
		});
    }
    
}