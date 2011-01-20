package com.devinxutal.fc.primitives;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Mesh {
	// Our vertex buffer.
	private FloatBuffer verticesBuffer = null;

	// Our index buffer.
	private ShortBuffer indicesBuffer = null;

	// The number of indices.
	private int numOfIndices = -1;

	// Flat Color
	private Color color = new Color();

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
		gl.glFrontFace(GL10.GL_CCW);
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
		gl.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color
				.getBlue() / 255f, color.getAlpha() / 255f);

		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);

		customizedDraw(gl);
		gl.glRotatef(-rz, 0, 0, 1);
		gl.glRotatef(-ry, 0, 1, 0);
		gl.glRotatef(-rx, 1, 0, 0);
		gl.glTranslatef(-x, -y, -z);
		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);
	}

	protected void customizedDraw(GL10 gl) {
		gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices,
				GL10.GL_UNSIGNED_SHORT, indicesBuffer);

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
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indicesBuffer = ibb.asShortBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.position(0);
		numOfIndices = indices.length;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
