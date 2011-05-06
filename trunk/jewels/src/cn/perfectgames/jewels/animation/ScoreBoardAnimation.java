package cn.perfectgames.jewels.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import cn.perfectgames.amaze.animation.AbstractAnimation;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.util.BitmapUtil;

public class ScoreBoardAnimation extends AbstractAnimation {

	public static final int FRAMES_PER_FLIP = Constants.FPS*300 /1000 ;
	private int digitNUM;
	private Bitmap[] digits;
	private Rect rect;

	private Paint paint;

	private int score;

	private int[] froms;
	private int[] tos;

	public ScoreBoardAnimation(int digitNum) {
		super(Constants.FPS*5, true);
		this.digitNUM = digitNum;
		froms = new int[digitNum];
		tos = new int[digitNum];
		digits = new Bitmap[digitNUM];
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	public void setRect(Rect rect) {
		if (digits != null) {
			for (Bitmap b : digits) {
				if (b != null) {
					b.recycle();
				}
			}
		}
		digits = new Bitmap[10];
		for (int i = 0; i <= 9; i++) {
			digits[i] = BitmapUtil.get().getBitmap(
					"images/digits/style1/" + i + ".png");
		}

		int w = digits[0].getWidth();
		int h = digits[0].getHeight();

		int aw = rect.width() / digitNUM;
		int ah = rect.height();
		double scale = Math.min(aw / (double) w, ah / (double) h);
		aw = (int) (scale * w);
		ah = (int) (scale * h);
		this.rect = new Rect(rect.left + (rect.width() - digitNUM* aw) / 2, rect.top
				+ (rect.height() - ah) / 2, rect.left
				+ (rect.width() + digitNUM * aw) / 2, rect.top + (rect.height() + ah)
				/ 2);

		for (int i = 0; i <= 9; i++) {
			Bitmap tmp = digits[i];
			digits[i] = Bitmap.createScaledBitmap(tmp, aw, ah, true);
			tmp.recycle();
		}
	}

	@Override
	protected void innerDraw(Canvas canvas, int current, int total) {
		int ALPHA_TRANSPARENT = 100;
		int ALPHA_SOLID = 255;
		if (current < 0) {
			// draw static score
			int sc = this.score;
			for (int i = digitNUM - 1; i >= 0; i--) {
				int d = sc % 10;
				if (i != digitNUM - 1 && sc == 0) {
					paint.setAlpha(ALPHA_TRANSPARENT);
				} else {
					paint.setAlpha(ALPHA_SOLID);
				}
				canvas.drawBitmap(digits[d], rect.left + rect.width() * i
						/ digitNUM, rect.top, paint);
				sc /= 10;
			}
		} else {
			// draw animated score
			canvas.clipRect(rect);
			
			int sc = this.score;
			for (int i = digitNUM - 1; i >= 0; i--) {
				int alpha = ALPHA_SOLID;
				int roll = current % FRAMES_PER_FLIP;
				int fixDigit = -1;
				if (froms[i] == tos[i]) {
					roll = 0;
					if (sc == 0) {
						alpha = ALPHA_TRANSPARENT;
					}
					fixDigit = tos[i];
				} else {
					int steps = froms[i] < tos[i] ? tos[i] - froms[i] : tos[i]
							- froms[i] + 10;
					if (current / FRAMES_PER_FLIP > steps -1 ) {
						fixDigit = tos[i];
						roll = 0;
					}
				}

				// draw
				paint.setAlpha(alpha);
				if (roll == 0) {
					Bitmap bm = null;
					if (fixDigit >= 0) {
						bm = digits[fixDigit];
					} else {
						bm = digits[(froms[i] + current / FRAMES_PER_FLIP) % 9];
					}
					canvas.drawBitmap(bm, rect.left + rect.width() * i
							/ digitNUM, rect.top, paint);
				} else {
					Bitmap up = digits[(froms[i] + current / FRAMES_PER_FLIP) % 9];
					Bitmap down = digits[(froms[i] + current / FRAMES_PER_FLIP + 1) % 9];
					float move = current % FRAMES_PER_FLIP
							/ (float) FRAMES_PER_FLIP * rect.height();
					canvas.drawBitmap(up, rect.left + rect.width() * i
							/ digitNUM, rect.top - move, paint);
					canvas.drawBitmap(down, rect.left + rect.width() * i
							/ digitNUM, rect.bottom - move, paint);
				}
				
				sc /= 10;
			}
			canvas.restore();
		}
	}

	public void setNewScore(int score) {
		int old = this.score;
		int neww = score;
		for (int i = digitNUM - 1; i >= 0; i--) {
			froms[i] = old % 10;
			tos[i] = neww % 10;
			old /=10;
			neww /= 10;
		}
		this.score = score;
	}

}
