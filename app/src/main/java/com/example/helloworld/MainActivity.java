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

import com.example.helloworld.sqlite.DatabaseAdaper;
import com.example.helloworld.sqlite.WorkTimeRecord;
import com.example.helloworld.utils.TimeUtil;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;


import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements  ClockinFragment.MyDialogFragment_Listener{
    private MyOnCalendarSelectListener myOnCalendarSelectListener;

    private CalendarView.OnCalendarSelectListener mOnCalendarSelectListener;
    private MyOnMonthChangeListener myOnMonthChangeListener;
    private static final int onCalendarSelectMsg = 0x123;
    private static final int onMonthChangedMsg = 0x124;
    private MyHandler mMyHandler = new MyHandler(this);
    private static TextView title;
    private static String tempSelectedDate;
    Button edit,save;
    private EditText startWorkTime,leaveWorkTime;
    private TimeUtil timeUtil;
    FragmentManager fm = getSupportFragmentManager();
    DatabaseAdaper dbAdapter;
    CalendarView calendarView;
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
        startWorkTime = findViewById(R.id.start_work_time);
        leaveWorkTime = findViewById(R.id.leave_work_time);
        title.setText(String.format("%s年%s月",calendarView.getCurYear(),calendarView.getCurMonth()));
        //不可编辑
        startWorkTime.setFocusableInTouchMode(false);
        leaveWorkTime.setFocusableInTouchMode(false);
        dbAdapter = new DatabaseAdaper(MainActivity.this);
        int n =  calendarView.getCurDay();
        timeUtil = new TimeUtil();
        //获取选择的日期
        Calendar calendar = calendarView.getSelectedCalendar();
        System.out.println("选择日期"+calendar.getDay());

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
        calendar.getDay();
        Log.i(TAG, n+"");
        Log.i(TAG, "选择日期"+calendar.getDay()+"");

        startWorkTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = startWorkTime.getText().toString().trim();
                if(!timeUtil.checkTimeFormat(text)) {
                    Toast.makeText(MainActivity.this,"时间格式非法，请输入合法时间", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 引用接口中定义的方法
    @Override
    public void getDataFromDialogFragment(String select) {
        Log.d(TAG, "DialogFragment回传的数据为：" + select);
        if ( ClockinFragment.CLOCK_IN.equals(select)) {
            clockIn();
            Log.i(TAG, "getDataFromDialogFragment: 上班");
        }else if (ClockinFragment.PUNCH_OFF.equals(select)) {
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
                showEditDialog(view);
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
        startWorkTime.setFocusable(true);
        leaveWorkTime.setFocusable(true);
        startWorkTime.setFocusableInTouchMode(true);
        leaveWorkTime.setFocusableInTouchMode(true);
        edit.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        Toast.makeText(this,"可以修改", Toast.LENGTH_SHORT).show();
    }

    private void afterClickSaveButton() {
        startWorkTime.setFocusable(false);
        leaveWorkTime.setFocusable(false);
        startWorkTime.setFocusableInTouchMode(false);
        leaveWorkTime.setFocusableInTouchMode(false);
        edit.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
        Toast.makeText(this,"保存成功", Toast.LENGTH_SHORT).show();
    }

    public void showEditDialog(View view)
    {
        ClockinFragment mClockinWindow = new ClockinFragment();
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
