package kr.example.mytravelnote;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import kr.example.mytravelnote.apikey.API_KEY;
import kr.example.mytravelnote.common.Value;
import kr.example.mytravelnote.common.Vibration;
import kr.net.mytravelnote.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SubActivity7_information extends Activity
{
	private String mKeyword = "null"; /* 맵 사용 Keyword 관련 변수 생성 */

    /* Context */
    private Context mContext = SubActivity7_information.this;
			
	/* TODO XML 관련 데이터 저장 변수 관련 구문 */
	private ArrayList<String> titleA = new ArrayList<String>(10); /* 상호명 관련 String 배열 변수 생성 */
	private ArrayList<String> telA = new ArrayList<String>(10); /* 전화번호 관련 String 배열 변수 생성 */
	private ArrayList<String> pointA = new ArrayList<String>(10); /* 장소 관련 String 배열 변수 생성 */
	private ArrayList<String> urlA = new ArrayList<String>(10); /* URL String 배열 변수 생성 */
	private ArrayList<String> latitudeA = new ArrayList<String>(10); /* 위도 관련 String 배열 변수 생성 */
	private ArrayList<String> longitudeA = new ArrayList<String>(10); /* 경도 관련 String 배열 변수 생성 */
		
	private double mapX = 0.0; /* 위도 관련 변수 생성 */
	private double mapY = 0.0; /* 경도 관련 변수 생성 */
	
	/* TODO 버튼 클릭 이벤트 함수 */
	private void Button_Click(Button result, final String keyword)
    {
		result.setOnClickListener(new OnClickListener()
        {
			@Override /* Auto-generated method stub */
 			public void onClick(View v) 
			{
                /* String */
				String result = null; /* 키워드를 조합 해주는 변수 생성 */
                Vibration.Vibrate(mContext);

				/* 키워드를 한글로 매칭해주는 구문 */
				switch(keyword) 
				{
					case ("FD6") : { mKeyword="FD6"; result="음식점"; break; } /* 음식점 */
					case ("BK9") : { mKeyword="BK9"; result="은행"; break; } /* 은행 */
					case ("HP8") : { mKeyword="HP8"; result="병원"; break; } /* 병원 */
					case ("CS2") : { mKeyword="CS2"; result="편의점"; break; } /* 편의점 */
					case ("AD5") : { mKeyword="AD5"; result="숙박시설"; break; } /* 숙박시설 */
					case ("OL7") : { mKeyword="OL7"; result="주유소&충전소"; break; } /* 주유소&충전소 */
				}

				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show(); /* 토스트 기능 출력 */
				new ParserXML().execute(mapX, mapY); /* XML 파싱 함수  호출 */

                if(titleA.isEmpty()!=true) /* ArrayList 데이터가 있는 경우 */
                { initialization(); }
			}}); 
	}
	
	@Override
	public void onBackPressed()
	{ finish(); }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        
        /* Intent */
        Intent mIntent = getIntent(); /* Intent 객체 생성 */
        mapX = mIntent.getDoubleExtra("MapX", 0.0); /* 위도 값 가져오기 */
        mapY = mIntent.getDoubleExtra("MapY", 0.0); /* 경도 값 가져오기 */
        		
        /* TextView */
		final TextView mTextView = (TextView)findViewById(R.id.information_point_text); /* TextView 객체 생성 */
		
		/* StringBuffer */
		StringBuffer mStringBuffer = new StringBuffer();
		mStringBuffer.append(Value.getAdminArea()); mStringBuffer.append(" ");
        mStringBuffer.append(Value.getLocalityArea()); mStringBuffer.append(" ");
		mStringBuffer.append(Value.getThoroughFareArea()); mStringBuffer.append(" ");
        mStringBuffer.append(Value.getFeatureNameArea());
		mTextView.setText(mStringBuffer.toString());
		
        /* 음식점 Button 객체 생성 구문 */
        final Button mButtonF = (Button)findViewById(R.id.foodzoon); /* 음식점 버튼 객체 생성 */
        Button_Click(mButtonF, "FD6"); /* 버튼 클릭 이벤트 함수 호출 */
        
        /* 은행 Button 객체 생성 구문 */
        final Button mButtonB = (Button)findViewById(R.id.bank); /* 은행 버튼 객체 생성 */
        Button_Click(mButtonB, "BK9"); /* 버튼 클릭 이벤트 함수 호출 */
        
        /* 병원 Button 객체 생성 구문 */
        final Button mButtonH = (Button)findViewById(R.id.hospital); /* 병원 버튼 객체 생성 */
        Button_Click(mButtonH, "HP8"); /* 버튼 클릭 이벤트 함수 호출 */
        
        /* 편의점 Button 객체 생성 구문 */
        final Button mButtonC = (Button)findViewById(R.id.convenience); /* 편의점 버튼 객체 생성 */
        Button_Click(mButtonC, "CS2"); /* 버튼 클릭 이벤트 함수 호출 */
        
        /* 숙소 Button 객체 생성 구문 */
        final Button mButtonL = (Button)findViewById(R.id.lodging); /* 숙소 버튼 객체 생성 */
        Button_Click(mButtonL, "AD5"); /* 버튼 클릭 이벤트 함수 호출 */
        
        /* 주유소 Button 객체 생성 구문 */
        final Button mButtonG = (Button)findViewById(R.id.oil); /* 주유소&충전소 버튼 객체 생성 */
        Button_Click(mButtonG, "OL7"); /* 버튼 클릭 이벤트 함수 호출 */
    }
	
	/* ArrayList Size Check 함수 */
	private void checkSizeArray(int length)
	{
        int lengthPoint = pointA.size(); /* Point Size */
        int lengthTel = telA.size(); /* TEL Size */
        int lengthUrl = urlA.size(); /* URL Size */
		
		/* ArrayList_Size가 맞지 않을 경우 맞춰 주는 구문 */
		while( (lengthPoint != length) || (lengthTel != length) || (lengthUrl != length) )
		{
			if(lengthPoint != length) { pointA.add("\n"); lengthPoint++; } /* 장소 */
			if(lengthTel != length) { telA.add("\n"); lengthTel++; } /* 전화번호 */
			if(lengthUrl != length) { urlA.add("\n"); lengthUrl++; } /* URL */
		}
	}
	
	/* XML 파싱 데이터를 리스트뷰에 추가시켜 주는 함수 */
	private void insertListView(double mapX, double mapY, int length)
    {
		/* ListView 관련 구문 */
        ListView list = (ListView) findViewById(R.id.information_listview); /* ListView 객체 생성 */
        ArrayList<MyData> dataArr = new ArrayList<MyData>();

        checkSizeArray(titleA.size()); /* ArrayList Size Check 함수 호출 */
        		
        /* Item을 증가 시켜 주는 구문 */
        for(int count=0; count < length; count++)
        {
        	/* 상호명 */
            String title = titleA.get(count);
        	/* 장소 */
            String point = pointA.get(count);
        	/* 전화번호 */
            String tel = telA.get(count);
        	/* URL */
            String url = urlA.get(count);
        	
        	/* \n 입력 방지 구문 */
        	if( ( !title.equals("\n") ) && ( !point.equals("\n") ) )
        	{ 
        		/* 남은 거리 계산 하는 구문 */
        		double mLatitude = Double.valueOf(latitudeA.get(count)).doubleValue(); /* 위도 */
            	double mLongitude = Double.valueOf(longitudeA.get(count)).doubleValue(); /* 경도 */
            	String distance = calculateDistance(mapX, mapY, mLatitude, mLongitude); /* 좌표를 이용한 거리 계산 함수 호출 */
            	/* Item을 추가 해주는 구문 */
        		dataArr.add(new MyData(title, point, tel, distance, mLatitude, mLongitude, url));
        	}
        }
        
        MyAdapter mAdapter = new MyAdapter(this, R.layout.activity_inforamtion_listview, dataArr);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setAdapter(mAdapter);
	}
	
	/* ArrayList 재사용을 위한 데이터 삭제 함수 */
	private void initialization()
	{
		titleA.clear(); /* 상호명 */
        telA.clear(); /* 전화번호 */
		pointA.clear(); /* 장소 */
		urlA.clear(); /* URL */
		latitudeA.clear(); /* 위도 */
		longitudeA.clear(); /* 경도 */
	}
	
	/* 좌표를 이용한 거리 계산 함수 */
	private static String calculateDistance(double lat1, double lon1, double lat2, double lon2)
    {
        final double EARTH_R = 6371000.0;
		double rad = 0.0, radLat1 = 0.0, radLat2 = 0.0, radDist = 0.0;
		double distance = 0.0, ret = 0.0;

        rad = Math.PI/180;
		radLat1 = rad * lat1;
		radLat2 = rad * lat2;
		radDist = rad * (lon1 - lon2);
		
		distance = Math.sin(radLat1) * Math.sin(radLat2);
		distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
		ret = EARTH_R * Math.acos(distance);
		
		double rslt = Math.round(Math.round(ret) / 1000);
		String result = rslt + "km"; /* String 형 변환 및 km */

        if( rslt == 0 ) result = Math.round(ret) + "m"; /* String 형변환 및 m */
		return result;
    }
		
	/* Custom Adpter Class */
	private class MyData
    {
		private String name = null; /* 상호명 저장 변수 생성 */
        private String address = null; /* 주소 저장 변수 생성 */
        private String tel = null; /* 전화 번호 저장 변수 생성 */
        private String distance = null; /* 남은 거리 저장 변수 생성 */
        private String placeUrl = null; /* 상세페이지 URL */
        private double mapX = 0.0; /* 위도 저장 변수 */
        private double mapY = 0.0; /* 경도 저장 변수 */

		/* 생성자 메소드 생성 */
		private MyData(String name, String address, String tel, String distance, double mapX, double mapY, String placeUrl)
		{
            this.name = name; this.address = address;
            this.tel = tel; this.distance = distance;
            this.mapX = mapX; this.mapY = mapY;
            this.placeUrl = placeUrl;
        }
    }
	
	/* Custom Adpter Class */
	private class MyAdapter extends BaseAdapter
    {
		private Context mContext = null;
		private int layoutId = 0;
		private ArrayList<MyData> myDataArr = null;
		private LayoutInflater Inflater = null;

		/* 생성자 메소드 생성 */
		private MyAdapter(Context mContext, int layoutId, ArrayList<MyData> myDataArr)
		{
            this.mContext=mContext; this.layoutId=layoutId;
            this.myDataArr = myDataArr; Inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
		
		@Override /* TODO Auto-generated method stub */
		public int getCount() { return myDataArr.size(); }

		@Override /* TODO Auto-generated method stub */
		public Object getItem(int position) { return myDataArr.get(position).name; }

		@Override /* TODO Auto-generated method stub */
		public long getItemId(int position) { return position; }

		@Override /* TODO Auto-generated method stub */
		public View getView(int position, View convertView, ViewGroup parent)
        {
			/* ListView에 ITEM 번호를 저장 하는 변수 생성 */
			final int TEMP = position;
			
			if(convertView == null) { convertView = Inflater.inflate(layoutId, parent, false); }

			/* Name TextView */
			final TextView nameTextView = (TextView)convertView.findViewById(R.id.information_name);
            nameTextView.setText(myDataArr.get(position).name);

			/* address TextView */
			final TextView addressTextView = (TextView)convertView.findViewById(R.id.information_address);
            addressTextView.setText(myDataArr.get(position).address);

			/* distance TextView */
			final TextView distanceTextView = (TextView)convertView.findViewById(R.id.information_distance);
            distanceTextView.setText(myDataArr.get(position).distance);
			
			/* 전화 버튼 기능 관련 구문 */
			final Button callButton = (Button)convertView.findViewById(R.id.information_phone_but);
            callButton.setOnClickListener(new OnClickListener()
            {
				@Override /* TODO Auto-generated method stub */
				public void onClick(View v)
                {
                    mVibrate();
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+ myDataArr.get(TEMP).tel));
                    startActivity(mIntent);
                }
            });
			
			/* 위치 버튼 기능 관련 구문 */
			final Button pointButton = (Button)convertView.findViewById(R.id.information_point_but);
            pointButton.setOnClickListener(new OnClickListener()
            {
				@Override /* TODO Auto-generated method stub */
				public void onClick(View v) 
				{
                    mVibrate(); /* 기기 진동 관련 함수 호출 */

                    /* Intent */
					Intent mIntent = new Intent(mContext, SubActivity4_map.class); /* 인텐트 객체 생성 */
                    mIntent.putExtra("Select_Mode", "information_Map"); /* 메인화면 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
                    mIntent.putExtra("Name", myDataArr.get(TEMP).name); /* 상호명 */
                    mIntent.putExtra("Latitude", myDataArr.get(TEMP).mapX); /* 위도 값을 전달 */
                    mIntent.putExtra("Longitude", myDataArr.get(TEMP).mapY); /* 경도 값을 전달 */
					startActivity(mIntent); /* 인텐트 시작 */
				} });

				convertView.setOnClickListener(new OnClickListener() {
				@Override /* TODO Auto-generated method stub */
				public void onClick(View v)
                {
					Toast.makeText(mContext, myDataArr.get(TEMP).name, Toast.LENGTH_SHORT).show(); /* 토스트 기능을 이용하여서 사용자가 선택 한 아이템 출력 */

                    /* Intent */
                    Intent mIntent = new Intent(Intent.ACTION_VIEW); /* Web View를 열어줄 intent 객체 생성 */
					Uri mUri = Uri.parse(myDataArr.get(TEMP).placeUrl); /* URL 객체 생성 */
                    mIntent.setData(mUri); /* URL 정보를 인텐트에 저장 */
                    startActivity(mIntent); /* 인텐트 실행 */
				} });
			
			return convertView; }
	}
	
	/* Server와 통신을 하기 위한 AsyncTask 클래스 */
	private class ParserXML extends AsyncTask<Double, Void, Void>
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
		protected Void doInBackground(Double... params) 
		{
			try 
			{
				/* Xml pull 파실 객체 생성 */
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser parser = factory.newPullParser();

					/* StringBuffer 관련 구문 */
					StringBuffer mStringBuffer = new StringBuffer("https://apis.daum.net/local/v1/search/category.xml?location="); /* StringBuffer 객체 생성 */
                    mStringBuffer.append(params[0]); mStringBuffer.append(","); mStringBuffer.append(params[1]); /* 위도와 경도 */
                    mStringBuffer.append("&code="); mStringBuffer.append(mKeyword);
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
						if(tagName.equals("title")) 
						{ titleA.add(parser.getText()); /* Log.e("XML - title", parser.getText()); */ } /* 상호명 */
						if(tagName.equals("latitude"))
						{ latitudeA.add(parser.getText()); /* Log.e("XML - latitude", parser.getText()); */ } /* 위도 */
						if(tagName.equals("longitude"))
						{ longitudeA.add(parser.getText()); /* Log.e("XML - longitude", parser.getText()); */ } /* 경도 */
						if(tagName.equals("phone")) 
						{ telA.add(parser.getText()); /* Log.e("XML - phone", parser.getText()); */ } /* 전화번호 */
						if(tagName.equals("address")) 
						{ pointA.add(parser.getText()); /* Log.e("XML - address", parser.getText()); */ } /* 장소 */
						if(tagName.equals("placeUrl")) 
						{ urlA.add(parser.getText()); /* Log.e("XML - placeUrl", parser.getText()); */ } /* URL */
					} else if (eventType == XmlPullParser.END_TAG) { tagName = parser.getName(); if(tagName.equals("channel")) { isItemTag = false; } }
					eventType = parser.next(); /* 다음 XML 객체로 이동 */
				}
			} 
			catch (Exception e) { e.printStackTrace(); } return null;
		}
		
		@Override
		protected void onPostExecute(Void doc)
		{
            insertListView(mapX, mapY, titleA.size()); /* XML 파싱 데이터를 리스트뷰에 추가시켜 주는 함수 호출 */
            mProgressDialog.dismiss(); /* Dialog 종료 부분 */
			super.onPostExecute(doc);
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

