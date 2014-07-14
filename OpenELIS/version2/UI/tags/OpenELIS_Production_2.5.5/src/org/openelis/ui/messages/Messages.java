package org.openelis.ui.messages;




public class Messages {

	private static UIMessages consts;
	
	public static UIMessages get() {
		if(consts == null)
			consts = com.google.gwt.core.shared.GWT.create(UIMessages.class);
		
		return consts;
	}
	
	private Messages(){}
}
