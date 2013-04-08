package com.modcrafting.mbd;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import com.modcrafting.mbd.decom.DecompJar;
import com.modcrafting.mbd.objects.MDTextArea;
import com.modcrafting.mbd.objects.ProgressWindow;
import com.modcrafting.mbd.sql.SQL;

public class MasterPluginDatabase extends JFrame implements WindowListener{
	private static final long serialVersionUID = 2878574498207291074L;
	public final static String database = "jdbc:mysql://server.modcrafting.com:3306/dbo_master";
	public final static Logger log = Logger.getLogger("MasterPluginDatabase");
	public Properties properties;
	private Connection connection;
	public Console console;
	public SQL datab;
	private JList actionlist;
	private Map<String, String> keyword = new HashMap<String, String>();
	public MasterPluginDatabase(Properties properties){
		ProgressWindow pw = new ProgressWindow(this);
		this.properties = properties;
		datab = new SQL(this);
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e){
			e.printStackTrace();
		}

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension center = new Dimension((int)(screenSize.width*0.75), (int)(screenSize.height*0.75));
		final int x = (int) (center.width * 0.2);
		final int y = (int) (center.height * 0.2);
		this.setBounds(x, y, center.width, center.height);
		this.setTitle(this.getClass().getSimpleName());
		Image img = new ImageIcon(PATH+File.separator+"resources"+File.separator+"bukkit.png").getImage();
		this.setIconImage(img);
		
