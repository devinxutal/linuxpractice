package cn.perfectgames.jewels.animation;

import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import cn.perfectgames.amaze.animation.AbstractAnimation;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.model.Jewel;

public class DripJewelsAnimation extends AbstractAnimation {
	public static final int EVENT_HALF_PASSED = 0;
	
	private int[] jewels;
	private PointF[] pos;
	private float speeds[][];
	private float shakes[][];
	private RectF[] rects;
	private Rect rect;
	
	
	private Paint paint;
	private Bitmap[] jewel_bitmaps;
	
	private int shakeFrameNum = 24;
	
	private  float g = 0.3f;
	private static final int CURVE_TIME = 800;
	public DripJewelsAnimation() {
		super(Constants.FPS*30/4);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
	}

	public void setJewelBitmap(Bitmap[] bitmaps){
		this.jewel_bitmaps = bitmaps;
		if(bitmaps!= null && bitmaps[0]!= null){
			float h = bitmaps[0].getHeight();
			Log.v("DripJewelsAnimation", "Jewel size: "+h);
			int frames = (int) (CURVE_TIME /(float)1000 * Constants.FPS);
			g = 6* h / (float)(frames * frames);
		}
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
		// init speed
		speeds = new float[this.jewels.length][2];
		Random r = new Random();
		float speedX = rect.width() * 4 /(2* (CURVE_TIME* Constants.FPS /1000));
		float speedY = g * (CURVE_TIME* Constants.FPS /1000);
		for(i =0; i<speeds.length; i++){
			speeds[i][0]  =  speedX* (r.nextFloat() - 0.5f);// speed x;
			speeds[i][1]  =  -speedY* (r.nextFloat()+1)/2;// speed y;
		}
		// init shakes
		shakes = new float[this.jewels.length][2];
		float shakeDistance = 0.1f;
		for(i=0; i<speeds.length; i++){
			float x = r.nextFloat()* shakeDistance;
			float y =(float) Math.sqrt(shakeDistance * shakeDistance - x*x);
			if(r.nextBoolean()){
				x = -x;
			}
			if(r.nextBoolean()){
				y = -y;
			}
			shakes[i][0] = x;
			shakes[i][1] = y;
		}
		// determine time
		int dripFrames = 2*(int)Math.round(Math.ceil(Math.sqrt(10 * rect.height()*2 / g)));
		this.setTotalFrame(this.shakeFrameNum+dripFrames);
	}
	
	protected void innerDraw(Canvas canvas, int current, int total) {
		if(current < this.shakeFrameNum){
			int frame = current % 4;
			if(jewels!= null){
				RectF destRect = new RectF();
				for(int i = 0; i<jewels.length; i++){
					RectF rect = rects[i];
					float R = rect.width();
					float shakeX = 0;
					float shakeY = 0;
					if(frame == 1){
						shakeX = shakes[i][0] * R;
						shakeY = shakes[i][1]* R;
					}else if(frame == 3){

						shakeX = -shakes[i][0]* R;
						shakeY = -shakes[i][1]*R;
					}
					destRect.left = rect.left+shakeX;
					destRect.right = rect.right +shakeX;
					destRect.top = rect.top+shakeY;
					destRect.bottom = rect.bottom +shakeY;
					canvas.drawBitmap(jewel_bitmaps[jewels[i]], this.rect, destRect, paint);
				}
			}
		}else{
			int frame = current - shakeFrameNum +1;
			if(jewels!= null){
				RectF destRect = new RectF();
				for(int i = 0; i<jewels.length; i++){
					RectF rect = rects[i];
					float deltaX = speeds[i][0] *  frame;
					float deltaY = speeds[i][1]* frame +0.5f * g * frame * frame;
					//speeds[i][0] += 0;
					//speeds[i][1] += g;
					
					destRect.left = rect.left+deltaX;
					destRect.right = rect.right +deltaX;
					destRect.top = rect.top+deltaY;
					destRect.bottom = rect.bottom +deltaY;
					canvas.drawBitmap(jewel_bitmaps[jewels[i]], this.rect, destRect, paint);
				}
			}
		}
	}

	
	protected void onStep(int current, int total) {
		if(current == total/2){
			this.notifyAnimationEventHappened(EVENT_HALF_PASSED);
		}
	}

}
