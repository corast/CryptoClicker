package com.sondreweb.cryptoclicker.Tabs;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.Activites.GameActivity;
import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.game.Profile;

/**
 * Denne har ansvar for å vise litt ekstra informasjon om profilen sin progress i spillet, mye utviklings potensiale her.
 *
 */
public class TabFragmentProgress extends Fragment {

    public static final String TAG = TabFragmentProgress.class.getName();

    //henter alle feltene vi kan legge data inn i.
    private static TextView  tv_miningValuePrSec;
    private static TextView tv_totBTCmined;
    private static TextView tv_totUSDamount;
    private static TextView tv_clickValue;
    private static TextView tv_clicks;

    private static boolean isOpen = false;

    public TabFragmentProgress(){}

    @Override
    public void onStart() {
        super.onStart();
        updateTextViews();
        isOpen = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isOpen = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_progress, container, false);

        tv_miningValuePrSec = (TextView) view.findViewById(R.id.tv_progress_btcValue);
        tv_totBTCmined = (TextView) view.findViewById(R.id.tv_progress_totBtcValue);
        tv_totUSDamount = (TextView) view.findViewById(R.id.tv_progress_totUsdAmount);
        tv_clickValue = (TextView) view.findViewById(R.id.tv_progress_clickValue);
        tv_clicks = (TextView) view.findViewById(R.id.tv_progress_clicks);

        return view;
    }

    public static void updateTextViews(){
        tv_miningValuePrSec.setText(Profile.CurrentProfil.getBigBTCValueAsString());
        tv_totBTCmined.setText(Profile.CurrentProfil.getBigTotBTCcounterAsString());
        tv_totUSDamount.setText(Profile.CurrentProfil.getBigTotUSDCounterAsString());
        tv_clickValue.setText(Profile.CurrentProfil.getBigClickerValueAsString());
        tv_clicks.setText(Profile.CurrentProfil.getClicks()+"");
    }

    public static boolean isOpen(){
        //return TabFragmentMarket.instantiate(Runtime)
        if(GameActivity.getTabSeleceted() == 3 && isOpen){  //denne bruker vi i GameActivity når den tar imot Broadcast med verdi fra MinerService
            //viss denne er sann kan den også kjøre en oppdate i listen.
            return true;
        }
        return false;
    }

}
