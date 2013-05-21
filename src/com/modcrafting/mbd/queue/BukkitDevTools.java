package com.modcrafting.mbd.queue;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ocpsoft.prettytime.PrettyTime;

import com.modcrafting.mbd.Chekkit;
import com.modcrafting.mbd.objects.BukkitDevPM;
import com.modcrafting.mbd.objects.MessageQueue;

public class BukkitDevTools {

    public BukkitDevTools() {
    }

    public static void sendBukkitDevPM(String user, String subject, String message, String key) {
        try {
            String url = "http://dev.bukkit.org/home/send-private-message/?api-key=" + key;
            Document doc = Jsoup.connect(url).data("cc_users", "").data("standard_users", user).data("subject", subject).data("markup_type", "creole").data("markup", message).userAgent(Chekkit.USER_AGENT).ignoreHttpErrors(true).timeout(0).post();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    
    
    public static String tokenizePMString(String PM, QueueFile qf, String deadline) {
        PM = PM.replace(":fileTitle:", qf.getTitle())
                .replace(":fileLink:", qf.getFilePageURL())
                .replace(":fileURL:", qf.getFileDownloadURL())
                .replace(":fileEditURL:", qf.getFilePageURL() + "edit/")
                .replace(":editButton:", "[[" + qf.getFilePageURL() + "edit/" + "|{{http://i.imgur.com/49jgiZM.png|}}]]")
                .replace(":authorName:", qf.getAuthor())
                .replace(":authorURL", "http://dev.bukkit.org/profiles/" + qf.getAuthor())
                .replace(":claimedName", Chekkit.bukkitDevUsername)
                .replace(":claimedURL", "http://dev.bukkit.org/profiles/" + qf.getClaimed())
                .replace(":deadline:", deadline)
                .replace(":titleExample:", "'LiteKits v1.0'")
                .replace(":projectName:", qf.getProjectName())
                .replace(":projectURL:", qf.getProjectURL())
                .replace(":fileSize:", qf.getReadableSize())
                .replace(":filePostDate:", BukkitDevTools.prettyTime(qf.getUploadTime()));
        
        return PM;
        
    }

    private static void showLabel(final String text, final QueueWindow qw) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                qw.showLabel(text);
            }

        });
    }

    public static void claimFiles(List<QueueFile> qfl, QueueWindow qw, String key, Chekkit ck) {
        Chekkit.log.info("Checking files for issues");
        BukkitDevTools.showLabel("Checking files for issues...", qw);


        List<BukkitDevPM> PMs = new ArrayList<BukkitDevPM>();
        List<Integer> filesToClaim = new ArrayList<Integer>();
        List<File> filesToDecompile = new ArrayList<File>();
        for (QueueFile qf : qfl) {
            Chekkit.log.info("Checking file " + qf.getFileID());
            if (qf.selected) {
                BukkitDevTools.showLabel("Checking file " + qf.getFileID() + "...", qw);
                if (qf.getClaimed() != null) {
                    BukkitDevTools.showLabel("File " + qf.getFileID() + " is already claimed. Informing user...", qw);
                    String msg = "The file '" + qf.getTitle() + "' is under review by " + qf.getClaimed() + ".\nDo you wish to claim this file anyway?";
                    int cont = JOptionPane.showConfirmDialog(qw, msg, "Warning! File already claimed.", JOptionPane.YES_NO_OPTION);
                    if (cont != JOptionPane.YES_OPTION) {
                        BukkitDevTools.showLabel("File " + qf.getFileID() + " is already claimed. User decided to abort file.", qw);
                        continue; //Next file please
                    }
                }
                
                if (qf.getAuthor().equals(Chekkit.bukkitDevUsername)) {
                    
                    BukkitDevTools.showLabel("File " + qf.getFileID() + " is user's own file. Informing user...", qw);
                    String msg = qf.getTitle() + " was uploaded by you. Generally, you need to let other staff review your files. Do you wish to continue?";
                    int cont = JOptionPane.showConfirmDialog(qw, msg, "Warning! File is your own.", JOptionPane.YES_NO_OPTION);
                    if (cont != JOptionPane.YES_OPTION) {
                        BukkitDevTools.showLabel("File " + qf.getFileID() + " is user's own file. User decided to abort file.", qw);
                        continue; //Next file please
                    }
                }

                if (!qf.hasNumberInTitle()) {
                    BukkitDevTools.showLabel("File " + qf.getFileID() + " has no title in version. Informing user.", qw);
                    String msg = "The file '" + qf.getTitle() + "' appears to be missing a version from its title.\nWould you like to send the user a PM?";
                    int cont = JOptionPane.showConfirmDialog(qw, msg, "Warning! File has no version number in title.", JOptionPane.YES_NO_OPTION);
                    if (cont == JOptionPane.YES_OPTION) {
                        BukkitDevTools.showLabel("File " + qf.getFileID() + " requires a PM to be sent. Adding message to queue...", qw);
                        PMs.add(qf.getVersionPM());
                    }
                }
                File dls = new File(Chekkit.PATH + File.separator + "downloads");
                if (!dls.exists() && !dls.mkdir()) {

                } else {
                    try {
                        String name = qf.getFileID() + "." + qf.getFileDownloadURL().substring(qf.getFileDownloadURL().lastIndexOf('/') + 1, qf.getFileDownloadURL().length());
                        BukkitDevTools.showLabel("Downloading " + name + "...", qw);
                        
                        File destination = new File(Chekkit.PATH + File.separator + "downloads" + File.separator + name);
                        
                        if (!destination.getPath().endsWith(".jar") && !destination.getPath().endsWith(".zip") && qf.isServerMod()) {
                            JOptionPane.showMessageDialog(qw, "This file is a not a JAR or ZIP. It'll be downloaded, but you'll need to extract/process it manually.", "Warning!", JOptionPane.WARNING_MESSAGE);
                        }
                        
                        FileUtils.copyURLToFile(new URL(qf.getFileDownloadURL()), destination);


                        if (destination.getPath().endsWith(".jar") || destination.getPath().endsWith(".zip")) {
                            filesToDecompile.add(destination);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Chekkit.log.info("Adding file");
                filesToClaim.add(qf.getFileID());
                qf.setClaimed();


            }


        }
        if (PMs.size() > 0) {
            new MessageQueue(PMs, key).setVisible(true);
        }
        Chekkit.log.info("Sending request...");
        BukkitDevTools.showLabel("Sending request...", qw);
        Connection c = Jsoup.connect("http://dev.bukkit.org/admin/approval-queue/?api-key=" + key);
        c.data("form_type", "file");
        c.data("file-status", "u");
        c.timeout(0);

        for (Integer id : filesToClaim) {
            c.data("file_checklist", id.toString());
            Chekkit.log.info("Added id: " + id);
        }
        try {
            c.userAgent(Chekkit.USER_AGENT).post();
        } catch (Exception e) {

            e.printStackTrace();
        }
        Chekkit.log.info("Refreshing");
        decompileSomeFiles(filesToDecompile, ck);
        requestQueueUpdate(qw);

    }

    public static void decompileSomeFiles(final List<File> files, final Chekkit ck) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ck.handleFiles(files);
            }
        });
    }

    public static String ordinal(int num) {
        String[] suffix = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        int m = num % 100;
        return num + suffix[(m > 10 && m < 20) ? 0 : (m % 10)];
    }


    public static UserInfo checkAPIKey(String key) {
        try {
            Document doc1 = Jsoup.connect("http://dev.bukkit.org/home/?api-key=" + key).userAgent(Chekkit.USER_AGENT).timeout(120000).get();
            Element loginReq = doc1.getElementById("login-next");

            if (loginReq != null) {
                return new UserInfo(null, KeyState.INVALID);
            }
            Element header = doc1.getElementById("hd");
            String username = header.child(1).text();
            if (username != null) {
                Chekkit.bukkitDevUsername = username;
            }
            Elements actions = doc1.getElementsByTag("dt");
            Boolean normal = false;
            for (Element action : actions) {
                if (action.text().equals("General")) {
                    normal = true;
                }

                if (action.text().equals("Moderation")) {


                    Chekkit.log.info(username);
                    return new UserInfo(username, KeyState.STAFF);

                }
            }

            if (normal) {
                return new UserInfo(username, KeyState.NORMAL);
            } else {
                return new UserInfo(null, KeyState.INVALID);
            }

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

    }

    public static String prettyTime(long timestamp) {
        PrettyTime p = new PrettyTime();
        return p.format(new Date(timestamp * 1000));
    }

    public static float sizeToBytes(String size) {
        // IEC denotes the i to show binary (2^x) sizes:
        // 1 KiB = 1024 bytes
        if (size.contains("KiB")) { // KibiBytes
            size = size.substring(0, size.indexOf("KiB") - 1);
            try {
                float f = Float.parseFloat(size);
                return f * 1024;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        if (size.contains("MiB")) { // MebiBytes
            size = size.substring(0, size.indexOf("MiB") - 1);
            try {
                float f = Float.parseFloat(size);
                return f * 1048576;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        if (size.contains("GiB")) { // Gibibyte !?!?
            size = size.substring(0, size.indexOf("GiB") - 1);
            try {
                float f = Float.parseFloat(size);
                return f * 1073741824;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }

        }

        // Else, we're assuming *B*ytes

        if (!size.contains("B")) {
            return -1;
        }

        size = size.substring(0, size.indexOf("B") - 1);
        try {
            float f = Float.parseFloat(size);
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static void removeFileFromTable(final List<Integer> fileIndexes, final QueueWindow qw) {
        /*
         * SwingUtilities.invokeLater(new Runnable() {
         * 
         * @Override public void run() { int i = 0; for (Integer qf :
         * fileIndexes) {
         * 
         * ((FileTableModel) qw.table.getModel()).removeRow(qf); //Drop the
         * index by 1 since the size is one less i++; }
         * 
         * } });
         */// Not used as we force a refresh when approving files anyway.
    }

    public static void requestQueueUpdate(final QueueWindow qw) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                qw.contentPane.remove(qw.scrollPane);
                qw.contentPane.repaint();
                qw.showLabel("Refreshing Queue...");
                qw.getQueue();

            }
        });
    }

    /**
     * This is a ridiculously messy method that parses the approval queue
     * 
     * @param key - The API key to use
     */
    public static ApprovalQueue parseFiles(String key, Boolean includeClaimed, String username) {
        List<String> sn = new ArrayList<String>();
        List<QueueFile> qfl = new ArrayList<QueueFile>();
        int numClaimed = 0;
        int total = 0;
        int own = 0;
        try {
            Connection c = Jsoup.connect("http://dev.bukkit.org/admin/approval-queue/?api-key=" + key);
            c.timeout(180000);
            Document doc1 = c.get();
            Element filesTable = doc1.getElementById("files");
            if (filesTable == null) {
                throw new Exception("Couldn't find the files table.");
            }

            filesTable = filesTable.getElementsByTag("tbody").get(0);
            Elements e = filesTable.getElementsByClass("row-joined-to-next");
            if (e.isEmpty()) {
                throw new Exception("No files in table.");
            }
            for (Element file : e) {
                Elements infoBlocks = file.getElementsByTag("td");
                if (infoBlocks.size() != 7) {
                    throw new Exception("Wrong number of info blocks.");
                }
                // *cringe*
                // int fileId = 0;
                int fileId = Integer.parseInt(infoBlocks.get(0).child(0).getAllElements().get(0).attr("value"));
                // Chekkit.log.info(infoBlocks.get(0).child(0).toString());
                String projectName = infoBlocks.get(1).text();
                String projectURL = infoBlocks.get(1).child(0).attr("href");
                String fileTitle = infoBlocks.get(2).child(0).text();
                String filePageURL = "http://dev.bukkit.org" + infoBlocks.get(2).child(0).attr("href");
                String fileDirectLink = infoBlocks.get(3).child(0).attr("href");
                // Chekkit.log.info(infoBlocks.get(3).child(0).toString());
                String size = infoBlocks.get(4).text().trim();
                float bytes = BukkitDevTools.sizeToBytes(size);
                String uploader = infoBlocks.get(5).text().trim();
                long date = Long.parseLong(infoBlocks.get(6).child(0).attr("data-epoch"));
                String claimed = infoBlocks.get(3).text();

                Boolean staff = infoBlocks.get(5).child(0).hasClass("user-moderator");
                if (staff && !sn.contains(uploader)) {
                    sn.add(uploader);
                }
                if (!claimed.contains("(Under Review")) {
                    claimed = null;
                } else {
                    claimed = claimed.substring(claimed.indexOf("(Under Review by ") + 17).trim();
                    claimed = claimed.substring(0, claimed.length() - 1);
                    numClaimed++;
                }
                total++;
                //If the file is claimed, and the user wants to exclude claimed files (only show unclaimed files)
                //and the file isn't claimed by the user in question, we'll skip the file
                if (claimed != null && !includeClaimed && !claimed.contains(username)) {
                    Chekkit.log.info("Skipping file claimed by " + claimed + " because not " + username);
                    if (claimed.contains(username))
                        own++;
                    continue;
                }

                QueueFile qf = new QueueFile(fileId, bytes, uploader, fileTitle, filePageURL, fileDirectLink, projectName, projectURL, claimed, date, size, staff, projectURL.contains("server-mod"));
                qfl.add(qf);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        ApprovalQueue aq = new ApprovalQueue(qfl, numClaimed, total, sn, own);
        return aq;
    }
    
    public static void harvestDeletionReasons(List<QueueFile> files, QueueWindow qw, String APIKey, Chekkit ck) {
        List<String> reasons = new ArrayList<String>();
        List<QueueFile> filesToDelete = new ArrayList<QueueFile>();
        
        for (QueueFile qf: files) {
            if (qf.selected && (qf.getClaimed() == null || !qf.getClaimed().equals(Chekkit.bukkitDevUsername))) {
                Chekkit.log.info(Chekkit.bukkitDevUsername + " " + qf.getClaimed());
                JOptionPane.showMessageDialog(null, "You must claim " + qf.getTitle() + " before deletion.\nAborting deletion...");
                break;
            }
            if (qf.selected && qf.hasNumberInTitle()) {
                showLabel("Harvesting reason for " + qf.getTitle(), qw);
                
                FileRejectionResult fr = new FileRejectionWindow(qf, null, APIKey).getResult();
                if (fr == null || fr.getRejectionReason().isEmpty()) {
                    Chekkit.log.info("ABORT");
                    reasons = null;
                    break;
                }
                Chekkit.log.info("ADDED");
                reasons.add(fr.getRejectionReason());
                filesToDelete.add(qf);
            }
            
            if (qf.selected && !qf.hasNumberInTitle()) {
                showLabel("Harvesting reason for " + qf.getTitle(), qw);
                FileRejectionResult fr = new FileRejectionWindow(qf, "File contains no version in title.", APIKey).getResult();
                if (fr == null || fr.getRejectionReason().isEmpty()) {
                    
                    Chekkit.log.info("ABORT");
                    reasons = null;
                    break;
                }
                Chekkit.log.info("ADDED");
                reasons.add(fr.getRejectionReason());
                filesToDelete.add(qf);
            }
        }
        if (reasons == null) {
            showLabel("User aborted deletion process", qw);
            return;
        }
        Chekkit.log.info("OUTPUT");
        for (String s : reasons) {
            Chekkit.log.info(s);
        }
        BukkitDevTools.deleteFiles(filesToDelete, qw, APIKey, ck, reasons);
    }
    
    public static void deleteFiles(List<QueueFile> files, QueueWindow queueWindow, String APIKey, Chekkit ck, List<String> reasons) {
        List<Integer> fileIds = new ArrayList<Integer>();
        List<Integer> fileIndexes = new ArrayList<Integer>();
        int i = 0;
        int toRemove = 0;
        Boolean co = true;
        //List<Integer> filesRemoving = new ArrayList<Integer>();
        for (QueueFile qf : files) {

            if (qf.selected) {
                if (qf.getClaimed() == null) {
                    JOptionPane.showMessageDialog(queueWindow, "You need to claim " + qf.getTitle() + " first.");
                    co = false;
                } else {
                    fileIds.add(qf.getFileID());
                    fileIndexes.add(i);
                    i++;

                }
            }
            toRemove++;
        }

        if (!co)
            return;

        if (fileIndexes.size() != reasons.size()) {
            throw new IllegalArgumentException("Reasons length should match number of files selected.");
        }
        //removeFileFromTable(fileIndexes, queueWindow);

        Connection c = Jsoup.connect("http://dev.bukkit.org/admin/approval-queue/?api-key=" + APIKey);
        c.data("form_type", "file");
        c.data("file-status", "d"); //d = deleted
        c.timeout(0);
        i = 0;
        for (int id : fileIds) {
            if (id == 0)
                break;
            c.data("file_checklist", Integer.toString(id));
            c.data("file-note", reasons.get(i));
            Chekkit.log.info("Added id: " + id + " with reason " + reasons.get(i));
            showLabel("Adding file + " + id + " to queue...", queueWindow);
            i++;
        }
        Chekkit.log.info("Sending request...");
        showLabel("Sending request...", queueWindow);
        try {
            Document d = c.userAgent(Chekkit.USER_AGENT).post();
        } catch (Exception e) {

            e.printStackTrace();
        }
        Chekkit.log.info("All done.");
        showLabel("Files deleted!", queueWindow);
        requestQueueUpdate(queueWindow);

    }
    
    public static void banUser(String username, String APIKey, String reason, Boolean stopLogin, Boolean removeComments, Boolean removeProjects) {
        String url = "http://dev.bukkit.org/admin/ban-user/?api-key=" + APIKey;
        Connection c = Jsoup.connect(url);
        if (stopLogin) {
            c.data("is_banned", "y");
        }
        
        if (removeComments) {
            c.data("delete_all_comments", "y");
        }
        
        if (removeProjects) {
            c.data("delete_all_projects", "y");
        }
        c.data("reason", reason);
        c.data("user", username);
        c.data("confirm", "y");
        
        c.timeout(0);
        try {
            c.userAgent(Chekkit.USER_AGENT).post();
        } catch (Exception e) {

            e.printStackTrace();
        }
        
    }

    public static void approveFiles(List<QueueFile> files, QueueWindow queueWindow, String APIKey, Chekkit ck) {
        List<Integer> fileIds = new ArrayList<Integer>();
        List<Integer> fileIndexes = new ArrayList<Integer>();
        int i = 0;
        int toRemove = 0;
        Boolean co = true;
        //List<Integer> filesRemoving = new ArrayList<Integer>();
        for (QueueFile qf : files) {

            if (qf.selected) {
                if (!qf.hasNumberInTitle()) {
                    JOptionPane.showMessageDialog(queueWindow, qf.getTitle() + " doesn't have a version in it's title.\nIf you've PM'd the user, please wait for them to add one.");
                    co = false;
                }
                if (qf.getClaimed() == null) {
                    JOptionPane.showMessageDialog(queueWindow, "You need to claim " + qf.getTitle() + " first.");
                    co = false;
                } else {
                    fileIds.add(qf.getFileID());
                    fileIndexes.add(i);
                    i++;

                }
            }
            toRemove++;
        }

        if (!co)
            return;


        //removeFileFromTable(fileIndexes, queueWindow);

        Connection c = Jsoup.connect("http://dev.bukkit.org/admin/approval-queue/?api-key=" + APIKey);
        c.data("form_type", "file");
        c.data("file-status", "s"); //s = safe?
        c.timeout(0);

        for (int id : fileIds) {
            if (id == 0)
                break;
            c.data("file_checklist", Integer.toString(id));
            Chekkit.log.info("Added id: " + id);
            showLabel("Adding file + " + id + " to queue...", queueWindow);
        }
        showLabel("Sending request...", queueWindow);
        try {
            c.userAgent(Chekkit.USER_AGENT).post();
        } catch (Exception e) {

            e.printStackTrace();
        }
        showLabel("Files approved!", queueWindow);
        requestQueueUpdate(queueWindow);

    }

    public static void recoverFiles(List<QueueFile> files, QueueWindow queueWindow, Chekkit ck) {
        List<File> toDecompile = new ArrayList<File>();
        for (QueueFile qf : files) {
            if (qf.selected) {
                String name = qf.getFileID() + "." + qf.getFileDownloadURL().substring(qf.getFileDownloadURL().lastIndexOf('/') + 1, qf.getFileDownloadURL().length());
                File destination = new File(Chekkit.PATH + File.separator + "downloads" + File.separator + name);
                if (destination.exists()) {
                    Chekkit.log.info("Found the file, let's try processing it.");
                    toDecompile.add(destination);
                } else {
                    Chekkit.log.info("Didn't find " + name + " - let's download it.");
                    
                    try {
                        FileUtils.copyURLToFile(new URL(qf.getFileDownloadURL()), destination);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    toDecompile.add(destination);
                }
            }
        }
        decompileSomeFiles(toDecompile, ck);
        
    }

}
