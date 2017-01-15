package kr.example.mytravelnote.weather;

import java.io.InputStream;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Weather_xml extends AsyncTask<Double, Void, Void>
{
	/* ProgressDialog */
	private ProgressDialog Dialog = null;
	
	/* Context */
	private Context mContext = null;
	
	/* TextView */
	private TextView mTextView = null;
	
	/* ImageView */
	private ImageView mImageView = null;
	
	/* String */
	private String[] mString = {null, null, null, null}; 

	/* Weather 생성자 - 1 */
	public Weather_xml(Context mContext, TextView mTextView, ImageView mImageView)
	{ 
		this.mContext = mContext; 
		this.mTextView = mTextView;
		this.mImageView = mImageView;
	}
	
	/* Weather 생성자 - 2 */
	public Weather_xml(Context mContext, TextView mTextView, ImageView mImageView, String mString)
	{
		this(mContext, mTextView, mImageView);
		this.mString[3] = mString;
	}
	
	@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
	protected void onPreExecute() 
	{
		if(!(mString[3] == "Map")) /* MainActivity 에서 호출 된 경우에만 발생 */
		{
			/* Dialog 설정 구문 */
			Dialog = new ProgressDialog(mContext);
			Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
			Dialog.setMessage("잠시만 기다려 주세요.");
			Dialog.setCancelable(false);
			Dialog.show();
		}
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Double... params) 
	{
		/* URL */
		URL mURL = null; /* URL 객체 생성 */
		
		try
		{
			/* Xml pull 파실 객체 생성 */
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			
			/* StringBuffer 관련 구문 */
			StringBuffer weather = new StringBuffer("http://api.openweathermap.org/data/2.5/weather?lat="); /* StringBuffer 객체 생성 */
			weather.append(params[0]); /* 위도 */ weather.append("&lon="); weather.append(params[1]); /* 경도 */ weather.append("&units=metric&mode=xml");
			
			/* URL 관련 예외처리 구문 */
			try
			{ mURL = new URL(weather.toString()); /* URL 객체 생성 */ }
			catch(Exception e) 
			{
				e.printStackTrace();
				Log.e("Weather - URL", e.getMessage());
				Toast.makeText(mContext, "OpenWeather에 접속하지 못하였습니다.", Toast.LENGTH_SHORT).show();
			}
			
			InputStream in = mURL.openStream(); /* 해당 URL로 연결 */
			parser.setInput(in, "UTF-8"); /* 외부 사이트 데이터와 인코딩 방식을 설정 */		
			
			/* XML 파싱 관련 변수 관련 구문 */
			int eventType = parser.getEventType(); /* 파싱 이벤트  관련 저장 변수 생성 */
			boolean isItemTag = false;
			String tagName = null; /* Tag의 이름을 저장 하는 변수 생성 */
		
			/* 
			 * END_DOCUMENT : XML 파일의 끝에 도달 하였을 경우 반환
			 * START_TAG : 요소의 시작 태그를 만났을 경우 반환
			 * TEXT : 요소의 텍스트를 만났을 경우 반환
			 * END_TAG : 요소의 종료 태그를 만났을 경우 반환
			 * */

			/* XML 문서를 읽어 들이는 구문 */
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{ 
				if(eventType == XmlPullParser.START_TAG) 
				{
					tagName = parser.getName();
					if(tagName.equals("current")) { isItemTag = true; } /* XML channel 시작과 끝부분 */
					
					if(tagName.equals("temperature")) 
					{ mString[0] = parser.getAttributeValue(null, "value"); } /* 온도 */
					
					if(tagName.equals("weather")) 
					{ 
						mString[1] = parser.getAttributeValue(null, "value"); /* 날씨 정보 */
						mString[2] = parser.getAttributeValue(null, "icon"); /* 날씨 아이콘 */
					} 
				}
				else if (eventType == XmlPullParser.END_TAG) 
				{ 
					tagName = parser.getName(); 
					if(tagName.equals("current")) { isItemTag = false; }
				}
				eventType = parser.next(); /* 다음 XML 객체로 이동 */
			}
		} 
		catch(Exception e)
		{
			e.printStackTrace();
			Log.e("Weather - XML", e.getMessage());
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		/* StringBuffer 객체 생성 - 날씨 온도와 정보 */
		StringBuffer weather_temp = new StringBuffer();
		weather_temp.append(mString[1]); weather_temp.append(" ("); weather_temp.append(Math.round(Double.valueOf(mString[0]).doubleValue())); weather_temp.append("℃)");
		mTextView.setText(weather_temp.toString()); /* 텍스트 뷰에 텍스트 연결 */
		    	
		/* StringBuffer 객체 생성 - 날씨 아이콘 */
		StringBuffer weather_icon = new StringBuffer(); /* StringBuffer 객체 생성 */
		weather_icon.append("@drawable/"); weather_icon.append("ic_"); weather_icon.append(mString[2]); /* Icon 경로를 조합 해주는 구문 */
		int resID = mContext.getResources().getIdentifier(weather_icon.toString(), "drawable", mContext.getPackageName()); /* 해당 아이콘에 경로를 조회 및 맞춰주는 구문 */
		mImageView.setImageResource(resID); /* 이미지뷰에 띄우기 */
		
		if(!(mString[3] == "Map")) { Dialog.dismiss(); /* Dialog 종료 부분 */ }
		super.onPostExecute(result);
	}
}