package com.modcrafting.mbd;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
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
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.modcrafting.mbd.decom.DecompJar;
import com.modcrafting.mbd.objects.MDTextArea;
import com.modcrafting.mbd.sql.SQL;

public class MasterPluginDatabase extends JFrame implements WindowListener{
	private static final long serialVersionUID = 2878574498207291074L;
	public final static String database = "jdbc:mysql://server.modcrafting.com:3306/dbo_master";
	public final static Logger log = Logger.getLogger("MasterPluginDatabase");
	public Properties properties;
	private Connection connection;
	public Console console;
	public SQL datab;
	public MasterPluginDatabase(Properties properties){
		this.properties = properties;
		getConnection();
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
		console = new Console(mdt);
		this.add(panel);
	    this.addWindowListener(this);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);		
		this.setVisible(true);
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
			System.out.println("Please enter your usename.");
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
					if (!f.isDirectory() && f.getName().contains(".jar")) 
						new Thread(new Runnable(){
							@Override
							public void run() {
								new DecompJar(f, datab);
								
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
			this.dispose();
			ImageIcon img = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"bukkit.png");
			JOptionPane.showMessageDialog(null, "MBD: Bukkit or Die.\nVersion: 0.1\nBy: Deathmarine", "Good Bye.", JOptionPane.PLAIN_MESSAGE, img);
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
