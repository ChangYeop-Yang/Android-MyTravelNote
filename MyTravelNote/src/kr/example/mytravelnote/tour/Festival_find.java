package kr.example.mytravelnote.tour;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.example.mytravelnote.SubActivity12_festival_minutely;
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
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/* TODO 행사 관련 정보 검색 클래스 */
public class Festival_find extends AsyncTask<Integer, Void, Integer>
{
	/* ProgressDialog */
	private ProgressDialog Dialog = null;
	
	/* Context */
	private Context mContext = null;
	
	/* ListView */
	private ListView mListView = null;
	
	/* Handler */
	private Handler handler = new Handler();
	
	/* int */
	private int totalRows = 0; /* 총 검색 된 목록 수 */
	private int[] mRegion = {0,0,0};
	
	/* String */
	private String startDate = null; /* 행사 시작 */
	
	/* TODO ArrayList */
	private ArrayList<String> title_A = null; /* 행사 제목 관련 ArrayList */
	private ArrayList<String> address_A = null; /* 행사 주소 관련 ArrayList */
	private ArrayList<String> id_A = null; /* 행사 ID 관련 ArrayList */
	private ArrayList<String> mapX_A = null; /* 행사 위도 관련 ArrayList */
	private ArrayList<String> mapY_A = null; /* 행사 경도 관련 ArrayList */
	private ArrayList<String> image_A = null; /* 행사 이미지 관련 ArrayList */
	private ArrayList<String> startDate_A = null; /* 행사 시작 관련 ArrayList */
	private ArrayList<String> dueDate_A = null; /* 행사 종료 관련 ArrayList */
	
	/* 생성자 */
	public Festival_find(Context mContext, String startDate, ListView mListView, int[] mRegion)
	{
		this.mContext = mContext;
		this.startDate = startDate;
		this.mListView = mListView;
		this.mRegion = mRegion;
	}
	
	/* TODO ArrayList의 길이를 맞추어 주는 메소드 */
	private void ArrayList_Check()
	{
		int legnth = title_A.size();
		int Image_legth = image_A.size(); /* 이미지 관련 변수 사이즈 저장 변수 */
        int Address_legth = address_A.size(); /* 주소 관련 변수 사이즈 저장 변수 */
        /* IMAGE Vector 데이터 검증 구문 */
        while( (legnth!=Image_legth) || (legnth!=Address_legth) )
        { 
        	if( (legnth!=Image_legth) ) { image_A.add("http://api.visitkorea.or.kr/static/images/common/noImage.gif"); Image_legth++; }
        	if( (legnth!=Address_legth) ) { address_A.add("주소정보 없음"); Address_legth++; }
        }
	}
	
