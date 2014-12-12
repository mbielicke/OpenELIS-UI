package org.openelis.ui.widget;

import org.openelis.ui.common.Util;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class CSSUtils {

	public static native String getStyleProperty(Element el, String prop) /*-{
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

	public static native String getStyle(Element el, String prop) /*-{
		var style = el.style[prop];
		return style;
	}-*/;

	public static double getAddedBorderWidth(Element el) {
		return Util.stripPX(getStyleProperty(el, "borderLeftWidth"))
				+ Util.stripPX(getStyleProperty(el, "borderRightWidth"));
	}

	public static double getAddedBorderHeight(Element el) {
		return Util.stripPX(getStyleProperty(el, "borderTopWidth"))
				+ Util.stripPX(getStyleProperty(el, "borderBottomWidth"));
	}

	public static double getAddedBorderWidth(
			com.google.gwt.dom.client.Element el) {
		return Util.stripPX(getStyleProperty(el, "borderLeftWidth"))
				+ Util.stripPX(getStyleProperty(el, "borderRightWidth"));
	}

	public static double getAddedBorderHeight(
			com.google.gwt.dom.client.Element el) {
		return Util.stripPX(getStyleProperty(el, "borderTopWidth"))
				+ Util.stripPX(getStyleProperty(el, "borderBottomWidth"));
	}

	public static double getWidth(Element el) {
		return Util.stripPX(getStyleProperty(el, "width"));
	}

	public static double getHeight(Element el) {
		return Util.stripPX(getStyleProperty(el, "height"));
	}

	public static double getWidth(com.google.gwt.dom.client.Element el) {
		return Util.stripPX(getStyleProperty(el, "width"));
	}

	public static double getHeight(com.google.gwt.dom.client.Element el) {
		return Util.stripPX(getStyleProperty(el, "height"));
	}

	public static double getWidth(Widget widget) {
		return getWidth(widget.getElement());
	}

	public static double getHeight(Widget widget) {
		return getHeight(widget.getElement());
	}

	public static String getStyle(com.google.gwt.dom.client.Element el,
			String prop) {
		return getStyle((Element) el, prop);
	}

	public static String getStyleProperty(com.google.gwt.dom.client.Element el,
			String prop) {
		return getStyleProperty((Element) el, prop);
	}

	public static double getAddedMarginWidth(
			com.google.gwt.dom.client.Element el) {
		return Util.stripPX(el.getStyle().getMarginRight())
				+ Util.stripPX(el.getStyle().getMarginLeft());
	}

	public static double getAddPaddingWidth(com.google.gwt.dom.client.Element el) {
		return Util.stripPX(el.getStyle().getPaddingLeft())
				+ Util.stripPX(el.getStyle().getPaddingRight());
	}

	public static double getAddPaddingHeight(
			com.google.gwt.dom.client.Element el) {
		return Util.stripPX(el.getStyle().getPaddingTop())
				+ Util.stripPX(el.getStyle().getPaddingBottom());
	}
}
