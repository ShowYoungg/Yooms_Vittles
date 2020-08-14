package com.soyombo.yoomsvittles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import static com.soyombo.yoomsvittles.MainActivity.bitmaps;
import static com.soyombo.yoomsvittles.ProductAdapter.cart;

public class DescriptionActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView price;
    private ProgressBar progressBar;
    private TextView productName;
    private TextView description;
    private TextView addToCart;
    private Button button;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //supportPostponeEnterTransition();

        imageView = findViewById(R.id.product_image);
        price = findViewById(R.id.price);
        productName = findViewById(R.id.short_description);
        addToCart = findViewById(R.id.add_to_cart);
        description = findViewById(R.id.des);
        button = findViewById(R.id.cash_out_button);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        if (getIntent() != null) {
            final String n = getIntent().getStringExtra("ProductName");
            imageView.setTransitionName(n);
            final String des = getIntent().getStringExtra("Description");
            final String destination = getIntent().getStringExtra("Destination");
            final int position = getIntent().getIntExtra("Position", 0);
            final int p = getIntent().getIntExtra("Price", 0);
            final boolean avail = getIntent().getBooleanExtra("Availability", true);
            //final Bitmap bitmap = getIntent().getParcelableExtra("Bitmap");
            //downloadImage(n, imageView, this);
            if (destination != null) {
                if (!destination.equals("cart")) {

                    imageView.setImageBitmap(bitmaps[position]);
                    //supportStartPostponedEnterTransition();
                } else {
                    imageView.setTransitionName(n);
                    imageView.setImageBitmap(bitmaps[cart.get(position).getBitmapId()]);
                    addToCart.setVisibility(View.GONE);
                    //imageView.setImageBitmap(bitmaps[position]);
                }
            }

            //imageView.setImageResource(imageId);
            price.setText("NGN" + String.valueOf(p));
            productName.setText(n);


            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product product = new Product(p, n, des, "","", des);
                    //CartItem cartItems = new CartItem(product, position);
                    for (CartItem c: cart) {
                        String name = c.getProduct().getProductName();
                        if (name.equals(n)){
                            int newNumber = c.getNumber() + 1;
                            c.setNumber(newNumber);
                            Toast.makeText(DescriptionActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    cart.add(new CartItem(product, position, 1));
                    Toast.makeText(DescriptionActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                }
            });

        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() == 0) {
                    Toast.makeText(DescriptionActivity.this, "No item added to cart yet", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(DescriptionActivity.this, CartActivity.class));
                }
            }
        });


    }

    public static void downloadImage(String fileName, ImageView image, Context context) {

        fileName = fileName.replaceAll(" ", "_");
        File downloadFolder = Environment.getExternalStorageDirectory();
        File dir = new File(downloadFolder + "/Yooms/");
        Uri downloadPath = Uri.fromFile(new File(dir, "/" + fileName + ".webp/"));

        if (downloadPath.toString() != null) {
            //image.setImageURI(downloadPath);
            Picasso.with(context).load(new File(Environment.getExternalStorageDirectory() + "/Yooms/" + fileName + ".webp/")).into(image);
            Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "There is no image of this name saved",
                    Toast.LENGTH_LONG).show();
        }
    }

}
