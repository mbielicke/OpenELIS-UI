package org.openelis.ui.widget;

import org.openelis.ui.resources.UIResources;

public class PortalStatus extends Button {
			
	public PortalStatus() {
		setCss(UIResources.INSTANCE.portalStatus());
	}
	
	public void setBusy(String message) {
		right.removeAllChildren();
		setImage(UIResources.INSTANCE.spinnerGlobe());
		setPixelSize(30,30);
		setRightText(message);
	}	
	
	public void setError(String message) {
		right.removeAllChildren();
		setImage(UIResources.INSTANCE.attention());
		setPixelSize(30,30);
		setRightText(message);
	}

}
