package com.sondreweb.cryptoclicker.game;

import java.util.ArrayList;

/**
 * Created by sondre on 01-Mar-16.
 */
public class Upgrade extends Info {

    ArrayList<Upgrade> upgradeArrayList;
    //TODO: Parcelable

 //TODO: legge til flere funskjoner.
    //TODO: lage Buy funskjoner.

    private int amount = 0; //default verdi

    private boolean bought = false;

    public Upgrade(String title, String desc, String imageSrc, double value, int cost) {
        super(title, desc, imageSrc, value, cost);
    }

    public void setAmount(){
        amount++;
    }

    public double getAccumalatedvalue(){
        return ((Info) this).getValue()*amount;
    }

    public boolean isBought(){
        return bought;
    }

    public void setIsBought(){
        bought = true;
    }

    public void initializeUpgrades(){
        // String title,String desc, String imgSrc, double value, int cost
        upgradeArrayList = new ArrayList <Upgrade>();
        upgradeArrayList.add(new Upgrade("Upgrade1","Test av første upgrade", "drawable/cpu_1.png",0.01,20));
        upgradeArrayList.add(new Upgrade("Upgrade2","Test av annen upgrade","drawable/cpu_1.png",0.02,30));

        //trenger flere av disse etterhvert.



        //looper gjennom og legger upgradene inni upgrade arrayet.

        //TODO: lag upgrades som vi legger inn i ArrayListen til spilleren.
    }

    @Override
    public String toString() {

        return "Name: "+this.getTitle() +" Cost: "+ this.getCost()+"$ Value: "+this.getValue()+" BTC /n";
    }

    public ArrayList<Upgrade>  getUpgradesArrayList(){
        return upgradeArrayList;
    }
}
