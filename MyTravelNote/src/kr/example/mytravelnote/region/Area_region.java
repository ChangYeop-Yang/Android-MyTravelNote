package kr.example.mytravelnote.region;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kr.example.mytravelnote.apikey.API_KEY;
import kr.example.mytravelnote.common.Value;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/* TODO 해당 지역 코드를 조회하는 클래스 */
public class Area_region extends AsyncTask<Void, Void, Void>
{
	/* APP NAME */
	private final String My_AppName = "MyTravel";
	
	/* TODO Context */
	private Context mContext = null;
	
	/* TODO ArratList */
	private ArrayList<String> Code = null; /* 지역 코드 번호 */
	private ArrayList<String> Name = null; /* 지역 이름 */
	
	/* TODO ProgressDialog */
	private ProgressDialog Dialog = null;
	
	/* TODO Spinner */
	private Spinner spinner = null;
	
	/* TODO Int */
	private int Area_Code = 0;
	private int mInt = 0;
	
	/* TODO String */
	private String[] mString = {null, null};
		
	/* TODO 생성자 1 */
	public Area_region(Context mContext, Spinner spinner) 
	{ 
		/* Context */
		this.mContext = mContext; 
		/* Spinner */
		this.spinner = spinner; 
	}
	/* TODO 생성자 2 */
	public Area_region(Context mContext, int mInt) 
	{
		/* Context */
		this(mContext, null);
		/* Int */
		this.mInt = mInt;
	}
	/* TODO 생성자 3 */
	public Area_region(Context mContext, int mInt, String str1, String str2)
	{
		this(mContext, null);
		this.mInt = mInt;
		mString[0] = str1; mString[1] = str2;
	}
	public Area_region(){}

	/* TODO 지역코드 areaCode find 함수 */
    public int Area_Codefind(String result) 
    {
    	/* 사용자가 입력한 값을 코드로 변환 해주는 구문 */
    	switch(result) 
    	{
    		/* 특별시 */
    		case ("서울특별시") : { Area_Code=1; break; }
            case ("세종특별자치시") : { Area_Code=8; break; }
            case ("제주특별자치도") : { Area_Code=39; break; }
    		/* 광역시 */
    		case ("인천광역시") : { Area_Code=2; break; }
            case ("대전광역시") : { Area_Code=3; break; }
            case ("대구광역시") : { Area_Code=4; break; }
    		case ("광주광역시") : { Area_Code=5; break; }
            case ("부산광역시") : { Area_Code=6; break; }
            case ("울산광역시") : { Area_Code=7; break; }
    		/* 도 */
    		case ("경기도") : { Area_Code=31; break; }
            case ("강원도") : { Area_Code=32; break; }
            case ("충청북도") : { Area_Code=33; break; }
    		case ("충청남도") : { Area_Code=34; break; }
            case ("경상북도") : { Area_Code=35; break; }
            case ("경상남도") : { Area_Code=36; break; }
    		case ("전라북도") : { Area_Code=37; break; }
            case ("전라남도") : { Area_Code=38; break; }
    	}
    	return Area_Code; /* 매치가 된 지역 코드를 반환 */
    }
    
    /* TODO 관광타입 Type_Code find 함수 */
    public int Type_Codefind(String result) {

    	int Type_Code=0; /* 관광타입코드 저장 변수 생성 */

    	/* 사용자가 입력한 문화 타입을 코드로 변환 해주는 구문 */
    	switch(result)
    	{
    	    /* 관광지 */
            case ("관광지") : { Type_Code=12; break; }
            /* 문화시설 */
            case ("문화시설") : { Type_Code=14; break; }
            /* 레포츠 */
            case ("레포츠") : { Type_Code=28; break; }
            /* 레포츠 */
            case ("쇼핑") : { Type_Code=38; break; }
        }
    	return Type_Code; /* 매치가 된 관광타입 코드를 반환 */
    }
    
    /* TODO 동코드 조회 함수 */
    public int Str_Area_find(String result)
    {
    	int number = 0; /* 지역 코드 저장 변수 */
    	int length = Name.size(); /* Name - ArrayList 길이 저장 */
    	
    	/* 입력받은 주소와 동코드를 조회하는 구문 */
    	for(int count=0; count<length; count++)
    	{ if(Name.get(count).equals(result)) { number = Integer.valueOf(Code.get(count)).intValue(); } }
  
    	return number; /* 값 반환 */
    }
    
    /* TODO 조회 된 동을 스피너에 출력 해주는 함수 */
    private void Area_Name()
    {
    	List<String> dataArr = new ArrayList<String>(10); /* Name이 들어가는 Arraylist 객체 생성 */
    	List<String> codeArr = new ArrayList<String>(10);
    	
    	int Name_legth = this.Name.size(); /* ArrayList Name Size 변수 생성 */
    	for(int count=0; count<Name_legth; count++) 
    	{ 
    		/* 동 이름 */
    		String Name = this.Name.get(count); 
    		dataArr.add(Name); 
    		/* 동 코드 */
    		String Code = this.Code.get(count);
    		codeArr.add(Code);  
    	}
    	/* Spinner 관련 구문 */    	
    	ArrayAdapter<String> Regional_adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, dataArr);
    	Regional_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(Regional_adapter); /* 함수 종료 */ return;
    }
	
    @Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
	protected void onPreExecute() 
	{
		/* ArrayList 객체 생성 */
    	Code = new ArrayList<String>(10); /* 지역 코드 번호 */
    	Name = new ArrayList<String>(10); /* 지역 이름 */
    	
		/* Dialog 설정 구문 */
		Dialog = new ProgressDialog(mContext);
		Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
		Dialog.setMessage("잠시만 기다려 주세요.");
		Dialog.show();
	}
    
	@Override
	protected Void doInBackground(Void... params) 
	{
		try {
			/* Xml pull 파실 객체 생성 */
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			
				StringBuffer Str_result = null; /* StringBuffer 객체 생성 */

				/* StringBuffer 관련 구문 */
				Str_result= new StringBuffer("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaCode?ServiceKey=");
				/* API KEY */ Str_result.append(API_KEY.kr_GoDATA_API_KEY); Str_result.append("&MobileOS=AND&MobileApp="); 
				Str_result.append(My_AppName); /* APP NAME */ Str_result.append("&numOfRows=40&areaCode="); Str_result.append(Area_Code); /* 지역코드 삽입 */ 
					
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
					if(tagName.equals("code")) 
					{ Code.add(parser.getText()); /* Log.e("XML - Code", parser.getText()); */ } /* 장소 */
					if(tagName.equals("name")) 
					{ Name.add(parser.getText()); /* Log.e("XML - Name", parser.getText()); */ } /* URL */ 
				} 
				else if (eventType == XmlPullParser.END_TAG) 
				{ 
					tagName = parser.getName(); 
					if(tagName.equals("response")) { isItemTag = false; }
				}
				eventType = parser.next(); /* 다음 XML 객체로 이동 */
			} in.close();
		} catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{	
		switch(mInt)
		{
			case (0) : { Area_Name(); /* 조회 된 동을 스피너에 출력 해주는 함수 호출 */ break; }
			case (2) : { Value.setAreaCode(Area_Codefind(mString[0]), Str_Area_find(mString[1])); break; }
		}
		
		
		Dialog.dismiss(); /* ProgressDialog 종료 */
		super.onPostExecute(result);
	}
}