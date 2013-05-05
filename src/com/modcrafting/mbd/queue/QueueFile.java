package com.modcrafting.mbd.queue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.modcrafting.mbd.objects.BukkitDevPM;

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
    
    /**
     * Whether or not the file is a server-mod
     */
    private Boolean serverMod = true;
   
    public Boolean selected = false;

    public QueueFile(int id, int size, String author, String title, String fileLink, String directLink, String projectName, String projectLink, String claimed, long postDate, String readableSize, Boolean isStaff, Boolean isServerMod) {
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
        this.serverMod = isServerMod;
        
    }
    //TODO: Lombok?
    
    public String getAuthor() {
        return this.author;
    }
    
    public String getClaimed() {
        return this.claimed;
    }
    
    public Boolean isServerMod() {
        return this.serverMod;
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
    
    public Boolean hasNumberInTitle() {
        return this.title.matches(".*\\d.*");
    }
    
    public int getFileID() {
        return this.id;
    }
    
    public BukkitDevPM getVersionPM() {
        String editURL = this.fileLink + "edit/";
        long theFuture = System.currentTimeMillis() + (86400 * 7 * 1000);
        Date nextWeek = new Date(theFuture);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(nextWeek);
        int d = cal.get(Calendar.DAY_OF_MONTH);
        String dayPart = d + BukkitDevTools.ordinal(d) + " ";
        String date = dayPart + sdf.format(nextWeek);
        BukkitDevPM message = new BukkitDevPM(this.author, "Hi " + this.author + "!\n\nThanks for uploading your file for " + this.projectName + ". Before it can be approved, you need to edit the file and add a version number to it. Example: 'LiteKits v1.0'.\n\nTo do this now, hit the button below and add the version to the 'Name' field.\n\n[[" + editURL + "|{{http://i.imgur.com/TvLphUs.png|}}]]\n\nPlease note that if you do not add a version before " + date + ", your file will be deleted and you'll need to upload it again.\n\nThanks!", "Your file, " + this.title);
        return message;
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
