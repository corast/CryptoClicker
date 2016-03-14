package com.sondreweb.cryptoclicker.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sondreweb.cryptoclicker.ResourceFragment;
import com.sondreweb.cryptoclicker.Tabs.TabFragmentClick;
import com.sondreweb.cryptoclicker.Tabs.TabFragmentMarket;
import com.sondreweb.cryptoclicker.game.Profile;

/**
 * Created by sondre on 06-Mar-16.
 */

//  https://www.websmithing.com/2011/02/01/how-to-update-the-ui-in-an-android-activity-using-data-from-a-background-service/
public class MinerService extends Service {

    //TODO: gjør slik at vi kan sende med data fra en eksisterende profil og fortsett fra den.
    //enten sende profilen med intent, med deres data.
    //eller bare sende med nødvendig data i intentet, som value.

            //for å ta imot data hvet jeg ikke helt. (hente de ut selv?)
    private static final String TAG = "MainMainerService";
    public static final String BROADCAST_ACTION = "com.sondreweb.cryptoclicker.action.DISPLAYEVENT";

    private final Handler handler = new Handler();//for å mine hele tiden.


    double btcMinedTotal = 0; //holder på hva vi har minet totalt siden start her.

    double btcAmountMined = 0; //holder på hva den har minet ut siden sist etterspørring.

    //denne skal liksom være hva brukeren miner pr sec med sine oppgraderinger.
    double profileMiningValue = 0; //denne må oppdateres etterhvert.

    private Intent broadCastIntent;

    private Intent broadCastReceiverBTC; //litt usikker på hva denne gjør..

    Profile currentProfile;

    public MinerService(){}//tom konstruktor


    @Override
    public void onCreate() {
        super.onCreate();

        broadCastIntent = new Intent(BROADCAST_ACTION);//initialiserer broadcast intentet

        //broadCastReceiverBTC = new Intent(this, TabFragmentClick.class);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ResourceFragment.SEND_RESPONSE); //lager filter her istedet for i XMLen
        //bare å legge til flere filter viss vi skal send mer hit, og ta hånd om de i recieveren.
        filter.addAction(TabFragmentMarket.SEND_VALUE_CHANGE);


        registerReceiver(broadcastReceiver, filter);
    }



    private void setprofileMiningValue(double value){
        if(value == 0){
            Log.d(TAG,"Error, broadcast sender verdien 0...");
        }else {
            profileMiningValue = value;
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ResourceFragment.SEND_RESPONSE: //håper dette ikke lager krøll
                    //Log.d(TAG, " Broadcast Mottat i TabFragmentClick");
                    btcAmountMined = 0; //threadsafe?
                    break;
                case TabFragmentMarket.SEND_VALUE_CHANGE: //viss vi skal oppdatere hvor mye vi får pr sec.
                    Bundle b = intent.getBundleExtra("Bundle");
                    setProfileMiningValue(b.getDouble("valueChange"));
                    //Log.i(TAG,"Vi miner: "+String.format("%.4f", profileMiningValue)+" btc pr sec" );
                    break;
            }
            //send broadcast for motatt data.
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.removeCallbacks(sendUpdateToUI);
        handler.postDelayed(sendUpdateToUI, 1000); //1 sec delay ved oppstart av servicet.

        /*Bundle extras = intent.getExtras();
        messageHandler = (Messenger) extras.get("MESSENGER");*/

        //sendMessage(MessageState.SHOW);

        return Service.START_NOT_STICKY; //vill ikke at den skal starte opp igjenn viss servicen ikke lukker seg selv
    }


    private Runnable sendUpdateToUI = new Runnable() {
        @Override
        public void run() {

            if(profileMiningValue != 0){//viss vi miner 0 i sekundet kan bare vente til neste itterasjon.
                //mine
                btcAmountMined = btcAmountMined +profileMiningValue;
                btcMinedTotal += profileMiningValue;
                SendInfo();
            }

            handler.postDelayed(this,1000); // 1 sec delay før vi miner igjenn.

        }
    };

    private void SendInfo(){
        Bundle b = new Bundle();
        b.putDouble("btcAmountMined", btcAmountMined);
        //Log.d(TAG, "Totalt minet til nå: " + btcMinedTotal );
        broadCastIntent.putExtra("Bundle", b);
        sendBroadcast(broadCastIntent);
    }

    @Override
    public void onDestroy() {
        //lagre?
        //fjerner eksisterende callbacks slik at vi ikke får flere enn vi vill ha
        handler.removeCallbacks(sendUpdateToUI);
        //fjerner Broadcast Receiveren.
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setBtcAmountMined(double btcAmountMined) {
        this.btcAmountMined = btcAmountMined;
    }

    public void setProfileMiningValue(double profileMiningValue) {
        this.profileMiningValue = profileMiningValue;
    }

}
