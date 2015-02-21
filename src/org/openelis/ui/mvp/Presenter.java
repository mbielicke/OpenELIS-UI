package org.openelis.ui.mvp;

import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.screen.State;

public abstract class Presenter {
	
	public State state;
	public abstract ModulePermission permissions();
}
