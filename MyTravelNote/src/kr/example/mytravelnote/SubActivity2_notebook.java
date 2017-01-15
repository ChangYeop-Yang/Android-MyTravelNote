package kr.example.mytravelnote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import kr.net.mytravelnote.R;

public class SubActivity2_notebook extends ActionBarActivity {
	
	/* SQLite 관련 변수 */
	protected dbHelper helper = null;
	protected SQLiteDatabase DataBase = null;

    /* Context */
    private Context mContext = SubActivity2_notebook.this;
	
	@SuppressWarnings("deprecation")
	protected void createSQLite()
    {
		/* dbHelper 객체 생성 */
		helper = new dbHelper(this);

		/* 읽기/쓰기 모드로 데이터베이스를 오픈 */
		try
        {
            DataBase = helper.getWritableDatabase(); /* 일기/쓰기 가 가능한 데이터베이스  */

			/* Query */
			final String Query1 = "SELECT * FROM photo_db ORDER BY title ASC";

			/* 커서 객체 생성 및 쿼리문 저장 */
			final Cursor mCursor = DataBase.rawQuery(Query1, null);
			startManagingCursor(mCursor); /* 액티비티의 생애주기를 커서의 생애주기를 일치시켜 주는 구문 */

			String[] from = { "title", "point" };
			int[] to = { android.R.id.text1, android.R.id.text2 };

            /* mSimpleCursorAdapter */
			SimpleCursorAdapter mSimpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, mCursor, from, to);

            /* ListView */
			final ListView list = (ListView) findViewById(R.id.data_list);
			list.setAdapter(mSimpleCursorAdapter);
			list.setOnItemClickListener(new OnItemClickListener()
            {
				@Override /* Auto-generated method stub */
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
				{ 
					Toast.makeText(mContext, mCursor.getString(1), Toast.LENGTH_SHORT).show(); /* 토스트 기능으로 해당 아이템을 클릭 시 제목을 출력 */
                    mVibrate(); /* 기기 진동 관련 함수 호출 */

                    /* Intent */
					Intent intent = new Intent(mContext, SubActivity6_gallery.class); /* 인텐트 객체 생성 */
					intent.putExtra("select", "note"); /* 인텐트에 결과 값을 전달(선택) */
	    			intent.putExtra("path", mCursor.getString(6)); /* 인텐트에 결과 값을 전달 (path - 경로) */
	    			intent.putExtra("code", mCursor.getString(0)); /* 인텐트에 결과 값을 전달 (path - 번호) */
	    			startActivity(intent); /* 인텐트 시작 */
				}
            });
		}
		/* 읽기 전용 모드로 데이터베이스를 오픈 */
		catch(SQLiteException e)
        {
            DataBase = helper.getReadableDatabase();
            e.printStackTrace();
            Log.e("Note - SQL", e.getMessage());
            Toast.makeText(mContext, "SQLite 문제가 발생 하였습니다.", Toast.LENGTH_SHORT).show();
        }
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);

        createSQLite(); /* SQLite 체크 관련 함수  */
        
        /* 설정 값을 가져오는 구문 */
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); /* 설정 값을 가져오기 위하여 Preferences 객체 생성 */
        String ActionBar_Name = null; /* 액션바 타이틀을 지정하기 위한 값을 저장하기 위한 스트링 변수 생성 */
        boolean ActionBar_title = mSharedPreferences.getBoolean("useUserName", false); /* 설정 값에서 설정 된 사용자 이름 사용 구분 가져오는 구문 */

        /* 설정 값에서 설정 된 사용자 이름을 가져오는 구문 */
        if(ActionBar_title == true) { ActionBar_Name = mSharedPreferences.getString("userName", "여행일기"); }
        else if(ActionBar_title == false) { ActionBar_Name = "여행일기"; }
        
        /* Action Bar 관련 구문 */
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setTitle(ActionBar_Name); /* ActionBar Title TEXT 설정 */
        mActionBar.setSubtitle("여행일기 - 다이어리"); /* ActionBar 소제목 TEXT 설정 */
        mActionBar.setDisplayHomeAsUpEnabled(false); /* ActionBar 홈화면 아이콘 생성 설정 */
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff"))); /* ActionBar 색상 변경 */
        mActionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff")));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sub2_notebook, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
	public void onBackPressed()
	{ finish(); }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        
    	/* TODO Action Bar 관련 구문 */
    	switch (item.getItemId())
        {
		    case(R.id.action_settings): /* 설정 화면 */
		    {
			    Toast.makeText(mContext, "설정", Toast.LENGTH_SHORT).show();

                /* Intent */
                Intent intent = new Intent(mContext, SubActivity3_setting.class);
			    startActivity(intent);
			    return true;
		    }
		    default : { return super.onOptionsItemSelected(item); }
	    }
        
    }
    
	/* TODO 기기 진동 관련 함수 */
	private void mVibrate()
    {
		Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); /* Vibrator 객체 생성 */
		long[] vibratePattern = {100, 100, 300};
        mVibrator.vibrate(300);
        mVibrator.vibrate(vibratePattern, -1);
    }
}
