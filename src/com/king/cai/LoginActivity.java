package com.king.cai;


import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;


import com.king.cai.R;
import com.king.cai.ServerInfo;
import com.king.cai.WifiStateManager;
import com.king.cai.common.ComunicableActivity;
import com.king.cai.examination.PaperActivity;
import com.king.cai.platform.KingCAIConfig;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


public class LoginActivity  extends ComunicableActivity  {
	private final static int GROUP_NORMAL = 0;
	private final static int MENU_MODIFY_PASSWORD = 1;
	private final static int MENU_SETTING = 2;
	private final static int MENU_WIFI_SETTING = 3;	
	
	private final static int DIALOG_MODIFY_PASSWORD = 1;
	
	private final static int EVENT_QUERY_TIME_OUT = 1;
	private final static int DELAY_QUERY_TIME = 5000;
	
	private final static int RESULT_LOAD_HEADER_PHOTO= 1;
	
	private final static String s_ConfigFileName = "kingcai_last_login";
	private final static String s_Tag_Number = "last_number";
	private final static String s_Tag_Grade = "last_grade";
	
	private EditText mTxtStudentID = null;
	private EditText mTxtPassword = null;
	private Button mBtnLogin = null;
    private CheckBox mCheckBoxOffline = null;	
    private ImageView mImgViewHeader = null;
	private String mServerIP;
	private String mSSID = "一年级";
	private String mID;
	private Spinner mSpinnerSSID = null;
	private WifiStateManager mWifiStateMgr = null;
	private TextView mTextViewBaseInfo = null;
	private TextView mTextViewStatus = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        BtnClickListener btnListener = new BtnClickListener();
        mBtnLogin = (Button)findViewById(R.id.btnLogin);
        mBtnLogin.setOnClickListener(btnListener);  
         
        mCheckBoxOffline = (CheckBox)findViewById(R.id.checkBoxOffline);
        mImgViewHeader = (ImageView)findViewById(R.id.imageViewHeaderPhoto);
        mImgViewHeader.setOnClickListener(btnListener);
        
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
        mTextViewBaseInfo.setText(R.string.NoInfo);
        
        mTextViewStatus = (TextView)findViewById(R.id.textViewStatus);
        
