package com.gionee.autocall;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class phoneListener {

    private Context mContext;
    private TelecomManager telecomManager;
    private phoneBroadcastReceiver receiver;
    private phoneStateListener mPhoneStateListener;

    public phoneListener(Context context) {
        mContext = context;
        receiver = new phoneBroadcastReceiver();
    }

    public void register(phoneStateListener listener) {
        mPhoneStateListener = listener;
        if (receiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PHONE_STATE");
            mContext.registerReceiver(receiver, filter);
        }
    }

    public void unregister() {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    private class phoneBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent_r) {

            TelephonyManager telecomManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            int TelephonyState=telecomManager.getCallState();
            switch (TelephonyState) {
                //来电
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i("KEVIN","RING.....");
                    String phony = intent_r.getStringExtra("incoming_number");
                        mPhoneStateListener.onStateRINGING();
//                    }
                    break;
                //接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    mPhoneStateListener.onStateOFFHOOK();
                    break;
                //挂断
                case TelephonyManager.CALL_STATE_IDLE:
                    mPhoneStateListener.onStateIDLE();
                    break;
                }
            }
        }


    public interface phoneStateListener {

        void onStateRINGING();

        void onStateOFFHOOK();

        void onStateIDLE();
    }
}

//public class MyReceiver extends BroadcastReceiver {
//    private int EXCUTE_COUNT;
//    private int PASS_COUNT;
//    private int FAIL_COUNT;
//    private double PASS_RATE;
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        //取来电号码（取到广播内容），   取广播值得键是系统定的，不能写别的
//        String phony=intent.getStringExtra("incoming_number");
//        Intent intent_acc=new Intent();
//        intent_acc.putExtra("INCOMING_NUMBER",phony);
////        Log.i("KEVIN",phony);
////        Intent intent1=new Intent();
//        if("android.intent.action.PHONE_STATE".equals(intent.getAction())){
//            //取到电话的服务   返回为，电话管理器
//            TelephonyManager telecomManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            //取到  来电的状态
//            int TelephonyState=telecomManager.getCallState();
//            switch (TelephonyState) {
//                //来电
//                case TelephonyManager.CALL_STATE_RINGING:
//
//                    Log.i("KEVIN","RING.....");
//                    break;
//                //接听
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    break;
//                //挂断
//                case TelephonyManager.CALL_STATE_IDLE:
//                    break;
//            }
//
//        }
//
//    }
//
//    }
