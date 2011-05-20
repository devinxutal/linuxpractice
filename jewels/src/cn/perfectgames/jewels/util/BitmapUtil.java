package cn.perfectgames.jewels.util;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import cn.perfectgames.jewels.R;

public class BitmapUtil {
	public static final String TAG = "BitmapUtil";
	private Context context;
	private Bitmap background;
	private Bitmap aimButton1;
	private Bitmap aimButton2;
	
	private Paint paint;

	private Bitmap screen;

	private static BitmapUtil util;

	public synchronized static BitmapUtil get(Context context) {
		if (util == null) {
			util = new BitmapUtil(context);
		}
		return util;
	}

	public synchronized static BitmapUtil get() {
		return util;
	}

	private BitmapUtil(Context context) {
		this.context = context;
		Log.v(TAG, "recreating resources");
		try {
			background = BitmapFactory.decodeStream(context.getAssets().open(
					"images/bg.jpg"));
			aimButton1 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.aim_button1);
			aimButton2 = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.aim_button2);

		

		} catch (IOException e) {
			e.printStackTrace();
		}
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(18);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setStrokeWidth(1f);
		paint.setAlpha(255);
	}

	public Bitmap getScreenBitmap(Context context) {
		if (screen == null) {
			Display display = ((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			int size = Math.max(display.getWidth(), display.getHeight());
			screen = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		}
		return screen;
	}

	public Bitmap getAimButtonBitmap1() {
		return aimButton1;
	}

	public Bitmap getAimButtonBitmap2() {
		return aimButton2;
	}

	public Bitmap getBitmapOfPreferedSize(String path, String fileName, String postfix, int size){
		for(String filepath: new String[]{(path+"/"+fileName+"_"+size+"."+postfix),(path+"/"+fileName+"."+postfix)}){
			Log.v("BitmapUtil", "trying to get bitmap: "+filepath);
			try{
				Bitmap bitmap = BitmapFactory.decodeStream(context
						.getAssets().open(filepath));
				if(bitmap.getWidth() != size){
					Bitmap temp = bitmap;
					bitmap = Bitmap.createScaledBitmap(temp, size, size, true);
					temp.recycle();
				}
				Log.v("BitmapUtil" , "bitmap size: "+ bitmap.getWidth());
				return bitmap;
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	
	public void drawBackgroundBitmap(Canvas canvas, int width, int height,
			Paint paint) {
		int w = background.getWidth();
		int h = background.getHeight();
		float scale = Math.min(w / (float) width, h / (float) height);
		int ww = (int) (width * scale);
		int hh = (int) (height * scale);
		int x = (w - ww) / 2;
		int y = (h - hh) / 2;

		paint.setAntiAlias(false);
		paint.setAlpha(255);
		canvas.drawBitmap(background, new Rect(x, y, x + ww, y + hh), new Rect(
				0, 0, width, height), paint);

	}

	public enum Direction {
		UP, DOWN, LEFT, RIGHT;
	}

	public Bitmap getBitmap(int id) {
		return BitmapFactory.decodeResource(context.getResources(), id);
	}
	
	public Bitmap getBitmap(String assetPath){
		try {
			return BitmapFactory.decodeStream(context.getAssets().open(
			assetPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public float dipToPixel(float dip){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) dip, this.context.getResources().getDisplayMetrics());

	}
}
