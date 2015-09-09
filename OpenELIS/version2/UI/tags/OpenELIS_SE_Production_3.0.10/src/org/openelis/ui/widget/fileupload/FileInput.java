package org.openelis.ui.widget.fileupload;

import org.openelis.ui.widget.ScreenWidgetInt;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This widget wraps another widget for display and will bring up the file 
 * open dialog when enabled and clicked.
 *
 */
public class FileInput extends FocusPanel {
	
	protected FileUpload fileLoad;
	protected ScreenWidgetInt widget;
	protected boolean enabled;
	protected FormData formData;
	protected String sendUrl;
	protected FormData.Callback callback;
	
	public FileInput() {
		getFileLoad();
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (enabled) {
					fileLoad.click();
				}
			}
		});
	}
	
	/*
	 * We set one <input type="file"> element in a document.  This method gets the 
	 * element from the document and wraps it to receive events if present, or creates
	 * the element and puts it in the document. 
	 */
	private void getFileLoad() {
		Element el;
		
		el = Document.get().getElementById("fileload");
		if (el != null) {
			fileLoad = FileUpload.wrap(el);
		} else {
			fileLoad = new FileUpload();
			RootPanel.get().add(fileLoad);
			fileLoad.getElement().setId("fileload");
			fileLoad.setVisible(false);
		}
		
		fileLoad.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				File[] files;
				
				files = File.getFiles(fileLoad.getElement().cast());
				formData = FormData.create();
				for(File file : files) {
					formData.append("file", file, file.name());
				}
				if (callback != null) {
					formData.send(sendUrl,callback);
				} else {
					formData.send(sendUrl);
				}
			}
		});
	}
	
	/**
	 * This method will also disable or enable the wrapped widget if it implements
	 * ScreenWidgetInt.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (widget != null) {
			widget.setEnabled(enabled);
		}
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
	
	public void addFormDataCallback(FormData.Callback callback) {
		this.callback = callback;
	}

	@Override
	public void add(Widget w) {
		super.add(w);
		if (w instanceof ScreenWidgetInt) {
			widget = (ScreenWidgetInt)w;
		}
	}

}
