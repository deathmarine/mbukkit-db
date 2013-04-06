package com.modcrafting.mbd.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.modcrafting.mbd.MasterPluginDatabase;

public class SQL {
	MasterPluginDatabase plugin;
	public SQL(MasterPluginDatabase masterPluginDatabase) {
		this.plugin = masterPluginDatabase;
	}
	
	public void setAddress(String packag, String clazz, String hash){
		try {
			Connection conn = plugin.getConnection();
			PreparedStatement ps = conn.prepareStatement("REPLACE INTO db_masterdbo (package,class,hash_contents) VALUES(?,?,?)");
			ps.setString(1, packag);
			ps.setString(2, clazz);
			ps.setString(3, hash);
			ps.executeUpdate();
			ps.close();
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public List<String> getHash(String packag, String clazz) {
		List<String> hash = new ArrayList<String>();
		try {
			Connection conn = plugin.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM db_masterdbo WHERE package = ? AND class = ?");
			ps.setString(1, packag);
			ps.setString(2, clazz);
			ResultSet rs = ps.executeQuery();
			while (rs.next()){
				hash.add(rs.getString("hash_contents"));
			}
			ps.close();
			rs.close();
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return hash;
	}

}
