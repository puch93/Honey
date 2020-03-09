package com.match.honey.network.netUtil;

public class NetUrls {
//    public static final String DOMAIN = "http://app.yeobo.co.kr";
    public static final String DOMAIN = "http://yeobohoney.adamstore.co.kr";
    public static final String LOGIN = DOMAIN + "/Member/login";       // 로그인
    public static final String CHECKVERSION = DOMAIN + "/Member/version_check";       // 버전체크
    public static final String REGMEM = DOMAIN + "/Member/regist_member";       // 회원가입
    public static final String REGAUDIO = DOMAIN + "/Member/setProfilewav";     // 녹음파일 올리기
    public static final String REGVIDEO = DOMAIN + "/Member/setProfilemov";     // 동영상 올리기
    public static final String REGGIF = DOMAIN + "/Member/setProfilegif";       // 움짤 올리기
    public static final String PROFREADALL = DOMAIN + "/Member/all_read_profile";       // 프로필 열람(전체)
    public static final String MYPROFREAD = DOMAIN + "/Member/read_profile";       // 프로필 열람(내가 본)
    public static final String PROFREADME = DOMAIN + "/Member/readed_profile";       // 내프로필 열람(나를 본)
    public static final String MYPROFREADDEL = DOMAIN + "/Member/del_read_profile";       // 내프로필 열람 삭제(나를 본)
    public static final String MYPROFREADEDDEL = DOMAIN + "/Member/del_readed_profile";       // 내프로필 열람 삭제(내가 본)
    public static final String SETMENUCOUNT = DOMAIN + "/Member/set_menu_count";       // 내프로필 열람 삭제(내가 본)
    public static final String LOGINSTATE = DOMAIN + "/Member/loginYN";       // 로그인 여부 확인(idx)
    public static final String ALLINTEREST = DOMAIN + "/Member/all_interest_list";       // 관심회원 리스트(전체)
    public static final String TOINTEREST = DOMAIN + "/Member/to_interest_list";       // 관심회원 리스트(내가)
    public static final String FROMINTEREST = DOMAIN + "/Member/from_interest_list";       // 관심회원 리스트(나에게)
    public static final String PROFMODIFY = DOMAIN + "/Member/setProfile";       // 프로필 수정
    public static final String PROFBASICMODIFY = DOMAIN + "/Member/setProfileBasic";       // 프로필 수정
//    public static final String MEMBERLIST = DOMAIN + "/Member/memberlist";       // 멤버 리스트
    public static final String MEMBERLIST = DOMAIN + "/Member/memberlist_new";       // 멤버 리스트
    public static final String MYPROFILE = DOMAIN + "/Member/getMyProfile";       // 내 프로필 정보
    public static final String CHATCOUNT = DOMAIN + "/Member/chat_count";       // 내 프로필 정보
    public static final String CHECKNICKREVISE = DOMAIN + "/Member/check_nick";       // 내 프로필 정보
    public static final String CHECKLOCREVISE = DOMAIN + "/Member/check_location";       // 내 프로필 정보
    public static final String CHECKMARRYREVISE = DOMAIN + "/Member/check_marriage";       // 내 프로필 정보
    public static final String MYPROFILEBASIC = DOMAIN + "/Member/getProfileBasic";       // 내 프로필 기본정보
    public static final String OTHERPROFILE = DOMAIN + "/Member/getProfile";       // 상대방 프로필 정보
    public static final String NEWUSER = DOMAIN + "/Member/new_member_list";       // 신규회원
    public static final String SETNEWUSERPUSH = DOMAIN + "/Member/setHopeAddrNew";       // 신규회원 푸시희망 세팅
    public static final String GETNEWUSERPUSH = DOMAIN + "/Member/getHopeAddrNew";       // 신규회원 푸시희망 세팅
    public static final String TODAYUSER = DOMAIN + "/Member/accesslist";       // 오늘접속 리스트
    public static final String ONLINEUSER = DOMAIN + "/Member/onlinelist";       // 오늘접속 리스트
    public static final String REQCHAT = DOMAIN + "/Chat/createRoom";       // 채팅요청
    public static final String SETINTER = DOMAIN + "/Member/set_interest";       // 관심회원 설정
    public static final String DELINTER = DOMAIN + "/Member/del_interest";       // 관심회원 설정
    public static final String TODAYCOUNT = DOMAIN + "/Member/get_today_count";    //오늘접속회원 갯수
    public static final String VIEWCOUNT = DOMAIN + "/Member/get_view_count";     //열람회원 갯수
    public static final String NEWCOUNT = DOMAIN + "/Member/get_new_count";      //신규가입회원 갯수
    public static final String LIKECOUNT = DOMAIN + "/Member/get_like_count";     //찜회원 갯수
    public static final String DROPMEMBER = DOMAIN + "/Member/drop_member";       // 회원탈퇴
    public static final String MEMPAUSE = DOMAIN + "/Member/set_diapause";       // 휴면설정
    public static final String MEMPAUSECHECK = DOMAIN + "/Member/set_diapause_check";       // 휴면설정체크
    public static final String MEMUNPAUSE = DOMAIN + "/Member/set_undiapause";       // 휴면해제
    public static final String SUGGEST = DOMAIN + "/Board/suggest_input";       // 제휴제안
    public static final String REGQUESTION = DOMAIN + "/Board/question_input";       // 문의하기(질문)
    public static final String REGQUESTIONREPORT = DOMAIN + "/Board/return_input";       // 신고등록
    public static final String REGQUESTIONNOMEM = DOMAIN + "/Board/question_nomember";       // 비회원문의
    public static final String QNALIST = DOMAIN + "/Board/qna_board_list";       // 내 문의 내역
    public static final String QNALISTREPORT = DOMAIN + "/Board/return_board_list";       // 내 문의 내역
    public static final String CHATIMGUP = DOMAIN + "/Chat/fileupload";       // 채팅 이미지 업로드
    public static final String HOPEADDR = DOMAIN + "/Member/set_hopeaddr";       // 배우자 희망지역 설정
    public static final String CHATLIST = DOMAIN + "/Chat/listChats";       // 채팅리스트
    public static final String CHATOUT = DOMAIN + "/Chat/leaveChat";       // 채팅방 나가기
    public static final String ACCESSCHECK = DOMAIN + "/Member/accesscheck";       //
    public static final String CHATBOOOKMARK = DOMAIN + "/Chat/setbookmark";       // 채팅방 즐겨찾기
    public static final String CHATPUSH = DOMAIN + "/Chat/fcmMsg";       // 채팅방 즐겨찾기
    public static final String LOGOUT = DOMAIN + "/Member/logout";       // 로그아웃
    public static final String REQAUTHNUM = DOMAIN + "/Member/sms_send";       // 인증번호 요청
    public static final String CHECKAUTHNUM = DOMAIN + "/Member/check_auth_join";       // 인증번호 검수
    public static final String REGCELLNUM = DOMAIN + "/Member/check_auth";       // 전화번호 변경

