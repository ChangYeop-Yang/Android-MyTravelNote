package kr.example.mytravelnote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import kr.example.mytravelnote.apikey.API_KEY;
import kr.example.mytravelnote.common.DownloadImage;
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
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

public class SubActivity12_festival_minutely extends ActionBarActivity {

	/* String */
	private String[] mStringT = {null, null, null, null}; /* 관광지 조회 관련 변수 */
	private String[] mStringC = {null, null, null, null}; /* 공통정보 조회 관련 변수 */
	private String[] mStringL = {null, null}; /* 위치 관련 변수 */
	private String[] mStringD = {null, null, null, null, null, null, null}; /* 상세정보 조회 관련 변수 */
	
	/* Context */
	private final Context mContext = SubActivity12_festival_minutely.this;
	
	/* ArrayList */
	private ArrayList<String> nameFA = null;
	private ArrayList<String> mapXFA = null;
	private ArrayList<String> mapYFA = null;
	
	private ArrayList<String> nameLA = null;
	private ArrayList<String> mapXLA = null;
	private ArrayList<String> mapYLA = null;
	
	/* IMAGE VIEW */
	private ImageView[] mImageView = {null, null, null, null};
	
	/* TEXT VIEW */
	private TextView[] mTextView = {null, null, null, null, null, null, null};
	
	/* TODO initialization 메소드 */
	private void initialization()
	{
		/* ImageView */
		mImageView[0] = (ImageView)findViewById(R.id.festivel_minutely_imageview); /* Image View 객체 생성 */
		mImageView[1] = (ImageView)findViewById(R.id.festivel_minutely_point_imageview); /* MAP Image View 객체 생성 */
		mImageView[2] = (ImageView)findViewById(R.id.festivel_minutely_foodzoon); /* Food Image View 객체 생성 */
		mImageView[3] = (ImageView)findViewById(R.id.festivel_minutely_bad); /* Lodging Image View 객체 생성 */
		
		/* TextView */
		mTextView[0] = (TextView)findViewById(R.id.festivel_minutely_overview); /* OverView Text View 객체 생성 */
		mTextView[1] = (TextView)findViewById(R.id.festivel_minutely_title_textview); /* Title Text View 객체 생성 */
		mTextView[2] = (TextView)findViewById(R.id.festivel_minutely_point_textview); /* Point Text View 객체 생성 */
		mTextView[3] = (TextView)findViewById(R.id.festivel_minutely_information_add); /* Information Text View 객체 생성 */
		mTextView[4] = (TextView)findViewById(R.id.festivel_minutely_information2_add); /* Information Text View 객체생성 */
		mTextView[5] = (TextView)findViewById(R.id.festivel_minutely_point_add); /* Point_info Text View 객체 생성 */
		mTextView[6] = (TextView)findViewById(R.id.festivel_minutely_use_add); /* USE Text View 객체 생성 */
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival_minutely);
        
        /* Intent 값 가져오는 구문 */
        Intent mIntent = getIntent(); /* Intent 객체 생성 */
        mStringT[0] = mIntent.getStringExtra("Contentid"); /* Content Id 값 가져오기 */
        mStringT[1] = mIntent.getStringExtra("Type_code"); /* Type 값 가져오기 */
        mStringT[2] = mIntent.getStringExtra("Area"); /* 지역 명을 가져오는 구문 */
        mStringT[3] = mIntent.getStringExtra("Title"); /* 행사 명을 가져오는 구문 */
    	
        initialization(); /* TODO initialization 메소드 호출 */
    	
    	/* ActionBar 관련 구문 */
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setTitle("행사정보 상세 정보");
        mActionBar.setSubtitle(mStringT[3]);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff"))); /* ActionBar 색상 변경 */
        mActionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#a1deff")));
    	
    	/* TODO KrParserXML */
        KrParserXML[] mKr = {null, null}; /* kr_GoDataXML 객체 생성 */
    	mKr[0] = new KrParserXML(); /* 공통정보 관련 객체 생성 */ mKr[0].execute(1); /* 행사 공통정보 조회 */
    	mKr[1] = new KrParserXML(); /* 상세정보 관련 객체 생성 */ mKr[1].execute(2); /* 행사 상세정보 조회 */
    	
