package com.modcrafting.mbd.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.modcrafting.mbd.Chekkit;
import com.modcrafting.mbd.objects.UpdateHolder;

public class SQL {
	//private Chekkit plugin;
	private Connection conn;
	private Properties props;
	private PreparedStatement ps = null;
	private List<UpdateHolder> updates = new ArrayList<UpdateHolder>();
	
	private String url = "jdbc:mysql://server.modcrafting.com:3306/dbo_master";
	public SQL(Chekkit masterPluginDatabase, Properties p) {
		//this.plugin = masterPluginDatabase;
		this.props = p;
	}
	
	public void connect(){
		conn = this.getConnection();
	}
	
	public void setConnection(Connection connection) {
        this.conn = connection;
    }
	
    public void shutdown() {
        PreparedStatement statement;
        try {
            if (conn == null || conn.isClosed()) {
                this.connect();
            }
            conn.setAutoCommit(false);
            statement = conn.prepareStatement("REPLACE INTO db_masterdbo (package,class,hash_contents) VALUES(?,?,?)");
            for(UpdateHolder s : this.updates){
                statement.setObject(1, s.getPack());
                statement.setObject(2, s.getClas());
                statement.setObject(3, s.getHash());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public void shutdown(List<String> list){
		for(String s : list){
			this.ps = null;
			try {
				if(conn == null || conn.isClosed()){
					this.connect();
				}
				this.ps = conn.prepareStatement(s);
				this.ps.executeUpdate();
				this.ps = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Connection getConnection() {
        try {
            if (conn != null && !conn.isClosed())
                return conn;
            setConnection(DriverManager.getConnection(this.url, this.props));
            return conn;
        } catch (SQLException ex) {
            try {
                String message = ex.getCause().getMessage();
                if (message.contains("is not allowed to connect to this MySQL server")) {
                    Chekkit.log.severe("Unable to connection to database: Please check your Username and Password.");
                } else {
                    Chekkit.log.severe("Unable to connect to the site.");
                    ex.printStackTrace();
                }
                System.exit(0);
            } catch (Exception ex2) {}
        }
        return null;
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
		this.updates.add(new UpdateHolder(packag, clazz, hash));
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
			if(!ps.isClosed() && ps != null){
				ps.close();
			}
			if(!rs.isClosed() && rs != null){
				rs.close();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return hash;
	}

}
