package com.sondreweb.cryptoclicker.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Upgrade taball for lagring av Upgrades
 */
public class UpgradesTable {

    public static final String TABLE_UPGRADE ="upgrade";

    public static final String COLUMN_UPGRADE_ID="_id"; //uniq ID
    public static final String COLUMN_DESC="name";// kort info.
    public static final String COLUMN_VALUE="value";// btc/sec verdien.
    public static final String COLUMN_SRCIMAGE="srcimg";   //bilde til upgraden
    public static final String COLUMN_TITLE="title"; //titel til upgrade.
    public static final String COLUMN_DEFAULT_COST="default_cost"; //holder på førstegangs kostnad.




    private static final String DATABASE_CREATE_UPGRADES =
            "create table " + TABLE_UPGRADE + "(" +
                    COLUMN_UPGRADE_ID + " integer primary key autoincrement, " +
                    COLUMN_TITLE + " text not null, " +   //String
                    COLUMN_DESC + " text not null, " + //String
                    COLUMN_VALUE + " text not null, " + //double/BigDecimal
                    COLUMN_DEFAULT_COST + " text not null, " + //double/BigDecimal
                    COLUMN_SRCIMAGE + " text not null " + ");"; //double


        //håper dette virker.
    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE_UPGRADES);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.v(UpgradesTable.class.getName(), "All data is lost");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_UPGRADE + ";");
        //    onCreate(database);
    }
}
