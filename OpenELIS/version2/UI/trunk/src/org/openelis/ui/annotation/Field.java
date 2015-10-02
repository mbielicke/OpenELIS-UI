package org.openelis.ui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openelis.ui.screen.Screen.ShortKeys;
import org.openelis.ui.screen.State;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
@Inherited
public @interface Field {
	String shortcut() default "";
	ShortKeys shortModifier() default ShortKeys.CTRL;
	State[] enable() default {State.ADD,State.UPDATE,State.QUERY};
	boolean queryable() default true;
	String[] tab() default {};
	String meta() default "";
	boolean validate() default true;
	State[] focus() default {};
	org.openelis.ui.screen.Permission permission() default org.openelis.ui.screen.Permission.NONE;
}
