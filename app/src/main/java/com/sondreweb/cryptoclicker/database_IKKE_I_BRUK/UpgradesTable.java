package com.sondreweb.cryptoclicker.database_IKKE_I_BRUK;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by sondre on 03-Mar-16.
 */
public class UpgradesTable {
    public static final String FOREIGN_KEY_REFERENCE = "profile_id";

    public static final String TABLE_PROFILE="upgrades";
    public static final String COLUMN_ID="_id";
    public static final String COLUMN_DESC="name";
    public static final String COLUMN_BOUGHT="bought";
    public static final String COLUMN_VALUE="value";
    public static final String COLUMN_COST="cost";
    public static final String COLUMN_PROFILE_ID="profile_id";
    public static final String COLUMN_TITLE="title";

    private static final String DATABASE_CREATE_PROFILES =
            "create table " + TABLE_PROFILE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_PROFILE_ID + " integer, " +
                    COLUMN_DESC + " text not null, " +
                    COLUMN_BOUGHT + " integer not null, " +
                    COLUMN_VALUE + " double not null, " +
                    COLUMN_COST + " integer not null, " +
                    COLUMN_TITLE + " text not null," +
                    " FOREIGN KEY("+COLUMN_PROFILE_ID+") REFERENCES profiles("+FOREIGN_KEY_REFERENCE+")"+");";


        //håper dette virker.
    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE_PROFILES);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.v("PROFILE TABLE", "All data is lost");
        database.execSQL("DROP TABLE IF EXIST "+ TABLE_PROFILE);
        onCreate(database);
    }
}
