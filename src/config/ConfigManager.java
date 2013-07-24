package config;

import java.io.File;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import util.StringValueUtils;

public class ConfigManager {

	private static Log log = LogFactory.getLog(ConfigManager.class);
	public static String logdir = "";
	public static String dest = "";
	public static String src = "";
	public static String logdb = "";
	public static String username = "";
	public static String pwd = "";
	public static int threadNum = 5;
	public static int interval = 5;
	
	private static File srcfile = null;
	private static File destfile = null;
//	private static String[] configname = new String[]{
//		"logdir","dest","src","logdb","username","pwd"
//	};

	private enum configname{logdir,dest,src,logdb,username,pwd,threadNum,interval}
	
	static{
        ResourceBundle prop = null;
        try {
            prop = ResourceBundle.getBundle("config");
            Enumeration e = prop.getKeys();
            while(e.hasMoreElements()){
            	e.nextElement();
            }
            logdir = prop.getString("logdir");
            dest = prop.getString("dest");
            src = prop.getString("src");
            logdb = prop.getString("logdb");
            username = prop.getString("username");
            pwd = prop.getString("pwd");
            threadNum = StringValueUtils.getInt(prop.getString("threadNum"),5);
            interval = StringValueUtils.getInt(prop.getString("interval"),5);
            
            srcfile = new File(src);
            destfile = new File(dest);
            File logfile = new File(logdir);
            if(!srcfile.exists()){
            	srcfile.mkdirs();
            }
            if(!destfile.exists()){
            	destfile.mkdirs();
            }
            if(!logfile.exists()){
            	logfile.mkdirs();
            }
        }
        catch (Exception f) {
            log.fatal("loadConfig 异常 ",f);
            System.exit( -2);
        }
	}
	
	public static String getConfig(String config) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException{
		if(ConfigManager.class.getField(config.toLowerCase()) == null){
			return null;
		}
		return ConfigManager.class.getField(config.toLowerCase()).get(null).toString();
		
	}
	
	public static File getsrcfile(){
		return srcfile;
	}
	
	public static File getdestfile(){
		return destfile;
	}
	/**
	 * @param args
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		System.out.println(getConfig(configname.username.toString()));
		System.out.println(getConfig("dest"));
		System.out.println(getConfig("LOGDB"));
		System.out.println(getConfig("pwD"));
	}

}
