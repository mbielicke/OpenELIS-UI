package org.openelis.ui.widget.fileupload;

import com.google.gwt.core.client.JavaScriptObject;

public class FormData extends JavaScriptObject {
	
	protected FormData() {
		
	}
	
	public static final native FormData create() /*-{
		return new FormData();
	}-*/;
	
	public final native void append(String name, File file) /*-{
		this.append(name,file);
	}-*/;
	
	public final native void append(String name, File file, String fileName) /*-{
		this.append(name,file,fileName);
	}-*/;
	
	public final native void append(String name, String value) /*-{
		this.append(name,value);
	}-*/;
	
	public final native void send(String url, final Callback callback) /*-{
		var request = new XMLHttpRequest();
		request.open("POST", url);
		request.onload = function(e) {
			if(request.status == 200)
				callback.@org.openelis.ui.widget.fileupload.FormData.Callback::success()();
			else
			    callback.@org.openelis.ui.widget.fileupload.FormData.Callback::failure()();
		};
		request.send(this);
	}-*/;
	
    public final native void send(String url) /*-{
		var request = new XMLHttpRequest();
		request.open("POST", url);
		request.send(this);
	}-*/;
	
	public static interface Callback {
		void success();
		void failure();
	}
}
