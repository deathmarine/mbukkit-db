package com.modcrafting.mbd.queue;

public class QueueFile {
    
    /**
     * File ID
     */
    private int id;
    
    /**
     * The size of the file that is in the queue. This is set in bytes.
     */
    private int size;
    
    /**
     * Human-readable String version of filesize
     */
    
    private String readableSize;
    
    /**
     * The BukkitDev username of the uploader.
     */
    private String author;
    
    /**
     * The title of the uploaded file (e.g. "LiteKits v1.0").
     */
    private String title;
    
    /**
     * The link to the file page.
     */
    private String fileLink;
    
    /**
     * The direct link to the JAR file.
     */
    private String directLink;
    
    /**
     * The file's project name.
     */
    private String projectName;
    
    /**
     * The link to the file's project.
     */
    private String projectLink;
    
    /**
     * null if unclaimed, a String with the staff's name if claimed.
     */
    private String claimed;
    
    /**
     * When the file was uploaded (epoch time)
     */
    private long postDate;
    
    /**
     * Whether the user that created the file is staff
     */
    private Boolean isStaff;

    public QueueFile(int id, int size, String author, String title, String fileLink, String directLink, String projectName, String projectLink, String claimed, long postDate, String readableSize, Boolean isStaff) {
        this.id = id;
        this.size = size;
        this.author = author;
        this.title = title;
        this.fileLink = fileLink;
        this.directLink = directLink;
        this.projectName = projectName;
        this.projectLink = projectLink;
        this.claimed = claimed;
        this.postDate = postDate;
        this.readableSize = readableSize;
        this.isStaff = isStaff;
        
    }
    //TODO: Lombok?
    
    public String getAuthor() {
        return this.author;
    }
    
    public String getClaimed() {
        return this.claimed;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public String getProjectName() {
        return this.projectName;
    }
    
    public String getProjectURL() {
        return this.projectLink;
    }
    
    public String getFilePageURL() {
        return this.fileLink;
    }
    
    public Boolean getCreatedByStaff() {
        return this.isStaff;
    }
    
    public String getFileDownloadURL() {
        return this.directLink;
    }
    
    public long getUploadTime() {
        return this.postDate;
    }
    
    public String getReadableSize() {
        return this.readableSize;
    }
    
    public void setFileStatus(FileStatus fs, String reason) {
        switch(fs) {
            case NORMAL:
                //TODO: Use API key to set file status to normal
                break;
            case UNDER_REVIEW:
                //TODO: Use API key to set file status to under review
                break;
            case DELETED:
                //TODO: Use API key and reason to set file to deleted
                break;
            
        }
    }
    
    

}
