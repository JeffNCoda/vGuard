package wenchao.kiosk;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class DispatcherService extends Service {
    private static final int REQ_NEXT_INSTANCE = 0;
    private AlarmManager alarmManager;
    private PendingIntent nextInstance;

    public DispatcherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initReceiver();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("BOOG-R", "Des");
        if(this.alarmManager != null){
            this.alarmManager.cancel(this.nextInstance);
        }
        super.onDestroy();
    }

    private void initReceiver() {
        onbootListener onbootListener = new onbootListener();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.setPriority(900);
        this.registerReceiver(onbootListener, filter);
    }
}
