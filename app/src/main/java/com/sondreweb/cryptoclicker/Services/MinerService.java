package com.sondreweb.cryptoclicker.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sondreweb.cryptoclicker.Activites.ContinueActivity;
import com.sondreweb.cryptoclicker.Activites.GameActivity;
import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.ResourceFragment;
import com.sondreweb.cryptoclicker.database.SQLiteHelper;
import com.sondreweb.cryptoclicker.game.Profile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Denne har ansvar for å mine hvert sekund eller hva vi har som Delay og sende dette til ResourceFragment, viss den er oppe.
 * Lagre i databasen når det er nødvendig.
 * Vise notifikasjon på at den kjører.
 * Gi mulighet for å lukkes.
 * Ta imot beskjeder som vi sender.
 */

//  https://www.websmithing.com/2011/02/01/how-to-update-the-ui-in-an-android-activity-using-data-from-a-background-service/
public class MinerService extends Service {

    //enten sende profilen med intent, med deres data.
    //eller bare sende med nødvendig data i intentet, som value.

            //for å ta imot data hvet jeg ikke helt. (hente de ut selv?)
    private static final String TAG = "MainMainerService";



    public static final String PLAYER_NAME = "com.sondreweb.cryptoclicker.Services.PLAYER_NAME";//for å sende Player name
    public static final String PLAYER_ID = "com.sondreweb.cryptoclicker.Services.PLAYER_ID";   //for å sende player_id
    public static final String BTC_VALUE = "com.sondreweb.cryptoclicker.Services.BTC_VALUE";    //for å sende btc_value

   // public static final String SEND_MINED_VALUE = "com.sondreweb.cryptoclicker.Services";

    public static final String SEND_CANCEL_CONFIRMATION ="com.sondreweb.cryptoclicker.CANCEL"; //til å bytte actions i notifikasjonen.

    public static final String SEND_OK ="com.sondreweb.cryptoclicker.OK";   //til å sende OK
    public static final String SEND_CANCEL ="com.sondreweb.cryptoclicker.SEND_CANCEL";  //til å sende Cancell

    public static final String BROADCAST_ACTION = "com.sondreweb.cryptoclicker.action.DISPLAYEVENT";

    public static final String BROADCAST_ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN"; //til å lagre før mobilen skrus av.

    public static final String BROADCAST_ACTION_API15_SHUTDOWN = "android.sondreweb.cryptoclicker.API15_STOPSERVICE";

    private static long timeSinceLastMining = System.currentTimeMillis();//for lagring av tid siden sist mining.

    private DecimalFormat decimalFormatBTC = new DecimalFormat("###,##0.0000");
    private DecimalFormatSymbols symbols = new DecimalFormatSymbols();

    private long player_id = -1; //samme som før, viss denne skal byttes må det sørges for at gammel data ikke fjernes.

    private boolean initalized = false;

    public void initalizeFormating(){ //initialiserer slik at vi får custom symbol seperator med DecimalFormat.
        if(!initalized) { //skal kunn kjøre denne en gang.
            symbols.setGroupingSeparator(' ');
            decimalFormatBTC.setDecimalFormatSymbols(symbols);
            initalized = true;
        }
    }




    private final Handler handler = new Handler();//for å mine hele tiden.

    private Snackbar snackbar;  //får se hvordan denne fungere og hva den gjør

    public int mId = 2112; //bare et random tall som er notifikasjons IDen, bruker den for å forandre informasjonen.

    private NotificationCompat.Builder notificationBuilder;
    private PendingIntent pendingOk;
    private PendingIntent pendingCancel;

        //lager en notification manager
    NotificationManager notificationManage;

    private int postDelay = 1; //delay på tråden, default 1 sec.

    BigDecimal bigMiningValue = new BigDecimal(0.0000); //i starten.
    BigDecimal bigBTCamountMined = new BigDecimal(0.0000);

    BigDecimal bigBTCminedTotal = new BigDecimal(0.0000); //dette skal holde på hvor mye vi har minet fra servicen, men tror jeg fjerner denne.

    //viss vi har minet masse fra før, og skal fortsette å mine, kunne det kanskje vært best om vi henter initialiseringen fra det vi lagret sist.
    //altså ikke det Profilen til spilleren har som totalt, men det Servicen lagred ved Avsluttning av appen.
    //Eller vi lagrer alt i samme database som bruker profilen og bare legger de sammen og forsette fra 0 her.

    private Intent broadCastIntent;

