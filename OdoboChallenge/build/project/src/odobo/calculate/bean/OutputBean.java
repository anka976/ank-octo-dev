package odobo.calculate.bean;

import java.util.List;

/**
 * Output container for JSON
 * @author anna
 *
 */
public class OutputBean {
	/**
	 * source vertical rectangles
	 */
	private List<RectangleBean> sourceRectangles;
	/**
	 * re-cutted horizontal rectangles
	 */
	private List<RectangleBean> rectangles;
	/**
	 * number of rectangles
	 */
	private int numRects;
	/**
	 * @return the sourceRectangles
	 */
	public List<RectangleBean> getSourceRectangles() {
		return sourceRectangles;
	}
	/**
	 * @param sourceRectangles the sourceRectangles to set
	 */
	public void setSourceRectangles(List<RectangleBean> sourceRectangles) {
		this.sourceRectangles = sourceRectangles;
	}
	/**
	 * @return the rectangles
	 */
	public List<RectangleBean> getRectangles() {
		return rectangles;
	}
	/**
	 * @param rectangles the rectangles to set
	 */
	public void setRectangles(List<RectangleBean> rectangles) {
		this.rectangles = rectangles;
	}
	/**
	 * @return the numRects
	 */
	public int getNumRects() {
		return numRects;
	}
	/**
	 * @param numRects the numRects to set
	 */
	public void setNumRects(int numRects) {
		this.numRects = numRects;
	}

}
