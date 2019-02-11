package com.modcrafting.mbd;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.BevelBorder;

import org.fife.ui.rsyntaxtextarea.Theme;

import com.modcrafting.mbd.decom.DecompJar;
import com.modcrafting.mbd.objects.Configuration;
import com.modcrafting.mbd.objects.KeywordFrame;
import com.modcrafting.mbd.objects.MDTextArea;
import com.modcrafting.mbd.objects.ProcessPanel;
import com.modcrafting.mbd.objects.UserPassWindow;
import com.modcrafting.mbd.queue.QueueWindow;
import com.modcrafting.mbd.sql.SQL;

@SuppressWarnings({"rawtypes" })
public class Chekkit extends JFrame implements WindowListener {
    private static final long serialVersionUID = 2878574498207291074L;
    public final static Logger log = Logger.getLogger("Chekkit");
    public Properties properties;
    public static Configuration config;
    public static Boolean useNimbus = false;
    public static Boolean hideProgress = false;
    public Console console;
    public static ProcessPanel processPanel = new ProcessPanel();
    public SQL datab;
    public static String username = new String();
    public static Map<String, String> keyword = new HashMap<String, String>();
    public static List<String> bannedpackage = new ArrayList<String>();
    private static Theme theme;
    public static final String VERSION = "1.6";
    public static final String JENKINS_BUILD = "#JENKINSBUILDNUMBER";
    public static Boolean showAbout = true;
    public static String bukkitDevUsername;
    public static String chekkitUsername;
    public static final String USER_AGENT = "Chekkit " + VERSION;
    public static String PATH = "unknown";
    private List<DecompJar> decompilingJars = Collections.synchronizedList(new ArrayList<DecompJar>());


    @SuppressWarnings("unchecked")
    public Chekkit(Properties properties) {
        
        
        System.out.println(PATH);
        this.properties = properties;
        
        datab = new SQL(properties);

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
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension center = new Dimension((int) (screenSize.width * 0.75), (int) (screenSize.height * 0.75));
        final int x = (int) (center.width * 0.2);
        final int y = (int) (center.height * 0.2);
        this.setBounds(x, y, center.width, center.height);
        this.setTitle(this.getClass().getSimpleName());
        String osType = System.getProperties().getProperty("os.name").toLowerCase();
        if (osType.contains("mac")) {
            try {
                // No touchy!
                Image image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/bukkit-icon.png"))).getImage();
                Class util = Class.forName("com.apple.eawt.Application");
                Method getApplication = util.getMethod("getApplication", new Class[0]);
                Object application = getApplication.invoke(util);
                Class params[] = new Class[1];
                params[0] = Image.class;
                Method setDockIconImage = util.getMethod("setDockIconImage", params);
                setDockIconImage.invoke(application, image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Image image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/bukkit-icon.png"))).getImage();
            setIconImage(image);
        }

        // Setup Console
        MDTextArea mdt = new MDTextArea(this);
        JPanel panel = new JPanel();
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu consoleMenu = new JMenu("Console");
        final JMenuItem openMenuItem = new JMenuItem("Open");
        final JFileChooser jfm = new JFileChooser();
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        JMenuItem clearMenuItem = new JMenuItem("Clear");
        JMenuItem keysMenuItem = new JMenuItem("Keywords");
        JMenuItem queueMenuItem = new JMenuItem("Open Queue");
        JMenuItem downloadsItem = new JMenuItem("Open downloads");
        fileMenu.add(openMenuItem);
        fileMenu.add(queueMenuItem);
        fileMenu.add(downloadsItem);
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAbout();
            }
        });
        fileMenu.add(aboutMenuItem);
        fileMenu.add(exitMenuItem);
        consoleMenu.add(clearMenuItem);
        consoleMenu.add(keysMenuItem);

        keysMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new KeywordFrame(keyword);
            }
        });
        
        downloadsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    java.awt.Desktop.getDesktop().open(new File(Chekkit.PATH + File.separator + "downloads"));
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    Chekkit.log.severe("Desktop API not supported.");
                }
            }
        });
        
        queueMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int processID = Chekkit.processPanel.addUnknownProcess("Checking API key information...");
                        new QueueWindow(useNimbus, Chekkit.this, processID);
                    }
                }).start();
            }
        });

        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == openMenuItem) {
                    jfm.setMultiSelectionEnabled(true);
                    int value = jfm.showOpenDialog(Chekkit.this);
                    System.out.println(value);
                    if (value == JFileChooser.APPROVE_OPTION) {
                        System.out.println("Approved");
                        File[] fileArray = jfm.getSelectedFiles();
                        List<File> fileList = new ArrayList<File>();
                        fileList.addAll(Arrays.asList(fileArray));
                        handleFiles(fileList);
                    } else {
                        System.out.println("Cancelled file selection");
                    }
                }
            }
        });
    	final Chekkit c = this;
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                c.dispatchEvent(new WindowEvent(c, WindowEvent.WINDOW_CLOSING));
            }
        });
        clearMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                console.getText().setText("Drag files Into the text box to analyze.\nOr select file(s) from the menu.\n");
            }
        });

        menuBar.add(fileMenu);
        menuBar.add(consoleMenu);
        this.setJMenuBar(menuBar);
        panel.setLayout(new BoxLayout(panel, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Console"));
        panel.add(new JScrollPane(mdt));
        JPanel p2 = new JPanel();
        try {
            keyword = (Map<String, String>) SLAPI.load("keywords.bin");
        } catch (Exception e) {
            log.info("Failed to load keywords list.");
            keyword.put(".getName().equals", "[WARN] Possible player name check");
            keyword.put(".getDisplayName().equals", "[WARN] Possible player name check");
            keyword.put(".setDisplayName(\"", "[WARN] Setting player display name directly");
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
            keyword.put("org.ow2", "[SEVERE] Using ASM");
            keyword.put("org.objectweb.asm", "[SEVERE] Using ASM");
            keyword.put(".shutdown();", "[SEVERE] Shutdown server attempt.");
            keyword.put("Thread", "[WARN] Odd Use of threading.");
            keyword.put("Process", "[SEVERE] Execution of external processes.");
            keyword.put("System.getSecurityManager()", "[SEVERE] Checking for security manager.");
            keyword.put("System.set", "[SEVERE] Attempt to modify system configuration.");
            keyword.put("Runtime.getRuntime()", "[SEVERE] Runtime modification.");
            keyword.put("opme", "[SEVERE] Investigate.");

        }
        bannedpackage.add("org.bukkit");
        bannedpackage.add("lib.PatPeter");
        bannedpackage.add("org.kitteh.tagapi");
        bannedpackage.add("com.comphenix.protocol");

        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.setBorder(BorderFactory.createTitledBorder("Processes"));
        p2.add(processPanel);
        JSplitPane sp = new JSplitPane(1, panel, p2);
        sp.setBorder(new BevelBorder(1));
        sp.setDividerSize(10);
        sp.setOneTouchExpandable(true);
        sp.setResizeWeight(0.5D);
        sp.setDividerLocation(750);

        console = new Console(mdt);
        getContentPane().add(sp);
        this.addWindowListener(this);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
    }

    public void errorMessage(String string) {
        JOptionPane.showMessageDialog(null, string, "Error!", 1);
    }
    
    public static void putToday(Configuration config) {
        int day = Calendar.DAY_OF_MONTH;
        config.set("today", Integer.toString(day));
        config.set("files-reviewed", Integer.toString(0));
    }

    public static void main(String[] args) {
        try {
            if (Chekkit.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar")) {
                PATH = new File(Chekkit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getAbsolutePath() + File.separator;
            } else {
                PATH = Chekkit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            }
            
        } catch(Exception e) {}
        System.out.println(PATH);
        config = new Configuration();
        
        
        
        if (!config.contains("version-pm-days")) {
            config.set("version-pm-days", "7");
        }
        
        if (!config.contains("version-pm-msg")) {
            String msg = "Hi :authorName:!\n\n" +
                    "Thanks for uploading your file for [[:projectURL:|:projectName:]]. Before it can be approved, you need to edit the file and add a version number to it. Example: :titleExample:." +
                    "\n\nTo do this now, hit the button below and add the version to the 'Name' field." +
                    "\n\n:editButton:\n\nPlease note that if you do not add a version before :deadline:, your file will be deleted and you'll need to upload it again." +
                    "\n\n**Once you've added your version, you should reply to this PM to let me know your file can be approved. This will save time for you and me.**" +
                    "\n\nThanks!";
            config.set("version-pm-msg", msg);
        }
        
        if (!config.contains("version-pm-subject")) {
            String subject = "Your file, :fileTitle:";
            config.set("version-pm-subject", subject);
        }
        
        if (config.getMenteeModeEnabled()) {
            if (config.getInteger("today") == null || config.getInteger("today") != Calendar.DAY_OF_MONTH) {
                putToday(config);
            }
            
        }
        useNimbus = config.getUseNimbus();
        showAbout = config.getShowAbout();
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
        if (Chekkit.PATH.contains(".jar")) {
            JOptionPane.showMessageDialog(null, "Please ensure the Chekkit JAR file is named 'Chekkit.jar' or 'mbd.jar'.\n" + Chekkit.PATH);
            System.exit(0);
        }

        List<String> argList = new ArrayList<String>();
        argList.addAll(Arrays.asList(args));

        //String username = new String();
        String password = new String();
        if (argList.contains("--nogui")) {
            for (String ar : args) {
                if (ar.contains("=")) {
                    String[] var = ar.split("=");
                    if (var[0].contains("u"))
                        username = var[1];
                    if (var[0].contains("p"))
                        password = var[1];
                }
            }
            if (username.length() < 1 || password.length() < 1) {
                System.out.println("Please enter your username.");
                Scanner scan = new Scanner(System.in);
                username = scan.nextLine();
                System.out.println("Please enter your password.");
                password = scan.nextLine();
                scan.close();
            }
            try {
                String prop = "submitted=1&username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
                HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://174.34.167.26:2063/check.php").openConnection()));
                httpcon.setDoOutput(true);
                httpcon.setDoInput(true);
                httpcon.setInstanceFollowRedirects(false);
                httpcon.setRequestMethod("POST");
                httpcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpcon.setRequestProperty("Content-Length", "" + Integer.toString(prop.getBytes().length));
                DataOutputStream wr = new DataOutputStream(httpcon.getOutputStream());
                wr.writeBytes(prop);
                wr.flush();
                wr.close();
                httpcon.disconnect();
                StringBuilder builder = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                br.close();
                final String[] arg = builder.toString().split(",");
                if (arg.length < 1) {
                    log.severe("Unable to connect to the site.");
                    System.exit(0);
                }
                if (arg[0].equalsIgnoreCase("get")) {
                    log.severe("Incorrect Username or Password.");
                    System.exit(0);
                }
                Chekkit.chekkitUsername = username;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
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
                        Toolkit.getDefaultToolkit().setDynamicLayout(true);
                        Properties props = new Properties();
                        props.put("autoReconnect", "true");
                        props.put("user", arg[0]);
                        props.put("password", arg[1]);
                        props.put("useUnicode", "true");
                        props.put("characterEncoding", "utf8");
                        new Chekkit(props);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            UserPassWindow upw = new UserPassWindow(useNimbus);
            final String user = upw.getUsername();
            final String pass = upw.getPassword();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (Exception e) {
                        e.printStackTrace(); // Never happens
                    }
                    Toolkit.getDefaultToolkit().setDynamicLayout(true);
                    Properties props = new Properties();
                    props.put("autoReconnect", "true");
                    props.put("user", user);
                    props.put("password", pass);
                    props.put("useUnicode", "true");
                    props.put("characterEncoding", "utf8");
                    new Chekkit(props);
                }
            });
        }
    }

    public void deleteCodeFiles(File zipFile) throws IOException {
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null, zipFile.getParentFile());

        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();
        tempFile.deleteOnExit();

        /*
         * try { Files.move(zipFile.toPath(), tempFile.toPath());
         * 
         * } catch (IOException e) { e.printStackTrace(); }
         */

        if (!zipFile.renameTo(tempFile)) {
            throw new IOException("Could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[1024];

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile));

        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean toBeDeleted = false;
            if (name.endsWith(".java")) {
                toBeDeleted = true;
                log.info("Deleting " + name);
            }
            if (!toBeDeleted) {
                // Add ZIP entry to output stream.
                zout.putNextEntry(new ZipEntry(name));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    zout.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
        // Complete the ZIP file
        zout.close();
        tempFile.delete();
    }

    public void handleFiles(final List<File> files) {
        if (files.isEmpty()) {
            Log("No File(s) selected.");
            return;
        }
        for (final File f : files) {
            if (!f.isDirectory() && (f.getName().toLowerCase().contains(".jar") ||
            		f.getName().toLowerCase().contains(".zip"))) {
                try {
                    log.info("Removing all Java source files from ZIP...");
                    deleteCodeFiles(f);
                } catch (IOException e) {
                    log.severe("Java source file removal failed!");
                    log.severe(e.getMessage());
                    e.printStackTrace();
                    return;
                }
                try {
					for(final File fil : goForZip(f)){
						System.out.println("Opening "+fil+".");
					    final Chekkit c = this;
					    new Thread(new Runnable() {
					        @Override
					        public void run() {
					            decompilingJars.add(new DecompJar(c, fil, datab, hideProgress, useNimbus));
					        }
					    }).start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
    }

    private File[] goForZip(File f) throws IOException {
    	List<File> temp = new ArrayList<File>();
        byte[] buf = new byte[1024];
        ZipInputStream zin = new ZipInputStream(new FileInputStream(f));
        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            if (name.endsWith(".jar")||name.endsWith(".zip")) {
            	File tempFile = new File(new File(PATH+File.separator), name);
            	tempFile.createNewFile();
            	
                FileOutputStream fout = new FileOutputStream(tempFile);
                int len;
                while ((len = zin.read(buf)) > 0) {
                    fout.write(buf, 0, len);
                }
                fout.close();
                deleteCodeFiles(tempFile);
                temp.add(tempFile);
            }
            entry = zin.getNextEntry();
        	
        }
        zin.close();
        temp.add(f);
        
		return temp.toArray(new File[]{});
	}

	private void Log(String string) {
        log.info(string);
    }

    @Override
    public void windowClosing(WindowEvent ev) {
        int value = JOptionPane.showConfirmDialog(ev.getWindow(), "Are you sure you want to close?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (value == JOptionPane.CANCEL_OPTION && value == JOptionPane.NO_OPTION) {
            this.setVisible(true);
        } else if (value == JOptionPane.YES_OPTION) {
            try {
                SLAPI.save(keyword, "keywords.bin");
            } catch (Exception e) {
                log.info("Failed to save keywords list.");
            }
            this.dispose();
            
            if (showAbout) {
               
            }
            System.exit(0);
        }
    }

    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
    }

    public static Theme getTheme() {
        return theme;
    }

    public static void setTheme(Theme theme) {
        Chekkit.theme = theme;
    }
    
    public void showAbout() {
        ImageIcon img = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/bukkit-icon-small.png")));
        if (Chekkit.JENKINS_BUILD.contains("JENKINS") || !Chekkit.JENKINS_BUILD.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(null, "Chekkit or Die.\nVersion: " + Chekkit.VERSION + " - Custom build.\nDeathmarine, lol768, zeeveener", "About Chekkit", JOptionPane.PLAIN_MESSAGE, img);
        } else {
            JOptionPane.showMessageDialog(null, "Chekkit or Die.\nVersion: " + Chekkit.VERSION + " - Jenkins build: " + Chekkit.JENKINS_BUILD + "\nDeathmarine, lol768, zeeveener", "About Chekkit", JOptionPane.PLAIN_MESSAGE, img);            
        }
    }
}
