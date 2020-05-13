package com.example.wemeet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.example.wemeet.pojo.Bug;
import com.example.wemeet.pojo.BugInterface;
import com.example.wemeet.pojo.VirusPoint;
import com.example.wemeet.util.NetworkUtil;
import com.example.wemeet.util.ReturnVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeLevelActivity extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view;
        view = inflater.inflate(R.layout.change_virus_level,container,false);
        Bundle bundle = getArguments();
        assert bundle != null;
        VirusPoint virusPoint = (VirusPoint)bundle.getSerializable("virusPoint");
        Bug bug = (Bug) bundle.getSerializable("bug");

        ImageView close = (ImageView) view.findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ShowVirusActivity showVirusActivity = new ShowVirusActivity();
                showVirusActivity.setArguments(bundle);
                showVirusActivity.show(getFragmentManager(),"virus");
            }
        });

        Spinner changeLevel = (Spinner)view.findViewById(R.id.level_change);
        assert virusPoint != null;
        changeLevel.setSelection(virusPoint.getStatus()-1);
        Button submitChangeLevel = (Button)view.findViewById(R.id.submit_change_level);

        submitChangeLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 保存 status 到数据库
                int toLevel = changeLevel.getSelectedItemPosition()+1;
                assert bug != null;
                Long bugID = bug.getBugProperty().getBugID();
                Bug newBug = new Bug();
                newBug.setVirusPoint(new VirusPoint().setStatus(toLevel));
                NetworkUtil.getRetrofit().create(BugInterface.class)
                        .updateBug(bugID, newBug)
                        .enqueue(new Callback<ReturnVO>() {
                            @Override
                            public void onResponse(Call<ReturnVO> call, Response<ReturnVO> response) {
                                // nothing to do. Maybe something to check
                            }

                            @Override
                            public void onFailure(Call<ReturnVO> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });

                dismiss();
                reloadMap();
                ShowVirusActivity showVirusActivity = new ShowVirusActivity();
                showVirusActivity.setArguments(bundle);
                showVirusActivity.show(getFragmentManager(),"virus");
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

    public void reloadMap(){
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.aMap.clear();
        mainActivity.showAroundBugs(116.22, 39.99);
    }
}
