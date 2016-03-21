package com.example.pazera.biketrails;


import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by pazera on 14-08-2015.
 */
public interface NetworkAPI {

    @GET("/login.php")
    public void getLoginResponse(@Header("Authorization") String authorization, Callback<DataForServer> cb);

    @POST("/insertUser.php")
    public void getRegisterResponse(@Body DataForServer dataForServer, @Header("Authorization") String authorization, Callback<RegisterResponse> rr);

    @POST("/getMyTrails.php")
    public void getMyTrails(@Body DataForServer dataForServer, Callback<List<Trail>> myTrailsCallbackList);

    @POST("/getAllTrails.php")
    public void getAllTrails(@Body DataForServer dataForServer, Callback<List<Trail>> allTrailsCallbackList);

    @POST("/insertTrail.php")
    public void insertTrail(@Body Trail trailToSend, Callback<DataForServer> dataForServer);
}