    public static final String FRIENDBLOCK = DOMAIN + "/Member/friend_block";       // 차단회원
    public static final String FRIENDBLOCKLIST = DOMAIN + "/Member/friend_block_list";       // 차단회원
    public static final String FRIENDBLOCKCANCEL = DOMAIN + "/Member/friend_block_cancel";       // 차단회원

    public static final String BLOCKLIST = DOMAIN + "/Member/get_block_member_list";       // 차단회원
    public static final String SETBLOCK = DOMAIN + "/Member/set_block_member";       // 차단회원 설정
    public static final String UNBLOCK = DOMAIN + "/Member/set_block_member_del";       // 차단회원 해제

    public static final String CHARGECOIN = DOMAIN + "/Member/charge_coin";       // 코인충전

    public static final String SETINTRODUCE = DOMAIN + "/Member/setintroduce";       // 자기소개 수정
    public static final String DECLARE = DOMAIN + "/Member/declaremember";       // 신고하기
    public static final String PIMGUP = DOMAIN + "/Member/setProfilepimg";       // 프로필 이미지 변경
    public static final String REGIMAGES = DOMAIN + "/Member/setProfileimg";       // 이미지 등록
    public static final String DELIMAGE = DOMAIN + "/Member/delProfileimg";       // 이미지 삭제
    public static final String PROFREADCHECK = DOMAIN + "/Member/getChargeProfile";       // 프로필 열람 체크
    public static final String PROFREADCHECK2 = DOMAIN + "/Member/getChkChargeProfile";       // 프로필 열람 체크
    public static final String INTERCHECK = DOMAIN + "/Member/getInterest";       // 관심있어요 체크

