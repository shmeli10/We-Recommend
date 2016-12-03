package ru.for_inform.we_recommend.model;

/**
 * Created by OS1 on 09.07.2016.
 */
public class MyManager {

    private String name         = "";
    private String position     = "";
    private String photoLink    = "";

    //////////////////////////////////////////////////////////////////////////////////

    public MyManager(String managerName) {

        // если значение задано
        if((managerName != null) && (!managerName.equals("null")))
            // сохраняем его
            this.name = managerName;
    }

    //////////////////////////////////////////////////////////////////////////////////

    public String getName() {

        return name;
    }

    //////////////////////////////////////////////////////////////////////////////////

    public String getPosition() {

        //
        return position;
    }

    public void setPosition(String managerPosition) {

        // если значение задано
        if((managerPosition != null) && (!managerPosition.equals("null")))
            // сохраняем его
            this.position = managerPosition;
    }

    //////////////////////////////////////////////////////////////////////////////////

    public String getPhotoLink() {

        return photoLink;
    }

    public void setPhotoLink(String managerPhotoLink) {

        // если значение задано
        if((managerPhotoLink != null)  && (!managerPhotoLink.equals("null")))
            // сохраняем его
            this.photoLink = managerPhotoLink;
    }
}