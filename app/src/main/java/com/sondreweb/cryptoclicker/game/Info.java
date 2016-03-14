package com.sondreweb.cryptoclicker.game;

/**
 * Created by sondre on 02-Mar-16.
 */
public abstract class Info {

    private String title;
    private String description;
    private String srcIcon;

    private double value;
    private double cost;

    public Info(String title,String description, String srcIcon, double value, double cost){
        this.title = title;
        this.description = description;
        this.srcIcon = srcIcon;
        this.value = value;
        this.cost = cost;
    }
    //todo: lage setter viss de trengs til senere tid.
    public String getDescription(){ return description;}
    public String getSrcIcon(){ return srcIcon;}
    public java.lang.String getTitle() {return title;}

    public double getValue(){ return value;}
    public double getCost(){ return cost;}


    //TODO: fiks description i begge klassene osv.

}
