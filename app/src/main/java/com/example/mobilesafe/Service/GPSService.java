package com.example.mobilesafe.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by abc on 2016/2/5.
 */
public class GPSService extends Service {
    private LocationManager lm;
    private MyLocationListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new MyLocationListener();
        //注册位置服务
        //给位置提供者设置条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // 设置参数细化：
        // criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度
        // criteria.setAltitudeRequired(false);//不要求海拔信息
        // criteria.setBearingRequired(false);//不要求方位信息
        // criteria.setCostAllowed(true);//是否允许付费
        // criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求

        String proveder = lm.getBestProvider(criteria, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        lm.requestLocationUpdates(proveder, 0, 0, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        lm.removeUpdates(listener);
        listener=null;
    }

    class MyLocationListener implements LocationListener {
         //位置变化时回调
        @Override
        public void onLocationChanged(Location location) {
            String longitude = "j:" + location.getLongitude() + "\n";
            String latitude = "w:" + location.getLatitude() + "\n";
            String accuracy = "a" + location.getAccuracy() + "\n";
            // 发短信给安全号码

            // 把标准的GPS坐标转换成火星坐标
            InputStream is;
            try {
                is = getAssets().open("axisoffset.dat");
                ModifyOffset offset = ModifyOffset.getInstance(is);
                PointDouble double1 = offset.s2c(new PointDouble(location
                        .getLongitude(), location.getLatitude()));
                latitude = "w:" + offset.Y + "\n";

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lastlocation", longitude + latitude + accuracy);
            editor.commit();
        }

        /**
         * 当状态发生改变的时候回调 开启--关闭 ；关闭--开启
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {


        }

        /**
         * 某一个位置提供者可以使用了
         */
        @Override
        public void onProviderEnabled(String provider) {


        }

        /**
         * 某一个位置提供者不可以使用了
         */
        @Override
        public void onProviderDisabled(String provider) {


        }
    }
}