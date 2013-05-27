package com.modcrafting.mbd.objects;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.modcrafting.mbd.Chekkit;
import javax.swing.JProgressBar;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.Box;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.Font;

public class UserPassWindow extends JFrame implements ActionListener, KeyListener {

    private static final long serialVersionUID = 6328274426699959338L;
    private String password = "";

    private JPanel panel;
    private JButton submit;
    private JTextField userField;
    private JPasswordField passField;
    private JLabel errorArea;
    private JCheckBox remember;

    public boolean isOpen = true;
    private JPanel pane_1;
    private SpringLayout sl_panel;
    private JProgressBar progressBar;
    private JPanel pane;
    private Component horizontalStrut_2;
    private Component horizontalStrut_3;
    private String type = "wait";
    private JPanel panel_2;

    public UserPassWindow(Boolean useNimbus) {

        super("Login");
        setResizable(false);
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
        panel = new JPanel();
        panel.setBounds(0, 0, 323, 145);
        this.setSize(329, 184);
        this.setAlwaysOnTop(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width / 2 - (this.getSize().width / 2), screenSize.height / 2 - (this.getSize().height / 2));
        this.setContents();
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        getContentPane().add(panel);

        horizontalStrut_2 = Box.createHorizontalStrut(20);
        pane.add(horizontalStrut_2);
        sl_panel.putConstraint(SpringLayout.WEST, horizontalStrut_2, 6, SpringLayout.EAST, pane);
        sl_panel.putConstraint(SpringLayout.SOUTH, horizontalStrut_2, 0, SpringLayout.SOUTH, pane);

        JPanel panel_1 = new JPanel();
        sl_panel.putConstraint(SpringLayout.NORTH, panel_1, 14, SpringLayout.NORTH, progressBar);
        sl_panel.putConstraint(SpringLayout.WEST, panel_1, 0, SpringLayout.WEST, panel);
        sl_panel.putConstraint(SpringLayout.SOUTH, panel_1, 10, SpringLayout.SOUTH, panel);
        sl_panel.putConstraint(SpringLayout.EAST, panel_1, 0, SpringLayout.EAST, panel);
        panel.add(panel_1);

        Component horizontalGlue = Box.createHorizontalGlue();
        panel_1.add(horizontalGlue);
        errorArea = new JLabel(" ");
        errorArea.setVerticalAlignment(SwingConstants.TOP);
        panel_1.add(errorArea);
        sl_panel.putConstraint(SpringLayout.NORTH, errorArea, 4, SpringLayout.SOUTH, progressBar);
        sl_panel.putConstraint(SpringLayout.WEST, errorArea, 0, SpringLayout.WEST, progressBar);
        sl_panel.putConstraint(SpringLayout.EAST, errorArea, 0, SpringLayout.EAST, progressBar);
        errorArea.setVisible(false);
        errorArea.setForeground(Color.RED);

        Component horizontalGlue_2 = Box.createHorizontalGlue();
        panel_1.add(horizontalGlue_2);
        
        this.setVisible(true);
        String build = (Chekkit.JENKINS_BUILD.contains("JENKINS")) ? "Custom build" : Chekkit.JENKINS_BUILD;
        this.errorArea.setText("Version " + Chekkit.VERSION + " - " + build);
        this.errorArea.setForeground(Color.BLUE);
        this.errorArea.setVisible(true);
        
        if (type != null && type.equals("immediate")) {
            submit.doClick();
        }
        
        
        
        while (this.isOpen) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.dispose();
    }

