package com.modcrafting.mbd.queue;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import javax.swing.JTable;

import com.modcrafting.mbd.Chekkit;
import com.modcrafting.mbd.objects.UserPassWindow;

public class QueueWindow extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1856749858855789365L;
    private JPanel contentPane;
    private JTable table;

    /**
     * Create the frame.
     */
    public QueueWindow(Boolean useNimbus) {
        String key = null;
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream(Chekkit.PATH + File.separator + "config.properties"));
            key = prop.getProperty("key");
        } catch (Exception e) {
            // Ignore it
        }
        if (key == "" || key == null || (BukkitDevTools.checkAPIKey(key) != KeyState.STAFF)) {
            Boolean in = true;
            while (in) {
                key = JOptionPane.showInputDialog(this, "Enter your BukkitDev API key:",  "More info required!", JOptionPane.INFORMATION_MESSAGE);
                if (key != null) {
                    KeyState ks = BukkitDevTools.checkAPIKey(key);
                    if (ks != KeyState.STAFF) {
                        if (ks == KeyState.NORMAL) {
                            JOptionPane.showMessageDialog(this, "The API key you supplied was for a non-staff account.", "Invalid API key", JOptionPane.WARNING_MESSAGE);
                        }
                        if (ks == KeyState.INVALID) {
                            JOptionPane.showMessageDialog(this, "The API key you supplied was invalid (not a valid account).", "Invalid API key", JOptionPane.WARNING_MESSAGE);
                        }
                        if (ks == null) {
                            JOptionPane.showMessageDialog(this, "There was an error validating the key.", "API key verification failure", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        in = false;
                    }
                } else {
                    in = false;
                }
            }
            
            if (key == null) {
                return;
            } else {
                
                try {
                    Properties prop = new Properties();
                    prop.load(new FileInputStream(Chekkit.PATH + File.separator + "config.properties"));
                    prop.setProperty("key", key);
                    prop.store(new FileOutputStream(Chekkit.PATH + File.separator + "config.properties"), "The main config");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
            
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();

        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        String[] columnNames = { "File title", "Author", "Size", "Submitted", "" };

        table = new JTable(new FileTableModel());

        contentPane.add(table, BorderLayout.CENTER);

        this.setVisible(true);
    }

}
