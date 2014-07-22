package uk.co.droidinactu.exerciseplanner.trackrecorder;

import java.util.ArrayList;
import java.util.Timer;

import uk.co.droidinactu.exerciseplanner.planviewer.PlanViewerActivity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public final class GpsService extends Service {

	public class GpsLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(final Location location) {
			DataAggregator.getInstance().addToWorkoutTrack(location);
			if (lastLocation != null) {
				DataAggregator.getInstance().totalDistanceMeters += lastLocation.distanceTo(location);
			}
			lastLocation = location;
			// FIXME: updateDistanceDisplay();
		}

		@Override
		public void onProviderDisabled(final String provider) {
			/* bring up the GPS settings */
			final Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		@Override
		public void onProviderEnabled(final String provider) {
			Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(final String provider, final int status, final Bundle extras) {
			/* This is called when the GPS status alters */
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				// FIXME:
				// locationStatusIcon.setImageResource(R.drawable.wrkout_recrdr_location_off);
				Log.v(LOG_TAG, "Location Status Changed: Out of Service");
				Toast.makeText(getApplicationContext(), "TPV Location Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				// FIXME:
				// locationStatusIcon.setImageResource(R.drawable.wrkout_recrdr_location_off);
				Log.v(LOG_TAG, "Location Status Changed: Temporarily Unavailable");
				Toast.makeText(getApplicationContext(), "TPV Location Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.AVAILABLE:
				// FIXME:
				// locationStatusIcon.setImageResource(R.drawable.wrkout_recrdr_location_found);
				Log.v(LOG_TAG, "Location Status Changed: Available");
				Toast.makeText(getApplicationContext(), "TPV Location Status Changed: Available", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	public class IncomingHandler extends Handler { // Handler of incoming
												   // messages from clients.
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case MSG_START_RECORDING:
				mClients.add(msg.replyTo);
				break;
			case MSG_STOP_RECORDING:
				mClients.remove(msg.replyTo);
				break;
			case MSG_SEND_LATEST:
				mClients.remove(msg.replyTo);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	public static final String LOG_TAG = GpsService.class.getSimpleName();
	public static final int MSG_START_RECORDING = 1;
	public static final int MSG_STOP_RECORDING = 2;
	public static final int MSG_NEW_LOCATION = 3;
	public static final int MSG_SEND_LATEST = 4;

	ArrayList<Messenger> mClients = new ArrayList<Messenger>(); // Keeps track
																// of all
																// current
																// registered
																// clients.

	private GpsLocationListener locationListener;
	private static final int ONE_MINUTE = 1000 * 60 * 1;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private LocationManager lm;
	private Location lastLocation = null;
	private NotificationManager nm;
	private final Timer timer = new Timer();
	private static boolean isRecording = false;

	final Messenger mMessenger = new Messenger(new IncomingHandler()); // Target
																	   // we
																	   // publish
																	   // for
																	   // clients
																	   // to
																	   // send
																	   // messages
																	   // to
																	   // IncomingHandler.

	public void initialise(final Context ctx) {
		Log.i(PlanViewerActivity.LOG_TAG, "DataModel::initialise()");

	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(final Location location, final Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		final long timeDelta = location.getTime() - currentBestLocation.getTime();
		final boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		final boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		final boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location,
		// use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must
			// be worse
		} else if (isSignificantlyOlder) { return false; }

		// Check whether the new location fix is more or less accurate
		final int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		final boolean isLessAccurate = accuracyDelta > 0;
		final boolean isMoreAccurate = accuracyDelta < 0;
		final boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		final boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) { return true; }
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(final String provider1, final String provider2) {
		if (provider1 == null) { return provider2 == null; }
		return provider1.equals(provider2);
	}

	@Override
	public IBinder onBind(final Intent arg0) {
		return mMessenger.getBinder();
	}

	@Override
	public void onRebind(final Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {

		locationListener = new GpsLocationListener();

		/*
		 * the location manager is the most vital part it allows access to
		 * location and GPS status services
		 */
		lm = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, locationListener);

		return 0;
	}

	@Override
	public boolean onUnbind(final Intent intent) {
		return super.onUnbind(intent);
	}

	private void sendMessageToUI() {

	}

	private void sendMessageToUI(final TrackPoint tp) {
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				// Send data as a Parcelable Object
				final Bundle b = new Bundle();
				b.putInt("TrackPointHeartRate", tp.heartRate);
				b.putParcelable("TrackPointLocation", tp.location);
				final Message msg = Message.obtain(null, MSG_NEW_LOCATION);
				msg.setData(b);
				mClients.get(i).send(msg);

			} catch (final RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				mClients.remove(i);
			}
		}
	}

	private void stopRecording() {
		lm.removeUpdates(locationListener);
	}

}
