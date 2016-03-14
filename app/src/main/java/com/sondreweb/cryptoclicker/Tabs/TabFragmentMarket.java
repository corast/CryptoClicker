package com.sondreweb.cryptoclicker.Tabs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.game.Profile;

import java.util.ArrayList;

/**
 * Created by sondre on 14-Feb-16.
 */

//TODO: http://developer.android.com/guide/topics/ui/layout/listview.html  Mye bedre å bruke dette en å måtte adde views etc.
public class TabFragmentMarket extends Fragment{

    //for å hide views(elements) http://stackoverflow.com/questions/5756136/how-to-hide-a-view-programmatically

    public static final String TAG ="TabFragmentMarket";

    public static final String SEND_VALUE_CHANGE ="com.sondreweb.cryptoclicker.Tabs.VALUE_CHANGE";

    //instansier alle de knappene vi vill trenge? kanskje bedre å behandle de anonymt
    private Button buyCpu1Button;

    private Intent broadcastServiceValueChange;//broadcast intent.

    private double testValue;

    private TextView amountCpu1;
    private int amountCpu1Test = 0;

    public TabFragmentMarket(){
        //tom konstruktør, påkrevd
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_tab_market,container,false);

        broadcastServiceValueChange = new Intent(SEND_VALUE_CHANGE);
        amountCpu1 = (TextView) view.findViewById(R.id.textViewAmountcpu1text);
        buyCpu1Button = (Button) view.findViewById(R.id.buyCpu1Button);
        setOnClickListener(buyCpu1Button);
        return view;
    }


    //TODO: gjør slik at det ikke er mulig å trykke på Buy knapp viss man ikke har nok ressurser å bruke.
    private void setOnClickListener(Button button){
                        //TODO: lag en eventhandler til alle knappene, som henter ut KnappIDen og bruker det å determinere hvilken vi prøvde å kjøpe.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((Button)v).setBackgroundColor(Color.GRAY);//setter knappen grå for å teste det.
                //v.setVisibility(v.INVISIBLE); //fjerner buy knappen.
                Profile.CurrentProfil.setCurrentValue(0.0001);


                //sender Broadcast om forandring i Valuen.
                sendValueChangeToService();


                amountCpu1Test++;  //bare for å sjekke. må finne på en bedre måte enn det her..

            }
        });
    }



    private void sendValueChangeToService(){//denne kan kjøre ved eventen at man kjøper en oppgradering.
        Bundle bundle = new Bundle();
        bundle.putDouble("valueChange", Profile.CurrentProfil.getCurrentValue()); //legger til hvor mye vi får pr sec.
        broadcastServiceValueChange.setAction(SEND_VALUE_CHANGE);
        broadcastServiceValueChange.putExtra("Bundle", bundle);

        getActivity().sendBroadcast(broadcastServiceValueChange); //getActivity() eller getContext .sendBroadcast ?
    }


    private double getUpdatedValue(){
        //skal hente hvor mye bruken har kjøpt fra arrayListen inni profile, siden arrayListene her og i Upgrade kunn er Defaulte, altså 0 er kjøpt opp.

        return 0.001;  //det vi regner ut det som brukeren har akkurat i dette øyeblikk.
    }

    //Denne klassen trenger en broadcast, for når vi kjøper en oppdatering.

}
