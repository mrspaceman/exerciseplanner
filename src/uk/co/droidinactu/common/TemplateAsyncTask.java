package uk.co.droidinactu.common;

import android.os.AsyncTask;

public class TemplateAsyncTask extends AsyncTask<Void, Void, Void> {
	private static final String LOG_TAG = TemplateAsyncTask.class.getSimpleName();

	@Override
	protected Void doInBackground(final Void... arg0) {

		// start tracing to
		// "/sdcard/droidinactu.traces/TemplateAsyncTask.trace"
		MyDebug.startMethodTracing("TemplateAsyncTask");

		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// calList.setAdapter(adapter);
		// }
		// });

		// stop tracing
		MyDebug.stopMethodTracing();

		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Thread.currentThread().setName("TemplateAsyncTask");
	}
}
