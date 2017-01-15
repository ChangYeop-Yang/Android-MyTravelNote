package kr.example.mytravelnote.tour;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import kr.example.mytravelnote.SubActivity4_map;
import kr.example.mytravelnote.SubActivity9_minutely;
import kr.example.mytravelnote.apikey.API_KEY;
import kr.net.mytravelnote.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/* TODO 한국관광공사에서 XML 형식으로 정보를 가져오는 클래스 */
public class Tour_find extends AsyncTask<Integer, Void, Integer>
{
	/* APP NAME */
	private final String My_AppName = "MyTravel";
	
	/* Handler */
	private Handler handler = new Handler();
	
	/* ProgressDialog */
	private ProgressDialog Dialog = null;
	
	/* Context */
	private Context mContext = null;
	
	/* ListView */
	private ListView mListView = null;
	
	/* TODO ArrayList */
	private ArrayList<String> address_A = null; /* 관광지 주소 저장 변수 */
	private ArrayList<String> tel_A = null; /* 관광지 전화번호 저장 변수 */
	private ArrayList<String> title_A = null; /* 관광지 이름 저장 변수 */
	private ArrayList<String> image_A = null; /* 관광지 썸네일 이미지 저장 변수 */
	private ArrayList<String> mapX_A = null; /* 관광지 위도 저장 변수 */
	private ArrayList<String> mapY_A = null; /* 관광지 경도 저장 변수 */
	private ArrayList<String> id_A = null; /* 해당 관광지 ID 저장 변수 */
	
	/* TODO int */
	private int total_row = 0; /* 총페이지 수 */
	private int[] mRegion = {0,0,0};
	
	/* TODO double */
	private double mapX = 0.0; /* 위도 */
	private double mapY = 0.0; /* 경도 */
	
	/* 생성자 */
	public Tour_find(Context mContext, ListView mListView, double mapX, double mapY, int[] mRegion)
	{
		this.mContext = mContext;
		this.mListView = mListView;
		this.mapX = mapX; /* 위도 */
		this.mapY = mapY; /* 경도 */
		this.mRegion = mRegion;
	}
	
	@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
	protected void onPreExecute() 
	{
		/* Dialog 설정 구문 */
		Dialog = new ProgressDialog(mContext);
		Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
		Dialog.setMessage("잠시만 기다려 주세요.");
		Dialog.show();
		
		/* ArrayList */
		address_A = new ArrayList<String>(10); /* 관광지 주소 저장 변수 */
		tel_A = new ArrayList<String>(10); /* 관광지 전화번호 저장 변수 */
		title_A = new ArrayList<String>(10); /* 관광지 이름 저장 변수 */
		image_A = new ArrayList<String>(10); /* 관광지 썸네일 이미지 저장 변수 */
		mapX_A = new ArrayList<String>(10); /* 관광지 위도 저장 변수 */
		mapY_A = new ArrayList<String>(10); /* 관광지 경도 저장 변수 */
		id_A = new ArrayList<String>(10); /* 해당 관광지 ID 저장 변수 */
		
		super.onPreExecute();
	}
	
