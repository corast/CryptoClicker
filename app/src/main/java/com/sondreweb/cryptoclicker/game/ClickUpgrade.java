package com.sondreweb.cryptoclicker.game;

import java.util.ArrayList;

/**
 * Created by sondre on 02-Mar-16.
 */
public class ClickUpgrade extends Info{

    private ArrayList<ClickUpgrade> clickUpgradeArrayList;

    //TODO: Parcelable

    private boolean bought = false;

    public ClickUpgrade(String title,String name, String srcIcon, double value, double cost) {
        super(title, name, srcIcon, value, cost);
    }

    public boolean isBought(){return bought;}
    public void setBought(){bought = true;}

    public void initaliseUpgrades(){
        // String name, String imgSrc, double value, int cost, boolean bought
        clickUpgradeArrayList = new ArrayList <ClickUpgrade>();
        clickUpgradeArrayList.add(new ClickUpgrade("Upgrade1","Info om oppgraderingen vi har laget", "drawable/circle_star_yellow.png",0.01,20));
        clickUpgradeArrayList.add(new ClickUpgrade("Upgrade2","Info om oppgraderingen vi har laget", "drawable/circle_star_yellow.png",0.01,20));
        clickUpgradeArrayList.add(new ClickUpgrade("Upgrade3","Info om oppgraderingen vi har laget", "drawable/circle_star_yellow.png",0.01,20));

        //trenger flere av disse etterhvert.

        //looper gjennom og legger upgradene inni upgrade arrayet.

        //TODO: lag upgrades som vi legger inn i ArrayListen til spilleren.
    }




    @Override
    public String toString() {
        return "Name: "+this.getTitle()+" Cost: "+this.getCost()+" $ Value:"+this.getValue()+" BTC /n";
    }
}
