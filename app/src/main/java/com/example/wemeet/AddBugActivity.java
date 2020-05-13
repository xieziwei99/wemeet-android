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

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.wemeet.pojo.Bug;
import com.example.wemeet.pojo.BugInterface;
import com.example.wemeet.pojo.BugProperty;
import com.example.wemeet.pojo.ChoiceQuestion;
import com.example.wemeet.pojo.ContentDesc;
import com.example.wemeet.pojo.VirusPoint;
import com.example.wemeet.pojo.user.User;
import com.example.wemeet.pojo.user.UserInterface;
import com.example.wemeet.util.NetworkUtil;
import com.example.wemeet.util.ReturnVO;

import java.sql.Timestamp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBugActivity extends AppCompatActivity {
    private EditText symptomsEditText;
    private Timestamp diseaseStartTime = null;
    EditText survivalTimeEditText = null;
    EditText questionTextInput = null;
    EditText aInput = null;
    EditText bInput = null;
    EditText cInput = null;
    EditText dInput = null;
    EditText scoreInput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 1);
        switch (type) {
            case 1:
                setContentView(R.layout.activity_add_bug);
                ((TextView) findViewById(R.id.text_question_type)).append("：" + getString(R.string.单项选择题));

                questionTextInput = findViewById(R.id.input_question_text);
                aInput = findViewById(R.id.input_a);
                bInput = findViewById(R.id.input_b);
                cInput = findViewById(R.id.input_c);
                dInput = findViewById(R.id.input_d);
                scoreInput = findViewById(R.id.input_score);
                survivalTimeEditText = findViewById(R.id.editText_survivalTime);

                // 添加非空约束Listener
                TextChange textChange = new TextChange();
                questionTextInput.addTextChangedListener(textChange);
                aInput.addTextChangedListener(textChange);
                bInput.addTextChangedListener(textChange);
                cInput.addTextChangedListener(textChange);
                dInput.addTextChangedListener(textChange);
                scoreInput.addTextChangedListener(textChange);
                survivalTimeEditText.addTextChangedListener(textChange);
                break;
            case 4:
                setContentView(R.layout.acticity_add_virus_point);
                symptomsEditText = findViewById(R.id.editText_symptoms);
                survivalTimeEditText = findViewById(R.id.editText_survivalTime);
                ((TextView) findViewById(R.id.text_question_type)).append("：" + getString(R.string.疫情点));
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)   // 26
    public void addBug(View view) {
        Bug bug = new Bug();
        BugProperty bugProperty = new BugProperty();
        ChoiceQuestion choiceQuestion = new ChoiceQuestion();
        VirusPoint virusPoint = new VirusPoint();
        int score = 0;
        Intent intent = getIntent();
        long milli = System.currentTimeMillis();

        bugProperty
                .setStartLatitude(intent.getDoubleExtra("lat", 0))
                .setStartLongitude(intent.getDoubleExtra("lon", 0))
                .setStartTime(new Timestamp(milli))
                .setLifeCount(10)
                .setRestLifeCount(10);
        boolean movable = intent.getBooleanExtra("movable", false);
        String survivalTime = survivalTimeEditText.getText().toString();
        if (survivalTime.isEmpty()) {
            bugProperty.setSurvivalTime(24);
        } else {
            bugProperty.setSurvivalTime(Integer.valueOf(survivalTime));
        }
        bugProperty.setMovable(movable);
        bugProperty
                .setDestLatitude(intent.getDoubleExtra("destLat", bugProperty.getStartLatitude()))
                .setDestLongitude(intent.getDoubleExtra("destLon", bugProperty.getStartLongitude()));
        bug.setBugProperty(bugProperty);

        switch (view.getId()) {
            case R.id.button_add_bug:
                score = Integer.parseInt(((EditText) findViewById(R.id.input_score)).getText().toString());
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

                bug.setChoiceQuestion(choiceQuestion);
                break;
            case R.id.button_add_virus_point:
                score = 0;
                virusPoint
                        .setDescription(((EditText) findViewById(R.id.editText_description)).getText().toString())
                        .setSymptoms(symptomsEditText.getText().toString())
                        .setDiseaseStartTime(diseaseStartTime)
                        .setType(4)
                        .setPublishTime(new Timestamp(milli));
                bug.setVirusPoint(virusPoint);
                break;
        }

        int finalScore = score * -1;
        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
        String email = settings.getString(LoginActivity.USER_EMAIL, "error");
        NetworkUtil.getRetrofit().create(UserInterface.class)
                .getUserByEmail(email)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User planter = response.body();
                        bug.setPlanter(planter);

                        NetworkUtil.getRetrofit().create(BugInterface.class)
                                .addBug(bug)
                                .enqueue(new Callback<ReturnVO>() {
                                    @Override
                                    public void onResponse(Call<ReturnVO> call, Response<ReturnVO> response) {
                                        if (!"error".equals(email)) {
                                            NetworkUtil.getRetrofit().create(UserInterface.class)
                                                    .changeScoreOfUser(email, finalScore)
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

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            RadioGroup radioGroup = findViewById(R.id.radioGroup_correct_answer);
            Button button = findViewById(R.id.button_add_bug);

            boolean b = questionTextInput.getText().length() > 0;
            boolean b1 = aInput.getText().length() > 0;
            boolean b2 = bInput.getText().length() > 0;
            boolean b3 = cInput.getText().length() > 0;
            boolean b4 = dInput.getText().length() > 0;
            boolean b5 = radioGroup.getCheckedRadioButtonId() != -1;
            boolean b6 = scoreInput.getText().length() > 0;
            boolean b7 = survivalTimeEditText.getText().length() > 0;
            if (b && b1 && b2 && b3 && b4 && b5 && b6 && b7) {
                button.setEnabled(true);
            } else {
                button.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    // 选择时间按钮
    public void chooseTime(View view) {
        TimePickerView timePickerView = new TimePickerBuilder(this, (date, v) -> diseaseStartTime = new Timestamp(date.getTime()))
                .setType(new boolean[]{true, true, true, true, false, false})
                .build();
        timePickerView.show();
    }
}
