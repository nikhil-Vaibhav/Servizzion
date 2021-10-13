package com.main.chatModel;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Service implements Serializable {

    public static final int CATEGORY_WEB_APP = 1;
    public static final int CATEGORY_MOBILE_APP = 2;

    public String category = "" , title = "", short_description = "" , long_description = "" , charges = "" , serviceID = "";
    public int category_ID ;
    public boolean isPaid;
    public List<String> serviceImages = new ArrayList<>();
    public User servedByUser;
    public Service() {}

    public Service(String category, String title, String short_description, String long_description, int category_ID, List<String> serviceImages , String charges , String serviceID , User servedByUser) {
        this.category = category;
        this.servedByUser = servedByUser;
        this.title = title;
        this.short_description = short_description;
        this.long_description = long_description;
        this.charges = charges;
        this.category_ID = category_ID;
        this.serviceID = serviceID;
        this.serviceImages = serviceImages;
    }
}
