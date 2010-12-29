package com.devinxutal.fmc.primitives;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class ESquare extends Mesh {
	public static final int DIM_X = 0x01;
	public static final int DIM_Y = 0x02;
	public static final int DIM_Z = 0x04;

	private Color pasteColor = new Color();
	private Color marginColor = new Color(50, 50, 50, 255);

	private ShortBuffer pasteIndices;
	private ShortBuffer backIndices;
	private ShortBuffer marginIndices;

	public ESquare() {
		this(new Point3F(0, 0, 0), new Point3F(0, 1, 0), new Point3F(1, 1, 0),
				new Point3F(1, 0, 0), 0.05f);
	}

	public ESquare(Point3F p1, Point3F p2, Point3F p3, Point3F p4,
			float marginRate) {
		float mr = marginRate;
		float vertices[] = {
				p1.x,
				p1.y,
				p1.z, // 0
				p2.x,
				p2.y,
				p2.z, // 1
				p3.x,
				p3.y,
				p3.z, // 2
				p4.x,
				p4.y,
				p4.z, // 3
				p1.x + mr * (p3.x - p1.x),
				p1.y + mr * (p3.y - p1.y),
				p1.z + mr * (p3.z - p1.z), // 4
				p2.x + mr * (p4.x - p2.x),
				p2.y + mr * (p4.y - p2.y),
				p2.z + mr * (p4.z - p2.z), // 5
				p3.x + mr * (p1.x - p3.x),
				p3.y + mr * (p1.y - p3.y),
				p3.z + mr * (p1.z - p3.z), // 6
				p4.x + mr * (p2.x - p4.x), p4.y + mr * (p2.y - p4.y),
				p4.z + mr * (p2.z - p4.z), // 7
		};

		setVertices(vertices);

		short[] indices_paste = new short[] { 4, 6, 5, 4, 7, 6 };

		short[] indices_back = new short[] { 0, 1, 2, 0, 2, 3 };

		short[] indices_margin = new short[] { 0, 7, 4,//
				0, 3, 7,//
				3, 6, 7,//
				3, 2, 6,//
				2, 5, 6,//
				2, 1, 5,//
				1, 4, 5,//
				1, 0, 4 };

		this.setPasteIndices(indices_paste);
		this.setBackIndices(indices_back);
		this.setMarginIndices(indices_margin);

	}

	protected void setPasteIndices(short[] indices) {
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		pasteIndices = ibb.asShortBuffer();
		pasteIndices.put(indices);
		pasteIndices.position(0);
	}

	protected void setBackIndices(short[] indices) {
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		backIndices = ibb.asShortBuffer();
		backIndices.put(indices);
		backIndices.position(0);
	}

	protected void setMarginIndices(short[] indices) {
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		marginIndices = ibb.asShortBuffer();
		marginIndices.put(indices);
		marginIndices.position(0);
	}

	@Override
	protected void customizedDraw(GL10 gl) {
		gl.glColor4f(marginColor.getRed() / 255f,
				marginColor.getGreen() / 255f, marginColor.getBlue() / 255f,
				marginColor.getAlpha() / 255f);
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT,
				backIndices);
		gl.glDrawElements(GL10.GL_TRIANGLES, 24, GL10.GL_UNSIGNED_SHORT,
				marginIndices);
		gl.glColor4f(pasteColor.getRed() / 255f, pasteColor.getGreen() / 255f,
				pasteColor.getBlue() / 255f, pasteColor.getAlpha() / 255f);
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT,
				pasteIndices);
	}

	public void setPasteColor(Color pasteColor) {
		this.pasteColor.setRed(pasteColor.getRed());
		this.pasteColor.setGreen(pasteColor.getGreen());
		this.pasteColor.setBlue(pasteColor.getBlue());
		this.pasteColor.setAlpha(pasteColor.getAlpha());
	}

	public void setMarginColor(Color marginColor) {
		this.pasteColor.setRed(marginColor.getRed());
		this.pasteColor.setGreen(marginColor.getGreen());
		this.pasteColor.setBlue(marginColor.getBlue());
		this.pasteColor.setAlpha(marginColor.getAlpha());
	}

}