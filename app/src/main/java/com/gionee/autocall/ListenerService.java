package com.gionee.autocall;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ListenerService extends Service {
    private Intent intent_l;
    public String incomingNumber="";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        manager.listen(new MyPhoneListener(),PhoneStateListener.LISTEN_CALL_STATE);
        intent.putExtra("incomingNumber",incomingNumber);
        return super.onStartCommand(intent, flags, startId);
    }

    class MyPhoneListener extends PhoneStateListener {
        /**
         * 当电话状态改变了将会执行该方法
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            incomingNumber=incomingNumber;
            Log.i("info","incomingNumber:"+incomingNumber);

//            switch(state) {
//                case TelephonyManager.CALL_STATE_IDLE:
//                    Log.i("info","CALL_STATE_IDLE");
//                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    Log.i("info","CALL_STATE_OFFHOOK");
//                    break;
//                case TelephonyManager.CALL_STATE_RINGING:
//                    Log.i("info","CALL_STATE_RINGING");
//                    break;
//            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
