package org.openelis.ui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openelis.ui.mvp.Presenter;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Inherited
public @interface View {
	String template();
	Class<? extends Presenter> presenter() default Presenter.class;

}
