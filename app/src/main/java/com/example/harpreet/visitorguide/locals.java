package com.example.harpreet.visitorguide;

import android.net.Uri;

public class locals {
    private double Lat;
    private double Long;
    private Uri img;
    private String placeName;
    private String itemName;
    private String reviews;
    private String category;
    private float rate;

    public locals(double Lat,double Long,Uri img,String placeName,String itemName,String reviews,String category,float rate)
    {
        this.category=category;
        this.Lat=Lat;
        this.Long=Long;
        this.img=img;
        this.placeName=placeName;
        this.itemName=itemName;
        this.reviews=reviews;
        this.rate=rate;
    }
    public String getPlaceName()
    {
        return placeName;
    }
    public String getItemName(){
        return itemName;
    }
    public String getReviews(){
        return reviews;
    }
    public String getCategory(){
        return category;
    }
    public Uri getImg(){
        return img;
    }
    public double getLat(){
        return Lat;
    }
    public double getLong(){
        return Long;
    }

    public float getRate() {
        return rate;
    }

    public void setLat(double Lat)
    {
        this.Lat=Lat;
    }
    public void setLong(double Long)
    {
        this.Long=Long;
    }
    public void setCategory(String category){
        this.category=category;
    }
    public void setImg(Uri img)
    {
        this.img=img;
    }
    public void setPlaceName(String placeName)
    {this.placeName=placeName;}
    public void setItemName(String itemName){
        this.itemName=itemName;
    }
    public void setReviews(String reviews)
    {
        this.reviews=reviews;
    }
    public void setRate(float rate){
        this.rate=rate;
    }
}
