package com.devinxutal.fmc.primitives;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cube extends Mesh {
	public Cube(float width, float height, float depth) {
		width /= 2;
		height /= 2;
		depth /= 2;

		float vertices[] = { -width, -height, -depth, // 0
				width, -height, -depth, // 1
				width, height, -depth, // 2
				-width, height, -depth, // 3
				-width, -height, depth, // 4
				width, -height, depth, // 5
				width, height, depth, // 6
				-width, height, depth, // 7
		};

		short indices[] = { 2, 6, 7, //
				2, 7, 3, // up
				0, 4, 5, //
				0, 5, 1, // down
				1, 5, 6, //
				1, 6, 2, // right
				3, 7, 4, //
				3, 4, 0, // left
				4, 7, 6, //
				4, 6, 5, // front
				3, 0, 1, //
				3, 1, 2, // back
		};


		setIndices(indices);
		setVertices(vertices);

		Color sideColors[] = new Color[] { Color.YELLOW, // up
				Color.WHITE, // down
				Color.RED, // right
				Color.ORANGE, // left
				Color.BLUE, // front
				Color.GREEN // back
		};
		setSideColors(sideColors);
	}

	// Our vertex buffer.
	private FloatBuffer verticesBuffer = null;

	// Our index buffer, for 6 face.
	private ShortBuffer[] indicesBuffer = null;

	// The number of indices.
	private int numOfIndices = -1;

	// Flat Color
	private float[] rgba = new float[] { 1f, 1f, 1f, 1f };
	private float[] rgbas;

	private Color[] sideColors;

	// Translate params.
	public float x = 0;
	public float y = 0;
	public float z = 0;

	// Rotate params.
	public float rx = 0;
	public float ry = 0;
	public float rz = 0;

	//
	public void draw(GL10 gl) {
		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CW);
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
		// Set flat color
		gl.glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);

		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);

		for (int i = 0; i < numOfIndices; i++) {
			if (sideColors != null) {
				Color color = sideColors[i % sideColors.length];
				gl.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f,
						color.getAlpha()/255f);
			}
			gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices,
					GL10.GL_UNSIGNED_SHORT, indicesBuffer[i]);

		}
		gl.glRotatef(-rz, 0, 0, 1);
		gl.glRotatef(-ry, 0, 1, 0);
		gl.glRotatef(-rx, 1, 0, 0);
		gl.glTranslatef(-x, -y, -z);

		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);
	}

	protected void setVertices(float[] vertices) {
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		verticesBuffer = vbb.asFloatBuffer();
		verticesBuffer.put(vertices);
		verticesBuffer.position(0);
	}

	protected void setIndices(short[] indices) {
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		indicesBuffer = new ShortBuffer[6];
		for (int i = 0; i < 6; i++) {
			ByteBuffer ibb = ByteBuffer.allocateDirect(6 * 2);
			ibb.order(ByteOrder.nativeOrder());
			indicesBuffer[i] = ibb.asShortBuffer();
			indicesBuffer[i].put(indices, i * 6, 6);
			indicesBuffer[i].position(0);
		}
		numOfIndices = 6;
	}

	protected void setColor(float red, float green, float blue, float alpha) {
		// Setting the flat color.
		rgba[0] = red;
		rgba[1] = green;
		rgba[2] = blue;
		rgba[3] = alpha;
	}

	protected void setColors(float[] colors) {
		this.rgbas = colors;
	}

	public void setSideColors(Color[] colors) {
		this.sideColors = colors;
	}

	public void setSideColors(Color up, Color down, Color right, Color left, Color front, Color back) {
		if(this.sideColors == null || this.sideColors.length != 6){
			sideColors = new Color[6];
		}
		sideColors[0] = up;
		sideColors[1] = down;
		sideColors[2] = right;
		sideColors[3] = left;
		sideColors[4] = front;
		sideColors[5] = back;
	}
}