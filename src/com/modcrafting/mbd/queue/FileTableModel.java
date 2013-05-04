package com.modcrafting.mbd.queue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.modcrafting.mbd.Chekkit;

public class FileTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -540092269499985493L;
    private List<String> columns = new ArrayList<String>();
    private Class[] types = { Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class, Integer.class, Long.class};
    public final List<QueueFile> files;
    private int totalRows;
    public Boolean sizeSort = false;
    public Boolean dateSort = false;
    public Boolean progSort = false;
    

    public boolean isCellEditable(int row, int column) {
       
        if (column == 0) {
            //Chekkit.log.info(Integer.toString(column));
            return true;
            
        } else {
            return false;
        }
    }
    

    @Override
    public Class getColumnClass(int columnIndex) {
        return this.types[columnIndex];
    }
    
    public FileTableModel(ApprovalQueue aq) {
        columns.add(" ");
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
    
    public void setValueAt(Object value, int row, int col) {
        if (col == 0) {
            files.get(row).selected ^= true;
            fireTableDataChanged();
            fireTableCellUpdated(row, col);
        }
        
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            //Actions
            return files.get(rowIndex).selected;
        }
        
        if (columnIndex == 1) {
            //Title!
            return files.get(rowIndex).getTitle();
        }
        
        if (columnIndex == 2) {
            //Project!
            return files.get(rowIndex).getProjectName();
        }
        
        if (columnIndex == 3) {
            //Size!
            return files.get(rowIndex).getReadableSize();
        }
        
        if (columnIndex == 4) {
            //Author!
            return files.get(rowIndex).getAuthor();
        }
        
        if (columnIndex == 5) {
            //Upload time!
            return BukkitDevTools.prettyTime(files.get(rowIndex).getUploadTime());
        }
        
        if (columnIndex == 6) {
            if (files.get(rowIndex).getClaimed() == null) {
                return "Awaiting approval";
            } else {
                return "Claimed: " + files.get(rowIndex).getClaimed();
            }
        }
        
        if (columnIndex == 7) {
            //REAL size
            return files.get(rowIndex).getSize();
        }
        
        if (columnIndex == 8) {
            //REAL upload time
            return files.get(rowIndex).getUploadTime();
        }
        
        return false;
        
       
    }

  

}
