package anticor;

import java.util.Date;

public class DataEntry {
	// exchange,stock_symbol,date,open,high,low,close,volume,adj close
	private String exchange;
	private String stock_symbol;
	private Date date;
	private double open;
	private double high;
	private double low;
	private double close;
	private int volume;
	private double adj_close;
	
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getExchange() {
		return exchange;
	}
	public void setStockSymbol(String stock_symbol) {
		this.stock_symbol = stock_symbol;
	}
	public String getStockSymbol() {
		return stock_symbol;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getDate() {
		return date;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getOpen() {
		return open;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getHigh() {
		return high;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getLow() {
		return low;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getClose() {
		return close;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public int getVolume() {
		return volume;
	}
	public void setAdjClose(double adj_close) {
		this.adj_close = adj_close;
	}
	public double getAdjClose() {
		return adj_close;
	}
}
