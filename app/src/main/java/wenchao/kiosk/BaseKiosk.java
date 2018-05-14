package wenchao.kiosk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class BaseKiosk extends Activity implements View.OnClickListener {
    private static final int uiFlags =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    private BroadcastReceiver screenOffReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);

        setContentView(R.layout.activity_base_kiosk);
        this.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        this.initReceiver();
        /**create click listener for the button so we can unlock the kiosk app.*/
        (this.findViewById(R.id.btn_hide)).setOnClickListener(this);
    }

    /**Register a receiver so we know when to kill this activity.*/
    private void initReceiver() {
        this.screenOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    BaseKiosk.this.finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        this.registerReceiver(screenOffReceiver, filter);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onAttachedToWindow() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.reenableKeyguard();
        super.onAttachedToWindow();
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        this.getWindow().addFlags(flags);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_HOME:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        /**Called when the user unlock the kiosk app*/
        this.finish();
    }

    @Override
    public void onBackPressed() { }

    /**When te Activity is Destroyed, we want to release the receiver cause we don't need it anymore*/
    @Override
    protected void onDestroy() {
        if(this.screenOffReceiver != null){
            this.unregisterReceiver(this.screenOffReceiver);
        }
        super.onDestroy();
    }
}
