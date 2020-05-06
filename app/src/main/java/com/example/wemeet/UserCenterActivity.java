package com.example.wemeet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wemeet.pojo.Bug;
import com.example.wemeet.pojo.BugInterface;
import com.example.wemeet.pojo.CatcherBugRecord;
import com.example.wemeet.pojo.user.User;
import com.example.wemeet.pojo.user.UserInterface;
import com.example.wemeet.util.NetworkUtil;

import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        assert user != null;
        ((TextView) findViewById(R.id.text_user_id)).append(": " + user.getId().toString());
        ((TextView) findViewById(R.id.text_user_name)).append(": " + user.getName());
        ((TextView) findViewById(R.id.text_user_email)).append(": " + user.getEmail());
        ((TextView) findViewById(R.id.text_user_score)).append(": " + user.getScore().toString());
    }

    // 种虫记录按钮
    public void getPlantBugsRecord(View view) {
        NetworkUtil.getRetrofit().create(UserInterface.class)
                .getPlantBugsByUserEmail(getUserEmail())
                .enqueue(new Callback<List<Bug>>() {
                    @Override
                    public void onResponse(Call<List<Bug>> call, Response<List<Bug>> response) {
                        List<Bug> plantBugs = response.body();
                        if (plantBugs != null) {
                            Toast.makeText(UserCenterActivity.this, "种植个数：" + plantBugs.size(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserCenterActivity.this, "你还没有种植过黄金虫", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Bug>> call, Throwable t) {
                        Log.e("网络请求失败", "onFailure: getPlantBugsByUserEmail()", t);
                    }
                });
    }

    // 捉虫记录按钮
    public void getCatchBugRecord(View view) {
        NetworkUtil.getRetrofit().create(BugInterface.class)
                .getCatchRecordsByEmail(getUserEmail())
                .enqueue(new Callback<Set<CatcherBugRecord>>() {
                    @Override
                    public void onResponse(Call<Set<CatcherBugRecord>> call, Response<Set<CatcherBugRecord>> response) {
                        Set<CatcherBugRecord> catchRecordSet = response.body();
                        if (catchRecordSet != null) {
                            Toast.makeText(UserCenterActivity.this, "捕捉个数：" + catchRecordSet.size(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Set<CatcherBugRecord>> call, Throwable t) {
                        Log.e("网络请求失败", "onFailure: getCatchRecordsByEmail()", t);
                    }
                });
    }

    private String getUserEmail() {
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
        return settings.getString(LoginActivity.USER_EMAIL, "error");
    }
}
