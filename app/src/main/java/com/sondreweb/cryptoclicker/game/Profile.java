package com.sondreweb.cryptoclicker.game;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sondreweb.cryptoclicker.Activites.GameActivity;
import com.sondreweb.cryptoclicker.ResourceFragment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *  Profil classen har ansvar for en ganske stor del av spillet.
 *  Håndtere looping gjennom Array som inneholder ClickUpgrades og Upgrades for å regne ut nye verdier.
 *  Holde oversikt over spill verdiene.
 *  Holde ArrayListene til Profilen.
 *  Formatere verdiene ved utskrift.
 *
 *
 *
 */
public class Profile implements Comparable<Profile> {
    //http://stackoverflow.com/questions/5200081/more-precise-numeric-datatype-than-double

    long databaseId = -1; //bare en default verdi.

    public static final String TAG ="Profile";

    public static final String defaultExchange = "420.9160";  //forløpig exchange rate.
    //viss vi ikke finner en ny exchange forbir dette Exchange raten.

    public static BigDecimal exchangeRateBTC_USD = new BigDecimal(defaultExchange);

    public static Profile CurrentProfil;//holder på hvilken profil vi bruker nå.

    private static ArrayList<Profile> profileArrayList; //her skal vi legge data om alle profiler, som har lagret i databasen.

    private ArrayList<Info> UpgradeList; //holder listen med Upgrads vi bruker for denne profilen

    private ArrayList<Info> ClickUpgradeList;//holder listen med ClickUpgrades vi bruker for denne profilen.

    private int clicks; //teller hvor mange ganger brukere har trykket på coinen, til achievment formål;

    private String dateCreated; //holder på når denne profilen ble laget.

    public static String defaultClickValue = "0.0010";

    private BigDecimal bigClickerValue = new BigDecimal(defaultClickValue);//Dette skal være default verdien vi klikker med.
                                                    //gjør nødvendige forandringer med funksjonere nede.
    private BigDecimal bigBTCValue; //Holder på hvor mye BTC vi miner pr sec, som vi sender med broadcast ved forandring.


    private BigDecimal bigUSDCounter; //Holder på hvor mye USD denne profilen har.
    private BigDecimal bigBTCCounter; //Holder på hvor mye BTC denne profilen har

    private BigDecimal bigTotUSDCounter;
    private BigDecimal bigTotBTCCounter;


    private String name; //navnet på Profilen

    public static Handler cHandler = new Handler();         //handler for bruk av tråder til å loope igjennom Array for å finne ut ny Value og CLickAmount.

    static DecimalFormatSymbols symbols = new DecimalFormatSymbols();

    public static DecimalFormat decimalFormatBTC = new DecimalFormat("###,##0.0000"); //vise 5 tall hele tiden, med mellomrom mellom hver 1000'ener
    public static DecimalFormat decimalFormatUSD = new DecimalFormat("###,##0.00"); //vise 3 tall hele tiden med mellomrom mellom hver 1000'ener
    public static DecimalFormat largeFormat = new DecimalFormat("###,##0"); //viser ikke decimaler.


    //mulig denne ikke fungere viss vi må bytte fra double til BigDecimal senere.
    //alternativt kan vi ha en int og bare late som om vi har 10000 mindre å rutte med, men det blir køddent.

    static boolean notInitalized = true;

    public static void initalizeFormating(){ //eneste jeg kom på for å legge til custom symbol seperator.
        if(notInitalized){
            symbols.setGroupingSeparator(' ');
            decimalFormatBTC.setDecimalFormatSymbols(symbols); //legger til Symbolet vi lagde.
            decimalFormatUSD.setDecimalFormatSymbols(symbols);
            largeFormat.setDecimalFormatSymbols(symbols);
            notInitalized = false;
        }
    }

    /*##########################################*/
    /*          Profile Constructors            */

    public Profile(String name){//for når vi lager en profil som vi lagrer i databasen ved new game.
        CurrentProfil = this;

        this.name = name;
        bigUSDCounter = new BigDecimal("0.0000");
        bigBTCCounter = new BigDecimal("0.0000");

        bigBTCValue = new BigDecimal("0.0000");

        bigClickerValue = new BigDecimal(defaultClickValue);
        bigTotUSDCounter = new BigDecimal("0.0000");
        bigTotBTCCounter = new BigDecimal("0.0000");

        this.clicks = 0;


        initalizeFormating();//litt rart å ha den her, men siden ikke-static og det der, eneste som fungere.
    }

