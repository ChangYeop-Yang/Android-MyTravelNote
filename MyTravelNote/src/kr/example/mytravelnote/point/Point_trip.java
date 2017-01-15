package kr.example.mytravelnote.point;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import kr.example.mytravelnote.SubActivity4_map;
import kr.example.mytravelnote.apikey.API_KEY;
import kr.net.mytravelnote.R;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/* 위치 기반 관광 정보 조회 */
public class Point_trip extends AsyncTask<Void, Void, Void>
{
	/* APP NAME */
	private final String My_AppName = "MyTravel";
	
	/* TODO ProgressDialog */
	private ProgressDialog Dialog = null;
	
	/* TODO Context */
	private Context mContext = null;

	/* TODO ListView */
	private ListView mListView = null;
	
	/* TODO Double */
	private double mapX = 0.0; /* 위도 관련 변수 */
	private double mapY = 0.0; /* 경도 관련 변수 */
	
	/* TODO Int */
	private int mInt = 0;
	
	/* TODO ArrayList */
	private ArrayList<String> name_A = null; /* 관광지 이름 저장 변수 */
	private ArrayList<String> address_A = null; /* 관광지 주소 저장 변수 */
	private ArrayList<String> mapX_A = null; /* 위도 저장 변수 */
	private ArrayList<String> mapY_A = null; /* 경도 저장 변수 */
	
	/* 생성자 - 1 */
	public Point_trip(Context mContext, double mapX, double mapY) 
	{ 
		this.mContext = mContext;
		this.mapX = mapX; /* 사용자로 부터 위도 값 저장 */
		this.mapY = mapY; /* 사용자로 부터 경도 값 저장 */
	}
	
	/* 생성자 - 2 */
	public Point_trip(Context mContext, double mapX, double mapY, ListView mListView)
	{
		this(mContext, mapX, mapY);
		this.mListView = mListView;
		mInt = 1;
	}
	
	/* TODO 좌표를 이용한 거리 계산 함수 */
	private String getCalcDistance(double lat1, double lon1, double lat2, double lon2) 
	{
		double EARTH_R, Rad, radLat1, radLat2, radDist;
		double distance, ret;
		
		EARTH_R = 6371000.0;
		Rad = Math.PI/180;
		radLat1 = Rad * lat1;
		radLat2 = Rad * lat2;
		radDist = Rad * (lon1 - lon2);
		
		distance = Math.sin(radLat1) * Math.sin(radLat2);
		distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
		ret = EARTH_R * Math.acos(distance);
		
		double rslt = Math.round(Math.round(ret) / 1000);
		String result = rslt + "km"; /* String 형 변환 및 km */
		if(rslt==0) result = Math.round(ret) + "m"; /* String 형변환 및 m */
		return result; 
	}
	
	/* TODO XML 파싱 데이터를 리스트뷰에 추가시켜 주는 메소드 */
	private void Listview_data() 
	{    
		final int legnth = name_A.size();
		String[] mString = {null, null};
		/*
		 * mString[0] = 제목
		 * mString[1] = 주소
		 * mString[2] = 전화번호
		 */
		double[] mDouble = {0.0, 0.0};
		
		ArrayList<MyData> dataArr = new ArrayList<MyData>(); /* ArrayList<MyData> 객체 생성 */
               		
        /* Item을 증가 시켜 주는 구문 */
        for(int count = 0; count<legnth; count++) 
        {
        	if(!name_A.get(count).equals("\n")) /* \n 입력 방지 구문 */
        	{	
        		/* Double */
        		mDouble[0] = Double.valueOf(mapX_A.get(count)).doubleValue();
        		mDouble[1] = Double.valueOf(mapY_A.get(count)).doubleValue();
        		
        		/* String */
        		mString[0] = name_A.get(count);
        		mString[1] = getCalcDistance(mapX, mapY, mDouble[0], mDouble[1]);
        		
        		dataArr.add(new MyData(mString[0], mString[1], mDouble[0], mDouble[1])); 
        	}
        }

        MyAdapter mAdapter = new MyAdapter(mContext, R.layout.activity_main_listview, dataArr);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(mAdapter);
	}
	
