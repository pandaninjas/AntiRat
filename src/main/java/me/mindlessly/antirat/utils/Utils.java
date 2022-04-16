package me.mindlessly.antirat.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Utils {

	public static Optional<String> getExtensionByStringHandling(String filename) {
		return Optional.ofNullable(filename).filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}

	public static int checkForRat(InputStream is) throws FileNotFoundException, IOException {
		int count = 0;
		Reader reader = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(reader);
		List<String> lines = br.lines().collect(Collectors.toList());
		Path rootDir = Paths.get(".").normalize().toAbsolutePath();
		File f = new File(rootDir.toString() +"/src/main/resources/checks.txt");
		try (BufferedReader b = new BufferedReader(new FileReader(f))) {
			String bad;
			while ((bad = b.readLine()) != null) {
				for(String line: lines) {
					if (line.contains(bad)) {
						count++;
						System.out.println("Red flag referenced - "+bad);
					}
				}
			}
		}
		return count;
	}

	
}
