package com.modcrafting.mbd.decom;

import java.awt.Component;
import java.awt.Toolkit;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
//import com.modcrafting.mbd.MasterPluginDatabase;

import com.modcrafting.mbd.Chekkit;

public class CheckedTreeCellRenderer extends DefaultTreeCellRenderer{
	private static final long serialVersionUID = -9076467828472979936L;
	DecompJar jar;
	JTree tree;
	Icon image;
	Icon image2;
	Icon image3;
	Icon image4;
	Icon image5;
	Icon image6;
	Icon image7;
	Icon image8;
	Icon image9;
	public CheckedTreeCellRenderer(DecompJar jar){
		this.image = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/star.png")));
		this.image2 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/bukkit-icon-tiny.png")));
		this.image3 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/package_obj.png")));
		this.image4 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/java.png")));
		this.image5 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/file.png")));
		this.image6 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/warn.png")));
		this.image7 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/package_obj_star.png")));
		this.image8 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/package_obj_warn.png")));
		this.image9 = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/resources/badskull_small.png")));
		this.jar = jar;
	}
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		this.tree = tree;
		if(!leaf && isBannedPack(value)){
			setIcon(image9);
			return this;
		}else if (isSafe(value) && isJava(value)){
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
	protected boolean isBannedPack(Object value){
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) value;
        String title = (String) node.getUserObject();
        for(String b: Chekkit.bannedpackage)
    		if(title.startsWith(b))
    			return true;
		return false;
	}
	
	protected boolean isWarn(Object value) {
		//NPE...
        //return jar.getJarFileEntryFromPath(new TreePath(((DefaultMutableTreeNode) value).getPath())).isWarn();
		return false;
	}
	
	protected boolean isSafe(Object value) {
		//NPE...
        //return jar.getJarFileEntryFromPath(new TreePath(((DefaultMutableTreeNode) value).getPath())).isSafe();
		return false;
    }
	
	protected boolean isPackageSafe(Object value) {
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) value;
        Enumeration<?> enums = node.children();
        int i = 0,o = 0;
        while(enums.hasMoreElements()){
            String[] args = ((DefaultMutableTreeNode) enums.nextElement()).getPath().toString().replace("[", "").replace("]", "").split(",");
    		StringBuilder sb = new StringBuilder();
    		for(int ii=1;ii<args.length-1;ii++){
    			sb.append(args[ii].trim()).append(".");
    		}
    		if(sb.length() > 0)
    			sb.deleteCharAt(sb.length()-1);
    		for(JarFileEntry jasr : jar.files){
    			if(jasr.getName().equalsIgnoreCase(args[args.length-1]) &&
    					jasr.getPackage().equalsIgnoreCase(sb.toString()) && jasr.isSafe()){
    				o++;
    			}
    		}
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
        int i = 0,o = 0;
        while(enums.hasMoreElements()){
            String[] args = ((DefaultMutableTreeNode) enums.nextElement()).getPath().toString().replace("[", "").replace("]", "").split(",");
    		StringBuilder sb = new StringBuilder();
    		for(int ii=1;ii<args.length-1;ii++){
    			sb.append(args[ii].trim()).append(".");
    		}
    		if(sb.length() > 0)
    			sb.deleteCharAt(sb.length()-1);
    		for(JarFileEntry jasr : jar.files){
    			if(jasr.getName().equalsIgnoreCase(args[args.length-1]) &&
    					jasr.getPackage().equalsIgnoreCase(sb.toString()) && jasr.isWarn()){
    				o++;
    			}
    		}
        	i++;
        }
        if(i==o)
        	return true;
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
        if (title.endsWith(".class")) {
            return true;
        }
        return false;
    }
	
	
}
