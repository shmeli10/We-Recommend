package ru.for_inform.we_recommend.model;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by OS1 on 20.05.2016.
 */
public class CustomRequest extends Request<JSONObject> {

    private Listener<JSONObject> listener;
    private Map<String, String> params;

    private String requestType = "";

    public CustomRequest(int method, String url, Map<String, String> params, Listener<JSONObject> responseListener, ErrorListener errorListener, String requestType) {
        super(method, url, errorListener);
        this.listener       = responseListener;
        this.params         = params;
        this.requestType    = requestType;
    }

    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    };

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            //
            JSONObject dataObject = new JSONObject();
            dataObject.put("requestType", requestType);

            //
            if(jsonString.substring(0,1).equals("{")) {

                //
                dataObject.put("responseType", "JSONObject");

                //
                dataObject.put("data", new JSONObject(jsonString));
            }
            //
            else {

                //
                dataObject.put("responseType", "JSONArray");

                //
                dataObject.put("data", new JSONArray(jsonString));
            }

            //
            return Response.success(dataObject, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}