package wenchao.kiosk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class onbootListener extends BroadcastReceiver {
    private static final String TAG = "BOOT-R";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, intent.getAction());
        switch (intent.getAction()){
            case Intent.ACTION_SCREEN_ON:
                showKiosk(context);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                this.onBoot(context);
                break;
        }
    }

    private void onBoot(Context context){
        this.showKiosk(context);
        Intent intent = new Intent(context, DispatcherService.class);
        context.startService(intent);
    }

    private void showKiosk(Context context){
        Intent i = new Intent(context, BaseKiosk.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