    	/* TODO DAUM */
        DaumParserXML[] mDaum = {null, null}; /* daum_LocationXML 객체 생성 */
    	mDaum[0] = new DaumParserXML("FD6", 1); /* 행사 주변 음식점 관련 객체 생성 */ mDaum[0].execute(); /* 행사 주변 음식점 조회 */
    	mDaum[1] = new DaumParserXML("AD5", 2); /* 행사 주변 숙소 관련 객체 생성 */ mDaum[1].execute(); /* 행사 주변 숙소 조회 */
        
        /* Google Map Image View */
        mImageView[1].setOnClickListener(new OnClickListener()
        {
			@Override /* TODO Auto-generated method stub */
			public void onClick(View v) 
			{
				AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext); /* Alert 객체 생성 */
                mAlertDialog.setTitle(mStringC[3]).setMessage("해당 위치로 이동 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) {
						if( (mStringL[0]!=null) && (mStringL[1]!=null) ) 
						{
							Intent intent = new Intent(mContext,SubActivity4_map.class);
							/* 값 전달 인텐트 구문 */
			    			intent.putExtra("Select_Mode", "minutely"); /* 주변정보와 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
			    			intent.putExtra("Latitude", Double.valueOf(mStringL[1]).doubleValue()); /* 위도 인텐트 값 전달 */
			    			intent.putExtra("Longitude", Double.valueOf(mStringL[0]).doubleValue()); /* 경도 인텐트 값 전달 */
			    			intent.putExtra("Title", mStringC[3]); /* 이름 값 전달 */ startActivity(intent); 
			    		} else { Toast.makeText(getApplicationContext(), "해당 위치가 존재 하지 않아 지도에 표시 할 수가 없습니다.", Toast.LENGTH_SHORT).show(); }
					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) { dialog.cancel(); } }).show();	
			} 
		});
        
        /* 주변음식점 조회 관련 구문 */
        mImageView[2].setOnClickListener(new OnClickListener()
        {
			@Override /* TODO Auto-generated method stub */
			public void onClick(View v) 
			{
				AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext); /* Alert 객체 생성 */
                mAlertDialog.setTitle(mStringC[3]).setMessage("주변 음식점 위치로 이동 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) {
						if( (mStringL[0]!=null) && (mStringL[1]!=null) )
                        {
							Intent intent = new Intent(mContext,SubActivity4_map.class);
							/* 값 전달 인텐트 구문 */
			    			intent.putExtra("Select_Mode", "minutely_zoon"); /* 주변정보와 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
			    			intent.putExtra("Mode", "음식점"); /* 음식점과 숙소를 구별 하는 값 전달 */
			    			/* 실수형 변수 전달 */
			    			intent.putExtra("Latitude", Double.valueOf(mStringL[1]).doubleValue()); /* 위도 인텐트 값 전달 */
			    			intent.putExtra("Longitude", Double.valueOf(mStringL[0]).doubleValue()); /* 경도 인텐트 값 전달 */
			    			/* 배열 변수 전달 */
			    			intent.putStringArrayListExtra("MapX", mapXFA);
			    			intent.putStringArrayListExtra("MapY", mapYFA);
			    			intent.putStringArrayListExtra("TITLE", nameFA);
			    			startActivity(intent);
                        } else { Toast.makeText(getApplicationContext(), "해당 위치가 존재 하지 않아 지도에 표시 할 수가 없습니다.", Toast.LENGTH_SHORT).show(); }
					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) { dialog.cancel(); } }).show();
			} 
		});
        
        /* 주변숙소 조회 관련 구문 */
        mImageView[3].setOnClickListener(new OnClickListener()
        {
			@Override /* TODO Auto-generated method stub */
			public void onClick(View v) 
			{
				AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext); /* Alert 객체 생성 */
                mAlertDialog.setTitle(mStringC[3]).setMessage("주변 숙박시설 위치로 이동 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) {
						if( (mStringL[0]!=null) && (mStringL[1]!=null) )
                        {
							Intent intent = new Intent(mContext,SubActivity4_map.class);
							/* 값 전달 인텐트 구문 */
			    			intent.putExtra("Select_Mode", "minutely_zoon"); /* 주변정보와 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
			    			intent.putExtra("Mode", "숙소"); /* 음식점과 숙소를 구별 하는 값 전달 */
			    			/* 실수형 변수 전달 */
			    			intent.putExtra("Latitude", Double.valueOf(mStringL[1]).doubleValue()); /* 위도 인텐트 값 전달 */
			    			intent.putExtra("Longitude", Double.valueOf(mStringL[0]).doubleValue()); /* 경도 인텐트 값 전달 */
			    			/* 배열 변수 전달 */
			    			intent.putStringArrayListExtra("MapX", mapXLA);
			    			intent.putStringArrayListExtra("MapY", mapYLA);
			    			intent.putStringArrayListExtra("TITLE", nameLA);
			    			startActivity(intent);
                        }
						else { Toast.makeText(getApplicationContext(), "해당 위치가 존재 하지 않아 지도에 표시 할 수가 없습니다.", Toast.LENGTH_SHORT).show(); }
					}
				}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
					@Override /* TODO Auto-generated method stub */
					public void onClick(DialogInterface dialog, int which) { dialog.cancel(); } }).show();
			} 
		});
    }
    
    @Override
	public void onBackPressed()
	{ finish(); }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sub9_minutely, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

    	/* Action Bar 관련 구문 */
    	switch (item.getItemId()) {
    		case(R.id.kakaotalk) : /* 카카오톡 */
    		{   
    			kakaotalk kakao = new kakaotalk(mContext, mStringT[3], mStringT[1], mStringT[0], mStringT[2], mStringD[5], mStringD[2]);
    			kakao.Festival_Kakao_Talk_write(2); /* 관광지 정보 카카오톡으로 정송 */
    			return true; 
    		}
    		case(R.id.save_icon) : /* 로컬 파일 저장 */
    		{
    			StringBuffer mStringBuffer = new StringBuffer(); /* StringBuffer 객체 생성 */
    			String mString = Environment.getExternalStorageState(); /* 외장 메모리 상태 저장 */
    			if(mString.equals(Environment.MEDIA_MOUNTED)) { mStringBuffer.append(Environment.getExternalStorageDirectory().getAbsolutePath()); mStringBuffer.append("/MyTravel/Festival/"); } /* 외장 메모리가 존재 하는 경우 외장 메모리 경로 저장 */
    			else { mStringBuffer.append(Environment.MEDIA_UNMOUNTED); mStringBuffer.append("/MyTravel/Festival/"); } /* 외장 메모리가 없는 경우 내장 메모리 경로 저장 */
    			
    			/* 그림 파일을 이미지 파일로 저장 시켜 주는 구문 */
    			ImageSave mSave = new ImageSave(mStringBuffer.toString());
    			mSave.execute(); /* Bitmap을 이미지 파일로 저장해주는 함수 호출 */
    			return true;
    		}
    		case(R.id.facebook) : /* 페이스북 */
    		{
    			Intent intent = new Intent(mContext,SubActivity10_facebook.class);
    			intent.putExtra("facebook_minutely", "tour_minutely_mode");
    			intent.putExtra("facebook_title", mStringC[3]); /* 제목 */
    			intent.putExtra("facebook_address", mStringT[0]); /* 주소 */
    			intent.putExtra("facebook_image", mStringT[1]); /* 관광지 사진 */
    			intent.putExtra("facebook_overview", mStringD[5]); /* 휴무일 */
    			startActivity(intent); return true;
    		}
    		default : { return super.onOptionsItemSelected(item); } } 
	}
	
	/* TODO ImageSave 클래스 */
	private class ImageSave extends AsyncTask<Void, Void, Void>
	{
		/* ProgressDialog */
		private ProgressDialog Dialog = null;
		
		/* String */
		private String[] mString = {null,null};
		
		/* 생성자 메소드 */
		ImageSave(String mString)
		{
			this.mString[0] = mString;
		}
		
		 /* TODO File의 이름을 시간과 사용자가 입력 한 결과를 받아서 조합 해주는 함수 */
		@SuppressWarnings("static-access")
		private String renameFile() {
			final Calendar calender = Calendar.getInstance(); /* 캘린더 객체 생성 */
			
				/* Calendar 변수 */
				int YEAR = calender.get(calender.YEAR); /* 년 */
				int MONTH = calender.get(calender.MONTH); /* 월 */
				int DAY = calender.get(calender.DAY_OF_MONTH); /* 일 */
				/* 문자열 합치기 */
				String mString = String.format("%d년 %d월 %d일 %s.JPG", YEAR, MONTH, DAY, mStringC[3]);
				return mString; /* 리턴 값 전달 */ }
		
		/* TODO 2개 이상의 Bitmap을 합쳐 주는 함수  */
		@SuppressWarnings("deprecation")
		private Bitmap combineImage(Bitmap first, Bitmap secod, boolean isVerticalMode) {
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
			Dialog = new ProgressDialog(mContext);
			Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
			Dialog.setMessage("잠시만 기다려 주세요.");
			Dialog.show();
			
			mString[1] = renameFile(); /* File의 이름을 시간과 사용자가 입력 한 결과를 받아서 조합 해주는 함수 */
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... params) 
		{
			/* RelativeLayout 레이아웃 객체 생성 */
			RelativeLayout[] mRelativeLayout = {null, null};
			
			/* mRelativeLayout[0] */
			mRelativeLayout[0] = (RelativeLayout)findViewById(R.id.festivel_minutely_total); /* photo_relative - RelativeLayout 객체 생성 */
			mRelativeLayout[0].setDrawingCacheEnabled(true); /* photo_relative - RelativeLayout 객체 생성 */
			mRelativeLayout[0].buildDrawingCache(true); /* photo_relative - RelativeLayout 객체 생성 */
			/* mRelativeLayout[1] */
			mRelativeLayout[1] = (RelativeLayout)findViewById(R.id.festivel_minutely_scrollView_below); /* mRelativeLayout[1] - RelativewLayout 객체 생성 */
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
			Dialog.dismiss();
			super.onPostExecute(result);
		}
		
	}
	
	/* TODO XML AsyncTask 클래스 */
	private class KrParserXML extends AsyncTask<Integer, Void, Integer>
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
		protected Integer doInBackground(Integer... params) 
		{
			try
			{
				/* Xml pull 파실 객체 생성 */
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser parser = factory.newPullParser();
				
				StringBuffer mStringBuffer = null; /* StringBuffer 객체 생성 */
				switch(params[0])
				{
					case (1) : /* 행사 상제 데이터 조회 */
					{ 
						/* StringBuffer 관련 구문 */
                        mStringBuffer = new StringBuffer("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey="); /* StringBuffer 객체 생성 */
                        mStringBuffer.append(API_KEY.kr_GoDATA_API_KEY); mStringBuffer.append("&contentId="); mStringBuffer.append(mStringT[0]);
                        mStringBuffer.append("&MobileOS=AND&MobileApp=MyTravel&defaultYN=Y&firstImageYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y"); break;
					}
					case (2) : /* 지역 코드 조회 */
					{ 	
						/* StringBuffer 관련 구문 */
                        mStringBuffer = new StringBuffer("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?ServiceKey="); /* StringBuffer 객체 생성 */
						/* API KEY */ mStringBuffer.append(API_KEY.kr_GoDATA_API_KEY); mStringBuffer.append("&contentTypeId="); mStringBuffer.append(15); mStringBuffer.append("&contentId="); mStringBuffer.append(mStringT[0]);
                        mStringBuffer.append("&MobileOS=AND&MobileApp=MyTravel&introYN=Y"); break;
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
							case (1) : /* 행사 상세정보 조회 */ 
							{
								if(tagName.equals("addr1")) 
								{ mStringC[0] = parser.getText(); /* Log.e("XML - addr1", parser.getText()); */ } /* 주소 */
								if(tagName.equals("firstimage")) 
								{ mStringC[1] = parser.getText(); /* Log.e("XML - IMG", parser.getText()); */ } /* 이미지 주소 */
								if(tagName.equals("mapx")) 
								{ mStringL[0] = parser.getText(); /* Log.e("XML - mapx", parser.getText()); */ } /* mapx */
								if(tagName.equals("mapy")) 
								{ mStringL[1] = parser.getText(); /* Log.e("XML - mapy", parser.getText()); */ } /* mapy */
								if(tagName.equals("overview")) 
								{ mStringC[2] = parser.getText(); /* Log.e("XML - overview", parser.getText()); */ } /* 행사 상세정보 */
								if(tagName.equals("title")) 
								{ mStringC[3] = parser.getText(); /* Log.e("XML - title", parser.getText()); */ } /* 이름 */ break;
							}
							case (2) : /* 상세 정보 조회 */ 
							{
								if(tagName.equals("eventplace")) 
								{ mStringD[0] = parser.getText(); /* Log.e("XML - eventplace", parser.getText()); */ } /* 행사장소 */
								if(tagName.equals("placeinfo")) 
								{ mStringD[1] = parser.getText(); /* Log.e("XML - placeinfo", parser.getText()); */ } /* 행사 위치 안내 */
								if(tagName.equals("sponsor1")) 
								{ mStringD[2] = parser.getText(); /* Log.e("XML - sponsor1", parser.getText()); */ } /* 주최사 명 */
								if(tagName.equals("sponsor1tel")) 
								{ mStringD[3] = parser.getText(); /* Log.e("XML - sponsor1tel", parser.getText()); */ } /* 주최사 전화번호 */ 
								if(tagName.equals("sponsor2tel")) 
								{ mStringD[4] = parser.getText(); /* Log.e("XML - sponsor2tel", parser.getText()); */ } /* 주관사 전화번호 */
								if(tagName.equals("usetimefestival")) 
								{ mStringD[5] = parser.getText(); /* Log.e("XML - usetimefestival", parser.getText()); */ } /* 이용정보 */ break;
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
		protected void onPostExecute(Integer result)
		{
			/* TextView */
			switch(result)
			{
				case (1) :
				{
					/* TODO Null Data 검증 구문 */
			        if(mStringD[5]==null) { mStringD[5] = "제공 된 이용정보가 없습니다."; } /* 이용정보  */
			        if(mStringC[0]==null) { mStringC[0] = "제공 된 주소정보가 없습니다."; } /* 주소정보 */
			        if(mStringD[2]==null) { mStringD[2] = "제동 된 주최사명 정보가 없습니다."; } /* 주최사 정보 */
			        if(mStringD[3]==null) { mStringD[3] = "제동 된 주관사 전화번호 정보가 없습니다."; } /* 주관사 전화번호 */
			        if(mStringD[4]==null) { mStringD[4] = "제동 된 주최사 전화번호 정보가 없습니다."; } /* 주최사 전화 번호 */
			        if(mStringD[0]==null) { mStringD[0] = "제공 된 행사장소 정보가 없습니다."; } /* 행사장소 정보 */
			        if(mStringD[1]==null) { mStringD[1] = "제공 된 행사위치안내 정보가 없습니다."; } /* 행사위치 안내 정보 */
			        if(mStringC[1]==null) { mStringC[1] = "http://api.visitkorea.or.kr/static/images/common/noImage.gif"; } /* 행사 이미지 정보 */
					
					/* TextView 설정 구문 */
			        mTextView[0].setText(Html.fromHtml(mStringC[2])); /* 상세 정보 설정 */
			        mTextView[1].setText(Html.fromHtml(mStringC[3])); /* 행사 제목 설정 */
			        mTextView[2].setText(Html.fromHtml(mStringC[0])); /* 행사 주소 설정 */
			        
			        /* 주최사 정보 설정 구문 */
			        StringBuffer mStringBuffer1 = new StringBuffer(); /* StringBuffer 객체 생성 */
                    mStringBuffer1.append(mStringD[2]); mStringBuffer1.append(" : "); mStringBuffer1.append(mStringD[3]);
			        mTextView[3].setText(Html.fromHtml(mStringBuffer1.toString())); /* 주최사 정보 설정 */
			        mTextView[4].setText(Html.fromHtml(mStringD[4])); /* 주관사 연락 정보 설정 */
			        
			        /* 행사장소 및 위치안내 설정 구문 */
			        StringBuffer mStringBuffer2 = new StringBuffer(); /* StringBuffer 객체 생성 */
                    mStringBuffer2.append(mStringD[0]); mStringBuffer2.append("\n"); mStringBuffer2.append(mStringD[1]);
			        mTextView[5].setText(Html.fromHtml(mStringBuffer2.toString()));
			        mTextView[6].setText(Html.fromHtml(mStringD[5])); /* 이용정보 */ break;
				}
				case (2) :
				{
					StringBuffer mStringBuffer = new StringBuffer("http://maps.googleapis.com/maps/api/staticmap?center=");
                    mStringBuffer.append(mStringL[1] + "," + mStringL[0]); /* 카메라 이동 위도와 경도 */
                    mStringBuffer.append("&zoom=18&size=320x320&scale=2&maptype=roadmap&");
                    mStringBuffer.append("markers=color:blue|label:N|");  mStringBuffer.append(mStringL[1] + "," + mStringL[0]); /* 마커 설정 위도와 경도 */
                    mStringBuffer.append("%7C11211&sensor=false"); mStringBuffer.append("&key=");
                    mStringBuffer.append(API_KEY.Google_Static_Key); /* Google API Key */

                    /* IMAGE DOWNLOAD */
                    DownloadImage.image(mImageView[0] ,mStringC[1], mContext);
                    DownloadImage.image(mImageView[1] ,mStringBuffer.toString(), mContext);
					break;
				}
			}

            mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
	}
	
	/* TODO Daum 주변 정보 관련 정보 클래스 */
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

            /* 음식점 */
			nameFA = new ArrayList<String>(10);
			mapXFA = new ArrayList<String>(10);
			mapYFA = new ArrayList<String>(10);
			/* 숙소 */
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
			
				/* StringBuffer 관련 구문 */
				StringBuffer mStringBuffer = new StringBuffer("https://apis.daum.net/local/v1/search/category.xml?location="); /* StringBuffer 객체 생성 */
                mStringBuffer.append(mStringL[1]); mStringBuffer.append(","); mStringBuffer.append(mStringL[0]); /* 위도와 경도 */
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
			/* Google map에 대한 StringBuffer 객체 생성 */
			StringBuffer mStringBuffer = new StringBuffer("http://maps.googleapis.com/maps/api/staticmap?center=");
            mStringBuffer.append(mStringL[1] + "," + mStringL[0]); /* 카메라 이동 위도와 경도 */
            mStringBuffer.append("&zoom=16&size=320x320&scale=2&maptype=roadmap&");
			
			switch(mInt)
			{
				case(1) :
				{
					int length = nameFA.size(); /* ArrayList 변수 사이즈 저장 변수 생성 */
					for(int count=0; count<length; count++)
					{ 
						String MapX = mapXFA.get(count); /* 위도 */
						String MapY = mapYFA.get(count); /* 경도 */
						if( (!MapX.equals("\n")) && (!MapY.equals("\n")) ) {
                            mStringBuffer.append("markers=color:red|label:F|");
                            mStringBuffer.append(MapX + "," + MapY); mStringBuffer.append("&"); /* 마커 설정 위도와 경도 */ }
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
						String MapX = mapXLA.get(count); /* 위도 */
						String MapY = mapYLA.get(count); /* 경도 */
						if( (!MapX.equals("\n")) && (!MapY.equals("\n")) ) {
                            mStringBuffer.append("markers=color:blue|label:L|");
                            mStringBuffer.append(MapX + "," + MapY); mStringBuffer.append("&"); /* 마커 설정 위도와 경도 */ }
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
}