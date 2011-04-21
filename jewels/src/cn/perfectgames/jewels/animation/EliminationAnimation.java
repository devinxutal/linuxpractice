package cn.perfectgames.jewels.animation;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import cn.perfectgames.amaze.animation.AbstractAnimation;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.model.Jewel;

public class EliminationAnimation extends AbstractAnimation {
	public static final int EVENT_HALF_PASSED = 0;
	
	private int[] jewels;
	private PointF[] pos;
	private RectF[] rects;
	private Rect rect;
	
	private int bitmapSize;
	
	private Paint paint;
	private Bitmap[] jewel_bitmaps;
	
	public EliminationAnimation() {
		super(Constants.FPS*3/4);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
	}

	public void setJewelBitmap(Bitmap[] bitmaps){
		this.jewel_bitmaps = bitmaps;
		this.bitmapSize = bitmaps[0].getHeight();
	}


	public void setJewels(List<Jewel> jewels, List<PointF> positions){
		this.jewels = new int[jewels.size()];
		int i = 0;
		for(Jewel j: jewels){
			this.jewels[i++] = j.getType();
		}
		this.pos = positions.toArray(new PointF[0]);
		
		//
		rect = new Rect(0,0,jewel_bitmaps[0].getWidth(), jewel_bitmaps[0].getHeight() );
		//
		rects = new RectF[this.jewels.length];
		for(i = 0; i<this.jewels.length; i++){
			rects[i] = new RectF(pos[i].x, pos[i].y, pos[i].x+rect.width(), pos[i].y+rect.height());
		}
	}
	
	protected void innerDraw(Canvas canvas, int current, int total) {


		float initSize = bitmapSize;
		float maxSize = bitmapSize * 1.15f;
		float maxSizeIndex = total/2;
		RectF destRect = new RectF();
		int alpha =0;
		float delta = 0;
		if(current <maxSizeIndex){
			alpha = 255;
			delta = -(maxSize -initSize)*current/maxSizeIndex;
		}else{
			alpha = (int)(255*(total -current)/(total- maxSizeIndex));
			float destSize = bitmapSize* (total - current)/(total-maxSizeIndex);
			delta = (initSize-destSize)/2;
		}
		paint.setAlpha(alpha);
		if(jewels!= null){
			for(int i = 0; i<jewels.length; i++){
				RectF rect = rects[i];
				destRect.left = rect.left+delta;
				destRect.right = rect.right -delta;
				destRect.top = rect.top+delta;
				destRect.bottom = rect.bottom - delta;
				canvas.drawBitmap(jewel_bitmaps[jewels[i]], this.rect, destRect, paint);
			}
		}
	}

	
	protected void onStep(int current, int total) {
		if(current == total/2){
			this.notifyAnimationEventHappened(EVENT_HALF_PASSED);
		}
	}
	
}
