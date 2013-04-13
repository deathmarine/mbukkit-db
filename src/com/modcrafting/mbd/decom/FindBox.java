package com.modcrafting.mbd.decom;

import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class FindBox extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4125409760166690462L;
	JCheckBox mcase;
    JCheckBox wrap;
    JCheckBox wholew;
    JCheckBox reverse;
    JButton findButton;
    JButton nextButton;
    JTextField textField;
    DecompJar base;
	public FindBox(DecompJar base) {
		this.base = base;
		
        JLabel label = new JLabel("Find What:");
        textField = new JTextField();
        mcase = new JCheckBox("Match Case");
        wrap = new JCheckBox("Wrap Around");
        wholew = new JCheckBox("Whole Words");
        reverse = new JCheckBox("Search Backwards");
        
        findButton = new JButton("Find");
        findButton.addActionListener(new FindButton());
        
        nextButton = new JButton("Find Next...");
        findButton.addActionListener(new FindButton());
        
        mcase.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wholew.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        reverse.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
 
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
 
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addComponent(label)
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                .addComponent(textField)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(mcase)
                        .addComponent(wholew))
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(wrap)
                        .addComponent(reverse))))
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                .addComponent(findButton)
                .addComponent(nextButton))
        );
        
        layout.linkSize(SwingConstants.HORIZONTAL, findButton, nextButton);
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                .addComponent(label)
                .addComponent(textField)
                .addComponent(findButton))
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(mcase)
                        .addComponent(wrap))
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(wholew)
                        .addComponent(reverse)))
                .addComponent(nextButton))
        );
 
        this.setTitle("Find");
        this.pack();
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }
	private class FindButton extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 75954129199541874L;

		@Override
		public void actionPerformed(ActionEvent event) {
			JButton button = (JButton) event.getSource();
			String name = button.getText();
			if(name.equals("Find")){
				int pos = base.tabbed.getSelectedIndex();
				String title = base.tabbed.getTitleAt(pos);
				HashFile hfile = base.open.get(title);
				Document doc = hfile.textArea.getDocument();
				try {
					String sg = doc.getText(0, doc.getLength()-1);
					int ps = sg.indexOf(textField.getText());
					hfile.textArea.select(ps, ps+textField.getText().length());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			if(name.equals("Find Next...")){
				
			}
		}
		
	}
}
