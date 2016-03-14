package com.sondreweb.cryptoclicker;


import android.content.Intent;
import android.os.Handler;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sondreweb.cryptoclicker.Services.MinerService;
import com.sondreweb.cryptoclicker.game.Game;
import com.sondreweb.cryptoclicker.game.Profile;

//skulle gjord dette om til et Fragment, slik at vi kan dele menuen fra MainMenuActivity
// i stedet for å måtte implementer menuen hver gang med onclicklisteners osv.

//TODO: start MainMiner activity i bakgrunn her, med profilen vi nettop har laget.
public class NewGameActivity extends AppCompatActivity {

    private EditText userNameTextField;
    private Spinner speedSpinner;
    private Toolbar toolbar;

    public static Handler messageHandler;

    private final static String TAG = "NEW_GAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        userNameTextField = (EditText)findViewById(R.id.userName);
        speedSpinner = (Spinner) findViewById(R.id.speedSpinner);

        //lager SpinnerArrayet vårt.
        Spinner spinner = (Spinner) findViewById(R.id.speedSpinner);
        //legger til Arrayet med Strings til Spinneren.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.speedSpinnerOptions,android.R.layout.simple_spinner_item);
        //Velger hvordan vi skal velge Recoursene vi har.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Game");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setSubtitle("Create a new profile");

        messageHandler = new MessageHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    public void startGameActivity(View view) {
        Intent intent = new Intent(this,GameActivity.class);
        //Log.d(TAG_NEW_GAME, "StartActivity GameActivity.class");

        //vi kan starte bakgrunnsmineprosessen her.
        if( createProfile()){
            Log.i("CREATE_PROFILE", "Profile created successfully, starting game");
            //TODO: start bakgrunsprosess som miner her ifra, med profilen vi lagde.
            /* Intent Service
            Intent minerIntent = new Intent(this,MainMinerIntentService_IKKE_I_BRUK.class);
            startService(minerIntent);
               */

           //serviceSom ikke skal stoppe.

            Intent minerIntent = new Intent(this,MinerService.class);
            Log.d(TAG,"Får laget intentet:" +minerIntent);
            // intent.putExtra("MESSAGER", new Messenger(messageHandler));
            try {
                getApplicationContext().startService(minerIntent);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            //TODO: lagre denne profilen i databasen.
            //minerIntent.putExtra("EXTRA_SESSION_ID", minerIntent);


           // startService(Game.getMinerIntent());


            startActivity(intent);

        }else{
            Toast.makeText(this, "Venligst skriv inn mer enn 3 tegn.", Toast.LENGTH_SHORT).show();
            Log.i("EditText","Tomt felt, trenger flere karakterer");
        }

    }

    private boolean createProfile(){
       String name = userNameTextField.getText().toString();
        if(name.length() == 0 || name.length() < 4){
            return false;
        }else{
            new Profile(name);
            return true;
        }
    }
}
