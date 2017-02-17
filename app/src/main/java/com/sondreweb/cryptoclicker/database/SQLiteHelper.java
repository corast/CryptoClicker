package com.sondreweb.cryptoclicker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sondreweb.cryptoclicker.game.ClickUpgrade;
import com.sondreweb.cryptoclicker.game.Info;
import com.sondreweb.cryptoclicker.game.Profile;
import com.sondreweb.cryptoclicker.game.Upgrade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Har ansvar for alt som har med databasen å gjøre, å update tabeller, inserte og å hente ut data.
 * Funskjoner for sjekking av data, om en profil er ny osl.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TAG = SQLiteHelper.class.getName();

    public static final int DATABASE_VERSION  = 1; //ved forandring av database schemet må vi forandre denne.
            //realistisk sett, så burde egentlig hele databasen byttes ut ved forandring, ivertfall ikke der hvor profil data ligger.

    public static final String DATABASE_NAME = "cryptoclicker.db";

    private static SQLiteHelper instance; //viss databasen er åpen kan vi bare bruke denne.

    private static Context sContext = null;

    private static Context getContext(){ //mulig unødvendig, men ville ikke at vi skal få en error pga dette.
        if(sContext == null){
            return getContext();
        }else
            return sContext;
    }

    //så vi kan slippe å close databasen og bare la den stå oppe, og bruke denn viss ledig.
    public static synchronized SQLiteHelper getInstance(Context context){
        if(instance == null){
            instance = new SQLiteHelper(context);
            sContext = context;
        }
        return instance;
    }

            //dette er samme som FeedReaderDbHelper fra developer.android om databaser.
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //første gang vi lager tabellene
        //lager ProfilTable først siden de andre tabellene refererer til denne med FK.
        //ved laging av tabelle må vi lage de som har tabelle som er avhengive av deres data først.
        ProfileTable.onCreate(db);

        ClickUpgradesTable.onCreate(db);

        UpgradesTable.onCreate(db);

        //siden disse peker på andre taballer burde de lages til slutt.

        ProfileClickUpgradeTable.onCreate(db);

        ProfileUpgradeTable.onCreate(db);
    }

    //mulig vi ikke vill oppdatere UpgradeTable viss vi kunn vi ha forandringene i ClickUpgradeTable etc.
    //profil derimot burde oppdatere ved new Profile fra NewGameActivity.

            //dette er kunn for å fornye strukcturen i databasen, ikke å bytte ut innholde?
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //når vi gjør forandrinver med versjonen.
        //må fjerne de tabellene som har relasjon først, som ProfileClickUpgradeTable og ProfileUpgradeTable,
            //siden ingen andre tabeller er avhengige av de.
        ProfileClickUpgradeTable.onUpgrade(db, oldVersion, newVersion);

        ProfileUpgradeTable.onUpgrade(db, oldVersion, newVersion);

        ProfileTable.onUpgrade(db,oldVersion,newVersion);

        ClickUpgradesTable.onUpgrade(db, oldVersion, newVersion);

        UpgradesTable.onUpgrade(db, oldVersion, newVersion);

        onCreate(db);//etter sletting, kan vi kjøre denne.
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }


    @Override
    public synchronized void close() { //lukker databasen
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
            //denne tar imot Resourde Iden på et bilde som R.drawable.navn_på_bilde.
    public String fromResourdeIdToString(int srcIcon){//vi må da gjøre dette om til kunn å være navn_på_bilde.
        return getContext().getResources().getResourceEntryName(srcIcon);
    }
            //denne tar imot navn på et bilde som navn_på_bilde over, og returnere nye resourceId på dette.
        //grunne til vi må bruke denne er fordi ved testing har jeg oppdaget at Resource IDen ikke er statisk fra nest startup etc, altså vi kan risikere å bruke feil bilder.
        //og det er ganske dårlig å lagre IDer på bilder i databasen, så navn på bildet var en mer logisk løsning.
        //med denne funksjonen får vi oppdatert R.drawable.navn_på_bilde id som er oppdatert.
    public int getImageResourceId(String image_name){
        return getContext().getResources().getIdentifier(image_name, "drawable", sContext.getPackageName());
    }


    /*##################################################################*/
    /*                            Profile table metoder                    */

    public boolean profileExists(long profile_id){//servicen trenger denne, siden vi kan ha slettet profilen den miner med.

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM "+ProfileTable.TABLE_PROFILE + " "+
                            "WHERE "+ProfileTable.COLUMN_PROFILE_ID + " = "+profile_id;

        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.getCount() == 0 || !cursor.moveToFirst()){ //skal sjekke om det ikke er noen rader for denne profilen.
            return false;
        }

        return true;
    }

    //legger til en ny profile, ved New Game activity.
    //må også legge til koblinger mellom profilen og Upgradelist, ClickUpgradeList.
    public long addProfile(Profile profile){
        SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(ProfileTable.COLUMN_NAME, profile.getName());

            TimeZone tzNorway = TimeZone.getTimeZone("GMT+2");
            Calendar c = Calendar.getInstance(tzNorway);
            //henter tiden nå, vill nok være et par sekunder feil, men det gjør ikke noe.
            String Time = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);


            values.put(ProfileTable.COLUMN_DATECREATED, Time); //lagrer Date + tid.
            values.put(ProfileTable.COLUMN_BTCAMOUNT, profile.getBigBitcoinCounter().toString()); //getBigBitcoinAsString etc inneholder alle string formateringer.
            values.put(ProfileTable.COLUMN_USDAMOUNT, profile.getUSDCounter().toString());
            values.put(ProfileTable.COLUMN_TOTUSDAMOUNT, profile.getBigTotUSDCounter().toString());
            values.put(ProfileTable.COLUMN_TOTBTCMINED, profile.getBigTotBTCCounter().toString());
            values.put(ProfileTable.COLUMN_CLICKS, profile.getClicks());
            values.put(ProfileTable.COLUMN_CLICK_VALUE, profile.getClickerValueAsBTC().toString());
            values.put(ProfileTable.COLUMN_BTC_VALUE, profile.getBigBTCValue());


            long id = db.insertOrThrow(ProfileTable.TABLE_PROFILE, null, values);//kaster error viss vi prøver å legge inn like navn.

            //viss vi komm hit før erroren ble kastet, kan vi legge til data i ProfileClickUpgrade og ProfileUpgrade Tablene.
            //siden vi har 0 av all data der.


            return id; //denne returnere -1 viss den failet, eller IDen til raden viss det gikk.
    }

    public ArrayList<Profile> getAllProfiles(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Profile> profileArrayList = new ArrayList<>();

        String selectQuery = "SELECT * FROM "+ProfileTable.TABLE_PROFILE;

        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex(ProfileTable.COLUMN_PROFILE_ID));//slik at vi kan faktis velge en profil.
                String dateCreated = cursor.getString(cursor.getColumnIndex(ProfileTable.COLUMN_DATECREATED));
                String name = cursor.getString(cursor.getColumnIndex(ProfileTable.COLUMN_NAME));

                String btcAmount = cursor.getString(cursor.getColumnIndex(ProfileTable.COLUMN_BTCAMOUNT));
                String totBTCamount = cursor.getString(cursor.getColumnIndex(ProfileTable.COLUMN_TOTBTCMINED));
                String usdAmount = cursor.getString(cursor.getColumnIndex(ProfileTable.COLUMN_USDAMOUNT));
                String totUSDamount = cursor.getString(cursor.getColumnIndex(ProfileTable.COLUMN_TOTUSDAMOUNT));

                int clicks = cursor.getInt(cursor.getColumnIndex(ProfileTable.COLUMN_CLICKS));

                String clickValue = cursor.getString(cursor.getColumnIndex(ProfileTable.COLUMN_CLICK_VALUE));
                String btcValue = cursor.getString(cursor.getColumnIndex(ProfileTable.COLUMN_BTC_VALUE));

                //long id,String dateCreated, String name, String btcAmount, String usdAmount, String totUSDamount, String totBTCamount, int clicks, String clickValue, String btcValue
                profileArrayList.add(new Profile(id, dateCreated, name, btcAmount, usdAmount,totUSDamount, totBTCamount, clicks,clickValue, btcValue ));

            }while(cursor.moveToNext());
        }
        return profileArrayList; //denne skal vi gi til ProfileAdapteret, ved onclick henter vi Upgrade og ClickUpgrade tilsvarande denne Profilen.
    }

    //trodde jeg kunne få bruk for denne for å sjekke på om navnene som ble laget i Profile var unike, men brukte heller insertOrTrow
    public int getProfileID(String name){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT "+ProfileTable.COLUMN_PROFILE_ID +
                " FROM "+ ProfileTable.TABLE_PROFILE  +
                " WHERE "+ ProfileTable.COLUMN_NAME + " = " + name ;

            //må være sikker på at ingen navn er like.
        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor.getInt(cursor.getColumnIndex(ProfileTable.COLUMN_PROFILE_ID));
    }

    //når vi skal slette profiler.
    public boolean removeProfileAndCorrespondingData(Profile profile){
        SQLiteDatabase db = this.getWritableDatabase();

        long profile_id = profile.getDatabaseId();

        //fjerner alle relasjoner mellom ProfileTable og UpgradeTable og ClickUpgradeTable
        //deretter fjerner selve profilen fra ProfileTable.

        String whereClauseUpgradeTable = profile_id + " = " +ProfileUpgradeTable.COLUMN_PROFILE_ID;
        db.delete(ProfileUpgradeTable.TABLE_PROFILE_UPGRADE,whereClauseUpgradeTable,null);

        String whereClauseProfileClickUpgradeTable = profile_id + " = " + ProfileClickUpgradeTable.COLUMN_PROFILE_ID;
        db.delete(ProfileClickUpgradeTable.TABLE_PROFILE_CLICK_UPGRADES,whereClauseProfileClickUpgradeTable,null);

        String whereClauseProfileTable = profile_id + " = " + ProfileTable.COLUMN_PROFILE_ID ;
        db.delete(ProfileTable.TABLE_PROFILE,whereClauseProfileTable,null);
        return false;
    }

    public void addBTCToProfile(BigDecimal bd, long service_profile_id){
        //ettersom dataen jeg skal oppdatere er lagret som Tekst, må jeg først hente den ned og bytte til BigDecimal, for deretter å addere.

        /*Vi må først hente ut hvor mye profilen har, og legge til antallet her, deretter legge til bd*/
        SQLiteDatabase db = this.getWritableDatabase();

        //Henter ut verdiene fra profilen vi bruker.

        String selectQuery = "SELECT "+ProfileTable.COLUMN_BTCAMOUNT+","+ProfileTable.COLUMN_TOTBTCMINED+" "+
                "FROM "+ProfileTable.TABLE_PROFILE+" WHERE "+ProfileTable.COLUMN_PROFILE_ID +" = "+ service_profile_id;
        Cursor cursor = db.rawQuery(selectQuery,null); //vi skal da peke på verdiene til denne profil_id'en

        if(cursor.moveToFirst() && cursor.getCount() == 1){//flytter oss til første rad, viss den finnes
            BigDecimal btcAmount =  new BigDecimal(cursor.getString(cursor.getColumnIndex(ProfileTable.COLUMN_BTCAMOUNT))); //lager BigDecimal av stringen.
            BigDecimal btcTotAmount =  new BigDecimal(cursor.getString(cursor.getColumnIndex(ProfileTable.COLUMN_TOTBTCMINED))); //lager BigDecimal av stringen.
            //vi skal da legge til den vi hente inn til disse vi allerede hadde.
            btcAmount = btcAmount.add(bd);
            btcTotAmount = btcTotAmount.add(bd);

            //da må vi update ProfilTaballen med disse verdiene.
            ContentValues cv = new ContentValues();

            cv.put(ProfileTable.COLUMN_BTCAMOUNT ,btcAmount.toString());
            cv.put(ProfileTable.COLUMN_TOTBTCMINED, btcTotAmount.toString());

            String whereClause = ProfileTable.COLUMN_PROFILE_ID + " = " + service_profile_id;
            db.update(ProfileTable.TABLE_PROFILE,cv,whereClause, null);

        }else{
            Log.e(TAG, "Profilen med id: " + service_profile_id + " finnes ikke i databasen");
        }

        //SELECT + COLUMN_BTCVALUE and COLUMN_BTCTOTVALUE
        /*      UPDATE table_name
                SET column1 = value1, etc
                where service_profile_id = COLUMN_PLAYER_ID
        *
        * */

    }



    public void updateProfile(Profile profile){
        SQLiteDatabase db = this.getWritableDatabase();

        //Log.d(TAG, "skal lagre:" + profile.toString());
        String btcAmount = profile.getBigBitcoinCounter().toString();
        String usdAmount = profile.getUSDCounter().toString();
        String totusdAmount = profile.getBigTotUSDCounter().toString();
        String totbtcAmount = profile.getBigTotBTCCounter().toString();
        int clicks = profile.getClicks();
        String clickValue = profile.getClickerValueAsBTC().toString();
        String btcValue = profile.getBigBTCValue();

        ContentValues contentValue = new ContentValues();
        contentValue.put(ProfileTable.COLUMN_BTCAMOUNT,btcAmount);
        contentValue.put(ProfileTable.COLUMN_USDAMOUNT,usdAmount);
        contentValue.put(ProfileTable.COLUMN_TOTUSDAMOUNT,totusdAmount);
        contentValue.put(ProfileTable.COLUMN_TOTBTCMINED,totbtcAmount);
        contentValue.put(ProfileTable.COLUMN_CLICKS,clicks);
        contentValue.put(ProfileTable.COLUMN_CLICK_VALUE,clickValue);
        contentValue.put(ProfileTable.COLUMN_BTC_VALUE, btcValue);

        db.update(ProfileTable.TABLE_PROFILE, contentValue, ProfileTable.COLUMN_PROFILE_ID + " = " + profile.getDatabaseId(), null);

    }



    /*##################################################################*/
    /*                      CLickUpgrade table metoder                  */

    public void createClickUpgrade(ClickUpgrade clickUpgrade){ //vi sender med et CLickUpgrade, så kan vi ta det vi trenger fra denne.
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //hvordan legger vi til _id?
        values.put(ClickUpgradesTable.COLUMN_TITLE,clickUpgrade.getTitle()); //legger til tittel om ClickUpgraden til databasen
        values.put(ClickUpgradesTable.COLUMN_DESC,clickUpgrade.getDescription());
        values.put(ClickUpgradesTable.COLUMN_VALUE,clickUpgrade.getValue()); //denne vill faile, siden jeg legger til double, den vill ha string...
        values.put(ClickUpgradesTable.COLUMN_COST, clickUpgrade.getCost()); //igjenn samme problemet med double to String, men vi får se.
        values.put(ClickUpgradesTable.COLUMN_SRCIMAGE, fromResourdeIdToString(clickUpgrade.getSrcIcon()));

        db.insert(ClickUpgradesTable.TABLE_CLICK_UPGRADES,null, values);
    }

    public ArrayList<Info> getAllClickUpgrades(){ //default, for nye profiler
        ArrayList<Info> upgradeArrayList = new ArrayList<Info>();

        String selectQuery = "SELECT * FROM "+ClickUpgradesTable.TABLE_CLICK_UPGRADES;

       // Log.i(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_CLICK_UPGRADE_ID));
                String title = cursor.getString(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_DESC));
                String srcIcon = cursor.getString(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_SRCIMAGE));

                double value = cursor.getDouble(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_VALUE));

                double cost = cursor.getDouble(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_COST));
                //String title,String description, int srcIcon, double value, double cost
                upgradeArrayList.add(new ClickUpgrade(id,title,description,getImageResourceId(srcIcon),value,cost)); //skal jeg ta med Iden her? eller bare bruke navnet på selve objectene?

            }while(cursor.moveToNext());
        }
        return  upgradeArrayList; //denne skal aldri være null.
    }


    public ArrayList<Info> getAllClickUpgrades(long profile_id){ //vi sender med indexen til profilen vi vill ha.

        ArrayList<Info> clickUpgradeArrayList = new ArrayList<Info>();

        String CU_id = ClickUpgradesTable.TABLE_CLICK_UPGRADES + "."+ ClickUpgradesTable.COLUMN_CLICK_UPGRADE_ID;
        String PCU_cid = ProfileClickUpgradeTable.TABLE_PROFILE_CLICK_UPGRADES + "." + ProfileClickUpgradeTable.COLUMN_CLICK_UPGRADE_ID;

        String PCU_pid = ProfileClickUpgradeTable.TABLE_PROFILE_CLICK_UPGRADES + "." + ProfileClickUpgradeTable.COLUMN_PROFILE_ID;

        String selectQuery = "SELECT  * " +
                "FROM " + ClickUpgradesTable.TABLE_CLICK_UPGRADES + " INNER JOIN " + ProfileClickUpgradeTable.TABLE_PROFILE_CLICK_UPGRADES +
                " ON "+ CU_id +" = "+ PCU_cid + " "+
                "WHERE " + profile_id + " = "+ PCU_pid;

       // rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});

       // Log.i(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_CLICK_UPGRADE_ID));
                String title = cursor.getString(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_DESC));
                String srcIcon = cursor.getString(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_SRCIMAGE));
                double value = cursor.getDouble(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_VALUE));
                double cost = cursor.getDouble(cursor.getColumnIndex(ClickUpgradesTable.COLUMN_COST));
                int bought = cursor.getInt(cursor.getColumnIndex(ProfileClickUpgradeTable.COLUMN_BOUGHT));
                //String title,String description, int srcIcon, double value, double cost
                clickUpgradeArrayList.add(new ClickUpgrade(id,title, description, getImageResourceId(srcIcon), value, cost, bought));

            }while(cursor.moveToNext());
        }

        /*
        cursor.close(); //er dette viktig?
        db.close(); //eller dette?
        */
        return  clickUpgradeArrayList; //denne skal aldri være null.
    }


    public void deleteProfileFromDatabaseAndItsData(long profile_id, String profile_name){//tar imot Iden til profilen vi skal slette, og navn



    }

    /*##################################################################*/
    /*                    Upgrade table metoder                */

    public void createUpgrade(Upgrade upgrade){
        //String title, String desc, int imageSrc, double value, double cost, int amount, amount og cost liksom forsvinner uansett.
        //"Upgrade1","Test av første upgrade", R.drawable.cpu_1, 0.0001, 0.010, 0

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UpgradesTable.COLUMN_TITLE, upgrade.getTitle());
        values.put(UpgradesTable.COLUMN_DESC, upgrade.getDescription());
        values.put(UpgradesTable.COLUMN_VALUE, upgrade.getValue());
        values.put(UpgradesTable.COLUMN_DEFAULT_COST, upgrade.getCost()); //kunn for først gangs profiler, når vi har spilt litt skal vi hente fra Profile_Upgrade cost.
        values.put(UpgradesTable.COLUMN_SRCIMAGE, fromResourdeIdToString(upgrade.getSrcIcon()));
     //   Log.d(TAG,"upgrade.getSrcIcon(): " +upgrade.getSrcIcon());
        //legger til et av objectene inn i UpgradeTable tabellen.
        db.insert(UpgradesTable.TABLE_UPGRADE, null, values);
    }


    public ArrayList<Info> getAllUpgrades(){//default, for nye profiler
        ArrayList<Info> upgradeArrayList = new ArrayList<>();
            //hvordan vi vill at den skal se ut.
        String selectQuery = "SELECT * FROM " + UpgradesTable.TABLE_UPGRADE;

       // Log.i(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
            //looper gjennom alle radene og legger til i upgradeArrayList.
        if(cursor.moveToFirst()){ //.moveToFirst vill si at vi hopper
            do{
                long id = cursor.getLong(cursor.getColumnIndex(UpgradesTable.COLUMN_UPGRADE_ID));
                String title = cursor.getString(cursor.getColumnIndex(UpgradesTable.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(UpgradesTable.COLUMN_DESC));
                String srcimage = cursor.getString(cursor.getColumnIndex(UpgradesTable.COLUMN_SRCIMAGE));

                double value = cursor.getDouble(cursor.getColumnIndex(UpgradesTable.COLUMN_VALUE));
                double default_cost = cursor.getDouble(cursor.getColumnIndex(UpgradesTable.COLUMN_DEFAULT_COST));

                // String title, String desc, int imageSrc, double value, double cost
                upgradeArrayList.add(new Upgrade(id,title,description,getImageResourceId(srcimage),value,default_cost));
            }while(cursor.moveToNext()); //true viss cursoren lander på en ny rad, false viss ikke.
        }
        return upgradeArrayList;
    }



    public ArrayList<Info> getAllUpgrades(long profile_id){
        ArrayList<Info> upgradeArrayList = new ArrayList<>();

        String U_id = UpgradesTable.TABLE_UPGRADE + "."+ UpgradesTable.COLUMN_UPGRADE_ID;

        String PU_uid = ProfileUpgradeTable.TABLE_PROFILE_UPGRADE + "." + ProfileUpgradeTable.COLUMN_UPGRADE_ID;
        String PU_pid = ProfileUpgradeTable.TABLE_PROFILE_UPGRADE+ "." + ProfileUpgradeTable.COLUMN_PROFILE_ID;

        String selectQuery = "SELECT  * " +
                "FROM " + UpgradesTable.TABLE_UPGRADE + " INNER JOIN " + ProfileUpgradeTable.TABLE_PROFILE_UPGRADE +" "+
                "ON "+ U_id +" = "+ PU_uid + " "+
                "WHERE " + profile_id + " = "+ PU_pid;

        //Log.i(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                long id = cursor.getLong(cursor.getColumnIndex(UpgradesTable.COLUMN_UPGRADE_ID));
                String title = cursor.getString(cursor.getColumnIndex(UpgradesTable.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndex(UpgradesTable.COLUMN_DESC));
                String srcIcon = cursor.getString(cursor.getColumnIndex(UpgradesTable.COLUMN_SRCIMAGE));
                double value = cursor.getDouble(cursor.getColumnIndex(UpgradesTable.COLUMN_VALUE));

                double cost = cursor.getDouble(cursor.getColumnIndex(ProfileUpgradeTable.COLUMN_COST));
                int amount = cursor.getInt(cursor.getColumnIndex(ProfileUpgradeTable.COLUMN_AMOUNT));
                // String title, String desc, int imageSrc, double value, double cost, int amount

                upgradeArrayList.add(new Upgrade(id,title,description,getImageResourceId(srcIcon),value,cost,amount));

            }while(cursor.moveToNext());
        }

        return upgradeArrayList;
    }

    /*          end Upgrade  */


    /*#######################################################*/
    /*          ProfileUpgrade table metoder            */

    public void updateProfileUpgrade(ArrayList<Upgrade> arrayList, Profile profile){//vi sender med Arraylisten til profilen.
        //veldig viktig vi sender mer rikgit ID her, ellers overskriver vi noen andre sin.

        //vi skal da her ta og legge inn tilsvarande rader i Profile_Upgrade table.
        long profile_id = profile.getDatabaseId();//henter Iden til denne profilen, tenkte det var bedre å sende objectet enn kunn IDen, for da er
                    //det letter å sende feil.

        //Log.d(TAG, "profile_id: "+profile_id);
        if(!arrayList.isEmpty()){
            //må først sjekke om vi allerede har laget radene til denne profilen i Profile_ClickUpgrade
            SQLiteDatabase db = this.getWritableDatabase();

            String selectQuery = "SELECT * FROM "+ProfileUpgradeTable.TABLE_PROFILE_UPGRADE +" "+
                    "WHERE "+ProfileUpgradeTable.COLUMN_PROFILE_ID +" = " + profile_id;

           // Log.i(TAG,selectQuery);
            Cursor cursor = db.rawQuery(selectQuery,null);

            if(cursor.getCount() != 0 && cursor.moveToFirst()){ //flytter oss øverst
                //update alle på en gang, eller må jeg lage mange forskjellige
                for(Upgrade upgrade : arrayList){ //oppdatere feltene
                    ContentValues cv = new ContentValues();

                    String updateQuery = "UPDATE "+ProfileUpgradeTable.TABLE_PROFILE_UPGRADE+" " +
                            "SET "+ProfileUpgradeTable.COLUMN_AMOUNT+" = "+upgrade.getAmount()+
                            ", "+ProfileUpgradeTable.COLUMN_COST+" = "+  upgrade.getBigCost().toString()+ " "+
                            "WHERE "+ProfileUpgradeTable.COLUMN_PROFILE_ID + " = "+profile_id+ " "+
                            "AND "+ProfileUpgradeTable.COLUMN_UPGRADE_ID+ " = "+upgrade.getId();

                    db.execSQL(updateQuery); //oppdaterer.

                    cv.put(ProfileUpgradeTable.COLUMN_AMOUNT,upgrade.getAmount());
                    cv.put(ProfileUpgradeTable.COLUMN_COST,upgrade.getBigCost().toString());

                  /*  db.update(ProfileUpgradeTable.TABLE_PROFILE_UPGRADE,cv,ProfileUpgradeTable.COLUMN_PROFILE_ID + " = "+profile_id+ " "+
                            "AND "+ProfileUpgradeTable.COLUMN_UPGRADE_ID + " = "+ upgrade.getId(),null); */
                }
            }else{//vi fikk ikke noen resultater fra Selecten, og må da legge til i tabellen
                for(Upgrade upgrade : arrayList){//skal legge til alle disse.
                    ContentValues values = new ContentValues();
                    values.put(ProfileUpgradeTable.COLUMN_PROFILE_ID,profile_id);
                    values.put(ProfileUpgradeTable.COLUMN_UPGRADE_ID, upgrade.getId());
                    values.put(ProfileUpgradeTable.COLUMN_AMOUNT, upgrade.getAmount());
                    values.put(ProfileUpgradeTable.COLUMN_COST, upgrade.getBigCost().toString());

                    db.insert(ProfileUpgradeTable.TABLE_PROFILE_UPGRADE,null, values);
                }
            }

        }else{
            Log.e(TAG, "ArrayListen fra updateProfileUpgrade er tom");
        }

    }

            //kjapp test om ny profil eller ikke
    public boolean checkIfProfileIsNewProfile(Profile p){
        long profile_id = p.getDatabaseId();
        SQLiteDatabase db = this.getReadableDatabase();

        //må sjekke om ProfileClickUpgrade eller ProfileUpgrade inneholder data om denne profilen, viss ikke må det være en ny profil.

        String selectQuery = "SELECT * FROM " + ProfileClickUpgradeTable.TABLE_PROFILE_CLICK_UPGRADES+ " "+
                            "WHERE " +ProfileClickUpgradeTable.COLUMN_PROFILE_ID +" = "+profile_id;

        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.getCount() != 0  && cursor.moveToFirst()){ //ikke en New Profile.
            db.close();
            return false;
        }

        return true;
    }


    /*#######################################################*/
    /*          ProfileCLickUpgrade table metoder            */
        //denne skal kjøre for hver gang vi går ut av GameActivity.
    public void updateProfileClickUpgrade(ArrayList<ClickUpgrade> arrayList, Profile profile){//vi sender med Arraylisten til profilen.
        //veldig viktig vi sender mer rikgit ID her, ellers overskriver vi noen andre sin.

        long profile_id = profile.getDatabaseId();

        if(!arrayList.isEmpty()){
            //må først sjekke om vi allerede har laget radene til denne profilen i Profile_ClickUpgrade
            SQLiteDatabase db = this.getWritableDatabase();
                //query som sjekker om det finnes noen rader med denne profil_iden.
            String selectQuery = "SELECT * FROM "+ProfileClickUpgradeTable.TABLE_PROFILE_CLICK_UPGRADES +" "+
                    "WHERE "+ProfileClickUpgradeTable.COLUMN_PROFILE_ID +" = " + profile_id;
          //  Log.d(TAG, selectQuery);
            Cursor cursor = db.rawQuery(selectQuery,null);

            if(cursor.getCount() != 0 && cursor.moveToFirst()){ //flytter oss øverst
                //update alle på en gang, eller må jeg lage mange forskjellige
                for(ClickUpgrade clickUpgrade : arrayList){ //oppdatere feltene

                    ContentValues cv = new ContentValues();

                    String updateQuery = "UPDATE "+ProfileClickUpgradeTable.TABLE_PROFILE_CLICK_UPGRADES+" " +
                            "SET "+ProfileClickUpgradeTable.COLUMN_BOUGHT+" = "+clickUpgrade.getBoughtInt()+" "+
                            "WHERE "+ProfileClickUpgradeTable.COLUMN_PROFILE_ID + " = "+profile_id+ " "+
                            "AND "+ProfileClickUpgradeTable.COLUMN_CLICK_UPGRADE_ID+ " = "+clickUpgrade.getId();
                    //Log.d(TAG, updateQuery);

                    cv.put(ProfileClickUpgradeTable.COLUMN_BOUGHT, clickUpgrade.getBoughtInt());

                    db.update(ProfileClickUpgradeTable.TABLE_PROFILE_CLICK_UPGRADES, cv,ProfileClickUpgradeTable.COLUMN_PROFILE_ID + " = " + profile_id + " " +
                            "AND " + ProfileClickUpgradeTable.COLUMN_CLICK_UPGRADE_ID + " = " + clickUpgrade.getId(), null);

                    //db.rawQuery(updateQuery, null);
                }
            }else{//vi fikk ikke noen resultater fra Selecten, og må da legge til i tabellen
                for(ClickUpgrade clickUpgrade : arrayList){//skal legge til alle disse.
                    ContentValues values = new ContentValues();
                    values.put(ProfileClickUpgradeTable.COLUMN_PROFILE_ID,profile_id);
                    values.put(ProfileClickUpgradeTable.COLUMN_CLICK_UPGRADE_ID, clickUpgrade.getId());
                    values.put(ProfileClickUpgradeTable.COLUMN_BOUGHT, clickUpgrade.getBoughtInt());

                    db.insert(ProfileClickUpgradeTable.TABLE_PROFILE_CLICK_UPGRADES,null, values);
                }
            }

        }else{
            Log.e(TAG,"ArrayListen fra updateProfileUpgrade er tom for: " + profile_id + " med navn " + profile.getName());
        }

    }

}
