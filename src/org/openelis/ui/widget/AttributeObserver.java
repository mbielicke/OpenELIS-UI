/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.ui.widget;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * @author tschmidt
 *
 */
public class AttributeObserver {
    
    JavaScriptObject observer;
    
    public AttributeObserver() {
        observer = getObserver(getMutationFunction());
    }
    
    protected native JavaScriptObject getMutationFunction() /*-{
        return function(mutations) {
            mutations.forEach(function(mutation) {
                var old = mutation.oldValue;
                var display = mutation.target.style.display;
                if(mutation.attributeName == 'style')
                @org.openelis.ui.widget.AttributeObserver::detectVisibleChange(Ljava/lang/String;Ljava/lang/String;)(old,display);
            });
        }
    }-*/;
    
    protected native JavaScriptObject getObserver(JavaScriptObject func) /*-{
       return new MutationObserver(func);
    }-*/;

    public native void observe(Element element) /*-{
        this.@org.openelis.ui.widget.AttributeObserver::observer.observe( element, { attributes: true, attributeOldValue : true, attributeFilter: ['style']})
    }-*/;
    
    protected static void detectVisibleChange(String oldValue,String display) {
        if(oldValue.contains("display: none")) {
            if("".equals(display))
                com.google.gwt.user.client.Window.alert("Made visible");
        }else if("none".equals(display)) {
            com.google.gwt.user.client.Window.alert("Made invisible");
        }
    }
}
