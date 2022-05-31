package com.example.diagnostor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

public class IntroActivity extends AppCompatActivity {

    private Handler handler;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //intro 안에 IntroActivity 클래스와 MainActivity 클래스를 전달
            //화면간 페이지 전환시 이용
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    //블루투스 지원 유무 확인
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        /*상태바 제거*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //handler 변수에 Handler 객체를 넣음
        init();


        //블루투스를 지원하는 기기인지 아닌지 검사수행
        if(mBluetoothAdapter == null){
            //장치가 블루투스를 지원하지 않는 경우,
            Toast.makeText(getApplicationContext(), "블루투스 이용이 불가하여 애플리케이션을 종료합니다.", Toast.LENGTH_SHORT).show();
            // 깔끔종료
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
        }else {
            //장치가 블루투스를 지원하는경우 블루투스가 활성화상태인지 검사수행
            if(!mBluetoothAdapter.isEnabled()){
                //비활성 상태일시 활성상태로
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            } else {
                handler.postDelayed(runnable, 3000);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1: // 56번 줄에서 requestCode 값 1
                if(resultCode==RESULT_OK){
                    // 블루투스 기능을 활성화 시켰을 때
                    Toast.makeText(getApplicationContext(), "블루투스를 활성화합니다.", Toast.LENGTH_SHORT).show();
                    //2초 뒤에 화면 전환을 하게함
                    handler.postDelayed(runnable, 2000);
                }else{
                    // 활성화 여부를 거부할경우 종료
                    Toast.makeText(getApplicationContext(), "블루트스 비활성화 시 애플리케이션을 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    moveTaskToBack(true); // 태스크를 백그라운드로 이동
                    finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                    android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
                }
                break;
        }
    }// onActivityResult()..

    //init 메소드는 handler 변수에 Handler 객체를 넣는 함수
    public void init(){
        handler = new Handler();
    }

    //앱이 다시 호출되는 경우를 막기위해 호출
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        handler.removeCallbacks(runnable);

    }
}