package com.modcrafting.mbd.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;

import com.modcrafting.mbd.Chekkit;

public class Configuration {

    private JFrame window = new JFrame();
    private File file;
    private Properties config = new Properties();
    private Properties defaults = new Properties();
    
    private Integer qRefRate = 15;
    private Boolean hideProgress = false;
    private Boolean useNimbus = false;
    private Boolean showAbout = false;
    private Boolean menteeMode = false;
    private String key = "";
    
    private Boolean openNotesOnFileOpen = false;
    
    public Configuration(){
        file = this.createConfig();
        try {
            config.load(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private File createConfig(){        
        File propF = new File(Chekkit.PATH + File.separator + "config.properties");
        if (propF.exists()) {
            try {
                defaults.load(new FileInputStream(propF));
                if(defaults.get("auto-queue-refresh-rate") == null){
                    defaults.put("auto-queue-refresh-rate", 15);
                }else{
                    qRefRate = Integer.parseInt((String) defaults.get("auto-queue-refresh-rate"));
                }
                
                if(defaults.get("enable-nimbus") == null){
                    defaults.put("enable-nimbus", false);
                }else{
                    useNimbus = Boolean.parseBoolean((String) defaults.get("enable-nimbus"));
                }
                
                if(defaults.get("about-on-close") == null){
                    defaults.put("about-on-close", false);
                }else{
                    showAbout = Boolean.parseBoolean((String) defaults.get("about-on-close"));
                }
                
                if(defaults.get("mentee-mode") == null){
                    defaults.put("mentee-mode", false);
                }else{
                    menteeMode = Boolean.parseBoolean((String) defaults.get("mentee-mode"));
                }
                
                if(defaults.get("key") == null){
                    defaults.put("key", "");
                }else{
                    key = defaults.getProperty("key");
                }
                
                if(defaults.get("escape-closes-file") == null){
                    defaults.put("escape-closes-file", "true");
                } 
                
                if(defaults.get("open-notes-on-file-open") == null){
                    defaults.put("open-notes-on-file-open", false);
                }else{
                    this.openNotesOnFileOpen = Boolean.parseBoolean((String)defaults.getProperty("open-notes-on-file-open"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                defaults.put("enable-nimbus", useNimbus.toString());
                defaults.put("about-on-close", showAbout.toString());
                defaults.put("auto-queue-refresh-rate", qRefRate.toString());
                defaults.put("key", key);
                defaults.put("mentee-mode", "false");
                defaults.put("open-notes-on-file-open", openNotesOnFileOpen.toString());
                defaults.put("escape-closes-file", "true");
                defaults.store(new FileOutputStream(propF), "The Chekkit config.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return propF;
    }
    
    public void set(String setting, Object value){
        config.put(setting, value);
        try {
            config.store(new FileOutputStream(file), "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String setting){
        String s = config.getProperty(setting);
        if(s != null && !s.equals("")){
            return s;
        }
        return "";
    }
    
    public String getString(String setting, String defaultValue){
        String s = config.getProperty(setting);
        if(s != null && !s.equals("")){
            return s;
        }
        return defaultValue;
    }
    
    public Boolean contains(String setting) {
        return config.containsKey(setting);
    }
    
    public Boolean getBoolean(String setting){
        try{
            Boolean s = Boolean.parseBoolean(config.getProperty(setting));
            if(s instanceof Boolean && s != null){
                return s;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
    
    public Boolean getBoolean(String setting, boolean defaultValue){
        try{
            Boolean s = Boolean.parseBoolean(config.getProperty(setting));
            if(s instanceof Boolean && s != null){
                return s;
            }
        }catch (Exception e){
            return defaultValue;
        }
        return defaultValue;
    }
    
    public Boolean getUseNimbus() {
        return this.useNimbus;
    }
    
    public Boolean getShowAbout() {
        return this.showAbout;
    }

    public Boolean getOpenNotesOnFileOpen() {
        return openNotesOnFileOpen;
    }
    
    public Boolean getMenteeModeEnabled() {
        return this.menteeMode;
    }
    
    public Integer getInteger(String setting){
        try{
            Integer s = Integer.parseInt(config.getProperty(setting));
            if(s instanceof Integer && s != null){
                return s;
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }
    
    public Integer getInteger(String setting, int defaultValue){
        try{
            Integer s = Integer.parseInt(config.getProperty(setting));
            if(s instanceof Integer && s != null){
                return s;
            }
        }catch (Exception e){
            return defaultValue;
        }
        return defaultValue;
    }
    
    public Double getDouble(String setting){
        try{
            Double s = Double.parseDouble(config.getProperty(setting));
            if(s instanceof Double && s != null){
                return s;
            }
        }catch(Exception e){
            return null;
        }
        return null;
    }
}
