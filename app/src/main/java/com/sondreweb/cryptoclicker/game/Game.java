package com.sondreweb.cryptoclicker.game;

import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by sondre on 08-Mar-16.
 */
public class Game{
    private static Game game = null;

    public static Game getGame() {
        if(game == null){
            game = new Game();
        }
        return game;
    }

    public Game (){
        super();
    }


    private String TAG = "Game Class";



    private Profile currentProfile; //profilen vi bruker nå.

    private ArrayList<Upgrade> UpgradeList;
    //TODO instansier Upgrade objectene vi kan lage

    private ArrayList<ClickUpgrade> ClickUpgradeList;
    //TODO instansier ClickUpgrade objectene vi kan lage

    private void click(){

        Profile.CurrentProfil.click();
    }

    //game funskjoner kan vi legge her.

    public static Intent minerIntent;

    public static void StoreMinerService(Intent intent){
        //dette skal i prinsippet starte MinerServiceTest_IKKE_I_BRUK
        minerIntent = intent;
    }

    public static Intent getMinerIntent(){
        return minerIntent;
    }


}
