package com.example.wemeet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wemeet.pojo.user.User;
import com.example.wemeet.pojo.user.UserInterface;
import com.example.wemeet.util.NetworkUtil;
import com.example.wemeet.util.ReturnVO;
import com.google.android.material.switchmaterial.SwitchMaterial;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String LOGGED_IN = "loggedIn";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_ROLE = "userRole";
    public int role = 0;

    EditText emailInput;
    EditText passwordInput;
    SwitchMaterial roleSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 监听输入框
        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);
        roleSwitch = findViewById(R.id.role_switch);
        roleSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getApplicationContext(), "切换至医生登录", Toast.LENGTH_SHORT).show();
                role = 1;
                roleSwitch.setText("当前登录身份：医生");
            } else {
                Toast.makeText(getApplicationContext(), "切换至普通用户登录", Toast.LENGTH_SHORT).show();
                role = 0;
                roleSwitch.setText("当前登录身份：普通用户");
            }
        });

        EditTextChange editTextChange = new EditTextChange();
        emailInput.addTextChangedListener(editTextChange);
        passwordInput.addTextChangedListener(editTextChange);
    }

    // 响应loginButton
    public void login(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        User tempUser = new User();
        tempUser.setEmail(email).setPassword(password).setRole(role);

        UserInterface userInterface = NetworkUtil.getRetrofit().create(UserInterface.class);
        userInterface.login(tempUser).enqueue(new Callback<ReturnVO>() {
            @Override
            public void onResponse(@NonNull Call<ReturnVO> call, @NonNull Response<ReturnVO> response) {
                ReturnVO result = response.body();
                assert result != null;
                Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                if (result.getCode() == 200) {
                    NetworkUtil.getRetrofit().create(UserInterface.class)
                            .getUserByEmail(email)
                            .enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    User user = response.body();
                                    assert user != null;
                                    // 保存登录状态信息
                                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putBoolean(LOGGED_IN, true);
                                    editor.putString(USER_EMAIL, email);
                                    editor.putInt(USER_ROLE, user.getRole());
                                    editor.apply();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    // 为了防止用户使用后退按钮返回到登录活动，您必须在启动一个新活动之后finish()该活动
                                    LoginActivity.this.finish();
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReturnVO> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // EditText监听器
    class EditTextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EditText emailInput = findViewById(R.id.input_email);
            EditText passwordInput = findViewById(R.id.input_password);
            Button loginButton = findViewById(R.id.button_login);
            boolean b = emailInput.getText().length() > 0;
            boolean b1 = passwordInput.getText().length() > 0;
            if (b && b1) {
                loginButton.setEnabled(true);
            } else {
                loginButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
