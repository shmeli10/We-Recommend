package ru.for_inform.we_recommend.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.for_inform.we_recommend.R;
import ru.for_inform.we_recommend.model.ExecutorCard;
import ru.for_inform.we_recommend.model.MyApp;
import ru.for_inform.we_recommend.model.MyManager;
import ru.for_inform.we_recommend.model.MyReview;
import ru.for_inform.we_recommend.model.MyService;

/**
 * Created by OS1 on 29.04.2016.
 */
public class SelectedExecutor_Activity  extends     Activity
                                        implements  View.OnClickListener{

    private Context             context;

    private ImageView           backIV;
    private TextView            headerTitleTV;

    private ScrollView          scrollViewSV;

    private NetworkImageView    executorAvatarNIV;
    private TextView            executorAboutMeTV;

    private TabHost             tabHost;

    private final int backIVResId               = R.id.SelectedExecutor_BackIV;
    private final int headerTitleTVResId        = R.id.SelectedExecutor_HeaderTitleTV;

    private final int scrollViewSVResId         = R.id.SelectedExecutor_ScrollViewSV;

    private final int executorAvatarNIVResId    = R.id.SelectedExecutor_AvatarNIV;
    private final int executorAboutMeTVResId    = R.id.SelectedExecutor_ExecutorAboutMeValueTV;

    final String TABS_TAG_1 = "PricesTab";
    final String TABS_TAG_2 = "ReviewsTab";

    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // задать файл-компоновщик элементов окна
        setContentView(R.layout.selected_executor_layout);

        //////////////////////////////////////////////////////////////////////////////////

        //
        context = this;
        density = context.getResources().getDisplayMetrics().density;

        //////////////////////////////////////////////////////////////////////////////////

        backIV = (ImageView) findViewById(backIVResId);

        headerTitleTV = (TextView) findViewById(headerTitleTVResId);
        headerTitleTV.setTypeface(MyApp.getBebasNeueBoldTypeface());

        scrollViewSV        = (ScrollView) findViewById(scrollViewSVResId);

        executorAvatarNIV   = (NetworkImageView) findViewById(executorAvatarNIVResId);

        executorAboutMeTV   = (TextView) findViewById(executorAboutMeTVResId);

        //////////////////////////////////////////////////////////////////////////////////

        backIV.setOnClickListener(this);

        //////////////////////////////////////////////////////////////////////////////////

        // задаем заголовок экрана
        setTitle();

        //////////////////////////////////////////////////////////////////////////////////

        // задаем данные выбранной карточки исполнителя
        setSelectedExecutorData();

        // задаем данные для вкладок
        setTabs();
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {

            case backIVResId:
                                // закрываем экран
                                finish();

                                break;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    //
    private void setTitle() {

        // задаем значение заголовку
        headerTitleTV.setText(MyApp.getSelectedExecutorShortName().toUpperCase());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setSelectedExecutorData() {

        // получаем ссылку на выбранную карточку исполнителя
        ExecutorCard selectedExecutorCard = MyApp.getSelectedExecutorCard();

        // получаем ссылку на менеджера в карточке исполнителя
        MyManager manager = selectedExecutorCard.getManager();

        // получаем идентификатор черного цвета
        int blackColorId = getResources().getColor(R.color.black);

        // создаем "контейнер данных исполнителя"
        LinearLayout executorDataLL = (LinearLayout) findViewById(R.id.SelectedExecutor_ExecutorTextDataRow1LL);

        ///////////////////////////////////////////////////////////////////////////

        // если значение задано
        if(manager != null) {

            // получаем ссылку на изображение менеджера
            String managerPhotoLink = manager.getPhotoLink();

            // если значение получено
            if(!managerPhotoLink.equals(""))
                // загружаем изображение
                executorAvatarNIV.setImageUrl(MyApp.getMediaLinkHead() + managerPhotoLink, MyApp.getAppImageLoader());
        }

        ///////////////////////////////////////////////////////////////////////////

        // получаем наименование исполнителя
        String executorName = selectedExecutorCard.getExecutor().getName();

        // если значение получено
        if(!executorName.equals("")) {

            LinearLayout.LayoutParams lpWW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);

            TextView executorNameValueTV = new TextView(context);
            executorNameValueTV.setLayoutParams(lpWW);
            executorNameValueTV.setTextSize(8.0f);
            executorNameValueTV.setTextColor(blackColorId);
            executorNameValueTV.setTypeface(Typeface.DEFAULT_BOLD);
            executorNameValueTV.setText(executorName);

            // добавляем текстовое представление в "контейнер данных исполнителя"
            executorDataLL.addView(executorNameValueTV);
        }

        ///////////////////////////////////////////////////////////////////////////

        // получаем наименование исполнителя
        String executorId = selectedExecutorCard.getExecutor().getId();

        // если значение задано
        if(!executorId.equals("")) {

            //
            View oneRowStrut = new View(context);
            oneRowStrut.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ((int) (8 * density)), 1.0f));

            //
            executorDataLL.addView(oneRowStrut);

            ////////////////////////////////////////////////////////////////////////////////

            //
            TextView executorIdTextTV = new TextView(context);
            executorIdTextTV.setTextSize(7.0f);
            executorIdTextTV.setTextColor(blackColorId);
            executorIdTextTV.setText("id:");

            //
            executorDataLL.addView(executorIdTextTV);

            ////////////////////////////////////////////////////////////////////////////////

            //
            TextView executorIdValueTV = new TextView(context);
            executorIdValueTV.setTextSize(7.0f);
            executorIdValueTV.setTextColor(blackColorId);
            executorIdValueTV.setText(executorId);

            //
            executorDataLL.addView(executorIdValueTV);
        }

        ///////////////////////////////////////////////////////////////////////////

        // если значение задано
        if(manager != null) {

            // получаем ФИО менеджера
            String managerName = selectedExecutorCard.getManager().getName();

            // если значение получено
            if (!managerName.equals("")) {

                //
                LinearLayout managerLL = (LinearLayout) findViewById(R.id.SelectedExecutor_ExecutorTextDataRow2LL);

                //
                LinearLayout.LayoutParams lpWW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                //
                TextView managerNameTextTV = new TextView(context);
                managerNameTextTV.setLayoutParams(lpWW);
                managerNameTextTV.setTextSize(7.0f);
                managerNameTextTV.setTextColor(blackColorId);
                managerNameTextTV.setTypeface(Typeface.DEFAULT_BOLD);
                managerNameTextTV.setText(getResources().getString(R.string.your_manager_text));

                managerLL.addView(managerNameTextTV);

                ////////////////////////////////////////////////////////////////////////////////

                //
                TextView managerNameValueTV = new TextView(context);
                managerNameValueTV.setLayoutParams(lpWW);
                managerNameValueTV.setTextSize(7.0f);
                managerNameValueTV.setTextColor(blackColorId);
                managerNameValueTV.setTypeface(Typeface.DEFAULT_BOLD);
                managerNameValueTV.setText(managerName);

                //
                setPaddings(managerNameValueTV, 2, 0, 0, 0);

                //
                managerLL.addView(managerNameValueTV);
            }
        }

        ///////////////////////////////////////////////////////////////////////////

        // получаем теги исполнителя
        String executorTags = selectedExecutorCard.getTags();

        // если значение получено
        if(!executorTags.equals("")) {

            //
            LinearLayout tagsLL = (LinearLayout) findViewById(R.id.SelectedExecutor_ExecutorTextDataRow3LL);

            //
            TextView tagsTV = new TextView(context);
            tagsTV.setTextSize(7.0f);
            tagsTV.setTextColor(blackColorId);
            tagsTV.setText(executorTags);

            //
            tagsLL.addView(tagsTV);
        }

        ///////////////////////////////////////////////////////////////////////////

        // получаем номер телефона исполнителя
        String executorPhone = selectedExecutorCard.getExecutor().getPhone();

        // если значение получено
        if(!executorPhone.equals("")) {

            //
            LinearLayout phoneLL = (LinearLayout) findViewById(R.id.SelectedExecutor_ExecutorTextDataRow4LL);

            //
            ImageView phoneIV = new ImageView(context);
            phoneIV.setLayoutParams(new ViewGroup.LayoutParams(((int) (10 * density)), ((int) (10 * density))));
            phoneIV.setImageResource(R.drawable.phone);

            phoneLL.addView(phoneIV);

            ////////////////////////////////////////////////////////////////////////////////

            //
            TextView phoneTV = new TextView(context);
            phoneTV.setTextSize(7.0f);
            phoneTV.setTextColor(blackColorId);
            phoneTV.setText(executorPhone);

            //
            setPaddings(phoneTV, 2, 0, 0, 0);

            //
            phoneLL.addView(phoneTV);
        }

        ///////////////////////////////////////////////////////////////////////////

        // получаем адрес электронной почты исполнителя
        String executorEmail = selectedExecutorCard.getExecutor().getEmail();

        // если значение получено
        if(!executorEmail.equals("")) {

            //
            LinearLayout mailLL = (LinearLayout) findViewById(R.id.SelectedExecutor_ExecutorTextDataRow5LL);

            //
            ImageView mailIV = new ImageView(context);
            mailIV.setLayoutParams(new ViewGroup.LayoutParams(((int)(10 * density)),((int)(10 * density))));
            mailIV.setImageResource(R.drawable.mail);

            mailLL.addView(mailIV);

            ////////////////////////////////////////////////////////////////////////////////

            //
            TextView mailTV = new TextView(context);
            mailTV.setTextSize(7.0f);
            mailTV.setTextColor(blackColorId);
            mailTV.setText(executorEmail);

            //
            setPaddings(mailTV, 2, 0, 0, 0);

            //
            mailLL.addView(mailTV);
        }

        ///////////////////////////////////////////////////////////////////////////

        // если коллекция с дополнительными полями данных не пустая
        if(!selectedExecutorCard.isFieldsMapEmpty()) {

            // получаем ссылку на коллекцию
            Map<String, String[]> fieldsMap = selectedExecutorCard.getFieldsMap();

            // получаем все ключи коллекции
            Set<String> fieldsMapKeysSet = fieldsMap.keySet();

            // переносим их в список для сортировки
            List<String> fieldsMapKeysList = new ArrayList<>();

            // если данные получены
            if(fieldsMapKeysSet != null)
                // кладем значения в список
                fieldsMapKeysList.addAll(fieldsMapKeysSet);

            // сортируем полученный список
            Collections.sort(fieldsMapKeysList);

            // проходим циклом по списку
            for(int i=0; i<fieldsMapKeysList.size(); i++) {

                // получаем из коллекции массив данных очередного дополнительного поля
                String[] fieldArr = fieldsMap.get(fieldsMapKeysList.get(i));

                // получаем заголовок и значение поля
                String fieldName  = fieldArr[0];
                String fieldValue = fieldArr[1];

                ////////////////////////////////////////////////////////////////////////////////////

                //
                LinearLayout fieldsLL = (LinearLayout) findViewById(R.id.SelectedExecutor_ExecutorTextDataRow6LL);

                //
                LinearLayout.LayoutParams lpMW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                //
                if(fieldName.equals("skype")) {

                    //
                    LinearLayout skypeLL = new LinearLayout(context);
                    skypeLL.setLayoutParams(lpMW);
                    skypeLL.setOrientation(LinearLayout.HORIZONTAL);

                    //
                    setPaddings(skypeLL, 5, 5, 0, 0);

                    ////////////////////////////////////////////////////////////////////////

                    //
                    ImageView skypeIV = new ImageView(context);
                    skypeIV.setLayoutParams(new ViewGroup.LayoutParams(((int)(10 * density)),((int)(10 * density))));
                    skypeIV.setImageResource(R.drawable.skype);

                    //
                    skypeLL.addView(skypeIV);

                    ////////////////////////////////////////////////////////////////////////

                    //
                    TextView skypeTV = new TextView(context);
                    skypeTV.setTextSize(7.0f);
                    skypeTV.setTextColor(blackColorId);
                    skypeTV.setText(fieldValue);

                    //
                    setPaddings(skypeTV, 2, 0, 0, 0);

                    //
                    skypeLL.addView(skypeTV);

                    ////////////////////////////////////////////////////////////////////////

                    //
                    fieldsLL.addView(skypeLL);
                }
                //
                else {

                    //
                    LinearLayout fieldLL = new LinearLayout(context);
                    fieldLL.setLayoutParams(lpMW);
                    fieldLL.setOrientation(LinearLayout.HORIZONTAL);

                    //
                    setPaddings(fieldLL, 5, 5, 0, 0);

                    ////////////////////////////////////////////////////////////////////////

                    StringBuilder fieldValueSB = new StringBuilder("");
                    fieldValueSB.append(fieldName);
                    fieldValueSB.append(":");

                    //
                    TextView fieldNameTV = new TextView(context);
                    fieldNameTV.setTextSize(7.0f);
                    fieldNameTV.setTextColor(blackColorId);
                    fieldNameTV.setText(fieldValueSB.toString());

                    //
                    setPaddings(fieldNameTV, 2, 0, 0, 0);

                    //
                    fieldLL.addView(fieldNameTV);

                    ////////////////////////////////////////////////////////////////////////

                    //
                    TextView fieldValueTV = new TextView(context);
                    fieldValueTV.setTextSize(7.0f);
                    fieldValueTV.setTextColor(blackColorId);
                    fieldValueTV.setText(fieldValue);

                    //
                    setPaddings(fieldValueTV, 2, 0, 0, 0);

                    //
                    fieldLL.addView(fieldValueTV);

                    ////////////////////////////////////////////////////////////////////////

                    //
                    fieldsLL.addView(fieldLL);
                }
            }
        }

        ///////////////////////////////////////////////////////////////////////////

        // получаем описание исполнителя
        String executorDescription = selectedExecutorCard.getExecutor().getDescription();

        // если значение получено
        if(!executorDescription.equals(""))
                // кладем его в текстовое представление
                executorAboutMeTV.setText(executorDescription);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setTabs() {

        // получаем ссылку на выбранную карточку исполнителя
        final ExecutorCard selectedExecutorCard = MyApp.getSelectedExecutorCard();

        //
        tabHost = (TabHost) findViewById(android.R.id.tabhost);

        // инициализация
        tabHost.setup();

        //
        TabHost.TabSpec tabSpec;

        //
        final LinearLayout.LayoutParams lpMW    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);

        //
        final LinearLayout.LayoutParams lpWW    = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
        setMargins(lpMW, 0, 50, 0, 50);

        //
        final LinearLayout.LayoutParams strutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ((int) (20 * density)), 1.0f);

        //
        TabHost.TabContentFactory TabFactory = new TabHost.TabContentFactory() {

            @Override
            public View createTabContent(String tag) {

                // выбрана вкладка "Цены"
                if (tag == TABS_TAG_1) {

                    //
                    LinearLayout pricesLL = new LinearLayout(context);
                    pricesLL.setLayoutParams(lpMW);
                    pricesLL.setOrientation(LinearLayout.VERTICAL);
                    pricesLL.setBackgroundResource(R.drawable.rect_with_grey_stroke);

                    // если коллекция с услугами исполнителя не пустая
                    if(!selectedExecutorCard.isServicesMapEmpty()) {

                        // получаем ссылку на коллекцию с услугами
                        Map<String, MyService> servicesMap = selectedExecutorCard.getServicesMap();

                        // получаем все ключи коллекции
                        Set<String> servicesMapKeysSet = servicesMap.keySet();

                        // переносим их в список для сортировки
                        List<String> servicesMapKeysList = new ArrayList<>();

                        // если данные получены
                        if(servicesMapKeysSet != null)
                            // кладем значения в список
                            servicesMapKeysList.addAll(servicesMapKeysSet);

                        // сортируем полученный список
                        Collections.sort(servicesMapKeysList);

                        // получаем количество услуг
                        int servicesSum = servicesMapKeysList.size();

                        // проходим циклом по списку
                        for(int i=0; i<servicesSum; i++) {

                            // получаем очередную услугу исполнителя
                            MyService service = servicesMap.get(servicesMapKeysList.get(i));

                            //
                            View priceView = getLayoutInflater().inflate(R.layout.price_row_layout, null);

                            //
                            TextView serviceNameTV = (TextView) priceView.findViewById(R.id.PriceRow_ServiceNameTV);
                            serviceNameTV.setText(service.getName());

                            //
                            TextView servicePriceTV = (TextView) priceView.findViewById(R.id.PriceRow_ServicePriceTV);
                            servicePriceTV.setText(service.getPrice()+ " " +service.getUnit());

                            //
                            LinearLayout priceContainerLL   = (LinearLayout) priceView.findViewById(R.id.PriceRow_ContainerLL);

                            //
                            LinearLayout footerLL           = (LinearLayout) priceView.findViewById(R.id.PriceRow_FooterLL);
                            footerLL.removeAllViews();

                            // если это нечетная строка в списке
                            if((i == 0) || ((i > 0) && (((i + 1) % 2) == 1)))
                                // подсвечиваем ее серым цветом
                                priceContainerLL.setBackgroundColor(getResources().getColor(R.color.price_row_grey));

                            // если это первая строка
                            if(i == 0 ) {

                                // если строк в списке будет больше 1
                                if(servicesSum != 1) {

                                    //
                                    LinearLayout.LayoutParams priceLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
                                    setMargins(priceLP, 5, 5, 5, 0);

                                    //
                                    priceContainerLL.setLayoutParams(priceLP);
                                }
                                // если строка будет единственной в списке
                                else {

                                    //
                                    LinearLayout.LayoutParams priceLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
                                    setMargins(priceLP,5,5,5,5);

                                    //
                                    priceContainerLL.setLayoutParams(priceLP);

                                    ////////////////////////////////////////////////////////////////

                                    //
                                    LinearLayout.LayoutParams hLineLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ((int)(1 * density)), 0.0f);

                                    //
                                    View hLine = new View(context);
                                    hLine.setLayoutParams(hLineLP);
                                    hLine.setBackgroundColor(getResources().getColor(R.color.h_line_grey));

                                    //
                                    footerLL.addView(hLine);
                                }
                            }
                            // если это последняя строка
                            else if(i == (servicesSum - 1)) {

                                //
                                LinearLayout.LayoutParams priceLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
                                setMargins(priceLP,5,0,5,5);

                                //
                                priceContainerLL.setLayoutParams(priceLP);

                                ////////////////////////////////////////////////////////////////

                                //
                                LinearLayout.LayoutParams hLineLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ((int)(1 * density)), 0.0f);

                                //
                                View hLine = new View(context);
                                hLine.setLayoutParams(hLineLP);
                                hLine.setBackgroundColor(getResources().getColor(R.color.h_line_grey));

                                //
                                footerLL.addView(hLine);
                            }
                            // если это строка между первой и последней
                            else {
                                //
                                LinearLayout.LayoutParams priceLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
                                setMargins(priceLP,5,0,5,0);

                                //
                                priceContainerLL.setLayoutParams(priceLP);
                            }

                            // добавляем очередной "контейнер с ценой услуги" в "контейнер всех цен на услуги"
                            pricesLL.addView(priceView);
                        }
                    }
                    // нет ни одной услуги
                    else {

                        LinearLayout noPriceLL = new LinearLayout(context);
                        noPriceLL.setLayoutParams(lpMW);
                        noPriceLL.setOrientation(LinearLayout.HORIZONTAL);
                        noPriceLL.setGravity(Gravity.CENTER_HORIZONTAL);

                        // создаем горизонтальную распорку для полей в строке ответа
                        View leftStrut = new View(context);
                        leftStrut.setLayoutParams(strutLP);
                        noPriceLL.addView(leftStrut);

                        //
                        TextView noPriceTextTV = new TextView(context);
                        noPriceTextTV.setLayoutParams(lpWW);
                        noPriceTextTV.setTextSize(8.0f);
                        noPriceTextTV.setTextColor(getResources().getColor(R.color.black));
                        noPriceTextTV.setText(getResources().getString(R.string.no_data_text));
                        noPriceLL.addView(noPriceTextTV);

                        View rightStrut = new View(context);
                        rightStrut.setLayoutParams(strutLP);
                        noPriceLL.addView(rightStrut);

                        //
                        pricesLL.addView(noPriceLL);
                    }

                    return pricesLL;
                }
                // выбрана вкладка "Отзывы"
                else if (tag == TABS_TAG_2) {

                    //
                    LinearLayout reviewsLL = new LinearLayout(context);
                    reviewsLL.setLayoutParams(lpMW);
                    reviewsLL.setOrientation(LinearLayout.VERTICAL);
                    reviewsLL.setBackgroundResource(R.drawable.rect_with_grey_stroke);

                    // если коллекция с отзывами исполнителя не пустая
                    if(!selectedExecutorCard.isReviewsMapEmpty()) {

                        // получаем ссылку на коллекцию с отзывами
                        Map<String, MyReview> reviewsMap = selectedExecutorCard.getReviewsMap();

                        // получаем все ключи коллекции
                        Set<String> reviewsMapKeysSet = reviewsMap.keySet();

                        // переносим их в список для сортировки
                        List<String> reviewsMapKeysList = new ArrayList<>();

                        // если данные получены
                        if(reviewsMapKeysSet != null)
                            // кладем значения в список
                            reviewsMapKeysList.addAll(reviewsMapKeysSet);

                        // сортируем полученный список
                        Collections.sort(reviewsMapKeysList);

                        // получаем количество отзывов в коллекции
                        int reviewsSum = reviewsMapKeysList.size();

                        //
                        StringBuilder reviewDateSB = new StringBuilder("");
                        reviewDateSB.append("ДД");
                        reviewDateSB.append(".");
                        reviewDateSB.append("ММ");
                        reviewDateSB.append(".");
                        reviewDateSB.append("ГГГГ");

                        // проходим циклом по списку
                        for(int i=0; i<reviewsSum; i++) {

                            // получаем очередную услугу исполнителя
                            MyReview review = reviewsMap.get(reviewsMapKeysList.get(i));

                            //
                            View reviewView = getLayoutInflater().inflate(R.layout.review_row_layout, null);

                            ////////////////////////////////////////////////////////////////////

                            //
                            TextView reviewDateTV = (TextView) reviewView.findViewById(R.id.ReviewRow_ReviewDateTV);
                            reviewDateTV.setText(reviewDateSB.toString());

                            //
                            TextView reviewerNameTV = (TextView) reviewView.findViewById(R.id.ReviewRow_ReviewerNameTV);
                            reviewerNameTV.setText(review.getReviewerName());

                            //
                            TextView reviewTextTV = (TextView) reviewView.findViewById(R.id.ReviewRow_ReviewTextTV);
                            reviewTextTV.setText(review.getText());

                            //
                            TextView reviewerIdValueTV = (TextView) reviewView.findViewById(R.id.ReviewRow_ReviewerIdValueTV);
                            reviewerIdValueTV.setText("**");

                            ////////////////////////////////////////////////////////////////////

                            //
                            reviewsLL.addView(reviewView);
                        }
                    }
                    // нет ни одного отзыва
                    else {

                        LinearLayout noReviewLL = new LinearLayout(context);
                        noReviewLL.setLayoutParams(lpMW);
                        noReviewLL.setOrientation(LinearLayout.HORIZONTAL);
                        noReviewLL.setGravity(Gravity.CENTER_HORIZONTAL);

                        // создаем горизонтальную распорку для полей в строке ответа
                        View leftStrut = new View(context);
                        leftStrut.setLayoutParams(strutLP);
                        noReviewLL.addView(leftStrut);

                        //
                        TextView noReviewTextTV = new TextView(context);
                        noReviewTextTV.setLayoutParams(lpWW);
                        noReviewTextTV.setTextSize(8.0f);
                        noReviewTextTV.setTextColor(getResources().getColor(R.color.black));
                        noReviewTextTV.setText(getResources().getString(R.string.no_data_text));
                        noReviewLL.addView(noReviewTextTV);

                        View rightStrut = new View(context);
                        rightStrut.setLayoutParams(strutLP);
                        noReviewLL.addView(rightStrut);

                        //
                        reviewsLL.addView(noReviewLL);
                    }

                    //
                    return reviewsLL;
                }

                return null;
            }
        };

        /////////////////////////////////////////////////////////////////

        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec(TABS_TAG_1);

        // название вкладки
        tabSpec.setIndicator(getResources().getString(R.string.prices_text));

        // указываем id компонента из FrameLayout, он и станет содержимым
        tabSpec.setContent(TabFactory);

        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);

        /////////////////////////////////////////////////////////////////

        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec(TABS_TAG_2);

        // название вкладки
        tabSpec.setIndicator(getResources().getString(R.string.reviews_text));

        // указываем id компонента из FrameLayout, он и станет содержимым
        tabSpec.setContent(TabFactory);

        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);

        /////////////////////////////////////////////////////////////////

        tabHost.setCurrentTab(0);

        /////////////////////////////////////////////////////////////////

        //
        changeTabsStyle();

        /////////////////////////////////////////////////////////////////

        // обработчик переключения вкладок
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                //
                changeTabsStyle();
            }
        });

        /////////////////////////////////////////////////////////////////

        // прокручиваем контенер ответов на последний ответ
        scrollViewSV.post(new Runnable() {
            @Override
            public void run() {
                scrollViewSV.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void changeTabsStyle(){

        TabWidget tabWidget = tabHost.getTabWidget();

        //
        for(int i=0; i<tabWidget.getChildCount(); i++)
        {
            View tabWidgetChild = tabWidget.getChildAt(i);

            tabWidgetChild.setBackgroundResource(R.drawable.simple_tab);            // не выбранная вкладка

            //
            TextView tv = (TextView) tabWidgetChild.findViewById(android.R.id.title); // выбранные вкладки
            tv.setTextColor(getResources().getColor(R.color.text_blue));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        View tabWidgetCurrentTab = tabWidget.getChildAt(tabHost.getCurrentTab());

        tabWidgetCurrentTab.setBackgroundResource(R.drawable.selected_tab);             // выбранные вкладки

        View currentTabWidgetChild = tabHost.getCurrentTabView();

        //
        TextView tv = (TextView) currentTabWidgetChild.findViewById(android.R.id.title); // выбранные вкладки
        tv.setTextColor(getResources().getColor(R.color.black));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void setMargins(LinearLayout.LayoutParams layout,int left, int top, int right, int bottom) {

        int marginLeft     = (int)(left * density);
        int marginTop      = (int)(top * density);
        int marginRight    = (int)(right * density);
        int marginBottom   = (int)(bottom * density);

        layout.setMargins(marginLeft, marginTop, marginRight, marginBottom);
    }

    //
    private void setPaddings(View view, int left, int top, int right, int bottom) {

        float density = context.getResources().getDisplayMetrics().density;

        int paddingLeft     = (int)(left * density);
        int paddingTop      = (int)(top * density);
        int paddingRight    = (int)(right * density);
        int paddingBottom   = (int)(bottom * density);

        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }
}