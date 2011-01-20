package com.devinxutal.fc.primitives;

public class Square extends Mesh {

	public Square() {
		this(new Point3F(0, 0, 0), new Point3F(0, 1, 0), new Point3F(1, 1, 0),
				new Point3F(1, 0, 0));
	}

	public Square(Point3F p1, Point3F p2, Point3F p3, Point3F p4) {
		float vertices[] = { p1.x, p1.y, p1.z, // 0
				p2.x, p2.y, p2.z, // 1
				p3.x, p3.y, p3.z, // 2
				p4.x, p4.y, p4.z // 3
		};

		short indices[] = { 0, 2, 1, 0, 3, 2 };

		setIndices(indices);
		setVertices(vertices);
	}
}