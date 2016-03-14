package com.sondreweb.cryptoclicker.Tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.ResourceFragment;
import com.sondreweb.cryptoclicker.Services.MinerService;
import com.sondreweb.cryptoclicker.Services.MinerIntentService;
import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.game.Profile;

/**
 * Created by sondre on 14-Feb-16.
 */
public class TabFragmentClick extends Fragment {
    private static final String TAG ="TAB_CLICK";

    public static final String SEND_RESPONSE ="com.sondreweb.cryptoclicker.Tabs.RESPONSE";
   // public static final String SEND_VALUE_CHANGE ="com.sondreweb.cryptoclicker.Tabs.VALUE_CHANGE";

    public static final String MINE = "com.sondreweb.cryptoclicker.Tabs.MINE";
    public static final String PRODUCE= "com.sondreweb.cryptoclicker.Tabs.PRODUCE";

    private Intent broadCastIntent;
    private Intent broadCastIntentToService;

    private ImageButton coinButton;
    private TextView btcAmount;
    private TextView udsAmount;

    public TabFragmentClick(){
        //tom konstruktør, påkrevd.
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //for å kunne lege til onclicklister til coinButton
        View view = inflater.inflate(R.layout.fragment_tab_click, container, false);

        coinButton = (ImageButton) view.findViewById(R.id.bitcoinClicker);
       // btcAmount = (TextView) view.findViewById(R.id.bitcoinAmount);
        //udsAmount = (TextView) view.findViewById(R.id.usdAmountTextView);

        //TODO: dollar stuffs for senere.


        coinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMinerIntent(); //fungere greit for nå.
                //må gjøre slik at vi avhengig av oppgraderinger får et visst antall som blir lagt til en
                // felles variabel mellom IntentServicet og denne.
            }
        });

        broadCastIntent = new Intent(getActivity(),MinerIntentService.class);

        broadCastIntentToService = new Intent(SEND_RESPONSE);

        return view;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //burde denne sjekke om det er riktig broadcast?
            //TODO: fjern dette, skal ikke ta imot noe fra her.
            //getBroadcastData(intent);
            //send broadcast for motatt data.
        }
    };



    private void getBroadcastData(Intent intent){
        Bundle b = intent.getBundleExtra("Bundle");
        double btcAmount = b.getDouble("btcAmountMined");
        Profile.CurrentProfil.addBTCs(btcAmount);
        //må gjøre noe mer med denne.
        getContext().sendBroadcast(broadCastIntentToService);
        //send Message til service.
        updateUI(50);

    }

    private void startMinerIntent(){
        Intent intent = new Intent(this.getContext(),MinerIntentService.class);
        intent.setAction(MINE);
        intent.putExtra(PRODUCE, "testing stuffs"); //egentlig ikke vits.
        getContext().startService(intent);

        ResourceFragment.updateResourceUITest();
        //updateUI(50);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().startService(broadCastIntent);//`?
        //filterer ut meldingen vi skal ha.
        getActivity().registerReceiver(broadcastReceiver,
                new IntentFilter(MinerService.BROADCAST_ACTION));//okay
        //registerer Receiveren vi skal ha med et filter.
        try{ //ungår nullPointException.
           // updateUI(0);                  //fjernet så lenge
        }catch (NullPointerException npe){
            Log.d(TAG,npe.toString());
        }catch (Exception e){
            Log.d(TAG,e.toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().stopService(broadCastIntent);
    }



    public void updateUI(int delay) {


        btcAmount.postDelayed(new Runnable() {
            public void run() {
                btcAmount.setText(Profile.CurrentProfil.returnBTCamountAsString());
            }
        }, delay);


        //btcAmount.setText(Profile.CurrentProfil.returnBTCamountAsString());
        //udsAmount.setText(Profile.CurrentProfil.returnUSDamountAsString());
        //Log.d("Update ui", "Oppdatare UI inni function");
    }

}
