package com.sondreweb.cryptoclicker.Activites;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sondreweb.cryptoclicker.ProfileAdapter;
import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.Services.MinerService;
import com.sondreweb.cryptoclicker.database.SQLiteHelper;
import com.sondreweb.cryptoclicker.game.Profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
*   ContinueActivity har ansvar for hente ned Profiler fra databasen og vise de i en list, samt gi mulighet for å fjerne profiler.
*   Og å kunne starte opp spillet med en valgt profil.
* */
public class ContinueActivity extends AppCompatActivity {
    private final static String TAG=ContinueActivity.class.getName();

    public final static String continueGame="com.sondreweb.cryptoclicker.Activites.continueGame"; //til å holde oversikt senere.

    //bruker denne til å sende en broadcast til MinerService, viss den tar den imot sjekker den om den miner med denne profilen,
    //hvis den gjør det så stopper den seg selv, og lagrer ingen ting. viss ikke så bare fortsetter den som vanlig.
    public final static String GET_DELETED_PLAYER_ID = "com.sondreweb.cryptoclicker.Activites.ContinueActivity.GET_DELETED_PLAYER_ID";

    public final static String DELETED_PROFILE_ID = "com.sondreweb.cryptoclicker.Activites.ContinueActivity.DELETED_PROFILE_ID";

    private static ArrayList<Profile> list; //holder på listen med profiler vi henter inn

    private static ListView listView;
    private static ProfileAdapter pAdapter;

    private Toolbar toolbar;

    private static Handler handler;

    private AlertDialog.Builder aBuilder;

    private Intent startGameIntent;

    private Intent broadcastDeletetPlayerId;
    //private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue);
        broadcastDeletetPlayerId = new Intent(GET_DELETED_PLAYER_ID);
        ViewStub stub = (ViewStub) findViewById(R.id.vs_continue_empty);
        stub.setLayoutResource(R.layout.empty_list);
        aBuilder = new AlertDialog.Builder(this);

        startGameIntent = new Intent(this,GameActivity.class);

        //createDeleteBuilder();
        listView = (ListView) findViewById(R.id.profileListView);

        listView.setEmptyView(stub); //setter en ViewStub for når Listen er tom.

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int viewId = view.getId();
                final Profile profile = ((Profile) parent.getItemAtPosition(position)); //henter ut Profil objectet.
                final long profile_id = profile.getDatabaseId();

                switch (viewId) {//skiller på hvilken Id det er på Viewet vi trykket på, disse er da Knappene til hvert view.
                    case R.id.ib_continue_ok: //vi trykker på grøn/svart OK icon.

                        Profile.CurrentProfil.setCurrentProfil(profile, profile.getDatabaseId()); //vi skal sarte spillet med ny profil.
                        //må sjekke om Profilen faktisk har noe i ProfileCLickUpgrade tabellen, eller den andre.
                        //siden en eller anne bug som hindre dette.

                        if (SQLiteHelper.getInstance(getBaseContext()).checkIfProfileIsNewProfile(profile)) {//true viss New Profile.
                            startGameIntent.setAction(NewGameActivity.startNewGame); //slik at vi kan identifisere hvor intent kom ifra.
                        } else {
                            startGameIntent.setAction(continueGame); //slik at vi kan identifisere hvor intent kom ifra.
                        }

                        startGameIntent.putExtra(GameActivity.PROFILE_ID, profile.getDatabaseId());

                        //starter game activity.
                        startActivity(startGameIntent);

                        break;
                    case R.id.ib_continue_delete: //vi trykker på søppel kassen.
                        aBuilder.setTitle(R.string.delete_alert_title);
                        aBuilder.setMessage(getApplicationContext().getString(R.string.delete_alert_message) + " " + profile.getName() + "?");

                        aBuilder.setPositiveButton(R.string.delete_alert_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                sendBroadcastToService(profile_id);

                                SQLiteHelper.getInstance(getBaseContext()).removeProfileAndCorrespondingData(profile);

                                //må da oppdatere ListViewet med det som står i databasen.
                                getAllProfilesWithThread();
                            }
                        });
                        aBuilder.setNegativeButton(R.string.delete_alert_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //vi skal ikke gjøre noen ting.
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = aBuilder.create();
                        dialog.show();
                        //Log.d(TAG, "Delete profile with name: "+profile.getName()+" and dbId: "+ profile.getDatabaseId());
                        break;
                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar_continue);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Continue");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setSubtitle("Select or delete profiles");

        getAllProfilesWithThread();
        //createListView(SQLiteHelper.getInstance(getBaseContext()).getAllProfiles());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int mId = item.getItemId();
        switch (mId){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent); //dunno hvordan dette kommer til å fungere.
                break;
            case android.R.id.home://gjør egentlig det samme som back knappen på telefonen, men ville bare teste den ut.
                super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendBroadcastToService(long p_id){//for når vi sletter en priofil, må vi sende hvilken vi sletter.
        broadcastDeletetPlayerId.putExtra(DELETED_PROFILE_ID,p_id);//legger til Profil_ID på Intentet

        sendBroadcast(broadcastDeletetPlayerId);//sender broadcast med data
    }


    @Override
    protected void onResume() {
        getAllProfilesWithThread();//henter nyeste versjon fra databasen.
        super.onResume();
    }

    private void getAllProfilesWithThread(){

        handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                list = SQLiteHelper.getInstance(getBaseContext()).getAllProfiles();
                Collections.sort(list);//sortere på BTCamount, den profilen som har mest BTC vill havne øverst.

                createListView(); //mulig dette er en veldig dårlig måte å gjøre slikt på.
                handler.removeCallbacks(this);
            }
        });
    }

    private void createListView(){//bytter ut hva som står i ListViewet.
        pAdapter = new ProfileAdapter(getBaseContext(),ContinueActivity.list);

        listView.setAdapter(pAdapter);
    }

    public static void updateListView(){ //mulig dette er ganske unødvendig.
        pAdapter.notifyDataSetChanged(); //oppdaterer Listen vi nå ser på, altså arrayet skrives ut på nytt med ArrayAdapteret, med hvor vi stod.

    }

}
