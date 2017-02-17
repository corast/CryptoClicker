package com.sondreweb.cryptoclicker.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Tabell for å holde oppdatert innhold mellom Profile og Upgrade.
 */
public class ProfileUpgradeTable {
    /*  Lagring for mange til mange forholdet mellom ProfilTable og UpgradeTable lagres her
    *       Denne er veldig viktig for brukeren, som ønsker å lagre hvor mye de har av ulike Upgrades og hvor mye de skal koste.
    * */


    public static final String TABLE_PROFILE_UPGRADE="profileUpgrade";


    public static final String COLUMN_PROFILE_ID="profile_id";
    public static final String COLUMN_UPGRADE_ID="upgrade_id";

    public static final String COLUMN_AMOUNT="amount";//int, antall vi har kjøpt av denne.
    public static final String COLUMN_COST="cost";  //holder på kostnaden til dette objectet.

    //String title, String desctiption, int srcIcon, double value, double cost

    //eventuelt lagre som int og << >> 4 decimaler.

    private static final String DATABASE_CREATE_PROFILE_UPGRADE =
            "create table " + TABLE_PROFILE_UPGRADE + "(" +
                    COLUMN_PROFILE_ID + " integer not null, " +
                    COLUMN_UPGRADE_ID + " integer not null, " +
                    COLUMN_AMOUNT + " integer not null, " +
                    COLUMN_COST + " text not null, " +
                    "FOREIGN KEY(" + COLUMN_PROFILE_ID + ") REFERENCES " + ProfileTable.TABLE_PROFILE + "(" + ProfileTable.COLUMN_PROFILE_ID + "), " +
                    "FOREIGN KEY(" + COLUMN_UPGRADE_ID + ") REFERENCES " + UpgradesTable.TABLE_UPGRADE + "(" + ProfileTable.COLUMN_PROFILE_ID + "), " +
                    "PRIMARY KEY(" + COLUMN_PROFILE_ID + "," + COLUMN_UPGRADE_ID + ")" + ");"; //double/BigDecimal
    //håper dette virker.

        /*
         " FOREIGN KEY(" + COLUMN_PROFILE_ID +") REFERENCES ProfileTable(" + ProfileTable.COLUMN_PROFILE_ID + ")," +
                    " FOREIGN KEY(" + COLUMN_CLICK_UPGRADE_ID + ") REFERENCES ClickUpgradeTable(" + ClickUpgradesTable.COLUMN_CLICK_UPGRADE_ID + ")," +
                    " PRIMARY KEY("+ COLUMN_PROFILE_ID + "," +COLUMN_CLICK_UPGRADE_ID + ")" + ");";
         */

    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE_PROFILE_UPGRADE);
    }
    //vill denne bytte ut hele databasen, eller bare pushe forandringene?
    //hver profil trenger en egen ClickUpgrade Tabell.

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.v(ProfileUpgradeTable.class.getName(), "All data is lost");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE_UPGRADE + ";");
        //    onCreate(database);
    }

}
