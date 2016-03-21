package com.example.pazera.biketrails;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

//import retrofit.client.Response;


public class LoginScreen extends Activity implements Communicator {


    private Button logInButton, goToLoginScreenButton, goToRegisterScreenButton, ignoreLoginButton, registerButton;
    private EditText usernameField, passwordField, registerUsernameField, registerPasswordField, registerRepeatPasswordField, registerEmailAddressField;
    private String hashedPwd;
    public static int NO_OPTIONS = 0, guestID = 99;
    private int regVisibilityFlag = 0, logVisibilityFlag = 0, loginVisibilityFlag = 0, connectionStatus;
    public static final String ENDPOINT = "http://192.168.1.100";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //Initializing all components

        logInButton = (Button) findViewById(R.id.logInButton);
        goToRegisterScreenButton = (Button) findViewById(R.id.goToRegisterScreenButton);
        ignoreLoginButton = (Button) findViewById(R.id.ignoreLoginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        usernameField = (EditText) findViewById(R.id.usernameEditText);
        passwordField = (EditText) findViewById(R.id.passwordEditText);
        registerUsernameField = (EditText) findViewById(R.id.registerUsernameEditText);
        registerPasswordField = (EditText) findViewById(R.id.registerPasswordEditText);
        registerRepeatPasswordField = (EditText) findViewById(R.id.registerRepeatPasswordEditText);
        registerEmailAddressField = (EditText) findViewById(R.id.registerEmailEditText);


        //Display welcome screen fragment
        displayWelcomeScreen();


    }


    public void login(View view) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft1 = fragmentManager.beginTransaction();
        LoggingScreenFragment loggingScreenFragment = new LoggingScreenFragment();
        ft1.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft1.add(R.id.loginscreen_layout, loggingScreenFragment, "loggingScreenFragment").commit();
        logVisibilityFlag = 1;

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();

        String username = usernameField.getText().toString();
        final String password = passwordField.getText().toString();


        String credentials = username + ":" + password;
        //final String credentials = "test:test1";
        String string = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        NetworkAPI api = adapter.create(NetworkAPI.class);
        api.getLoginResponse(string, new Callback<DataForServer>() {

            @Override
            public void success(DataForServer userTest, Response response) {
                Log.d("success", "LoginScreen");
                connectionStatus = userTest.getConnectionStatus();


                switch (connectionStatus) {

                    case 1: {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginScreen.this);
                        dialogBuilder.setMessage(R.string.connectionToDBFailed)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setTitle("Warning");
                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
                        removeLoggingScreenFragment();
                        logVisibilityFlag = 0;
                        break;
                    }

                    case 2: {
                        Toast.makeText(LoginScreen.this, "Welcome " + userTest.getUsername() + "!",
                                Toast.LENGTH_SHORT).show();
                        removeLoggingScreenFragment();
                        logVisibilityFlag = 0;
                        startMainScreen(userTest.getUserid(), userTest.getUsername(), password);
                        usernameField.setText("");
                        passwordField.setText("");
                        break;
                    }

                    case 3: {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginScreen.this);
                        dialogBuilder.setMessage("Wrong username or password")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setTitle("Warning");
                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
                        removeLoggingScreenFragment();
                        logVisibilityFlag = 0;
                        usernameField.setText("");
                        passwordField.setText("");
                        break;
                    }

                }

            }

