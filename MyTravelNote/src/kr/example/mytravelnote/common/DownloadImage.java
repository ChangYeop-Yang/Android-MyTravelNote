package kr.example.mytravelnote.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Mari on 2015-05-08.
 */
public class DownloadImage
{
    /* TODO URL을 통해서 이미지를 받아오는 메소드 */
    final static public void image(final ImageView mImageView, String mString, final Context mContext)
    {
        /* NewtWork Threed 관련 구문 */
        if(android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy); }

        try
        {
            URL mURL = new URL(mString);
            URLConnection mURLConnection = mURL.openConnection();
            mURLConnection.connect();

            int mLength = mURLConnection.getContentLength();
            BufferedInputStream mBuffer = new BufferedInputStream(mURLConnection.getInputStream(), mLength);

            Bitmap mBitmap = BitmapFactory.decodeStream(mBuffer);
            mBuffer.close();

            mImageView.setImageBitmap(mBitmap);
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
            Toast.makeText(mContext, "이미지 다운로드 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            Log.e("IMAGE - URL", e.getMessage());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(mContext, "서버와의 연결이 정상적이지 않습니다.", Toast.LENGTH_SHORT).show();
            Log.e("IMAGE - Stream", e.getMessage());
        }
        return;
    }
}
