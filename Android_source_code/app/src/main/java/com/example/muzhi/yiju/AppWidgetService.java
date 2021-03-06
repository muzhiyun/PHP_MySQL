package com.example.muzhi.yiju;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppWidgetService extends Service{

    public static final String ACTION_MAKE_NUMBER = "app_widget_provider";
    private String response = null;
    private static AppWidgetService example = new AppWidgetService();

    OkHttpClient client = new OkHttpClient();
    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

/*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
*/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    MyReceiver receiver;
    @Override
    public void onCreate() {
        super.onCreate();
        //动态注册广播接收器
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        Log.d("TAG","动态注册广播接收器");
        filter.addAction(ACTION_MAKE_NUMBER);
        registerReceiver(receiver, filter);
    }
    @Override
    public void onDestroy() {
        //注销广播接收器
        unregisterReceiver(receiver);
        Log.d("TAG","注销广播接收器");
        super.onDestroy();
    }
//
//    public void getResp () {
//        try {
//            response = example.run("http://192.168.43.91/yiju/index.php?m=json");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.print(response);
//    }

    /**
     * 广播接收器
     */
    class MyReceiver extends BroadcastReceiver {


        // 接收到Widget发送的广播
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG","接收器已收到");
            //Toast.makeText(context, "接收到已刷新", Toast.LENGTH_SHORT).show();
            if (ACTION_MAKE_NUMBER.equals(intent.getAction())) {
                // 生成一个随机数字，以系统广播的形式将这个数字提交到
                //Toast.makeText(context, "小北你好!", Toast.LENGTH_SHORT).show();
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                ComponentName provider = new ComponentName(context,NewAppWidget.class);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
                //设置要显示的TextView，及显示的内容
                try {
                    Log.d("TAG","进来了");
                    String old = response;
                    AppWidgetService testappserver =new AppWidgetService();
                    MyReceiver recvier =testappserver.new MyReceiver();
                    //Toast.makeText(context, next_text, Toast.LENGTH_SHORT).show();
//                    getResp();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                response = example.run("http://192.168.1.155/yiju/index.php");
                                System.out.println("thread_run: " + response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
//                    while (true) {
//                        if (response != null) {
//                            break;
//                        }
//                    }
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("TAG","拿到回复 内容是  "+ response);
                    if (response==old) {
                        response="服务器未连接";
                    }

                    views.setTextViewText(R.id.appwidget_text, response);
                    // 发送一个系统广播
                    manager.updateAppWidget(provider, views);
                }catch (Exception e)
            {
                e.printStackTrace();
                views.setTextViewText(R.id.appwidget_text, "软件异常");
                // 发送一个系统广播
                manager.updateAppWidget(provider, views);
            }

            }
        }

    }

}

