package com.devinxutal.tetris.cfg;

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

	public static int CONTROL_JOY_STICK = 1;
	public static int CONTROL_SENSOR = 2;
	public static int CONTROL_BOTH = 3;

	public int getControl() {
		int defaultValue = CONTROL_SENSOR;
		if (preference != null) {
			return Integer.valueOf(preference.getString("control_manner", "2"));
		}
		return defaultValue;
	}

	public static int JOT_STICK_POSITION_LEFT = 2;
	public static int JOT_STICK_POSITION_RIGHT = 1;

	public int getJoyStickPosition() {
		int defaultValue = JOT_STICK_POSITION_RIGHT;
		if (preference != null) {
			return Integer.valueOf(preference.getString("joystick_position",
					"1"));
		}
		return defaultValue;
	}

	public boolean isBackgroundMusicOn() {
		if (preference != null) {
			return preference.getBoolean("background_music", false);
		}
		return false;
	}

	public boolean isSoundEffectsOn() {
		if (preference != null) {
			return preference.getBoolean("sound_effects", false);
		}
		return false;
	}

	public static final int DIFFICULTY_EASY = 1;
	public static final int DIFFICULTY_STANDARD = 2;
	public static final int DIFFICULTY_HARD = 3;

	public int getDifficulty() {
		int defaultValue = 2;
		if (preference != null) {
			return Integer.valueOf(preference.getString("difficulty", "2"));
		}
		return defaultValue;
	}

	public int getShuffleSteps() {
		int defaultValue = 20;

		if (preference != null) {
			return Integer.valueOf(preference.getString("shuffle_steps", "20"));
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

	public String getScreenOrientation() {
		String defaultValue = "auto";
		if (preference != null) {
			return preference.getString("screen_orientation", defaultValue);
		}
		return defaultValue;
	}

}
