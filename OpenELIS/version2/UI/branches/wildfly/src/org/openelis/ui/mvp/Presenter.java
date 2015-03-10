package org.openelis.ui.mvp;

import org.openelis.ui.common.ModulePermission;
import org.openelis.ui.screen.State;

import com.google.gwt.user.client.ui.ResizeComposite;

public abstract class Presenter extends ResizeComposite{
	
	public State state;
	public abstract ModulePermission permissions();
}
