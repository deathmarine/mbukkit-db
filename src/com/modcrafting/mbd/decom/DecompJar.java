package com.modcrafting.mbd.decom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.fife.ui.rtextarea.RTextScrollPane;

import com.modcrafting.mbd.MasterPluginDatabase;
import com.modcrafting.mbd.objects.ProgressWindow;
import com.modcrafting.mbd.sql.SQL;

public class DecompJar extends JFrame implements HyperlinkListener, WindowListener{
	private static final long serialVersionUID = 1559666464481837372L;
	HashMap<String, HashSet<HashFile>> files = new HashMap<String, HashSet<HashFile>>();
	JTabbedPane tabbed;
	SQL database;
	Map<String, String> map;
	HashMap<String, HashFile> safe = new HashMap<String, HashFile>();
	HashMap<String, HashFile> open = new HashMap<String, HashFile>();
	HashMap<String, HashFile> warn = new HashMap<String, HashFile>();
	File file;
	public DecompJar(File file, SQL sql, Map<String, String> map){
		long time = System.currentTimeMillis();
		database = sql;
		this.map = map;
		this.file = file;
		ProgressWindow pw = new ProgressWindow(this);
		Image img = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"bukkit.png").getImage();
		this.setIconImage(img);
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e){
			e.printStackTrace();
		}
		File newFile = new File(MasterPluginDatabase.PATH + File.separator + "decomp"
				+ File.separator + file.getName());
		String[] cl = new String[] { "java", "-jar",
				MasterPluginDatabase.PATH + File.separator + "lib" + File.separator + "fernflower.jar",
				"-dgs=true", file.getAbsolutePath(), MasterPluginDatabase.PATH + File.separator + "decomp" };
		ProcessBuilder builder = new ProcessBuilder(cl);
		builder.redirectErrorStream(true);
		try {
			Process process = builder.start();
			BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()));
			String line;
			while((line = reader.readLine())!=null){
				System.out.println(line);
			}
			process.waitFor();
			reader.close();
			process.getErrorStream().close();
			process.getOutputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(!newFile.exists()){
			System.out.println("Failed");
			this.dispose();
		}
		for(File fs : extract(newFile)){
			recursiveFolderLoad(fs);
		}

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension center = new Dimension((int)(screenSize.width*0.75), (int)(screenSize.height*0.75));
		final int x = (int) (center.width * 0.2);
		final int y = (int) (center.height * 0.2);
		this.setBounds(x, y, center.width, center.height);
		this.setTitle(file.getName());
	    HashFile fa = null;
		System.out.println("Connecting to database...");
	    database.connect();
		System.out.println("Building Tree...");
	    DefaultMutableTreeNode top = new DefaultMutableTreeNode(file.getName());
	    List<String> list = Arrays.asList(files.keySet().toArray(new String[0]));
	    Collections.sort(list);
	    for(String packs : list){
	    	if(packs.length()>0){
	    		//TODO: Needs Better Package Breakdown
	    		/*
	    		 * Tree-
	    		 *     |-Node
	    		 *     |   |-File
	    		 *     |   |-SubNode
	    		 *     |       |-File
	    		 *     |       |-File
	    		 *  
	    		StringBuilder sb = new StringBuilder();
	    		for(String s: packs.split(".")){
	    			if(files.containsKey(sb.toString())){
	    		    	DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(s);
	    				if(files.containsKey(s)){
	    			    	for(HashFile f: files.get(s)){
	    			    		
	    			    	}
	    				}
	    			}else{
	    				sb.append(s).append(".");
	    			}
	    		}
	    		 *Start Rework
	    		*/
		    	DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(packs);
			    	for(HashFile f: files.get(packs)){
				    	DefaultMutableTreeNode dmtn1 = new DefaultMutableTreeNode(f.getFile().getName());
				    	dmtn.add(dmtn1);
						System.out.println("Checking hash for: "+f.getFile().getName());
						for(String hash : database.getHash(packs, f.getFile().getName())){
							if(f.checkDiffs(hash)){
								safe.put(f.getFile().getName(), f);
								System.out.println("SAFE:"+f.getFile().getName());
							}
						}
						System.out.println("Checking warnings for: "+f.getFile().getName());
						if(f.list.size()>0){
							warn.put(f.getFile().getName(), f);
							for(String s: f.list){
								System.out.println("WARN: "+s);
							}
						}
			    	}
		    	top.add(dmtn);
		    	//End
	    	}else{
		    	for(HashFile f: files.get(packs)){
		    		if(f.getFile().getName().equalsIgnoreCase("plugin.yml")){
				    	fa = f;
		    		}
			    	DefaultMutableTreeNode dmtn1 = new DefaultMutableTreeNode(f.getFile().getName());
			    	top.add(dmtn1);
		    	}
	    	}
	    }
	    database.disconnect();
		System.out.println("Disconnecting from database...");
		
	    JTree tree = new JTree(top);
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    tree.getSelectionModel().addTreeSelectionListener(new TreeListener());
	    tree.setCellRenderer(new CheckedTreeCellRenderer(this));
	    
	    JPanel panel2 = new JPanel();
	    panel2.setLayout(new BoxLayout(panel2, 1));
	    panel2.setBorder(BorderFactory.createTitledBorder("Structure"));
	    panel2.add(new JScrollPane(tree));
	    
	    tabbed = new JTabbedPane();
	    tabbed.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	    //tabbed.
	    if(fa!=null)
	    	addTab(fa.getFile().getName(), fa.scrollPane, fa);
	    
	    JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, 1));
	    panel.setBorder(BorderFactory.createTitledBorder("Code"));
	    panel.add(tabbed);
	    
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel2, panel);
	    this.getContentPane().add(sp);
	    this.addWindowListener(this);
		JMenuBar mbar = new JMenuBar();
		JMenu menu = new JMenu("Edit");
		mbar.add(menu);
		mbar.setVisible(true);
		this.setJMenuBar(mbar);
	    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		
		
		System.out.println("Done in : "+(System.currentTimeMillis()-time)+"ms");
		pw.close();
	}
	
	private File[] extract(File file) {
		File newFile = new File(MasterPluginDatabase.PATH + File.separator + "decomp"
				+ File.separator + file.getName());
		System.out.println("Extracting Contents...");
		File dir = new File(MasterPluginDatabase.PATH + File.separator + "ext"
				+ File.separator);
		dir.mkdir();
		File newDir = new File(dir,file.getName());
		try{
			JarFile jar = new JarFile(newFile);
			Enumeration<JarEntry> enums = jar.entries();
			while (enums.hasMoreElements()) {
				JarEntry je = (JarEntry) enums.nextElement();
				File fl = new File(newDir +File.separator+ je.getName());
		        if(!fl.exists()){
		            fl.getParentFile().mkdirs();
		            fl = new File(newDir +File.separator+ je.getName());
		        }
		        if(je.isDirectory())
		            continue;
		        InputStream is = jar.getInputStream(je);
		        FileOutputStream fo = new FileOutputStream(fl);
		        while(is.available()>0)
		        {
		            fo.write(is.read());
		        }
				fo.close();
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Extraction Complete...");
		return newDir.listFiles();
	}
	
	public void recursiveFolderLoad(File fs){
		String pattern = Pattern.quote(System.getProperty("file.separator"));
		String dir = new File(MasterPluginDatabase.PATH + File.separator + "ext"
				+ File.separator).getAbsolutePath();
		StringBuilder sb = new StringBuilder();
		int ss = fs.getAbsolutePath().split(pattern).length;
		for(String s:fs.getAbsolutePath().split(pattern)){
			if(!dir.contains(s) && !s.contains(".")){
				sb.append(s);
				if(ss>1){
					sb.append(".");
				}
			}
		}
		if(sb.length()>0)
			sb.deleteCharAt(sb.length()-1);
		String packag = sb.toString();
		if(!fs.isDirectory()){
			
			//TODO: Sort lists.
			
			if(this.files.containsKey(packag)){
				HashSet<HashFile> set = this.files.get(packag);
				set.add(new HashFile(packag, fs, this));
				System.out.println("Loading: "+packag+"."+fs.getName());
				this.files.put(packag, set);
			}else{
				HashSet<HashFile> set = new HashSet<HashFile>();
				set.add(new HashFile(packag, fs, this));
				System.out.println("Loading: "+packag+"."+fs.getName());
				this.files.put(packag, set);
			}
		}else{
			for(File s:fs.listFiles()){
				recursiveFolderLoad(s);
			}	
		}
	}
	
	public void addTab(String title, RTextScrollPane rTextScrollPane, HashFile file){
		if(tabbed.indexOfTab(title)==-1){
			tabbed.addTab(title, rTextScrollPane);
			tabbed.setSelectedIndex(tabbed.indexOfTab(title));
			int index = tabbed.indexOfTab(title);
			JPanel pnlTab = new JPanel(new GridBagLayout());
			pnlTab.setOpaque(false);
			
			JLabel lblTitle = new JLabel(title);
			JButton btnClose = new JButton("x");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			pnlTab.add(lblTitle, gbc);
			gbc.gridx++;
			gbc.weightx = 0;
			pnlTab.add(btnClose, gbc);
			tabbed.setTabComponentAt(index, pnlTab);
			btnClose.addActionListener(new CloseTab(title, pnlTab));
			if(safe.containsKey(title)){
				tabbed.setBackgroundAt(index, Color.GREEN);
			}
		}else{
			tabbed.setSelectedIndex(tabbed.indexOfTab(title));
		}
		if(!open.containsKey(file)){
			open.put(title, file);
			if(file.list.size()>0){
				StringBuilder sb = new StringBuilder();
				sb.append("The following files contains markers at:\n");
				for(String er: file.list){
					sb.append(er).append("\n");
				}
				JOptionPane.showMessageDialog(this, sb.toString(), "Warning!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {
		System.out.println(event.getURL());
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent ev) {
		if(open.size()>0){
    		int value = JOptionPane.showConfirmDialog(ev.getWindow(),"You still have files open.\n\nAre you sure you want to close?", "OpenWindows", JOptionPane.OK_CANCEL_OPTION);
    		if(value==JOptionPane.CANCEL_OPTION && value==JOptionPane.NO_OPTION){
    			this.setVisible(true);
    			return;
    		}else if(value==JOptionPane.YES_OPTION){
    			this.dispose();
    		}
		}else{
			this.dispose();
		}

		File newFile = new File(MasterPluginDatabase.PATH + File.separator + "decomp"
				+ File.separator + file.getName());
		File dir = new File(MasterPluginDatabase.PATH + File.separator + "ext"
				+ File.separator);
		dir.mkdir();
		File newDir = new File(dir,file.getName());
		FileUtils.deleteFolder(newDir);
		FileUtils.deleteFolder(newFile);

		int value = JOptionPane.showConfirmDialog(ev.getWindow(),"Delete the source file?", "Deletion", JOptionPane.OK_CANCEL_OPTION);
		if(value==JOptionPane.YES_OPTION){
			FileUtils.deleteFolder(file);
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private class TreeListener implements TreeSelectionListener{
		@Override
		public void valueChanged(TreeSelectionEvent selection) {
			//TODO: Bug on same file name under different packages.
			String[] args = selection.getPath().toString().replace("[", "").replace("]", "").split(",");
			if(args.length<2)
				return;
			if(args.length==2){
				if(files.containsKey("")){
					for(HashFile file :files.get("")){
						if(file.getFile().getName().equals(args[1].trim())){
							addTab(file.getFile().getName(), file.scrollPane, file);
							return;
						}
					}
				}
			}
			if(args.length==3){
				if(files.containsKey(args[1].trim().toString())){
					for(HashFile file :files.get(args[1].trim().toString())){
						if(file.getFile().getName().equals(args[2].trim())){
							addTab(file.getFile().getName(), file.scrollPane, file);
							return;
						}
					}
				}
			}
		}
	}
	
	private class CloseTab extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2301441154974782485L;
		JPanel panel;
		String title;
		public CloseTab(String title, JPanel panel) {
			this.panel = panel;
			this.title = title;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
	    	if(open.containsKey(title)){
	    		HashFile hash = open.get(title);
	    		if(safe.containsKey(title) || title.endsWith(".MF")){
	        		open.remove(title);
	    			tabbed.remove(panel);
	    			return;
	    		}
	    		if(hash.getPackage().length()>0){
	        		int value = JOptionPane.showConfirmDialog(panel,"Save to database", "Would you like to save this file.", JOptionPane.YES_NO_CANCEL_OPTION);
	        		if(value==JOptionPane.CANCEL_OPTION){
	        			return;
	        		}else if(value==JOptionPane.YES_OPTION){
	        			if(open.containsKey(title)){
	        				HashFile file = open.get(title);
	            			database.setAddress(file.getPackage(), file.getFile().getName(), file.getHash());
	            			database.disconnect();
	        			}
	        			safe.put(title, hash);
	        		}
	    		}
	    		open.remove(title);
	    	}
			tabbed.remove(panel);
		}
	}
}
