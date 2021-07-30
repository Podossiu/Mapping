package com.example.test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import com.example.test2.Permission.AppPermissionHandlerActivity;
import com.example.test2.Permission.AppPermissionsInfo;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;
import androidx.annotation.NonNull;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

public class MapViewActivity extends AppCompatActivity
        implements MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.MapViewEventListener
                   ,MapView.CurrentLocationEventListener, AutoPermissionsListener{

    private static final String LOG_TAG = "MapViewAct";
    private MapView mMapView;
    private MapReverseGeoCoder mReverseGeoCoder = null;
    private boolean isUsingCustomLocationMarker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // autopermission 함수 사용 permission 확인
        AutoPermissions.Companion.loadAllPermissions(this,101);

        mMapView = findViewById(R.id.map_view);

        mMapView.setMapViewEventListener(this);
        mMapView.setMapType(MapView.MapType.Standard);

        mMapView.setCurrentLocationRadius(100); // meter
        mMapView.setCurrentLocationRadiusFillColor(Color.argb(77, 255, 255, 0));
        mMapView.setCurrentLocationRadiusStrokeColor(Color.argb(77, 255, 165, 0));

        MapPOIItem.ImageOffset trackingImageAnchorPointOffset = new MapPOIItem.ImageOffset(28, 28); // 좌하단(0,0) 기준 앵커포인트 오프셋
        MapPOIItem.ImageOffset directionImageAnchorPointOffset = new MapPOIItem.ImageOffset(65, 65);
        MapPOIItem.ImageOffset offImageAnchorPointOffset = new MapPOIItem.ImageOffset(15, 15);
        mMapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.custom_arrow_map_present_tracking, trackingImageAnchorPointOffset);
        mMapView.setCustomCurrentLocationMarkerDirectionImage(R.drawable.custom_map_present_direction, directionImageAnchorPointOffset);
        mMapView.setCustomCurrentLocationMarkerImage(R.drawable.custom_map_present, offImageAnchorPointOffset);

        // gps_data 객체 생성
        Gps_Data gps_data = new Gps_Data(MapViewActivity.this);
        double latitude = gps_data.getLatitude();
        double longitude = gps_data.getLongitude();


        Button tracking_btn = (Button) findViewById(R.id.tracking_button);
        tracking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMapView.getCurrentLocationTrackingMode() == MapView.CurrentLocationTrackingMode.TrackingModeOff) {
                    mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                    mMapView.setShowCurrentLocationMarker(true);
                    mMapView.setZoomLevel(2, true);
                    mReverseGeoCoder = new MapReverseGeoCoder("d058366a1b603c25fbc930a62ac7eab5", mMapView.getMapCenterPoint(), MapViewActivity.this, MapViewActivity.this);
                    mReverseGeoCoder.startFindingAddress();
                }else {
                    mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                    mMapView.setShowCurrentLocationMarker(false);
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // net.daum.mf.map.api.MapView.MapViewEventListener
    public void onMapViewInitialized(MapView mapView) {
        Log.i(LOG_TAG, "MapView had loaded. Now, MapView APIs could be called safely");
        //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.537229,127.005515), 2, true);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapCenterPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewCenterPointMoved (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("DaumMapLibrarySample");
        alertDialog.setMessage(String.format("Double-Tap on (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
        alertDialog.setPositiveButton("OK", null);
        alertDialog.show();
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

        if(mMapView.getCurrentLocationTrackingMode() == MapView.CurrentLocationTrackingMode.TrackingModeOff) {
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            mMapView.setShowCurrentLocationMarker(true);
            mMapView.setZoomLevel(2, true);
            mReverseGeoCoder = new MapReverseGeoCoder("d058366a1b603c25fbc930a62ac7eab5", mMapView.getMapCenterPoint(), MapViewActivity.this, MapViewActivity.this);
            mReverseGeoCoder.startFindingAddress();
        }else {
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mMapView.setShowCurrentLocationMarker(false);
        }
        MapPoint.GeoCoordinate mapPointGeo = mapView.getMapCenterPoint().getMapPointGeoCoord();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("DaumMapLibrarySample");
        alertDialog.setMessage(String.format("Long-Press on (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
        alertDialog.setPositiveButton("OK", null);
        alertDialog.show();
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewSingleTapped (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewDragStarted (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewDragEnded (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onMapViewMoveFinished (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
        Log.i(LOG_TAG, String.format("MapView onMapViewZoomLevelChanged (%d)", zoomLevel));
    }

    /////////////////////////////////////////////////
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }
    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
        Toast.makeText(MapViewActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onDenied(int i, String[] strings) {

    }

    @Override
    public void onGranted(int i, String[] strings) {

    }

}
