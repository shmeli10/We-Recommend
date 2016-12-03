package ru.for_inform.we_recommend.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ru.for_inform.we_recommend.R;
import ru.for_inform.we_recommend.model.MyApp;
import ru.for_inform.we_recommend.model.MyUser;
import ru.for_inform.we_recommend.model.PhoneBook_Contact;
import ru.for_inform.we_recommend.model.RootHashTag;
import ru.for_inform.we_recommend.model.MyRequest;
import ru.for_inform.we_recommend.model.TagColor;

/**
 * Created by OS1 on 29.04.2016.
 */
public class Loading_Activity   extends     Activity
                                implements  View.OnClickListener,
                                            MyRequest.OnResponseReturnListener,
                                            ActivityCompat.OnRequestPermissionsResultCallback {

    private Context context;

    private SharedPreferences shPref;
    private MyRequest myRequest;

    private final int notAgreeTVResId = R.id.LicenseDialog_NotAgreeTV;
    private final int agreeTVResId    = R.id.LicenseDialog_AgreeTV;

    private TextView notAgreeTV;
    private TextView agreeTV;

    private AlertDialog.Builder errorDialog;

    private Dialog licenseDialog;

    private Handler handler;

    private LoadingTask loadingTask;

    private final int AUTHORIZE     = 2;
    private final int SHOW_LICENSE  = 3;
    private final int APP_ERROR     = 4;

    private final int REQUEST_CODE_ASK_PERMISSIONS = 115;

    // private final String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.loading_layout);

        //////////////////////////////////////////////////////////////////////////////////

        // получаем контекст
        context = this;

        //////////////////////////////////////////////////////////////////////////////////

        // определить переменную для работы с Preferences
        shPref = getSharedPreferences("user_data", MODE_PRIVATE);

        // подгрузить данные из Preferences
        loadTextFromPreferences();

        //////////////////////////////////////////////////////////////////////////////////

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case AUTHORIZE:
                                        // обращаемся к серверу с запросом авторизации
                                        authorize();

                                        break;
                    case SHOW_LICENSE:
                                        // показать "диалоговое окно с лицензионным соглашением"
                                        showLicenseDialog();

                                        break;
                    case APP_ERROR:
                                        // возникла критическая ошибка, закрываем приложение
                                        showErrorDialog(R.string.get_user_id_error, true);

                                        break;
                }
            }
        };

        //////////////////////////////////////////////////////////////////////////////////

        // формируем объект для общения с сервером
        myRequest = new MyRequest(context);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadingTask = new LoadingTask();
        loadingTask.execute();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            // если нажата кнопка "Не согласен"
            case notAgreeTVResId:

                                    // сохраням значение
                                    MyUser.setAgreeWithAppLicense(false);

                                    // сохраняем значение в Preferences
                                    saveTextInPreferences("user_license_agree", "N");

                                    // меняем фон текствью и цвета текста в нем
                                    notAgreeTV.setBackgroundResource(R.color.light_brown);
                                    notAgreeTV.setTextColor(getResources().getColor(R.color.white));

                                    // закрываем "диалоговое окно с лицензионным соглашением"
                                    closeLicenseDialog();

                                    // закрываем приложение
                                    finish();

                                    break;
            // если нажата кнопка "Cогласен"
            case agreeTVResId:

                                    // сохраням значение
                                    MyUser.setAgreeWithAppLicense(true);

                                    // сохраняем значение в Preferences
                                    saveTextInPreferences("user_license_agree", "Y");

                                    // меняем фон текствью и цвета текста в нем
                                    agreeTV.setBackgroundResource(R.color.light_brown);
                                    agreeTV.setTextColor(getResources().getColor(R.color.white));

                                    // закрываем "диалоговое окно с лицензионным соглашением"
                                    closeLicenseDialog();

                                    // создаем нового пользователя
                                    createUser();

                                    break;
        }
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            // это ответ на запрос доступа
            case REQUEST_CODE_ASK_PERMISSIONS:
                                                // если доступ дан
                                                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                                                    // обращаемся к телефонной книге пользователя и задаем ему полученные данные контактов
                                                    setUserContacts();

                                                    // получаем корневые хеш-теги
                                                    getRootTags();
                                                }
                                                // если в доступе отказано
                                                else
                                                    // получаем корневые хеш-теги
                                                    getRootTags();

                                                break;

            default:                            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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

                    JSONObject dataObject   = null;
                    JSONArray  dataArray    = null;

                    // если "тип ответа" получен
                    if((responseType != null) && (!responseType.equals(""))) {

                        // переменная для сообщения сервера
                        String message = "";

                        // если "тип ответа" - JSONObject
                        if(responseType.equals("JSONObject")) {

                            // если ответ сервера содержит параметр "data"
                            if (serverResponse.has("data")) {

                                // получаем из него JSONObject
                                dataObject = serverResponse.getJSONObject("data");

                                // если JSONObject содержит параметр "message"
                                if((dataObject != null) && (dataObject.has("message")))
                                    // получаем из него значение
                                    message = dataObject.getString("message");
                            }
                        }
                        // если "тип ответа" - JSONArray
                        else if(responseType.equals("JSONArray")) {

                            // если ответ сервера содержит параметр "data"
                            if (serverResponse.has("data"))
                                // получаем из него JSONArray
                                dataArray = serverResponse.getJSONArray("data");
                        }

                        ///////////////////////////////////////////////////////////////////////////////

                        // если "тип запроса" - "Авторизация"
                        if (requestType.equals("authorize")) {

                            // если сообщение из ответа сервера получено
                            if ((message != null) && (!message.equals(""))) {

                                // если это сообщение - "Пользователь найден"
                                if (message.equals(getResources().getString(R.string.authorize_success))) {

                                    // если это авторизация нового пользователя
                                    if (MyUser.getId().equals("")) {

                                        // если JSONObject содержит параметр "id"
                                        if (dataObject.has("id")) {

                                            // получаем из него значение
                                            String userIdStr = dataObject.getString("id");

                                            // если идентификатор задан
                                            if(userIdStr != null) {

                                                // сохраняем значение
                                                MyUser.setId(userIdStr);

                                                // сохраняем полученное значение в Preferences
                                                saveTextInPreferences("user_id", userIdStr);
                                            }
                                        }
                                        // если JSONObject НЕ содержит параметр "id"
                                        else
                                            // критическая ошибка, закрываем приложение
                                            showErrorDialog(R.string.server_error, true);
                                    }

                                    // если JSONObject содержит параметр "access_token"
                                    if (dataObject.has("access_token")) {

                                        // получаем из него значение
                                        String user_access_token = dataObject.getString("access_token");

                                        // сохраняем значение
                                        MyUser.setAccessToken(user_access_token);

                                        // сохраняем полученное значение в Preferences
                                        saveTextInPreferences("user_access_token", user_access_token);

                                        ////////////////////////////////////////////////////////////

                                        // получаем доступ к телефонной книге, если его нет и читаем контакты
                                        grantToReadContacts();
                                    }
                                    // если JSONObject НЕ содержит параметр "access_token"
                                    else
                                        // ошибка сервера, закрываем приложение
                                        showErrorDialog(R.string.server_error, true);
                                }
                                // если это сообщение - "Пользователь не найден"
                                else if (message.equals(getResources().getString(R.string.authorize_error)))
                                    // ошибка сервера, закрываем приложение
                                    showErrorDialog(R.string.get_user_id_error, true);
                                // если это неизвестное сообщение
                                else
                                    // возникла критическая ошибка, закрываем приложение
                                    showErrorDialog(R.string.server_error, true);
                            }
                            // если сообщение из ответа сервера НЕ получено
                            else
                                // возникла критическая ошибка, закрываем приложение
                                showErrorDialog(R.string.server_error, true);
                        }
                        // если "тип запроса" - "Теги"
                        else if (requestType.equals("rootTags")) {

                            // если JSONArray задан
                            if((dataArray != null)) {

                                // получаем кол-во корневых хеш-тегов в JSONArray
                                int rootHashTagsSum = dataArray.length();

                                // если JSONArray не пустой
                                if(rootHashTagsSum > 0) {

                                    // будем накапливать данные по тегам в коллекции
                                    Map<String, RootHashTag> rootHashTagsMap = new HashMap<>();

                                    // проходим циклом по JSONArray
                                    for(int i=0; i<rootHashTagsSum; i++) {

                                        // получаем очередной элемент из dataArray
                                        JSONObject rootHashTagJSONObj = (JSONObject) dataArray.get(i);

                                        // получаем идентификатор и наименование очередного корневого хеш-тега
                                        String rootHashTagIdStr     = rootHashTagJSONObj.getString("id");
                                        String rootHashTagNameStr   = rootHashTagJSONObj.getString("name");

                                        ////////////////////////////////////////////////////////////

                                        // создаем объект "цвет тега"
                                        TagColor tagColor = new TagColor(i,MyApp.getRootTagBgColor(i));

                                        ////////////////////////////////////////////////////////////

                                        // если значения заданы
                                        if((rootHashTagIdStr != null) && (rootHashTagNameStr != null)) {

                                            // если значения не пустые
                                            if((!rootHashTagIdStr.equals("")) && (!rootHashTagNameStr.equals(""))) {

                                                // создаем очередной объект "корневой хеш-тег"
                                                RootHashTag rootHashTag = new RootHashTag(rootHashTagIdStr, rootHashTagNameStr, tagColor);

                                                /////////////////////////////////////////////////////////////////////////////

                                                // будем хранить значение ключа коллекции
                                                StringBuilder rootHashTagsMapKeySB = new StringBuilder("");

                                                // если значение меньше 10
                                                if(i < 10)
                                                    // добавляем впереди 0, для правильной сортировки
                                                    rootHashTagsMapKeySB.append("0");

                                                // добавляем значение
                                                rootHashTagsMapKeySB.append("" +i);

                                                /////////////////////////////////////////////////////////////////////////////

                                                // добавляем очередной корневой хеш-тег в коллекцию
                                                rootHashTagsMap.put(rootHashTagsMapKeySB.toString(), rootHashTag);
                                            }
                                        }
                                    }

                                    // передаем ссылку на коллекцию корневых тегов
                                    MyApp.setAppRootHashTagsMap(rootHashTagsMap);

                                    // если коллекция с тегами не пустая
                                    if(!MyApp.isAppRootHashTagsMapEmpty())
                                        // переходим на "Главную страницу"
                                        moveForward();
                                    // если коллекция с тегами пустая
                                    else
                                        // критическая ошибка, закрываем приложение
                                        showErrorDialog(R.string.server_error, true);
                                }
                                // если JSONArray пустой
                                else
                                    // критическая ошибка, закрываем приложение
                                    showErrorDialog(R.string.server_error, true);
                            }
                            // если JSONArray не задан
                            else
                                // критическая ошибка, закрываем приложение
                                showErrorDialog(R.string.server_error, true);
                        }
                        // если "тип запроса" - "Регистрация"
                        else if (requestType.equals("signup")) {

                            // если сообщение из ответа сервера получено
                            if ((message != null) && (!message.equals(""))) {

                                // если это сообщение - "Регистрация прошла успешно."
                                if (message.equals(getResources().getString(R.string.signup_success))) {

                                    // если JSONObject задан
                                    if((dataObject != null)) {

                                        // если JSONObject содержит параметр "access_token"
                                        if(dataObject.has("access_token")) {

                                            // кладем его значение в переменную
                                            MyUser.setAccessToken(dataObject.getString("access_token"));

                                            // авторизуемся
                                            authorize();
                                        }
                                        // если JSONObject НЕ содержит параметр "access_token"
                                        else
                                            // критическая ошибка, закрываем приложение
                                            showErrorDialog(R.string.server_error, true);
                                    }
                                    // если JSONObject не задан
                                    else
                                        // критическая ошибка, закрываем приложение
                                        showErrorDialog(R.string.server_error, true);
                                }
                                // если это неизвестное сообщение
                                else
                                    // критическая ошибка, закрываем приложение
                                    showErrorDialog(R.string.server_error, true);
                            }
                            // если сообщение из ответа сервера НЕ получено
                            else
                                // возникла критическая ошибка, закрываем приложение
                                showErrorDialog(R.string.server_error, true);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

                // возникла критическая ошибка, закрываем приложение
                showErrorDialog(R.string.server_error, true);
            }
        }
        else
            // возникла критическая ошибка, закрываем приложение
            showErrorDialog(R.string.server_error, true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void authorize() {

        // формируем параметры запроса к серверу
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("accessToken", MyUser.getAccessToken());

        // отправляем запрос авторизации на сервер
        MyApp.sendRequest(myRequest, Request.Method.POST, "site/login-mobile", null, "authorize", null, requestBody);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void showLicenseDialog() {

        // создаем "диалоговое окно для лицензионного соглашения"
        licenseDialog = new Dialog(context, R.style.InfoDialog_Theme);
        licenseDialog.setContentView(R.layout.license_dialog_layout);
        licenseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // задаем реакцию на нажатие кнопки "Не согласен"
        notAgreeTV = (TextView) licenseDialog.findViewById(notAgreeTVResId);
        notAgreeTV.setOnClickListener(Loading_Activity.this);

        // задаем реакцию на нажатие кнопки "Согласен"
        agreeTV    = (TextView) licenseDialog.findViewById(agreeTVResId);
        agreeTV.setOnClickListener(Loading_Activity.this);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // находим "представление с прокруткой"
        final ScrollView scrollViewSV = (ScrollView) licenseDialog.findViewById(R.id.LicenseDialog_ScrollViewSV);

        // создаем текстовое представление лицензионного соглашения
        TextView licenseTextTV = new TextView(context);
        licenseTextTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        licenseTextTV.setTextSize(8);
        licenseTextTV.setTextColor(context.getResources().getColor(R.color.black));
        licenseTextTV.setText(getAssetData(R.string.license_path));
        setPaddings(licenseTextTV, 10, 0, 10, 0);

        // добавляем текстовое представление в прокрутку
        scrollViewSV.addView(licenseTextTV);

        // показываем сформированное диалоговое окно
        licenseDialog.show();
    }

    // показывем диалоговое окно с ошибкой
    private void showErrorDialog(int resId, final boolean closeApp) {

        // создаем окно для вывода ошибки
        errorDialog = new AlertDialog.Builder(context);

        // формируем заголовок и текст сообщения в окне
        errorDialog.setTitle(getResources().getString(R.string.error));                         // заголовок
        errorDialog.setMessage(getResources().getString(resId)); // сообщение
        errorDialog.setPositiveButton(context.getResources().getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                // если это критическая ошибка
                if (closeApp)
                    // закрываем приложение
                    finish();
            }
        });

        errorDialog.setCancelable(true);
        errorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });

        // показываем окно
        errorDialog.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // получаем текст лицензионного соглашения из файла
    private String getAssetData(int resId) {

        // будем хранить текст лицензионного соглашения
        String str_data = new String();

        byte[] buffer = null;

        InputStream is;

        try {

            // из потока получаем информацию, считанную из файла
            is = getAssets().open(context.getResources().getString(resId));

            // получаем размер информации для стения
            int size = is.available();

            // формируем массив байтов нужного размера
            buffer = new byte[size];

            // записываем данные в массив байтов
            is.read(buffer);

            // закрываем поток
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // если данные считаны
        if(buffer != null)
            // формируем из них строковое значение
            str_data = new String(buffer);

        // возвращаем полученные данные
        return str_data;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void closeLicenseDialog() {

        // если "диалоговое окно с лицензионным соглашением" открыто
        if(licenseDialog != null)
            // закрыть его
            licenseDialog.dismiss();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void moveForward() {

        // переходим на экран "Главная страница"
        Intent intent = new Intent(Loading_Activity.this, RootHashTag_Activity.class);
        startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void createUser() {

        // формируем параметры запроса к серверу
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("signupToken", getResources().getString(R.string.signup_token));

        // отправляем запрос на сервер
        MyApp.sendRequest(myRequest, Request.Method.POST, "site/signup-mobile", null, "signup", null, requestBody);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void sendContacts() {

        // получаем ссылку на коллекцию с контактами
        Map<String, PhoneBook_Contact> contactsMap = MyUser.getContactsMap();

        // получаем все ключи коллекции
        Set<String> contactsMapKeysSet = contactsMap.keySet();

        // переносим их в список для сортировки
        List<String> contactsMapKeysList = new ArrayList<>();

        // если данные получены
        if(contactsMapKeysSet != null)
            // кладем значения в список
            contactsMapKeysList.addAll(contactsMapKeysSet);

        // сортируем полученный список
        Collections.sort(contactsMapKeysList);

        ////////////////////////////////////////////////////////////////////////////////////////

        // формируем параметры запроса к серверу
        Map<String, String> requestBody = new HashMap<>();

        // проходим циклом по списку
        for(int i=0; i<contactsMapKeysList.size(); i++) {

            // получаем из коллекции очередной контакт
            PhoneBook_Contact contact = contactsMap.get(contactsMapKeysList.get(i));

            String phone = contact.getPhoneNumber();
            String phoneCrypted = contact.getPhoneNumberCrypted();

            // формируем параметр в теле запроса, с массивом полученных телефонов
            // requestBody.put("phones[" + i + "]", phonesList.get(i));
            requestBody.put("phones[" + i + "]", phone);

            Log.d("myLogs", "{" +i+ "} - phone= " + phone + ", phoneCrypted= " + phoneCrypted);
        }

        ////////////////////////////////////////////////////////////////////////////////

        // отправляем запрос на сервер
        MyApp.sendRequest(myRequest, Request.Method.POST, "site/sync-mobile-phones", "?", "synchronization", new String[]{"token=" + MyUser.getAccessToken()}, requestBody);
    }

    //
    private void grantToReadContacts() {

        // если версия ОС Андроид 23 и больше (Marshmallow+)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            // проверяем предоставлен ли ранее доступ к телефонной книге (чтение)
            int hasReadContactsPermission = ContextCompat.checkSelfPermission(Loading_Activity.this, Manifest.permission.READ_CONTACTS);

            // если доступа нет
            if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {

                // отправлям запрос на предоставление такого доступа
                ActivityCompat.requestPermissions(  Loading_Activity.this,
                                                    new String[] {Manifest.permission.READ_CONTACTS},
                                                    REQUEST_CODE_ASK_PERMISSIONS
                );
            }
        }
        // если версия ОС Андроид меньше чем 23 (Pre-Marshmallow)
        else {
            // обращаемся к телефонной книге пользователя и задаем ему полученные данные контактов
            setUserContacts();

            // получаем корневые хеш-теги
            getRootTags();
        }
    }

    //
    private void setUserContacts() {

        try {

            // получаем курсор с данными
            Cursor cursor = getContacts();

            // если курсор получен
            if (cursor != null) {

                // если курсор содержит данные
                if (cursor.getCount() > 0) {

                    // будем накапливать полученные контакты в коллекции
                    Map<String, PhoneBook_Contact> contactsMap = new HashMap<>();

                    // будем хранить значение для ключа коллекции
                    int contactMapKey = 0;

                    // наполняем коллекцию данными
                    while (cursor.moveToNext()) {

                        // получаем имя очередного контакта
                        String name = cursor.getString(1);

                        // получаем номер телефона очередного контакта
                        String phone = cursor.getString(2);

                        // если имя контакта получено
                        if ((name != null) && (!name.equals("")) && (phone != null) && (!phone.equals(""))) {

                            // если символ в строке есть
                            if (phone.indexOf("+") != -1)
                                // удаляем его
                                phone = phone.replace("+", "");

                            // если символ в строке есть
                            if (phone.indexOf("(") != -1)
                                // удаляем его
                                phone = phone.replace("(", "");

                            // если символ в строке есть
                            if (phone.indexOf(")") != -1)
                                // удаляем его
                                phone = phone.replace(")", "");

                            // если символ в строке есть
                            if (phone.indexOf("-") != -1)
                                // удаляем его
                                phone = phone.replace("-", "");

                            // если символ в строке есть
                            if (phone.indexOf(":") != -1)
                                // удаляем его
                                phone = phone.replace(":", "");

                            // если символ в строке есть
                            if (phone.indexOf("/") != -1)
                                // удаляем его
                                phone = phone.replace("/", "");

                            // если символ в строке есть
                            if (phone.indexOf(",") != -1)
                                // удаляем его
                                phone = phone.replace(",", "");

                            // если символ в строке есть
                            if (phone.indexOf(" ") != -1)
                                // удаляем его
                                phone = phone.replace(" ", "");

                            ////////////////////////////////////////////////////////////////////

                            // берем только последние 10 цифр номера телефона
                            int startPos = (phone.length() - 10);
                            int endPos = phone.length();

                            String phoneNumber = phone.substring(startPos, endPos);

                            // создаем очередной объект "контакт"
                            // PhoneBook_Contact userContact = new PhoneBook_Contact(phone.substring(startPos, endPos), name);
                            PhoneBook_Contact userContact = new PhoneBook_Contact(phoneNumber, MyApp.getCrypted(phoneNumber), name);

                            ////////////////////////////////////////////////////////////////////

                            // формируем значение очередного ключа коллекции
                            StringBuilder contactMapKeySB = new StringBuilder("");

                            // если текущее значение ключа меньше 10
                            if(contactMapKey < 10)
                                // добавляем к нему значение 0, чтобы правильно работала сортировка
                                contactMapKeySB.append("0");

                            // добавляем значение ключа
                            contactMapKeySB.append(contactMapKey);

                            // кладем очередной контакт в коллекцию
                            contactsMap.put(contactMapKeySB.toString(), userContact);

                            // увеличиваем значение ключа на 1
                            contactMapKey++;
                        }
                    }

                    // отдаем ссылку на сформированную коллекцию
                    MyUser.setContactsMap(contactsMap);

                    // если коллекция содержит данные контактов из телефонной книги
                    if (!MyUser.isContactsMapEmpty())
                        // отправляем их на сервер
                        sendContacts();
                }
            }
        }
        catch(Exception exc) {

            //
            showErrorDialog(R.string.no_contacts_list_access, false);
        }
    }

    //
    private Cursor getContacts() {

        Cursor cursor = null;

        try {
            // читаем контакты из телефонной книги телефона
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                                ContactsContract.CommonDataKinds.Phone.NUMBER},
                                                null,
                                                null,
                                                null);
            startManagingCursor(cursor);
        }
        catch(Exception exc) {
            //
            showErrorDialog(R.string.no_contacts_list_access, false);
        }

        //
        return cursor;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void getRootTags() {

        // отправляем запрос на получение корневых хеш-тегов (они не подчинены никакому другому тегу)
        MyApp.sendRequest(myRequest, Request.Method.GET, "tags", "?", "rootTags", new String[]{"tag_id=null"}, null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setPaddings(View view, int left, int top, int right, int bottom) {

        float density = context.getResources().getDisplayMetrics().density;

        int paddingLeft     = (int)(left * density);
        int paddingTop      = (int)(top * density);
        int paddingRight    = (int)(right * density);
        int paddingBottom   = (int)(bottom * density);

        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * сохранение заданных значений в Preferences
     * @param field - поле
     * @param value - значение
     */
    private void saveTextInPreferences(String field, String value) {
        SharedPreferences.Editor ed = shPref.edit();
        ed.putString(field, value);
        ed.commit();
    }

    /**
     * загрузка сохраненных значений из Preferences
     */
    private void loadTextFromPreferences() {

        // если Preferences содержат параметр
        if (shPref.contains("user_id"))
            // получить его значение
            MyUser.setId(shPref.getString("user_id", ""));

        // если Preferences содержат параметр
        if (shPref.contains("user_access_token"))
            // получить его значение
            MyUser.setAccessToken(shPref.getString("user_access_token", ""));

        // если Preferences содержат параметр
        if (shPref.contains("user_license_agree")) {
            // получить его значение
            String userAnswer = shPref.getString("user_license_agree", "");

            // если ранее пользователь согласился с лицензионным соглашением
            if(userAnswer.equals("Y"))
                // фиксируем это
                MyUser.setAgreeWithAppLicense(true);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    class LoadingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {

                // "уснули" на 5 секунд
                TimeUnit.SECONDS.sleep(5);

                // если идентификатор пользователя определен
                if(!MyUser.getId().equals(""))
                    // отпрвляем сообщение "Авторизация"
                    handler.sendEmptyMessage(AUTHORIZE);
                // если идентификатор пользователя НЕ определен
                else {

                    // если пользователь еще не дал согласия
                    if(!MyUser.isAgreeWithAppLicense())
                        // отпрвляем сообщение "Авторизация"
                        handler.sendEmptyMessage(SHOW_LICENSE);
                }
            }
            catch(Exception exc) {
                exc.printStackTrace();

                //
                handler.sendEmptyMessage(APP_ERROR);
            }

            return null;
        }
    }
}