package com.sondreweb.cryptoclicker;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.game.Profile;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
/*  Ansvar for å legge til data i hvert element i ListViewet for Exchange rate.

       Fungerte ikke som jeg ville han den til
 */
public class ExchangeSpinnerAdapter extends BaseAdapter implements SpinnerAdapter{
    public static final String TAG = ExchangeSpinnerAdapter.class.getName();

    private Activity activity;//holder på aktiviteten
    private ArrayList<BigDecimal> b_list;

    public ExchangeSpinnerAdapter(Activity activity, ArrayList<BigDecimal> b_list){
        this.b_list = b_list;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return b_list.size();
    }

    @Override
    public BigDecimal getItem(int position) {
        return b_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolder {
        TextView btc_amount;
        TextView usd_amount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      //  BigDecimal bigDecimal_ourValue = getItem(position);
        // Profile profile = getItem(position); //henter Layouten til det objecet vi trykket på.
       // BigDecimal bigDecimal_ourValue = getItem(position);
        //Log.d(TAG, bigDecimal_ourValue.toString());

        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.exchange_row_template, parent,false);

            viewHolder.btc_amount = (TextView) convertView.findViewById(R.id.exchange_textView_bitcoin);
            viewHolder.usd_amount = (TextView) convertView.findViewById(R.id.exchange_textView_dollar);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BigDecimal btc_usd_exchangeRate = Profile.CurrentProfil.getExchangeRateBTC_USD();
        BigDecimal dollar_recieved = btc_usd_exchangeRate.multiply(b_list.get(position));

        viewHolder.btc_amount.setText(Profile.decimalFormatBTC.format(b_list.get(position)));
        viewHolder.usd_amount.setText(Profile.decimalFormatUSD.format(dollar_recieved));

        //tenkte jeg skulle legge til et ekstra element i ListViewet, som skulle være Exchange all, men var egentlig ikke vits.
        if(b_list.get(position).compareTo(new BigDecimal("-1")) == 0){//betyr av dette er Exchange all knappen.
            //betyr at vi har valg 0.
        }

        return convertView;
    }
}
