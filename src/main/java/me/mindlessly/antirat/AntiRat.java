package me.mindlessly.antirat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.mindlessly.antirat.utils.Console;
import me.mindlessly.antirat.utils.OSValidator;
import me.mindlessly.antirat.utils.Utils;

public class AntiRat {
	private Scanner scanner;
	private Console console;

	public static void main(String args[]) {
		AntiRat m = new AntiRat();
		try {
			m.onEnable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onEnable() throws IOException {
		console = new Console();
		System.setOut(console.getOut());
		System.setIn(console.getIn());
		int count = 0;
		File folder = null;
		
		if(OSValidator.isWindows()) {
			folder = new File("C:/Users/" + System.getProperty("user.name") + "/AppData/Roaming/.minecraft/mods");
		}else if(OSValidator.isMac()) {
			folder = new File("~/Library/Application Support/minecraft/mods");
		}else if(OSValidator.isUnix()) {
			folder = new File("/home/"+System.getProperty("user.name")+"/.minecraft/mods");
		}else {
			System.out.println("Your OS is not supported!");
			return;
		}
		File[] contents = folder.listFiles();
		for (File file : contents) {
			count = 0;
			if (Utils.getFileExtension(file).equals(".jar")) {
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
		if (scanner != null) {
			scanner.close();
		}
	}

}