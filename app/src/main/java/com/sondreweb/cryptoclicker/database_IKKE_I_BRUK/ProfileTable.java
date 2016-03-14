package com.sondreweb.cryptoclicker.database_IKKE_I_BRUK;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by sondre on 03-Mar-16.
 */


public class ProfileTable {

    public static final String TABLE_PROFILE="profiles";
    public static final String COLUMN_ID="_id";
    public static final String COLUMN_NAME="name";
    public static final String COLUMN_BTCAMOUNT="BTCamount";
    public static final String COLUMN_USDAMOUNT="USDamount";
    public static final String COLUMN_TOTBTCMINED="TOTBTCmined";


    private static final String DATABASE_CREATE_PROFILES =
            "create table " + TABLE_PROFILE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_NAME + " text not null, " +
                    COLUMN_BTCAMOUNT + " double not null, " +
                    COLUMN_USDAMOUNT + " integer not null, " +
                    COLUMN_TOTBTCMINED + " double not null" + ");";

    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE_PROFILES);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.v("PROFILE TABLE", "All data is lost");
        database.execSQL("DROP TABLE IF EXIST "+ TABLE_PROFILE);
        onCreate(database);

    }

}
