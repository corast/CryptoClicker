package com.sondreweb.cryptoclicker;

import android.os.Handler;
import android.os.Message;

/**
 * Created by sondre on 09-Mar-16.
 */
public class MessageHandler extends Handler {

    static final int BTC_COLLECTED = 1;
    static final int VALUE_CHANGED = 2;
    //public String BTC_COLLECTED = "btcCollectedFromBroadcast";
    //public String VALUE_CHANGED = "valueBtcChanged";

    @Override
    public void handleMessage(Message msg) {
        switch(msg.what){
            case BTC_COLLECTED:
                //gjor noe annet
                break;
            case VALUE_CHANGED:
                //gjør noe annent
                break;
        }

    }

}
