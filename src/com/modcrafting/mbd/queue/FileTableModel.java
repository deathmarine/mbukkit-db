package com.modcrafting.mbd.queue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = -540092269499985493L;
    List<String> columns = new ArrayList<String>();
    
    public FileTableModel() {
        columns.add("Title");
        columns.add("Size");
        columns.add("Author");
        columns.add("Project");
        columns.add("");
    }
    
    
    @Override
    public int getRowCount() {
        // TODO Auto-generated method stub
        return 2;
    }
    
     
    
    @Override
    public String getColumnName(int index) {
        return columns.get(index);
    }

    @Override
    public int getColumnCount() {
        // TODO Auto-generated method stub
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // TODO Auto-generated method stub
        return "t";
    }

  

}
