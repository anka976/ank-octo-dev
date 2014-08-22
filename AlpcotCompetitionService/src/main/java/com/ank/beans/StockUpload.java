package com.ank.beans;

import java.util.Date;
import java.util.List;


public class StockUpload {
	
	private List<Stock> stock;
	private Date time;
	/**
	 * @return the stock
	 */
	public List<Stock> getStock() {
		return stock;
	}
	/**
	 * @param stock the stock to set
	 */
	public void setStock(List<Stock> stock) {
		this.stock = stock;
	}
	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}

}
