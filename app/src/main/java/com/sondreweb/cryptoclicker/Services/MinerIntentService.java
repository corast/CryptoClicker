package com.sondreweb.cryptoclicker.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.sondreweb.cryptoclicker.ResourceFragment;
import com.sondreweb.cryptoclicker.game.ClickUpgrade;
import com.sondreweb.cryptoclicker.game.Profile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Ansvar for å starte Click funskjon i Profile, og skille på om vi klikker på Bitcoinen eller holder inne og drar.
 */
public class MinerIntentService extends IntentService{

    private static final String TAG = MinerIntentService.class.getName();
    public static final String MINE = "com.sondreweb.cryptoclicker.Tabs.MINE";
    public static final String HOLD = "com.sondreweb.cryptoclicker.Tabs.HOLD";

    public MinerIntentService() {
        super("MinerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //hvor vi skal click på knappen.
        if(intent != null){
            if(intent.getAction() != null){
            switch (intent.getAction()) {
                case MINE:
                    Profile.CurrentProfil.click();
                    break;
                case HOLD:
                    Profile.CurrentProfil.hold();
                    break;
                }

                ResourceFragment.updateResourceUIWithMainThread();
            }

            /*final String action = intent.getAction();
            if(MINE.equals(action)){//betyr at vi ber intetet om å mine
                Profile.CurrentProfil.click();
                ResourceFragment.updateResourceUIWithMainThread();//siden vi er i en annen tråd må vi oppdatere på denne måten.
            }

            Log.d(TAG,"Action: "+ intent.getAction()); */
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
