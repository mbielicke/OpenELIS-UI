package org.openelis.ui.widget.fileupload;

import java.util.Iterator;

import org.openelis.ui.widget.ScreenWidgetInt;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileInput extends FocusWidget implements HasWidgets {
	
	FileUpload element;
	ScreenWidgetInt widget;
	boolean enabled;
	protected FormData formData;
	protected String sendUrl;
	protected FormData.Callback callback;
	
	public FileInput() {
		getFileLoad();
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (enabled) {
					element.click();
				}
			}
		});
	}
	
	private void getFileLoad() {
		Element el;
		
		el = Document.get().getElementById("fileload");
		if (el != null) {
			element = element.wrap(el);
		} else {
			element = new FileUpload();
			RootPanel.get().add(element);
			element.getElement().setId("fileload");
			element.setVisible(false);
		}
		
		element.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				File[] files = FileDrop.getFiles(element.getElement().cast());
				
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
		setElement(w.getElement());
		if (w instanceof ScreenWidgetInt) {
			widget = (ScreenWidgetInt)w;
		}
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Widget> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}
}
