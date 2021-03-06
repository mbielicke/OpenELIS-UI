package org.openelis.ui.widget;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;

/**
 * This interface will be implemented by widgets that need exceptions 
 * to be assigned to them and displayed by the ExceptionHelper.
 * @author tschmidt
 *
 */
public interface HasExceptions extends HasMouseOverHandlers, HasMouseOutHandlers {

    /**
     * Convenience method to check if a widget has exceptions so we do not need
     * to go through the cost of merging the logical and validation exceptions
     * in the getExceptions method.
     * 
     * @return
     */
    public boolean hasExceptions();

    /**
     * Method for programmer to logical exception to the widget
     * 
     * @param exception
     */
    public void addException(Exception exception);

    /**
     * Returns a list of logical exceptions to be displayed
     * 
     * @return
     */
    public ArrayList<Exception> getEndUserExceptions();

    /**
     * Returns a list of validation exceptions to be displayed
     * 
     * @return
     */
    public ArrayList<Exception> getValidateExceptions();

    /**
     * Clears both Logical and validation exceptions list.
     */
    public void clearExceptions();
    
    /**
     * Clears only exceptions added by the programmer
     */
    public void clearEndUserExceptions();
    
    /**
     * Clears only the excpetions add by widgets
     */
    public void clearValidateExceptions();

    /**
     * Used to add an exception style to a widget. A composite widget may need
     * special handling to add the Style name in the correct place. For instance
     * Dropdown would want to add the style to the textbox portion only so the
     * red line does not extend to under the drop arrow as well
     * 
     * @param style
     */
    public void addExceptionStyle();

    /**
     * Removes the exception style from the widget.
     * 
     * @param style
     */
    public void removeExceptionStyle();

}
