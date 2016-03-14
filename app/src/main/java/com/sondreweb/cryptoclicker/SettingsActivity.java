package com.sondreweb.cryptoclicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;


/*
*   Settings brukeren kan trenge skal legges her.
*   Språk (NOK,ENG) etc
*   Tilgang til nett
*   Strømsparing?
*   Animasjoner på/av
*
*   Trenger ikke en menu her i første omgang
*
*   Når vi starter spillet kan vi skru av/på ting utifra hva som er valg inni her.
*       slik som start screen
*       Tutorial
*       Animasjoner(når de forhåpentligvis kommer)
*       etc.
*
* */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //spinner for language
        Spinner spinner = (Spinner) findViewById(R.id.languageSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.optionsMenuLanguage,android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //checkboxene må alle ha onclick listenerers
        final CheckedTextView ctv = (CheckedTextView)findViewById(R.id.checkedTestViewTesting);
        //gjør slik at checkboxen togles checked eller not checked.
        ctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ctv.isChecked())
                    ctv.setChecked(false);
                else
                    ctv.setChecked(true);
            }});
        //ctv.isChecked() gir oss boolean på om den er valgt eller ikke.
    }

}
