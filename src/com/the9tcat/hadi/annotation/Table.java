package com.the9tcat.hadi.annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * 
 * name: your table's name, default will be your class's name
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public abstract @interface Table
{
  public abstract String name() default "";
}
