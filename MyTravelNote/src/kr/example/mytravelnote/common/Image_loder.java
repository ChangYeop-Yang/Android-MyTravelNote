package kr.example.mytravelnote.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

/* TODO 이미지를 다운 받아서 이미지 뷰에 출력 */
public class Image_loder extends AsyncTask<Void, Void, Void>
{
	/* ImageView */
	private ImageView mImageView = null;
	
	/* URL */
	private URL mURL = null;
	
	/* String */
	private String mString = null;
	
	/* Bitmap */
	private Bitmap mBitmap = null;
	
	/* Context */
	private Context mContext = null;
	
	/* TODO ProgressDialog */
	private ProgressDialog Dialog = null;
	
	/* TODO 생성자 메소드 */
	public Image_loder(Context mContext, ImageView mImageView, String mString)
	{
		this.mContext = mContext;
		this.mImageView = mImageView;
		this.mString = mString;
	}
	
	@Override /* 프로세스가 실행되기 전에 실행 되는 부분 - 초기 설정 부분 */
	protected void onPreExecute() 
	{
		/* Dialog 설정 구문 */
		Dialog = new ProgressDialog(mContext);
		Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
		Dialog.setMessage("잠시만 기다려 주세요.");
		Dialog.show();
		
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) 
	{
        try
        {
            mURL = new URL(mString);
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
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		Dialog.dismiss(); /* Dialog 종료 부분 */
		super.onPostExecute(result);
	}
}