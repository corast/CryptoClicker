package com.sondreweb.cryptoclicker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.Tabs.TabFragmentExchange;
import com.sondreweb.cryptoclicker.game.Profile;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

/**
 * Har ansvar for å gi brukeren mulighet til å exchange Bitcoins for Dollar, som blir brukt til å kjøpe bedre ClickUpgrades.
 */
public class ExchangeAdapter extends ArrayAdapter<BigDecimal>{
    public static final String TAG =ExchangeAdapter.class.getName();


    final Animation animExchange = AnimationUtils.loadAnimation(getContext(), R.anim.scale_bitcoin);

    private static class ViewHolder {
        TextView btc_amount;
        TextView usd_amount;
    }

    public ExchangeAdapter(Context context, ArrayList<BigDecimal> resource) {
        super(context, R.layout.exchange_row_template, resource);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
       // Profile profile = getItem(position); //henter Layouten til det objecet vi trykket på.
       final BigDecimal bigDecimal_ourValue = getItem(position);
        //Log.d(TAG, bigDecimal_ourValue.toString());

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.exchange_row_template, parent,false);

            viewHolder.btc_amount = (TextView) convertView.findViewById(R.id.exchange_textView_bitcoin);
            viewHolder.usd_amount = (TextView) convertView.findViewById(R.id.exchange_textView_dollar);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        BigDecimal btc_usd_exchangeRate = Profile.CurrentProfil.getExchangeRateBTC_USD();
        BigDecimal dollar_recieved = btc_usd_exchangeRate.multiply(bigDecimal_ourValue);

        viewHolder.btc_amount.setText(Profile.decimalFormatBTC.format(bigDecimal_ourValue));
        viewHolder.usd_amount.setText(Profile.decimalFormatUSD.format(dollar_recieved));

        if(bigDecimal_ourValue.compareTo(new BigDecimal("-1")) == 0){//betyr av dette er Exchange all knappen.
            //betyr at vi har valg 0.
        }

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TabFragmentExchange.position = position; //gir posisjon til TabFragmentExchange.
                v.startAnimation(animExchange);
                return false;
            }
        });

        //TODO: finn ut hvordan vi aktiverer knappene, som tilhører Vievet/profilen.

        return convertView;
    }

}
