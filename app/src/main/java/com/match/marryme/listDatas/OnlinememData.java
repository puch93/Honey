package com.match.marryme.listDatas;

import java.io.Serializable;

import lombok.Data;

@Data
public class OnlinememData implements Serializable{
    private String idx;
    private String type;
    private String id;
    private String pw;
    private String name;
    private String familyname;
    private String nick;
    private String byear;
    private String bmonth;
    private String gender;
    private String addr1;
    private String addr2;
    private String hopeaddr;
    private String m_fcm_token;
    private String regdate;
    private String lat;
    private String lon;
    private String zodiac;
    private String loginYN;
    private String interest_idxs;
    private String u_cell_num;
    private String coin;
    private String pimg;
    private String lastnameYN;
}
