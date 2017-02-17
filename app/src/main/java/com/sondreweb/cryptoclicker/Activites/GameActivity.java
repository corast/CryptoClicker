package com.sondreweb.cryptoclicker.Activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.sondreweb.cryptoclicker.ColoredSnackbar;
import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.ResourceFragment;
import com.sondreweb.cryptoclicker.Services.MinerService;
import com.sondreweb.cryptoclicker.Tabs.TabFragmentMarket;
import com.sondreweb.cryptoclicker.Tabs.TabsPagerAdapter;
import com.sondreweb.cryptoclicker.database.SQLiteHelper;
import com.sondreweb.cryptoclicker.game.Profile;

/**
 * Ansvar for å starte lagring ,starte MinerService og be MinerService bytte delay når det trengs
 * Samt sende forandringer på Valuen vi miner med.
 * Dette er selve spillet om du vill.
 */
public class GameActivity extends AppCompatActivity {
    //dette er activiten som alltid ligger i bakgrunn når vi har fragmentene oppe.
    public static final String SEND_VALUE_CHANGE ="com.sondreweb.cryptoclicker.VALUE_CHANGE";
    public static final String VALUE_CHANGE_KEY = "com.sondreweb.cryptoclicker.BIG_VALUE_CHANGE";

    public static final String PROFILE_ID ="com.sondreweb.cryptoclicker.Activites.PROFILE_ID";


    public static final String SEND_DELAY_CHANGE ="com.sondreweb.cryptoclicker.Activites.DELAY_CHANGE";


    private final static String TAG = "GameActivity";

    private String[] tabs = {"Click","Market","Exchange","Progress"};

    private static Intent broadcastToServiceIntentValueChange;

    private static Intent broadcastToServiceDelay;


    private static Context gContext; //til senere bruk.

    private static Intent intentThatStaretGameActivity;

    private static Intent startMinerIntent;

    private TabsPagerAdapter adapterViewPager;
    private static ViewPager viewPager;
    public TabLayout tabLayout;

    @Override
    protected void onStart() {
        ResourceFragment.updateResourceUIAsMainThread();
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gContext = this.getApplicationContext();

        intentThatStaretGameActivity = getIntent(); //må sjekke om det kom fra NewGame eller Continye activity.
        //fra NewGame sender vi kunn Profilen, men ikke ArrayListene som tilhører default profilen.

        //kan også bruke Profile.CurrentProfile.getDatabaseId, men tilfelle det er en annen profil der, er dette hakke bedre.
        //Intent vi brukte på å komme hit vill være unikt.
        long profile_id = intentThatStaretGameActivity.getExtras().getLong(PROFILE_ID); //ProfilId som vi sender med, som må være den vi spiler med.

        startMinerIntent = new Intent(this,MinerService.class);
        if(Profile.CurrentProfil.getDatabaseId() != profile_id){//sjekker om vi har byttet på hvilken profil som er i bruk for øyeblikket.
            Profile.CurrentProfil.setDatabaseId(profile_id);
        }


        if(intentThatStaretGameActivity.getAction() == NewGameActivity.startNewGame ){ //betyr at dette er en ny profil og har ingen ting i databasen.
            //vi skal starte ned game, må først sjekke om CurrentProfileId == -1;

            final Snackbar snackbar = Snackbar.make(findViewById(R.id.game_coordinator), "Welcome! Click coin to start mining, or buy Upgrades to do it for you!", Snackbar.LENGTH_INDEFINITE);


            SQLiteHelper db = new SQLiteHelper(this);

            db.getAllClickUpgrades();//henter ClickUpgrades for nye profiler.

            Profile.CurrentProfil.setDatabaseId(profile_id);

                //Henter Arrayene som denne nye profilen trenger.
            Profile.CurrentProfil.setClickUpgradeList(db.getAllClickUpgrades());

            Profile.CurrentProfil.setUpgradeList(db.getAllUpgrades());

            db.close(); //lukker siden vi er ferdig med å bruke den.
            //vi kan da bare starte servicen.
            //må først finne ut om den allerede kjører, viss den gjør det så kan vi stoppe den/lagre profilen den bruke og starte en ny.


            //sender med noe info MinerServicen som den vill kunne trenge.
            startMinerIntent.putExtra(MinerService.PLAYER_NAME, Profile.CurrentProfil.getName());
            startMinerIntent.putExtra(MinerService.PLAYER_ID, profile_id);

            startService(startMinerIntent);
            snackbar.setAction("GOT IT", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });

            ColoredSnackbar.welcome(snackbar).show();

        }else if(intentThatStaretGameActivity.getAction() == ContinueActivity.continueGame){//betyr at vi må hente mange ting fra databasen

            //en veldig rar bug jeg har hvor GameActivity ikke starte første gang en profil er laget.
            //men vi blir sent tilbake til Continue Activity.

            SQLiteHelper db = new SQLiteHelper(this);

            //henter Listene til denne Profilen.
            Profile.CurrentProfil.setClickUpgradeList( db.getAllClickUpgrades(profile_id));
            Profile.CurrentProfil.setUpgradeList(db.getAllUpgrades(profile_id));

            //siden vi allerede har hentet Profil data i profil objektet trenger vi ikke det.
            db.close();

            startMinerIntent.putExtra(MinerService.PLAYER_NAME, Profile.CurrentProfil.getName());
            startMinerIntent.putExtra(MinerService.PLAYER_ID, profile_id);
            startMinerIntent.putExtra(MinerService.BTC_VALUE, Profile.CurrentProfil.getBigBTCValue());
            startMinerIntent.setAction(ContinueActivity.continueGame); //sender med at vi kom fra continueActivity til servicen.
            //slik at vi kan hente ut BTC_VALUE også.
            startService(startMinerIntent);

            Log.d(TAG, "Vi skal continue game");
        }

