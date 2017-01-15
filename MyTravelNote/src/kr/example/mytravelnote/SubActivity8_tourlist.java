package kr.example.mytravelnote;

import java.util.concurrent.ExecutionException;

import kr.example.mytravelnote.region.Area_region;
import kr.example.mytravelnote.tour.Tour_find;
import kr.net.mytravelnote.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.Toast;

public class SubActivity8_tourlist extends Activity 
{
	/* Double */
	private double mapX = 0.0; /* 위도 관련 변수 */
	private double mapY = 0.0; /* 경도 관련 변수 */

    /* Context */
    private Context mContext = SubActivity8_tourlist.this;

	/* ListView 관련 변수 */
	private ListView mListView = null;
	private boolean lastitemFlag = false; /* 화면에 리스트의 마지막 아이템 확인 변수 */
	
	/* LinearLayout 관련 변수 */
	private LinearLayout mLinearLayout = null; /* 버튼 관련 레이아웃 */
	
	/* TODO 지역 기반 변수 */
	private int[] mRegion = {0,0,1};
	private int regionalCode = 0; /* 시/구/군 코드를 상수 값으로 저장 하는 변수 */
	private int totalRows = 0; /* 해당 자료 갯수를 저장 하는 변수 */
	private int totalPage = 1; /* 총 페이지 수를 저장 하는 변수 */
	private int nowPage = 1; /* 현제 페이지를 저장 해 주는 변수 */
	
	/* Spinner */
	private Spinner[] mSpinner = new Spinner[2];
	
	/* Button */
	private Button[] mButton = new Button[2];
			
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

