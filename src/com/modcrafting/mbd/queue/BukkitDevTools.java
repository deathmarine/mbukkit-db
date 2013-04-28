package com.modcrafting.mbd.queue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.modcrafting.mbd.Chekkit;

public class BukkitDevTools {

    public BukkitDevTools() {

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
    
    public static int sizeToBytes(String size) {
        return 0;
        
    }

    /**
     * This is a ridiculously messy method that parses the approval queue
     * @param key - The API key to use
     * @return A list of QueueFiles
     */
    public static List<QueueFile> parseFiles(String key) {
        List<QueueFile> qfl = new ArrayList<QueueFile>(); 
        
        try {
            Connection c =  Jsoup.connect("http://dev.bukkit.org/admin/approval-queue/?api-key=" + key);
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
            for (Element file: e) {
                Elements infoBlocks = file.getElementsByTag("td");
                if (infoBlocks.size() != 7) {
                    throw new Exception("Wrong number of info blocks.");
                }
                // *cringe*
                //int fileId = 0;
                int fileId = Integer.parseInt(infoBlocks.get(0).child(0).getAllElements().get(0).attr("value"));
                //Chekkit.log.info(infoBlocks.get(0).child(0).toString());
                String projectName = infoBlocks.get(1).text();
                String projectURL = infoBlocks.get(1).child(0).attr("href");
                String fileTitle = infoBlocks.get(2).child(0).text();
                String filePageURL = "http://dev.bukkit.org" + infoBlocks.get(2).child(0).attr("href");
                String fileDirectLink = infoBlocks.get(3).child(0).attr("href");
                String size = infoBlocks.get(4).text().trim();
                int bytes = BukkitDevTools.sizeToBytes(size);
                String uploader = infoBlocks.get(5).text().trim();
                long date = Long.parseLong(infoBlocks.get(6).child(0).attr("data-epoch"));
                String claimed = infoBlocks.get(3).text();
                if (!claimed.contains("(Under Review")) {
                    claimed = null;
                } else {
                    claimed = claimed.substring(claimed.indexOf("(Under Review by ") + 17).trim();
                    claimed = claimed.substring(0, claimed.length() - 1);
                }
                
                QueueFile qf = new QueueFile(fileId, bytes, uploader, fileTitle, filePageURL, fileDirectLink, projectName, projectURL, claimed, date);
                qfl.add(qf);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return qfl;
    }

}
