package com.sondreweb.cryptoclicker.game;

import android.content.Context;
import android.util.Log;

import com.sondreweb.cryptoclicker.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;

/**
 *  Info er klasses som ClickUpgrade og Upgrade klassen extender fra, slik at vi enkelt kan hente ut felles data mellom de fra denne.
 *  Ansvarlig for å ha get-ere som formatere det de returner.
 */


public class Info{

    private static final String TAG = Info.class.getName();

    private String title;
    private String description;

    private int srcIcon;
    private double value;
    private double cost;

    public Info(String title,String description, int srcIcon, double value, double cost){
        this.title = title;
        this.description = description;
        this.srcIcon = srcIcon;
        this.value = value;
        this.cost = cost;
    }

    public String getDescription(){ return description;}

    public int getSrcIcon(){ return srcIcon;}

    public java.lang.String getTitle() {return title;}

    public double getValue(){ return value;}
    public String getValueAsAString(){
        if(value >= 10000000){
            return String.valueOf(Profile.largeFormat.format(value));
        }else
        return String.valueOf(Profile.decimalFormatUSD.format(value));
    }
    //return String.valueOf(decimalFormatBTC.format(bitCoinCounter));
    public double getCost(){ return cost;}

    public BigDecimal getBigCost(){

        BigDecimal bigDecimal = new BigDecimal(cost).setScale(4, RoundingMode.HALF_EVEN);
        //Log.d("Tag","BigDecila getBitCost:" + bigDecimal );
        return bigDecimal; //0.00009 -> 0.0001, 0.0002212312312 -> 0.0002
    }

    public String getCostAsString(){ //dette står i Price feltet, og formaterse basert på størrelse.

            if(cost >= 1000000){ //viss vi er oppe i millioner
                return String.valueOf(Profile.largeFormat.format(cost));
            }else
            return String.valueOf(Profile.decimalFormatBTC.format(cost)); //får se hvordan dette blir.

    }

    public BigDecimal getBigValue(){
        return new BigDecimal(this.value);
    }

    //øker hva et produkt koster å kjøpe.
    public void setCost(){
        this.cost = 1.05*cost; //cost skal øke med 25 for hver gang vi kjøper noe.
    }

    @Override
    public String toString() {return title+" "+ value + "\n";}

    //TODO: fiks description i begge klassene osv.

}
