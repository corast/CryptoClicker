package com.sondreweb.cryptoclicker.game;

import java.util.ArrayList;

/**
 * Created by sondre on 29-Feb-16.
 */
public class Profile {
    //classe for profiler vi har laget
    //tror ikke den trenger å extende noe.
    public static Profile CurrentProfil;

    private ArrayList<Upgrade> UpgradeList;
    //TODO instansier Upgrade objectene vi kan lage

    private ArrayList<ClickUpgrade> ClickUpgradeList;
    //TODO instansier ClickUpgrade objectene vi kan lage



    //tror det er best å ha disse her, istedet for hos et eller annet fragment.
    //men vi må se hvordan det blir med instentServicen
    private int clicks; //teller hvor mange ganger brukere har trykket på coinen, til achievment formål;

    private double bitCoinCounter;
    private double usdCounter;
    private double currentValue;

    private String name;

    public Profile(String name){
        this.name = name;
        CurrentProfil = this;//kunn ved new game dette fungerer.
    }

    public String getName(){
        return name;
    }

    public void buy(Upgrade upgrade){
        //TODO: fylle ut denne til å kjøpe produkter når brukeren trykker tilsvarende knapp
        //kan ta imot flere parametre enn denne, som hvilken upgrade vi kjøper, antall
        UpdateUpgradeList(upgrade);
        //amount må være større enn 0. Alt kunn tillate å kjøpe en av gangen. Mulig mye bedre/letter.
       double upgradeCost = upgrade.getCost();

        if(usdCounter >= upgradeCost){//vi har nok penger.
            //TODO: updateUSDNegativeAmount(Amount); fuksjon som oppdaterer med intentServicen.
            usdCounter = usdCounter-upgradeCost; // mulig dette ikke vill virke
            // pga bakgrunnsprossess som forsatt kanskje miner ting.
            upgrade.setAmount();
            upgrade.setIsBought();

        }else{
            //TODO: gi beskjed om at vi ikke har nok penger
            return;
        }
        //update list etc,
    }

    public synchronized void click(){
        //TODO: trenger en variabel som oppdateres med hva vi har i upgrades som gjelder clickUpgades
        bitCoinCounter=bitCoinCounter+0.001;
        clicks++;
    }

    public void buy(ClickUpgrade clickUpgrade){
        double clickUpgradeCost = clickUpgrade.getCost();

        if(usdCounter >= clickUpgradeCost){

        }else{
            //TODO: gi beskjed om at vi ikke har nok penger.
            return;
        }
    }

    private void UpdateUpgradeList(Upgrade upgrade){
        //update Listen vi har, slik at antall i listen stemmer.
    }

    public ArrayList<Upgrade> getUpgradeList(){
        return UpgradeList;
    }

    public double getBitCoinCounter(){
        return bitCoinCounter;
    }

    public String returnBTCamountAsString(){
        return String.valueOf(String.format("%.4f", bitCoinCounter));
    }

    public String returnUSDamountAsString(){
        return String.valueOf(String.format("%.2f", usdCounter));
    }


    public int getClicks(){
        return clicks;
    }


    //lager objectene vi skal bruke.



    //legger til btcen som er minet fra servicen hit.
    public synchronized void addBTCs(double btc){
        bitCoinCounter += btc;
    }

    public synchronized void setCurrentValue(double value){
        currentValue += value;
    }

    public double getCurrentValue(){
        return currentValue;
    }

}
