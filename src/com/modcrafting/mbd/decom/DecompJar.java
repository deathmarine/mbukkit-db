package com.modcrafting.mbd.decom;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.modcrafting.mbd.Chekkit;
import com.modcrafting.mbd.objects.CodeTab;
import com.modcrafting.mbd.objects.NoteDialog;
import com.modcrafting.mbd.sql.SQL;

public class DecompJar extends JFrame implements HyperlinkListener, WindowListener{
	private static final long serialVersionUID = 1559666464481837372L;

	HashMap<String, HashSet<HashFile>> files = new HashMap<String, HashSet<HashFile>>();
	HashMap<String, HashSet<String>> opened = new HashMap<String, HashSet<String>>();
	public String mainclass;
	JTabbedPane tabbed;
	public SQL database;
	Map<String, String> map;
	HashMap<String, HashFile> safe = new HashMap<String, HashFile>();
	HashMap<String, HashFile> open = new HashMap<String, HashFile>();
	HashMap<String, HashFile> warn = new HashMap<String, HashFile>();
	List<String> bannedPackage;
	List<String> prevOpenBadFiles = new ArrayList<String>();
	List<String> databaseUpdates = new ArrayList<String>();
	File file;
	private int processID;
    private Chekkit main;
    private JTree tree;
        
