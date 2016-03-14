package com.sondreweb.cryptoclicker.database_IKKE_I_BRUK;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sondre on 02-Mar-16.
 */
public class SQLiteAdapter extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION  = 1;

    public static final String DATABASE_NAME = "profile.db";
    public static final String TABLE_NAME = "Upgrads";
    public static final String TABLE_NAME_TWO = "ClickUpgrades";
    public static final String TABLE_NAME_THREE = "Profile";
    public static final String TABLE_NAME_FOUR = "Achievments";

    public static final String KEY_ID ="_id";
    public static final String KEY_NAME = "name";
        /* med mer..*/
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteAdapter sqLiteAdapter;

    public SQLiteAdapter(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TODO: lag databasen her.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //todo: sjekk om databasen er nyeste versjon.


    }
}
