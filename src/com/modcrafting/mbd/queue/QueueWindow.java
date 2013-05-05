package com.modcrafting.mbd.queue;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ldap.SortKey;
import javax.swing.AbstractAction;
import javax.swing.Action;
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
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;

import com.modcrafting.mbd.Chekkit;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

public class QueueWindow extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1856749858855789365L;

    private JPanel contentPane = new JPanel();
    public JProgressBar progressBar = new JProgressBar();
    private JLabel label = new JLabel("");
    private String APIKey = "";
    public JTable table = new JTable();
    private JMenuItem refreshQueue = new JMenuItem("Refresh Queue");
    private JMenuItem exit = new JMenuItem("Exit");
    private JCheckBoxMenuItem showClaimed = new JCheckBoxMenuItem("Show Claimed Files");
    private JCheckBoxMenuItem autoRefresh = new JCheckBoxMenuItem("Auto Refresh (30s)");
    private Thread thisThread;
    private Thread refreshThread;
    private boolean isThreadRunning = false;
    private JScrollPane scrollPane;
    private SpringLayout sl_contentPane = new SpringLayout();
    private int prop = 0;
    private JMenuItem mntmClaimOldest = new JMenuItem("Mark 10 oldest files");
    private JMenuItem mntmMarkSelectedFiles = new JMenuItem("Mark selected files");
    private final JMenuItem mntmUnmarkSelectedFiles = new JMenuItem("Unmark selected files");
    private final JMenuItem mntmMarkTop = new JMenuItem("Mark 10 top files");
    private final JMenuItem mntmClaimSelectedFiles = new JMenuItem("Claim marked files");
    private final JMenuItem mntmApproveMarkedFiles = new JMenuItem("Approve marked files");
    private String DBOName;
    private Chekkit ck;

    /**
     * Initialize the object
     */
    
 
    public QueueWindow(Boolean useNimbus, JFrame parent, int pId) {
        super("File Queue");
        
        this.ck = (Chekkit) parent;
        if (!this.getAPI()) {
            return;
        }
        Chekkit.processPanel.removeProcess(pId);
        this.createFrame(parent);
        this.showLabel("Loading Queue...");
        this.getQueue();
    }

    public void showLabel(String text) {
        this.label.setText(text);
        this.label.setVisible(true);
        this.repaint();
    }


    public void hideLabel() {
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
        
        JMenu mnFiles = new JMenu("Files");
        menuBar.add(mnFiles);
        mntmMarkSelectedFiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mntmMarkSelectedFiles.addActionListener(this);
        mnFiles.add(mntmMarkSelectedFiles);
        mntmUnmarkSelectedFiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mntmUnmarkSelectedFiles.addActionListener(this);
        
        mnFiles.add(mntmUnmarkSelectedFiles);
        
        
        mntmClaimSelectedFiles.addActionListener(this);
        mntmClaimSelectedFiles.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        mnFiles.add(mntmClaimSelectedFiles);
        mntmClaimOldest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mntmClaimOldest.addActionListener(this);
        
        mnFiles.add(mntmClaimOldest);
        mntmMarkTop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mntmMarkTop.addActionListener(this);
        
        mnFiles.add(mntmMarkTop);
        
        JMenuItem mntmMarkClaimedFiles = new JMenuItem("Mark claimed files");
        mnFiles.add(mntmMarkClaimedFiles);
        
        
        mntmApproveMarkedFiles.addActionListener(this);
        mnFiles.add(mntmApproveMarkedFiles);
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

    public void refreshThread() {
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
        final String username = this.DBOName;
        this.thisThread = new Thread(new Runnable() {
            @Override
            public void run() {
                showProgressBar();
                ApprovalQueue aq = BukkitDevTools.parseFiles(APIKey, showClaimed.isSelected(), username);
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
                final FileTableModel model = new FileTableModel(aq, QueueWindow.this);
                
                table = new JTable(model);
                table.setDefaultRenderer(String.class, new FileCellRenderer(aq.getFileList()));
                table.setAutoCreateRowSorter(true);
                table.removeColumn(table.getColumnModel().getColumn(7));
                table.removeColumn(table.getColumnModel().getColumn(7));
                table.getColumnModel().getColumn(0).setMaxWidth(25);
                table.getRowSorter().addRowSorterListener(
                        new RowSorterListener() {
                            
                            @Override
                            public void sorterChanged(RowSorterEvent e) {
                                //Don't touch this, it works.
                                Boolean serviced = false;
                                FileTableModel ftm = (FileTableModel) table.getModel();
                                
                                //Chekkit.log.info("From the top. " + e.getType().toString());
                                if (e.getType() != RowSorterEvent.Type.SORTED) {
                                    return;
                                }
                                
                                if (ftm.progSort) {
                                    ftm.progSort = false;
                                    //Chekkit.log.info("Destroyed a non-user sort event.");
                                    return;
                                  }
                                
                               
                                if (table.getRowSorter().getSortKeys().get(0).getColumn() == 3) {
                                    //Chekkit.log.info("Size sort! Compensating...");
                                    List l = new ArrayList<SortKey>();
                                    RowSorter.SortKey sk;
                                    Chekkit.log.info(ftm.sizeSort.toString());
                                    if (ftm.sizeSort) {
                                        sk = new RowSorter.SortKey(7, SortOrder.ASCENDING);
                                        ftm.sizeSort = false;
                                        
                                    } else {
                                        sk = new RowSorter.SortKey(7, SortOrder.DESCENDING);
                                        ftm.sizeSort = true;
                                    }
                                    
                                    l.add(sk);
                                    ftm.progSort = true;
                                    table.getRowSorter().setSortKeys(l);
                                    serviced = true;
                                    return;
                                }
                                
                                if (table.getRowSorter().getSortKeys().get(0).getColumn() == 5) {
                                    //Chekkit.log.info("Date sort! Compensating...");
                                    List l = new ArrayList<SortKey>();
                                    RowSorter.SortKey sk;
                                    
                                    Chekkit.log.info(ftm.dateSort.toString());
                                    if (ftm.dateSort) {
                                        sk = new RowSorter.SortKey(8, SortOrder.ASCENDING);
                                        ftm.dateSort = false;
                                        
                                    } else {
                                        sk = new RowSorter.SortKey(8, SortOrder.DESCENDING);
                                        ftm.dateSort = true;
                                        
                                    }
                                    l.add(sk);
                                    ftm.progSort = true;
                                    table.getRowSorter().setSortKeys(l);
                                    serviced = true;
                                    return;
                                }
                                if (!serviced) {
                                    ftm.dateSort = false;
                                    ftm.sizeSort = false;
                                    //Chekkit.log.info("Resetting date & size.");
                                }
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
        key = Chekkit.config.getString("key");
        UserInfo is = BukkitDevTools.checkAPIKey(key);
        if (key == "" || key == null || (is.getKeyState() != KeyState.STAFF)) {
            Boolean in = true;
            while (in) {
                key = JOptionPane.showInputDialog(this, "Enter your BukkitDev API key:", "More info required!", JOptionPane.INFORMATION_MESSAGE);
                if (key != null) {
                    is = BukkitDevTools.checkAPIKey(key);
                    KeyState ks = is.getKeyState();
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
                        DBOName = is.getUsername();
                        in = false;
                    }
                } else {
                    in = false;
                }
            }

            if (key == null) {
                return false;
            } else {
                Chekkit.config.set("key", key);
            }
        } else {
            if (is.getUsername() != null) {
                this.DBOName = is.getUsername();
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
        } else if (e.getSource() == this.mntmMarkSelectedFiles) {
            for (int i : this.table.getSelectedRows()) {
                table.getModel().setValueAt(true, table.convertRowIndexToModel(i), 0);
            }
        } else if (e.getSource() == this.mntmUnmarkSelectedFiles) {
            for (int i : this.table.getSelectedRows()) {
                table.getModel().setValueAt(false, table.convertRowIndexToModel(i), 0);
            }
        } else if (e.getSource() == this.mntmClaimOldest) {
            FileTableModel ftm = (FileTableModel) table.getModel(); 
            int i = 0;
            int done = 0;
            for (QueueFile qf: ftm.files) {
                
                if (qf.getClaimed() == null && !qf.selected) {
                    table.getModel().setValueAt(true, i, 0);
                    done++;
                }
                if (done == 10) {
                    break;
                }
                i++;  
            }
        } else if (e.getSource() == this.mntmMarkTop) {
            int done = 0;
            int i = 0;
            while (done != 10) {
                if (!(Boolean) table.getModel().getValueAt(table.convertRowIndexToModel(i), 0)) {
                    table.getModel().setValueAt(true, table.convertRowIndexToModel(i), 0);
                    done++;
                }
                i++;
            }
            
        } else if (e.getSource() == this.mntmClaimSelectedFiles) {
            final FileTableModel ftm = (FileTableModel) table.getModel(); 
            Chekkit.log.info("Fired!");
            /**/SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {/**/
                    Chekkit.log.info("Starting claim");
                    BukkitDevTools.claimFiles(ftm.files, QueueWindow.this, APIKey, ck); //TODO: Put this in a new thread
                    progressBar.setVisible(false);
                    hideLabel();
                    
                    /**/}
                
            });/**/
            refreshThread();
        } else if (e.getSource() == this.mntmApproveMarkedFiles) {
            final FileTableModel ftm = (FileTableModel) table.getModel(); 
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {/**/
                    BukkitDevTools.approveFiles(ftm.files, QueueWindow.this, APIKey, ck); //TODO: Put this in a new thread
                    progressBar.setVisible(false);
                    hideLabel();
                    
                    /**/}
                
            });/**/
            
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
