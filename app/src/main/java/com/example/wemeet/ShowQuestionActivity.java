package com.example.wemeet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wemeet.pojo.Bug;
import com.example.wemeet.pojo.ChoiceQuestion;
import com.example.wemeet.pojo.user.UserInterface;
import com.example.wemeet.util.NetworkUtil;
import com.example.wemeet.util.ReturnVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowQuestionActivity extends AppCompatActivity {
    private String message = "";
    //    private boolean right = false;
    private double score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_question);

        Intent intent = getIntent();
        Bug bug = (Bug) intent.getSerializableExtra("bug");
        assert bug != null;
        if (bug.getBugProperty().getBugContent().getType() == 1) {
            ((TextView) findViewById(R.id.questionTypeText)).setText("单项选择题");
            ChoiceQuestion bugContent = bug.getChoiceQuestion();
            ((TextView) findViewById(R.id.questionText)).setText(bugContent.getQuestion().getTextContent());
            ((RadioButton) findViewById(R.id.radioButtonA)).setText(bugContent.getChoiceA());
            ((RadioButton) findViewById(R.id.radioButtonB)).setText(bugContent.getChoiceB());
            if (bugContent.getChoiceC() != null) {
                ((RadioButton) findViewById(R.id.radioButtonC)).setText(bugContent.getChoiceC());
            } else {
                View buttonC = findViewById(R.id.radioButtonC);
                buttonC.setVisibility(View.GONE);
                buttonC.setClickable(false);
            }
            if (bugContent.getChoiceD() != null) {
                ((RadioButton) findViewById(R.id.radioButtonD)).setText(bugContent.getChoiceD());
            } else {
                View buttonD = findViewById(R.id.radioButtonD);
                buttonD.setClickable(false);
                buttonD.setVisibility(View.GONE);
            }
        }
    }

    public void showAnswerResult(View view) {
        Intent intent = getIntent();
        Bug bug = (Bug) intent.getSerializableExtra("bug");
        assert bug != null;

        boolean checked = ((RadioButton) view).isChecked();

        // 点击过后使单选框不能再被点击
        RadioGroup radioGroup = findViewById(R.id.radio_group_choices);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }

        if (bug.getBugProperty().getBugContent().getType() == 1) {
            ChoiceQuestion bugContent = bug.getChoiceQuestion();
            String correctAnswer = bugContent.getCorrectAnswer().toUpperCase();
            String userAnswer = "";
            switch (view.getId()) {
                case R.id.radioButtonA:
                    if (checked) {
                        userAnswer = "A";
                    }
                    break;
                case R.id.radioButtonB:
                    if (checked) {
                        userAnswer = "B";
                    }
                    break;
                case R.id.radioButtonC:
                    if (checked) {
                        userAnswer = "C";
                    }
                    break;
                case R.id.radioButtonD:
                    if (checked) {
                        userAnswer = "D";
                    }
                    break;
            }
            if (userAnswer.equals(correctAnswer)) {
//                right = true;
                score = bugContent.getScore();
                message = "恭喜你！答对了，增加积分：" + score + "分。";
            } else {
//                right = false;
                score = (((double) bugContent.getScore()) / 4) * -1;
                message = "Sorry! 很遗憾，你将被扣除积分：" + score * -1 + "分。\n正确答案是：" + correctAnswer;
            }
        }

        SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
        String email = settings.getString(LoginActivity.USER_EMAIL, "error");
        if (!"error".equals(email)) {
            UserInterface userInterface = NetworkUtil.getRetrofit().create(UserInterface.class);
            userInterface.changeScoreOfUser(email, score).enqueue(new Callback<ReturnVO>() {
                @Override
                public void onResponse(Call<ReturnVO> call, Response<ReturnVO> response) {
                    // nothing to do
                }

                @Override
                public void onFailure(Call<ReturnVO> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            // 通过bug.getBugProperty().getBugID()和email 建立虫子与用户间，捕捉与被捕捉的关系

        }

        new AlertDialog.Builder(this)
                .setTitle("WeMeet")
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.cancel();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ShowQuestionActivity.this.finish();
                })
                .setNeutralButton("再看看", null)
                .create()
                .show();
    }
}
