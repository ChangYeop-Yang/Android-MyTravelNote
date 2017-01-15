package kr.example.mytravelnote.common;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Mari on 2015-05-08.
 */
public class Vibration
{
    /* TODO 기기 진동 관련 함수 */
    final static public void Vibrate(Context mContext)
    {
        Vibrator mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE); /* Vibrator 객체 생성 */
        long[] vibratePattern = {100, 100, 300};
        mVibrator.vibrate(300);
        mVibrator.vibrate(vibratePattern, -1);
    }
}
