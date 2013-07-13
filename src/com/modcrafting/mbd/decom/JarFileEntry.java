package com.modcrafting.mbd.decom;

import java.awt.Panel;
import java.awt.Toolkit;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.modcrafting.mbd.Chekkit;

public class JarFileEntry implements SyntaxConstants {
    Pattern pattern = Pattern.compile("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
	private String pack;
	private String name;
	private boolean safe = false;
	private boolean warn = false;
	private boolean open = false;
	private String hash;
	
	RTextScrollPane scrollPane;
	Panel image_pane;
	RSyntaxTextArea textArea;
	List<String> list = new ArrayList<String>();
	HashMap<Integer, String> set = new HashMap<Integer, String>();
	Icon image;
	public JarFileEntry(String pack, String name, String contents){
		this.setPackage(pack);
		this.name = name;
		textArea = new RSyntaxTextArea(25, 70);
		textArea.setCaretPosition(0);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setClearWhitespaceLinesEnabled(false);
		textArea.setEditable(false);
		textArea.setAntiAliasingEnabled(true);
		textArea.setCodeFoldingEnabled(true);
		if(name.toLowerCase().endsWith(".java"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JAVA);
		else if(name.toLowerCase().endsWith(".xml") 
				|| name.toLowerCase().endsWith(".rss")
				|| name.toLowerCase().endsWith(".project")
				|| name.toLowerCase().endsWith(".classpath"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_XML);
		else if(name.toLowerCase().endsWith(".h"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_C);
		else if(name.toLowerCase().endsWith(".sql"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_SQL);
		else if(name.toLowerCase().endsWith(".js"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JAVASCRIPT);
		else if(name.toLowerCase().endsWith(".php")
				|| name.toLowerCase().endsWith(".php5")
				|| name.toLowerCase().endsWith(".phtml"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_PHP);
		else if(name.toLowerCase().endsWith(".html") 
				|| name.toLowerCase().endsWith(".htm")
				|| name.toLowerCase().endsWith(".xhtm")
				|| name.toLowerCase().endsWith(".xhtml"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_HTML);
		else if(name.toLowerCase().endsWith(".js"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JAVASCRIPT);
		else if(name.toLowerCase().endsWith(".lua"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_LUA);
		else if(name.toLowerCase().endsWith(".bat"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_WINDOWS_BATCH);
		else if(name.toLowerCase().endsWith(".pl"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_PERL);
		else if(name.toLowerCase().endsWith(".sh"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_UNIX_SHELL);
		else if(name.toLowerCase().endsWith(".css"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_CSS);
		else if(name.toLowerCase().endsWith(".json"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_JSON);
		else if(name.toLowerCase().endsWith(".txt"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_NONE);
		else if(name.toLowerCase().endsWith(".rb"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_RUBY);
		else if(name.toLowerCase().endsWith(".make")
				|| name.toLowerCase().endsWith(".mak"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_MAKEFILE);
		else if(name.toLowerCase().endsWith(".py"))
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_PYTHON);
		else
			textArea.setSyntaxEditingStyle(SYNTAX_STYLE_PROPERTIES_FILE);
		scrollPane = new RTextScrollPane(textArea, true);
		scrollPane.setIconRowHeaderEnabled(true);		
		this.image = (Icon) new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/warn.png")));
		if(hash != null){
			try {
				this.load(contents);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}else{
			textArea.setText(contents);
			textArea.setCaretPosition(0);
			textArea.discardAllEdits();
		}
	}
	
	private void load(String sr) throws NoSuchAlgorithmException, IOException{
		String[] lines = sr.split("\n");
		int lineNum = 1;
		StringBuilder sb = new StringBuilder();
		for(String line : lines){
			if(name.endsWith(".java")){
				for(String as: Chekkit.keyword.keySet()){
					if (line.toLowerCase().contains(as.toLowerCase())) {
						String check = Chekkit.keyword.get(as);
						list.add(check+" ("+getPackage()+"."+ name+ " @L" + lineNum + ")\n"+line.trim());
						line = line+" //"+check;
						set.put(lineNum-1, line);
						setWarn(true);
					}
				}
				if(validIP(line.toLowerCase())){
					String check = "[SEVERE] IP was found embedded.";
					list.add(check+" ("+getPackage()+"."+ name+ " @L" + lineNum + ")\n"+line.trim());
					line = line+" //"+check;
					set.put(lineNum-1, line);
					setWarn(true);
				}
			}
			sb.append(line).append("\n");
			lineNum++;
		}
		textArea.setText(sb.toString());
		textArea.setCaretPosition(0);
		textArea.discardAllEdits();
		for(Integer it : set.keySet()){
			try {
				scrollPane.getGutter().addLineTrackingIcon(it, image);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getPackage(){
		return pack;
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

	public boolean isSafe() {
		return safe;
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	public boolean isWarn() {
		return warn;
	}

	public void setWarn(boolean warn) {
		this.warn = warn;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void setPackage(String pack) {
		this.pack = pack;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
