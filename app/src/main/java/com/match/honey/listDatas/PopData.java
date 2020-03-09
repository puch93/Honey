package com.match.honey.listDatas;

public class PopData {
    String item;
    boolean isSelected = false;

    public PopData(){}

    public PopData(String item){
        this.item = item;
    }

    public PopData(String item,boolean isSelected) {
        this.item = item;
        this.isSelected = isSelected;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
