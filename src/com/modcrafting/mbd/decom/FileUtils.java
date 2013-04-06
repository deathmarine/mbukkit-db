package com.modcrafting.mbd.decom;

import java.io.File;

public class FileUtils {
	public static void deleteFolder(File file) {
		File trfil = file;
		if(!trfil.isDirectory())
			trfil = file.getParentFile();
		for (File f : trfil.listFiles()) {
			if(f.isDirectory()){
				deleteFolder(f);
			}else{
				f.delete();
			}
		}
		file.delete();
	}
}
