package com.modcrafting.mbd.queue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ocpsoft.prettytime.PrettyTime;

import com.modcrafting.mbd.Chekkit;

public class BukkitDevTools {

    public BukkitDevTools() {
    }

    public static void sendBukkitDevPM(String user, String subject, String message, String key) {
        try {
            String url = "http://dev.bukkit.org/home/send-private-message/?api-key=" + key;
            Document doc = Jsoup.connect(url).data("cc_users", "").data("standard_users", user).data("subject", subject).data("markup_type", "creole").data("markup", message).userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:20.0) Gecko/20100101 Firefox/20.0").ignoreHttpErrors(true).post();
            Chekkit.log.info(doc.toString());
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }

    public static KeyState checkAPIKey(String key) {
        try {
            Document doc1 = Jsoup.connect("http://dev.bukkit.org/home/?api-key=" + key).get();
            Element loginReq = doc1.getElementById("login-next");
            if (loginReq != null) {
                return KeyState.INVALID;
            }

            Elements actions = doc1.getElementsByTag("dt");
            Boolean normal = false;
            for (Element action : actions) {
                if (action.text().equals("General")) {
                    normal = true;
                }

                if (action.text().equals("Moderation")) {
                    return KeyState.STAFF;
                }
            }

            if (normal) {
                return KeyState.NORMAL;
            } else {
                return KeyState.INVALID;
            }

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

    }

    public static String prettyTime(long timestamp) {
        PrettyTime p = new PrettyTime();
        return p.format(new Date(timestamp * 1000));
    }

    public static int sizeToBytes(String size) {
        // IEC denotes the i to show binary (2^x) sizes:
        // 1 KiB = 1024 bytes
        if (size.contains("KiB")) { // KibiBytes
            size = size.substring(0, size.indexOf("KiB") - 1);
            try {
                float f = Float.parseFloat(size);
                return (int) f * 1024;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        if (size.contains("MiB")) { // MebiBytes
            size = size.substring(0, size.indexOf("MiB") - 1);
            try {
                float f = Float.parseFloat(size);
                return (int) f * 1048576;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        if (size.contains("GiB")) { // Gibibyte !?!?
            size = size.substring(0, size.indexOf("MiB") - 1);
            try {
                float f = Float.parseFloat(size);
                return (int) f * 1073741824;
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
            return (int) f;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * This is a ridiculously messy method that parses the approval queue
     * 
     * @param key - The API key to use
     * @return A list of QueueFiles
     */
    public static ApprovalQueue parseFiles(String key) {
        List<String> sn = new ArrayList<String>();
        List<QueueFile> qfl = new ArrayList<QueueFile>();
        int numClaimed = 0;
        int total = 0;
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
                int bytes = BukkitDevTools.sizeToBytes(size);
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
                QueueFile qf = new QueueFile(fileId, bytes, uploader, fileTitle, filePageURL, fileDirectLink, projectName, projectURL, claimed, date, size, staff);
                qfl.add(qf);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        ApprovalQueue aq = new ApprovalQueue(qfl, numClaimed, total, sn);
        return aq;
    }

}
