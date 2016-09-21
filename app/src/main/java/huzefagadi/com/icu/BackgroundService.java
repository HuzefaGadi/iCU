package huzefagadi.com.icu;

/**
 * Created by huzefaasger on 20-09-2016.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class BackgroundService extends Service implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    String TAG="icu";
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
        /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS;
    CommonUtility commonUtility;

    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {

            try {
                commonUtility = new CommonUtility(this);
                buildGoogleApiClient();

                mGoogleApiClient.connect();


            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        writeLogs("Service onStartCommand");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onStart(Intent intent, int startId) {
    }

    @Override
    public void onDestroy() {
        try {

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).

        try {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

               // stopCurrentService();
            }
        } catch(SecurityException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(final Location location) {

        try {

            writeLogs("Location Changed");
            mCurrentLocation = location;

            //First Check if Device Has Connection to Internet.
            if (commonUtility.checkInternetConnectivity()) {
                SingleLocation singleLocation = new SingleLocation();
                singleLocation.setUserName(commonUtility.getValueFromPreferences(CommonUtility.USERNAME));
                singleLocation.setLatitude(String.valueOf(location.getLatitude()));
                singleLocation.setLongitude(String.valueOf(location.getLongitude()));
                singleLocation.setLocTime(String.valueOf(location.getTime()));
                String locationData = new Gson().toJson(singleLocation);
                sendLocation(locationData);

            }

        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            writeLogs(CommonUtility.convertStackTraceToString(e));
        } catch (Exception e) {
            writeLogs(CommonUtility.convertStackTraceToString(e));
        }
    }

    void sendLocation(String locationData)
    {
        new sendMessagetask().execute(locationData);
    }
    private class sendMessagetask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            try
            {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("location",urls[0]);
                // HTTP request header
                con.setRequestProperty("project_id", "769444996602");
                con.setRequestProperty("Authorization","key=AIzaSyDiEPq8IZrVWF4VHrpD5GWmAWhDqPoBnzI");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("POST");
                con.connect();

                // HTTP request
                JSONObject data = new JSONObject();
                data.put("to", "/topics/"+ CommonUtility.ICU_TOPIC);
                data.put("data", jsonObject);


                OutputStream os = con.getOutputStream();
                os.write(data.toString().getBytes("UTF-8"));
                os.close();

                // Read the response into a string
                InputStream is = con.getInputStream();
                String responseString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
                is.close();
                Log.d(TAG,responseString);
                // Parse the JSON string and return the notification key
                JSONObject response = new JSONObject(responseString);
                return response.toString();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }



        protected void onPostExecute(String result) {

        }
    }
    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        try {
            writeLogs("Building GoogleApiClient");
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            writeLogs("GoogleApiClient Built");
            createLocationRequest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            writeLogs(CommonUtility.convertStackTraceToString(e));
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */




    @Override
    public void onConnected(Bundle connectionHint) {


        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        try {
            if (mCurrentLocation == null && mGoogleApiClient.isConnected()) {
                writeLogs("Connected to GoogleApiClient in method onConnected()");
                startLocationUpdates();
            } else {
                writeLogs("GoogleApiClient is not Connected in method onConnected()");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            writeLogs("GoogleApiClient is not Connected in method onConnected()");
        }
		/*writeLogs("Getting Last Location From Fused Location API");
			mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
			writeLogs("Got Last Location From Fused Location API");
			if(mCurrentLocation!=null){
				writeLogs("Latitude: " + mCurrentLocation.getLatitude());
				writeLogs("Latitude: " + mCurrentLocation.getLongitude());
			}
		}*/
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to attempt to re-establish the connection.
        try {
            writeLogs("Connection suspended --> Connecting Again");
            mGoogleApiClient.connect();
            writeLogs("Google Api Client Connected Again");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            writeLogs(CommonUtility.convertStackTraceToString(e));
        }
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        try {
            writeLogs("Creating Location Request");
            mLocationRequest = new LocationRequest();

            // Sets the desired interval for active location updates. This interval is
            // inexact. You may not receive updates at all if no location sources are available, or
            // you may receive them slower than requested. You may also receive updates faster than
            // requested if other applications are requesting location at a faster interval.
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

            // Sets the fastest rate for active location updates. This interval is exact, and your
            // application will never receive updates faster than this value.
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            writeLogs("Location Request Created");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            writeLogs(CommonUtility.convertStackTraceToString(e));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in onConnectionFailed.
        writeLogs("Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
      //  stopCurrentService();
    }

    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            } else {
                writeLogs("GoogleApiClient not connected while removing Location Update");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            writeLogs("Error in Closing the location updates");
            writeLogs(CommonUtility.convertStackTraceToString(e));
        }

    }






    private void writeLogs(String log) {

            Log.d(TAG,log);

    }
}

