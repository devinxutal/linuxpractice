package com.devinxutal.tetris.cfg;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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

	public static final int POSITION_RIGHT = 2;
	public static final int POSITION_LEFT = 1;

	public int getDirectDownButtonPosition() {
		int defaultValue = POSITION_LEFT;
		if (preference != null) {
			return Integer.valueOf(preference.getString("direct_down_position",
					"1"));
		}
		return defaultValue;
	}

	public static final int ACTION_DIRECT_DOWN = 1;
	public static final int ACTION_QUICK_DOWN = 2;
	public static final int ACTION_TURN = 3;

	public int getCenterButtonAction() {
		int defaultValue = ACTION_DIRECT_DOWN;
		if (preference != null) {
			return Integer.valueOf(preference.getString("center_button_action",
					"1"));
		}
		return defaultValue;
	}

	public String getBlockStyle() {
		String defaultValue = "classic";
		if (preference != null) {
			return preference.getString("block_style", defaultValue);
		}
		return defaultValue;
	}

	public boolean isBackgroundMusicOn() {
		if (preference != null) {
			return preference.getBoolean("background_music", false);
		}
		return false;
	}

	public void setBackgroundMusicOn(boolean on) {
		if (preference != null) {
			Editor editor = preference.edit();
			editor.putBoolean("background_music", on);
			editor.commit();
		}
	}

	public boolean isSoundEffectsOn() {
		if (preference != null) {
			return preference.getBoolean("sound_effects", false);
		}
		return false;
	}

	public void setSoundEffectsOn(boolean on) {
		if (preference != null) {
			Editor editor = preference.edit();
			editor.putBoolean("sound_effects", on);
			editor.commit();
		}
	}

	public int getStartLevel() {
		int defaultValue = 1;
		if (preference != null) {
			return Integer.valueOf(preference.getString("difficulty", "1"));
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
