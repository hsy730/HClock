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
import com.example.helloworld.utils.StringUtil;
import com.example.helloworld.utils.TimeUtil;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;


import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements  Fragment.MyDialogFragment_Listener{

    private static final int MSG_CALENDAR_SELECT = 0x123;
    private static final int MSG_MONTH_CHANGED = 0x124;
    private MyHandler mMyHandler = new MyHandler(this);
    private static TextView title;
    private static String selectMonth,selectDay;

    private static WorkTimeRecord todayRecord;
    Button edit,save,signInBt,signOutBt;
    private static EditText signInEt,signOutEt;
    private TimeUtil timeUtil;
    FragmentManager fm = getSupportFragmentManager();
    private static DatabaseAdaper dbAdapter;
    CalendarView calendarView;
    private boolean validSignIn;
    private boolean validSignOut;
    private TextView overTime;
    private String preSignInTime, preSignOutTime;
//    private boolean isSignIn = true;
//    private String signInTime = null;
    String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        getSupportActionBar().hide();
        setContentView(R.layout.activity_calendar);
        //获取选择的日期
        initLayout();
    }

    public void click(View view) {
        System.out.println("我被点击了");
        switch (view.getId()){
            // 编辑按钮
            case R.id.edit:
                makeTimeEtEditableAndShowSaveBt();
                break;
            // 保存按钮
            case R.id.save:
                makeTimeEtUneditableAndShowEditBt();
                saveEditContent();
                setTotalOverTimeInTextView();
                Toast.makeText(this,"保存成功", Toast.LENGTH_SHORT).show();
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
                setTotalOverTimeInTextView();
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
        overTime = findViewById(R.id.over_time);
        dbAdapter = new DatabaseAdaper(MainActivity.this);
        timeUtil = new TimeUtil();


        Calendar calendar = calendarView.getSelectedCalendar();
        selectDay = String.valueOf(calendar.getDay());
        selectMonth = String.format("%s-%s",calendar.getYear(),calendar.getMonth());
        // 填写editText时间
        initTimeInEt();
        // 日历控件监听器
        addCalendarListener();
        // 签到签退文本框监听器
        addEditTextListener();
        setTotalOverTimeInTextView();
        showSignInOrSignOutButton();
    }

    private static void initTimeInEt() {
        String TAG = "MainActivity";
     /*   String month = selectDay.substring(0,7);
        String day = selectDay.substring(8);
        Log.i(TAG, "initTimeInEt: "+month+","+day);*/
        WorkTimeRecord record = dbAdapter.getRecordAtDayOfMonth(selectMonth,selectDay);
        if (record == null ) {
            signInEt.setText("");
            signOutEt.setText("");
        } else {
            signInEt.setText(record.getBeginTime());
            signOutEt.setText(record.getEndTime());
        }

    }

    // 初始化签到签退按钮，查询当天数据，如果无则显示签到
    private void showSignInOrSignOutButton() {
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
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String change = signInEt.getText().toString().trim();
                Log.i(TAG, "signInTime, afterTextChanged: "+change);
                if(!timeUtil.checkTimeFormat(change)) {
                    validSignIn = false;
//                    Toast.makeText(MainActivity.this,"时间格式非法，请输入合法时间", Toast.LENGTH_SHORT).show();
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
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String change = signOutEt.getText().toString().trim();
                Log.i(TAG, "signOutEt, afterTextChanged: "+change);
                if(!timeUtil.checkTimeFormat(change)) {
                    validSignOut = false;
                    save.setClickable(false);
//                    Toast.makeText(MainActivity.this,"时间格式非法，请输入合法时间", Toast.LENGTH_SHORT).show();
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
        // 选择日期改变
        MyOnCalendarSelectListener myOnCalendarSelectListener = new MyOnCalendarSelectListener(){
            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick){
                if(isClick) {
              /*      text.setText(calendar.getDay());
                    //消息要先传进Message中，再由Message传递给Handler处理
                    Message msg = Message.obtain();
                    //Message类有属性字段arg1、arg2、what...
                    msg.obj = calendar;
                    msg.what = MSG_CALENDAR_SELECT;
                    //sendMessage()用来传送Message类的值到mHandler
                    mMyHandler.sendMessage(msg);*/
//                    selectDay = String.format("%s-%s-%s",calendar.getYear(),calendar.getMonth(),calendar.getDay());
                    selectDay = String.valueOf(calendar.getDay());
                    Log.i(TAG, "onCalendarSelect: "+ calendar.getDay());
                    initTimeInEt();
                }
            }
        };
        // 显示月分改变
        MyOnMonthChangeListener myOnMonthChangeListener = new MyOnMonthChangeListener(){
            @Override
            public void onMonthChange(int year, int month){
               /* Message msg = Message.obtain();
                msg.obj = String.format("%s年%s月",year,month);
                msg.what = MSG_MONTH_CHANGED;
                mMyHandler.sendMessage(msg);*/
                title.setText(String.format("%s年%s月",year,month));
                selectMonth = String.format("%s-%s",year,month);
                setTotalOverTimeInTextView();
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

    private void makeTimeEtEditableAndShowSaveBt() {
        preSignInTime = signInEt.getText().toString().trim();
        preSignOutTime = signOutEt.getText().toString().trim();

        signInEt.setFocusable(true);
        signInEt.setFocusableInTouchMode(true);

        signOutEt.setFocusable(true);
        signOutEt.setFocusableInTouchMode(true);

        edit.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        Toast.makeText(this,"可以修改", Toast.LENGTH_SHORT).show();
    }

    private void makeTimeEtUneditableAndShowEditBt() {
        signInEt.setFocusable(false);
        signInEt.setFocusableInTouchMode(false);

        signOutEt.setFocusable(false);
        signOutEt.setFocusableInTouchMode(false);
        edit.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
    }

    private void saveEditContent() {
        WorkTimeRecord record = new WorkTimeRecord();
        String signIn = signInEt.getText().toString();
        String signOut = signOutEt.getText().toString();
        record.setMonth(selectMonth);
        record.setDay(selectDay);
        record.setBeginTime(timeUtil.formatClockTime(signIn));
        record.setEndTime(timeUtil.formatClockTime(signOut));
        Log.i(TAG, "saveEditContent: "+record.toString());
        Log.i(TAG, "saveEditContent: preSignInTime/"+preSignInTime+"preSignOutTime/"+preSignOutTime);
        // 之前全为空，执行insert，否则，执行update
        if (StringUtil.isEmpty(preSignInTime) && StringUtil.isEmpty(preSignOutTime)) {
            dbAdapter.add(record);
            Log.i(TAG, "saveEditContent: DB ADD");
        } else {
            dbAdapter.update(record);
            Log.i(TAG, "saveEditContent: DB UPDATE");
        }
    }

    public void showEditDialog(View view) {
        Fragment mClockinWindow = new Fragment();
//        Bundle bundle = new Bundle();
//        calendarView.getCurDay();
//        bundle.putString("startEt", );
//        bundle.putInt("endEt", calendarView.getCurMonth());
//        bundle.putInt("Data03", 33333);
//        mClockinWindow.setArguments(bundle);
        mClockinWindow.show(fm, "EditNameDialog");
    }

    public void setTotalOverTimeInTextView() {
        ArrayList<WorkTimeRecord> records = dbAdapter.getRecordsInMonth(selectMonth);
        double totalOverTime = timeUtil.sumOverTimeInAMonth(records);
        overTime.setText( (totalOverTime > 0) ? String.format("%.2f", totalOverTime) : "");
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
/*                    case MSG_CALENDAR_SELECT:
                        Calendar calendar = (Calendar) msg.obj;
                        selectDay = String.format("%s-%s-%s",calendar.getYear(),calendar.getMonth(),calendar.getDay());
                        Log.i(TAG, selectDay);
                        initTimeInEt();//
                    break;*/
                   /* case MSG_MONTH_CHANGED:
                        String MonthInfo = (String)msg.obj;
                        title.setText(MonthInfo);*/
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
//            if (timeUtil.getCurYearMonthDay().equals(selectDay)) {
            if (timeUtil.isToday(selectMonth, selectDay)) {
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
//            if (timeUtil.getCurYearMonthDay().equals(selectDay)) {
            if (timeUtil.isToday(selectMonth, selectDay)) {
                Log.i(TAG, "clockIn: 相等，是今天");
                signInEt.setText(todayRecord.getBeginTime());
                signOutEt.setText(todayRecord.getEndTime());
            }
        } catch (Exception e) {
            Log.e(TAG, "punchOff: ",e);
        }
    }
}
