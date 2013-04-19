package com.modcrafting.mbd.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.modcrafting.mbd.Chekkit;
//import com.modcrafting.mbd.MasterPluginDatabase;

public class SQL {
	//MasterPluginDatabase plugin;
	Chekkit plugin;
	Connection conn;
//	public SQL(MasterPluginDatabase masterPluginDatabase) {
//		this.plugin = masterPluginDatabase;
//	}
	public SQL(Chekkit masterPluginDatabase) {
		this.plugin = masterPluginDatabase;
	}
	public void connect(){
		conn = plugin.getConnection();
	}
	
	public void disconnect(){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setAddress(String packag, String clazz, String hash) {
		try {
			if(conn == null || conn.isClosed()){
				connect();
			}
			PreparedStatement ps = conn.prepareStatement("REPLACE INTO db_masterdbo (package,class,hash_contents) VALUES(?,?,?)");
			ps.setString(1, packag);
			ps.setString(2, clazz);
			ps.setString(3, hash);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public List<String> getHash(String packag, String clazz) {
		List<String> hash = new ArrayList<String>();
		try {
			if(conn == null || conn.isClosed()){
				connect();
			}
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM db_masterdbo WHERE package = ? AND class = ?");
			ps.setString(1, packag);
			ps.setString(2, clazz);
			ResultSet rs = ps.executeQuery();
			while (rs.next()){
				hash.add(rs.getString("hash_contents"));
			}
			ps.close();
			rs.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return hash;
	}

}
