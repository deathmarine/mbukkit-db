package com.modcrafting.mbd.decom;

import java.io.File;

import javax.swing.tree.TreeNode;

public class FileUtils {
	public static void deleteFolder(File file) {
		if(file.isDirectory()){
			for (File f : file.listFiles()) {
				deleteFolder(f);
			}
		}
		System.out.println("Deleting: "+file.getAbsolutePath());
		file.delete();
	}
	public static String treePathToPackage(TreeNode[] path){
		if(path.length<1){
			return path[0].toString();
		}
		StringBuilder sb = new StringBuilder();
        for(int i=1;i<path.length;i++){
        	sb.append(path[i].toString()).append(".");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
	}
}
