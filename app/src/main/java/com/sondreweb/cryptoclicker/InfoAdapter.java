package com.sondreweb.cryptoclicker;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.game.ClickUpgrade;
import com.sondreweb.cryptoclicker.game.Info;
import com.sondreweb.cryptoclicker.game.Profile;
import com.sondreweb.cryptoclicker.game.Upgrade;

import java.util.ArrayList;

/**
 * Ansvar for å legge til data i hvert element i ListViewet, basert på hvilket object vi vill vise.
 */

// https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView#defining-the-adapter
public class InfoAdapter extends ArrayAdapter<Info>{
        //TODO: finn ut om vi trenger en ArrayAdapter til Upgrade og ClickUpgrade.

    public static final String TAG ="InfoAdapter";

    //klasse som gjør slik at vi legger disse feltene i cache, og ting går sirka 15% raskere.
    private static class ViewHolder{
        ImageView upgradeImage;
        TextView title;
        TextView upgradeDesc;
        TextView price;
        TextView valueUpgrade;
        TextView amountUpgrade;
        TextView textAmount;
        RelativeLayout rLayout;
    }

    public InfoAdapter(Context context, ArrayList<Info> resource) {
        super(context,R.layout.row_layout_market_upgrades,resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Info info = getItem(position); //henter Layouten til det objecet vi trykket på.

        ViewHolder viewHolder; //dette er Vievet vi ser på nå.

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());// alltid usikker på hvilken context å putte inn...
            convertView = inflater.inflate(R.layout.row_layout_market_upgrades,parent,false);

            //Initialiserer feltene i Layouten for hvert Info object.
            viewHolder.upgradeImage = (ImageView) convertView.findViewById(R.id.upgradeImage);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.upgradeDesc = (TextView) convertView.findViewById(R.id.upgradeDesc);
            viewHolder.price = (TextView) convertView.findViewById(R.id.price);
            viewHolder.valueUpgrade = (TextView) convertView.findViewById(R.id.valueUpgrade);

            viewHolder.amountUpgrade = (TextView) convertView.findViewById(R.id.amountUpgrade);
            viewHolder.textAmount = (TextView) convertView.findViewById(R.id.textAmount);

            viewHolder.rLayout = (RelativeLayout) convertView.findViewById(R.id.MarketInfoLayout);


            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /*todo: se om vi kan gjøre dette bedre for der det ikke skal være antall, men kunn om den allered er kjøpt
           fjerne om ((CLickUpgrade)info).isBought() == true;  dvs at den ikke kan kjøpes igjenn, og trenger da heller ikke være i listen.
        */

        viewHolder.upgradeImage.setImageResource(info.getSrcIcon()); //henter bilde Iden
        viewHolder.title.setText(info.getTitle());
        viewHolder.upgradeDesc.setText(info.getDescription());
        viewHolder.rLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.ManitouBlue));
        //TODO: legg til et ekstra tekstfelt som forteller hvor mye vi får pr sec av denne type oppgradering.
        //eg gange Amount*Value, viss 0, Hide feltet, veldig enkel opperasjon. Må legge til i Layouten først.


    //mindre intensiv metode å sjekke om dette Viewet ikke koster for mye?
        if(info instanceof Upgrade){

            //må sjekke om Prisen på det vi skal kjøpe er mer enn det vi har.
            //sjekker om costnaden er større enn hva vi har.
            //Log.d(TAG,info.getBigCost().compareTo(Profile.CurrentProfil.getBigBitcoinCounter())+ " Hva denne returnere.");

            /*Log.d(TAG, Profile.CurrentProfil.getBigBitcoinCounter().compareTo(info.getBigCost()) + " btc.cpr(cost) "+ info.getTitle()+" "+
                    Boolean.toString(Profile.CurrentProfil.getBigBitcoinCounter().compareTo(info.getBigCost()) < 0));*/

            if(Profile.CurrentProfil.getBigBitcoinCounter().compareTo(info.getBigCost()) < 0){  //cost skal være mindre, compareTo skal returnere 1 eller 0
                //x.compareTo(y) returnere -1 viss x<y, 0 viss x==y, og 1 viss x> y
                convertView.setAlpha(0.5f);
            }else{ //returnere -1 viss bigCitCoinCounter >= cost.
                convertView.setAlpha(1);
            }
            //TODO: sjekke om vi har råd, med hva vi har og sette en alpga på de viss vi ikke har råd.
            viewHolder.price.setText(info.getCostAsString()+" btc");//fiks dette.
            //TODO: få til å vise bitcoin symbolet: Ƀ  \u0243 (ingen ting fungerer)
            //viewHolder.price.setText(Html.fromHtml(info.getCost()+"&#579;"));
            viewHolder.valueUpgrade.setText(info.getValueAsAString() + " btc/s"); //TODO: formater disse slik at vi vår riktig antall nuller.
            viewHolder.amountUpgrade.setText(((Upgrade)info).getAmount()+"");

        }else{ //instance ClickUpgrade

            if(Profile.CurrentProfil.getUSDCounter().compareTo(info.getBigCost()) < 0){ //viss vi ikke har råd?
                convertView.setAlpha(0.5f);
            }else{
                convertView.setAlpha(1);
            }
            viewHolder.price.setText(info.getCostAsString() + " $");

            viewHolder.valueUpgrade.setText(info.getValue() + " BTC/click");

            viewHolder.textAmount.setVisibility(View.GONE); //fjerner feltet og plassen det tar
            viewHolder.amountUpgrade.setVisibility(View.GONE);
            if(((ClickUpgrade)info).isBought()){ //denne returnere false om de ikke er kjøpt
                //myView.setVisibility((myData == null) ? View.GONE : View.VISIBLE);
                viewHolder.rLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.darkRed));
                viewHolder.rLayout.setAlpha(0.2f); //vill sette de som allerede er kjøpt til å være ende mer transparent
                //convertView.setVisibility(View.GONE); //fjerner dette contetViewet.
                //convertView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.darkRed));
                //convertView.setAlpha(0.2f);
            //TODO: bedre feedback på at vi ikke kan kjøpe dette igjenn.
            }else{
                viewHolder.rLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.teal));
            }
            //viewHolder.rLayout.setBack

            //TODO: gjør at ting ser litt bedre ut viss det er en ClickUpgrade fremfor en Upgrade.
        }

        //TODO: finn ut hva vi gjør med Amount som vi ikke har i ClickUpgrade, eventuelt tillate å kjøpe flere ganger.

        return convertView;
    }
}
