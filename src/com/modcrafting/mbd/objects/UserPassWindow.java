package com.modcrafting.mbd.objects;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SpringLayout;
import javax.swing.border.Border;


public class UserPassWindow extends JWindow implements ActionListener, KeyListener{

	private static final long serialVersionUID = 6328274426699959338L;
	private String username = "";
	private String password = "";
	
	private JPanel panel;
	private JButton submit;
	private JButton cancel;
	private JTextField userField;
	private JPasswordField passField;
	private JTextArea errorArea;
		
	public boolean isOpen = true;
		
	public UserPassWindow(){
		super();
		panel = new JPanel();
		Border compound = BorderFactory.createCompoundBorder(
				BorderFactory.createRaisedBevelBorder(), 
				BorderFactory.createLoweredBevelBorder());
		panel.setBorder(compound);
		this.setSize(300, 150);
		this.setAlwaysOnTop(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width/2 - (this.getSize().width/2),screenSize.height/2 - (this.getSize().height/2));
        this.setContents();
		this.add(panel);
		this.setVisible(true);
		while(this.isOpen){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.dispose();
	}
	
	public void setContents(){
		SpringLayout layout = new SpringLayout();
		submit = new JButton("Submit");
		submit.addActionListener(this);
		layout.putConstraint(SpringLayout.SOUTH, submit, 0, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.WEST, submit, 0, SpringLayout.WEST, panel);
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		layout.putConstraint(SpringLayout.SOUTH, cancel, 0, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.EAST, cancel, 0, SpringLayout.EAST, panel);
		panel.setLayout(layout);
		
		JPanel pane = new JPanel();
		userField = new JTextField("", 16);
		userField.addKeyListener(this);
		pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		pane.add(new JLabel("Username:     "));
		pane.add(userField);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, pane, -30, SpringLayout.VERTICAL_CENTER, panel);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pane, 0, SpringLayout.HORIZONTAL_CENTER, panel);
		panel.add(pane);
		
		pane = new JPanel();
		passField = new JPasswordField("", 16);
		passField.addKeyListener(this);
		pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		pane.add(new JLabel("Password:     "));
		pane.add(passField);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, pane, 0, SpringLayout.VERTICAL_CENTER, panel);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pane, 0, SpringLayout.HORIZONTAL_CENTER, panel);
		panel.add(pane);
		
		errorArea = new JTextArea("");
		errorArea.setVisible(false);
		
		submit.setSize(50, 15);
		cancel.setSize(50, 15);
		panel.add(submit);
		panel.add(cancel);
		panel.add(errorArea);
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public boolean checkUserAndPass(String user, String pass){
		try {
			String prop = "submitted=1&username="
					+ URLEncoder.encode(username, "UTF-8") + "&password=" 
					+ URLEncoder.encode(password, "UTF-8"); 
			HttpURLConnection httpcon = (HttpURLConnection) ((new URL("http://server.modcrafting.com:2063/check.php").openConnection()));
			httpcon.setDoOutput(true);
			httpcon.setDoInput(true);
			httpcon.setInstanceFollowRedirects(false); 
			httpcon.setRequestMethod("POST");
			httpcon.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			httpcon.setRequestProperty("Content-Length", "" + Integer.toString(prop.getBytes().length));
			DataOutputStream wr = new DataOutputStream(httpcon.getOutputStream());
			wr.writeBytes(prop);
			wr.flush();
			wr.close();
			httpcon.disconnect();
			StringBuilder builder = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
			String line;
			while((line = br.readLine()) != null) {
				builder.append(line);
			}
			br.close();
			final String[] arg = builder.toString().split(",");
			if(arg.length<1){
				this.errorArea.setText("Unable to connect to the site.");
				this.errorArea.setVisible(true);
				return false;
			}
			if(arg[0].equalsIgnoreCase("get")){
				this.errorArea.setText("Incorrect Username or Password.");
				this.errorArea.setVisible(true);
				return false;
			}
			this.username = arg[0];
			this.password = arg[1];
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == this.submit){
			try{
				if(this.errorArea.isVisible()){
					this.errorArea.setVisible(false);
				}
				if(this.userField.getText().equals("")){
					this.errorArea.setText("Please enter a username.");
					this.errorArea.setVisible(true);
				}else if(this.passField.getPassword().equals("")){
					this.errorArea.setText("Please enter a password.");
					this.errorArea.setVisible(true);
				}else{
					this.username = this.userField.getText();
					this.password = new String(this.passField.getPassword());
					if(this.checkUserAndPass(username, password)){
						this.isOpen = false;
					}
				}
			}catch(NullPointerException ex){
				this.errorArea.setText("Please ensure that your username and password have both been entered.");
				this.errorArea.setVisible(true);
			}
		}else if(e.getSource() == this.cancel){
			System.exit(0);
		}
	}

	@ Override
	public void keyPressed(KeyEvent e) {
		if(e.getSource() == this.userField || e.getSource() == this.passField){
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				this.submit.doClick();
			}else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.cancel.doClick();
			}
		}
	}

	@ Override
	public void keyReleased(KeyEvent e) {}

	@ Override
	public void keyTyped(KeyEvent e) {}	

    
}