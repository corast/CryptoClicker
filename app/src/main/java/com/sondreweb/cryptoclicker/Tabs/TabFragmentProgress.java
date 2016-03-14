package com.sondreweb.cryptoclicker.Tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sondreweb.cryptoclicker.R;

/**
 * Created by sondre on 14-Feb-16.
 */
public class TabFragmentProgress extends Fragment {
    public TabFragmentProgress(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_progress, container, false);
    }
}
