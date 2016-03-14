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
import com.sondreweb.cryptoclicker.game.Profile;

/**
 * Created by sondre on 14-Mar-16.
 */
public class ResourceFragment extends Fragment{
    private static final String TAG ="FRAGMENT_RESOURCE";

    public static final String SEND_RESPONSE ="com.sondreweb.cryptoclicker.RESPONSE";

    private IntentFilter filter = new IntentFilter();

    private TextView btcAmount;
    private static TextView btcAmountStatic; //testing
    private TextView udsAmount;

    private Intent broadCastIntentToService;

    public ResourceFragment(){} //tom konstruktør

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource,container,false);

        btcAmount = (TextView) view.findViewById(R.id.btcAmountTextView);
        btcAmountStatic = (TextView) view.findViewById(R.id.btcAmountTextView);

        udsAmount = (TextView) view.findViewById(R.id.usdAmountTextView);

        //lag broadCast for send

        broadCastIntentToService = new Intent(SEND_RESPONSE);

        return view;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getBroadcastData(intent);
            //funksjon som kjører når MinerServicen sender broadcast
        }
    };

    private void getBroadcastData(Intent intent){
        Log.d(TAG, "Vi har mottatt Broadcasten");
        Bundle b = intent.getBundleExtra("Bundle");
        double btcAmount = b.getDouble("btcAmountMined");
        Profile.CurrentProfil.addBTCs(btcAmount);
        //må gjøre noe mer med denne.
        getActivity().sendBroadcast(broadCastIntentToService);
        //send Message til service.
        updateResourceUI(50); //50ms delay, bare fordi.
    }

    @Override
    public void onResume() {
        super.onResume();
        filter.addAction(MinerService.BROADCAST_ACTION);
            //registere Receiveren med filteret på broadcastene vi skal lese og gjøre noe med.
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    public void updateResourceUI(int delay){
        //delay pga MinerIntentService trenger litt tid på å oppdatere hva vi har av BTCs.
        btcAmount.postDelayed(new Runnable() {  //burde denne være tread safe?
            public void run() {
                btcAmount.setText(Profile.CurrentProfil.returnBTCamountAsString());
            }
        }, delay);
    }

    @Override
    public void onPause() {
        super.onPause();
        //fjerner slik at vi ikke henter fram broadcast når fragmentet sover.
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    public static void updateResourceUITest(){
        btcAmountStatic.post(new Runnable() {
            @Override
            public void run() {
                btcAmountStatic.setText(Profile.CurrentProfil.returnBTCamountAsString());
            }
        }); //får se om dette fungerer..
    }
}
