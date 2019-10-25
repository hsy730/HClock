package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.helloworld.listener.MyOnCalendarSelectListener;
import com.example.helloworld.listener.MyOnMonthChangeListener;
import com.example.helloworld.sqlite.DatabaseAdaper;
import com.example.helloworld.sqlite.WorkTimeRecord;
import com.example.helloworld.utils.TimeUtil;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;


import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements  Fragment.MyDialogFragment_Listener{
    private MyOnCalendarSelectListener myOnCalendarSelectListener;

    private CalendarView.OnCalendarSelectListener mOnCalendarSelectListener;
    private MyOnMonthChangeListener myOnMonthChangeListener;
    private static final int onCalendarSelectMsg = 0x123;
    private static final int onMonthChangedMsg = 0x124;
    private MyHandler mMyHandler = new MyHandler(this);
    private static TextView title;
    private static String tempSelectedDate;
    Button edit,save,signInBt,signOutBt;
    private EditText signInEt,signOutEt;
    private TimeUtil timeUtil;
    FragmentManager fm = getSupportFragmentManager();
    DatabaseAdaper dbAdapter;
    CalendarView calendarView;
    private boolean validSignIn;
    private boolean validSignOut;
    private boolean isSignIn = true;
    private String signInTime = null;
    String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendarView);
        TextView text = findViewById(R.id.text);
        title = findViewById(R.id.title);
        edit = findViewById(R.id.edit);
        save = findViewById(R.id.save);
        signInEt = findViewById(R.id.sign_in_time);
        signOutEt = findViewById(R.id.sign_out_time);
        signInBt = findViewById(R.id.clock_in);
        signOutBt = findViewById(R.id.punch_off);


        title.setText(String.format("%s年%s月",calendarView.getCurYear(),calendarView.getCurMonth()));
        //不可编辑
        signInEt.setFocusableInTouchMode(false);
        signOutEt.setFocusableInTouchMode(false);
        dbAdapter = new DatabaseAdaper(MainActivity.this);
        int n =  calendarView.getCurDay();
        timeUtil = new TimeUtil();
        //获取选择的日期
        Calendar calendar = calendarView.getSelectedCalendar();
        System.out.println("选择日期"+calendar.getDay());
        // 日历控件监听器
        addCalendarListener();
        // 签到签退监听器
        addEditTextListener();
        // 签退
        if (signInTime != null && !"".equals(signInTime)) {
            signInBt.setVisibility(View.GONE);
            signOutBt.setVisibility(View.VISIBLE);
        } else {
            // 签到
            signInBt.setVisibility(View.VISIBLE);
            signOutBt.setVisibility(View.GONE);
        }

        calendar.getDay();
        Log.i(TAG, n+"");
        Log.i(TAG, "选择日期"+calendar.getDay()+"");
    }

    private void addEditTextListener() {
        signInEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = signInEt.getText().toString().trim();
                Log.i(TAG, "signInTime，beforeTextChanged: "+text);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String change = signInEt.getText().toString().trim();
                Log.i(TAG, "signInTime, afterTextChanged: "+change);
                if(!timeUtil.checkTimeFormat(change)) {
                    validSignIn = false;
                    Toast.makeText(MainActivity.this,"时间格式非法，请输入合法时间", Toast.LENGTH_SHORT).show();
                } else {
                    validSignIn = true;
                    if (validSignOut) {
                        save.setClickable(true);
                    }
                    Toast.makeText(MainActivity.this,"时间格式合法", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = signInEt.getText().toString().trim();
                Log.i(TAG, "signInTime，afterTextChanged: "+text);
            }
        });

        signOutEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = signOutEt.getText().toString().trim();
                Log.i(TAG, "signOutEt，beforeTextChanged: "+text);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String change = signOutEt.getText().toString().trim();
                Log.i(TAG, "signOutEt, afterTextChanged: "+change);
                if(!timeUtil.checkTimeFormat(change)) {
                    validSignOut = false;
                    save.setClickable(false);
                    Toast.makeText(MainActivity.this,"时间格式非法，请输入合法时间", Toast.LENGTH_SHORT).show();
                } else {
                    validSignOut = true;
                    if (validSignIn) {
                        save.setClickable(true);
                    }
                    Toast.makeText(MainActivity.this,"时间格式合法", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String text = signOutEt.getText().toString().trim();
                Log.i(TAG, "signOutEt，afterTextChanged: "+text);
            }
        });
    }

    private void addCalendarListener() {

        myOnCalendarSelectListener = new MyOnCalendarSelectListener(){
            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick){
                if(isClick) {
//                    text.setText(calendar.getDay());
                    //消息要先传进Message中，再由Message传递给Handler处理
                    Message msg = Message.obtain();
                    //Message类有属性字段arg1、arg2、what...
                    msg.obj = calendar;
                    msg.what = onCalendarSelectMsg;
                    //sendMessage()用来传送Message类的值到mHandler
                    mMyHandler.sendMessage(msg);
                    Log.i(TAG, "onCalendarSelect: "+ calendar.getDay());
                }
            }
        };

        myOnMonthChangeListener = new MyOnMonthChangeListener(){
            @Override
            public void onMonthChange(int year, int month){
                Message msg = Message.obtain();
                msg.obj = String.format("%s年%s月",year,month);
                msg.what = onMonthChangedMsg;
                mMyHandler.sendMessage(msg);
            }
        };
        calendarView.setOnCalendarSelectListener(myOnCalendarSelectListener);
        calendarView.setOnMonthChangeListener(myOnMonthChangeListener);
    }

    // 引用接口中定义的方法
    @Override
    public void getDataFromDialogFragment(String select) {
        Log.d(TAG, "DialogFragment回传的数据为：" + select);
        if ( Fragment.CLOCK_IN.equals(select)) {
            clockIn();
            Log.i(TAG, "getDataFromDialogFragment: 上班");
        }else if (Fragment.PUNCH_OFF.equals(select)) {
            punchOff();
            Log.i(TAG, "getDataFromDialogFragment: 下班");
        }

    }
    public void click(View view) {
        System.out.println("我被点击了");
        switch (view.getId()){
            case R.id.edit:
                afterClickEditButton();
                break;
            case R.id.save:
                afterClickSaveButton();
                break;
            case R.id.clock_in:

            case R.id.punch_off:

                break;
            case R.id.search_sql:
                searchAll();
                break;
                default:
                    break;
        }
    }

    private void searchAll() {
        ArrayList<WorkTimeRecord> records =  dbAdapter.findAll();
        System.out.println("====================");
        for (WorkTimeRecord record:records){
            System.out.println(record.toString());
        }
        System.out.println("--------------------");
    }

    private void afterClickEditButton() {
        signInEt.setFocusable(true);
        signInEt.setFocusableInTouchMode(true);

        signOutEt.setFocusable(true);
        signOutEt.setFocusableInTouchMode(true);

        edit.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        Toast.makeText(this,"可以修改", Toast.LENGTH_SHORT).show();
    }

    private void afterClickSaveButton() {
        signInEt.setFocusable(false);
        signInEt.setFocusableInTouchMode(false);

        signOutEt.setFocusable(false);
        signOutEt.setFocusableInTouchMode(false);
        edit.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
        Toast.makeText(this,"保存成功", Toast.LENGTH_SHORT).show();
    }

    public void showEditDialog(View view)
    {
        Fragment mClockinWindow = new Fragment();
//        Bundle bundle = new Bundle();
//        calendarView.getCurDay();
//        bundle.putString("startEt", );
//        bundle.putInt("endEt", calendarView.getCurMonth());
//        bundle.putInt("Data03", 33333);
//        mClockinWindow.setArguments(bundle);
        mClockinWindow.show(fm, "EditNameDialog");
    }






    private static class MyHandler extends Handler{
        // SoftReference<Activity> 也可以使用软应用 只有在内存不足的时候才会被回收
        private final WeakReference<Activity> mActivity;
        String TAG = "MyHandler";
        private MyHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null){
                //做操作
                switch ( msg.what){
                    case onCalendarSelectMsg:
                        Calendar calendar = (Calendar) msg.obj;
                        tempSelectedDate = String.format("%s-%s-%s",calendar.getYear(),calendar.getMonth(),calendar.getDay());
                        Log.i(TAG, tempSelectedDate);
                    break;
                    case onMonthChangedMsg:
                        String MonthInfo = (String)msg.obj;
                        title.setText(MonthInfo);
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    private void punchOff() {
        try {
            WorkTimeRecord record = new WorkTimeRecord(timeUtil.getCurrentMonth(),
                    timeUtil.getCurrentDayInMonth(), "22", timeUtil.getCurrentTimeStr());
            dbAdapter.update(record);
            Log.i(TAG, "punchOff:succ");
        } catch (Exception e) {
            Log.e(TAG, "punchOff: ",e);
        }
    }

    private void clockIn() {
        try {
            WorkTimeRecord record = new WorkTimeRecord(timeUtil.getCurrentMonth()
                    , timeUtil.getCurrentDayInMonth(), timeUtil.getCurrentTimeStr(), null);
            dbAdapter.add(record);
        } catch (Exception e) {
            Log.e(TAG, "clockIn: " , e);
        }
    }

}
