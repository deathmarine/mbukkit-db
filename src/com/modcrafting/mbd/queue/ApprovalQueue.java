package com.modcrafting.mbd.queue;

import java.util.List;

public class ApprovalQueue {
    /**
     * This holds the list of QueueFile objects
     */
    private List<QueueFile> qfl;
    
    /**
     * The total number of files claimed
     */
    private int claimed;
    
    /**
     * The total number of files in the queue
     */
    private int total;
    
    
    /**
     * The list of staff authors in the queue.
     */
    private List<String> staffNames;

    public ApprovalQueue(List<QueueFile> qfl, int claimed, int total, List<String> staffNames) {
        this.qfl = qfl;
        this.claimed = claimed;
        this.total = total;
        this.staffNames = staffNames;
    }
    
    public List<QueueFile> getFileList() {
        return this.qfl;
    }
    
    public int getFileTotal() {
        return this.total;
    }
    
    public int getFilesClaimed() {
        return this.claimed;
    }
    
    public int getFilesUnclaimed() {
        return this.total - this.claimed;
    }
    
    public List<String> getStaffUploaders() {
        return this.staffNames;
    }

}
