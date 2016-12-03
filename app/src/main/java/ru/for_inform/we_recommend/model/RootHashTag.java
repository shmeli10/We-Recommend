package ru.for_inform.we_recommend.model;

/**
 * Created by OS1 on 04.07.2016.
 */
public class RootHashTag extends Tag {

    private TagColor tagColor;

    public RootHashTag(String rootHashTagId, String rootHashTagName,TagColor rootTagColor) {

        //
        this.id         = rootHashTagId;
        this.name       = rootHashTagName;
        this.tagColor   = rootTagColor;
    }

    //////////////////////////////////////////////////////////

    //
    public TagColor getTagColor() {

        return tagColor;
    }
}