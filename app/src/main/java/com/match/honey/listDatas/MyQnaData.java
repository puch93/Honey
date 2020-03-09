package com.match.honey.listDatas;

public class MyQnaData {

    private String idx;
    private String qnatype;         // 로그인 | 접속오류.....
    private boolean replystate;      // 답변완료 | 답변대기
    private String qtext;
    private String answer;
    private String regdate;
    private String attach;

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getIdx() {
        return idx;
    }

    public void setQnatype(String qnatype) {
        this.qnatype = qnatype;
    }

    public String getQnatype() {
        return qnatype;
    }

    public void setReplystate(boolean replystate) {
        this.replystate = replystate;
    }

    public boolean isReplystate() {
        return replystate;
    }

    public void setQtext(String qtext) {
        this.qtext = qtext;
    }

    public String getQtext() {
        return qtext;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getAttach() {
        return attach;
    }
}
