package net.daum.android.map.openapi.sampleapp.demos;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import net.daum.android.map.openapi.sampleapp.MapApiConst;
import net.daum.android.map.openapi.sampleapp.R;
import net.daum.android.map.openapi.search.Item;
import net.daum.android.map.openapi.search.OnFinishSearchListener;
import net.daum.android.map.openapi.search.Searcher;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.GeoCoordinate;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchDemoActivity extends FragmentActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {

    private static final String LOG_TAG = "SearchDemoActivity";

    private MapView mMapView;
    private EditText mEditTextQuery;
    private Button mButtonSearch;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_search);

        mMapView = (MapView)findViewById(R.id.map_view);
        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);
        
        mEditTextQuery = (EditText) findViewById(R.id.editTextQuery); // 검색창
        
        mButtonSearch = (Button) findViewById(R.id.buttonSearch); // 검색버튼
        mButtonSearch.setOnClickListener(new OnClickListener() { // 검색버튼 클릭 이벤트 리스너
			@Override
			public void onClick(View v) {
		        String query = mEditTextQuery.getText().toString();
		        if (query == null || query.length() == 0) {
		        	showToast("검색어를 입력하세요.");
		        	return;
		        }
		        hideSoftKeyboard(); // 키보드 숨김
		        GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
		        double latitude = geoCoordinate.latitude; // 위도
		        double longitude = geoCoordinate.longitude; // 경도
		        int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
		        int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
		        String apikey = MapApiConst.DAUM_MAPS_LOCAL_API_KEY; // 로컬 apikey(http://dna.daum.net/apis/local) 
		        
		        Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
		        searcher.searchKeyword(query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
					@Override
					public void onSuccess(List<Item> itemList) {
						mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
						showResult(itemList); // 검색 결과 보여줌 
					}
					
					@Override
					public void onFail() {
						showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
					}
				});
			}
		});
    }
    
    private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditTextQuery.getWindowToken(), 0);
    }

    public void onMapViewInitialized(MapView mapView) {
        Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely");
        
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.537229,127.005515), 2, true);
        
        Searcher searcher = new Searcher();
        String query = mEditTextQuery.getText().toString();
        double latitude = 37.537229;
        double longitude = 127.005515;
        int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
        int page = 1;
        String apikey = MapApiConst.DAUM_MAPS_LOCAL_API_KEY;
        
        searcher.searchKeyword(query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
        	@Override
        	public void onSuccess(final List<Item> itemList) {
        		showResult(itemList);
        	}

        	@Override
        	public void onFail() {
        		showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
        	}
        });
    }
    
    private void showToast(final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(SearchDemoActivity.this, text, Toast.LENGTH_SHORT).show();
			}
		});
    }
    
	private void showResult(List<Item> itemList) {
		MapPointBounds mapPointBounds = new MapPointBounds();
		
		for (int i = 0; i < itemList.size(); i++) {
			Item item = itemList.get(i);

			MapPOIItem poiItem = new MapPOIItem();
			poiItem.setItemName(item.title);
			poiItem.setTag(i);
			MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
			poiItem.setMapPoint(mapPoint);
			mapPointBounds.add(mapPoint);
			poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
			poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
			poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
			poiItem.setCustomImageAutoscale(false);
			poiItem.setCustomImageAnchor(0.5f, 1.0f);

			View customCalloutBalloonView = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
			ImageView imageViewBadge = (ImageView) customCalloutBalloonView.findViewById(R.id.badge);
			TextView textViewTitle = (TextView) customCalloutBalloonView.findViewById(R.id.title);
			textViewTitle.setText(item.title);
			TextView textViewDesc = (TextView) customCalloutBalloonView.findViewById(R.id.desc);
			textViewDesc.setText(item.address);
			imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
			poiItem.setCustomCalloutBalloon(customCalloutBalloonView);

			mMapView.addPOIItem(poiItem);
			mTagItemMap.put(poiItem.getTag(), item);
		}
		
		mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
		
		MapPOIItem[] poiItems = mMapView.getPOIItems();
		if (poiItems.length > 0) {
			mMapView.selectPOIItem(poiItems[0], false);
		}
	}
	
	private Drawable createDrawableFromUrl(String url) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
	
	@Override
	public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
		Item item = mTagItemMap.get(mapPOIItem.getTag());
		StringBuilder sb = new StringBuilder();
		sb.append("title=").append(item.title).append("\n");
		sb.append("imageUrl=").append(item.imageUrl).append("\n");
		sb.append("address=").append(item.address).append("\n");
		sb.append("newAddress=").append(item.newAddress).append("\n");
		sb.append("zipcode=").append(item.zipcode).append("\n");
		sb.append("phone=").append(item.phone).append("\n");
		sb.append("category=").append(item.category).append("\n");
		sb.append("longitude=").append(item.longitude).append("\n");
		sb.append("latitude=").append(item.latitude).append("\n");
		sb.append("distance=").append(item.distance).append("\n");
		sb.append("direction=").append(item.direction).append("\n");
		Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	@Deprecated
	public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
	}

	@Override
	public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
	}

	@Override
	public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
	}

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
    }

}
