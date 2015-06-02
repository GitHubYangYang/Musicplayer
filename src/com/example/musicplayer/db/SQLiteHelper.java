package com.example.musicplayer.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper{
	private static final int VERSION = 1;
	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
    public SQLiteHelper(Context context,String name,int version){
    	this(context,name,null,version);
    }
    public SQLiteHelper(Context context,String name){
    	this(context, name, VERSION);
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("create table users(usernumber varchar(20),password varchar(20))");
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
