package com.example.wemeet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 是否登录
        if (!hasLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            InitActivity.this.finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            InitActivity.this.finish();
        }
    }

    // 判断用户是否登录
    public boolean hasLoggedIn() {
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
        return settings.getBoolean(LoginActivity.LOGGED_IN, false);
    }
}