    //til å hente profiler fra databasen        //fant ut at det var lettere å hente ArrayListene hver for seg og bruke Set metode.
    public Profile(long id,String dateCreated, String name, String btcAmount, String usdAmount, String totUSDamount, String totBTCamount, int clicks, String clickValue, String btcValue, ArrayList<Info> UpgradeList, ArrayList<Info> ClickUpgradeList){
        CurrentProfil = this; //hvordan skal jeg løse dette?

        this.databaseId = id; //slik at vi kan sende denne til databasen.

        this.dateCreated = dateCreated;
        this.name = name;
        this.bigBTCCounter = new BigDecimal(btcAmount);
        this.bigUSDCounter = new BigDecimal(usdAmount);
        this.bigTotBTCCounter = new BigDecimal(totBTCamount);
        this.bigTotUSDCounter = new BigDecimal(totUSDamount);
        this.clicks = clicks;
        this.bigBTCValue = new BigDecimal(btcValue); //denne kan regnes ut ifra Arrayet med Upgrades.
        this.bigClickerValue = new BigDecimal(clickValue);//denne skal inneholde defaulten.

        UpgradeList = this.UpgradeList; //henter listen med Upgrades.
        ClickUpgradeList = this.ClickUpgradeList; //henter listen med ClickUpgrades.
        initalizeFormating();
    }

        //brukdere denne for å skrive ut profiler i en ArrayList fra databasen.
    public Profile(long id,String dateCreated, String name, String btcAmount, String usdAmount, String totUSDamount, String totBTCamount, int clicks, String clickValue, String btcValue){

        CurrentProfil = this;

        this.databaseId = id;

        this.dateCreated = dateCreated;
        this.name = name;
        this.bigBTCCounter = new BigDecimal(btcAmount);
        this.bigUSDCounter = new BigDecimal(usdAmount);
        this.bigTotBTCCounter = new BigDecimal(totBTCamount);
        this.bigTotUSDCounter = new BigDecimal(totUSDamount);
        this.clicks = clicks;
        this.bigClickerValue = new BigDecimal(clickValue);
        this.bigBTCValue = new BigDecimal(btcValue); //denne kan regnes ut ifra Arrayet med Upgrades.

        initalizeFormating();
    }

    /*                           End Profile Constructors                                   */




    //tror det er bedre å gjøre en slik sjekk for hver gang vi kjøper noe, fremfor å bare legge til Valuen på det vi kjøper, og sende det.
    //For noen tilfeller, kan det kanskje gi oss problemer. Dette er mer tidkrevende, men kanskje lett nok uansett.

    public void updateClickerAmount(final ArrayList<Info> clickUpgradeArrayList){ //krever samme Array som vi sender med iAdapter i TabFragmentMarket.
        //cHandler = new Handler();

        cHandler.post(new Runnable() {
            @Override
            public void run() {
                final long start = System.nanoTime(); //henter tiden i ns.
                final List<Info> ListOfInfos = Collections.synchronizedList(new ArrayList<Info>(clickUpgradeArrayList));

                BigDecimal decimalTestValue = new BigDecimal(defaultClickValue); //tar med oss default verdi.
                //denne tråden skal gå igjennom ArrayListen vi har over med en itterator og hente verdiene.

                synchronized (ListOfInfos){ //se nærmere på hvordan denne fungerer.

                    Iterator<Info> it = ListOfInfos.iterator();
                    while(it.hasNext()){ //går gjennom hvert object i listen.
                        ClickUpgrade clickUpgrade = ((ClickUpgrade) it.next());
                        if(clickUpgrade.isBought()) {//slik at vi kunn går igjennom de som ikke er kjøpt.
                            decimalTestValue = decimalTestValue.add(clickUpgrade.getBigValue());
                        }
                    }

                    //sjekker om vi har fått mer verd på klikkingen
                    if(decimalTestValue.compareTo(Profile.CurrentProfil.getClickerValueAsBTC()) >= 0){
                        decimalTestValue = decimalTestValue.setScale(4, RoundingMode.HALF_EVEN);//denne tror jeg skal rounde.
                        Profile.CurrentProfil.setClickerValue(decimalTestValue);
                    } //else verdien vi klikker med øker ikke.

                    cHandler.removeCallbacks(this);//fjerner denne tråden fra melding køen?
                }
            }
        });

    } //end updateClickerAmount

