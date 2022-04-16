package me.mindlessly.antirat.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

	public static String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return "";
		}
		return name.substring(lastIndexOf);
	}

	public static int checkForRat(InputStream is) throws FileNotFoundException, IOException {
		int count = 0;
		Reader reader = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(reader);
		List<String> lines = br.lines().collect(Collectors.toList());
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = loader.getResourceAsStream("checks.txt");
		try (BufferedReader b = new BufferedReader(new InputStreamReader(inputStream))) {
			String bad;
			while ((bad = b.readLine()) != null) {
				for (String line : lines) {
					if (line.contains(bad)) {
						count++;
						System.out.println("Red flag referenced - " + bad);
					}
				}
			}
		}
		return count;
	}

}