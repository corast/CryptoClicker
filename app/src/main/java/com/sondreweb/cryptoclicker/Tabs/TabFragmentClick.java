package com.sondreweb.cryptoclicker.Tabs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.Services.MinerIntentService;
import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.game.Profile;

/**
 * TabFragmentClick har i ansvar for å vise spilleren Bitcoinen vi kan klikke på, starte opp MinerIntentService når dette skjer.
 * samt gi feedback og sende oppdaterings request til Resource fragment.
 * pluss noe matte for visning av floating text og clikking av sirkulært bilde.
 */
public class TabFragmentClick extends Fragment {

    int api_version = Build.VERSION.SDK_INT; //hener API nivå vi sitter på med, slik at vi kan bruke ting som Elivation viss de finnes.
    private static final String TAG =TabFragmentClick.class.getName();

    private Intent clickServiceIntent; //intent for starting av IntentServiceset.




    private static Handler handler = new Handler();

    private TextView floatingTextview;

    private ImageButton coinButton;
    private RelativeLayout Rlayout;

    long timeSinceLastDown;
    long timeSinceLastMove;

    public TabFragmentClick(){
        //tom konstruktør, påkrevd.
    }
        //TODO: Center Coin og legg til noe form for feedback på clicking.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //for å kunne lege til onclicklister til coinButton
        View view = inflater.inflate(R.layout.fragment_tab_click, container, false);//hva skjer viss vi bytter til true?

        //floatingTextview = (TextView) view.findViewById(R.id.floatingTextView);
        Rlayout = (RelativeLayout) view.findViewById(R.id.clickTabRelativeLayout);
        coinButton = (ImageButton) view.findViewById(R.id.bitcoinClicker);
        //Log.d(TAG, "View Width: "+ width + " coinButtonWidth:"+ coinButtonWidth);

        //dette fungere ikke.
      //  coinButton.setLayoutParams(new ActionBar.LayoutParams(view.getWidth(),view.getWidth()));
        final Animation animScale = AnimationUtils.loadAnimation(getContext(), R.anim.scale_bitcoin);

        final Animation animHold = AnimationUtils.loadAnimation(getContext(),R.anim.hold_animation);

        //TODO: dollar stuffs for senere.

        coinButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (clickInCircle(event, v.getWidth() / 2, v.getWidth() / 2, v.getHeight() / 2)) {

                    //todo: ta ett av bildene og lag til en tilsvarende med skygge. Krevde for mye å bruke .setImageResource.
                    if (event.getAction() == MotionEvent.ACTION_UP) {

                            //viss vi gjøre noe annet når knappen slippes.
                        //orginalt forandret jeg Alpha her, men når jeg gikk over til animert knapp så droppet jeg det.
                        //viss jeg skal forandre alpga på noe fremover, er det mye bedre å gjøre det i selve animasjonen.

                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        timeSinceLastDown = System.currentTimeMillis();
                        timeSinceLastMove = System.currentTimeMillis();

                        v.startAnimation(animScale);

                        //((ImageButton ) v).setImageResource(R.drawable.bitcoin1);
                        //Log.d(TAG, "action down");
                        floatingText(event.getX() - 90, event.getY());
                                //-90 fordi vi vill sirka sentrere det.
                        startMinerIntent(MinerIntentService.MINE);
                    } else if(event.getAction() == MotionEvent.ACTION_MOVE){
                        //v.setAlpha(0.7f);
                        if(api_version >= 21){
                           // v.setElevation(2);
                        }
                        //viss tiden sisen sist ACTION_MOVE er under 0.2 sec, så kan vi gi coins
                        timeSinceLastMove =  System.currentTimeMillis();//henter tiden i ns.
                       // Log.d(TAG,"timeSinceWeClicked :"+ (timeSinceLastMove - timeSinceLastDown));
                        if(timeSinceLastMove - timeSinceLastDown > 300){//vill at tiden siden vi flyttet oss skal være 300 ms.
                            timeSinceLastDown = System.currentTimeMillis();//oppdatere tiden for å finne ut sist vi kjørte denne.
                            floatingText(event.getX() - 90, event.getY());
                            v.setAnimation(animHold);
                            startMinerIntent(MinerIntentService.HOLD);
                            //v.setAlpha(1);
                            if(api_version >= 21){
                               // v.setElevation(1);
                            }
                        }
                    }
                }
                return false;
            }
        });

       /* coinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMinerIntent(); //fungere greit for nå.
                //må gjøre slik at vi avhengig av oppgraderinger får et visst antall som blir lagt til en
                // felles variabel mellom IntentServicet og denne.
            }
        }); */

        clickServiceIntent = new Intent(getActivity(),MinerIntentService.class);
        //initializerer intent.

        return view;
    }

    @Override
    public void onStart() {
        //siden vi vill forandre størrelsen på Bitcoinen, men problemet er at det ikke virker helt som jeg ville
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //denne fungere litt dårlig, siden høyden er ikke lik bredden på Fragmentet.
            //må da fjerne bredde på Toolbaren + tabLayouten.
            coinButton.getLayoutParams().width = size.y-140;
        }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            //denne fungere berdre.
            coinButton.getLayoutParams().height = size.x;
        }
        super.onStart();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChange");
        super.onConfigurationChanged(newConfig);
        int orientation =  newConfig.orientation;
        Log.d(TAG, "Orientation: "+ orientation);
        //Log.d(TAG,"Orientation: "+ newConfig.orientation);
    }

        //viser flytende TextView når vi klikker.
    private void floatingText(final float x,final float y){
                    //final variabler som trådene trenger.
        //handler = new Handler();
        //TODO: lage et nytt view eller hva det nå må være får å vise litt tekst.
            //lager et enjelt testView med en relativ layout rundt, kunne vell også vært linear.
       final TextView tv = new TextView(getContext()); //denne skal ikke forandres
        tv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.floatingTextBitcoin)); //får se om denne fungerer.
        tv.setShadowLayer(3, 2, 2, R.color.ManitouBlue); //må kanskje forandres litt
        //tv.setHighlightColor(ContextCompat.getColor(getContext(),R.color.white));
        tv.setTextSize(22); //trengte større tekst.
        tv.setText("+ " + Profile.CurrentProfil.getBigClickerValueAsString());

        final float randomVerdi = randomRange(10,90);//10,60
        tv.setY(y); //setter det ved y verdien vi nettop har  klikket
        tv.setX(x + randomVerdi); //setter det ved x verdien vi nettop har klikket
        Rlayout.addView(tv);//letter til Layouten.
        //Log.d(TAG, "RandomVerdi:" + randomRange(0,50));

        //bruke async task istedet? Men den klarer kunn 6 tråder sammtidig. Her kan vi ha så mange ganger brukeren trykker på 3 sekunder.
        handler.postDelayed(new Runnable() {
            float f = 0;//teller for når Tråden skal fjernes.
            float oldx = x+randomVerdi;
            float oldy = y;

            @Override
            public void run() {

                if (f <= 1) { //viss den er null er vi ferdig
                    if(f==0) {
                       // Log.d(TAG, "Vi setter det første gang oldy: " + oldy + " oldx: " + oldx);
                    }
                    f = f + 0.02f; // 1/0.02 = 50 ganger før denne er ferdig.
                    tv.setAlpha(1 - f); //setter Apha til hvert View som tråden har, slik at det gradvis blir usynelig.
                    tv.setY(oldy -= 5); //flyter 5 pixler(ps?) oppover hver gang for å lage noe "animasjon".
                    tv.setX(oldx-=1);
                                                //2000 ms før ferdig.
                    handler.postDelayed(this, 30); //2+-0.040 sekunder totalt før her tråd forsvinner.
                } else { //tråden er ferdig å kjøre
                    Rlayout.removeView(tv);//fjerner Layoyte fra Viewet, vill ikke ha 1000en vis med alpha 0..
                    handler.removeCallbacks(this); //fjerner tråden.
                }
            }
        }, 0);
    }

    public static float randomRange(float min, float max){//returner en random verdi, slik at vi ikke setter textView på helt sted.
        float fortegn = Math.random() < 0.5 ? -1 : 1;
        float range = (min-max)+1;//+1 så vi ungår 0.
        return (float)((Math.random()*range)+min)*fortegn;
    }
        //hjelp til denne  http://stackoverflow.com/questions/9049868/round-button-in-android-avoid-presses-outside-the-button
    //forbeholdt at ImageButton bildet har minimalt med kanter utenfor sirkelen vi vill trykke på.
    private boolean clickInCircle(MotionEvent e, int radius, int x, int y){
        int dx = ((int) e.getX()) - x;
        int dy = ((int) e.getY()) - y;
        double d = Math.sqrt((dx*dx)+(dy*dy));
        if(d < radius){
            return true;
        }else
            return false;
    }


    private void startMinerIntent(String tag){
        Intent intent = new Intent(this.getContext(),MinerIntentService.class);
        intent.setAction(tag);
        getContext().startService(intent);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().startService(clickServiceIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().stopService(clickServiceIntent);
    }

}
