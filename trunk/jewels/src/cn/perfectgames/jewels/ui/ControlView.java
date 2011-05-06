package cn.perfectgames.jewels.ui;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import cn.perfectgames.jewels.R;
import cn.perfectgames.jewels.cfg.Configuration;
import cn.perfectgames.jewels.control.ButtonInfo;
import cn.perfectgames.jewels.control.GameController;
import cn.perfectgames.jewels.sound.SoundManager;
import cn.perfectgames.jewels.util.MathUtil;

public class ControlView extends LinearLayout implements OnTouchListener,
		OnClickListener {
	private GameController controller;
	private List<GameControlListener> listeners = new LinkedList<GameControlListener>();


	public static final int BTN_PAUSE = 3400;
	public static final int BTN_SOUND = 3401;
	public static final int BTN_MUSIC = 3402;

	private static final String TAG = "GameControlView";
	public static float SLIDE_THRESHOLD = 10;

	private List<ButtonInfo> buttons = new LinkedList<ButtonInfo>();

	private List<ImageButton> controlButtons = new LinkedList<ImageButton>();
	private ImageButton soundButton;
	private ImageButton musicButton;
	private ImageButton pauseButton;

	private Configuration config;
	public ControlView(Context context) {
		super(context);
		init();
		this.config = Configuration.config();
		this.resetControlButtons();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		SLIDE_THRESHOLD = Math.min(w, h) / 50;
		super.onSizeChanged(w, h, oldw, oldh);

	}

	public void setButtons(List<ButtonInfo> buttons) {
		this.buttons.clear();
		this.buttons.addAll(buttons);
	}

	public void setGameController(GameController controller) {
		this.controller = controller;
	}

	private void init() {
		this.soundButton = makeButton(BTN_SOUND, R.drawable.icon_sound_on);
		this.musicButton = makeButton(BTN_MUSIC, R.drawable.icon_music_on);
		this.pauseButton = makeButton(BTN_PAUSE, R.drawable.icon_pause);
		controlButtons.add(soundButton);
		controlButtons.add(musicButton);
		
		this.addView(pauseButton);
		for (ImageButton b : controlButtons) {
			this.addView(b);
		}
		this.setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	private float oldXForPlayground = -1;
	private float oldYForPlayground = -1;
	public boolean onTouch(View arg0, MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
			float x = event.getX();
			float y = event.getY();

			//for playground
			oldXForPlayground = x;
			oldYForPlayground = y;
			if(this.controller != null){
				controller.getPlayground().touch(x, y);
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			
			float x = event.getX();
			float y = event.getY();
		
			//for playground
			if(this.controller != null){
				controller.getPlayground().flip(x - oldXForPlayground, y - oldYForPlayground);
			}
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			
		}
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!changed) {
			return;
		}

		soundButton.measure(r - l, b - t);
		int width = r - l;
		int height = b - t;
		int len = width > height ? height : width;
		int btn_len = soundButton.getMeasuredWidth();
		int marginH = 5;
		int marginV = 0;
		int padding = 5;
		if (Math.min(width, height) < 300) {
			marginV = padding = 1;
		}
		int index = -1;
		
		// layout pause button
		pauseButton.layout(marginH,marginV,marginH+btn_len, marginV+btn_len);
		// layout right aligned buttons
		for (ImageButton btn : controlButtons) {
			index++;
			btn.layout(
					width - (marginH + index * (padding + btn_len) + btn_len),
					marginV, width - (marginH + index * (padding + btn_len)),
					marginV + btn_len);

		}
	}

	private ImageButton makeButton(int id, int resid) {
		ImageButton b = new ImageButton(getContext());
		b.setFocusable(false);
		b.setId(id);
		b.setBackgroundResource(R.drawable.transparent_button);
		b.setImageResource(resid);
		b.setOnClickListener(this);
		return b;
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case BTN_SOUND:
			config.setSoundEffectsOn(!config.isSoundEffectsOn());

			resetControlButtons();
			break;
		case BTN_MUSIC:
			boolean soundon = !config.isBackgroundMusicOn();
			config.setBackgroundMusicOn(soundon);
			resetControlButtons();
			if (!soundon) {
				SoundManager.get((Activity) this.getContext())
						.stopBackgroundMusic();
			} else {
				SoundManager.get((Activity) this.getContext())
						.playBackgroundMusic();
			}
			break;
		default:
			notifyButtonClicked(view.getId());
		}
	}

	public void resetControlButtons() {
		if (config.isBackgroundMusicOn()) {
			musicButton.setImageResource(R.drawable.icon_music_on);
		} else {
			musicButton.setImageResource(R.drawable.icon_music_off);
		}
		if (config.isSoundEffectsOn()) {
			soundButton.setImageResource(R.drawable.icon_sound_on);
		} else {
			soundButton.setImageResource(R.drawable.icon_sound_off);
		}
		this.invalidate();
	}

	public interface GameControlListener {
		void buttonClickced(int id);

		void buttonPressed(int id);

		void buttonReleased(int id);
	}

	public boolean addGameControlListener(GameControlListener l) {
		return listeners.add(l);
	}

	public boolean removeGameControlListener(GameControlListener l) {
		return listeners.remove(l);
	}

	public void clearGameControlListener() {
		listeners.clear();
	}

	public void notifyButtonClicked(int id) {
		for (GameControlListener l : listeners) {
			l.buttonClickced(id);
		}
	}

	public void notifyButtonPressed(int id) {
		for (GameControlListener l : listeners) {
			l.buttonPressed(id);
		}
	}

	public void notifyButtonReleased(int id) {
		for (GameControlListener l : listeners) {
			l.buttonReleased(id);
		}
	}

	public void configurationChanged(Configuration config) {
		this.resetControlButtons();
	}

}
