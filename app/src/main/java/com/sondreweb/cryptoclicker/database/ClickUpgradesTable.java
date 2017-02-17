package com.sondreweb.cryptoclicker.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*    Database for lagring av ClickUpgrade objecter.
 *       Vi vill kunn lagre disse først gang Appen starte opp, og vill ikke gjøre endringer deretter.
 * */
public class ClickUpgradesTable {


    //en så lenge går det greit å ha Cost og Value som double, siden de inneholder nok presisjon for øyeblikket.
    //mulig dette vill skape et problem for senere tid, men planlegger ikke å ha så alt for store verdier.
            //kan være lettere å bruke faktiske navnet?

    public static final String TABLE_CLICK_UPGRADES="ClickUpgrade";

    public static final String COLUMN_CLICK_UPGRADE_ID="_id";

    public static final String COLUMN_DESC="name"; //description, litt info tekst om objectet
    public static final String COLUMN_VALUE="value";//holder på hvor mye ekstra vi får av å ha denne.
    public static final String COLUMN_COST="cost";  //holder på kostnaden til dette objectet.
    public static final String COLUMN_TITLE="title";   //tittelen til clickUpgraden, strengt tatt er title uniq med måten jeg tenkt å lage de på.
    public static final String COLUMN_SRCIMAGE="srcimg";   //tittelen til clickUpgraden, strengt tatt er title uniq med måten jeg tenkt å lage de på.

    //String title, String desctiption, int srcIcon, double value, double cost

        //finn ut om jeg må bruke BLOB istedet for Text for lagring av double?
        //eventuelt lagre som int og << >> 4 decimaler.

    private static final String DATABASE_CREATE_CLICK_UPGRADES =
            "create table " + TABLE_CLICK_UPGRADES + "(" +
                    COLUMN_CLICK_UPGRADE_ID + " integer primary key autoincrement, " +
                    COLUMN_TITLE + " text not null, " + //String
                    COLUMN_DESC + " text not null, " +    //String
                    COLUMN_VALUE + " text not null, " + //double/BigDecimal.  kan eventuelt lagre som real, men den var like presis som float.
                    COLUMN_COST + " text not null, " + //double/BigDecimal
                    COLUMN_SRCIMAGE + " text not null "+");"; //int
    //håper dette virker.



    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE_CLICK_UPGRADES);
    }
            //vill denne bytte ut hele databasen, eller bare pushe forandringene?
        //hver profil trenger en egen ClickUpgrade Tabell.

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.v(ClickUpgradesTable.class.getName(), "All data is lost");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CLICK_UPGRADES+";");
        //    onCreate(database);
    }
}
