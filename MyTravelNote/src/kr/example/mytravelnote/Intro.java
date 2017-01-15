package kr.example.mytravelnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import kr.net.mytravelnote.R;

public class Intro extends Activity
{
	private Handler handler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.intro);

        String context = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager)getSystemService(context);
        
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        { alertCheckGPS(); }
        else 
        {            
            handler = new Handler();
            handler.postDelayed(thread, 2000);
        }
	}

	Runnable thread = new Runnable()
	{
		@Override
		public void run()
		{
			Intent intent = new Intent(Intro.this, MainActivity.class);
			startActivity(intent);
			finish();

			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	};

	/* TODO GPS 체크 관련 함수 */
	private void alertCheckGPS()
	{
		AlertDialog.Builder bulider = new AlertDialog.Builder(this);
		bulider.setTitle("위치 서비스 설정").setMessage("위치 서비스 활성화가 되어 있지 않습니다. 설정화면으로 이동하시 겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() 
		{	
			@Override /* TODO Auto-generated method stub */
			public void onClick(DialogInterface dialog, int which) 
			{
				Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
				System.exit(0);
			}
		}).setNegativeButton("취소", new DialogInterface.OnClickListener() 
		{	
			@Override /* TODO Auto-generated method stub */
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
				System.exit(0);
			}
		}).show();
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		handler.removeCallbacks(thread);
	}
}