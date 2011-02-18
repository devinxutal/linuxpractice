package com.devinxutal.man20.sound;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.devinxutal.man20.cfg.Configuration;

public class SoundManager {
	private static final String TAG = "SoundManager";
	private SoundPool soundpool;
	private Activity activity;
	private MediaPlayer mediaPlayer;
	private AudioManager audioManager;
	private int btnMusicID = -1;
	private int crashMusicID = -1;

	public static boolean initialized = false;
	public static SoundManager soundManager = null;

	private float BACKGROUND_SOUND_VOLUME = 0.15f;

	public synchronized static SoundManager get(Activity activity) {
		if (soundManager == null) {
			init(activity);
		}
		return soundManager;
	}

	public synchronized static void init(Activity activity) {
		Log.v(TAG, "initialize");
		if (!initialized) {
			initialized = true;
			soundManager = new SoundManager(activity);
		}
	}

	public synchronized static void release() {
		Log.v(TAG, "release");
		if (soundManager != null) {
			soundManager.internalRelease();
			soundManager = null;
		}
		initialized = false;
	}

	private SoundManager(Activity activity) {
		soundpool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		mediaPlayer = new MediaPlayer();
		audioManager = (AudioManager) activity
				.getSystemService(Context.AUDIO_SERVICE);
		try {
			mediaPlayer.reset();
			mediaPlayer.setLooping(true);
			mediaPlayer.setVolume(BACKGROUND_SOUND_VOLUME,
					BACKGROUND_SOUND_VOLUME);

			mediaPlayer.setDataSource(activity.getAssets().openFd(
					"sounds/bg.mp3").getFileDescriptor());
			mediaPlayer.prepare();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Log.v(TAG, "soundpool null?" + (soundpool == null));
		try {

			this.btnMusicID = soundpool.load(activity.getAssets().openFd(
					"sounds/buttonclick.mp3"), 1);

			this.crashMusicID = soundpool.load(activity.getAssets().openFd(
					"sounds/crash.mp3"), 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.activity = activity;
	}

	public void playBackgroundMusic() {
		if (!Configuration.config().isSoundEffectsOn()) {
			return;
		}
		Log.v(TAG, "play background music");
		if (!mediaPlayer.isPlaying()) {
			Log.v(TAG, "really play background music");
			float streamVolume = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			streamVolume = BACKGROUND_SOUND_VOLUME
					* streamVolume
					/ audioManager
							.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			mediaPlayer.setVolume(streamVolume, streamVolume);
			mediaPlayer.start();
		}
	}

	public void stopBackgroundMusic() {
		Log.v(TAG, "stop background music");
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			try {
				mediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void pauseBackgroundMusic() {
		Log.v(TAG, "pause background music");
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}

	}

	public void playButtonClickEffect() {

		if (!Configuration.config().isSoundEffectsOn()) {
			return;
		}
		if (soundpool == null) {
			return;
		}
		float streamVolume = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume
				/ audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (btnMusicID >= 0) {
			soundpool.play(btnMusicID, streamVolume, streamVolume, 1, 0, 1);
		}
	}

	public void playCrashEffect() {

		if (!Configuration.config().isSoundEffectsOn()) {
			return;
		}
		if (soundpool == null) {
			return;
		}
		float streamVolume = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume
				/ audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (crashMusicID >= 0) {
			soundpool.play(crashMusicID, streamVolume, streamVolume, 1, 0, 1);
		}
	}

	private void internalRelease() {
		if (soundpool != null) {
			soundpool.release();
			soundpool = null;
		}
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.release();
		}
	}
}
