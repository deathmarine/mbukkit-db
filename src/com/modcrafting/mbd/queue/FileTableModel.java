package com.modcrafting.mbd.queue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.table.AbstractTableModel;

import com.modcrafting.mbd.Chekkit;

public class FileTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -540092269499985493L;
    private List<String> columns = new ArrayList<String>();
    private Class[] types = { Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class, Float.class, Long.class};
    public final List<QueueFile> files;
    private int totalRows;
    public Boolean sizeSort = false;
    public Boolean dateSort = false;
    public Boolean progSort = false;
    private int selected = 0;
    private QueueWindow qw;
    

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
    
    public FileTableModel(ApprovalQueue aq, QueueWindow qw) {
        this.qw = qw;
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
            if (files.get(row).selected && !((Boolean) value)) {
                //If it was originally selected and it's being deselected
                selected--;
                //We drop the count by one
            }
            if (!files.get(row).selected && ((Boolean) value)) {
                //If it was originally unselected and it's being selected
                selected++;
                //We increase the count by one
            }
            files.get(row).selected = (Boolean) value;
            fireTableDataChanged();
            fireTableCellUpdated(row, col);
            qw.showLabel(selected + " files selected.");
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


    public void removeRow(int toRemove) {
        files.remove(toRemove);
        fireTableDataChanged();
        
        
        
    }

  

}
