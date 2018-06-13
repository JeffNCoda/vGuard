package wenchao.kiosk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;


public class BaseKiosk extends LockableActivity implements View.OnClickListener {

    private BroadcastReceiver screenOffReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_kiosk);

        // LOCK THE HOME BUTTON AND THE POWER BUTTON
        this.Lock();

        this.initReceiver();

        // SET THE CLICK LISTENER FOR THE 'UNLOCK' BUTTON
        (this.findViewById(R.id.btn_hide)).setOnClickListener(this);
    }

    // CREATES A BROADCAST RECEIVER AND REGISTERS IT FOR WHEN THE SCREEN IS OFF SO WE CAN KILL THIS ACTIVITY TO SAVE BATTERY LIFE.
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


    // CALLED WHEN ONE OF THE VIEWS REGISTERED WITH THIS ONCLICK METHOD IS CLICKED.
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_hide:
                this.Unlock();
                this.finish();
        }
    }

    //WHEN THE ACTIVITY IS DESTROYED, WE RELEASE THE RECEIVER BECAUSE WE DON'T NEED IT ANYMORE.
    @Override
    protected void onDestroy() {
        if(this.screenOffReceiver != null){
            this.unregisterReceiver(this.screenOffReceiver);
        }
        this.Unlock();
        super.onDestroy();
    }
}

