package org.openelis.ui.widget;

import java.io.Serializable;

import org.openelis.ui.resources.LinkCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;

public class Link extends Composite {
    
    Label<String> label;
    Details       details;
    LinkCSS       css;
  
    public Link() {
        label = new Label<String>();
        details = new Details();
        css = UIResources.INSTANCE.link();
        css.ensureInjected();
        
        label.setStyleName(css.Link());
        
        initWidget(new Label<String>());
        
        label.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(details.url, details.name, details.features);
            }
        });
    }
    
    public Link(Details details) {
        this();
        setDetails(details);
    }
    
    public Link(String text, String url) {
        this();
        details.text = text;
        this.label.setValue(text);
        details.url = url;
    }
    
    public Link(String text, String url, String name, String features) {
        this(text,url);
        details.name = name;
        details.features =  features;
    }

    public Details getDetails() {
        return details;
    }
    
    public void setDetails(Details details) {
        this.details = details;
        label.setValue(details.text);
    }
    
    public static class Details implements Serializable {

        private static final long serialVersionUID = 1L;
        
        public String text,url,name,features;

        public Details() {
            
        }
        
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFeatures() {
            return features;
        }

        public void setFeatures(String features) {
            this.features = features;
        }
        
    }

}
