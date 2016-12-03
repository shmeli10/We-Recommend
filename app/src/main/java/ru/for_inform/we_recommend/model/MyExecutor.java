package ru.for_inform.we_recommend.model;

/**
 * Created by OS1 on 08.07.2016.
 */
public class MyExecutor {

    private String id           = "";
    private String name         = "";
    private String description  = "";
    private String education    = "";
    private String skill        = "";
    private String about        = "";
    private String siteLink     = "";
    private String email        = "";
    private String phone        = "";

    private String typeidStr    = "";
    private String typeName     = "";

    //////////////////////////////////////////////////////////////////////////////////

    public MyExecutor(String executorId, String executorName) {

        // если значение задано
        if((executorId != null) && (!executorId.equals("null")))
            // сохраняем его
            this.id = executorId;

        // если значение задано
        if((executorName != null) && (!executorName.equals("null")))
            // сохраняем его
            this.name   = executorName;
    }

    //////////////////////////////////////////////////////////////////////////////////

    public String getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    //////////////////////////////////////////////////////////////////////////////////

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        // если значение задано
        if((description != null) && (!description.equals("null")))
            // сохраняем его
            this.description = description;
    }

    //////////////////////////////////////////////////////////////////////////////////

    public String getPhone() {

        return phone;
    }

    public void setPhone(String phone) {

        // если значение задано
        if((phone != null) && (!phone.equals("null")))
            // сохраняем его
            this.phone = phone;
    }

    //////////////////////////////////////////////////////////////////////////////////

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        // если значение задано
        if((email != null) && (!email.equals("null")))
            // сохраняем его
            this.email = email;
    }
}