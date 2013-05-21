package com.modcrafting.mbd.queue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.modcrafting.mbd.Chekkit;
import com.modcrafting.mbd.objects.BukkitDevPM;

public class QueueFile {
    
    /**
     * File ID
     */
    private int id;
    
    /**
     * The size of the file that is in the queue. This is set in bytes.
     */
    private float size;
    
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

    public QueueFile(int id, float size, String author, String title, String fileLink, String directLink, String projectName, String projectLink, String claimed, long postDate, String readableSize, Boolean isStaff, Boolean isServerMod) {
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
    
    public float getSize() {
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
    
    public void setClaimed() {
        this.claimed = Chekkit.bukkitDevUsername;
    }
    
    public BukkitDevPM getVersionPM() {
        int num = Chekkit.config.getInteger("version-pm-days", 7);
        Chekkit.log.info(Integer.toString(num));
        Chekkit.log.info("Now: " + System.currentTimeMillis());
        long secs = (86400000L * num);
        long theFuture = System.currentTimeMillis() + secs;
        Chekkit.log.info(Long.toString(theFuture));
        
        Date nextWeek = new Date(theFuture);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(nextWeek);
        int d = cal.get(Calendar.DAY_OF_MONTH);
        String dayPart = BukkitDevTools.ordinal(d) + " ";
        String date = dayPart + sdf.format(nextWeek);
        String subject = "Your file, :fileTitle:";
        String msg = "Hi :authorName:!\n\n" +
        		     "Thanks for uploading your file for {{:projectURL|:ProjectName:}}. Before it can be approved, you need to edit the file and add a version number to it. Example: :titleExample:." +
        		     "\n\nTo do this now, hit the button below and add the version to the 'Name' field." +
        		     "\n\n:editButton:\n\nPlease note that if you do not add a version before :deadline:, your file will be deleted and you'll need to upload it again." +
        		     "\n\n**Once you've added your version, you should reply to this PM to let me know your file can be approved. This will save time for you and me.**" +
        		     "\n\nThanks!";

        BukkitDevPM message = new BukkitDevPM(this.author, BukkitDevTools.tokenizePMString(Chekkit.config.getString("version-pm-msg", msg), this, date), BukkitDevTools.tokenizePMString(Chekkit.config.getString("version-pm-subject", subject), this, date));
        
        return message;
    }

    
    

}
