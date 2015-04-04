package org.esmerilprogramming.cloverx.annotation.path;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by efraimgentil<efraimgentil@gmail.com> on 04/04/15.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Put {

  String template() default Path.NO_TEMPLATE;

  String[] value() default Path.NO_PATH;

}
