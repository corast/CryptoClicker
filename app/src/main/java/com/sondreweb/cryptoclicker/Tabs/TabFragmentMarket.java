package com.sondreweb.cryptoclicker.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.sondreweb.cryptoclicker.Activites.GameActivity;
import com.sondreweb.cryptoclicker.InfoAdapter;
import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.ProfileAdapter;
import com.sondreweb.cryptoclicker.game.Info;
import com.sondreweb.cryptoclicker.game.Profile;

import java.util.ArrayList;

/**
 * Ansvar for å vise en oversikt over hva vi har kjøpt, og hva vi kan kjøpe. Samt gi beskjer om at elementet vi har klikket skal kjøpes viss det går.
 */

public class TabFragmentMarket extends Fragment implements View.OnClickListener {

    //for å hide views(elements) http://stackoverflow.com/questions/5756136/how-to-hide-a-view-programmatically
    //Tenkte den kunne vøre greit å Hide kjøpte ClickUpgrades, men det endte opp med at de var usynlige, men tok fortsatt plassen.

    public static final String TAG ="TabFragmentMarket";

    private ListView listView; //listviewet

    private static InfoAdapter iAdapter;

    private Button clickUpgradeButton;
    private Button upgradeButton;

    public TabFragmentMarket(){
        //tom konstruktør, påkrevd
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_tab_market, container, false);

        listView = (ListView) view.findViewById(R.id.markedListView);

        clickUpgradeButton = (Button) view.findViewById(R.id.clickUpgradeButton);
        clickUpgradeButton.setOnClickListener(this); //onClick til selve fragmentet.
        upgradeButton = (Button) view.findViewById(R.id.upgradeButton);
        upgradeButton.setOnClickListener(this); //onClick til selve fragmentet.

        final Animation animScale = AnimationUtils.loadAnimation(getContext(), R.anim.scale_bitcoin);
        final Animation animNoBuy = AnimationUtils.loadAnimation(getContext(),R.anim.exchange_animation);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if(Profile.CurrentProfil.BuyBig(((Info) parent.getItemAtPosition(position)),getContext())){
                        updateListView(); //forteller at vi har  gjordt foranringer med dataene som adaptere bruker.
                        view.startAnimation(animScale);
                    }else{ //vi fikk ikke kjøpt den.
                        view.startAnimation(animNoBuy);
                    }

                //Buy kan returnere en Bool true viss det gikk ann å kjøpe, og false viss man ikke hadde råd, Trenger da heller ikke å kjøre en oppdater sjekk.
               // iAdapter.notifyDataSetChanged(); //TODO: gjør slik at denne trykkes hvert sekund, viss TabFragmentMarket kjører.
                //updateListView();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //tenket vi kunne kjøpe flere viss vi holdt lengte, men dette blir veldig problematisk å få til, siden prisen forandes ved hvert kjøpt.
            //og derfor ikke hvet nøyaktig hvor mange vi kommer til å få råd til.
            //Det jeg må er å lage en ny funksjon for akkurat dette, som blir seende ganske lik ut som BuyBig, men noen ekstra sjekker på hvor mange vi kommer til å få råd til.
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(Profile.CurrentProfil.BuyBig(((Info) parent.getItemAtPosition(position)),getContext())){
                    view.startAnimation(animScale);
                   return true;
                }else
                    view.startAnimation(animNoBuy);
                    return false;
            }
        });

        buttonChange(upgradeButton); //bytter innhold i listen.

        //TODO: få til å faktis bruke orden
        //Log.d(TAG,"\u0243 +  testing btc symbol");
        return view;
    }


    public static boolean isOpen(){
        //return TabFragmentMarket.instantiate(Runtime)
        if(GameActivity.getTabSeleceted() == 1){  //denne bruker vi i GameActivity når den tar imot Broadcast med verdi fra MinerService
                                                //viss denne er sann kan den også kjøre en oppdate i listen.
            return true;
        }
        return false;
    }

    public static void updateListView(){
        iAdapter.notifyDataSetChanged(); //oppdaterer Listen vi nå ser på, altså arrayet skrives ut på nytt med ArrayAdapteret, med hvor vi stod.
    }

    @Override
    public void onClick(View v) {//får knappene til å se litt bedre ut.

        switch (v.getId()){
            case R.id.upgradeButton:
                //TODO: gjør noe som tyder til at knappen er aktivert.
                buttonChange(upgradeButton);
                break;
            case R.id.clickUpgradeButton:
                //TODO: gjør noe som tyder til at knappen er akativert/deaktivert
                buttonChange(clickUpgradeButton);
                break;
            default:
                Log.d(TAG,"Noe feil her med knappene..");
        }
        //TODO: gjør ting basert på hvilken knapp er klikket.
    }

    //clickUpgradeButton.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY); //rare disse her as
    //upgradeButton.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

    private void buttonChange(Button button){ //får de to knappene til å se sånn tålig ut.

        //todo: hent arrayene fra spilleren sin profil, siden disse er kunn statiske(Defaulte) og forandres ikke i lengden.

        if(button.equals(clickUpgradeButton)){
            //TODO: fix.
            ArrayList<Info> clickUpgradeArray = new ArrayList<Info>(Profile.CurrentProfil.getClickUpgradeList());
            //forsatt en test med denne
            //TODO: hent ut et Array fra databasen, som skal da være tidligere lagrede verdier.

            upgradeButton.setAlpha(1);

            clickUpgradeButton.setClickable(false);
            upgradeButton.setClickable(true);

            createListView(clickUpgradeArray);

        }else{ //button.equals(UpgradeButton)

            ArrayList<Info> upgradeArray = new ArrayList<Info>(Profile.CurrentProfil.getUpgradeList());
            //forsatt en test med denne
            //TODO: hent ut et Array fra databasen, som skal da være tidligere lagrede verdier.
            //dette middeltidlige Arrayet som vi henter ut kan vi da gi til denne.

            clickUpgradeButton.setAlpha(1);

            clickUpgradeButton.setClickable(true);
            upgradeButton.setClickable(false);

            createListView(upgradeArray);
        }

        button.setAlpha(0.400f);//får det til å se litt ut som man clicker på layouten.

    }

    private void createListView(ArrayList<Info> infoArray){//bytter ut hva som står i ListViewet.
            //realistisk sett må vi sende inn Arrayet fra databasen eller fra profilen til brukeren.
        //todo: finn ut om bedre måte å gjøre dette her på.
        iAdapter = new InfoAdapter(getActivity(), infoArray);
        listView.setAdapter(iAdapter);
    }


}
