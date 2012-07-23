package com.king.cai.messageservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

//TODO:如果ByteBuffer长度长于 socket缓冲区大小，那么需要切割为多个message发送
public class ImageRequestMessage extends RequestMessage{
	private final static String s_MsgTag = "[ImageRequest]";
	private Integer mQuestionID;
	ByteBuffer mRawData = null;
	public ImageRequestMessage(Integer id, String path){
		super(s_MsgTag);
		mQuestionID = id;
		LoadImage2Buffer(path);
	}
	
	private void LoadImage2Buffer(String path){
    	File file = new File(path);//创建一个文件对象
    	FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(file);
			//读文件
	    	byte[] buffer = new byte[1024];//缓存
	    	int len = 0;
	    	ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			while( (len = inStream.read(buffer))!= -1){//直到读到文件结束
				outStream.write(buffer, 0, len);
			}
			byte[] data = outStream.toByteArray();//得到文件的二进制数据
			mRawData = ByteBuffer.allocate(data.length);
			mRawData.put(data);
			
			outStream.close();
			inStream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String Pack(){
		return "[id]" + mQuestionID + "[length]" + mRawData.array().length
				+ "[data]" + mRawData.toString();
	}
}