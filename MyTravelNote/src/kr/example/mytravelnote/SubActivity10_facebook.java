package kr.example.mytravelnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.PendingCall;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import kr.net.mytravelnote.R;

public class SubActivity10_facebook extends Activity {
	
	/* FaceBook 관련 변수 */
	private UiLifecycleHelper uiHelper;
	/* Intent 관련 저장 변수 */
	private String TITLE = null; /* 제목 */
	private String ADDRESS = null; /* 주소 */
	private String IMAGE = null; /* 이미지 */
	private String OVERVIEW = null; /* 휴무일 */

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        
        Intent intent = getIntent(); /* 인텐트 객체 생성 */
        if( intent.getStringExtra("facebook_minutely").equals("tour_minutely_mode") ) 
        {
        	TITLE = intent.getStringExtra("facebook_title");
        	ADDRESS = intent.getStringExtra("facebook_address");
        	IMAGE = intent.getStringExtra("facebook_image");
        	OVERVIEW = intent.getStringExtra("facebook_overview");
        }
        
        /* FaceBook 관련 구문 */
        uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(savedInstanceState);
        
        Session.openActiveSession(this, true, new Session.StatusCallback() {
				// TODO Auto-generated method stub
				@SuppressWarnings("deprecation")
				public void call(Session session, SessionState state, Exception exception) {
				if(session.isOpened()) 
				{ 
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
						@Override /* TODO Auto-generated method stub */
						public void onCompleted(GraphUser user, Response response) 
						{
								if(user != null) {
								TextView welcome = (TextView) findViewById(R.id.facebook_id);
								welcome.setText("Hello" + user.getName() + "!"); }
								publishFeedialog();
						} }); 
				} } });
       
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		uiHelper.onResume();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	public void onPause() 
	{
		super.onPause();
		uiHelper.onPause();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	/* 페이스북과 연결을 하기 위한 함수 */
	@Override
	public void onActivityResult(int requertCode, int resultCode, Intent date) {
		super.onActivityResult(requertCode, resultCode, date);
		Session.getActiveSession().onActivityResult(this, requertCode, resultCode, date);
		
		uiHelper.onActivityResult(requertCode, resultCode, date, new FacebookDialog.Callback() {
			@Override /* TODO Auto-generated method stub */
			public void onError(PendingCall pendingCall, Exception error, Bundle data) 
			{ Log.e("Activity", String.format("Error: %s", error.toString())); }
			
			@Override /* TODO Auto-generated method stub */
			public void onComplete(PendingCall pendingCall, Bundle data) 
			{ Log.i("Activity", "Success!"); } });
	}
	
	/* 페이스북에 공유하기에 해당하는 함수 */
	private void publishFeedialog() {
		 Bundle params = new Bundle();
		 	params.putString("name", TITLE);
		    params.putString("caption", ADDRESS);
		    params.putString("description", OVERVIEW);
		    params.putString("picture", IMAGE);	

		    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(SubActivity10_facebook.this, Session.getActiveSession(),params)).setOnCompleteListener(new OnCompleteListener() {
		            @Override
		            public void onComplete(Bundle values, FacebookException error) {
		            	/* When the story is posted, echo the success */
	                    /* and the post Id. */
		            	if (error == null) 
		            	{
		                    final String postId = values.getString("post_id");
		                    if (postId != null) { Toast.makeText(SubActivity10_facebook.this, "정상적으로 게시물이 등록되었습니다.", Toast.LENGTH_SHORT).show(); finish(); } 
		                    /* User clicked the Cancel button */
		                    else { Toast.makeText(SubActivity10_facebook.this, "Publish cancelled", Toast.LENGTH_SHORT).show(); }
		                /* User clicked the "x" button */
		            	} else if (error instanceof FacebookOperationCanceledException) { Toast.makeText(SubActivity10_facebook.this, "Publish cancelled", Toast.LENGTH_SHORT).show(); } 
		            	/* Generic, ex: network error */
		            	else { Toast.makeText(SubActivity10_facebook.this, "Error posting story", Toast.LENGTH_SHORT).show(); } } }).build(); feedDialog.show();
	}
}
