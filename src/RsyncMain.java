import java.io.File;
import java.sql.Connection;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import util.DBUtil;
import util.LogUtil;
import util.StringUtils;
import config.ConfigManager;


public class RsyncMain {

    public static BlockingQueue<Runnable> workQueue;
    public static  ThreadPoolExecutor tpe = null; 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		init();
		TransferThread  t = null;
		File src = ConfigManager.getsrcfile();
		Connection con = null;
		try{
			con = DBUtil.getConnection();
			while(true){
				try{
				if(con.isClosed()){
					DBUtil.close(con);
					con = DBUtil.getConnection();
				}
				Collection c = FileUtils.listFiles(src, TrueFileFilter.INSTANCE,TrueFileFilter.INSTANCE);
				for(Object i :c){
					File file = (File)i;
					String destfile = ConfigManager.dest + "/" + StringUtils.replace(file.getAbsolutePath(), ConfigManager.getsrcfile().getAbsolutePath(), "");
					t = new TransferThread(con,file.getAbsolutePath(),destfile);
					tpe.execute(t);
				}
				}catch (RejectedExecutionException e) {
					LogUtil.error(e.getLocalizedMessage(),e);
				}
				pause();
				
			}
		}catch (Exception e) {
			LogUtil.error("main error",e);
		}finally{
			tpe.shutdown();
			if(con != null){
				DBUtil.close(con);
			}
		}
	}

	private static void pause() {
		try {
			Thread.sleep(6*1000);
		} catch (InterruptedException e) {
			LogUtil.error("pause error",e);
		}

	}
	private static void init() {
		int corePoolSize=ConfigManager.threadNum;
        int maximumPoolSize=ConfigManager.threadNum+1;
        long keepAliveTime = 1000;
        TimeUnit unit = TimeUnit.MINUTES;
        workQueue = new ArrayBlockingQueue<Runnable>(100);
        tpe = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue,new ThreadPoolExecutor.DiscardPolicy());
	}
	
	
}
