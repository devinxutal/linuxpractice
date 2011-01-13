package com.devinxutal.fmc.ui;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.devinxutal.fmc.model.CubeColor;
import com.devinxutal.fmc.model.CubeState;

public class PlaneCubeView extends View {
	String TAG = "PlaneCubeView";

	private List<PlaneCubeListener> listeners = new LinkedList<PlaneCubeListener>();
	private CubeColor[][][] currCube;
	private int order;
	PointF points[];
	Paint paint = new Paint();

	Bitmap colorPicker;
	Canvas colorPickerCanvas;
	boolean needResetPicker = false;

	public PlaneCubeView(Context context) {
		super(context);
		order = 3;
		init();
	}

	private void init() {
		colorPickerCanvas = new Canvas();
		points = new PointF[7];
		currCube = new CubeColor[order + 2][order + 2][order + 2];
		for (int i = 0; i < order + 2; i++) {
			for (int j = 0; j < order + 2; j++) {
				for (int k = 0; k < order + 2; k++) {
					currCube[i][j][k] = CubeColor.BLACK;
				}
			}
		}
		for (int i = 1; i <= order; i++) {
			for (int j = 1; j <= order; j++) {
				currCube[i][j][0] = CubeColor.ORANGE; // back
				currCube[i][j][order + 1] = CubeColor.RED; // front
				currCube[i][0][j] = CubeColor.WHITE; // down
				currCube[i][order + 1][j] = CubeColor.YELLOW; // up
				currCube[0][i][j] = CubeColor.BLUE; // left
				currCube[order + 1][i][j] = CubeColor.GREEN; // right
			}
		}

		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(5);
		paint.setStrokeJoin(Paint.Join.ROUND);
	}

	public void setColors(CubeColor from[][][]) {
		for (int i = 0; i < order + 2; i++) {
			for (int j = 0; j < order + 2; j++) {
				for (int k = 0; k < order + 2; k++) {
					currCube[i][j][k] = from[i][j][k];
				}
			}
		}
	}

	public void setColor(int x, int y, int z, CubeColor color) {
		try {
			this.currCube[x][y][z] = color;
			Log.v(TAG, "set color succeed: " + color);
		} catch (Exception e) {
		}
		this.invalidate();
	}

	public CubeColor getColor(int x, int y, int z) {
		try {
			return this.currCube[x][y][z];
		} catch (Exception e) {
		}
		return null;
	}

