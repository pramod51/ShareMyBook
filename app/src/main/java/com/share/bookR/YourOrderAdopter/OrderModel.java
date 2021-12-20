package com.share.bookR.YourOrderAdopter;

public class OrderModel {
    String title,url,name,status;

    public OrderModel(String title, String url, String name, String status) {
        this.title = title;
        this.url = url;

        this.name = name;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
