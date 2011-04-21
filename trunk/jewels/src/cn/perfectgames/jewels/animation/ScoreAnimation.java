package cn.perfectgames.jewels.animation;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import cn.perfectgames.amaze.animation.AbstractAnimation;
import cn.perfectgames.jewels.cfg.Constants;
import cn.perfectgames.jewels.util.TextPainter;

public class ScoreAnimation extends AbstractAnimation {

	private List<ScoreEntry> scoreEntries = new LinkedList<ScoreEntry>();

	private int maxNum;
	private Bitmap[] scores;
	private Canvas canvas;
	private int scoreW;
	private int scoreH;
	private Rect rect;
	
	private TextPainter painter;
	private float jewelSize;
	private Paint paint;
	
	public ScoreAnimation(int maxNum) {
		super(Constants.FPS);
		this.maxNum = maxNum;
		scores = new Bitmap[maxNum];
		canvas = new Canvas();
		painter = new TextPainter();
		paint = new Paint();
		paint.setAntiAlias(true);
	}

	public void setJewelSize(int size){
		if(this.jewelSize == size){
			return ;
		}
		this.jewelSize = size;
		this.scoreW = (int)(2*size);
		this.scoreH = (int)(size);
		for(int i = 0 ; i<maxNum; i++){
			if(scores[i]!= null){
				scores[i].recycle();
			}
			scores[i] = Bitmap.createBitmap(scoreW,scoreH, Config.ARGB_8888);
		}

		rect = new Rect(0,0,scoreW, scoreH);
		painter.setStrokeColor(Color.DKGRAY);
		float textSize  =size*2.5f/5;
		painter.setStrokeWidth(textSize*0.1f);
		painter.setTextSize(textSize);
		
	}
	@Override
	protected void innerDraw(Canvas canvas, int current, int total) {
		int i = 0;
		paint.setAlpha( 255 * (total - current)/total);
		float ydelta = 2* jewelSize* current/total; 
		for(ScoreEntry se: scoreEntries){
			canvas.drawBitmap(scores[i], se.position.x - rect.width()/2, se.position.y - rect.height()/2 - ydelta, paint);
			i++;
		}
	}

	public void addScore(String score, PointF position, int color) {
		if(scoreEntries.size() >= maxNum){
			return;
		}
		this.scoreEntries.add(new ScoreEntry(score, position, color));
		Bitmap bitmap = scores[scoreEntries.size()-1];
		canvas.setBitmap(bitmap);
		paint.setAlpha(255);
		
		canvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);
		
		painter.setTextColor(color);
		painter.drawCenteredText(canvas, score, rect);
	}

	public void clearScores() {
		this.scoreEntries.clear();
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
