package com.modcrafting.mbd.objects;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.modcrafting.mbd.decom.JarFileEntry;


public class CodeTab extends JPanel{

	private static final long serialVersionUID = -2812216757673964463L;
	private JLabel closeButton = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/icon_close.png"))));
	private JLabel tabTitle = new JLabel();
	private String title = "";
	JarFileEntry file;
	
	public CodeTab(JarFileEntry t){
		super(new GridBagLayout());
		file = t;
		this.setOpaque(false);
		this.title = t.getName();
		this.tabTitle = new JLabel(title);
		
		this.createTab();
	}
	
	public JarFileEntry getJarFileEntry(){
		return file;
	}
	
	public JLabel getButton(){
		return this.closeButton;
	}
	
	public void createTab(){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		this.add(tabTitle, gbc);
		gbc.gridx++;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.anchor = GridBagConstraints.EAST;
		this.add(closeButton, gbc);
	}
}