         /* NewtWork Threed 관련 구문 */
        if(android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy); }

        /* Intent */
        Intent mIntent = getIntent(); /* Intent 객체 생성 */
        mapX = mIntent.getDoubleExtra("MapX", 0.0); /* 위도 변수 생성 */
        mapY = mIntent.getDoubleExtra("MapY", 0.0); /* 경도 변수 생성 */
                
        /* Spinner */
        mSpinner[0] = (Spinner)findViewById(R.id.area_spinner); /* 지역 코드 스피너 객체 생성 */
        mSpinner[1] = (Spinner)findViewById(R.id.regional_spinner); /* 시/구/군 코드 스피너 객체 생성 */
        
        /* Button */
        mButton[0] = (Button)findViewById(R.id.tour_next_button); /* 다음 버튼 객체 생성 */
        mButton[1] = (Button)findViewById(R.id.tour_befor_button); /* 이전 버튼 객체 생성 */
                    
        /* LinearLayout */
        mLinearLayout = (LinearLayout)findViewById(R.id.tour_buuton_layout); /* 버튼 관련 레이아웃 */
        mLinearLayout.setVisibility(View.GONE); /* 버튼 레이아웃 비활성화 */
        
        /* ListView */
        mListView = (ListView)findViewById(R.id.tour_Main_listview); /* ListView 객체 생성 */
        mListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
			@Override /* TODO Auto-generated method stub */
			public void onScrollStateChanged(AbsListView view, int scrollState) /* 리스트 부의 스크롤이 바닥에 닿았을 경우  */
			{ 
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastitemFlag) { mLinearLayout.setVisibility(View.VISIBLE); /* Layout을 보여준다. */	}
				else { mLinearLayout.setVisibility(View.GONE); /* Layout을 감춘다. */ }
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

        /* 다음 버튼 정의 */
        mButton[0].setOnClickListener(new OnClickListener()
        {
			@Override /* TODO Auto-generated method stub */
			public void onClick(View v) 
			{
				if(nowPage>=totalPage)
				{ 
					mButton[0].setEnabled(false); /* 다음 버튼 비활성화 */
					Toast.makeText(mContext, "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
				}
				else if(nowPage<totalPage)
				{
					nowPage++; /* 페이지 수 증가 */
					mButton[1].setEnabled(true); /* 이전 버튼 활성화 */
					Toast.makeText(mContext, "다음페이지", Toast.LENGTH_SHORT).show();

					Tour_find tour_find = new Tour_find(mContext, mListView, mapX, mapY, mRegion);
					tour_find.execute(mRegion[0], mRegion[1], regionalCode, nowPage);
				}  
			}
        });
        
        /* 이전 버튼 정의 */
        mButton[1].setOnClickListener(new OnClickListener()
        {
			@Override /* TODO Auto-generated method stub */
			public void onClick(View v) 
			{
				if(nowPage==1)
				{
					mButton[1].setEnabled(false); /* 이전 버튼 비활성화 */
					Toast.makeText(mContext, "첫 페이지 입니다.", Toast.LENGTH_SHORT).show();
				}
				else if(nowPage!=1)
				{
					nowPage--; /* 페이지 수 감소 */
					mButton[0].setEnabled(true); /* 다음 버튼 활성화 */
					Toast.makeText(mContext, "이전페이지", Toast.LENGTH_SHORT).show();
					
					Tour_find tour_find = new Tour_find(mContext, mListView, mapX, mapY, mRegion);
					tour_find.execute(mRegion[0], mRegion[1], regionalCode, nowPage);
				}
			}
        });
        
        /* Spinner 관련 구문 */
        mSpinner[1].setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { regionalCode = position+1; }
			@Override /* TODO Auto-generated method stub */ public void onNothingSelected(AdapterView<?> parent) { } });
        
        /* 지역 코드 관련 스피너 생성 */
        ArrayAdapter<?> areaAdapter = ArrayAdapter.createFromResource(this, R.array.Area, android.R.layout.simple_spinner_item);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner[0].setAdapter(areaAdapter);
        mSpinner[0].setOnItemSelectedListener(new OnItemSelectedListener() 
        {
			@Override /* TODO Auto-generated method stub */
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{ 
		        Area_region area = new Area_region(mContext, mSpinner[1]); /* Area_region 객체 생성 */
		        mRegion[1] = area.Area_Codefind(mSpinner[0].getSelectedItem().toString()); /* Area_code에 지역 코드 저장 */
				area.execute(); /* 지역 코드 조회 */
			}
			@Override /* TODO Auto-generated method stub */ public void onNothingSelected(AdapterView<?> parent) { } 
		});
        
        /* 관광 타입 코드 관련 스피너 생성 */
        final Spinner typeSpinner = (Spinner)findViewById(R.id.type_spinner); /* 관광 타입 코드 스피터 객체 생성 */
        ArrayAdapter<?> typeAdapter = ArrayAdapter.createFromResource(this, R.array.Type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override /* TODO Auto-generated method stub */
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{ 
				Area_region mArea = new Area_region(); /* Area_region 객체 생성 */
				mRegion[0] =  mArea.Type_Codefind((typeSpinner.getSelectedItem().toString())); /* 관광타입 Type_Code find 함수 */ }
			@Override /* TODO Auto-generated method stub */
			public void onNothingSelected(AdapterView<?> parent) {  } });
       
        /* 찾기 버튼 관련 구문 */
        Button findButton = (Button)findViewById(R.id.find_button);
        findButton.setOnClickListener(new OnClickListener()
        {
			@Override /* TODO Auto-generated method stub */
			public void onClick(View v) 
			{ 
				nowPage = 1; /* 현제 페이지 초기화 */
				
				Tour_find mTour = new Tour_find(mContext, mListView, mapX, mapY, mRegion);
				try { totalRows = mTour.execute(mRegion[0], mRegion[1], regionalCode, nowPage).get(); }
				catch (InterruptedException e) { e.printStackTrace(); } 
				catch (ExecutionException e) { e.printStackTrace(); }
				
				totalPage = pageCalculate(totalRows);

                /* Button 활성화 관련 구문 */
                switch(totalPage)
                {
                    case (1) :
                    {
                        mButton[0].setEnabled(false); /* 다음 버튼 비활성화 */
                        mButton[1].setEnabled(false); /* 이전 버튼 비활성화 */
                        break;
                    }
                    default : { mButton[0].setEnabled(true); break; }
                }
			} });
    }
    
    @Override
	public void onBackPressed()
	{ finish(); }
    
    /* XML에서 받은 데이터를 통해 총 페이지 수를 계산 하여서 저장 하는 함수 */
    private int pageCalculate(int result)
    {
    	int mInt = 0; /* 총 페이지 수를 저장 하기 위한 변수 */
    	if( result%10==0 ) { mInt=result/10; } /* 나머지가 0인 경우 페이지 수 값을 바로 저장 */
    	else if( result%10!=0 ) { mInt=(result/10)+1; } /* 나머지가 0이 아닐 경우 페이지 수에 +1을 해서 저장 */
    	return mInt; /* 계산 된 페이지 수를 총 페이지 수 전역 변수에 저장*/
    }
}
