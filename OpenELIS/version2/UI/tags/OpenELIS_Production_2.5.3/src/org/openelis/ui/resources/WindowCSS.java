package org.openelis.ui.resources;

import com.google.gwt.resources.client.CssResource;

public interface WindowCSS extends CssResource {
	
	String Caption();
	String ScreenWindowLabel();
	String CloseButton();
	String MinimizeButton();
	String StatusBar();
	String WindowBody();
	String WindowPanel();
	String LegacyWindowPanel();
	String ScreenLoad();
	String warnPopupLabel();
	String errorPopupLabel();
	String GlassPanel();
	String spinnerIcon();
	String ErrorPanel();
	String unfocused();
    String ModalPanel();
    String WarnIcon();
    String ErrorIcon();
    String InputError();
    String InputWarning();
    String top();
    String Resizer();
    String NoSelect();
    String ResizeWindow();
    String MaximizeButton();
    String RestoreButton();
    String North();
    String East();
    String West();
    String South();
    int borderWidth();
}
