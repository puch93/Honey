package com.match.honey.listDatas;

import java.io.Serializable;

public class LastnameData implements Serializable{
    String lastnameText;
    String initial;
    boolean checkstate = false;

    public void setLastnameText(String lastnameText) {
        this.lastnameText = lastnameText;
    }

    public String getLastnameText() {
        return lastnameText;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getInitial() {
        return initial;
    }

    public void setCheckstate(boolean checkstate) {
        this.checkstate = checkstate;
    }

    public boolean isCheckstate() {
        return checkstate;
    }
}
