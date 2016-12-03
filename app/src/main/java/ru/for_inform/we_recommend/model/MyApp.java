package ru.for_inform.we_recommend.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by OS1 on 30.06.2016.
 */
public class MyApp {

    private static RootHashTag selectedRootHashTag;
    private static HashTag selectedHashTag;
    private static ExecutorCard selectedExecutorCard;

    private static Map<String, RootHashTag>  appRootHashTagsMap     = new HashMap<>();
    private static Map<String, HashTag>      appHashTagsMap         = new HashMap<>();
    private static Map<String, ExecutorCard> appExecutorsCardsMap   = new HashMap<>();

    private static Typeface bebasNeueBoldTypeface;

    private static ImageLoader appImageLoader;

    private static String mediaLinkHead  = "http://front.rocketlead.org";

    private static String requestUrlHead = "http://rocketlead.org/web/api/";

    private static int maxLength = 21;

    private static String[] bgColorArr = new String[] { "#36b6eb",
                                                        "#e740a2",
                                                        "#7eb106",
                                                        "#d4b927",
                                                        "#990cb2",
                                                        "#c85110",
    };

    /**
     * Lower case Hex Digits.
     */
    private static final String HEX_DIGITS = "0123456789abcdef";

    /**
     * Byte mask.
     */
    private static final int BYTE_MSK = 0xFF;

    /**
     * Hex digit mask.
     */
    private static final int HEX_DIGIT_MASK = 0xF;

    /**
     * Number of bits per Hex digit (4).
     */
    private static final int HEX_DIGIT_BITS = 4;

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static String getMediaLinkHead() {

        return mediaLinkHead;
    }