    public void updateminerValue(final ArrayList<Info> upgradeArrayList, final Context context){

        //cHandler = new Handler();

        cHandler.post(new Runnable() {

            final List<Info> ListOfInfos = Collections.synchronizedList(new ArrayList<Info>(upgradeArrayList));
            final long start = System.nanoTime(); //henter tiden å i nanoSec.

            @Override
            public void run() {

                BigDecimal decimalTestValue = new BigDecimal("0"); //BD vi bruker for å holde på verdiene mideltidlig.
                //denne tråden skal gå igjennom ArrayListen vi har over med en itterator og hente verdiene.

                synchronized (ListOfInfos){ //kjør koden synkront

                    Iterator<Info> it = ListOfInfos.iterator();

                    while(it.hasNext()){ //går gjennom hvert object i listen.
                        Upgrade upgrade = ((Upgrade) it.next());
                        if(upgrade.getAmount() != 0) { //amount er hvor mange vi har kjøpt, 0 ved ingen.
                            //henter acumulated value.
                            decimalTestValue = decimalTestValue.add(upgrade.getBigAcumulatedValue());

                        }
                    }

                    //sjekk om vi miner mer.
                    if(decimalTestValue.compareTo(Profile.CurrentProfil.getCurrentBigValueBD()) > 0){ //returnere -1 viss første er større enn andre verdi.

                        decimalTestValue = decimalTestValue.setScale(4, RoundingMode.HALF_EVEN);//runder ned tallet
                        //slik at det ikke er eksempelvis 45.2212351512512521 men heller 45.2212
                        Profile.CurrentProfil.setbigCurrentValue(decimalTestValue);
                        GameActivity.sendValueChangeToService(context);
                    } //else verdien vi klikker med øker ikke.

                    cHandler.removeCallbacks(this); //dunno om denne gjør det jeg vill.
                }
            }
        });
    }


    public boolean BuyBig(Info infoObject, Context context){
        //double cost = infoObject.getCost();//går greit å ha kostnaden som Double, men ikke summen av costnadene.
        BigDecimal bigCost = infoObject.getBigCost(); //henter BigDecimal costnad, slik at vi kan sammenligne med det vi har liggende.


        if(infoObject instanceof ClickUpgrade){ //betyr vi må sjekke om vi har nok penger
            if(bigUSDCounter.compareTo(bigCost) >= 0 && !((ClickUpgrade) infoObject).isBought()){ //denne skal koste dollar, men dette har vi ikke noe av forløpig.
                ((ClickUpgrade) infoObject).setBought(); //setter Boolen om den er kjøpt eller ikke til True.
                        //men det er selvfølgelig noen ting man ikke kan kjøpe igjenn.
                subTractBigUSD(bigCost); //oppdatere det vi har av USD.

                updateClickerAmount(Profile.CurrentProfil.getClickUpgradeList());

                ResourceFragment.updateResourceUIWithMainThread(); //oppdatere etter kjøp.
                return true;
            }else{
                //Vi har allerede kjøpt dette CLickUpgrade Objectet
                //TODO: forandre layouten i InfoAdapter på de som allerede er kjøpt. legge til et icon eller forandre bakgrunnsfarge?
                //beste hadde kanskje vært om de ikke ble vist i det hele tatt, men det er kjipt i lengden.
                return false;
            }

        }else{ //instanceof Upgrade
            if(bigBTCCounter.compareTo(bigCost) >= 0){ //må sjekke om kostnaden er mindre eller lik det vi har.
                //compareTo returnere -1 viss bigBTCCounter er mindre enn cost og 0 viss lik.
            //henter verdien , vi må ha mer eller like mye som den koster

                bigBTCCounter = bigBTCCounter.subtract(bigCost); //fjerner verdien av hva vi nettop kjøpte.
                ((Upgrade)infoObject).setAmount(); //legger til 1 på denne Upgrade.

                ResourceFragment.updateResourceUIWithMainThread(); //oppdaterer UI, siden vi har gjort forandringer på antall ressurser, ved bruk av ressurser på å kjøpe.
                //oppdaterer hvor mye vi får i sekundet.

                updateminerValue(Profile.CurrentProfil.getUpgradeList(), context); //looper igjennon arrayet med en tråd.


               // updateCurrentBigValue(context); //looper igjennom Arrayet vi har , og sender en broadcast til MinerService viss den ikke er lik.
                return true;                  //burde aldri være lik.

            }else{
                //vi har ikke råd.
                return false;
            }
        }

    }


