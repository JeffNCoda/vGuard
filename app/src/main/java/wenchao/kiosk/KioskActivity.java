package wenchao.kiosk;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class KioskActivity extends Activity {
    /*
        1. Set Device owner and lock task/pin screen
        2. Set as home intent
        3. Disable power off button/ give a way to turn off the device - not possible
        4. Disable volume botton if required
        5. stop screen to turn off, or lock
     */
    private boolean lockState;
    private final int REQ_BOOT = 132;
    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onAttachedToWindow() {
       // this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        try {
            Process ps = Runtime.getRuntime().exec("adb shell dpm set-device-owner wenchao.kiosk/.MyAdmin");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.lockState = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(this.checkSelfPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_DENIED){
                this.requestPermissions(new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, REQ_BOOT);
                return;
            }
        }

        initReceiver();
        /* Set the app into full screen mode */
        getWindow().getDecorView().setSystemUiVisibility(flags);

        /* Following code allow the app packages to lock task in true kiosk mode */
        setContentView(R.layout.activity_lock_activity);
        // get policy manager
        DevicePolicyManager myDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // get this app package name
        ComponentName mDPM = new ComponentName(this, MyAdmin.class);
        //startLockTask();
        myDevicePolicyManager.isDeviceOwnerApp("");
        if (myDevicePolicyManager.isDeviceOwnerApp(this.getPackageName())) {
            // get this app package name
           String[] packages = {this.getPackageName()};
            // mDPM is the admin package, and allow the specified packages to lock task
            myDevicePolicyManager.setLockTaskPackages(mDPM, packages);


        } else {
            Toast.makeText(getApplicationContext(),"Not owner of device", Toast.LENGTH_LONG).show();
       }



        setVolumMax();

        Button lock_btn = (Button)findViewById(R.id.lock_button);
        Button unlock_btn = (Button)findViewById(R.id.unlock_button);

        lock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!lockState){
                    startLockTask();
                    lockState = true;
                }
            }
        });

        unlock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lockState){
                    stopLockTask();
                    lockState = false;
                }

            }
        });
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void initReceiver() {
        onbootListener onbootListener = new onbootListener();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED);
        this.registerReceiver(onbootListener, filter);

        MyAdmin myAdmin = new MyAdmin();
        IntentFilter intentFilter = new IntentFilter(DeviceAdminReceiver.ACTION_DEVICE_ADMIN_ENABLED);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(keyCode == KeyEvent.KEYCODE_HOME){
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setVolumMax(){
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(
                AudioManager.STREAM_SYSTEM,
                am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),
                0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(wenchao.kiosk.R.menu.menu_lock_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == wenchao.kiosk.R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
