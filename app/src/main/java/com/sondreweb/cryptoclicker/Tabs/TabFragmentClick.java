package com.sondreweb.cryptoclicker.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sondreweb.cryptoclicker.ResourceFragment;
import com.sondreweb.cryptoclicker.Services.MinerIntentService;
import com.sondreweb.cryptoclicker.R;

/**
 * Created by sondre on 14-Feb-16.
 */
public class TabFragmentClick extends Fragment {
    private static final String TAG ="TAB_CLICK";

    public static final String SEND_RESPONSE ="com.sondreweb.cryptoclicker.Tabs.RESPONSE";
   // public static final String SEND_VALUE_CHANGE ="com.sondreweb.cryptoclicker.Tabs.VALUE_CHANGE";

    public static final String MINE = "com.sondreweb.cryptoclicker.Tabs.MINE";
    public static final String PRODUCE= "com.sondreweb.cryptoclicker.Tabs.PRODUCE";

    private Intent clickServiceIntent; //intent for starting av IntentServiceset.

    private ImageButton coinButton;

    public TabFragmentClick(){
        //tom konstruktør, påkrevd.
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //for å kunne lege til onclicklister til coinButton
        View view = inflater.inflate(R.layout.fragment_tab_click, container, false);

        coinButton = (ImageButton) view.findViewById(R.id.bitcoinClicker);


        //TODO: dollar stuffs for senere.


        coinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMinerIntent(); //fungere greit for nå.
                //må gjøre slik at vi avhengig av oppgraderinger får et visst antall som blir lagt til en
                // felles variabel mellom IntentServicet og denne.
            }
        });

        clickServiceIntent = new Intent(getActivity(),MinerIntentService.class);
        //initializerer intent.

        return view;
    }



    private void startMinerIntent(){
        Intent intent = new Intent(this.getContext(),MinerIntentService.class);
        intent.setAction(MINE);
        intent.putExtra(PRODUCE, "testing stuffs"); //egentlig ikke vits.
        getContext().startService(intent);

       // ResourceFragment.updateResourceUITest(50); //updatere UI med litt delay, slik at
        //                                      MinerIntetService kan gjøre seg ferdig.
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().startService(clickServiceIntent);//tror ikke jeg trenger dette..
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().stopService(clickServiceIntent);
    }

}
