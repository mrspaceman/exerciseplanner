/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uk.co.droidinactu.exerciseplanner.planviewer.smartwatch.sony2;

import java.util.Random;

import uk.co.droidinactu.exerciseplanner.planviewer.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.sonyericsson.extras.liveware.aef.notification.Notification;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.notification.NotificationUtil;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

/**
 * The sample extension service handles extension registration and inserts data
 * into the notification database.
 */
public class SW2PlanViewExtensionService extends ExtensionService {

	/**
	 * Extensions specific id for the source
	 */
	public static final String EXTENSION_SPECIFIC_ID = "EXTENSION_SPECIFIC_ID_SAMPLE_NOTIFICATION";

	/**
	 * Extension key
	 */
	public static final String EXTENSION_KEY = "uk.co.droidinactu.exerciseplanner.planviewer.smartwatch.sony2.key";

	/**
	 * Log tag
	 */
	public static final String LOG_TAG = SW2PlanViewExtensionService.class.getSimpleName();

	/**
	 * Event names
	 */
	private static final String[] NAMES = new String[] { "Name A", "Name B", "Name C", "Name D", "Name D", "Name E", };

	/**
	 * Event messages
	 */
	private static final String[] MESSAGE = new String[] { "Message 1", "Message 2", "Message 3", "Message 4",
			"Message 5", "Message 6", };

	/**
	 * Time between new data insertion
	 */
	private static final long INTERVAL = 10 * 1000;

	/**
	 * Starts periodic insert of data handled in onStartCommand()
	 */
	public static final String INTENT_ACTION_START = "com.sonymobile.smartconnect.extension.notificationsample.action.start";

	/**
	 * Stop periodic insert of data, handled in onStartCommand()
	 */
	public static final String INTENT_ACTION_STOP = "com.sonymobile.smartconnect.extension.notificationsample.action.stop";

	/**
	 * Add data, handled in onStartCommand()
	 */
	private static final String INTENT_ACTION_ADD = "com.sonymobile.smartconnect.extension.notificationsample.action.add";

	public SW2PlanViewExtensionService() {
		super(EXTENSION_KEY);
	}

	/**
	 * Add some "random" data
	 */
	private void addData() {
		final Random rand = new Random();
		final int index = rand.nextInt(5);
		final String name = NAMES[index];
		final String message = MESSAGE[index];
		final long time = System.currentTimeMillis();
		final long sourceId = NotificationUtil.getSourceId(this, EXTENSION_SPECIFIC_ID);
		if (sourceId == NotificationUtil.INVALID_ID) {
			Log.e(LOG_TAG, "Failed to insert data");
			return;
		}
		final String profileImage = ExtensionUtils.getUriString(this, R.drawable.widget_default_userpic_bg);

		final ContentValues eventValues = new ContentValues();
		eventValues.put(Notification.EventColumns.EVENT_READ_STATUS, false);
		eventValues.put(Notification.EventColumns.DISPLAY_NAME, name);
		eventValues.put(Notification.EventColumns.MESSAGE, message);
		eventValues.put(Notification.EventColumns.PERSONAL, 1);
		eventValues.put(Notification.EventColumns.PROFILE_IMAGE_URI, profileImage);
		eventValues.put(Notification.EventColumns.PUBLISHED_TIME, time);
		eventValues.put(Notification.EventColumns.SOURCE_ID, sourceId);

		try {
			getContentResolver().insert(Notification.Event.URI, eventValues);
		} catch (final IllegalArgumentException e) {
			Log.e(LOG_TAG, "Failed to insert event", e);
		} catch (final SecurityException e) {
			Log.e(LOG_TAG, "Failed to insert event, is Live Ware Manager installed?", e);
		} catch (final SQLException e) {
			Log.e(LOG_TAG, "Failed to insert event", e);
		}
	}

