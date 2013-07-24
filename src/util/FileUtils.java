package util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;

public class FileUtils {


	public static String[] listFiles(String s, FilenameFilter myfilter) {
		if (s == null || s.trim().length() < 1)
			return null;
		ArrayList aa = new ArrayList();
		aa = listFiles(s, aa, myfilter);
		if (aa == null)
			return new String[0];
		String[] lResult = new String[aa.size()];
		aa.toArray(lResult);
		return lResult;
	}
	

	private static ArrayList listFiles(String s, ArrayList tt,
			FilenameFilter myfilter) {

		if(tt.size()>1000){
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return tt;
		}
		File tta = new File(s);
		String[] sublist = null;
		if (tta.isFile())
			tt.add(s);
		else {
			if (myfilter != null)
				sublist = tta.list(myfilter);
			else
				sublist = tta.list();
			if (sublist == null)
				return null;
			for (int i = 0; i < sublist.length; i++) {
				if (s.endsWith("/"))
					listFiles(s + sublist[i], tt, myfilter);
				else
					listFiles(s + "/" + sublist[i], tt, myfilter);
			}

		}
		return tt;
	}
	
	public static void main(String[] args) {
	}
}
