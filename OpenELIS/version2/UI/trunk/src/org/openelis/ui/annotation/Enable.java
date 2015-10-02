package org.openelis.ui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openelis.ui.screen.State;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
@Inherited
public @interface Enable {
	State[] value();
}
