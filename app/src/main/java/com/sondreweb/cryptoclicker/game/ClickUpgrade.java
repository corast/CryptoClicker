package com.sondreweb.cryptoclicker.game;

import android.util.Log;

import com.sondreweb.cryptoclicker.R;

import java.util.ArrayList;

/**
 * Ansvar for å lage ClickUpgrade objecer som profilen skal bruke for å klikke med mer verdi.
 */
public class ClickUpgrade extends Info{

    private static final String TAG = ClickUpgrade.class.getName();


    private boolean bought; //oversikt om dette object er kjøpt enda eller ikke.

    private long id; //dette skal være IDen til denne ClickUpgraden.

            //title, description, srcIcon, value, cost, bought
    //kunstruktro til databasen
    public ClickUpgrade(String title,String description, int srcIcon, double value, double cost){
        super(title, description, srcIcon, value, cost);
        this.bought = false;
        //ID vill være null, men den vill få en ID fra databasen når vi henter ut senere.
    }

    //kunstruktro fra databasen ny profil.
    public ClickUpgrade(long id,String title,String description, int srcIcon, double value, double cost){
        super(title, description, srcIcon, value, cost);
        this.bought = false;
        this.id = id;
    }

    //konstructor fra Databasen
    public ClickUpgrade(long id,String title,String description, int srcIcon, double value, double cost, int bought) {
        super(title, description, srcIcon, value, cost);
        this.bought = getBoughtBool(bought); //slik at vi kan hente alt fra databasen
        this.id = id; //setter IDen.
    }

    public boolean isBought(){return bought;}

    public void setBought(){bought = true;}

    public boolean getBoughtBool(int b){//når vi har int men trenger bool
        if(b == 1){
            return true;
        }else if(b == 0){
            return false;
        }else{
            Log.d(TAG, "Error, vi får noe annet en 1 og 0 fra databasen.");
            return false;
        }
    }

    public long getId(){
        return this.id;
    }

    public int getBoughtInt(){ //når vi har bool men trenger int.
        return (bought) ? 1 : 0; //true: return 1 ,else return 0
    }


    @Override
    public String toString() {
        return "Name: "+this.getTitle()+" Cost: "+this.getCost()+" $ Value:"+this.getValue()+" BTC /n";
    }
}
