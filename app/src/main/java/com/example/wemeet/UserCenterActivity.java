package com.example.wemeet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wemeet.pojo.user.User;

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
}
