package com.example.wemeet;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.wemeet.pojo.Bug;
import com.example.wemeet.pojo.BugInterface;
import com.example.wemeet.pojo.user.User;
import com.example.wemeet.pojo.user.UserInterface;
import com.example.wemeet.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    MapView mapView = null;
    AMap aMap = null;
    public AMapLocationClient locationClient = null;
    public AMapLocationClientOption option = null;
    public AMapLocationListener locationListener = null;
    private Marker marker = null;   // 保存新种植的marker
    private List<Marker> markerList = new ArrayList<>();    // 保存已经种植的marker列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 申请权限：定位权限、读权限、写权限
        String[] neededPermissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        List<String> tempPermissions = new ArrayList<>();
        for (String p : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                tempPermissions.add(p);
            }
        }
        if (!tempPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, tempPermissions.toArray(new String[0]), 100);
        }

        // 是否登录
        if (!hasLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }

        // 初始化地图
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(2000);
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        aMap.setMyLocationStyle(myLocationStyle);

        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.setMyLocationEnabled(true);

        // 定位
//        locationClient = new AMapLocationClient(getApplicationContext());
//        locationListener = location -> {
//            if (location != null) {
//                if (location.getErrorCode() == 0) {
//                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                            new LatLng(location.getLatitude(), location.getLongitude()), 16, 0, 0)));
//                } else {    // 定位失败
//                    Log.e("AMapError", "location Error, ErrCode:"
//                            + location.getErrorCode() + ", errInfo:"
//                            + location.getErrorInfo());
//                }
//            }
//        };
//        locationClient.setLocationListener(locationListener);
//        option = new AMapLocationClientOption();
//        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
//                .setOnceLocation(true);
//        locationClient.setLocationOption(option);
//        locationClient.startLocation();

        // 设置option场景
//        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
//        if (null != locationClient) {
//            locationClient.setLocationOption(option);
//            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
//            locationClient.stopLocation();
//            locationClient.startLocation();
//        }

        showAroundBugs(116.22, 39.99);
        // 初始视角移动到北邮
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(39.9643, 116.3557), 16, 0, 0)));
//        aMap.setOnMapLongClickListener(latLng -> aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker").draggable(true)));

        // 对每个marker设置一个点击事件
        aMap.setOnInfoWindowClickListener(marker -> {
            Bug bug = (Bug) marker.getObject();
            if (bug != null) {
                Intent intent = new Intent(this, ShowQuestionActivity.class);
                intent.putExtra("bug", bug);
                startActivity(intent);
            }
        });

        aMap.setOnMapClickListener(latLng -> this.markerList.forEach(marker1 -> {
            if (marker1.isInfoWindowShown()) {
                marker1.hideInfoWindow();
            }
        }));
    }

    // 判断用户是否登录
    public boolean hasLoggedIn() {
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
        return settings.getBoolean(LoginActivity.LOGGED_IN, false);
    }

    // 需要重载回调函数：用户对权限申请做出相应操作后执行
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限被拒绝：" + permissions[i], Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void showAroundBugs(double userLon, double userLat) {
        BugInterface request = NetworkUtil.getRetrofit().create(BugInterface.class);
        request.getAroundBugs(userLon, userLat)
                .enqueue(new Callback<List<Bug>>() {
                    @Override
                    public void onResponse(Call<List<Bug>> call, Response<List<Bug>> response) {
                        List<Bug> body = response.body();
                        if (body != null) {
                            for (Bug bug : body) {
                                Marker marker = aMap.addMarker(new MarkerOptions().position(new LatLng(
                                        bug.getBugProperty().getStartLatitude(), bug.getBugProperty().getStartLongitude()))
                                        .title(bug.getChoiceQuestion().getScore() + "分虫")   // TODO: 2020/2/6 需要改进逻辑
//                                        .title("第" + bug.getBugProperty().getBugID() + "号虫子")
                                        .snippet("发布时间：" + bug.getBugProperty().getStartTime().toString() + "\n点击进行捕捉"));
                                marker.setObject(bug);
                                markerList.add(marker);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Bug>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    // 响应登出按钮的事件
    public void logout(View view) {
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(LoginActivity.LOGGED_IN);
        editor.remove(LoginActivity.USER_EMAIL);
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    // 响应个人中心按钮的事件
    public void gotoUserCenter(View view) {
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
        String email = settings.getString(LoginActivity.USER_EMAIL, "error");
        if (!"error".equals(email)) {
            // 获取当前用户
            UserInterface userInterface = NetworkUtil.getRetrofit().create(UserInterface.class);
            userInterface.getUserByEmail(email).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    Intent intent = new Intent(MainActivity.this, UserCenterActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(this, "系统发生了不知名的错误", Toast.LENGTH_LONG).show();
        }
    }

    // 响应种植虫子按钮
    public void plantBugs(View view) {
        Button plantBugButton = findViewById(R.id.button_plant_bugs);
        String command = plantBugButton.getText().toString();
        if ("种植虫子".equals(command)) {
            marker = aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude()))
                    .title("种植虫子")
                    .snippet("长按标记来拖动标记以确定种植位置")
                    .draggable(true)
            );
            marker.showInfoWindow();
            findViewById(R.id.button_logout).setEnabled(false);
            findViewById(R.id.button_user_center).setEnabled(false);
            markerList.forEach(marker1 -> marker1.setClickable(false));
            plantBugButton.setText("确认");
        }
        if ("确认".equals(command)) {
//            Log.e("AMap", marker.getPosition().toString());
            Intent intent = new Intent(MainActivity.this, AddBugActivity.class);
            intent.putExtra("lat", marker.getPosition().latitude);
            intent.putExtra("lon", marker.getPosition().longitude);
            startActivity(intent);
        }
    }

    // 地图生命周期的管理，好像不写也没什么影响
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }
}
