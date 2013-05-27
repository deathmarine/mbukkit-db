package com.modcrafting.mbd.decom;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

//import com.modcrafting.mbd.MasterPluginDatabase;

public class HashFile implements SyntaxConstants {
    Pattern pattern = Pattern.compile("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
	private File file;
	private String hash;
	private String pack;
	RTextScrollPane scrollPane;
	Panel image_pane;
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
		textArea.setAntiAliasingEnabled(true);
		textArea.setCodeFoldingEnabled(true);
		if(file.getName().toLowerCase().endsWith(".java"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JAVA);
		else if(file.getName().toLowerCase().endsWith(".xml") 
				|| file.getName().toLowerCase().endsWith(".rss")
				|| file.getName().toLowerCase().endsWith(".project")
				|| file.getName().toLowerCase().endsWith(".classpath"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_XML);
		else if(file.getName().toLowerCase().endsWith(".h"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_C);
		else if(file.getName().toLowerCase().endsWith(".sql"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_SQL);
		else if(file.getName().toLowerCase().endsWith(".js"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JAVASCRIPT);
		else if(file.getName().toLowerCase().endsWith(".php")
				|| file.getName().toLowerCase().endsWith(".php5")
				|| file.getName().toLowerCase().endsWith(".phtml"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_PHP);
		else if(file.getName().toLowerCase().endsWith(".html") 
				|| file.getName().toLowerCase().endsWith(".htm")
				|| file.getName().toLowerCase().endsWith(".xhtm")
				|| file.getName().toLowerCase().endsWith(".xhtml"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_HTML);
		else if(file.getName().toLowerCase().endsWith(".js"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JAVASCRIPT);
		else if(file.getName().toLowerCase().endsWith(".lua"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_LUA);
		else if(file.getName().toLowerCase().endsWith(".bat"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_WINDOWS_BATCH);
		else if(file.getName().toLowerCase().endsWith(".pl"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_PERL);
		else if(file.getName().toLowerCase().endsWith(".sh"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_UNIX_SHELL);
		else if(file.getName().toLowerCase().endsWith(".css"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_CSS);
		else if(file.getName().toLowerCase().endsWith(".json"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JSON);
		else if(file.getName().toLowerCase().endsWith(".txt"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_NONE);
		else if(file.getName().toLowerCase().endsWith(".rb"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_RUBY);
		else if(file.getName().toLowerCase().endsWith(".make")
				|| file.getName().toLowerCase().endsWith(".mak"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_MAKEFILE);
		else if(file.getName().toLowerCase().endsWith(".py"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_PYTHON);
		else
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_PROPERTIES_FILE);
		if(file.getName().toLowerCase().endsWith(".png")
				||file.getName().toLowerCase().endsWith(".jpg")
				||file.getName().toLowerCase().endsWith(".jpeg")
				||file.getName().toLowerCase().endsWith(".gif")){
    		Image newimage = new ImageIcon(Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath())).getImage();
    		textArea.setBackgroundImage(newimage);
    		scrollPane = new RTextScrollPane(textArea, true);
		}else{
			scrollPane = new RTextScrollPane(textArea, true);
			scrollPane.setIconRowHeaderEnabled(true);		
			this.image = (Icon) new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/warn.png")));
			try {
				this.load();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
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
				if(validIP(line.toLowerCase())){
					String check = "[SEVERE] IP was found embedded.";
					list.add(check+" ("+pack+"."+ file.getName()+ " @L" + lineNum + ")\n"+line.trim());
					line = line+" //"+check;
					set.add(lineNum-1);	
				}
			}
			if(file.getName().toLowerCase().contains("plugin.yml") && line.startsWith("main: "))
				jar.mainclass = line.replace("main: ", "").trim();
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

	public boolean validIP(String ip) {
	    if (ip == null || ip.isEmpty()) return false;
	    ip = ip.trim();
	    try {
	        return pattern.matcher(ip).find();
	    } catch (PatternSyntaxException ex) {
	        return false;
	    }
	}

}
