/*  * PC与Android客户端实现文件的传送  * PC服务器端  */  package com.android.test;  import java.io.BufferedInputStream;  import java.io.DataInputStream;  import java.io.DataOutputStream;  import java.io.File;  import java.io.FileInputStream;  import java.io.IOException;  import java.net.ServerSocket;  import java.net.Socket;  public class TransferFileServer {      private static final int HOST_PORT = 8821;      private void start() {          Socket s = null;          try {              ServerSocket ss = new ServerSocket(HOST_PORT);              while (true) {                  String filePath = "/home/fan/Pictures/1.jpg";                  File file = new File(filePath);                  System.out.println("文件长度:" + (int) file.length());                  s = ss.accept();                  log("建立Socket连接");                  DataInputStream dis = new DataInputStream(                          new BufferedInputStream(s.getInputStream()));                  dis.readByte();                  DataInputStream fis = new DataInputStream(                          new BufferedInputStream(new FileInputStream(filePath)));                  DataOutputStream dos = new DataOutputStream(s.getOutputStream());                  dos.writeUTF(file.getName());                  dos.flush();                  dos.writeLong((long) file.length());                  dos.flush();                  int bufferSize = 8192;                  byte[] buf = new byte[bufferSize];                  while (true) {                      int read = 0;                      if (fis != null) {                          read = fis.read(buf);                      }                      if (read == -1) {                          break;                      }                      dos.write(buf,0,read);                  }                  dos.flush();                   // 注意关闭socket链接哦，不然客户端会等待server的数据过来，                  // 直到socket超时，导致数据不完整。                  fis.close();                  s.close();                  log("文件传输完成");              }          } catch (IOException e) {              // TODO Auto-generated catch block              e.printStackTrace();          }      }      void log(String msg) {          System.out.println(msg);      }      public static void main(String args[]) {          new TransferFileServer().start();      }  }  