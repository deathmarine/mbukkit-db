package com.modcrafting.mbd.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;

import com.modcrafting.mbd.Chekkit;

//import com.modcrafting.mbd.MasterPluginDatabase;

public class MDTextArea extends JTextArea implements MouseListener, ActionListener, DropTargetListener{
	/**
	 * 
	 */
//	MasterPluginDatabase mdb;  
	Chekkit mdb;
// 	Image image = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"fedora.png").getImage();
 	Image image = new ImageIcon(Chekkit.PATH+File.separator+"resources"+File.separator+"fedora.png").getImage();
	private static final long serialVersionUID = 5406400669307759665L;
//	public MDTextArea(MasterPluginDatabase mdb){
	public MDTextArea(Chekkit mdb){
		super();
		this.mdb = mdb;
		this.setBackground(Color.WHITE);
		this.setForeground(Color.BLACK);
		this.setCaretColor(this.getForeground());
		this.setFont(new Font(Font.SERIF,0,10));
		this.setLineWrap(true);
		this.setWrapStyleWord(true);
		this.setEditable(false);
	    this.addMouseListener(this);
	    this.setOpaque(false);
	    DropTarget dt = new DropTarget();
		try {
			dt.addDropTargetListener(this);
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		this.setDropTarget(dt);
		append("Drag files Into the text box to analyze.\nOr select file(s) from the menu.\n");
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 3) {
		      JPopupMenu popup = new JPopupMenu();
		      JMenuItem menuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
		      menuItem.setText("Copy");
		      popup.add(menuItem);
		      menuItem = new JMenuItem("Clear");
		      menuItem.addActionListener(this);
		      popup.add(menuItem);
		      popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.setText("");						
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {

	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent event) {
		event.acceptDrop(DnDConstants.ACTION_COPY);
		Transferable transferable = event.getTransferable();
		if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
            DataFlavor[] flavors = transferable.getTransferDataFlavors();
			for (DataFlavor flavor : flavors) {
				try {
					if (flavor.isFlavorJavaFileListType()) {
						List<File> files = (List<File>) transferable
								.getTransferData(flavor);
						mdb.handleFiles(files);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			event.dropComplete(true);
        } else {
            DataFlavor[] flavors = transferable.getTransferDataFlavors();
            boolean handled = false;
            for (int zz = 0; zz < flavors.length; zz++) {
                if (flavors[zz].isRepresentationClassReader()) {
					try {
						Reader reader = flavors[zz].getReaderForText(transferable);
                        BufferedReader br = new BufferedReader(reader);
                        List<File> list = new ArrayList<File>();
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            try {
                                if(new String("" + (char) 0).equals(line)) continue;
                                File file = new File(new URI(line));
                                list.add(file);
                            } catch (Exception ex) {
                         	   ex.printStackTrace();
                            }
                        }
                        mdb.handleFiles(list);
                        event.getDropTargetContext().dropComplete(true);
                        handled = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
                    break;
                }
            }
            if(!handled){
                event.rejectDrop();
            }
        }

	}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {

	}

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if(!System.getProperties().getProperty("os.name").toLowerCase().contains("mac")){
        	g.drawImage(image, 0, 0, this);
        }
	 	super.paintComponent(g);  
	}  
}
