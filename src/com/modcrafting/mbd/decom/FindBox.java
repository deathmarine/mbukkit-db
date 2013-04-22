package com.modcrafting.mbd.decom;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

public class FindBox extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4125409760166690462L;
	JCheckBox mcase;
    JCheckBox regex;
    JCheckBox wholew;
    JCheckBox reverse;
    JButton findButton;
    JTextField textField;
    DecompJar base;
	public FindBox(DecompJar base) {
		this.base = base;
		
        JLabel label = new JLabel("Find What:");
        textField = new JTextField();

		int pos = base.tabbed.getSelectedIndex();
		if(pos>=0){
			String title = base.tabbed.getTitleAt(pos);
			HashFile hfile = base.open.get(title);
			textField.setText(hfile.textArea.getSelectedText());
		}
        mcase = new JCheckBox("Match Case");
        regex = new JCheckBox("Regex");
        wholew = new JCheckBox("Whole Words");
        reverse = new JCheckBox("Search Backwards");
        
        findButton = new JButton("Find");
        findButton.addActionListener(new FindButton());
        this.getRootPane().setDefaultButton(findButton);
        
        mcase.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        regex.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wholew.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        reverse.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension center = new Dimension((int)(screenSize.width*0.35), (int)(screenSize.height*0.20));
		final int x = (int) (center.width * 0.2);
		final int y = (int) (center.height * 0.2);
		this.setBounds(x, y, center.width, center.height);
		this.setResizable(false);
        GroupLayout layout = new GroupLayout(getRootPane());
        getRootPane().setLayout(layout);
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
                        .addComponent(regex)
                        .addComponent(reverse))))
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                .addComponent(findButton))
        );
        
        layout.linkSize(SwingConstants.HORIZONTAL, findButton);
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                .addComponent(label)
                .addComponent(textField)
                .addComponent(findButton))
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(mcase)
                        .addComponent(regex))
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(wholew)
                        .addComponent(reverse))))
        );
 
        this.setName("Find");
        //this.pack();
        //this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }
	private class FindButton extends AbstractAction{
		/**
		 * 
		 */
		private static final long serialVersionUID = 75954129199541874L;

		@Override
		public void actionPerformed(ActionEvent event) {
			int pos = base.tabbed.getSelectedIndex();
			String title = base.tabbed.getTitleAt(pos);
			HashFile hfile = base.open.get(title);
			SearchContext context = new SearchContext();
      		if (textField.getText().length() == 0)
		         return;
      		
      		context.setSearchFor(textField.getText());
      		context.setMatchCase(mcase.isSelected());
      		context.setRegularExpression(regex.isSelected());
      		context.setSearchForward(!reverse.isSelected());
      		context.setWholeWord(wholew.isSelected());
      		
            if (!SearchEngine.find(hfile.textArea, context)) {
               hfile.textArea.setSelectionStart(0);
               hfile.textArea.setSelectionEnd(0);
            }
		}
		
	}
}
