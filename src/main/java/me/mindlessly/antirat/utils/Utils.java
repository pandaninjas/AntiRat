package me.mindlessly.antirat.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;

import me.mindlessly.antirat.AntiRat;

public class Utils {

	public static String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return "";
		}
		return name.substring(lastIndexOf);
	}

	public static void checkForRat(ArrayList<String> toCheck) throws FileNotFoundException, IOException {
		int count = 0;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = loader.getResourceAsStream("checks.txt");
		try (BufferedReader b = new BufferedReader(new InputStreamReader(inputStream))) {
			String bad;
			while ((bad = b.readLine()) != null) {
				for (String s : toCheck) {
					if (s.contains(bad)) {
						System.out.println("Red flag - " + bad);
						if (s.toLowerCase().contains("discord.com/api/webhooks")) {
							System.out.println("Webhook - " + s);
						}
						count++;
					}
				}

			}
		}
		AntiRat.setCount(AntiRat.getCount() + count);
	}

	public static void checkThroughClasses(File[] classes) throws FileNotFoundException, IOException {
		for (File f : classes) {
			if (Utils.getFileExtension(f).equals(".class")) {
				ClassParser cp = new ClassParser(f.getAbsolutePath());
				JavaClass jc = cp.parse();
				ArrayList<String> toCheck = new ArrayList<>();
				ConstantPool constantPool = jc.getConstantPool();
				for (int i = 0; i < constantPool.getConstantPool().length; i++) {
					Constant c = constantPool.getConstant(i);
					if (c != null) {
						String toAdd = constantPool.constantToString(c);
						if (c instanceof ConstantUtf8) {
							if (isHeavilyObfuscated(toAdd)) {
								AntiRat.setCount(AntiRat.getCount() + 1);
								System.out.println("Red flag - Malicious code is potentially being obfuscated");
								continue;
							}
							toCheck.add(toAdd);
						}
					}
				}
				checkForRat(toCheck);
			} else if (f.isDirectory()) {
				checkThroughClasses(f.listFiles());
			}
		}

	}

	private static boolean isHeavilyObfuscated(String toAdd) {
		//I know this check is a bit shit, it is mainly to deal with a specific case I keep seeing
		if(toAdd.contains("IIIIIIIIIIIII")) {
			return true;
		}
		return false;
	}

	public static void extractFolder(String zipFile, String extractFolder) {
		try {
			int BUFFER = 2048;
			File file = new File(zipFile);

			ZipFile zip = new ZipFile(file);
			String newPath = extractFolder;

			new File(newPath).mkdir();
			Enumeration zipFileEntries = zip.entries();

			// Process each entry
			while (zipFileEntries.hasMoreElements()) {
				// grab a zip file entry
				ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
				String currentEntry = entry.getName();

				File destFile = new File(newPath, currentEntry);
				// destFile = new File(newPath, destFile.getName());
				File destinationParent = destFile.getParentFile();

				// create the parent directory structure if needed
				destinationParent.mkdirs();

				if (!entry.isDirectory()) {
					BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
					int currentByte;
					// establish buffer for writing file
					byte data[] = new byte[BUFFER];

					// write the current file to disk
					FileOutputStream fos = new FileOutputStream(destFile);
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

					// read and write until last byte is encountered
					while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, currentByte);
					}
					dest.flush();
					dest.close();
					is.close();
				}

			}
			zip.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}