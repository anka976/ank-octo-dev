package odobo.calculate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import odobo.calculate.bean.GeneratorConfig;
import odobo.calculate.bean.RectangleBean;

/**
 * Main calculation class, used for rectangle generation and re-cutting.
 * 
 * @author anna
 * 
 */
public class RectGenerator {

	/**
	 * Generates initial set of rectangles
	 * 
	 * @param config
	 *            generation configuration
	 * @return unmodifiable List of generated RectangleBeans
	 */
	public static List<RectangleBean> generateInitialRectangles(GeneratorConfig config) {
		List<RectangleBean> res = new ArrayList<RectangleBean>();
		Random rand = new Random();

		RectangleBean rectangleBean;
		int currentX = 0;
		int width;

		for (int i = 0; i < config.getNumberOfRects(); i++) {
			rectangleBean = new RectangleBean();
			rectangleBean.setX(currentX);
			rectangleBean.setY(config.getPlaymapHeight());
			
			width = rand.nextInt(config.getPlaymapWidth() - currentX - 
					GeneratorConfig.MIN_RECT_WIDTH * (config.getNumberOfRects() - i));
			width = width < GeneratorConfig.MIN_RECT_WIDTH ? GeneratorConfig.MIN_RECT_WIDTH : width;
			
			rectangleBean.setWidth(width);
			rectangleBean.setHeight(GeneratorConfig.MIN_RECT_HEIGHT + 
					rand.nextInt(config.getPlaymapHeight() - GeneratorConfig.MIN_RECT_HEIGHT));
			
			rectangleBean.setHeight(rectangleBean.getHeight() % GeneratorConfig.MIN_RECT_HEIGHT == 0?
					rectangleBean.getHeight() : rectangleBean.getHeight() - 
					(rectangleBean.getHeight() % GeneratorConfig.MIN_RECT_HEIGHT));

			currentX += rectangleBean.getWidth();

			res.add(rectangleBean);

		}

		return Collections.unmodifiableList(res);
	}

	/**
	 * Cuts horizontal rectangles from vertical ones
	 * @param initialRects vertical rectangles
	 * @return
	 */
	public static List<RectangleBean> reCutRectanglesInHorisontalDirection(List<RectangleBean> initialRects) {
		List<RectangleBean> res;

		// Sort by X-axis
		List<RectangleBean> xsorted = new ArrayList<RectangleBean>(initialRects);
		Collections.sort(xsorted, new XaxisComparator());

		res = processVerticalSkyline(xsorted, xsorted.get(0).getX(), xsorted.get(0).getY(), 0);

		return res;
	}

	
	/**
	 * Cuts horizontal rectangles from the skyline of vertical rectangles. Recursive.
	 * 
	 * @param xsorted list of rectangles, sorted  by X axis
	 * @param ysorted map of rectangles, sorted  by Y axis
	 * @param xlastIndex last rectangle on X axis to process
	 * @param xaxisFloor current X axis floor
	 * @param yaxisFloor current Y axis floor
	 * @param lastHeght last horizontal rectangle height
	 * @return horizontal rectangle
	 */
	private static List<RectangleBean> processVerticalSkyline(List<RectangleBean> xsorted, int xaxisFloor, int yaxisFloor, int lastHeght) {
		
		// Sort by y-axis (height)
		SortedMap<Integer, List<RectangleBean>> ysorted = new TreeMap<Integer, List<RectangleBean>>();
		List<RectangleBean> value;
		int xindex = 0;

		for (RectangleBean rectangleBean : xsorted) {
			value = ysorted.get(rectangleBean.getHeight());
			if (value == null) {
				value = new ArrayList<RectangleBean>();
				ysorted.put(rectangleBean.getHeight(), value);
			}
			rectangleBean.setxAxisOrder(xindex);
			value.add(rectangleBean);
			xindex++;
		}

		List<RectangleBean> res = new ArrayList<RectangleBean>();
		XaxisComparator xaxisComparator = new XaxisComparator();
		RectangleBean horizontalRectangle;
		List<RectangleBean> yvalue;
		int xlastIndex = xsorted.size() - 1;
		
		HashSet<RectangleBean> toBeProcessedInsublists = new HashSet<RectangleBean>();
		
		for (Integer rectangleBeanId : ysorted.keySet()) {
			// Sort by X-axis
			yvalue = ysorted.get(rectangleBeanId);
			
			//if this rectangle is in sublist already then continue
			if (toBeProcessedInsublists.contains(yvalue.get(0)) || xlastIndex < 0) {
				continue;
			}
			
			Collections.sort(yvalue, xaxisComparator);
			int nextXIndex = yvalue.get(yvalue.size()-1).getxAxisOrder() - 1;
			
			//find  next x-cut
			int xcutCurrentIndex = yvalue.get(yvalue.size()-1).getxAxisOrder();
			for (int i = yvalue.size() - 1; i >= 0; i--) {
				if (toBeProcessedInsublists.contains(yvalue.get(i))) {
					continue;
				}
			
				
				if ((i != 0 && yvalue.get(i).getxAxisOrder() - yvalue.get(i-1).getxAxisOrder() > 1 ) &&
						xcutCurrentIndex - yvalue.get(i).getxAxisOrder() > 1 || i == 0) {
					nextXIndex = yvalue.get(i).getxAxisOrder() - 1;
						break;
				}
				xcutCurrentIndex = yvalue.get(i).getxAxisOrder();
			}
			
			horizontalRectangle = getHorizontalRectangle(xsorted, xlastIndex, nextXIndex + 1, xaxisFloor, yaxisFloor, lastHeght);
			lastHeght += horizontalRectangle.getHeight();
			yaxisFloor -= horizontalRectangle.getHeight();
			res.add(horizontalRectangle);
			
			List<List<RectangleBean>> sublists = getSublistsToProcess(yvalue, xsorted, xlastIndex);
			
			if (!sublists.isEmpty()) {
				for (List<RectangleBean> list : sublists) {
					//some sublists are 'hanged', exclude them from current iteration
					toBeProcessedInsublists.addAll(list);
					List<RectangleBean> sublistRectangles = processVerticalSkyline(list, list.get(0).getX(), yaxisFloor, lastHeght); //list.get(0).getY() + list.get(0).getHeight());
					res.addAll(sublistRectangles);
				}
			}
			xlastIndex = nextXIndex;
		}
		
		return res;
	}
	
