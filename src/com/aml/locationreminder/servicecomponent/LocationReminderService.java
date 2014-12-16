package com.aml.locationreminder.servicecomponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.aml.locationreminder.LocationUpdateActivity;
import com.aml.locationreminder.databasecomponent.LocationReminderDatabaseAccessUtility;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class LocationReminderService extends Service {

	public static int SERVICE_COUNT;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private ArrayList<String> places = new ArrayList<String>();
	private LocationManager mlocManager = null;
	private LocationListener mlocListener;

	private final class ServiceHandler extends Handler {

		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {

			  mlocManager = (LocationManager)
			  getSystemService(Context.LOCATION_SERVICE);
			  
			  mlocListener = new MyLocationListener();
			  mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
			  20000, 100, mlocListener);
			 

		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
		SERVICE_COUNT = 1;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mlocManager != null) {
			mlocManager.removeUpdates(mlocListener);
		}

		SERVICE_COUNT = 0;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		if (getContentResolver() != null) {
			Cursor c = getContentResolver().query(
					LocationReminderDatabaseAccessUtility.CONTENT_URI, null,
					null, null, null);
			places.clear();
			if (c.moveToFirst()) {

				do {
					places.add(c.getString(0));
				} while (c.moveToNext());
			}
		}
		if (places.isEmpty()) {
			Toast.makeText(getApplicationContext(),
					"Location Reminder Service Stoped", Toast.LENGTH_SHORT)
					.show();
			stopSelf();
		}

		if (1 == SERVICE_COUNT) {

			Message msg = mServiceHandler.obtainMessage();
			msg.arg1 = startId;
			mServiceHandler.sendMessage(msg);
		}
		SERVICE_COUNT++;

		return Service.START_NOT_STICKY;

	}

	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {

			double currentLatitude = loc.getLatitude();
			double currentLongitude = loc.getLongitude();
			Geocoder gc = new Geocoder(getApplicationContext(),
					Locale.getDefault());
			try {
				List<Address> addresses = gc.getFromLocation(currentLatitude,
						currentLongitude, 1);
				if (addresses.size() != 0) {

					String streetName = addresses.get(0).getFeatureName();
					String cityName = addresses.get(0).getLocality();
					
					for(String tempName: places){
						
						if (tempName.equalsIgnoreCase(cityName) || tempName.equalsIgnoreCase(streetName)) {
							
							Intent new_Activity = new Intent(
									getApplicationContext(),
									LocationUpdateActivity.class);
							new_Activity.putExtra("placeToDelete", tempName );
							new_Activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(new_Activity);
						}						
					}


				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "Gps Enabled",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	}

}