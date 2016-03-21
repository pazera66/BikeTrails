package com.example.pazera.biketrails;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by pazera on 12-08-2015.
 */
public class Network extends AsyncTask<String, Context, String> {

    private Context mContext;
    String response;

    @Override
    protected String doInBackground(String... params) {
        OpenHttpGETConnection(params[0]);
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
        //LoginScreen.responseStr = s;

    }

    public Network(Activity context){
        mContext = context;

    }


    public void OpenHttpGETConnection(String address) {
        try{
            URL url = null;
            try {
                url = new URL(address);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            response = readStream(in);
            urlConnection.disconnect();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String readStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }


}

