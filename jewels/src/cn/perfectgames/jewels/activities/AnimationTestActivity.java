package cn.perfectgames.jewels.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.perfectgames.amaze.animation.Animation;
import cn.perfectgames.jewels.animation.HintAnimation;
import cn.perfectgames.jewels.animation.SelectionAnimation;
import cn.perfectgames.jewels.cfg.Configuration;

public class AnimationTestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Configuration.config()
				.setSharedPreferences(
						PreferenceManager
								.getDefaultSharedPreferences(getBaseContext()));
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//testSelectionAnimation();
		testHintAnimation();
		
	}
	
	public void testSelectionAnimation(){
		SelectionAnimation ani = new SelectionAnimation();
		ani.setSize(40);
		ani.setLocation(0,0);
		testAnimation(ani, 40 ,2);
	}
	public void testHintAnimation(){
		HintAnimation ani = new HintAnimation();
		ani.setSize(40);
		ani.setLocation(0,0);
		testAnimation(ani, 40, 1);
	}
	
	public void testAnimation(Animation animation, int canvasSize, int loop){
		animation.reset();
		animation.start();
		LinearLayout list = new LinearLayout(this);
		list.setOrientation(LinearLayout.VERTICAL);
		for(int i =0; i< animation.getFrameCount()*loop; i++){
			animation.step();
			Bitmap b = Bitmap.createBitmap(canvasSize, canvasSize, Bitmap.Config.ARGB_8888);
			Canvas c  = new Canvas(b);
			animation.draw(c);
			
			//create views
			LinearLayout item = new LinearLayout(this);
			item.setOrientation(LinearLayout.HORIZONTAL);
			TextView t = new TextView(this);
			t.setText(i+". ");
			item.addView(t);
			
			ImageView v = new ImageView(this);
			v.setImageBitmap(b);
			item.addView(v);
			list.addView(item, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}
		ScrollView scrollView = new ScrollView(this);
		scrollView.addView(list);
		this.setContentView(scrollView);
	}
}