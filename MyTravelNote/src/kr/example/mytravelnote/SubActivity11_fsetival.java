package kr.example.mytravelnote;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import kr.example.mytravelnote.region.Area_region;
import kr.example.mytravelnote.tour.Festival_find;
import kr.net.mytravelnote.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SubActivity11_fsetival extends Activity {

	/* TextView */
	private TextView Inforamtion = null; /* 행사 정보 안내 관련 텍스트 뷰 */
	
	/* Button */
	private Button[] mButton = {null, null, null};
	
	/* ListView */
	private ListView mListView = null; /* 행사 정보 출력 리스트뷰 */
	private boolean lastitemFlag = false; /* 화면에 리스트의 마지막 아이템 확인 변수 */
	private LinearLayout button_LinearLayout = null; /* LinearLayout */
	
	/* Spinner */
	private Spinner[] mSpinner = {null, null}; /* Region 관련 Spinner */
	
	/* String */
	private String Area_Name = null; /* 지역 명 */
	private String startDate = null; /* 행사 시작 */
	
	/* int */
	private int region[] = {0, 0, 2}; /* Region 관련 Int */
	private int page[] = {1, 0}; /* Page 관련 Int */
				
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festival);
		
        /* Button */
        mButton[0] = (Button)findViewById(R.id.festival_back); /* 뒤로가기 */ mButton[0].setEnabled(false);
        mButton[1] = (Button)findViewById(R.id.festival_next); /* 다음 */ mButton[1].setEnabled(false);
        mButton[2] = (Button)findViewById(R.id.festival_find_button); /* 검색하기 */
        
        /* Spinner */
        mSpinner[0] = (Spinner)findViewById(R.id.festival_adminarea_spinner); /* 특별시/광역시/도 관련 스피너 */
        mSpinner[1] = (Spinner)findViewById(R.id.festival_area_spinner); /* 시/구/군 관련 스피너 */
        
        Date_SetText(); /* 현재 날짜를 구해주는 메소드 호출 */
        
		button_LinearLayout = (LinearLayout)findViewById(R.id.festival_button_layout); /* LinearLayout 객체 생성 */
		button_LinearLayout.setVisibility(View.GONE); /* Layout을 감춘다 */
        
        /* ListView 설정 구문 */
		mListView = (ListView)findViewById(R.id.festival_listView); /* 행사 정보 출력 리스트 뷰 */
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override /* TODO Auto-generated method stub */
			public void onScrollStateChanged(AbsListView view, int scrollState) /* 리스트 부의 스크롤이 바닥에 닿았을 경우  */
			{ 
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastitemFlag) { button_LinearLayout.setVisibility(View.VISIBLE); /* Layout을 보여준다. */ }
				else { button_LinearLayout.setVisibility(View.GONE); /* Layout을 보여준다. */ }
			}
			@Override /* TODO Auto-generated method stub */
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
			{ lastitemFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount); }
			/* 현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
			 * totalItemCount - 리스트의 전체 갯수
			 * visibleItemCount - 현제 화면에 보이는 리스트 갯수
			 * firstVisibleItem - 첫번째 아이템 번호
			 *  */
		});
        
        /* 이전 페이지 정의 구문 */
        mButton[0].setOnClickListener(new OnClickListener() 
        {
					@Override /* TODO Auto-generated method stub */
					public void onClick(View v)
					{ 
						switch(page[0]) /* 현재 페이지 수 별로 버튼 활성화/비활성화 구문 */
						{
							case (1) : /* 첫 페이지와 현제 페이지가 같을 경우 */
							{
								mButton[0].setEnabled(false); /* 버튼 비활성화 */
								Toast.makeText(SubActivity11_fsetival.this, "첫 번째 페이지 입니다.", Toast.LENGTH_SHORT).show(); /* 첫페이지를 알려 주는 구문 */
								break;
							}
							default : 
							{
								page[0]--; /* 현제 페이지 감소 */
								
								/* 현제 페이지 수 표시 구문 */
								Toast.makeText(SubActivity11_fsetival.this, "현제 페이지 " + page[0], Toast.LENGTH_SHORT).show(); /* 현제 페이지 수를 알려 주는 구문 */
								
								Festival_find mFestival = new Festival_find(SubActivity11_fsetival.this, startDate, mListView, region); /* Festival_find 객체 생성 */
								mFestival.execute(page[0]); /* 행사 조회 */ break;
							}
						}
		} });
        
		/* 다음 페이지 정의 구문 */
        mButton[1].setOnClickListener(new OnClickListener() {
				@Override /* TODO Auto-generated method stub */
				public void onClick(View v) 
				{
					if(page[0]==page[1]) /* 현재 페이지가 마지막 페이지 인 경우 */
					{
						mButton[1].setEnabled(false); /* 버튼 비활성화 */
						Toast.makeText(SubActivity11_fsetival.this, "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show(); /* 마지막 페이지를 알려 주는 구문 */
					}
					else /* 현재 페이지가 마지막 페이지가 아닌 경우 */
					{
						page[0]++; /* 현제 페이지 증가 */
						
						/* 현제 페이지 수 표시 구문 */
						Toast.makeText(SubActivity11_fsetival.this, "현제 페이지 " + page[0], Toast.LENGTH_SHORT).show(); /* 현제 페이지 수를 알려 주는 구문 */
						
						Festival_find mFestival = new Festival_find(SubActivity11_fsetival.this, startDate, mListView, region); /* Festival_find 객체 생성 */
						mFestival.execute(page[0]); /* 행사 조회 */
					}
				} });
        
		/* 검색 버튼 정의 구문 */
        mButton[2].setOnClickListener(new OnClickListener()
        {
					@Override /* TODO Auto-generated method stub */
					public void onClick(View v) 
					{						
						Festival_find mFestival = new Festival_find(SubActivity11_fsetival.this, startDate, mListView, region); /* 객체 생성 */
						
						try { page[1] = Total_Page_Select(mFestival.execute(page[0]).get()); /* 행사 조회 */ } 
						catch (InterruptedException e) { e.printStackTrace(); } 
						catch (ExecutionException e) { e.printStackTrace(); } 
						
						if(page[1]!=1) { mButton[1].setEnabled(true); /* 다음 버튼 활성화 */ }
					} });
				
				/* admin_region Spinner 설정 구문*/
				ArrayAdapter<?> area_adapter = ArrayAdapter.createFromResource(SubActivity11_fsetival.this, R.array.Area, android.R.layout.simple_spinner_item); /* 스피너에 값을 저장 하기 위한 ArrayList 객체 생성 */
				area_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mSpinner[0].setAdapter(area_adapter); /* 스피너 설정 */
				mSpinner[0].setOnItemSelectedListener(new OnItemSelectedListener() 
				{
					@Override /* TODO Auto-generated method stub */
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
					{
							Area_Name = mSpinner[0].getSelectedItem().toString(); /* 지역 명을 가져오는 구문 */
							/* Area_region 객체 생성 */
							Area_region mArea = new Area_region(SubActivity11_fsetival.this, mSpinner[1]);
							region[0] = mArea.Area_Codefind(Area_Name); /* 선택 된 스피너 값을 지역코드와 매치 시키는 구문 */
							mArea.execute(); /* 선택 받은 값을 통해 스피너를 정의 하는 함수 */
					}
					@Override /* TODO Auto-generated method stub */
					public void onNothingSelected(AdapterView<?> parent) { } 
				});
				
				/* region Spinner 설정 구문 */
				mSpinner[1].setOnItemSelectedListener(new OnItemSelectedListener() 
				{
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { region[1] = position+1; }
					@Override /* TODO Auto-generated method stub */ public void onNothingSelected(AdapterView<?> parent) { } 
				});
    }
    
    /* TODO XML에서 받은 데이터를 통해 총 페이지 수를 계산 하여서 저장 하는 함수 */
    private int Total_Page_Select(int result) {
    	int Page = 0; /* 총 페이지 수를 저장 하기 위한 변수 */
    	if( result%10==0 ) { Page=result/10; } /* 나머지가 0인 경우 페이지 수 값을 바로 저장 */
    	else if( result%10!=0 ) { Page=(result/10)+1; } /* 나머지가 0이 아닐 경우 페이지 수에 +1을 해서 저장 */
    	return Page; /* 계산 된 페이지 수를 총 페이지 수 전역 변수에 저장*/
    }
    
    /* TODO 뒤로가기 버튼 정의 메소드 */
    @Override
	public void onBackPressed()
	{ finish(); }
    
    /* TODO 현재 날짜를 구해주는 메소드 */
    private void Date_SetText()
    {
    	/* 현제 날짜를 가져 오는 구문 */
        final SimpleDateFormat[] mSimpleDateFormat = {null, null};
        mSimpleDateFormat[0] = new SimpleDateFormat("yyyy년 MM월 dd일", java.util.Locale.getDefault());
        mSimpleDateFormat[1] = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
        final Date date = new Date(); /* Date 객체 생성 */
        
        startDate = mSimpleDateFormat[1].format(date).toString(); /* 현제 날짜 저장 */
        
        /* TextView 설정 구문 */
        Inforamtion = (TextView)findViewById(R.id.festival_information); /* 행사 정보 안내 관련 텍스트 뷰 */
        StringBuffer Date_result = new StringBuffer(); /* StringBuffer 객체 생성 */
        Date_result.append(mSimpleDateFormat[0].format(date)); Date_result.append(" 관련 행사정보");
        Inforamtion.setText(Date_result.toString()); /* 텍스트 뷰 설정 */
    }
}