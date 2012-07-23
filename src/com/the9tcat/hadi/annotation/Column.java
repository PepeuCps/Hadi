package com.the9tcat.hadi.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * name:the column name you want to define, default will be your variable name <br/><br/>
 * primary: if the column is a primary key, default is false<br/><br/>
 * autoincrement: if the column is autoincrement, default is false<br/><br/>
 * default_value: the column's default value, default will be null
 */
@Retention(RetentionPolicy.RUNTIME)
public abstract @interface Column
{
  public abstract String name() default "";
  public abstract boolean primary() default false;
  public abstract boolean autoincrement() default false;
  public abstract String default_value() default "null";
}