	/**
	 * Show toast with event information
	 * 
	 * @param eventId
	 *            The event id
	 */
	public void doAction1(final int eventId) {
		Log.d(LOG_TAG, "doAction1 event id: " + eventId);
		Cursor cursor = null;
		try {
			String name = "";
			String message = "";
			cursor = getContentResolver().query(Notification.Event.URI, null,
					Notification.EventColumns._ID + " = " + eventId, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int nameIndex = cursor.getColumnIndex(Notification.EventColumns.DISPLAY_NAME);
				final int messageIndex = cursor.getColumnIndex(Notification.EventColumns.MESSAGE);
				name = cursor.getString(nameIndex);
				message = cursor.getString(messageIndex);
			}

			final String toastMessage = getText(R.string.action_event_1) + ", Event: " + eventId + ", Name: " + name
					+ ", Message: " + message;
			Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
		} catch (final SQLException e) {
			Log.e(LOG_TAG, "Failed to query event", e);
		} catch (final SecurityException e) {
			Log.e(LOG_TAG, "Failed to query event", e);
		} catch (final IllegalArgumentException e) {
			Log.e(LOG_TAG, "Failed to query event", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	@Override
	protected RegistrationInformation getRegistrationInformation() {
		return new SW2PlanViewRegistrationInformation(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sonyericsson.extras.liveware.aef.util.ExtensionService#
	 * keepRunningWhenConnected()
	 */
	@Override
	protected boolean keepRunningWhenConnected() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "onCreate");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
	}

	@Override
	protected void onRefreshRequest() {
		// Do nothing here, only relevant for polling extensions, this
		// extension is always up to date
	}

	/**
	 * Called when extension and sources has been successfully registered.
	 * Override this method to take action after a successful registration.
	 */
	@Override
	public void onRegisterResult(final boolean result) {
		super.onRegisterResult(result);
		Log.d(LOG_TAG, "onRegisterResult");

		// Start adding data if extension is active in preferences
		if (result) {
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			final boolean isActive = prefs.getBoolean(getString(R.string.preference_key_is_active), false);
			if (isActive) {
				startAddData();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see android.app.Service#onStartCommand()
	 */
	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		final int retVal = super.onStartCommand(intent, flags, startId);
		if (intent != null) {
			if (INTENT_ACTION_START.equals(intent.getAction())) {
				Log.d(LOG_TAG, "onStart action: INTENT_ACTION_START");
				startAddData();
				stopSelfCheck();
			} else if (INTENT_ACTION_STOP.equals(intent.getAction())) {
				Log.d(LOG_TAG, "onStart action: INTENT_ACTION_STOP");
				stopAddData();
				stopSelfCheck();
			} else if (INTENT_ACTION_ADD.equals(intent.getAction())) {
				Log.d(LOG_TAG, "onStart action: INTENT_ACTION_ADD");
				addData();
				stopSelfCheck();
			}
		}

		return retVal;
	}

	@Override
	protected void onViewEvent(final Intent intent) {
		final String action = intent.getStringExtra(Notification.Intents.EXTRA_ACTION);
		final String hostAppPackageName = intent.getStringExtra(Registration.Intents.EXTRA_AHA_PACKAGE_NAME);
		final boolean advancedFeaturesSupported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(this,
				hostAppPackageName);

		final int eventId = intent.getIntExtra(Notification.Intents.EXTRA_EVENT_ID, -1);
		if (Notification.SourceColumns.ACTION_1.equals(action)) {
			doAction1(eventId);
		} else if (Notification.SourceColumns.ACTION_2.equals(action)) {
			// Here we can take different actions depending on the device.
			if (advancedFeaturesSupported) {
				Toast.makeText(this, "Action 2 API level 2", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Action 2", Toast.LENGTH_LONG).show();
			}
		} else if (Notification.SourceColumns.ACTION_3.equals(action)) {
			Toast.makeText(this, "Action 3", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Start periodic data insertion into event table
	 */
	private void startAddData() {
		final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		final Intent i = new Intent(this, SW2PlanViewExtensionService.class);
		i.setAction(INTENT_ACTION_ADD);
		final PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), INTERVAL, pi);
	}

	/**
	 * Cancel scheduled data insertion
	 */
	private void stopAddData() {
		final AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		final Intent i = new Intent(this, SW2PlanViewExtensionService.class);
		i.setAction(INTENT_ACTION_ADD);
		final PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		am.cancel(pi);
	}
}
