
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;

import util.DBUtil;
import util.LogUtil;
import util.StringUtils;
import util.StringValueUtils;
import config.ConfigManager;


/**
 * @author Administrator
 *
 */
public class TransferThread implements Runnable{
	
	public static final String STATUS_WAIT = "WAIT";
	public static final String STATUS_COPY = "COPY";
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_FAIL = "FAIL";
	
	private String srcfile = null;
	private String destfile = null;
	
	private String relasrc = null;
	private Connection conn = null;
	
	public TransferThread(Connection conn,String srcfile,String destfile) {
		try{
		this.conn = conn;
		this.srcfile = srcfile;
		this.destfile = destfile;
		String srcabf = new File(srcfile).getAbsolutePath();
		
		relasrc = StringUtils.replace(srcabf, ConfigManager.getsrcfile().getAbsolutePath(), "");
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	public void run() {
		if(!StringUtils.isNull(srcfile, true) && !StringUtils.isNull(destfile, true) && !(conn==null)){
			try {
				boolean b = check();
				if(!b){
					firstStep();
					secondStep();
					thirdStep();
				}
			} catch (Exception e1) {
				LogUtil.error("execute error",e1);
				failStep();
			}
		}else{
			LogUtil.error("srcfile:"+srcfile);
			LogUtil.error("destfile:"+destfile);
			LogUtil.error("conn:"+conn);
		}
	}

	// check the 
	private boolean check() throws SQLException {
		String sql = "select count(*) from copylog where filepath = '"+relasrc+"'";
		boolean exist = StringValueUtils.getBoolean(String.valueOf(DBUtil.getIntBySql(conn, sql)));
		if(!exist){
			return false;
		}
		sql = "select status from copylog where filepath = '"+relasrc+"'";
		String status = DBUtil.getStringBySql(conn, sql);
		if(!status.equalsIgnoreCase(STATUS_SUCCESS)){
			return false;
		}
		return true;
	}
	
	//insert filepath,start ,status
	private void firstStep() throws SQLException {
		String sql = "insert into copylog(filepath,starttime,status) values(?,?,?)";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, relasrc);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, STATUS_WAIT);
			ps.executeUpdate();
		} finally{
			DBUtil.close(ps);
		}
		
	}
	//copy file,status
	private void secondStep() throws SQLException, IOException {
		String sql = "update copylog set status=? where filepath=?";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, STATUS_COPY);
			ps.setString(2, relasrc);
			ps.executeUpdate();
			DBUtil.close(ps);
		}  finally{
			DBUtil.close(ps);
		}
		FileUtils.copyFile(new File(srcfile), new File(destfile));
	}
	
	//finish endtime,status
	private void thirdStep() throws SQLException {
		String sql = "update copylog set status=?,endtime=? where filepath=?";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, STATUS_SUCCESS);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, relasrc);
			ps.executeUpdate();
		} finally{
			DBUtil.close(ps);
		}
	}
	//fail..
	private void failStep() {
		String sql = "update copylog set status=? where filepath=?";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, STATUS_FAIL);
			ps.setString(2, relasrc);
			ps.executeUpdate();
		} catch (SQLException e) {
			LogUtil.error("failStep error", e);
		}finally{
			DBUtil.close(ps);
		}
		
	}
}
