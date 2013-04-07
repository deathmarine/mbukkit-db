package com.modcrafting.mbd.objects;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.JProgressBar;
import javax.swing.JWindow;

public class ProgressWindow extends JWindow{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1208439404933042854L;
	JProgressBar prog;
	public ProgressWindow(Frame f){
		super(f);
		prog = new JProgressBar();
		prog.setIndeterminate(true);
        getContentPane().add(prog, BorderLayout.CENTER);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = prog.getPreferredSize();
        setLocation(screenSize.width/2 - (labelSize.width/2),screenSize.height/2 - (labelSize.height/2));
        setVisible(true);
	}
	
	
	public void close(){
		this.setVisible(false);
		this.dispose();
	}
}
