package com.zapeat.activity;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class ZapeatApp extends Application implements LocationListener {

	private LocationManager locationManager;
	private Double latitude;
	private Double longitude;

	@Override
	public void onCreate() {
		super.onCreate();
		this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		Location location = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (location != null) {
			this.latitude = location.getLatitude();
			this.longitude = location.getLongitude();
		}

	}

	public Double getLatitudeAtual() {
		return this.latitude;
	}

	public Double getLongitudeAtual() {
		return this.longitude;
	}

	@Override
	public void onLocationChanged(Location location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
