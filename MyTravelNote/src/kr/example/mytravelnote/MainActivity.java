package kr.example.mytravelnote;

import kr.example.mytravelnote.common.BackPressClose;
import kr.example.mytravelnote.common.DownloadImage;
import kr.example.mytravelnote.common.Geocoder_L;
import kr.example.mytravelnote.common.Image_loder;
import kr.example.mytravelnote.daum.Daum_Zoon;
import kr.example.mytravelnote.point.Point_trip;
import kr.example.mytravelnote.weather.Weather_xml;
import kr.net.mytravelnote.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends ActionBarActivity
{
    /* Navigation Drawer 관련 변수 */
    private String[] navItems = {"행사정보", "관광정보", "주변정보", "나의위치", "추천하기", "종료하기"}; /* 메뉴명 저장 변수 */
    private ListView lvNavList;

    @SuppressWarnings("unused")
    private FrameLayout flContainer = null;

    /* ActionBar Toggle 관련 변수 */
    private ActionBarDrawerToggle mDrawerToggle = null;
    private DrawerLayout mDrawerLayout = null;

    /* Context */
    private Context mContext = MainActivity.this;

    /* GPS 관련 함수 */
    private boolean is = true;
    private double MapX = 0.0; /* 위도 */
    private double MapY = 0.0; /* 경도 */

    /* 약관 메시지 */
    private final String message = "GPS기능이 탑재되었거나, Wi-Fi 기능이 탑재된 단말기 또는 Cell ID에 기반한 단말기 등을 비롯하여 위치정보수집기능이 주기적으로 이동통신 및 Wi-Fi 기지국에 대한 위치정보를 수집합니다. 이러한 정보는, 이용자의 현재 위치를 확인하고 서비스를 제공하는데 사용됩니다. 그 외 활용할 목적으로 서버에 수집, 전송, 또는 제 3자에게 제공하지 않습니다.";

    /* BackPressClose 객체 생성 */
    private BackPressClose backPressclose = new BackPressClose(this);
	
	/* 네비게이션 드로어 관련 클래스 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override /* Auto-generated method stub */
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
		{
			switch(position)
            {
				case (0) : /* 관광정보 화면으로 이동 */
				{
					Toast.makeText(getApplicationContext(), "행사정보", Toast.LENGTH_SHORT).show();
	    			Intent intent = new Intent(mContext, SubActivity11_fsetival.class);
	    			startActivity(intent);
	    			break; 
				}
				case (1) : /* 관광정보 화면으로 이동 */
				{
					Toast.makeText(getApplicationContext(), "관광정보", Toast.LENGTH_SHORT).show();
	    			Intent intent = new Intent(mContext, SubActivity8_tourlist.class);
	    			intent.putExtra("MapX", MapX); /* 위도 값 전달 */
	    			intent.putExtra("MapY", MapY); /* 경도 값 전달 */
	    			startActivity(intent);
					break; 
				}
				case (2) : /* 주변정보 화면으로 이동 */
				{
					Toast.makeText(getApplicationContext(), "주변정보", Toast.LENGTH_SHORT).show();
	    			Intent intent = new Intent(mContext, SubActivity7_information.class);
	    			intent.putExtra("MapX", MapX); /* 위도 값 전달 */
	    			intent.putExtra("MapY", MapY); /* 경도 값 전달 */
	    			startActivity(intent);
					break; 
				}
				case (3) : /* 나의위치 화면으로 이동 */
				{
					Toast.makeText(getApplicationContext(), "나의위치", Toast.LENGTH_SHORT).show();
	    			Intent intent = new Intent(mContext, SubActivity4_map.class);
	    			intent.putExtra("Select_Mode", "Main_Map"); /* 주변정보와 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
	    			intent.putExtra("MapX", MapX); /* 위도 값 전달 */
	    			intent.putExtra("MapY", MapY); /* 경도 값 전달 */
	    			startActivity(intent);
					break; 
				}
				case (4) : /* 추천하기 화면으로 이동 */
				{
					Toast.makeText(getApplicationContext(), "추천하기", Toast.LENGTH_SHORT).show();
	    			Intent intent = new Intent(mContext, SubActivity13_comment.class);
	    			intent.putExtra("MapX", MapX); /* 위도 값 전달 */
	    			intent.putExtra("MapY", MapY); /* 경도 값 전달 */
	    			startActivity(intent);
					break; 
				}
				case (5) : /* Application 종료 */
                { System.exit(0); break; }
            }
			mDrawerLayout.closeDrawer(lvNavList);
		}
	}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding);

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
        mActionBar.setSubtitle("여행일기 - 홈"); /* ActionBar 소제목 TEXT 설정 */
        mActionBar.setDisplayHomeAsUpEnabled(true); /* ActionBar 홈화면 아이콘 생성 설정 */
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff"))); /* ActionBar 색상 변경 */
        mActionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff")));
        
        /* Navigation Drawer 관련 객체 생성 */
        lvNavList = (ListView)findViewById(R.id.lv_activity_main_nav_list);
        flContainer = (FrameLayout)findViewById(R.id.fl_activity_main_container);
        
        /* Navigation Drawer 관련 구문 */
        lvNavList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        lvNavList.setBackgroundColor(Color.WHITE);
        lvNavList.setOnItemClickListener(new DrawerItemClickListener());
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_activity_main_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open) 
        {
        	/* Navigation Drawer 닫았을 경우 호출 되는 함수 */
        	public void onDrawerClosed(View view) { super.onDrawerClosed(view); }
        	/* Navigation Drawer 열었을 경우 호출 되는 함수 */
        	public void onDrawerOpened(View drawerView) { super.onDrawerOpened(drawerView); }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        /* 사용자 위치 정보 제공 동의 구문 */
        AlertDialog.Builder bulider = new AlertDialog.Builder(this); /* AlertDialog 객체 생성 */
        bulider.setTitle("위치정보를 수집/이용/제공 동의").setMessage(message).setCancelable(false).setPositiveButton("동의", new DialogInterface.OnClickListener() 
        {	
			@Override /* Auto-generated method stub */
			public void onClick(DialogInterface dialog, int which) 
			{ requestGPS(); /* GPS 위도와 경도를 찾아주는 함수 */ }
		}).setNegativeButton("취소", new DialogInterface.OnClickListener() 
		{
			@Override /* Auto-generated method stub */
			public void onClick(DialogInterface dialog, int which) { dialog.cancel(); System.exit(0); /* Application 종료 */ }
		}).show();
    }
    
    /* 뒤로가기 두 번 누를시 호출 되는 함수 */
    @Override
    public void onBackPressed() 
    { backPressclose.Back_Close(); }
    
    /* TODO GPS 위도와 경도를 찾아주는 함수 */
	private void requestGPS()
    {
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			
			/* GPS의 좌표가 바뀔 경우 호출 되는 함수 */
		    public void onLocationChanged(Location location) 
		    {
		      // Called when a new location is found by the network location provider.
		    	double mLatitude = location.getLatitude(); /* 위도 */
		    	double mLongitude= location.getLongitude(); /* 경도 */
		    	MapX = mLatitude; /* 전역 변수 저장 - 위도 */
		    	MapY = mLongitude; /* 전역 변수 저장 - 경도 */
		    	
		    	if(is == true) 
		    	{  
		    		/* 텍스트 뷰 */
					final TextView mTextView_W = (TextView)findViewById(R.id.weather_information_textview); /* 현제 날씨 정보와 온도를 출력 해주는 텍스트 뷰 */
					final TextView mTextView_M = (TextView)findViewById(R.id.point_information_textview);
					
					/* 이미지 뷰 */
					final ImageView mImageView_W = (ImageView)findViewById(R.id.weather_information_imageview); /* 현제 날씨 아이콘을 등록 해주는 구문 */
					final ImageView mImageView_M = (ImageView)findViewById(R.id.tour_map_imageview);
					
					/* 리스트 뷰 */
			        final ListView[] mListView_D = {null, null};
			        mListView_D[0] = (ListView)findViewById(R.id.food_place_listview); /* 움식점 리스트뷰 */
			        mListView_D[1] = (ListView)findViewById(R.id.lodging_place_listview); /* 숙소 리스트뷰 */
			        final ListView mListView_T = (ListView)findViewById(R.id.tour_listview);  /* 여행 리스트뷰 */
			        
			        /* 날씨 정보를 가져오는 구문 */
		    		final Weather_xml mWeather = new Weather_xml(mContext, mTextView_W, mImageView_W); /* weather_xml 객체 생성 */
		    		mWeather.execute(mLatitude, mLongitude); /* 현제 위치 기상정보 조회 */
			        
		    		/* Daum */
					final Daum_Zoon[] mDaum = {null, null}; /* Daum_Zoon 객체 생성 */
					mDaum[0] = new Daum_Zoon(mContext, "FD6", mListView_D[0], mLatitude, mLongitude); /* 음식점 */
					mDaum[0].execute(); /* 주변 음식점 조회 */
					mDaum[1] = new Daum_Zoon(mContext, "AD5", mListView_D[1], mLatitude, mLongitude); /* 숙소 */
					mDaum[1].execute(); /* 주변 숙소 조회 */
					
					/* Tour */
					final Point_trip mTrip = new Point_trip(mContext, mLatitude, mLongitude, mListView_T); /* Point_trip 객체 생성 */
					mTrip.execute(); /* 주변 관광지 조회 */
		    		
					/* 지도 이미지를 가져오는 구문 */
		    		StringBuffer mGoogleURL = new StringBuffer("http://maps.googleapis.com/maps/api/staticmap?center="); /* StringBuffer 객체 생성 */
                    mGoogleURL.append(mLatitude + "," + mLongitude); /* 카메라 이동 위도와 경도 */ mGoogleURL.append("&zoom=18&size=640x640&scale=2&maptype=roadmap&markers=color:blue|label:N|");
                    mGoogleURL.append(mLatitude + "," + mLongitude); /* 마커 설정 위도와 경도 */ mGoogleURL.append("%7C11211&sensor=false"); mGoogleURL.append("&key=AIzaSyAPwZbV6iLNwLv23_3l-dmS_E6pVX34e0I"); /* Google API Key */

                    DownloadImage.image(mImageView_M, mGoogleURL.toString(), mContext);

					/* GeoCoder */
					final Geocoder_L mGeocoder = new Geocoder_L(mContext, mTextView_M, 1); /* mGeocoder 객체 생성 */
					mGeocoder.execute(mLatitude, mLongitude); /* Geocoder 조회 구문 */
	
					is = false;
		    	}
		   }
		    
		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		    public void onProviderEnabled(String provider) {}
		    public void onProviderDisabled(String provider) {}
		  };

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    
    protected void onPostCreate(Bundle savedInstanceState)
    {
    	super.onPostCreate(savedInstanceState);
    	mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	
    	/* Action Bar Toggle 관련 구문 */
    	if(mDrawerToggle.onOptionsItemSelected(item)) { return true; }
        
    	/* Action Bar 관련 구문 */
    	switch (item.getItemId())
        {
    		case(R.id.action_settings): /* 설정 화면 */
    		{ 
    			Toast.makeText(this, "설정", Toast.LENGTH_SHORT).show(); 
    			Intent intent = new Intent(mContext, SubActivity3_setting.class);
    			startActivity(intent);
    			return true; 
    		}
    		case(R.id.action_notebook) : /* 공책 화면 */
    		{ 
    			Toast.makeText(this, "공책", Toast.LENGTH_SHORT).show();
    			Intent intent = new Intent(mContext, SubActivity2_notebook.class);
    			startActivity(intent);
    			return true; 
    		}
    		case(R.id.action_camara) : /* 촬영 화면 */
    		{ 
    			Toast.makeText(this, "촬영", Toast.LENGTH_SHORT).show(); 
    			Intent intent = new Intent(mContext, SubActivity_photo.class);
    			intent.putExtra("MapX", MapX); /* 위도 값 전달 */
    			intent.putExtra("MapY", MapY); /* 경도 값 전달 */
    			startActivity(intent);
    			return true; 
    		}
    		default : { return super.onOptionsItemSelected(item); }
        }
    }
}
