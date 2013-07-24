package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import config.ConfigManager;

public class DBUtil {

	private DBUtil(){}
	
	public static Connection getConnection() {
		Connection conn = null;
		if(!ConfigManager.logdb.startsWith("jdbc:oracle")){
			LogUtil.error("only oracle supported");
			System.exit(-1);
		}
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(ConfigManager.logdb, ConfigManager.username, ConfigManager.pwd);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
    /**
     * 简单执行一个SQL语句（INSERT/UPDATE/DELETE）
     *
     * @param con
     *            数据库连接
     * @param sSQL
     *            SQL语句
     * @return 操作所影响的记录数
     */
    public static int excuteSQL(Connection con, String sSQL) {
        int iRet = -1;
        Statement stat = null;

        try {
            stat = con.createStatement();
            iRet = stat.executeUpdate(sSQL);
        } catch (Exception ex) {
        	ex.printStackTrace();
            LogUtil.error("excuteUpdate Error:" + sSQL);
        } finally {
            close(stat);
            stat = null;
        }
        return iRet;

    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param rs
     *            记录集
     * @param st
     *            预处理语句
     * @param conn
     *            数据库连接
     */
    public static void close(ResultSet rs, Statement st, Connection conn) {
        try {
            if(rs!=null)
                rs.close();
            rs = null;
        } catch (Exception e) {
        }

        try {
            if(st!=null)
                st.close();
            st = null;
        } catch (Exception e) {
        }

        try {
            if(conn!=null)
                conn.close();
            conn = null;
        } catch (Exception e) {
        }
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param rs
     *            记录集
     * @param st
     *            预处理语句
     * @param conn
     *            数据库连接
     */
    public static void close(ResultSet rs, PreparedStatement st, Connection conn) {
        try {
            if(rs!=null)
                rs.close();
            rs = null;
        } catch (Exception e) {
        }

        try {
            if(st!=null)
                st.close();
            st = null;
        } catch (Exception e) {
        }

        try {
            if(conn!=null)
                conn.close();
            conn = null;
        } catch (Exception e) {
        }
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param st
     *            预处理语句
     * @param conn
     *            数据库连接
     */
    public static void close(Statement st, Connection conn) {
        close(null, st, conn);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param conn
     *            数据库连接
     */
    public static void close(Connection conn) {
        close(null, null, conn);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param rs
     *            记录集
     * @param st
     *            预处理语句
     */
    public static void close(ResultSet rs, Statement st) {
        close(rs, st, null);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param rs
     *            记录集
     */
    public static void close(ResultSet rs) {
        close(rs, null, null);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param st
     *            预处理语句
     */
    public static void close(Statement st) {
        close(null, st, null);
    }

    /**
     * 从数据库中方便的获取一个整型值，根据指定的SQL语句 通常象select count(*) from xxx语句可以方便的得到结果
     *
     * @param conn
     *            指定的数据库连接
     * @param sql
     *            指定查询SQL语句
     * @return 查询到的结果
     * @exception SQLException
     */
    public static int getIntBySql(Connection conn, String sql)
            throws SQLException {
        int ret = 0;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                ret = rs.getInt(1);
            }
        } catch (Exception ex) {
            throw new SQLException(ex.getMessage());
        } finally {
            close(rs, pst);
            rs = null;
            pst = null;
        }
        return ret;
    }

    /**
     * 从数据库中获取一个字符串结果，根据指定的SQL语句
     *
     * @param 数据库连接
     * @param sql
     *            指定SQL语句
     * @return 字符结果
     * @exception SQLException
     */
    public static String getStringBySql(Connection conn, String sql)
            throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        String ret = null;

        try {
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                ret = rs.getString(1);
            }
        } catch (Exception ex) {
            throw new SQLException(ex.getMessage());
        } finally {
            close(rs, st);
            rs = null;
            st = null;
        }

        return ret;
    }
}
