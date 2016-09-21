package huzefagadi.com.icu;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    CommonUtility commonUtility;
    DBHandler dbHandler;
    GoogleMap map;
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHandler = new DBHandler(this);
        commonUtility = new CommonUtility(this);
        if (commonUtility.getValueFromPreferences(CommonUtility.USERNAME) != null) {
            Gson gson = new Gson();
            if (!isMyServiceRunning(BackgroundService.class)) {
                Intent intentForBackground = new Intent(this, BackgroundService.class);
                startService(intentForBackground);
            }
//            List<SingleLocation> list = dbHandler.getAllLocations(commonUtility.getValueFromPreferences(commonUtility.USERNAME));
        //    for (int i = 0; i < list.size(); i++) {
        //        Log.d("DATA", gson.toJson(list.get(i), SingleLocation.class));
        //    }

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            handler.postDelayed(runnable, 5000);
        } else {

            Intent intent = new Intent(this, SignInActvity.class);
            startActivity(intent);
            finish();
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        try {
            map.setMyLocationEnabled(true);
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {

                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
                    map.moveCamera(center);
                    map.animateCamera(zoom);
                    map.setOnMyLocationChangeListener(null);

                }
            });

            this.map = map;
            plotOnMap();
        } catch (SecurityException e) {

        }


    }

    private void plotOnMap() {
        if (map != null) {
            try {
                List<SingleLocation> listAllLocations = dbHandler.getAllLatestLocations();
                map.clear();
                for(int i=0;i<listAllLocations.size();i++)
                {
                    SingleLocation singleLocation = listAllLocations.get(i);
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(singleLocation.getLatitude()),
                                    Double.parseDouble(singleLocation.getLongitude())))
                            .title(singleLocation.getUserName())).showInfoWindow();
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try{
                plotOnMap();
            }
            catch (Exception e) {
                // TODO: handle exception
            }
            finally{
                //also call the same runnable to call it at regular interval
                handler.postDelayed(this, 5000);
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 5000);
    }
}