	/* TODO ListView 아이템을 추가해주는 함수 */
	private void ListView_add()
	{
		/* ListView 관련 구문 */
        ArrayList<MyData> dataArr = new ArrayList<MyData>();
        String result = null;
        
        /* Item을 증가 시켜 주는 구문 */
        int legnth = title_A.size();
        for(int count=0; count<legnth; count++) 
        {
        	/* 제목과 장소를 저장하는 String 변수 생성 */
        	String title=title_A.get(count);  /* 상호명 */
        	String point=address_A.get(count); /* 장소 */
        	String image=image_A.get(count); /* 이미지 */
        	String id=id_A.get(count); /* 컨텐츠ID */
        	
			try /* 날짜 관련 */
			{
		        StringBuffer date_result = new StringBuffer(); /* StringBuffer 객체 생성 */
				/* StringBuffer구문과 DateFormat구문 */
	        	SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
	        	SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy년MM월dd일", java.util.Locale.getDefault());
	        	
				Date temp_1 = dateformat1.parse(startDate_A.get(count)); /* Date 형변환은 위한 Date 변수 생성 */
	        	Date temp_2 = dateformat1.parse(dueDate_A.get(count)); /* Date 형변환은 위한 Date 변수 생성 */
	        	
	        	date_result.append(dateformat2.format(temp_1)); /* 시작 날짜 - 데이터 포멧 */ date_result.append(" ~ ");
	        	date_result.append(dateformat2.format(temp_2)); /* 종료 날짜 - 데이터 포멧 */
	        	result = date_result.toString(); /* Date 값 저장 */
			} catch (ParseException e) { e.printStackTrace(); }
        		/* 남은 거리 계산 하는 구문 */
        		double Latitude=Double.valueOf(mapX_A.get(count)).doubleValue(); /* 위도 */
            	double Longitude=Double.valueOf(mapY_A.get(count)).doubleValue(); /* 경도 */
            	/* Item을 추가 해주는 구문 */
        		dataArr.add(new MyData(image, title, point, result, id, Latitude, Longitude));
        }        
        MyAdapter mAdapter = new MyAdapter(mContext, R.layout.activity_festival_listview, dataArr);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(mAdapter);
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
		address_A = new ArrayList<String>(10); /* 행사 주소 저장 변수 */
		title_A = new ArrayList<String>(10); /* 행사 이름 저장 변수 */
		image_A = new ArrayList<String>(10); /* 행사 썸네일 이미지 저장 변수 */
		mapX_A = new ArrayList<String>(10); /* 행사 위도 저장 변수 */
		mapY_A = new ArrayList<String>(10); /* 행사 경도 저장 변수 */
		id_A = new ArrayList<String>(10); /* 해당 행사 ID 저장 변수 */
		startDate_A = new ArrayList<String>(10); /* 행사 시작 관련 저장 변수 */
		dueDate_A = new ArrayList<String>(10); /* 행사 종료 관련 저장 변수 */
		
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
			
				StringBuffer Kr_Data = null; /* String Buffer 객체 생성 */

				Kr_Data = new StringBuffer("http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchFestival?ServiceKey="); /* StringBuffer 객체 생성 */
				Kr_Data.append(API_KEY.kr_GoDATA_API_KEY); Kr_Data.append("&eventStartDate="); Kr_Data.append(startDate); Kr_Data.append("&eventEndDate=");
				Kr_Data.append("&areaCode="); Kr_Data.append(mRegion[0]); Kr_Data.append("&sigunguCode="); Kr_Data.append(mRegion[1]);
				Kr_Data.append("&cat1=&cat2=&cat3=&listYN=Y&MobileOS=AND&MobileApp=MyTravel&arrange=O&numOfRows=10&pageNo="); Kr_Data.append(params[0]);
						
				/* 외부 사이트 연결 관련 구문 */
				URL url = new URL(Kr_Data.toString()); /* URL 객체 생성 */ 
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
				} else if (eventType == XmlPullParser.TEXT && isItemTag) 
				{
					if(tagName.equals("addr1")) 
					{ address_A.add(parser.getText()); /* Log.e("XML - addr1", parser.getText()); */ } /* 주소 */
					if(tagName.equals("contentid")) 
					{ id_A.add(parser.getText()); /* Log.e("XML - contentid", parser.getText()); */ } /* 해당 게시물 ID 값 */
					if(tagName.equals("eventenddate")) 
					{ startDate_A.add(parser.getText()); /* Log.e("XML - eventenddate", parser.getText()); */ } /* 행사 끝나는 날짜 */
					if(tagName.equals("eventstartdate")) 
					{ dueDate_A.add(parser.getText()); /* Log.e("XML - eventstartdate", parser.getText()); */ } /* 행사 시작 날짜 */
					if(tagName.equals("firstimage2")) 
					{ image_A.add(parser.getText()); /* Log.e("XML - image", parser.getText()); */ } /* 이미지 */
					if(tagName.equals("mapx")) 
					{ mapX_A.add(parser.getText()); /* Log.e("XML - mapx", parser.getText()); */ } /* 경도 */
					if(tagName.equals("mapy")) 
					{ mapY_A.add(parser.getText()); /* Log.e("XML - mapy", parser.getText()); */ } /* 위도 */
					if(tagName.equals("title")) 
					{ title_A.add(parser.getText()); /* Log.e("XML - title", parser.getText()); */ } /* 이름 */
					if(tagName.equals("totalCount")) 
					{ totalRows = Integer.valueOf(parser.getText()).intValue(); } 
				} else if (eventType == XmlPullParser.END_TAG) { tagName = parser.getName(); if(tagName.equals("response")) { isItemTag = false; } }
				eventType = parser.next(); /* 다음 XML 객체로 이동 */
			}
		} catch (Exception e) { e.printStackTrace(); }
		return totalRows;
	}
	
	@Override
	protected void onPostExecute(Integer result)
	{
		ArrayList_Check(); /* ArrayList의 길이를 맞추어 주는 메소드 */
		ListView_add(); /* ListView 아이템을 추가해주는 함수 */
		Dialog.dismiss(); /* Dialog 종료 부분 */
		super.onPostExecute(result);
	}
	
	/* TODO 데이터 관련 클래스 */
	class MyData
	{
		String url; /* Image Icon 저장 변수 생성 */
		String name; /* 상호명 저장 변수 생성 */
		String address; /* 주소 저장 변수 생성 */
		String tel; /* 전화 번호 저장 변수 생성 */
		String date; /* 남은 거리 저장 변수 생성 */
		String contentid; /* 해당 관광지 ID 저장 변수 생성 */
		double Latitude; /* 위도 저장 변수 */
		double Longitude; /* 경도 저장 변수 */
		
		/* 행사 생성자 메소드 생성 */
		MyData(String url, String name, String address, String date, String contentid, double Latitude, double Longitude)
		{ this.url = url; this.name=name; this.address=address; this.date=date; this.contentid=contentid; this.Latitude=Latitude; this.Longitude=Longitude; }
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
    	public View getView(int position, View convertView, ViewGroup parent)
        {
    		/* 이벤트 리스너를 위한 ListView에 ITEM 번호를 저장 하는 변수 생성 */
    		final int pos = position; 
    		
    		if(convertView == null) { convertView = Inflater.inflate(layoutId, parent, false); }
    	
    		/* ImageView */
    		mImageView[0] = (ImageView)convertView.findViewById(R.id.festival_image);
            try
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
    				mTextView[0] = (TextView)convertView.findViewById(R.id.festival_name);
    				mTextView[0].setText(myDataArr.get(position).name);
    				/* address TextView */
    				mTextView[1] = (TextView)convertView.findViewById(R.id.festival_address);
    				mTextView[1].setText(myDataArr.get(position).address);
    				/* date TextView */
    				mTextView[2] = (TextView)convertView.findViewById(R.id.festival_date);
    				mTextView[2].setText(myDataArr.get(position).date);
    				
    		/* 해당 아이템 클릭 시 정의 되는 구문 */
    		convertView.setOnClickListener(new OnClickListener() {
    			@Override /* TODO Auto-generated method stub */
    			public void onClick(View v) 
    			{ 
    				Intent intent = new Intent(mContext,SubActivity12_festival_minutely.class);
        			intent.putExtra("Contentid", myDataArr.get(pos).contentid); /* 주변정보와 혼동이 생기지 않도록 해주는 인텐드 값 전달 */
        			intent.putExtra("Type_code", String.valueOf(mRegion[0])); /* 관광지 타입 번호를 인텐드 값 전달 */
        			intent.putExtra("Area", String.valueOf(mRegion[1])); /* 지역 명 인텐트 값 전달 */
        			intent.putExtra("Title", myDataArr.get(pos).name); /* 문화재 이름 인텐트 값 전달 */
        			mContext.startActivity(intent);
    			} }); return convertView; }
    	} 
	
}