    public void setContents() {
        String user = "";
        String pass = "";
        
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(Chekkit.PATH + File.separator + "authentication.property"));
            user = prop.getProperty("username");
            pass = prop.getProperty("password");
            type = prop.getProperty("type");
            
        } catch (Exception e) {
            //Ignore it
        }

        pane = new JPanel();
        passField = new JPasswordField("", 16);
        passField.addKeyListener(this);
        pane_1 = new JPanel();
        userField = new JTextField("", 16);
        userField.setFont(new Font("Dialog", Font.PLAIN, 11));
        userField.addKeyListener(this);
        sl_panel = new SpringLayout();
        sl_panel.putConstraint(SpringLayout.NORTH, pane, 6, SpringLayout.SOUTH, pane_1);
        sl_panel.putConstraint(SpringLayout.WEST, pane, 0, SpringLayout.WEST, panel);
        sl_panel.putConstraint(SpringLayout.NORTH, pane_1, 10, SpringLayout.NORTH, panel);
        sl_panel.putConstraint(SpringLayout.WEST, pane_1, 0, SpringLayout.WEST, panel);
        sl_panel.putConstraint(SpringLayout.SOUTH, pane_1, 35, SpringLayout.NORTH, panel);
        panel.setLayout(sl_panel);
        userField.setText(user);
        pane_1.setLayout(new BoxLayout(pane_1, BoxLayout.X_AXIS));

        Component horizontalStrut = Box.createHorizontalStrut(20);
        pane_1.add(horizontalStrut);
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUsername.setPreferredSize(new Dimension(100,lblUsername.getHeight()));
        pane_1.add(lblUsername);
        
        Component horizontalGlue_1 = Box.createHorizontalGlue();
        pane_1.add(horizontalGlue_1);
        pane_1.add(userField);
        panel.add(pane_1);
        passField.setText(pass);
        pane.setLayout(new BoxLayout(pane, BoxLayout.LINE_AXIS));

        Component horizontalStrut_1 = Box.createHorizontalStrut(20);
        pane.add(horizontalStrut_1);
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPassword.setPreferredSize(new Dimension(100,lblUsername.getHeight()));
        pane.add(lblPassword);
        
        Component horizontalGlue = Box.createHorizontalGlue();
        pane.add(horizontalGlue);
        pane.add(passField);
        panel.add(pane);

        panel_2 = new JPanel();
        sl_panel.putConstraint(SpringLayout.NORTH, panel_2, 72, SpringLayout.NORTH, panel);
        sl_panel.putConstraint(SpringLayout.WEST, panel_2, 0, SpringLayout.WEST, panel);
        sl_panel.putConstraint(SpringLayout.EAST, panel_2, 0, SpringLayout.EAST, panel);
        sl_panel.putConstraint(SpringLayout.SOUTH, pane, -6, SpringLayout.NORTH, panel_2);
        sl_panel.putConstraint(SpringLayout.SOUTH, panel_2, -40, SpringLayout.SOUTH, panel);
        panel.add(panel_2);
        panel_2.setLayout(null);

        remember = new JCheckBox("Remember");
        remember.setBounds(18, 5, 110, 23);
        sl_panel.putConstraint(SpringLayout.NORTH, remember, 6, SpringLayout.NORTH, panel_2);
        sl_panel.putConstraint(SpringLayout.WEST, remember, 75, SpringLayout.WEST, panel_2);
        panel_2.add(remember);
        sl_panel.putConstraint(SpringLayout.EAST, remember, 0, SpringLayout.EAST, pane);
        submit = new JButton("Login");
        submit.setBounds(225, 2, 73, 28);
        sl_panel.putConstraint(SpringLayout.EAST, submit, -10, SpringLayout.EAST, panel_2);
        panel_2.add(submit);
        submit.addActionListener(this);
        progressBar = new JProgressBar();
        sl_panel.putConstraint(SpringLayout.WEST, progressBar, 8, SpringLayout.WEST, panel);
        sl_panel.putConstraint(SpringLayout.EAST, progressBar, -3, SpringLayout.EAST, panel);
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        sl_panel.putConstraint(SpringLayout.SOUTH, progressBar, -20, SpringLayout.SOUTH, panel);
        if (pass != "") {
            remember.setSelected(true);
        }
        
        


        horizontalStrut_3 = Box.createHorizontalStrut(20);
        pane_1.add(horizontalStrut_3);
        panel.add(progressBar);
        
    }

    public String getUsername() {
        return Chekkit.username;
    }

    public String getPassword() {
        return this.password;
    }
    
    public void setLabel(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                errorArea.setVisible(true);
                errorArea.setForeground(Color.RED);
                errorArea.setText(text);
            }
        });
    }
    
    public void hideLabel(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                errorArea.setVisible(false);
            }
        });
    }
    
    public void hideProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisible(false);
            }
        });
    }


    public boolean checkUserAndPass(String user, String pass) {
        try {
            String prop = "submitted=1&username=" + URLEncoder.encode(Chekkit.username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
            HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://server.modcrafting.com:2063/check.php").openConnection()));
            httpcon.setDoOutput(true);
            httpcon.setDoInput(true);
            httpcon.setInstanceFollowRedirects(false);
            httpcon.setRequestMethod("POST");
            httpcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpcon.setRequestProperty("Content-Length", "" + Integer.toString(prop.getBytes().length));
            httpcon.setConnectTimeout(3000);
            httpcon.setReadTimeout(3000);
            DataOutputStream wr = new DataOutputStream(httpcon.getOutputStream());
            wr.writeBytes(prop);
            wr.flush();
            wr.close();
            httpcon.disconnect();
            String ver = httpcon.getHeaderField("X-Chekkit-Version");
            try {
                float f = Float.parseFloat(ver);
                if (f < Float.parseFloat(Chekkit.VERSION)) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(null, "It appears you're using a later version than is defined as stable.\nYou can still use this build, but be aware it is under development.");
                        }

                    });

                } else {
                    if (ver != null) {
                        if (!ver.equals(Chekkit.VERSION)) {
                            setLabel("A different version, " + ver + " is available.");

                            return false;
                        }

                    } else {
                        System.out.println("WARN: Skipped update check due to missing header.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            StringBuilder builder = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            final String[] arg = builder.toString().split(",");
            if (arg.length < 1) {
                setLabel("Unable to connect to the site.");
                return false;
            }
            if (arg[0].equalsIgnoreCase("get")) {
                setLabel("Incorrect Username or Password.");
                return false;
            }

            Chekkit.chekkitUsername = user;

            if (this.remember.isSelected()) {
                try {
                    Properties rememberMe = new Properties();
                    rememberMe.put("username", user);
                    rememberMe.put("password", pass);
                    rememberMe.put("type", type);
                    FileOutputStream fos = new FileOutputStream(Chekkit.PATH + File.separator + "authentication.property");
                    rememberMe.store(fos, "Properties file if you've chosen to remember login details");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    File f = new File(Chekkit.PATH + File.separator + "authentication.property");
                    f.delete();
                } catch (Exception e) {

                }

            }

            Chekkit.username = arg[0];
            this.password = arg[1];
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            setLabel("A connection error ocurred while authenticating.");
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.submit) {
            try {
                if (this.errorArea.isVisible()) {
                    this.errorArea.setVisible(false);
                }
                if (this.userField.getText().equals("")) {
                    setLabel("Please enter a username");
                } else if (this.passField.getPassword().length == 0) {
                    setLabel("Please enter a password");
                } else {
                    this.errorArea.setText("Authenticating...");
                    this.errorArea.setForeground(Color.BLUE);
                    this.errorArea.setVisible(true);
                    Chekkit.username = this.userField.getText();
                    this.password = new String(this.passField.getPassword());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (checkUserAndPass(Chekkit.username, password)) {
                                SwingUtilities.invokeLater(new Runnable() {

                                    public void run() {
                                        isOpen = false;
                                    }

                                });
                            }
                            hideProgress();
                        }
                    });
                    t.start();

                }
            } catch (NullPointerException ex) {
                setLabel("Please fill in both fields.");
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == this.userField || e.getSource() == this.passField) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                this.submit.doClick();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
