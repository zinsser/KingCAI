package com.king.cai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.king.cai.R;
import com.king.cai.common.ComunicableActivity;
import com.king.cai.common.KingCAIUtils;
import com.king.cai.examination.DownloadManager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ExplorerActivity extends ComunicableActivity  {
	private GridView mExplorerView = null;
	private String mRootPath;
	private Button mButtonParentPath = null;
	
	public class FileInfo{
		public String mName;
		public String mDirPath;
		public int mIconResId;
		public View.OnClickListener mClickListener;
		
		public FileInfo(String name, String path, int resId, View.OnClickListener listener){
			mName = name;
			mDirPath = path;
			mIconResId = resId;
			mClickListener = listener;
		}
	}
	private ArrayList<FileInfo> mFiles = new ArrayList<FileInfo>(); 
	private TextView mTextViewTitle = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
        setContentView(R.layout.explorerlist);

    	if (getIntent().hasExtra("RootDir")){
    		mRootPath = getIntent().getExtras().getString("RootDir");
    	}else{
    		mRootPath = KingCAIUtils.getRootPath();
    	}
    	createRootDirectory(mRootPath);
    	constructFileList(mRootPath);
    	
    	mTextViewTitle = (TextView)findViewById(R.id.textViewExplorerTitle);
    	updateTitle(mRootPath);
        mExplorerView = (GridView)findViewById(R.id.gridViewExplorer);
        ExplorerAdapter adapter = new ExplorerAdapter();
        mExplorerView.setAdapter(adapter);
        
        mButtonParentPath = (Button)findViewById(R.id.buttonReturn);
        mButtonParentPath.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String parentPath = (String)v.getTag();
				if (parentPath != null){
					constructFileList(parentPath);
					((BaseAdapter)mExplorerView.getAdapter()).notifyDataSetChanged();
					
					updateTitle(parentPath);
				}else{
					updateTitle(mRootPath);
				}
			}
		});
	}

	@Override
	protected void onServiceReady() {
		mServiceChannel.getExplorerDirectory();
	};	
	
	
	
	private File mRootDir = null;	
	private void createRootDirectory(String rootDir){
		if (mRootDir == null){
			mRootDir = new File(rootDir);
		}
		if (!mRootDir.exists()) {
			mRootDir.mkdirs();
		}
	}	
	
	private void touchFileByPath(String filePath){
		String rootPath = KingCAIUtils.getRootPath();
		if (mRootDir != null){
			rootPath = mRootDir.getPath(); 
		}
		rootPath += "/";
		String localPath = rootPath + filePath.replace('\\', '/');
		File tempFile = new File(localPath);
		if (!tempFile.exists()){
			tempFile.mkdirs();
		}
	}
	
	private boolean isFileExist(String filePath){
		int namePos = filePath.lastIndexOf("\\");
		String pathName = filePath.substring(0, namePos).replace('\\', '/');
		String fileName = filePath.substring(namePos+1, filePath.length());
		File file = new File(mRootDir.getPath()+ '/'+ pathName, fileName);
		return file.exists();
	}
	
	@Override
	protected void doHandleInnerMessage(Message innerMessage) {
		switch (innerMessage.what){
		case KingCAIConfig.EVENT_EXPLORER_FILE_READY:{
			Bundle bundle = innerMessage.getData();
			String name = bundle.getString("Name");
			String id = bundle.getString("Id");
			String size = bundle.getString("Size");
			DownloadManager.getInstance().addTask(mInnerMessageHandler, name, size, id);
			touchFileByPath(name.substring(0, name.lastIndexOf('\\')));
			break;
		}
		case KingCAIConfig.EVENT_EXPLORER_DIRECTORY_READY:{
			DownloadManager.getInstance().dispatchTask();
			break;
		}
		case KingCAIConfig.EVENT_REQUEST_FILE:{
			Bundle bundle = innerMessage.getData();
			String name = bundle.getString("Name");
			String id = bundle.getString("Id");
			String strSize = bundle.getString("Size");
			Integer size = Integer.valueOf(strSize);
			if (!isFileExist(name)){
				mServiceChannel.updateDownloadInfo(size);
				mServiceChannel.getExplorerFile(id);
			}else{
				mServiceChannel.updateDownloadInfo(0);
				DownloadManager.getInstance().finishCurrentTask();				
			}
			break;
		}
		case KingCAIConfig.EVENT_NEW_FILE:{
			Bundle bundle = innerMessage.getData();
			byte[] datas = bundle.getByteArray("Content");
			String filePath = ((DownloadFileTask)DownloadManager.getInstance().getCurrentTask()).getFileName();
			int namePos = filePath.lastIndexOf("\\");
			String pathName = filePath.substring(0, namePos).replace('\\', '/');
			String fileName = filePath.substring(namePos+1, filePath.length());
			try {
				File file = new File(mRootDir.getPath()+ '/'+ pathName, fileName);
				FileOutputStream outStream = new FileOutputStream(file);
				outStream.write(datas);
				outStream.close();
				showToast("成功接收:"+file.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			mServiceChannel.updateDownloadInfo(0);
			DownloadManager.getInstance().finishCurrentTask();
			break;
		}
		default:
			break;
		}
	}
	
	//initpath = file:       ///sdcard/kingcai
	private void constructFileList(String path){
		File f = new File(path);
		File[] files = f.listFiles();

		mFiles.clear();
		
		if (!mRootPath.equals(path)) {
			mButtonParentPath.setTag(f.getParent());			
		}

		if (files != null && files.length > 0){
			for (File file : files){
				if (getResIdByFile(file) != -1){
					mFiles.add(new FileInfo(file.getName(), file.getPath(),
								getResIdByFile(file), 
								file.isDirectory() ? new FolderClickListener(file.getPath()) 
												   : new FileClickListener(file)));
				}
			}
		}
	}
	
	public class FolderClickListener implements View.OnClickListener{
		private String mNextPath = null;
		
		public FolderClickListener(String nextDir){
			mNextPath = nextDir;
		}

		public void onClick(View v) {
			constructFileList(mNextPath);
			((BaseAdapter)mExplorerView.getAdapter()).notifyDataSetChanged();
			updateTitle(mNextPath);
		}		
	}
	
	private void updateTitle(String path){
		if (path != null){
			int lastSeperatorPos = path.lastIndexOf("/");
			if (lastSeperatorPos+1 < path.length()){
				String title = path.substring(lastSeperatorPos+1, path.length());
				mTextViewTitle.setText(title);
			}else{
				mTextViewTitle.setText(path);
			}
		}
	}
	
	public class FileClickListener implements View.OnClickListener{
		private File mFile = null;
		public FileClickListener(File file){
			mFile = file;
		}
		public void onClick(View v) {
			
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(mFile), getMIMEByFile(mFile));
			try{
				startActivity(intent);
			}catch (ActivityNotFoundException e){
				Toast.makeText(ExplorerActivity.this, "未找到读取该文件的应用程序\n  请安装后重试!", 2000).show();
			}
		}
	}
	
	public static class FileInfoWithMime{
		String mExt;
		String mMime;
		int mIconId;
		
		public FileInfoWithMime(String ext, String mime, int iconResId){
			mExt = ext;
			mMime = mime;
			mIconId = iconResId;
		}
	}
	
    private static final FileInfoWithMime[] mFileStaticInfos = {
		new FileInfoWithMime("txt", "text/plain", R.drawable.cover_txt),
		
		new FileInfoWithMime("png", "image/*", R.drawable.cover_ebk),
		new FileInfoWithMime("jpg", "image/*", R.drawable.cover_ebk),		
		new FileInfoWithMime("bmp", "image/*", R.drawable.cover_ebk),
		new FileInfoWithMime("gif", "image/*", R.drawable.cover_ebk),	
		
		new FileInfoWithMime("mp3", "audio/*", R.drawable.cover_mp3),
		new FileInfoWithMime("m4a", "audio/*", R.drawable.cover_mp3),
		new FileInfoWithMime("wav", "audio/*", R.drawable.cover_mp3),
		new FileInfoWithMime("amr", "audio/*", R.drawable.cover_mp3),
		
		new FileInfoWithMime("mp4", "video/*", R.drawable.cover_mp4),
		new FileInfoWithMime("3gp", "video/*", R.drawable.cover_mp4),
		
		new FileInfoWithMime("chm", "application/x-chm", R.drawable.cover_chm),
		new FileInfoWithMime("xls", "application/vnd.ms-excel", R.drawable.cover_xls),
		new FileInfoWithMime("xlsx", "application/vnd.ms-excel", R.drawable.cover_xls),		
		new FileInfoWithMime("doc", "application/msword", R.drawable.cover_doc),
		new FileInfoWithMime("docx", "application/msword", R.drawable.cover_doc),
		new FileInfoWithMime("ppt", "application/vnd.ms-powerpoint", R.drawable.cover_ppt),
		new FileInfoWithMime("pptx", "application/vnd.ms-powerpoint", R.drawable.cover_ppt),
		new FileInfoWithMime("html", "text/html", R.drawable.cover_html),
		new FileInfoWithMime("mht", "text/html", R.drawable.cover_html),
		new FileInfoWithMime("htm", "text/html", R.drawable.cover_html),
		new FileInfoWithMime("pdf", "application/pdf", R.drawable.cover_pdf)
	}; 
	
    private FileInfoWithMime getFileInfoWithMime(File file){
		String fileName = file.getName();
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1, 
											fileName.length()).toLowerCase();
		FileInfoWithMime retInfo = null;
		for (FileInfoWithMime info : mFileStaticInfos){
			if (info.mExt.equals(ext)){
				retInfo = info; 
				break;
			}
		}
		
		return retInfo;
    }
    
	private String getMIMEByFile(File file){
		String mime = "*";
		if (file.isFile()){
			mime = "*/*";
			FileInfoWithMime info =  getFileInfoWithMime(file);
			if (info != null){
				mime = info.mMime;
			}
		}
		
		return mime;
	}
	
	private int getResIdByFile(File file){
		int resId = R.drawable.cartoon_folder;
		
		if (file.isFile()){
			resId = -1;  ///-1 表示不支持的文件类型 
			FileInfoWithMime info = getFileInfoWithMime(file);
			if (info != null){
				resId = info.mIconId;
			}
		}

		return resId;
	}
	
    public class ExplorerAdapter extends BaseAdapter{    	
		public int getCount() {
			return mFiles.size();
		}

		public Object getItem(int index) {
			return mFiles.get(index);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = null;
			if (convertView == null){
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.exploreritem, null);				
				view = (TextView) convertView.findViewById(R.id.textViewItem);
				convertView.setTag(view);
			}else{
				view = (TextView)convertView.getTag();				
			}
			FileInfo info = mFiles.get(position);
			if (info != null){
				view.setText(info.mName);
				view.setOnClickListener(info.mClickListener);
				view.setBackgroundResource(info.mIconResId);
			}else{
				view.setBackgroundResource(R.drawable.default_book_cover);
				view.setClickable(false);
				view.setVisibility(View.INVISIBLE);
			}			
			return convertView;
		}
    }
}
