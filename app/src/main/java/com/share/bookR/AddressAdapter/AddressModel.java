package com.share.bookR.AddressAdapter;

public class AddressModel {
    String name,details,from,saveAs;

    public AddressModel(String name, String details, String from, String saveAs) {
        this.name = name;
        this.details = details;
        this.from = from;
        this.saveAs = saveAs;
    }

    public String getSaveAs() {
        return saveAs;
    }

    public void setSaveAs(String saveAs) {
        this.saveAs = saveAs;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
