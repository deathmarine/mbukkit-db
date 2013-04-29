package com.modcrafting.mbd.queue;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.modcrafting.mbd.Chekkit;

public class QueueWindow extends JFrame implements ActionListener{

    private static final long serialVersionUID = 1856749858855789365L;
    
    private JPanel contentPane = new JPanel();
    private JProgressBar progressBar = new JProgressBar();
    private JLabel label = new JLabel("");
    private String APIKey = "";
    private JTable table = new JTable(new DefaultTableModel(null,new String[] {"Title", "Size", "Author", "Project", "Status"}){
        private static final long serialVersionUID = 5344763309058756161L;
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    });
    private JMenuItem refreshQueue = new JMenuItem("Refresh Queue");
    private JMenuItem exit = new JMenuItem("Exit");
    private JCheckBoxMenuItem showClaimed = new JCheckBoxMenuItem("Show Claimed Files");
    private Thread thisThread;
    private boolean isThreadRunning = false;
    
    /**
     * Initialize the object
     */
    public QueueWindow(Boolean useNimbus) {
        super("File Queue");
        if(!this.getAPI()){
        	return;
        }
        this.createFrame();
        this.getQueue();
    }

    private void showLabel(String text){
    	this.label.setText(text);
    	this.label.setVisible(true);
    }
    
    private void hideLabel(){
    	this.label.setVisible(false);
    }
    
    private void showProgressBar(){
    	this.progressBar.setIndeterminate(true);
    	this.progressBar.setVisible(true);
    }
    
    private void hideProgressBar(){
    	this.progressBar = null;
    }
    
    private void createFrame(){
    	this.setIconImage(Toolkit.getDefaultToolkit().getImage(QueueWindow.class.getResource("/resources/bukkit-icon.png")));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setBounds(100, 100, 463, 334);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        mnFile.add(this.exit);
        this.exit.addActionListener(this);
        JMenu mnView = new JMenu("View");
        menuBar.add(mnView);
        mnView.add(this.showClaimed);
        this.showClaimed.addActionListener(this);
        mnView.add(this.refreshQueue);
        this.refreshQueue.addActionListener(this);
        
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        SpringLayout sl_contentPane = new SpringLayout();
        contentPane.setLayout(sl_contentPane);
        
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
        
        sl_contentPane.putConstraint(SpringLayout.WEST, this.label, 10, SpringLayout.WEST, table);
        contentPane.add(this.label);
        
        sl_contentPane.putConstraint(SpringLayout.SOUTH, this.label, -2, SpringLayout.SOUTH, this.progressBar);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, table, -6, SpringLayout.NORTH, this.progressBar);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, verticalGlue, -6, SpringLayout.NORTH, this.progressBar);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, this.progressBar, 0, SpringLayout.SOUTH, this.contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, this.progressBar, -10, SpringLayout.EAST, this.contentPane);
        contentPane.add(this.progressBar);
        this.setMinimumSize(this.getSize());
        this.setVisible(true);
    }
    
    private void getQueue(){
    	this.thisThread = new Thread(new Runnable(){
    		@Override
    		public void run(){
    			showLabel("Loading Queue...");
    			showProgressBar();
    			List<QueueFile> qfl = BukkitDevTools.parseFiles(APIKey);
                for (QueueFile q : qfl) {
                	if(isThreadRunning){
                		String c = q.getClaimed();
                        if (c == null) {
                            c = "Nope";
                        }
                        //System.out.println(q.getAuthor() + " has uploaded " + q.getTitle() + " at " + q.getFileDownloadURL() + " on " + q.getUploadTime() + " for project " + q.getProjectName() + ". It's under review: " + c); 
                	}
                }
                hideLabel();
                hideProgressBar();
    		}
    	});
    	this.isThreadRunning = true;
    	this.thisThread.start();
    }

    private boolean getAPI(){
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
                return false;
            } else {
            	try {
                    Properties prop = new Properties();
                    prop.load(new FileInputStream(Chekkit.PATH + File.separator + "config.properties"));
                    prop.setProperty("key", key);
                    prop.store(new FileOutputStream(Chekkit.PATH + File.separator + "config.properties"), "The main config");
                } catch (Exception e) {
                    e.printStackTrace();
                }   
            }
        }
        this.APIKey = key;
        return true;
    }

	
    @ Override
	public void actionPerformed(ActionEvent e) {
       	if(e.getSource() == this.exit){
    		this.isThreadRunning = false;
    		this.dispose();
    	}
    }
}
