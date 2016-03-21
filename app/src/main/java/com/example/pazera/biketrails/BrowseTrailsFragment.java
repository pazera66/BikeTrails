package com.example.pazera.biketrails;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


/**
 * Created by pazera on 26-08-2015.
 */
public class BrowseTrailsFragment extends Fragment {

    public static Polyline line;
    private ListView trailList;
    Button distanceSortButton, timeSortButton, caloriesSortButton;
    DataForServer dataForServer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.browse_trails_fragment, container, false);
        return layout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataForServer = new DataForServer();
        dataForServer.setSortType("routeID");

        getAllTrailsFromServer(dataForServer);

        distanceSortButton = (Button) getActivity().findViewById(R.id.sortDistButton);
        timeSortButton = (Button) getActivity().findViewById(R.id.sortTimeButton);
        caloriesSortButton = (Button) getActivity().findViewById(R.id.sortCaloriesButton);

        distanceSortButton.setOnClickListener(sortDistanceListener);
        timeSortButton.setOnClickListener(sortTimeListener);
        caloriesSortButton.setOnClickListener(sortCaloriesListener);






    }

    View.OnClickListener sortDistanceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (distanceSortButton.getText().equals("Distance \u25bc")) {
                distanceSortButton.setText("Distance \u25b2");
                dataForServer.setSortType("distDESC");
                getAllTrailsFromServer(dataForServer);
            } else {
                distanceSortButton.setText("Distance \u25bc");
                dataForServer.setSortType("distASC");
                getAllTrailsFromServer(dataForServer);
            }
            ;
        }
    };

    View.OnClickListener sortTimeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (timeSortButton.getText().equals("Time \u25bc")) {
                timeSortButton.setText("Time \u25b2");
                dataForServer.setSortType("timeDESC");
                getAllTrailsFromServer(dataForServer);
            } else {
                timeSortButton.setText("Time \u25bc");
                dataForServer.setSortType("timeASC");
                getAllTrailsFromServer(dataForServer);
            }
            ;
        }
    };

    View.OnClickListener sortCaloriesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (caloriesSortButton.getText().equals("Calories \u25bc")) {
                caloriesSortButton.setText("Calories \u25b2");
                dataForServer.setSortType("caloriesDESC");
                getAllTrailsFromServer(dataForServer);
            } else {
                caloriesSortButton.setText("Calories \u25bc");
                dataForServer.setSortType("caloriesASC");
                getAllTrailsFromServer(dataForServer);
            }
            ;
        }
    };


    private void getAllTrailsFromServer(DataForServer dataForServer) {
        OkHttpClient client = new OkHttpClient();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(LoginScreen.ENDPOINT)
                .setClient(new OkClient(client))
                .build();


        NetworkAPI api = adapter.create(NetworkAPI.class);
        trailList = (ListView) getActivity().findViewById(R.id.browseTrailsListview);
        trailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (line != null) {
                    line.remove();
                }
                Trail trail = (Trail) trailList.getAdapter().getItem(position);
                PolylineOptions lineDrawer = new PolylineOptions().width(5).color(Color.BLUE);
                for (int i = 0; i < trail.getListSize(); i++) {
                    lineDrawer.add(new LatLng(trail.getLatitude(i), trail.getLongtitude(i)));
                }
                line = MapActivity.map.addPolyline(lineDrawer);
                MapActivity.FOLLOWINGFLAG = true;
            }
        });



        api.getAllTrails(dataForServer, new Callback<List<Trail>>() {
            @Override
            public void success(final List<Trail> trails, Response response) {
                ListAdapter trailListAdapter = new AdapterAllTrailsList(getActivity(), trails);
                trailList.setAdapter(trailListAdapter);

            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("PAZERA", error.getMessage());
            }
        });


    }
}
