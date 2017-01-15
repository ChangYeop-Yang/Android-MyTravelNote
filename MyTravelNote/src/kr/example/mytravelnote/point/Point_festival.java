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
import android.os.AsyncTask;

/* TODO 주변 위치 행사 관련 */
public class Point_festival extends AsyncTask<Void, Void, Void>
{
	/* APP NAME */
	private final String My_AppName = "MyTravel";
	
	/* TODO ProgressDialog */
	private ProgressDialog Dialog = null;
	/* TODO Context */
	private Context mContext = null;
	
	/* TODO Double */
	private double mapX = 0.0; /* 위도 관련 변수 */
	private double mapY = 0.0; /* 경도 관련 변수 */
	
	/* TODO ArrayList */
	private ArrayList<String> name_A = null; /* 관광지 이름 저장 변수 */
	private ArrayList<String> address_A = null; /* 관광지 주소 저장 변수 */
	private ArrayList<String> mapX_A = null; /* 위도 저장 변수 */
	private ArrayList<String> mapY_A = null; /* 경도 저장 변수 */
	
	/* 생성자 */
	public Point_festival(Context mContext, double mapX, double mapY)
	{
		this.mContext = mContext;
		this.mapX = mapX; /* 위도 */
		this.mapY = mapY; /* 경도 */
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
			result.append(API_KEY.kr_GoDATA_API_KEY); result.append("&contentTypeId=15&mapX="); result.append(mapY); result.append("&mapY="); result.append(mapX);
			result.append("&radius=20000&listYN=Y&MobileOS=AND&MobileApp="); result.append(My_AppName); result.append("&arrange=A&numOfRows=30&pageNo=1");
				
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
		int legth = name_A.size(); /* ArrayList 사이즈를 저장 하는 변수 생성 */
		/* Daum Map에 마커를 찍어주는 구문 */
		for(int count=0; count<legth; count++)
		{
			String Name = name_A.get(count); /* 상호명 */
			String Point = address_A.get(count); /* 주소 */
			double MapX = Double.valueOf(mapX_A.get(count)).doubleValue(); /* 위도 */
			double MapY = Double.valueOf(mapY_A.get(count)).doubleValue(); /* 경도 */
			Daum_Maker(Name, Point, MapX, MapY); /* Daum Map에 Maker를 찍어주는 함수 호출 */
		}
		
		Dialog.dismiss(); /* ProgressDialog 종료 */
		super.onPostExecute(result);
	}
}