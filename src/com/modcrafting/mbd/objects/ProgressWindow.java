package com.modcrafting.mbd.objects;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.JProgressBar;
import javax.swing.JWindow;

public class ProgressWindow extends JWindow{
	
	private static final long serialVersionUID = -1208439404933042854L;
	private JProgressBar prog = null;
	
	public ProgressWindow(Frame f){
		super(f);
		prog = new JProgressBar();
		prog.setIndeterminate(true);
		this.setAlwaysOnTop(true);
        this.getContentPane().add(prog, BorderLayout.CENTER);
        this.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = prog.getPreferredSize();
        this.setLocation(screenSize.width/2 - (labelSize.width/2),screenSize.height/2 - (labelSize.height/2));
        this.setVisible(true);
	}
	
	public void setValue(int val){
		if(this.prog == null){
			return;
		}
		this.prog.setIndeterminate(false);
		this.prog.setValue(val);
	}
	
	public int getValue(){
		if(this.prog == null){
			return 0;
		}
		return this.prog.getValue();
	}
	
	public void close(){
		this.setVisible(false);
		this.dispose();
	}
}
