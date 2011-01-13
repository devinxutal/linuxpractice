package com.devinxutal.fmc.cfg;

import android.content.SharedPreferences;

public class Configuration {
	private static Configuration config = new Configuration();

	private SharedPreferences preference;

	public static Configuration config() {
		return config;
	}

	private Configuration() {

	}

	public void setSharedPreferences(SharedPreferences p) {
		this.preference = p;
	}

	public int getCubeSize() {
		int defaultValue = 3;
		if (preference != null) {
			return Integer.valueOf(preference.getString("cube_size", "3"));
		}
		return defaultValue;
	}

	public int getRotationInterval() {
		int defaultValue = 500;
		if (preference != null) {
			return Integer.valueOf(preference.getString("rotation_interval",
					"500"));
		}
		return defaultValue;
	}

	public int getAnimationSpeed() {
		int defaultValue = 400;
		if (preference != null) {
			return Integer.valueOf(preference.getString("animation_speed",
					"400"));
		}
		return defaultValue;
	}

	public int getAnimationQuality() {
		int defaultValue = 2;
		if (preference != null) {
			return Integer.valueOf(preference.getString("animation_quality",
					"2"));
		}
		return defaultValue;
	}
}
