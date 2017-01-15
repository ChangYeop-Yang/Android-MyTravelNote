package kr.example.mytravelnote.daum;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import kr.example.mytravelnote.apikey.API_KEY;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

/* TODO Daum에 위치에 해당하는 이미지를 불러오는 클래스 */
public class Daum_Location extends AsyncTask<Void, Void, Void>
{
	/* ArrayList */
	private ArrayList<String> name_A = null;
	private ArrayList<String> mapX_A = null;
	private ArrayList<String> mapY_A = null;
	
	/* ImageView */
	private ImageView mImageView = null;
	
	/* ProgressDialog */
	private ProgressDialog Dialog = null;
	
	/* Context */
	private Context mContext = null;
	
	/* Double */
	private double[] mDouble = {0.0,0.0};
	
	/* String */
	private String mString = null;
	
	/* 생성자 */
	public Daum_Location(ImageView mImageView, Context mContext, String mString, double mapX, double mapY)
	{
		this.mContext = mContext;
		this.mImageView = mImageView;
		this.mString = mString;
		mDouble[0] = mapX; /* 위도 */
		mDouble[1] = mapY; /* 경도 */
	}

	@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
	protected void onPreExecute() 
	{
		/* Dialog 설정 구문 */
		Dialog = new ProgressDialog(mContext);
		Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
		Dialog.setMessage("지도 이미지를 다운로드 중입니다.");
		Dialog.show();
		
		/* ArrayList */
		name_A = new ArrayList<String>(10);
		mapX_A = new ArrayList<String>(10);
		mapY_A = new ArrayList<String>(10);
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
			StringBuffer result = new StringBuffer("https://apis.daum.net/local/v1/search/category.xml?location="); /* StringBuffer 객체 생성 */
			result.append(mDouble[0]); result.append(","); result.append(mDouble[1]); /* 위도와 경도 */
			result.append("&code="); result.append(mString); result.append("&redius=1000");
			result.append("&apikey="); result.append(API_KEY.Daum_API_KEY); /* Daum API KEY */ 
					
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
				if(tagName.equals("channel")) { isItemTag = true; } /* XML channel 시작과 끝부분 */
			} else if (eventType == XmlPullParser.TEXT && isItemTag) 
			{
				if(tagName.equals("title"))
				{ name_A.add(parser.getText()); /* Log.e("XML - title", parser.getText()); */ } /* 이름 */
				if(tagName.equals("latitude"))
				{ mapX_A.add(parser.getText()); /* Log.e("XML - latitude", parser.getText()); */ } /* 위도 */
				if(tagName.equals("longitude"))
				{ mapY_A.add(parser.getText()); /* Log.e("XML - longitude", parser.getText()); */ } /* 경도 */
			} else if (eventType == XmlPullParser.END_TAG) { tagName = parser.getName(); if(tagName.equals("channel")) { isItemTag = false; } }
			eventType = parser.next(); /* 다음 XML 객체로 이동 */
		}
		} catch (Exception e) { Log.e("Daum XML", e.getMessage()); }
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		Dialog.dismiss();
		mapStatic_Downlad(); /* 이미지를 Google서버로 부터 받아오는 함수 호출 */
		super.onPostExecute(result);
	}
	
	/* TODO 이미지를 Google서버로 부터 받아오는 함수 */
	private void mapStatic_Downlad()
	{
		/* Google map에 대한 StringBuffer 객체 생성 */
		StringBuffer Google_string = new StringBuffer("http://maps.googleapis.com/maps/api/staticmap?center=");
		Google_string.append(mDouble[0] + "," + mDouble[1]); /* 카메라 이동 위도와 경도 */
		Google_string.append("&zoom=16&size=320x320&scale=2&maptype=roadmap&");
		
		int legth = name_A.size(); /* ArrayList 변수 사이즈 저장 변수 생성 */
		for(int count=0; count<legth; count++) 
		{ 
			String MapX = mapX_A.get(count); /* 위도 */
			String MapY = mapY_A.get(count); /* 경도 */
			if( (!MapX.equals("\n")) && (!MapY.equals("\n")) ) {
			Google_string.append("markers=color:red|label:F|");
			Google_string.append(MapX + "," + MapY); Google_string.append("&"); /* 마커 설정 위도와 경도 */ }
		}
		Google_string.append("%7C11211&sensor=false"); Google_string.append("&key=");
		Google_string.append(API_KEY.Google_Static_Key); /* Google API Key */
		
		try
		{
			URL mURL = new URL(Google_string.toString()); /* URL 객체 생성 */
			HttpURLConnection http = (HttpURLConnection)mURL.openConnection(); /* HttpURLConnection 객체 생성 및 접속 */
			http.setDoInput(true);
			http.connect(); /* 서버와의 연결 */
			
			InputStream is = http.getInputStream(); /* InputStream 객체 생성 */
			Bitmap mBitmap = BitmapFactory.decodeStream(is); /* 서버로 부터 받은 이미지를 Bitmap에 저장 */
			
			mImageView.setImageBitmap(mBitmap); /* Bitmap을 이미지 뷰에 변경 */
		}
		catch(Exception e)
		{
			Toast.makeText(mContext, "Google에서 이미지를 지도 이미지를 받아 올 수 없습니다.", Toast.LENGTH_SHORT).show();
			Log.e("GoogleStaticMap", e.getMessage());
		}
	}
}