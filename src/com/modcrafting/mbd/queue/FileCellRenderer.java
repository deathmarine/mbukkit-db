package com.modcrafting.mbd.queue;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import com.modcrafting.mbd.Chekkit;

public class FileCellRenderer extends DefaultTableCellRenderer {
    private List<Integer> nameRowVals = new ArrayList<Integer>();
    private List<Integer> staffRowVals = new ArrayList<Integer>();
    private Color darkGreen = new Color(22, 166, 22);
    Component defaultRender;

    public FileCellRenderer(List<QueueFile> qf) {
        int id = 0;
        for (QueueFile f : qf) {
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


        Color selected = new Color(255, 255, 173);
        c.setBackground(table.getBackground());
        if (((Boolean) table.getModel().getValueAt(table.convertRowIndexToModel(row), 0))) {
            c.setBackground(selected);
        }

        if (column == 1 && nameRowVals.contains(table.convertRowIndexToModel(row))) {
            c.setForeground(Color.RED);
        } else {
            if (column == 4 && staffRowVals.contains(table.convertRowIndexToModel(row))) {
                c.setForeground(darkGreen);
            } else {

                if (isSelected && !((Boolean) table.getModel().getValueAt(table.convertRowIndexToModel(row), 0))) { //Readable text when selected
                    c.setForeground(Color.WHITE);
                    c.setBackground(table.getSelectionBackground());
                } else {
                    c.setForeground(Color.BLACK);
                    

                }
            }

        }


        return c;
    }
}
