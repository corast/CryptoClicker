package com.sondreweb.cryptoclicker.Tabs;

import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.Activites.GameActivity;
import com.sondreweb.cryptoclicker.ColoredSnackbar;
import com.sondreweb.cryptoclicker.ExchangeAdapter;
import com.sondreweb.cryptoclicker.ExchangeSpinnerAdapter;
import com.sondreweb.cryptoclicker.R;
import com.sondreweb.cryptoclicker.ResourceFragment;
import com.sondreweb.cryptoclicker.game.Profile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Denne tabben har ansvar for å gi brukeren UI for å exchange Bitcoins til USD
 * Hente daglig exchange rate fra nettet på en asynctask og bytte viss vi fikk noe resultat.
 *      Viss vi ikke har nett vår vi bare null.
 *  Veldig mye utviklings potensiale her, prøvde å bruke en Spinner, men ListView var så mye greier å håndtere.
 */

//  http://download.finance.yahoo.com/d/quotes.csv?s=BTCUSD=X&f=a   //Dette henter Exchange rate fra finance.yahoo.
public class TabFragmentExchange extends Fragment {
    private static final String TAG ="TAB_EXCHANGE";

    private static final ArrayList<BigDecimal> BitCoinValuesToExchange = new ArrayList<BigDecimal>();

    private static Spinner spinner;

    private static RelativeLayout relativeLayout;

    private static Handler handler = new Handler();

    private static Context context;

    private static ExchangeAdapter exchangeAdapter;

    private static TextView exchange_rate_textview;

    public static boolean isOpen = false;

    public static int position = -1;

    //forhåpentligviss skjer det ikke noe krøll med linke.
    private String URLRequest = "http://download.finance.yahoo.com/d/quotes.csv?s=BTCUSD=X&f=p";
    //Dette henter Bitcoin Exchange rate fra finance.yahoo, med PengeEnhet s=BTCUSD=X, og f=b betyr å hente bid verdi.

    private ListView listView;

    private Button exchangeAllButton;
    //forløpig exchange rate.
    //viss vi ikke finner en ny exchange forbir dette Exchange raten.

    public TabFragmentExchange(){
        //tom konstruktør, påkrevd
    }
        //TODO: finn en exchange value fra btc til USD.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_exchange,container,false);


        context = getContext();
     //   spinner = (Spinner) view.findViewById(R.id.exchange_spinner);
      //  spinner.setAdapter(exchangeSpinnerAdapter);

        //exchange_button = (ImageButton) view.findViewById(R.id.exchange_imageButton_selected);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.tab_fragment_exchange_relativLayout);
        //relativeLayout = (RelativeLayout) view.findViewById(R.id.exchange_row);

        exchange_rate_textview = (TextView) view.findViewById(R.id.exchange_textView_exchange_rate);

        listView = (ListView) view.findViewById(R.id.exchange_listView);

        exchangeAdapter = new ExchangeAdapter(getContext(),BitCoinValuesToExchange);
        listView.setAdapter(exchangeAdapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {//siden OnTouchEvententen gjelder for ListViewet og Viewet inni ListViewet, kan vi bruke begge to.
                switch (event.getAction()) {
                    //onTouchEvente til ExchangeAdapterer gjelder først, deretter ListView.

                    //måtte hente ut Posisjon, men det gikk dårlig med OnTouchListener, siden den vet kunn hvilket View som ble Touchet, som alltid er selve ListViewet.
                    case MotionEvent.ACTION_DOWN: //med en gang vi rører elementer i ListViewet.
                        if (position != -1) {

                            if (Profile.CurrentProfil.exchangeBitcoinsForUSD((BigDecimal) listView.getItemAtPosition(position))) {
                                floatingText(event.getX() - 90, event.getY(), (BigDecimal)
                                        ((BigDecimal) listView.getItemAtPosition(position)).multiply(Profile.CurrentProfil.getExchangeRateBTC_USD()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                                updateListView();
                            } else {

                            }
                            position = -1;//resetter.
                        }

                        break;
                    case MotionEvent.ACTION_UP: //når vi slipper Layouten

                        break;
                }

                return false;
            }
        });

        //TODO: onclick knapp for hvert exchange element, eller onclick for Layouten.
        return view;
    }

    public static void floatingText(final float x,final float y,BigDecimal addedValue){
        //final variabler som trådene trenger.
        //handler = new Handler();
        //TODO: lage et nytt view eller hva det nå må være får å vise litt tekst.

        final TextView tv = new TextView(context); //denne skal ikke forandres
        tv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        tv.setTextColor(ContextCompat.getColor(context, R.color.greenDollar)); //får se om denne fungerer.
        tv.setShadowLayer(3, 2, 2, R.color.ManitouBlue); //må kanskje forandres litt
        //tv.setHighlightColor(ContextCompat.getColor(getContext(),R.color.white));
        tv.setTextSize(22);
        tv.setText("+ " + addedValue.toString());
        //skulle hatt en layout for hver tråd.

        final float randomVerdi = TabFragmentClick.randomRange(10,60);
        tv.setY(y); //setter det ved y verdien vi nettop har  klikket
        tv.setX(x + randomVerdi); //setter det ved x verdien vi nettop har klikket
        relativeLayout.addView(tv);//letter til Layouten.
        //Log.d(TAG, "RandomVerdi:" + randomRange(0,50));

        //bruke async task istedet? Men den klarer kunn 6 tråder sammtidig. Her kan vi ha så mange ganger brukeren trykker på 3 sekunder.
        handler.postDelayed(new Runnable() {
            float f = 0;//teller for når Tråden skal fjernes.
            float oldx = x+randomVerdi;
            float oldy = y;

            @Override
            public void run() {

                if (f <= 1) { //viss den er null er vi ferdig
                    f = f + 0.02f; // 1/0.02 = 50 ganger før denne er ferdig.
                    tv.setAlpha(1 - f); //setter Apha til hvert View som tråden har, slik at det gradvis blir usynelig.
                    tv.setY(oldy -= 5); //flyter 5 pixler(ps?) oppover hver gang for å lage noe "animasjon".
                    tv.setX(oldx-=1);
                    //2000 ms før ferdig.
                    handler.postDelayed(this, 30); //2+-0.040 sekunder totalt før her tråd forsvinner.
                } else { //tråden er ferdig å kjøre
                    relativeLayout.removeView(tv);//fjerner Layoyte fra Viewet, vill ikke ha 1000en vis med alpha 0..
                    handler.removeCallbacks(this); //fjerner tråden.
                }
            }
        }, 0);
    }