    public synchronized void click(){ //click metode som kjører addBigBTC metoden.

        addBigBTC(bigClickerValue); //legger til det vi får pr click.

        clicks++;//legger til ett klikk antall.
    }

    public synchronized void hold(){
        addBigBTC(bigClickerValue);
    }


    public boolean exchangeBitcoinsForUSD(BigDecimal bigDecimal){
        if(bigDecimal.compareTo(this.getBigBitcoinCounter()) <= 0){//viss exchange valuer er større, eller lik enn det vi har.
            this.subtractBigBTC(bigDecimal);
            this.addBigUSD(bigDecimal, this.getExchangeRateBTC_USD());
            ResourceFragment.updateResourceUIAsMainThread();
            return true;
        }
        return false;
    }


    /*                  Settere og Gettere nedover                       */


    /*####################################################################*/
    /*                  bigUSDcounter funksjoner/metoder                */

    public BigDecimal getUSDCounter(){return bigUSDCounter;}

    public String getBigUSDamountAsString(){

        return decimalFormatUSD.format(bigUSDCounter);
    }


    public void addBigUSD(String usd){ //veldig sjelden vi ikke opererer med BigDecimal.
        bigUSDCounter = bigUSDCounter.add(new BigDecimal(usd));
    }

    public void addBigUSD(BigDecimal usd,BigDecimal exchangeRateBTC_USD){
        bigUSDCounter = bigUSDCounter.add(usd.multiply(exchangeRateBTC_USD));
        addBigTotUSDCounter(usd.multiply(exchangeRateBTC_USD));
    }

    public void subTractBigUSD(BigDecimal usd){
        if(bigUSDCounter.compareTo(usd) >= 0){ //må sjekke om ikke usd er større enn bigUSDcounter, kan ikke være -1 i dette tilfelle.

            bigUSDCounter = bigUSDCounter.subtract(usd);
            ResourceFragment.updateResourceUIWithMainThread(); //kan sende at vi burde oppdatere UI.
        }
    }


    /*#######################################################################*/
    /*               bigBTCCounter funksjoner/metoder                  */

    public synchronized  void addBigBTC(String btc){
        bigBTCCounter = bigBTCCounter.add(new BigDecimal(btc)); //tror dette skal funger, men får se.
        bigTotBTCCounter = bigTotBTCCounter.add(new BigDecimal(btc));
    }

    public synchronized void addBigBTC(BigDecimal btc){

        bigBTCCounter = bigBTCCounter.add(btc);
        bigTotBTCCounter = bigTotBTCCounter.add(btc);
    }

    public synchronized BigDecimal getBigBitcoinCounter(){
        return bigBTCCounter;
    }

    public synchronized void subtractBigBTC(BigDecimal btc){
        bigBTCCounter = bigBTCCounter.subtract(btc);
    }

    public String getBigBTCamountAsString(){ //denne lager error?
        return decimalFormatBTC.format(bigBTCCounter); //dunno hvordan denne fungerer.
        //return String.valueOf(decimalFormatBTC.format(bigBTCCounter)); //får håpe dette fungerere.
    }


    /*###########################################################*/
    /*              bigBTCValue funksjoner/metoder          */

    public String getBigBTCValue(){ //litt ubrukelig, men bra for å sende til videre til servicen.

        return this.bigBTCValue.toString(); //Denne skal returnere BigDecimalen vi har for Value pr sec som String til broadcast, for sending til service.
    }

    private void setbigCurrentValue(BigDecimal btc){ //denne skal bytte på verdien av bigBTCValue, viss motatte verdi er større enn tidligere verdi.
            bigBTCValue = btc;  //kan være problematisk viss vi sender inn feil verdi.
    }

    public String getBigBTCValueAsString(){
        return decimalFormatBTC.format(bigBTCValue);
    }

    /**
     * @return
     */
    private synchronized BigDecimal getCurrentBigValueBD(){
        return Profile.CurrentProfil.bigBTCValue;
    }
    /*              end bigBTCValue             */

    /*###################################################*/
    /*              bigTotBTCCounter metoder            */
    public BigDecimal getBigTotBTCCounter() {
        return bigTotBTCCounter;
    }

    public void setBigTotBTCCounter(BigDecimal bigTotBTCCounter) {
        this.bigTotBTCCounter = bigTotBTCCounter;
    }

