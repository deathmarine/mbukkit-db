/*package com.modcrafting.mbd.queue;

import javax.swing.table.AbstractTableModel;

class QueueTable extends AbstractTableModel {
    
    private String[] columnNames = { "First Name", "Last Name", "Sport", "# of Years", "Vegetarian" };
    private Object[][] data = { { "Kathy", "Smith", "Snowboarding", new Integer(5), new Boolean(false) }, { "John", "Doe", "Rowing", new Integer(3), new Boolean(true) }, { "Sue", "Black", "Knitting", new Integer(2), new Boolean(false) }, { "Jane", "White", "Speed reading", new Integer(20), new Boolean(true) }, { "Joe", "Brown", "Pool", new Integer(10), new Boolean(false) } };

    public final Object[] longValues = { "Jane", "Kathy", "None of the above", new Integer(20), Boolean.TRUE };

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }


    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }


    public boolean isCellEditable(int row, int col) {
        // Note that the data/cell address is constant,
        // no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }


    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("Setting value at " + row + "," + col + " to " + value + " (an instance of " + value.getClass() + ")");
        }

        data[row][col] = value;
        fireTableCellUpdated(row, col);

        if (DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + data[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }
}*/