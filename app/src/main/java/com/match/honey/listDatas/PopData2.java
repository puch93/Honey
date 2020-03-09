package com.match.honey.listDatas;

public class PopData2 {
    String item;
    int item_year;
    boolean isSelected = false;

    public PopData2(){}

    public PopData2(String item){
        this.item = item;
    }

    public PopData2(String item, int item_year, boolean isSelected) {
        this.item = item;
        this.isSelected = isSelected;
        this.item_year = item_year;
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


    public void setItem_year(int item_year) {
        this.item_year = item_year;
    }

    public int getItem_year() {
        return item_year;
    }
}
