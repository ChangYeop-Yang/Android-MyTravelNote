package kr.example.mytravelnote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import kr.example.mytravelnote.common.DownloadImage;
import kr.example.mytravelnote.common.Value;
import kr.example.mytravelnote.common.Vibration;
import kr.net.mytravelnote.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SubActivity_photo extends Activity {
		
	/* Google Map Static 관련 변수 */
	private Handler handler = new Handler();

    /* Context */
    private final Context mContext = SubActivity_photo.this;
		
	/* 카메라 관련 변수 */
	private static final int CAMERA_CAPTURE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	
	/* SQLite 관련 변수 */
	protected dbHelper helper;
	protected SQLiteDatabase DataBase;
	
	/* Double */
	private double mapX = 0.0; /* 위도 */
	private double mapY = 0.0; /* 경도 */

    /* EditText */
    private EditText mEditText = null;

    /* Calendar */
    private Calendar mCalendar = null;

    /* String */
    private String display = null; /* 사용자에게 입력 받은 화면 배치 관련 변수 */

    /* Int */
    private int displayNumber = 0; /* 화면 저장용 관련 변수 */
    
	/* float */
    private float ratingScore = 0;
	
	/* TODO SQLite 체크 관련 함수 */
	protected void createSQLite()
    {
		/* dbHelper 객체 생성 */
		helper = new dbHelper(this); 
		
		/* 읽기/쓰기 모드로 데이터베이스를 오픈 */
		try { DataBase = helper.getWritableDatabase(); }
		/* 읽기 전용 모드로 데이터베이스를 오픈 */
		catch(SQLiteException ex)
        {
            DataBase = helper.getReadableDatabase();
            ex.printStackTrace();
            Log.e("PHOTO - SQL", ex.getMessage());
            Toast.makeText(mContext, "SQLite 문제가 발생 하였습니다.", Toast.LENGTH_SHORT).show();
        }
	}

	@Override
	public void onBackPressed()
	{ finish(); }
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        /* 설정 값을 가져오는 구문 */
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); /* 설정 값을 가져오기 위하여 Preferences 객체 생성 */
        display = mSharedPreferences.getString("useDisplay", "1"); /* Display 값 입력 구문 */

        /* Intent */
        Intent mIntent = getIntent(); /* Intent 값을 가져오는 구문 */
        mapX = mIntent.getDoubleExtra("MapX", 0.0); /* 위도 */
        mapY = mIntent.getDoubleExtra("MapY", 0.0); /* 경도 */

        /* 사용자에게 입력 받은 값으로 화면을 성정 해주는 구문 */
        switch(display)
        {
        	case ("1") : /* 1 * 1 화면 배치  */
        	{ 
        		setContentView(R.layout.activity_sub_photo);

                /* RatingBar */
                final RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
                setRatingBar(mRatingBar);

                /* Button */
                final Button mButton = (Button)findViewById(R.id.check_button);
                setSaveButton(mButton);
                
                /* ImageView */
            	final ImageView mImageView = (ImageView)findViewById(R.id.photo_image);
                setImageView(mImageView, 0);

                break;
        	}
        	case ("2") : /* 1 * 2 화면 배치 */
        	{ 
        		setContentView(R.layout.activity_photo_two);

        		/* RatingBar */
                final RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
                setRatingBar(mRatingBar);

                /* Button */
                final Button mButton = (Button)findViewById(R.id.check_button);
                setSaveButton(mButton);
                
                /* ImageView */
            	final ImageView[] mImageView = {(ImageView)findViewById(R.id.photo_image_1), (ImageView)findViewById(R.id.photo_image_2)};
                setImageView(mImageView[0], 1);
                setImageView(mImageView[1], 2);

            	break;
        	}
        	case ("3") : /* 1 * 3 화면 배치 */
        	{ 
        		setContentView(R.layout.activity_photo_thread);

        		/* RatingBar */
                final RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
                setRatingBar(mRatingBar);
                
                /* Button */
                final Button mButton = (Button)findViewById(R.id.check_button);
                setSaveButton(mButton);
                
                /* ImageView 객체 생성 */
            	final ImageView[] mImageView = {(ImageView)findViewById(R.id.photo_image_1), (ImageView)findViewById(R.id.photo_image_2), (ImageView)findViewById(R.id.photo_image_3)};
                setImageView(mImageView[0], 1);
                setImageView(mImageView[1], 2);
                setImageView(mImageView[2], 3);

                break;
        	}
        }

        initialization(mapX, mapY);
        createSQLite(); /* SQLite 생성 및 업그레이드 관련 함수 호출 */
    }

    /* TODO 초기화 관련 메소드 */
    private void initialization(double mapX, double mapY)
    {
        /* ImageView */
        final ImageView mImageView = (ImageView)findViewById(R.id.gps_image);

        /* TextView */
        final TextView[] mTextView = {(TextView)findViewById(R.id.report_date_textvie2), (TextView)findViewById(R.id.report_point_text)};

        /* EditText */
        mEditText = (EditText) findViewById(R.id.NameEditText); /* EditText 객체 생성 */

        /* Calendar */
        mCalendar = Calendar.getInstance();

                                       /* MAP IMAGE DOWNLOAD */
        /* StringBuffer */
        StringBuffer mStringBuffer1 = new StringBuffer("http://maps.googleapis.com/maps/api/staticmap?center=");
        mStringBuffer1.append(mapX + "," + mapY); /* 카메라 이동 위도와 경도 */ mStringBuffer1.append("&zoom=18&size=640x640&scale=2&maptype=roadmap&markers=color:blue|label:N|");
        mStringBuffer1.append(mapX + "," + mapY); /* 마커 설정 위도와 경도 */ mStringBuffer1.append("%7C11211&sensor=false");
        mStringBuffer1.append("&key=AIzaSyAPwZbV6iLNwLv23_3l-dmS_E6pVX34e0I"); /* Google API Key */

        /* IMAGE DOWNLOAD */
        DownloadImage.image(mImageView, mStringBuffer1.toString(), mContext);

        /* Calendar SetText */
        mTextView[0].setText(mCalendar.get(mCalendar.YEAR) + " 년 " + (mCalendar.get(mCalendar.MONTH)+1) + " 월 " + mCalendar.get(mCalendar.DAY_OF_MONTH) + " 일 ");

        /* StringBuffer */
        StringBuffer mStringBuffer2 = new StringBuffer(); mStringBuffer2.append(Value.getAdminArea());
        mStringBuffer2.append(" "); mStringBuffer2.append(Value.getLocalityArea()); mStringBuffer2.append(" ");
        mStringBuffer2.append(Value.getThoroughFareArea()); mStringBuffer2.append(" "); mStringBuffer2.append(Value.getFeatureNameArea());

        /* Location SetText */
        mTextView[1].setText(mStringBuffer2.toString());

        return;
    }

    /* 버튼 설정 관련 메소드 */
    private void setSaveButton(final Button mButton)
    {
        mButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Vibration.Vibrate(mContext); /* 기기 진동 관련 함수 호출 */
                final String mString = "sdcard/MyTravel/"; /* String 형식으로 경로 저장 */

                /* Bitmap을 이미지 파일로 저장해주는 함수 호출 */
                if( mEditText.getText().toString() != null )
                {
                    try
                    {
                        String path = new ImageSave().execute(mString).get(); /* Bitmap을 JPEG 파일로 저장 시켜주는 함수 */
                        insertData(mapX, mapY, path); /* DATABASE에 데이터를 추가하는 함수 호출 */
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    catch(ExecutionException e)
                    {
                        e.printStackTrace();
                    }

                } else { Toast.makeText(mContext, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show(); }
            }
        });
        return;
    }

    /* 레이팅바 설정 관련 메소드 */
    private void setRatingBar(final RatingBar mRatingBar)
    {
        mRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            { ratingScore=rating; Toast.makeText(mContext, "점수 : " + rating, Toast.LENGTH_SHORT).show(); }
        });

        return;
    }

    /* ImageView 설정 관련 메소드 */
    private void setImageView(ImageView mImageView, final int mInt)
    {
        mImageView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /* String */
                String mString = mInt + " 번 화면";

                switch(mInt)
                {
                    case (0) : { Toast.makeText(mContext, "사진촬영", Toast.LENGTH_SHORT).show(); break; }
                    default :
                    {
                        Toast.makeText(mContext, mString, Toast.LENGTH_SHORT).show();
                        displayNumber = mInt;
                        break;
                    }
                }

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_CAPTURE);
            }
        });
    }
    
    /* TODO DATABASE에 데이터를 추가하는 함수 */
    private void insertData(double mapX, double mapY, String path)
    {
    	/* EditText 객체 생성 및 데이터 저장 */
        final EditText mEditText = (EditText) findViewById(R.id.NameEditText); /* 제목 EditText  */
        String name = mEditText.getText().toString();
        final TextView mTextView = (TextView) findViewById(R.id.report_point_text); /* 장소 EditText */
        String address = mTextView.getText().toString();
        
        DataBase.beginTransaction(); /* 트랜잭션 실행 */
        
        try
        {
            /* Query */
            String query1 = "INSERT INTO photo_db VALUES (null, '" + name + "','" + address + "','" + ratingScore + "','" + mapX + "','" + mapY + "','" + path +"');";
            /* Query문 실행 */
            DataBase.execSQL(query1);
            /* 아무런 문제가 없는 경우 데이터를 저장 시킨다. */
            DataBase.setTransactionSuccessful();
        }
        catch (SQLiteException ex)
        {
            ex.printStackTrace(); /* SQLite 오류가 발생 시 토스트 기능 출력 */
            Log.e("SQL - INSERT", ex.getMessage());
            Toast.makeText(mContext, "자료를 저장하는 도중에 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
        }
        finally { DataBase.endTransaction(); /* 트랜잭션 종료 */ }
    }
    
    /* 액티비티 간에 값을 전달하는 함수 */
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	/* 카메라의 인텐트 결과를 받아 들려 Bitmap 쪼갠 후 받아서 image view 출력해주는 함수 */
    	if(requestCode == CAMERA_CAPTURE && resultCode == RESULT_OK)
        {
    		switch(display)
    		{
    			case ("1") : 
    			{
    				/* ImageView */
    				final ImageView mImageView = (ImageView)findViewById(R.id.photo_image);

                    try
                    {
                        Bitmap mBitmap = Images.Media.getBitmap(getContentResolver(), data.getData());
                        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        mImageView.setImageBitmap(matrixBitmap(mBitmap));
                    }
					catch (FileNotFoundException e) { e.printStackTrace(); } 
					catch (IOException e) { e.printStackTrace(); }
					
    				 break;
    			}
    			case ("2") :
    			{
                    /* ImageView */
                    final ImageView[] mImageView = {(ImageView)findViewById(R.id.photo_image_1), (ImageView)findViewById(R.id.photo_image_2)};

                    switch(displayNumber)
    				{
    					case (1) : 
    					{
                            try
                            {
                                Bitmap mBitmap = Images.Media.getBitmap(getContentResolver(), data.getData());
                                mImageView[0].setScaleType(ImageView.ScaleType.FIT_XY);
                                mImageView[0].setImageBitmap(matrixBitmap(mBitmap));
                            }
                            catch (FileNotFoundException e) { e.printStackTrace(); }
                            catch (IOException e) { e.printStackTrace(); }

                            break;
    					}
    					case (2) :
    					{
                            try
                            {
                                Bitmap mBitmap = Images.Media.getBitmap(getContentResolver(), data.getData());
                                mImageView[1].setScaleType(ImageView.ScaleType.FIT_XY);
                                mImageView[1].setImageBitmap(matrixBitmap(mBitmap));
                            }
                            catch (FileNotFoundException e) { e.printStackTrace(); }
                            catch (IOException e) { e.printStackTrace(); }

                            break;
    					}
    				}
                    break;
    			}
    			case ("3") :
    			{
                    /* ImageView */
                    final ImageView[] mImageView = {(ImageView)findViewById(R.id.photo_image_1), (ImageView)findViewById(R.id.photo_image_2), (ImageView)findViewById(R.id.photo_image_3)};

    				switch(displayNumber)
    				{
    					case (1) : 
    					{
                            try
                            {
                                Bitmap mBitmap = Images.Media.getBitmap(getContentResolver(), data.getData());
                                mImageView[0].setScaleType(ImageView.ScaleType.FIT_XY);
                                mImageView[0].setImageBitmap(mBitmap);
                            }
                            catch (FileNotFoundException e) { e.printStackTrace(); }
                            catch (IOException e) { e.printStackTrace(); }

                            break;
    					}
    					case (2) :
    					{
                            try
                            {
                                Bitmap mBitmap = Images.Media.getBitmap(getContentResolver(), data.getData());
                                mImageView[1].setScaleType(ImageView.ScaleType.FIT_XY);
                                mImageView[1].setImageBitmap(mBitmap);
                            }
                            catch (FileNotFoundException e) { e.printStackTrace(); }
                            catch (IOException e) { e.printStackTrace(); }

                            break;
    					}
    					case (3) :
    					{
                            try
                            {
                                Bitmap mBitmap = Images.Media.getBitmap(getContentResolver(), data.getData());
                                mImageView[2].setScaleType(ImageView.ScaleType.FIT_XY);
                                mImageView[2].setImageBitmap(mBitmap);
                            }
                            catch (FileNotFoundException e) { e.printStackTrace(); }
                            catch (IOException e) { e.printStackTrace(); }

                            break;
    					}
    				}
                    break;
    			}
    		}
    		
    	}
    }
	
	/* TODO Image를 회전을 시키는 함수 */
	private Bitmap matrixBitmap(Bitmap tempBitmap)
	{
        /* Bitmap */
        Bitmap mBitmap = null;

        /* BitmapFactory */
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inDither  = true;
        option.inPurgeable = true;

        if(tempBitmap != null) /* Bitmap이 공백이 아닐 경우 */
		{

			/* Matrix */
			Matrix mMatrix = new Matrix(); /* Matrix 객체 생성 */
            mMatrix.setRotate(90); /* Matrix 회전각 값 설정 */

			try
			{
                mBitmap = Bitmap.createBitmap(tempBitmap,0,0, tempBitmap.getWidth(), tempBitmap.getHeight(), mMatrix, true); /* Bitmap 객체 생성 */
                tempBitmap.recycle();
			} 
			catch(OutOfMemoryError e)
            {
                e.printStackTrace();
                Log.e("PHOTO - MATRIX", e.getMessage());
            }
		}
        return mBitmap;
	}

    private class ImageSave extends AsyncTask<String, Void, String>
    {
        /* ProgressDialog */
        private ProgressDialog mProgressDialog = null;

        @Override
        protected void onPreExecute()
        {
			/* Dialog 설정 구문 */
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); /* 원형 프로그래스 다이얼 로그 스타일로 설정 */
            mProgressDialog.setMessage("잠시만 기다려 주세요.");
            mProgressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params)
        {
            /* RelativeLayout */
            RelativeLayout mRelativeLayout = (RelativeLayout)findViewById(R.id.main_container);
            mRelativeLayout.setDrawingCacheEnabled(true);
            mRelativeLayout.buildDrawingCache(true);

            /* Bitmap */
            Bitmap result = Bitmap.createBitmap(mRelativeLayout.getMeasuredWidth(), mRelativeLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888); /* minutely_total_relative 에 대한 가로 값과 높이 값을 저장 하고 Bitmap에 대한 투명도를 지정한다  */

            /* Canvas */
            Canvas screenShotCanvas = new Canvas(result);
            mRelativeLayout.draw(screenShotCanvas);

            /* File */
            File imagePath = new File(params[0]);

            if(!imagePath.exists()) { imagePath.mkdirs(); }

            File fileCacheItem = new File(params[0] + renameFile());
            String path = fileCacheItem.getAbsolutePath(); /* 해당 파일이 저장 되어 있는 경로를 저장 */
            OutputStream out = null;

            try
            {
                fileCacheItem.createNewFile();
                out = new FileOutputStream(fileCacheItem);
                result.compress(CompressFormat.JPEG, 100, out);
            }
            catch (FileNotFoundException e) { Log.e("GREC", e.getMessage()); }
            catch (IOException e) { Log.e("GREC", e.getMessage()); }

            return path;
        }

        @Override
        protected void onPostExecute(String result)
        {
            Toast.makeText(mContext, "정상적으로 저장이 완료되었습니다.", Toast.LENGTH_SHORT).show(); /* 정상적으로 저장이 완료 되었을 경우 */
            mProgressDialog.dismiss();
            super.onPostExecute(result);
        }

        private String renameFile()
        {
            /* String */
            String mString[] = {mEditText.getText().toString(), null};

             int year = mCalendar.get(mCalendar.YEAR); /* 년 */
             int month = mCalendar.get(mCalendar.MONTH); /* 월 */
             int day = mCalendar.get(mCalendar.DAY_OF_MONTH); /* 일 */

             mString[1] = String.format("%d년 %d월 %d일 %s.JPG", year, month, day, mString);
             return mString[1]; /* 리턴 값 전달 */
        }
    }
}