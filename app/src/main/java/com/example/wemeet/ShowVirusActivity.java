package com.example.wemeet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wemeet.pojo.Bug;

import org.w3c.dom.Text;

import androidx.fragment.app.DialogFragment;

public class ShowVirusActivity extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view;
        view = inflater.inflate(R.layout.activity_bug_content,container,false);
        Bundle bundle = getArguments();
        assert bundle != null;
        Bug bug = (Bug) bundle.getSerializable("bug");
        assert bug != null;
        if (bug.getBugProperty().getBugContent().getType() == 4) {
            switch (bug.getVirusPoint().getStatus()){
                case 1:
                    ((TextView)view.findViewById(R.id.level)).setText("症状虫子");
                    break;
                case 2:
                    ((TextView)view.findViewById(R.id.level)).setText("疑似虫子");
                    break;
                case 3:
                    ((TextView)view.findViewById(R.id.level)).setText("确诊虫子");
                    break;
            }
            ((TextView)view.findViewById(R.id.symptoms)).append("："+bug.getVirusPoint().getSymptoms());
            if(bug.getVirusPoint().getDiseaseStartTime()!=null){
                ((TextView)view.findViewById(R.id.symptoms_start_time)).append("："+bug.getVirusPoint().getDiseaseStartTime().toString());
            }
            else{
                ((TextView)view.findViewById(R.id.symptoms_start_time)).append("：无");
            }
            ((TextView)view.findViewById(R.id.note)).append("："+bug.getVirusPoint().getDescription());
        }

        ImageView close = (ImageView) view.findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button changeLevel = (Button)view.findViewById(R.id.change_level);
        changeLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putSerializable("virusPoint",bug.getVirusPoint());
                ChangeLevelActivity changeLevelActivity = new ChangeLevelActivity();
                changeLevelActivity.setArguments(bundle);
                changeLevelActivity.show(getFragmentManager(),"changeLevel");
                dismiss();
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        super.onResume();
    }

}
