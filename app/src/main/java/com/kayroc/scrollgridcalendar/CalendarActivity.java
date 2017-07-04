package com.kayroc.scrollgridcalendar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kayroc.scrollcalendarlib.custom.ScrollableLayout;
import com.kayroc.scrollcalendarlib.utils.CalendarUtils;
import com.kayroc.scrollcalendarlib.utils.SpecialCalendar;
import com.nineoldandroids.view.ViewHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 日历显示activity
 */
public class CalendarActivity extends FragmentActivity implements OnGestureListener ,View.OnClickListener{

    private GestureDetector gestureDetector = null;
    private CalendarAdapter calV = null;
    private GridView gridView = null;
    private TextView topText = null;
    private ScrollableLayout mScrollLayout;
    private RelativeLayout mTopLayout;
    private LinearLayout mBtnLeft,mBtnRight;
    private ListView mListView;
    private TestAdapter mAdapter;
    private ArrayList<String> mData = new ArrayList<String>();

    private static int jumpMonth = 0;      //每次滑动，增加或减去一个月,默认为0（即显示当前月）
    private static int jumpYear = 0;       //滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private String currentDate = "";

    private float location;             // 最终决定的收缩比例值
    private float currentLoction = 1f;  // 记录当天的收缩比例值
    private float selectLoction = 1f;   // 记录选择那一天的收缩比例值

    public CalendarActivity() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date);  //当期日期
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        gridView = (GridView) findViewById(R.id.gridview);
        mListView = (ListView) findViewById(R.id.main_lv);
        for (int i = 0;i<30;i++){
            mData.add("");
        }
        mAdapter = new TestAdapter(mData,this);
        mListView.setAdapter(mAdapter);

        mBtnLeft = (LinearLayout) findViewById(R.id.btn_prev_month);
        mBtnRight = (LinearLayout) findViewById(R.id.btn_next_month);
        mBtnLeft.setOnClickListener(this);
        mBtnRight.setOnClickListener(this);


        // TODO 计算当天的位置和收缩比例
        SpecialCalendar calendar = new SpecialCalendar();
        boolean isLeapYear = calendar.isLeapYear(year_c);
        int days = calendar.getDaysOfMonth(isLeapYear,month_c);
        int dayOfWeek = calendar.getWeekdayOfMonth(year_c,month_c);
        int todayPosition = day_c;
        if (dayOfWeek != 7){
            days = days + dayOfWeek;
            todayPosition += dayOfWeek -1;
        }else{
            todayPosition -= 1;
        }
        /**
         * 如果 少于或者等于35天显示五行 多余35天显示六行
         * 五行: 收缩比例是：0.25，0.5，0.75，1
         * 六行: 收缩比例是：0.2，0.4，0.6，0.8，1
         */
        if (days <= 35){
            CalendarUtils.scale = 0.25f;
            currentLoction = (4 - todayPosition/7) * CalendarUtils.scale;
        }else{
            CalendarUtils.scale = 0.2f;
            currentLoction = (5 - todayPosition/7) * CalendarUtils.scale;
        }
        location = currentLoction;
        mTopLayout = (RelativeLayout) findViewById(R.id.rl_head);
        mScrollLayout = (ScrollableLayout)findViewById(R.id.scrollableLayout);
        mScrollLayout.setOnScrollListener(new ScrollableLayout.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {

                ViewHelper.setTranslationY(mTopLayout, currentY * location);
            }
        });

        mScrollLayout.getHelper().setCurrentContainer(mListView);

        gestureDetector = new GestureDetector(this);
        calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
        addGridView();
        gridView.setAdapter(calV);
        topText = (TextView) findViewById(R.id.tv_month);
        addTextToTopTextView(topText);

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        if (e1.getX() - e2.getX() > 120) {
            //像左滑动
            jumpMonth++;     //下一个月
            upDateView();
            return true;
        } else if (e1.getX() - e2.getX() < -120) {
            //向右滑动
            jumpMonth--;     //上一个月
            upDateView();
            return true;
        }
        return false;
    }

    private void upDateView(){
        addGridView();   //添加一个gridView
        calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
        gridView.setAdapter(calV);
        addTextToTopTextView(topText);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return this.gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    //添加头部的年份 闰哪月等信息
    public void addTextToTopTextView(TextView view) {
        StringBuffer textDate = new StringBuffer();
        textDate.append(calV.getShowYear()).append("年").append(
                calV.getShowMonth()).append("月").append("\t");
        view.setText(textDate);
        view.setTextColor(Color.WHITE);
        view.setTypeface(Typeface.DEFAULT_BOLD);
    }

    //添加gridview
    private void addGridView() {

        // TODO 如果滑动到其他月默认定位到第一行，划回本月定位到当天那行
        if (jumpMonth == 0){
//            location = currentLoction;
        }else{
//            location = 1f;
        }
        // TODO 选择的月份 定位到选择的那天
        if (((jumpMonth + month_c)+"").equals(CalendarUtils.zMonth)){
//            location = selectLoction;
        }
        Log.d("location", "location == " + location + "   currentLoction == " + currentLoction);

        gridView.setOnTouchListener(new OnTouchListener() {
            //将gridview中的触摸事件回传给gestureDetector
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return CalendarActivity.this.gestureDetector.onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            //gridView中的每一个item的点击事件
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                //点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
                int startPosition = calV.getStartPositon();
                int endPosition = calV.getEndPosition();
                String scheduleDay;
                String scheduleYear;
                String scheduleMonth;
                location = (float) ((5 - position/7) * 0.2);
                if (startPosition <= position + 7 && position <= endPosition - 7) {
                    scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //这一天的阳历
                    //String scheduleLunarDay = calV.getDateByClickItem(position).split("\\.")[1];  //这一天的阴历
                    scheduleYear = calV.getShowYear();
                    scheduleMonth = calV.getShowMonth();
                    CalendarUtils.zYear = scheduleYear;
                    CalendarUtils.zMonth = scheduleMonth;
                    CalendarUtils.zDay = scheduleDay;

                    if (CalendarUtils.scale == 0.2f){
                        location = (5 - position/7) * CalendarUtils.scale;
                    }else{
                        location = (4 - position/7) * CalendarUtils.scale;
                    }
                    selectLoction = location;
                    calV.notifyDataSetChanged();
                    Toast.makeText(CalendarActivity.this, scheduleYear + "-" + scheduleMonth + "-" + scheduleDay, Toast.LENGTH_SHORT).show();

                }
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_prev_month:
                jumpMonth --;     //上一个月
                upDateView();
                break;
            case R.id.btn_next_month:
                jumpMonth ++;     //下一个月
                upDateView();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO 页面被销毁时，清空选择的日期数据
        CalendarUtils.zYear = "";
        CalendarUtils.zMonth ="";
        CalendarUtils.zDay = "";
    }


}