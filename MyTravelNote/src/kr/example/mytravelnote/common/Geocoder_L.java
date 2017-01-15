package kr.example.mytravelnote.common;

import java.util.List;

import kr.example.mytravelnote.region.Area_region;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Geocoder_L extends AsyncTask<Double, Void, Void>
{
	/* Context */
	private Context mContext = null;
	
	/* TextView */
	private TextView mTextView = null;
	
	/* Geocoder */
	private Geocoder mGeocoder = null;
	
	/* List<Address> */
	private List<Address> mList = null;
	
	/* Int */
	private int mInt = 0;
	
	/* String */
	private String[] mString = {null, null, null, null};
	/* 
	 * mString[0] = 특별시 / 광역시 / 도
	 * mString[1] = 시 / 구 / 군
	 * mString[2] = 동 / 읍 / 면
	 * mString[3] = 번지
	 * */
	
	/* Geocoder 생성자 - 1 */
	public Geocoder_L(Context mContext, TextView mTextView, int mInt)
	{
		this.mContext = mContext;
		this.mTextView = mTextView;
		this.mInt = mInt;
	}
	
	@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
	protected void onPreExecute() 
	{
		/* Geocoder 관련 구문 */
		mGeocoder = new Geocoder(mContext); /* Geocoder 객체 생성 */
		super.onPreExecute();
	}
	
	@Override
	protected Void doInBackground(Double... params) 
	{
		try /* 현재 위치 부분 */
    	{
			mList = mGeocoder.getFromLocation(params[0], params[1], 1); /* 위도와 경도를 통해서 현재 위치를 받아오는 구문 */
    		
    		/* 지역 주소를 저장하기 위한 구문 */
    		if( (mList != null) && (mList.size()>0) )
    		{
    			Address mAddress = mList.get(0); /* Address 주소 객체 생성 */
    			mString[0] = mAddress.getAdminArea(); /* 특별시/광역시/도 */
    			mString[1] = mAddress.getLocality(); /* 시/구/군 */
    			mString[2] = mAddress.getThoroughfare(); /* 돟 / 읍 / 면 */
    			mString[3] = mAddress.getFeatureName(); /* 번지 */
    			Value.setArea(mString[0], mString[1], mString[2], mString[3]); /* Address 저장 */
    		}
    	} 
		catch(Exception e) 
    	{ 
			Log.e("Geocoder", e.getMessage());
			e.printStackTrace(); 
			Toast.makeText(mContext, "현재 위치의 정보를 조회하지 못하였습니다.", Toast.LENGTH_SHORT).show();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{ 
		StringBuffer mStringBuffer = new StringBuffer(); /* 주소 관련 StringBuffer 객체 생성 */
		mStringBuffer.append(mString[1]); mStringBuffer.append(" "); mStringBuffer.append(mString[2]); /* 동/읍 */ mStringBuffer.append(" "); mStringBuffer.append(mString[3]); /* 번지 */
		mTextView.setText(mStringBuffer.toString()); /* 현제 주소를 텍스트 뷰로 출력 */
		
		if(mInt == 1)
		{
			/* 지역 코드를 조회 하는 구문 */
			final Area_region mArea = new Area_region(mContext, 2, mString[0], mString[1]);
			mArea.Area_Codefind(mString[0]);
			mArea.execute(); /* 지역 코드 조회 */
		}
		
		super.onPostExecute(result); 
	}
}