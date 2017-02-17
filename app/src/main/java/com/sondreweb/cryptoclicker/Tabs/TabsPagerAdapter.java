package com.sondreweb.cryptoclicker.Tabs;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Denne har ansvar for å bytte tabber og gi oss mulighet for å swipe bytte tab.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "TabsPagerAdapter";

    private int numberOfTabs;


    //konstruktør for å lage tabene.
    public TabsPagerAdapter(FragmentManager fragmentManager, int numberOfTabs){
        super(fragmentManager);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        //switcher mellom hvilken fragment å hoppe til.
        switch (position){
            case 0:
                TabFragmentClick tab1 = new TabFragmentClick();
                return tab1;
            case 1:
                TabFragmentMarket tab2 = new TabFragmentMarket();
                return tab2;
            case 2:
                TabFragmentExchange tab3 = new TabFragmentExchange();
                return tab3;
            case 3:
                TabFragmentProgress tab4 = new TabFragmentProgress();
                return tab4;
            default:
                return null;
        }
        //skulle gjerne gjordt det slik at vi kan flytte oss fra tab 0 til tab 3, og fra tab 3 til 0.
        //ved å dra skjermen over til venstre eller omvent.
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
