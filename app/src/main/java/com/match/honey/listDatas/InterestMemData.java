package com.match.honey.listDatas;

import lombok.Data;

@Data
public class InterestMemData {

    private String idx;
    private String midx;
    private String itemtype;
    private String regdate;
    private boolean checkState;
    private InterestPData datas = new InterestPData();
    private String iptype;
    private String cate;
    private String piimgcnt;
    private boolean isMe;
}
