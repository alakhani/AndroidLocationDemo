package com.example.androidlocationdemo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;

import java.util.ArrayList;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationClient.OnAddGeofencesResultListener {
	
	private LocationClient locationClient;
    private LocationRequest locationRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resp == ConnectionResult.SUCCESS) {
            locationClient = new LocationClient(this, this, this);
            locationClient.connect();
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onAddGeofencesResult(int arg0, String[] arg1) {
		// TODO Auto-generated method stub
		 if (LocationStatusCodes.SUCCESS == arg0) {
	            //todo check geofence status
	        } else {

	        }
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle bundle) {
		// TODO Auto-generated method stub
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5 * 50 * 1000);
        locationRequest.setFastestInterval(5 * 50 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Intent intent = new Intent(this, LocationIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, 0);
        locationClient.requestLocationUpdates(locationRequest, pendingIntent);

        ArrayList<Store> storeList = getStoreList();
        if (null != storeList && storeList.size() > 0) {
            ArrayList<Geofence> geofenceList = new ArrayList<Geofence>();
            for (Store store : storeList) {
                float radius = (float) store.radius;
                Geofence geofence = new Geofence.Builder()
                        .setRequestId(store.id)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .setCircularRegion(store.latitude, store.longitude, radius)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .build();

                geofenceList.add(geofence);
            }

            PendingIntent geoFencePendingIntent = PendingIntent.getService(this, 0,
                    new Intent(this, GeofenceIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT);
            locationClient.addGeofences(geofenceList, geoFencePendingIntent, this);
        }
		
	}

    private ArrayList<Store> getStoreList() {
        ArrayList<Store> storeList = new ArrayList<Store>();
        for (int i = 0; i < 1; i++) {
            Store store = new Store();
            store.id = String.valueOf(i);
            store.address = "Beijing, China";
            store.latitude = 39.17513657;
            store.longitude = -86.49361420;
            store.radius = 100.0D;

            storeList.add(store);
        }

        return storeList;
    }

    public class Store {
        String id;
        String address;
        double latitude;
        double longitude;
        double radius;
    }
    
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
