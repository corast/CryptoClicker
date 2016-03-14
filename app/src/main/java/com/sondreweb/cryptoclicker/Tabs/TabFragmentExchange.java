package com.sondreweb.cryptoclicker.Tabs;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sondreweb.cryptoclicker.MinerServiceTest_IKKE_I_BRUK;
import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.game.Game;

/**
 * Created by sondre on 14-Feb-16.
 */
public class TabFragmentExchange extends Fragment {

    Messenger mService = null;

    boolean mBound;

    private int btcCollected;


    //class for interaksjon med hoved interfacet i Servicen
    private ServiceConnection mConnection = new ServiceConnection(){
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className){
            mService = null;
            mBound = false;
        }
    };


    public void sendMessage(){
        if(!mBound) return;

        Message msg = Message.obtain(null, MinerServiceTest_IKKE_I_BRUK.BTC_COLLECTED,0,0);
        try{
            mService.send(msg);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            getBroadcastData(intent);

            //send broadcast for motatt data.

        }
    };


    private void getBroadcastData(Intent intent){
        Bundle b = intent.getBundleExtra("btcAmountMined");
        double btcAmount = b.getDouble("btcAmountMined");
        btcCollected +=btcAmount; //legger til det som er minet til nå.
        Log.d("Service intent test", "btcAmountMined: " + btcAmount + "");
        //må gjøre noe mer med denne.

        //send Message til service.
        sendMessage();
        //updateUI();

        Log.d("TabFragmentClass","Vi har minet tilsammen: " + btcCollected);

    }

    public TabFragmentExchange(){
        //tom konstruktør, påkrevd
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_exchange,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        //binder
       // getActivity().bindService(Game.getMinerIntent(),mConnection,Context.BIND_ADJUST_WITH_ACTIVITY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unbind from service
        /*if(mBound){
            getActivity().unbindService(mConnection);
            mBound = false;
        }*/
    }
}
