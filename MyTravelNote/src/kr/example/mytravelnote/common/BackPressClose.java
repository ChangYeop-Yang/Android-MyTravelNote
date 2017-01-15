package kr.example.mytravelnote.common;

import android.app.Activity;
import android.widget.Toast;

/* 뒤로가기 버튼 정의 */
public class BackPressClose {
	
	private long backKeyPressedTime = 0; /* 뒤로가기 버튼을 누른 시간 */
	private Toast toast; /* 안내문을 띄어 주기 위한 토스트 변수 */
	private Activity activity; /* 엑티비티를 입력 받을 변수 */
	
	/* 현제 Activity를 입력 받기 위한 생성자 */
	public BackPressClose(Activity context)
	{ this.activity = context; }
	
	/* 사용자에게 두 번의 뒤로가기 입력을 받은 경우 */
	public void Back_Close() 
	{
		/* 한 번의 뒤로가기 버튼이 눌린 뒤 현제 시간을 변수에 저장 한 후 토스트 출력 */
		if(System.currentTimeMillis() > backKeyPressedTime + 2000)
		{
			backKeyPressedTime = System.currentTimeMillis();
			showGuide(); return;
		}
		/* 한 번의 뒤로가기 버튼이 눌린 후 0 ~ 2초 사이에 한 번더 눌리게 되면 현제 엑티비티를 호출 */
		if(System.currentTimeMillis() <= backKeyPressedTime + 2000)
		{
			activity.finish(); /* 현제 엑티비티 종료 */
			toast.cancel(); /* 토스트 싱행 중단 */
		}
	}
	
	/* 사용자에게 한 번의 뒤로가기를 입력 받은 후 출력 되는 구문 */
	public void showGuide() {
		toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
		toast.show();
	}
	
}