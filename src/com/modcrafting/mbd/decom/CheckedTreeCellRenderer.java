package com.modcrafting.mbd.decom;

import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.modcrafting.mbd.MasterPluginDatabase;

public class CheckedTreeCellRenderer extends DefaultTreeCellRenderer{
	private static final long serialVersionUID = -9076467828472979936L;
	DecompJar jar;
	Icon image;
	Icon image2;
	Icon image3;
	Icon image4;
	Icon image5;
	Icon image6;
	public CheckedTreeCellRenderer(DecompJar jar){
		this.image = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"star.png");
		this.image2 = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"bukkit_small.png");
		this.image3 = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"package_obj.png");
		this.image4 = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"java.png");
		this.image5 = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"file.png");
		this.image6 = new ImageIcon(MasterPluginDatabase.PATH+File.separator+"resources"+File.separator+"warn.png");
		this.jar = jar;
	}
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (isSafe(value)){
			setIcon(image);
			setToolTipText("This is a safe file.");
			return this;
		}else if(isWarn(value)){
			setIcon(image6);
			setToolTipText(null);
			return this;
		}else if(isJar(value)){
			setIcon(image2);
			setToolTipText(null);
			return this;
		}else if(!leaf){
			setIcon(image3);
			setToolTipText(null);
			return this;
		}else if(leaf && isJava(value) && !isSafe(value) && !isWarn(value)){
			setIcon(image4);
			setToolTipText(null);
			return this;
		}else{
			setIcon(image5);
			setToolTipText(null);
			return this;
		}
	}
	
	protected boolean isSafe(Object value) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        String title = (String) node.getUserObject();
        if (jar.safe.containsKey(title)) {
            return true;
        }
        return false;
    }
	
	protected boolean isJar(Object value) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        String title = (String) node.getUserObject();
        if (title.endsWith(".jar")) {
            return true;
        }
        return false;
    }
	
	protected boolean isJava(Object value) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        String title = (String) node.getUserObject();
        if (title.endsWith(".java")) {
            return true;
        }
        return false;
    }
	
	protected boolean isWarn(Object value) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)value;
        String title = (String) node.getUserObject();
        if (jar.warn.containsKey(title)) {
            return true;
        }
        return false;		
	}
	
	
}