	public DecompJar(Chekkit main, File file, SQL sql, Map<String, String> map, List<String> banpack, Boolean progressDisplay, Boolean useNimbus){
		long time = System.currentTimeMillis();
        this.main = main;
		processID = Chekkit.processPanel.addProcess("Opening " + file.getName().replaceAll(".jar", ""));
		database = sql;
		this.map = map;
		this.file = file;
		this.bannedPackage = banpack;
		Image img = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/bukkit-icon.png"))).getImage();
		this.setIconImage(img);
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName()) && useNimbus) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
                e.printStackTrace();
           
        }
		File newFile = new File(Chekkit.PATH + File.separator + "decomp"
				+ File.separator + file.getName());
		String[] cl = new String[] { "java", "-jar",
				Chekkit.PATH + File.separator + "lib" + File.separator + "fernflower.jar",
				"-dgs=true", file.getAbsolutePath(), Chekkit.PATH + File.separator + "decomp" };
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
		Chekkit.processPanel.setBarValue(processID, 25);
		for(File fs : extract(newFile)){
			recursiveFolderLoad(fs);
		}
		Chekkit.processPanel.setBarValue(processID, 50);
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
	    Chekkit.processPanel.setBarValue(processID, 75);
	    String badpack = new String();
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
	            for(String b: banpack){
	        		if(packs.startsWith(b)){
	        			badpack = b; 
	        		}
	            }
	    		
	    		
		    	DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(packs);
		    	for(HashFile f: files.get(packs)){
			    	DefaultMutableTreeNode dmtn1 = new DefaultMutableTreeNode(f.getFile().getName());
			    	dmtn.add(dmtn1);
					System.out.println("Checking hash for: "+f.getFile().getName());
					for(String hash : database.getHash(packs, f.getFile().getName())){
						if(f.checkDiffs(hash)){
							safe.put(f.getFile().getName(), f);
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
	    if(badpack.length()>0){
			Icon img2 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/badskull_large.png")));
            JOptionPane.showMessageDialog(this, "Restricted Library \""+badpack+"\" was found.\nRecommend Deny.", "Restricted Lib", JOptionPane.PLAIN_MESSAGE, img2);
	    }
	    tree = new JTree(top);
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    tree.setCellRenderer(new CheckedTreeCellRenderer(this));
	    TreeListener tl = new TreeListener(tree);
	    tree.addMouseListener(tl);
	    tree.addTreeSelectionListener(tl);
	    
	    
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
		JMenuItem mitem = new JMenuItem("Find");
		mitem.addActionListener(new Find(this));
		mitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		mitem.getAccessibleContext().setAccessibleDescription("Searches the currently selected tab.");
        JMenu menu2 = new JMenu("File");
        JMenuItem closeCurrentFile = new JMenuItem("Close current file");
        menu2.add(closeCurrentFile);
		closeCurrentFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        closeCurrentFile.getAccessibleContext().setAccessibleDescription("Closes the current tab");
		closeCurrentFile.addActionListener(new CloseCurrentTab(this));
                
                JMenuItem openNotes = new JMenuItem("Open Notes");
                menu2.add(openNotes);
                openNotes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0));
                openNotes.getAccessibleContext().setAccessibleDescription("Opens the Notes for this project");
                openNotes.addActionListener(new OpenNotes(this));
		menu.add(mitem);
		mbar.add(menu2);
		mbar.add(menu);
		menu2 = new JMenu("Themes");
		menu2.add(new JMenuItem(new ThemeAction("Default", "default.xml")));
		menu2.add(new JMenuItem(new ThemeAction("Dark", "dark.xml")));
		menu2.add(new JMenuItem(new ThemeAction("Eclipse", "eclipse.xml")));
		menu2.add(new JMenuItem(new ThemeAction("Visual Studio", "vs.xml")));
		mbar.add(menu2);
		mbar.setVisible(true);
		Chekkit.processPanel.setBarValue(processID, 100);
		this.setJMenuBar(mbar);
	    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
                if(Chekkit.config.getOpenNotesOnFileOpen()){
                    new NoteDialog(this);
                }
		
		Chekkit.processPanel.removeProcess(processID);
		
		System.out.println("Done in : "+(System.currentTimeMillis()-time)+"ms");
	}
	
	@ SuppressWarnings ("resource")
	private File[] extract(File file) {
		File newFile = new File(Chekkit.PATH + File.separator + "decomp"
				+ File.separator + file.getName());
		System.out.println("Extracting Contents...");
		File dir = new File(Chekkit.PATH + File.separator + "ext"
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
		String dir = new File(Chekkit.PATH + File.separator + "ext" + File.separator).getAbsolutePath();
		StringBuilder sb = new StringBuilder();
		String breakdown = fs.getAbsolutePath().replace(dir, "").trim();
		int ss = breakdown.split(pattern).length;
		for(String s:breakdown.split(pattern)){
			if(!s.contains(".")){ //HERE
				sb.append(s);
				if(ss>1)
					sb.append(".");
			}
		}
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1);
		}
		String packag = sb.toString();
		if(!fs.isDirectory()){
			HashSet<HashFile> set = new HashSet<HashFile>();
			if(packag.length()>0)
				packag = packag.substring(1);
			if(this.files.containsKey(packag))
				set = this.files.get(packag);
			set.add(new HashFile(packag, fs, this));
			System.out.println("Loading: "+packag+"."+fs.getName());
			this.files.put(packag, set);
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
			
			CodeTab ct = new CodeTab(title);
			ct.getButton().addMouseListener(new CloseTab(title));
			tabbed.setTabComponentAt(index, ct);
		}else{
			tabbed.setSelectedIndex(tabbed.indexOfTab(title));
		}
		if(!open.containsKey(file)){
			open.put(title, file);
			if(file.list.size()>0 && !prevOpenBadFiles.contains(title)){
				JList<String> list = new JList<String>(file.list.toArray(new String[0]));
				JScrollPane jsp = new JScrollPane(list);
				jsp.setPreferredSize(new Dimension(750,225));
				jsp.setMaximumSize(new Dimension(1000,300));
				prevOpenBadFiles.add(title);
				JOptionPane.showMessageDialog(this, jsp, "Warning!", JOptionPane.ERROR_MESSAGE);
			}
		}
		if(Chekkit.getTheme()!=null)
			Chekkit.getTheme().apply(file.textArea);
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {
		System.out.println(event.getURL());
	}

	@Override
	public void windowClosing(WindowEvent ev) {
		if(open.size()>0){
    		int value = JOptionPane.showConfirmDialog(ev.getWindow(),"You still have " + open.size() + " files open.\nAre you sure you want to close?", "OpenWindows", JOptionPane.YES_NO_OPTION);
    		if(value==JOptionPane.NO_OPTION || value==JOptionPane.NO_OPTION){
    			this.setVisible(true);
    			return;
    		}else if(value==JOptionPane.YES_OPTION){
    			this.dispose();
    		}
		}else{
			this.dispose();
		}
		//More efficient deletion
		new Thread(
			new Runnable(){
			@Override
			public void run(){
				processID = Chekkit.processPanel.addProcess("Deleting leftover files from " + file.getName().replaceAll(".yml", ""));
				File newFile = new File(Chekkit.PATH + File.separator + "decomp"
						+ File.separator + file.getName());
				File dir = new File(Chekkit.PATH + File.separator + "ext"
						+ File.separator);
				dir.mkdir();
				File newDir = new File(dir,file.getName());
				FileUtils.deleteFolder(newDir);
				Chekkit.processPanel.setBarValue(processID, 50);
				FileUtils.deleteFolder(newFile);
				Chekkit.processPanel.setBarValue(processID, 100);
				Chekkit.processPanel.removeProcess(processID);
				processID = Chekkit.processPanel.addProcess("Updating database for " + file.getName().replaceAll(".yml", ""));
				database.shutdown(databaseUpdates);
				Chekkit.processPanel.setBarValue(processID, 100);
				Chekkit.processPanel.removeProcess(processID);
			}
		}).start();
		int value = JOptionPane.showConfirmDialog(ev.getWindow(),"Delete the source file?", "Deletion", JOptionPane.YES_NO_OPTION);
		if(value==JOptionPane.YES_OPTION){
			FileUtils.deleteFolder(file);
		}
	}

	private void setFileSafe(final HashFile file){
		safe.put(file.getFile().getName(), file);
		if(open.containsKey(file.getFile().getName())){
			open.remove(file.getFile().getName());
		}
		tree.validate();
		tree.repaint();
		tree.updateUI();
		
	}
	
	private class Find extends AbstractAction{
		private static final long serialVersionUID = 836048800878134300L;
		DecompJar jar;
		public Find(DecompJar jar){
			this.jar = jar;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			new FindBox(jar);
		}
	}
	
	private class CloseCurrentTab extends AbstractAction{
        private static final long serialVersionUID = 836048800878134300L;
        //DecompJar jar;
        public CloseCurrentTab(DecompJar jar){
            //this.jar = jar;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            //JMenuItem me = (JMenuItem) e.getSource();
            int selected = tabbed.getSelectedIndex();
            if (selected != -1) {
                closeOpenTab(selected);
            } else {
                Chekkit.log.severe("No tab selected");
            }
        }
    }
	
	public void closeOpenTab(int index) {
	    Component co = tabbed.getComponentAt(index);
	    String title = tabbed.getTitleAt(index);
        if(open.containsKey(title)){
            HashFile hash = open.get(title);
            if(safe.containsKey(title) || title.endsWith(".MF") || title.endsWith(".yml")){
                open.remove(title);
                tabbed.remove(co);
                return;
            }
            if(hash.getFile().getName().endsWith(".java")){
                int value = JOptionPane.showConfirmDialog(co,"Save to database?");
                if(value==JOptionPane.CLOSED_OPTION || value == JOptionPane.CANCEL_OPTION){
                    return;
                }else if(value==JOptionPane.YES_OPTION && !safe.containsValue(hash)){
                    safe.put(title, hash);
                    if(open.containsKey(title)){
                        HashFile file = open.get(title);
                        //setFileSafe(file);
                        databaseUpdates.add("REPLACE INTO db_masterdbo (package,class,hash_contents) VALUES('" +
                                file.getPackage() + "','" +
                                file.getFile().getName() + "','" +
                                file.getHash() +
                                "')");
                        open.remove(title); 
                        tabbed.remove(co);  
                        return;
                    }
                }
                open.remove(title);
            }
        }
        tabbed.remove(co);
	}

	private class TreeListener extends MouseAdapter implements ActionListener, TreeSelectionListener{
		JTree tree;
		TreePath path;
		public TreeListener(JTree tree){
			this.tree = tree;
		}
		public TreeListener(JTree tree, TreePath path){
			this.tree = tree;
			this.path = path;
		}
		
        @Override
		public void mouseClicked(MouseEvent event) {
			TreePath trp = tree.getPathForLocation(event.getX(), event.getY());
			if(trp==null)
				return;
			final String[] args = trp.toString().replace("[", "").replace("]", "").split(",");
			if(SwingUtilities.isRightMouseButton(event)){
		        TreePath selPath = tree.getPathForLocation(event.getX(), event.getY());
		        tree.getSelectionModel().setSelectionPath(selPath);
		        JPopupMenu popup = new JPopupMenu();
		        for (String ac : new String[]{
		        		"Save",
		        		"Acknowledge",
		        		"Close",
		        		"Close All"
		        		}) {
		        	JMenuItem menuItem = new JMenuItem(ac);
					if(args.length==2 && opened.containsKey("")){
						if(opened.get("").contains(args[1])){
							menuItem.setEnabled(false);
						}
					}
		        	if(selPath.toString().contains(".java") && menuItem.isEnabled()){
		        		
			        	menuItem.addActionListener(new TreeListener(tree, selPath));
		        	} else if(safe.containsKey(args[args.length-1]) && ac.equals("Save")){
		        		menuItem.setEnabled(false);			        	
		        	} else if(open.containsKey(args[args.length-1]) && ac.equals("Close")){
		        		menuItem.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e) {
								int index = tabbed.indexOfTab(args[args.length-1]);
								Component co = tabbed.getComponentAt(index);
				        		open.remove(args[args.length-1]);
				    			tabbed.remove(co);
								
							}
		        			
		        		});
		        	} else if(ac.equals("Close All")){
		        		menuItem.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e) {
								for(int i=tabbed.getTabCount();i>tabbed.getTabCount();i--){
									String title = tabbed.getTitleAt(i);
									if(open.containsKey(title))
									open.remove(i);
									tabbed.removeTabAt(i);
								}
								
							}
		        			
		        		});
		        	} else {
		        		menuItem.setEnabled(false);
		        	}
		        	if(ac.equals("Acknowledge"))
		        		menuItem.setEnabled(false);
			        popup.add(menuItem);
		        }
		        popup.show(event.getComponent(), event.getX(), event.getY());
		        return;
			}
			/*if(event.getClickCount()==1 && SwingUtilities.isLeftMouseButton(event)){ //ActionPerformed happens anyway, no need to add twice
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
			}*/
		}
		@Override
		public void actionPerformed(ActionEvent event) {
		    JMenuItem source = (JMenuItem) event.getSource();
		    String action = source.getText();
	    	String[] args = path.toString().replace("[", "").replace("]", "").split(",");
		    if (action.equals("Open")){
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
		    if (action.equals("Save")){
				if(args.length<2)
					return;
				if(args.length==2){
					if(files.containsKey("")){
						for(final HashFile file :files.get("")){
							if(file.getFile().getName().equals(args[1].trim())
									&& !safe.values().contains(file)
									&& file.getFile().getName().endsWith(".java")){
									
								
								setFileSafe(file);
								return;
							}
						}
					}
				}
				if(args.length==3){
					if(files.containsKey(args[1].trim().toString())){
						for(final HashFile file :files.get(args[1].trim().toString())){
							if(file.getFile().getName().equals(args[2].trim())
									&& !safe.values().contains(file)
									&& file.getFile().getName().endsWith(".java")){
								setFileSafe(file);
								return;
							}
						}
					}
				}
		    }
		    if (action.equals("Acknowledge")){}
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TreePath trp = e.getPath();
			if(trp == null){
				return;
			}
			final String[] args = trp.toString().replace("[", "").replace("]", "").split(",");
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
	
	private class CloseTab extends MouseAdapter{
		String title;
		public CloseTab(String title) {
			this.title = title;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			int index = tabbed.indexOfTab(title);
			closeOpenTab(index);
			
		}
	}
	
	private class ThemeAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6618680171943723199L;
		private String xml;

		public ThemeAction(String name, String xml) {
			putValue(NAME, name);
			this.xml = "/themes/"+xml;
		}

		public void actionPerformed(ActionEvent e) {
			InputStream in = getClass().getResourceAsStream(xml);
			try {
				if(in!=null){
					Theme theme = Theme.load(in);
					Chekkit.setTheme(theme);
					for(HashFile hf : open.values()){
						theme.apply(hf.textArea);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {}
	@Override
	public void windowClosed(WindowEvent arg0) {}
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
	@Override
	public void windowIconified(WindowEvent arg0) {}
	@Override
	public void windowOpened(WindowEvent arg0) {}

    private static class OpenNotes implements ActionListener {

        private DecompJar file;
        public OpenNotes(DecompJar aThis) {
            file = aThis;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new NoteDialog(file);
        }
    }
        
}
