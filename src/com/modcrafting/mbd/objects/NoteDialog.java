package com.modcrafting.mbd.objects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import com.modcrafting.mbd.Chekkit;
import com.modcrafting.mbd.decom.DecompJar;

public class NoteDialog extends JDialog{
	private static final long serialVersionUID = -8392059868787987006L;

	private ImageIcon image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/icon_add.png")));
	DecompJar parent;
	JTextArea textarea;
	JTextField field;
	public NoteDialog(DecompJar instance){
		super(instance);
		this.parent = instance;
		if(parent.mainclass != null){
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			final Dimension center = new Dimension((int)(screenSize.width*0.25), (int)(screenSize.height*0.45));
			final int x = (int) (center.width * 0.2);
			final int y = (int) (center.height * 0.2);
			this.setBounds(x, y, center.width, center.height);
			JToolBar toolBar = new JToolBar();
			JLabel label = new JLabel(image);
			label.setBorder(BorderFactory.createRaisedBevelBorder());
			label.addMouseListener(new AddListener());
			toolBar.add(label);
			field = new JTextField(150);
			field.setText("Add Note...");
			//label.setFont(new Font("Serif", Font.BOLD, 12));
			//label.setForeground(Color.WHITE);
			toolBar.add(field);
			toolBar.setBackground(new Color(58,163,31));
			toolBar.setBorder(BorderFactory.createLoweredBevelBorder());
			this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
			
			textarea = new JTextArea();
			for(Treple trep : parent.database.getNotes(parent.mainclass)){
		        Date date = new Date(trep.getTime());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				textarea.setText(textarea.getText()+trep.getUsername()+": "+format.format(date)+"\n"+trep.getNote()+"\n");
			}
			textarea.setEditable(false);
			
			this.getContentPane().add(new JScrollPane(textarea));
			this.setVisible(true);	
		}
	}
	
	private void addText(String username, long time, String note) {
        Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		textarea.setText(textarea.getText()+username+": "+format.format(date)+"\n"+note+"\n");
	}
	
	private class AddListener extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent event) {
			if(SwingUtilities.isLeftMouseButton(event)){
				actionPerformed();
			}
		}
		public void actionPerformed() {
			final String text = field.getText();
			if (text.equals("Add Note...") || text.isEmpty()) {
			    JOptionPane.showMessageDialog(NoteDialog.this, "You must enter a note!", "Note error", JOptionPane.WARNING_MESSAGE);
			} else {
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
						parent.database.setNote(parent.mainclass, Chekkit.username, text);
					}
				});
				addText(Chekkit.realUsername, System.currentTimeMillis(), text);
				field.setText("Add Note...");
    		
			}
		}
	}
}
