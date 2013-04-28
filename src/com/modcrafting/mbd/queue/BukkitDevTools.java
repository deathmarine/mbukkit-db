package com.modcrafting.mbd.queue;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

}