		//Setup Console
		MDTextArea mdt = new MDTextArea(this);
	    JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, 1));
	    panel.setBorder(BorderFactory.createTitledBorder("Console"));
	    panel.add(new JScrollPane(mdt));
	    JPanel p2 = new JPanel();
	    this.actionlist = new JList();
	    try {
            keyword = (Map<String, String>) SLAPI.load("keywords.bin");
        } catch (Exception e) {
            log.info("Failed to load keywords list.");
            keyword.put(".getName().equals", "[WARN] Possible player name check");
            keyword.put(".getDisplayName().equals", "[WARN] Possible player name check");
            keyword.put(".setBanned(", "[WARN] Banning player");
            keyword.put("new URL(", "[WARN] Setting up URL connection");
            keyword.put(".openConnection(", "[WARN] Opens URL connection");
            keyword.put(".dispatchCommand(", "[WARN] Dispatches a command");
            keyword.put("http", "[WARN] Making HTTP(S) connection");
            keyword.put("getDefinedMethod", "[WARN] Getting defined method");
            keyword.put("getMethod", "[WARN] Getting method");
            keyword.put("ClassLoader.class", "[WARN] Use of ClassLoader");
            keyword.put("hack", "[WARN] Use of the string \"hack\"");
            keyword.put(".setOp(", "[SEVERE] Setting OP status");
            keyword.put("backdoor", "[SEVERE] Use of the string \"backdoor\"");
            keyword.put("abstract enum", "[SEVERE] Use of abstract enum - investigate");
            keyword.put("\"op ", "[SEVERE] Setting op status");
            keyword.put("org.ow2", "[SERVERE] Using ASM");
            keyword.put("org.objectweb.asm", "[SERVERE] Using ASM");
        }
	    
	    this.actionlist.setListData(keyword.keySet().toArray());
	    this.actionlist.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
			    if (e.getButton() == 3) {
			        int index = actionlist.locationToIndex(e.getPoint());
			        actionlist.setSelectedIndex(index);
			        final String name = (String) actionlist.getSelectedValue();
			        if (name == null) return;
			        JPopupMenu popup = new JPopupMenu();
			        for (String ac : new String[]{"Add","Edit","Delete"}) {
			          JMenuItem menuItem = new JMenuItem(ac);
			          menuItem.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent e) {
						    JMenuItem source = (JMenuItem)e.getSource();
						    String action = source.getText();
						    if (action.equalsIgnoreCase("edit")){
						    	String check = JOptionPane.showInputDialog("Enter the new keyword(s) to search for:");
						    	if(check==null||check.length()<1){
						    		errorMessage("You must enter a string!");
						    		return;
						    	}
						    	String message = JOptionPane.showInputDialog("Enter the message to display:");
						    	if(message==null||message.length()<1){
						    		errorMessage("You must enter a string!");
						    		return;
						    	}
						    	keyword.remove(name);
						    	keyword.put(check, message);
						    	actionlist.setListData(keyword.keySet().toArray());
						    	System.out.println("\nEditted Keyword: "+name+" to "+check+" : "+message+"\n");
						    }
						    if (action.equalsIgnoreCase("add")){
						    	String check = JOptionPane.showInputDialog("Enter the keyword(s) to search for:");
						    	if(check==null||check.length()<1){
						    		errorMessage("You must enter a string!");
						    		return;
						    	}
						    	String message = JOptionPane.showInputDialog("Enter the message to display:");
						    	if(message==null||message.length()<1){
						    		errorMessage("You must enter a string!");
						    		return;
						    	}

						    	keyword.put(check, message);
						    	actionlist.setListData(keyword.keySet().toArray());
						    	System.out.println("\nAdded Keyword: "+check+" : "+message+"\n");
						    }
						    if (action.equalsIgnoreCase("delete")){
						    	keyword.remove(name);
						    	actionlist.setListData(keyword.keySet().toArray());
						    	System.out.println("\nRemoved Keyword: "+name+"\n");
						    }

							
						}
			        	  
			          });
			          popup.add(menuItem);
			        }
			        popup.show(e.getComponent(), e.getX(), e.getY());
			      }
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
	    	
	    });
	    p2.setLayout(new BoxLayout(p2, 1));
	    p2.setBorder(BorderFactory.createTitledBorder("Keywords"));
	    p2.add(new JScrollPane(this.actionlist));
		JSplitPane sp = new JSplitPane(1, panel, p2);
	    sp.setBorder(new BevelBorder(1));
	    sp.setDividerSize(10);
	    sp.setOneTouchExpandable(true);
	    sp.setResizeWeight(0.5D);
	    sp.setDividerLocation(750);
	    
		console = new Console(mdt);
		this.add(sp);
	    this.addWindowListener(this);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);		
		this.setVisible(true);
		pw.close();
	}

	public void errorMessage(String string) {
		JOptionPane.showMessageDialog(null, string, "Error!", 1);
	}


	public static String PATH = new File(MasterPluginDatabase.class.getProtectionDomain()
			.getCodeSource().getLocation().getPath()).getAbsolutePath()
			.replace(File.separator + "mbd.jar", "");
	
	public static void main(String[] args){
		String username = new String();
		String password = new String();
		for(String ar:args){
			if(ar.contains("=")){
				String[] var = ar.split("=");
				if(var[0].contains("u"))
					username = var[1];
				if(var[0].contains("p"))
					password = var[1];
			}
		}
		if(username.length()<1 || password.length()<1){
			System.out.println("Please enter your username.");
			Scanner scan = new Scanner(System.in);
			username = scan.nextLine();
			System.out.println("Please enter your password.");
			password = scan.nextLine();
		}
		try{
			String prop = "submitted=1&username="
					+ URLEncoder.encode(username, "UTF-8") + "&password=" 
					+ URLEncoder.encode(password, "UTF-8"); 
			HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://server.modcrafting.com:2063/check.php").openConnection()));
			httpcon.setDoOutput(true);
			httpcon.setDoInput(true);
			httpcon.setInstanceFollowRedirects(false); 
			httpcon.setRequestMethod("POST");
			httpcon.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			httpcon.setRequestProperty("Content-Length", "" + Integer.toString(prop.getBytes().length));
			DataOutputStream wr = new DataOutputStream(httpcon.getOutputStream());
			wr.writeBytes(prop);
			wr.flush();
			wr.close();
			httpcon.disconnect();
			StringBuilder builder = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
			String line;
			while((line = br.readLine()) != null) {
				builder.append(line);
			}
			br.close();
			final String[] arg = builder.toString().split(",");
			if(arg.length<1){
				log.severe("Unable to connect to the site.");
				System.exit(0);
			}
			if(arg[0].equalsIgnoreCase("get")){
				log.severe("Incorrect Username or Password.");
				System.exit(0);
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						UIManager.setLookAndFeel(UIManager.
												getSystemLookAndFeelClassName());
					} catch (Exception e) {
						e.printStackTrace(); // Never happens
					}
					Toolkit.getDefaultToolkit().setDynamicLayout(true);
					Properties props = new Properties();
					props.put("autoReconnect", "true");
					props.put("user", arg[0]);
					props.put("password", arg[1]);
					props.put("useUnicode", "true");
					props.put("characterEncoding", "utf8");	
					new MasterPluginDatabase(props); 
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public Connection getConnection() {
		try {
			if(connection!=null && !connection.isClosed())
				return connection;
			setConnection(DriverManager.getConnection(database, properties));
			return connection;
		} catch (SQLException ex) {
			String message = ex.getCause().getMessage();
			if(message.contains("is not allowed to connect to this MySQL server")){
				log.severe("Unable to connection to database: Please check your Username and Password.");
			}else{
				log.severe("Unable to connect to the site.");
				ex.printStackTrace();
			}
			System.exit(0);
		}
		return null;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void handleFiles(final List<File> files) {
		if (files.size() == 0) {
			Log("No File(s) selected.");
			return;
		}
				for (final File f:files) {
					if (!f.isDirectory() && f.getName().toLowerCase().contains(".jar")) 
						new Thread(new Runnable(){
							@Override
							public void run() {
								new DecompJar(f, datab, keyword);
								
							}
						}).start();
				}
	}

	private void Log(String string) {
		log.info(string);		
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
		int value = JOptionPane.showConfirmDialog(ev.getWindow(),"Are you sure you want to close?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
		if(value==JOptionPane.CANCEL_OPTION && value==JOptionPane.NO_OPTION){
			this.setVisible(true);
		}else if(value==JOptionPane.YES_OPTION){
		    try {
                SLAPI.save(keyword, "keywords.bin");
            } catch (Exception e) {
                log.info("Failed to save keywords list.");
            }
			this.dispose();
			ImageIcon img = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"bukkit.png");
			JOptionPane.showMessageDialog(null, "MBD: Bukkit or Die.\nVersion: 0.2\nBy: Deathmarine", "Good Bye.", JOptionPane.PLAIN_MESSAGE, img);
			System.exit(0);
		
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
	
}
