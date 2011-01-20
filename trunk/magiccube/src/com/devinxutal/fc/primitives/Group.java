package com.devinxutal.fc.primitives;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

public class Group extends Mesh {
	private Vector<Mesh> children = new Vector<Mesh>();

	@Override
	public void draw(GL10 gl) {

		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);

		int size = children.size();
		for (int i = 0; i < size; i++) {
			children.get(i).draw(gl);
		}

		gl.glRotatef(-rz, 0, 0, 1);
		gl.glRotatef(-ry, 0, 1, 0);
		gl.glRotatef(-rx, 1, 0, 0);
		gl.glTranslatef(-x, -y, -z);
	}

	public void add(int location, Mesh object) {
		children.add(location, object);
	}

	public boolean add(Mesh object) {
		return children.add(object);
	}

	public void clear() {
		children.clear();
	}

	public Mesh get(int location) {
		return children.get(location);
	}

	public Mesh remove(int location) {
		return children.remove(location);
	}

	public boolean remove(Object object) {
		return children.remove(object);
	}

	public int size() {
		return children.size();
	}
}
