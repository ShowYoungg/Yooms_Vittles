package com.soyombo.yoomsvittles;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Products implements Parcelable {
    private ArrayList<String> productName;
    private ArrayList<Integer> productPrice;
    private ArrayList<String> productDescription;
    private ArrayList<String> productShortDescription;
    private ArrayList<String> quantity;
    private ArrayList<String> pictureUrl;

    public Products() {
        this.productPrice = productPrice;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productShortDescription = productShortDescription;
        this.pictureUrl = pictureUrl;
        this.quantity = quantity;
    }

    public Products(ArrayList<Integer> productPrice, ArrayList<String> productName, ArrayList<String> productDescription, ArrayList<String> pictureUrl,
                    ArrayList<String> productShortDescription, ArrayList<String> quantity) {
        this.productPrice = productPrice;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productShortDescription = productShortDescription;
        this.pictureUrl = pictureUrl;
        this.quantity = quantity;
    }


    protected Products(Parcel in) {
        productName = in.createStringArrayList();
        productDescription = in.createStringArrayList();
        productShortDescription = in.createStringArrayList();
        quantity = in.createStringArrayList();
        pictureUrl = in.createStringArrayList();
    }

    public static final Creator<Products> CREATOR = new Creator<Products>() {
        @Override
        public Products createFromParcel(Parcel in) {
            return new Products(in);
        }

        @Override
        public Products[] newArray(int size) {
            return new Products[size];
        }
    };

    public void setProductName(ArrayList<String> productName) {
        this.productName = productName;
    }

    public ArrayList<String> getProductName() {
        return this.productName;
    }

    public void setProductPrice(ArrayList<Integer> productPrice) {
        this.productPrice = productPrice;
    }

    public ArrayList<Integer> getProductPrice() {
        return this.productPrice;
    }

    public void setProductDescription(ArrayList<String> productDescription) {
        this.productDescription = productDescription;
    }

    public ArrayList<String> getProductDescription() {
        return this.productDescription;
    }

    public void setProductShortDescription(ArrayList<String> productShortDescription) {
        this.productShortDescription = productShortDescription;
    }

    public ArrayList<String> getProductShortDescription() {
        return this.productShortDescription;
    }

    public void setQuantity(ArrayList<String> quantity) {
        this.quantity = quantity;
    }

    public ArrayList<String> getQuantity() {
        return this.quantity;
    }

    public void setPictureUrl(ArrayList<String> pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public ArrayList<String> getPictureUrl() {
        return this.pictureUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(productName);
        dest.writeStringList(productDescription);
        dest.writeStringList(productShortDescription);
        dest.writeStringList(quantity);
        dest.writeStringList(pictureUrl);
    }
}