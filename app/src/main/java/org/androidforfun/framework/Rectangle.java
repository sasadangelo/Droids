package org.androidforfun.framework;

public class Rectangle {
	protected int x, y;
	protected int width, height;

	public Rectangle() {
	}

	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle(Rectangle rect) {
		x = rect.x;
		y = rect.y;
		width = rect.width;
		height = rect.height;
	}

	public void set(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public boolean contains(int x, int y) {
		return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
	}

	public boolean contains(Rectangle rectangle) {
		int xmin = rectangle.x;
		int xmax = xmin + rectangle.width;

		int ymin = rectangle.y;
		int ymax = ymin + rectangle.height;

		return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width))
			&& ((ymin > y && ymin < y + height) && (ymax > y && ymax < y + height));
	}

	public boolean overlaps(Rectangle r) {
		return (contains(r.x, r.y) || contains(r.x+r.width-1, r.y) || contains(r.x, r.y+r.width-1) ||
				contains(r.x+r.width-1, r.y+r.width-1));
	}

	public void set(Rectangle rect) {
		this.x = rect.x;
		this.y = rect.y;
		this.width = rect.width;
		this.height = rect.height;
	}

	public int getX () {
		return x;
	}

	public void setX (int x) {
		this.x = x;
	}

	public int getY () {
		return y;
	}

	public void setY (int y) {
		this.y = y;
	}

	public int getWidth () {
		return width;
	}

	public void setWidth (int width) {
		this.width = width;
	}

	public int getHeight () {
		return height;
	}

	public void setHeight (int height) {
		this.height = height;
	}
}
