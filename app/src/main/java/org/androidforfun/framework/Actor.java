package org.androidforfun.framework;

public class Actor {
	protected int x, y;
	protected int width, height;

	public Actor() {
	}

	public Actor(int x, int y) {
		this.x=x;
		this.y=y;
	}

	public Actor(int x, int y, int width, int height) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void moveBy(int x, int y) {
		this.x += x;
		this.y += y;
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
	public int getBottom() {
		return y + height;
	}
	public int getRight() {
		return x + width;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setBounds(Rectangle rectangle) {
		this.x = rectangle.getX();
		this.y = rectangle.getY();
		this.width = rectangle.getWidth();
		this.height = rectangle.getHeight();
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	public boolean collide(Actor actor) {
		return getBounds().overlaps(actor.getBounds());
	}

	public boolean collide() {
		return false;
	}

	public boolean equals(Object object) {
		if (object instanceof Actor) {
			Actor actor = (Actor) object;
			if (x==actor.x && y==actor.y && width==actor.width && height==actor.height) {
				return true;
			}
		}
		return false;
	}
}
