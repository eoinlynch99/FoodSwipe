package com.fyp.foodswipe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // initializing variables
    Location deviceLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    int DEFAULT_ZOOM = 15;
    private static final int REQUEST_CODE = 101;
    private Marker[] restaurantMarkers;
    private MarkerOptions[] restaurants;
    private GoogleMap gMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // initializes fusedLocationProviderClient to be used to get users location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        int MAX_PLACES = 20;
        restaurantMarkers = new Marker[MAX_PLACES];
        getLocation();
    } // end onCreate


    // checking if user has granted location permissions and then getting the users location
    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        } // end if

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        // end onSuccess
        task.addOnSuccessListener(location -> {
            if(location != null) {
                deviceLocation = location;
                Toast.makeText(getApplicationContext(), deviceLocation.getLatitude() + "" + deviceLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(MapsActivity.this);
            } // end if
        }); // end task.addOnSuccessListener
    } // end getLocation


    // class that is called when map is initialised
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        double lat = deviceLocation.getLatitude();
        double lng = deviceLocation.getLongitude();

        LatLng latLng = new LatLng(lat, lng);

        // adding a blue coloured marker for the user's location
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // moves the map camera to the user's location
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        // places API http query
        String restaurantsSearchURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+lat+","+lng+
                "&radius=5000&sensor=true" +
                "&types=restaurant"+
                "&key=AIzaSyB55zVwYmfsg_5776MOyUU3-XOiGMAH4zc";

        // running the query
        new GetPlaces().execute(restaurantsSearchURL);
    } // end onMapReady


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } // end if
        } // end if
    } // end onRequestPermissionsResult


    // class used to verify places http query and verify query
    @SuppressLint("StaticFieldLeak")
    private class GetPlaces extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... restaurantsURL) {
            //fetch places
            StringBuilder placesBuilder = new StringBuilder();
            for (String placeSearchURL : restaurantsURL) {
                try {
                    // places the place API search in a URL
                    URL requestUrl = new URL(placeSearchURL);
                    HttpURLConnection connection = (HttpURLConnection)requestUrl.openConnection();

                    // specifying that a http GET request is needed
                    connection.setRequestMethod("GET");
                    connection.connect();

                    // variable to store response code if there's any connection feedback
                    int responseCode = connection.getResponseCode();

                    // if the http response code says everything is functioning
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        // reads the contents of the page
                        BufferedReader bReader;
                        InputStream inputStream = connection.getInputStream();

                        if (inputStream == null) {
                            return "";
                        } // end if

                        bReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;

                        while ((line = bReader.readLine()) != null) {
                            placesBuilder.append(line).append("\n");
                        } // end while

                        if (placesBuilder.length() == 0) {
                            return "";
                        } // end if

                        // log for debugging
                        Log.d("test", placesBuilder.toString());
                    } // end if
                    else {
                        // log for debugging
                        Log.i("test", "Unsuccessful HTTP Response Code: " + responseCode);
                    } // end else

                } // end try
                // error with URL
                catch (MalformedURLException e) {
                    // log for debugging
                    Log.e("test", "Error processing Places API URL", e);
                } // end catch
                // catch for error
                catch (IOException e) {
                    // log for debugging
                    Log.e("test", "Error connecting to Places API", e);
                } // end catch
            } // end for
            return placesBuilder.toString();
        } // end doInBackground


        // class used to process the data from doInBackground
        protected void onPostExecute(String result) {
            try {
                // jsonOBJECT used to store the string variable result from doInBackground
                JSONObject resultObject = new JSONObject(result);
                // creates the array to store data in
                JSONArray restaurantsArray = resultObject.getJSONArray("results");
                // create marker for each place returned
                restaurants = new MarkerOptions[((JSONArray) restaurantsArray).length()];

                // log used for debugging
                Log.d("test", "The placesArray length is " + restaurantsArray.length() + "...............");

                // for loop through places
                for (int i = 0; i < restaurantsArray.length(); i++) {
                    // variable to check for missing values, if any are present no marker placed
                    boolean missingValue;

                    LatLng restLatLng = null;
                    String restName = "";
                    String vicinity = "";

                    // try catch for any places with missing values
                    try {
                        missingValue = false;
                        // get the details of each place at each index
                        JSONObject placeObject = restaurantsArray.getJSONObject(i);
                        // gets information about place geometry
                        JSONObject loc = placeObject.getJSONObject("geometry")
                                .getJSONObject("location");
                        // reads the latitude and longitude of the place
                        restLatLng = new LatLng(Double.parseDouble(loc.getString("lat")),
                                Double.parseDouble(loc.getString("lng")));
                        // storing the area
                        vicinity = placeObject.getString("vicinity");
                        // storing the place name
                        restName = placeObject.getString("name");
                    } // end try

                    // catch to account for a restaurant with missing values
                    catch (JSONException jse) {
                        Log.v("RESTAURANT", "missing value");
                        missingValue = true;
                        jse.printStackTrace();
                    } // end catch

                    // making sure a restaurant with missing info doesn't display
                    if (missingValue) restaurants[i] = null;
                    else
                        restaurants[i] = new MarkerOptions()
                                .position(restLatLng)
                                .title(restName)
                                .snippet(vicinity);
                } // end for
            }// end try
            catch (Exception e) {
                e.printStackTrace();
            }// end catch

            // checking if there's values stored at restaurants and restaurantsMarkers
            if (restaurants != null && restaurantMarkers != null) {
                // log used for debugging
                Log.d("test", "The restaurantMarkers length is " + restaurantMarkers.length + "...............");

                // for loop to navigate through places
                for (int x = 0; x < restaurants.length && x < restaurantMarkers.length; x++) {
                    // if there is a value at the current index
                    if (restaurants[x] != null) {
                        // places a marker on the map
                        restaurantMarkers[x] = gMap.addMarker(restaurants[x]);
                    } // end if
                } // end for
            } // end if
        } // end onPostExecute
    } // end GetPlaces
} // end MapsActivity