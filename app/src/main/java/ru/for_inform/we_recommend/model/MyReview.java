package ru.for_inform.we_recommend.model;

/**
 * Created by OS1 on 08.07.2016.
 */
public class MyReview {

    private String typeName        = "";
    private String reviewerPhone   = "";
    private String reviewerName    = "";
    private String text            = "";

    ////////////////////////////////////////////////

    public MyReview(String reviewerPhone, String reviewerName, String reviewText) {

        // если значение задано
        if(!reviewerPhone.equals("null"))
            // сохраняем его
            this.reviewerPhone = reviewerPhone;

        // если значение задано
        if(!reviewerName.equals("null"))
            // сохраняем его
            this.reviewerName = reviewerName;

        // если значение задано
        if(!reviewText.equals("null"))
            // сохраняем его
            this.text = reviewText;
    }

    ////////////////////////////////////////////////

    public String getReviewerName() {

        return reviewerName;
    }

    public String getText() {

        return text;
    }

    ////////////////////////////////////////////////

    public String getTypeName() {

        return typeName;
    }

    public void setTypeName(String reviewTypeName) {

        // если значение задано
        if((reviewTypeName != null) && (!reviewTypeName.equals("null")))
            // сохраняем его
            this.typeName = reviewTypeName;
    }
}