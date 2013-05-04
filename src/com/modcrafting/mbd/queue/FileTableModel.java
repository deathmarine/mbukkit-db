package com.modcrafting.mbd.queue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class FileTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -540092269499985493L;
    private List<String> columns = new ArrayList<String>();
    private Class[] types = { String.class, String.class, String.class, String.class, String.class, String.class, Integer.class, Long.class};
    public final List<QueueFile> files;
    private int totalRows;
    public Boolean sizeSort = false;
    public Boolean dateSort = false;

    public boolean isCellEditable(int row, int column) {
        return false;
    }
    

    @Override
    public Class getColumnClass(int columnIndex) {
        return this.types[columnIndex];
    }
    
    public FileTableModel(ApprovalQueue aq) {
        columns.add("Title");
        columns.add("Project");
        columns.add("Size");
        
        columns.add("Author");
        columns.add("Posted");
        columns.add("Status");
        columns.add("realSize");
        columns.add("realDate");
        files = aq.getFileList();
        totalRows = files.size();
        
    }
    
    
    @Override
    public int getRowCount() {
        // TODO Auto-generated method stub
        return totalRows;
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
        if (columnIndex == 0) {
            //Title!
            return files.get(rowIndex).getTitle();
        }
        
        if (columnIndex == 1) {
            //Project!
            return files.get(rowIndex).getProjectName();
        }
        
        if (columnIndex == 2) {
            //Size!
            return files.get(rowIndex).getReadableSize();
        }
        
        if (columnIndex == 3) {
            //Author!
            return files.get(rowIndex).getAuthor();
        }
        
        if (columnIndex == 4) {
            //Upload time!
            return BukkitDevTools.prettyTime(files.get(rowIndex).getUploadTime());
        }
        
        if (columnIndex == 6) {
            //REAL size
            return files.get(rowIndex).getSize();
        }
        
        if (columnIndex == 7) {
            //REAL upload time
            return files.get(rowIndex).getUploadTime();
        }
        
        if (files.get(rowIndex).getClaimed() == null) {
            return "Awaiting approval";
        } else {
            return "Claimed: " + files.get(rowIndex).getClaimed();
        }
    }

  

}
