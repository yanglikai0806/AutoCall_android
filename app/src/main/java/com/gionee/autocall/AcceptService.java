package com.gionee.autocall;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AcceptService extends Service {

    private phoneListener mPhoneListener;
    private String PHONE_NUMBER;
    private String TEST_LOOPS;
    private String CASE_ID;
    private BufferedWriter bw;
    private SimpleDateFormat sdf;

    private int EXCUTE_COUNT;
    private int PASS_COUNT;
    private int FAIL_COUNT;
    public double PASS_RATE;
//    TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    public int onStartCommand(Intent intent_acc, int flags, int startId) {
        Log.i("KEVIN", "AcceptService is started" + "-----");
        PHONE_NUMBER = intent_acc.getStringExtra("PHONE_NUMBER");
        TEST_LOOPS = intent_acc.getStringExtra("TEST_LOOPS");
        CASE_ID = intent_acc.getStringExtra("CASE_ID");
        Log.i("KEVIN", PHONE_NUMBER);
        int _TEST_LOOPS=Integer.parseInt(TEST_LOOPS);
        

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        writeData(sdf.format(new Date(System.currentTimeMillis()))+","+CASE_ID+"，自动接听");
        writeData("来电次数,接听次数,失败次数,成功率");

        mPhoneListener.register(new phoneListener.phoneStateListener() {
            TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            @Override
            public void onStateRINGING() {
                SystemClock.sleep(2 * 1000);
                EXCUTE_COUNT=EXCUTE_COUNT+1;
                sendKeyCode(KeyEvent.KEYCODE_HEADSETHOOK);
                SystemClock.sleep(1 * 1000);
                int state = manager.getCallState();
                if (state == 2) {
                    PASS_COUNT = PASS_COUNT + 1;
//                    Log.d("KEVIN", "onStateRINGING: WORKING");
                    System.out.println(PASS_COUNT);
                }
            }

            @Override
            public void onStateOFFHOOK() {

            }

            @Override
            public void onStateIDLE() {

            }
        });


        return super.onStartCommand(intent_acc, flags, startId);
    }

    public void onCreate() {
        EXCUTE_COUNT=0;
        PASS_COUNT=0;
        FAIL_COUNT=0;
        super.onCreate();
        Toast.makeText(getApplicationContext(), "开始自动接听", Toast.LENGTH_SHORT).show();
        mPhoneListener = new phoneListener(this);
//        mPhoneListener.register(new phoneListener.phoneStateListener() {
//            TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//            @Override
//            public void onStateRINGING() {
//                SystemClock.sleep(2 * 1000);
//                EXCUTE_COUNT=EXCUTE_COUNT+1;
//                sendKeyCode(KeyEvent.KEYCODE_HEADSETHOOK);
//        SystemClock.sleep(1 * 1000);
//        int state = manager.getCallState();
//        if (state == 2) {
//            PASS_COUNT = PASS_COUNT + 1;
////                    Log.d("KEVIN", "onStateRINGING: WORKING");
//            System.out.println(PASS_COUNT);
//        }
//    }
//
//    @Override
//    public void onStateOFFHOOK() {
//
//    }
//
//    @Override
//    public void onStateIDLE() {
//
//    }
//});
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "自动接听已停止", Toast.LENGTH_SHORT).show();
        if (mPhoneListener != null) {
            mPhoneListener.unregister();
        }
        DecimalFormat df = new DecimalFormat("0.00%");
        if(EXCUTE_COUNT>0){
            writeData(TEST_LOOPS+","+String.valueOf(EXCUTE_COUNT)+","+String.valueOf(PASS_COUNT)+","+String.valueOf(df.format(PASS_COUNT/EXCUTE_COUNT)));
        }
        super.onDestroy();
    }

    private void sendKeyCode(final int keyCode){
        new Thread () {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    Log.d("KEVIN","Exception when sendPointerSync");
                }
            }
        }.start();}

    private void writeData(String str){
        init();
        try {
//            bw.newLine();
//            bw.write("NOTE");
//            bw.newLine();
            bw.write(str);
            bw.newLine();
//            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        try{
            FileOutputStream fos = new FileOutputStream(newFile(),true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            bw = new BufferedWriter(osw);
        }catch(IOException e){
            Log.d("KEVIN","BufferedWriter Initialization error");
        }
        Log.d("KEVIN","Initialization Successful");
    }

    private File newFile() {
//        try {
        File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + File.separator  + "AutoCall");
        fileDir.mkdir();
        String basePath = Environment.getExternalStorageDirectory() + File.separator + "AutoCall" + File.separator + "record.csv";
        return new File(basePath);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
