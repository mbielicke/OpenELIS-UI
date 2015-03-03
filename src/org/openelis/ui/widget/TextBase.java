package org.openelis.ui.widget;

import org.openelis.ui.resources.TextCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This class is an extension of GWT's TextBox widget to add our Masking, Alignment, and Case
 * logic so it can be wrapped by other widgets in the library
 *
 */
public class TextBase extends Composite {
	
    /**
     * Textbox attributes
     */
    protected TextBox.TextAlignment                 alignment = TextBox.TextAlignment.LEFT;
    protected Case                                  textCase  = Case.MIXED;

	
    protected boolean                               enforceMask;
    protected HandlerRegistration                   keyDown,keyPress;
    
    public enum Case {
        MIXED, UPPER, LOWER
    };
    
    protected String                                picture,mask;
    
    protected TextBase                              source = this;
    
    protected TextBox                               box;
    
    protected TextCSS                               css = UIResources.INSTANCE.text();
    
    public TextBase() {
    	box = GWT.create(TextBox.class);
    	initWidget(box);
    	setCSS(UIResources.INSTANCE.text());
    }
    
    /**
     * This method is overwritten to implement case management. Use the
     * setValue/getValue methods for normal screen use.
     */
    public String getText() {
    	String text;
    	
    	text = box.getText();
    	
    	if(enforceMask && text.equals(picture))
    		text = "";
    	
    	if(text.trim().length() == 0)
    		return "";
    	
        switch (textCase) {
            case UPPER:
                return text.toUpperCase();
            case LOWER:
                return text.toLowerCase();
            default:
                return text;
        }
    }
        
    /**
     * This method is overridden to make sure the Case style is applied to the widget  
     */
    @Override
    public void setStyleName(String style) {
    	super.setStyleName(style);
    	setCase(this.textCase);
    }
    
    /**
     * Set the text case for input.
     */
    public void setCase(Case textCase) {
   	
    	if(textCase == null)
    		textCase = Case.MIXED;
    	
    	this.textCase = textCase;
    	
        switch (textCase) {
            case UPPER:
                box.addStyleName(css.Upper());
                box.removeStyleName(css.Lower());
                break;
            case LOWER:
                box.addStyleName(css.Lower());
                box.removeStyleName(css.Upper());
                break;
            default:
           		box.removeStyleName(css.Upper());
           		box.removeStyleName(css.Lower());
        }
    }


    /**
     * Set the text alignment.
     */
    public void setTextAlignment(TextBox.TextAlignment alignment) {
        this.alignment = alignment;
        box.setAlignment(alignment);
    }
    
    /**
     * Sets the current mask to be used in the textbox and if first mask set will set up
     * handlers to receive events to apply the mask.
     */
    public void setMask(String msk) {
    	StringBuffer pic;
    
    	this.mask = msk;
    	
    	/*
    	 * If passed mask is null, set properties to turn off masking
    	 */
    	if(msk == null) {
    		enforceMask = false;
    		picture = null;
    		box.setMaxLength(255);
    		if(keyDown != null) {
    			keyDown.removeHandler();
    			keyPress.removeHandler();
    			keyDown = null;
    			keyPress = null;
    		}
    		return;
    	}
    	
    	enforceMask = true;
    	
    	/*
    	 * Create default picture with spaces so we can 
    	 * identify when the user wants to null out the field
    	 */
    	pic = new StringBuffer();
    	for(char mc : mask.toCharArray()) {
    		switch (mc) {
    			case '9' :
    			case 'X' :
    				pic.append(" ");
    				break;
    			default :
    				pic.append(mc);
    		}
    	}
    	picture = pic.toString();
    	
    	/*
    	 * If mask has been previously enabled we don't want or need to 
    	 * re-add the key handlers
    	 */
    	if(keyDown == null) {
    		/*
    		 * Delete and BackSpace keys are handled in KeyDown because Chrome and IE do not 
    		 * pass these keys to the KeyPressEvent.  
    		 */
    		keyDown = box.addKeyDownHandler(new KeyDownHandler() {
    			public void onKeyDown(KeyDownEvent event) {
    				if(!box.isReadOnly())
    					maskKeyDown(event);
    			}

    		});

    		/*
    		 * Masks are applied in all browsers in KeyPressEvent because it is the only method that will allow us to
    		 * view the typed character before it being applied to the textbox itself. 
    		 */
    		keyPress = box.addKeyPressHandler(new KeyPressHandler() {
    			public void onKeyPress(KeyPressEvent event) {
    				if(!box.isReadOnly())
    					maskKeyPress(event);
    			}

    		});
    		
    	}
    	
    	box.setMaxLength(mask.length());
    }
    	
    
    /**
     * Method to allow wrapping widget to toggle enforcing the setMask.  Used primarily for widgets
     * that participate in Query.
     * @param enforce
     */
    public void enforceMask(boolean enforce) {
        enforceMask = enforce;
        if(mask == null)
            return;
    	
    	if(enforce)
    	    box.setMaxLength(mask.length());
    	else
    	    box.setMaxLength(255);
    }
    
