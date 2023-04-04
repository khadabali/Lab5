//package com.example.lab5;
package com.example.lab5;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "Lab_5DB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "ToDoList";
    public final static String COL_ITEMS = "Item";
    public final static String COL_ID = "ID";

    //Step 1
    //adding Another column in database table
    public final static String COL_URGENT = "Urgent";

    public MyOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    //function if no database file exists
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Step 2
        //Add this new Urgent column to query to create table
        String query = "CREATE TABLE " + TABLE_NAME +
                " ( " +
                COL_ID + " INTEGER PRIMARY KEY," +
                COL_ITEMS + " TEXT," +
                COL_URGENT + " BOOLEAN)";

        db.execSQL(query);
    }

    //function for when device db version is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int NewVersion) {

        //Drop the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Creating the new table:
        onCreate(db);
    }
}