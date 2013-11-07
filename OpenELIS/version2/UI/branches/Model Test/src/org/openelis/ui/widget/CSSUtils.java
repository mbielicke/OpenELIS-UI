package org.openelis.ui.widget;

import org.openelis.ui.common.Util;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class CSSUtils {

    public static native String getStyleProperty(Element el, String prop)  /*-{ 
        var computedStyle;
        if (document.defaultView && document.defaultView.getComputedStyle) { // standard (includes ie9)
          computedStyle = document.defaultView.getComputedStyle(el, null)[prop];
    
        } else if (el.currentStyle) { // IE older
          computedStyle = el.currentStyle[prop];
    
        } else { // inline style
          computedStyle = el.style[prop];
        }
        return computedStyle;
   }-*/;
  
   public static native String getStyle(Element el, String prop)  /*-{ 
        var style = el.style[prop];
        return style;
   }-*/;
   
   public static int getAddedBorderWidth(Element el) {
       return Util.stripUnits(getStyleProperty(el, "borderLeftWidth")) + 
              Util.stripUnits(getStyleProperty(el,"borderRightWidth"));
   }
   
   public static int getAddedBorderHeight(Element el) {
       return Util.stripUnits(getStyleProperty(el,"borderTopWidth")) +
              Util.stripUnits(getStyleProperty(el,"borderBottomWidth"));
   }
   
   public static int getAddedBorderWidth(com.google.gwt.dom.client.Element el) {
       return Util.stripUnits(getStyleProperty(el, "borderLeftWidth")) + 
              Util.stripUnits(getStyleProperty(el,"borderRightWidth"));
   }
   
   public static int getAddedBorderHeight(com.google.gwt.dom.client.Element el) {
       return Util.stripUnits(getStyleProperty(el,"borderTopWidth")) +
              Util.stripUnits(getStyleProperty(el,"borderBottomWidth"));
   }
   
   public static int getWidth(Element el) {
       return Util.stripUnits(getStyleProperty(el,"width"));
   }
   
   public static int getHeight(Element el) {
       return Util.stripUnits(getStyleProperty(el,"height"));
   }
   
   public static int getWidth(com.google.gwt.dom.client.Element el) {
       return Util.stripUnits(getStyleProperty(el,"width"));
   }
   
   public static int getHeight(com.google.gwt.dom.client.Element el) {
       return Util.stripUnits(getStyleProperty(el,"height"));
   }
   
   public static int getWidth(Widget widget) {
       return getWidth(widget.getElement());
   }
   
   public static int getHeight(Widget widget) {
       return getHeight(widget.getElement());
   }
   
   public static String getStyle(com.google.gwt.dom.client.Element el, String prop) {
       return getStyle((Element)el,prop);
   }
   
   public static String getStyleProperty(com.google.gwt.dom.client.Element el,String prop) {
       return getStyleProperty((Element)el,prop);
   }
}
