package com.the9tcat.hadi;

import java.lang.reflect.Field;

public class ColumnAttribute {
	public String name;
	public String type;
	public String default_value = null;
	public int length;
	public boolean primary = false;
	public boolean autoincrement = false;
	public Field field;
}
