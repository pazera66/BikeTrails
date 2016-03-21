package com.example.pazera.biketrails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.OkHttpClient;

import android.os.Handler;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.Endpoint;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by pazera on 17-08-2015.
 */
public class MapActivity extends FragmentActivity implements
        Communicator2,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static GoogleMap map;
    private MapFragment mapFragment;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 5; //15 seconds  interval
    private static final long FASTEST_INTERVAL = 1000 * 4;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    PolylineOptions lineDrawer;
    private String username, password, userID, trailname;
    private Trail trail = new Trail();
    private int DIALOGFLAG = 0, seconds = 0, minutes = 0, hours = 0, meters = 0,
            mseconds = 0;
    private float zoomLevel = 0, avgSpeed = 0;
    boolean pauseFlag = false, trackingFlag = false, cameraFlag = true;
    public static boolean FOLLOWINGFLAG = false;
    Communicator2 comm2;
    EditText trailNameEditText;
    View view;
    AlertDialog dialog;

    private Handler handler;
    private boolean Running = true;
    private Thread timerThread;
    private String timeTemplate;

    private Button pauseButton, startStopButton, gotoMyTrailsButton, browseToMyTrailsButton;
    private TextView timeView, caloriesView, distanceView, speedView;

    private Polyline line;
    private double currentLatitude = 0;
    private double currentLongtitude = 0;

    private final double METeq = 0.0175, weight = 100;
    private double MET = 0, calories = 0, km = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        setContentView(R.layout.map_activity);

        initializeGoogleApiClient();

        createLocationRequest();

        initializeLayoutComponents();

        username = getIntent().getExtras().getString("username");
        password = getIntent().getExtras().getString("password");
        userID = getIntent().getExtras().getString("id");




        if (username.equals("test") && userID.equals("999")) {
            gotoMyTrailsButton = (Button) findViewById(R.id.gotoMytrails);
            gotoMyTrailsButton.setText(R.string.unavailable);
            gotoMyTrailsButton.setTextColor(Color.parseColor("#777777"));
            gotoMyTrailsButton.setEnabled(false);
        }

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);


    }


    private void initializeLayoutComponents() {
        pauseButton = (Button) findViewById(R.id.pauseButton);
        startStopButton = (Button) findViewById(R.id.startStopButton);
        timeView = (TextView) findViewById(R.id.time);
        caloriesView = (TextView) findViewById(R.id.calories);
        speedView = (TextView) findViewById(R.id.speed);
        distanceView = (TextView) findViewById(R.id.distance);
    }


    private void handleNewLocation(Location currentLocation) {
        Log.d(TAG, currentLocation.toString());
        currentLatitude = currentLocation.getLatitude();
        currentLongtitude = currentLocation.getLongitude();
        zoomLevel = map.getCameraPosition().zoom;

        if (cameraFlag) {
            animateCamera(15);
            cameraFlag = false;
        }

        if (trackingFlag) {
            animateCamera(zoomLevel);
            trail.setLatitude(currentLatitude);
            trail.setLongtitude(currentLongtitude);
            if (line != null) {
                line.remove();
                if (!FOLLOWINGFLAG) {
                    line.remove();
                }
            }
            lineDrawer = new PolylineOptions().width(5).color(Color.BLUE);
            for (int i = 0; i < trail.getListSize(); i++) {
                lineDrawer.add(new LatLng(trail.getLatitude(i), trail.getLongtitude(i)));
            }
            if (!FOLLOWINGFLAG) {
                line = map.addPolyline(lineDrawer);
            }
            if (trail.getListSize() > 1) {

                Location location1 = new Location("location1");
                location1.setLatitude(trail.getLatitude(trail.getListSize() - 2));
                location1.setLongitude(trail.getLongtitude(trail.getListSize() - 2));

                Location location2 = new Location("location2");
                location2.setLatitude(trail.getLatitude(trail.getListSize() - 1));
                location2.setLongitude(trail.getLongtitude(trail.getListSize() - 1));

                meters = meters + Math.round(location1.distanceTo(location2));
                km = meters * 0.001;
                if (km > 0.000) {
                    distanceView.setText(getString(R.string.distanceSpace) + String.format("%.2f", (float) km) + " km");
                    avgSpeed = (float) ((meters / seconds) * (3.6));
                    speedView.setText(getString(R.string.SpeedSpace) + String.format("%.1f", avgSpeed) + " km/h");
                    caloriesView.setText(getString(R.string.caloriesSpace) + String.format("%.0f", (float) calories));

                }

            }

        }


    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    private void animateCamera(float i) {
        LatLng latlng = new LatLng(currentLatitude, currentLongtitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, i);//10
        map.animateCamera(cameraUpdate);
    }

    private void stopTracking() {
        if (guestCheck() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
            builder.setMessage("Do you want to save your trail?")
                    .setTitle("Session end");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    startStopButton.setText("Start");
                    trackingFlag = false;
                    askAboutName();

                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    trail = new Trail();
                    trackingFlag = false;
                    startStopButton.setText("Start");
                    resetMeters();
                    //line.remove();
                    if (MyTrailsFragment.line != null) {
                        MyTrailsFragment.line.remove();
                    }
                    if (BrowseTrailsFragment.line != null) {
                        BrowseTrailsFragment.line.remove();
                    }
                }
            });

            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    trackingFlag = true;
                    Running = true;
                    startCounter();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private String askAboutName() {
        LayoutInflater inflater = MapActivity.this.getLayoutInflater();
        view = inflater.inflate(R.layout.name_dialog_layout, null);




        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setView(view);
        trailNameEditText = (EditText) view.findViewById(R.id.nameDialogEditText);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                trailname = trailNameEditText.getText().toString();
                sendTrailToServer(trailname);

            }
        });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        if (DIALOGFLAG == 0) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            DIALOGFLAG++;
        }

        trailNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (start == 0) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    //Toast.makeText(MapActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                //Toast.makeText(MapActivity.this, "cccccccccccc", Toast.LENGTH_SHORT).show();
                //dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                if (s.equals("")) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                }

            }
        });

        return null;
    }

    private void resetMeters() {
        timeView.setText("Time 00:00:00");
        distanceView.setText("Dist.   0.00 km");
        speedView.setText("Speed 0.00 km/h ");
        caloriesView.setText("Calories 0");
        seconds = 0;
        meters = 0;
        avgSpeed = 0;
        calories = 0;
        FOLLOWINGFLAG = false;
        if (line != null) {
            line.remove();
        }
        trail = new Trail();
        lineDrawer = new PolylineOptions();

    }

    private void sendTrailToServer(String name) {
        trail.setUser_ID(Integer.parseInt(userID));
        trail.setName(name);
        trail.setDistance(km);
        trail.setTime(seconds);
        trail.setCalories(calories);
        trail.setAvgSpeed(avgSpeed);

        OkHttpClient client = new OkHttpClient();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(LoginScreen.ENDPOINT)
                .setClient(new OkClient(client))
                .build();

        NetworkAPI api = adapter.create(NetworkAPI.class);
        api.insertTrail(trail, new Callback<DataForServer>() {
            @Override
            public void success(DataForServer confirmation, Response response) {
                Toast.makeText(MapActivity.this, confirmation.getConfirmation(), Toast.LENGTH_SHORT).show();
                resetMeters();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MapActivity.this, "Something went wrong with trail upload", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void startStopSession(View view) {
        if (startStopButton.getText().toString().equals("Start")) {
            startStopButton.setText("Stop");
            trackingFlag = true;
            Running = true;
            startCounter();
        } else {
            stopTracking();
            Running = false;

        }


    }

    public void pauseSession(View view) {
        if (trackingFlag) {
            pauseButton.setText("Unpause");
            trackingFlag = false;
            Running = false;
        } else if (startStopButton.getText().toString() == "Stop") {
            trackingFlag = true;
            pauseButton.setText(R.string.pause);
            Running = true;
            startCounter();
        }

    }

    private void startCounter() {
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (Running) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            seconds++;
                            minutes = seconds / 60;
                            hours = minutes / 60;
                            mseconds = seconds % 60;
                            timeTemplate = "Time: " + String.format("%02d", hours) + ":"
                                    + String.format("%02d", minutes) + ":" + String.format("%02d", mseconds);
                            timeView.setText(timeTemplate);
                            if (meters > 0) {
                                MET = 20 * avgSpeed - 252;
                                if (MET <0) {MET = 0;}
                                calories = ((METeq * MET * weight) / 60) * seconds;

                            }
                        }
                    });
                }
            }
        };

        new Thread(runnable).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected");
        currentLocation = LocationServices.FusedLocationApi.getLastLocation((googleApiClient));
        if (currentLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        }

    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, getString(R.string.locationServicesSuspended));

    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void initializeGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void gotoMyTrails(View view) {
        MyTrailsFragment myTrailsFragment = new MyTrailsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.map_activity_layout, myTrailsFragment, "mytrailsfragment")
                .addToBackStack("")
                .commit();

    }


    public void gotoBrowseTrails(View view) {
        BrowseTrailsFragment browseTrailsFragment = new BrowseTrailsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.map_activity_layout, browseTrailsFragment, "browsetrailsfragment")
                .addToBackStack("")
                .commit();
      //  BrowseTrailsFragment tempFragment = (BrowseTrailsFragment) getSupportFragmentManager().findFragmentByTag("browsetrailsfragment");
      //  tempFragment.disableGoToMyTrailsButton();
    }

    public int guestCheck() {
        int isThisGuest = 0;
        if (username.equals("test") && userID.equals("999")) {
            isThisGuest = 1;
            return isThisGuest;
        }
        return isThisGuest;
    }

    @Override
    public String getUserID() {
        return userID;
    }

}

