package com.sondreweb.cryptoclicker.Activites;


import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.sondreweb.cryptoclicker.ColoredSnackbar;
import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.database.SQLiteHelper;
import com.sondreweb.cryptoclicker.game.Profile;

//skulle gjord dette om til et Fragment, slik at vi kan dele menuen fra MainMenuActivity
// i stedet for å måtte implementer menuen hver gang med onMenuclicklisteners osv.
/**
 *  Denne har ansvar for å lage nye profiler, og ta imot error meldinger fra databasen viss ting ikke gikk.
 *  Når en profil er laget, starter vi opp GameActivity med denne.
 */



public class NewGameActivity extends AppCompatActivity {

    private final static String TAG = NewGameActivity.class.getName();

    public static final String profilId = "com.sondreweb.cryptoclicker.newgameactivity.profilId";

    public static final String startNewGame = "com.sondreweb.cryptoclicker.newgameactivity.startNewGame";

    private EditText userNameTextField;
    private Toolbar toolbar;

    private long res; //denne holder på Iden til profilen vi nettop laget, kan sende denne videre når vi starter gamet.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        userNameTextField = (EditText)findViewById(R.id.userName);

        //lager SpinnerArrayet vårt.
        //Spinner spinner = (Spinner) findViewById(R.id.speedSpinner);
        //legger til Arrayet med Strings til Spinneren.
        /*
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.speedSpinnerOptions,android.R.layout.simple_spinner_item);
                Velger hvordan vi skal velge Recoursene vi har.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        speedSpinner.setAdapter(adapter);
             */

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Game");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setSubtitle("Create a new profile");
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

    //onclick eventen til Knappen New Game.
    public void startGameActivity(View view) {
        if(view.getId() == R.id.buttonStartGame) {
            //getBaseContext(), this eller getApplicationContext() ? 
            Intent gameIntent = new Intent(getBaseContext(), GameActivity.class);
            Log.d(TAG, "startGameActivity intent: " + gameIntent.toString());
            Log.d(TAG, "getBaseContext:" + getBaseContext());
            Log.d(TAG, "this " + this);
            Log.d(TAG, "getAplicationContext" + getApplicationContext());

            //vi kan starte bakgrunnsmineprosessen her.
            if (createProfile()) {
                Log.i("CREATE_PROFILE", "Profile created successfully, starting game");

                if (res > -1) { //bare i tilfelle.

                    gameIntent.putExtra(GameActivity.PROFILE_ID, res);
                    gameIntent.setAction(startNewGame); //slik at vi kan identifisere hvor intent kom ifra.
                    //starter game activity.
                    startActivity(gameIntent);
                } else {
                    Log.e(TAG, "res verdi:" + res);
                }


            } else {
                Log.d(TAG, "klarte ikke lage profil.");
            }
        }else{
            Log.e(TAG,"feil view...");
        }
    }

    private boolean createProfile(){
       String name = userNameTextField.getText().toString(); //henter profilnavn
        if(name.length() == 0 || name.length() < 4){
            Snackbar snackbar = Snackbar.make(findViewById(R.id.new_game_cordinator), R.string.new_game_info,Snackbar.LENGTH_LONG);
            ColoredSnackbar.warn(snackbar).show();
            return false;
        }else{
                //sjekker om profilen blir laget i databasen, siden name er satt til å være uniq, vill det ikke gå viss det allerede finnes en med det navnet.
            if(!createProfileAndSaveInDatabase(name)){ //returnere false viss det ikke gikk pga navnet.
                Snackbar snackbar2 = Snackbar.make(findViewById(R.id.new_game_cordinator), "Profile name " + name + " is already taken.", Snackbar.LENGTH_LONG);
                ColoredSnackbar.alert(snackbar2).show();
                return false;
            }
            //dette gikk også, så vi har laget en profil.
            return true;
        }
    }
        //fanger opp Execptions fra databasen som den kaster videre hit, som gjør at vi kan sjekke om en profil allerede eksister i databasen.
    public boolean createProfileAndSaveInDatabase(String name) {

        SQLiteHelper db = SQLiteHelper.getInstance(this); //kan denne gi memory leaks?

        try {
            res = db.addProfile(new Profile(name)); //dette gjør at Profile.CurrentProfile blir denne profilen.
                    //samtidig så returnere databasen Iden til profilen.
            db.close();

            if (res < 0) { // -1 viss si error, altså ikke unikt navn.
                return false;
            }else{ //viss databasen returnere noe annet enn -1 så skulle det gå bra.
                return true;
            }

        } catch (SQLiteConstraintException e) {
            //betyr at vi prøver å legge inn et navn som allerede er der.
            return false;
        }
    }

}