	/* TODO Daum Map에 Maker를 찍어주는 함수 */
	private void Daum_Maker(String name, String address, double Latitude, double Longitude)
	{
		/* Maker 기본 객체 생성 관련 구문 */
		MapPOIItem marker = new MapPOIItem(); /* Maker 관련 객체 생성 */
		marker.setTag(2); /* 일반 마커 태그 번호 부여 */
		marker.setItemName(name); /* Maker 아이템 이름 설정 */
		marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Latitude, Longitude)); /* Maker 경로 설정 */
		
		/* Custom Marker 구문 */
		marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); /* Maker 커스텀 설정 */
		marker.setCustomImageResourceId(R.drawable.ic_trip); /* 마커 이미지 변경 */
		marker.setCustomImageAutoscale(false);
		
		/* Maker 생성 */ SubActivity4_map.mapView.addPOIItem(marker);
	}
	
	@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
	protected void onPreExecute() 
	{
		/* ArrayList 객체 생성 */
		name_A = new ArrayList<String>(10); /* 관광지 이름 저장 변수 */
		address_A = new ArrayList<String>(10); /* 관광지 주소 저장 변수 */
		mapX_A = new ArrayList<String>(10); /* 위도 저장 변수 */
		mapY_A = new ArrayList<String>(10); /* 경도 저장 변수 */
		
		/* Dialog 설정 구문 */
		Dialog = new ProgressDialog(mContext);
		Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
		Dialog.setMessage("잠시만 기다려 주세요.");
		Dialog.show();
	}
	
	@Override
	protected Void doInBackground(Void... params) 
	{
		try 
		{
			/* Xml pull 파실 객체 생성 */
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			
				/* StringBuffer 관련 구문 */
				StringBuffer result = new StringBuffer("http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList?ServiceKey="); /* StringBuffer 객체 생성 */
				result.append(API_KEY.kr_GoDATA_API_KEY); result.append("&contentTypeId=12&mapX="); result.append(mapY); result.append("&mapY="); result.append(mapX);
				result.append("&radius=10000&listYN=Y&MobileOS=AND&MobileApp="); result.append(My_AppName); result.append("&arrange=A&numOfRows=30&pageNo=1");
			
			/* 외부 사이트 연결 관련 구문 */
			URL url = new URL(result.toString()); /* URL 객체 생성 */ 
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
					if(tagName.equals("addr1")) 
					{ address_A.add(parser.getText()); /* Log.e("XML - addr1", parser.getText()); */ } /* 주소 */
					if(tagName.equals("mapx")) 
					{ mapY_A.add(parser.getText()); /* Log.e("XML - mapx", parser.getText()); */ } /* 위도 */
					if(tagName.equals("mapy")) 
					{ mapX_A.add(parser.getText()); /* Log.e("XML - mapy", parser.getText()); */ } /* 경도 */
					if(tagName.equals("title")) 
					{ name_A.add(parser.getText()); /* Log.e("XML - title", parser.getText()); */ } /* 관광지명 */
				} 
				else if (eventType == XmlPullParser.END_TAG) { tagName = parser.getName(); if(tagName.equals("response")) { isItemTag = false; }  }
				eventType = parser.next(); /* 다음 XML 객체로 이동 */
			}
		} 
		catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		switch(mInt)
		{
			case (1) : { Listview_data(); break; }
			default : 
			{
				int legth = name_A.size(); /* ArrayList 사이즈를 저장 하는 변수 생성 */
				/* Daum Map에 마커를 찍어주는 구문 */
				for(int count=0; count<legth; count++)
				{
					String Name = name_A.get(count); /* 상호명 */
					String Point = address_A.get(count); /* 주소 */
					double MapX = Double.valueOf(mapX_A.get(count)).doubleValue(); /* 위도 */
					double MapY = Double.valueOf(mapY_A.get(count)).doubleValue(); /* 경도 */
					Daum_Maker(Name, Point, MapX, MapY);
				} break;
			}
		}
		
		Dialog.dismiss(); /* ProgressDialog 종료 */
		super.onPostExecute(result);
	}
	
	/* TODO Custom Adpter Class */
	class MyData 
	{
		private String name; /* 상호명 저장 변수 생성 */
		private String adress; /* 주소 저장 변수 생성 */
		private double Latitude; /* 위도 저장 변수 */
		private double Longitude; /* 경도 저장 변수 */
		
		/* 생성자 메소드 생성 */
		MyData(String name, String adress, double Latitude, double Longitude) 
		{ this.name=name; this.adress=adress; this.Latitude=Latitude; this.Longitude=Longitude; } 
	}
	
	/* TODO Custom Adpter Class */
	class MyAdapter extends BaseAdapter 
	{
		/* Context */
		private Context mContext = null;
		
		/* int */
		private int layoutId = 0;
		
		/* ArrayList */
		private ArrayList<MyData> myDataArr = null;
		
		/* LayoutInflater */
		private LayoutInflater Inflater = null;
		
		/* TextView */
		private TextView[] mTextView = {null, null};
		
		/* Button */
		private Button[] mButton = {null, null};
		
		/* 생성자 메소드 생성 */
		MyAdapter(Context mContext, int layoutId, ArrayList<MyData> myDataArr) 
		{ this.mContext = mContext; this.layoutId=layoutId; this.myDataArr = myDataArr; Inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); }
		
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
			final int pos = position; 
			
			if(convertView == null) { convertView = Inflater.inflate(layoutId, parent, false); }
			
			/* TextView */
			mTextView[0] = (TextView)convertView.findViewById(R.id.main_Name_TextView);
			mTextView[1] = (TextView)convertView.findViewById(R.id.main_destiation_TextView);
			
			/* Button */
			mButton[0] = (Button)convertView.findViewById(R.id.main_Button_Call);
			mButton[1] = (Button)convertView.findViewById(R.id.main_Button_Location);
			
			/* Name TextView */
			mTextView[0].setText(myDataArr.get(position).name);
			/* address TextView */
			mTextView[1].setText(myDataArr.get(position).adress);

			/* 전화 버튼 기능 관련 구문 */
			mButton[0].setOnClickListener(new OnClickListener()
			{
				@Override /* TODO Auto-generated method stub */
				public void onClick(View v) 
				{ Toast.makeText(mContext, "전화번호가 등록되지 않았습니다.", Toast.LENGTH_SHORT).show(); }
			});
			
			/* 위치 버튼 기능 관련 구문 */
			mButton[1].setOnClickListener(new OnClickListener() 
			{
				@Override /* TODO Auto-generated method stub */
				public void onClick(View v) 
				{ 
					Intent intent = new Intent(mContext, SubActivity4_map.class); /* 인텐트 객체 생성 */
					intent.putExtra("Select_Mode", "information_Map"); /* 메인화면 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
					intent.putExtra("Name", myDataArr.get(pos).name); /* 상호명 */
					intent.putExtra("Latitude", myDataArr.get(pos).Latitude); /* 위도 값을 전달 */
					intent.putExtra("Longitude", myDataArr.get(pos).Longitude); /* 경도 값을 전달 */
					mContext.startActivity(intent); /* 인텐트 시작 */
				} 
			}); 
			
			return convertView; 
		}
	}
}