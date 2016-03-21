package com.example.pazera.biketrails;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by pazera on 02-08-2015.
 */
public class WelcomeScreenFragment extends Fragment implements View.OnClickListener {

    private Button goToLoginScreenButton, goToRegisterScreenButton, ignoreLoginButton;
    Communicator comm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.welcome_screen_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comm = (Communicator) getActivity();

        goToLoginScreenButton = (Button) getActivity().findViewById(R.id.loginToAccountButton);
        goToLoginScreenButton.setOnClickListener(loginListener);

        goToRegisterScreenButton = (Button) getActivity().findViewById(R.id.goToRegisterScreenButton);
        goToRegisterScreenButton.setOnClickListener(registerListener);

        ignoreLoginButton = (Button) getActivity().findViewById(R.id.ignoreLoginButton);
        ignoreLoginButton.setOnClickListener(ignoreListener);
    }

    // goToLoginScreenButton onClick listener
    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            comm.hideWelcomeScreen();
            comm.setLoginVisibilityFlag();        }
    };

    // goToRegisterScreenButton onClick listener
    View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            comm.hideWelcomeScreen();
            comm.displayRegisterFragment();
        }
    };


    // ignoreLoginButton onClick listener
    View.OnClickListener ignoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            comm.hideWelcomeScreen();
            comm.loginAsGuest();
        }
    };


    @Override
    public void onClick(View v) {

    }
}


