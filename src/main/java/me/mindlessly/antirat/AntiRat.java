package me.mindlessly.antirat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.mindlessly.antirat.utils.Utils;

public class AntiRat {

	private Scanner scanner;

	public static void main(String args[]) {
		AntiRat m = new AntiRat();
		try {
			m.onEnable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onEnable() throws IOException {
		int count = 0;
		File folder = new File("C:/Users/" + System.getProperty("user.name") + "/AppData/Roaming/.minecraft/mods");
		File[] contents = folder.listFiles();
		for (File file : contents) {
			count = 0;
			if (Utils.getExtensionByStringHandling(file.getName()).get().equals("jar")) {
				System.out.println("Currently scanning " + file.getName());
				ZipFile zip = new ZipFile(file);
				if (file != null) {
					Enumeration<? extends ZipEntry> entries = zip.entries();
					if (entries != null) {
						while (entries.hasMoreElements()) {
							ZipEntry entry = entries.nextElement();
							InputStream inputStream = zip.getInputStream(entry);
							count += Utils.checkForRat(inputStream);
						}
						zip.close();
						if (count > 0) {
							System.out.println("A total of " + count + " red flags have been found.");
							System.out.println("Would you like to delete " + file.getName() + "? (Y/N)");
							scanner = new Scanner(System.in);
							String decision = scanner.next();
							if (decision.equalsIgnoreCase("Y")) {
								file.delete();
								System.out.println("File deleted.");
							} else {
								System.out.println("File not deleted.");
							}
						} else {
							System.out.println("File is likely safe.");
						}
					}
				}
			}
		}
		scanner.close();
	}

}