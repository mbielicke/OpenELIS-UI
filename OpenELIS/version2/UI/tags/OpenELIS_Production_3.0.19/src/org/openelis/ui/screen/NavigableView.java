package org.openelis.ui.screen;

import static org.openelis.ui.screen.State.DEFAULT;
import static org.openelis.ui.screen.State.DISPLAY;

import org.openelis.ui.annotation.Enable;
import org.openelis.ui.annotation.Permission;
import org.openelis.ui.annotation.Shortcut;
import org.openelis.ui.widget.AtoZButtons;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.Menu;
import org.openelis.ui.widget.MenuItem;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.uibinder.client.UiField;

public abstract class NavigableView<T> extends View<T> {
	
	@UiField
    @Enable({State.DEFAULT,State.DISPLAY,State.QUERY})
	@Permission(org.openelis.ui.screen.Permission.SELECT)
    @Shortcut("q")
    public Button query;
    
	@UiField
    @Enable({State.DISPLAY})
    @Shortcut("p")
    public Button previous;
    
	@UiField
    @Enable({State.DISPLAY})
    @Shortcut("n")
    public Button next;
    
	@UiField
    @Enable({State.DEFAULT,State.DISPLAY,State.ADD})
	@Permission(org.openelis.ui.screen.Permission.ADD)
    @Shortcut("a")
    public Button add;
    
	@UiField
    @Enable({State.DISPLAY,State.UPDATE})
	@Permission(org.openelis.ui.screen.Permission.UPDATE)
    @Shortcut("u")
    public Button update;
    
	@UiField
    @Enable({State.QUERY,State.ADD,State.UPDATE})
    @Shortcut("m")
    public Button commit;
    
	@UiField
    @Enable({State.ADD,State.UPDATE,State.QUERY})
    @Shortcut("o")
    public Button abort;
    
	@UiField
    public Button loadResults; 
    
    @UiField
    public AtoZButtons atozButtons;

    @UiField
    public Table atozTable;
    
    protected Presenter<T> presenter;
    
    public void setState(State state) {
        switch (state) {
        	case QUERY : 
        		query.lock();
        		query.setPressed(true);
        		break;
        	case ADD : 
        		add.lock();
        		add.setPressed(true);
        		break;
        	case UPDATE :
        		update.lock();
        		update.setPressed(true);
        		break;
        	default :
        }
        boolean enable;
        enable = isState(DEFAULT, DISPLAY).contains(state) && presenter.permissions().hasSelectPermission();
        atozButtons.setEnabled(enable);
    }

}
