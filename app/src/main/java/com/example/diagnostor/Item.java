package com.example.diagnostor;

public class Item {
    String pgn;
    String spn;
    String des;
    String res;
    String val;
    String ex;

    //생성
    public Item(String pgn, String spn, String des, String res, String val, String ex){
        this.pgn = pgn;
        this.spn = spn;
        this.des = des;
        this.res = res;
        this.val = val;
        this.ex = ex;
    }

    //게터, 세터
    public String getPgn() {
        return pgn;
    }
    public void setPgn(String pgn) {
        this.pgn = pgn;
    }
    public String getSpn() {
        return spn;
    }
    public void setSpn(String spn) {
        this.spn = spn;
    }
    public String getDes() {
        return des;
    }
    public void setDes(String des){
        this.des = des;
    }
    public String getRes() {
        return res;
    }
    public void setRes(String res) {
        this.res = res;
    }
    public String getVal() {
        return val;
    }
    public void setVal(String val) {
        this.val = val;
    }
    public String getEx() {
        return ex;
    }
    public void setEx(String ex) {
        this.ex = ex;
    }

    @Override
    public String toString() {
        return "Item{" + "pgn=" + pgn + ", spn=" + spn +", des=" + des + ", res=" + res + ", val=" + val + ", ex=" + ex + "}";
    }

}
