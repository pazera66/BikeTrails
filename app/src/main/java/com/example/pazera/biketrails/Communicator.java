package com.example.pazera.biketrails;

import android.view.View;

/**
 * Created by pazera on 03-08-2015.
 */
public interface Communicator {
    void hideWelcomeScreen();
    void displayRegisterFragment();
    void registerClick();

    void loginAsGuest();

    void setLoginVisibilityFlag();
}