    //
    public static String getRequestUrlHead() {

        return requestUrlHead;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static Map<String, RootHashTag> getAppRootHashTagsMap() {

        return appRootHashTagsMap;
    }

    //
    public static boolean isAppRootHashTagsMapEmpty() {

        return appRootHashTagsMap.isEmpty();
    }

    //
    public static void setAppRootHashTagsMap(Map<String, RootHashTag> rootHashTagsMap) {

        // если значение задано
        if(rootHashTagsMap != null)
            // кладем его в переменную
            appRootHashTagsMap = rootHashTagsMap;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static Map<String, HashTag> getAppHashTagsMap() {

        return appHashTagsMap;
    }

    //
    public static boolean isAppHashTagsMapEmpty() {

        return appHashTagsMap.isEmpty();
    }

    //
    public static void setAppHashTagsMap(Map<String, HashTag> hashTagsMap) {

        // если значение задано
        if(hashTagsMap != null)
            // кладем его в переменную
            appHashTagsMap = hashTagsMap;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static Map<String, ExecutorCard> getAppExecutorsCardsMap() {

        return appExecutorsCardsMap;
    }

    //
    public static boolean isAppExecutorsCardsMapEmpty() {

        return appExecutorsCardsMap.isEmpty();
    }

    //
    public static void setAppExecutorsCardsMap(Map<String, ExecutorCard> executorsCardsMap) {

        // если значение задано
        if(executorsCardsMap != null)
            // кладем его в переменную
            appExecutorsCardsMap = executorsCardsMap;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static String getSelectedRootHashTagShortName() {

        return getShort(selectedRootHashTag.getName());
    }

    //
    public static int getSelectedRootHashTagColorId() {

        return selectedRootHashTag.getTagColor().getTagColorId();
    }

    //
    public static void setSelectedRootHashTag(RootHashTag rootHashTag) {

        // если значение задано
        if(rootHashTag != null)
            // сохраняем его
            selectedRootHashTag = rootHashTag;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static String getSelectedHashTagShortName() {

        return getShort(selectedHashTag.getName());
    }

    //
    public static void setSelectedHashTag(HashTag hashTag) {

        // если ссылка задана
        if (hashTag != null)
            // сохраняем ее
            selectedHashTag = hashTag;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static String getSelectedExecutorShortName() {

        return getShort(selectedExecutorCard.getExecutor().getName());
    }

    //
    public static ExecutorCard getSelectedExecutorCard() {

        return selectedExecutorCard;
    }
    //
    public static void setSelectedExecutorCard(ExecutorCard executorCard) {

        // если ссылка задана
        if (executorCard != null)
            // сохраняем ее
            selectedExecutorCard = executorCard;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static String getRootTagBgColor(int colorId) {

        //
        StringBuilder resultColor = new StringBuilder("");

        // получаем размер массива цветов
        int bgColorArrLength = bgColorArr.length;

        // если значение меньше размера массива цветов
        if(colorId < bgColorArrLength)
            // получаем цвет на основании прищедшего значения
            resultColor.append(bgColorArr[colorId]);
        // если значение не меньше размера массива цветов
        else {

            // получаем остаток от деления на размер массива
            int newColorId = (colorId % bgColorArrLength);

            // получаем цвет на основании вычисленного значения
            resultColor.append(bgColorArr[newColorId]);
        }

        return resultColor.toString();
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static Typeface getBebasNeueBoldTypeface() {

        return bebasNeueBoldTypeface;
    }

    //
    public static void setBebasNeueBoldTypeface(AssetManager assetManager, String bebasNeueBoldFont) {

        // если значение задано
        if((assetManager != null) && (bebasNeueBoldFont != null))
            // получаем ссылку на шрифт
            bebasNeueBoldTypeface = Typeface.createFromAsset(assetManager, bebasNeueBoldFont);
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static ImageLoader getAppImageLoader() {

        return appImageLoader;
    }

    //
    public static void setAppImageLoader(Context context) {

        // если значение задано
        if(context != null)
            // получаем ссылку на шрифт
            appImageLoader = MySingleton.getInstance(context).getImageLoader();
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public static String getShort(String longValue) {

        StringBuilder shortValueSB = new StringBuilder("");

        // если длина больше допустимой
        if(longValue.length() > maxLength) {
            // обрезаем строку и дополняем троеточием
            shortValueSB.append(longValue.substring(0, (maxLength - 1)));
            shortValueSB.append("...");
        }
        // если длина не превышает допустимой
        else
            // вернем строку без изменений
            shortValueSB.append(longValue);

        return shortValueSB.toString();
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public static String getCrypted(final String phoneClean) {

        Log.d("myLogs", "==========================");
        Log.d("myLogs", "MyApp: getCrypted: phone= " +phoneClean);

        try {

            String phone_md5        = getMD5(phoneClean);
            String salt             = "56f639a018d45fd03e9fa9f1d7ad7c57cd26f556";
            String phone_md5_salted = phone_md5 + salt;

            // String result = computeSha1OfByteArray(phoneClean.getBytes(("UTF-8")));
            String phone_sha = computeSha1OfByteArray(phone_md5_salted.getBytes(("UTF-8")));

            // Log.d("myLogs", "MyApp: getCrypted: phoneCrypted= " +result);
            Log.d("myLogs", "MyApp: getCrypted: phone_md5= "        +phone_md5);
            Log.d("myLogs", "MyApp: getCrypted: phone_md5_salted= " +phone_md5_salted);
            Log.d("myLogs", "MyApp: getCrypted: phone_sha= "        +phone_sha);

            // return result;
            return phone_sha;
            // return computeSha1OfByteArray(phoneClean.getBytes(("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    public static String getMD5(String input) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

//    private static byte[] computeMD5OfByteArray(final byte[] phoneByteArr) {
//
//        Log.d("myLogs", "MyApp: computeMD5OfByteArray: phone= " +phoneByteArr.toString());
//
//        try {
//
//            MessageDigest md_MD5 = MessageDigest.getInstance("MD5");
//            md_MD5.update(phoneByteArr);
//
//            byte[] phone_MD5_ByteArr = md_MD5.digest();
//
//            // String phone_MD5 = phoneCryptedByteArr.toString() + "56f639a018d45fd03e9fa9f1d7ad7c57cd26f556";
//            String phone_MD5 = phone_MD5_ByteArr.toString();
//            String salt      = "56f639a018d45fd03e9fa9f1d7ad7c57cd26f556";
//
//            String result = phone_MD5 + salt;
//
//            Log.d("myLogs", "MyApp: computeMD5OfByteArray: phone= " +phoneByteArr.toString()+ ", phone_MD5= " +phone_MD5);
//            Log.d("myLogs", "MyApp: computeMD5OfByteArray: phone= " +phoneByteArr.toString()+ ", with salt= " +result);
//
//            // return (phone_MD5.getBytes(("UTF-8")));
//            return (result.getBytes(("UTF-8")));
//
//        } catch (NoSuchAlgorithmException ex) {
//            throw new UnsupportedOperationException(ex);
//        } catch (UnsupportedEncodingException e) {
//            throw new UnsupportedOperationException(e);
//        }
//    }

    private static String computeSha1OfByteArray(final byte[] phoneByteArr) {

        // Log.d("myLogs", "MyApp: computeSha1OfByteArray: phone= " +phoneByteArr.toString());

        try {

            MessageDigest md_SHA_1 = MessageDigest.getInstance("SHA-1");
            // md_SHA_1.update(computeMD5OfByteArray(phoneByteArr));

            md_SHA_1.update(phoneByteArr);

            byte[] phone_SHA_1_ByteArr = md_SHA_1.digest();

            String phone_SHA_1 = phone_SHA_1_ByteArr.toString();
            // Log.d("myLogs", "MyApp: computeSha1OfByteArray:  phone= " +phoneByteArr.toString()+ ", phone_SHA_1= " +phone_SHA_1);

            return toHexString(phone_SHA_1_ByteArr);

        } catch (NoSuchAlgorithmException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     * Compute a String in HexDigit from the input.
     *
     * @param byteArray
     *                a row byte array
     * @return a hex String
     */
    private static String toHexString(final byte[] byteArray) {

        StringBuilder sb = new StringBuilder(byteArray.length * 2);

        for (int i = 0; i < byteArray.length; i++) {

            int b = byteArray[i] & BYTE_MSK;

            sb.append(HEX_DIGITS.charAt(b >>> HEX_DIGIT_BITS)).append(HEX_DIGITS.charAt(b & HEX_DIGIT_MASK));
        }
        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static void sendRequest(MyRequest myRequest, int requestMethod, String requestTail, String requestTailSeparator, String requestType, String[] paramsArr, Map<String, String> requestBody) {

        // задаем типа запроса(GET|POST)
        myRequest.setRequestMethod(requestMethod);

        // формируем хвост запроса - обращение к методу
        myRequest.setRequestUrlTail(requestTail);

        // формируем разделитель в запросе части с параметрами
        if(requestTailSeparator != null)
            // формируем разделитель в запросе части с параметрами
            myRequest.setRequestTailSeparator(requestTailSeparator);

        // задаем тип запрашиваемой у сервера информации
        myRequest.setRequestType(requestType);

        // если массив параметров задан
        if(paramsArr != null)
            // задаем массив параметров, отправляемых в запросе серверу
            myRequest.setRequestParams(paramsArr);

        // если коллекция параметров задана
        if(requestBody != null)
            // задаем параметры для передачи в теле запроса серверу
            myRequest.setRequestBody(requestBody);

        // отправляем GET запрос
        myRequest.send();
    }
}