    public static final String FINDID = DOMAIN + "/Member/findid";       // 아이디 찾기
    public static final String FINDPW = DOMAIN + "/Member/findpw";       // 비밀번호 찾기
    public static final String FINDPWREG = DOMAIN + "/Member/findpw_regi";       // 비밀번호 찾기

    public static final String POPUPADS = DOMAIN + "/Board/ads_popup";       // 비밀번호 찾기

    /**
     * 기본정보 설정
     */
    public static final String CHANGENICK = DOMAIN + "/Member/set_basic_info_nick";       // 대화명(닉네임) 변경
    public static final String CHANGEAGE = DOMAIN + "/Member/set_basic_info_byear";       // 나이(생년) 변경
    public static final String CHANGETYPE = DOMAIN + "/Member/set_basic_info_ctype";       // 성혼유형(결혼/재혼) 변경
    public static final String CHANGELOC = DOMAIN + "/Member/set_basic_info_area";       // 지역 변경
    public static final String CHANGEPW = DOMAIN + "/Member/set_basic_info_pwd";       // 비밀번호 변경
    public static final String CHANGECELLNUM = DOMAIN + "/Member/set_basic_info_ucellnum";       // 휴대폰번호 변경

    public static final String DELWAVE = DOMAIN + "/Member/delProfilewav";       // 음성삭제
    public static final String DELGIF = DOMAIN + "/Member/delProfilegif";       // 움짤삭제
    public static final String DELMOV = DOMAIN + "/Member/delProfilemov";       // 움짤삭제

    /**
     * 게시판 리스트
     * 파라미터 : btype=보드형태(notice:공지사항,....) / pg=페이징넘버
     */
    public static final String BOARDLIST = DOMAIN + "/Board/view_list";       // 게시판 리스트
    public static final String REVIEWLIST = DOMAIN + "/Board/review_list";       // 게시판 리스트
    public static final String BOARDLISTCOUNT = DOMAIN + "/Board/view_notice_count";       // 게시판 리스트
    public static final String REVIEWCOUNT = DOMAIN + "/Board/view_review_count";       // 게시판 리스트

    public static final String BOARDDETAIL = DOMAIN + "/Board/view_detail";       // 게시판 상세
    public static final String WRITEBOARD = DOMAIN + "/Board/regist_board";       // 게시판 작성
    public static final String REGISTREVIEW = DOMAIN + "/Board/regist_review";       // 게시판 작성

    public static final String TOKLIST = DOMAIN + "/Board/talk_board_list";       // 톡톡 게시판 리스트
    public static final String TOKLISTDETAIL = DOMAIN + "/Board/talk_board_view";       // 톡톡 게시판 리스트
    public static final String WRITETOKBOARD = DOMAIN + "/Board/talk_comment_input";       // 톡톡 게시판 글 작성
    public static final String UPDATETOKBOARD = DOMAIN + "/Board/talk_update_view";       // 톡톡 게시판 글 조회수 업데이트


    /**
     * 아이템 구매 관련
     */
    public static final String ITEMCHECK = DOMAIN + "/Member/sell_check";       // 아이템 사용가능여부 체크


    public static final String MYITEM = DOMAIN + "/Member/myitem";       // 아이템관리
    public static final String ITEMUSE = DOMAIN + "/Member/myuseitem";       // 통장관리
    public static final String BUYITEM = DOMAIN + "/Member/sell_item";       // 아이템구매
    public static final String BUYITEMREMAIN = DOMAIN + "/Member/sell_remain";       // 아이템구매
    public static final String BUYITEMINFO = DOMAIN + "/Member/sell_info";       //

    public static final String BUYSUBCOIN = DOMAIN + "/Member/autopay";       // 구독결제


    public static final String TERMS = DOMAIN + "/Main/app_info";       // 이용약관

    public static final String REGMPIMG = DOMAIN + "/Member/setProfilempimg";       // 혼인관계 증명 등록
    public static final String REGJOBIMG = DOMAIN + "/Member/setProfilejobimg";       // 직업사진
    public static final String REGANNIMG = DOMAIN + "/Member/setProfileannualimg";       // 연봉사진
    public static final String REGEDUIMG = DOMAIN + "/Member/setProfileeducationimg";       // 학력사진
}
