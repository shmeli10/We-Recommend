package ru.for_inform.we_recommend.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.for_inform.we_recommend.R;
import ru.for_inform.we_recommend.model.ExecutorCard;
import ru.for_inform.we_recommend.model.MyExecutor;
import ru.for_inform.we_recommend.model.HashTag;
import ru.for_inform.we_recommend.model.MyApp;
import ru.for_inform.we_recommend.model.MyManager;
import ru.for_inform.we_recommend.model.MyRequest;

/**
 * Created by OS1 on 02.05.2016.
 */
public class HashTag_Activity extends       Activity
                              implements    View.OnClickListener,
                                            MyRequest.OnResponseReturnListener {

    private Context   context;
    private MyRequest myRequest;

    private ImageView backIV;
    private TextView  headerTitleTV;

    private LinearLayout hashTagsLL;

    private final int backIVResId           = R.id.RootHashTag_BackIV;
    private final int headerTitleTVResId    = R.id.RootHashTag_HeaderTitleTV;
    private final int hashTagsLLResId       = R.id.RootHashTag_HashTagsLL;

    private float   density;

    // private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.hash_tag_layout);

        //////////////////////////////////////////////////////////////////////////////////

        backIV          = (ImageView) findViewById(backIVResId);
        headerTitleTV   = (TextView) findViewById(headerTitleTVResId);
        headerTitleTV.setTypeface(MyApp.getBebasNeueBoldTypeface());

        hashTagsLL = (LinearLayout) findViewById(hashTagsLLResId);

        //////////////////////////////////////////////////////////////////////////////////

        backIV.setOnClickListener(this);

        //////////////////////////////////////////////////////////////////////////////////

        // получаем контекст
        context = this;
        density = context.getResources().getDisplayMetrics().density;

        //////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        myRequest = new MyRequest(context);

        //////////////////////////////////////////////////////////////////////////////////

        //
        setTitle();

        //
        setSubCategories();
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {

            case backIVResId:
                                // закрыть экран
                                finish();

                                break;
        }
    }

    //
    public void onResponseReturn(JSONObject serverResponse) {

        // если ответ сервера не пустой
        if (serverResponse != null) {

            try {

                String requestType = "";
                String responseType = "";

                // если ответ сервера содержит параметр "requestType"
                if (serverResponse.has("requestType"))
                    // получаем из него "тип запроса"
                    requestType = serverResponse.getString("requestType");

                // если ответ сервера содержит параметр "responseType"
                if (serverResponse.has("responseType"))
                    // получаем из него "тип ответа"
                    responseType = serverResponse.getString("responseType");

                // если "тип запроса" получен
                if((requestType != null) && (!requestType.equals(""))) {

                    JSONArray dataArray = null;

                    // если "тип ответа" получен
                    if((responseType != null) && (!responseType.equals(""))) {

                        // если "тип ответа" - JSONArray
                        if(responseType.equals("JSONArray")) {

                            // если ответ сервера содержит параметр "data"
                            if (serverResponse.has("data"))
                                // получаем из него JSONArray
                                dataArray = serverResponse.getJSONArray("data");
                        }

                        ///////////////////////////////////////////////////////////////////////////////

                        // если "тип запроса" - "Исполнители"
                        if (requestType.equals("executors")) {

                            // если JSONArray задан
                            if((dataArray != null)) {

                                // сохраням полученных исполнителей в коллекции
                                setExecutors(dataArray);

                                // переходим на экран "Список исполнителей"
                                moveForward();
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

                // Log.d(LOG_TAG, "HashTag_Activity: onResponseReturn(): Error!");
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    //
    private void setTitle() {

        // задаем значение заголовку
        headerTitleTV.setText(MyApp.getSelectedRootHashTagShortName().toUpperCase());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void getExecutors(HashTag selectedHashTag) {

        // сохраняем ссылку на выбранный хеш-тег
        MyApp.setSelectedHashTag(selectedHashTag);

        // отправляем запрос на получение списка исполнителей, связанных с выбранным хеш-тегом
        MyApp.sendRequest(myRequest, Request.Method.GET, "tags/" + selectedHashTag.getId() + "/executors", null, "executors", null, null);
    }

    //
    private void setExecutors(JSONArray dataArray) {

        // будем хранить карточки исполнителей
        Map<String, ExecutorCard> executorsCardsMap = new HashMap<>();

        try {
            // проходим циклом по массиву данных
            for (int i=0; i<dataArray.length(); i++) {

                // получаем очередную карточку исполнителя
                JSONObject executorCardJSONObj = (JSONObject) dataArray.get(i);

                // если JSONObject содержит параметр "id"
                if(executorCardJSONObj.has("id")) {

                    // получаем идентификатор карточки исполнителя
                    String cardIdStr = executorCardJSONObj.getString("id");

                    // если идентификатор карточки получен
                    if((cardIdStr != null) && (!cardIdStr.equals("")) && (!cardIdStr.equals("null"))) {

                        // будем хранить ссылку на исполнителя
                        MyExecutor executor = null;

                        // если JSONObject содержит параметр "user_id"
                        if(executorCardJSONObj.has("user_id")) {

                            String executorIdStr    = executorCardJSONObj.getString("user_id");
                            String executorName     = "";

                            // если JSONObject содержит параметр "display_name"
                            if (executorCardJSONObj.has("display_name"))
                                // получаем значение
                                executorName = executorCardJSONObj.getString("display_name");

                            // создаем исполнителя
                            executor = new MyExecutor(executorIdStr, executorName);

                            // если JSONObject содержит параметр "description"
                            if (executorCardJSONObj.has("description"))
                                // сохраняем значение
                                executor.setDescription(executorCardJSONObj.getString("description"));
                        }

                        ////////////////////////////////////////////////////////////////////////

                        // будем хранить ссылку на менеджера
                        MyManager manager = null;

                        // если JSONObject содержит параметр "manager_name"
                        if(executorCardJSONObj.has("manager_name")) {

                            String managerName = executorCardJSONObj.getString("manager_name");

                            if((managerName != null) && (!managerName.equals("")) && (!managerName.equals("null"))) {

                                // создаем исполнителя
                                manager = new MyManager(managerName);

                                // если JSONObject содержит параметр "photo_manager_link"
                                if (executorCardJSONObj.has("photo_manager_link"))
                                    // получаем значение
                                    manager.setPhotoLink(executorCardJSONObj.getString("photo_manager_link"));

                                // если JSONObject содержит параметр "position_manager"
                                if (executorCardJSONObj.has("position_manager"))
                                    // получаем значение
                                    manager.setPosition(executorCardJSONObj.getString("position_manager"));
                            }
                        }

                        ////////////////////////////////////////////////////////////////////////

                        // создаем объект "карточка исполнителя"
                        ExecutorCard executorCard = new ExecutorCard(executor, manager, cardIdStr);

                        // если JSONObject содержит параметр "rate"
                        if (executorCardJSONObj.has("rate"))
                            // сохраняем рейтинг
                            executorCard.setRate(executorCardJSONObj.getString("rate"));

                        /////////////////////////////////////////////////////////////////////////////

                        // будем хранить значение ключа коллекции
                        StringBuilder executorsCardsMapKeySB = new StringBuilder("");

                        // если значение меньше 10
                        if (i < 10)
                            // добавляем впереди 0, для правильной сортировки
                            executorsCardsMapKeySB.append("0");

                        // добавляем значение
                        executorsCardsMapKeySB.append("" + i);

                        /////////////////////////////////////////////////////////////////////////////

                        // добавляем очередную карточку исполнителя в коллекцию
                        executorsCardsMap.put(executorsCardsMapKeySB.toString(), executorCard);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

            // Log.d(LOG_TAG, "HashTag_Activity: onResponseReturn(): Error!");
        }

        // передаем ссылку на коллекцию карточек исполнителей
        MyApp.setAppExecutorsCardsMap(executorsCardsMap);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setSubCategories() {

        //
        LinearLayout.LayoutParams lp_WW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //
        LinearLayout.LayoutParams tagLP_WW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setMargins(tagLP_WW, 10, 5, 0, 0);

        ////////////////////////////////////////////////////////////////////////////////////////////

        //
        hashTagsLL.removeAllViews();

        //
        int borderColorResId = getResources().getIdentifier("@drawable/rounded_rect_root_hash_tag_color_" + MyApp.getSelectedRootHashTagColorId(), null, getPackageName());

        // получаем колллекцию с хеш-тегами
        Map<String, HashTag> hashTagsMap = MyApp.getAppHashTagsMap();

        // получаем все ключи коллекции
        Set<String> hashTagsMapKeysSet = hashTagsMap.keySet();

        // переносим их в список для сортировки
        List<String> hashTagsMapKeysList = new ArrayList<>();

        // если данные получены
        if(hashTagsMapKeysSet != null)
            // кладем значения в список
            hashTagsMapKeysList.addAll(hashTagsMapKeysSet);

        // сортируем полученный список
        Collections.sort(hashTagsMapKeysList);

        // проходим циклом по списку
        for(int i=0; i<hashTagsMapKeysList.size(); i++) {

            // получаем из коллекции очередной хеш-тег
            final HashTag hashTag = hashTagsMap.get(hashTagsMapKeysList.get(i));

            // создаем очередное текстовое представление с названием хеш-тега
            TextView hashTagNameTV = new TextView(context);
            hashTagNameTV.setLayoutParams(lp_WW);
            hashTagNameTV.setGravity(Gravity.CENTER);
            hashTagNameTV.setTextSize(10);
            hashTagNameTV.setTypeface(Typeface.DEFAULT_BOLD);
            hashTagNameTV.setTextColor(Color.BLACK);
            hashTagNameTV.setText("# " + hashTag.getName());

            // создаем контейнер
            final LinearLayout hashTagLL = new LinearLayout(context);
            hashTagLL.setLayoutParams(tagLP_WW);
            hashTagLL.setBackgroundResource(borderColorResId);
            setPaddings(hashTagLL, 15, 15, 15, 15);

            // добавляем текстовое представление в контейнер
            hashTagLL.addView(hashTagNameTV);

            // задаем обработчик щелчка по хеш тегу
            hashTagLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // получаем исполнителей, связанных с выбранным хеш-тегом
                    getExecutors(hashTag);
                }
            });

            // добавляем очередной хеш тег в "контейнер хеш тегов"
            hashTagsLL.addView(hashTagLL);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void moveForward() {

        // переходим на экран "Исполнители"
        Intent intent = new Intent(context, ExecutorsList_Activity.class);
        startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setMargins(LinearLayout.LayoutParams layout,int left, int top, int right, int bottom) {

        int marginLeft     = (int)(left * density);
        int marginTop      = (int)(top * density);
        int marginRight    = (int)(right * density);
        int marginBottom   = (int)(bottom * density);

        layout.setMargins(marginLeft, marginTop, marginRight, marginBottom);
    }

    //
    private void setPaddings(View view, int left, int top, int right, int bottom) {

        float density = context.getResources().getDisplayMetrics().density;

        int paddingLeft     = (int)(left * density);
        int paddingTop      = (int)(top * density);
        int paddingRight    = (int)(right * density);
        int paddingBottom   = (int)(bottom * density);

        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }
}