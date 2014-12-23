package org.openelis.ui.widget;

import static org.openelis.ui.common.Util.stripPX;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class CSSUtils {

	public static String BORDER_TOP_WIDTH = "borderTopWidth";
	public static String BORDER_BOTTOM_WIDTH = "borderBottomWidth";
	public static String BORDER_LEFT_WIDTH = "borderLeftWidth";
	public static String BORDER_RIGHT_WIDTH = "borderRightWidth";
	public static String PADDING_LEFT = "paddingLeft";
	public static String PADDING_RIGHT = "paddingRight";
	public static String PADDING_TOP = "paddingTop";
	public static String PADDING_BOTTOM = "paddingBottom";
	public static String MARGIN_TOP = "marginTop";
	public static String MARGIN_BOTTOM = "marginBottom";
	public static String MARGIN_LEFT = "marginLeft";
	public static String MARGIN_RIGHT = "marginRight";
	
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


	public static double getAddedBorderWidth(Element el) {
		return stripPX(getStyleProperty(el, BORDER_LEFT_WIDTH))
				+ stripPX(getStyleProperty(el, BORDER_RIGHT_WIDTH));
	}

	public static double getAddedBorderHeight(Element el) {
		return stripPX(getStyleProperty(el, BORDER_TOP_WIDTH))
				+ stripPX(getStyleProperty(el, BORDER_BOTTOM_WIDTH));
	}

	public static double getAddedPaddingWidth(Element el) {
		return stripPX(getStyleProperty(el,PADDING_LEFT)) +
			   stripPX(getStyleProperty(el, PADDING_RIGHT));
	}
	
	public static double getAddedPaddingHeight(Element el) {
		return stripPX(getStyleProperty(el,PADDING_BOTTOM)) +
			   stripPX(getStyleProperty(el,PADDING_TOP));
	}
	
	public static double getAddedMarginWidth(Element el) {
		return stripPX(getStyleProperty(el,MARGIN_LEFT)) +
			   stripPX(getStyleProperty(el,MARGIN_RIGHT));
	}
	
	public static double getAddedMarginHeight(Element el) {
		return stripPX(getStyleProperty(el,MARGIN_TOP)) +
			   stripPX(getStyleProperty(el,MARGIN_BOTTOM));
	}

	public static double getWidth(Element el) {
		return stripPX(getStyleProperty(el, "width"));
	}

	public static double getHeight(Element el) {
		return stripPX(getStyleProperty(el, "height"));
	}

	public static double getWidth(Widget widget) {
		return getWidth(widget.getElement());
	}

	public static double getHeight(Widget widget) {
		return getHeight(widget.getElement());
	}

}
