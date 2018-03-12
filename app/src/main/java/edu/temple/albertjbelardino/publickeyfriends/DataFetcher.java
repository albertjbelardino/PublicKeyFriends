package edu.temple.albertjbelardino.publickeyfriends;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by albertjbelardino on 3/4/2018.
 */



public class DataFetcher {
    Partners partners;
    Context callingContext;

    public DataFetcher(Context callingContext) {
        this.callingContext = callingContext;
        partners = new Partners();
    }

    /*
    //wait 15 seconds to do something
    Runnable r = new Runnable() {
        @Override
        public void run() {
            for (boolean val = true; val; ) {
                URL currentPriceURL;

                try {
                    currentPriceURL = new URL("http://api.coindesk.com/v1/bpi/currentprice.json");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    currentPriceURL.openStream()));

                    String response = "", tmpResponse;

                    tmpResponse = reader.readLine();
                    while (tmpResponse != null) {
                        response = response + tmpResponse;
                        tmpResponse = reader.readLine();
                    }

                    JSONObject bitcoinPriceObject;

                    try {
                        bitcoinPriceObject = new JSONObject(response);
                        Message msg = Message.obtain();
                        msg.obj = bitcoinPriceObject;
                        currentPriceHandler.sendMessage(msg);
                        bitcoinPriceObject = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Thread t = new Thread(r);
    //t.start();

    Handler currentPriceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            JSONObject responseObject = (JSONObject) msg.obj;

            /*
            try {

                currentPriceTV.setText( "$ " + (responseObject
                        .getJSONObject("bpi")
                        .getJSONObject("USD")
                        .getDouble("rate")));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return false;
        }
    });
    */

    public Partners getUsers(final Partners p) {
        JsonArrayRequest req = new JsonArrayRequest(Contract.GET_REQUEST_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String s = "";

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject person = (JSONObject) response.get(i);

                                String name = person.getString("username");
                                String lat = person.getString("latitude");
                                String lng = person.getString("longitude");

                                s += name + " " + lat + " " + "\n";

                                p.add(new Partner(name, Double.parseDouble(lat), Double.parseDouble(lng)));

                                //partners.add(new Partner(name, Double.parseDouble(lat), Double.parseDouble(lng)));


                            }

                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(callingContext);
        requestQueue.add(req);

        return p;
    }
}
