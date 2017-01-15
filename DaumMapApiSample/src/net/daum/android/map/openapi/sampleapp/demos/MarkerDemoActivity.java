package net.daum.android.map.openapi.sampleapp.demos;

import net.daum.android.map.openapi.sampleapp.MapApiConst;
import net.daum.android.map.openapi.sampleapp.R;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MarkerDemoActivity extends FragmentActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {

	private final int MENU_DEFAULT_MARKER = Menu.FIRST;
    private final int MENU_CUSTOM_MARKER = Menu.FIRST + 1;
    private final int MENU_SHOW_ALL = Menu.FIRST + 2;

    private static final MapPoint CUSTOM_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.537229, 127.005515);
    private static final MapPoint DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.4020737, 127.1086766);

    private MapView mMapView;
	private MapPOIItem mDefaultMarker;
    private MapPOIItem mCustomMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_nested_mapview);
        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setMapViewEventListener(this);
        mMapView.setPOIItemEventListener(this);

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_DEFAULT_MARKER, Menu.NONE, "DefaultMarker");
        menu.add(0, MENU_CUSTOM_MARKER, Menu.NONE, "CustomMarker");
        menu.add(0, MENU_SHOW_ALL, Menu.NONE, "ShowAll");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch (item.getItemId()) {
	    	case MENU_DEFAULT_MARKER : {
	    		createDefaultMarker(mMapView);
	    		return true;
	    	}
	    	case MENU_CUSTOM_MARKER : {
	    		createCustomMarker(mMapView);
	    		return true;
	    	}
	    	case MENU_SHOW_ALL : {
	    		showAll();
	    	}
    	}
    	
    	return super.onOptionsItemSelected(item);
    }

    private void createDefaultMarker(MapView mapView) {
    	if (mDefaultMarker == null) {
	        mDefaultMarker = new MapPOIItem();
	        String name = "Default Marker";
	        mDefaultMarker.setItemName(name);
	        mDefaultMarker.setTag(0);
	        mDefaultMarker.setMapPoint(DEFAULT_MARKER_POINT);
	        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
	        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
	
	        mapView.addPOIItem(mDefaultMarker);
    	}
    	
    	mapView.selectPOIItem(mDefaultMarker, true);
    	mapView.setMapCenterPoint(DEFAULT_MARKER_POINT, false);
    }

    private void createCustomMarker(MapView mapView) {
    	if (mCustomMarker == null) {
	        mCustomMarker = new MapPOIItem();
	        String name = "Custom Marker";
	        mCustomMarker.setItemName(name);
	        mCustomMarker.setTag(1);
	        mCustomMarker.setMapPoint(CUSTOM_MARKER_POINT);
	
	        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
	        mCustomMarker.setCustomImageResourceId(R.drawable.custom_marker_red);
	        mCustomMarker.setCustomImageAutoscale(false);
	        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);
	
	        View customCalloutBalloonView = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
	        ((ImageView) customCalloutBalloonView.findViewById(R.id.badge)).setImageResource(R.drawable.ic_launcher);
	        mCustomMarker.setCustomCalloutBalloon(customCalloutBalloonView);
	        
	        mapView.addPOIItem(mCustomMarker);
    	}
    	
    	mapView.selectPOIItem(mCustomMarker, true);
    	mapView.setMapCenterPoint(CUSTOM_MARKER_POINT, false);
    }

    private void showAll() {

        int padding = 20;
        float minZoomLevel = 7;
        float maxZoomLevel = 10;
        MapPointBounds bounds = new MapPointBounds(CUSTOM_MARKER_POINT, DEFAULT_MARKER_POINT);
        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

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
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Toast.makeText(this, "Click Callout Balloon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}
