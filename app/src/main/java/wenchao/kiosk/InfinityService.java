package wenchao.kiosk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class InfinityService extends Service {
    public static final String ACTION_ON_DESTROY = "com.infinity.onDestroy";
    private final Thread looper;
    private final Context context;

    public InfinityService(Context appContext){
        this.context = appContext;
        this.looper = new Thread(new Runnable() {
            final int LOCK_TIME = 1000;
            int counter = 0;
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(LOCK_TIME);
                        Log.i("BOOT-R", "ticks=" + counter++);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Log.i("BOOT-R", "C");
    }

    public InfinityService() {
        Log.i("BOOT-R", "V");

        this.context = null;
        this.looper = new Thread(new Runnable() {
            final int LOCK_TIME = 1000;
            int counter = 0;
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(LOCK_TIME);
                        Log.i("BOOT-R", "ticks=" + counter++);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.looper.start();
        Log.i("BOOT-R", "S");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("BOOT-R", "D");

        super.onDestroy();
        Intent intent = new Intent(ACTION_ON_DESTROY);
        this.sendBroadcast(intent);
    }
}

