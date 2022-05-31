package com.example.diagnostor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;

import static com.example.diagnostor.BTS.bts;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private FloatingActionButton fab_main, fab_con, fab_dis;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    private Set<BluetoothDevice> devices;   // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private static ItemAdapter adapter;

    String BTState = "0";
    String data0, data1, data2, data3, data4, data5, data6, data7, data8, data9, data10,
                  data11, data12, data13, data14, data15, data16, data17, data18, data19, data20,
                  data21, data22, data23, data24, data25, data26, data27;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*상태바 제거*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        /*리스트뷰 관련*/
        ListView listView = findViewById(R.id.listView);
        adapter = new ItemAdapter();
        /*0*/adapter.addItem(new Item("61443","92","현재 속도에서 엔진 퍼센트 부하","1 %/bit, 0 offset","", "SPN 513의 값과 더하면 실제 엔진토크가 나온다.(0000 = +0.000%, 0001 = +0.125%, ..., 0111 = +0.875%, 1000-1111 = Not available)"));
        /*1*/adapter.addItem(new Item("61444","512","운전자 요구 엔진 - 퍼센트 토크","1 %/bit, -125 % offset","", "운전자가 요구한 엔진의 토크 출력입니다. Operational Range : 0 to 125 %"));
        /*2*/adapter.addItem(new Item("61444","513","실제 엔진 - 퍼센트 토크","1 %/bit, -125 % offset","", "엔진의 계산된 출력 토크입니다."));
        /*3*/adapter.addItem(new Item("61444","190","엔진 속도","0.125 rpm/bit, 0 offset","", "최소 크랭크축 각도 720도에서 계산한 실제 엔진 속도를 실린더의 수로 나눈 실제 엔진 속도."));
        /*4*/adapter.addItem(new Item("61444","2432","엔진 수요 - 퍼센트 토크","1 %/bit, -125 % offset","", "연기 제어, 소음 제어 및 낮은 제어를 포함한 모든 동적 내부 입력에 의한 엔진의 요청 토크 출력 그리고 고속 거버닝."));
        /*5*/adapter.addItem(new Item("65253","247","총 엔진 작동 시간","0.05 hr/bit, 0 offset","", "엔진의 누적 작동 시간."));
        /*6*/adapter.addItem(new Item("65253","249","엔진 총 회전수","1000 r/bit, 0 offset","", "엔진의 누적 회전 수."));
        /*7*/adapter.addItem(new Item("65262","110","엔진 냉각수 온도","1 deg C/bit, -40 deg C offset","", "엔진 냉각 시스템에서 발견되는 액체의 온도."));
        /*8*/adapter.addItem(new Item("65262","174","엔진 연료 온도","1 deg C/bit, -40 deg C offset","", "첫 번째 연료 제어 시스템을 통과하는 연료(또는 가스)의 온도."));
        /*9*/adapter.addItem(new Item("65262","175","엔진 오일 온도","0.03125 deg C/bit, -273 deg C offset","", "엔진 윤활유의 온도."));
        /*10*/adapter.addItem(new Item("65262","176","엔진 터보차저 오일 온도","0.03125 deg C/bit, -273 deg C offset","", "터보차저 윤활유의 온도."));
        /*11*/adapter.addItem(new Item("65262","52","엔진 인터쿨러 온도","1 deg C/bit, -40 deg C offset","", "터보차저 뒤에 위치한 인터쿨러에서 발견되는 액체의 온도."));
        /*12*/adapter.addItem(new Item("65262","1134","엔진 인터쿨러 서모스탯 개방","0.4 %/bit, 0 offset","", "값이 0%일 경우 온도조절기가 완전히 닫힌 상태를 의미하며 100%일 경우 온도조절기가 완전히 열린 상태를 의미합니다."));
        /*13*/adapter.addItem(new Item("65263","98","엔진 오일 레벨","0.4 %/bit, 0 offset","", "최대 요구량에 대한 엔진 섬프 오일의 현재 부피 비율."));
        /*14*/adapter.addItem(new Item("65263","100","엔진 오일 압력","4 kPa/bit, 0 offset","", "오일 펌프에 의해 제공되는 엔진 윤활 시스템의 오일 게이지 압력."));
        /*15*/adapter.addItem(new Item("65263","109","엔진 냉각수 압력","2 kPa/bit, 0 offset","", "엔진 냉각 시스템에서 발견되는 액체의 게이지 압력."));
        /*16*/adapter.addItem(new Item("65263","111","엔진 냉각수 레벨","0.4 %/bit, 0 offset","", "총 냉각 시스템 부피에 대한 엔진 냉각 시스템에서 발견되는 액체 부피의 비율."));
        /*17*/adapter.addItem(new Item("65266","183","엔진 연료 비율","0.05 L/h per bit, 0 offset","", "단위 시간당 엔진이 소비하는 연료의 양."));
        /*18*/adapter.addItem(new Item("65266","184","엔진 순간 연비","1/512 km/L per bit, 0 offset","", "현재 차량 속도에서 현재 연비."));
        /*19*/adapter.addItem(new Item("65266","185","엔진 평균 연비","1/512 km/L per bit, 0 offset","", "차량 운행 구간의 순간 연비 평균"));
        /*20*/adapter.addItem(new Item("65271","114","순 배터리 전류","1 A/bit, -125 A offset","", "배터리 또는 배터리로/밖으로 흐르는 전류의 순 흐름."));
        /*21*/adapter.addItem(new Item("65271","115","교류 발전기 전류","1 A/bit, 0 offset","", "교류 발전기에서 흐르는 전류 측정."));
        /*22*/adapter.addItem(new Item("65271","167","충전 시스템 전위(전압)","0.05 V/bit, 0 offset","", "충전 시스템 출력에서 측정된 전위."));
        /*23*/adapter.addItem(new Item("65271","168","배터리 전위 / 전원 입력 1","0.05 V/bit, 0 offset","", "ECM/액추에이터 등의 입력에서 측정된 배터리 전위의 첫 번째 소스를 측정합니다."));
        /*24*/adapter.addItem(new Item("65271","158","키 스위치 배터리 전위","0.05 V/bit, 0 offset","", "키 스위치 또는 유사한 스위칭 장치를 통해 공급되는 전자 제어 장치의 입력에서 측정된 배터리 전위"));
        /*25*/adapter.addItem(new Item("65276","80","와셔액 레벨","0.4 %/bit, 0 offset","", "앞유리 세척 시스템에 있는 액체 저장소의 총 용기 부피에 대한 액체 부피의 비율."));
        /*26*/adapter.addItem(new Item("65276","96","연료 레벨 1","0.4 %/bit, 0 offset","", "연료 저장 용기의 총 부피에 대한 연료 부피의 비율."));
        /*27*/adapter.addItem(new Item("","","","","", ""));
        listView.setAdapter(adapter);

        //테스트배드
        /*
        String ecuData = "e18fefc008ff00ffffffffffff";
        String ecuSubData = ecuData.substring(3,7); //substring(3,7)은 인덱스 3~6번까지 가져온다.
        try{
            if(ecuSubData.equals("fefc")){

            }
        } catch (Exception e){
            Log.d("parsing", "Exception");
        }
        */


        /*블루투스 브로드캐스트 리시버 등록*/
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //연결 확인
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //연결 끊김 확인
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND);    //기기 검색됨
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);   //기기 검색 시작
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //기기 검색 종료
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        registerReceiver(mBluetoothStateReceiver, stateFilter);


        /*fab 애니메이션 정의*/
        mContext = getApplicationContext();

        fab_open = AnimationUtils.loadAnimation(mContext,R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext,R.anim.fab_close);

        fab_main = (FloatingActionButton)findViewById(R.id.fab_main);
        fab_con = (FloatingActionButton)findViewById(R.id.fab_con);
        fab_dis = (FloatingActionButton)findViewById(R.id.fab_dis);

        fab_main.setOnClickListener(this);
        fab_con.setOnClickListener(this);
        fab_dis.setOnClickListener(this);


        if(savedInstanceState != null){
            try{
                adapter.items.get(0).setVal(savedInstanceState.getString("data0") + "%"); data0 = savedInstanceState.getString("data0"); //화면회전 후 다시 화면회전시 데이터값이 null이되므로 이 뒷부분을 추가해준 것이다.
                adapter.items.get(1).setVal(savedInstanceState.getString("data1") + "%"); data1 = savedInstanceState.getString("data1");
                adapter.items.get(2).setVal(savedInstanceState.getString("data2") + "%"); data2 = savedInstanceState.getString("data2");
                adapter.items.get(3).setVal(savedInstanceState.getString("data3") + " rpm"); data3 = savedInstanceState.getString("data3");
                adapter.items.get(4).setVal(savedInstanceState.getString("data4") + "%"); data4 = savedInstanceState.getString("data4");
                adapter.items.get(5).setVal(savedInstanceState.getString("data5") + " h"); data5 = savedInstanceState.getString("data5");
                adapter.items.get(6).setVal(savedInstanceState.getString("data6") + " r"); data6 = savedInstanceState.getString("data6");
                adapter.items.get(7).setVal(savedInstanceState.getString("data7") + " ℃"); data7 = savedInstanceState.getString("data7");
                adapter.items.get(8).setVal(savedInstanceState.getString("data8") + " ℃"); data8 = savedInstanceState.getString("data8");
                adapter.items.get(9).setVal(savedInstanceState.getString("data9") + " ℃"); data9 = savedInstanceState.getString("data9");
                adapter.items.get(10).setVal(savedInstanceState.getString("data10") + " ℃"); data10 = savedInstanceState.getString("data10");
                adapter.items.get(11).setVal(savedInstanceState.getString("data11") + " ℃"); data11 = savedInstanceState.getString("data11");
                adapter.items.get(12).setVal(savedInstanceState.getString("data12") + "%"); data12 = savedInstanceState.getString("data12");
                adapter.items.get(13).setVal(savedInstanceState.getString("data13") + "%"); data13 = savedInstanceState.getString("data13");
                adapter.items.get(14).setVal(savedInstanceState.getString("data14") + " kPa"); data14 = savedInstanceState.getString("data14");
                adapter.items.get(15).setVal(savedInstanceState.getString("data15") + " kPa"); data15 = savedInstanceState.getString("data15");
                adapter.items.get(16).setVal(savedInstanceState.getString("data16") + " kPa"); data16 = savedInstanceState.getString("data16");
                adapter.items.get(17).setVal(savedInstanceState.getString("data17") + " L/h"); data17 = savedInstanceState.getString("data17");
                adapter.items.get(18).setVal(savedInstanceState.getString("data18") + " km/L"); data18 = savedInstanceState.getString("data18");
                adapter.items.get(19).setVal(savedInstanceState.getString("data19") + " km/L"); data19 = savedInstanceState.getString("data19");
                adapter.items.get(20).setVal(savedInstanceState.getString("data20") + " A"); data20 = savedInstanceState.getString("data20");
                adapter.items.get(21).setVal(savedInstanceState.getString("data21") + " A"); data21 = savedInstanceState.getString("data21");
                adapter.items.get(22).setVal(savedInstanceState.getString("data22") + " V"); data22 = savedInstanceState.getString("data22");
                adapter.items.get(23).setVal(savedInstanceState.getString("data23") + " V"); data23 = savedInstanceState.getString("data23");
                adapter.items.get(24).setVal(savedInstanceState.getString("data24") + " V"); data24 = savedInstanceState.getString("data24");
                adapter.items.get(25).setVal(savedInstanceState.getString("data25") + "%"); data25 = savedInstanceState.getString("data25");
                adapter.items.get(26).setVal(savedInstanceState.getString("data26") + "%"); data26 = savedInstanceState.getString("data6");

                BTState = savedInstanceState.getString("BTState");
                if(BTState.equals("0")){
                    fab_main.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                    fab_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b00020")));
                    fab_con.setClickable(true);
                    fab_dis.setClickable(false);
                    BTState = "0";
                }else{
                    fab_main.setImageResource(R.drawable.ic_baseline_bluetooth_audio_24);
                    fab_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#018786")));
                    fab_con.setClickable(false);
                    fab_dis.setClickable(true);
                    BTState = "1";
                }

            }catch (Exception e) {
                Log.d("화면회전", "화면 회전시 데이터 전송부분 에러");
            }

        }


        /*블루투스 핸들러로 블루트스 연결 뒤 수신된 데이터를 읽어옴. 여기서 데이터 값들에 대한 처리.*/
        BTS.getInstance().mBluetoothHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == BTS.getInstance().BT_MESSAGE_READ) {

                    String ecuData = null;

                    try{
                        ecuData = new String((byte[]) msg.obj, "UTF-8");
                        //index 10 ~ 25, 총 16개의 헥스값으로 2개당 1바이트 총 8바이트이다.
                        String ecuSubData = ecuData.substring(3,7);
                        if(ecuSubData.equals("f003")){
                            //61443

                            //92
                            ecuSubData = ecuData.substring(14,16);
                            int temp = Integer.parseInt(ecuSubData,16);
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            }else {
                                ecuSubData = String.valueOf(temp);
                            }
                            data0 = ecuSubData;
                            adapter.items.get(0).setVal(ecuSubData + "%");
                            adapter.notifyDataSetChanged();

                        } else if(ecuSubData.equals("f004")){
                            //61444

                            //512
                            ecuSubData = ecuData.substring(12,14);
                            int temp = Integer.parseInt(ecuSubData,16);
                            temp = temp - 125;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            }else{
                                ecuSubData = String.valueOf(temp);
                            }
                            data1 = ecuSubData;
                            adapter.items.get(1).setVal(ecuSubData + "%");
                            adapter.notifyDataSetChanged();

                            //513
                            ecuSubData = ecuData.substring(14,16);
                            int temp2 = Integer.parseInt(ecuSubData, 16);
                            temp2 = temp2 - 125;
                            ecuSubData = String.valueOf(temp2);
                            data2 = ecuSubData;
                            adapter.items.get(2).setVal(ecuSubData + "%");
                            adapter.notifyDataSetChanged();

                            //190
                            ecuSubData = ecuData.substring(18,20);
                            ecuSubData = ecuSubData + ecuData.substring(16,18);
                            int temp3 = Integer.parseInt(ecuSubData, 16);
                            temp3 = temp3 * 125;
                            temp3 = temp3 / 1000;
                            ecuSubData = String.valueOf(temp3);
                            data3 = ecuSubData;
                            adapter.items.get(3).setVal(ecuSubData + " rpm");
                            adapter.notifyDataSetChanged();

                            //2432
                            ecuSubData = ecuData.substring(24,26);
                            int temp4 = Integer.parseInt(ecuSubData, 16);
                            temp4 = temp4 - 125;
                            ecuSubData = String.valueOf(temp4);
                            data4 = ecuSubData;
                            adapter.items.get(4).setVal(ecuSubData + "%");
                            adapter.notifyDataSetChanged();

                        } else if(ecuSubData.equals("fee5")){
                            //65253

                            //247
                            ecuSubData = ecuData.substring(16,18);
                            ecuSubData = ecuSubData + ecuData.substring(14,16);
                            ecuSubData = ecuSubData + ecuData.substring(12,14);
                            ecuSubData = ecuSubData + ecuData.substring(10,12);
                            int temp = Integer.parseInt(ecuSubData,16);
                            temp = temp * 5;
                            temp = temp / 100;
                            ecuSubData = String.valueOf(temp);
                            data5 = ecuSubData;
                            adapter.items.get(5).setVal(ecuSubData + " h");
                            adapter.notifyDataSetChanged();

                            //249
                            ecuSubData = ecuData.substring(24,26);
                            ecuSubData = ecuSubData + ecuData.substring(22,24);
                            ecuSubData = ecuSubData + ecuData.substring(20,22);
                            ecuSubData = ecuSubData + ecuData.substring(18,20);
                            long temp2 = Long.parseLong(ecuSubData,16);
                            temp2 = temp2 * 1000;
                            if(ecuSubData.equals("ffffffff")){
                                ecuSubData = "?";
                            }else{
                                ecuSubData = String.valueOf(temp2);
                            }
                            data6 = ecuSubData;
                            adapter.items.get(6).setVal(ecuSubData + " r");
                            adapter.notifyDataSetChanged();

                        } else if(ecuSubData.equals("feee")){
                            //65262

                            //110
                            ecuSubData = ecuData.substring(10,12);
                            int temp = Integer.parseInt(ecuSubData,16);
                            temp = temp - 40;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            } else{
                                ecuSubData = String.valueOf(temp);
                            }
                            data7 = ecuSubData;
                            adapter.items.get(7).setVal(ecuSubData + " ℃");
                            adapter.notifyDataSetChanged();

                            //174
                            ecuSubData = ecuData.substring(12,14);
                            int temp2 = Integer.parseInt(ecuSubData, 16);
                            temp2 = temp2 -40;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            } else{
                                ecuSubData = String.valueOf(temp2);
                            }
                            data8 = ecuSubData;
                            adapter.items.get(8).setVal(ecuSubData + " ℃");
                            adapter.notifyDataSetChanged();

                            //175
                            ecuSubData = ecuData.substring(16,18);
                            ecuSubData = ecuSubData + ecuData.substring(14,16);
                            double temp3 = Integer.parseInt(ecuSubData, 16);
                            temp3 = temp3 * 0.03125;
                            temp3 = temp3 - 273;
                            if(ecuSubData.equals("ffff")){
                                ecuSubData = "?";
                            } else{
                                ecuSubData = String.valueOf((int)temp3);
                            }
                            data9 = ecuSubData;
                            adapter.items.get(9).setVal(ecuSubData + " ℃");
                            adapter.notifyDataSetChanged();

                            //176
                            ecuSubData = ecuData.substring(20,22);
                            ecuSubData = ecuSubData + ecuData.substring(18,20);
                            double temp4 = Integer.parseInt(ecuSubData, 16);
                            temp4 = temp4 * 0.03125;
                            temp4 = temp4 - 273;
                            if(ecuSubData.equals("ffff")){
                                ecuSubData = "?";
                            } else {
                                ecuSubData = String.valueOf((int)temp4);
                            }
                            data10 = ecuSubData;
                            adapter.items.get(10).setVal(ecuSubData + " ℃");
                            adapter.notifyDataSetChanged();

                            //52
                            ecuSubData = ecuData.substring(22, 24);
                            int temp5 = Integer.parseInt(ecuSubData,16);
                            temp5 = temp5 - 40;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            } else{
                                ecuSubData = String.valueOf(temp5);
                            }
                            data11 = ecuSubData;
                            adapter.items.get(11).setVal(ecuSubData + " ℃");
                            adapter.notifyDataSetChanged();

                            //1134
                            ecuSubData = ecuData.substring(24, 26);
                            int temp6 = Integer.parseInt(ecuSubData,16);
                            temp6 = temp6 * 4;
                            temp6 = temp6 / 10;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            } else{
                                ecuSubData = String.valueOf(temp6);
                            }
                            data12 = ecuSubData;
                            adapter.items.get(12).setVal(ecuSubData + " %");
                            adapter.notifyDataSetChanged();

                        } else if(ecuSubData.equals("feef")){
                            //65263
                            //98
                            ecuSubData = ecuData.substring(14, 16);
                            int temp = Integer.parseInt(ecuSubData, 16);
                            temp = temp * 4;
                            temp = temp / 10;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            } else{
                                ecuSubData = String.valueOf(temp);
                            }
                            data13 = ecuSubData;
                            adapter.items.get(13).setVal(ecuSubData + " %");
                            adapter.notifyDataSetChanged();

                            //100
                            ecuSubData = ecuData.substring(16, 18);
                            int temp2 = Integer.parseInt(ecuSubData, 16);
                            temp2 = temp2 * 4;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            } else{
                                ecuSubData = String.valueOf(temp2);
                            }
                            data14 = ecuSubData;
                            adapter.items.get(14).setVal(ecuSubData + " kPa");
                            adapter.notifyDataSetChanged();

                            //109
                            ecuSubData = ecuData.substring(22,24);
                            int temp3 = Integer.parseInt(ecuSubData,16);
                            temp3 = temp3 * 2;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            } else{
                                ecuSubData = String.valueOf(temp3);
                            }
                            data15 = ecuSubData;
                            adapter.items.get(15).setVal(ecuSubData + " kPa");
                            adapter.notifyDataSetChanged();

                            //111
                            ecuSubData = ecuData.substring(24,26);
                            int temp4 = Integer.parseInt(ecuSubData,16);
                            temp4 = temp4 * 4;
                            temp4 = temp4 / 10;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            } else{
                                ecuSubData = String.valueOf(temp4);
                            }
                            data16 = ecuSubData;
                            adapter.items.get(16).setVal(ecuSubData + " kPa");
                            adapter.notifyDataSetChanged();

                        } else if(ecuSubData.equals("fef2")){
                            //65266

                            //183
                            ecuSubData = ecuData.substring(12,14);
                            ecuSubData = ecuSubData + ecuData.substring(10,12);
                            int temp = Integer.parseInt(ecuSubData,16);
                            temp = temp * 5;
                            temp = temp / 100;
                            if(ecuSubData.equals("ffff")){
                                ecuSubData = "?";
                            }else {
                                ecuSubData = String.valueOf(temp);
                            }
                            data17 = ecuSubData;
                            adapter.items.get(17).setVal(ecuSubData + " L/h");
                            adapter.notifyDataSetChanged();

                            //184
                            ecuSubData = ecuData.substring(16,18);
                            ecuSubData = ecuSubData + ecuData.substring(14,16);
                            int temp2 = Integer.parseInt(ecuSubData,16);
                            temp2 = temp2 / 512;
                            if(ecuSubData.equals("ffff")){
                                ecuSubData = "?";
                            }else {
                                ecuSubData = String.valueOf(temp2);
                            }
                            data18 = ecuSubData;
                            adapter.items.get(18).setVal(ecuSubData + " km/L");
                            adapter.notifyDataSetChanged();

                            //185
                            ecuSubData = ecuData.substring(20, 22);
                            ecuSubData = ecuSubData + ecuData.substring(18,20);
                            int temp3 = Integer.parseInt(ecuSubData,16);
                            temp3 = temp3 / 512;
                            if(ecuSubData.equals("ffff")){
                                ecuSubData = "?";
                            }else {
                                ecuSubData = String.valueOf(temp3);
                            }
                            data19 = ecuSubData;
                            adapter.items.get(19).setVal(ecuSubData + " km/L");
                            adapter.notifyDataSetChanged();

                        } else if(ecuSubData.equals("fef7")){
                            //114
                            ecuSubData = ecuData.substring(10,12);
                            int temp = Integer.parseInt(ecuSubData, 16);
                            temp = temp - 125;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            }else {
                                ecuSubData = String.valueOf(temp);
                            }
                            data20 = ecuSubData;
                            adapter.items.get(20).setVal(ecuSubData + " A");
                            adapter.notifyDataSetChanged();

                            //115
                            ecuSubData = ecuData.substring(12,14);
                            int temp2 = Integer.parseInt(ecuSubData, 16);
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            }else{
                                ecuSubData = String.valueOf(temp2);
                            }
                            data21 = ecuSubData;
                            adapter.items.get(21).setVal(ecuSubData + " A");
                            adapter.notifyDataSetChanged();

                            //167
                            ecuSubData = ecuData.substring(16,18);
                            ecuSubData = ecuSubData + ecuData.substring(14,16);
                            int temp3 = Integer.parseInt(ecuSubData,16);
                            temp3 = temp3 * 5;
                            temp3 = temp3 / 100;
                            if(ecuSubData.equals("ffff")){
                                ecuSubData = "?";
                            }else{
                                ecuSubData = String.valueOf(temp3);
                            }
                            data22 = ecuSubData;
                            adapter.items.get(22).setVal(ecuSubData + " V");
                            adapter.notifyDataSetChanged();

                            //168
                            ecuSubData = ecuData.substring(20,22);
                            ecuSubData = ecuSubData + ecuData.substring(18,20);
                            int temp4 = Integer.parseInt(ecuSubData,16);
                            temp4 = temp4 * 5;
                            temp4 = temp4 / 100;
                            if(ecuSubData.equals("ffff")){
                                ecuSubData = "?";
                            }else{
                                ecuSubData = String.valueOf(temp4);
                            }
                            data23 = ecuSubData;
                            adapter.items.get(23).setVal(ecuSubData + " V");
                            adapter.notifyDataSetChanged();

                            //158
                            ecuSubData = ecuData.substring(24,26);
                            ecuSubData = ecuSubData + ecuData.substring(22,24);
                            int temp5 = Integer.parseInt(ecuSubData, 16);
                            temp5 = temp5 * 5;
                            temp5 = temp5 / 100;
                            if(ecuSubData.equals("ffff")){
                                ecuSubData = "?";
                            }else{
                                ecuSubData = String.valueOf(temp5);
                            }
                            data24 = ecuSubData;
                            adapter.items.get(24).setVal(ecuSubData + " V");
                            adapter.notifyDataSetChanged();

                        } else if(ecuSubData.equals("fefc")){
                            //65276

                            //80
                            ecuSubData = ecuData.substring(10,12);
                            int temp = Integer.parseInt(ecuSubData, 16);
                            temp = temp * 4;
                            temp = temp / 10;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            }else {
                                ecuSubData = String.valueOf(temp);
                            }
                            data25 = ecuSubData;
                            adapter.items.get(25).setVal(ecuSubData + " %");
                            adapter.notifyDataSetChanged();

                            //96
                            ecuSubData = ecuData.substring(12,14);
                            int temp2 = Integer.parseInt(ecuSubData, 16);
                            temp2 = temp2 * 4;
                            temp2 = temp2 / 10;
                            if(ecuSubData.equals("ff")){
                                ecuSubData = "?";
                            }else {
                                ecuSubData = String.valueOf(temp2);
                            }
                            data26 = ecuSubData;
                            adapter.items.get(26).setVal(ecuSubData + " %");
                            adapter.notifyDataSetChanged();

                        }
                    }catch (Exception e){
                        Log.d("received", "handleMessage: Error");
                    }
                }
                return false;
            }
        });


    }//onCreate().....................................................................................................................................................

    class ItemAdapter extends BaseAdapter {
        //데이터가 드러가있지 않고, 어떻게 담을지만 정의해둠
        ArrayList<Item> items = new ArrayList<Item>();

        @Override
        public int getCount(){
            return items.size();
        }

        public void addItem(Item item){
            items.add(item);
        }

        @Override
        public Object getItem(int position){
            return items.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        //어댑터가 데이터를 관리하고 뷰도 만듬
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ItemView itemView = null;
            if(convertView == null){
                itemView = new ItemView(getApplicationContext());
            }else {
                itemView = (ItemView)convertView;
            }
            Item item = items.get(position);
            itemView.setPgn(item.getPgn());
            itemView.setSpn(item.getSpn());
            itemView.setDes(item.getDes());
            itemView.setRes(item.getRes());
            itemView.setVal(item.getVal());
            return itemView;
        }
    }


    //리시버 블루투스 상태변화 BroadcastReceiver
    BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();   //입력된 action
            Log.d("Bluetooth action", action);
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = null;
            if(device != null) {
                name = device.getName();    //broadcast를 보낸 기기의 이름을 가져온다.
            }
            // 입력된 action에 따라서 함수를 처리한다.
            switch (action) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch(state) {
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(getApplicationContext(),"블루투스가 비활성화 되었습니다.",Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(getApplicationContext(),"블루투스가 활성화 되었습니다.",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:  // 블루투스 기기연결
                    Toast.makeText(getApplicationContext(),"블루투스 기기와 연결되었습니다.",Toast.LENGTH_SHORT).show();
                    fab_main.setImageResource(R.drawable.ic_baseline_bluetooth_audio_24);
                    fab_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#018786")));
                    fab_con.setClickable(false);
                    fab_dis.setClickable(true);
                    BTState = "1";


                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:   // 블루투스 기기 끊어짐
                    Toast.makeText(getApplicationContext(),"블루투스 기기와의 연결이 끊어졌습니다.",Toast.LENGTH_SHORT).show();
                    BTS.getInstance().cancel();
                    fab_main.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                    fab_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b00020")));
                    fab_con.setClickable(true);
                    fab_dis.setClickable(false);
                    BTState = "0";

                    break;
                case BluetoothDevice.ACTION_PAIRING_REQUEST:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.fab_main:
                toggleFab();
                break;
            case R.id.fab_con:
                toggleFab();
                //블루투스 연결 항목표시
                selectBluetoothDevice();

                break;
            case R.id.fab_dis:
                toggleFab();
                try{
                    if(BTS.getInstance().mBluetoothSocket.isConnected()){

                        Toast.makeText(getApplicationContext(),"블루투스 연결을 해제합니다.",Toast.LENGTH_SHORT).show();
                        BTS.getInstance().cancel();
                        fab_main.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24);
                        fab_main.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#b00020")));
                    }
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(),"블루투스 연결을 확인해주세요.",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void selectBluetoothDevice() {
        // 이미 페어링 되어있는 블루투스 기기를 찾습니다.
        BTS.getInstance();
        devices = BTS.getInstance().mBluetoothAdapter.getBondedDevices();
        // 페어링 된 디바이스의 크기를 저장
        int pariedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없는 경우
        if (pariedDeviceCount == 0) {
            // 페어링을 하기위한 함수 호출
            Toast.makeText(getApplicationContext(),"블루투스 설정에서 페어링을 진행해 주세요.",Toast.LENGTH_SHORT).show();
        }
        // 페어링 되어있는 장치가 있는 경우
        else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 되어있는 디바이스 목록");
            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();
            // 모든 디바이스의 이름을 리스트에 추가
            for (BluetoothDevice bluetoothDevice : devices) {
                list.add(bluetoothDevice.getName());
            }
            list.add("취소");

            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);

            // 해당 아이템을 눌렀을 때 호출 되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 해당 디바이스와 연결하는 함수 호출
                    if(charSequences[which].toString().equals("취소")){
                        dialog.dismiss();
                    }else{
                        connectDevice(charSequences[which].toString());
                    }
                }
            });

            // 뒤로가기 버튼 누를 때 창이 안닫히도록 설정
            builder.setCancelable(false);
            // 다이얼로그 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

    public void connectDevice(String deviceName) {
        // 페어링 된 디바이스들을 모두 탐색
        for (BluetoothDevice tempDevice : devices) {
        // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if(deviceName.equals(tempDevice.getName())){
                BTS.mBluetoothDevice = tempDevice;
                break;
            }
        }
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        boolean connectFlag = false;
        try {
            BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
            BTS.getInstance().mBluetoothSocket.connect();
            BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

            if(BTS.getInstance().isAlive()) {
                BTS.getInstance().interrupt();
            }
            bts = new BTS();
            BTS.getInstance().start();

            connectFlag = true;
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "연결에 실패하여 재시도합니다." , Toast.LENGTH_SHORT).show();
        }
        if(!connectFlag){   // 2차 시도
            try {
                BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                BTS.getInstance().mBluetoothSocket.connect();
                BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                if(BTS.getInstance().isAlive()) {
                    BTS.getInstance().interrupt();
                }
                bts = new BTS();
                BTS.getInstance().start();

                connectFlag = true;

            } catch (IOException e1) {
                Toast.makeText(getApplicationContext(), "연결에 실패하여 재시도합니다.." , Toast.LENGTH_SHORT).show();
            }
        }

        if(!connectFlag){   // 3차 시도
            try {
                BTS.getInstance().mBluetoothSocket = BTS.getInstance().mBluetoothDevice.createRfcommSocketToServiceRecord(BTS.getInstance().BT_UUID);
                BTS.getInstance().mBluetoothSocket.connect();
                BTS.getInstance().BluetoothThread(BTS.getInstance().mBluetoothSocket);

                if(BTS.getInstance().isAlive()) {
                    BTS.getInstance().interrupt();
                }

                bts = new BTS();
                BTS.getInstance().start();

                connectFlag = true;

            } catch (IOException e1) {
                Toast.makeText(getApplicationContext(), "블루투스 연결에 실패하였습니다." , Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void toggleFab(){
        if(isFabOpen){
            fab_con.startAnimation(fab_close);
            fab_dis.startAnimation(fab_close);
            fab_con.setClickable(false);
            fab_dis.setClickable(false);
            isFabOpen = false;
        }else{
            fab_con.startAnimation(fab_open);
            fab_dis.startAnimation(fab_open);
            fab_con.setClickable(true);
            fab_dis.setClickable(true);
            isFabOpen = true;
        }
    }

    //원형큐에 데이터가 남아있다면 센딩하는 쓰레드
    class SendMessageThread extends Thread {
        public void run() {
            while(true){
                try {
                    if(!ArrayQueue.getInstance().isEmpty()){
                        String data = ArrayQueue.getInstance().dequeue();
                        BTS.getInstance().write(data);
                        Log.d("sendgingMessage : ",data);
                    }
                    Thread.sleep(500);
                } catch(Exception e) { }
            }
        }
    }

    //화면 회전시 데이터 유지시키는 부분
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putString("BTState", BTState);
        outState.putString("data0", data0);outState.putString("data1", data1);outState.putString("data2", data2);
        outState.putString("data3", data3);outState.putString("data4", data4);outState.putString("data5", data5);
        outState.putString("data6", data6);outState.putString("data7", data7);outState.putString("data8", data8);
        outState.putString("data9", data9);outState.putString("data10", data10);outState.putString("data11", data11);
        outState.putString("data12", data12);outState.putString("data13", data13);outState.putString("data14", data14);
        outState.putString("data15", data15);outState.putString("data16", data16);outState.putString("data17", data17);
        outState.putString("data18", data18);outState.putString("data19", data19);outState.putString("data20", data20);
        outState.putString("data21", data21);outState.putString("data22", data22);outState.putString("data23", data23);
        outState.putString("data24", data24);outState.putString("data25", data25);outState.putString("data26", data26);
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기버튼 비활성화
    }
}