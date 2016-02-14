package com.example.mobilesafe;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafe.domain.AppInfo;
import com.example.mobilesafe.engine.AppInfoProvider;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 2016/2/14.
 */
public class AppManagerActivity extends Activity {
    private TextView tv_avail_rom;
    private TextView tv_avail_sd;
    private ListView lv_app_manager;
    private LinearLayout ll_loading;
    private List<AppInfo> infos;
    /**
     * 用户软件集合
     */
    private List<AppInfo> userAppinfos;
    /**
     * 系统软件集合
     */
    private List<AppInfo> systemAppinfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
        tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        long sdSize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        long romSize = getAvailMemory();//获取内存
        tv_avail_sd.setText("SD卡可用空间:" + Formatter.formatFileSize(this, sdSize)); //formatFileSize()将内存空间转换格式
        tv_avail_rom.setText("内存可用空间:" + Formatter.formatFileSize(this, romSize));
        //避免主进程阻塞 infos= AppInfoProvider.getAppInfos(this);
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            public void run() {
                infos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
                systemAppinfos = new ArrayList<AppInfo>();
                userAppinfos = new ArrayList<AppInfo>();
                for (AppInfo info : infos) {
                    if (info.isUserApp()) {
                        userAppinfos.add(info);
                    } else {
                        systemAppinfos.add(info);
                    }

                }
                //加载ListView数据适配器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lv_app_manager.setAdapter(new AppAdapter());
                        ll_loading.setVisibility(View.INVISIBLE);
                    }
                });
            }

            ;
        }.start();
    }

    private class AppAdapter extends BaseAdapter {

        //控制listView 的条目
        @Override
        public int getCount() {
            //return infos.size();
            return userAppinfos.size()+1+systemAppinfos.size()+1;//增加的两条标签
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position==0){
                //显示系统程序有多少个的小标签
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户程序"+userAppinfos.size()+"个");
                return tv;
            }else if(position==userAppinfos.size()+1){
                //显示用户程序有多少个的小标签
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统程序"+systemAppinfos.size()+"个");
                return tv;
            }

            View view;
            ViewHolder viewHolder;
            AppInfo  info;
            if(position<=userAppinfos.size()){
                 info = userAppinfos.get(position-1);
            }else {
                 info = systemAppinfos.get(position-2-userAppinfos.size());
            }

            //不仅检查是否为空并且要判断是否能被复用
            if (convertView != null&&convertView instanceof RelativeLayout) {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.list_item_appinfo, null);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
                viewHolder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
                view.setTag(viewHolder);
            }
            viewHolder.tv_name.setText(info.getName());
            viewHolder.icon.setImageDrawable(info.getIcon());
            if (info.isInRom()) {
                viewHolder.tv_location.setText("手机内存");
            } else {
                viewHolder.tv_location.setText("外部存储");
            }
            return view;
        }
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_location;
        ImageView icon;
    }

    /**
     * 获取某个目录的可用空间
     *
     * @param path 路径
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)  //要求最低版本
    private long getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        long size = statFs.getBlockSizeLong();//获取分区的大小
        long count = statFs.getAvailableBlocksLong();//获取可用分区的个数
        statFs.getBlockCountLong();//获取所有的分区
        return size * count;
    }

    /**
     * 获取可用内存
     *
     * @return
     */
    public long getAvailMemory() {
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        manager.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }
}