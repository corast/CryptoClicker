package com.sondreweb.cryptoclicker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by sondre on 13-Feb-16.
 */

/**
 * Virker ikke ende, hvet ikke hvordan jeg skal bruke den, eller så bruker jeg bare Fragments instedetfor.
 * Settings trenger ikke en menu tror jeg.
 */

public class BaseMenuActivity extends Activity{
    //dette skal være actionbaren vår for alle sidene i appen.
    //trenger en Quit knapp
        //exit and save
        //exit,save but keep running in background
        //exit and save
    //settings knapp
    //må gjør slik at ting stemmer overens med XML fila seneren.
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

}
