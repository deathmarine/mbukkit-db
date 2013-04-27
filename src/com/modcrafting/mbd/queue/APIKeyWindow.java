package com.modcrafting.mbd.queue;

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
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.modcrafting.mbd.Chekkit;

public class APIKeyWindow extends JFrame implements ActionListener, KeyListener {

    private static final long serialVersionUID = 6328274426699959338L;
    private String key = "";

    private JPanel panel;
    private JButton submit;
    private JButton cancel;
    private JTextField keyField;
    private JLabel errorArea;

    public boolean isOpen = true;

    public APIKeyWindow(Boolean useNimbus) {

        super("I need more info!");
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
        this.setSize(300, 150);
        this.setAlwaysOnTop(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width / 2 - (this.getSize().width / 2), screenSize.height / 2 - (this.getSize().height / 2));
        this.setContents();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(panel);
        this.setVisible(true);
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
        SpringLayout layout = new SpringLayout();
        submit = new JButton("Save");
        submit.addActionListener(this);
        layout.putConstraint(SpringLayout.SOUTH, submit, 0, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.WEST, submit, 0, SpringLayout.WEST, panel);

        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        layout.putConstraint(SpringLayout.SOUTH, cancel, 0, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.EAST, cancel, 0, SpringLayout.EAST, panel);

        panel.setLayout(layout);

        JPanel pane = new JPanel();
        keyField = new JTextField("", 16);
        keyField.addKeyListener(this);
        keyField.setText(key);
        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
        pane.add(new JLabel("API Key:    "));
        pane.add(keyField);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, pane, -30, SpringLayout.VERTICAL_CENTER, panel);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pane, 0, SpringLayout.HORIZONTAL_CENTER, panel);
        panel.add(pane);

        errorArea = new JLabel("");
        errorArea.setVisible(false);
        errorArea.setForeground(Color.RED);
        submit.setSize(50, 15);
        cancel.setSize(50, 15);
        panel.add(submit);
        panel.add(cancel);
        panel.add(errorArea);
    }

    public String getKey() {
        return this.key;
    }

    public boolean checkKey(String key) {
        // TODO: Check the API key
        try {
            Properties rememberMe = new Properties();
            rememberMe.put("key", key);
            FileOutputStream fos = new FileOutputStream(Chekkit.PATH + File.separator + "config.properties");
            rememberMe.store(fos, "The main config.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.submit) {
            try {
                if (this.errorArea.isVisible()) {
                    this.errorArea.setVisible(false);
                }
                if (this.keyField.getText().equals("")) {
                    this.errorArea.setText("Please enter an API key.");
                    this.errorArea.setVisible(true);
                } else {
                    this.key = this.keyField.getText();
                    if (this.checkKey(key)) {
                        this.isOpen = false;
                    }
                }
            } catch (NullPointerException ex) {
                this.errorArea.setText("Please enter your API key");
                this.errorArea.setVisible(true);
            }
        } else if (e.getSource() == this.cancel) {
            System.exit(0);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == this.keyField) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                this.submit.doClick();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                this.cancel.doClick();
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
