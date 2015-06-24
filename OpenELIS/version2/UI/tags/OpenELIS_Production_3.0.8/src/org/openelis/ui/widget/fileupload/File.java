package org.openelis.ui.widget.fileupload;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;

public class File extends JavaScriptObject {
	
	protected File() {
		
	}
	
	public final native Date lastModifiedDate() /*-{
		return this.lastModifiedDate;
	}-*/;
	
	public final native String name() /*-{
		return this.name;
	}-*/;
	
	public final native int size() /*-{
		return this.size;
	}-*/;
	
	public final native String type() /*-{
		return this.type;
	}-*/;
	
    protected static native File[] getFiles(JavaScriptObject transfer) /*-{
		return transfer.files;
	}-*/;
}
