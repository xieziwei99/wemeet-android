package com.example.wemeet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.wemeet.pojo.VirusPoint;

import java.util.Objects;

import androidx.fragment.app.DialogFragment;

public class ChangeLevelActivity extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view;
        view = inflater.inflate(R.layout.change_virus_level,container,false);
        Bundle bundle = getArguments();
        assert bundle != null;
        VirusPoint virusPoint = (VirusPoint)bundle.getSerializable("virusPoint");

        ImageView close = view.findViewById(R.id.close_button);
        close.setOnClickListener(v -> {
            dismiss();
            ShowVirusActivity showVirusActivity = new ShowVirusActivity();
            showVirusActivity.setArguments(bundle);
            assert getFragmentManager() != null;
            showVirusActivity.show(getFragmentManager(),"virus");
        });

        Spinner changeLevel = view.findViewById(R.id.level_change);
        assert virusPoint != null;
        changeLevel.setSelection(virusPoint.getStatus()-1);
        int toLevel = changeLevel.getSelectedItemPosition()+1;
        Button submitChangeLevel = view.findViewById(R.id.submit_change_level);

        //----------------------------------变更疫情点等级-------------------------------------
        //----------------------------不知道怎么把修改的数据保存-----------------------------
        submitChangeLevel.setOnClickListener(view1 -> {
            switch (toLevel){
                case 1:
                    virusPoint.setStatus(1);
                    break;
                case 2:
                    virusPoint.setStatus(2);
                    break;
                case 3:
                    virusPoint.setStatus(3);
                    break;
            }
            dismiss();
            reloadMap();
            ShowVirusActivity showVirusActivity = new ShowVirusActivity();
            showVirusActivity.setArguments(bundle);
            assert getFragmentManager() != null;
            showVirusActivity.show(getFragmentManager(),"virus");
        });
        return view;
    }

    @Override
    public void onResume() {
        LayoutParams params = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);
        super.onResume();
    }

    private void reloadMap(){
        MainActivity mainActivity = (MainActivity)getActivity();
        assert mainActivity != null;
        mainActivity.aMap.clear();
        mainActivity.showAroundBugs(116.22, 39.99);
    }
}
