/*
 *  Copyright 2013 Lolmewn <info@lolmewn.nl>.
 */

package com.modcrafting.mbd.objects;

/**
 *
 * @author Lolmewn <info@lolmewn.nl>
 */
public class UpdateHolder {

    private String pack, clas, hash;
    
    public UpdateHolder(String packag, String clas, String hash) {
        this.pack = packag;
        this.clas = clas;
        this.hash = hash;
    }

    public String getClas() {
        return clas;
    }

    public String getHash() {
        return hash;
    }

    public String getPack() {
        return pack;
    }

}
