package com.match.honey.listDatas;

import lombok.Data;

@Data
public class ChatMessage {
    private String idx;
    private String gender;
    private String pimg;
    private String nick;
    private String msg;
    private String regdate;
    private String readmem;
    private String totalmem;
    private String type;
    private String readnum;
    private String t_no;
    private boolean ismymsg;

    // todo 자신 / 상대방 메시지 구분??
//    public ChatMessage(String type,String pimg,String nick, String msg,String regdate,String gender, String readmem,String totalmem){

    /**
     *
     * @param type 메세지타입(시스템,자신,상대방)
     * @param msg 메세지
     * @param regdate 보낸시각
     * @param idx 인덱스
     * @param readnum 읽음상태
     */
    public ChatMessage(String type, String msg, String regdate, String idx, String readnum){
        this.type = type;
        this.msg = msg;
        this.regdate = regdate;
        this.idx = idx;
        this.readnum = readnum;
    }

//    public ChatMessage(String type,String pimg, String nick, String msg, String regdate){
//        this.type = type;
//        this.pimg = pimg;
//        this.nick = nick;
//        this.msg = msg;
//        this.regdate = regdate;
////        this.readnum = readnum;
////        this.ismymsg = ismymsg;
////        this.gender = gender;
////        this.t_no = t_no;
////        this.readmem = readmem;
////        this.totalmem = totalmem;
//    }

    public ChatMessage(String type,String pimg,String nick, String msg,String regdate,String readnum,String gender,String t_no,boolean ismymsg){
        this.type = type;
        this.pimg = pimg;
        this.nick = nick;
        this.msg = msg;
        this.regdate = regdate;
        this.readnum = readnum;
        this.ismymsg = ismymsg;
        this.gender = gender;
        this.t_no = t_no;
//        this.readmem = readmem;
//        this.totalmem = totalmem;
    }

    public ChatMessage(String type,String pimg,String nick, String msg,String regdate,String gender,boolean ismymsg){
        this.type = type;
        this.pimg = pimg;
        this.nick = nick;
        this.msg = msg;
        this.regdate = regdate;
        this.ismymsg = ismymsg;
        this.gender = gender;
    }
}
