package org.openelis.ui.messages;



import com.google.gwt.core.client.GWT;

public class Messages {

	private static UIMessages consts;
	
	public static UIMessages get() {
		if(consts == null)
			consts = GWT.create(UIMessages.class);
		
		return consts;
	}
	
	private Messages(){}
}
