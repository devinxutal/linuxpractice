package cn.perfectgames.jewels.animation;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import cn.perfectgames.amaze.animation.AbstractAnimation;
import cn.perfectgames.jewels.R;
import cn.perfectgames.jewels.util.BitmapUtil;

public class SelectionAnimation extends AbstractAnimation {

	private List<ScoreEntry> scores = new LinkedList<ScoreEntry>();

	private int size = 0;
	private Bitmap selector = null;
	private float x, y;

	private Paint paint;

	public SelectionAnimation() {
		super(20);
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	public void setSize(int size) {
		if (selector == null || this.size != size) {
			Bitmap background = BitmapUtil.get().getBitmap(R.drawable.selector);

			if (selector != null) {
				selector.recycle();
			}
			selector = Bitmap.createScaledBitmap(background, size, size, true);
			background.recycle();
		}
	}

	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	protected void innerDraw(Canvas canvas) {
		double total = getFrameCount();
		double current = getCurrentFrame();
		Log.v("SelectionAnimation", "selection draw: " + current + "/" + total);
		int alpha = (int) (255 * Math.abs((total / 2 - current)) / total * 2);
		paint.setAlpha(alpha);
		canvas.drawBitmap(selector, x, y, paint);
	}

	public void addScore(String score, PointF position, int color) {
		this.scores.add(new ScoreEntry(score, position, color));
	}

	public void clearScores() {
		this.scores.clear();
	}

	public class ScoreEntry {
		public String score;
		public PointF position;
		public int color;

		public ScoreEntry(String score, PointF position, int color) {
			super();
			this.score = score;
			this.position = position;
			this.color = color;
		}

	}

}