        cleanForm();
    }

    @Override
    public void onStart(){
    	super.onStart();
    	ReadLastLogin();
    	mWifiStateMgr.Register(getApplication());
    	mTextViewStatus.setText(R.string.FindSSIDStatus);
        mWifiStateMgr.StartScanServer();
    }
    
    @Override
    public void onStop(){
    	mWifiStateMgr.UnRegister(getApplication());
    	SaveLastLogin();
    	super.onStop();    	
    }
    

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_HOME:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    
	private void ReadLastLogin(){
		String ssid;
		SharedPreferences sp = getSharedPreferences(s_ConfigFileName, 0);
		mID = sp.getString(s_Tag_Number, "");
		ssid = sp.getString(s_Tag_Grade, "");
		if (ssid.length() == 0){
			mSpinnerSSID.setSelection(0);
		}else{
			for (int i = 1; i < mSpinnerSSID.getAdapter().getCount(); ++i){
				if (mSpinnerSSID.getAdapter().getItem(i).equals(ssid)){
					mSpinnerSSID.setSelection(i);
					break;
				}
			}
		}
		mTxtStudentID.setText(mID);
	}
	
    private void SaveLastLogin(){
		//写入
		SharedPreferences sp = getSharedPreferences(s_ConfigFileName, 0);
		SharedPreferences.Editor editor = sp.edit();

		editor.putString(s_Tag_Number, mTxtStudentID.getText().toString());
		editor.putString(s_Tag_Grade, mSSID);
		editor.commit();    	
    }
    
    private void SaveStudentInfo(String strID, String name, String password, String photo){
		StudentDBHelper helper = new StudentDBHelper(getApplicationContext());
		ContentValues values = new ContentValues();		

		Integer id = Integer.parseInt(strID);
		values.put(StudentDBHelper.s_StudentTag_ID, id);  
		values.put(StudentDBHelper.s_StudentTag_Name, name);  
		values.put(StudentDBHelper.s_StudentTag_Passwd, password); 
		values.put(StudentDBHelper.s_StudentTag_Info, "");
		
        // 将Bitmap压缩成PNG编码，质量为100%存储            
		//final ByteArrayOutputStream os = new ByteArrayOutputStream(); 			
//        photo.compress(Bitmap.CompressFormat.PNG, 100, os);     			
		values.put(StudentDBHelper.s_StudentTag_Photo, /*os.toByteArray()*/photo);
				
		
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
		}
    }

    public Handler mInnerHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what){
    		case EVENT_QUERY_TIME_OUT:
    			mTextViewStatus.setText(R.string.FailQueryServerStatus);
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
		    			mTextViewStatus.setText(R.string.QueryServerStatus);						
						mServiceChannel.QueryServer();						
						Message msg = mInnerHandler.obtainMessage(EVENT_QUERY_TIME_OUT);
						mInnerHandler.sendMessageDelayed(msg, DELAY_QUERY_TIME);
					}else{
						mSpinnerSSID.performClick();
		    			mTextViewStatus.setText(R.string.SelectClassTip);
					}
				}else{
				    StartPaperActivity("初三一班  张三丰", mCheckBoxOffline.isChecked());					
				}
	    	}else if (btn == mImgViewHeader){
	    		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  
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
    	mTextViewStatus.setText(R.string.AppInitStatus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(GROUP_NORMAL, MENU_MODIFY_PASSWORD, 0, R.string.ModifyPassword);
//    	menu.add(GROUP_NORMAL, MENU_SETTING, 0, R.string.Setting);
    	menu.add(GROUP_NORMAL, MENU_WIFI_SETTING, 0, R.string.WifiSetting);
    	
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    	case MENU_MODIFY_PASSWORD:
    		if (mTxtStudentID != null && mTxtStudentID.getText().length() != 0){
	    		LayoutInflater inflater = LayoutInflater.from(getApplication());
	    		final View modifyView = inflater.inflate(R.layout.modifypassword, null);
	    		final Dialog dlg = new Dialog(this, R.style.NoTitleDialog);
	    		dlg.setContentView(modifyView);
	        	String title = String.format(getResources().getString(R.string.SecretTitle), 
	        			mTxtStudentID.getText().toString());   		
	    		((TextView)modifyView.findViewById(R.id.textViewTitle)).setText(title);
	    		Button buttonOK = (Button)modifyView.findViewById(R.id.buttonOK);
	    		buttonOK.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						EditText textFirst = (EditText)modifyView.findViewById(R.id.editTextFirstPassword);
						EditText textSecond = (EditText)modifyView.findViewById(R.id.editTextSecond);
						EditText textOld = (EditText)modifyView.findViewById(R.id.editTextOldPassword);
						//TODO:检查textOld是否正确
			    		StudentDBHelper dbHelper = new StudentDBHelper(LoginActivity.this);
			    		ContentValues values = dbHelper.FindItem(mID);
						String oldPwd = (String)values.get(StudentDBHelper.s_StudentTag_Passwd);
						if (oldPwd != null && oldPwd.equals(textOld)){
							//TODO:检查前后两者是否相等
							if (textFirst.getText().equals(textSecond.getText())){
								//TODO:设置密码到数据库
								values.put(StudentDBHelper.s_StudentTag_Passwd, textFirst.toString());
								dbHelper.Update(values, mID);
							}
						}
					}
				});
	    		Button buttonCancel = (Button)modifyView.findViewById(R.id.buttonCancel);    		
	    		buttonCancel.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
	    		
	    		dlg.show();
    		}
    		break;
    	case MENU_SETTING:
    		Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
    		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
    				Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    		startActivity(intent);
    		break;
    	case MENU_WIFI_SETTING:
    		Intent intentWifi = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
    		intentWifi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
    				Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    		startActivity(intentWifi);
    		break;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }    

    @Override
    protected Dialog onCreateDialog(int id){
    	if (id == DIALOG_MODIFY_PASSWORD){
    		LayoutInflater inflater = LayoutInflater.from(getApplication());
    		View modifyView = inflater.inflate(R.layout.modifypassword, null);	
    		AlertDialog.Builder builder = new AlertDialog.Builder(this)
    			.setPositiveButton(android.R.string.ok, null)
    			.setNegativeButton(android.R.string.cancel, null)
    			.setView(modifyView);
    		return builder.create();
    	}
    	return super.onCreateDialog(id);
    }
    
    public void StartPaperActivity(String studentInfo, boolean bOffline){
    	SaveStudentInfo(mID, studentInfo, mTxtPassword.getText().toString(), mImgViewHeader.getDrawable().toString());
    	
		Intent openSheetActivity = new Intent(LoginActivity.this, PaperActivity.class);
		openSheetActivity.putExtra(KingCAIConfig.StudentInfo, studentInfo);
		openSheetActivity.putExtra(KingCAIConfig.ServerIP, mServerIP);
		openSheetActivity.putExtra(KingCAIConfig.SSID, mSSID);
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
			mSSID = (String)mSpinnerSSID.getAdapter().getItem(mSpinnerSSID.getSelectedItemPosition());
			mTextViewStatus.setText(R.string.FoundServerStatus);
			mServiceChannel.setServerIPAddr(mServerIP);
			mServiceChannel.connectServer(mTxtStudentID.getText().toString(), 
										  mTxtPassword.getText().toString());
		}


		public void onLoginSuccess(String studentinfo) {
			mTextViewStatus.setText(R.string.SuccessLoginStatus);
			StartPaperActivity(studentinfo, mCheckBoxOffline.isChecked());
		}


		public void onLoginFail() {
			mTextViewStatus.setText(R.string.FailLoginStatus);
			showToast(R.string.InputCorrectIDTip);			
			cleanForm();
		}


		public void onPaperTitleReceived(String title) {
			Log.d("PaperActivity", title);			
		}


		public void onNewQuestion(String answer, int type, String content, boolean bHasImage) {
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