    public String getBigTotBTCcounterAsString(){
        return decimalFormatBTC.format(bigTotBTCCounter);
    }

    /*####################################################*/
    /*              bigTotUSDCounter metoder*/
    public BigDecimal getBigTotUSDCounter() {
        return bigTotUSDCounter;
    }

    public void setBigTotUSDCounter(BigDecimal bigTotUSDCounter) {
        this.bigTotUSDCounter = bigTotUSDCounter;
    }
    public String getBigTotUSDCounterAsString(){
        return decimalFormatUSD.format(bigTotUSDCounter);
    }

    public void addBigTotUSDCounter(BigDecimal bc){
       bigTotUSDCounter = bigTotUSDCounter.add(bc);
    }

    /*          end bigTotBTCcounter            */

    /*############################################################*/
    /*                       BigClickerValue metoder               */

    public double getBigClickedValueAsDouble(){
        return bigClickerValue.doubleValue();
    }

    public String getBigClickerValueAsString(){
        return decimalFormatBTC.format(bigClickerValue);
    }

    public BigDecimal getClickerValueAsBTC(){
        return this.bigClickerValue;
    }

    public synchronized void setClickerValue(BigDecimal bigDecimal){
        bigClickerValue = bigDecimal;
    }

    /*              end BigClickerValue */

    /*#######################################################*/
    /*                  databaseId metoder                  */
    public long getDatabaseId(){
        return databaseId;
    }

    public void setDatabaseId(long id){
        this.databaseId = id;
    }
    /*              end databaseId            */


    public int getClicks(){
        return clicks;
    } //til senere bruk.

    public String getName(){
        return name;
    }

    public synchronized String getDateCreated(){
        return dateCreated;
    }

    public BigDecimal getExchangeRateBTC_USD(){
        return exchangeRateBTC_USD;
    }

    public void setExchangeRateBTC_USD(String exchangeRateBTC_usd){
        exchangeRateBTC_USD = new BigDecimal(exchangeRateBTC_usd);
    }


    @Override
    public String toString() { //kunn for litt testing av loggen.
        return "Navm: "+name + " Clicks: "+clicks+" BTCAmount: "+getBigBTCValue();
    }

    public void setCurrentProfil(Profile profil,long id){
        Profile.CurrentProfil = profil;
        this.databaseId = id;
        //kan vi state servicen her ifra? Hadde gjordt ting litt lettere.
        //kan fjerne denne, sier heller at GameActivity kan starte denne selv, da har vi antatt at vi har vår profil.
       // GameActivity.startMinerService();
    }


    /*      Set/get for ArrayListene      */

    public ArrayList<Info> getClickUpgradeList() {
        return ClickUpgradeList;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ClickUpgrade> getClickUpgradeListAsClickUpgrade(){

        ArrayList<Info> infoArrayList = getClickUpgradeList();
        for(Info info : infoArrayList){
            if(info instanceof Upgrade){
                Log.e(TAG, "getClickUpgradeList inneholder Upgrades");
            }
        }

        return (ArrayList<ClickUpgrade>) (ArrayList<?>) infoArrayList;

    }

    public ArrayList<Info> getUpgradeList(){
        return UpgradeList;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Upgrade> getUpgradeListAsUpgrade(){
        //Mulig dette er litt trengt og litt dårlig kode.
        //men det er bedre å gjøre en test for mye her, enn å få masse advarsler inni SQLiteHelper som bruker dette.
        ArrayList<Info> infoArraList = getUpgradeList();
        for(Info info : infoArraList){//sjekker om denne inneholder ClickUpgrades
            if(info instanceof ClickUpgrade){
                Log.e(TAG, "getUpgradeList inneholder ClickUpgrades");
            }
        }

        return (ArrayList<Upgrade>) (ArrayList<?>) infoArraList;
    }

    public void setClickUpgradeList(ArrayList<Info> clickUpgradeList){
        this.ClickUpgradeList = clickUpgradeList;
    }

    public void setUpgradeList(ArrayList<Info> upgradeList){
        this.UpgradeList = upgradeList;
    }


    @NonNull
    @Nullable
    @Override
    public int compareTo(Profile another) { //skal sorter med høyste verdi først.
        if(another != null) {
            return (-1) * this.getBigBitcoinCounter().compareTo(another.getBigBitcoinCounter());
        }else{
            return 0;//viss vi sammeligner med 0 bør vi ikke flytte oss.
        }
    }
}