            @Override
            public void failure(RetrofitError error) {
                String connectionState = "Connection to the server failed";
                Toast.makeText(LoginScreen.this, connectionState, Toast.LENGTH_SHORT).show();
                removeLoggingScreenFragment();
                logVisibilityFlag = 0;
            }

        });
    }


    private void startMainScreen(String id, String username, String p) {
        Intent createMainScreenIntent = new Intent(this, MapActivity.class);
        createMainScreenIntent.putExtra("id", id);
        createMainScreenIntent.putExtra("username", username);
        createMainScreenIntent.putExtra("password", p);
        startActivity(createMainScreenIntent);
    }


    public void showWelcomeScreen() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft1 = fragmentManager.beginTransaction();
        WelcomeScreenFragment welcomeScreenFragment = (WelcomeScreenFragment) fragmentManager.findFragmentByTag("welcomeScreen");
        ft1.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft1.show(welcomeScreenFragment).commit();
    }

    public void displayWelcomeScreen() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft1 = fragmentManager.beginTransaction();
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment();
        ft1.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft1.add(R.id.loginscreen_layout, welcomeScreenFragment, "welcomeScreen").commit();
    }


    @Override
    public void hideWelcomeScreen() {
        FragmentManager fragmentManager = getFragmentManager();
        WelcomeScreenFragment welcomeScreenFragment = (WelcomeScreenFragment) fragmentManager.findFragmentByTag("welcomeScreen");
        FragmentTransaction ft1 = fragmentManager.beginTransaction();
        ft1.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft1.hide(welcomeScreenFragment);
        ft1.commit();
    }

    @Override
    public void displayRegisterFragment() {
        hideWelcomeScreen();
        FragmentManager fragmentManager = getFragmentManager();
        RegisterScreenFragment registerScreenFragment = new RegisterScreenFragment();
        FragmentTransaction ft1 = fragmentManager.beginTransaction();
        ft1.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft1.add(R.id.loginscreen_layout, registerScreenFragment, "registerFragment");
        ft1.commit();
        regVisibilityFlag = 1;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (regVisibilityFlag == 1) {
                removeRegisterScreen();
                showWelcomeScreen();
            }

            if (loginVisibilityFlag == 1 && logVisibilityFlag == 0) {
                showWelcomeScreen();
                loginVisibilityFlag = 0;
            }
            if (logVisibilityFlag == 1) {
                removeLoggingScreenFragment();

            }


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void removeRegisterScreen() {
        FragmentManager fragmentManager = getFragmentManager();
        RegisterScreenFragment registerScreenFragment = (RegisterScreenFragment) fragmentManager.findFragmentByTag("registerFragment");
        FragmentTransaction ft1 = fragmentManager.beginTransaction();
        ft1.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft1.remove(registerScreenFragment).commit();
        regVisibilityFlag = 0;
    }


    private void removeLoggingScreenFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        LoggingScreenFragment loggingScreenFragment = (LoggingScreenFragment) fragmentManager.findFragmentByTag("loggingScreenFragment");
        FragmentTransaction ft2 = fragmentManager.beginTransaction();
        ft2.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft2.remove(loggingScreenFragment).commit();
        logVisibilityFlag = 0;
    }

    @Override
    public void registerClick() {
        registerUsernameField = (EditText) findViewById(R.id.registerUsernameEditText);
        registerPasswordField = (EditText) findViewById(R.id.registerPasswordEditText);
        registerRepeatPasswordField = (EditText) findViewById(R.id.registerRepeatPasswordEditText);
        registerEmailAddressField = (EditText) findViewById(R.id.registerEmailEditText);

        if (registerUsernameField.getText().toString().matches("")) {
            Toast.makeText(this, "Username field is empty", Toast.LENGTH_SHORT).show();
        }

        if (registerEmailAddressField.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Email field is empty", Toast.LENGTH_SHORT).show();
        }

        Boolean param = isEmailValid(registerEmailAddressField.getText().toString());

        if (!param) {
            Toast.makeText(getApplicationContext(), "Incorrect email address", Toast.LENGTH_SHORT).show();
        }

        if (registerPasswordField.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Password field is empty", Toast.LENGTH_SHORT).show();
        }

        if (registerRepeatPasswordField.getText().toString().equals(registerRepeatPasswordField.getText().toString())) {


            OkHttpClient client = new OkHttpClient();

            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(LoginScreen.ENDPOINT)
                    .setClient(new OkClient(client))
                    .build();

            String username = registerUsernameField.getText().toString();
            String password = registerPasswordField.getText().toString();
            String credentials = username + ":" + password;
            String credentialsInBytes = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            DataForServer dataForServer = new DataForServer();
            dataForServer.setEmail(registerEmailAddressField.getText().toString());

            NetworkAPI api = adapter.create(NetworkAPI.class);
            api.getRegisterResponse(dataForServer, credentialsInBytes, new Callback<RegisterResponse>() {

                @Override
                public void success(RegisterResponse registerResponse, Response response) {
                    Log.d("success", "LoginScreen");
                    connectionStatus = registerResponse.getConnectionStatus();


                    switch (connectionStatus) {

                        case 1: {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginScreen.this);
                            dialogBuilder.setMessage(R.string.connectionToDBFailed)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setTitle("Warning");
                            AlertDialog dialog = dialogBuilder.create();
                            dialog.show();
                            break;
                        }

                        case 2: {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginScreen.this);
                            dialogBuilder.setMessage("Account with this username already exists.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setTitle("Warning");
                            AlertDialog dialog = dialogBuilder.create();
                            dialog.show();
                            break;
                        }

                        case 3: {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginScreen.this);
                            dialogBuilder.setMessage("Registration successfull")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setTitle("Warning");
                            AlertDialog dialog = dialogBuilder.create();
                            dialog.show();
                            removeRegisterScreen();
                            showWelcomeScreen();
                            break;
                        }

                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    String connectionState = "Connection to the server failed";
                    Toast.makeText(LoginScreen.this, connectionState, Toast.LENGTH_SHORT).show();
                }

            });




    } else
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void loginAsGuest() {
        startMainScreen("999", "test", "test");
        loginVisibilityFlag = 1;
    }

    @Override
    public void setLoginVisibilityFlag() {
        loginVisibilityFlag = 1;
    }


    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }




}



