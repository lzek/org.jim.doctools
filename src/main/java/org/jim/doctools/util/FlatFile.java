package org.jim.doctools.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class FlatFile {
	public static void write(String filename, String word) throws IOException {
		write(filename, word, true);
	}

	public static void write(String filename, String word, Boolean isappend) throws IOException {
		File f = new File(filename);
		if (!f.exists()) {
			File pf = f.getParentFile();
			if (pf != null)
				pf.mkdirs();
			isappend = false;
		}
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f, isappend), "UTF-8");
		osw.append(word);
		osw.flush();
		osw.close();
	}

	public static ArrayList<String> read(String filename) throws IOException {
		ArrayList<String> result = new ArrayList<String>();
		File f = new File(filename);
		if (f.exists()) {
			InputStreamReader rd = new InputStreamReader(new FileInputStream(f), "UTF-8");
			BufferedReader br = new BufferedReader(rd);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line != null && !line.equals("")) {
					result.add(line);
				}
			}
			rd.close();
			br.close();
		}
		return result;
	}

	public static void upgradePropertiesFromStandard(HashMap<String, String> dproperties) throws IOException {
		ArrayList<String> appProperties = FlatFile.read("properties.ini");

		for (String x : appProperties) {
			int splitPos = x.indexOf("=");
			if (splitPos < 0)
				continue;
			String newkey = x.substring(0, splitPos);
			if (newkey.trim().startsWith("#") || newkey.trim().startsWith("["))
				continue;
			String newvalue = "";
			if (splitPos + 1 < x.length())
				newvalue = x.substring(splitPos + 1);
			Boolean hasfind = false;
			for (String dpkey : dproperties.keySet()) {
				if (newkey.contentEquals(dpkey)) {
					hasfind = true;
					break;
				}
			}
			if (!hasfind) {
				dproperties.put(newkey, newvalue);
			}
		}
	}

	public static void saveProperties(HashMap<String, String> dp) throws IOException {
		ArrayList<String> appProperties = FlatFile.read("app.properties");

		for (String dpkey : dp.keySet()) {
			if (dpkey.trim().startsWith("#") || dpkey.trim().startsWith("["))
				continue;

			Boolean hasset = false;

			for (int index = 0; index < appProperties.size(); index++) {
				int splitPos = appProperties.get(index).indexOf("=");
				if (splitPos < 0)
					continue;
				String appkey = appProperties.get(index).substring(0, splitPos);
				if (appkey.contentEquals(dpkey)) {
					appProperties.set(index, appkey + "=" + dp.get(dpkey));
					hasset = true;
				}
			}
			if (!hasset) {
				appProperties.add(dpkey + "=" + dp.get(dpkey));
			}
		}

		saveProperties(appProperties);
	}

	public static ArrayList<String> loadProperties() throws IOException {
		return read("app.properties");
	}

	public static void saveProperties(ArrayList<String> appProperties) throws IOException {
		saveProperties(appProperties, "app.properties");
	}

	public static void saveProperties(ArrayList<String> appProperties, String PropertiesName) throws IOException {
		for (int i = 0; i < appProperties.size(); i++) {
			if (i == 0) {
				FlatFile.write(PropertiesName, appProperties.get(i), false);
			} else {
				FlatFile.write(PropertiesName, "\r\n" + appProperties.get(i));
			}
		}
	}
}