         if(Profile.CurrentProfil.getDatabaseId() == -1){//-1 vill si ingen profil er activ.
            //gammel kode for hvordan jeg sjekket om det var NewGame før, og ikke Continue.
             //dette vill nå aldri kjøre.
             SQLiteHelper db = new SQLiteHelper(this);

             db.getAllClickUpgrades();

             Profile.CurrentProfil.setDatabaseId(profile_id);

             Profile.CurrentProfil.setClickUpgradeList(db.getAllClickUpgrades());


             Profile.CurrentProfil.setUpgradeList(db.getAllUpgrades());

             db.close(); //lukker siden vi er ferdig med å bruke den.

         }else{

         }

        //starter backgroundServicen.

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_game); //henter toolbar fra Layout.
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Game");

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);//henter tabLayout fra Layout.
        for(String tabName : tabs){ //legger til Tabs
            tabLayout.addTab(tabLayout.newTab().setText(tabName));
        }

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapterViewPager = new TabsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapterViewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.setPageTransformer(false, new CubeOutTransformer()); //legger til animasjon ved bytting av tab.
                    //api vi har lagt til i app.gradle

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                if((tab.getPosition() == 3 && viewPager.getCurrentItem() == 0)){ //viss vi bytter fra Tab 0 til 3.
                    viewPager.setCurrentItem(tab.getPosition(),true);
                    //ser så rart ut viss vi har smooth scroll på, hopper for raskt igjennom.
                }else if((tab.getPosition() == 0 && viewPager.getCurrentItem() == 3)){ //viss vi bytter fra Tab 3 til 0.
                    viewPager.setCurrentItem(tab.getPosition(), false);
                }
                else{
                    viewPager.setCurrentItem(tab.getPosition());
                }

                if (viewPager.getCurrentItem() == 1) {
                    TabFragmentMarket.updateListView();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        broadcastToServiceIntentValueChange = new Intent(SEND_VALUE_CHANGE); //for sending av verdi forandring til Servicen.
        broadcastToServiceDelay = new Intent(SEND_DELAY_CHANGE);

        startMinerService();
    }

    public static int getTabSeleceted(){
        return viewPager.getCurrentItem(); //returnere hvilken tab vi har åpne.
                //bruker dette til å finne ut ok vi må potensielt oppdatere noen TextViews osl.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //legger til menu_toolbar layouten for knappene
        getMenuInflater().inflate(R.menu.menu_toolbar_game, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public static void startMinerService(){
        String profileName =  Profile.CurrentProfil.getName();
        //gContext skal ikke være null så lenge vi har startet opp GameActivity.
        Intent minerService = new Intent(gContext, MinerService.class);
    }

    @Override  //mulig dette blir samme som gContext her?
    public Context getBaseContext() {
        return super.getBaseContext();
    }

    @Override
    protected void onPause() {

        super.onPause();

        //lager classen som skal lagre til databasen og exekverer lagring i bakgrunn.
        new saveProfileToDatabaseAsyncTask().execute();

    }

    @Override
    protected void onStop() {
        //lurer på om dette faktisk er unødvendig.
        GameActivity.sendDelayChangeToService(getBaseContext());
        super.onStop();
        Log.d(TAG, "I OnStop for GameActivity");
        //lagre?
    }

    public static void sendDelayChangeToService(Context context){//forteller at vi skal forandre Delayet til servicen.

        broadcastToServiceDelay.setAction(SEND_DELAY_CHANGE);

        context.sendBroadcast(broadcastToServiceDelay);
    }

    public static void sendValueChangeToService(Context context){//denne kan kjøre ved eventen at man kjøper en oppgradering.

        broadcastToServiceIntentValueChange.setAction(SEND_VALUE_CHANGE);
        //broadcastToServiceIntentValueChange.putExtra("Bundle", bundle);
        broadcastToServiceIntentValueChange.putExtra(VALUE_CHANGE_KEY, Profile.CurrentProfil.getBigBTCValue());

        // Log.d(TAG, "Vi skal sende oppdatert value til Service: "+bundle.getDouble("valueChange"));
        context.sendBroadcast(broadcastToServiceIntentValueChange);
    }

    //lager en egen Asynctask som ikke klager på tomme parameter .execute()
    private class saveProfileToDatabaseAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {//lagrer med annen tråd.

            SQLiteHelper.getInstance(gContext).updateProfileClickUpgrade(Profile.CurrentProfil.getClickUpgradeListAsClickUpgrade(), Profile.CurrentProfil);
            SQLiteHelper.getInstance(gContext).updateProfileUpgrade(Profile.CurrentProfil.getUpgradeListAsUpgrade(), Profile.CurrentProfil);

            SQLiteHelper.getInstance(gContext).updateProfile(Profile.CurrentProfil);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //ensete problemet jeg kan se er at viss det tar litt lang til å lagre, vill ikke man kunne se det med en gang i ContinueActivkty listViewet.
            //notifyChange på listViewet vil heller ikke fungere, siden det kunn fungere viss man har forandret Arrayet ListViewet bruker, vi har jo forandre dataene i Databasen.
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//slik at vi kan bruke Iconene i Toolbaren.
        int mId = item.getItemId();
        switch(mId){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent); //dunno hvordan dette kommer til å fungere.
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
