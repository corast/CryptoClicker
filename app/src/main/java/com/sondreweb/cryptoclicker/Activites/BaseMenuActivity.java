package com.sondreweb.cryptoclicker.Activites;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;

import com.sondreweb.cryptoclicker.R;

/**
 * Denne er ikke i bruk, så vidt jeg vet. Trodde det kunne være lurt å ha en menu liggende, som alle kunne bruke.
 * Hvor da vi skulle ha kunn ett sted med OnMenuItemClickListener, men fikk ikke helt tid til å få sett skikkelig på dette.
 */

public class BaseMenuActivity extends Activity{

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
