package com.king.cai.common;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;

public class KingCAIUtils {
	public static String IPIntToString(final int ipAddr){
		return ((ipAddr) & 0xff) + "."
				+ ((ipAddr >> 8) & 0xff) + "."
				+ ((ipAddr >> 16) & 0xff) + "."
				+ ((ipAddr >> 24) & 0xff);
	}
	
	public static String ServerIPIntToString(final int ipAddr){
		return ((ipAddr) & 0xff) + "."
				+ ((ipAddr >> 8) & 0xff) + "."
				+ ((ipAddr >> 16) & 0xff) + "."
				+ 2;  //·þÎñÆ÷IPÎª xxx.xxx.xxx.2
	}
	
	public static String IPLongToString(long ipAddr){
		String ipAddress = "";
		if (ipAddr != 0){
			if (ipAddr < 0){
				ipAddr += 0x100000000L;
			}
			ipAddress = String.format("%d.%d.%d.%d", ipAddr & 0xFF, (ipAddr >> 8) & 0xFF, 
							(ipAddr >> 16) & 0xFF, (ipAddr >> 24) & 0xFF);
		}
		
		return ipAddress;
	}	
	
	public static void showLogger(Handler handler, final String log, final String tag){
		Bundle bundle = new Bundle();
		bundle.putCharSequence("DEST", tag);
		bundle.putString("MSG", log);
		Message msg = new Message();
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
		
}
