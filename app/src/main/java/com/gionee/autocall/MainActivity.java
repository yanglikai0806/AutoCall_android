package com.gionee.autocall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private EditText PhoneNumber;
    private EditText HoldTime;
    private EditText WaitTime;
    private EditText TestLoops;
    private EditText CaseId;
    private Button StartButton;
    private Button StopButton;
    private Intent intent;
    private Intent intent_acc;
//    private Intent intent_r;
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PhoneNumber = findViewById(R.id.phoneNumber);
        HoldTime = findViewById(R.id.holdTime);
        WaitTime = findViewById(R.id.waitTime);
        TestLoops = findViewById(R.id.testLoops);
        CaseId = findViewById(R.id.caseId);
        StartButton = findViewById(R.id.startButton);
        StopButton = findViewById(R.id.stopButton);
        RadioGroup radgroup = (RadioGroup) findViewById(R.id.radioGroup);

        radgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radbtn = (RadioButton) findViewById(checkedId);
//                Toast.makeText(getApplicationContext(), "你选了" + radbtn.getText(), Toast.LENGTH_SHORT).show();
                if(radbtn.getText().equals("自动呼叫")){
                    intent =new Intent(MainActivity.this, CallService.class);//启动服务
                    StartButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            intent =new Intent(MainActivity.this, CallService.class);//启动服务
                            intent.putExtra("PHONE_NUMBER", PhoneNumber.getText().toString());
                            intent.putExtra("HOLD_TIME", HoldTime.getText().toString());
                            intent.putExtra("WAIT_TIME", WaitTime.getText().toString());
                            intent.putExtra("TEST_LOOPS", TestLoops.getText().toString());
                            intent.putExtra("CASE_ID",CaseId.getText().toString());
                            startService(intent);
                        }
                    });
                    StopButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stopService(intent);
                        }
                    });

                } else {
                    intent_acc =new Intent(MainActivity.this, AcceptService.class);//启动服务
                    StartButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intent_acc.putExtra("PHONE_NUMBER", PhoneNumber.getText().toString());
                            intent_acc.putExtra("TEST_LOOPS", TestLoops.getText().toString());
                            intent_acc.putExtra("CASE_ID",CaseId.getText().toString());
                            startService(intent_acc);



//                            Intent intent_r =new Intent(MainActivity.this,phoneListener.class);
//                            Bundle bundle =new Bundle();
//                            bundle.putString("PHONE_NUMBER",PhoneNumber.getText().toString() );
//                            intent_r.putExtras(bundle);
//                            sendBroadcast(intent_r);

                        }
                    });
                    StopButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stopService(intent_acc);
                        }
                    });

                }
            }
        });



    }
}
