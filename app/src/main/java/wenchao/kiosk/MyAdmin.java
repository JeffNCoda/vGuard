package wenchao.kiosk;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by wxj on 29/07/2015.
 */
public class MyAdmin extends DeviceAdminReceiver{
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Log.i("GGAL", "Admin Enabled");
    }
}
