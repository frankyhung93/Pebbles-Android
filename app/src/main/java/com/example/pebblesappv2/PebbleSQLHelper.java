package com.example.pebblesappv2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ChunFaiHung on 2017/2/12.
 */

public class PebbleSQLHelper extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "pebbles_tdl";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "td_date";
    public static final String COLUMN_TIME = "td_time";
    public static final String COLUMN_DESC = "td_desc";
    public static final String COLUMN_LUPDATE_TIME = "td_lupdate_time";

    private static final String DATABASE_NAME = "pebbles.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "CREATE TABLE " +
                                                        TABLE_NAME +
                                                        "( " +
                                                        COLUMN_ID +
                                                        " integer primary key autoincrement, " +
                                                        COLUMN_DATE +
                                                        " text not null, " +
                                                        COLUMN_TIME +
                                                        " text not null, " +
                                                        COLUMN_DESC +
                                                        " text not null, " +
                                                        COLUMN_LUPDATE_TIME +
                                                        " integer not null);";

    public PebbleSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PebbleSQLHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
