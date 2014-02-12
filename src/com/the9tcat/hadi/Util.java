package com.the9tcat.hadi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.the9tcat.hadi.annotation.Column;
import com.the9tcat.hadi.annotation.Table;

public class Util {
    /**
     * @param application The HadiApplication
     * @param object      the class that you want to fetch attributes
     * @return
     */
    public final static List<ColumnAttribute> getTableColumn(HadiApplication application, Class<?> object) {
        List<ColumnAttribute> atas;
        //first get from cache
        if (application != null) {
            atas = application.getTableAttributes(object);
            if (atas != null) {
                return atas;
            }
        }
        atas = new ArrayList<ColumnAttribute>();
        List<Field> fields = getFieldsFromObject(object);

        Column tmp_c;
        for (Field field : fields) {
            tmp_c = field.getAnnotation(Column.class);
            if (tmp_c != null) {
                ColumnAttribute ata = new ColumnAttribute();
                ata.field = field;
                ata.name = tmp_c.name();
                if (ata.name.equals("")) {
                    ata.name = field.getName();
                }
                field.setAccessible(true);
                int len = 0;
                if ((len = isSQLFloat(field.getType())) > 0) {
                    ata.type = "REAL";
                    ata.length = 0;
                } else if ((len = isSQLInteger(field.getType())) > 0) {
                    ata.type = "INTEGER";
                    ata.length = len;
                } else if ((len = isSQLString(field.getType())) > 0) {
                    ata.type = "TEXT";
                    ata.length = 0;
                }
                if (!tmp_c.default_value().equals("null")) {
                    ata.default_value = null;
                }
                if ((ata.name.equals("_id") || ata.name.equals("id")) && ata.length > 0 && tmp_c.autoincrement() != false) {
                    ata.autoincrement = true;
                    ata.primary = true;
                } else {
                    ata.autoincrement = tmp_c.autoincrement();
                    ata.primary = tmp_c.primary();
                }
                atas.add(ata);
            }
        }
        //cache
        if (application != null && atas != null) {
            application.addTableAttributes(object, atas);
        }
        return atas;
    }

    /**
     * @param object the the class's name
     * @return
     */
    public final static String getTableName(Class<?> object) {
        Table t = object.getAnnotation(Table.class);
        if (t == null) {
            return null;
        } else {
            if (t.name().equals("")) {
                return object.getSimpleName();
            } else {
                return t.name();
            }
        }
    }

    public final static boolean hasColumn(String column, String[] columns) {
        for (String c : columns) {
            if (c.equals(column)) {
                return true;
            }
        }
        return false;
    }

    public final static void loadModel(Class<?> object, Cursor cursor, Object model) {
        List<Field> fields = getFieldsFromObject(object);
        Column tmp_c;
        for (Field field : fields) {
            tmp_c = field.getAnnotation(Column.class);
            String fieldName = "";
            if (tmp_c != null) {
                fieldName = tmp_c.name();
                if (fieldName.equals("")) {
                    fieldName = field.getName();
                }
            }
            Class<?> fieldType = field.getType();
            int columnIndex = cursor.getColumnIndex(fieldName.replace("[","").replace("]",""));
            if (columnIndex < 0) {
                continue;
            }

            field.setAccessible(true);
            try {
                if ((fieldType.equals(Boolean.class))
                        || (fieldType.equals(Boolean.TYPE))) {
                    field.set(model, Boolean
                            .valueOf(cursor.getInt(columnIndex) != 0));
                } else if (fieldType.equals(Character.TYPE)) {
                    field.set(model, Character.valueOf(cursor.getString(
                            columnIndex).charAt(0)));
                } else if (fieldType.equals(java.util.Date.class)) {
                    field.set(model, new java.util.Date(cursor
                            .getLong(columnIndex)));
                } else if (fieldType.equals(java.sql.Date.class)) {
                    field.set(model, new java.sql.Date(cursor
                            .getLong(columnIndex)));
                } else if ((fieldType.equals(Double.class))
                        || (fieldType.equals(Double.TYPE))) {
                    field.set(model, Double.valueOf(cursor
                            .getDouble(columnIndex)));
                } else if ((fieldType.equals(Float.class))
                        || (fieldType.equals(Float.TYPE))) {
                    field.set(model, Float.valueOf(cursor
                            .getFloat(columnIndex)));
                } else if ((fieldType.equals(Integer.class))
                        || (fieldType.equals(Integer.TYPE))) {
                    field.set(model, Integer.valueOf(cursor
                            .getInt(columnIndex)));
                } else if ((fieldType.equals(Long.class))
                        || (fieldType.equals(Long.TYPE))) {
                    field.set(model, Long.valueOf(cursor.getLong(columnIndex)));
                } else if (fieldType.equals(String.class)) {
                    field.set(model, cursor.getString(columnIndex));
                }
            } catch (IllegalArgumentException e) {
                Log.e(LogParams.LOGGING_TAG, e.getMessage());
            } catch (IllegalAccessException e) {
                Log.e(LogParams.LOGGING_TAG, e.getMessage());
            } catch (SecurityException e) {
                Log.e(LogParams.LOGGING_TAG, e.getMessage());
            }
        }
    }

