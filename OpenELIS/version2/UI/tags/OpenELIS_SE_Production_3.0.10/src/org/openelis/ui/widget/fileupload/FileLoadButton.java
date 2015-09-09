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
package org.openelis.ui.widget.fileupload;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;

/**
 * A decorated file upload is a widget which hides a FileUpload showing
 * a clickable and customizable Widget, normally a button.
 */
@Deprecated
public class FileLoadButton extends ResizeComposite implements HasText, HasName, HasChangeHandlers {

  /**
   * An abstract class which is the base for specific browser implementations.
   */
  private static class FileLoadButtonImpl {

    protected Widget button;
    protected AbsolutePanel container;
    protected FileUploadWithMouseEvents input;
    protected boolean enabled;

    public void init(AbsolutePanel container, FileUploadWithMouseEvents input) {
      this.container = container;
      this.input = input;
    }

    public void resize() {
      if (button != null) {
        container.setWidth(button.getOffsetWidth() + "px");
        container.setHeight(button.getOffsetHeight() + "px");
      }
    }

    public void setButton(Widget widget) {
      this.button = widget;
      if (button instanceof HasMouseOverHandlers) {
        ((HasMouseOverHandlers) button).addMouseOverHandler(new MouseOverHandler() {
          public void onMouseOver(MouseOverEvent event) {
        	  if(enabled)
        		  button.addStyleDependentName(STYLE_BUTTON_OVER_SUFFIX);
          }
        });
      }
      if (button instanceof HasMouseOutHandlers) {
        ((HasMouseOutHandlers) button).addMouseOutHandler(new MouseOutHandler() {
          public void onMouseOut(MouseOutEvent event) {
        	  if(enabled)
        		  button.removeStyleDependentName(STYLE_BUTTON_OVER_SUFFIX);
          }
        });
      }
    }
    
    public void setEnabled(boolean enabled) {
    	this.enabled = enabled;
    }
    
  
  }
 

  /**
   * Implementation for browsers which support the click() method:
   * IE, Chrome, Safari
   * 
   * The hack here is to put the customized button
   * and the file input statically positioned in an absolute panel. 
   * This panel has the size of the button, and the input is not shown 
   * because it is placed out of the width and height panel limits.
   * 
   */
  @SuppressWarnings("unused")
  private static class FileLoadButtonImplClick extends FileLoadButtonImpl {

    private static HashMap<Widget, HandlerRegistration> clickHandlerCache = new HashMap<Widget, HandlerRegistration>();

    private static native void clickOnInputFile(Element elem) /*-{
      elem.click();
    }-*/;

    public void init(AbsolutePanel container, FileUploadWithMouseEvents input) {
      super.init(container, input);
      container.add(input, 500, 500);
      container.getElement().getStyle().setProperty("cssFloat", "left");
      container.getElement().getStyle().setDisplay(Display.INLINE);
    }

