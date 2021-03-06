package com.ibm.spe.tool.util;

import java.util.ResourceBundle;

public class ConfigUtils {

	private static ResourceBundle bundle = null;

	private static String BUNDLENAME = "compare-tool";

	private static ResourceBundle getResourceBundle() {
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(BUNDLENAME);
		}
		return bundle;
	}

	public static String getProperty(String name, String defaultValue) {
		if (name == null)
			return null;
		String value = null;
		try {
			value = getResourceBundle().getString(name).trim();
			if (value != null && value.length() == 0) {
				value = defaultValue;
			}
		} catch (Throwable ex) {
			value = defaultValue;
			System.out.println("Key: " + name + " not found, set to default value: " + defaultValue);
		}
		return value;
	}

	public static String getProperty(String name) {
		return getProperty(name, null);
	}

	public static void main(String[] args) {
		System.out.println(ConfigUtils.getProperty("mm_ignore_columns"));
	}
}
