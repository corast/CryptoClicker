package com.sondreweb.cryptoclicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.sondreweb.cryptoclicker.game.Profile;

import java.util.ArrayList;

/**
 * Ansvar for å legge til data i hvert element i ListViewet.
 * Samnt sende OnCLicks på knappene tilbake til parent.
 */
public class ProfileAdapter extends ArrayAdapter<Profile>{


    public static final String TAG ="ProfileAdapter";

    private static class ViewHolder {

            TextView name;
            TextView btcAmount;
            TextView usdAmount;
            TextView btcValue;
            TextView btcClickValue;
            TextView clicks;
            TextView dateCreated;
            ImageButton okButton;
            ImageButton deleteButton;

    }

    public ProfileAdapter(Context context, ArrayList<Profile> resource) {
        super(context, R.layout.continue_row_template, resource);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        Profile profile = getItem(position); //henter Layouten til det objecet vi trykket på.

        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.continue_row_template, parent,false);

            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_continue_title);
            viewHolder.btcAmount = (TextView) convertView.findViewById(R.id.tv_continue_btc);
            viewHolder.usdAmount = (TextView) convertView.findViewById(R.id.tv_continue_usd);
            viewHolder.btcValue = (TextView) convertView.findViewById(R.id.tv_continue_btc_s);
            viewHolder.btcClickValue = (TextView) convertView.findViewById(R.id.tv_continue_btc_click);
            viewHolder.clicks = (TextView) convertView.findViewById(R.id.tv_continue_click);
            viewHolder.dateCreated = (TextView) convertView.findViewById(R.id.tv_continue_date_created);

            viewHolder.okButton = (ImageButton) convertView.findViewById(R.id.ib_continue_ok);
            viewHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.ib_continue_delete);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

            //note. AsString() metodene returnere formaterte Stringer med profilen sin formateringer.
        viewHolder.name.setText(profile.getName());
        viewHolder.btcAmount.setText(profile.getBigBTCamountAsString());
        viewHolder.usdAmount.setText(profile.getBigUSDamountAsString());
        viewHolder.btcValue.setText(profile.getBigBTCValue());
        viewHolder.btcClickValue.setText(profile.getBigClickerValueAsString());
        viewHolder.clicks.setText(profile.getClicks()+"");
        viewHolder.dateCreated.setText(profile.getDateCreated());

        //onclickListeneres til knappene som hører til hvert profilView
        viewHolder.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, 0);
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ListView) parent).performItemClick(view, position, 0);
            }
        });

        //TODO: finn ut hvordan vi aktiverer knappene, som tilhører Vievet/profilen.

        return convertView;
    }
}
