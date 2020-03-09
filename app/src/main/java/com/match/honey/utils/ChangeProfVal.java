package com.match.honey.utils;

public class ChangeProfVal {

    public ChangeProfVal(){

    }

    public String getAnnual(String strVal){
        String annual = "";
        switch (strVal){
            case "2000만원미만":
                annual = "1999";
                break;
            case "2000만원대":
                annual = "2000";
                break;
            case "3000만원대":
                annual = "3000";
                break;
            case "4000만원대":
                annual = "4000";
                break;
            case "5000만원대":
                annual = "5000";
                break;
            case "6000만원대":
                annual = "6000";
                break;
            case "7000만원대":
                annual = "7000";
                break;
            case "8000만원대":
                annual = "8000";
                break;
            case "9000만원대":
                annual = "9000";
                break;
            case "1억이상":
                annual = "10000";
                break;
        }
        return annual;
    }

    public String getReannual(String strVal){
        String annual = "";
        switch (strVal){
            case "1999":
                annual = "2000만원미만";
                break;
            case "2000":
                annual = "2000만원대";
                break;
            case "3000":
                annual = "3000만원대";
                break;
            case "4000":
                annual = "4000만원대";
                break;
            case "5000":
                annual = "5000만원대";
                break;
            case "6000":
                annual = "6000만원대";
                break;
            case "7000":
                annual = "7000만원대";
                break;
            case "8000":
                annual = "8000만원대";
                break;
            case "9000":
                annual = "9000만원대";
                break;
            case "10000":
                annual = "1억이상";
                break;
        }
        return annual;
    }

    public String getMainSmoke(String msmoke){
        String smoke = "";
        switch (msmoke){
            case "경험없음":
                smoke = "none";
                break;
            case "금연":
                smoke = "prohibit_smoke";
                break;
            case "흡연":
                smoke = "smoking";
                break;
        }
        return smoke;
    }

    public String setMainSmoke(String msmoke){
        String smoke = "";
        switch (msmoke){
            case "none":
                smoke = "경험없음";
                break;
            case "prohibit_smoke":
                smoke = "금연";
                break;
            case "smoking":
                smoke = "흡연";
                break;
        }
        return smoke;
    }

    public String getSmoketype1(String item){
        String count = "";
        switch (item){
            case "1년차":
                count = "365";
                break;
            case "2년차":
                count = "730";
                break;
            case "3년차":
                count = "1095";
                break;
            case "4년차":
                count = "1460";
                break;
            case "5년이상":
                count = "1825";
                break;
        }
        return count;
    }

    public String setSmoketype1(String item){
        String count = "";
        switch (item){
            case "365":
                count = "1년차";
                break;
            case "730":
                count = "2년차";
                break;
            case "1095":
                count = "3년차";
                break;
            case "1460":
                count = "4년차";
                break;
            case "1825":
                count = "5년이상";
                break;
        }
        return count;
    }

    public String getSmoketype2(String item){
        String count = "";
        switch (item){
            case "일 5개비이상":
                count = "5";
                break;
            case "일 10개비이상":
                count = "10";
                break;
            case "일 1갑이하":
                count = "20";
                break;
            case "일 1갑이상":
                count = "21";
                break;
        }
        return count;
    }

    public String setSmoketype2(String item){
        String count = "";
        switch (item){
            case "5":
                count = "일 5개비이상";
                break;
            case "10":
                count = "일 10개비이상";
                break;
            case "20":
                count = "일 1갑이하";
                break;
            case "21":
                count = "일 1갑이상";
                break;
        }
        return count;
    }

    public String getProperty(String property){
        String res = "";
        switch (property){
            case "3천만원미만":
                res = "2999";
                break;
            case "7천만원미만":
                res = "6999";
                break;
            case "1억미만":
                res = "9999";
                break;
            case "3억미만":
                res = "29999";
                break;
            case "5억미만":
                res = "49999";
                break;
            case "10억미만":
                res = "99999";
                break;
            case "10억이상":
                res = "100000";
                break;
        }
        return res;
    }

    public String setProperty(String property){
        String res = "";
        switch (property){
            case "2999":
                res = "3천만원미만";
                break;
            case "6999":
                res = "7천만원미만";
                break;
            case "9999":
                res = "1억미만";
                break;
            case "29999":
                res = "3억미만";
                break;
            case "49999":
                res = "5억미만";
                break;
            case "99999":
                res = "10억미만";
                break;
            case "100000":
                res = "10억이상";
                break;
        }
        return res;
    }


    public String setAnnParam(String ann){
        String res = "";
        switch (ann){
            case "전체":
                break;
//            case "3,000만원 이상":
//                res = "3000";
//                break;
//            case "5,000만원 이상":
//                res = "5000";
//                break;
//            case "1억 이상":
//                res = "10000";
//                break;

            case "2000만원미만":
                res = "1999";
                break;
            case "2000만원대":
                res = "2000";
                break;
            case "3000만원대":
                res = "3000";
                break;
            case "4000만원대":
                res = "4000";
                break;
            case "5000만원대":
                res = "5000";
                break;
            case "6000만원대":
                res = "6000";
                break;
            case "7000만원대":
                res = "7000";
                break;
            case "8000만원대":
                res = "8000";
                break;
            case "9000만원대":
                res = "9000";
                break;
            case "1억이상":
                res = "10000";
                break;
        }

        return res;
    }

    public String setPropParam(String prop){
        String res = "";
        switch (prop){
            case "전체":
                break;
//            case "1억 이상":
//                res = "10000";
//                break;
//            case "3억 이상":
//                res = "30000";
//                break;
//            case "5억 이상":
//                res = "50000";
//                break;
//            case "10억 이상":
//                res = "100000";
//                break;

            case "3천만원미만":
                res = "2999";
                break;
            case "7천만원미만":
                res = "6999";
                break;
            case "1억미만":
                res = "9999";
                break;
            case "3억미만":
                res = "29999";
                break;
            case "5억미만":
                res = "49999";
                break;
            case "10억미만":
                res = "99999";
                break;
            case "10억이상":
                res = "100000";
                break;
        }
        return res;
    }


    public String setSmokeParam(String smoke){
        String res = "";
        switch (smoke){
            case "경험없음":
                res = "none";
                break;
            case "금연":
                res = "prohibit_smoke";
                break;
            case "흡연":
                res = "smoking";
                break;
        }
        return res;
    }

    public String setChild(String child){
        String res = "";
        switch (child) {
            case "유자녀":
                res = "Y";
                break;
            case "무자녀":
                res = "N";
                break;
        }
        return res;
    }


    public String setDrinkParam(String drink){
        String res = "";
        switch (drink){
            case "비 음주자":
                res = "N";
                break;
            case "음주자":
                res = "Y";
                break;
        }
        return res;
    }

    public String setFamily(String fmly){
        String res = "";
        switch (fmly){
            case "all":
                res = "두분 다 살아계십니다";
                break;
            case "father":
                res = "아버지만 살아계십니다";
                break;
            case "mother":
                res = "어머니만 살아계십니다";
                break;
            case "none":
                res = "두분 다 돌아가셨습니다";
                break;
        }
        return res;
    }

    public String setWmry(String wmry){
        String res = "";
        switch (wmry){
            case "3month":
                res = "3개월 내 결혼할 계획입니다";
                break;
            case "6month":
                res = "6개월 내 결혼할 계획입니다";
                break;
            case "12month":
                res = "1년 내 결혼할 계획입니다";
                break;
            case "anytime":
                res = "언제든지 결혼할 계획입니다";
                break;
        }
        return res;
    }
}