    private String profileName;

    public MinerService(){}//tom konstruktor

    public synchronized int getPostDelay(){ //siden tråden kan prøve å bruke den.
        return postDelay;
    }
    public synchronized int getPostDelayInMS(){
        return postDelay*1000;
    }


    public synchronized void setPostDelay(int delay){ //kan bruke denne for threadsafe endring av delayet.
                            //mulig det kunn er handeleren threaden som bare vill bruke denne?.
        postDelay = delay;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Starting service");

        broadCastIntent = new Intent(BROADCAST_ACTION);//initialiserer broadcast intentet
        initalizeFormating(); //slik at BTCValue ser litt bedre ut.

        //lager et filter for BroadCastReciveren.
        IntentFilter filter = new IntentFilter();
        filter.addAction(ResourceFragment.SEND_RESPONSE); //lager filter her istedet for i XMLen
        //bare å legge til flere filter viss vi skal send mer hit, og ta hånd om de i recieveren.
        filter.addAction(GameActivity.SEND_VALUE_CHANGE);
        filter.addAction(this.SEND_CANCEL_CONFIRMATION); //sendes når brukeren vill skru av servicen.
        filter.addAction(ContinueActivity.GET_DELETED_PLAYER_ID);
        filter.addAction(this.SEND_CANCEL); //sendes når brukeren vill skru av servicen.
        filter.addAction(this.SEND_OK);
        filter.addAction(GameActivity.SEND_DELAY_CHANGE);

        filter.addAction(BROADCAST_ACTION_API15_SHUTDOWN);//for API lvl 15.

        filter.addAction(BROADCAST_ACTION_SHUTDOWN); //denne skal sendes av systemet når mobilen skrus av, vi kan da lagre, etc

        registerReceiver(broadcastReceiver, filter);

        notificationManage = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    private void setBigProfileMiningValue(BigDecimal big){
        if(big.equals(BigDecimal.ZERO)){ // den er 0
            Log.e(TAG,"Error, broadcast sender verdien 0...Til setBigProfileMiningValue");
        }else
            setBigMiningValue(big);

    }


    //tar imot Broadcast som blir sent til Servicen.
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ResourceFragment.SEND_RESPONSE://vi har fått svar på broadcastene vi sender(
                    bigBTCamountMined = BigDecimal.ZERO; //setter denne til å være eksakt 0.
                    break;
                case GameActivity.SEND_VALUE_CHANGE: //viss vi skal oppdatere hvor mye vi får pr sec.

                    String bigValueChange = intent.getExtras().getString(GameActivity.VALUE_CHANGE_KEY);
                    try {
                        setBigProfileMiningValue(new BigDecimal(bigValueChange));
                    } catch(NullPointerException e) {
                        //vi får Null fra Broadcasten fra GameActivity om value change.
                        Log.e(TAG,e.getMessage());
                    }

                    startInForeground(); //oppdaterer Notificationen til å ta med hva vi miner pr sec.
                    //Log.i(TAG,"Vi miner: "+String.format("%.4f", profileMiningValue)+" btc pr sec" );
                    break;
                        //todo: bytt ut stringen med en static String over.
                case MinerService.BROADCAST_ACTION_SHUTDOWN: //brukeren skur av mobilen
                    stopMinerServiceAndSave();
                    break;
                case MinerService.SEND_CANCEL_CONFIRMATION://viss brukern prøved å skru av servicen fra notifikasjons bar.
                    //kunne strengt tatt
                    //Log.d(TAG, "Vi prøver å sku av servicen, viss appen kjørere skal vi gi en feilmelding?");
                    notificationBuilder.mActions.clear(); //dunno om denne faktisk gjør noe da

                    sendConfirmationNotification(); //skulle gjerne ha byttet ut actionene med 2 andre, altså en med OK og en Mec CANCEL
                                //og da kan man velge OK får å være sikker på å skru av servicen og Cancell for å bare oppdater notifiaksjone med det som stod der før.
                                //men da må jeg basicly ha 3 broadcasts, som kan bli mye strev, (dårlig kode).
                    //stopForeground(true); //fjerner notifikasjonen
                    //stopSelf(); //stopper servicen.
                    //TODO: lag en annen notification som inneholder en status på appen.
                    break;
                case MinerService.SEND_CANCEL:
                    notificationBuilder.mActions.clear(); //fjerner de to actione vi nettop la til
                    startInForeground();
                    break;
                case MinerService.SEND_OK:
                    stopMinerServiceAndSave(); //denne kan vi bruke flere ganger
                    handler.removeCallbacks(sendUpdateToUI); //stopper tråden.
                    Log.d(TAG,"Vi stopper servicen");
                    break;
                case ContinueActivity.GET_DELETED_PLAYER_ID:
                    long id = intent.getExtras().getLong(ContinueActivity.DELETED_PROFILE_ID);
                    //en kjapp test på om de er like
                    if(id == player_id){
                        //de er like, må da stoppe prosessen
                        //Siden det betyr at vi har slettet profilen servicen drev og minet med, noe som ikke er vits.
                        stopForeground(true);
                        clearService();
                        stopSelf();
                    } //viss dette ikke er profilen vi miner med, så kan vi bare fortsette som før.
                    break;
                case GameActivity.SEND_DELAY_CHANGE:
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    int delayTest = Integer.valueOf(sharedPreferences.getString("mining_delay", "300"));

                    //notificationBuilder.setContentTitle("Mining Bitcoins (" + getPostDelay()+" s delay)");

                    handler.removeCallbacks(sendUpdateToUI);
                    if(delayTest != 0){//bare i tilfelle.
                        updateDelay(delayTest);
                    }
                    handler.postDelayed(sendUpdateToUI,getPostDelayInMS()); //oppdaterer delayet.

                    startInForeground();//oppdaterer notifikasjonen.
                    //handler.postDelayed(sendUpdateToUI, getPostDelayInMS());//slik at vi har 100 ms på å oppdater Delay.

                    break;
                case MinerService.BROADCAST_ACTION_API15_SHUTDOWN:
                    Log.d(TAG, "API 15 movil skrur av Notifikasjon");
                    stopMinerServiceAndSave();
                    break;
            }
            //send broadcast for motatt data.
        }
    };

    public long getSecondsSinceLastThreadrun(){
        long timeNow = System.currentTimeMillis();
        timeNow = timeNow - timeSinceLastMining;

        timeNow = timeNow / 1000; //milisekunder til sekunder

        return timeNow;
    }

    public void stopMinerServiceAndSave(){ //flere tilfeller denne kan kjøre.
        stopForeground(true); //fjerner notifikasjonen.
        if(player_id != -1) {//viss vi ikke har noen profil å lagre til, har vi ikke noe vi har minet heller.
            BigDecimal TimeNowBD = new BigDecimal(getSecondsSinceLastThreadrun()); //holder sekundene siden sist kjøring i en BigDecimal.

            BigDecimal newValueMined = TimeNowBD.multiply(bigMiningValue); //ganger med hvor mye vi skal få pr sek.

            bigBTCamountMined = bigBTCamountMined.add(newValueMined); //legger til nye verdi.

            SQLiteHelper.getInstance(this).addBTCToProfile(bigBTCamountMined, player_id); //lagrer dette til riktig profil i databasen.

        }
        stopSelf();
    }

    public void clearService(){
        this.updateDelay(1);//bytter tilbake til 1 sec delay, viss det ikke var det.

        this.setBigBTCamountMined(BigDecimal.ZERO);//resetter miningAmount
        this.setBigMiningValue(BigDecimal.ZERO);  //resetter miningValue.

    }

    //håndterere bytting av hvilken profil vi miner med.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long new_player = intent.getExtras().getLong(MinerService.PLAYER_ID);//henter player_id
        handler.removeCallbacks(sendUpdateToUI); //siden vi skal starte med ny Profil, må vi også stoppe tråden.
            //ettersom vi potensielt har byttet profil, så kan vi ikke risikere at tråden kjører akkurat idet vi er på vei å bytte.
            //som vil gi den andre profilen plutselig veldig mye.

        profileName = intent.getExtras().getString(MinerService.PLAYER_NAME);//henter navnet til profilen.

        if(player_id == -1){//vill si at ingen Profil miner for øyeblikket
            //setMiningServiceValues(intent);
        }else if(player_id == new_player ){//
            if(getPostDelay() != 1){//viss vi ikke har 1 sec delay, så vill vi potensielt miste opp til max delay med Bitcoins.

                BigDecimal TimeNowBD = new BigDecimal(getSecondsSinceLastThreadrun()); //holder sekundene siden sist kjøring i en BigDecimal.
                BigDecimal newValueMined = TimeNowBD.multiply(bigMiningValue);
                bigBTCamountMined = bigBTCamountMined.add(newValueMined); //legger til nye verdi.
            }
            //da skal vi bare fortsette.
            //setMiningServiceValues(intent);
        }else{//vill si at noen allerede bruker Servicen til å mine.
            Log.d(TAG, "Vi kommer hit til Onstart...");
            //bare en sjekk på databasen om vi får noe resultalt på å søke etter IDen i databasen i ProfileTable.
            boolean profileStillExist = SQLiteHelper.getInstance(this).profileExists(player_id);

            if(profileStillExist){ //skal lagre det som vi har mint opp.
                Log.d(TAG, "MinerService skal lagre i databasen");
                //siden vi vill ha et delay på sirka 5 min, risikere man å miste 5 min med mining verdi.
                BigDecimal TimeNowBD = new BigDecimal(getSecondsSinceLastThreadrun()); //holder sekundene siden sist kjøring i en BigDecimal.
                BigDecimal newValueMined = TimeNowBD.multiply(bigMiningValue);
                bigBTCamountMined = bigBTCamountMined.add(newValueMined); //legger til nye verdi.
                SQLiteHelper.getInstance(this).addBTCToProfile(bigBTCamountMined,player_id);

                clearService();
            }else{//bare forkaster hva som har blitt gjort i løpet av tiden.
                clearService();
            }
        }

        handler.postDelayed(sendUpdateToUI, 1000); //1 sec delay ved oppstart av servicet.
        setPostDelay(1);//setter Delay til å være 1 sec.

        setMiningServiceValues(intent); //ikke alltid nødvendig.

        startInForeground();//oppdaterer Notifikasjonen med nytt Profile navn og diverse.
        return Service.START_NOT_STICKY; //vill ikke at den skal starte opp igjenn viss servicen ikke lukker seg selv
    }


    public void setMiningServiceValues(Intent intent){
            //henter player_id som vi sente med intentet.
        player_id = intent.getExtras().getLong(MinerService.PLAYER_ID);

                //viss profilen kommer fra continue
        if(intent.getAction() == ContinueActivity.continueGame){//da vet vi at vi også har sent med BTC_VALUE extraen.

            String bigMiningValue = intent.getExtras().getString(MinerService.BTC_VALUE);
            //legger inn bigMiningValue som vi fikk vite fra profilen.
            setBigMiningValue(new BigDecimal(bigMiningValue));
        }
        //viss den ikke kommer fra continueActivity, så må den komme fra New Game Activity.
        //og da skal vi ikke bytte annet enn player_id
    }

    //TODO: send broadcast ved exit av Appen.
    private synchronized void updateDelay(int delayInt){
        setPostDelay(delayInt); //threadsafe forandring i tilfelle
    }


    /*###################################################################*/
    /*             Tråd som kjørere hele tiden med tidsDelay            */

    private Runnable sendUpdateToUI = new Runnable() {//denne som miner hvert sekund.
        @Override
        public void run() {

            //todo: ta delay i betraktning, men hva gjør vi viss vi sender update på delay midt i en update?
            if(bigMiningValue.compareTo(BigDecimal.ZERO) != 0){ //sjekker om vi har null som Value.

                setBigBTCamountMinedThread();

                //bigBTCamountMined = bigBTCamountMined.add(bigMiningValue.multiply(new BigDecimal(getPostDelay())));

                SendMinedCoins();

                timeSinceLastMining = System.currentTimeMillis();

            }else{//denne kjører før vi har begynt å mine.
                notificationBuilder.setProgress(100, 0, false);
                notificationBuilder.setContentText("Waiting for something to mine...");
                notificationBuilder.setSubText(null);
            }


            if(bigBTCamountMined.compareTo(BigDecimal.ZERO) != 0 && getPostDelay() != 1){ //denne er 0 viss bigBTCamount == 0.
                //Log.d(TAG,"Vi prøver å sette ny notification.");
                notificationBuilder.setSubText(decimalFormatBTC.format(bigBTCamountMined) + " BTC");
                notificationBuilder.setContentTitle("Mining Bitcoins ("+getReadableDelay()+" delay)");
            }else{ //den er lik 0.
                //Log.d(TAG,"Vi setter den til null");
                notificationBuilder.setSubText(null);//setter denne til null, viss det fjerner den da.
            }

            startForeground(mId, notificationBuilder.build()); //denne oppdaterer notifikasjonen.

            //viss det ikke er noe forandring gjør ikke denne noe tror jeg.
            handler.postDelayed(this, getPostDelayInMS()); // 1 sec delay før vi miner igjenn.
        }
    };

    /*                    end Tråd                    */
    /*##################################################*/

    public synchronized String getReadableDelay(){
        int delay = getPostDelay(); //henter delay i sekunder
        if(delay == 1){
            return "1 s";
        }
        String sDelay = String.valueOf(delay/60)+" m";
       return sDelay;
    }

    public synchronized void setBigBTCamountMinedThread(){
        bigBTCamountMined = bigBTCamountMined.add(bigMiningValue.multiply(new BigDecimal(getPostDelay())));
    }

    public synchronized void setBigBTCamountMined(BigDecimal bd){
        bigBTCamountMined = bd;
    }

    public synchronized void setBigMiningValue(BigDecimal bd){
        bigMiningValue = bd;
    }

    private void SendMinedCoins(){ //gjør som navnet sier og informere om mined coins.

        broadCastIntent.putExtra("sendMinedValue", bigBTCamountMined.toString());

        broadCastIntent.putExtra("profileNameFromService",profileName); //sender med profil navnet for å sjekke om det matcher.
        sendBroadcast(broadCastIntent);
    }


    private void startInForeground(){ //TODO: litt krevende å kjøre på denne måten?

        //TODO: husk å dokumentere at denne kanskje krever API 16(set foreground()), men det er en ganske nødvenig funksjon
        //Notification.Builder.addAction Adden in API level 16, dedeprecated in API level 23.

        //TODO: CONT: slik at det er en nødvendig endring.
        Intent stopServiceIntent = new Intent(SEND_CANCEL_CONFIRMATION);
        PendingIntent pendingIntentStop = PendingIntent.getBroadcast(this, 0,stopServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //dunno om dette fungere.
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_pickaxe)
                .setContentTitle("Mining Bitcoins ("+getReadableDelay()+" delay)")
                .setContentText(decimalFormatBTC.format(bigMiningValue) + " BTC/sec")
                .setContentInfo(profileName) //text helt til høyre, ved siden av ContentText.
                .setGroupSummary(true) //får se hva denne gjør
                //.setSubText(decimalFormatBTC.format(bigBTCamountMined) + " BTC") //denne er litt teit, med at det står hele tiden 0 her.
                .setTicker("Mining service running")
                .addAction(R.drawable.cancel_24, "Save and Stop Mining service", pendingIntentStop) //må finne ut hvordan jeg bruker denne.
                .setPriority(NotificationCompat.PRIORITY_MAX); //gjør slik at actionene ikke blir underprioritet med plass
                                                                    //i notifikasjones bare( andre tar plassen deres).
        //når vi trykker på knappen fyrer PendingIntentet, men aner ikke hvordan kontrolere det.

        Intent notificationIntent = new Intent(this, MinerService.class); //dunno hva denne gjør?
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //intent til å vise loadingbar

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent); //dette tror jeg er den "loading/progress" baren.

        notificationBuilder.setProgress(100, 0, true); //loadingbaren bare fortsetter å loade.

        startForeground(mId, notificationBuilder.build()); //vi får se om dette gjor noe nyttig.
    }

    public void sendConfirmationNotification(){
        Intent sendOk = new Intent(SEND_OK);
        Intent sendCancel = new Intent(SEND_CANCEL);

        pendingOk = PendingIntent.getBroadcast(getApplicationContext(),0,sendOk,PendingIntent.FLAG_UPDATE_CURRENT);

        pendingCancel = PendingIntent.getBroadcast(getApplicationContext(),0,sendCancel,PendingIntent.FLAG_UPDATE_CURRENT);

        //TODO: finn bedre fargede action icons med svart/hvitt icon og transparent i midten.
        notificationBuilder.mActions.clear();//fjerner alle actions
        notificationBuilder.addAction(new NotificationCompat.Action(R.drawable.ok_sv_48,"Ok", pendingOk));
       // new NotificationCompat.Action(R.drawable.ok_48,"Ok", pendingOk);

        notificationBuilder.addAction(R.drawable.cancel_sv_48,"Cancel", pendingCancel);

        startForeground(mId, notificationBuilder.build());
    }


    @Override
    public void onDestroy() {

        //fjerner eksisterende callbacks slik at vi ikke får flere enn vi vill ha
        handler.removeCallbacks(sendUpdateToUI); //fjerner tråden, viss det ikke er gjordt.
        //todo: lagre?

        //fjerner Broadcast Receiveren.
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
