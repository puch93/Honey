package com.match.honey.listDatas;

public class HopestyleData {
    private String idx;
    private String text;
    private boolean selectState = false;

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getIdx() {
        return idx;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSelectState(boolean selectState) {
        this.selectState = selectState;
    }

    public boolean isSelectState() {
        return selectState;
    }
}
