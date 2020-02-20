package com.example.wemeet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wemeet.pojo.Bug;
import com.example.wemeet.pojo.BugInterface;
import com.example.wemeet.pojo.BugProperty;
import com.example.wemeet.pojo.ChoiceQuestion;
import com.example.wemeet.pojo.ContentDesc;
import com.example.wemeet.pojo.user.UserInterface;
import com.example.wemeet.util.NetworkUtil;
import com.example.wemeet.util.ReturnVO;

import java.sql.Timestamp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bug);

        ((TextView) findViewById(R.id.text_question_type)).append("：选择题");

        EditText questionTextInput = findViewById(R.id.input_question_text);
        EditText aInput = findViewById(R.id.input_a);
        EditText bInput = findViewById(R.id.input_b);
        EditText cInput = findViewById(R.id.input_c);
        EditText dInput = findViewById(R.id.input_d);
        EditText scoreInput = findViewById(R.id.input_score);
        TextChange textChange = new TextChange();
        questionTextInput.addTextChangedListener(textChange);
        aInput.addTextChangedListener(textChange);
        bInput.addTextChangedListener(textChange);
        cInput.addTextChangedListener(textChange);
        dInput.addTextChangedListener(textChange);
        scoreInput.addTextChangedListener(textChange);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)   // 26
    public void addBug(View view) {
        Bug bug = new Bug();
        BugProperty bugProperty = new BugProperty();
        ChoiceQuestion choiceQuestion = new ChoiceQuestion();
        Intent intent = getIntent();

        long milli = System.currentTimeMillis();
        bugProperty
                .setStartLatitude(intent.getDoubleExtra("lat", 0))
                .setStartLongitude(intent.getDoubleExtra("lon", 0))
                .setMovable(false)
                .setSurvivalTime(24)
                .setStartTime(new Timestamp(milli))
                .setLifeCount(10);
        Integer score = Integer.valueOf(((EditText) findViewById(R.id.input_score)).getText().toString());
        choiceQuestion
                .setQuestion(new ContentDesc().setTextContent(
                        ((EditText) findViewById(R.id.input_question_text)).getText().toString()))
                .setChoiceA(((EditText) findViewById(R.id.input_a)).getText().toString())
                .setChoiceB(((EditText) findViewById(R.id.input_b)).getText().toString())
                .setChoiceC(((EditText) findViewById(R.id.input_c)).getText().toString())
                .setChoiceD(((EditText) findViewById(R.id.input_d)).getText().toString())
                .setScore(score)
                .setType(1)
                .setPublishTime(new Timestamp(milli));
        RadioGroup radioGroup = findViewById(R.id.radioGroup_correct_answer);
        String answer = ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
        choiceQuestion.setCorrectAnswer(answer);

        bug.setBugProperty(bugProperty);
        bug.setChoiceQuestion(choiceQuestion);

        NetworkUtil.getRetrofit().create(BugInterface.class)
                .addBug(bug)
                .enqueue(new Callback<ReturnVO>() {
                    @Override
                    public void onResponse(Call<ReturnVO> call, Response<ReturnVO> response) {
                        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
                        String email = settings.getString(LoginActivity.USER_EMAIL, "error");
                        if (!"error".equals(email)) {
                            NetworkUtil.getRetrofit().create(UserInterface.class)
                                    .changeScoreOfUser(email, score)
                                    .enqueue(new Callback<ReturnVO>() {
                                        @Override
                                        public void onResponse(Call<ReturnVO> call, Response<ReturnVO> response) {

                                        }

                                        @Override
                                        public void onFailure(Call<ReturnVO> call, Throwable t) {
                                            t.printStackTrace();
                                        }
                                    });
                        }
                        new AlertDialog.Builder(AddBugActivity.this)
                                .setTitle("WeMeet")
                                .setMessage("种植成功")
                                .setPositiveButton("确定", (dialog, which) -> {
                                    Intent intent1 = new Intent(AddBugActivity.this, MainActivity.class);
                                    startActivity(intent1);
                                    AddBugActivity.this.finish();
                                })
                                .create()
                                .show();
                    }

                    @Override
                    public void onFailure(Call<ReturnVO> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EditText questionTextInput = findViewById(R.id.input_question_text);
            EditText aInput = findViewById(R.id.input_a);
            EditText bInput = findViewById(R.id.input_b);
            EditText cInput = findViewById(R.id.input_c);
            EditText dInput = findViewById(R.id.input_d);
            EditText scoreInput = findViewById(R.id.input_score);
            RadioGroup radioGroup = findViewById(R.id.radioGroup_correct_answer);
            Button button = findViewById(R.id.button_add_bug);

            boolean b = questionTextInput.getText().length() > 0;
            boolean b1 = aInput.getText().length() > 0;
            boolean b2 = bInput.getText().length() > 0;
            boolean b3 = cInput.getText().length() > 0;
            boolean b4 = dInput.getText().length() > 0;
            boolean b5 = radioGroup.getCheckedRadioButtonId() != -1;
            boolean b6 = scoreInput.getText().length() > 0;
            if (b && b1 && b2 && b3 && b4 && b5 && b6) {
                button.setEnabled(true);
            } else {
                button.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }
}
