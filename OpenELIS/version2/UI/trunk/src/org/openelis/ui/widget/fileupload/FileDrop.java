package org.openelis.ui.widget.fileupload;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.HasDragEnterHandlers;
import com.google.gwt.event.dom.client.HasDragLeaveHandlers;
import com.google.gwt.event.dom.client.HasDropHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 *  This class is used to make a widget accept dropped files that can be uploaded to a URL. 
 */
public class FileDrop extends Widget implements DropHandler, DragEnterHandler, DragLeaveHandler, DragOverHandler, HasDragEnterHandlers, HasDragLeaveHandlers, HasDropHandlers {
	
	protected FormData formData;
	protected boolean sendAuto,enabled = true;
	protected String sendUrl;
	protected Widget dropArea;
	protected int x0,x1,y0,y1;
	boolean over;
	
	HandlerRegistration dragEnter, dragLeave;
	/**
	 * Constructor that accepts a widget to enable as an area to drop files.  The url param
	 * is where the files will be uploaded to.
	 * 
	 * @param dropArea
	 * @param url
	 */
	public FileDrop(Widget dropArea, String url) {
		this(dropArea,url,true);
	}
	
	/**
	 * This constructor can be used to override the default behavior of sending files as soon
	 * as they are dropped by passing false in the sendAuto param.  The send() method must then 
	 * be called to upload the files
	 * 
	 * @param dropArea
	 * @param url
	 * @param sendAuto
	 */
	public FileDrop(Widget dropArea, String url, boolean sendAuto) {
		formData = FormData.create();
		this.sendAuto = sendAuto;
		this.sendUrl = url;
		this.dropArea = dropArea;

		dropArea.addDomHandler(this, DropEvent.getType());
		
		dragEnter = dropArea.addDomHandler(this, DragEnterEvent.getType());
		
		dropArea.addDomHandler(this, DragOverEvent.getType());
		
		dragLeave = dropArea.addDomHandler(this, DragLeaveEvent.getType());
		
	}

	/**
	 * This method is implemented as part of the DropHandler interface.  It
	 * can be overridden by screens if necessary. 
	 */
	@Override
	public void onDrop(DropEvent event) {
		event.stopPropagation();
		event.preventDefault();
		
		if(!enabled)
			return;
		
		over = false;

		File[] files = getFiles(event.getDataTransfer());
		
		if(sendAuto)
			formData = FormData.create();
		
		for(File file : files) {
			formData.append("file", file, file.name());
		}
		
		if(sendAuto)
			formData.send(sendUrl);
		
		//DOM.releaseCapture(dropArea.getElement());
	}
	
	/**
	 * This method is implemented as part of the DragEnter interface.  It can be overridden 
	 * to code some visual cue to the user to know the have entered a drop area 
	 */
	@Override
	public final void onDragEnter(DragEnterEvent event) {
		event.preventDefault();
		event.stopPropagation();
		
		if(enabled && !over) {
			over = true;
			DragEnterEvent.fireNativeEvent(event.getNativeEvent(), this);
		}
	}
	
	@Override
	public void onDragOver(DragOverEvent event ) {
		event.preventDefault();
		event.stopPropagation();
	}
	
	@Override
	public final void onDragLeave(DragLeaveEvent event) {
		event.preventDefault();
		event.stopPropagation();
		
		if(enabled && !overDrop(event.getNativeEvent().getScreenX(),
				                event.getNativeEvent().getScreenY())) {
			over = false;
			DragLeaveEvent.fireNativeEvent(event.getNativeEvent(), this);
		}
	}

	
	/**
	 * This method can be called to send all files that have been dropped to the url that 
	 * that is set in sendUrl.  Only necessary if sendAuto has been set to false.
	 */
	public void send() {
		assert sendUrl != null;
		
		formData.send(sendUrl);
	}
	
	/**
	 * This method can be called to send all files that have been dropped to the url that 
	 * is set in sendUrl with a callback that will notify the caller of success or failure.
	 * @param callback
	 */
	public void send(FormData.Callback callback) {
		assert sendUrl != null;
		
		formData.send(sendUrl,callback);
	}

	/**
	 * Method enables to disables the drop area
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Returns true if the dropArea is enabled to receive dropped files
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Sets the URL to send the dropped files to
	 * @param url
	 */
	public void setSendUrl(String url) {
		this.sendUrl = url;
	}
	
	/**
	 * Returns the currently set URL for the dropped files
	 * @return
	 */
	public String getSendUrl() {
		return sendUrl;
	}
	
	/**
	 * Pass true if the files should be sent as soon as they are dropped
	 * @param sendAuto
	 */
	public void setSendOnDrop(boolean sendAuto) {
		this.sendAuto = sendAuto;
	}
	
	/**
	 * Returns true if if the files will be sent on drop
	 * @return
	 */
	public boolean isSendOnDrop() {
		return sendAuto;
	}
	
    protected static native File[] getFiles(JavaScriptObject transfer) /*-{
    	return transfer.files;
    }-*/;


    private void getCoordinates() {
    	x0 = dropArea.getAbsoluteLeft();
    	x1 = x0 + dropArea.getOffsetWidth();
    	y0 = dropArea.getAbsoluteTop();
    	y1 = y0 + dropArea.getOffsetHeight();
    }
    
    private boolean overDrop(int x, int y) {
    	getCoordinates();
    	return x > x0 && x < x1 && y > y0 && y < y1;
    }
    
	@Override
	public HandlerRegistration addDropHandler(DropHandler handler) {
		return addHandler(handler, DropEvent.getType());
	}

	@Override
	public HandlerRegistration addDragLeaveHandler(DragLeaveHandler handler) {
		return addHandler(handler, DragLeaveEvent.getType());
	}

	@Override
	public HandlerRegistration addDragEnterHandler(DragEnterHandler handler) {
		return addHandler(handler, DragEnterEvent.getType());
	}
	
}
