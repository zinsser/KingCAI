package com.jczhou.kingcai.messageservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

//TODO:���ByteBuffer���ȳ��� socket��������С����ô��Ҫ�и�Ϊ���message����
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
    	File file = new File(path);//����һ���ļ�����
    	FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(file);
			//���ļ�
	    	byte[] buffer = new byte[1024];//����
	    	int len = 0;
	    	ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			while( (len = inStream.read(buffer))!= -1){//ֱ�������ļ�����
				outStream.write(buffer, 0, len);
			}
			byte[] data = outStream.toByteArray();//�õ��ļ��Ķ���������
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