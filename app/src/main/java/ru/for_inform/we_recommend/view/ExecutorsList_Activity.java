package ru.for_inform.we_recommend.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.NetworkImageView;

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
import ru.for_inform.we_recommend.model.MyApp;
import ru.for_inform.we_recommend.model.MyExecutor;
import ru.for_inform.we_recommend.model.MyManager;
import ru.for_inform.we_recommend.model.MyReview;
import ru.for_inform.we_recommend.model.MyService;
import ru.for_inform.we_recommend.model.MyUser;
import ru.for_inform.we_recommend.model.MyRequest;

/**
 * Created by OS1 on 11.06.2016.
 */
public class ExecutorsList_Activity     extends     Activity
                                        implements  View.OnClickListener,
                                                    MyRequest.OnResponseReturnListener {

    private Context      context;
    private MyRequest    myRequest;

    private ImageView    backIV;
    private TextView     headerTitleTV;
    private LinearLayout executorsLL;

    private final int backIVResId           = R.id.ExecutorsList_BackIV;
    private final int headerTitleTVResId    = R.id.ExecutorsList_HeaderTitleTV;
    private final int executorsLLResId      = R.id.ExecutorsList_ExecutorsLL;

    private float density;

    // private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.executors_list_layout);

        //////////////////////////////////////////////////////////////////////////////////

        backIV          = (ImageView) findViewById(backIVResId);
        headerTitleTV   = (TextView) findViewById(headerTitleTVResId);
        headerTitleTV.setTypeface(MyApp.getBebasNeueBoldTypeface());

        executorsLL     = (LinearLayout) findViewById(executorsLLResId);

        //////////////////////////////////////////////////////////////////////////////////

        backIV.setOnClickListener(this);

        //////////////////////////////////////////////////////////////////////////////////

        // получаем контекст
        context = this;
        density = context.getResources().getDisplayMetrics().density;

        // задаем контекст для загрузчика изображений
        MyApp.setAppImageLoader(context);

        //////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        myRequest = new MyRequest(context);

        //////////////////////////////////////////////////////////////////////////////////

        // задаем заголовок экрана
        setTitle();

        // задаем список исполнителей
        setExecutorsCardsList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {

            case backIVResId:
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

                    JSONObject dataObject   = null;
                    JSONArray dataArray     = null;

                    // если "тип ответа" получен
                    if ((responseType != null) && (!responseType.equals(""))) {

                        // если "тип ответа" - JSONObject
                        if(responseType.equals("JSONObject")) {

                            // если ответ сервера содержит параметр "data"
                            if (serverResponse.has("data"))

                                // получаем из него JSONObject
                                dataObject = serverResponse.getJSONObject("data");
                        }
                        // если "тип ответа" - JSONArray
                        else if(responseType.equals("JSONArray")) {

                            // если ответ сервера содержит параметр "data"
                            if (serverResponse.has("data"))
                                // получаем из него JSONArray
                                dataArray = serverResponse.getJSONArray("data");
                        }

                        ///////////////////////////////////////////////////////////////////////////////

                        // если "тип запроса" - "Исполнитель"
                        if (requestType.equals("selectedExecutor"))
                            // размещаем полученные данные карточки исполнителя на экране
                            setSelectedExecutorCardData(dataObject);
                        // если "тип запроса" - "Теги исполнителя"
                        else if (requestType.equals("selectedExecutorTags"))
                            // размещаем полученные данные о тегах в карточке исполнителя на экране
                            setSelectedExecutorTagsData(dataObject, dataArray);
                        // если "тип запроса" - "Цены"
                        else if(requestType.equals("selectedExecutorServices"))
                            // размещаем полученные данные о тегах в карточке исполнителя на экране
                            setSelectedExecutorServicesData(dataArray);
                        else if(requestType.equals("selectedExecutorReviews"))
                            // размещаем полученные данные отзывов об исполнителе на экране
                            setSelectedExecutorReviewsData(dataArray);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

                // Log.d(LOG_TAG, "ExecutorsList_Activity: onResponseReturn(): Error!");
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    //
    private void setTitle() {

        // задаем значение заголовку
        headerTitleTV.setText(MyApp.getSelectedHashTagShortName().toUpperCase());
    }

    //
    private void setExecutorsCardsList() {

        // чистим контейнер от прежних значений
        executorsLL.removeAllViews();

        // получаем колллекцию с карточками исполнителей
        Map<String, ExecutorCard> executorsCardsMap = MyApp.getAppExecutorsCardsMap();

        // если коллекция не пустая
        if(!MyApp.isAppExecutorsCardsMapEmpty()) {

            // получаем все ключи коллекции
            Set<String> executorsCardsMapKeysSet = executorsCardsMap.keySet();

            // переносим их в список для сортировки
            List<String> executorsCardsMapKeysList = new ArrayList<>();

            // если данные получены
            if(executorsCardsMapKeysSet != null)
                // кладем значения в список
                executorsCardsMapKeysList.addAll(executorsCardsMapKeysSet);

            // сортируем полученный список
            Collections.sort(executorsCardsMapKeysList);

            // проходим циклом по списку
            for(int i=0; i<executorsCardsMapKeysList.size(); i++) {

                // получаем из коллекции очередной хеш-тег
                final ExecutorCard executorCard = executorsCardsMap.get(executorsCardsMapKeysList.get(i));

                // получаем исполнителя
                MyExecutor executor = executorCard.getExecutor();

                //////////////////////////////////////////////////////////////////

                // получаем "заготовку" карточки исполнителя
                View executorRowView = LayoutInflater.from(context).inflate(R.layout.executor_row_layout, null);

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                LinearLayout executorsContainerLL = (LinearLayout) executorRowView.findViewById(R.id.ExecutorRow_ContainerLL);

                LinearLayout.LayoutParams executorRowLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                setMargins(executorRowLP, 5, 0, 5, 5);
                executorsContainerLL.setLayoutParams(executorRowLP);

                executorsContainerLL.setOrientation(LinearLayout.VERTICAL);
                executorsContainerLL.setBackgroundResource(R.drawable.rounded_rect_with_grey_stroke);
                setPaddings(executorsContainerLL, 10, 10, 10, 10);

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // создаем представление для изображения исполнителя
                NetworkImageView executorAvatarNIV = (NetworkImageView) executorRowView.findViewById(R.id.ExecutorRow_AvatarNIV);

                // получаем ссылку на менеджера в карточке исполнителя
                MyManager manager = executorCard.getManager();

                // если значение задано
                if(manager != null) {

                    // получаем ссылку на изображение менеджера
                    String managerPhotoLink = manager.getPhotoLink();

                    // если значение получено
                    if(!managerPhotoLink.equals(""))
                        // загружаем изображение
                        executorAvatarNIV.setImageUrl(MyApp.getMediaLinkHead() + managerPhotoLink, MyApp.getAppImageLoader());
                }

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // создаем текстовое представление с наименованием компанмии/ФИО исполнителя
                TextView executorNameTV = (TextView) executorRowView.findViewById(R.id.ExecutorRow_ExecutorNameTV);
                executorNameTV.setText(MyApp.getShort(executor.getName()));

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // создаем текстовое представление с рейтингом исполнителя
                TextView executorRateTV = (TextView) executorRowView.findViewById(R.id.ExecutorRow_ExecutorRateValueTV);
                executorRateTV.setText(executorCard.getRate());

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // создаем текстовое представление с идентификатором исполнителя
                TextView executorIdTV = (TextView) executorRowView.findViewById(R.id.ExecutorRow_ExecutorIdValueTV);
                executorIdTV.setText(executor.getId());

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // создаем кнопку "Выбрать" и обработчик клика по ней
                LinearLayout executorChooseLL = (LinearLayout) executorRowView.findViewById(R.id.ExecutorRow_ExecutorChooseLL);
                executorChooseLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // получаем расширенные данные по выбранной карточке исполнителя
                        getSelectedExecutorCard(executorCard);
                    }
                });

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // создаем текстовое представление с числом отзывом к карточке исполнителя
                TextView executorReviewsSumTV = (TextView) executorRowView.findViewById(R.id.ExecutorRow_ExecutorReviewValueTV);
                executorReviewsSumTV.setText(executorCard.getReviewsSum());

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // создаем текстовое представление с текстом о исполнителе
                TextView executorAboutMeTV = (TextView) executorRowView.findViewById(R.id.ExecutorRow_ExecutorAboutMeValueTV);
                executorAboutMeTV.setText(executor.getDescription());

                // добавляем заполненную "заготовку" карточки исполнителя в "контейнер с карточками исполнителей"
                executorsLL.addView(executorRowView);
            }
        }
        // если коллекция пуста
        else {


        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    //
    private void getSelectedExecutorCard(ExecutorCard selectedExecutorCard) {

        // сохраняем ссылку на выбранную карточку исполнителя
        MyApp.setSelectedExecutorCard(selectedExecutorCard);

        // получаем идентификатор выбранной карточки исполнителя
        String cardIdStr = selectedExecutorCard.getId();

        // формируем массив запросов для получения расширенной информации по выбранной карточке
        String[][] requestsArr = new String[4][];

        requestsArr[0] = new String[]{"executors/" + cardIdStr + "?expand=executor_field",  "selectedExecutor"};
        requestsArr[1] = new String[]{"executors/" + cardIdStr + "/tags",                   "selectedExecutorTags"};
        requestsArr[2] = new String[]{"executors/" + cardIdStr + "/services",               "selectedExecutorServices"};
        requestsArr[3] = new String[]{"executors/" + cardIdStr + "/rewiews",                "selectedExecutorReviews"};

        // проходим циклом по массиву данных для запросов
        for(int i=0; i<requestsArr.length; i++) {

            // получаем данные очередного запроса
            String[] requestDataArr = requestsArr[i];

            // отправляем запрос серверу
            MyApp.sendRequest(myRequest, Request.Method.GET, requestDataArr[0], null, requestDataArr[1], null, null);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setSelectedExecutorCardData(JSONObject dataObject) {

        // получаем ссылку на выбранную карточку исполнителя
        ExecutorCard selectedExecutorCard = MyApp.getSelectedExecutorCard();

        try {

            // если ссылка на выбранную карточку исполнителя задана
            if(selectedExecutorCard != null) {

                // получаем исполнителя
                MyExecutor executor = selectedExecutorCard.getExecutor();

                // если JSONObject содержит параметр "virtual_user_phone"
                if (dataObject.has("virtual_user_phone"))
                    // сохраняем значение
                    executor.setPhone(dataObject.getString("virtual_user_phone"));

                // если JSONObject содержит параметр "virtual_user_email"
                if (dataObject.has("virtual_user_email"))
                    // сохраняем значение
                    executor.setEmail(dataObject.getString("virtual_user_email"));

                ////////////////////////////////////////////////////////////////////////////////////

                // если JSONObject содержит параметр "executor_field"
                if (dataObject.has("executor_field")) {

                    // получаем дополнительные поля
                    JSONArray fieldsJSONArray = dataObject.getJSONArray("executor_field");

                    // будем хранить дополнительные поля
                    Map<String, String[]> сardFieldsMap = new HashMap<>();

                    // проходим циклом по массиву данных
                    for (int i=0; i<fieldsJSONArray.length(); i++) {

                        // получаем данные очередного дополнительного поля
                        JSONObject fieldJSONObject = (JSONObject) fieldsJSONArray.get(i);

                        String fieldNameStr = "";
                        String fieldValueStr = "";

                        // если JSONObject содержит параметр "virtual_field_label"
                        if (fieldJSONObject.has("virtual_field_label"))
                            // получаем значение
                            fieldNameStr = fieldJSONObject.getString("virtual_field_label");

                        // если JSONObject содержит параметр "value"
                        if (fieldJSONObject.has("value"))
                            // получаем значение
                            fieldValueStr = fieldJSONObject.getString("value");

                        // если значения заданы
                        if((fieldNameStr != null) && (fieldValueStr != null)) {

                            // если значения не пустые
                            if((!fieldNameStr.equals("")) && (!fieldNameStr.equals("null")) && (!fieldValueStr.equals("")) && (!fieldValueStr.equals("null"))) {

                                // будем хранить значение ключа коллекции
                                StringBuilder cardsFieldsMapKeySB = new StringBuilder("");

                                // если значение меньше 10
                                if (i < 10)
                                    // добавляем впереди 0, для правильной сортировки
                                    cardsFieldsMapKeySB.append("0");

                                // добавляем значение
                                cardsFieldsMapKeySB.append("" + i);

                                /////////////////////////////////////////////////////////////////////////////

                                // добавляем очередное дополнительное поле в коллекцию
                                сardFieldsMap.put(cardsFieldsMapKeySB.toString(), new String[]{fieldNameStr, fieldValueStr});
                            }
                        }
                    }

                    // сохраняем ссылку на коллекцию с дополнительными полями
                    selectedExecutorCard.setFieldsMap(сardFieldsMap);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

            // Log.d(LOG_TAG, "ExecutorsList_Activity: setSelectedExecutorCardData(): JSONException.");
        }
    }

    //
    private void setSelectedExecutorTagsData(JSONObject dataObject, JSONArray dataArray) {

        try {

            // будем хранить теги исполнителя
            StringBuilder executorTagsSB = new StringBuilder("");

            // если JSONObject задан
            if(dataObject != null) {

                // если JSONObject содержит параметр "name"
                if (dataObject.has("name")) {

                    // получаем из него значение
                    String executorTagStr = dataObject.getString("name");

                    // если тэг задан и содержит значение
                    if((executorTagStr != null) && (!executorTagStr.equals("")) && (!executorTagStr.equals("null"))) {

                        // добавляем в формируемую строку очередной тег
                        executorTagsSB.append("# ");
                        executorTagsSB.append(executorTagStr);
                    }
                }
            }

            // если JSONArray задан
            if(dataArray != null) {

                // проходим циклом по массиву значений
                for(int i=0; i<dataArray.length(); i++) {

                    // получаем очередной тег
                    JSONObject executorTagJSONObj = (JSONObject) dataArray.get(i);

                    // если JSONObject содержит параметр "name"
                    if(executorTagJSONObj.has("name")) {

                        // получаем из него значение
                        String executorTagStr = executorTagJSONObj.getString("name");

                        // если тэг задан и содержит значение
                        if((executorTagStr != null) && (!executorTagStr.equals("")) && (!executorTagStr.equals("null"))) {

                            // если это не первый элемент
                            if(i > 0)
                                // добавляем разделитель
                                executorTagsSB.append(", ");

                            // добавляем в формируемую строку очередной тег
                            executorTagsSB.append("# ");
                            executorTagsSB.append(executorTagStr);
                        }
                    }
                }
            }

            // сохраняем сформированную строку тегов исполнителя
            MyApp.getSelectedExecutorCard().setTags(executorTagsSB.toString());

        } catch (JSONException e) {
            e.printStackTrace();

            // Log.d(LOG_TAG, "ExecutorsList_Activity: setSelectedExecutorTagsData(): JSONException.");
        }
    }

    //
    private void setSelectedExecutorServicesData(JSONArray dataArray) {

        // будем хранить услуги исполнителя
        Map<String, MyService> servicesMap = new HashMap<>();

        try {

            // если массив данных задан
            if(dataArray != null) {

                // проходим циклом по массиву данных
                for(int i=0; i<dataArray.length(); i++) {

                    // получаем очередную услугу
                    JSONObject serviceJSONObj = (JSONObject) dataArray.get(i);

                    ////////////////////////////////////////////////////////////////

                    // если JSONObject содержит параметр "service_id"
                    if(serviceJSONObj.has("service_id")) {

                        // получаем идентификатор услуги
                        String serviceId = serviceJSONObj.getString("service_id");

                        // если идентификатор услуги получен
                        if((serviceId != null) && (!serviceId.equals("")) && (!serviceId.equals("null"))) {

                            String serviceName  = "";
                            String servicePrice = "";
                            String serviceUnit  = "";

                            // если JSONObject содержит параметр "virtual_service_name"
                            if(serviceJSONObj.has("virtual_service_name"))
                                // получаем из него значение
                                serviceName = serviceJSONObj.getString("virtual_service_name");

                            // если JSONObject содержит параметр "price"
                            if(serviceJSONObj.has("price"))
                                // получаем из него значение
                                servicePrice = serviceJSONObj.getString("price");

                            // если JSONObject содержит параметр "virtual_service_unit"
                            if(serviceJSONObj.has("virtual_service_unit"))
                                // получаем из него значение
                                serviceUnit = serviceJSONObj.getString("virtual_service_unit");

                            ////////////////////////////////////////////////////////////////////////

                            // будем хранить значение ключа коллекции
                            StringBuilder servicesMapKeySB = new StringBuilder("");

                            // если значение меньше 10
                            if (i < 10)
                                // добавляем впереди 0, для правильной сортировки
                                servicesMapKeySB.append("0");

                            // добавляем значение
                            servicesMapKeySB.append("" + i);

                            /////////////////////////////////////////////////////////////////////////////

                            // добавляем очередную услугу в коллекцию
                            servicesMap.put(servicesMapKeySB.toString(), new MyService(serviceId, serviceName, servicePrice, serviceUnit));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

            // Log.d(LOG_TAG, "ExecutorsList_Activity: setSelectedExecutorServicesData(): JSONException.");
        }

        // передаем ссылку на коллекцию услуг исполнителя
        MyApp.getSelectedExecutorCard().setServicesMap(servicesMap);
    }

    //
    private void setSelectedExecutorReviewsData(JSONArray dataArray) {

        // будем хранить отзывы
        Map<String, MyReview> reviewsMap = new HashMap<>();

        try {

            // если массив данных задан
            if(dataArray != null) {

                // проходим циклом по массиву данных
                for(int i=0; i<dataArray.length(); i++) {

                    // получаем очередной отзыв
                    JSONObject reviewJSONObj = (JSONObject) dataArray.get(i);

                    ////////////////////////////////////////////////////////////////////////////////

                    String reviewerPhone = "";
                    String reviewerName  = "";
                    String reviewText    = "";

                    // если JSONObject содержит параметр "rewiewer_phone"
                    if(reviewJSONObj.has("rewiewer_phone")) {

                        // получаем из него значение
                        reviewerPhone = reviewJSONObj.getString("rewiewer_phone");

                        // если получен номер телефона оставившего отзыв
                        if((reviewerPhone != null) && (!reviewerPhone.equals("")) && (!reviewerPhone.equals("null"))) {

                            // получаем имя контакта
                            // reviewerName = getReviewerName(reviewerPhoneStr);
                            reviewerName = MyUser.getContactName(reviewerPhone);

                            // если имя пользователя задано
                            if(reviewerName != null) {

                                // если имя пользователя из записной книжки не получено
                                if(reviewerName.equals("")) {

                                    // если JSONObject содержит параметр "rewiewer_name"
                                    if(reviewJSONObj.has("rewiewer_name"))

                                        // получаем из него значение
                                        reviewerName = reviewJSONObj.getString("rewiewer_name");
                                }
                            }
                        }

                        ////////////////////////////////////////////////////////////////////////////

                        // если JSONObject содержит параметр "text"
                        if(reviewJSONObj.has("text"))
                            // получаем из него значение
                            reviewText = reviewJSONObj.getString("text");

                        ////////////////////////////////////////////////////////////////////////////

                        // если значения заданы
                        if((reviewerPhone != null) && (reviewerName != null) && (reviewText != null)) {

                            // будем хранить значение ключа коллекции
                            StringBuilder reviewsMapKeySB = new StringBuilder("");

                            // если значение меньше 10
                            if (i < 10)
                                // добавляем впереди 0, для правильной сортировки
                                reviewsMapKeySB.append("0");

                            // добавляем значение
                            reviewsMapKeySB.append("" + i);

                            /////////////////////////////////////////////////////////////////////////////

                            MyReview review = new MyReview(reviewerPhone, reviewerName, reviewText);

                            // если JSONObject содержит параметр "virtual_rewiew_type_name"
                            if(reviewJSONObj.has("virtual_rewiew_type_name"))
                                // сохраняем значение
                                review.setTypeName(reviewJSONObj.getString("virtual_rewiew_type_name"));

                            // добавляем очередной отзыв в коллекцию
                            reviewsMap.put(reviewsMapKeySB.toString(), review);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

            // Log.d(LOG_TAG, "ExecutorsList_Activity: setExecutorReviewsData(): JSONException.");
        }

        // передаем ссылку на коллекцию услуг исполнителя
        MyApp.getSelectedExecutorCard().setReviewsMap(reviewsMap);

        ////////////////////////////////////////////////////////////////////////////////////////////

        // переходим на экран "Карточка пользователя"
        moveForward();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void moveForward() {

        // переходим на экран "Исполнители"
        Intent intent = new Intent(context, SelectedExecutor_Activity.class);
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