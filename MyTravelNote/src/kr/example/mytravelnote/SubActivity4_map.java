package kr.example.mytravelnote;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import kr.example.mytravelnote.apikey.API_KEY;
import kr.example.mytravelnote.common.Geocoder_L;
import kr.example.mytravelnote.common.Value;
import kr.example.mytravelnote.common.Vibration;
import kr.example.mytravelnote.point.Point_festival;
import kr.example.mytravelnote.point.Point_trip;
import kr.example.mytravelnote.weather.Weather_xml;
import kr.net.mytravelnote.R;
import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;
import net.daum.adam.publisher.AdView.OnAdClickedListener;
import net.daum.adam.publisher.AdView.OnAdClosedListener;
import net.daum.adam.publisher.AdView.OnAdFailedListener;
import net.daum.adam.publisher.AdView.OnAdLoadedListener;
import net.daum.adam.publisher.AdView.OnAdWillLoadListener;
import net.daum.adam.publisher.impl.AdError;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPOIItem.CalloutBalloonButtonType;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapView.CurrentLocationEventListener;
import net.daum.mf.map.api.MapView.POIItemEventListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SubActivity4_map extends ActionBarActivity implements MapView.MapViewEventListener, POIItemEventListener, CurrentLocationEventListener {
	
	/* SQLite 관련 변수 */
	protected dbHelper helper;
	protected SQLiteDatabase DataBase;
		
	/* Daum Map 관련 객체 생성 */
	public static MapView mapView; /* Daum 지도관련 MapView */
    
	/* GPS 자기 위치를 가져오는 초기화 함수 생성 */
	private double MapX = 0.0; /* 위도 */
	private double MapY = 0.0; /* 경도 */
	
	/* ActionBar 관련 변수 */
	private ActionBar actionBar; /* ActionBar 관련 변수 */
	
	/* Context */
	private Context mContext = SubActivity4_map.this;
	
	/* TODO XML 관련 데이터 저장 변수 구문 */
	ArrayList<String> Title = new ArrayList<String>(10); /* 상호명 관련 String 배열 변수 생성 */
	ArrayList<String> Latitude = new ArrayList<String>(10); /* 위도 관련 String 배열 변수 생성 */
	ArrayList<String> Longitude = new ArrayList<String>(10); /* 경도 관련 String 배열 변수 생성 */
	ArrayList<String> Address = new ArrayList<String>(10); /* 주소 저장 변수 생성 */

	/* HashMap */
	private ArrayList<HashMap<String,Double>> routeAH = new ArrayList<HashMap<String, Double>>(); /* 사용자의 위치를 저장하는 HashMap 객체 생성 */
	
	/* Adview 관련 변수 */
	private AdView adView = null;
	
	protected void createSQLite()
    {
		/* dbHelper 객체 생성 */
		helper = new dbHelper(this); 
		
		/* 읽기/쓰기 모드로 데이터베이스를 오픈 */
		try { DataBase = helper.getWritableDatabase(); }
		/* 읽기 전용 모드로 데이터베이스를 오픈 */
		catch(SQLiteException ex) { DataBase = helper.getReadableDatabase(); Toast.makeText(getApplicationContext(), "SQLite 문제가 발생 하였습니다.", Toast.LENGTH_SHORT).show(); }
	}
	
	/* TODO createDaumMap 관련 함수 */
	private void createDaumMap()
    {
		mapView = new MapView(this); /* Daum Map 관련 객체 생성 */
		mapView.setDaumMapApiKey(API_KEY.Daum_map_APIKEY); /* Daum Map ApiKey 입력 구문 */
				
		/* Daum Map View Event 관련 구문 */
		mapView.setMapViewEventListener(this);
		mapView.setPOIItemEventListener(this); /* Marker 이벤트 관련 구문 */
		mapView.setCurrentLocationEventListener(this); /* Traking 모드 활성화 */
		mapView.isShowingCurrentLocationMarker(); /* 현위치를 표시하는 마커를 생성한다. */
	
		ViewGroup mapViewContainer = (ViewGroup)findViewById(R.id.map_view); /* Daum Map을 띄울 View 객체 생성 */
		mapViewContainer.addView(mapView);
	}
	
	/* TODO SQL createDaumMap Maker 관련 함수 */
	private void insertDBMaker(String title, String point, double latitude, double longitude, String path)
	{		
		/* Maker 기본 객체 생성 구문 */
		MapPOIItem mMapPOIItem = new MapPOIItem(); /* Maker 관련 객체 생성 */
        mMapPOIItem.setItemName(path); /* Maker 이름 */
        mMapPOIItem.setTag(1); /* Maker 태그번호 */
        mMapPOIItem.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude)); /* Maker 경로 설정 */
		
		/* Custom Marker 구문 */
        mMapPOIItem.setMarkerType(MapPOIItem.MarkerType.CustomImage); /* Maker 커스텀 설정 */
        mMapPOIItem.setCustomImageResourceId(R.drawable.ic_point); /* Maker 커스텀 이미지 설정 */
        mMapPOIItem.setCustomImageAutoscale(false);
		
		/* Custom Marker XML 설정 구문 */
		View mView = getLayoutInflater().inflate(R.layout.activity_custommaker, null);
		((ImageView) mView.findViewById(R.id.marker_image)).setImageResource(R.drawable.ic_maker); /* Maker Image 변경 해주는 구문 */
		((TextView) mView.findViewById(R.id.main_title)).setText(title); /* Maker Text 변경 해주는 구문 */
		((TextView) mView.findViewById(R.id.sub_title)).setText(point); /* Maker 장소 변경 해주는 구문 */
        mMapPOIItem.setCustomCalloutBalloon(mView); /* 커스텀 마커를 레이아웃에 그려주는 구문  */
		/* Maker 생성 */ mapView.addPOIItem(mMapPOIItem);
	}
	
	/* TODO Theme createDaumMap Maker 관련 함수 */
	@SuppressLint("InflateParams") 
	private void insertTypeMaker(String Name, String Address, String MapX, String MapY, String is) 
	{
		double lat = Double.valueOf(MapX).doubleValue(); /* 위도 형 변환 */
		double log = Double.valueOf(MapY).doubleValue(); /* 경도 형 변환 */
		
		/* Maker 기본 객체 생성 구문 */
		MapPOIItem marker = new MapPOIItem(); /* Maker 관련 객체 생성 */
		marker.setTag(3); /* 일반 마커 태그 번호 부여 */
		marker.setItemName(Name); /* Maker 이름 */
		marker.setMapPoint(MapPoint.mapPointWithGeoCoord(lat, log)); /* Maker 경로 설정 */
		
		switch(is) 
		{
			/* 해수욕장 */
			case ("해수욕장") : 
			{
				/* Custom Marker 구문 */
				marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); /* Maker 커스텀 설정 */
				marker.setCustomImageResourceId(R.drawable.beach_maker); /* 마커 이미지 변경 */
				marker.setCustomImageAutoscale(false);
		
				/* Custom Marker XML 설정 구문 */
				View customCalloutBalloonView = getLayoutInflater().inflate(R.layout.activity_custommaker, null);
				((ImageView) customCalloutBalloonView.findViewById(R.id.marker_image)).setImageResource(R.drawable.bit_beach); /* Maker Image 변경 해주는 구문 */
				((TextView) customCalloutBalloonView.findViewById(R.id.main_title)).setText(Name); /* Maker Text 변경 해주는 구문 */
				((TextView) customCalloutBalloonView.findViewById(R.id.sub_title)).setText(Address); /* Maker 장소 변경 해주는 구문 */
				marker.setCustomCalloutBalloon(customCalloutBalloonView); /* 커스텀 마커를 레이아웃에 그려주는 구문  */ break; 
			}
			case ("수목원") : 
			{ 
				/* Custom Marker 구문 */
				marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); /* Maker 커스텀 설정 */
				marker.setCustomImageResourceId(R.drawable.tree_marker); /* 마커 이미지 변경 */
				marker.setCustomImageAutoscale(false); 
				
				/* Custom Marker XML 설정 구문 */
				View customCalloutBalloonView = getLayoutInflater().inflate(R.layout.activity_custommaker, null);
				((ImageView) customCalloutBalloonView.findViewById(R.id.marker_image)).setImageResource(R.drawable.bit_beach); /* Maker Image 변경 해주는 구문 */
				((TextView) customCalloutBalloonView.findViewById(R.id.main_title)).setText(Name); /* Maker Text 변경 해주는 구문 */
				((TextView) customCalloutBalloonView.findViewById(R.id.sub_title)).setText(Address); /* Maker 장소 변경 해주는 구문 */
				marker.setCustomCalloutBalloon(customCalloutBalloonView); /* 커스텀 마커를 레이아웃에 그려주는 구문  */ break; 
			}
			case ("온천") :
			{
				/* Custom Marker 구문 */
				marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); /* Maker 커스텀 설정 */
				marker.setCustomImageResourceId(R.drawable.ic_bath); /* 마커 이미지 변경 */
				marker.setCustomImageAutoscale(false);
				
				/* Custom Marker XML 설정 구문 */
				View customCalloutBalloonView = getLayoutInflater().inflate(R.layout.activity_custommaker, null);
				((ImageView) customCalloutBalloonView.findViewById(R.id.marker_image)).setImageResource(R.drawable.ic_water); /* Maker Image 변경 해주는 구문 */
				((TextView) customCalloutBalloonView.findViewById(R.id.main_title)).setText(Name); /* Maker Text 변경 해주는 구문 */
				((TextView) customCalloutBalloonView.findViewById(R.id.sub_title)).setText(Address); /* Maker 장소 변경 해주는 구문 */
				marker.setCustomCalloutBalloon(customCalloutBalloonView); /* 커스텀 마커를 레이아웃에 그려주는 구문  */
				break;
			}
		}
		/* Maker 생성 */ mapView.addPOIItem(marker);
	}
	
	/* TODO createDaumMap Point Maker 관련 함수 */
	private void insertBasicMaker(String Name, double Latitude, double Longitude, String Number)
	{
			/* Maker 기본 객체 생성 관련 구문 */
			MapPOIItem marker = new MapPOIItem(); /* Maker 관련 객체 생성 */
			marker.setTag(2); /* 일반 마커 태그 번호 부여 */
			marker.setItemName(Name); /* Maker 아이템 이름 설정 */
			marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Latitude, Longitude)); /* Maker 경로 설정 */
			switch(Number) 
			{
				case ("1") : { marker.setMarkerType(MapPOIItem.MarkerType.BluePin); break; } /* 일반 */
				case ("2") : 
				{ 			
					/* Custom Marker 구문 */
					marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); /* Maker 커스텀 설정 */
					marker.setCustomImageResourceId(R.drawable.food_maker); /* 마커 이미지 변경 */ break; 
				} /* 음식점 */
				case ("3") : 
				{ 
					/* Custom Marker 구문 */
					marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); /* Maker 커스텀 설정 */
					marker.setCustomImageResourceId(R.drawable.ldging_maker); /* 마커 이미지 변경 */break; 
				} /* 숙소 */
				case ("4") : 
				{ 
					/* Custom Marker 구문 */
					marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); /* Maker 커스텀 설정 */
					marker.setCustomImageResourceId(R.drawable.oil_marker); /* 마커 이미지 변경 */ break; 
				} /* 주유소 */
			}
			/* Maker 생성 */ mapView.addPOIItem(marker);
	}
	
	@Override
	public void onBackPressed()
	{ finish(); }
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		/* adView가 서버로 부터 광고를 받지 못하였을 경우 */
		if(adView != null) { adView.destroy(); adView = null; }
	}
	
	/* TODO Daum@AdView 설정 관련 함수 */
	private void createAdView()
	{
        /* String */
        final String mString = "DAN-1iyq3whlvsdph";

        /* AdView */
		adView = (AdView) findViewById(R.id.adview);
		
		// 광고 클릭시 실행할 리스너
        adView.setOnAdClickedListener(new OnAdClickedListener() 
        { public void OnAdClicked() { } });

        // 광고 내려받기 실패했을 경우에 실행할 리스너
        adView.setOnAdFailedListener(new OnAdFailedListener() {
            public void OnAdFailed(AdError arg0, String arg1)  { }
        });

        // 광고를 정상적으로 내려받았을 경우에 실행할 리스너
        adView.setOnAdLoadedListener(new OnAdLoadedListener() {
            public void OnAdLoaded() { }
        });

        // 광고를 불러올때 실행할 리스너
        adView.setOnAdWillLoadListener(new OnAdWillLoadListener() {
            public void OnAdWillLoad(String arg1) { }
        });

        // 광고를 닫았을때 실행할 리스너
        adView.setOnAdClosedListener(new OnAdClosedListener() {
            public void OnAdClosed() { }
        });
        
        /* AdView 설정 구문 */
        adView.setClientId(mString); /* AdView 광고 ID 설정 */
        adView.setRequestInterval(15); /* AdView 광고 갱신 시간 설정 */
        adView.setAnimationType(AnimationType.FLIP_HORIZONTAL); /* AdView 애니메이션 효과 설정 */
        adView.setVisibility(View.VISIBLE);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
               
        /* NewtWork Threed 관련 구문 */
        if(android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy); }

        /* 설정 값을 가져오는 구문 */
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); /* 설정 값을 가져오기 위하여 Preferences 객체 생성 */
        String nameActionBar = null; /* 액션바 타이틀을 지정하기 위한 값을 저장하기 위한 스트링 변수 생성 */
        boolean isActionBar = mSharedPreferences.getBoolean("useUserName", false); /* 설정 값에서 설정 된 사용자 이름 사용 구분 가져오는 구문 */
        
        /* 설정 값에서 설정 된 사용자 이름을 가져오는 구문 */
        nameActionBar = isActionBar == true ? mSharedPreferences.getString("userName", "여행일기") : "여행일기";
        
        /* Action Bar 관련 구문 */
        actionBar = getSupportActionBar();
        actionBar.setTitle(nameActionBar); /* ActionBar Title TEXT 설정 */
        actionBar.setSubtitle("여행일기 - 나의위치"); /* ActionBar 소제목 TEXT 설정 */
        actionBar.setDisplayHomeAsUpEnabled(false); /* ActionBar 홈화면 아이콘 생성 설정 */
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff"))); /* ActionBar 색상 변경 */
    	actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff")));

        /* Button */
        final Button[] mButton = {(Button)findViewById(R.id.food_button), (Button)findViewById(R.id.lodging_button), (Button)findViewById(R.id.oli_button), (Button)findViewById(R.id.location_button)};
    	findMapButton(mButton[0], "FD6", "2"); /* Button map 관련 메소드 호출 - 음식점 관련 버튼 */
    	findMapButton(mButton[1], "AD5", "3"); /* Button map 관련 메소드 호출 - 숙소 관련 버튼 */
    	findMapButton(mButton[2], "OL7", "4"); /* Button map 관련 메소드 호출 - 주유소 관련 버튼 */

    	/* 나의 위치 버튼 정의 구문 */
        mButton[3].setOnClickListener(new OnClickListener() {
            @Override /* TODO Auto-generated method stub */
            public void onClick(View v) {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading); /* 트레킹 모드 */
            }
        });

        createAdView(); /* AdView Method */
        createDaumMap(); /* DAUM MAP Method */
        requestGPS(); /* GPS Method */

        createSQLite(); /* SQLite Method */
        selectDB(); /* SQLite DataBase Method */

        Intent intent=getIntent(); /* Intent 객체 생성 */
    	switch(intent.getStringExtra("Select_Mode")) 
    	{
    		/* 주변위치 */
    		case ("Main_Map") : 
    		{
                MapX = intent.getDoubleExtra("MapX", 0.0); /* 위도 관련 변수 */
    			MapY = intent.getDoubleExtra("MapY", 0.0); /* 경도 관련 변수 */
                mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(MapX, MapY), 2, true);
                break;
    		}
    		/* 주변정보 */
    		case ("information_Map") : 
    		{
    			double Latitude = intent.getDoubleExtra("Latitude", 0.0); /* 위도 값을 가져오기 */ 
     			double Longitude = intent.getDoubleExtra("Longitude", 0.0); /* 경도 값을 가져오기 */
     			String Name = intent.getStringExtra("Name"); /* 상호명 값을 가져오기 */
     			/* createDaumMap Point Maker 관련 함수 */ insertBasicMaker(Name, Latitude, Longitude, "1"); 
     			mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Latitude, Longitude), 2, true); break; 
    		}
    		/* 관광정보 */
    		case ("tour_list") : 
    		{
    			double Latitude = intent.getDoubleExtra("Latitude", 0.0); /* 위도 값을 가져오기 */ 
     			double Longitude = intent.getDoubleExtra("Longitude", 0.0); /* 경도 값을 가져오기 */
     			String Name = intent.getStringExtra("Name"); /* 상호명 값을 가져오기 */
     			/* createDaumMap Point Maker 관련 함수 */ insertBasicMaker(Name, Latitude, Longitude, "1"); 
     			mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Latitude, Longitude), 2, true); break; 
    		}
    		/* 상제 페이지 */
    		case ("minutely") :
    		{
    			double Latitude = Double.valueOf(intent.getStringExtra("Latitude")).doubleValue(); /* 위도 값을 가져오기 */ 
     			double Longitude = Double.valueOf(intent.getStringExtra("Longitude")).doubleValue(); /* 경도 값을 가져오기 */ 
     			String Name = intent.getStringExtra("Title"); /* 이름 값을 가져오기 */
     			/* createDaumMap Point Maker 관련 함수 */ insertBasicMaker(Name, Latitude, Longitude, "1"); 
     			mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Latitude, Longitude), 2, true); break; 
    		}
    		/* 주변 위치 페이지*/
    		case ("minutely_zoon") :
    		{		
    			String Mode = intent.getStringExtra("Mode"); /* 음식점 인지 숙소 인지 구별해주는 값 가져오기 */
    			/* 실수형 변수 값 */
    			double Latitude = intent.getDoubleExtra("Latitude", 0.0); /* 위도 값을 가져오기 */ 
     			double Longitude = intent.getDoubleExtra("Longitude", 0.0); /* 경도 값을 가져오기 */
     			/* 배열 변수 값 */
    			ArrayList<String> MapX = intent.getStringArrayListExtra("MapX");
    			ArrayList<String> MapY = intent.getStringArrayListExtra("MapY");
    			ArrayList<String> TITLE = intent.getStringArrayListExtra("TITLE");

    			int length = TITLE.size();
     			for(int count=0; count<length; count++)
     			{
     				if(!TITLE.get(count).equals("\n")) 
     				{
     					String title = TITLE.get(count); /* 상호명 */
     					double x = Double.valueOf(MapX.get(count)).doubleValue(); /* 위도 */
     					double y = Double.valueOf(MapY.get(count)).doubleValue(); /* 경도 */
     					/* Custom Maker를 위한 구문 */
     					switch(Mode) 
     					{
     						case ("음식점") : { /* createDaumMap Point Maker 관련 함수 */ insertBasicMaker(title, x, y, "2");  break; }
     						case ("숙소") : { /* createDaumMap Point Maker 관련 함수 */ insertBasicMaker(title, x, y, "3");  break; }
     					}
     				}
     			}
     			mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Latitude, Longitude), 2, true); break; 
    		}
    	}
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sub4_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        
    	/* Action Bar 관련 구문 */
    	switch (item.getItemId()) {
		case(R.id.route): 
		{
            /* 트래킹 모드가 실행이 되어있는지 검사 */
			if(routeAH.isEmpty() == true) { Toast.makeText(mContext, "트래킹 모드를 먼저 실행을 한 후에 실행해주시기 바랍니다.", Toast.LENGTH_SHORT).show(); }
            else { drawRoutePolyline(); /* 사용자의 위치를 맵에 Polyline으로 그려주는 함수 호출 */ }
			return true;
		}
		case(R.id.zoom_in) : { mapView.zoomIn(true); return true; } /* Daum Zoom in */
		case(R.id.zoom_out) : { mapView.zoomOut(true); return true; } /* Daum Zoom out */
		case(R.id.travel_beach) : /* 해수욕장 */
		{
			mapView.removeAllPOIItems(); /* 지도에 찍힌 모든 마커를 지운다. */
			new DaumParserXML().execute("3", "12", "A01", "A0101", "A01011200", "해수욕장"); /* Daum XML 파싱 관련 함수 호출 */
			return true;
		}
		case(R.id.travel_tree) : /* 수목원 - A01010700 */
		{
			mapView.removeAllPOIItems(); /* 지도에 찍힌 모든 마커를 지운다. */
			new DaumParserXML().execute("3", "12", "A01", "A0101", "A01010700", "수목원"); /* Daum XML 파싱 관련 함수 호출 */
			return true;
		}
		case(R.id.travel_beath) : /* 온천 */
		{
			mapView.removeAllPOIItems(); /* 지도에 찍힌 모든 마커를 지운다. */
			new DaumParserXML().execute("3", "12", "A02", "A0202", "A02020300", "온천"); /* Daum XML 파싱 관련 함수 호출 */
			return true;
		}
		case(R.id.travel_trip) : /* 주변 관광지 */
		{
			mapView.removeAllPOIItems(); /* 지도에 찍힌 모든 마커를 지운다. */
			new Point_trip(mContext, MapX, MapY).execute();
			return true;
		}
		case(R.id.travel_festival) : /* 주변 행사 */
		{
			mapView.removeAllPOIItems(); /* 지도에 찍힌 모든 마커를 지운다. */
			new Point_festival(mContext, MapX, MapY).execute();
			return true;
		}
		default : { return super.onOptionsItemSelected(item); } }      
    }
    
    /* TODO Button map 관련 메소드 */
    private void findMapButton(Button button, final String keyword, final String number) 
    {
    	button.setOnClickListener(new OnClickListener() 
    	{
			@Override /* TODO Auto-generated method stub */
			public void onClick(View v) 
			{
                Vibration.Vibrate(mContext);
				mapView.removeAllPOIItems(); /* 지도 상의 모든 마커 지우기 */
    			selectDB(); /* DB의 자료를 찾아주는 함수 호출 */
    			
				String mapX = Double.toString(MapX); /* 위도 String 형변환 */
				String mapY = Double.toString(MapY); /* 경도 String 형변환 */
				new DaumParserXML().execute("1", mapX, mapY, keyword, number); /* Daum XML 파싱 관련 함수 호출 */
			} 
		});
    }
    
    /* TODO 사용자의 위치를 저장하는 함수 */
    private void saveRoute(Double mapX, Double mapY)
    {
    	HashMap<String, Double> map = new HashMap<String, Double>(); /* HashMap 객체 생성 */
    	map.put("MapX", mapX); /* 위도 값 */
    	map.put("MapY", mapY); /* 경도 값 */
        routeAH.add(map); /* ArrayList 추가 */
    }
    
    /* TODO 사용자의 위치를 맵에 Polyline으로 그려주는 함수 */
	private void drawRoutePolyline()
	{
		mapView.removeAllPolylines(); /* 기존에 사용되었던 폴리라인을 지운다. */
		MapPolyline polyline = new MapPolyline(); /* MapPolyline 객체 생성 */
		polyline.setTag(8278); /* polyline Tag 번호 지정 */
		polyline.setLineColor(Color.argb(128, 255, 51, 0)); /* polyline 색 지정 */
				
		/* polyline 좌표 지정 구문 */
		int length = routeAH.size(); /* ArrayList 길이 저장 */

		for(int count=0; count<length; count++)
		{ polyline.addPoint(MapPoint.mapPointWithGeoCoord(routeAH.get(count).get("MapX"), routeAH.get(count).get("MapY"))); }
		mapView.addPolyline(polyline); /* polyline 지도에 그리기 */
	}
    
	/* TODO GPS 위도와 경도를 찾아주는 함수 */
	private void requestGPS()
    {
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			
			/* GPS의 좌표가 바뀔 경우 호출 되는 함수 */
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		    	double latitude = location.getLatitude(); /* 위도 */
		    	double longitude= location.getLongitude(); /* 경도 */
		    	MapX = latitude;  /* 경도 관련 변수 */
	    		MapY = longitude; /* 위도 관련 변수 */
	    		
	    		/* TextView */
	    		final TextView[] mTextView = {(TextView)findViewById(R.id.point_textview), (TextView)findViewById(R.id.weather_textview)};
	            
	            /* ImageView */
	            final ImageView mImageView = (ImageView)findViewById(R.id.weather_imgview);

	            /* Geocoder */
	    		Geocoder_L mGeocoder = new Geocoder_L(mContext, mTextView[0], 0); /* Geocoder_L 객체 생성 */
	    		mGeocoder.execute(latitude, longitude); /* 현재 접속 장소 조회 */
	    		
	    		/* Weather */
	    		Weather_xml mWeather = new Weather_xml(mContext, mTextView[1], mImageView, "Map"); /* Weather_xml 객체 생성 */
	    		mWeather.execute(latitude, longitude); /* 현재 접속 장소 날씨 조회 */
		   }
		    
		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		    public void onProviderEnabled(String provider) {}
		    public void onProviderDisabled(String provider) {}
		  };

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); }
	
	/* TODO DataBase Select 함수 */
	private void selectDB()
    {
		/* Query */ final String query1 = "SELECT * FROM photo_db";
		DataBase.beginTransaction(); /* 트레젝션 실행 */
		
		try 
		{ 
			/* Cursor 객체 생성 */Cursor point_cursor = DataBase.rawQuery(query1, null);
			/* DB를 조회하여서 Maker 함수 호출을 통해서 구글 지도에 마커를 찍어주는 구문 */
			while(point_cursor.moveToNext()) 
			{ insertDBMaker(point_cursor.getString(1), point_cursor.getString(2), point_cursor.getDouble(4), point_cursor.getDouble(5), point_cursor.getString(6)); }
			/*
			 * point_cursor.getInt(0) = _id
			 * point_cursor.getString(1) = title
			 * point_cursor.getString(2) = point
			 * point_cursor.getFloat(3) = score
			 * point_cursor.getDouble(4) = latitude
			 * point_cursor.getDouble(5) = longitude
			 * point_cursor.getString(6) = path
			 */

			/* 정상적으로 쿼리 문이 수행 되었을 경우 트랜잭션 성공 */
            point_cursor.close();
            DataBase.setTransactionSuccessful();
		}
		catch (SQLiteException ex)
        {
            ex.printStackTrace(); /* SQLite 오류가 발생 시 토스트 기능 출력 */
            Log.e("MAP - SQL", ex.getMessage());
            Toast.makeText(mContext, "SQLite SELECT ERROR", Toast.LENGTH_SHORT).show();
        }
        finally { DataBase.endTransaction(); /* 트랜잭션 종료 */ } 
	}

	/* Server와 통신을 하기 위한 AsyncTask 클래스 */
	private class DaumParserXML extends AsyncTask<String, Void, String[]>
	{
		/* ProgressDialog */
		ProgressDialog mProgressDialog = null; /* ProgressDialog 변수 */
		
		@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
		protected void onPreExecute() 
		{
			/* Dialog 설정 구문 */
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
            mProgressDialog.setMessage("잠시만 기다려 주세요.");
            mProgressDialog.show();
			super.onPreExecute();
		}
		
		@Override /* AsyncTask 실행 부분 */
		protected String[] doInBackground(String... params) 
		{
			try {
				/* Xml pull 파실 객체 생성 */
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser parser = factory.newPullParser();
				
				StringBuffer mStringBuffer = null; /* StringBuffer 객체 생성 */
				
				switch(params[0])
				{
					case ("1") :
					{
						/* StringBuffer 관련 구문 */
						mStringBuffer = new StringBuffer("https://apis.daum.net/local/v1/search/category.xml?location="); /* StringBuffer 객체 생성 */
						mStringBuffer.append(params[1]); mStringBuffer.append(","); mStringBuffer.append(params[2]); /* 위도와 경도 */
						mStringBuffer.append("&code="); mStringBuffer.append(params[3]); mStringBuffer.append("&radius=10000");
						mStringBuffer.append("&apikey="); mStringBuffer.append(API_KEY.Daum_API_KEY); /* Daum API KEY */
						break;
					}
					case ("3") :
					{
						/* StringBuffer 관련 구문 */
						mStringBuffer = new StringBuffer("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey="); /* StringBuffer 객체 생성 */
						mStringBuffer.append(API_KEY.kr_GoDATA_API_KEY); mStringBuffer.append("&contentTypeId="); mStringBuffer.append(params[1]); 
						mStringBuffer.append("&areaCode="); mStringBuffer.append(Value.getAdminAreaCode()); mStringBuffer.append("&sigunguCode="); mStringBuffer.append(Value.getRegionAreaCode());
						mStringBuffer.append("&cat1="); mStringBuffer.append(params[2]); mStringBuffer.append("&cat2="); mStringBuffer.append(params[3]); mStringBuffer.append("&cat3="); mStringBuffer.append(params[4]);
						mStringBuffer.append("&listYN=Y&MobileOS=AND&MobileApp=MyTravel&arrange=A&numOfRows=15&pageNo=1"); 
						break;
					}
				}

					/* 외부 사이트 연결 관련 구문 */
					URL url = new URL(mStringBuffer.toString()); /* URL 객체 생성 */ 
				 	InputStream in = url.openStream(); /* 해당 URL로 연결 */
					parser.setInput(in, "UTF-8"); /* 외부 사이트 데이터와 인코딩 방식을 설정 */		
					
				/* XML 파싱 관련 변수 관련 구문 */
				int eventType = parser.getEventType(); /* 파싱 이벤트  관련 저장 변수 생성 */
				boolean isItemTag = false;
				String tagName = null; /* Tag의 이름을 저장 하는 변수 생성 */

				/* XML 문서를 읽어 들이는 구문 */
				while (eventType != XmlPullParser.END_DOCUMENT) 
				{
					if(eventType == XmlPullParser.START_TAG) 
					{
						tagName = parser.getName();
						switch(params[0]) 
						{
							case ("1") : { if(tagName.equals("channel")) { isItemTag = true; } /* XML channel 시작과 끝부분 */ break; }
							case ("3") : { if(tagName.equals("response")) { isItemTag = true; } /* XML channel 시작과 끝부분 */ break; }
						}
					} else if (eventType == XmlPullParser.TEXT && isItemTag) 
					{
						switch(params[0])
						{
							case ("1") : /* Daum 지역 조회 및 저장 */
							{
								if(tagName.equals("title")) 
								{ Title.add(parser.getText()); /* Log.e("XML - title", parser.getText()); */ } /* 상호명 */
								if(tagName.equals("latitude"))
								{ Latitude.add(parser.getText()); /* Log.e("XML - latitude", parser.getText()); */ } /* 위도 */
								if(tagName.equals("longitude"))
								{ Longitude.add(parser.getText()); /* Log.e("XML - longitude", parser.getText()); */ } /* 경도 */
								break;
							}
							case ("3") :
							{
								if(tagName.equals("addr1")) 
								{ Address.add(parser.getText()); /* Log.e("XML - addr1", parser.getText()); */ } /* 주소 */
								if(tagName.equals("mapx")) 
								{ Longitude.add(parser.getText()); /* Log.e("XML - mapx", parser.getText()); */ } /* 경도 */
								if(tagName.equals("mapy")) 
								{ Latitude.add(parser.getText()); /* Log.e("XML - mapy", parser.getText()); */ } /* 위도 */
								if(tagName.equals("title")) 
								{ Title.add(parser.getText()); /* Log.e("XML - title", parser.getText()); */ } /* 이름 */
								break;
							}
						}
					} 
					else if (eventType == XmlPullParser.END_TAG) 
					{ 
						tagName = parser.getName();
						switch(params[0])
						{ 
							case ("1") : { if(tagName.equals("channel")) { isItemTag = false; } break; }
							case ("3") : { if(tagName.equals("response")) { isItemTag = false; } break; }
						}
					}
					eventType = parser.next(); /* 다음 XML 객체로 이동 */
				}
			} catch (Exception e) { e.printStackTrace(); } return params;
		}
		
		@Override
		protected void onPostExecute(String[] result)
		{
			switch(result[0])
			{
				case ("1") :
				{
					int length = Title.size(); /* ArrayList 사이즈를 저장 하는 변수 생성 */
					/* Daum Map에 마커를 찍어주는 구문 */
					for(int count=0; length>count; count++) 
					{ 
						if(!Title.get(count).equals("\n")) 
						{ 
							String title = Title.get(count); /* 제목 입력 */
							double latitude = Double.valueOf(Latitude.get(count)).doubleValue(); /* 위도 */
							double longitude = Double.valueOf(Longitude.get(count)).doubleValue(); /* 경도 */
							insertBasicMaker(title, latitude, longitude, result[4]); 
						} 
					} 
					/* ArrayList 재사용을 위한 변수 초기화 */
					Title.clear();
					Latitude.clear();
					Longitude.clear();
					break;
				}
				case ("3") :
				{
					int length = Title.size(); /* ArrayList 사이즈를 저장 하는 변수 생성 */
					/* Daum Map에 마커를 찍어주는 구문 */
					for(int count=0; count<length; count++)
					{
						String Name = Title.get(count); /* 상호명 */
						String Point = Address.get(count); /* 주소 */
						String MapX = Latitude.get(count); /* 위도 */
						String MapY = Longitude.get(count); /* 경도 */
						insertTypeMaker(Name, Point, MapX, MapY, result[5]); /* Theme createDaumMap Maker 관련 함수 호출 */
					}
					/* ArrayList 재사용을 위한 변수 초기화 */
					Title.clear(); /* 상호명 */
					Address.clear(); /* 주소 */
					Latitude.clear(); /* 위도 */
					Longitude.clear(); /* 경도 */
					break;
				}
			}

            mProgressDialog.dismiss(); /* Dialog 종료 부분 */
			super.onPostExecute(result);
		}
	}
	
	/**************************************************************************** DAUM 관련 메소트 모음 *******************************************************************************************************/
	
	@Override /* TODO Auto-generated method stub */
	public void onMapViewCenterPointMoved(MapView arg0, MapPoint arg1) 
	{ }

	@Override /* TODO Auto-generated method stub */
	public void onMapViewDoubleTapped(MapView arg0, MapPoint arg1) 
	{ /* 사용자가 지도 위 한 지점을 더블 터치한 경우 호출 */ mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff); /* 두번 터지 한 경우 트레킹 모드가 종료 */ }
	
	@Override
	public void onMapViewDragEnded(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override /* Daum Map의 중심점을 옮겨 주는 함수 */
	public void onMapViewDragStarted(MapView arg0, MapPoint arg1) 
	{ actionBar.hide(); /* ActionBar 숨김 */ }

	@Override /* TODO Auto-generated method stub */
	public void onMapViewInitialized(MapView arg0) {  }

	@Override
	public void onMapViewLongPressed(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override /* 사용자가 지도를 드레그 종료하였을 경우 */
	public void onMapViewMoveFinished(MapView arg0, MapPoint arg1) 
	{ actionBar.show(); /* ActionBar 표시 */ }

	@Override /* 사용자가 지도를 터치 하였을 경우 호출 되는 함수 */
	public void onMapViewSingleTapped(MapView arg0, MapPoint arg1) 
	{}

	@Override
	public void onMapViewZoomLevelChanged(MapView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override /* Maker의 대화상자를 선택 하였을 경우 출력되는 함수 */
	@Deprecated /* TODO Auto-generated method stub */
	public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1) 
	{
		final String Maker_Name=arg1.getItemName(); /* OnClick 이벤트 리스너를 사용하기 위한 final 변수 생성 */
		/* Maker 선택  이벤트 구현 구문 */
		switch(arg1.getTag()) 
		{
			case (3) : 
			{
				final String[] items = {"자동차 길찾기", "대중교통 길찾기", "도보 길찾기"}; /* Dialog 메뉴 이름 설정 */
				final double lat = arg1.getMapPoint().getMapPointGeoCoord().latitude; /* 해당 마커가 찍힌 곳에 위도를 저장 */
				final double log = arg1.getMapPoint().getMapPointGeoCoord().longitude; /* 해당 마커가 찍힌 곳에 경도를 저장 */
				
				AlertDialog.Builder alert = new AlertDialog.Builder(this); /* AlertDialog 객체 생성 */
				alert.setTitle(Maker_Name); /* alert 타이틀 설정 */
				alert.setItems(items, new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) 
					{
						/* StringBuffer 객체 생성 */
						StringBuffer result = new StringBuffer("daummaps://route?sp=");
						result.append(MapX); result.append(","); result.append(MapY); /* 시작점 */
						result.append("&ep="); result.append(lat); result.append(","); result.append(log); /* 도착점 */
							/* 해당 메뉴에 맞는 길찾기를 도와주는 구문 */
							switch(items[which]) 
							{
								case ("자동차 길찾기") : { result.append("&by=CAR"); break; } /* 자동차 길찾기 */
								case ("대중교통 길찾기") : { result.append("&by=PUBLICTRANSIT"); break; } /* 대중교통 길찾기 */
								case ("도보 길찾기") : { result.append("&by=FOOT"); break; } /* 도보 길찾기 */
							}
							/* 화면 전환 인텐트 관련 구문 */
							Uri uri = Uri.parse(result.toString()); /* Uri 객체 생성 및  스키마 값 저장 */
							Intent intent = new Intent(); /* 인텐트 객체 생성 */
							intent.setAction(Intent.ACTION_VIEW); 
							intent.setData(uri);
							/* 해당 다음 지도 어플이 설치 유무 확인 구문 */
							if(intent.resolveActivity(getPackageManager()) != null) { startActivity(intent); }
							else { Toast.makeText(mContext, "Daum Map이 설치 되어 있지 않습니다.", Toast.LENGTH_SHORT).show(); }
					} }); alert.show(); break;
			}
			case (2) : 
			{
				final String[] items = {"자동차 길찾기", "대중교통 길찾기", "도보 길찾기"}; /* Dialog 메뉴 이름 설정 */
				final double lat = arg1.getMapPoint().getMapPointGeoCoord().latitude; /* 해당 마커가 찍힌 곳에 위도를 저장 */
				final double log = arg1.getMapPoint().getMapPointGeoCoord().longitude; /* 해당 마커가 찍힌 곳에 경도를 저장 */
				
				AlertDialog.Builder alert = new AlertDialog.Builder(this); /* AlertDialog 객체 생성 */
				alert.setTitle(Maker_Name); /* alert 타이틀 설정 */
				alert.setItems(items, new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) 
					{
						/* StringBuffer 객체 생성 */
						StringBuffer result = new StringBuffer("daummaps://route?sp=");
						result.append(MapX); result.append(","); result.append(MapY); /* 시작점 */
						result.append("&ep="); result.append(lat); result.append(","); result.append(log); /* 도착점 */
						
						/* Boolean 변수 생성 */
							/* 해당 메뉴에 맞는 길찾기를 도와주는 구문 */
							switch(items[which]) 
							{
								case ("자동차 길찾기") : { result.append("&by=CAR"); break; } /* 자동차 길찾기 */
								case ("대중교통 길찾기") : { result.append("&by=PUBLICTRANSIT"); break; } /* 대중교통 길찾기 */
								case ("도보 길찾기") : { result.append("&by=FOOT"); break; } /* 도보 길찾기 */
							}
								/* 화면 전환 인텐트 관련 구문 */
								Uri uri = Uri.parse(result.toString()); /* Uri 객체 생성 및  스키마 값 저장 */
								Intent intent = new Intent(); /* 인텐트 객체 생성 */
								intent.setAction(Intent.ACTION_VIEW); 
								intent.setData(uri);
								/* 해당 다음 지도 어플이 설치 유무 확인 구문 */
								if(intent.resolveActivity(getPackageManager()) != null) { startActivity(intent); }
								else { Toast.makeText(mContext, "Daum Map이 설치 되어 있지 않습니다.", Toast.LENGTH_SHORT).show(); } 
						} }); alert.show(); break;
			}
			case (1) : 
			{
				AlertDialog.Builder bulider = new AlertDialog.Builder(this); /* Alert 객체 생성 */
				bulider.setTitle("해당 이미지 파일로 이동하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {		
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which)
                    {
						Vibration.Vibrate(mContext);

						/* Intent 관련 구문 */
						Intent intent = new Intent(mContext,SubActivity6_gallery.class); /* 인텐트 객체 생성 */
						intent.putExtra("select", "map"); /* 인텐트에 결과 값을 전달(선택) */
	    				intent.putExtra("Maker_id", Maker_Name); /* 인텐트에 결과 값을 전달(마커이름)  */
	    				startActivity(intent); /* 인텐트 시작 */
					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() { /* 취소 버튼 관련 구문 */
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
				}).show(); break;
			}
		}
	}

	@Override
	public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1,
			CalloutBalloonButtonType arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDraggablePOIItemMoved(MapView arg0, MapPOIItem arg1,
			MapPoint arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override /* TODO Auto-generated method stub */ 
	public void onPOIItemSelected(MapView arg0, MapPOIItem arg1) {

	}

	@Override
	public void onCurrentLocationDeviceHeadingUpdate(MapView arg0, float arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override /* 트레킹 모드를 통해서 단말의 현위치 좌표값을 통보받을 수 있는 함수 */
	public void onCurrentLocationUpdate(MapView arg0, MapPoint arg1, float arg2) 
	{ saveRoute(arg1.getMapPointGeoCoord().latitude, arg1.getMapPointGeoCoord().longitude); /* 사용자의 위치를 저장하는 함수 */ }

	@Override
	public void onCurrentLocationUpdateCancelled(MapView arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCurrentLocationUpdateFailed(MapView arg0) {
		// TODO Auto-generated method stub
		
	}
}