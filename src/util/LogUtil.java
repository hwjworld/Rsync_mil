/**
 * 
 */
package util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import config.ConfigManager;

/**
 * @author Administrator
 *
 */
public class LogUtil {
	
	public static final Logger log = getLogger(LogUtil.class);
	
	static public void info(String msg)
	{
		log.info(msg);
	}
	
	static public void error(String msg)
	{
		log.error(msg);
	}
	
	static public void error(String msg,Throwable ex)
	{
		log.error(msg,ex);
	}

	private static Logger getLogger(Class c) {
		Logger log = Logger.getLogger(c);
		/* For log initalization */
		PatternLayout layout = new PatternLayout();
		RollingFileAppender appender = null;
		try {
			layout.setConversionPattern("%5p [%d{yyyy-MM-dd HH:mm:ss}] (%F:%M:%L) - %m%n");
			appender = new RollingFileAppender(layout, ConfigManager.logdir+"/Rsync_mil.log");

		} catch (Exception e) {
		}
		log.removeAllAppenders();
		log.addAppender(appender);
		log.setLevel((Level) Level.INFO);
		return log;
	}
	
	public static void main(String[] args) {
		log.info("q14r3");
	}
}
