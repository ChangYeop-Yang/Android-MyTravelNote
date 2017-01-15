package kr.example.mytravelnote;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.example.mytravelnote.common.Vibration;
import kr.example.mytravelnote.region.Area_region;
import kr.net.mytravelnote.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SubActivity13_comment extends Activity
{
    /* String */
	private final static String SERVER_URL = "http://Marihome.iptime.org/travel/db_insert.php"; /* 서버 주소를 저장하는 변수 생성 */
	private final static String SERVER_DOWNLOAD_URL = "http://Marihome.iptime.org/travel/db_select.php"; /* 서버 주소를 저장하는 변수 생성 */

    private String[] mStringR = {null, null, null, null};
    /*
        1. mStringR[0] EditText Value
        2. mStringR[1] 현재 위치 주소
        3. mStringR[2] 액션타 타이을 주소
        4. mString[3] 현재 위치 코드
     */

	/* TextView */
	private TextView mTextView = null; /* 현제 위치 안내 관련 텍스트 뷰 */
    
	/* Button */
    private Button[] mButton = {null, null};
    /*
        1. 전송 버튼
        2. 새로고침 버튼
     */

	/* EditText */
	private EditText mEditText = null; /* 사용자에게 글을 입력받는 EditText */
	
	/* ProgressDialog */
	private ProgressDialog mProgressDialog = null;
    
    /* Context */
    private final Context mContext = SubActivity13_comment.this;
	
	/* 좌표 관련 변수 */
	private double MapX = 0.0; /* 위도 */
	private double MapY = 0.0; /* 경도 */
		
	/* XML 관련 ArrayList 변수 */
	private ArrayList<String> Name = new ArrayList<String>(10); /* 이름 */
	private ArrayList<String> Talk = new ArrayList<String>(10); /* 대화 */
	private ArrayList<String> Date = new ArrayList<String>(10); /* 날짜 */
	private ArrayList<String> Point = new ArrayList<String>(10); /* 장소 */
		
		@Override
	    public void onCreate(Bundle savedInstanceState) 
		{
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_comment);
	        
	        /* Preferences 설정 값을 가져오는 구문 */
	        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); /* 설정 값을 가져오기 위하여 Preferences 객체 생성 */
	        boolean actionBarIs = mSharedPreferences.getBoolean("useUserName", false); /* 설정 값에서 설정 된 사용자 이름 사용 구분 가져오는 구문 */

	        /* 설정 값에서 설정 된 사용자 이름을 가져오는 구문 */
            mStringR[2] = actionBarIs == true ? mSharedPreferences.getString("userName", "여행일기") : "여행일기";

            Intent intent = getIntent(); /* Intent 객체 생성 */
	        MapX = intent.getDoubleExtra("MapX", 0.0); /* 위도 값 */
	        MapY = intent.getDoubleExtra("MapY", 0.0); /* 경도 값 */
	        
	        /* TextView */
	        mTextView = (TextView)findViewById(R.id.comment_information); /* 현제 접속 된 지역 주소 표시 */
	        /* Button */
            mButton[0] = (Button)findViewById(R.id.comment_send_but); /* 전송 버튼 */
            mButton[1] = (Button)findViewById(R.id.comment_refresh_but); /* 새로고침 버튼 */
            mButton[1].setOnClickListener(new OnClickListener()
	        {
				@Override /* TODO Auto-generated method stub */
				public void onClick(View v) 
				{
					new TransferData().execute("R"); /* 서버에서 데이터를 가져오는 구문 */
                    Vibration.Vibrate(mContext);

			        /* 전 자료를 삭제 해주는 구문 */
			        if(Name.isEmpty()==false)
					{
						Name.clear(); Date.clear();
						Talk.clear(); Point.clear();
					}
				}
	        });
            mButton[0].setEnabled(false);
	        /* EditText */
            mEditText = (EditText)findViewById(R.id.comment_write_edit); /* 입력 부분 */
	        /* EditText 글자 수 관련 기능 정의 */
            mEditText.addTextChangedListener(new TextWatcher()
	        {
				@Override /* TODO Auto-generated method stub */
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				@Override /* TODO Auto-generated method stub */
				public void onTextChanged(CharSequence s, int start, int before, int count) {}
				@Override /* TODO Auto-generated method stub */
				public void afterTextChanged(Editable s) /* Text가 변경이 되었을 경우 */
				{ 
					if(mEditText.getText().length()!=0)
					{ mButton[0].setEnabled(true); /* 공백이므로 버튼 터치 잠금 해제 */ }
					else { mButton[0].setEnabled(false); /* 공백이므로 버튼 터치 잠금 */ }
				}				
	        });
	        /* EditText Enter 기능 정의 */
            mEditText.setOnKeyListener(new OnKeyListener()
	        {
				@Override /* TODO Auto-generated method stub */
				public boolean onKey(View v, int keyCode, KeyEvent event) 
				{
					if(keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction())
					{
                        if(mEditText.getText().length()!=0) /* 공백이 아닐 경우 */
						{ mStringR[0] = mEditText.getText().toString(); }
						else { Toast.makeText(mContext, "글이 입력 되지 않았습니다.", Toast.LENGTH_SHORT).show(); }
						return true;
					}
					return false;
				}	
	        });
	        	      
	        /* 전송 버튼 정의 구문 */
            mButton[0].setOnClickListener(new OnClickListener()
	        {
				@SuppressLint("SimpleDateFormat") 
				@Override /* TODO Auto-generated method stub */
				public void onClick(View v)
				{
					if(mEditText.getText().length()!=0) /* 공백이 아닐 경우 */
					{ 
						/* 현제 날짜를 구해오는 구문 */
						Date date = new Date(); /* 현제 날짜 및 Date 객체 생성 */
						SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); /* 날짜 저장을 위해 포멧 설정 */

                        /* Vibration */
                        Vibration.Vibrate(mContext);

                        mStringR[0] = mEditText.getText().toString(); /* 사용자가 다른 방법으로 입력 하였을 경우에 값 저장 */
						new TransferData().execute("W", mStringR[2], mStringR[0], mSimpleDateFormat.format(date).toString(), mStringR[1], mStringR[3]); /* 쓰기관련 Network 관련  AsyncTask */

                        mEditText.setText(null); /* 재사용을 위해 공백으로 값 변경 */
                        mButton[0].setEnabled(false); /* 공백이므로 버튼 터치 잠금 */
					}
				}
	        });
	        
	        Geocoder_find(MapX, MapY); /* 위도와 경도를 통해서 지오코딩을 하여서 구글에서 주소를 반환하는 함수 */
	        new TransferData().execute("R"); /* 서버에서 데이터를 가져오는 구문 */
		}
		
		/* TODO 위도와 경도를 통해서 지오코딩을 하여서 구글에서 주소를 반환하는 함수 */ 
		private void Geocoder_find(double Latitude, double Longitude) /* Latitude(위도) 와 Longitude(경도)의 인자를 받아서 사용하는 함수 */
		{
			/* Google Geocoder 을 위한 객체 생성 */
			Geocoder geocoder = new Geocoder(mContext);
			/* 주소 관련 변수 */
			List<Address> list = null;
			
			try { list = geocoder.getFromLocation(Latitude, Longitude, 1); }
			/* 위도와 경도를 이용하여서 해당 주소를 저장 */
			catch (NumberFormatException e) { e.printStackTrace(); }
			catch (IOException e) { e.printStackTrace(); }
			
			/* 해당 지역의 정보를 받은 뒤 작동이 되는 구문 */
			if( (list != null) && (list.size()>0) ) 
			{
				Address address = list.get(0);
				
				StringBuffer result = new StringBuffer(); /* StringBuffer 객체 생성 */
				result.append(address.getAdminArea()); result.append(" ");
				result.append(address.getLocality()); result.append(" "); 
				result.append(address.getThoroughfare()); result.append(" ");
				
				/* 지역 번호 매칭 */
				Area_region mArea = new Area_region(); /* Area_region 객체 생성 */
                mStringR[3] = String.valueOf(mArea.Area_Codefind(address.getAdminArea())); /* 현제 구역 저장 */

                mStringR[1] = result.toString(); /* 현제 장소 저장 */
				mTextView.setText(result.toString()); /* TextView 설정 */	
			}
		}
		
		@Override
		public void onBackPressed()
		{ finish(); }
		
		/* TODO Custom Adpter Class */
		class MyData {
			String name; /* 상호명 저장 변수 생성 */
			String talk; /* 주소 저장 변수 생성 */
			String point; /* 전화 번호 저장 변수 생성 */
			String date; /* 남은 거리 저장 변수 생성 */
			/* 생성자 메소드 생성 */
			MyData(String name, String talk, String point, String date) 
			{ this.name=name; this.talk=talk; this.point=point; this.date=date; } }
		
		/* TODO Custom Adpter Class */
		class MyAdapter extends BaseAdapter {
			Context context;
			int layoutId;
			ArrayList<MyData> myDataArr;
			LayoutInflater Inflater;
			/* 생성자 메소드 생성 */
			MyAdapter(Context context, int layoutId, ArrayList<MyData> myDataArr) 
			{ this.context=context; this.layoutId=layoutId; this.myDataArr = myDataArr; Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); }
			
			@Override /* TODO Auto-generated method stub */
			public int getCount() { return myDataArr.size(); }

			@Override /* TODO Auto-generated method stub */
			public Object getItem(int position) { return myDataArr.get(position).name; }

			@Override /* TODO Auto-generated method stub */
			public long getItemId(int position) { return position; }

			@Override /* TODO Auto-generated method stub */
			public View getView(int position, View convertView, ViewGroup parent) {							
				if(convertView == null) { convertView = Inflater.inflate(layoutId, parent, false); }
				/* Name TextView */
				final TextView name_text = (TextView)convertView.findViewById(R.id.Comment_name);
				name_text.setText(myDataArr.get(position).name);
				/* talk TextView */
				final TextView talk_text = (TextView)convertView.findViewById(R.id.Comment_text);
				talk_text.setText(myDataArr.get(position).talk);
				/* point TextView */
				final TextView point_text = (TextView)convertView.findViewById(R.id.Comment_point);
				point_text.setText(myDataArr.get(position).point);
				/* date TextView */
				final TextView date_text = (TextView)convertView.findViewById(R.id.Comment_date);
				date_text.setText(myDataArr.get(position).date);
				
				return convertView; } }
		
		/* TODO XML 파싱 데이터를 리스트뷰에 추가시켜 주는 함수 */
		private void insertListView(int length) {
			/* ListView 관련 구문 */
	        ListView list = (ListView) findViewById(R.id.comment_listview); /* ListView 객체 생성 */
	        ArrayList<MyData> dataArr = new ArrayList<MyData>();
	        
	        /* Item을 증가 시켜 주는 구문 */
	        for(int count=0; count<length; count++)
	        {
	        	/* 제목과 장소를 저장하는 String 변수 생성 */
	        	String title=Name.get(count);  /* 상호명 */
	        	String point=Point.get(count); /* 장소 */
	        	String talk=Talk.get(count); /* 전화번호 */
	        	String date=Date.get(count); /* URL */
	            /* Item을 추가 해주는 구문 */
	        	dataArr.add(new MyData(title, talk, point, date) );  
	        }
	        MyAdapter mAdapter = new MyAdapter(this, R.layout.activity_comment_listview, dataArr);
	        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        list.setAdapter(mAdapter);
		}
		
		
	/* Server와 통신을 하기 위한 AsyncTask 클래스 */
	private class TransferData extends AsyncTask<String, Void, String>
	{
		@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
		protected void onPreExecute() 
		{
			/* ProgressDialog 설정 구문 */
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
				switch(params[0])
				{
					case ("W") : /* Server에 데이터를 보내는 구문 */
					{
						HttpClient client = new DefaultHttpClient(); /* 서버와 주고 데이터를 주고 받기 위하여  HttpClient 객체 생성 */
						HttpPost post = new HttpPost(SERVER_URL); /* 서버와 연결 및 HttpPost 객체 생성 */
					
						/* Date를 서버로 보낼 데이터를 보내는 구문 */
						List<NameValuePair> param = new ArrayList<NameValuePair>(); /* NameValuePair(키, 쌍)으로 이루어진 List 객체 생성 */
						param.add(new BasicNameValuePair("name", params[1])); /* 이름 */
						param.add(new BasicNameValuePair("talk", params[2])); /* 대화 */
						param.add(new BasicNameValuePair("date", params[3])); /* 날짜 */
						param.add(new BasicNameValuePair("point", params[4])); /* 장소 */
						param.add(new BasicNameValuePair("zoon", params[5])); /* 구역 */
				
						/* 문자 Encoding 구문 */
						UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, HTTP.UTF_8); /* 한글이 깨지지 않도록 캐릭터 셋 지정 */
						post.setEntity(ent); /* 캐릭터셋 설정 - UTF-8 */
				
						HttpResponse responsePOST = client.execute(post);
						HttpEntity resEntity = responsePOST.getEntity();
				
						if(resEntity != null) { Log.e("HTTP", EntityUtils.toString(resEntity)); } break;
					}
					case ("R") : /* XML 파싱 구문 */
					{
							try {
								/* Xml pull 파실 객체 생성 */
								XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
								factory.setNamespaceAware(true);
								XmlPullParser parser = factory.newPullParser();
									
									StringBuffer XML_Reslut = new StringBuffer(); /* StringBuffer 객체 생성 */
									XML_Reslut.append(SERVER_DOWNLOAD_URL); XML_Reslut.append("?zoon="); XML_Reslut.append(mStringR[3]);
								
									/* 외부 사이트 연결 관련 구문 */
									URL url = new URL(XML_Reslut.toString()); /* URL 객체 생성 */ 
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
										if(tagName.equals("comment")) { isItemTag = true; } /* XML channel 시작과 끝부분 */
									} else if (eventType == XmlPullParser.TEXT && isItemTag) 
									{
										if(tagName.equals("name")) /* 이름 */
										{ Name.add(parser.getText()); }
										if(tagName.equals("talk")) /* 대화 */
										{ Talk.add(parser.getText()); }
										if(tagName.equals("date")) /* 날짜 */
										{ Date.add(parser.getText()); }
										if(tagName.equals("point")) /* 장소 */
										{ Point.add(parser.getText()); }
									} else if (eventType == XmlPullParser.END_TAG) { tagName = parser.getName(); if(tagName.equals("comment")) { isItemTag = false; } }
									eventType = parser.next(); /* 다음 XML 객체로 이동 */
								}
							} catch (Exception e) { e.printStackTrace(); } }
							/* 
							 * END_DOCUMENT : XML 파일의 끝에 도달 하였을 경우 반환
							 * START_TAG : 요소의 시작 태그를 만났을 경우 반환
							 * TEXT : 요소의 텍스트를 만났을 경우 반환
							 * END_TAG : 요소의 종료 태그를 만났을 경우 반환
							 * */
						break;
					}
			} 
			catch(UnsupportedEncodingException e) { e.printStackTrace(); }
			catch(IOException e) { e.printStackTrace(); }
			 
			return params[0];
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			if(result.equals("R")) { insertListView(Name.size()); /* 리스트 뷰를 그려주는 함수 호출 */ }
			mProgressDialog.dismiss(); /* mProgressDialog 종료 부분 */
			super.onPostExecute(result);
		}
	}
}