package com.king.cai;

import java.io.File;
import java.util.ArrayList;

import com.king.cai.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ExplorerActivity extends Activity {
	private GridView mExplorerView = null;
	private String mRootDir;
	
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
        
    	Bundle extra = getIntent().getExtras();
    	if (extra.containsKey("RootDir")){
    		mRootDir = extra.getString("RootDir");
    	}else{
    		mRootDir = Environment.getExternalStorageDirectory().getPath();
    	}
    	constructFileList(mRootDir);
    	mTextViewTitle = (TextView)findViewById(R.id.textViewExplorerTitle);
    	mTextViewTitle.setText(mRootDir);
        mExplorerView = (GridView)findViewById(R.id.gridViewExplorer);
        ExplorerAdapter adapter = new ExplorerAdapter();
        mExplorerView.setAdapter(adapter);
        
        findViewById(R.id.buttonReturn).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	//initpath = file:///sdcard/kingcai
	private void constructFileList(String path){
		File f = new File(path);
		File[] files = f.listFiles();

		mFiles.clear();
		
		if (!mRootDir.equals(path)) {
			mFiles.add(new FileInfo("..", f.getPath(), 
						R.drawable.cartoon_folder, new FolderClickListener(f.getParent())));
		}
		
		if (files != null && files.length > 0){
			for (File file : files){
				mFiles.add(new FileInfo(file.getName(), file.getPath(),
							getResIdByFile(file), 
							file.isDirectory() ? new FolderClickListener(file.getPath()) 
											   : new FileClickListener(file)));
			}
		}
	}
	
	public class FolderClickListener implements View.OnClickListener{
		private String mNextDirectory = null;
		
		public FolderClickListener(String nextDir){
			mNextDirectory = nextDir;
		}
		public void onClick(View v) {
			constructFileList(mNextDirectory);
			((BaseAdapter)mExplorerView.getAdapter()).notifyDataSetChanged();
			mTextViewTitle.setText(mNextDirectory);
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
		
		new FileInfoWithMime("mp3", "audio/*", R.drawable.cover_ebk),
		new FileInfoWithMime("m4a", "audio/*", R.drawable.cover_ebk),
		new FileInfoWithMime("wav", "audio/*", R.drawable.cover_ebk),
		new FileInfoWithMime("amr", "audio/*", R.drawable.cover_ebk),
		
		new FileInfoWithMime("mp4", "video/*", R.drawable.cover_ebk),
		new FileInfoWithMime("3gp", "video/*", R.drawable.cover_ebk),
		
		new FileInfoWithMime("chm", "application/x-chm", R.drawable.cover_chm),
		new FileInfoWithMime("xls", "application/vnd.ms-excel", R.drawable.cover_chm),
		new FileInfoWithMime("docx", "application/msword", R.drawable.cover_chm),
		new FileInfoWithMime("pptx", "application/vnd.ms-powerpoint", R.drawable.cover_chm),
		new FileInfoWithMime("doc", "application/msword", R.drawable.cover_chm),
		new FileInfoWithMime("ppt", "application/vnd.ms-powerpoint", R.drawable.cover_chm),
		new FileInfoWithMime("html", "text/html", R.drawable.cover_html),
		new FileInfoWithMime("mht", "text/html", R.drawable.cover_html),
		new FileInfoWithMime("htm", "text/html", R.drawable.cover_html),
		new FileInfoWithMime("pdf", "application/pdf", R.drawable.cover_pdb)
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
			resId = R.drawable.default_book_cover;
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
    };
}
