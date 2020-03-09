package com.match.honey.listDatas;

import lombok.Data;

@Data
public class MsgboxData {
    private String itemtype;
    private String msgcnt;
    private String ru_bookmark;

    private String idx;
    private String user_idx;
    private String room_idx;
    private String created_at;
    private String msg;
    private String read_user_idx;
    private ChatDetailData user;
    private ChatDetailData friend;

    private String pimg_ck;
    private int character;

    private boolean checkState = false;
    private boolean isFavorite = false;
}