	/**
	 * Get hanged sublist to process
	 * @param currentYCutValue current pile of shortest rectangles
	 * @param xsorted parent processing list
	 * @param lastCutIndex current rectangle, from which the cut was done
	 * @return sublist
	 */
	private static List<List<RectangleBean>> getSublistsToProcess(List<RectangleBean> currentYCutValue, List<RectangleBean> xsorted, int lastCutIndex) {
		List<List<RectangleBean>> sublists = new ArrayList<List<RectangleBean>>();
		List<RectangleBean> sublist;
		
		int currentCutIndex = lastCutIndex+1;
		RectangleBean rectangleYCut;
		
		//reverse order, last is first
		for (int i = currentYCutValue.size() - 1; i >= 0; i--) {
			rectangleYCut = currentYCutValue.get(i);
			
			boolean h = (i < currentYCutValue.size() - 1 
					&& currentYCutValue.get(i+1).getxAxisOrder() - currentYCutValue.get(i).getxAxisOrder() == 1);
			
			if (rectangleYCut.getxAxisOrder()+1 > currentCutIndex || h) {
				continue;
			}
			
			sublist = xsorted.subList(rectangleYCut.getxAxisOrder()+1, currentCutIndex);
			if (!sublist.isEmpty()) {
				sublists.add(sublist);
				currentCutIndex = rectangleYCut.getxAxisOrder();
			}
		}
		
		return sublists;
	}
	

	
	/**
	 * Cuts first horizontal rectangle from the skyline of vertical rectangles
	 * @param xsorted  list of rectangles, sorted  by X axis
	 * @param xlastIndex  last rectangle on X axis to process
	 * @param ycutIndex  last rectangle on Y axis to process
	 * @param xaxisFloor current X axis floor
	 * @param yaxisFloor current Y axis floor
	 * @param lastHeght last horizontal rectangle height
	 * @return
	 */
	private static RectangleBean getHorizontalRectangle(List<RectangleBean> xsorted,
			int xlastIndex, int ycutIndex, int xaxisFloor, int yaxisFloor, int lastHeght) {
		RectangleBean rectangleBean = new RectangleBean();
		rectangleBean.setY(yaxisFloor);
		rectangleBean.setX(xaxisFloor);
		RectangleBean xlast = xsorted.get(xlastIndex);
		RectangleBean ycutRect = xsorted.get(ycutIndex);
		rectangleBean.setWidth(xlast.getX() - xaxisFloor + xlast.getWidth());
		rectangleBean.setHeight(ycutRect.getHeight() - lastHeght);
		return rectangleBean;
	}

	/**
	 * Comparator for sorting rectangles by X axis
	 * 
	 * @author anna
	 * 
	 */
	private static class XaxisComparator implements Comparator<RectangleBean> {
		@Override
		public int compare(RectangleBean o1, RectangleBean o2) {
			return Integer.compare(o1.getX(), o2.getX());
		}

	}

}
