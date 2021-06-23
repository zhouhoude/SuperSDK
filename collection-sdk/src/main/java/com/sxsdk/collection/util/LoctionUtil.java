package com.sxsdk.collection.util;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.sxsdk.collection.callback.InfoCallBack;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.PermissionChecker;

public class LoctionUtil {

    private static LocationManager locationManager;
    private static LocationListener locationListener;

    public static void getLocation(final Context context, final InfoCallBack callBack){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                location(context, location, callBack);
                stopLocation();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_DENIED) {
            Log.e("onLocationChanged", "location: GPS 拒绝");
            return;
        }
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_DENIED) {
            Log.e("onLocationChanged", "location: NET 拒绝");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1000, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1000, locationListener);
    }

    /**
     * 结束定位
     */
    private static void stopLocation() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    /**********
     * 获取所有联系人(只要手机号码为11位，如果有+86，去除+86。有重复去除重复)
     * @param context
     */
    private static void location(Context context, Location location, InfoCallBack callBack) {
        Geocoder gc = new Geocoder(context, Locale.getDefault());
        Log.e("onLocationChanged", "onLocationChanged: " + location.getLatitude() + "   " + location.getLongitude());
        List<Address> locationList = null;
        try {
            locationList = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("onLocationChanged", "locationList:error "+ e.getMessage());
        }
        JSONObject addressJson = new JSONObject();
        String addressDetail = "";
        if(locationList != null && locationList.size() > 0) {

            Log.e("onLocationChanged", "locationList: " + locationList.size());
            Address address = locationList.get(0);//得到Address实例
            if(address != null){
                addressJson.put("country_name", address.getCountryName());//国家
                addressJson.put("country_code", address.getCountryCode());//国家Code
                addressJson.put("admin_area", address.getAdminArea());//省
                addressJson.put("locality", address.getLocality());//市
                addressJson.put("sub_admin_area", address.getSubAdminArea());//区
                addressJson.put("feature_name", address.getFeatureName());//街道

                for(int i = 0; address.getAddressLine(i) != null; i++){
                    addressJson.put("address" + i, address.getAddressLine(i));
                }
                addressDetail = address.getAddressLine(0) + "";
            }
        }
        if (callBack != null) {
            Log.e("onLocationChanged", "addressDetail: " + addressDetail);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject json = new JSONObject();
            json.put("longitude", location.getLatitude());
            json.put("latitude", location.getLatitude());
            json.put("address", addressDetail);
            json.put("addressInfo", addressJson);
            json.put("longitude", format.format(new Date(location.getTime())));
            callBack.result(json.toJSONString());
        }
    }
}
