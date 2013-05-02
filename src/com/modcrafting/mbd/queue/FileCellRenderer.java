package com.modcrafting.mbd.queue;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.modcrafting.mbd.Chekkit;

public class FileCellRenderer extends DefaultTableCellRenderer {
    private List<Integer> nameRowVals = new ArrayList<Integer>();
    private List<Integer> staffRowVals = new ArrayList<Integer>();
    private Color darkGreen = new Color(22,166,22);

    public FileCellRenderer(List<QueueFile> qf) {
        int id = 0;
        for (QueueFile f: qf) {
            if (!f.getTitle().matches(".*\\d.*")) {
                nameRowVals.add(id);
            }
            if (f.getCreatedByStaff()) {
                staffRowVals.add(id);
            }
            id++;
        }
        
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Only for specific cell
        if (column == 0 && nameRowVals.contains(row)) {
            c.setForeground(Color.RED);
        } else {
            if (column == 3 && staffRowVals.contains(row)) {
                c.setForeground(darkGreen);
            } else {
                c.setForeground(Color.BLACK);
            }
            
        }
        
        return c;
    }
}
