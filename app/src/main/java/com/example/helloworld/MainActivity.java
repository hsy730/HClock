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
//    private MyOnCalendarSelectListener myOnCalendarSelectListener;
//    private MyOnMonthChangeListener myOnMonthChangeListener;
    private static final int MSG_CALENDAR_SELECT = 0x123;
    private static final int MSG_MONTH_CHANGED = 0x124;
    private MyHandler mMyHandler = new MyHandler(this);
    private static TextView title;
    private static String selectDay;

    private static WorkTimeRecord todayRecord;
    Button edit,save,signInBt,signOutBt;
    private EditText signInEt,signOutEt;
    private TimeUtil timeUtil;
    FragmentManager fm = getSupportFragmentManager();
    DatabaseAdaper dbAdapter;
    CalendarView calendarView;
    private boolean validSignIn;
    private boolean validSignOut;
//    private boolean isSignIn = true;
//    private String signInTime = null;
    String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initLayout();
        int n =  calendarView.getCurDay();
        //获取选择的日期
        Calendar calendar = calendarView.getSelectedCalendar();
        selectDay = String.format("%s-%s-%s",calendar.getYear(),calendar.getMonth(),calendar.getDay());
        System.out.println("选择日期"+calendar.getDay());
        // 日历控件监听器
        addCalendarListener();
        // 签到签退文本框监听器
        addEditTextListener();

        initSignInAndSignOutButton();
        
        calendar.getDay();
        Log.i(TAG, n+"");
        Log.i(TAG, "选择日期"+calendar.getDay()+"");
    }

    public void click(View view) {
        System.out.println("我被点击了");
        switch (view.getId()){
            // 编辑按钮
            case R.id.edit:
                afterClickEditButton();
                break;
            // 保存按钮
            case R.id.save:
                afterClickSaveButton();
                break;
            // 签到按钮
            case R.id.clock_in:
                clockIn();
                signInBt.setVisibility(View.GONE);
                signOutBt.setVisibility(View.VISIBLE);
                break;
            // 签退按钮
            case R.id.punch_off:
                punchOff();
                break;
            case R.id.search_sql:
                searchAll();
                break;
            default:
                break;
        }
    }

    private void initLayout() {
        calendarView = findViewById(R.id.calendarView);
        title = findViewById(R.id.title);
        edit = findViewById(R.id.edit);
        save = findViewById(R.id.save);
        signInEt = findViewById(R.id.sign_in_time);
        signOutEt = findViewById(R.id.sign_out_time);
        title.setText(String.format("%s年%s月",calendarView.getCurYear(),calendarView.getCurMonth()));
        //编辑框初始化不可编辑
        signInEt.setFocusableInTouchMode(false);
        signOutEt.setFocusableInTouchMode(false);

        signInBt = findViewById(R.id.clock_in);
        signOutBt = findViewById(R.id.punch_off);
        dbAdapter = new DatabaseAdaper(MainActivity.this);
        timeUtil = new TimeUtil();
    }

    // 初始化签到签退按钮，查询当天数据，如果无则显示签到
    private void initSignInAndSignOutButton() {
        todayRecord = dbAdapter.getRecordAtDayOfMonth(timeUtil.getCurYearMonth(),timeUtil.getCurrentDayInMonth());
        if(todayRecord == null) {
            signInBt.setVisibility(View.VISIBLE);
            signOutBt.setVisibility(View.GONE);
            Log.i(TAG, "initSignInAndSignOutButton: 无数据，显示签到按钮");
        } else {
            // 保存签到时间，签退时更新数据库
//            todaySignInTime = todayRecord.getBeginTime();
            signInBt.setVisibility(View.GONE);
            signOutBt.setVisibility(View.VISIBLE);
            Log.i(TAG, "initSignInAndSignOutButton: "+todayRecord.toString());
        }
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

        MyOnCalendarSelectListener myOnCalendarSelectListener = new MyOnCalendarSelectListener(){
            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick){
                if(isClick) {
//                    text.setText(calendar.getDay());
                    //消息要先传进Message中，再由Message传递给Handler处理
                    Message msg = Message.obtain();
                    //Message类有属性字段arg1、arg2、what...
                    msg.obj = calendar;

                    msg.what = MSG_CALENDAR_SELECT;
                    //sendMessage()用来传送Message类的值到mHandler
                    mMyHandler.sendMessage(msg);
                    Log.i(TAG, "onCalendarSelect: "+ calendar.getDay());
                }
            }
        };

        MyOnMonthChangeListener myOnMonthChangeListener = new MyOnMonthChangeListener(){
            @Override
            public void onMonthChange(int year, int month){
                Message msg = Message.obtain();
                msg.obj = String.format("%s年%s月",year,month);
                msg.what = MSG_MONTH_CHANGED;
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
                    case MSG_CALENDAR_SELECT:
                        Calendar calendar = (Calendar) msg.obj;
                        selectDay = String.format("%s-%s-%s",calendar.getYear(),calendar.getMonth(),calendar.getDay());
                        Log.i(TAG, selectDay);
                    break;
                    case MSG_MONTH_CHANGED:
                        String MonthInfo = (String)msg.obj;
                        title.setText(MonthInfo);
                    default:
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    private void clockIn() {
        try {
            todayRecord = new WorkTimeRecord(timeUtil.getCurYearMonth()
                    , timeUtil.getCurrentDayInMonth(), timeUtil.getCurrentTimeStr(), null);
            Log.i(TAG, "clockIn: todayRecord," + todayRecord.toString());
            dbAdapter.add(todayRecord);
            Log.i(TAG, "clockIn: success");
            // 如果为当天，则顺便设置显示
            Log.i(TAG, "selectDay:"+selectDay+"今天日期："+timeUtil.getCurYearMonthDay());
            if (timeUtil.getCurYearMonthDay().equals(selectDay)) {
                Log.i(TAG, "clockIn: 相等，是今天");
                signInEt.setText(todayRecord.getBeginTime());
            }
        } catch (Exception e) {
            Log.e(TAG, "clockIn: " , e);
        }
    }

    private void punchOff() {
        try {
            todayRecord.setEndTime(timeUtil.getCurrentTimeStr());
            Log.i(TAG, "punchOff: todayRecord，"+ todayRecord.toString());
            dbAdapter.update(todayRecord);
            Log.i(TAG, "punchOff:success");
            // 如果为当天，则顺便设置显示
            Log.i(TAG, "selectDay:"+selectDay+"今天日期："+timeUtil.getCurYearMonthDay());
            if (timeUtil.getCurYearMonthDay().equals(selectDay)) {
                Log.i(TAG, "clockIn: 相等，是今天");
                signInEt.setText(todayRecord.getBeginTime());
                signOutEt.setText(todayRecord.getEndTime());
            }
        } catch (Exception e) {
            Log.e(TAG, "punchOff: ",e);
        }
    }

}
