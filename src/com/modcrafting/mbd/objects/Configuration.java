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
    private String key = "";
    
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
                
                if(defaults.get("hide-progress") == null){
                    defaults.put("hide-progress", false);
                }else{
                    hideProgress = Boolean.parseBoolean((String) defaults.get("hide-progress"));
                }
                
                if(defaults.get("key") == null){
                    defaults.put("key", "");
                }else{
                    key = defaults.getProperty("key");
                }   
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                defaults.put("enable-nimbus", useNimbus.toString());
                defaults.put("hide-progress", hideProgress.toString());
                defaults.put("about-on-close", showAbout.toString());
                defaults.put("auto-queue-refresh-rate", qRefRate.toString());
                defaults.put("key", key);
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
