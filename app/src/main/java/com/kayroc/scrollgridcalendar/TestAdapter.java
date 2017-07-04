package com.kayroc.scrollgridcalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * **********************************
 * User: kayroc
 * Date: 2016年 03月 14日
 * Time: 下午6:33
 *
 * @QQ : 649751118
 * **********************************
 */
public class TestAdapter extends BaseAdapter {

    private ArrayList<String> mList;
    private Context mContext;

    public TestAdapter(ArrayList<String> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout,null);
        TextView  tv = (TextView) convertView.findViewById(R.id.tvtext);
        tv.setText("这是第 " + position +" 条测试数据");
        return convertView;
    }
}