    public boolean isMaskEnforced() {
    	return mask != null && enforceMask;
    }
    
    protected void maskKeyDown(KeyDownEvent event) {
		String input;
		char mc;
		int cursor,selectStart,selectEnd;
		StringBuffer applied;

		/*
		 * If return if mask is not be enforced such as when in Query Mode;
		 */
		if(!isMaskEnforced())
			return;
		
		if((event.isAnyModifierKeyDown() && !event.isShiftKeyDown()) ||
		   (event.getNativeKeyCode() >= KeyCodes.KEY_F1 && event.getNativeKeyCode() <= KeyCodes.KEY_F12))
			return;

		input = getText();  // Current state of the Textbox including selection.

		cursor = box.getCursorPos(); //Current position of cursor when key was pressed.

		/*
		 * If backspace or delete key is hit we want to blank out the current positon and return
		 * if backspacing or deleting a mask literal the literal will be re-asserted and the text
		 * will not change 
		 */
		if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_BACKSPACE || 
				event.getNativeEvent().getKeyCode() == KeyCodes.KEY_DELETE) {


			/*
			 * If part of the text is selected we want to blank out the selection preserving any mask literals 
			 * and the current length of length of textbox input. 
			 */
			if(box.getSelectionLength() > 0) {
				applied = new StringBuffer();

				selectStart = getText().indexOf(box.getSelectedText());  // Start position of selection
				selectEnd = selectStart + box.getSelectionLength();              // End positon of selection.

				applied.append(input.substring(0, selectStart));  // Copy the start of the input up to the start of selection into buffer.

				/*
				 * Loop through the selected portion and either blank out or insert mask literals
				 */
				for(int i = 0; i < box.getSelectionLength(); i++) {
					if(mask.toCharArray()[applied.length()] == '9' || mask.toCharArray()[applied.length()] == 'X')
						applied.append(" ");
					else
						applied.append(mask.toCharArray()[applied.length()]);
				}

				applied.append(input.substring(selectEnd));    // Copy the portion of input after selection in to the buffer

				input = applied.toString();                    // Set input to the buffer so that the inputed char can be inserted

				cursor = selectStart;                          // Set the Cursor to beginning of selection since this is where we want start
			}

			applied = new StringBuffer();

			/*
			 * Subtract 1 from cursor if backspace and then can be treated like delete
			 */
			if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_BACKSPACE)
				cursor--;

			if(cursor  >= 0) {
			
			    mc = mask.charAt(cursor);  // get current mask char based on cursor

			    /*
			     * if mask position is not a literal we will remove the char
			     * and replace with blank.  If it is a literal we want to just echo 
			     * what is in the textbox.
			     */
			    if(mc == '9' || mc == 'X') {
			        applied.append(input.substring(0,cursor));
			        applied.append(" ");
			        applied.append(input.substring(cursor+1));
			    }else {
			        applied.append(input); 
			    }
			}else {
			    applied.append(input);
			}

