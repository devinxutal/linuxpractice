package cn.perfectgames.jewels.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import cn.perfectgames.amaze.animation.AbstractAnimation;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.model.Jewel;

public class SwapAnimation extends AbstractAnimation {

	private Jewel jewel1;
	private PointF loc1;
	private Jewel jewel2;
	private PointF loc2;
	boolean swapBack;
	
	

	private Paint paint;
	private Bitmap[] jewel_bitmaps;
	
	private static final int BASIC_FRAMES = Constants.FPS*3/10;
	public SwapAnimation() {
		super(Constants.FPS * 6/10);
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	@Override
	protected void innerDraw(Canvas canvas, int current, int total) {
		int index = current <BASIC_FRAMES?current: 2*BASIC_FRAMES - current;
		float x1 = loc1.x + (loc2.x - loc1.x)*index/BASIC_FRAMES;
		float y1 = loc1.y + (loc2.y- loc1.y)*index/BASIC_FRAMES;

		float x2 = loc2.x + (loc1.x - loc2.x)*index/BASIC_FRAMES;
		float y2 = loc2.y + (loc1.y- loc2.y)*index/BASIC_FRAMES;
		if(current> BASIC_FRAMES){
			canvas.drawBitmap(jewel_bitmaps[jewel2.getType()], x2, y2, paint);
			canvas.drawBitmap(jewel_bitmaps[jewel1.getType()], x1, y1, paint);
		}else{
			canvas.drawBitmap(jewel_bitmaps[jewel1.getType()], x1, y1, paint);
			canvas.drawBitmap(jewel_bitmaps[jewel2.getType()], x2, y2, paint);
			
		}
	}
	public void setJewelBitmap(Bitmap[] bitmaps){
		this.jewel_bitmaps = bitmaps;
	}
	
	public void setSwap(Jewel jewel1, PointF loc1,
			Jewel jewel2, PointF loc2, boolean swapBack) {
		
		this.jewel1 = jewel1;
		this.loc1 = loc1;
		this.jewel2 = jewel2;
		this.loc2 = loc2;
		this.swapBack = swapBack;
		if(swapBack){
			this.changeTotalFrames(BASIC_FRAMES*2+1);
			
		}else{
			this.changeTotalFrames(BASIC_FRAMES+1);
		}
	}


}
