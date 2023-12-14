package pw.geckonerd.CursedRB.Launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

public class HttpUtil {
	public static final String rubetaAPI = "http://client.rubeta.net/launcher.php?b=public&v=20221116&m=";
	public static final String cursedrbAPI = "http://138.2.143.151:8951/cursedrb/api/";
	
	public static AuthResponse login(String user, String pwd) {
		AuthResponse ar = new AuthResponse();
		
		try {
			HttpURLConnection conn = cookConnection(rubetaAPI + "login");
			configurePost(conn);
			postData(conn, ("l=" + user + "&p=" + md5encode(pwd)).getBytes("utf-8"));
			String data = readData(conn);
			
			if(data.startsWith("Err: "))
			{
				ar.setErrorServer(data);
			} else if(data.matches("[a-z\\d]{32,40}")) {
				ar.setToken(data);
			} else {
				throw new RuntimeException("Данные не совпали с фильтрами! Данные: " + data);
			}
		} catch(Exception e) {
			ar.setErrorException(e);
		}
		return ar;
	}
	public static String md5encode(String pass) {
	    try {
	      MessageDigest m = MessageDigest.getInstance("MD5");
	      byte[] data = pass.getBytes();
	      m.update(data, 0, data.length);
	      BigInteger i = new BigInteger(1, m.digest());
	      return String.format("%1$032X".toLowerCase(), new Object[] { i });
	    } catch (NoSuchAlgorithmException e) {
	      CursedRBLauncher.panic(LoginFrame.instance, "Форма входа не смогла получить MD5-хэш пароля!", e);
	      return "";
	    } 
	  }
	public static AuthResponse refreshToken(String user, String token) {
		AuthResponse ar = new AuthResponse();
		try {
			HttpURLConnection conn = cookConnection(rubetaAPI + "refreshtoken");
			configurePost(conn);
			postData(conn, ("l=" + user + "&t=" + token).getBytes("utf-8"));
			String data = readData(conn);
			
			if(data.startsWith("Err: "))
			{
				ar.setErrorServer(data);
			} else if(data.matches("[a-z\\d]{32,40}")) {
				ar.setToken(data);
			} else {
				throw new RuntimeException("Данные не совпали с фильтрами! Данные: " + data);
			}
		} catch(Exception e) {
			ar.setErrorException(e);
		}
		return ar;
	}
	
	public static String getNews() {
		try {
			HttpURLConnection con = cookConnection(rubetaAPI + "news");
			// read data
			try(InputStream is = con.getInputStream(); InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(isr)) {
	              return br.lines().collect(Collectors.joining(""));
            }
		} catch(Exception e) {
			return "Не получилось получить новости :(\nПричина:\n" + PanicFrame.convertException(e);
		}
	}
	
	public static HttpURLConnection cookConnection(String urlS) throws IOException {
		URL url = new URL(urlS);
		return (HttpURLConnection)url.openConnection();
	}
	
	public static void configurePost(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setDoOutput(true);
	}
	
	public static void postData(HttpURLConnection conn, byte[] bytes) throws IOException {
		try(OutputStream os = conn.getOutputStream()) {
		    os.write(bytes, 0, bytes.length);			
		}
	}
	
	public static String readData(HttpURLConnection conn) throws UnsupportedEncodingException, IOException {
		StringBuilder sb = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
		    String responseLine = null;
		    while ((responseLine = br.readLine()) != null) {
		        sb.append(responseLine);
		    }
		}
		return sb.toString();
	}
	public static byte[] getServers() throws Exception {
		// TODO: do it the normal way
		HttpURLConnection conn = cookConnection(cursedrbAPI + "servers");
		InputStream input = conn.getInputStream();
		byte[] buffer = new byte[4096 * 2];
		int read = input.read(buffer);
		
		return java.util.Arrays.copyOf(buffer, read);
	}
}
