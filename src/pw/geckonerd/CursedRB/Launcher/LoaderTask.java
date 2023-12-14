package pw.geckonerd.CursedRB.Launcher;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public class LoaderTask {
	File f;
	File verFile;
	File targetDir;
	String url;
	int ver;
	
	public LoaderTask(File targetPath, String serverURL, int version, File targetDir) {
		f = targetPath;
		verFile = new File(f.getAbsolutePath() + ".version");
		url = serverURL;
		ver = version;
		this.targetDir = targetDir;
	}
	
	public boolean needsUpdate() throws Exception {
		if(!f.exists())
			return true;
		if(!verFile.exists())
			return true;
		BufferedReader reader = new BufferedReader(new FileReader(verFile));
		int verLocal = Integer.parseInt(reader.readLine());
		reader.close();
		if(verLocal < ver)
			return true;
		return false;
	}
	
	public void download(LoaderFrame frame) throws Exception {
	    if (!targetDir.exists())
	    	targetDir.mkdirs();
	    if(f.exists())
	    	f.delete();
	    f.getParentFile().mkdirs();

        URLConnection urlConnection = HttpUtil.cookConnection(url);
        urlConnection.setConnectTimeout((int)TimeUnit.SECONDS.toMillis(3L));
        int contentLengthLong = urlConnection.getContentLength();
        try(BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream()); 
            FileOutputStream fileOutputStream = new FileOutputStream(f)) {
        	byte[] data = new byte[4096];
        	int totalWrited = 0;
        	int readed;
        	while ((readed = in.read(data)) != -1) {
        		fileOutputStream.write(data, 0, readed);
        		totalWrited += readed;
        		frame.setTaskMeter2(totalWrited, contentLengthLong);
        	} 
        }
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(verFile));
        writer.write(String.valueOf(ver));
        writer.close();
	}
}