			/*
			 * Set new Text and cursor position into widget
			 */
			box.setText(applied.toString());
			box.setCursorPos(cursor > -1 ? cursor : 0);
			
		    /*
    	     * KeyPressEvent occurs before the browser applies changes to the textbox.
		     *  We stop propagation and default since we already set the changes we wanted
		     * otherwise the typed char would be repeated
		     */
		    event.preventDefault();
	        event.stopPropagation();
		}
	}
    
    protected void maskKeyPress(KeyPressEvent event) {
		String input;
		int cursor,selectStart,selectEnd;
		char ch,mc;
		StringBuffer applied;

		boolean loop;

		/*
		 * If return if mask is not be enforced such as when in Query Mode;
		 */
		if(!isMaskEnforced())
			return;
		
		if((event.isAltKeyDown() || event.isControlKeyDown() || event.isMetaKeyDown()) ||
	       (event.getNativeEvent().getKeyCode() >= KeyCodes.KEY_F1 && event.getNativeEvent().getKeyCode() <= KeyCodes.KEY_F12))
			return;

		input = getText();  // Current state of the Textbox including selection.

		cursor = box.getCursorPos(); //Current position of cursor when key was pressed.
        
		selectStart = cursor;
        
		/*
		 * If part of the text is selected we want to blank out the selection preserving any mask literals 
		 * and the current length of length of textbox input. 
		 */
		if(box.getSelectionLength() > 0) {
			applied = new StringBuffer();

			selectStart = getText().indexOf(box.getSelectedText());  // Start position of selection
			selectEnd = selectStart + box.getSelectionLength();              // End positon of selection.

			applied.append(input.substring(0, selectStart));  // Copy the start of the input up to the start of selection into buffer.

			/*
			 * Loop through the selected portion and either blank out or insert mask literals
			 */
			for(int i = 0; i < box.getSelectionLength(); i++) {
				if(mask.toCharArray()[applied.length()] == '9' || mask.toCharArray()[applied.length()] == 'X')
					applied.append(" ");
				else
					applied.append(mask.toCharArray()[applied.length()]);
			}

			applied.append(input.substring(selectEnd));    // Copy the portion of input after selection in to the buffer

			input = applied.toString();                    // Set input to the buffer so that the inputed char can be inserted

			cursor = selectStart;                          // Set the Cursor to beginning of selection since this is where we want start
		}

		applied = new StringBuffer();                      // Get new Buffer
		
		/*
		 * Do nothing if navigating or tabing out of box
		 */
		if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_TAB ||
				event.getNativeEvent().getKeyCode() == KeyCodes.KEY_LEFT ||
				event.getNativeEvent().getKeyCode() == KeyCodes.KEY_RIGHT ||
				event.getNativeEvent().getKeyCode() == KeyCodes.KEY_DELETE ||
				event.getNativeEvent().getKeyCode() == KeyCodes.KEY_BACKSPACE ||
				event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER )
			return;


		ch = event.getUnicodeCharCode() == 0 ? (char)event.getNativeEvent().getKeyCode() : event.getCharCode();   // character typed by user
		
		applied.append(input.substring(0,cursor));  // Copy the portion of input up to the cursor to the buffer

		/*
		 * This event is before the textbox's check for Max Length so we need to do the check 
		 */
		if(applied.length() >= mask.length())
			return;

		mc = mask.charAt(applied.length());  // Get the Mask char for the position typed

		/*
		 * Perfrom switch at least once possibly more if literals are to be inserted
		 */
		do {
			loop = false;
			switch(mc) {
			case '9' : 
				if(Character.isDigit(ch))    // if input matches add to buffer 
					applied.append(ch);
				else {
					applied = new StringBuffer(input);
					cursor = input.length() - 1;     // Decrement cursor and through input if not right type
				}
				break;
			case 'X' :
				if(Character.isLetterOrDigit(ch)) // if input matches add to buffer 
					applied.append(ch);
				else {
					applied = new StringBuffer(input);
					cursor = input.length() - 1;    // Decrement cursor and through input if not right type
				}
				break;
			default :
				applied.append(mc);         // Apply literal from mask always in this case

				/*
				 * if inputed char does not match literal then we 
				 * want to loop again to try and match and apply the input to the 
				 * next position in the mask
				 */
				if(mc != ch) {
					loop = true;
					mc = mask.charAt(applied.length());
					cursor++;  
				}
			}
		}while (loop);

		cursor++;  // Advance cursor

		/*
		 * If cursor is not at end of input copy the the rest of input to buffer
		 */
		if(cursor < input.length())
			applied.append(input.substring(cursor));

		/*
		 * Set new Text and cursor position into widget
		 */
		box.setText(applied.toString());
		box.setCursorPos(cursor);

		/*
		 * KeyPressEvent occurs before the browser applies changes to the textbox.
		 * We stop propogation and defualt since we already set the changes we wanted
		 * other wise the typed char would be repeated
		 */
		event.preventDefault();
		event.stopPropagation();
	}
    
    
    private String applyMask(String text) {
    	boolean loop;
    	StringBuffer applied = new StringBuffer();
    	int maskCursor = 0,textCursor = 0;
    	
    	if(!isMaskEnforced())
    		return text;
    	
    	while(textCursor < text.length() && maskCursor < mask.length()) {

    		char mc = mask.charAt(maskCursor);  // Get the Mask char for the position typed
    		char ch = text.charAt(textCursor);

    		/*
    		 * Perfrom switch at least once possibly more if literals are to be inserted
    		 */
    		do {
    			loop = false;
    			switch(mc) {
    			case '9' : 
    				if(Character.isDigit(ch))    // if input matches add to buffer 
    					applied.append(ch);
    				break;
    			case 'X' :
    				if(Character.isLetterOrDigit(ch)) // if input matches add to buffer 
    					applied.append(ch);
    				break;
    			default :
    				applied.append(mc);         // Apply literal from mask always in this case

    				/*
    				 * if inputed char does not match literal then we 
    				 * want to loop again to try and match and apply the input to the 
    				 * next position in the mask
    				 */
    				if(mc != ch) {
    					loop = true;
    					maskCursor++;
    					mc = mask.charAt(maskCursor); 
    				}
    			}
    		}while (loop);
    		
    		textCursor++;
    		maskCursor++;
    	}
    	
    	return applied.toString();
    }
	
    public void setCSS(TextCSS css) {
    	css.ensureInjected();
    	this.css = css;
    }
    
    
    /*
     *  Methods below here are pass-through to box to complete the composition 
     */
    
    public void setEnabled(boolean enabled) {
        box.setReadOnly(!enabled);
    }

    public boolean isEnabled() {
        return !box.isReadOnly();
    }
    
    public void setSelectionRange(int pos, int length) {
        box.setSelectionRange(pos, length);
    }
    
    public void setFocus(boolean focused) {
        box.setFocus(focused);
    }
    
    public void setText(String text) {
    	text = applyMask(text);
    	
    	if(getMaxLength() > -1)
    		text = text.substring(0,getMaxLength());
        
    	box.setText(text);
    }
    
    public void setReadOnly(boolean readOnly) {
        box.setReadOnly(readOnly);
    }
    
    public int getSelectionLength() {
        return box.getSelectionLength();
    }
    
    public boolean isReadOnly() {
        return box.isReadOnly();
    }
    
    public void selectAll() {
        box.selectAll();
    }
    
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return box.addFocusHandler(handler);
    }
    
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return box.addBlurHandler(handler);
    }
    
    public void setMaxLength(final int length) {
    	box.getElement().setAttribute("maxLength", String.valueOf(length));       
    }
    
    public int getMaxLength() {
    	return box.getMaxLength();
    }
    
    public void setAlignment(TextBox.TextAlignment align) {
        box.setAlignment(alignment);
    }
    
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return box.addKeyUpHandler(handler);
    }
    
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return box.addKeyPressHandler(handler);
    }
    
}
