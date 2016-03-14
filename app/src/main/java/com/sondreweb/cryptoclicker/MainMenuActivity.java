package com.sondreweb.cryptoclicker;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {
    private final static String TAG_MAIN_MENU = "Testing";
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: initialiser objects som vi trenger.

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main menu");
        //enabler tilbake pil, men gjør ingen ting pga onOptionsSelected.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setSubtitle("Subtitle test");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //legger til menu_toolbar layouten for knappene
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); //bør lag en switch med IDdene, slik at vi kan bruke tilbake knapp også.
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
    //starte new game Activitien, onclicken
    public void startNewGameActivity(View view) {
        Intent intent = new Intent(this,NewGameActivity.class);
        startActivity(intent);
    }
    //starte continue Activitien, onclicken
    public void startContinueActivity(View view) {
        Intent intent = new Intent(this,ContinueActivity.class);
        startActivity(intent);
    }
    //starte settings Activitien, onclicken
    public void startSettingsActivity(View view) {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }
    //testing av fragment view.
    public void startHelpActivity(View view){

    }
}
