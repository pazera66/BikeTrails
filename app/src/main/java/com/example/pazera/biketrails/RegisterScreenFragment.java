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
public class RegisterScreenFragment extends Fragment implements View.OnClickListener {

    private Communicator comm;
    private Button registerButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_screen_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        comm = (Communicator) getActivity();
        registerButton = (Button) getActivity().findViewById(R.id.registerButton);
        registerButton.setOnClickListener(registerButtonListener);


    }

    View.OnClickListener registerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            comm.registerClick();

        }
    };


    @Override
    public void onClick(View v) {

    }
}
