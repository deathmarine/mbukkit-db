package com.modcrafting.mbd;

import java.awt.Dimension;
import java.awt.Frame;
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
import com.modcrafting.mbd.objects.KeywordFrame;
import com.modcrafting.mbd.objects.MDTextArea;
import com.modcrafting.mbd.objects.ProcessPanel;
import com.modcrafting.mbd.objects.UserPassWindow;
import com.modcrafting.mbd.queue.QueueWindow;
import com.modcrafting.mbd.sql.SQL;

@SuppressWarnings({"rawtypes", "resource"})
public class Chekkit extends JFrame implements WindowListener {
    private static final long serialVersionUID = 2878574498207291074L;
    public final static Logger log = Logger.getLogger("Chekkit");
    public Properties properties;
    public static Boolean useNimbus = false;
    public static Boolean hideProgress = false;
    public Console console;
    public static ProcessPanel processPanel = new ProcessPanel();
    public SQL datab;
    public static String username = new String();
    private Map<String, String> keyword = new HashMap<String, String>();
    private List<String> bannedpackage = new ArrayList<String>();
    private static Theme theme;
    public static final String VERSION = "1.5";
    public static String PATH = new File(Chekkit.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath().replace(File.separator + "Chekkit.jar", "").replace(File.separator + "mbd.jar", "");

    @SuppressWarnings("unchecked")
    public Chekkit(Properties properties) {
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
        fileMenu.add(openMenuItem);
        fileMenu.add(queueMenuItem);
        fileMenu.add(exitMenuItem);
        consoleMenu.add(clearMenuItem);
        consoleMenu.add(keysMenuItem);

        keysMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new KeywordFrame(keyword);
            }
        });

        queueMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable(){
                	@Override
                	public void run(){
                		new QueueWindow(useNimbus, Chekkit.this);
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
                        for (File f : fileArray) {
                            fileList.add(f);
                        }
                        handleFiles(fileList);
                    } else {
                        System.out.println("Cancelled file selection");
                    }
                }
            }
        });
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Frame frame = Frame.getFrames()[0];
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
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
        this.add(sp);
        this.addWindowListener(this);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
    }

    public void errorMessage(String string) {
        JOptionPane.showMessageDialog(null, string, "Error!", 1);
    }

    public static void main(String[] args) {
        List<String> argList = new ArrayList<String>();
        for (String s : args) {
            argList.add(s);
        }
        File propF = new File(Chekkit.PATH + File.separator + "config.properties");
        if (propF.exists()) {
            try {
                Properties config = new Properties();
                config.load(new FileInputStream(propF));
                useNimbus = Boolean.parseBoolean((String) config.get("enable-nimbus"));
                hideProgress = Boolean.parseBoolean((String) ("hide-progress"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Properties config = new Properties();
                config.put("enable-nimbus", useNimbus.toString());
                config.put("hide-progress", hideProgress.toString());
                config.store(new FileOutputStream(propF), "The Chekkit config.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://server.modcrafting.com:2063/check.php").openConnection()));
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
                SwingUtilities.invokeLater(new Runnable() {
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
        if (files.size() == 0) {
            Log("No File(s) selected.");
            return;
        }
        for (final File f : files) {
            if (!f.isDirectory() && f.getName().toLowerCase().contains(".jar")) {
                try {
                    log.info("Removing all Java source files from ZIP...");
                    deleteCodeFiles(f);
                } catch (IOException e) {
                    log.severe("Java source file removal failed!");
                    log.severe(e.getMessage());
                    e.printStackTrace();

                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        new DecompJar(f, datab, keyword, bannedpackage, hideProgress, useNimbus);

                    }
                }).start();
            }
        }
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
            ImageIcon img = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/bukkit-icon-small.png")));
            JOptionPane.showMessageDialog(null, "Chekkit or Die.\nVersion: " + Chekkit.VERSION + "\nDeathmarine, lol768, zeeveener", "Good Bye.", JOptionPane.PLAIN_MESSAGE, img);
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
}
