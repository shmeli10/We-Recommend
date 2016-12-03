package ru.for_inform.we_recommend.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

/**
 * Created by OS1 on 20.05.2016.
 */
public class MyRequest {

    private Context context;

    private String      requestUrlTail;
    private String[]    requestParamsArr;
    private String      requestTailSeparator;

    private Map<String, String> requestBody;

    private OnResponseReturnListener responseReturnListener;

    private String requestType = "";

    private int requestMethod = Request.Method.GET;

    // private final String LOG_TAG = "myLogs";

    //////////////////////////////////////////////

    // интерфейс для работы с вызывающими классами
    public interface OnResponseReturnListener {
        void onResponseReturn(JSONObject serverResponse);
    }

    //////////////////////////////////////////////

    //
    public MyRequest(Context context) {

        this.context = context;

        // если вызывающий класс реализует интерфейс
        if (this.context instanceof OnResponseReturnListener)
            // получаем ссылку на него
            responseReturnListener = (OnResponseReturnListener) context;
        else
            throw new ClassCastException(context.toString() + " must implement OnResponseReturnListener");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void setRequestMethod(int requestMethod) {
        this.requestMethod = requestMethod;
    }

    //
    public void setRequestUrlTail(String requestUrlTail) {
        this.requestUrlTail = requestUrlTail;
    }

    //
    public void setRequestParams(String[] requestParamsArr) {
        this.requestParamsArr = requestParamsArr;
    }

    //
    public void setRequestTailSeparator(String requestTailSeparator) {
        this.requestTailSeparator = requestTailSeparator;
    }

    //
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    //
    public void setRequestBody(Map<String, String> requestBody) {
        this.requestBody = requestBody;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    //
    public void send() {

        RequestQueue queue = Volley.newRequestQueue(context);

        // будем формировать строку запроса
        StringBuilder request = new StringBuilder("" + MyApp.getRequestUrlHead() + requestUrlTail);

        // если параметры заданы
        if((requestParamsArr != null) && (requestParamsArr.length > 0)) {

            // добавлям символ заданного разделителя в хвост основной части запроса
            request.append(requestTailSeparator);

            // проходим циклом по массиву заданных параметров
            for(int i=0; i<requestParamsArr.length; i++) {

                // добавляем к основной части запроса, строку с заданными параметрами
                request.append("" +requestParamsArr[i]);

                // если параметров несколько и это не первый элемент
                if((i > 0) && (requestParamsArr.length > 1))
                    // добавляем разделитель
                    request.append("&");
            }
        }

        // Log.d(LOG_TAG, "MyRequest: send: request= " +request.toString());

        // формируем запрос
        CustomRequest jsonObjectRequest = new CustomRequest(requestMethod, request.toString(), requestBody, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {

                // Log.d(LOG_TAG, "MyRequest: send: response= " +response.toString());

                try {
                    // возвращаем ответ сервера
                    responseReturnListener.onResponseReturn(response);
                } catch (Exception error) {

                    error.printStackTrace();

                    // формируем JSONObject оболочку
                    JSONObject dataObject = new JSONObject();

                    try {
                        // добавляем уточняющие параметры в ответ сервера
                        dataObject.put("requestType",   requestType);
                        dataObject.put("responseType",  "JSONObject");
                        dataObject.put("data",          new JSONObject());

                    } catch (JSONException je) {

                        // Log.d(LOG_TAG, "ServerRequest: sendGetRequest(): CustomRequest: onResponse(): Exception: JSONException");
                    }

                    // возвращаем "обернутый" ответ сервера
                    responseReturnListener.onResponseReturn(dataObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();

                // формируем JSONObject оболочку
                JSONObject dataObject = new JSONObject();

                try {
                    // добавляем уточняющие параметры в ответ сервера
                    dataObject.put("requestType",   requestType);
                    dataObject.put("responseType",  "JSONObject");
                    dataObject.put("data",          new JSONObject());

                } catch (JSONException je) {

                    // Log.d(LOG_TAG, "ServerRequest: sendGetRequest(): CustomRequest: onErrorResponse(): VolleyError: JSONException");
                }

                // возвращаем "обернутый" ответ сервера
                responseReturnListener.onResponseReturn(dataObject);
            }
        }, requestType);

        // кладем запрос в очередь
        queue.add(jsonObjectRequest);
    }
}