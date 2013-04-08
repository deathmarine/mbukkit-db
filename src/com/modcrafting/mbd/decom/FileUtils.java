package com.modcrafting.mbd.decom;

import java.io.File;

public class FileUtils {
	public static void deleteFolder(File file) {
		if(file.isDirectory()){
			for (File f : file.listFiles()) {
				deleteFolder(f);
			}
		}
		System.out.println("Deleteing: "+file.getAbsolutePath());
		file.delete();
	}
}
