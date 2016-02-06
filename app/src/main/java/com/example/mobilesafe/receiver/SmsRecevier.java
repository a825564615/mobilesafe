package com.example.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.example.mobilesafe.R;
import com.example.mobilesafe.Service.GPSService;

import java.util.Objects;

/**
 * Created by abc on 2016/2/5.
 */
public class SmsRecevier extends BroadcastReceiver {
    private SharedPreferences sp;
    private String TAG="Test";
    @Override
    public void onReceive(Context context, Intent intent) {
       sp=context.getSharedPreferences("config",context.MODE_PRIVATE);
      //写接收短信代码
      Object[] objs = (Object[]) intent.getExtras().get("pdus");//pdus短信的协议
      for(Object o :objs){
          String phone=sp.getString("phone",null);

          //获取具体的某一条短信
          SmsMessage sms= SmsMessage.createFromPdu((byte[])o);
          //获取发送者
          String sender = sms.getOriginatingAddress();
          //获取短信内容
          String body =sms.getMessageBody();
          if(sender.contains(phone)){

              if("#*location*#".equals(body)){
                  //得到手机的GPS
                  Log.i(TAG, "得到手机的GPS");
                  //启动服务
                  Intent i = new Intent(context,GPSService.class);
                  context.startService(i);
                  SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                  String lastlocation = sp.getString("lastlocation", null);
                  if(TextUtils.isEmpty(lastlocation)){
                      //位置没有得到
                      SmsManager.getDefault().sendTextMessage(sender, null, "geting loaction.....", null, null);
                  }else{
                      SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
                  }


                  //把这个广播终止掉
                  abortBroadcast();
              }else if("#*alarm*#".equals(body)){
                  //播放报警影音
                  Log.i(TAG, "播放报警影音");
                  MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                  player.setLooping(false);//
                  player.setVolume(1.0f, 1.0f);
                  player.start();

                  abortBroadcast();
              }
              else if("#*wipedata*#".equals(body)){
                  //远程清除数据
                  Log.i(TAG, "远程清除数据");
                  abortBroadcast();
              }
              else if("#*lockscreen*#".equals(body)){
                  //远程锁屏
                  Log.i(TAG, "远程锁屏");
                  abortBroadcast();
              }
          }
      }
    }
}