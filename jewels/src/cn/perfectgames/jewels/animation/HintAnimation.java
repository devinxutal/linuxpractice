package cn.perfectgames.jewels.animation;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import cn.perfectgames.amaze.animation.AbstractAnimation;
import cn.perfectgames.jewels.R;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.util.BitmapUtil;

public class HintAnimation extends AbstractAnimation {

	private List<ScoreEntry> scores = new LinkedList<ScoreEntry>();

	private int size = 0;
	private Bitmap spark1 = null;
	private Bitmap spark2 = null;
	private float x, y;

	private Matrix matrix = new Matrix();

	private Paint paint;

	public HintAnimation() {
		super((int)(1.5 * Constants.FPS));
		paint = new Paint();
		paint.setAntiAlias(true);

		spark1 = BitmapUtil.get().getBitmap(R.drawable.spark1);
		spark2 = BitmapUtil.get().getBitmap(R.drawable.spark2);

	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setLocation(float x, float y) {
		this.x = x ;
		this.y = y ;
	}

	public void setLocation(PointF point) {
		this.setLocation(point.x, point.y);
	}

	@Override
	protected void innerDraw(Canvas canvas, int current, int total) {
		int alpha = (255);

		float scale = 1;
		if (current < total / 4) {
			scale = scale * current / (total / 4);
		} else if (current > total / 2) {
			scale = scale * (total - current) / (total - total / 2);
		}
		float spin = (float) 180 * current / total;
		paint.setAlpha(alpha);
		Log.v("HintAnimation", "alpha, rotate, scale:" + alpha + " " + spin
				+ " " + scale);
		Log.v("HintAnimation", "position: " + x + "," + y);

		matrix.reset();
		matrix.postRotate(spin, spark1.getWidth() / 2f, spark1.getHeight() / 2f);
		matrix.postScale(scale, scale, spark1.getWidth() / 2f,
				spark1.getHeight() / 2f);
		matrix.postTranslate(x + (size - spark1.getWidth()) / 2f, y
				+ (size - spark1.getHeight()) / 2f);
		canvas.drawBitmap(spark1, matrix, paint);

		matrix.reset();
		matrix.postRotate(spin, spark2.getWidth() / 2f, spark2.getHeight() / 2f);
		matrix.postScale(scale, scale, spark2.getWidth() / 2f,
				spark2.getHeight() / 2f);
		matrix.postTranslate(x + (size - spark2.getWidth()) / 2f, y
				+ (size - spark2.getHeight()) / 2f);
		canvas.drawBitmap(spark2, matrix, paint);
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
