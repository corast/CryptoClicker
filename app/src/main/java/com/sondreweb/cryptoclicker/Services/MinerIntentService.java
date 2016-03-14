package com.sondreweb.cryptoclicker.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.sondreweb.cryptoclicker.ResourceFragment;
import com.sondreweb.cryptoclicker.game.Profile;

/**
 * Created by sondre on 07-Mar-16.
 */
public class MinerIntentService extends IntentService{

    private static final String MINE = "com.sondreweb.cryptoclicker.Tabs.MINE";

    private static final String  PRODUCE= "com.sondreweb.cryptoclicker.Tabs.PRODUCE";

    public MinerIntentService() {
        super("MinerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //hvor vi skal click på knappen.
        //todo: calculate amount to get when clicking
        //todo: click.
        if(intent != null){
            final String action = intent.getAction();
            if(MINE.equals(action)){//betyr at vi ber intetet om å mine
                Log.d("MinerIntentService", "Click");
                Profile.CurrentProfil.click();
                //update ui skal kjøre etter dette...?
                //ResourceFragment.updateResourceUITest();
            }
        }
    }

    @Override
    public void onDestroy() {
        //sende data back?
        super.onDestroy();

    }

}
