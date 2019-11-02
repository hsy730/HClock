package com.example.hclock;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.fragment.app.DialogFragment;

import com.example.hclock.utils.TimeUtil;


public class Fragment extends DialogFragment {
    static final String CLOCK_IN = "上班";
    static final String PUNCH_OFF = "下班";
    private static final String TAG = "Fragment";
    private static final int CLOCK_MSG = 0x256;
    private String select = "";
    private TimeUtil timeUtil;
//    DatabaseAdaper dbAdapter;
    private MyDialogFragment_Listener myDialogFragment_Listener;


    // 回调接口，用于传递数据给Activity -------
    public interface MyDialogFragment_Listener {
//        void getDataFromDialogFragment(int Data01, int Data02, int Data03);

        void getDataFromDialogFragment(String select);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            myDialogFragment_Listener = (MyDialogFragment_Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implementon MyDialogFragment_Listener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.clock_in, container);
        RadioGroup rg = view.findViewById(R.id.RadioGroup1);
        // 为按钮组绑定事件
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton r = view.findViewById(checkedId);
                select = r.getText().toString();
                Toast.makeText(getContext(),
                        "你选择的是:" + select, Toast.LENGTH_SHORT).show();
            }
        });
        Button okButton = view.findViewById(R.id.ok);
        Button cancelButton = view.findViewById(R.id.cancel);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                Toast.makeText(getContext(),
                        "你选择的是:确定" , Toast.LENGTH_SHORT).show();
//                ArrayList<WorkTimeRecord> records =  dbAdapter.findAll();
//                WorkTimeRecord record1 = records.get(0);
//                System.out.println(record1.getBeginTime() + ","+record1.getEndTime() + "," + record1.getDay());
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                Toast.makeText(getContext(),
                        "你选择的是:取消" , Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void dismiss() {
    }

    // DialogFragment关闭时回传数据给Activity
    @Override
    public void onDestroy() {
        // 通过接口回传数据给activity
        if (myDialogFragment_Listener != null) {
            myDialogFragment_Listener.getDataFromDialogFragment(select);
        }
        super.onDestroy();
    }
}