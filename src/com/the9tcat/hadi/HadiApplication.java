package com.the9tcat.hadi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;

public class HadiApplication extends Application {

	private DatabaseManager mDatabaseManager;
	private Map<Class<?>,List<ColumnAttribute>> tableAttributes = new HashMap<Class<?>,List<ColumnAttribute>>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.mDatabaseManager = new DatabaseManager(this);
	}

	@Override
	public void onTerminate() {
		if (this.mDatabaseManager != null) {
			this.mDatabaseManager.close();
		}
		super.onTerminate();
	}

	public DatabaseManager getDataBaseManager(){
		return mDatabaseManager;
	}
	
	public List<ColumnAttribute> getTableAttributes(Class<?> table){
		return tableAttributes.get(table);
	}
	
	public void addTableAttributes(Class<?> table,List<ColumnAttribute> attrs){
		tableAttributes.put(table, attrs);
	}
}
