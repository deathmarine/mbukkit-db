package com.modcrafting.mbd.queue;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import javax.swing.JTable;

import com.modcrafting.mbd.Chekkit;

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
    public QueueWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        String[] columnNames = {"File title",
                "Author",
                "Size",
                "Submitted",
                ""};

        table = new JTable(null, columnNames);
        
        contentPane.add(table, BorderLayout.CENTER);
        
        this.setVisible(true);
    }

}
