package com.match.marryme.listDatas;

import lombok.Data;

@Data
public class TokboardData {

    private String idx;             // 게시판 인덱스?? 고유값
    private String boardimg;        // 게시판 리스트에 띄울 이미지
    private String detailtitle;     // 게시판 상세 페이지의 타이틀
    private String subject;         // 입력창의 힌트
    private String subimg;          // 게시판 상세 페이지에 들어갈 이미지
    private String particnt;        // 참여인원수(게시판에 글쓴 인원수)
    private String hit;        // 조회수
    private String comment_count;        // 댓글수
}