    public static final List<Object> processCursor(Context context,
                                                   Class<?> object, Cursor cursor) {
        List<Object> entities = new ArrayList<Object>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    Object entity = object.newInstance();
                    Util.loadModel(object, cursor, entity);
                    entities.add(entity);
                } while (cursor.moveToNext());
            }
        } catch (InstantiationException e) {
            Log.e(LogParams.LOGGING_TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(LogParams.LOGGING_TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.e(LogParams.LOGGING_TAG, e.getMessage());
        }
        return entities;
    }

    public static final void setAutoId(HadiApplication application, Object obj, long id) {
        List<ColumnAttribute> columns = getTableColumn(application, obj.getClass());
        for (ColumnAttribute col : columns) {
            if (col.autoincrement) {
                try {
                    Class<?> fieldType = col.field.getType();
                    if ((fieldType.equals(Integer.class))
                            || (fieldType.equals(Integer.TYPE))) {
                        col.field.setInt(obj, (int) id);
                    } else if ((fieldType.equals(Long.class))
                            || (fieldType.equals(Long.TYPE))) {
                        col.field.setLong(obj, id);
                    }
                } catch (IllegalArgumentException e) {
                    Log.e(LogParams.LOGGING_TAG, e.getMessage());
                } catch (IllegalAccessException e) {
                    Log.e(LogParams.LOGGING_TAG, e.getMessage());
                }
                return;
            }
        }
    }

    private static int isSQLFloat(Class<?> object) {
        if ((object.equals(Double.class)) || (object.equals(Double.TYPE))
                || (object.equals(Float.class)) || (object.equals(Float.TYPE))) {
            return 1;
        }
        return 0;
    }

    private static int isSQLInteger(Class<?> object) {
        if (object.equals(Boolean.class) || object.equals(Boolean.TYPE)) {
            return 1;
        }
        if ((object.equals(Integer.class))
                || (object.equals(Integer.TYPE))) {
            return 4;
        }
        if ((object.equals(Long.class))
                || (object.equals(Long.TYPE))) {
            return 8;
        }
        if ((object.equals(java.util.Date.class))
                || (object.equals(java.sql.Date.class))) {
            return 8;
        }
        return 0;
    }

    private static int isSQLString(Class<?> object) {
        if ((object.equals(String.class)) || (object.equals(Character.TYPE))) {
            return 1;
        }
        return 0;
    }

    public static String dumpToString(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        sb.append(object.getClass().getSimpleName()).append('{');

        boolean firstRound = true;

        for (Field field : fields) {
            if (!firstRound) {
                sb.append(", ");
            }
            firstRound = false;
            field.setAccessible(true);
            try {
                final Object fieldObj = field.get(object);
                final String value;
                if (null == fieldObj) {
                    value = "null";
                } else {
                    value = fieldObj.toString();
                }
                sb.append(field.getName()).append('=').append('\'')
                        .append(value).append('\'');
            } catch (IllegalAccessException ignore) {
                //this should never happen
            }
        }

        sb.append('}');
        return sb.toString();
    }


    public static List<Field> getFieldsFromObject(Class<?> object){
        Field[] fields = object.getDeclaredFields();

        if (fields.length == 0) {
            fields = object.getFields();
        }

        List<Field> list = new ArrayList(Arrays.asList(fields));

        if(object.getSuperclass()!=null){
            Field[] superFields = object.getSuperclass().getDeclaredFields();
            if(superFields.length>0){
                List<Field> list1 = new ArrayList(Arrays.asList(superFields));
                list.addAll(list1);
            }
        }

        return list;
    }
}
