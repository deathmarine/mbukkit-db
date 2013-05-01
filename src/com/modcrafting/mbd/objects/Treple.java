package com.modcrafting.mbd.objects;

public class Treple{
	String username;
	String note;
	Long time;
	public Treple(String username, String note, Long time){
		this.username = username;
		this.note = note;
		this.time = time;
	}
	public String getUsername(){
		return username;
	}
	public String getNote(){
		return note;
	}
	public long getTime(){
		return time;
	}
}
