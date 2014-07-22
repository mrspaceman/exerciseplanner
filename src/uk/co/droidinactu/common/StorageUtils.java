package uk.co.droidinactu.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.os.Environment;

public final class StorageUtils {

	/**
	 * Similar to android.os.Environment.getExternalStorageDirectory(), except
	 * that here, we return all possible storage directories. The Environment
	 * class only returns one storage directory. If you have an extended SD
	 * card, it does not return the directory path. Here we are trying to return
	 * all of them.
	 * 
	 * @return
	 */
	public static String[] getStorageDirectories() {
		String[] dirs = null;
		BufferedReader bufReader = null;
		try {
			bufReader = new BufferedReader(new FileReader("/proc/mounts"));
			final ArrayList<String> list = new ArrayList<String>();
			String line;
			while ((line = bufReader.readLine()) != null) {
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
}
