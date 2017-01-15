package net.daum.android.map.openapi.sampleapp.demos;

import net.daum.android.map.openapi.sampleapp.MapApiConst;
import net.daum.android.map.openapi.sampleapp.R;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class EventsDemoActivity extends FragmentActivity implements MapView.MapViewEventListener {
	
	private static final String LOG_TAG = "EventsDemoActivity";
	
	private MapView mapView;
    private TextView mTapTextView;
    private TextView mDragTextView;
    private TextView mCameraTextView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_events);
        mapView = (MapView)findViewById(R.id.daumMapView);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setMapViewEventListener(this);
        mTapTextView = (TextView) findViewById(R.id.tap_text);
        mDragTextView = (TextView) findViewById(R.id.drag_text);
        mCameraTextView = (TextView) findViewById(R.id.camera_text);
    }

	@Override
	public void onMapViewInitialized(MapView mapView) {
		// MapView had loaded. Now, MapView APIs could be called safely.
		mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);
		Log.i(LOG_TAG, "onMapViewInitialized");
	}

	@Override
	public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
		MapPoint.GeoCoordinate mapPointGeo = mapCenterPoint.getMapPointGeoCoord();
		mCameraTextView.setText("camera position{target=" + String.format("lat/lng: (%f,%f), zoomLevel=%d", mapPointGeo.latitude, mapPointGeo.longitude, mapView.getZoomLevel()));
		Log.i(LOG_TAG, String.format("MapView onMapViewCenterPointMoved (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
	}

	@Override
	public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
		MapPoint.GeoCoordinate mapPointGeo = mapView.getMapCenterPoint().getMapPointGeoCoord();
		mCameraTextView.setText("camera position{target=" + String.format("lat/lng: (%f,%f), zoomLevel=%d", mapPointGeo.latitude, mapPointGeo.longitude, mapView.getZoomLevel()));
		Log.i(LOG_TAG, String.format("MapView onMapViewZoomLevelChanged (%d)", zoomLevel));
	}

	@Override
	public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
		MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
		MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();
		mTapTextView.setText("single tapped, point=" + String.format("lat/lng: (%f,%f) x/y: (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude, mapPointScreenLocation.x, mapPointScreenLocation.y));
		Log.i(LOG_TAG, String.format("MapView onMapViewSingleTapped (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
	}

	@Override
	public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
		MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
		MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();
		mTapTextView.setText("double tapped, point=" + String.format("lat/lng: (%f,%f) x/y: (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude, mapPointScreenLocation.x, mapPointScreenLocation.y));
		Log.i(LOG_TAG, String.format(String.format("MapView onMapViewDoubleTapped (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude)));
	}

	@Override
	public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
		MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
		MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();
		mTapTextView.setText("long pressed, point=" + String.format("lat/lng: (%f,%f) x/y: (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude, mapPointScreenLocation.x, mapPointScreenLocation.y));
		Log.i(LOG_TAG, String.format(String.format("MapView onMapViewLongPressed (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude)));
	}
	
	@Override
	public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
		MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
		MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();
		mDragTextView.setText("drag started, point=" + String.format("lat/lng: (%f,%f) x/y: (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude, mapPointScreenLocation.x, mapPointScreenLocation.y));
		Log.i(LOG_TAG, String.format("MapView onMapViewDragStarted (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
	}
	
	@Override
	public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
		MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
		MapPoint.PlainCoordinate mapPointScreenLocation = mapPoint.getMapPointScreenLocation();
		mDragTextView.setText("drag ended, point=" + String.format("lat/lng: (%f,%f) x/y: (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude, mapPointScreenLocation.x, mapPointScreenLocation.y));
		Log.i(LOG_TAG, String.format("MapView onMapViewDragEnded (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
	}

	@Override
	public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
		MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Toast.makeText(getBaseContext(), "MapView move finished", Toast.LENGTH_SHORT).show();
		Log.i(LOG_TAG, String.format("MapView onMapViewMoveFinished (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
	}
}
