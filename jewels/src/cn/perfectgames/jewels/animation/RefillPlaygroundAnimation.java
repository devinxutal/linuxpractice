package cn.perfectgames.jewels.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import cn.perfectgames.amaze.animation.AbstractAnimation;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.model.Jewel;

public class RefillPlaygroundAnimation extends AbstractAnimation {
	public static final int EVENT_HALF_PASSED = 0;

	private int row;
	private int col;
	private PointF pos;

	private int size;

	private Paint paint;
	private Bitmap[] jewel_bitmaps;

	private Jewel[][] froms;
	private Jewel[][] tos;

	public RefillPlaygroundAnimation(int rows, int cols) {
		super(Constants.FPS * 3 / 2);
		this.row = rows;
		this.col = cols;

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);

		froms = new Jewel[row][col];
		tos = new Jewel[row][col];
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				froms[r][c] = new Jewel();
				tos[r][c] = new Jewel();
			}
		}
	}

	public void setJewelBitmap(Bitmap[] bitmaps) {
		this.jewel_bitmaps = bitmaps;
		this.size = bitmaps[0].getHeight();
	}

	public void setPlaygroundRefill(Jewel[][] from, Jewel[][] to,
			PointF topleft, int size) {
		this.setOriginalJewels(froms);
		this.setRefilledJewels(tos);
		this.setTopLeftPosition(topleft);
		this.setSize(size);
	}

	public void setOriginalJewels(Jewel[][] originals) {
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				froms[r][c].copy(originals[r][c]);
			}
		}
	}

	public void setRefilledJewels(Jewel[][] refills) {
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				tos[r][c].copy(refills[r][c]);
			}
		}
	}

	public void setTopLeftPosition(PointF pos) {
		this.pos = pos;
	}

	public void setSize(int size) {
		this.size = size;
	}

	protected void innerDraw(Canvas canvas, int current, int total) {
		Jewel[][] jewels = froms;
		if(current >= total/2){
			jewels = tos;
			current = total - current - 1;
		}
		total = total/2;
		
		float initSize = size;
		float maxSize = size * 1.1f;
		float maxSizeIndex = total / 2;
		int alpha = 0;
		float delta = 0;
		if (current < maxSizeIndex) {
			alpha = 255;
			delta = -(maxSize - initSize) * current / maxSizeIndex;
		} else {
			alpha = (int) (255 * (total - current) / (total - maxSizeIndex));
			float destSize = size * (total - current) / (total - maxSizeIndex);
			delta = (initSize - destSize) / 2;
		}
		paint.setAlpha(alpha);
		Rect rect = new Rect(0,0, size, size);
		RectF destRect = new RectF();
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				Jewel jewel = jewels[r][c];
				destRect.left = pos.x + size*c + delta;
				destRect.right = pos.x + size*(c+1) - delta;
				destRect.top = pos.y + size*r + delta;
				destRect.bottom = pos.y + size*(r+1) - delta;
				canvas.drawBitmap(jewel_bitmaps[jewel.getType()], rect,
						destRect, paint);
			}
		}

	}

	protected void onStep(int current, int total) {
	}

}
