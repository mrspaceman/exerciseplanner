/*
 * Copyright (c) 2011 Andy Aspell-Clark
 *
 */
package uk.co.droidinactu.exerciseplanner.trackrecorder;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import uk.co.droidinactu.exerciseplanner.TrainerApplication;
import uk.co.droidinactu.exerciseplanner.planviewer.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public final class HeartRateService extends Service {

	private class PolarCommsTask extends AsyncTask<String, HeartRateRecord, Integer> {

		private static final String LOG_TAG = "PolarCommsTask::";

		private Boolean continueRunning = true;
		private final HeartRateRecord hrr = new HeartRateRecord();
		private BluetoothSocket mmSocket = null;

		public PolarCommsTask() {
			super();
		}

		private void closeSocket() {
			try {
				mmSocket.close();
			} catch (final IOException closeException) {
				Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "closeException : ", closeException);
			}
			mmSocket = null;
		}

		/**
		 * Blocks until a connection is made.
		 */
		private void connectToSocket() {
			// Cancel discovery because it will slow down the connection
			myBluetoothAdapter.cancelDiscovery();
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();

			} catch (final IOException connectException) {
				Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "connectException : ", connectException);
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (final IOException closeException) {
					Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "closeException : ", closeException);
				}
				mmSocket = null;
			}
		}

		@Override
		protected Integer doInBackground(final String... params) {
			while (continueRunning) {
				listenForHr();
			}
			return 1;
		}

		private void listenForHr() {
			showConnectionStatusNotification(HeartRateService.this, R.drawable.ic_connection_started);
			setupSocket();
			if (mmSocket != null) {
				connectToSocket();

				// If a connection was accepted
				if (mmSocket != null) {
					try {
						showConnectionStatusNotification(HeartRateService.this, R.drawable.ic_connection_sucedded);
						final DataInputStream dataInputStream = new DataInputStream(mmSocket.getInputStream());

						final int[] byteRecord = new int[20];
						int dyn_data = 0;
						int curr_byte_nbr = 0;

						while ((dyn_data = dataInputStream.readUnsignedByte()) >= 0 && continueRunning) {
							if (dyn_data == 254 && byteRecord[0] != 0) {
								hrr.parseBytes(byteRecord);
								publishProgress(hrr);

								for (int i = 0; i < curr_byte_nbr; i++) {
									byteRecord[i] = 0;
								}
								curr_byte_nbr = 0;
							}
							if (curr_byte_nbr < byteRecord.length) {
								byteRecord[curr_byte_nbr++] = dyn_data;
							}
						}

						closeSocket();
					} catch (final IOException e) {
						Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "Comms Exception : ", e);
						showConnectionStatusNotification(HeartRateService.this, R.drawable.ic_connection_failed);
					}
				}
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			continueRunning = false;
			closeSocket();
		}

		@Override
		protected void onPostExecute(final Integer result) {
			super.onPostExecute(result);
			continueRunning = false;
			closeSocket();
		}

		@Override
		protected void onProgressUpdate(final HeartRateRecord... values) {
			super.onProgressUpdate(values);
			final ContentResolver cr = getContentResolver();
			try {
				// cr.insert(HRLiveProvider.CONTENT_URI,
				// values[0].getContentValues());
			} catch (final Exception e) {
				Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "exception storing data : ", e);
			}
		}

		private void setupSocket() {
			try {
				// MY_UUID is the app’s UUID string, also used by the server
				// code
				// mmSocket =
				// polarBtDev.createRfcommSocketToServiceRecord(MY_UUID);

				final Method m = polarBtDev.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
				mmSocket = (BluetoothSocket) m.invoke(polarBtDev, 1);
			} catch (final SecurityException e) {
				Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			} catch (final NoSuchMethodException e) {
				Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			} catch (final IllegalArgumentException e) {
				Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			} catch (final InvocationTargetException e) {
				Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			} catch (final Exception e) {
				Log.d(TrainerApplication.LOG_TAG, LOG_TAG + "createRfcommSocketToServiceRecord Exception : ", e);
				e.printStackTrace();
			}
		}
	}

	private static final UUID MY_UUID = UUID.fromString("f2b90360-973f-11e0-aa80-0800200c9a66");

	private BluetoothAdapter myBluetoothAdapter;

	private BluetoothDevice polarBtDev;
	private static final int HEARTRATE_MSG_ID = 1;
	private static final String LOG_TAG = "HeartRateService::";
	private final PolarCommsTask polarCommsTsk = new PolarCommsTask();

	private static final String NAME = "PolarHRMonitor";

	private static final int CONNECTION_STATUS_NOTIFY_ID = 32768;

	public static void showConnectionStatusNotification(final Context context, final int icon) {
		Log.i(TrainerApplication.LOG_TAG, LOG_TAG + "notifyServerConnection()");

		CharSequence contentText = "";
		CharSequence tickerText = "";
		switch (icon) {
		case R.drawable.ic_connection_started:
			contentText = "trying to Connect";
			tickerText = "";
			break;
		case R.drawable.ic_connection_sucedded:
			contentText = "Connected";
			tickerText = "";
			break;
		default:
			contentText = "Connection failed";
			tickerText = "";
		}

		final String ns = Context.NOTIFICATION_SERVICE;
		final long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, tickerText, when);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		final CharSequence contentTitle = "Server Connection";
		final Intent notificationIntent = new Intent();
		final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		mNotificationManager.cancel(CONNECTION_STATUS_NOTIFY_ID);
		mNotificationManager.notify(CONNECTION_STATUS_NOTIFY_ID, notification);
	}

	private Set<BluetoothDevice> pairedDevices = null;

	private boolean enableBluetooth() {
		myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (myBluetoothAdapter == null) { return false; }

		if (!myBluetoothAdapter.isEnabled()) {
			final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(enableIntent);
		}
		return true;
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TrainerApplication.LOG_TAG, LOG_TAG + "onCreate()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TrainerApplication.LOG_TAG, LOG_TAG + "onDestroy()");
		polarCommsTsk.continueRunning = false;
		polarCommsTsk.cancel(true);

		final NotificationManager mNManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNManager.cancel(HEARTRATE_MSG_ID);
		mNManager.cancel(CONNECTION_STATUS_NOTIFY_ID);
	}

	@Override
	public void onStart(final Intent intent, final int startId) {
		super.onStart(intent, startId);
		Log.i(TrainerApplication.LOG_TAG, LOG_TAG + "onStart()");
		try {
			if (enableBluetooth()) {
				Log.e(TrainerApplication.LOG_TAG, LOG_TAG + "Bluetooth on :)");
				pairWithDevice();
				polarCommsTsk.execute(NAME);
				// showHeartRateNotification("HeartRate Service Running");
			} else {
				Log.e(TrainerApplication.LOG_TAG, LOG_TAG + "Bluetooth won't turn on !!!");
				// showHeartRateNotification("HeartRate Service Not Running");
			}

		} catch (final Exception e) {
			Log.e(TrainerApplication.LOG_TAG, LOG_TAG + "onStart() enable bluetooth failed", e);
		}
	}

	private void pairWithDevice() {
		pairedDevices = myBluetoothAdapter.getBondedDevices();
		for (final BluetoothDevice dev : pairedDevices) {
			Log.d(TrainerApplication.LOG_TAG,
					LOG_TAG + "BT device: [name :" + dev.getName() + "; addr:" + dev.getAddress() + "; BtClass:"
							+ dev.getBluetoothClass() + "; bonded:" + dev.getBondState());
			if (dev.getName().toLowerCase().contains("polar")) {
				polarBtDev = dev;
			}
		}
	}

}
