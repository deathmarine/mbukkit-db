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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.fife.ui.rsyntaxtextarea.Theme;
import com.modcrafting.mbd.Chekkit;
import com.modcrafting.mbd.objects.CodeTab;
import com.modcrafting.mbd.objects.NoteDialog;
import com.modcrafting.mbd.sql.SQL;
import com.strobel.assembler.metadata.JarTypeLoader;
import com.strobel.assembler.metadata.MetadataSystem;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.assembler.metadata.TypeReference;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilationOptions;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import com.strobel.decompiler.languages.Languages;
import com.strobel.decompiler.languages.java.JavaFormattingOptions;

public class DecompJar extends JFrame implements WindowListener{
	private static final long serialVersionUID = 1559666464481837372L;

	HashSet<JarFileEntry> files = new HashSet<JarFileEntry>();
	public String mainclass;
	JTabbedPane tabbed;
	public SQL database;
	List<String> prevOpenBadFiles = new ArrayList<String>();
	List<String> databaseUpdates = new ArrayList<String>();
	File file;
	private int processID;
    private Chekkit main;
    private JTree tree;

	DecompilerSettings settings;
	DecompilationOptions decompilationOptions;
    
	public DecompJar(Chekkit main, File file, SQL sql, Boolean progressDisplay, Boolean useNimbus){
		
		long time = System.currentTimeMillis();
        this.main = main;
		processID = Chekkit.processPanel.addProcess("Opening " + file.getName().replaceAll(".jar", ""));
		database = sql;
		this.file = file;
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
		Chekkit.processPanel.setBarValue(processID, 25);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension center = new Dimension((int)(screenSize.width*0.75), (int)(screenSize.height*0.75));
		final int x = (int) (center.width * 0.2);
		final int y = (int) (center.height * 0.2);
		this.setBounds(x, y, center.width, center.height);
		this.setTitle(file.getName());
		System.out.println("Connecting to database...");
	    database.connect();
		Chekkit.processPanel.setBarValue(processID, 50);
		System.out.println("Building Tree...");
	    tree = new JTree();
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    tree.setCellRenderer(new CheckedTreeCellRenderer(this));
	    TreeListener tl = new TreeListener();
	    
	    tree.addMouseListener(tl);
	    tree.addTreeSelectionListener(tl);
	    
		settings = new DecompilerSettings();
	    if (settings.getFormattingOptions() == null)
	      settings.setFormattingOptions(JavaFormattingOptions.createDefault());
	    settings.setAlwaysGenerateExceptionVariableForCatchBlocks(true);
	    settings.setFlattenSwitchBlocks(true);
	    settings.setForceExplicitImports(true);
	    settings.setLanguage(Languages.java());
	    settings.setShowNestedTypes(true);
	    settings.setShowSyntheticMembers(true);
	    DefaultMutableTreeNode top = new DefaultMutableTreeNode(getName(file.getName()));
	    JarFile jar;
		try {
			jar = new JarFile(file);
		    settings.setTypeLoader(new JarTypeLoader(jar));
			decompilationOptions = new DecompilationOptions();
		    decompilationOptions.setSettings(settings);
		    decompilationOptions.setFullDecompilation(true);
		    Enumeration<JarEntry> entry = jar.entries();
		    List<String> mass = new ArrayList<String>();
		    String badpack = new String();
		    while(entry.hasMoreElements()){
		    	JarEntry jfi = entry.nextElement();
		    	mass.add(jfi.getName());
		    	String pack = jfi.getName().replace(getName(jfi.getName()), "");
		    	pack = pack.substring(0, pack.length()-1).replaceAll("/", ".");
		    	MessageDigest md = MessageDigest.getInstance("MD5");
				InputStream is = new DigestInputStream(jar.getInputStream(jfi), md);
				//process
	            for(String b: Chekkit.bannedpackage){
	        		if(pack.startsWith(b)){
	        			badpack = b; 
	        		}
	            }
				JarFileEntry hf = null;
				if(jfi.getName().endsWith(".class")){
					//Do Decompile
					String internalName = StringUtilities.removeRight(jfi.getName(), ".class");
				    MetadataSystem metadataSystem = new MetadataSystem(settings.getTypeLoader());
				    TypeReference type = metadataSystem.lookupType(internalName);
				    TypeDefinition resolvedType = null;
				    if ((type == null) || ((resolvedType = type.resolve()) == null)) {
				    	return;
				    }
				    StringWriter stringwriter = new StringWriter();
				    settings.getLanguage().decompileType(resolvedType, new PlainTextOutput(stringwriter), decompilationOptions);
				  
				    hf = new JarFileEntry(pack, getName(jfi.getName()), stringwriter.getBuffer().toString());
				    stringwriter.close();
				}else{
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while((line = br.readLine())!=null){
						sb.append(line).append("\n");
					}
					br.close();
				    hf = new JarFileEntry(pack, getName(jfi.getName()), sb.toString());
				}
				is.close();
				byte[] mdbytes = md.digest();
				StringBuffer hexString = new StringBuffer();
		    	for (int i=0;i<mdbytes.length;i++) {
		    		String hex=Integer.toHexString(0xff & mdbytes[i]);
		   	     	if(hex.length()==1) hexString.append('0');
		   	     	hexString.append(hex);
		    	}
		    	String hash = hexString.toString();
				for(String hash1 : database.getHash(pack, getName(jfi.getName()))){
					if(hash.equals(hash1)){
						hf.setSafe(true);
					}
				}
				if(hf != null){
					hf.setHash(hash);
					files.add(hf);
				}
		    }
		    Collections.sort(mass,String.CASE_INSENSITIVE_ORDER);
		    for(String pack : mass){
		    	LinkedList<String> list = new LinkedList<String>(Arrays.asList(pack.split("/")));
		    	load(top, list);
		    }
		    tree.setModel(new DefaultTreeModel(top));
		    if(badpack.length()>0){
				Icon img2 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/badskull_large.png")));
	            JOptionPane.showMessageDialog(this, "Restricted Library \""+badpack+"\" was found.\nRecommend Deny.", "Restricted Lib", JOptionPane.PLAIN_MESSAGE, img2);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    Chekkit.processPanel.setBarValue(processID, 75);
	    
	    JPanel panel2 = new JPanel();
	    panel2.setLayout(new BoxLayout(panel2, 1));
	    panel2.setBorder(BorderFactory.createTitledBorder("Structure"));
	    panel2.add(new JScrollPane(tree));
	    
	    tabbed = new JTabbedPane();
	    tabbed.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	    
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
		closeCurrentFile.addActionListener(new CloseCurrentTab());
                
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
    
    public String getName(String path) {
  		if(path==null)
  			return "";
  		int i = path.lastIndexOf("/");
  		if(i==-1)
  			i = path.lastIndexOf("\\");
  		if(i!=-1)
  			return path.substring(i+1);
  		return path;
  	}
    
    public DefaultMutableTreeNode load(DefaultMutableTreeNode node, List<String> args){
    	if(args.size() > 0){
    		String name = args.remove(0);
    		DefaultMutableTreeNode nod = getChild(node, name);
    		if(nod == null)
    			nod = new DefaultMutableTreeNode(name);
    		node.add(load(nod, args));
    	}	    		
    	return node;
    }

	@SuppressWarnings("unchecked")
    public DefaultMutableTreeNode getChild(DefaultMutableTreeNode node, String name){
		Enumeration<DefaultMutableTreeNode> entry = node.children();
		while(entry.hasMoreElements()){
			DefaultMutableTreeNode nods = entry.nextElement();
			if(nods.getUserObject().equals(name)){
				return nods;
			}
		}
		return null;
    }
	
	public void addTab(JarFileEntry file){
		if(file.isOpen()){
			tabbed.setSelectedIndex(tabbed.indexOfTab(file.getName()));
			return;
		}
		if(tabbed.indexOfTab(file.getName())==-1){
			tabbed.addTab(file.getName(), file.scrollPane);
			tabbed.setSelectedIndex(tabbed.indexOfTab(file.getName()));
			int index = tabbed.indexOfTab(file.getName());
			CodeTab ct = new CodeTab(file);
			ct.getButton().addMouseListener(new CloseTab(file.getName()));
			tabbed.setTabComponentAt(index, ct);
		}
		file.setOpen(true);
		if(file.list.size()>0 && !prevOpenBadFiles.contains(file.getName())){
			JList<String> list = new JList<String>(file.list.toArray(new String[0]));
			JScrollPane jsp = new JScrollPane(list);
			jsp.setPreferredSize(new Dimension(750,225));
			jsp.setMaximumSize(new Dimension(1000,300));
			prevOpenBadFiles.add(file.getName());
			JOptionPane.showMessageDialog(this, jsp, "Warning!", JOptionPane.ERROR_MESSAGE);
		}
		if(Chekkit.getTheme()!=null)
			Chekkit.getTheme().apply(file.textArea);
	}

	@Override
	public void windowClosing(WindowEvent ev) {
		int size = tabbed.getComponentCount();
		if(size>0){
    		int value = JOptionPane.showConfirmDialog(ev.getWindow(),"You still have " + size + " files open.\nAre you sure you want to close?", "OpenWindows", JOptionPane.YES_NO_OPTION);
    		if(value==JOptionPane.NO_OPTION || value==JOptionPane.NO_OPTION){
    			this.setVisible(true);
    			return;
    		}else if(value==JOptionPane.YES_OPTION){
    			this.dispose();
    		}
		}else{
			this.dispose();
		}
		new Thread(
			new Runnable(){
			@Override
			public void run(){
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

	private void setFileSafe(JarFileEntry file){
		file.setSafe(true);
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
        @Override
        public void actionPerformed(ActionEvent e) {
            int selected = tabbed.getSelectedIndex();
            if (selected != -1) {
                closeOpenTab(selected);
            } else {
                if (Chekkit.config.getBoolean("escape-closes-file", true)) {
                    DecompJar.this.getToolkit().getSystemEventQueue().postEvent(new WindowEvent(DecompJar.this, WindowEvent.WINDOW_CLOSING));
                }
            }
        }
    }
	
	public void closeOpenTab(int index) {
	    Component co = tabbed.getComponentAt(index);
	    if(co instanceof CodeTab){
	    	JarFileEntry hash = ((CodeTab) co).getJarFileEntry();
	    	hash.setOpen(false);

            if(hash.getName().endsWith(".class")){
                int value = JOptionPane.showConfirmDialog(co,"Save to database?");
                if(value==JOptionPane.CLOSED_OPTION || value == JOptionPane.CANCEL_OPTION){
                    return;
                }else if(value==JOptionPane.YES_OPTION && !hash.isSafe()){
                    hash.setSafe(true);
                        //setFileSafe(file);
                        databaseUpdates.add("REPLACE INTO db_masterdbo (package,class,hash_contents) VALUES('" +
                        		hash.getPackage() + "','" +
                        		hash.getName() + "','" +
                        		hash.getHash() +
                                "')");
                }
            }
	    }
        tabbed.remove(co);
	}

	private class TreeListener extends MouseAdapter implements ActionListener, TreeSelectionListener{
		
        @Override
		public void mouseClicked(MouseEvent event) {
			TreePath trp = tree.getPathForLocation(event.getX(), event.getY());
			if(trp==null)
				return;
			JarFileEntry jfil = null;
			for(JarFileEntry jar : files){
				if(jar.getName().equalsIgnoreCase(getNameFromPath(trp)) &&
						jar.getPackage().equalsIgnoreCase(getPackageFromPath(trp))){
					jfil = jar;
				}
			}
			if(jfil != null && SwingUtilities.isRightMouseButton(event)){
		        final TreePath selPath = tree.getPathForLocation(event.getX(), event.getY());
		        tree.getSelectionModel().setSelectionPath(selPath);
		        JPopupMenu popup = new JPopupMenu();
		        JMenuItem menuItem = new JMenuItem("Save");
	        	menuItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						JarFileEntry file = getJarFileEntryFromPath(selPath);
						if(file.getName().endsWith(".class") && 
								file.getHash() != null){
							setFileSafe(file);
						}
						
					}
	        	});	
		        popup.add(menuItem);
		        popup.show(event.getComponent(), event.getX(), event.getY());
		        return;
			}
		}
        
		@Override
		public void actionPerformed(ActionEvent event) {
		    JMenuItem source = (JMenuItem) event.getSource();
		    String action = source.getText();
			final String[] args = action.replace("[", "").replace("]", "").split(",");
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<args.length-1;i++){
				sb.append(args[i]).append(".");
			}
			if(sb.length() > 0)
				sb.deleteCharAt(sb.length()-1);
			for(JarFileEntry jar : files){
				if(jar.getName().equalsIgnoreCase(args[args.length-1]) &&
						jar.getPackage().equalsIgnoreCase(sb.toString())){
					addTab(jar);
					System.out.println("Cleared Tab.");
				}
			}
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TreePath trp = e.getPath();
			if(trp == null)
				return;
			for(JarFileEntry jar : files){
				if(jar.getName().equalsIgnoreCase(getNameFromPath(trp)) &&
						jar.getPackage().equalsIgnoreCase(getPackageFromPath(trp))){
					addTab(jar);
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
	
	public JarFileEntry getJarFileEntryFromPath(TreePath path){
		for(JarFileEntry jar : files){
			if(jar.getName().equalsIgnoreCase(getNameFromPath(path)) &&
					jar.getPackage().equalsIgnoreCase(getPackageFromPath(path))){
				return jar;
			}
		}
		return null;
	}
	
	public String getPackageFromPath(TreePath path){
		final String[] args = path.toString().replace("[", "").replace("]", "").split(",");
		StringBuilder sb = new StringBuilder();
		for(int i=1;i<args.length-1;i++){
			sb.append(args[i].trim()).append(".");
		}
		if(sb.length() > 0)
			sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public String getNameFromPath(TreePath path){
		final String[] args = path.toString().replace("[", "").replace("]", "").split(",");
		return args[args.length-1].trim();
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
					for(JarFileEntry hf : files){
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
