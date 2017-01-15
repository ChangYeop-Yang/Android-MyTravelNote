package kr.example.mytravelnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* SQLite 관련 Class 구문 */
class dbHelper extends SQLiteOpenHelper {
	
	/* SQLite 관련 변수 */
	public static final String DATABASE_NAME = "mytravel.db"; /* DataBase NAME */
	public static final int DATABASE_VERSION = 5; /* DataBase VERSION */
	
	/* TODO Auto-generated method stub */
	public dbHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); } /* SQLiteOpenHelper 생성자 함수 */

	@Override /* TODO Auto-generated method stub */
	public void onCreate(SQLiteDatabase db) 
	/* 데이터베이스가 처음으로 생성될 때에 호출이 되는 함수 */{
		
		/* SQLite Table 생성 */
		String tabel_query1 = "CREATE TABLE photo_db ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + "title TEXT, point TEXT, score FLOAR, latitude DOUBLE, longitude DOUBLE, path String);"; /* 사진 관련 */
		db.execSQL(tabel_query1); /* Query문 실핼 */
	}
	
	@Override /* TODO Auto-generated method stub */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	/* 데이터베이스가 업그레이드 될 필요가 있을 경우 호출이 되는 함수 */{
		/* SQLlite 관련 String 변수 */
		String db_query1 = "DROP TABLE IF EXISTS photo_db"; /* photo_db TABLE 삭제 */
		db.execSQL(db_query1); /* (구 버전은 삭제 후 새 버전으로 생성) -------> */ onCreate(db); }
}