	public CubeState getCubeState() {
		CubeState state = new CubeState();
		state.order = order;
		for (int i = 1; i <= order; i++) {
			for (int j = 1; j <= order; j++) {
				state.add(0, i, j, currCube[0][i][j]);
				state.add(order + 1, i, j, currCube[order + 1][i][j]);
				state.add(i, 0, j, currCube[i][0][j]);
				state.add(i, order + 1, j, currCube[i][order + 1][j]);
				state.add(i, j, 0, currCube[i][j][0]);
				state.add(i, j, order + 1, currCube[i][j][order + 1]);
			}
		}
		return state;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (needResetPicker) {
			Log.v(TAG, "redraw color picker");
			this.drawCube(colorPickerCanvas, true);
		}
		this.drawCube(canvas, false);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != oldw || h != oldh) {
			colorPicker = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			colorPickerCanvas.setBitmap(colorPicker);
		}
		needResetPicker = true;
		// ajust locator points;
		if (points == null) {
			points = new PointF[7];
		}
		float len = Math.min(w, h) * 5f / 10f;
		float cx = w / 2f;
		float cy = h / 2f - h / 12f;
		float sr3 = (float) Math.sqrt(3);
		float lensr3 = len * sr3 / 2;
		float lenhalf = len / 2;
		points[0] = new PointF(cx, cy);
		points[1] = new PointF(cx, cy - len * 4 / 5);
		points[2] = new PointF(cx + lensr3, cy - lenhalf);
		points[3] = new PointF(cx + lensr3 * 5 / 6, cy + lenhalf * 11 / 10f);
		points[4] = new PointF(cx, cy + len * 11 / 10f);
		points[5] = new PointF(cx - lensr3 * 5 / 6, cy + lenhalf * 11 / 10f);
		points[6] = new PointF(cx - lensr3, cy - lenhalf);

	}

	float[] props1 = new float[] { 8f / 21, 7f / 21, 6f / 21 };
	float[] props2 = new float[] { 6f / 21, 7f / 21, 8f / 21 };

	private void drawCube(Canvas canvas, boolean picker) {
		// whole cube
		Path p = new Path();
		if (points[0] == null) {
			return;
		}
		p.moveTo(points[1].x, points[1].y);
		for (int i = 2; i <= 6; i++) {
			p.lineTo(points[i].x, points[i].y);
		}
		p.close();
		canvas.drawPath(p, paint);
		// up
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j <= 3; j++) {
				int color = currCube[i][4][j].getColor();
				if (picker) {
					color = Color.rgb(i * 20, 4 * 20, j * 20);
				}
				drawPiece(canvas, color, i, j, props2, props2, points[1],
						points[2], points[0], points[6], !picker);
			}
		}
		// front
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j <= 3; j++) {
				int color = currCube[i][j][4].getColor();
				if (picker) {
					color = Color.rgb(i * 20, j * 20, 4 * 20);
				}
				drawPiece(canvas, color, i, j, props2, props2, points[5],
						points[4], points[0], points[6], !picker);
			}
		}
		// up
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j <= 3; j++) {
				int color = currCube[4][j][i].getColor();
				if (picker) {
					color = Color.rgb(4 * 20, j * 20, i * 20);
				}
				drawPiece(canvas, color, i, j, props2, props2, points[3],
						points[4], points[0], points[2], !picker);
			}
		}
	}

	// a d
	// n
	// b c
	// m

	private void drawPiece(Canvas canvas, int color, int m, int n,
			float[] propsM, float[] propsN, PointF a, PointF b, PointF c,
			PointF d, boolean drawContour) {
		float acumM = 0;
		float acumN = 0;
		float x, y;
		Path path = new Path();
		float contour[] = new float[4 * 4 * 2];
		// first point
		for (int i = 0; i < m - 1; i++) {
			acumM += propsM[i];
		}
		for (int i = 0; i < n - 1; i++) {
			acumN += propsN[i];
		}

		float x1 = a.x + (b.x - a.x) * acumM;
		float x2 = d.x + (c.x - d.x) * acumM;
		x = x1 + (x2 - x1) * acumN;

		float y1 = a.y + (d.y - a.y) * acumN;
		float y2 = b.y + (c.y - b.y) * acumN;
		y = y1 + (y2 - y1) * acumM;

		path.moveTo(x, y);

		contour[0] = contour[14] = x;
		contour[1] = contour[15] = y;

		// second point
		acumM = 0;
		acumN = 0;
		for (int i = 0; i < m; i++) {
			acumM += propsM[i];
		}
		for (int i = 0; i < n - 1; i++) {
			acumN += propsN[i];
		}

		x1 = a.x + (b.x - a.x) * acumM;
		x2 = d.x + (c.x - d.x) * acumM;
		x = x1 + (x2 - x1) * acumN;

		y1 = a.y + (d.y - a.y) * acumN;
		y2 = b.y + (c.y - b.y) * acumN;
		y = y1 + (y2 - y1) * acumM;

		path.lineTo(x, y);
		contour[2] = contour[4] = x;
		contour[3] = contour[5] = y;
		// third point

		acumM = 0;
		acumN = 0;
		for (int i = 0; i < m; i++) {
			acumM += propsM[i];
		}
		for (int i = 0; i < n; i++) {
			acumN += propsN[i];
		}

		x1 = a.x + (b.x - a.x) * acumM;
		x2 = d.x + (c.x - d.x) * acumM;
		x = x1 + (x2 - x1) * acumN;

		y1 = a.y + (d.y - a.y) * acumN;
		y2 = b.y + (c.y - b.y) * acumN;
		y = y1 + (y2 - y1) * acumM;
		path.lineTo(x, y);

		contour[6] = contour[8] = x;
		contour[7] = contour[9] = y;
		// forth point

		acumM = 0;
		acumN = 0;
		for (int i = 0; i < m - 1; i++) {
			acumM += propsM[i];
		}
		for (int i = 0; i < n; i++) {
			acumN += propsN[i];
		}

		x1 = a.x + (b.x - a.x) * acumM;
		x2 = d.x + (c.x - d.x) * acumM;
		x = x1 + (x2 - x1) * acumN;

		y1 = a.y + (d.y - a.y) * acumN;
		y2 = b.y + (c.y - b.y) * acumN;
		y = y1 + (y2 - y1) * acumM;
		path.lineTo(x, y);

		contour[10] = contour[12] = x;
		contour[11] = contour[13] = y;
		// draw
		path.close();
		paint.setColor(color);
		canvas.drawPath(path, paint);
		if (drawContour) {
			paint.setColor(CubeColor.BLACK.getColor());
			canvas.drawLines(contour, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		if (colorPicker != null) {
			int pixel = colorPicker.getPixel(x, y);
			int i = Color.red(pixel) / 20;
			int j = Color.green(pixel) / 20;
			int k = Color.blue(pixel) / 20;
			if ((i | j | k) != 0) {
				notifyCubeClicked(i, j, k);
			}
		}
		return super.onTouchEvent(event);
	}

	public boolean addPlaneCubeListener(PlaneCubeListener l) {
		return this.listeners.add(l);
	}

	public boolean removePlaneCubeListener(PlaneCubeListener l) {
		return this.listeners.remove(l);
	}

	public void clearPlaneCubeListener() {
		this.listeners.clear();
	}

	protected void notifyCubeClicked(int x, int y, int z) {
		Log.v(TAG, "cube clicked: " + x + " " + y + " " + z);
		for (PlaneCubeListener l : listeners) {
			l.cubeClicked(x, y, z);
		}
	}
}
