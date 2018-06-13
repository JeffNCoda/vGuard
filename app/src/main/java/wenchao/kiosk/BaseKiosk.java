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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.app.AlertDialog;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

public class BaseKiosk extends Activity implements View.OnClickListener {
    private static final int uiFlags =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    private BroadcastReceiver screenOffReceiver;

    HomeKeyLocker mHomeKeyLocker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        this.getWindow().setType(
//                WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG
//                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        super.onAttachedToWindow();
    super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_FULLSCREEN);
//   //     super.onCreate(savedInstanceState);
this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        setContentView(R.layout.activity_base_kiosk);
//        this.getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);

        mHomeKeyLocker = new HomeKeyLocker();
        setContentView(R.layout.activity_base_kiosk);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        // Calling Function in Class HomeKeyLocker to Block Home Button on this Activity.
        mHomeKeyLocker.lock(this);

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
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
//        lock.reenableKeyguard();
//        super.onAttachedToWindow();
//        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//        this.getWindow().addFlags(flags);
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_HOME:
                Log.i("Sgj", "HOME");
                return  true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    case KeyEvent.KEYCODE_VOLUME_MUTE:
                        Toast.makeText(this, "Volume is Blocked", Toast.LENGTH_SHORT).show();
                        return true;


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

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "P", Toast.LENGTH_SHORT).show();
    }

    /**When te Activity is Destroyed, we want to release the receiver cause we don't need it anymore*/
    @Override
    protected void onDestroy() {
        if(this.screenOffReceiver != null){
            this.unregisterReceiver(this.screenOffReceiver);
        }
        super.onDestroy();
    }
}


class HomeKeyLocker {
    private OverlayDialog mOverlayDialog;

    public void lock(Activity activity) {
        if (mOverlayDialog == null) {
            mOverlayDialog = new OverlayDialog(activity);
            mOverlayDialog.show();
        }
    }

    public void unlock() {
        if (mOverlayDialog != null) {
            mOverlayDialog.dismiss();
            mOverlayDialog = null;
        }
    }
    static class OverlayDialog extends AlertDialog {

        public OverlayDialog(Activity activity) {
            super(activity, R.style.AppTheme);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.type = TYPE_SYSTEM_ERROR;
            params.dimAmount = 0.0F; // transparent
            params.width = 0;
            params.height = 0;
            params.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(params);
            getWindow().setFlags(FLAG_SHOW_WHEN_LOCKED | FLAG_NOT_TOUCH_MODAL, 0xffffff);
            setOwnerActivity(activity);
            setCancelable(false);
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            if(!hasFocus) {
                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
               this.getContext().sendBroadcast(closeDialog);
            }
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            switch(keyCode){
                case KeyEvent.KEYCODE_VOLUME_UP:
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        case KeyEvent.KEYCODE_VOLUME_MUTE:
                    return true;
                    case KeyEvent.KEYCODE_POWER:
                        Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        this.getContext().sendBroadcast(closeDialog);
                        return true;

            }
            return super.onKeyDown(keyCode, event);
        }

        public final boolean dispatchTouchEvent(MotionEvent motionevent) {
            Toast.makeText(this.getContext(), motionevent.getAction() + "s", Toast.LENGTH_SHORT).show();
            return true;
        }

        protected final void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            FrameLayout framelayout = new FrameLayout(getContext());
            framelayout.setBackgroundColor(0);
            setContentView(framelayout);
        }
    }
}