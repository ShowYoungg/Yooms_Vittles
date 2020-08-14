package com.soyombo.yoomsvittles;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private Product product;
    private int bitmapId;
    private int number;

    public CartItem(){
        this.product = product;
        this.bitmapId = bitmapId;
        this.number = number;
    }

    public CartItem(Product product, int bitmapId, int number){
        this.product = product;
        this.bitmapId = bitmapId;
        this.number = number;
    }

    protected CartItem(Parcel in) {
        product = in.readParcelable(Product.class.getClassLoader());
        bitmapId = in.readInt();
        number = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(product, flags);
        dest.writeInt(bitmapId);
        dest.writeInt(number);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    public Product getProduct() {
        return product;
    }
    public void setPictureUrl(Product product) {
        this.product = product;
    }
    public int getBitmapId(){ return bitmapId;}
    public void setBitmapId(int bitmapId){ this.bitmapId = bitmapId;}
    public int getNumber(){ return number;}
    public void setNumber(int number){ this.number = number;}
}

// client id 343442189874-3mgu20m1nl0mjl3p70p0sau86bqd732c.apps.googleusercontent.com
