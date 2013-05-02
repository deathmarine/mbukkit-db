package com.modcrafting.mbd.queue;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.ldap.SortKey;
import javax.swing.Box;
import javax.swing.DefaultRowSorter;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
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
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.modcrafting.mbd.Chekkit;

public class QueueWindow extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1856749858855789365L;

    private JPanel contentPane = new JPanel();
    private JProgressBar progressBar = new JProgressBar();
    private JLabel label = new JLabel("");
    private String APIKey = "";
    private JTable table = new JTable();
    private JMenuItem refreshQueue = new JMenuItem("Refresh Queue");
    private JMenuItem exit = new JMenuItem("Exit");
    private JCheckBoxMenuItem showClaimed = new JCheckBoxMenuItem("Show Claimed Files");
    private JCheckBoxMenuItem autoRefresh = new JCheckBoxMenuItem("Auto Refresh (30s)");
    private Thread thisThread;
    private Thread refreshThread;
    private boolean isThreadRunning = false;
    private JScrollPane scrollPane;
    private SpringLayout sl_contentPane = new SpringLayout();
    private Boolean sizeSort = false;
    private Boolean dateSort = false;
    private int prop = 0;

    /**
     * Initialize the object
     */
    public QueueWindow(Boolean useNimbus, JFrame parent) {
        super("File Queue");
        if (!this.getAPI()) {
            return;
        }
        this.createFrame(parent);
        this.showLabel("Loading Queue...");
        this.getQueue();
    }

    private void showLabel(String text) {
        this.label.setText(text);
        this.label.setVisible(true);
    }

    private void hideLabel() {
        this.label.setVisible(false);
    }

    private void showProgressBar() {
        this.progressBar.setIndeterminate(true);
        this.progressBar.setVisible(true);
    }

    private void hideProgressBar() {
        this.progressBar.setVisible(false);
    }

    private void createFrame(JFrame parent) {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(QueueWindow.class.getResource("/resources/bukkit-icon.png")));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setBounds(100, 100, 463, 334);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);
        mnFile.add(this.autoRefresh);
        mnFile.add(this.exit);
        this.autoRefresh.addActionListener(this);
        this.exit.addActionListener(this);
        JMenu mnView = new JMenu("View");
        menuBar.add(mnView);
        mnView.add(this.showClaimed);
        this.showClaimed.addActionListener(this);
        mnView.add(this.refreshQueue);
        this.refreshQueue.addActionListener(this);

        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(sl_contentPane);

        sl_contentPane.putConstraint(SpringLayout.NORTH, table, 10, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, table, 0, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, table, 0, SpringLayout.EAST, contentPane);

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

        this.setSize((int) (parent.getSize().getWidth() * 0.75), (int) (parent.getSize().getHeight() * 0.75));
        int x = (int) (parent.getLocation().getX() + ((parent.getSize().getWidth() - this.getSize().getWidth()) / 2));
        int y = (int) (parent.getLocation().getY() + ((parent.getSize().getHeight() - this.getSize().getHeight()) / 2));
        this.setLocation(x, y);

        this.setMinimumSize(new Dimension(400, 600));
        this.setVisible(true);
    }

    private void refreshThread() {
        this.refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (autoRefresh.isSelected()) {
                    try {
                        Thread.sleep(30000);
                        contentPane.remove(scrollPane);
                        showLabel("Automatic Queue Refresh...");
                        getQueue();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.refreshThread.start();
    }

    private void getQueue() {
        this.thisThread = new Thread(new Runnable() {
            @Override
            public void run() {
                showProgressBar();
                ApprovalQueue aq = BukkitDevTools.parseFiles(APIKey, showClaimed.isSelected());
                List<QueueFile> qfl = aq.getFileList();
                int total = aq.getFileTotal();
                Object[][] files;
                /*int applicable;
                if (!showClaimed.isSelected()) {
                    applicable = aq.getFilesUnclaimed();
                } else {
                    files = new Object[qfl.size()][6];
                }*/

                /*String[] columnNames = { "Title", "Project", "Size", "Author", "Posted", "Status" };
                int index = 0;

                int claimed = aq.getFilesClaimed();
                for (QueueFile q : qfl) {
                    if (isThreadRunning) {
                        String c = q.getClaimed();
                        Chekkit.log.info(q.getAuthor() + ": " + q.getCreatedByStaff());
                        
                        if (c == null) {
                            c = "";
                            files[index][0] = q.getTitle();
                            files[index][1] = q.getProjectName();
                            files[index][2] = q.getSize();
                            files[index][3] = q.getAuthor();
                            files[index][4] = BukkitDevTools.prettyTime(q.getUploadTime());
                            files[index][5] = "Awaiting approval";
                            index++;
                        } else if (showClaimed.isSelected()) {
                            if (!q.hasNumberInTitle()) {
                                files[index][0] = "{" + q.getTitle();
                            } else {
                                files[index][0] = q.getTitle();
                            }
                            files[index][1] = q.getProjectName();
                            files[index][2] = q.getSize();
                            files[index][3] = q.getAuthor();
                            files[index][4] = BukkitDevTools.prettyTime(q.getUploadTime());
                            files[index][5] = "Claimed by " + c;

                            index++;
                        }
                        // System.out.println(q.getAuthor() + " has uploaded " +
                        // q.getTitle() + " at " + q.getFileDownloadURL() +
                        // " on " + q.getUploadTime() + " for project " +
                        // q.getProjectName() + ". It's under review: " + c);
                    } else {
                        return;
                    }
                }*/

                QueueWindow.this.setTitle("File Queue: " + (aq.getFileTotal()) + " Total Files, " + aq.getFilesClaimed() + " Claimed");
                
                /*final DefaultTableModel model = new DefaultTableModel(files, columnNames) {
                    private static final long serialVersionUID = 5344763309058756161L;

                    
                };*/
                final FileTableModel model = new FileTableModel(aq);
                
                table = new JTable(model);
                table.setDefaultRenderer(String.class, new FileCellRenderer(aq.getFileList()));
                table.setAutoCreateRowSorter(true);
                table.removeColumn(table.getColumnModel().getColumn(6));
                table.removeColumn(table.getColumnModel().getColumn(6));
                table.getRowSorter().addRowSorterListener(
                        new RowSorterListener() {
                            
                            @Override
                            public void sorterChanged(RowSorterEvent e) {
                                if (e.getType() != RowSorterEvent.Type.SORTED) {
                                    return;
                                }
                                if (table.getRowSorter().getSortKeys().get(0).getColumn() == 2) {
                                    Chekkit.log.info("Size sort! Compensating...");
                                    List l = new ArrayList<SortKey>();
                                    RowSorter.SortKey sk;
                                    if (sizeSort) {
                                        sk = new RowSorter.SortKey(6, SortOrder.ASCENDING);
                                        sizeSort = false;
                                        
                                    } else {
                                        sk = new RowSorter.SortKey(6, SortOrder.DESCENDING);
                                        sizeSort = true;
                                    }
                                    
                                    l.add(sk);
                                    table.getRowSorter().setSortKeys(l);
                                    return;
                                }
                                
                                if (table.getRowSorter().getSortKeys().get(0).getColumn() == 4) {
                                    Chekkit.log.info("Date sort! Compensating...");
                                    List l = new ArrayList<SortKey>();
                                    RowSorter.SortKey sk;
                                    if (dateSort) {
                                        sk = new RowSorter.SortKey(7, SortOrder.ASCENDING);
                                        dateSort = false;
                                        
                                    } else {
                                        sk = new RowSorter.SortKey(7, SortOrder.DESCENDING);
                                        dateSort = true;
                                        
                                    }
                                    l.add(sk);
                                    table.getRowSorter().setSortKeys(l);
                                    return;
                                }
                                dateSort = false;
                                sizeSort = false;
                            }
                        });
                scrollPane = new JScrollPane(table);
                sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, contentPane);
                sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, contentPane);
                sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -20, SpringLayout.SOUTH, contentPane);
                sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, contentPane);

                contentPane.add(scrollPane);
                table.setVisible(true);
                contentPane.repaint();

                hideLabel();
                hideProgressBar();
                isThreadRunning = false;
            }
        });
        this.isThreadRunning = true;
        this.thisThread.start();
    }

    private boolean getAPI() {
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
                key = JOptionPane.showInputDialog(this, "Enter your BukkitDev API key:", "More info required!", JOptionPane.INFORMATION_MESSAGE);
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

    @SuppressWarnings("deprecation")
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.exit) {
            this.isThreadRunning = false;
            this.dispose();
        } else if (e.getSource() == this.refreshQueue) {
            this.contentPane.remove(this.scrollPane);
            this.contentPane.repaint();
            this.showLabel("Refreshing Queue...");
            this.getQueue();
        } else if (e.getSource() == this.showClaimed) {
            this.contentPane.remove(this.scrollPane);
            this.showLabel("Getting Claimed...");
            this.table.setVisible(false);
            this.getQueue();
        } else if (e.getSource() == this.autoRefresh) {
            if (this.autoRefresh.isSelected()) {
                this.refreshThread();
            } else {
                this.refreshThread.stop();
            }
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.isThreadRunning = false;
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
