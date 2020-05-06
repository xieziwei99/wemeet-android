package com.example.wemeet;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.utils.overlay.MovingPointOverlay;
import com.example.wemeet.pojo.Bug;
import com.example.wemeet.pojo.BugInterface;
import com.example.wemeet.pojo.BugProperty;
import com.example.wemeet.pojo.CatcherBugRecord;
import com.example.wemeet.pojo.VirusPoint;
import com.example.wemeet.pojo.user.User;
import com.example.wemeet.pojo.user.UserInterface;
import com.example.wemeet.util.MarkerInfo;
import com.example.wemeet.util.MathUtil;
import com.example.wemeet.util.NetworkUtil;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
    private DrawerLayout mDrawerLayout;     //侧滑菜单


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //topbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //侧栏导航栏
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }       //导航按钮的显示

        //侧栏个人信息
        if(navigationView.getHeaderCount() > 0) {
            View header = navigationView.getHeaderView(0);
            ((TextView) header.findViewById(R.id.text_user_id)).append(": " + "id");
            ((TextView) header.findViewById(R.id.text_user_name)).append(": " + "name");
            ((TextView) header.findViewById(R.id.text_user_email)).append(": " + "email");
            ((TextView) header.findViewById(R.id.text_user_score)).append(": " + "score");
        }

        navigationView.setCheckedItem(R.id.home);//将首页菜单项设置为默认选中
        //侧滑栏menu选项的监听
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setCheckable(true);
                item.setChecked(true);
                switch(item.getItemId()) {
                    case R.id.button_logout://登出
                        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove(LoginActivity.LOGGED_IN);
                        editor.remove(LoginActivity.USER_EMAIL);
                        editor.apply();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                }
                mDrawerLayout.closeDrawers();//关闭滑动菜单
                return true;
            }
        });

        //悬浮按钮
        FloatingActionButton plantBug = (FloatingActionButton)findViewById(R.id.button_plant_bugs);
        plantBug.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                plantBugs(v);
            }});

        requestPermissions();

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

        // 对每个marker设置一个点击事件
        aMap.setOnInfoWindowClickListener(marker -> {
            MarkerInfo info = (MarkerInfo) marker.getObject();
            if (info.getBug() != null) {
                Intent intent = new Intent(this, ShowQuestionActivity.class);
                intent.putExtra("bug", info.getBug());
                intent.putExtra("caught", info.isCaught());
                intent.putExtra("userAnswer", info.getUserAnswer());
                startActivity(intent);
            }
        });

        aMap.setOnMapClickListener(latLng -> this.markerList.forEach(marker1 -> {
            if (marker1.isInfoWindowShown()) {
                marker1.hideInfoWindow();
            }
        }));

        // 为什么在这里输出 aMap.getMyLocation() 是 null，难道是因为异步任务？
        new Thread(() -> System.out.println("-------------------" + aMap.getMyLocation())).start();
    }

    //toolbar的menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tool_bar,menu);
        return true;
    }

    //toolbar的事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_setting:
                Toast.makeText(this,"点击设置" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_help:
                Toast.makeText(this,"点击了帮助" , Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
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
                        List<Bug> aroundBugs = response.body();

                        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
                        String email = settings.getString(LoginActivity.USER_EMAIL, "error");
                        if (!"error".equals(email)) {
                            request.getCatchRecordsByEmail(email).enqueue(new Callback<Set<CatcherBugRecord>>() {
                                @Override
                                public void onResponse(Call<Set<CatcherBugRecord>> call, Response<Set<CatcherBugRecord>> response) {
                                    Set<CatcherBugRecord> records = response.body();

                                    if (aroundBugs != null) {
                                        for (Bug bug : aroundBugs) {
                                            Marker marker = null;
                                            final BugProperty bugProperty = bug.getBugProperty();
                                            if (bugProperty.getBugContent().getType() == 1) {
                                                marker = aMap.addMarker(new MarkerOptions().position(new LatLng(
                                                        bugProperty.getStartLatitude(), bugProperty.getStartLongitude()))
                                                        .title("第" + bugProperty.getBugID() + "号虫子")
                                                        .snippet("发布时间：" + bugProperty.getStartTime().toString() + "\n"
                                                                + "剩余可捉次数：" + bugProperty.getRestLifeCount() + "\n"
                                                                + "点击进行捕捉")
                                                );
                                                MarkerInfo info = new MarkerInfo();
                                                info.setBug(bug).setCaught(false).setUserAnswer(null);
                                                if (records != null) {
                                                    for (CatcherBugRecord record : records) {
                                                        if (record.getCaughtBug().equals(bugProperty)) {
                                                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gray)));
                                                            marker.setSnippet("你已经捉过了哦！\n" + "点击可查看详细情况");
                                                            info.setCaught(true).setUserAnswer(record.getUserAnswer());
                                                        }
                                                    }
                                                }
                                                marker.setObject(info);
                                            } else if (bugProperty.getBugContent().getType() == 4) {
                                                // 为什么在这里可以使用 aMap.getMyLocation()
                                                double userLat = aMap.getMyLocation().getLatitude();
                                                double userLon = aMap.getMyLocation().getLongitude();
                                                double bugLat = bugProperty.getStartLatitude();
                                                double bugLon = bugProperty.getStartLongitude();
                                                VirusPoint virusPoint = bug.getVirusPoint();
                                                marker = aMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(bugLat, bugLon))
                                                        .title(getString(R.string.疫情点))
                                                        .snippet(String.format(Locale.CHINA, "距离您大约%.2f米\n种植者 %s\n症状 %s\n发病时间 %s\n其他描述 %s",
                                                                MathUtil.getDistance(bugLat, bugLon, userLat, userLon),
                                                                bugProperty.getPlanter().getName(),
                                                                virusPoint.getSymptoms(),
                                                                virusPoint.getDiseaseStartTime(),
                                                                virusPoint.getDescription()
                                                        ))
                                                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.red)))
                                                );
                                            }
                                            markerList.add(marker);

                                            if (bugProperty.isMovable()) {
                                                // make markers movable
                                                MovingPointOverlay smoothMoveMarker = new MovingPointOverlay(aMap, marker);
                                                List<LatLng> points = new ArrayList<LatLng>() {{
                                                    add(new LatLng(bugProperty.getStartLatitude(), bugProperty.getStartLongitude()));
                                                    add(new LatLng(bugProperty.getDestLatitude(), bugProperty.getDestLongitude()));
                                                }};
                                                smoothMoveMarker.setPoints(points);
                                                smoothMoveMarker.setTotalDuration(90);
                                                smoothMoveMarker.setVisible(true);
                                                smoothMoveMarker.startSmoothMove();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Set<CatcherBugRecord>> call, Throwable t) {

                                }
                            });
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
        FloatingActionButton plantBugButton = findViewById(R.id.button_plant_bugs);
        String command = plantBugButton.getLabelText();
        if ("种植虫子".equals(command)) {
            marker = aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude()))
                    .title("种植虫子")
                    .snippet("长按标记来拖动标记以确定种植位置")
                    .draggable(true)
            );
            marker.showInfoWindow();
            markerList.forEach(marker1 -> marker1.setClickable(false));
            plantBugButton.setLabelText("确认");
        }
        if ("确认".equals(command)) {
            // 弹窗让用户选择种植虫子的类型
            String[] typeChoices = {getString(R.string.单项选择题), getString(R.string.疫情点)};
            final int[] typeChosen = new int[1];    // FIXME: 2020/3/3 可否更好的解决
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("WeMeet 选择虫子类型")
                    .setSingleChoiceItems(typeChoices, 0, (dialog, which) -> typeChosen[0] = which)
                    .setPositiveButton("确定", (dialog, which) -> {
                        Intent intent = new Intent(MainActivity.this, AddBugActivity.class);
                        switch (typeChosen[0]) {
                            case 0:
                                intent.putExtra("type", 1);
                                break;
                            case 1:
                                intent.putExtra("type", 4);
                                break;
                            default:
                                break;
                        }
                        intent.putExtra("lat", marker.getPosition().latitude);
                        intent.putExtra("lon", marker.getPosition().longitude);
                        startActivity(intent);
                    })
                    .create()
                    .show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 每次start都会重新加载大量虫子（如果有的话）
        showAroundBugs(116.22, 39.99);
//        // 初始视角移动到北邮
//        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                new LatLng(39.9643, 116.3557), 16, 0, 0)));

        // 定位
        locationClient = new AMapLocationClient(getApplicationContext());
        locationListener = location -> {
            if (location != null) {
                if (location.getErrorCode() == 0) {
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                            new LatLng(location.getLatitude(), location.getLongitude()), 16, 0, 0)));
                } else {    // 定位失败
                    Log.e("AMapError", "location Error, ErrCode:"
                            + location.getErrorCode() + ", errInfo:"
                            + location.getErrorInfo());
                }
            }
        };
        locationClient.setLocationListener(locationListener);
        option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                .setOnceLocation(true);
        locationClient.setLocationOption(option);
        locationClient.startLocation();

        // 设置option场景
//        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
//        if (null != locationClient) {
//            locationClient.setLocationOption(option);
//            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
//            locationClient.stopLocation();
//            locationClient.startLocation();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        aMap.clear();
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

    /**
     * 申请程序需要的权限
     */
    private void requestPermissions() {
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
    }
}
