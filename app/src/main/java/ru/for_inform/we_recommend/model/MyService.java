package ru.for_inform.we_recommend.model;

/**
 * Created by OS1 on 09.07.2016.
 */
public class MyService {

    private String id    = "";
    private String name  = "";
    private String price = "";
    private String unit  = "";

    //////////////////////////////////////////////////////////////////////////////////

    public MyService(String serviceId, String serviceName, String servicePrice, String serviceUnit) {

        // сохраняем значение
        this.id = serviceId;

        // если значение задано
        if((serviceName != null) && (!serviceName.equals("null")))
            // сохраняем его
            this.name = serviceName;

        // если значение задано
        if((servicePrice != null) && (!servicePrice.equals("null")))
            // сохраняем его
            this.price = servicePrice;

        // если значение задано
        if((serviceUnit != null) && (!serviceUnit.equals("null")))
            // сохраняем его
            this.unit = serviceUnit;
    }

    //////////////////////////////////////////////////////////////////////////////////

    public String getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public String getPrice() {

        return price;
    }

    public String getUnit() {

        return unit;
    }
}