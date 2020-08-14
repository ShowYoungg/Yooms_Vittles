package com.soyombo.yoomsvittles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import static com.soyombo.yoomsvittles.ProductAdapter.cart;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private Button checkOut;
    private ProductAdapter productAdapter;
    private ArrayList<Product> arrayList;


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
        setContentView(R.layout.activity_cart);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        checkOut = findViewById(R.id.button);

        recyclerView = findViewById(R.id.product_list_cart);
        arrayList = new ArrayList<>();
        for (CartItem item: cart) {
            arrayList.add(item.getProduct());
        }

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, CheckOutActivity.class));
            }
        });

        productAdapter = new ProductAdapter(CartActivity.this, arrayList, "cart");
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(productAdapter);

        DividerItemDecoration decoration2 = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.HORIZONTAL);
        DividerItemDecoration decoration1 = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration2);
        recyclerView.addItemDecoration(decoration1);
    }

    public void resetCart(int which){
        arrayList = new ArrayList<>();
        for (int i = 0; i < cart.size(); i++) {
            if (i != which){
                arrayList.add(cart.get(i).getProduct());
            } else {
                cart.remove(i);
            }
        }

        ProductAdapter p = new ProductAdapter(CartActivity.this, arrayList, "cart");
        p.notifyDataSetChanged();
    }
}
