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
public class MyTrailsFragment extends Fragment {

    public static Polyline line;
    private ListView trailList;
    private Communicator2 comm2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View layout = inflater.inflate(R.layout.my_trails_fragment, container, false);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        comm2 = (Communicator2) getActivity();
        getMyTrailsFromServer();

    }


    private void getMyTrailsFromServer() {
        OkHttpClient client = new OkHttpClient();

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(LoginScreen.ENDPOINT)
                .setClient(new OkClient(client))
                .build();


        NetworkAPI api = adapter.create(NetworkAPI.class);
        trailList = (ListView) getActivity().findViewById(R.id.mytrailsListview);
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


        DataForServer dataForServer = new DataForServer();
        dataForServer.setUserid(comm2.getUserID());

        api.getMyTrails(dataForServer, new Callback<List<Trail>>() {
            @Override
            public void success(final List<Trail> trails, Response response) {
                ListAdapter trailListAdapter = new AdapterMyTrailsList(getActivity(), trails);
                trailList.setAdapter(trailListAdapter);

            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("PAZERA", error.getMessage());
            }
        });
    }
}
