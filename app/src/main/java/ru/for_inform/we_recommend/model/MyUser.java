package ru.for_inform.we_recommend.model;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by OS1 on 30.06.2016.
 */
public class MyUser {

    private static String id            = "";
    private static String accessToken   = "";

    private static boolean isAgreeWithAppLicense;

    private static Map<String, PhoneBook_Contact> contactsMap = new HashMap<>();

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static String getId() {

        return id;
    }

    //
    public static void setId(String userId) {

        // если значение задано
        if(userId != null)
            // кладем его в переменную
            id = userId;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static String getAccessToken() {

        return accessToken;
    }

    //
    public static void setAccessToken(String userAccessToken) {

        // если значение задано
        if(userAccessToken != null)
            // кладем его в переменную
            accessToken = userAccessToken;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static String getContactName(String phoneNumber) {

        String contactName = "";

        // если коллекция не пустая
        if(!isContactsMapEmpty()) {

            // если номер телефона задан
            if (phoneNumber != null) {

                // проходим циклом по коллекции
                for(Map.Entry<String, PhoneBook_Contact> entry: contactsMap.entrySet()) {

                    // получаем очередной контакт
                    PhoneBook_Contact contact = entry.getValue();

                    // если номера телефонов совпадают
                    if (contact.getPhoneNumber().equals(phoneNumber))
                        // получаем имя контакта
                        contactName = contact.getName();
                }
            }
        }

        return contactName;
    }

    //
    public static Map<String, PhoneBook_Contact> getContactsMap() {

        return contactsMap;
    }

    //
    public static boolean isContactsMapEmpty() {

        return contactsMap.isEmpty();
    }

    //
    public static void setContactsMap(Map<String, PhoneBook_Contact> userContactsMap) {

        // если значение задано
        if(userContactsMap != null)
            // кладем его в переменную
            contactsMap = userContactsMap;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    //
    public static boolean isAgreeWithAppLicense() {

        return isAgreeWithAppLicense;
    }

    //
    public static void setAgreeWithAppLicense(boolean userAnswer) {

        // сохраняем значение
        isAgreeWithAppLicense = userAnswer;
    }
}