package com.sondreweb.cryptoclicker.Activites;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.database.SQLiteHelper;
import com.sondreweb.cryptoclicker.game.ClickUpgrade;
import com.sondreweb.cryptoclicker.game.Upgrade;

import java.io.File;

/**
 * Viser en Splash screen ved oppstart av appen, som er iconet til appen og en grå bakgrunn.
 *
 * Sjekker også at databasene er laget, viss de allerede er laget regner vi at de inneholder nødveldig data.
 *      viss de ikke er laget, så starter vi servicen som håndterer slikt.
 */
public class SplashActivity extends AppCompatActivity{
    public static final String TAG=SplashActivity.class.getName();

    public static Intent intent;
    SQLiteHelper db;
    //la denne gjøre jobbing eller lage en tråd? Avhengig av hvordan denne oppererer
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        intent = new Intent(this, MainMenuActivity.class);

        new initalizeDatabase().execute(); //eksekverer Database Initialisering.
    }

    //String title,String description, int srcIcon, double value, double cost
    public void createUpgrades(){
        db.createUpgrade(new Upgrade("Upgrade 1", "old CPU", R.drawable.cpu_2, 0.0001, 0.010));
        db.createUpgrade(new Upgrade("Upgrade 2", "virge gx", R.drawable.virge_gx, 0.0010, 0.100));
        db.createUpgrade(new Upgrade("Upgrade 3", "GTX 340", R.drawable.gxt340, 0.0100, 1.000));
        db.createUpgrade(new Upgrade("Upgrade 4", "GTX 280", R.drawable.gtx280, 0.1000, 10.000));
        db.createUpgrade(new Upgrade("Upgrade 5", "GTX 480", R.drawable.gtx480, 0.2000, 20.000));
        db.createUpgrade(new Upgrade("Upgrade 6", "GTX 580", R.drawable.gtx580, 0.4000, 40.000));
        db.createUpgrade(new Upgrade("Upgrade 7", "GTX 660", R.drawable.gtx660, 0.8000, 80.000));
        db.createUpgrade(new Upgrade("Upgrade 8", "GTX 970", R.drawable.gtx970, 2.0000, 160.000));
        db.createUpgrade(new Upgrade("Upgrade 9", "Tesla K40", R.drawable.tesla_k40, 10000.0000, 1000000.000));
    }

    //String title,String description, int srcIcon, double value, double cost
    public void createClickUpgrades(){
        db.createClickUpgrade(new ClickUpgrade("Upgrade1", "Better Clicking", R.drawable.circle_star_yellow, 0.0100, 1.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade2", "Better Clicking", R.drawable.circle_star_yellow, 0.0200, 2.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade3", "Better Clicking", R.drawable.circle_star_yellow, 0.0400, 4.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade4", "Better Clicking", R.drawable.circle_star_yellow, 0.0800, 8.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade5", "Better Clicking", R.drawable.circle_star_yellow, 0.1600, 32.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade6", "Better Clicking", R.drawable.circle_star_yellow, 0.3200, 128.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade7", "Better Clicking", R.drawable.circle_star_yellow, 0.6400, 512.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade8", "Better Clicking", R.drawable.circle_star_yellow, 1.2800, 2048.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade9", "Better Clicking", R.drawable.circle_star_yellow, 2.5600, 8192.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade10", "Better Clicking", R.drawable.circle_star_yellow, 5.1200, 32768.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade11", "Better Clicking", R.drawable.circle_star_yellow, 10.2400, 131072.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade12", "Better Clicking", R.drawable.circle_star_yellow, 20.4800, 524288.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade12", "Better Clicking", R.drawable.circle_star_yellow, 20.4800, 2097152.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade12", "Better Clicking", R.drawable.circle_star_yellow, 40.9600, 8388608.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade12", "Better Clicking", R.drawable.circle_star_yellow, 81.9200, 33554432.0));
        db.createClickUpgrade(new ClickUpgrade("Upgrade12", "Better Clicking", R.drawable.circle_star_yellow, 9000.0000, 134217728000.0));
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"OnPause");
        super.onPause();
    }

    @Override
    protected void onStop() {

        Log.d(TAG,"OnStop");
        super.onStop();
    }

    //mulig dette er en ganske dum løsning, siden vi ikke skal tillate å forlate SpashActivity før vi har gjørt dette med database.
    //alternativ kan vi sende brukeren til Menyen , men deaktivere knappene til denne har kjørt seg ferdig.
    private class initalizeDatabase extends AsyncTask<Void, Void, Void> {
        private long timeToWait = 600;
        @Override
        protected Void doInBackground(Void... params) {//lagrer med annen tråd.
           long start = System.currentTimeMillis();
            db = SQLiteHelper.getInstance(getApplicationContext()); //sørger denne for å lage databasene

            File databasePath = getApplicationContext().getDatabasePath(SQLiteHelper.DATABASE_NAME);


            if (!(databasePath.exists())) {//sjekker om databasen eksisterer, ved å kjøre en sjekk om den kan åpnes.
                Log.d(TAG, "Creating Database");
                //db = new SQLiteHelper(getApplicationContext()); //sørger denne for å lage databasene
                    createUpgrades();
                    createClickUpgrades();
            } else {
                Log.d(TAG, "Database Already exists");
            }

            long time = System.currentTimeMillis() - start;
            if(time < timeToWait){//viss vi har ventet mindre enn 600 ms, så skal vi vente litt.
                try {
                    Thread.sleep(timeToWait-time); //slik at vi venter minimum tid til 600ms
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }


            db.close();


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //eventuelt enable meny knapper viss de ikke er enabled.
            finish();//fjerner denne Activiteten fra backstack og alt det der. Siden denne ikke har noe betydning senere.
            startActivity(intent);

            super.onPostExecute(aVoid);
        }
    }
}
