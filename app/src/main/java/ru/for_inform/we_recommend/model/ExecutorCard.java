package ru.for_inform.we_recommend.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OS1 on 08.07.2016.
 */
public class ExecutorCard {

    private MyExecutor executor;
    private MyManager manager;

    private String id    = "";

    private String cityId       = "";
    private String cityName     = "";

    private String photoLink    = "";
    private String activeDate   = "";

    private String rate         = "";

    private String tags         = "";

    private String status       = "";

    private boolean verified    = false;

    private Map<String, String[]> fieldsMap     = new HashMap<>();
    private Map<String, String> youtubeLinksMap = new HashMap<>();

    private Map<String, MyService> servicesMap  = new HashMap<>();
    private Map<String, MyReview> reviewsMap    = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ExecutorCard(MyExecutor executor, MyManager manager, String cardId) {

        // если значение задано
        if((cardId != null) && (!cardId.equals("null")))
            // сохраняем его
            this.id = cardId;

        this.executor   = executor;
        this.manager    = manager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public MyExecutor getExecutor() {

        return executor;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public MyManager getManager() {

        return manager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String getId() {

        return id;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String getRate() {

        StringBuilder rateSB = new StringBuilder("");

        // если значение получено
        if(!rate.equals(""))
            // добавляем к строке полученное значение
            rateSB.append(rate);
        // если значения нет
        else
            // добавляем к строке прочерк
            rateSB.append("-");

        // возвращаем сформированную строку
        return rateSB.toString();
    }

    public void setRate(String rate) {

        // если значение задано
        if(rate != null)
            // сохраняем его
            this.rate = rate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String getTags() {

        return tags;
    }

    public void setTags(String cardTags) {

        // если значение задано
        if(cardTags != null)
            // сохраняем его
            this.tags = cardTags;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Map<String, MyService> getServicesMap() {

        return servicesMap;
    }

    public boolean isServicesMapEmpty() {

        // если коллекция пуста, вернуть true
        return servicesMap.isEmpty();
    }

    public void setServicesMap(Map<String, MyService> cardServicesMap) {

        // если значение задано
        if(cardServicesMap != null)
            // сохраняем его
            this.servicesMap = cardServicesMap;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Map<String, String[]> getFieldsMap() {

        // вернуть ссылку на коллекцию
        return fieldsMap;
    }

    public boolean isFieldsMapEmpty() {

        // если коллекция пуста, вернуть true
        return fieldsMap.isEmpty();
    }

    public void setFieldsMap(Map<String, String[]> сardFieldsMap) {

        // если ссылка задана
        if(сardFieldsMap != null)
            // сохраняем ее
            this.fieldsMap = сardFieldsMap;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String getReviewsSum() {

        int result = 0;

        // если коллекция задана
        if(reviewsMap != null)
            // получаем ее размер
            result = reviewsMap.size();

        return "" +result;
    }

    public Map<String, MyReview> getReviewsMap() {

        return reviewsMap;
    }

    public boolean isReviewsMapEmpty() {

        // если коллекция пуста, вернуть true
        return reviewsMap.isEmpty();
    }

    public void setReviewsMap(Map<String, MyReview> cardReviewsMap) {

        // если значение задано
        if(cardReviewsMap != null)
            // сохраняем его
            this.reviewsMap = cardReviewsMap;
    }
}