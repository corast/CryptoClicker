package com.sondreweb.cryptoclicker;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.Tabs.TabsPagerAdapter;

/**
 * Created by sondre on 13-Feb-16.
 */
public class GameActivity extends AppCompatActivity {

    //dette er activiten som alltid ligger i bakgrunn når vi har fragmentene oppe.

    private final static String TAG = "GameActivity";
    private String[] tabs = {"Click","Market","Exchange","Progress"};

    private ImageButton bitcoinClicker;

    private TextView bitcoinAmount,usdAmount;

    private void initalizeVars(){
        bitcoinClicker = (ImageButton) findViewById(R.id.bitcoinClicker);
        bitcoinAmount = (TextView) findViewById(R.id.btcAmountTextView);
        usdAmount = (TextView) findViewById(R.id.usdAmountTextView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_game);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Game");

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        for(String tabName : tabs){ //legger til Tabs
            tabLayout.addTab(tabLayout.newTab().setText(tabName));
        }


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //mulig må gjøre noe her..
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        //Intent minerIntent = new Intent(this, MinerServiceTest_IKKE_I_BRUK.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //legger til menu_toolbar layouten for knappene
        getMenuInflater().inflate(R.menu.menu_toolbar_game, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void loggingTesting(){
        Log.d(TAG, "GameActivity kjører..");
    }

    //Game game = Game.getGame();

    @Override
    public Context getBaseContext() {
        return super.getBaseContext();
    }

}
