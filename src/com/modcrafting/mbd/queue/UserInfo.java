package com.modcrafting.mbd.queue;

public class UserInfo {
    
    private KeyState ks;
    private String username;

    public UserInfo(String DBOName, KeyState ks) {
        this.username = DBOName;
        this.ks = ks;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public KeyState getKeyState() {
        return this.ks;
    }

}
