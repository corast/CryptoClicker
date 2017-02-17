package com.sondreweb.cryptoclicker.Activites;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.Services.MinerService;

/*
*   Ansvar for å vise en liten meny til brukeren, som veileder til andre aktivitere.
* */

public class MainMenuActivity extends AppCompatActivity {
    private final static String TAG = "MainMenyActivity";

    private Toolbar toolbar;

    int api_version = Build.VERSION.SDK_INT; //henter API nivå vi sitter med.

    private AlertDialog.Builder aBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main menu");
        //enabler tilbake pil, men gjør ingen ting pga onOptionsSelected.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        //toolbar.setSubtitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //legger til menu_toolbar layouten for knappene, med pre-defined layout. Gir mer mening i GameActivity, hvor vi bruker samme Menu.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        if(api_version == 15){//sjekker specifikt etter API 15, siden alt over funger ting fint med.
            aBuilder = new AlertDialog.Builder(this);
            showMinerServiceStopIcon(menu);
        }



        return super.onCreateOptionsMenu(menu);


    }

    public void showMinerServiceStopIcon(Menu menu){
        MenuItem item = menu.findItem(R.id.service_shut_down);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //kontroll over hva som blir trykket i toolbaren
        int id = item.getItemId(); //bør lag en switch med IDdene, slik at vi kan bruke tilbake knapp også.
        switch (id){
            case R.id.action_settings:
                Log.d(TAG, "action_setting");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent); //dunno hvordan dette kommer til å fungere.
                break;
            case R.id.service_shut_down:
                final Intent shutDownIntent = new Intent(MinerService.BROADCAST_ACTION_API15_SHUTDOWN);
                aBuilder.setTitle(R.string.main_meny_confirmation_service_title);
                aBuilder.setMessage(getApplicationContext().getString(R.string.main_meny_confirmation_service_message));

                aBuilder.setPositiveButton(R.string.main_meny_confirmation_service_positiv, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendBroadcast(shutDownIntent);
                    }
                });
                aBuilder.setNegativeButton(R.string.main_meny_confirmation_service_nevagive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //vi skal ikke gjøre noen ting.
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = aBuilder.create();
                dialog.show();
        }



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

    //TODO: bytt ut denne med faktisk hjelp om hvordan appen fungerer.
    //inneholder nå AndroidDatabaseManager fra nettet.
    public void startHelpActivity(View view){
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
}
