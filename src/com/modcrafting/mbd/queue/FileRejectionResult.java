package com.modcrafting.mbd.queue;

public class FileRejectionResult {
    private QueueFile qf;
    private String reason;
    
    public FileRejectionResult(QueueFile qf, String reason) {
        this.qf = qf;
        this.reason = reason;
    }
    
    public String getRejectionReason() {
        return this.reason;
    }
    
    public QueueFile getQueueFile() {
        return this.qf;
    }

}
