package cn.perfectgames.jewels.animation;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.PointF;
import cn.perfectgames.amaze.animation.AbstractAnimation;

public class ScoreAnimation extends AbstractAnimation {

	private List<ScoreEntry> scores = new LinkedList<ScoreEntry>();

	public ScoreAnimation() {
		super(10);
	}

	@Override
	protected void innerDraw(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	public void addScore(String score, PointF position, int color) {
		this.scores.add(new ScoreEntry(score, position, color));
	}

	public void clearScores() {
		this.scores.clear();
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
