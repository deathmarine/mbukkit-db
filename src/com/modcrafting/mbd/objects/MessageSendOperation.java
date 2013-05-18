package com.modcrafting.mbd.objects;

import java.util.List;
import java.util.UUID;

import javax.swing.SwingUtilities;

import com.modcrafting.mbd.queue.BukkitDevTools;

public class MessageSendOperation implements Runnable {
    private List<BukkitDevPM> queue;
    private String key;
    private MessageQueue mq;

    public MessageSendOperation(List<BukkitDevPM> PMs, String APIKey, MessageQueue mq) {
        this.queue = PMs;
        this.key = APIKey;
        this.mq = mq;
    }

    public void setLabel(final String text) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                mq.lblNewLabel.setText(text);
            }
        });
    }

    public void setProgress(final int amount) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                mq.progressBar.setValue(amount);
            }
        });
    }

    public void closeWindow() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                mq.setVisible(false);
                mq.dispose();
            }
        });
    }

    public void run() {
        int i = 1;

        mq.progressBar.setValue(0);
        int total = queue.size();
        for (BukkitDevPM message : queue) {
            String msg = message.getMessage();
            msg = msg + "<<size 0%>>" + UUID.randomUUID().toString() + "<</size>>"; //Stop Curse spam block
            setLabel("Sending message " + i + " of " + total + "...");
            BukkitDevTools.sendBukkitDevPM(message.getRecipient(), message.getSubject(), msg, key);
            setProgress((i / total) * 100);
            if (i != total) { //Only do this if we have another PM to send!
                setLabel("Waiting for 15 seconds...");
                try {
                    Thread.sleep(15000); //Stop Curse spam block
                } catch (InterruptedException e) {
                    //We don't care :-D
                    e.printStackTrace();
                }
                
            }
        }
        closeWindow();
        
    }

}
