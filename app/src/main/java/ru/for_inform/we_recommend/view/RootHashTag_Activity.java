package ru.for_inform.we_recommend.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import ru.for_inform.we_recommend.model.HashTag;
import ru.for_inform.we_recommend.model.MyApp;
import ru.for_inform.we_recommend.model.MyUser;
import ru.for_inform.we_recommend.model.RootHashTag;
import ru.for_inform.we_recommend.model.MyRequest;

/**
 * Created by os1 on 13.04.2016.
 */
public class RootHashTag_Activity   extends     Activity
                                    implements  MyRequest.OnResponseReturnListener {

    private Context             context;
    private MyRequest           myRequest;
    private AlertDialog.Builder errorDialog;

    private float   density;

    private final int leftRootHashTagsLLResId  = R.id.Main_LeftRootHashTagsLL;
    private final int rightRootHashTagsLLResId = R.id.Main_RightRootHashTagsLL;

    private LinearLayout leftRootHashTagsLL;
    private LinearLayout rightRootHashTagsLL;

    // private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.root_hash_tag_layout);

        //////////////////////////////////////////////////////////////////////////////////

        leftRootHashTagsLL  = (LinearLayout) findViewById(leftRootHashTagsLLResId);
        rightRootHashTagsLL = (LinearLayout) findViewById(rightRootHashTagsLLResId);

        //////////////////////////////////////////////////////////////////////////////////

        // получаем контекст
        context = this;
        density = context.getResources().getDisplayMetrics().density;

        // задаем шрифт для заголовка экрана
        MyApp.setBebasNeueBoldTypeface(getAssets(), getString(R.string.bebas_neue_bold_font));

        //////////////////////////////////////////////////////////////////////////////////

        // получаем количество контактов в колллекции
        int contactsSum = MyUser.getContactsMap().size();

        // если получен хоть один подходящий контакт
        if(contactsSum > 0)
            Toast.makeText(context, "Получено контактов: " + contactsSum, Toast.LENGTH_LONG).show();
        // не получен ни один подходящий контакт
        else
            Toast.makeText(context, "Ни один подходящий контакт не был получен из записной книги.", Toast.LENGTH_LONG).show();

        //////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        myRequest = new MyRequest(context);

        //////////////////////////////////////////////////////////////////////////////////

        //
        setCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //
    public void onResponseReturn(JSONObject serverResponse) {

        // если ответ сервера не пустой
        if (serverResponse != null) {

            try {

                String requestType  = "";
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

                        // если "тип запроса" - "Теги"
                        if (requestType.equals("tags")) {

                            // если JSONArray задан
                            if((dataArray != null)) {

                                // получаем кол-во хеш-тегов в JSONArray
                                int hashTagsSum = dataArray.length();

                                // если JSONArray не пустой
                                if(hashTagsSum > 0) {

                                    // будем накапливать данные по тегам в коллекции
                                    Map<String, HashTag> hashTagsMap = new HashMap<>();

                                    // проходим циклом по JSONArray
                                    for(int i=0; i<hashTagsSum; i++) {

                                        // получаем очередной элемент из dataArray
                                        JSONObject hashTagJSONObj = (JSONObject) dataArray.get(i);

                                        // получаем идентификатор и наименование очередного корневого хеш-тега
                                        String hashTagIdStr     = hashTagJSONObj.getString("id");
                                        String hashTagNameStr   = hashTagJSONObj.getString("name");

                                        ////////////////////////////////////////////////////////////

                                        // если значения заданы
                                        if((hashTagIdStr != null) && (hashTagNameStr != null)) {

                                            // если значения не пустые
                                            if((!hashTagIdStr.equals("")) && (!hashTagNameStr.equals(""))) {

                                                // создаем очередной объект "хеш-тег"
                                                HashTag hashTag = new HashTag(hashTagIdStr, hashTagNameStr);

                                                /////////////////////////////////////////////////////////////////////////////

                                                // будем хранить значение ключа коллекции
                                                StringBuilder hashTagsMapKeySB = new StringBuilder("");

                                                // если значение меньше 10
                                                if(i < 10)
                                                    // добавляем впереди 0, для правильной сортировки
                                                    hashTagsMapKeySB.append("0");

                                                // добавляем значение
                                                hashTagsMapKeySB.append("" +i);

                                                /////////////////////////////////////////////////////////////////////////////

                                                // добавляем очередной хеш-тег в коллекцию
                                                hashTagsMap.put(hashTagsMapKeySB.toString(), hashTag);
                                            }
                                        }
                                    }

                                    // передаем ссылку на коллекцию тегов
                                    MyApp.setAppHashTagsMap(hashTagsMap);

                                    // если коллекция с тегами не пустая
                                    if(!MyApp.isAppHashTagsMapEmpty())
                                        // переходим на экран "Хеш-теги"
                                        moveForward();
                                    // если коллекция с тегами пустая
                                    else
                                        // критическая ошибка, закрываем приложение
                                        showErrorDialog(R.string.no_hash_tags_error, false);
                                }
                                // если JSONArray пустой
                                else
                                    // ошибка, но не закрываем приложение
                                    showErrorDialog(R.string.no_hash_tags_error, false);
                            }
                            // если JSONArray не задан
                            else
                                // ошибка, но не закрываем приложение
                                showErrorDialog(R.string.no_hash_tags_error, false);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

                // Log.d(LOG_TAG, "RootHashTags_Activity: onResponseReturn(): Error!");
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    //
    private void setCategories() {

        // получаем размер экрана
        Display d = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = d.getHeight();

        // задаем размеры для блока категории
        int size = (int) ((height - (75 * density)) / 3);

        // создаем компоновщик
        LinearLayout.LayoutParams lp    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, size);
        LinearLayout.LayoutParams lp_WW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // получаем колллекцию с корневыми хеш-тегами
        Map<String, RootHashTag> rootHashTagsMap = MyApp.getAppRootHashTagsMap();

        // получаем все ключи коллекции
        Set<String> rootHashTagsMapKeysSet = rootHashTagsMap.keySet();

        // переносим их в список для сортировки
        List<String> rootHashTagsMapKeysList = new ArrayList<>();

        // если данные получены
        if(rootHashTagsMapKeysSet != null)
            // кладем значения в список
            rootHashTagsMapKeysList.addAll(rootHashTagsMapKeysSet);

        // сортируем полученный список
        Collections.sort(rootHashTagsMapKeysList);

        // проходим циклом по списку
        for(int i=0; i<rootHashTagsMapKeysList.size(); i++) {

            // получаем из коллекции очередной корневой хеш-тег
            final RootHashTag rootHashTag = rootHashTagsMap.get(rootHashTagsMapKeysList.get(i));

            LinearLayout.LayoutParams rootHashTagImage_lp = new LinearLayout.LayoutParams(110, 110);
            setMargins(rootHashTagImage_lp, 0, 20, 0, 0);

            ////////////////////////////////////////////////////////////////////////////////////

            // формируем изображение
            ImageView rootHashTagImage = new ImageView(context);
            rootHashTagImage.setLayoutParams(rootHashTagImage_lp);
            rootHashTagImage.setImageResource(R.drawable.root_hash_tag_white);

            ////////////////////////////////////////////////////////////////////////////////////

            // формируем тестовое представление с наименовением корневого хеш-тега
            TextView rootHashTagNameTV = new TextView(context);
            rootHashTagNameTV.setLayoutParams(lp_WW);
            rootHashTagNameTV.setGravity(Gravity.CENTER);
            rootHashTagNameTV.setTextSize(18);
            rootHashTagNameTV.setTypeface(MyApp.getBebasNeueBoldTypeface());
            rootHashTagNameTV.setTextColor(context.getResources().getColor(R.color.white));
            rootHashTagNameTV.setText(rootHashTag.getName());

            setPaddings(rootHashTagNameTV, 5, 15, 5, 0);

            ////////////////////////////////////////////////////////////////////////////////////

            // создаем контейнер
            final LinearLayout rootHashTagLL = new LinearLayout(context);
            rootHashTagLL.setLayoutParams(lp);
            rootHashTagLL.setOrientation(LinearLayout.VERTICAL);
            rootHashTagLL.setGravity(Gravity.CENTER_HORIZONTAL);
            rootHashTagLL.setBackgroundColor(Color.parseColor(rootHashTag.getTagColor().getTagColorValue()));

            // задаем обработчик щелчка по категории
            rootHashTagLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // получаем хеш-теги, подчиненные заданному корневому хеш-тегу
                    getHashTags(rootHashTag);
                }
            });

            // добавляем изображение и текстовое представление в контейнер
            rootHashTagLL.addView(rootHashTagImage);
            rootHashTagLL.addView(rootHashTagNameTV);

            // получаем остаток от деления на 2
            switch (i % 2) {

                // если число четное
                case 0:
                        // добавляем элемент в "левый контейнер корневых хеш-тегов"
                        leftRootHashTagsLL.addView(rootHashTagLL);

                        break;
                // если число не четное
                case 1:
                        // добавляем элемент в "правый контейнер корневых хеш-тегов"
                        rightRootHashTagsLL.addView(rootHashTagLL);

                        break;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void getHashTags(RootHashTag selectedRootHashTag) {

        // сохраняем ссылку на выбранный корневой хеш-тег
        MyApp.setSelectedRootHashTag(selectedRootHashTag);

        // отправляем запрос на получение хеш-тегов, подчиненных выбранному корневому хеш-тегу
        MyApp.sendRequest(myRequest, Request.Method.GET, "tags", "?", "tags", new String[]{"tag_id=" +selectedRootHashTag.getId()}, null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void moveForward() {

        // переходим на экран "Хеш-теги"
        Intent intent = new Intent(context, HashTag_Activity.class);
        startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // показывем диалоговое окно с ошибкой
    private void showErrorDialog(int resId, final boolean closeApp) {

        //
        errorDialog = new AlertDialog.Builder(context);

        //
        errorDialog.setTitle(getResources().getString(R.string.error)); // заголовок
        errorDialog.setMessage(getResources().getString(resId));        // сообщение
        errorDialog.setPositiveButton(context.getResources().getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                // если это критическая ошибка
                if(closeApp)
                    // закрываем приложение
                    finish();
            }
        });

        //
        errorDialog.setCancelable(true);

        //
        errorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });

        //
        errorDialog.show();
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