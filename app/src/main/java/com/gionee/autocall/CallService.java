package com.gionee.autocall;

import android.Manifest;
import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
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

public class CallService extends Service {
    private TelephonyManager tm;
    private Context context;
    private Intent intent_call;
    private String PHONE_NUMBER;
    private String HOLD_TIME;
    private String WAIT_TIME;
    private String TEST_LOOPS;
    private String CASE_ID;
    private BufferedWriter bw;
    private SimpleDateFormat sdf;

    private int EXCUTE_COUNT;
    private int PASS_COUNT;
    private int FAIL_COUNT;
    private float PASS_RATE;
    private boolean myFlag=true;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("KEVIN", "Service is started" + "-----");
        PHONE_NUMBER = intent.getStringExtra("PHONE_NUMBER");
        HOLD_TIME = intent.getStringExtra("HOLD_TIME");
        WAIT_TIME = intent.getStringExtra("WAIT_TIME");
        TEST_LOOPS = intent.getStringExtra("TEST_LOOPS");
        CASE_ID = intent.getStringExtra("CASE_ID");

        if(HOLD_TIME.equals("") | WAIT_TIME.equals("") | TEST_LOOPS.equals("") | CASE_ID.equals("") | PHONE_NUMBER.equals("") ){
            Toast.makeText(getApplicationContext(), "参数不能有空值", Toast.LENGTH_SHORT).show();
        }else{
            int _HOLD_TIME=Integer.parseInt(HOLD_TIME);//String 转换为 int
            int _WAIT_TIME=Integer.parseInt(WAIT_TIME);
            int _TEST_LOOPS=Integer.parseInt(TEST_LOOPS);
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            writeData(sdf.format(new Date(System.currentTimeMillis()))+","+CASE_ID+"，自动呼叫");
            writeData("测试次数,执行次数,成功次数,失败次数,成功率");
//        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        manager.listen(new MyPhoneListener(),PhoneStateListener.LISTEN_CALL_STATE);


            startTest(_TEST_LOOPS,_HOLD_TIME,_WAIT_TIME); //开始测试
            Toast.makeText(getApplicationContext(), "开始自动呼叫", Toast.LENGTH_SHORT).show();
        }


        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        myFlag=false;
        Toast.makeText(getApplicationContext(), "自动呼叫即将停止", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void startTest(final int _TEST_LOOPS, final int _HOLD_TIME, final int _WAIT_TIME) {
        new Thread() {
            public void run() {
                EXCUTE_COUNT=0;
                PASS_COUNT=0;
                FAIL_COUNT=0;
                PASS_RATE=0;
                SystemClock.sleep(2 * 1000);

                for (int i = 0; i < _TEST_LOOPS; i++) {
                    if(myFlag==false){
                        break;
                    }
//                    Log.i("KEVIN","THREADING");
                    intent_call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + PHONE_NUMBER));

                    if (ActivityCompat.checkSelfPermission(CallService.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
//            return;
                    }

                    startActivity(intent_call);
                    SystemClock.sleep(2 * 1000);
                    EXCUTE_COUNT=EXCUTE_COUNT+1;
                    SystemClock.sleep(_HOLD_TIME * 1000);
                    TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    int state=manager.getCallState();
                    if(state==2){
                        PASS_COUNT=PASS_COUNT+1;
                    }else {
                        FAIL_COUNT=FAIL_COUNT+1;
                    }
//                    System.out.println(state);
                    sendKeyCode(KeyEvent.KEYCODE_ENDCALL);//挂断电话
                    SystemClock.sleep(_WAIT_TIME * 1000);

                }
                DecimalFormat df = new DecimalFormat("0.00%");
                writeData(TEST_LOOPS+","+String.valueOf(EXCUTE_COUNT)+","+String.valueOf(PASS_COUNT)+","+String.valueOf(FAIL_COUNT)+","+String.valueOf(df.format(PASS_COUNT/EXCUTE_COUNT)));
            }
        }.start();}


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
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
    class MyPhoneListener extends PhoneStateListener {
        /**
         * 当电话状态改变了将会执行该方法
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.i("info","incomingNumber:"+incomingNumber);
            switch(state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i("info","CALL_STATE_IDLE");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i("info","CALL_STATE_OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i("info","CALL_STATE_RINGING");
                    break;
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
