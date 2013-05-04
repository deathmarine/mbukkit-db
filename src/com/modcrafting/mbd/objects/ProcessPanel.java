package com.modcrafting.mbd.objects;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProcessPanel extends JPanel{

	private static final long serialVersionUID = -8380040772948037404L;
	private List<JPanel> list = new ArrayList<JPanel>();
	
	public ProcessPanel(){
		super(new GridLayout(25, 1));
		this.setVisible(true);
	}
	
	public void setBarValue(int id, int value){
		List<Component> comps = Arrays.asList(list.get(id).getComponents());
		JProgressBar bar = null;
		for(Component c : comps){
			if(c instanceof JProgressBar){
				bar = (JProgressBar)c;
			}
		}
		if(bar == null){
			return;
		}
		bar.setValue(value);
	}
	
	public int addProcess(String p){
		JPanel panel = new JPanel();
		JLabel label = new JLabel(p);
		JProgressBar bar = new JProgressBar(0, 100);
		
		bar.setValue(0);
		label.setVisible(true);
		bar.setVisible(true);
		
		panel.add(label);
		panel.add(bar);
		
		list.add(panel);
		this.add(panel);
		this.revalidate();
		this.repaint();
		
		return list.indexOf(panel);
	}
	
	public int addUnknownProcess(String p){
        JPanel panel = new JPanel();
        JLabel label = new JLabel(p);
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        label.setVisible(true);
        bar.setVisible(true);
        
        panel.add(label);
        panel.add(bar);
        
        list.add(panel);
        this.add(panel);
        this.revalidate();
        this.repaint();
        
        return list.indexOf(panel);
    }
	
	public void removeProcess(Integer id){
		this.remove(list.get(id));
		list.remove(id);
		this.revalidate();
		this.repaint();
	}
}
