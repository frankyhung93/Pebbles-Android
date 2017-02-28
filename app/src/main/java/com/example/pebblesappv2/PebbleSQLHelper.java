package com.example.pebblesappv2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ChunFaiHung on 2017/2/12.
 */

public class PebbleSQLHelper extends SQLiteOpenHelper{
    private static PebbleSQLHelper sInstance;

    public static final String TABLE_NAME_TDL = "pebbles_tdl";
    public static final String COLUMN_ID_TDL = "_id";
    public static final String COLUMN_DATE_TDL = "td_date";
    public static final String COLUMN_TIME_TDL = "td_time";
    public static final String COLUMN_DESC_TDL = "td_desc";
    public static final String COLUMN_LUPDATE_TIME_TDL = "td_lupdate_time";

    public static final String TABLE_NAME_ROUTINES = "pebbles_routines";
    public static final String COLUMN_ID_ROUTINES = "_id";
    public static final String COLUMN_ICON_ID_ROUTINES = "icon_id";
    public static final String COLUMN_ICON_NAME_ROUTINES = "icon_name";
    public static final String COLUMN_ICON_BG_COLOR_ROUTINES = "bg_color";
    public static final String COLUMN_ICON_TX_COLOR_ROUTINES = "tx_color";
    public static final String COLUMN_LUPDATE_TIME_ROUTINES = "icon_lupdate_time";

    private static final String DATABASE_NAME = "pebblesv2.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_1 = "CREATE TABLE " +
                                                        TABLE_NAME_TDL +
                                                                "( " +
                                                        COLUMN_ID_TDL +
                                                                " integer primary key autoincrement, " +
                                                        COLUMN_DATE_TDL +
                                                                " text not null, " +
                                                        COLUMN_TIME_TDL +
                                                                " text not null, " +
                                                        COLUMN_DESC_TDL +
                                                                " text not null, " +
                                                        COLUMN_LUPDATE_TIME_TDL +
                                                                " integer not null)";
    private static final String DATABASE_CREATE_2 = "CREATE TABLE " +
                                                        TABLE_NAME_ROUTINES +
                                                                "( " +
                                                        COLUMN_ID_ROUTINES +
                                                                " integer primary key autoincrement, " +
                                                        COLUMN_ICON_ID_ROUTINES +
                                                                " integer not null, " +
                                                        COLUMN_ICON_NAME_ROUTINES +
                                                                " text not null, " +
                                                        COLUMN_ICON_BG_COLOR_ROUTINES +
                                                                " integer not null, " +
                                                        COLUMN_ICON_TX_COLOR_ROUTINES +
                                                                " integer not null, " +
                                                        COLUMN_LUPDATE_TIME_ROUTINES +
                                                                " integer not null)";

    public static synchronized PebbleSQLHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new PebbleSQLHelper(context);
        }
        return sInstance;
    }

    private PebbleSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_1);
        database.execSQL(DATABASE_CREATE_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PebbleSQLHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TDL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ROUTINES);
        onCreate(db);
    }
}
