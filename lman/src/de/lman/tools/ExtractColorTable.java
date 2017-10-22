package de.lman.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class ExtractColorTable {
	
	public static void main(String[] args) throws IOException {
		String filename = "c:/users/final/Desktop/colortable.csv";
		File file = new File(filename);
		
		StringBuilder out = new StringBuilder();
		try (Reader reader = new InputStreamReader(new FileInputStream(file))) {
			char[] buffer = new char[2048];
			while (true) {
				int read = reader.read(buffer, 0, 2048);
				if (read <= 0) {
					break;
				}
				out.append(buffer, 0, read);
			}
		}
		
		String[] lines = out.toString().split("\n");
		int lineCount = 0;
		for (String line: lines) {
			String[] columns = line.split(",");
			if (lineCount > 0 && columns.length >= 2) {
				String name = columns[0];
				String hex = columns[1];
				System.out.println("public static final int " + name.replaceAll("[^a-zA-Z]", "") + " = " + hex.replace("#", "0x") + ";");
			}
			lineCount++;
		}
	}
	
}
