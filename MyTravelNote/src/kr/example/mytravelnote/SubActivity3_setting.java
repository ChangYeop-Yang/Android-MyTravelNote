package kr.example.mytravelnote;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.widget.Toast;
import kr.net.mytravelnote.R;

public class SubActivity3_setting extends PreferenceActivity {

    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.actvity_setting);
        
        /* Preference 관련 구문 */
        EditTextPreference User_Name = (EditTextPreference)findPreference("userName"); /* 사용자 이름을 설정 받는 구문 */
        /* EditTextPreference 정의 구문 */
        User_Name.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
			@Override /* TODO Auto-generated method stub */
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				StringBuffer result = new StringBuffer(); /* StringBuffer 객체 생성 */
				result.append("<FONT COLOR=#0000ff>"); result.append(newValue); result.append("</FONT>"); result.append(" 으로 설정이 완료되었습니다.");
				Toast.makeText(SubActivity3_setting.this, Html.fromHtml(result.toString()),Toast.LENGTH_SHORT).show();
				return true;
			}	
        });
        
        ListPreference User_Display = (ListPreference)findPreference("useDisplay"); /* 화면 설정을 받는 구문 */
        User_Display.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
			@Override /* TODO Auto-generated method stub */
			public boolean onPreferenceChange(Preference preference, Object newValue) 
			{
				StringBuffer result = new StringBuffer(); /* StringBuffer 객체 생성 */
				result.append("<FONT COLOR=#0000ff>"); result.append(newValue); result.append("</FONT>"); result.append(" 으로 설정이 완료되었습니다.");
				Toast.makeText(SubActivity3_setting.this, Html.fromHtml(result.toString()),Toast.LENGTH_SHORT).show();
				return true;
			}
        });
    }
    
    @Override
	public void onBackPressed()
	{ finish(); }
}