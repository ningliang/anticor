package anticor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import util.MultiFileSource;

// Assumes data is sorted by date, not necessarily by symbol
public class DataSource {
	private Map<Integer, String> symbols_by_index = new TreeMap<Integer, String>();
	private Map<String, Integer> indices_by_symbol = new HashMap<String, Integer>();
	private Map<Integer, Date> dates_by_index = new TreeMap<Integer, Date>();
	private Map<Date, Integer> indices_by_date = new HashMap<Date, Integer>();
	private List<Date> ordered_dates;
	private double[][] prices;
	
	public DataSource(String dir) throws Exception {
		int symbol_counter = 0;
		int date_counter = 0;
		
		// First scan - get all symbols and dates
		MultiFileSource source = new MultiFileSource(dir);
		for (String line = source.nextLine(); line != null; line = source.nextLine()) {
			DataEntry entry = parseEntry(line);
			
			if (!indices_by_date.containsKey(entry.getDate())) {
				indices_by_date.put(entry.getDate(), date_counter);
				dates_by_index.put(date_counter, entry.getDate());
				date_counter++;
			}
			
			if (!indices_by_symbol.containsKey(entry.getStockSymbol())) {
				indices_by_symbol.put(entry.getStockSymbol(), symbol_counter);
				symbols_by_index.put(symbol_counter, entry.getStockSymbol());
				symbol_counter++;
			}
		}
		
		ordered_dates = new ArrayList<Date>(dates_by_index.values());
		prices = new double[date_counter][symbol_counter];
		
		// Second scan - fill in the data this time
		source = new MultiFileSource(dir);
		for (String line = source.nextLine(); line != null; line = source.nextLine()) {
			DataEntry entry = parseEntry(line);
			int date_index = indices_by_date.get(entry.getDate());
			int symbol_index = indices_by_symbol.get(entry.getStockSymbol());
			prices[date_index][symbol_index] = entry.getClose();
		}
		
		// Fill in prices for missing dates from beginning to end
		for (int i = 0; i < ordered_dates.size(); i++) {
			for (int j = 0; j < symbols_by_index.size(); j++) {
				if (i > 0 && prices[i][j] == 0.0 && prices[i-1][j] != 0.0) {
					prices[i][j] = prices[i-1][j];
				}
			}
		}
		
		// Fill in prices for missing dates from end to beginning
		for (int i = ordered_dates.size() - 1; i >= 0; i--) {
			for (int j = 0; j < symbols_by_index.size(); j++) {
				if (i < ordered_dates.size() - 1 && prices[i][j] == 0.0 && prices[i+1][j] != 0.0) {
					prices[i][j] = prices[i+1][j];
				}
			}
		}
	}
	
	public int getSymbolCount() {
		return symbols_by_index.size();
	}
	
	public int getDateCount() {
		return ordered_dates.size();
	}

	public Date getDateForIndex(int date_index) {
		return ordered_dates.get(date_index);
	}
	
	public double[] getPricesForDateIndex(int date_index) {
		return prices[date_index];
	}
	
	public double[] getRelativePricesForDateIndex(int date_index) {
		double[] retval = new double[getSymbolCount()]; // By default we return all 1's
		if (date_index > 0) {
			for (int i = 0; i < getSymbolCount(); i++) {
				retval[i] = prices[date_index][i] / prices[date_index - 1][i];
			}
		} else {
			for (int i = 0; i < getSymbolCount(); i++) {
				retval[i] = 1.0;
			}
		}
		return retval;
	}
	
	public double[] getLogRelativePricesForDateIndex(int date_index) {
		double[] retval = getRelativePricesForDateIndex(date_index);
		if (date_index > 0) {
			for (int i = 0; i < retval.length; i++) {
				retval[i] = Math.log(retval[i]);
			}
		}
		return retval;
	}
	                                                       
	
	public double[] getPricesForSymbol(int symbol_index) {
		double[] symbol_prices = new double[getDateCount()];
		for (int i = 0; i < getDateCount(); i++) {
			symbol_prices[i] = prices[i][symbol_index];
		}
		return symbol_prices;
	}
		
	// exchange,stock_symbol,date,open,high,low,close,volume,adj close
	private DataEntry parseEntry(String str) {
		DataEntry entry = new DataEntry();
		
		String[] fields = str.split(",");
		entry.setExchange(fields[0]);
		entry.setStockSymbol(fields[1]);
		entry.setDate(parseDate(fields[2]));
		entry.setOpen(Double.parseDouble(fields[3]));
		entry.setHigh(Double.parseDouble(fields[4]));
		entry.setLow(Double.parseDouble(fields[5]));
		entry.setClose(Double.parseDouble(fields[6]));
		entry.setVolume(Integer.parseInt(fields[7]));
		entry.setAdjClose(Double.parseDouble(fields[8]));
		
		return entry;
	}
	
	private Date parseDate(String str) {
		String[] fields = str.split("-");
		int year = Integer.parseInt(fields[0]);
		int month = Integer.parseInt(fields[1]);
		int day = Integer.parseInt(fields[2]);
		return new Date(year, month - 1, day);
	}
}