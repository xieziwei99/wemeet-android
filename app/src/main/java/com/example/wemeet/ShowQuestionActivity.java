package com.example.wemeet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wemeet.pojo.Bug;
import com.example.wemeet.pojo.BugInterface;
import com.example.wemeet.pojo.ChoiceQuestion;
import com.example.wemeet.pojo.user.UserInterface;
import com.example.wemeet.util.NetworkUtil;
import com.example.wemeet.util.ReturnVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.fragment.app.DialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowQuestionActivity extends DialogFragment {
    private String message = "";
    //    private boolean right = false;
    private double score;
    private String userAnswer = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.activity_show_question, container, false);

        //关闭按钮
        ImageView close = (ImageView) view.findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //显示内容
        Bundle bundle = getArguments();
        assert bundle != null;
        Bug bug = (Bug) bundle.getSerializable("bug");
        assert bug != null;

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group_choices);
        RadioButton radioButtonA = (RadioButton) view.findViewById(R.id.radioButtonA);
        RadioButton radioButtonB = (RadioButton) view.findViewById(R.id.radioButtonB);
        RadioButton radioButtonC = (RadioButton) view.findViewById(R.id.radioButtonC);
        RadioButton radioButtonD = (RadioButton) view.findViewById(R.id.radioButtonD);
        Button submitButton = (Button) view.findViewById(R.id.submit_button);

        if (bug.getBugProperty().getBugContent().getType() == 1) {
            ((TextView) view.findViewById(R.id.questionTypeText)).setText("单项选择题");

            ChoiceQuestion bugContent = bug.getChoiceQuestion();
            //题目
            ((TextView) view.findViewById(R.id.questionText)).setText(bugContent.getQuestion().getTextContent());
            //选项
            radioButtonA.setText(bugContent.getChoiceA());
            radioButtonB.setText(bugContent.getChoiceB());
            if (bugContent.getChoiceC() != null) {
                radioButtonC.setText(bugContent.getChoiceC());
            } else {
                View buttonC = view.findViewById(R.id.radioButtonC);
                buttonC.setVisibility(View.GONE);
                buttonC.setClickable(false);
            }
            if (bugContent.getChoiceD() != null) {
                radioButtonD.setText(bugContent.getChoiceD());
            } else {
                View buttonD = view.findViewById(R.id.radioButtonD);
                buttonD.setClickable(false);
                buttonD.setVisibility(View.GONE);
            }
        }

        //是否已抓
        boolean caught = bundle.getBoolean("caught", false);
        if (caught) {
            String userAnswer = bundle.getString("userAnswer");
            List<String> answerList = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
            int checkedIndex = answerList.indexOf(userAnswer);
            ((RadioButton) radioGroup.getChildAt(checkedIndex)).setChecked(true);
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(false);
            }
            submitButton.setEnabled(false);
        }


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bug.getBugProperty().getBugContent().getType() == 1) {
                    ChoiceQuestion bugContent = bug.getChoiceQuestion();
                    String correctAnswer = bugContent.getCorrectAnswer().toUpperCase();
                    userAnswer = "";
                    if (radioButtonA.isChecked())
                        userAnswer = "A";
                    else if (radioButtonB.isChecked())
                        userAnswer = "B";
                    else if (radioButtonC.isChecked())
                        userAnswer = "C";
                    else if (radioButtonD.isChecked())
                        userAnswer = "D";
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

                SharedPreferences settings = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
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
                    NetworkUtil.getRetrofit().create(BugInterface.class)
                            .addUserCatchesBugConstraint(bug.getBugProperty().getBugID(), email, userAnswer)
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

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("WeMeet")
                        .setMessage(message)
                        .setPositiveButton("确定", (dialog, which) -> {
                            dialog.cancel();
                            ShowQuestionActivity.this.dismiss();
                            reloadMap();
                        })
                        .setNeutralButton("再看看", (dialogInterface, i) -> {
                            reloadMap();
                        })
                        .create()
                        .show();
            }
        });

        return view;
    }

    public void showAnswerResult(View view,Bundle savedInstanceState) {
//        Intent intent = getIntent();
//        Bundle bundle = intent.getBundleExtra("Message");
        Bundle bundle = getArguments();
        assert bundle != null;
        Bug bug = (Bug) bundle.getSerializable("bug");
        assert bug != null;

        boolean checked = ((RadioButton) view).isChecked();

        // 点击过后使单选框不能再被点击
        RadioGroup radioGroup = Objects.requireNonNull(getActivity()).findViewById(R.id.radio_group_choices);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }

        if (bug.getBugProperty().getBugContent().getType() == 1) {
            ChoiceQuestion bugContent = bug.getChoiceQuestion();
            String correctAnswer = bugContent.getCorrectAnswer().toUpperCase();
            userAnswer = "";
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

        SharedPreferences settings = Objects.requireNonNull(getActivity()).getSharedPreferences(LoginActivity.PREFS_NAME, 0); // 0 - for private mode
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
            NetworkUtil.getRetrofit().create(BugInterface.class)
                    .addUserCatchesBugConstraint(bug.getBugProperty().getBugID(), email, userAnswer)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("WeMeet")
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.cancel();
                    ShowQuestionActivity.this.dismiss();
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.onResume();
                })
                .setNeutralButton("再看看", null)
                .create()
                .show();

    }

    //窗口全屏（伪）
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        super.onResume();
    }

    //捉虫后刷新地图
    public void reloadMap(){
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.aMap.clear();
        mainActivity.showAroundBugs(116.22, 39.99);
    }
}
