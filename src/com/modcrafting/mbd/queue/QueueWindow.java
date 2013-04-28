package com.modcrafting.mbd.queue;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import javax.swing.JTable;

import com.modcrafting.mbd.Chekkit;
import com.modcrafting.mbd.objects.UserPassWindow;
import javax.swing.table.DefaultTableModel;
import javax.swing.SpringLayout;
import javax.swing.JProgressBar;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

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
        super("File queue");
        setIconImage(Toolkit.getDefaultToolkit().getImage(QueueWindow.class.getResource("/resources/bukkit-icon.png")));
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
        setBounds(100, 100, 463, 334);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        
        JMenuItem mntmExit = new JMenuItem("Exit");
        mnFile.add(mntmExit);
        
        JMenu mnView = new JMenu("View");
        menuBar.add(mnView);
        
        JCheckBoxMenuItem chckbxmntmShowClaimedFiles = new JCheckBoxMenuItem("Show claimed files");
        mnView.add(chckbxmntmShowClaimedFiles);
        
        JMenuItem mntmRefreshQueue = new JMenuItem("Refresh queue");
        mnView.add(mntmRefreshQueue);
        contentPane = new JPanel();

        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        String[] columnNames = { "File title", "Author", "Size", "Submitted", "" };
        SpringLayout sl_contentPane = new SpringLayout();
        contentPane.setLayout(sl_contentPane);

        table = new JTable(new DefaultTableModel(
            null,
            new String[] {
                "Title", "Size", "Author", "Project", "Status"
            }
        ) {
            /**
             * 
             */
            private static final long serialVersionUID = 5344763309058756161L;
           
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        
        sl_contentPane.putConstraint(SpringLayout.NORTH, table, 10, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, table, 0, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, table, 0, SpringLayout.EAST, contentPane);
        JScrollPane newPane = new JScrollPane(table);
        sl_contentPane.putConstraint(SpringLayout.NORTH, newPane, 0, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, newPane, 0, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, newPane, -20, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, newPane, 0, SpringLayout.EAST, contentPane);
        contentPane.add(newPane);
        
        Component verticalGlue = Box.createVerticalGlue();
        sl_contentPane.putConstraint(SpringLayout.EAST, verticalGlue, -69, SpringLayout.EAST, contentPane);
        contentPane.add(verticalGlue);
        
        JLabel lblNewLabel = new JLabel("Loading queue...");
        sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST, table);
        contentPane.add(lblNewLabel);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblNewLabel, -2, SpringLayout.SOUTH, progressBar);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, table, -6, SpringLayout.NORTH, progressBar);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, verticalGlue, -6, SpringLayout.NORTH, progressBar);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, progressBar, 0, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, contentPane);
        contentPane.add(progressBar);
        this.setMinimumSize(this.getSize());
        this.setVisible(true);
        SwingUtilities.invokeLater(new Runnable(){
          @Override
          public void run() {
             table = null;
             
          }
        });
        
        
    }
}
