* Daum Map Android library
http://dna.daum.net/apis/mmaps/android

* Daum DNA Developer Network Forum
http://cafe.daum.net/daumdna

* Technical Support
jetoyrice@daumcorp.com
krispy@daumcorp.com


change log -----------------------------------------------
- 1.2.11 (2014/9/29)
  * 현위치 트래킹시 여러장의 마커 이미지를 순차적으로 보여주는 애니메이션 추가
  
- 1.2.9 (2014/9/12)
  * 커스텀 말풍선 Pressed 효과 추가
  * 마커 이미지 다양한 dpi 지원 
  * MapView 객체를 해제시키는 API 추가
  * 탭과 같은 미세한 움직임에는 dragStarted, dragEnded 이벤트 발생하지 않도록 수정
  
- 1.2.8 (2014/9/4)
  * 중심점과 직경(meter)으로 카메라 움직이는 기능 추가
  * CurrentLocationEventListener, POIItemEventListener NPE 수정

- 1.2.7 (2014/8/27)
  * 현위치 트래킹시 지도가 이동하지 않는 모드 추가
  * 지도 이동시 애니메이션이 안되는 현상 수정
  * selectPOIItem() 사용시 말풍선이 보이지 않는 현상 수정

- 1.2.6 (2014/8/14)
  * 커스텀 말풍선 기능 추가
  * 좌표-주소 변환 기능 개선
  
- 1.2.5 (2014/7/23)
  * onMapViewDragStarted, onMapViewDragEnded, onMapViewMoveFinished 이벤트 리스너 추가
  * moveCamera(CameraUpdate cameraUpdate) 메소드 추가
  * findPOIItemByName 메소드 호출시 발생하는 ClassCastException 처리
  * 여러개의 마커가 있을때, 마커를 차례대로 누르면 활성화/비활성화 아이콘이 비정상적으로 나타나는 현상 수정
  * 데모앱을 리스트뷰로 변경
  
- 1.2.4 (2014/7/1)
  * 마커 선택 효과 버그 수정
  
- 1.2.2 (2014/6/16)
  * 커스텀 현위치 마커 이미지, 반경 선/면 컬러 적용 기능 추가
  * 마커 선택 효과 추가
  * 말풍선 좌/우측 버튼 추가 기능 추가
  * 원그리기 기능 추가

- 1.2.1 (2013/3/12)
  * bug fix : net.daum.mf.map.api.MapView.POIItemEventListener.onPOIItemSelected() is not called for POI item that is not draggable.

- 1.2.0 (2012/11/1)
  * HD Map Tile support (@see net.daum.mf.map.api.MapView.setHDMapTileEnabled(boolean))
  * Map Tile Persistent Cache support (@see net.daum.mf.map.api.MapView.setMapTilePersistentCacheEnabled(boolean))
  * armv7 binary support (libs/armeabi, libs/armeabi-v7a)
  * enhanced CalloutBalloon/Pin UI design
  * When using custom image for MapPOIItem, anchor point of custom image is set to bottom-center point by default
  * do not change zoom level when current location tracking is activated
  * bug fix for net.daum.mf.map.api.MapView.getPOIItems() and getPolylines()

- 1.0.8 (2012/8/24)
  * fix crash issue when closing map-view

- 1.0.7 (2012/5/31)
  * fix for NullPointerException case of MapView.getMapCenterPoint()

- 1.0.5 (2012/5/7)
  * improve accuracy of current location tracking functionality
  * map tile versioning

- 1.0.1 (2012/4/18)
  * net.daum.mf.map.api.MapView.MapViewEventListener.onMapViewInitialized(MapView) event added
  * support embedding net.daum.mf.map.api.MapView as custom-view component in Android layout XML  

- 1.0 (2012/3/16)
  * initial release