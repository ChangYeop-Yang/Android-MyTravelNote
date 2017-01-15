package kr.example.mytravelnote.daum;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import kr.example.mytravelnote.SubActivity4_map;
import kr.example.mytravelnote.apikey.API_KEY;
import kr.net.mytravelnote.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Daum_Zoon extends AsyncTask<Void, Void, Void>
{
	/* Context */
	private Context mContext = null;
	
	/* ArrayList */
	private ArrayList<String> mArrayList_Name = null;
	private ArrayList<String> mArrayList_MapX = null;
	private ArrayList<String> mArrayList_MapY = null;
	private ArrayList<String> mArrayList_Tel = null;
	
	/* String */
	private String mString = null;
	
	/* ListView */
	private ListView mListView = null;
	
	/* Double */
	private double[] mDouble = {0.0, 0.0};
	
	/* Daum_Zoon 생성자 */
	public Daum_Zoon(Context mContext, String mString, ListView mListView, double MapX, double MapY)
	{
		this.mString = mString;
		this.mContext = mContext;
		this.mListView = mListView;
		mDouble[0] = MapX; mDouble[1] = MapY;
	}
	
	/* TODO XML 파싱 데이터를 리스트뷰에 추가시켜 주는 메소드 */
	private void Listview_data() 
	{    
		final int legnth = mArrayList_Name.size();
		String[] mString = {null, null, null};
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
        	if(!mArrayList_Name.get(count).equals("\n")) /* \n 입력 방지 구문 */
        	{	
        		/* Double */
        		mDouble[0] = Double.valueOf(mArrayList_MapX.get(count)).doubleValue();
        		mDouble[1] = Double.valueOf(mArrayList_MapY.get(count)).doubleValue();
        		
        		/* String */
        		mString[0] = mArrayList_Name.get(count);
        		mString[1] = getCalcDistance(this.mDouble[0], this.mDouble[1], mDouble[0], mDouble[1]);
        		mString[2] = mArrayList_Tel.get(count);
        		
        		dataArr.add(new MyData(mString[0], mString[1], mString[2], mDouble[0], mDouble[1])); 
        	}
        }

        MyAdapter mAdapter = new MyAdapter(mContext, R.layout.activity_main_listview, dataArr);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(mAdapter);
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
	
	@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
	protected void onPreExecute() 
	{
		/* ArrayList */
		mArrayList_Name = new ArrayList<String>(10);
		mArrayList_MapX = new ArrayList<String>(10);
		mArrayList_MapY = new ArrayList<String>(10);
		mArrayList_Tel = new ArrayList<String>(10);
		
		super.onPreExecute();
	}
	
	@Override /* AsyncTask 실행 부분 */
	protected Void doInBackground(Void... params) 
	{
		try 
		{
			/* Xml pull 파실 객체 생성 */
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();

				/* StringBuffer 관련 구문 */
				StringBuffer Daum = new StringBuffer("https://apis.daum.net/local/v1/search/category.xml?location="); /* StringBuffer 객체 생성 */
				Daum.append(mDouble[0]); Daum.append(","); Daum.append(mDouble[1]); /* 위도와 경도 */ Daum.append("&code="); Daum.append(mString);
				Daum.append("&apikey="); Daum.append(API_KEY.Daum_API_KEY); /* Daum API KEY */
				
				/* 외부 사이트 연결 관련 구문 */
				URL url = new URL(Daum.toString()); /* URL 객체 생성 */ 
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
					{ mArrayList_Name.add(parser.getText()); /* Log.e("XML - title", parser.getText()); */ } /* 상호명 */
					if(tagName.equals("latitude"))
					{ mArrayList_MapX.add(parser.getText()); /* Log.e("XML - latitude", parser.getText()); */ } /* 위도 */
					if(tagName.equals("longitude"))
					{ mArrayList_MapY.add(parser.getText()); /* Log.e("XML - longitude", parser.getText()); */ } /* 경도 */
					if(tagName.equals("phone")) 
					{ mArrayList_Tel.add(parser.getText()); /* Log.e("XML - phone", parser.getText()); */ } /* 전화번호 */
				} else if (eventType == XmlPullParser.END_TAG) { tagName = parser.getName(); if(tagName.equals("channel")) { isItemTag = false; } }
				eventType = parser.next(); /* 다음 XML 객체로 이동 */
			}
		} 
		catch (Exception e) 
		{ 
			Log.e("Daum_Zoon - ", e.getMessage());
			e.printStackTrace();
			Toast.makeText(mContext, "Daum에서 주변정보를 가져오지 못하였습니다.", Toast.LENGTH_SHORT).show();
		} return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		Listview_data(); /* XML 파싱 데이터를 리스트뷰에 추가시켜 주는 메소드 호출 */
		super.onPostExecute(result);
	}
	
	/* TODO Custom Adpter Class */
	class MyData 
	{
		private String name; /* 상호명 저장 변수 생성 */
		private String adress; /* 주소 저장 변수 생성 */
		private String tel; /* 전화 번호 저장 변수 생성 */
		private double Latitude; /* 위도 저장 변수 */
		private double Longitude; /* 경도 저장 변수 */
		
		/* 생성자 메소드 생성 */
		MyData(String name, String adress, String tel, double Latitude, double Longitude) 
		{ this.name=name; this.adress=adress; this.tel=tel; this.Latitude=Latitude; this.Longitude=Longitude; } 
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
				{ 
					if(!myDataArr.get(pos).tel.equals(null))
					{ Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+ myDataArr.get(pos).tel)); mContext.startActivity(intent); }
					else { Toast.makeText(mContext, "전화번호가 등록되지 않았습니다.", Toast.LENGTH_SHORT).show(); }
				}
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