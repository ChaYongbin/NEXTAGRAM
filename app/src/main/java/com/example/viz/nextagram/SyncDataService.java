
package com.example.viz.nextagram;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class SyncDataService extends Service {

    // 로그를 위한 태그를 각 클래스 이름으로 설정하기 위해 아래와 같이 TAG를 설정
    private static final String TAG = SyncDataService.class.getSimpleName();
    private TimerTask mTask;
    private Timer mTimer;
    // Server에서 Article들을 받아오기 위한 Proxy
    private Proxy proxy;
    // 받아온 Article들을 DB에 저장하기 위한 Dao
    private ProviderDao dao;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        proxy = new Proxy(getApplicationContext());
        dao = new ProviderDao(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        // 새로운 Timer Task 를 생성한다.
        mTask = new TimerTask() {
            @Override
            public void run() {
                String jsonData = proxy.getJSON();
                if (jsonData != null) {
                    dao.insertJsonData(jsonData);

                }
            }
        };

        // 새로운 Timer를 생성한다.
        mTimer = new Timer();
        // Timer에게 위에 작성한 TimerTask, 시작하기 원하는 시점, 주기를 인자로 설정한다.
        mTimer.schedule(mTask, 1000*5, 1000*5);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestory");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

