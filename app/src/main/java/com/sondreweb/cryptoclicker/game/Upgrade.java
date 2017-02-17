package com.sondreweb.cryptoclicker.game;

import android.nfc.Tag;
import android.util.Log;

import com.sondreweb.cryptoclicker.R;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Ansvar for å lage Upgrade Objecter, som blir brukt av MinerServicen for å mine raskere.
 */
public class Upgrade extends Info {
    private final static String TAG = "Upgrade";


    private int amount = 0; //default verdi
    private long id; //dette skal være IDen til denne Upgraden.


        //ved henting av Lagrede Upgrades tilhørende profiler.
        // String title, String desc, int imageSrc, double value, double cost, int amount

    //konstruktor på data fra databasen
    public Upgrade(long id,String title, String desc, int imageSrc, double value, double cost, int amount) {
        super(title, desc, imageSrc, value, cost);
        this.amount = amount;
        this.id = id;
    }

    //konstruktor på data fra databasen ny profil.
    public Upgrade(long id,String title, String desc, int imageSrc, double value, double cost){
        super(title,desc,imageSrc,value,cost);
        this.amount = 0;
        this.id = id;
    }

      // String title, String desc, int imageSrc, double value, double cost
    //konstruktor på data til databasen.
    public Upgrade(String title, String desc, int imageSrc, double value, double cost){
        super(title,desc,imageSrc,value,cost);
        this.amount = 0;
    }

    public void setAmount(){
        //forandre Cost kanskje, slik at det blir litt dyrer på kjøpe neste gang?
        adjustPrice();
        amount++;
    }

    public void adjustPrice(){//denne skal øke prisen for hver gang vi kjøper noe.
        this.setCost();
    }

    public double getAccumalatedvalue(){
        return ((Info) this).getValue()*amount;
    }


    public int getAmount() {
        return amount;
    }

    public BigDecimal getBigAcumulatedValue(){
        return new BigDecimal(this.getAccumalatedvalue());
    }

    @Override
    public String toString() {

        return "Name: "+this.getTitle() +" Cost: "+ this.getCost()+"$ Value: "+this.getAccumalatedvalue()+" BTC ";
    }

    public long getId(){
        return this.id;
    }

}
