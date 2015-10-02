package org.openelis.ui.screen;

import org.openelis.ui.annotation.Enable;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;

import com.google.gwt.uibinder.client.UiField;

public abstract class NavigableViewWithHistory<T> extends NavigableView<T> {
	
	@UiField
    @Enable(State.DISPLAY)
    public Button optionsButton;

	@UiField
    @Enable(State.DISPLAY)
    public  Menu optionsMenu;
    
	@UiField
    @Enable(State.DISPLAY)
    public MenuItem history;
}
