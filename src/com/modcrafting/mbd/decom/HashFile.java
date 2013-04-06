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
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

public class HashFile implements SyntaxConstants {
	private File file;
	private String hash;
	private String pack;
	RTextScrollPane scrollPane;
	RSyntaxTextArea textArea;
	public HashFile(String pack, File file, DecompJar jar){
		this.file = file;
		this.pack = pack;
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
		Gutter gutter = scrollPane.getGutter();
		gutter.setBookmarkingEnabled(true);
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
		while((line = br.readLine())!=null){
			sb.append(line).append("\n");
			//TODO: Check for phrases here JOptionPane Display warning
			//      And/OR Add a tab with the code and set the Caret Position Highlighted
		}
		textArea.setText(sb.toString());
		textArea.setCaretPosition(0);
		textArea.discardAllEdits();
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
