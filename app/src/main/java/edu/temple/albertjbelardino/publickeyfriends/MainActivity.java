package edu.temple.albertjbelardino.publickeyfriends;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PartnerListFragment.OnFragmentInteractionListener,
        PartnerMapFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    TextView t;
    FragmentManager fm;
    Location userLocation;
    Partners partners;
    Partners relativePartners;
    String[] userNames;
    ArrayList<MarkerOptions> markerList;
    GoogleMap googleMap;
    GoogleApiClient googleApiClient;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        createGoogleClient();
        createLocationRequest();
        createLocationCallback();

        partners = new Partners();
        relativePartners = new Partners();
        markerList = new ArrayList<>();
        fm = getFragmentManager();
        getUsers();

        int i = 0;
        String s = "";

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //initialize fragment manager


        //beginning of app have user register
        //make sure username is alphanumeric
        //start a new activity with the fragments on success


        //start thread(update every 30 seconds)
        //get current location
        //if currentLocation.distanceTo(realTimeLocation) > 10
        //update users location with push request
        //get JSON array of users -> check
        //populate and sort user list
        //create a marker for each user
        //put markers on Map, titles are usernames


        //fragment landscape and portrait handling
    }

    public void getUsers() {
        JsonArrayRequest req = new JsonArrayRequest(Contract.GET_REQUEST_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            //get user location and username
                            Location myLocation = userLocation;
                            String myUserName = "belardino";
                            Double myLat = myLocation.getLatitude();
                            Double myLng = myLocation.getLongitude();

                            //make the current user Partner
                            Partner me = new Partner(myUserName, myLat, myLng);

                            //create usernames for spinner
                            String[] userNames = new String[response.length()];

                            //create partners array
                            for (int i = 0; i < response.length(); i++) {

                                //object is a person
                                JSONObject person = (JSONObject) response.get(i);

                                //add name to user names array for spinner fragment
                                String name = person.getString("username");
                                userNames[i] = name;

                                String lat = person.getString("latitude");
                                String lng = person.getString("longitude");

                                //add partner to partner array with real locations for map fragment
                                partners.add(new Partner(name, Double.parseDouble(lat), Double.parseDouble(lng)));

                                //get LatLng of current user
                                LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                                //add user to list of Markers
                                markerList.add(new MarkerOptions().position(latLng).title(name));

                                //get Latitude and Longitude relative to my location
                                Double relativeLat = Double.parseDouble(lat) - myLocation.getLatitude();
                                Double relativeLng = Double.parseDouble(lng) - myLocation.getLongitude();

                                //create a list of partners relative to my location
                                relativePartners.add(new Partner(name, relativeLat, relativeLng));
                            }

                            //sort according to distance
                            relativePartners.update();

                            //create an array of usernames based on distance
                            for (int i = 0; i < relativePartners.partnersList.size(); i++)
                                userNames[i] = relativePartners.partnersList.get(i).userName;

                            //change fragments
                            fm
                                    .beginTransaction()
                                    .replace(R.id.ListFragment, PartnerListFragment.newInstance(userNames))
                                    .commit();

                            fm
                                    .beginTransaction()
                                    .replace(R.id.MapFragment, PartnerMapFragment.newInstance(markerList, me))
                                    .commit();

                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

    public void setCurrentLocation(Location location) {
        userLocation = location;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //connect api client
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //disconnect client when application is paused
        if (googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    public void createGoogleClient() {
        //build api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        //location request with high accuracy, can be lower accuracies for less power/resources

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds //min interval of location update
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds   //max interval of location update
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            //if location not retrieved by Google Services API at start of application
                //explicitly state that you want to start receiving updates from it
            if(userLocation == null) {
                LocationServices
                        .getFusedLocationProviderClient(this)
                        .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        }
    }

    public void createLocationCallback() {
        // ?
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                }
            };
        };
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //if new distance is greater than 10 meters away
        if(userLocation.distanceTo(location) > 1) {
            userLocation = location;

            fm.popBackStack();
            fm.popBackStack();


            getUsers();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
