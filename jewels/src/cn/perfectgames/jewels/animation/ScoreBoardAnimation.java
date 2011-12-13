package cn.perfectgames.jewels.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import cn.perfectgames.amaze.animation.AbstractAnimation;
import cn.perfectgames.jewels.GoJewelsApplication;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.util.BitmapUtil;

public class ScoreBoardAnimation extends AbstractAnimation {

	public static final int FRAMES_PER_FLIP = Constants.FPS*400 /1000 ;
	private int digitNUM;
	private Bitmap[] digits;
	private Rect rect;

	private Paint paint;

	private int score;

	private int[] froms;
	private int[] tos;

	private int asiFrom =0; // alpha start index
	private int asiTo = 0;	// alpha start index
	public ScoreBoardAnimation(int digitNum) {
		super(FRAMES_PER_FLIP*2, true);
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
			digits[i] = GoJewelsApplication.getBitmapUtil().getBitmap(
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

			Rect bitmapRect = new Rect(0, 0, digits[0].getWidth(), digits[0].getHeight());
			RectF toRect = new RectF();
			for(int i = 0; i< froms.length; i++){
				int alpha = ALPHA_SOLID;
				Bitmap digit = digits[froms[i]];
				int asi = asiFrom;
				int ii = current;
				if( current >= FRAMES_PER_FLIP){
					ii = FRAMES_PER_FLIP*2 - current;
					asi = asiTo;
					digit = digits[tos[i]];
				}
				if(i<asi){
					alpha = ALPHA_TRANSPARENT;
				}
				paint.setAlpha(alpha);
				if(froms[i]  == tos[i]){
					canvas.drawBitmap(digit, rect.left + rect.width() * i
							/ digitNUM, rect.top, paint);
				}else{
					float delta =  ii * digit.getWidth() / FRAMES_PER_FLIP /2;
					toRect.left = rect.left + rect.width() * i
					/ digitNUM+ delta;
					toRect.right = toRect.left + digit.getWidth() - 2 * delta;
					toRect.top = rect.top;
					toRect.bottom = rect.top+ digit.getHeight();
					canvas.drawBitmap(digit, bitmapRect, toRect, paint);
				}
				
			}
		}
	}

	public void setNewScore(int score) {
		int old = this.score;
		int neww = score;
		asiFrom = asiTo = -1;
		for (int i = digitNUM - 1; i >= 0; i--) {
			froms[i] = old % 10;
			tos[i] = neww % 10;
			old /=10;
			neww /= 10;
			if(old == 0 && asiFrom <0){ 
				asiFrom = i;
			}
			if(neww == 0 && asiTo <0){
				asiTo = i;
			}
		}
		this.score = score;
	}

}
