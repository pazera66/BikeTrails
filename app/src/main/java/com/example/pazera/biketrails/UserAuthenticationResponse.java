package com.example.pazera.biketrails;

/**
 * Created by pazera on 16-08-2015.
 */
public class UserAuthenticationResponse {

    private String id;
    private String username;
    private int connectionStatus;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(int connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

