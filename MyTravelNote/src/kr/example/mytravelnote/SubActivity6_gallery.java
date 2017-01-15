package kr.example.mytravelnote;

import java.io.File;
import kr.net.mytravelnote.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class SubActivity6_gallery extends ActionBarActivity
{
	/* Intent 관련 변수 */
	private String intent_select = null; /* 인텐트를 통한 결과 값을 저장하는 변수 - 선택 */
	private String intent_path = null; /* 인텐트를 통한 결과 값을 저장하는 변수 - 경로 */
	private String intent_code = null; /* 인텐트를 통한 결과 값을 저장하는 변수 - 번호 */

    /* ShareActionProvider */
	private ShareActionProvider mShareActionProvider = null;

    /* Context */
    private Context mContext = SubActivity6_gallery.this;

	/* SQLite 관련 변수 */
	protected dbHelper helper = null;
	protected SQLiteDatabase DataBase = null;

	/* TODO SQLite 체크 관련 함수 */
	protected void createSQLite()
    {
		/* dbHelper 객체 생성 */
		helper = new dbHelper(this); 
		
		/* 읽기/쓰기 모드로 데이터베이스를 오픈 */
		try { DataBase = helper.getWritableDatabase(); }
		/* 읽기 전용 모드로 데이터베이스를 오픈 */
		catch(SQLiteException e)
        {
            DataBase = helper.getReadableDatabase();
            e.printStackTrace();
            Log.e("Gallery - SQL", e.getMessage());
            Toast.makeText(mContext, "SQLite 문제가 발생 하였습니다.", Toast.LENGTH_SHORT).show();
        }
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        /* SharedPreferences */
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); /* 설정 값을 가져오기 위하여 Preferences 객체 생성 */
        /* String */
        String mActionBarName = null; /* 액션바 타이틀을 지정하기 위한 값을 저장하기 위한 스트링 변수 생성 */
        /* Boolean */
        boolean mActionBarIs = mSharedPreferences.getBoolean("useUserName", false); /* 설정 값에서 설정 된 사용자 이름 사용 구분 가져오는 구문 */

        /* 설정 값에서 설정 된 사용자 이름을 가져오는 구문 */
        mActionBarName = (mActionBarIs == true) ? mSharedPreferences.getString("userName", "여행일기") : ("여행일기");
        
        /* Action Bar 관련 구문 */
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setTitle(mActionBarName); /* ActionBar Title TEXT 설정 */
        mActionBar.setSubtitle("여행일기"); /* ActionBar 소제목 TEXT 설정 */
        mActionBar.setDisplayHomeAsUpEnabled(false); /* ActionBar 홈화면 아이콘 생성 설정 */
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff"))); /* ActionBar 색상 변경 */
        mActionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff")));

        createSQLite(); /* SQLite 체크 관련 함수 호출 */

        /* ImageView */
        final ImageView mImageView = (ImageView) findViewById(R.id.expendimageView); /* 이미지 뷰 객체 생성 */
        
        Intent mIntent = getIntent(); /* 인텐트 객체 생성 */
        intent_select = mIntent.getStringExtra("select");
        
        /* intent 값 불러오는 구문 */
        switch(intent_select)
        {
        	case ("note") : /* notebook -> gallery */
        	{
        		intent_path = mIntent.getStringExtra("path"); /* notebook에서 받은 인턴트 값을 저장 - 경로 */
                intent_code = mIntent.getStringExtra("code"); /* notebook에서 받은 인턴트 값을 저장 - 번호 */

                /* Uri */
                Uri mUri =  Uri.parse(intent_path);
                mImageView.setImageURI(mUri); /* 입력 받은 경로를 이미지 뷰에 이미지 출력 */
                break;
        	}
        	case ("map") : /* map -> gallery */
        	{
        		intent_path = mIntent.getStringExtra("Maker_id"); /* map에서 받은 인턴트 값을 저장 - 번호 */

        		if( !intent_path.equals(null) )
                { 
                	Uri mUri =  Uri.parse(intent_path);
                    mImageView.setImageURI(mUri); /* 입력 받은 경로를 이미지 뷰에 이미지 출력 */
                } else { finish(); } /* 리턴 값이 널일 경우 해당 인텐트 종료 */
        	}
        }
	}
	
	@Override
	public void onBackPressed()
	{ finish(); }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sub6_gallery, menu);
        
        /* Set up ShareActionProvider's default share intent */
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(getDefaultIntent());
        
        return super.onCreateOptionsMenu(menu);
    }
	
	/** Defines a default (dummy) share intent to initialize the action provider.
	  * However, as soon as the actual content to be used in the intent
	  * is known or changes, you must update the share intent by again calling
	  * mShareActionProvider.setShareIntent()
	  */
	@SuppressWarnings("static-access")
	private Intent getDefaultIntent()
	{
		Intent mIntent = new Intent(Intent.ACTION_SEND);
        mIntent.setType("image/JPG"); /* Intent를 통한 전송타입 설정 */
		Uri uri = Uri.fromFile(new File(intent_path)); /* Image파일 경로를 설정 및 Uri 객체 생성*/
        mIntent.putExtra(mIntent.EXTRA_STREAM, uri); /* Intent에 저장 */
		return mIntent; /* Intent 값 반환 */
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
		
    	/* Action Bar 관련 구문 */
    	switch (item.getItemId())
        {
    		case(R.id.Delete_icon) : /* 삭제버튼 */
    		{
    			AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
                mAlertDialog.setMessage("해당 파일을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener()
                {
    				/* Auto-generated method stub */
    				@Override
					public void onClick(DialogInterface dialog, int which) 
    				{
    					Toast.makeText(getApplicationContext(), "해당 파일이 삭제 되었습니다.", Toast.LENGTH_SHORT).show(); /* 정상 적으로 삭제가 되었을 경우 출력 */
    					new File(intent_path).delete(); /* 해당 파일을 삭제 하는 구문 */
    					
    					/* DataBase 구문 */
    					DataBase.beginTransaction(); /* 트랜잭션 실행 */
    					final String query1 = "DELETE FROM photo_db WHERE _id = '" + intent_code + "'"; /* Query */
    					
    					try
                        {
                           DataBase.execSQL(query1);
                           DataBase.setTransactionSuccessful(); /* 정상적으로 쿼리 문이 수행 되었을 경우 트랜잭션 성공 */
                        }
    					catch (SQLiteException e)
                        {
                            e.printStackTrace(); /* SQLite 오류가 발생 시 토스트 기능 출력 */
                            Log.e("gallery - SQL", e.getMessage());
                            Toast.makeText(mContext, "파일 삭제 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        }
    			        finally { DataBase.endTransaction(); } /* 트랜잭션 종료 */

    					/* Intent 구문 */
    					Intent mIntent = new Intent(mContext, SubActivity2_notebook.class); /* Intent 객체 생성 */
    	    			startActivity(mIntent); /* Intent 실행 */
    	    		}
				}).setNegativeButton("아니오", new DialogInterface.OnClickListener()
                {
					@Override /* Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) 
					{ Toast.makeText(mContext, "취소되었습니다.", Toast.LENGTH_SHORT).show(); dialog.cancel(); }
                });
    			AlertDialog mAlert = mAlertDialog.create();
                mAlert.setTitle("삭제");
                mAlert.show();
    			return true;
            }
    		case(android.R.id.home) :
    		{
    			/* Intent 구문 */
				Intent mIntent = new Intent(mContext, SubActivity2_notebook.class); /* Intent 객체 생성 */
				Toast.makeText(mContext, "뒤로가기", Toast.LENGTH_SHORT).show(); /* 뒤로 이동 하였다는 토스트 출력 */
    			startActivity(mIntent); /* Intent 실행 */
                finish();
				return true;
    		}
    		default : { return super.onOptionsItemSelected(item); } } 
    	}
}