package com.match.marryme.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StringUtil {
    public static final String KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhFrNSkou9eM1EpjqD0dV9ESYPDxnGLjVWBJYUX7owabneLUgDs3OcH3kZRS1eYuWASYVoBOJwPRh03ecZXuENrVJj0J7iG/yYZMyC/WCEt2b2Tyxq0GtZOnmbuqb7/OxieiwPXjEZu84/G7XN/JD1Q0znW1EzDqrpP+oKdH1i2yHnIRgY+gS/+yQHyG478eqnxxiDjYAXHqOV4X0LsjB1/69GJ0LnjefMOgrEDVP/9fP6lmz/3cPJXdVv4VMi0XEw/bc7G27Ap7nEICAJSIij2xaX2OT9uUA5iY+GJ8G/l8jgeXCHILBbDfVQ34NtpNHejp0oR0voOdtjpxFp4SPHwIDAQAB";
    // 채팅
    public static final String CTYPESYS = "system";
    public static final String CTYPEMY = "my";
    public static final String CTYPEOTHER = "other";

    public static final String TYPEDATE = "date";
    public static final String TYPEITEM = "item";
    public static final String RSUCCESS = "success";
    public static final String RFAIL = "fail";
    public static final String TAG = "TEST_HOME";
    public static final String TAG_ERROR = "TEST_ERROR";
    public static final String TAG_SOCKET = "TEST_SOCKET";

    // 아이템 타입
    public static final String TYPE_M = "message";
    public static final String TYPE_P = "profile";
    public static final String TYPE_I = "interest";
    public static final String TYPE_UP = "profileup";

    // 아이템 코드
    public static final String MITEM01 = "m_01_4900";
    public static final String MITEM02 = "m_01_5900";
    public static final String MITEM03 = "m_01_6900";
    public static final String MITEM04 = "m_05_21900";
    public static final String MITEM05 = "m_10_39500";
    public static final String MITEM06 = "m_15_49900";
    public static final String MITEM07 = "m_30_89500";

    public static final String PITEM01 = "p_03_3000";
    public static final String PITEM02 = "p_05_5000";
    public static final String PITEM03 = "p_15_15000";
    public static final String PITEM04 = "p_30_25000";

    public static final String IITEM01 = "i_01_1000";
    public static final String IITEM02 = "i_05_4900";
    public static final String IITEM03 = "i_10_9000";
    public static final String IITEM04 = "i_30_24900";

    public static final String UITEM01 = "up_30_3000";
    public static final String UITEM02 = "up_50_5000";
    public static final String UITEM03 = "up_100_10000";
    public static final String UITEM04 = "up_200_19000";
    public static final String UITEM05 = "up_400_36000";
    public static final String UITEM06 = "up_600_50000";

    public static final String IMOTICON = "imot:";


    public static final String INTEREST01 = "interest_01";
    public static final String INTEREST02 = "interest_02";
    public static final String INTEREST03 = "interest_03";
    public static final String INTEREST04 = "interest_04";

    public static final String MESSAGE01 = "message_01";
    public static final String MESSAGE02 = "message_02";
    public static final String PROFILE01 = "profile_01";


    public static boolean isNull(String str) {
        if (str == null || str.length() == 0 || str.equals("null")) {
            return true;
        } else {
            return false;
        }
    }

    public static String setNumComma(int price) {
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(price);
    }

    public static String getStr(JSONObject jo, String key) {
        String s = null;
        try {
            s = jo.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static boolean isImage(String msg) {
        String reg = "^([\\S]+(\\.(?i)(jpg|png|jpeg))$)";

        return msg.matches(reg);
    }

    public static String calDateBetweenAandBSub(String cmpDate) {
//        SimpleDateFormat formatNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String currentDate = formatNow.format(System.currentTimeMillis());

        try { // String Type을 Date Type으로 캐스팅하면서 생기는 예외로 인해 여기서 예외처리 해주지 않으면 컴파일러에서 에러가 발생해서 컴파일을 할 수 없다.
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
            Date FirstDate = format.parse(cmpDate);
            Date SecondDate = new Date();

            Log.e(StringUtil.TAG, "FirstDate.getTime(): " + FirstDate.getTime());
            Log.e(StringUtil.TAG, "SecondDate.getTime(): " + SecondDate.getTime());

            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
            // 연산결과 -950400000. long type 으로 return 된다.
            long calDate = FirstDate.getTime() - SecondDate.getTime();
            Log.e(StringUtil.TAG, "calDate " + calDate);

            // 지난 일자면 return 0
            if(calDate < 0) {
                return "0";
            } else {
                // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
                // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
                long calDateDays = calDate / (24 * 60 * 60 * 1000);

                calDateDays = Math.abs(calDateDays);

                Log.e(StringUtil.TAG, "calDateDays " + calDateDays);

                return String.valueOf(calDateDays);
            }
        } catch (ParseException e) {
            // 예외 처리
            return "0";
        }
    }

    public static boolean calcExpire(String expiredate) {
        boolean isExpire = false;
        long etime = 0;
        long ctime = System.currentTimeMillis();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            Date exp = format.parse(expiredate);
            etime = exp.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (ctime > etime) {
            // 날짜 지남
            isExpire = false;
        } else {
            // 날짜 안지남
            isExpire = true;
        }

        return isExpire;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context. * @param uri The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String calcAge(String byear) {
        // 현재 연도에서 출생 연도를 뺀다. (2018 - 2000 = 18)
        // 1살을 더한다. (18 + 1 = 19)
        Calendar c = Calendar.getInstance();
//        Log.i(TAG,"year: "+(c.get(Calendar.YEAR)-Integer.parseInt(byear)+1));
        int lastYear = c.get(Calendar.YEAR) - Integer.parseInt(byear) + 1;

        return String.valueOf(lastYear);
    }

    public static boolean isToday(String idate) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Log.i(StringUtil.TAG, "today: " + sdf.format(date) + " idate: " + idate);

        return idate.equalsIgnoreCase(sdf.format(date));
    }

    public static int calcCoin(int coin, int price) {
        int res = coin - price;

        if (res < 0) {
            Log.i(StringUtil.TAG, "needcoin: " + Math.abs(res));
//            binding.tvNeedcoin.setText(String.valueOf(Math.abs(res)));
            return Math.abs(res);
        } else {
//            binding.tvNeedcoin.setText("0");
            return 0;
        }
    }


    public static String calcZodiac(int year) {
//        int zodiaccnt = 12;
//        int result;
        String zodiac = "";

//        result = year % zodiaccnt;

//        switch (result){
        switch (year % 12) {
            case 0:
                zodiac = "원숭이";
                break;
            case 1:
                zodiac = "닭";
                break;
            case 2:
                zodiac = "개";
                break;
            case 3:
                zodiac = "돼지";
                break;
            case 4:
                zodiac = "쥐";
                break;
            case 5:
                zodiac = "소";
                break;
            case 6:
                zodiac = "호랑이";
                break;
            case 7:
                zodiac = "토끼";
                break;
            case 8:
                zodiac = "용";
                break;
            case 9:
                zodiac = "뱀";
                break;
            case 10:
                zodiac = "말";
                break;
            case 11:
                zodiac = "양";
                break;
        }


        return zodiac;
    }

}
