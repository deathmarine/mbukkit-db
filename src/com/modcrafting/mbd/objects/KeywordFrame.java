package com.modcrafting.mbd.objects;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@ SuppressWarnings ({"unchecked", "rawtypes"})
public class KeywordFrame extends JDialog implements ActionListener, MouseListener{

	private static final long serialVersionUID = -9357517243027081L;
	public boolean isOpen = false;
	private Map<String, String> keywords;
	JButton edit;
	JButton add;
	JButton remove;
	JButton reset;
	JScrollPane jsp;
	JList list;
	String selected = null;
	
	public KeywordFrame(Map<String, String> map){
		this.setName("Keywords");
		this.keywords = map;
		this.createGUI();
		this.isOpen = true;
	}
	
	private void createGUI(){
		list = new JList(this.keywords.keySet().toArray());
		
		jsp = new JScrollPane(list);
		list.addMouseListener(this);
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		
		edit = new JButton("Edit");
		edit.addActionListener(this);
		add = new JButton("Add");
		add.addActionListener(this);
		remove = new JButton("Remove");
		remove.addActionListener(this);
		reset = new JButton("Reset to default");
		reset.addActionListener(this);
		buttons.add(edit);
		buttons.add(add);
		buttons.add(remove);
		buttons.add(reset);
		
		jsp.setSize(450, 300);
		buttons.setSize(450, 100);
		main.setSize(500, 450);
		main.add(jsp);
		main.add(buttons);
		this.setSize(new Dimension(500,500));
		this.add(main);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width / 2 - (this.getSize().width / 2), screenSize.height / 2 - (this.getSize().height / 2));
		
		main.setVisible(true);
		this.setVisible(true);
	}

	
	public void errorMessage(String string) {
        JOptionPane.showMessageDialog(null, string, "Error!", 1);
    }
	
	@ Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == edit){
			if(selected == null){
				return;
			}
			String check = JOptionPane.showInputDialog("Enter the new keyword(s) to search for:");
            if (check == null || check.length() < 1) {
                errorMessage("You must enter a string!");
                return;
            }
            String message = JOptionPane.showInputDialog("Enter the message to display:");
            if (message == null || message.length() < 1) {
                errorMessage("You must enter a string!");
                return;
            }
            keywords.remove(selected);
            keywords.put(check, message);
            list.setListData(keywords.keySet().toArray());
            System.out.println("\nEditted Keyword: " + selected + " to " + check + " : " + message + "\n");
		}else if(e.getSource() == add){
			String check = JOptionPane.showInputDialog("Enter the keyword(s) to search for:");
			if (check == null || check.length() < 1) {
	             errorMessage("You must enter a string!");
	             return;
	         }
	         String message = JOptionPane.showInputDialog("Enter the message to display:");
	         if (message == null || message.length() < 1) {
	             errorMessage("You must enter a string!");
	             return;
	         }

	         keywords.put(check, message);
	         list.setListData(keywords.keySet().toArray());
	         System.out.println("\nAdded Keyword: " + check + " : " + message + "\n");
		}else if(e.getSource() == remove){
			if(selected == null){
				return;
			}
			keywords.remove(selected);
			list.setListData(keywords.keySet().toArray());
			System.out.println("\nRemoved Keyword: " + selected + "\n");
		}else if(e.getSource() == reset){
			keywords.clear();
			keywords.put(".getName().equals", "[WARN] Possible player name check");
			keywords.put(".getDisplayName().equals", "[WARN] Possible player name check");
			keywords.put(".setDisplayName(\"", "[WARN] Setting player display name directly");
			keywords.put(".setBanned(", "[WARN] Banning player");
			keywords.put("new URL(", "[WARN] Setting up URL connection");
			keywords.put(".openConnection(", "[WARN] Opens URL connection");
			keywords.put(".dispatchCommand(", "[WARN] Dispatches a command");
			keywords.put("http", "[WARN] Making HTTP(S) connection");
			keywords.put("getDefinedMethod", "[WARN] Getting defined method");
			keywords.put("getMethod", "[WARN] Getting method");
			keywords.put("ClassLoader.class", "[WARN] Use of ClassLoader");
			keywords.put("hack", "[WARN] Use of the string \"hack\"");
			keywords.put(".setOp(", "[SEVERE] Setting OP status");
			keywords.put("backdoor", "[SEVERE] Use of the string \"backdoor\"");
			keywords.put("abstract enum", "[SEVERE] Use of abstract enum - investigate");
			keywords.put("\"op ", "[SEVERE] Setting op status");
			keywords.put("org.ow2", "[SEVERE] Using ASM");
			keywords.put("org.objectweb.asm", "[SEVERE] Using ASM");
			keywords.put(".shutdown();", "[SEVERE] Shutdown server attempt.");
			keywords.put("Thread", "[WARN] Odd Use of threading.");
			keywords.put("Process", "[SEVERE] Execution of external processes.");
			keywords.put("System.getSecurityManager()", "[SEVERE] Checking for security manager.");
			keywords.put("System.set", "[SEVERE] Attempt to modify system configuration.");
			keywords.put("Runtime.getRuntime()", "[SEVERE] Runtime modification.");
			keywords.put("opme", "[SEVERE] Investigate.");
            list.setListData(keywords.keySet().toArray());
            System.out.println("\nReset keywords to default values.\n");
		}
	}

	@ Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == list){
			int index = list.locationToIndex(e.getPoint());
			list.setSelectedIndex(index);
			selected = (String)list.getSelectedValue();	
		}
	}
	@ Override
	public void mousePressed(MouseEvent e) {}
	@ Override
	public void mouseReleased(MouseEvent e) {}
	@ Override
	public void mouseEntered(MouseEvent e) {}
	@ Override
	public void mouseExited(MouseEvent e) {}

	
}
