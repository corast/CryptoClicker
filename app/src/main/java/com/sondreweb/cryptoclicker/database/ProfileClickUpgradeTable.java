package com.sondreweb.cryptoclicker.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Tabell for å holde oppdatert innhold mellom Profil og ClickUpgrade.
 */
public class ProfileClickUpgradeTable {

    /**  Laging av mange til mange forholdet mellom profil og ClickUpgrade
     *
     *
     */

    public static final String TABLE_PROFILE_CLICK_UPGRADES="profileClickUpgrade";


    public static final String COLUMN_BOUGHT="bought"; //holder på om objecte allerede er kjøpt.

    public static final String COLUMN_PROFILE_ID = "profile_id"; //henter ut IDen til profilen.
    public static final String COLUMN_CLICK_UPGRADE_ID = "clickUpgrade_id"; //henter ut IDen til profilen.



            //forsøk på database.
    private static final String DATABASE_CREATE_PROFILE_CLICK_UPGRADES =
            "create table " + TABLE_PROFILE_CLICK_UPGRADES + "(" +
                    COLUMN_PROFILE_ID + " integer not null ," + //int
                    COLUMN_CLICK_UPGRADE_ID + " integer not null, " +
                    COLUMN_BOUGHT + " integer not null, " + //boolean, 0 eller 1.

                    " FOREIGN KEY(" + COLUMN_PROFILE_ID +") REFERENCES "+ ProfileTable.TABLE_PROFILE+"(" + ProfileTable.COLUMN_PROFILE_ID + ")," +
                    " FOREIGN KEY(" + COLUMN_CLICK_UPGRADE_ID + ") REFERENCES "+ ClickUpgradesTable.TABLE_CLICK_UPGRADES+"(" + ClickUpgradesTable.COLUMN_CLICK_UPGRADE_ID + ")," +
                    " PRIMARY KEY("+ COLUMN_PROFILE_ID + "," +COLUMN_CLICK_UPGRADE_ID + ")" + ");";


        /*
        * COLUMN_BOUGHT + " integer not null, " +  //boolean int, 1 eller 0
        * */


    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE_PROFILE_CLICK_UPGRADES);
    }
    //vill denne bytte ut hele databasen, eller bare pushe forandringene?
    //hver profil trenger en egen ClickUpgrade Tabell.

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.v("PROFILE TABLE", "All data is lost");
        database.execSQL("DROP TABLE IF EXISTS "+ TABLE_PROFILE_CLICK_UPGRADES+";");

       // onCreate(database);
    }
}
