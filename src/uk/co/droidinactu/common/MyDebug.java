package uk.co.droidinactu.common;

import java.io.File;

import android.os.Debug;

public final class MyDebug {

	public static boolean DEBUGGING = false;
	public static boolean TRACE = false;
	public static String TRACE_DIRECTORY = "droidinactu.traces";

	public static void startMethodTracing(final String traceFile) {
		if (DEBUGGING && TRACE) {
			Debug.startMethodTracing(TRACE_DIRECTORY + File.separator + traceFile);
		}
	}

	public static void stopMethodTracing() {
		if (DEBUGGING && TRACE) {
			Debug.stopMethodTracing();
		}
	}
}
