package kr.example.mytravelnote;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import kr.example.mytravelnote.apikey.API_KEY;
import kr.example.mytravelnote.common.DownloadImage;
import kr.example.mytravelnote.common.Image_loder;
import kr.example.mytravelnote.sns.kakaotalk;
import kr.net.mytravelnote.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SubActivity9_minutely extends ActionBarActivity {

	            /* ArrayList */
    /* 음식점 */
    private ArrayList<String> nameFA = null;
	private ArrayList<String> mapXFA = null;
	private ArrayList<String> mapYFA = null;
    /* 숙소 */
	private ArrayList<String> nameLA = null;
	private ArrayList<String> mapXLA = null;
	private ArrayList<String> mapYLA = null;

    /* String */
	private String contentsID = null; /* Tour에서 받은 해당 관광지 ID를 저장 시키는 변수 생성 */
    private String areaName = null; /* 지역 명을 저장하는 변수 생성 */

    /* INT */
    private int typeID = 0; /* Tour에서 받은 해당 관관지 타입을 저장 시키는 변수 생성  */
    
	/* APP NAME */
	private final String APP_NAME = "MyTravel";
	
	/* Context */
	private final Context mContext = SubActivity9_minutely.this;

    /* String[] */
    private String[] informationC = {null, null, null, null, null, null, null};
    /*
        1. informationC[0] 관광지 주소 1
        2. informationC[1] 관광지 주소 2
        3. informationC[2] 관광지명
        4. informationC[3] 관광지 이미지
        5. informationC[4] 관광지 상세정보
        6. informationC[5] 관광지 경도
        7. informationC[6] 관광지 위도
    */
    private String[] informationD = {null, null, null, null, null, null};
	/*
	    1. informationD[0] 문의 및 안내
	    2. informationD[1] 휴무일
	    3. informationD[2] 유모차 대여
	    4. informationD[3] 애완동물
	    5. informationD[4] 주차장
	    6. informationD[5] 이용시간
	*/

	/* TextView */
	private TextView[] mTextView = new TextView[8];
	
	/* ImageView */
	private ImageView[] mImageView = new ImageView[4];
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_minutely);

        /* Intent */
        Intent mIntent = getIntent(); /* 인텐트 객체 생성 */
        contentsID = mIntent.getStringExtra("Contentid"); /* 해당 관광지 ID 를 가져오는 구문 */
        typeID = mIntent.getIntExtra("Type_code", 0); /* 해당 관광지 타입 ID를 가져오는 구문 */
        areaName = mIntent.getStringExtra("areaName"); /* 지역 명을 가져오는 구문 */
        informationC[2] = mIntent.getStringExtra("Title"); /* 타이틀 정보 */

        /* StringBuffer */
        StringBuffer mStringBuffer = new StringBuffer(); /* StringBuffer 객체 생성 */
        mStringBuffer.append(areaName); mStringBuffer.append(" 관광 정보");
        
        /* ActionBar 관련 구문 */
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setTitle("관광지 상세 정보"); /* 액션바 타이틀 설정 */
        mActionBar.setSubtitle(informationC[2]); /* 액션바 서브 타이틀 설정 */
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff"))); /* ActionBar 색상 변경 */
        mActionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff")));

        initialization();
    	
    	/* KrParserXML AsyncTask */
        KrParserXML[] mKrData = {new KrParserXML(), new KrParserXML()};
    	mKrData[0].execute("1"); /* 해당 관광지 ID를 통해서 XML에 공통정보를 조회하는 함수 호출 */
    	mKrData[1].execute("2"); /* 해당 관광지 ID를 통해서 XML에 상세정보를 조회하는 함수 호출 */

        /* DaumParserXML AsyncTask */
        DaumParserXML[] mDaum = {new DaumParserXML("FD6", 1), new DaumParserXML("AD5", 2)};
    	mDaum[0].execute(); /* 관광지 주변 음식점 조회 */
    	mDaum[1].execute(); /* 관광지 주변 숙소 조회 */
		
		/* 장소 관련 이미지 뷰 정의 */
		mImageView[1].setOnClickListener(new OnClickListener()
        {
			@Override /* TODO Auto-generated method stub */
			public void onClick(View v) 
			{ 
				AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext); /* Alert 객체 생성 */
                mAlertDialog.setTitle(informationC[2]).setMessage("해당 위치로 이동 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener()
                {
					@Override /* Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which)
                    {
						if( (informationC[6]!=null) && (informationC[5]!=null) )
                        {
                            /* Intent */
							Intent mIntent = new Intent(mContext, SubActivity4_map.class);
                            mIntent.putExtra("Select_Mode", "minutely"); /* 주변정보와 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
                            mIntent.putExtra("Latitude", informationC[6]); /* 위도 인텐트 값 전달 */
                            mIntent.putExtra("Longitude", informationC[5]); /* 경도 인텐트 값 전달 */
                            mIntent.putExtra("Title", informationC[2]); /* 이름 값 전달 */
                            startActivity(mIntent);
                        }
						else { Toast.makeText(mContext, "해당 위치가 존재 하지 않아 지도에 표시 할 수가 없습니다.", Toast.LENGTH_SHORT).show(); }
					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) { dialog.cancel(); } }).show();				
			} });
		
		/* 음식점 장소 관련 이미지 뷰 정의 */
		mImageView[2].setOnClickListener(new OnClickListener()
        {
			@Override /* TODO Auto-generated method stub */
			public void onClick(View v) 
			{
				AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext); /* Alert 객체 생성 */
                mAlertDialog.setTitle(informationC[2]).setMessage("주변 음식점 위치로 이동 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override /* Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) {
						if( (informationC[6]!=null) && (informationC[5]!=null) )
                        {
                            /* Intent */
							Intent mIntent = new Intent(mContext, SubActivity4_map.class);
                            mIntent.putExtra("Select_Mode", "minutely_zoon"); /* 주변정보와 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
                            mIntent.putExtra("Mode", "음식점"); /* 음식점과 숙소를 구별 하는 값 전달 */
                            mIntent.putExtra("Latitude", Double.valueOf(informationC[6]).doubleValue()); /* 위도 인텐트 값 전달 */
                            mIntent.putExtra("Longitude", Double.valueOf(informationC[5]).doubleValue()); /* 경도 인텐트 값 전달 */
			    			/* ArrayList Transfer */
                            mIntent.putStringArrayListExtra("MapX", mapXFA);
                            mIntent.putStringArrayListExtra("MapY", mapYFA);
                            mIntent.putStringArrayListExtra("TITLE", nameFA);
			    			startActivity(mIntent);
                        }
						else { Toast.makeText(mContext, "해당 위치가 존재 하지 않아 지도에 표시 할 수가 없습니다.", Toast.LENGTH_SHORT).show(); }
					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) { dialog.cancel(); } }).show();
			} 
		});
		
		/* 숙소 장소 관련 이미지 뷰 정의 */
		mImageView[3].setOnClickListener(new OnClickListener()
        {
			@Override /* TODO Auto-generated method stub */
			public void onClick(View v) 
			{
				AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext); /* Alert 객체 생성 */
                mAlertDialog.setTitle(informationC[2]).setMessage("주변 숙박시설 위치로 이동 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener()
                {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which)
                    {
						if( (informationC[6]!=null) && (informationC[5]!=null) )
                        {
                            /* Intent */
							Intent mIntent = new Intent(mContext, SubActivity4_map.class);
                            mIntent.putExtra("Select_Mode", "minutely_zoon"); /* 주변정보와 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
                            mIntent.putExtra("Mode", "숙소"); /* 음식점과 숙소를 구별 하는 값 전달 */
                            mIntent.putExtra("Latitude", Double.valueOf(informationC[6]).doubleValue()); /* 위도 인텐트 값 전달 */
                            mIntent.putExtra("Longitude", Double.valueOf(informationC[5]).doubleValue()); /* 경도 인텐트 값 전달 */
			    			/* ArrayList Transfer */
                            mIntent.putStringArrayListExtra("MapX", mapXLA);
                            mIntent.putStringArrayListExtra("MapY", mapYLA);
                            mIntent.putStringArrayListExtra("TITLE", nameLA);
			    			startActivity(mIntent);
                        }
						else { Toast.makeText(mContext, "해당 위치가 존재 하지 않아 지도에 표시 할 수가 없습니다.", Toast.LENGTH_SHORT).show(); }
					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) { dialog.cancel(); } }).show();
			} 
		});
	}
	
	/* TODO 초기 설정 메소드 */
	private void initialization()
	{
		/* TextView 관련 객체 생성 구문 */
    	mTextView[0] = (TextView)findViewById(R.id.minutely_title_textview); /* minutely_title_textview 객체 생성 - 광광지 이름 */
    	mTextView[1] = (TextView)findViewById(R.id.minutely_point_textview); /* minutely_point_textview 객체 생성 - 장소 */
    	mTextView[2] = (TextView)findViewById(R.id.minutely_offday_add); /* minutely_offday_add 객체 생성 - 휴무일 */
    	mTextView[3] = (TextView)findViewById(R.id.minutely_information_add); /* minutely_information_add 객체 생성 - 문의 밎 안내 */
    	mTextView[4] = (TextView)findViewById(R.id.minutely_parking_add); /* minutely_informationD[4]_add 객체 생성 - 주차 시설 */
    	mTextView[5] = (TextView)findViewById(R.id.minutely_pat_baby_add); /* minutely_pat_baby_add 객체 생성 - 아기와 애완동물 여부 */
    	mTextView[6] = (TextView)findViewById(R.id.minutely_usetime_add); /* minutely_informationD[5]_add 객체 생성 - 이용시간 */
    	mTextView[7] = (TextView)findViewById(R.id.minutely_overview); /* minutely_overview 객체 생성 - 상세설명 */
		
		/* ImageView 관련 객체 생성 구문 */
    	mImageView[0] = (ImageView)findViewById(R.id.minutely_imageview); /* minutely_imageview 객체 생성 - 대표 이미지 */
    	mImageView[1] = (ImageView)findViewById(R.id.minutely_point_imageview); /* minutely_point_imageview 객체 생성 - 해당 장소 지도 이미지 */
    	mImageView[2] = (ImageView)findViewById(R.id.minutely_foodzoon); /* minutely_foodzoon 객체 생성 - 해당 장소 음식점 위지 지도 이미지 */
    	mImageView[3] = (ImageView)findViewById(R.id.minutely_bad); /* minutely_bad 객체 생성 - 해당 장소 숙소 위치 지도 이미지 */
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sub9_minutely, menu);
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public void onBackPressed()
	{ finish(); }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

    	/* Action Bar 관련 구문 */
    	switch (item.getItemId())
        {
    		case(R.id.facebook) : /* 페이스북 */
    		{
                /* Intent */
    			Intent mIntent = new Intent(mContext, SubActivity10_facebook.class);
                mIntent.putExtra("facebook_minutely", "tour_minutely_mode");
                mIntent.putExtra("facebook_title", informationC[2]); /* 제목 */
                mIntent.putExtra("facebook_address", informationC[0]); /* 주소 */
                mIntent.putExtra("facebook_image", informationC[3]); /* 관광지 사진 */
                mIntent.putExtra("facebook_overview", informationC[4]); /* 휴무일 */
    			startActivity(mIntent); return true;
			}
    		case(R.id.kakaotalk) : /* 카카오톡 */
    		{ 
    			kakaotalk mKakao = new kakaotalk(mContext, informationC[2], informationC[3], informationC[0], informationD[0], informationD[1], informationD[4], informationD[5], informationD[4]);
                mKakao.Festival_Kakao_Talk_write(1); /* 관광지 정보 카카오톡으로 정송 */
    			return true; 
    		}
    		case(R.id.save_icon) : /* 로컬 파일 저장 */
    		{
                /* StringBuffer */
    			StringBuffer mStringBuffer = new StringBuffer(); /* StringBuffer 객체 생성 */

                /* String */
    			String mString = Environment.getExternalStorageState(); /* 외장 메모리 상태 저장 */

                /* 외부 저장소가 연결되어 있는지 확인하는 구문 */
    			if(mString.equals(Environment.MEDIA_MOUNTED))
                { mStringBuffer.append(Environment.getExternalStorageDirectory().getAbsolutePath()); mStringBuffer.append("/MyTravel/Minutely/"); } /* 외장 메모리가 존재 하는 경우 외장 메모리 경로 저장 */
    			else { mStringBuffer.append(Environment.MEDIA_UNMOUNTED); mStringBuffer.append("/MyTravel/Minutely/"); } /* 외장 메모리가 없는 경우 내장 메모리 경로 저장 */

                ImageSave mSave = new ImageSave(mStringBuffer.toString()); /* save_Image 객체 생성 */
    			mSave.execute();
    			return true; 
    		}
    		default : { return super.onOptionsItemSelected(item); } } 
	}
	
	/* TODO XML NULL DATA Select 함수 */
	private void findInsertNull()
    {
		/* 공통 정보 관련 내용 */
		if(informationC[1]==null) { informationC[1]=""; } /* 주소 2 */
		if(informationC[3]==null) { informationC[3]="http://api.visitkorea.or.kr/static/images/common/noImage.gif"; } /* 대표 이미지 */
		/* 상세 정보 관련 내용 */
		if(informationD[2]==null) { informationD[2]="없음"; } /* 애완동물 여부 */
		if(informationD[3]==null) { informationD[3]="불가"; } /* 애완동물 여부 */
		if(informationD[0]==null) { informationD[0]="정보 없음"; } /* 안내 및 문의 */
		if(informationD[4]==null) { informationD[4]="정보 없음"; } /* 주차시설 */
		if(informationD[1]==null) { informationD[1]="연중 무휴"; } /* 쉬는날 */
		if(informationD[5]==null) { informationD[5]="24시간"; } /* 이용시간 */
	}
	
	/* TODO XML AsyncTask 클래스 */
	class KrParserXML extends AsyncTask<String, Void, String>
	{
		/* ProgressDialog */
		private ProgressDialog mProgressDialog = null;
		
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
		protected String doInBackground(String... params) 
		{
			try
			{
				/* Xml pull 파실 객체 생성 */
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser parser = factory.newPullParser();

                /* StringBuffer */
				StringBuffer mStringBuffer = null; /* StringBuffer 객체 생성 */
				switch(params[0])
				{
					case ("1") : /* 공통정보 */
					{
						/* StringBuffer 관련 구문 */
                        mStringBuffer = new StringBuffer("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey="); /* StringBuffer 객체 생성 */
						/* API KEY */ mStringBuffer.append(API_KEY.kr_GoDATA_API_KEY); mStringBuffer.append("&contentTypeId="); mStringBuffer.append(typeID); /* 관광타입 코드  ID 삽입 */
                        mStringBuffer.append("&contentId="); mStringBuffer.append(contentsID); mStringBuffer.append("&MobileOS=AND&MobileApp="); mStringBuffer.append(APP_NAME);
                        mStringBuffer.append("&defaultYN=Y&firstImageYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y");
						break;
					}
					case ("2") : /* 상제정보 */
					{
						/* StringBuffer 관련 구문 */
                        mStringBuffer = new StringBuffer("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey="); /* StringBuffer 객체 생성 */
						/* API KEY */ mStringBuffer.append(API_KEY.kr_GoDATA_API_KEY); mStringBuffer.append("&contentTypeId="); mStringBuffer.append(typeID); /* 관광타입 코드  ID 삽입 */
                        mStringBuffer.append("&contentId="); mStringBuffer.append(contentsID); mStringBuffer.append("&MobileOS=AND&MobileApp="); mStringBuffer.append(APP_NAME); mStringBuffer.append("&introYN=Y");
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
						if(tagName.equals("response")) { isItemTag = true; } /* XML channel 시작과 끝부분 */	
					} 
					else if (eventType == XmlPullParser.TEXT && isItemTag) 
					{
						switch(params[0])
						{
							case ("1") : 
							{
								if(tagName.equals("addr1")) 
								{ informationC[0] = parser.getText(); /* Log.e("XML - Address_1", parser.getText()); */ } /* 주소1 */
								if(tagName.equals("addr2")) 
								{ informationC[1] = parser.getText(); /* Log.e("XML - Address_2", parser.getText()); */ } /* 주소2 */
								if(tagName.equals("firstimage")) 
								{ informationC[3] = parser.getText(); /* Log.e("XML - Image", parser.getText()); */ } /* Image URL */
								if(tagName.equals("mapx")) 
								{ informationC[5] = parser.getText(); /* Log.e("XML - mapx", parser.getText()); */ } /* mapx */
								if(tagName.equals("mapy")) 
								{ informationC[6] = parser.getText(); /* Log.e("XML - mapy", parser.getText()); */ } /* mapy */
								if(tagName.equals("overview")) 
								{ informationC[4] = parser.getText(); /* Log.e("XML - Overview", parser.getText()); */ } /* Overview */
								break;
							}
							case ("2") :
							{
								if(tagName.equals("chkinformationD[2]riage")) 
								{ informationD[2] = parser.getText(); /* Log.e("XML - chkinformationD[2]riage", parser.getText()); */ } /* 유모차 대여 여부 */
								if(tagName.equals("informationD[3]")) 
								{ informationD[3] = parser.getText(); /* Log.e("XML - informationD[3]", parser.getText()); */ } /* 애완동물 여부 */
								if(tagName.equals("infocenter")) 
								{ informationD[0] = parser.getText(); /* Log.e("XML - Information", parser.getText()); */ } /* 문의 및 안내 */
								if(tagName.equals("informationD[4]")) 
								{ informationD[4] = parser.getText(); /* Log.e("XML - informationD[4]", parser.getText()); */ } /* 주차장 여부 */
								if(tagName.equals("informationD[1]")) 
								{ informationD[1] = parser.getText(); /* Log.e("XML - informationD[1]", parser.getText()); */ } /* 휴무일 */
								if(tagName.equals("informationD[5]")) 
								{ informationD[5] = parser.getText(); /* Log.e("XML - informationD[5]", parser.getText()); */ } /* 이용시간 */
								break;
							}
						}
					} 
					else if (eventType == XmlPullParser.END_TAG) 
					{ 
						tagName = parser.getName();
						if(tagName.equals("response")) { isItemTag = false; }
					}
					eventType = parser.next(); /* 다음 XML 객체로 이동 */
				}
			} catch (Exception e) { e.printStackTrace(); }
			return params[0];
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			switch(result)
			{
				case ("1") : 
				{
                    findInsertNull(); /* XML에 받은 데이터 중에 NULL 값이 있을 경우 다른 값으로 대처 하는 함수 호출 */

                    /* 지도 이미지를 불러오는 구문 */
                    StringBuffer mStringBuffer = new StringBuffer("http://maps.googleapis.com/maps/api/staticmap?center=");
                    mStringBuffer.append(informationC[6] + "," + informationC[5]); /* 카메라 이동 위도와 경도 */
                    mStringBuffer.append("&zoom=18&size=640x640&scale=2&maptype=roadmap&");
                    mStringBuffer.append("markers=color:blue|label:N|");  mStringBuffer.append(informationC[6] + "," + informationC[5]); /* 마커 설정 위도와 경도 */
                    mStringBuffer.append("%7C11211&sensor=false"); mStringBuffer.append("&key=");
                    mStringBuffer.append(API_KEY.Google_Static_Key); /* Google API Key */

                    /* IMAGE DOWNLOAD */
                    DownloadImage.image(mImageView[0], informationC[3], mContext);
                    DownloadImage.image(mImageView[1], mStringBuffer.toString(), mContext);
                    break;
				}
				case ("2") : 
				{  
					/* StringBuffer 설정 구문 */
					StringBuffer[] mStrBuf = {null, null};
					
					/* mStrBuf[0] - 관광지 주소 */
					mStrBuf[0] = new StringBuffer(); /* 주소 관련 StringBuffer */
					mStrBuf[0].append(informationC[0]); mStrBuf[0].append(informationC[1]);
					/* mStrBuf[1] - 유모차 및 애완견 관련 정보 */
					mStrBuf[1] = new StringBuffer(); /* 유모차 및 애완견 관련 정보 */
					mStrBuf[1].append(informationD[3]); mStrBuf[1].append(" / "); mStrBuf[1].append(informationD[2]);
					
					/* TextView 관련 설정 구문 */
					mTextView[0].setText(informationC[2]); /* 관광지 이름 설정 */
					mTextView[1].setText(mStrBuf[0].toString()); /* 장소 설정 */
					mTextView[2].setText(Html.fromHtml(informationD[1])); /* 휴무일 설정 */
					mTextView[3].setText(Html.fromHtml(informationD[0])); /* 문의 및 안내 */
					mTextView[4].setText(Html.fromHtml(informationD[4])); /* 주차 관련 설정 */
					mTextView[5].setText(Html.fromHtml(mStrBuf[1].toString())); /* 아기와 애완동물 설정 */
					mTextView[6].setText(Html.fromHtml(informationD[5])); /* 이용시간 */
					mTextView[7].setText(Html.fromHtml(informationC[4])); /* 상세 설명 설정 */
					break;
				}
			}
            mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
	}
	
	/* TODO Daum 주변 정보 관련 정보 */
	private class DaumParserXML extends AsyncTask<Void, Void, Void>
	{
		/* ProgressDialog */
		private ProgressDialog mProgressDialog = null;
		
		/* String */
		private String mString = null;
		/* int */
		private int mInt = 0;
		
		/* 생성자 메소드 */
        private DaumParserXML(String mString, int mInt)
		{
			this.mString = mString;
			this.mInt = mInt;
		}

		@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
		protected void onPreExecute() 
		{
			/* Dialog 설정 구문 */
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
            mProgressDialog.setMessage("잠시만 기다려 주세요.");
            mProgressDialog.show();
			
			/* ArrayList */
			nameFA = new ArrayList<String>(10);
			mapXFA = new ArrayList<String>(10);
			mapYFA = new ArrayList<String>(10);
			
			nameLA = new ArrayList<String>(10);
			mapXLA = new ArrayList<String>(10);
			mapYLA = new ArrayList<String>(10);
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) 
		{
			try {
				/* Xml pull 파실 객체 생성 */
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser parser = factory.newPullParser();
			
				/* StringBuffer */
				StringBuffer mStringBuffer = new StringBuffer("https://apis.daum.net/local/v1/search/category.xml?location="); /* StringBuffer 객체 생성 */
                mStringBuffer.append(informationC[6]); mStringBuffer.append(","); mStringBuffer.append(informationC[5]); /* 위도와 경도 */
                mStringBuffer.append("&code="); mStringBuffer.append(mString); mStringBuffer.append("&redius=1000");
                mStringBuffer.append("&apikey="); mStringBuffer.append(API_KEY.Daum_API_KEY); /* Daum API KEY */
						
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
					if(tagName.equals("channel")) { isItemTag = true; } /* XML channel 시작과 끝부분 */
				} else if (eventType == XmlPullParser.TEXT && isItemTag) 
				{
					switch(mInt) 
					{
						case (1) :
						{
							if(tagName.equals("title"))
							{ nameFA.add(parser.getText()); /* Log.e("XML - title", parser.getText()); */ } /* 이름 */
							if(tagName.equals("latitude"))
							{ mapXFA.add(parser.getText()); /* Log.e("XML - latitude", parser.getText()); */ } /* 위도 */
							if(tagName.equals("longitude"))
							{ mapYFA.add(parser.getText()); /* Log.e("XML - longitude", parser.getText()); */ } /* 경도 */
							break;
						}
						case (2) :
						{
							if(tagName.equals("title"))
							{ nameLA.add(parser.getText()); /* Log.e("XML - title", parser.getText()); */ } /* 이름 */
							if(tagName.equals("latitude"))
							{ mapXLA.add(parser.getText()); /* Log.e("XML - latitude", parser.getText()); */ } /* 위도 */
							if(tagName.equals("longitude"))
							{ mapYLA.add(parser.getText()); /* Log.e("XML - longitude", parser.getText()); */ } /* 경도 */
							break;
						}
					}
				} else if (eventType == XmlPullParser.END_TAG) { tagName = parser.getName(); if(tagName.equals("channel")) { isItemTag = false; } }
				eventType = parser.next(); /* 다음 XML 객체로 이동 */
			}
			} catch (Exception e) { Log.e("Daum XML", e.getMessage()); }
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			/* StringBuffer */
			StringBuffer mStringBuffer = new StringBuffer("http://maps.googleapis.com/maps/api/staticmap?center=");
            mStringBuffer.append(informationC[6] + "," + informationC[5]); /* 카메라 이동 위도와 경도 */
            mStringBuffer.append("&zoom=16&size=320x320&scale=2&maptype=roadmap&");
			
			switch(mInt)
			{
				case(1) :
				{
					int length = nameFA.size(); /* ArrayList 변수 사이즈 저장 변수 생성 */
					for(int count=0; count<length; count++)
					{ 
						String mapX = mapXFA.get(count); /* 위도 */
						String mapY = mapYFA.get(count); /* 경도 */

						if( (!mapX.equals("\n")) && (!mapY.equals("\n")) )
                        {
                            mStringBuffer.append("markers=color:red|label:F|");
                            mStringBuffer.append(mapX + "," + mapY); mStringBuffer.append("&"); /* 마커 설정 위도와 경도 */
                        }
					}
                    mStringBuffer.append("%7C11211&sensor=false"); mStringBuffer.append("&key=");
                    mStringBuffer.append(API_KEY.Google_Static_Key); /* Google API Key */

                    /* IMAGE DOWNLOAD */
                    DownloadImage.image(mImageView[2], mStringBuffer.toString(), mContext);
                    break;
				}
				case(2) :
				{
					int length = nameLA.size(); /* ArrayList 변수 사이즈 저장 변수 생성 */
					for(int count=0; count<length; count++)
					{ 
						String mapX = mapXLA.get(count); /* 위도 */
						String mapY = mapYLA.get(count); /* 경도 */

						if( (!mapX.equals("\n")) && (!mapY.equals("\n")) )
                        {
                            mStringBuffer.append("markers=color:blue|label:L|");
                            mStringBuffer.append(mapX + "," + mapY); mStringBuffer.append("&"); /* 마커 설정 위도와 경도 */
                        }
					}
                    mStringBuffer.append("%7C11211&sensor=false"); mStringBuffer.append("&key=");
                    mStringBuffer.append(API_KEY.Google_Static_Key); /* Google API Key */

                    /* IMAGE DOWNLOAD */
                    DownloadImage.image(mImageView[3], mStringBuffer.toString(), mContext);
                    break;
				}
			}
            mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
		
	}

	/* TODO Save_Image 클래스 */
        private class ImageSave extends AsyncTask<Void, Void, Void>
        {
            /* ProgressDialog */
            private ProgressDialog mProgressDialog = null;

            /* String */
            private String[] mString = {null,null};

            /* 생성자 메소드 */
        ImageSave(String mString)
		{ this.mString[0] = mString; }

		 /* File의 이름을 시간과 사용자가 입력 한 결과를 받아서 조합 해주는 함수 */
		@SuppressWarnings("static-access")
		private String createName()
        {
			final Calendar mCalender = Calendar.getInstance(); /* 캘린더 객체 생성 */
			
			/* Calendar 변수 */
			int YEAR = mCalender.get(mCalender.YEAR); /* 년 */
			int MONTH = mCalender.get(mCalender.MONTH); /* 월 */
			int DAY = mCalender.get(mCalender.DAY_OF_MONTH); /* 일 */

			/* 문자열 합치기 */
			String mString = String.format("%d년 %d월 %d일 %s.JPG", YEAR, MONTH, DAY, informationC[2]);
			return mString; /* 리턴 값 전달 */
        }
		
		/* 2개 이상의 Bitmap을 합쳐 주는 함수  */
		@SuppressWarnings("deprecation")
		private Bitmap combineImage(Bitmap first, Bitmap secod, boolean isVerticalMode)
        {
			Options option = new Options();
			option.inDither  = true;
			option.inPurgeable = true;
			
			Bitmap bitmap = null;
			if(isVerticalMode) { bitmap = Bitmap.createScaledBitmap(first, first.getWidth(), first.getHeight()+secod.getHeight(), true); }
			else { bitmap = Bitmap.createScaledBitmap(first, first.getWidth()+secod.getWidth(), first.getHeight(), true); }

			/* isVerticalMode 값이 true 일 경우 세로, isVerticalMode 값이 false 일 경우 가로 */
			Paint p = new Paint();
			p.setDither(true);
			p.setFlags(Paint.ANTI_ALIAS_FLAG);
			Canvas c = new Canvas(bitmap);
			c.drawBitmap(first, 0, 0, null);
			if(isVerticalMode) { c.drawBitmap(secod, 0, first.getHeight(), null); }
			else { c.drawBitmap(secod, first.getWidth(), 0, null); }
			
			first.recycle(); secod.recycle();
			
			return bitmap;
		}

		@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
		protected void onPreExecute() 
		{
			/* Dialog 설정 구문 */
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
            mProgressDialog.setMessage("잠시만 기다려 주세요.");
            mProgressDialog.show();
			
			mString[1] = createName(); /* File의 이름을 시간과 사용자가 입력 한 결과를 받아서 조합 해주는 함수 */
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) 
		{
			/* RelativeLayout 레이아웃 객체 생성 */
			RelativeLayout[] mRelativeLayout = {null, null};
			
			/* mRelativeLayout[0] */
			mRelativeLayout[0] = (RelativeLayout)findViewById(R.id.minutely_total); /* photo_relative - RelativeLayout 객체 생성 */
			mRelativeLayout[0].setDrawingCacheEnabled(true); /* photo_relative - RelativeLayout 객체 생성 */
			mRelativeLayout[0].buildDrawingCache(true); /* photo_relative - RelativeLayout 객체 생성 */
			/* mRelativeLayout[1] */
			mRelativeLayout[1] = (RelativeLayout)findViewById(R.id.minutely_scrollView_below); /* minutely_scrollView_below_relative - RelativewLayout 객체 생성 */
			mRelativeLayout[1].setDrawingCacheEnabled(true); /* photo_relative - RelativeLayout 객체 생성 */
			mRelativeLayout[1].buildDrawingCache(true); /* photo_relative - RelativeLayout 객체 생성 */
			
			Bitmap[] mBitmap = {null, null, null}; /* Bitmap 객체 생성 */
			mBitmap[0] = Bitmap.createBitmap(mRelativeLayout[0].getMeasuredWidth(), mRelativeLayout[0].getMeasuredHeight(), Bitmap.Config.ARGB_8888); /* mRelativeLayout[0] 에 대한 가로 값과 높이 값을 저장 하고 Bitmap에 대한 투명도를 지정한다  */
			mBitmap[1] = Bitmap.createBitmap(mRelativeLayout[1].getMeasuredWidth(), mRelativeLayout[1].getMeasuredHeight(), Bitmap.Config.ARGB_8888); /* mRelativeLayout[1] 에 대한 가로 값과 높이 값을 저장 하고 Bitmap에 대한 투명도를 지정한다  */
			
			Canvas[] mCanvas = {null, null}; /* Canvas 객체 생성 */
			mCanvas[0] = new Canvas(mBitmap[0]); /* 캔버스 객체 생성 - Bitmap에 대한 mCanvas[0] 이미지 정보를 캔버스에 저장 */
			mRelativeLayout[0].draw(mCanvas[0]); /* mRelativeLayout[0] 에 대한 이미지를 그려준다. */
			
			mCanvas[1] = new Canvas(mBitmap[1]); /* 캔버스 객체 생성 - Bitmap에 대한 mCanvas[1] 이미지 정보를 캔버스에 저장 */
			mRelativeLayout[1].draw(mCanvas[1]); /* mRelativeLayout[1] 에 대한 이미지를 그려준다. */
			
			mBitmap[2] = combineImage(mBitmap[0], mBitmap[1], true); /* 두개의 합 쳐진 이미지를 저장 하는 Bitmap 공간 */
			
			/* 파일 변수 생성 및  경로를 저장 */
			File imagePath = new File(mString[0]);
			
			if(!imagePath.exists()) { imagePath.mkdirs(); }
			
			File fileCacheItem = new File(mString[0] + mString[1]);
			fileCacheItem.getAbsolutePath(); /* 해당 파일이 저장 되어 있는 경로를 저장 */
			OutputStream out = null;
			try { /* 정상적으로 저장이 되었을 경우 */ 
				fileCacheItem.createNewFile();
				out = new FileOutputStream(fileCacheItem);
				mBitmap[2].compress(CompressFormat.JPEG, 100, out); 
			} 
			catch (FileNotFoundException e) 
			{ 
				Toast.makeText(mContext, "저장위치의 문제가 발생하였거나 용량이 부족합니다.", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				Log.e("GREC", e.getMessage()); 
			} 
			catch (IOException e) 
			{ 
				Toast.makeText(mContext, "내장/외장메모리의 위치를 찾을 수가 없습니다.", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
				Log.e("GREC", e.getMessage()); 
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			Toast.makeText(mContext, "정상적으로 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show(); /* 정상적으로 저장이 완료 되었을 경우 */
            mProgressDialog.dismiss();
			super.onPostExecute(result);
		}	
	}
}