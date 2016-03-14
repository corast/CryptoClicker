package com.sondreweb.cryptoclicker;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sondreweb.cryptoclicker.game.Game;
import com.sondreweb.cryptoclicker.game.Profile;

/**
 * Created by sondre on 09-Mar-16.
 */
public class MinerServiceTest_IKKE_I_BRUK extends Service {

    private static final String TAG = "MainMainerService";
    public static final String BROADCAST_ACTION = "com.sondreweb.cryptoclicker.action.DISPLAYEVENT";

    private final Handler handler = new Handler();//for å mine hele tiden.

    Intent startIntent;

    double btcMinedTotal = 0;

    double btcAmountMined = 0; //testing


    //denne skal liksom være hva brukeren miner pr sec med sine oppgraderinger.
    double profileMiningValue = 0.001; //denne må oppdateres etterhvert.

    private Intent intent;
    public static final int BTC_COLLECTED = 1;
    public static final int VALUE_CHANGED = 2;

    final Messenger mMessager = new Messenger(new IncomingMessageHandler());

    public MinerServiceTest_IKKE_I_BRUK(){}//tom konstruktor

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "minerService Startet");
        intent = new Intent(BROADCAST_ACTION);
    }



    class IncomingMessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BTC_COLLECTED:
                    setBtcAmountMined(0); //resetter, siden vi har tatt imot broadcasten.
                    break;
                case VALUE_CHANGED:
                    //betyr at vi har sent med en value også.
                    msg.getData();
                    break;
                default:
                    super.handleMessage(msg);


            }
        }
    }






    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.removeCallbacks(sendUpdateToUI);
        handler.postDelayed(sendUpdateToUI, 1000); //delayer 1 sec før vi kjører.
        startIntent = intent;
        Game.minerIntent = intent;
        return Service.START_NOT_STICKY; //vill ikke at den skal starte opp igjenn viss servicen ikke lukker seg selv
    }




    private Runnable sendUpdateToUI = new Runnable() {  //denne som oppdaterer
        @Override
        public void run() {

            //mine
            btcAmountMined = btcAmountMined + profileMiningValue;
            btcMinedTotal += profileMiningValue;
            //send info;
            SendInfo();
            handler.postDelayed(this,5000);//delayer 5 sec før vi kjører run() igjenn

        }
    };

    private void SendInfo(){
        Log.d(TAG, "Entered SendInfo");
        Log.d(TAG, "Minet i service: " + btcMinedTotal);
        Bundle b = new Bundle();
        b.putDouble("btcAmountMined", btcAmountMined);
        intent.putExtra("btcAmountMined", b);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        //lagre?
        //fjerner eksisterende callbacks slik at vi ikke får flere enn vi vill ha
        handler.removeCallbacks(sendUpdateToUI);
        super.onDestroy();
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //testing
        return mMessager.getBinder();
    }

    public void setBtcAmountMined(double btcAmountMined) {
        this.btcAmountMined = btcAmountMined;
    }

    public void setProfileMiningValue(double profileMiningValue) {
        this.profileMiningValue = profileMiningValue;
    }

    public Intent getStartIntent(){
        return startIntent;
    }


}
