package com.soyombo.yoomsvittles;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import static com.soyombo.yoomsvittles.MainActivity.bitmaps;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context mContext;
    //private Target mTarget;
    private ArrayList<Product> list = new ArrayList<>();
    public static ArrayList<CartItem> cart = new ArrayList<>();
    //public static HashMap<Integer, Product> cart = new HashMap<>();
    private String destination; //This string defines which activity between MainActivity and CartActivity is using this adapter
    private Product product2;
    public static Bitmap bitmap2;
    private AlertDialog bb;
    private boolean isDialogShowing = false;
    private AlertDialog.Builder b;

    public ProductAdapter(Context context, ArrayList<Product> keys, String destination) {

        this.list = keys;
        this.destination = destination;
        this.mContext = context;
        notifyDataSetChanged();
    }

    public void resetAdapter() {
        list.clear();
    }


    @Override
    public ProductViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem;
        if (destination.equals("cart")) {
            layoutIdForListItem = R.layout.cart_product_display;
        } else {
            layoutIdForListItem = R.layout.product_display;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        ProductAdapter.ProductViewHolder viewHolder = new ProductAdapter.ProductViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        //Always declare your object inside here; never set it as a global object
        final Product product = list.get(position);
        product2 = product;

        if (product != null) {
            //ViewCompat.setTransitionName(holder.imageView, product.getProductName());

            holder.price.setText("NGN" + String.valueOf(product.getPrice()));
            holder.shortDescription.setText(product.getProductName());

            if (destination.equals("cart")) {
                holder.imageView.setImageBitmap(bitmaps[cart.get(position).getBitmapId()]);
            } else {
                holder.imageView.setImageBitmap(bitmaps[position]);

                Bitmap bitmapImage = ((BitmapDrawable) holder.imageView.getDrawable()).getBitmap();
                if (bitmapImage != null) {
                    if (bitmapImage.sameAs(bitmaps[position])) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }
            }

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, DescriptionActivity.class)
                            .putExtra("ProductName", product.getProductName())
                            .putExtra("Position", position)
                            .putExtra("Price", product.getPrice())
                            .putExtra("Destination", destination)
                            .putExtra("Description", product.getDescription())
                            .putExtra("ShortDescription", product.getShortDescription())
                            .putExtra("Measurement", product.getMeasurement());

                    //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext.getApplicationContext(),
                    //holder.imageView, ViewCompat.getTransitionName(holder.imageView));
                    mContext.startActivity(intent);
                }
            });

            if (!destination.equals("cart")) {
                holder.addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //CartItem cartItems = new CartItem(product, position, 1);
                        for (CartItem c : cart) {
                            String name = c.getProduct().getProductName();
                            if (name.equals(product.getProductName())) {
                                int newNumber = c.getNumber() + 1;
                                c.setNumber(newNumber);
                                Toast.makeText(mContext, "Item added to cart", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        cart.add(new CartItem(product, position, 1));
                        Toast.makeText(mContext, "Item added to cart", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (destination.equals("cart")) {

                holder.quantity.setText(String.valueOf(cart.get(position).getNumber()));

                holder.minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int s = Integer.parseInt(holder.quantity.getText().toString().trim());
                        int doubleClickChecker = position;
                        if (s > 1) {
                            s -= 1;
                            holder.quantity.setText(String.valueOf(s));
                            cart.get(position).setNumber(s);
                        } else {

                            if (!isDialogShowing){
                                deleteDialog(position);
                            }
                        }
                    }
                });

                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int s = Integer.parseInt(holder.quantity.getText().toString().trim());
                        s += 1;
                        holder.quantity.setText(String.valueOf(s));
                        cart.get(position).setNumber(s);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        final TextView price;
        final TextView addToCart;
        final TextView shortDescription;
        final ImageView imageView;
        final RelativeLayout relativeLayout;
        final ProgressBar progressBar;
        Button minus;
        Button add;
        EditText quantity;
        //Button checkOut;

        public ProductViewHolder(View itemView) {
            super(itemView);

            price = itemView.findViewById(R.id.price);
            addToCart = itemView.findViewById(R.id.add_to_cart);
            shortDescription = itemView.findViewById(R.id.short_description);
            imageView = itemView.findViewById(R.id.product_image);
            relativeLayout = itemView.findViewById(R.id.product_display_layout);
            progressBar = itemView.findViewById(R.id.progress_bar);

            if (destination.equals("cart")) {
                add = itemView.findViewById(R.id.add);
                minus = itemView.findViewById(R.id.minus);
                quantity = itemView.findViewById(R.id.quantity);
                //checkOut = itemView.findViewsWithText(R.id.che);
            }
        }
    }

    public void deleteDialog(final int position) {
        isDialogShowing = true;
        b = new AlertDialog.Builder(mContext);
        b.setCancelable(false);
        b.setTitle(mContext.getResources().getString(R.string.app_name));
        b.setMessage("Do you intend to delete this item?");
        b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isDialogShowing = false;
            }
        });
        b.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // if cart item remains one, the last one is about to be removed,
                // just go to MainActivity since there won't be anything in the cart again after deletion
                if (list.size() == 1){
                    Toast.makeText(mContext, "There is no items in the cart", Toast.LENGTH_SHORT).show();
                    ((Activity) mContext).finish();
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                }
                list.remove(position);
                cart.remove(position);
                notifyDataSetChanged();
                isDialogShowing = false;

            }
        });
        b.show();
    }
}
