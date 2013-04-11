package com.modcrafting.mbd.decom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.modcrafting.mbd.MasterPluginDatabase;

public class HashFile implements SyntaxConstants {
	private File file;
	private String hash;
	private String pack;
	RTextScrollPane scrollPane;
	RSyntaxTextArea textArea;
	DecompJar jar;
	List<String> list = new ArrayList<String>();
	HashSet<Integer> set = new HashSet<Integer>();
	Icon image;
	public HashFile(String pack, File file, DecompJar jar){
		this.file = file;
		this.pack = pack;
		this.jar = jar;
		textArea = new RSyntaxTextArea(25, 70);
		textArea.setCaretPosition(0);
		textArea.addHyperlinkListener(jar);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setClearWhitespaceLinesEnabled(false);
		textArea.setEditable(false);
		textArea.setCodeFoldingEnabled(true);
		textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JAVA);
		
		scrollPane = new RTextScrollPane(textArea, true);
		scrollPane.setIconRowHeaderEnabled(true);		
		this.image = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"warn.png");
		try {
			this.load();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void load() throws NoSuchAlgorithmException, IOException{
		MessageDigest md = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(file);
		is = new DigestInputStream(is, md);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		int lineNum = 1;
		System.out.println("Searching Files... ");
		while((line = br.readLine())!=null){
			if(file.getName().endsWith(".java")){
				for(String as: jar.map.keySet()){
					if (line.toLowerCase().contains(as.toLowerCase())) {
						String check = jar.map.get(as);
						list.add(check+" ("+pack+"."+ file.getName()+ " @L" + lineNum + ")\n"+line.trim());
						line = line+" //"+check;
						set.add(lineNum-1);
					}
				}
			}
			sb.append(line).append("\n");
			lineNum++;
		}
		textArea.setText(sb.toString());
		textArea.setCaretPosition(0);
		textArea.discardAllEdits();
		for(Integer it : set){
			try {
				scrollPane.getGutter().addLineTrackingIcon(it, image);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		br.close();
		is.close();
		byte[] mdbytes = md.digest();
		StringBuffer hexString = new StringBuffer();
    	for (int i=0;i<mdbytes.length;i++) {
    		String hex=Integer.toHexString(0xff & mdbytes[i]);
   	     	if(hex.length()==1) hexString.append('0');
   	     	hexString.append(hex);
    	}
		this.hash = hexString.toString();
	}

	public String getHash() {
		return hash;
	}
	
	public File getFile(){
		return file;
	}
	
	public String getPackage(){
		return pack;
	}
	
	public boolean checkDiffs(String hash){
		return this.hash.equals(hash);
	}

}
