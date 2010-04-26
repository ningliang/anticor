package processing;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Samples data values >= min_year
 */
public class Sample {
	private String src_dir;
	private String dest_dir;
	private int min_year;
	
	public Sample(String src_dir, String dest_dir, int min_year) {
		this.src_dir = src_dir;
		this.dest_dir = dest_dir;
		this.min_year = min_year;
	}
	
	@SuppressWarnings("deprecation")
	public void run() throws IOException {
		File dir = new File(src_dir);
		for (File src_file : dir.listFiles()) {
			if (src_file.getAbsolutePath().contains("daily_prices")) {
				File dest_file = new File(dest_dir + "/" + src_file.getName());
				BufferedWriter out_file = new BufferedWriter(new FileWriter(dest_file.getAbsolutePath()));
				BufferedReader in_file = new BufferedReader(new FileReader(src_file.getAbsolutePath()));
				for (String line = in_file.readLine(); line != null; line = in_file.readLine()) {
					if (!line.contains("NYSE")) {
						continue;
					}
					String[] fields = line.split(",");
					int year = Integer.parseInt(fields[2].split("-")[0]);
					if (year >= min_year) {
						out_file.write(line + "\n");
					}				
				}
				in_file.close();
				out_file.close();
				System.out.println("Sampled " + src_file.getAbsolutePath());
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("<src_dir> <dest_dir> <min_year>");
			System.exit(1);
		}
		String src_dir = args[0];
		String dest_dir = args[1];
		int min_year = Integer.parseInt(args[2]);
		new Sample(src_dir, dest_dir, min_year).run();
	}
}