    public void setButton(Widget widget) {
      super.setButton(widget);
      HandlerRegistration clickRegistration = clickHandlerCache.get(widget);
      if (clickRegistration != null) {
        clickRegistration.removeHandler();
      }
      if (button != null) {
        if (button instanceof HasClickHandlers) {
          clickRegistration = ((HasClickHandlers) button).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	if(enabled)
            		clickOnInputFile(input.getElement());
            }
          });
          clickHandlerCache.put(widget, clickRegistration);
        }
      }
    }
  }

  /**
   * Implementation for browsers which do not support the click() method:
   * FF, Opera
   * 
   * The hack here is to place the customized button and the file input positioned
   * statically in an absolute panel which has size of the button. 
   * The file input is wrapped into a transparent panel, which also has the button
   * size and is placed covering the customizable button.
   * 
   * When the user puts his mouse over the button and clicks on it, what really 
   * happens is that the user clicks on the transparent file input showing
   * the choose file dialog.
   * 
   */  
  @SuppressWarnings("unused")
  private static class FileLoadButtonImplNoClick extends FileLoadButtonImpl {

    private SimplePanel wrapper;

    public void init(AbsolutePanel container, FileUploadWithMouseEvents input) {
      super.init(container, input);
      wrapper = new SimplePanel();
      wrapper.add(input);
      container.add(wrapper, 0, 0);
      wrapper.setStyleName("wrapper");
      
      // Not using GWT 2.0.x way to set Style attributes in order to be
      // compatible with old GWT releases
      container.getElement().getStyle().setProperty("cssFloat", "left");
      wrapper.getElement().getStyle().setTextAlign(TextAlign.LEFT);
      wrapper.getElement().getStyle().setZIndex(1);
      input.getElement().getStyle().setMarginLeft(-1500, Unit.PX);
      input.getElement().getStyle().setFontSize(500, Unit.PX);
      input.getElement().getStyle().setBorderWidth(0, Unit.PX);
      input.getElement().getStyle().setOpacity(0);
      input.getElement().setAttribute("size", "1");
      
      // Trigger over and out handlers which already exist in the covered button.
      input.addMouseOverHandler(new MouseOverHandler() {
        public void onMouseOver(MouseOverEvent event) {
          if (button != null && enabled) {
            button.fireEvent(event);
          }
        }
      });
      input.addMouseOutHandler(new MouseOutHandler() {
        public void onMouseOut(MouseOutEvent event) {
          if (button != null && enabled) {
            button.fireEvent(event);
          }
        }
      });
    }

    public void resize() {
      super.resize();
      if (button != null) {
        wrapper.setWidth(button.getOffsetWidth() + "px");
        wrapper.setHeight(button.getOffsetHeight() + "px");
      }
    }
    
    public void setEnabled(boolean enabled) {
    	super.setEnabled(enabled);
    	if(enabled)
    		input.getElement().getStyle().setDisplay(Display.INLINE);
    	else
    		input.getElement().getStyle().setDisplay(Display.NONE);
    }
  }

  /**
   * A FileUpload which implements onChange, onMouseOver and onMouseOut events.
   * 
   * Note FileUpload in version 2.0.x implements onChange event, but we put it here 
   * in order to be compatible with 1.6.x
   *
   */
  public static class FileUploadWithMouseEvents extends FileUpload implements HasMouseOverHandlers, HasMouseOutHandlers, HasChangeHandlers {

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
      return addDomHandler(handler, ChangeEvent.getType());
    }

    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
      return addDomHandler(handler, MouseOutEvent.getType());
    }

    public HandlerRegistration addMouseOverHandler(final MouseOverHandler handler) {
      return addDomHandler(handler, MouseOverEvent.getType());
    }
  }

  private static final String STYLE_BUTTON_OVER_SUFFIX = "over";
  private static final String STYLE_CONTAINER = "DecoratedFileUpload";
  protected Widget button;
  protected AbsolutePanel container;
  protected FileUploadWithMouseEvents input = new FileUploadWithMouseEvents();
  protected boolean reuseButton = false;
  private FileLoadButtonImpl impl;

  public FileLoadButton() {
    impl = GWT.create(FileLoadButtonImpl.class);
    container = new AbsolutePanel();
    container.addStyleName(STYLE_CONTAINER);
    initWidget(container);
    impl.init(container, input);
  }

  public FileLoadButton(Widget button) {
    this();
    setButton(button);
  }
  
  @Override
  public void onResize() {
  	super.onResize();
  	if (button instanceof ResizeComposite) {
  		((ResizeComposite)button).onResize();
  	}
  }

  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return input.addChangeHandler(handler);
  }

  public String getFilename() {
    return input.getFilename();
  }

  public FileUpload getFileUpload() {
    return input;
  }

  public String getName() {
    return input.getName();
  }

  public String getText() {
    if (button == null) {
      return "";
    }
    if (button instanceof HasText) {
      return ((HasText) button).getText();
    } else {
      return button.toString();
    }
  }

  public Widget getWidget() {
    return this;
  }

  @Override
  public void onAttach() {
    super.onAttach();
    if (button == null) {
      button = new Button("Browse");
      setButton(button);
    } else {
      impl.resize();
    }
  }

  public void setButton(Widget button) {
    assert button instanceof HasClickHandlers : "Button should extend HasClickHandlers";
    if (this.button != null) {
      container.remove(this.button);
    }
    this.button = button;
    container.add(button, 0, 0);
    impl.setButton(button);
    impl.resize();
  }

  public void setButtonSize(String width, String height) {
    button.setSize(width, height);
    impl.resize();
  }

  public void setName(String fieldName) {
    input.setName(fieldName);
  }

  public void setText(String text) {
    if (button instanceof HasText) {
      ((HasText) button).setText(text);
      impl.resize();
    }
  }
  
  public void setEnabled(boolean enabled) {
	  impl.setEnabled(enabled);
  }

}