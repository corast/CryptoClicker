package com.sondreweb.cryptoclicker;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.sondreweb.cryptoclicker.game.Profile;

/**
 * Created by sondre on 01-Mar-16.
 */
public class NewGameFragment_IKKE_I_BRUK extends Fragment {
//TODO: test om denne i det hele tatt virker.
    private EditText userNameTextField;
    private Spinner speedSpinner;

    private final static String TAG_NEW_GAME = "NEW_GAME";

    public View onCreate(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userNameTextField = (EditText) getView().findViewById(R.id.userName);
        speedSpinner = (Spinner) getView().findViewById(R.id.speedSpinner);

        //lager SpinnerArrayet vårt.
        Spinner spinner = (Spinner) getView().findViewById(R.id.speedSpinner);
        //legger til Arrayet med Strings til Spinneren.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.speedSpinnerOptions,android.R.layout.simple_spinner_item);


        //Velger hvordan vi skal velge Recoursene vi har.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        return inflater.inflate(R.layout.activity_new_game, container, false);


    }
    public void startGameActivity(View view) {
        Intent intent = new Intent(getActivity(),GameActivity.class);
        getActivity().startActivity(intent);
        //Log.d(TAG_NEW_GAME, "StartActivity GameActivity.class");

        //vi kan starte bakgrunnsmineprosessen her.
        if( createProfile()){
            Log.i("CRETE_PROFILE", "Profile created successfully, starting game");
            startActivity(intent);
        }else{
            Log.i("EditText","Tomt felt, trenger flere karakterer");
        }

    }

    private boolean createProfile(){
        String name = userNameTextField.getText().toString();
        if(name.length() == 0 || name.length() < 4){
            return false;
        }else{
            new Profile(name);
            return true;
        }
    }


}
