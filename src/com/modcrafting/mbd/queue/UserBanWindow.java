package com.modcrafting.mbd.queue;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class UserBanWindow extends JFrame {

    private JPanel contentPane;
    private JTextField textField;
    private JLabel lblReason;
    private JCheckBox chckbxNewCheckBox;
    private JCheckBox chckbxNewCheckBox_1;
    private JCheckBox chckbxNewCheckBox_2;
    private JTextArea textArea;
    private String APIKey;
    private JButton btnAuthorBackdoor;
    private String defaultText = "You need to ensure that your reasoning is valid and that there is no doubt as to the issue before banning any user. Do not take banning lightly - as stated on DBO: 'If your claim is unfounded, then your moderator privileges will very likely be removed.'";
    /**
     * Create the frame.
     */
    public UserBanWindow(String username, final String APIKey) {
        this.APIKey = APIKey;
    
        setTitle("Ban user");
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 470, 273);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel topLabel = new JLabel("Username:");
        topLabel.setBounds(10, 7, 77, 15);
        contentPane.add(topLabel);
        
        textField = new JTextField();
        textField.setBounds(92, 5, 114, 25);
        contentPane.add(textField);
        
        lblReason = new JLabel("Reason:");
        lblReason.setBounds(29, 42, 58, 15);
        contentPane.add(lblReason);
        
        textArea = new JTextArea();
        if (username != null) {
            textField.setText(username);
        }
        textArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals(defaultText)) {
                    textArea.setForeground(Color.black);
                    textArea.setText("");
                }
            }
            
        
            @Override
            public void focusLost(FocusEvent e) {
                if (textArea.getText().equals("")) {
                    textArea.setForeground(Color.gray);
                    textArea.setText(defaultText);
                }
            }
        });
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBounds(92, 39, 350, 110);
        contentPane.add(textArea);
        textArea.setText(this.defaultText);
        textArea.setForeground(Color.gray);
        chckbxNewCheckBox = new JCheckBox("Disallow login");
        chckbxNewCheckBox.setBounds(10, 157, 124, 23);
        chckbxNewCheckBox.setSelected(true);
        contentPane.add(chckbxNewCheckBox);
        
        chckbxNewCheckBox_1 = new JCheckBox("Delete all projects");
        chckbxNewCheckBox_1.setBounds(139, 157, 155, 23);
        contentPane.add(chckbxNewCheckBox_1);
        
        chckbxNewCheckBox_2 = new JCheckBox("Delete comments");
        chckbxNewCheckBox_2.setBounds(299, 157, 148, 23);
        contentPane.add(chckbxNewCheckBox_2);
        final JSlider slider = new JSlider();
        slider.setBounds(10, 214, 155, 16);
        
        
       
        
        
        
        JLabel lblNewLabel = new JLabel("Slide to confirm");
        lblNewLabel.setBounds(10, 188, 110, 15);
        contentPane.add(lblNewLabel);
        slider.setValue(0);
        contentPane.add(slider);
        final JButton banButton = new JButton("Ban user");
        banButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        BukkitDevTools.banUser(textField.getText(), APIKey, textArea.getText(), chckbxNewCheckBox.isSelected(), chckbxNewCheckBox_1.isSelected(), chckbxNewCheckBox_2.isSelected());
                    }
                });
                t.start();
                dispose();
            }
        });
        banButton.setBounds(350, 205, 97, 25);
        banButton.setEnabled(false);
        contentPane.add(banButton);
        
        btnAuthorBackdoor = new JButton("Author backdoor");
        btnAuthorBackdoor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setForeground(Color.black);
                textArea.setText("Including a backdoor into a plugin that gave the author operator status or other special privileges.");
            }
        });
        btnAuthorBackdoor.setBounds(278, 5, 164, 25);
        contentPane.add(btnAuthorBackdoor);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (slider.getValue() == 100) {
                    banButton.setEnabled(true);
                } else {
                    banButton.setEnabled(false);
                }
            }
        });
        this.setVisible(true);
        
    }
}
