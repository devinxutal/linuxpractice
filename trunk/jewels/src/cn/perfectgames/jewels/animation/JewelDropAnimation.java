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

public class JewelDropAnimation extends AbstractAnimation {

	private Jewel[] jewels;
	private PointF[] initPos;
	private PointF[] destPos;

	private float upperBound;
	private int bitmapSize;
	
	private Paint paint;
	private Bitmap[] jewel_bitmaps;
	
	public JewelDropAnimation() {
		super(Constants.FPS/3);

		paint = new Paint();
		paint.setAntiAlias(true);
	}

	public void setJewelBitmap(Bitmap[] bitmaps){
		this.jewel_bitmaps = bitmaps;
		this.bitmapSize = bitmaps[0].getHeight();
	}
	
	public void setUpperBound(float bound){
		this.upperBound = bound;
	}

	public void setDropJewels(List<Jewel> jewels, List<PointF> initPositions, List<PointF> destPositions){
		this.jewels = jewels.toArray(new Jewel[0]);
		this.initPos = initPositions.toArray(new PointF[0]);
		this.destPos = destPositions.toArray(new PointF[0]);
	}
	
	protected void innerDraw(Canvas canvas, int current, int total) {
		
		if(jewels!= null){
			for(int i = 0; i<jewels.length; i++){
				float x = initPos[i].x + (destPos[i].x - initPos[i].x)*current/(total-1);
				float y = initPos[i].y + (destPos[i].y - initPos[i].y)*current/(total-1);
				draw(canvas, jewel_bitmaps[jewels[i].getType()],x, y);
			}
		}
	}
	
	protected void draw (Canvas canvas, Bitmap bitmap,float x, float y){
		if(y>=upperBound){
			canvas.drawBitmap(bitmap,x, y, paint);
		}else if(y<upperBound- bitmapSize){
			//do nothing
		}else{
			float h = y+bitmapSize - upperBound;
			
			canvas.drawBitmap(bitmap, new Rect(0, Math.round(bitmapSize - h), bitmapSize, bitmapSize),
									  new RectF(x, upperBound, x+bitmapSize, upperBound+h), paint);
		}
	}

}
