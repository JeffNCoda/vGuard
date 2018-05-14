package wenchao.kiosk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InfintyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(InfinityService.ACTION_ON_DESTROY)){
            context.startService(new Intent(context, InfinityService.class));
        }
    }
}
