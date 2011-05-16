package cn.perfectgames.jewels.sound;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import cn.perfectgames.jewels.R;
import cn.perfectgames.jewels.cfg.Configuration;


public class SoundManager {
	public static final int STYLE_JEWEL =0;
	public static final int STYLE_ANIMAL = 1;
	
	private int style = 0;
	
	private static final String TAG = "SoundManager";
	private SoundPool soundpool;
	private Activity activity;
	private MediaPlayer mediaPlayer;
	private AudioManager audioManager;
	private int btnMusicID = -1;
	private int turnMusicID = -1;
	private int moveMusicID = -1;
	private int downMusicID = -1;
	private int eliminationMusicID = -1;
	
	private int[][] eliminationSounds = new int[2][7];
	private int[] illigalSwapSounds = new int[2];
	private int[] dropSounds = new int[2];

	private float streamVolume = 1;

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
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		audioManager = (AudioManager) activity
				.getSystemService(Context.AUDIO_SERVICE);
		try {
			mediaPlayer.reset();
			mediaPlayer.setLooping(true);
			AssetFileDescriptor fd = activity.getAssets().openFd(
					"sounds/tetris.mp3");
			mediaPlayer.setDataSource(fd.getFileDescriptor(), fd
					.getStartOffset(), fd.getLength());
			mediaPlayer.prepare();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Log.v(TAG, "soundpool null?" + (soundpool == null));
		try {

			this.btnMusicID = soundpool.load(activity.getAssets().openFd(
					"sounds/buttonclick.mp3"), 1);

			this.moveMusicID = soundpool.load(activity, R.raw.move, 10);
			this.turnMusicID = soundpool.load(activity, R.raw.turn, 10);
			this.downMusicID = soundpool.load(activity, R.raw.down, 10);
			this.eliminationMusicID = soundpool.load(activity,
					R.raw.elimination, 10);
			
			this.dropSounds[0] = this.dropSounds[1] = soundpool.load(activity, R.raw.drop_jewel,10);

			this.illigalSwapSounds[0]= this.illigalSwapSounds[1] = soundpool.load(activity, R.raw.illegal_swap,10);
			
			int tmpId = soundpool.load(activity,R.raw.remove,10);
			for(int i =0; i<2; i++){
				for(int j = 0; j<7; j++){
					eliminationSounds[i][j] = tmpId;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.activity = activity;

		streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume
				/ audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	public void playBackgroundMusic() {
		Log.v(TAG, "play background music 1");
		if (!Configuration.config().isBackgroundMusicOn()) {
			return;
		}
		Log.v(TAG, "play background music 2");
		if (!mediaPlayer.isPlaying()) {
			Log.v(TAG, "really play background music");
			mediaPlayer.setVolume(streamVolume, streamVolume);
			mediaPlayer.setVolume(1, 1);
			Log.v(TAG, "really play background music");
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

	public void playMoveEffect() {
		if (!Configuration.config().isSoundEffectsOn()) {
			return;
		}
		if (soundpool == null) {
			return;
		}
		if (moveMusicID >= 0) {
			soundpool.play(moveMusicID, streamVolume, streamVolume, 1, 0, 1);
		}
	}

	public void playTurnEffect() {
		if (!Configuration.config().isSoundEffectsOn()) {
			return;
		}
		if (soundpool == null) {
			return;
		}
		if (turnMusicID >= 0) {
			soundpool.play(turnMusicID, streamVolume, streamVolume, 1, 0, 1);
		}
	}

	public void playDownEffect() {
		if (!Configuration.config().isSoundEffectsOn()) {
			return;
		}
		if (soundpool == null) {
			return;
		}
		if (downMusicID >= 0) {
			soundpool.play(downMusicID, streamVolume, streamVolume, 1, 0, 1);
		}
	}

	public void playEliminationEffect() {
		if (!Configuration.config().isSoundEffectsOn()) {
			return;
		}
		if (soundpool == null) {
			return;
		}
		if (eliminationMusicID >= 0) {
			soundpool.play(eliminationMusicID, streamVolume, streamVolume, 1,
					0, 1);
		}
	}
	
	public void playIllegalSwapEffect(){
		if(!Configuration.config().isSoundEffectsOn()){
			return;
		}
		if(soundpool == null){
			return;
		}
		soundpool.play(this.illigalSwapSounds[style], streamVolume, streamVolume, 1,0,1);
	}

	public void playDropEffect(){
		if(!Configuration.config().isSoundEffectsOn()){
			return;
		}
		if(soundpool == null){
			return;
		}
		soundpool.play(this.dropSounds[style], streamVolume, streamVolume, 1,0,1);
	}


	public void playEliminationEffect(int type){
		if(!Configuration.config().isSoundEffectsOn()){
			return;
		}
		if(soundpool == null){
			return;
		}
		soundpool.play(this.eliminationSounds[style][type], streamVolume, streamVolume, 1,0,1);
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
