package com.modcrafting.mbd.objects;

public class BukkitDevPM {
    public String receiver;
    public String message;
    public String subject;

    public BukkitDevPM(String receiver, String message, String subject) {
        this.receiver = receiver;
        this.message = message;
        this.subject = subject;
    }
    
    public String getRecipient() {
        return this.receiver;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getSubject() {
        return this.subject;
    }


}
