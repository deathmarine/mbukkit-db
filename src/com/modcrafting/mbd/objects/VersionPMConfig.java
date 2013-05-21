package com.modcrafting.mbd.objects;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JEditorPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;

public class VersionPMConfig extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField textField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            VersionPMConfig dialog = new VersionPMConfig();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public VersionPMConfig() {
        setTitle("Version PM Configuration");
        setBounds(100, 100, 450, 329);
        getContentPane().setLayout(new BorderLayout());
        {
            JPanel panel = new JPanel();
            getContentPane().add(panel, BorderLayout.NORTH);
            panel.setLayout(new GridLayout(1, 1, 0, 20));
            {
                JLabel lblNewLabel_3 = new JLabel("<html><p style = \"padding: 5px;\">Version PMs can be sent to an author to let them know that their file needs to have a version in its title. This dialog allows you to configure the subject, message and deadline length for files.</p></html>");
                lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(lblNewLabel_3);
            }
        }
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.rowHeights = new int[] {30, 150, 30};
        gbl_contentPanel.columnWidths = new int[] {120, 300};
        gbl_contentPanel.columnWeights = new double[]{0.0, 0.0};
        gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblNewLabel = new JLabel("Subject");
            GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
            gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
            gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel.gridx = 0;
            gbc_lblNewLabel.gridy = 0;
            contentPanel.add(lblNewLabel, gbc_lblNewLabel);
        }
        {
            textField = new JTextField();
            GridBagConstraints gbc_textField = new GridBagConstraints();
            gbc_textField.fill = GridBagConstraints.BOTH;
            gbc_textField.insets = new Insets(0, 0, 5, 0);
            gbc_textField.gridx = 1;
            gbc_textField.gridy = 0;
            contentPanel.add(textField, gbc_textField);
            textField.setAlignmentX(Component.RIGHT_ALIGNMENT);
            textField.setColumns(10);
        }
        {
            JLabel lblNewLabel_1 = new JLabel("Message");
            GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
            gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
            gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel_1.gridx = 0;
            gbc_lblNewLabel_1.gridy = 1;
            contentPanel.add(lblNewLabel_1, gbc_lblNewLabel_1);
            lblNewLabel_1.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        }
        {
            JEditorPane editorPane = new JEditorPane();
            GridBagConstraints gbc_editorPane = new GridBagConstraints();
            gbc_editorPane.fill = GridBagConstraints.BOTH;
            gbc_editorPane.insets = new Insets(0, 0, 5, 0);
            gbc_editorPane.gridx = 1;
            gbc_editorPane.gridy = 1;
            contentPanel.add(editorPane, gbc_editorPane);
        }
        {
            JLabel lblNewLabel_2 = new JLabel("Deadline in days");
            GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
            gbc_lblNewLabel_2.fill = GridBagConstraints.BOTH;
            gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
            gbc_lblNewLabel_2.gridx = 0;
            gbc_lblNewLabel_2.gridy = 2;
            contentPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);
        }
        {
            JSpinner spinner = new JSpinner();
            spinner.setModel(new SpinnerNumberModel(1, 1, 30, 1));
            GridBagConstraints gbc_spinner = new GridBagConstraints();
            gbc_spinner.insets = new Insets(0, 0, 5, 0);
            gbc_spinner.fill = GridBagConstraints.BOTH;
            gbc_spinner.gridx = 1;
            gbc_spinner.gridy = 2;
            contentPanel.add(spinner, gbc_spinner);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("Save");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

}