	@Override
	protected Integer doInBackground(Integer... params) 
	{
		try {
			/* Xml pull 파실 객체 생성 */
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			
			StringBuffer Str_result = null; /* StringBuffer 객체 생성 */

				/* StringBuffer 관련 구문 */
				Str_result = new StringBuffer("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?ServiceKey="); /* StringBuffer 객체 생성 */
				/* API KEY */ Str_result.append(API_KEY.kr_GoDATA_API_KEY); Str_result.append("&contentTypeId="); /* 관관 타입 코드 */ Str_result.append(params[0]);
				Str_result.append("&areaCode="); Str_result.append(params[1]); /* 지역 코드 */ Str_result.append("&sigunguCode="); Str_result.append(params[2]); /* 시/구/군 의 값이 사용자에 의해 입력 받았을 경우 */
				Str_result.append("&cat1=&cat2=&cat3=&listYN=Y&MobileOS=AND&MobileApp="); Str_result.append(My_AppName); Str_result.append("&arrange=O&numOfRows=10&pageNo="); Str_result.append(params[3]);
					
				/* 외부 사이트 연결 관련 구문 */
				URL url = new URL(Str_result.toString()); /* URL 객체 생성 */ 
			 	InputStream in = url.openStream(); /* 해당 URL로 연결 */
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
					if(tagName.equals("response")) { isItemTag = true; } /* XML channel 시작과 끝부분 */
				} 
				else if (eventType == XmlPullParser.TEXT && isItemTag) 
				{
					if(tagName.equals("addr1")) 
					{ address_A.add(parser.getText()); /* Log.e("XML - address", parser.getText()); */ } /* 장소 */
					if(tagName.equals("contentid")) 
					{ id_A.add(parser.getText()); /* Log.e("XML - contentid", parser.getText()); */ } /* 해당 관광지 ID */
					if(tagName.equals("firstimage2")) 
					{ image_A.add(parser.getText()); /* Log.e("XML - firstimage2", parser.getText()); */ } /* 상호명 */
					if(tagName.equals("mapx"))
					{ mapY_A.add(parser.getText()); /* Log.e("XML - Longitude", parser.getText()); */ } /* 위도 */
					if(tagName.equals("mapy"))
					{ mapX_A.add(parser.getText()); /* Log.e("XML - Latitude", parser.getText()); */ } /* 경도 */
					if(tagName.equals("tel"))
					{ tel_A.add(parser.getText()); /* Log.e("XML - tel", parser.getText()); */ } /* 전화번호 */
					if(tagName.equals("title"))
					{ title_A.add(parser.getText()); /* Log.e("XML - title", parser.getText()); */ } /* 상호명 */
					if(tagName.equals("totalCount")) 
					{ total_row=Integer.parseInt(parser.getText()); /* Log.e("XML - Rows", parser.getText()); */ } /* 현제 조회 된 총 갯수 */ 
				} 
				else if (eventType == XmlPullParser.END_TAG) 
				{ 
					tagName = parser.getName(); 
					if(tagName.equals("response")) { isItemTag = false; }
				}
				eventType = parser.next(); /* 다음 XML 객체로 이동 */
			}
		} catch (Exception e) { e.printStackTrace(); }
		return total_row;
	}
	
	@Override
	protected void onPostExecute(Integer result)
	{
		ArrayList_Size_Check(); /* ArrayList Size Check 함수 */
		ListView_Add(); /* ListView 아이템을 추가해주는 함수 */
		
		Dialog.dismiss(); /* Dialog 종료 부분 */
		super.onPostExecute(result);
	}
	
	/* TODO ArrayList Size Check 함수 */
	private void ArrayList_Size_Check() 
	{
		int length = title_A.size(); /* 제목 ArrayList 변수 길이 저장 */
		int image_length = image_A.size(); /* 이미지 ArrayList 변수 길이 저장 */
		int tel_length = tel_A.size(); /* 전화번호 ArrayList 변수 길이 저장 */
		int mapX_length = mapX_A.size(); /* 위도 ArrayList 변수 길이 저장 */
		int mapY_length = mapY_A.size(); /* 경도 ArrayList 변수 길이 저장 */
		
		/* ArrayList_Size가 맞지 않을 경우 맞춰 주는 구문 */
		while( ((image_length!=length) || (tel_length!=length)) || ((mapX_length!=length) || (mapY_length!=length)) )
		{
			if(mapX_length!=length) { mapX_A.add("0.0"); mapX_length++; } /* 위도 저장 백터 변수 추가 데이터 저장 */
			if(mapY_length!=length) { mapY_A.add("0.0"); mapY_length++; } /* 경도 저장 백터 변수 추가 데이터 저장 */
			if(image_length!=length) { image_A.add("http://api.visitkorea.or.kr/static/images/common/noImage.gif"); image_length++; } /* 이미지 저장 백터 변수 추가 데이터 저장 */
			if(tel_length!=length) { tel_A.add("Not Tell"); tel_length++; } /* 전화번호 저장 백터 변수 추가 데이터 저장 */
		}
	}
	
	/* TODO ListView 아이템을 추가해주는 함수 */
	private void ListView_Add()
	{
		/* ListView 관련 구문 */
        ArrayList<MyData> dataArr = new ArrayList<MyData>();
        
        int length = title_A.size(); /* title_A 사이즈 저장 */
        /* Item을 증가 시켜 주는 구문 */
        for(int count=0; count<length; count++) 
        {
        	/* 제목과 장소를 저장하는 String 변수 생성 */
        	String title=title_A.get(count);  /* 상호명 */
        	String address=address_A.get(count); /* 장소 */
        	String tel=tel_A.get(count); /* 전화번호 */
        	String url=image_A.get(count); /* URL */
        	String contentid=id_A.get(count); /* 관광지 ID */
        		
        	/* 남은 거리 계산 하는 구문 */
        	double mpaX=Double.valueOf(mapX_A.get(count)).doubleValue(); /* 위도 */
            double mpaY=Double.valueOf(mapY_A.get(count)).doubleValue(); /* 경도 */
            String distance = CalcDistance(this.mapX, this.mapY, mpaX, mpaY); /* 좌표를 이용한 거리 계산 함수 호출 */
            /* Item을 추가 해주는 구문 */
        	dataArr.add(new MyData(url, title, address, tel, distance, contentid, mpaX, mpaY) ); 
        }
        
        /* View ID 생성 구문 */
        MyAdapter mAdapter = new MyAdapter(mContext, R.layout.activity_tour_listview, dataArr);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(mAdapter);
	}
	
	/* TODO 좌표를 이용한 거리 계산 함수 */
	private String CalcDistance(double lat1, double lon1, double lat2, double lon2) 
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
	
    class MyData
    {
    	private String url; /* Image Icon 저장 변수 생성 */
    	private String name; /* 상호명 저장 변수 생성 */
    	private String adress; /* 주소 저장 변수 생성 */
    	private String tel; /* 전화 번호 저장 변수 생성 */
    	private String distance; /* 남은 거리 저장 변수 생성 */
    	private String contentid; /* 해당 관광지 ID 저장 변수 생성 */
    	private double Latitude; /* 위도 저장 변수 */
    	private double Longitude; /* 경도 저장 변수 */
    	
    	/* 관광지 생성자 메소드 생성 */
    	MyData(String url, String name, String adress, String tel, String distance, String contentid, double Latitude, double Longitude) 
    	{ this.url = url; this.name=name; this.adress=adress; this.tel=tel; this.distance=distance; this.contentid=contentid; this.Latitude=Latitude; this.Longitude=Longitude; }
    }
    
    /* TODO Adapter 관련 메소드 */
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
    	private TextView[] mTextView = {null, null, null};
    	
    	/* Button */
    	private Button[] mButton = {null, null, null};
    	
    	/* ImageView */
    	private ImageView[] mImageView = {null};
    		
    	/* 생성자 메소드 생성 */
    	MyAdapter(Context context, int layoutId, ArrayList<MyData> myDataArr) 
    	{ 
    		this.mContext = context; 
    		this.layoutId = layoutId; 
    		this.myDataArr = myDataArr;
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
    	}
    	
    	@Override /* TODO Auto-generated method stub */
    	public int getCount() { return myDataArr.size(); }

    	@Override /* TODO Auto-generated method stub */
    	public Object getItem(int position) { return myDataArr.get(position).name; }

    	@Override /* TODO Auto-generated method stub */
    	public long getItemId(int position) { return position; }

    	@SuppressWarnings("deprecation")
    	@Override /* TODO Auto-generated method stub */
    	public View getView(int position, View convertView, ViewGroup parent) {			
    		/* 이벤트 리스너를 위한 ListView에 ITEM 번호를 저장 하는 변수 생성 */
    		final int pos = position; 
    		
    		if(convertView == null) { convertView = Inflater.inflate(layoutId, parent, false); }
    	
    				/* ImageView */
    				mImageView[0] = (ImageView)convertView.findViewById(R.id.tour_listview_photo);

                    try /* IMAGE를 다운받아서 IMAGE VIEW에 표시하는 부분 */
                    {
                        URL mURL = new URL(myDataArr.get(pos).url);
                        URLConnection mURLConnection = mURL.openConnection();
                        mURLConnection.connect();

                        int mLength = mURLConnection.getContentLength();
                        BufferedInputStream mBuffer = new BufferedInputStream(mURLConnection.getInputStream(), mLength);

                        Bitmap mBitmap = BitmapFactory.decodeStream(mBuffer);
                        mBuffer.close();

                        mImageView[0].setImageBitmap(mBitmap);
                    }
                    catch(MalformedURLException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(mContext, "이미지 다운로드 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("Festival IMAGE - URL", e.getMessage());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(mContext, "서버와의 연결이 정상적이지 않습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("Festival IMAGE - Stream", e.getMessage());
                    }
					
    				/* Name TextView */
    				mTextView[0] = (TextView)convertView.findViewById(R.id.tour_listview_name);
    				mTextView[0].setText(myDataArr.get(position).name);
    				/* address TextView */
    				mTextView[1] = (TextView)convertView.findViewById(R.id.tour_listview);
    				mTextView[1].setText(myDataArr.get(position).adress);
    				/* distance TextView */
    				mTextView[2] = (TextView)convertView.findViewById(R.id.tour_listview_distance);
    				mTextView[2].setText(myDataArr.get(position).distance);
    				
    					/* 전화 버튼 기능 관련 구문 */
    					mButton[0] = (Button)convertView.findViewById(R.id.tour_listview_call);
    					mButton[0].setOnClickListener(new OnClickListener() 
    					{
    						@Override /* TODO Auto-generated method stub */
    						public void onClick(View v) { Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+ myDataArr.get(pos).tel)); mContext.startActivity(intent); }
    					});
    				
    					/* 위치 버튼 기능 관련 구문 */
    					mButton[1] = (Button)convertView.findViewById(R.id.tour_listview_point);
    					mButton[1].setOnClickListener(new OnClickListener() 
    					{
    						@Override /* TODO Auto-generated method stub */
    						public void onClick(View v) 
    						{ 
    							Intent intent = new Intent(mContext,SubActivity4_map.class); /* 인텐트 객체 생성 */
    							intent.putExtra("Select_Mode", "tour_list"); /* 메인화면 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
    							intent.putExtra("Name", myDataArr.get(pos).name); /* 상호명 */
    							intent.putExtra("Latitude", myDataArr.get(pos).Latitude); /* 위도 값을 전달 */
    							intent.putExtra("Longitude", myDataArr.get(pos).Longitude); /* 경도 값을 전달 */
    							mContext.startActivity(intent); /* 인텐트 시작 */
    						} 
    					}); 
    				
    		/* 해당 아이템 클릭 시 정의 되는 구문 */
    		convertView.setOnClickListener(new OnClickListener() {
    			@Override /* TODO Auto-generated method stub */
    			public void onClick(View v) 
    			{ 
    				Intent intent = new Intent(mContext,SubActivity9_minutely.class);
        			intent.putExtra("Contentid", myDataArr.get(pos).contentid); /* 주변정보와 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
        			intent.putExtra("Type_code", mRegion[0]); /* 관광지 타입 번호를 인텐드 값 전달 */
        			intent.putExtra("Area", mRegion[1]); /* 지역 명 인텐트 값 전달 */
        			intent.putExtra("Title", myDataArr.get(pos).name); /* 문화재 이름 인텐트 값 전달 */
        			mContext.startActivity(intent);
    			} }); return convertView; }
    	} 
}