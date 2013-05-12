package com.modcrafting.mbd.queue;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JEditorPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.Box;

import com.modcrafting.mbd.Chekkit;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FileRejectionWindow extends JFrame {

    private JPanel contentPane;
    public List<String> reasons = new ArrayList<String>();
    private Boolean finished = false;
    private QueueFile qf;
    private String reason;
    private Boolean closing = false;
    
    
    
    public FileRejectionResult getResult() {
        return (closing)? null : new FileRejectionResult(qf, reason);
    }

    /**
     * Create the frame.
     */
    public FileRejectionWindow(final QueueFile qf, String est, final String APIKey) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closing = true;
            }
        });
        this.qf = qf;
        //Thanks to @Jacek for these :)
        reasons.add("Contains a backdoor allowing the author access to special features or bypass permissions checks.");
        reasons.add("Contains hidden features that allows players to be given OP status or bypass permissions checks.");
        reasons.add("Includes a library that is not used, the Bukkit API, CraftBukkit server, thumbs.db or .DS_Store files.");
        reasons.add("It must be possible to disable any update checking or auto-updating via the projects config file.");
        reasons.add("Only the actual download should be uploaded here, images can be hosted using the image uploader anything else should be on the project page.");
        reasons.add("Your file contains unresolved compilation issues, this is caused by an error in your source code resulting in a broken and unusable plugin. Please fix your plugin, then resubmit your file.");
        reasons.add("The file does not appear to be your own work.");
        setResizable(false);
        setTitle("Rejecting file " + qf.getTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 344);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        SpringLayout sl_contentPane = new SpringLayout();
        contentPane.setLayout(sl_contentPane);
        
        final JComboBox comboBox = new JComboBox();
        sl_contentPane.putConstraint(SpringLayout.EAST, comboBox, 256, SpringLayout.WEST, contentPane);
        comboBox.setModel(new DefaultComboBoxModel(new String[] {"Author backdoor", "Malicious", "Unnecessary files", "Update cheking without config", "Not a plugin", "Unresolved compilation problems", "Stolen"}));
        sl_contentPane.putConstraint(SpringLayout.NORTH, comboBox, 10, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, comboBox, 10, SpringLayout.WEST, contentPane);
        contentPane.add(comboBox);
        
        final JTextArea rejectReason = new JTextArea();
        sl_contentPane.putConstraint(SpringLayout.NORTH, rejectReason, 6, SpringLayout.SOUTH, comboBox);
        sl_contentPane.putConstraint(SpringLayout.WEST, rejectReason, 0, SpringLayout.WEST, comboBox);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, rejectReason, 250, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, rejectReason, -15, SpringLayout.EAST, contentPane);
        rejectReason.setLineWrap(true);
        rejectReason.setWrapStyleWord(true);
        contentPane.add(rejectReason);
        
        Box horizontalBox = Box.createHorizontalBox();
        sl_contentPane.putConstraint(SpringLayout.NORTH, horizontalBox, 260, SpringLayout.NORTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, horizontalBox, -10, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, horizontalBox, 10, SpringLayout.WEST, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, horizontalBox, -10, SpringLayout.EAST, contentPane);
        contentPane.add(horizontalBox);
        
        JButton btnNewButton = new JButton("Cancel");
        horizontalBox.add(btnNewButton);
        sl_contentPane.putConstraint(SpringLayout.WEST, btnNewButton, 0, SpringLayout.WEST, comboBox);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnNewButton, -10, SpringLayout.SOUTH, contentPane);
        
        Component horizontalGlue = Box.createHorizontalGlue();
        horizontalBox.add(horizontalGlue);
        
        JButton btnNewButton_2 = new JButton("Ban user");
        btnNewButton_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new UserBanWindow(qf.getAuthor(), APIKey);
            }
        });
        horizontalBox.add(btnNewButton_2);
        
        Component horizontalGlue_1 = Box.createHorizontalGlue();
        horizontalBox.add(horizontalGlue_1);
        
        JButton btnNewButton_1 = new JButton("Reject");
        btnNewButton_1.setAlignmentX(Component.RIGHT_ALIGNMENT);
        horizontalBox.add(btnNewButton_1);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, btnNewButton_1, 0, SpringLayout.SOUTH, contentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, btnNewButton_1, -10, SpringLayout.EAST, contentPane);
        
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileRejectionWindow.this.reason = rejectReason.getText();
                FileRejectionWindow.this.finished = true;
                dispose();
            }
        });
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileRejectionWindow.this.closing = true;
                dispose();
            }
        });
        
        
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rejectReason.setText(reasons.get(comboBox.getSelectedIndex()));
            }
        });
        
        if (est != null) {
            rejectReason.setText(est);
        }
        this.setVisible(true);
        while (!finished && !closing) {
            //Spin around for a bit - this is a new thread so nobody cares
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        
    }
}
