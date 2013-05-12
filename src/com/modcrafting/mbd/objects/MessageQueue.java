package com.modcrafting.mbd.objects;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.SpringLayout;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class MessageQueue extends JDialog {

    /**
     * Launch the application.
     */
    public JLabel lblNewLabel = new JLabel("Sending messages...");
    public JProgressBar progressBar = new JProgressBar();

    /**
     * Create the dialog.
     */
    public MessageQueue(List<BukkitDevPM> messages, String APIKey) {
        setTitle("Message queue");
        setBounds(100, 100, 202, 86);
        
        SpringLayout springLayout = new SpringLayout();
        getContentPane().setLayout(springLayout);
        
        
        springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 10, SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST, getContentPane());
        getContentPane().add(lblNewLabel);
        
        
        springLayout.putConstraint(SpringLayout.NORTH, progressBar, 9, SpringLayout.SOUTH, lblNewLabel);
        springLayout.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, getContentPane());
        getContentPane().add(progressBar);
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);
        new Thread(new MessageSendOperation(messages, APIKey, this)).start();

    }
}
