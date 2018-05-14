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
        /** Service Constructor.*/
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /** Called when the Service has been constructed(called only once in service lifecycle). we can now start implementing our own code.*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initReceiver();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initReceiver() {
        /** IMPORTANT PART*/


        /** Since there are many system events, when create a filter for our receiver so the System can know what events to notify us about.*/
        /** We filter:
         * 1) BOOT_COMPLETED, QUICKBOOT_POWERON( when the phone restarts): So we can start the app since it's killed when the phone is off.
         * 2) SCREEN_ON (phone screen on): So we can start the BaseKiosk Activity on top of the lock screen.
         * 3) SCREEN_OFF (phone screen off): So we can kill the BaseKiosk when screen is off to save battery.
         * 4) USER_PRESENT (user is using the phone): to know when the user in interacting with the app.
         * */
        IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction("android.intent.action.QUICKBOOT_POWERON");
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);

        /** Create the receiver object the system will use to notify us of the events we filtered .*/
        onbootListener onbootListener = new onbootListener();

        /** Lastly we tell the system to register our receiver with the filters.*/
        this.registerReceiver(onbootListener, filter);
    }
}
