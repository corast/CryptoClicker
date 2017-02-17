package com.sondreweb.cryptoclicker.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

   /*  Profil database for lagring av Profil objecter.
    *
    * */

public class ProfileTable {


    public static final String TAG=ProfileTable.class.getName();

    public static final String TABLE_PROFILE="Profile";

    public static final String COLUMN_PROFILE_ID="profile_id";
    public static final String COLUMN_DATECREATED="dateCreated"; //denne holder på når vi faktisk lagde denne profilen.
    //public static final String COLUMN_DATELASTPLAYED="dateLastPlayed"; //denne holder på når vi faktisk lagde denne profilen.

            //så lenge vi kan alterer på bestemte kollonner så vill dette gå bra.
    public static final String COLUMN_NAME="name"; //navnet på profilen
    public static final String COLUMN_BTCAMOUNT="btcAmount"; //holder på profilens nåværende BTCamount:
    public static final String COLUMN_USDAMOUNT="usdAmount"; //holder på profilens nåværende USDamount;
    public static final String COLUMN_TOTUSDAMOUNT="totUSDamount"; //holder på profilens nåværende USDamount;
    public static final String COLUMN_TOTBTCMINED="totBTCamount"; //holder på BTCamount totalt gjennom spillet.
                                    //med tråden og det vi klikket på bitcoinen.
    public static final String COLUMN_CLICKS="clicks"; //holder på antall clicks brukeren har gjordt
    public static final String COLUMN_CLICK_VALUE="clickValue"; //holder på nåværende verdi av hvert click.
    public static final String COLUMN_BTC_VALUE="btcValue"; //holder på nåværende BTCvalue pr sec i får.

        //NAME skal være uniqt for hver bruker, sammen med en ID.
    private static final String DATABASE_CREATE_PROFILES =
            "create table " + TABLE_PROFILE + "(" +
                    COLUMN_PROFILE_ID + " integer primary key autoincrement, " + //denne kan også sendes til servicen.
                    COLUMN_DATECREATED + " text not null, "+  //Enkleste løsning, String
                    //COLUMN_DATELASTPLAYED + " integer not null, "+  //Date, Calendar hadde vert det beste
                    COLUMN_NAME + " text not null unique, " +   //må sjekke om vi ikke har samme navn fra før.
                    COLUMN_BTCAMOUNT + " text not null, " + //BigDecimal
                    COLUMN_USDAMOUNT + " text not null, " + //BigDecimal
                    COLUMN_TOTUSDAMOUNT + " text not null, "+ //BigDecimal
                    COLUMN_TOTBTCMINED + " text not null, " + //BigDecimal
                    COLUMN_CLICKS+ " integer not null, "+ //int
                    COLUMN_CLICK_VALUE+ " text not null, " + //BigDecimal
                    COLUMN_BTC_VALUE +" text not null "+");"; //BigDecimal
                //velger å holde på Valuene siden da kan vi vise de i Continue Activitien når vi skal velge profil.

    public static void onCreate(SQLiteDatabase database){

        database.execSQL(DATABASE_CREATE_PROFILES);//execute sql setning
        Log.d(TAG, "onCreate ProfileTable");
    }
        //upgrade database strukturen.
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){

            Log.v(TAG, "Removes all data from table:"+ TABLE_PROFILE+" Upgrade table from version:"+oldVersion+" To: "+newVersion);

            database.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE+";"); //drop table og bytter ut med no ennet, viss vi forandrer versjonen
            //onCreate(database); //lager en ny tabell.

    }

}
