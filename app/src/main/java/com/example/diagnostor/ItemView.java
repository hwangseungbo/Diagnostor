package com.example.diagnostor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ItemView extends LinearLayout {
    TextView item_pgn, item_spn, item_des, item_res, item_val, item_ex;

    public ItemView(Context context) {
        super(context);
        init(context);  //인플레이션하여 붙여주는 역할
    }

    public ItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // 지금 만든 객체(xml 레이아웃)를 인플레이션(메모리 객체화)해서 붙여줌
    // LayoutInflater를 써서 시스템 서비스를 참조할 수 있음
    // 단말이 켜졌을 때 기본적으로 백그라운드에서 실행시키는 것을 시스템 서비스라고 함
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.listview_item, this, true);

        item_pgn = findViewById(R.id.item_pgn);
        item_spn = findViewById(R.id.item_spn);
        item_des = findViewById(R.id.item_des);
        item_res = findViewById(R.id.item_res);
        item_val = findViewById(R.id.item_val);
    }

    public void setPgn(String pgn) {
        item_pgn.setText(pgn);
    }

    public void setSpn(String spn) {
        item_spn.setText(spn);
    }

    public void setDes(String des) {
        item_des.setText(des);
    }

    public void setRes(String res) {
        item_res.setText(res);
    }

    public void setVal(String val) {
        item_val.setText(val);
    }

}

