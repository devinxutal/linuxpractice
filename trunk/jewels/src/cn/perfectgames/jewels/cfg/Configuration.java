package cn.perfectgames.jewels.cfg;

import java.util.Locale;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

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


	public String getJewelStyle() {
		String defaultValue = "classic";
		if (preference != null) {
			return preference.getString("jewel_style", defaultValue);
		}
		return defaultValue;
	}

	public int getHintDelay(){
		int defaultValue = 10000;
		if (preference != null) {
			return Integer.valueOf(preference.getString("hint_delay", defaultValue+""));
		}
		return defaultValue;
	}
	public boolean isBackgroundMusicOn() {
		if (preference != null) {
			return preference.getBoolean("background_music", true);
		}
		return true;
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
			return preference.getBoolean("sound_effects", true);
		}
		return true;
	}

	public void setSoundEffectsOn(boolean on) {
		if (preference != null) {
			Editor editor = preference.edit();
			editor.putBoolean("sound_effects", on);
			editor.commit();
		}
	}

	public Locale getLanguage() {

		Locale l = Locale.getDefault();
		if (preference != null) {
			String locale = preference.getString("language", "default");
			Log.v("configuration", locale);
			if (locale.equals("zh")) {
				l = Locale.SIMPLIFIED_CHINESE;
			} else if (locale.equals("en")) {
				l = Locale.ENGLISH;

			} 
		}

		Log.v("configuration", "finally get : "+ l);
		return l;
	}
}
