package processing;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Groups rows by date, sorted by symbol, and outputs into files by month/year
 */
public class GroupAndSort {
	private String src_dir;
	private String dest_dir;
	
	private Map<String, Map<Integer, Map<String, String>>> lines_by_year_month_and_day_and_symbol;
	
	public GroupAndSort(String src_dir, String dest_dir) {
		this.src_dir = src_dir;
		this.dest_dir = dest_dir;
		this.lines_by_year_month_and_day_and_symbol = new HashMap<String, Map<Integer, Map<String,String>>>();
	}
	
	public void run() throws IOException {
		File dir = new File(src_dir);
		for (File file : dir.listFiles()) {
			if (file.getAbsolutePath().contains("daily_prices")) {
				BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					String[] fields = line.split(",");
					String[] date_fields = fields[2].split("-");
					String year_month_key = date_fields[0] + "-" + date_fields[1];
					int day_key = Integer.parseInt(date_fields[2]);
					String symbol = fields[1];
					if (lines_by_year_month_and_day_and_symbol.get(year_month_key) == null) {
						lines_by_year_month_and_day_and_symbol.put(year_month_key, new TreeMap<Integer, Map<String, String>>());
					}
					if (lines_by_year_month_and_day_and_symbol.get(year_month_key).get(day_key) == null) {
						lines_by_year_month_and_day_and_symbol.get(year_month_key).put(day_key, new TreeMap<String, String>());
					}
					lines_by_year_month_and_day_and_symbol.get(year_month_key).get(day_key).put(symbol, line);
				}
				reader.close();
				System.out.println("Consumed " + file.getAbsolutePath());
			}			
		} 
		
		for (String year_month_key : lines_by_year_month_and_day_and_symbol.keySet()) {
			String dest_file = dest_dir + "/" + year_month_key + ".txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(dest_file));
			for (Map<String, String> lines_by_symbol : lines_by_year_month_and_day_and_symbol.get(year_month_key).values()) {
				for (String line : lines_by_symbol.values()) {
					writer.write(line + "\n");
				}
			}
			writer.close();
			System.out.println("Wrote " + dest_file);
		}
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("<src_dir> <dest_dir>");
			System.exit(1);
		}
		
		String src_dir = args[0];
		String dest_dir = args[1];
		new GroupAndSort(src_dir, dest_dir).run();
	}
}
