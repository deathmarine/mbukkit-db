package com.modcrafting.mbd.objects;

import java.util.List;
import java.util.UUID;

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
    
    public void run() {
        int i = 1;
        mq.progressBar.setMaximum(100);
        mq.progressBar.setMinimum(0);
        mq.progressBar.setValue(0);
        int total = queue.size();
        for (BukkitDevPM message: queue) {
            String msg = message.getMessage();
            msg = msg + "<<size 0%>>" + UUID.randomUUID().toString() + "<</size>>"; //Stop Curse spam block
            mq.lblNewLabel.setText("Sending message " + i + " of " + total + "...");
            BukkitDevTools.sendBukkitDevPM(message.getRecipient(), message.getSubject(), msg, key);
            mq.progressBar.setValue((i / total) * 100);
            if (i != total) { //Only do this if we have another PM to send!
                mq.lblNewLabel.setText("Waiting for 15 seconds...");
                try {
                    Thread.sleep(15000); //Stop Curse spam block
                } catch (InterruptedException e) {
                    //We don't care :-D
                    e.printStackTrace();
                }
            }
        }
    }

}
