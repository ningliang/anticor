package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MultiFileSource {
	private List<File> files;
	private BufferedReader reader;
	
	public MultiFileSource(String src_dir) {
		this.files = new ArrayList<File>();
		for (File file : new File(src_dir).listFiles()) {
			if (file.isFile() && file.getName().endsWith("txt")) {
				files.add(file);
			}
		}
	}
	
	public String nextLine() throws Exception {
		String line = null;
		while (line == null && files.size() > 0) {
			if (reader == null) {
				File file = files.remove(0);
				reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			}
			line = reader.readLine();
			if (line == null) {
				reader.close();
				reader = null;
			}
		}
		return line;
	}
}
