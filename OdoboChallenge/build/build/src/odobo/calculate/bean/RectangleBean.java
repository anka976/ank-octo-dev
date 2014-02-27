package odobo.calculate.bean;

/**
 * POJO for rectangle data storage
 * @author anna
 *
 */
public class RectangleBean {
	private int height;
	private int width;
	private int x;
	private int y;
	/**
	 * operational property for holding the index of rectangle on X axis
	 */
	private transient int xAxisOrder;
	
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	/**
	 * @return the xAxisOrder
	 */
	public int getxAxisOrder() {
		return xAxisOrder;
	}
	/**
	 * @param xAxisOrder the xAxisOrder to set
	 */
	public void setxAxisOrder(int xAxisOrder) {
		this.xAxisOrder = xAxisOrder;
	}

}
