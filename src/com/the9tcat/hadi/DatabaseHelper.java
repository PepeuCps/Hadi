package com.the9tcat.hadi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.the9tcat.hadi.annotation.Table;
import dalvik.system.DexFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String Hadi_DB_NAME = "Hadi_DB_NAME";
    private static final String Hadi_DB_VERSION = "Hadi_DB_VERSION";
    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, getDBName(context), null, getDBVersion(context));
        this.mContext = context;
        scanTables(getWritableDatabase());
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        ArrayList<Class<?>> tables = getEntityClasses(mContext);

        Log.i(LogParams.LOGGING_TAG, "Creating " + tables.size() + " tables");
        List<String> primarys = new ArrayList<String>();
        StringBuffer sb;
        for (Class<?> table : tables) {
            List<ColumnAttribute> columns = Util.getTableColumn(((HadiApplication) mContext.getApplicationContext()), table);
            if (columns.size() == 0) {
                continue;
            }
            sb = new StringBuffer();
            primarys.clear();
            boolean find_increment = false;
            for (ColumnAttribute column : columns) {
                if (column.primary) {
                    primarys.add(column.name);
                }
                sb.append(column.name);
                sb.append(" ");
                sb.append(column.type);
                if (column.autoincrement) {
                    find_increment = true;
                    sb.append(" PRIMARY KEY AUTOINCREMENT");
                    primarys.add(column.name);
                } else {
                    if (column.length > 0) {
                        sb.append("(");
                        sb.append(column.length);
                        sb.append(")");
                    }
                    if (column.default_value != null) {
                        sb.append(" default " + column.default_value);
                    }
                }
                sb.append(" , ");
            }
            if (primarys.size() > 0 && !find_increment) {
                String pms = "";
                for (String pm : primarys) {
                    pms = pms + pm + ",";
                }
                sb.append(" PRIMARY KEY (" + pms.substring(0, pms.length() - 1) + ") , ");
            }
            String sql = "CREATE TABLE " + Util.getTableName(table) + " (" + sb.toString().substring(0, sb.length() - 2) + " )";

            Log.i(LogParams.LOGGING_TAG, sql);

            db.execSQL(sql);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LogParams.LOGGING_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        ArrayList<Class<?>> tables = getEntityClasses(this.mContext);
        for (Class<?> table : tables) {
            db.execSQL("DROP TABLE IF EXISTS "
                    + Util.getTableName(table));
        }
        onCreate(db);
    }

    private void scanTables(SQLiteDatabase db) {
        ArrayList<Class<?>> tables = getEntityClasses(mContext);

        Log.i(LogParams.LOGGING_TAG, "Creating " + tables.size() + " tables");
        List<String> primarys = new ArrayList<String>();
        StringBuffer sb;
        for (Class<?> table : tables) {
            try {
                String tableName = Util.getTableName(table);
                Cursor c = db.query(false, tableName, null, null, null, null, null, null, null);
                //all old Columns Names
                List<String> oldColumns = Arrays.asList(c.getColumnNames());
                //all new Columns
                List<ColumnAttribute> newColumns = Util.getTableColumn(((HadiApplication) mContext.getApplicationContext()), table);
                //all new Columns names
                List<String> newColumnsNames = new ArrayList<String>();
                StringBuffer sb1 = new StringBuffer();
                for (ColumnAttribute col : newColumns) {
                    //Log.i(LogParams.LOGGING_TAG, "Existing columns: " + oldColumns.toString());
                    //Log.i(LogParams.LOGGING_TAG, "Column exists? " + col.name);
                    if (!oldColumns.contains(col.name.replace("[","").replace("]",""))) {
                        sb1.append(col.name);
                        sb1.append(" ");
                        sb1.append(col.type);
                        if (col.length > 0) {
                            sb1.append("(");
                            sb1.append(col.length);
                            sb1.append(")");
                        }
                        if (col.default_value != null) {
                            sb1.append(" default " + col.default_value);
                        }
                        String sqlAlter = "ALTER TABLE " + tableName + " ADD COLUMN " + sb1.toString() + ";";
                        Log.i(LogParams.LOGGING_TAG, sqlAlter);
                        db.execSQL(sqlAlter);
                        sb1 = new StringBuffer();
                    }
                }

            } catch (SQLiteException e) {
                List<ColumnAttribute> columns = Util.getTableColumn(((HadiApplication) mContext.getApplicationContext()),
                        table);
                if (columns.size() == 0) {
                    continue;
                }
                sb = new StringBuffer();
                primarys.clear();
                boolean find_increment = false;
                for (ColumnAttribute column : columns) {
                    if (column.primary) {
                        primarys.add(column.name);
                    }
                    sb.append(column.name);
                    sb.append(" ");
                    sb.append(column.type);
                    if (column.autoincrement) {
                        find_increment = true;
                        sb.append(" PRIMARY KEY AUTOINCREMENT");
                        primarys.add(column.name);
                    } else {
                        if (column.length > 0) {
                            sb.append("(");
                            sb.append(column.length);
                            sb.append(")");
                        }
                        if (column.default_value != null) {
                            sb.append(" default " + column.default_value);
                        }
                    }
                    sb.append(" , ");
                }
                if (primarys.size() > 0 && !find_increment) {
                    String pms = "";
                    for (String pm : primarys) {
                        pms = pms + pm + ",";
                    }
                    sb.append(" PRIMARY KEY (" + pms.substring(0, pms.length() - 1) + ") , ");
                }
                String sql = "CREATE TABLE " + Util.getTableName(table) + " (" + sb.toString().substring(0, sb.length() - 2) + " )";

                Log.i(LogParams.LOGGING_TAG, sql);

                db.execSQL(sql);
            }
        }
    }

    private static ArrayList<Class<?>> getEntityClasses(Context context) {
        ArrayList<Class<?>> entityClasses = new ArrayList<Class<?>>();

        try {
            String packageName = context.getPackageName();
            String path = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
            DexFile dexfile = new DexFile(path);
            Enumeration<String> entries = dexfile.entries();

            while (entries.hasMoreElements()) {
                String name = (String) entries.nextElement();

                if (name.contains(packageName)) {
                    Log.i(LogParams.LOGGING_TAG, "Found class: " + name);
                    Class<?> discoveredClass = null;
                    try {
                        discoveredClass = Class.forName(name, true, context.getClass().getClassLoader());
                    } catch (ClassNotFoundException e) {
                        Log.e(LogParams.LOGGING_TAG, e.getMessage());
                    }

                    if ((discoveredClass == null) ||
                            discoveredClass.getAnnotation(Table.class) == null) {
                        continue;
                    }
                    entityClasses.add((Class<?>) discoveredClass);
                }
            }
        } catch (IOException e) {
            Log.e(LogParams.LOGGING_TAG, e.getMessage());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LogParams.LOGGING_TAG, e.getMessage());
        }
        return entityClasses;
    }

    private static String getDBName(Context context) {
        String dbName = getMetaData(context, Hadi_DB_NAME);
        if (dbName == null) {
            dbName = "Application.db";
        }
        return dbName;
    }

    private static int getDBVersion(Context context) {
        int dbVersion = getMetaDataInt(context, Hadi_DB_VERSION);
        if (dbVersion > 0) {
            return dbVersion;
        }
        return 1;
    }

    private static String getMetaData(Context context, String name) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(
                    context.getPackageName(), 128);
            return ai.metaData.getString(name);

        } catch (Exception e) {
            Log.w(LogParams.LOGGING_TAG, "Couldn't find meta data string: " + name);
        }
        return null;
    }

    private static int getMetaDataInt(Context context, String name) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(
                    context.getPackageName(), 128);
            return ai.metaData.getInt(name);
        } catch (Exception e) {
            Log.w(LogParams.LOGGING_TAG, "Couldn't find meta data string: " + name);
        }
        return 0;
    }

}
