package uk.co.droidinactu.exerciseplanner.planviewer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public final class SDCardUtils {
	public static final String LOG_TAG = SDCardUtils.class.getSimpleName();

	/**
	 * Number of bytes in one KB = 2<sup>10</sup>
	 */
	private final static long SIZE_KB = 1024L;

	/**
	 * Number of bytes in one MB = 2<sup>20</sup>
	 */
	private final static long SIZE_MB = SIZE_KB * SIZE_KB;

	/**
	 * Number of bytes in one GB = 2<sup>30</sup>
	 */
	private final static long SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;

	private static final int sdCardDiscoveryMethod = 1;

	/**
	 * @return Number of bytes available on external storage
	 */
	public static long getExternalAvailableSpaceInBytes() {
		long availableSpace = -1L;
		try {
			final StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			availableSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return availableSpace;
	}

	/**
	 * @return giga bytes of bytes available on external storage
	 */
	public static long getExternalAvailableSpaceInGB() {
		return getExternalAvailableSpaceInBytes() / SIZE_GB;
	}

	/**
	 * @return Number of kilo bytes available on external storage
	 */
	public static long getExternalAvailableSpaceInKB() {
		return getExternalAvailableSpaceInBytes() / SIZE_KB;
	}

	/**
	 * @return Number of mega bytes available on external storage
	 */
	public static long getExternalAvailableSpaceInMB() {
		return getExternalAvailableSpaceInBytes() / SIZE_MB;
	}

	/**
	 * @return Total number of available blocks on external storage
	 */
	public static long getExternalStorageAvailableBlocks() {
		long availableBlocks = -1L;
		try {
			final StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			availableBlocks = stat.getAvailableBlocks();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return availableBlocks;
	}

	/**
	 * @return external storage size in giga bytes
	 */
	public static long getExternalTotalSpaceInBytes() {
		final StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		final long totalSpace = stat.getBlockCount() * (long) stat.getBlockSize();
		return totalSpace;
	}

	/**
	 * @return external storage size in giga bytes
	 */
	public static long getExternalTotalSpaceInGB() {
		final StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		final long totalSpace = stat.getBlockCount() * (long) stat.getBlockSize();
		return totalSpace / SIZE_GB;
	}

	public static String[] getStorageDirectories() {
		switch (sdCardDiscoveryMethod) {
		case 1:
			return getStorageDirectoriesMthd1();
		case 2:
			return getStorageDirectoriesMthd2();
		}
		return new String[] { "" };
	}

	/**
	 * Similar to android.os.Environment.getExternalStorageDirectory(), except
	 * that here, we return all possible storage directories. The Environment
	 * class only returns one storage directory. If you have an extended SD
	 * card, it does not return the directory path. Here we are trying to return
	 * all of them.
	 * 
	 * @return
	 */
	private static String[] getStorageDirectoriesMthd1() {
		String[] dirs = null;
		BufferedReader bufReader = null;
		try {
			bufReader = new BufferedReader(new FileReader("/proc/mounts"));
			final ArrayList<String> list = new ArrayList<String>();
			String line;
			while ((line = bufReader.readLine()) != null) {
				Log.i(LOG_TAG, "getStorageDirectoriesMthd1() mountpoint : " + line);
				if (line.contains("vfat") || line.contains("/mnt")) {
					final StringTokenizer tokens = new StringTokenizer(line, " ");
					String s = tokens.nextToken();
					s = tokens.nextToken(); // Take the second token, i.e. mount
											// point

					if (s.equals(Environment.getExternalStorageDirectory().getPath())) {
						list.add(s);
					} else if (line.contains("/dev/block/vold")) {
						if (!line.contains("/mnt/secure") && !line.contains("/mnt/asec") && !line.contains("/mnt/obb")
								&& !line.contains("/dev/mapper") && !line.contains("tmpfs")) {
							list.add(s);
						}
					}
				}
			}

			dirs = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				dirs[i] = list.get(i);
			}
		} catch (final FileNotFoundException e) {
		} catch (final IOException e) {
		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (final IOException e) {
				}
			}
		}

		return dirs;
	}

	private static String[] getStorageDirectoriesMthd2() {
		final String[] dirs = null;
		final String[] commands = new String[] { "mount" };

		String mountResults = "";
		try {
			final Process proc = Runtime.getRuntime().exec(commands);
			final InputStream stdout = proc.getInputStream();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));

			Thread.sleep(2000);

			String line = reader.readLine();
			while (line != null && !line.trim().equals("--EOF--")) {
				mountResults += line + "--split--";
				line = reader.readLine();
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String[] mountpoints = mountResults.split("--split--");
		for (final String mountpoint : mountpoints) {
			Log.i(LOG_TAG, "getStorageDirectoriesMthd2() mountpoint : " + mountpoint);
		}

		return dirs;
	}

	public static void test() {
		final double sdCardSizeBytes = SDCardUtils.getExternalTotalSpaceInBytes();
		final double sdCardAvailBytes = SDCardUtils.getExternalAvailableSpaceInBytes();

		final double sdCardAvailKb = SDCardUtils.getExternalAvailableSpaceInKB();
		final double sdCardAvailMb = SDCardUtils.getExternalAvailableSpaceInMB();

		final double sdCardSizeGb = SDCardUtils.getExternalTotalSpaceInGB();
		final double sdCardAvailGb = SDCardUtils.getExternalAvailableSpaceInGB();

		final int availablePercent = (int) (sdCardAvailBytes / sdCardSizeBytes * 100);
		final int usedPercent = 100 - availablePercent;

		Log.i(LOG_TAG, "DCSDCardInfoWidget::onUpdateData() : [sdCardSizeBytes:" + sdCardSizeBytes + "] [sdCardAvailBytes:"
				+ sdCardAvailBytes + "] [availablePercent:" + availablePercent + "] [usedPercent:" + usedPercent + "]");

		if (usedPercent <= 25) {
			Log.i(LOG_TAG, "DCSDCardInfoWidget::onUpdateData() : using icon fill_00pct");
		} else if (usedPercent < 50) {
			Log.i(LOG_TAG, "DCSDCardInfoWidget::onUpdateData() : using icon fill_25pct");
		} else if (usedPercent < 75) {
			Log.i(LOG_TAG, "DCSDCardInfoWidget::onUpdateData() : using icon fill_50pct");
		} else if (usedPercent < 95) {
			Log.i(LOG_TAG, "DCSDCardInfoWidget::onUpdateData() : using icon fill_75pct");
		} else {
			Log.i(LOG_TAG, "DCSDCardInfoWidget::onUpdateData() : using icon fill_100pct");
		}
	}
}
