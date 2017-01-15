package net.daum.android.map.openapi.sampleapp.demos;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;
import net.daum.android.map.openapi.sampleapp.MapApiConst;
import net.daum.android.map.openapi.sampleapp.R;
import net.daum.mf.map.api.*;

public class CameraDemoActivity extends FragmentActivity implements MapView.MapViewEventListener {

    private static final int MENU_CAMERA_1 = Menu.FIRST;
    private static final int MENU_CAMERA_2 = Menu.FIRST + 1;
    private static final int MENU_CAMERA_3 = Menu.FIRST + 2;
    private static final int MENU_CAMERA_4 = Menu.FIRST + 3;
    private static final int MENU_CAMERA_5 = Menu.FIRST + 4;
    private MapView mapView;

    private static final MapPoint MAP_POINT_POI1 = MapPoint.mapPointWithGeoCoord(37.537229, 127.005515);
    private static final MapPoint MAP_POINT_POI2 = MapPoint.mapPointWithGeoCoord(37.4020737, 127.1086766);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_nested_mapview);
        mapView = (MapView)findViewById(R.id.map_view);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setMapViewEventListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_CAMERA_1, Menu.NONE, "Point 1");
        menu.add(0, MENU_CAMERA_2, Menu.NONE, "Point 2, Zoom Lv 7");
        menu.add(0, MENU_CAMERA_3, Menu.NONE, "Point 1, Diameter");
        menu.add(0, MENU_CAMERA_4, Menu.NONE, "Bounds, Padding");
        menu.add(0, MENU_CAMERA_5, Menu.NONE, "Bounds, Padding, Zoom min/max");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CAMERA_1 : {
                toast(item.getTitle());
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(MAP_POINT_POI1));
                return true;
            }
            case MENU_CAMERA_2 : {
                toast(item.getTitle());
                mapView.moveCamera(CameraUpdateFactory.newMapPoint(MAP_POINT_POI2, 7f));
                return true;
            }
            case MENU_CAMERA_3 : {
                toast(item.getTitle());
                mapView.moveCamera(CameraUpdateFactory.newMapPointAndDiameter(MAP_POINT_POI1, 500f));
                return true;
            }
            case MENU_CAMERA_4 : {
                toast(item.getTitle());
                MapPointBounds bounds = new MapPointBounds(MAP_POINT_POI1, MAP_POINT_POI2);
                mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, 100));
                return true;
            }
            case MENU_CAMERA_5 : {
                toast(item.getTitle());
                MapPointBounds bounds = new MapPointBounds(MAP_POINT_POI1, MAP_POINT_POI2);
                mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, 100, 3, 7));
                return true;
            }
        }

        return onOptionsItemSelected(item);
    }

    private void toast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        // MapView had loaded. Now, MapView APIs could be called safely.

        MapPOIItem poiItem1 = new MapPOIItem();
        poiItem1.setItemName("POI1");
        poiItem1.setMapPoint(MAP_POINT_POI1);
        poiItem1.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mapView.addPOIItem(poiItem1);

        MapPOIItem poiItem2 = new MapPOIItem();
        poiItem2.setItemName("POI2");
        poiItem2.setMapPoint(MAP_POINT_POI2);
        poiItem2.setMarkerType(MapPOIItem.MarkerType.YellowPin);
        mapView.addPOIItem(poiItem2);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
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
}