    public static boolean isOpen(){
        //return TabFragmentMarket.instantiate(Runtime)
        if(GameActivity.getTabSeleceted() == 2 && isOpen){  //denne bruker vi i GameActivity når den tar imot Broadcast med verdi fra MinerService
            //viss denne er sann kan den også kjøre en oppdate i listen.
            return true;
        }
        return false;
    }


    //må lage en BigDecimal array som holder det vi kan komme bort i å trenge.
    public static void initalizeBigDecimalValues(BigDecimal stop){ //vi må sende med hvor mye spilleren har av bitcoins, så
        BitCoinValuesToExchange.clear(); //tømmer det slik at vi ikke har mange gamle verdier også.
        BitCoinValuesToExchange.add(new BigDecimal("0.0100"));
                    //når vi har like mye som spilleren selv har stopper vi.
        for(BigDecimal i = new BigDecimal("0.1000"); i.compareTo(stop) < 0 ; i = i.movePointRight(1)){

            BitCoinValuesToExchange.add(i);
        }

    }


    public static void updateListView(){
        initalizeBigDecimalValues(Profile.CurrentProfil.getBigBitcoinCounter());

        //exchangeSpinnerAdapter.notifyDataSetInvalidated();
        //exchangeSpinnerAdapter.notifyDataSetChanged();//når vi forandre Arrayet den ble gitt.

        exchangeAdapter.notifyDataSetChanged();
    }

    class DownloadExchangeFileFromUrl extends AsyncTask<String, String, String> {

        public final String DTAG = DownloadExchangeFileFromUrl.class.getName();

        @Override
        protected void onPreExecute() {
            //denne bruker UI tråden, slik at vi kan vise en eller anne Loading her, og lukke den senere i onPostExecute, viss det skulle være ønskelig.
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... e_url) {//String er exchange raten.
                        //kan få den til å hente fra
            String sResult = null;

            if(checkIfNetwork(getContext())){
                Log.d(TAG, "vi har nett");
                try {
                    URL url = new URL(e_url[0]);
                    Log.d(TAG, "henter info fra nett:");
                    //inputStream tar imot en Stream, som blir returnert med url.openStream().
                    BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()));
                    //Log.d(TAG,"Readline: "+input.readLine());
                    sResult = input.readLine();

                    input.close();
                }catch (MalformedURLException e){
                    Log.e(DTAG, e.getMessage());
                } catch (Exception e) {
                    Log.e(DTAG, e.getMessage());
                }
            }else {
                Log.d(TAG, "vi har ikke nett");
            }

            return sResult;//returneter til onPostExecute metoden, som tar imot parameteren.
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG,"Vi er ferdig med å hente data");
            if(result != null){ //siden sResult ikke er null lenger, må den ha fått noe fra nettet.
                Profile.CurrentProfil.setExchangeRateBTC_USD(result);
                Log.d(TAG, "vi har hentet ny data: " + result);
                BigDecimal bd = new BigDecimal(result);
                exchange_rate_textview.setText(Profile.decimalFormatUSD.format(bd)+" USD to 1 BTC");//oppdatere texten.
            }

            //viss vi vill foreksempel fjerne en Loading bar eller noe slikt, men det går ganske raskt å hente denne en fila fra appen, siden den er veldig lite
            //og TabFragmentExchange blir opprettet før Brukeren besøker tabben, holder å besøke enn av de andre etter oppstart.
            super.onPostExecute(result);
        }
    }//end class DownloadExchangeRate

        //sjekker om vi har nett.
    public boolean checkIfNetwork(Context context){
        Log.d(TAG, "context "+  context);
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = (activNetwork != null && activNetwork.isConnectedOrConnecting());
            return isConnected;
        }catch (NullPointerException e){
            e.printStackTrace();
            return false; //av en eller anne grunn så kan connectivityManger være null, tror det er noe med contexten.
        }
    //Det som er litt rart, er at selv om jeg skrur av Wifi på emulatoren så returnere denne true?
    }

    @Override
    public void onPause() {
        super.onPause();
        isOpen = false;
    }

    @Override
    public void onStart() {
        super.onStart();

        if((Profile.CurrentProfil.getExchangeRateBTC_USD()).compareTo(new BigDecimal(Profile.defaultExchange)) == 0){//denne skal returnere 0 viss de er like.
            //vill si de er like og vi må hente ny exchange.
            Log.d(TAG,"Vi må hente ny verdi fra nett");
            new DownloadExchangeFileFromUrl().execute(URLRequest);
        }

        updateListView();

        exchange_rate_textview.setText(Profile.decimalFormatUSD.format(Profile.CurrentProfil.getExchangeRateBTC_USD()) + " USD to 1 BTC");//oppdatere texten.

        isOpen = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
