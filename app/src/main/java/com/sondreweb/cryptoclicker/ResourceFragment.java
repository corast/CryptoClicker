package com.sondreweb.cryptoclicker;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.Services.MinerService;
import com.sondreweb.cryptoclicker.Tabs.TabFragmentExchange;
import com.sondreweb.cryptoclicker.Tabs.TabFragmentMarket;
import com.sondreweb.cryptoclicker.Tabs.TabFragmentProgress;
import com.sondreweb.cryptoclicker.game.Profile;

/**
 * Hovedansvar er å ta imot broadcast fra Servicen hvert sekund og å oppdatere Visning for BTC og USD.
 * Sørger også for at ListViews holdes oppdatert, samt Info i progress Taben, etter som denne mottar meldinger hver sekund, kan vi også oppdatere disse ved forandring i verdi.
 */

public class ResourceFragment extends Fragment{
    private static final String TAG ="FRAGMENT_RESOURCE";

    public static final String SEND_RESPONSE ="com.sondreweb.cryptoclicker.RESPONSE";

//    public static final String SEND_VALUE_CHANGE ="com.sondreweb.cryptoclicker.Tabs.VALUE_CHANGE";

    private IntentFilter filter = new IntentFilter();

    private static TextView btcAmount; //fungerte bedre en ikke-static

    private static TextView udsAmount;

    private Intent broadCastIntentToService; //må bruke denne

    //private static Intent broadCastValueChangeToService; //må bruke denne

    public ResourceFragment(){} //tom konstruktør

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource, container, false);

        btcAmount = (TextView) view.findViewById(R.id.tv_resource_btc);

        udsAmount = (TextView) view.findViewById(R.id.tv_resource_usd);
        //lag broadCast for send

        broadCastIntentToService = new Intent(SEND_RESPONSE);

        return view;
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {//tar imot minet ressurser fra MinerService.
        @Override
        public void onReceive(Context context, Intent intent) {
            getBroadcastData(intent);

        }
    };


    private void getBroadcastData(Intent intent){

        String valueRecieved = intent.getExtras().getString("sendMinedValue");

        //henter profil navnet som ble sendt fra servicen. Kan sjekke om det matcher med det vi sitter med.
        String profileNameRecieved = intent.getExtras().getString("profileNameFromService");
        //Profile.CurrentProfil.addBTCs(btcAmount);


        //todo: bedre måte å finne ut om vi har byttet profil underveis, Burde si ifra til MinerServicen at vi har gått ut av Activitien GameActivity
        if(profileNameRecieved.compareToIgnoreCase(Profile.CurrentProfil.getName()) == 0 ) {
            //returnere -10 eller 10 viss annerledes og 0 viss like.

            //profilene har samme navn
            Profile.CurrentProfil.addBigBTC(valueRecieved);//legger til double verdi inn i BigDecimalen.

            //må gjøre noe mer med denne.
            getActivity().sendBroadcast(broadCastIntentToService);//fra Underliggende activity.
            //send Message til service.
            updateResourceUIWithMainThread();

            if (TabFragmentMarket.isOpen()) { //vet ikke om dette kan gjøres bedre //TODO: testing.
                TabFragmentMarket.updateListView();
            }else if(TabFragmentProgress.isOpen()){
                TabFragmentProgress.updateTextViews();
            }else if(TabFragmentExchange.isOpen()){ //betyr at brukeren har Tabben Exchange oppe.
                TabFragmentExchange.updateListView();
            }

        }else{ //dette fungerte ikke tror jeg pga at vi fortsatt jobber med de samme ArrayListene til forrige profil.
            //TODO: vi bytter profil, lagre det du minnet og restart?
            Log.d(TAG,"Vi har byttet profil underveis.");
        }
    }

    @Override
    public void onResume() { //kunne også ha en broadcast reviecer i GameActivity, og filtere ut til hvert fragment etterhvert som de skal ha det.
                            //men nesten letter at de har egne som kunn er aktive når de selv er aktive, programmerings messig.
        super.onResume();
        filter.addAction(MinerService.BROADCAST_ACTION);
            //registere Receiveren med filteret på broadcastene vi skal lese og gjøre noe med.
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    public static void updateResourceUIWithMainThread(){

        btcAmount.post(new Runnable() {  //er denne thread safe?
            public void run() {
                btcAmount.setText(Profile.CurrentProfil.getBigBTCamountAsString());
                udsAmount.setText(Profile.CurrentProfil.getBigUSDamountAsString());
            }
        });
    }

    public static void updateResourceUIAsMainThread(){ //trenger ikke lage en tråd med UI tråd som forteller UI tråd å oppdatere UI...
        btcAmount.setText(Profile.CurrentProfil.getBigBTCamountAsString());
        udsAmount.setText(Profile.CurrentProfil.getBigUSDamountAsString());
    }

    @Override
    public void onPause() {
        super.onPause();
        //fjerner slik at vi ikke henter fram broadcast når fragmentet sover.
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
