package odobo.calculate.bean;

/**
 * Configuration class for calculation
 * @author anna
 *
 */
public class GeneratorConfig {
	
	public final static int DEFAULT_NUMBER_OF_RECTS = 5;
	/**
	 * Maximum number of rectangles
	 */
	public final static int MAX = 15;
	/**
	 * Minimum number of rectangles
	 */
	public final static int MIN = 1;
	
	/**
	 * Minimum width of rectangles
	 */
	public final static int MIN_RECT_WIDTH = 5;
	
	/**
	 * Minimum height of rectangles
	 */
	public final static int MIN_RECT_HEIGHT = 5;
	
	/**
	 * default number of rectangles
	 */
	private int numberOfRects = MIN;
	/**
	 * default  playmap width
	 */
	private int playmapWidth = 150;
	/**
	 * default
	 */
	private int playmapHeight = 150;
	
	public GeneratorConfig() {
		 this(DEFAULT_NUMBER_OF_RECTS);
	}
	
	/**
	 * Constructs new config with default values and
	 * @param numberOfRects number of rectangles to generate. Will be corrected according to MIN and MAX values.
	 */
	public GeneratorConfig(int numberOfRects) {
		this.numberOfRects = MIN > numberOfRects ? MIN : (MAX < numberOfRects ? MAX : numberOfRects);  
	}
	
	/**
	 * @return the numberOfRects
	 */
	public int getNumberOfRects() {
		return numberOfRects;
	}
	/**
	 * @param numberOfRects the numberOfRects to set
	 */
	public void setNumberOfRects(int numberOfRects) {
		this.numberOfRects = numberOfRects;
	}

	/**
	 * @return the playmapWidth
	 */
	public int getPlaymapWidth() {
		return playmapWidth;
	}

	/**
	 * @param playmapWidth the playmapWidth to set
	 */
	public void adjustPlaymapWidthBy(int playmapWidth) {
		this.playmapWidth = playmapWidth < MIN_RECT_WIDTH * numberOfRects ? MIN_RECT_WIDTH * numberOfRects : playmapWidth;
	}

	/**
	 * @return the playmapHeight
	 */
	public int getPlaymapHeight() {
		return playmapHeight;
	}

	/**
	 * @param playmapHeight the playmapHeight to set
	 */
	public void adjustPlaymapHeghtBy(int playmapHeight) {
		this.playmapHeight = playmapHeight;
	}

}
