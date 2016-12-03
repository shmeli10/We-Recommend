package ru.for_inform.we_recommend.model;

/**
 * Created by OS1 on 04.07.2016.
 */
public class PhoneBook_Contact {

    private String phoneNumber          = "";
    private String phoneNumberCrypted   = "";
    private String name                 = "";

    public PhoneBook_Contact(String phoneNumber, String name) {

        this.phoneNumber = phoneNumber;
        this.name        = name;
    }

    public PhoneBook_Contact(String phoneNumber, String phoneNumberCrypted, String name) {

        this(phoneNumber, name);
        this.phoneNumberCrypted = phoneNumberCrypted;
    }

    //////////////////////////////////////////////////////////

    //
    public String getPhoneNumber() {

        return phoneNumber;
    }

    //
    public String getPhoneNumberCrypted() {

        return phoneNumberCrypted;
    }

    //////////////////////////////////////////////////////////

    //
    public String getName() {

        return name;
    }
}