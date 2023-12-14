package pw.geckonerd.CursedRB.Launcher;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MineStat {
  private String address;
  
  private final int port;
  
  private final int timeout;
  
  private boolean serverUp;
  
  private int currentPlayers;
  
  private int maximumPlayers;
  
  private long latency;
  
  public MineStat(String address, int port) {
    this(address, port, 2);
  }
  
  public MineStat(String address, int port, int timeoutInSeconds) {
    this.address = address;
    this.port = port;
    this.timeout = timeoutInSeconds;
    betaQuery();
  }
  
  public int getTimeout() {
    return this.timeout * 1000;
  }
  
  public int getCurrentPlayers() {
    return this.currentPlayers;
  }
  
  public int getMaximumPlayers() {
    return this.maximumPlayers;
  }
  
  public long getLatency() {
    return this.latency;
  }
  
  public boolean isServerUp() {
    return this.serverUp;
  }
  
  public void betaQuery() {
    try {
      this.address = java.net.InetAddress.getByName(this.address).getHostAddress();
      Socket clientSocket = new Socket();
      long startTime = System.currentTimeMillis();
      clientSocket.connect(new InetSocketAddress(this.address, this.port), getTimeout());
      this.latency = System.currentTimeMillis() - startTime;
      DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
      DataInputStream dis = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
      dos.writeInt(1);
      dos.writeBytes("þ");
      dis.readInt();
      if (dis.readUnsignedByte() != 255) {
        clientSocket.close();
        return;
      } 
      int dataLen = dis.readUnsignedShort();
      byte[] rawServerData = new byte[dataLen * 2];
      dis.read(rawServerData, 0, dataLen * 2);
      clientSocket.close();
      String[] serverData = (new String(rawServerData, StandardCharsets.UTF_16)).split("\000");
      if (serverData.length < 3)
        return; 
      this.currentPlayers = Integer.parseInt(serverData[1]);
      this.maximumPlayers = Integer.parseInt(serverData[2]);
      this.serverUp = true;
    } catch (Exception e) {
      e.printStackTrace();
      CursedRBLauncher.log("Failed to ping server!");
      this.serverUp = false;
    } 
  }
}
