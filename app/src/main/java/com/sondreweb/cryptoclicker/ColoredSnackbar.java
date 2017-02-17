package com.sondreweb.cryptoclicker;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Denne klassen har ansvar for å fargelegge Snackbarer basert på hvilken beskjed vi skal gi til brukeren.
 */

// kodeEksempel som jeg tok dette fra: http://www.technotalkative.com/part-3-styling-snackbar/
public class ColoredSnackbar {

    //noen farger som vi kan bruke for å sette bakgrunns farte på Snackbarer.
    private static final int red = 0xfff44336;
    private static final int green = 0xff7FFF00;
    private static final int light_blue = 0xDD00cdcd;
    private static final int orange = 0xffffc107;

    private static View getSnackBarLayour(Snackbar snackbar){
        if(snackbar != null){
            return snackbar.getView();
        }
        return null;
    }

    public static Snackbar colorSnackBar(Snackbar snackbar, int colorId){
        View snackbarView = getSnackBarLayour(snackbar);
        if(snackbarView != null){
            snackbarView.setBackgroundColor(colorId);
        }
        return snackbar;
    }


    //basert på hva vi vill få fram til brukeren kan vi bruke forskjellig farge.
    public static Snackbar alert(Snackbar snackbar){
        return colorSnackBar(snackbar, red);
    }

    public static Snackbar warn(Snackbar snackbar){
        return colorSnackBar(snackbar, orange);
    }
    public static Snackbar welcome(Snackbar snackbar){
        return colorSnackBar(snackbar, light_blue);
    }

}
