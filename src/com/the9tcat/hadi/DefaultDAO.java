package com.the9tcat.hadi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DefaultDAO {

	private DatabaseManager mDatabaseManager;
	private HadiApplication mApplication;
	private Context mContext;
	private static boolean SHOW_LOG = true;
	public static String Lock = "dblock";
	
	public DefaultDAO(Context context){
		this.mApplication = ((HadiApplication)context.getApplicationContext());
		this.mDatabaseManager = mApplication.getDataBaseManager();
		this.mContext = context;		
	}
	
	/**
	 * 
	 * @param model the model that you want to save to database
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insert(Object model) {
		long result = 0;
		synchronized(Lock) {
			SQLiteDatabase db = this.mDatabaseManager.open();
			result = insertModel(db,model);
			Util.setAutoId(mApplication, model, result);
			this.mDatabaseManager.close();
			
			if(SHOW_LOG)
				Log.i(LogParams.LOGGING_TAG, "Created Model: " + Util.dumpToString(model));
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param models the data that you want to add to database
	 * @return the number of inserted rows
	 */
	public long insert_list(List<?> models) {
		long result = 0;
		long tmp_id = 0;
		
		synchronized(Lock) {
			SQLiteDatabase db = this.mDatabaseManager.open();	
			try {				
				db.beginTransaction();			
				for(Object obj:models){
					tmp_id = insertModel(db,obj);				
					if(tmp_id>0){
						Util.setAutoId(mApplication, obj, tmp_id);
						result ++;			
					}
					db.yieldIfContendedSafely();
				}
				db.setTransactionSuccessful();
			} finally { 
				db.endTransaction();
				this.mDatabaseManager.close();			
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param model the model that you want to save to database
	 * @return the number of rows affected
	 */
	public long delete(Class<?> model,String whereClause,String[] whereArgs) {
		long result = 0;
		synchronized(Lock) {
			SQLiteDatabase db = this.mDatabaseManager.open();
			result = db.delete(Util.getTableName(model), whereClause, whereArgs);
			this.mDatabaseManager.close();
			
			if(SHOW_LOG)
				Log.i(LogParams.LOGGING_TAG, "Deleted Model: " + Util.dumpToString(model));
		}
		return result;
	}
	
	/**
	 * 
	 * @param model the data that you want to add to database
	 * @return the number of rows affected
	 */
	public long delete_by_primary(Object model) {
		List<ColumnAttribute> atas = Util.getTableColumn(mApplication, (Class<?>)model.getClass());
		String whereClause = "";
		List<String> whereArgs = new ArrayList<String>();
		for(ColumnAttribute ata:atas){
			if(ata.primary){
				whereClause = whereClause + " and "+ata.name+" = ?";
				try {
					whereArgs.add(ata.field.get(model).toString());
				} catch (IllegalArgumentException e) {
					Log.e(LogParams.LOGGING_TAG, "delete_by_primary : " , e);
				} catch (IllegalAccessException e) {
					Log.e(LogParams.LOGGING_TAG, "delete_by_primary : " , e);
				}
			}
		}
		if(whereArgs.size()>0){
			return delete(model.getClass(),whereClause.substring(4),whereArgs.toArray(new String[whereArgs.size()]));
		}
		return -1;
	}
	
	/**
	 * 
	 * @param model update by primary key, please make sure your class has primary key
	 * @return the number of rows affected
	 */
	public long update_by_primary(Object model){
		List<ColumnAttribute> atas = Util.getTableColumn(mApplication, (Class<?>)model.getClass());
		String whereClause = "";
		List<String> whereArgs = new ArrayList<String>();
		List<String> columns = new ArrayList<String>();
		for(ColumnAttribute ata:atas){
			if(ata.primary){
				whereClause = whereClause + "and "+ata.name+" = ?";
				try {
					whereArgs.add(ata.field.get(model).toString());
				} catch (IllegalArgumentException e) {
					Log.e(LogParams.LOGGING_TAG, "update_by_primary : " , e);
				} catch (IllegalAccessException e) {
					Log.e(LogParams.LOGGING_TAG, "update_by_primary : " , e);
				}
			}else{
				columns.add(ata.name);
			}
		}
		if(whereArgs.size()>0){
			return update(model,columns.toArray(new String[columns.size()]),whereClause.substring(4),whereArgs.toArray(new String[whereArgs.size()]));
		}
		return -1;
	}
	/**
	 * 
	 * @param model the object that you want to update
	 * @param columns columns that you want to changed
	 * @param whereClause the optional WHERE clause to apply when updating. Passing null will update all rows.
	 * @param whereArgs
	 * @return the number of rows affected
	 */
	public long update(Object model,String[] columns,String whereClause,String[] whereArgs){
		long result = 0;
		boolean update_column = false;
		
		if(columns != null && columns.length > 0){
			update_column = true;
		}
		
		synchronized(Lock) {
			SQLiteDatabase db = this.mDatabaseManager.open();
			ContentValues values = new ContentValues();
			List<ColumnAttribute> atas = Util.getTableColumn(mApplication, (Class<?>)model.getClass());
			for (ColumnAttribute ata : atas) {
				if(update_column&&!Util.hasColumn(ata.name, columns)){
					continue;
				}
				Object value = null;
				try {
					value = ata.field.get(model);
				} catch (IllegalArgumentException e) {
					Log.e(LogParams.LOGGING_TAG, "update : " , e);
				} catch (IllegalAccessException e) {
					Log.e(LogParams.LOGGING_TAG, "update : " , e);
				}
                if (value == null) {
                    values.put(ata.name, (String) null);
                } else if ((value.getClass().equals(Boolean.class)) || (value.getClass().equals(Boolean.TYPE))) {
					values.put(ata.name, (Boolean) value);
				} else if (value.getClass().equals(java.util.Date.class)) {
					values.put(ata.name, ((java.util.Date)value).getTime());
				} else if (value.getClass().equals(java.sql.Date.class)) {
					values.put(ata.name, ((java.sql.Date)value).getTime());
				} else if ((value.getClass().equals(Double.class)) || (value.getClass().equals(Double.TYPE))) {
					values.put(ata.name, (Double) value);
				} else if ((value.getClass().equals(Float.class)) || (value.getClass().equals(Float.TYPE))) {
					values.put(ata.name, (Float) value);
				} else if ((value.getClass().equals(Integer.class))	|| (value.getClass().equals(Integer.TYPE))) {
					values.put(ata.name, (Integer) value);
				} else if ((value.getClass().equals(Long.class)) || (value.getClass().equals(Long.TYPE))) {
					values.put(ata.name, (Long) value);
				} else if ((value.getClass().equals(String.class)) || (value.getClass().equals(Character.TYPE))) {
					values.put(ata.name, value.toString());
				} 
			}
			result = db.update(Util.getTableName(model.getClass()), values, whereClause, whereArgs);
			this.mDatabaseManager.close();
		}
		return result;
	}
	/**
	 * 
	 * @param model The Class that you want to selected
	 * @param distinct true if you want each row to be unique, false otherwise.
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	 * @param groupBy A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	 * @param having A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
	 * @param order How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @param limit Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no LIMIT clause
	 * @return list of objects
	 */
	public List<?> select(Class<?> model,boolean distinct, String selection,String[] selectionArgs,String groupBy,String having, String order, String limit){
		List<Object> list = null;			
		synchronized(Lock) {
			SQLiteDatabase db = this.mDatabaseManager.open();		
			Cursor cursor = db.query(distinct, Util.getTableName(model), null, selection, selectionArgs, groupBy, having, order, limit);
			list = Util.processCursor(mContext,model,cursor);
			cursor.close();
			db.close();
		}
		return list;
	}
	/**
	 *
	 * @param model The Class that you want to selected
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	 * @return the count of data
	 */
	public long count(Class<?> model, String selection,String[] selectionArgs){
		long count = 0;
		synchronized(Lock) {
			SQLiteDatabase db = this.mDatabaseManager.open();
            String whereClause = selection != null ? " where " + selection: "";
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + Util.getTableName(model) + whereClause, selectionArgs);
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
			db.close();
		}
		return count;
	}
	
	private long insertModel(SQLiteDatabase db,Object model){
		ContentValues values = new ContentValues();
		List<ColumnAttribute> atas = Util.getTableColumn(mApplication, (Class<?>)model.getClass());
		for (ColumnAttribute ata : atas) {
			Object value = null;
			try {
				value = ata.field.get(model);
				if(ata.autoincrement&&Long.parseLong(value.toString())<=0){
					continue;
				}
			} catch(Exception e){
				Log.e(LogParams.LOGGING_TAG, "insertModel : " , e);
			}
			if (value == null) {
				continue;
			}
			
			if ((value.getClass().equals(Boolean.class)) || (value.getClass().equals(Boolean.TYPE))) {
				values.put(ata.name, (Boolean) value);
			} else if (value.getClass().equals(java.util.Date.class)) {
				values.put(ata.name, ((java.util.Date)value).getTime());
			} else if (value.getClass().equals(java.sql.Date.class)) {
				values.put(ata.name, ((java.sql.Date)value).getTime());
			} else if ((value.getClass().equals(Double.class)) || (value.getClass().equals(Double.TYPE))) {
				values.put(ata.name, (Double) value);
			} else if ((value.getClass().equals(Float.class)) || (value.equals(Float.TYPE))) {
				values.put(ata.name, (Float) value);
			} else if ((value.getClass().equals(Integer.class)) || (value.getClass().equals(Integer.TYPE))) {
				values.put(ata.name, (Integer) value);
			} else if ((value.getClass().equals(Long.class)) || (value.getClass().equals(Long.TYPE))) {
				values.put(ata.name, (Long) value);
			} else if ((value.getClass().equals(String.class)) || (value.getClass().equals(Character.TYPE))) {
				values.put(ata.name, value.toString());
			} 
		}
		
		long result = db.insert(Util.getTableName(model.getClass()), null, values);
		
		if(SHOW_LOG)
			Log.i(LogParams.LOGGING_TAG, "Creating Model: " + Util.dumpToString(model));
		
		return result;
	}
	
	public Object findFirst(final Class<?> cls, String whereClause, String[] whereArgs) {
		List<?> list = this.select(cls, false, whereClause, whereArgs, null, null, null, null);
		
		if (list.size() > 0) {
			return list.iterator().next();
		}
		return null;
	}
	
	public Object findFirst(final Class<?> cls) {
		List<?> list = this.select(cls, false, null, null, null, null, null, "1");
		
		if (list.size() > 0) {
			return list.iterator().next();
		}
		return null;
	}
}
