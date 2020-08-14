package com.soyombo.yoomsvittles;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable{
    private int price;
    private String productName;
    private String measurement;
    private String description;
    private String pictureUrl;
    private String shortDescription;

    public Product(){
        this.price = price;
        this.productName = productName;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.measurement = measurement;
        this.shortDescription = shortDescription;
    }

    public Product(int price, String productName, String description, String pictureUrl,
                   String measurement, String shortDescription){
        this.price = price;
        this.productName = productName;
        this.description = description;
        this.pictureUrl = pictureUrl;
        this.measurement = measurement;
        this.shortDescription = shortDescription;
    }


    protected Product(Parcel in) {
        price = in.readInt();
        productName = in.readString();
        measurement = in.readString();
        description = in.readString();
        pictureUrl = in.readString();
        shortDescription = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getPictureUrl() {
        return pictureUrl;
    }
    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
    public int getPrice(){ return price;}
    public void setPrice(int price){ this.price = price;}
    public String getProductName(){ return productName;}
    public void setProductName(String productName){ this.productName = productName;}
    public String getDescription(){ return description;}
    public void setDescription(String description){ this.description = description;}
    public String getShortDescription(){ return description;}
    public void setShortDescription(String description){ this.description = description;}
    public String getMeasurement(){ return measurement;}
    public void setMeasurement(String measurement){ this.measurement = measurement;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(price);
        dest.writeString(productName);
        dest.writeString(measurement);
        dest.writeString(description);
        dest.writeString(pictureUrl);
        dest.writeString(shortDescription);
    }
}
