package com.jczhou.kingcai;


import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jczhou.kingcai.common.ComunicableActivity;
import com.jczhou.kingcai.common.TransDialog;
import com.jczhou.kingcai.examination.Answer;
import com.jczhou.kingcai.examination.PaperActivity;
import com.jczhou.kingcai.examination.PaperDBHelper;
import com.jczhou.platform.KingCAIConfig;
import com.jczhou.kingcai.ServerInfo;
import com.jczhou.kingcai.SocketService;
import com.jczhou.kingcai.WifiStateManager;

import com.jczhou.kingcai.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity  extends ComunicableActivity  {
	private final static int GROUP_NORMAL = 0;
	private final static int MENU_MODIFY_PASSWORD = 1;
	
	private final static int DIALOG_MODIFY_PASSWORD = 1;
	
	private final static int EVENT_QUERY_TIME_OUT = 1;
	private final static int DELAY_QUERY_TIME = 5000;
	
	private final static int RESULT_LOAD_HEADER_PHOTO= 1;
	
	private EditText mTxtStudentID = null;
	private EditText mTxtPassword = null;
	private Button mBtnLogin = null;
    private CheckBox mCheckBoxOffline = null;	
    private ImageView mImgViewHeader = null;
	private String mServerIP;
	private Spinner mSpinnerSSID = null;
	private WifiStateManager mWifiStateMgr = null;
	private TextView mTextViewBaseInfo = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        mBtnLogin = (Button)findViewById(R.id.btnLogin);
        mBtnLogin.setOnClickListener(new BtnClickListener());  
         
        mCheckBoxOffline = (CheckBox)findViewById(R.id.checkBoxOffline);
        mImgViewHeader = (ImageView)findViewById(R.id.imageViewHeaderPhoto);
        mImgViewHeader.setOnClickListener(new BtnClickListener());
        
        mTxtStudentID = (EditText)findViewById(R.id.txtStudentID);
        mTxtPassword = (EditText)findViewById(R.id.txtPassword);
        
        mWifiStateMgr = new WifiStateManager(this, new WifiStateEventListener());
        
        initSpinnerBar();
        findViewById(R.id.imageViewSpinnerDown).setOnClickListener(new View.OnClickListener(){

			public void onClick(View v) {
				if (mSpinnerSSID != null){
					mSpinnerSSID.performClick();
				}
			}
        	
        });
        mTextViewBaseInfo = (TextView)findViewById(R.id.textViewBaseInfo);
        
        cleanForm();
    }

    @Override
    public void onStart(){
    	super.onStart();
    	mWifiStateMgr.Register(getApplication());
        mWifiStateMgr.StartScanServer();
    }
    
    @Override
    public void onStop(){
    	mWifiStateMgr.UnRegister(getApplication());
    	super.onStop();    	
    }

    public void SaveStudent(String strID, String name, String password, Bitmap photo){
		StudentDBHelper helper = new StudentDBHelper(getApplicationContext());
		ContentValues values = new ContentValues();		

		Integer id = Integer.parseInt(strID);
		values.put(StudentDBHelper.s_StudentTag_ID, id);  
		values.put(StudentDBHelper.s_StudentTag_Name, name);  
		values.put(StudentDBHelper.s_StudentTag_Passwd, password); 
		values.put(StudentDBHelper.s_StudentTag_Info, "");
		
		 // 将Bitmap压缩成PNG编码，质量为100%存储            
		final ByteArrayOutputStream os = new ByteArrayOutputStream(); 			
        photo.compress(Bitmap.CompressFormat.PNG, 100, os);     			
		values.put(StudentDBHelper.s_StudentTag_Photo, os.toByteArray());
				
		
		helper.Insert(values, id);

		helper.close();
	}

    private void initSpinnerBar(){
        mSpinnerSSID = (Spinner) findViewById(R.id.spinnerSSID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ReadSSIDFromConfig(adapter);
        mSpinnerSSID.setOnItemSelectedListener(new SpinnerItemClickListener());
        mSpinnerSSID.setAdapter(adapter);
        mSpinnerSSID.setEnabled(false);
    }
    
    private void ReadSSIDFromConfig(ArrayAdapter<String> adapter){
    	adapter.add("请选择班级");
    	adapter.add("一年级");
    	adapter.add("二年级");
    	adapter.add("五年级");
    	adapter.add("初三（1）班");
    }
    
    public class SpinnerItemClickListener implements AdapterView.OnItemSelectedListener{


		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String tag = (String)arg0.getAdapter().getItem(arg2);
			if (arg2 != 0){
		        mWifiStateMgr.ConnectToWifi(tag);
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
    }
    
    public class WifiStateEventListener implements WifiStateManager.WifiStateListener{


		public void onScanResultChanged(ArrayList<ServerInfo> serverInfos) {
	        mSpinnerSSID.setEnabled(true);
		}


		public void onServerInfoChanged(String wifiInfo) {
			// TODO Auto-generated method stub
		}
    }

    public Handler mInnerHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case EVENT_QUERY_TIME_OUT:
    			Toast.makeText(LoginActivity.this, R.string.ServerNotFound, 2000).show();
    			break;
    		}
    	}
    };
    
    public class BtnClickListener implements OnClickListener{

	    public void onClick(View btn){
	    	if (btn == mBtnLogin){
				InputMethodManager im =(InputMethodManager)btn.getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
				im.hideSoftInputFromWindow(mTxtStudentID.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
				im.hideSoftInputFromWindow(mTxtPassword.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				if (!mCheckBoxOffline.isChecked()){
					if (mSpinnerSSID.getSelectedItemPosition() != 0){
						SocketService.getInstance().QueryServer();
						Message msg = mInnerHandler.obtainMessage(EVENT_QUERY_TIME_OUT);
						mInnerHandler.sendMessageDelayed(msg, DELAY_QUERY_TIME);
					}else{
						mSpinnerSSID.performClick();
		    			Toast.makeText(LoginActivity.this, R.string.SelectClassTip, 2000).show();					
					}
				}else{
				    StartPaperActivity("初三一班  张三丰", mCheckBoxOffline.isChecked());					
				}
	    	}else if (btn == mImgViewHeader){
	    		Intent intent = new Intent();  
 	            intent.setType("image/*");  
	            intent.setAction(Intent.ACTION_GET_CONTENT);  
	            startActivityForResult(intent, RESULT_LOAD_HEADER_PHOTO);      		
	    	}
	    }
    };

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == RESULT_LOAD_HEADER_PHOTO 
	    		&& resultCode == RESULT_OK && null != data) {
	    	Uri selectedImage = data.getData();
	        String[] filePathColumn = { MediaStore.Images.Media.DATA };
	        Cursor cursor = getContentResolver().query(selectedImage,
	        		filePathColumn, null, null, null);
	        cursor.moveToFirst();
	        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	        String picturePath = cursor.getString(columnIndex);
	        cursor.close();

	        mImgViewHeader.setImageBitmap(BitmapFactory.decodeFile(picturePath));
	    }
	}
    
    private void cleanForm(){
    	mTxtStudentID.setText("");
    	mTxtPassword.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(GROUP_NORMAL, MENU_MODIFY_PASSWORD, 0, R.string.ModifyPassword);

    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    	case MENU_MODIFY_PASSWORD:
 //   		showDialog(DIALOG_MODIFY_PASSWORD);
 //   		LayoutInflater inflater = LayoutInflater.from(getApplication());
 //   		View modifyView = inflater.inflate(, null);
    	//    			.setTitle(R.string.ModifyPassword)	
 //   		AlertDialog.Builder builder = new AlertDialog.Builder(this)
  //  			.setPositiveButton(android.R.string.ok, null)
 //   			.setNegativeButton(android.R.string.cancel, null)
 //   			.setView(modifyView);
    		TransDialog dlg = new TransDialog(this, R.xml.style);
    		dlg.setContentView(R.layout.modifypassword);
    		dlg.show();
    		
    		break;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }    

    @Override
    protected Dialog onCreateDialog(int id){
    	if (id == DIALOG_MODIFY_PASSWORD){
    		LayoutInflater inflater = LayoutInflater.from(getApplication());
    		View modifyView = inflater.inflate(R.layout.modifypassword, null);
    	//    			.setTitle(R.string.ModifyPassword)	
    		AlertDialog.Builder builder = new AlertDialog.Builder(this)
    			.setPositiveButton(android.R.string.ok, null)
    			.setNegativeButton(android.R.string.cancel, null)
    			.setView(modifyView);
    		return builder.create();
    	}
    	return super.onCreateDialog(id);
    }
    
    public void StartPaperActivity(String studentInfo, boolean bOffline){
		Intent openSheetActivity = new Intent(LoginActivity.this, PaperActivity.class);
		openSheetActivity.putExtra(KingCAIConfig.StudentInfo, studentInfo);
		openSheetActivity.putExtra(KingCAIConfig.ServerIP, mServerIP);
		openSheetActivity.putExtra(KingCAIConfig.SSID, "一年级");
		openSheetActivity.putExtra(KingCAIConfig.Offline, bOffline);
		startActivity(openSheetActivity);
		finish();
    }
    
	@Override    
    protected EventProcessListener doGetEventProcessListener(){
    	return new LoginEventProcessListener();
    }
    
	public class LoginEventProcessListener implements ComunicableActivity.EventProcessListener {

		public void  onTalkingFinished(final String serverip){
			mInnerHandler.removeMessages(EVENT_QUERY_TIME_OUT);
			mServerIP = serverip;
			
			SocketService.getInstance().ConnectServer(mTxtStudentID.getText().toString(), 
										mTxtPassword.getText().toString());
		}


		public void onLoginSuccess(String studentinfo) {
			StartPaperActivity(studentinfo, mCheckBoxOffline.isChecked());
		}


		public void onLoginFail() {
			Toast.makeText(LoginActivity.this, R.string.InputCorrectIDTip, 2000).show();			
			cleanForm();
		}


		public void onPaperTitleReceived(String title) {
			Log.d("PaperActivity", title);			
		}


		public void onNewQuestion(String answer, int type, String content) {
			Log.d("PaperActivity", content);
		}


		public void onCleanPaper() {
			finish();
		}


		public void onNewImage(Integer id, ByteBuffer buf) {
			Log.d("PaperActivity", "new image");			
		}
	}    
}
