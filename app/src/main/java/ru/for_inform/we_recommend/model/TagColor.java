package ru.for_inform.we_recommend.model;

/**
 * Created by OS1 on 04.07.2016.
 */
public class TagColor {

    private int id;
    private String value;

    public TagColor(int id, String value) {

        this.id = id;
        this.value = value;
    }

    //////////////////////////////////////////////////////////

    public int getTagColorId() {

        return id;
    }

    //////////////////////////////////////////////////////////

    public String getTagColorValue() {

        return value;
    }
}