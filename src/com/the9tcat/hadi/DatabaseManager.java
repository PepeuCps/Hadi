package com.the9tcat.hadi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
	private DatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mDB;

	public SQLiteDatabase getDB() {
		return this.mDB;
	}

	public DatabaseManager(Context context) {
		this.mDatabaseHelper = new DatabaseHelper(context);
	}

	public SQLiteDatabase open() {
		this.mDB = this.mDatabaseHelper.getWritableDatabase();
		return this.mDB;
	}

	public void close() {
		if (this.mDB != null) {
			this.mDB.close();
			this.mDB = null;
		}
	}
}
