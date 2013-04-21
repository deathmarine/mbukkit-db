package com.modcrafting.mbd.decom;

import java.awt.Component;
import java.awt.Toolkit;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
//import com.modcrafting.mbd.MasterPluginDatabase;

public class CheckedTreeCellRenderer extends DefaultTreeCellRenderer{
	private static final long serialVersionUID = -9076467828472979936L;
	DecompJar jar;
	Icon image;
	Icon image2;
	Icon image3;
	Icon image4;
	Icon image5;
	Icon image6;
	Icon image7;
	Icon image8;
	public CheckedTreeCellRenderer(DecompJar jar){
		this.image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/star.png")));
		this.image2 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/bukkit-icon-tiny.png")));
		this.image3 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/package_obj.png")));
		this.image4 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/java.png")));
		this.image5 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/file.png")));
		this.image6 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/warn.png")));
		this.image7 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/package_obj_star.png")));
		this.image8 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/package_obj_warn.png")));
//		this.image = new ImageIcon(Chekkit.PATH+File.separator+"resources"+File.separator+"star.png");
//		this.image2 = new ImageIcon(Chekkit.PATH+File.separator+"resources"+File.separator+"bukkit_small.png");
//		this.image3 = new ImageIcon(Chekkit.PATH+File.separator+"resources"+File.separator+"package_obj.png");
//		this.image4 = new ImageIcon(Chekkit.PATH+File.separator+"resources"+File.separator+"java.png");
//		this.image5 = new ImageIcon(Chekkit.PATH+File.separator+"resources"+File.separator+"file.png");
//		this.image6 = new ImageIcon(Chekkit.PATH+File.separator+"resources"+File.separator+"warn.png");
//		this.image7 = new ImageIcon(Chekkit.PATH+File.separator+"resources"+File.separator+"package_obj_star.png");
//		this.image8 = new ImageIcon(Chekkit.PATH+File.separator+"resources"+File.separator+"package_obj_warn.png");
		this.jar = jar;
	}
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (isSafe(value) && isJava(value)){
			setIcon(image);
			return this;
		}else if(isWarn(value) && isJava(value)){
			setIcon(image6);
			return this;
		}else if(isJar(value)){
			setIcon(image2);
			return this;
		}else if(!leaf && isPackageSafe(value)){
			setIcon(image7);
			return this;
		}else if(!leaf && isPackageWarn(value)){
			setIcon(image8);
			return this;
		}else if(!leaf){
			setIcon(image3);
			return this;
		}else if(leaf && isJava(value) && !isSafe(value) && !isWarn(value)){
			setIcon(image4);
			return this;
		}else{
			setIcon(image5);
			return this;
		}
	}
	
	protected boolean isSafe(Object value) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) value;
        String title = (String) node.getUserObject();
        
        if (jar.safe.containsKey(title)){ //BAD
            return true;
        }
        return false;
    }
	
	protected boolean isPackageSafe(Object value) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) value;
        Enumeration<?> enums = node.children();
        int i = 0,o = 0;
        while(enums.hasMoreElements()){
        	DefaultMutableTreeNode child = (DefaultMutableTreeNode) enums.nextElement();
        	String ntitle = (String) child.getUserObject();
        	if(jar.safe.containsKey(ntitle))
        		o++;
        	i++;
        }
        if(i==o)
        	return true;
        return false;
	}
	
	protected boolean isPackageWarn(Object value) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) value;
        Enumeration<?> enums = node.children();
        while(enums.hasMoreElements()){
        	DefaultMutableTreeNode child = (DefaultMutableTreeNode) enums.nextElement();
        	String ntitle = (String) child.getUserObject();
        	if(jar.warn.containsKey(ntitle))
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
