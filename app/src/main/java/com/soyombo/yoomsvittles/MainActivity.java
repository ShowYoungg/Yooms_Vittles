package com.soyombo.yoomsvittles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;



public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private GridView listView;
    private RecyclerView recyclerView;
    public static RecyclerView recyclerView1;
    private ArrayList<Product> productArrayList;
    private ArrayList<Product> productArrayList1;
    private ProductAdapter productAdapter;
    private String quantity = "dfdffdfdff";
    private ProgressBar progressBar;
    private LinearLayout progressBar_layout;
    public static Bitmap[] bitmaps;
    private ArrayList<String> urllinks = new ArrayList<>();

    private Uri uri;
    private Uri uri2;
    private File file;
    private String uriString = "https://script.google.com/macros/s/AKfycbz5975d5935O8BiFdiiUf_lnNq0dj1HQ3iwr9mEBITURGT919HW/exec?id=18ZMzFzEZUYBeotZDz6HtAGh5Ox-8z4oWPFQNTjjYn8Y&sheet=Sheet1";
    //private final String uriString = "https://script.google.com/macros/s/AKfycbz5975d5935O8BiFdiiUf_lnNq0dj1HQ3iwr9mEBITURGT919HW/exec";
    //private String jsonResultConvertedToString;
    private SharedPreferences sharedPreferences;
    public static final String JSON_KEY = "JSON_OBJECT_CONVERTED_TO_STRING";
    private Products products;
    private String json;

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("JSONRESPONSE", json);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        json = savedInstanceState.getString("JSONRESPONSE", "");
        productArrayList1 = parseJSON(json);

        productAdapter = new ProductAdapter(MainActivity.this, productArrayList1, "");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(productAdapter);

        progressBar.setVisibility(View.GONE);

        DividerItemDecoration decoration2 = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(decoration2);
        recyclerView1 = recyclerView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.logo);
        recyclerView = findViewById(R.id.product_recycler_view);
        progressBar = findViewById(R.id.lo);
        //progressBar_layout = findViewById(R.id.lo_layout);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        productArrayList = new ArrayList<>();
        productArrayList1 = new ArrayList<>();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (productArrayList1.size() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }, 4000);

        productAdapter = new ProductAdapter(MainActivity.this, productArrayList1, "");
        json = sharedPreferences.getString(JSON_KEY, "");
        monitorNetwork();

        if (isOnline()) {
            downloadJSON();
        } else {
            networkDialog();
        }
    }

    private void downloadJSON() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {

                uri = Uri.parse(uriString);
                URL urii = null;
                try {
                    urii = new URL(uri.toString());
                } catch (MalformedURLException exception) {
                    Log.e("URL_CREATION", "Error creating URL", exception);
                }

                String jsonResponse = "";
                try {
                    jsonResponse = getResponseFromHttpUrl(urii);
                    json = jsonResponse;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i("JSonRESPONSE", jsonResponse);

                return jsonResponse;
            }

            @Override
            protected void onPostExecute(String jsonResponse) {
                super.onPostExecute(jsonResponse);

                jsonResponse = jsonResponse.replace("(", " ");
                jsonResponse = jsonResponse.replace(")", " ");
                if (productArrayList1.size() > 0) {
                    productAdapter.resetAdapter();
                }
                productArrayList1 = parseJSON(jsonResponse);

                productAdapter = new ProductAdapter(MainActivity.this, productArrayList1, "");
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(productAdapter);

                DividerItemDecoration decoration2 = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.HORIZONTAL);
                recyclerView.addItemDecoration(decoration2);
                recyclerView1 = recyclerView;

                progressBar.setVisibility(View.GONE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(JSON_KEY, jsonResponse);
                editor.apply();
            }
        }.execute();
    }

    private String jsn = "{\"yooms\":{\"ProductName\":[\"Snail\",\"Red Oil 5kg\",\"Local Food\",\"Chips\",\"Minced Pie\",\"Sausage Roll\",\"Red Oil 2.5kg\",\"Chin Chin\"],\"ProductPrice\":[2500,1500,1500,1500,1500,1500,1500,1800],\"ProductDescription\":[\"dkkdieofdjfd\",\"dkkdieofdjfd\",\"dkkdieofdjfd\",\"dkkdieofdjfd\",\"dkkdieofdjfd\",\"dkkdieofdjfd\",\"dkkdieofdjfd\",\"fhgheiieee\"],\"ProductShortDescription\":[\"wuwuuue\",\"deeiid\",\",lhkkgkgkgkkfgfgf\",\"wuwuuue\",\"wuwuuue\",\"wuwuuue\",\"wuwuuue\",\"dkldldlwwlldd\"],\"Quantity\":[\"2kg\",\"5kg\",\"1 Plate\",\"0.5kg\",\"1kg\",\"0.5kg\",\"2.5kg\",\"1kg\"],\"PictureUrl\":[\"https://drive.google.com/file/d/1v0WPr9iaW4wCmBxLT4lPrrBrvB-eScc5/view?usp=sharing\",\"https://drive.google.com/file/d/1Uz4K9r2xxO6hgC7Hb_dRKd6NsbmAxOwU/view?usp=sharing\",\"https://drive.google.com/file/d/1Cisijfjmb70E2u3-sFuI1vPIExG10Bp_/view?usp=sharing\",\"https://drive.google.com/file/d/1rV_OQMIrzRSxvILeDHXGmAPTq2-drU9z/view?usp=sharing\",\"https://drive.google.com/file/d/1MzXWYEgaGCOAbohkYBcd-zhLaRikgab8/view?usp=sharing\",\"https://drive.google.com/file/d/1Yuc3m8Nf5fS9epFPV4PovJS7nW1AuKvc/view\",\"https://drive.google.com/file/d/1uoSkylwOFsyjBJFHSr6j7vtjHL89nZ80/view?usp=sharing\",\"https://drive.google.com/file/d/1yj3-IhGMFsyvR648unVP9SKPSBTzQ8oy/view?usp=sharing\"]}}";

    private String jsn2 = "{\"page\":1,\"per_page\":10,\"total\":79,\"total_pages\":8,\"data\":[{\"id\":1,\"userId\":1,\"userName\":\"John Oliver\",\"timestamp\":1549536882071,\"txnType\":\"debit\",\"amount\":\"$1,670.57\",\"location\":{\"id\":7,\"address\":\"770, Deepends, Stockton Street\",\"city\":\"Ripley\",\"zipCode\":44139},\"ip\":\"212.215.115.165\"}";
    public ArrayList<Product> parseJSON(String json) {
        Products products = new Products();
        try {
            //JSONObject jsonParser = (JSONObject) new JsonParser().parse(jsn2);
            JSONObject mainJSONObject = new JSONObject(json);
            JSONObject childJSONObject = mainJSONObject.getJSONObject("yooms");
            for (Iterator<String> it = childJSONObject.keys(); it.hasNext(); ) {
                String s = it.next();
                Log.i("CHILDOBJECT", s);
            }

            Log.i("CHILDOBJECT1", childJSONObject.getString("ProductName"));
            Log.i("CHILDOBJECT2", String.valueOf(childJSONObject.getJSONArray("ProductName")));

            ArrayList<String> prodName = new ArrayList<>();
            ArrayList<Integer> prodPrice = new ArrayList<>();
            ArrayList<String> prodDescription = new ArrayList<>();
            ArrayList<String> prodShortDescription = new ArrayList<>();
            ArrayList<String> qt = new ArrayList<>();
            ArrayList<String> pictUrl = new ArrayList<>();


            JSONArray productName = childJSONObject.getJSONArray("ProductName");
            JSONArray productDescription = childJSONObject.getJSONArray("ProductDescription");
            JSONArray productShortDescription = childJSONObject.getJSONArray("ProductShortDescription");
            JSONArray productPrice = childJSONObject.getJSONArray("ProductPrice");
            JSONArray quantity = childJSONObject.getJSONArray("Quantity");
            JSONArray pictureUrl = childJSONObject.getJSONArray("PictureUrl");

            JSONArray[] sObject = new JSONArray[]{productName, productDescription,
                    productShortDescription, productPrice, quantity, pictureUrl};
            ArrayList<String> list = new ArrayList<>();
            for (JSONArray ja : sObject) {
                if (list.size() != 0) {
                    if (ja == productDescription) {
                        prodName.addAll(list);
                        products.setProductName(prodName);
                        //Log.i("RTRRR", prodName.get(0));
                        list.clear();
                    } else if (ja == productShortDescription) {
                        prodDescription.addAll(list);
                        products.setProductDescription(prodDescription);
                        list.clear();
                    } else if (ja == productPrice) {
                        prodShortDescription.addAll(list);
                        products.setProductShortDescription(prodShortDescription);
                        list.clear();
                    } else if (ja == quantity) {
                        for (String s : list) {
                            prodPrice.add(Integer.parseInt(s));
                            //Log.i("RTRRRss", String.valueOf(s));
                        }
                        products.setProductPrice(prodPrice);
                        list.clear();
                    } else if (ja == pictureUrl) {
                        qt.addAll(list);
                        products.setQuantity(qt);
                        //Log.i("RTRRRs", qt.get(0));
                        list.clear();
                    }
                }
                list = new ArrayList<>(ja.length());
                for (int i = 0; i < ja.length(); i++) {
                    if (ja == pictureUrl) {
                        pictUrl.add(ja.getString(i));
                        products.setPictureUrl(pictUrl);
                    }
                    list.add(ja.getString(i));
                    //Log.i("JSONSTRINGS", String.valueOf(pictureUrl.get(0)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return marshallProducts(products);
    }

    private ArrayList<Product> marshallProducts(Products products) {
        Product product = new Product();
        if (products.getProductName() != null) {
            for (int i = 0; i <= products.getProductName().size() - 1; i++) {
                String[] p = products.getPictureUrl().get(i).split("/");
                String imageLink = "https://drive.google.com/uc?export=download&id=" + p[5];
                urllinks.add(imageLink);

                bitmaps = new Bitmap[products.getProductName().size()];
                getBitmaps(imageLink, i);
                if (recyclerView.getAdapter() != null)
                    recyclerView.getAdapter().notifyDataSetChanged();

                product = new Product(products.getProductPrice().get(i), products.getProductName().get(i),
                        products.getProductDescription().get(i), imageLink,
                        products.getQuantity().get(i), products.getProductShortDescription().get(i));
                productArrayList.add(product);
            }
        }

        return productArrayList;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void monitorNetwork() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isOnline()) {
                    networkDialog();
                    return;
                } else {
                    //downloadJSON();
                    monitorNetwork();
                }
            }
        }, 200);
    }

    public void networkDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("You are offline, please connect to the internet")
                //.setMessage(getResources().getString(R.string.internet_error))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monitorNetwork();
                        if (isOnline()) downloadJSON();
                    }
                }).setCancelable(false).show();
    }

    /**
     * saveImage() gets and saves images to SD card or internal memory of the device
     *
     * @param fileName
     * @param image
     * @throws FileNotFoundException
     */
    public static void saveImage(String fileName, ImageView image) throws FileNotFoundException {

        fileName = fileName.replaceAll(" ", "_");
        Bitmap bitmapImage = ((BitmapDrawable) image.getDrawable()).getBitmap();
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/Yooms/");
        dir.mkdirs();

        File file = new File(dir + "/" + fileName + ".webp/");
        Log.i("File path ", "File Path " + file);

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.WEBP, 100, out);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void getBitmaps(final String uriString2, final int position) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                //URL url = urls[0];

                uri2 = Uri.parse(uriString2);
                URL urii = null;
                try {
                    urii = new URL(uri2.toString());
                } catch (MalformedURLException exception) {
                    Log.e("URL_CREATION", "Error creating URL", exception);
                }

                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) urii.openConnection();
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    return BitmapFactory.decodeStream(bufferedInputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                bitmaps[position] = bitmap;
                if (position == urllinks.size() - 1) {
                    recyclerView.setAdapter(productAdapter);
                }
            }
        }.execute();
    }
}


//Script
//https://script.googleusercontent.com/macros/echo?user_content_key=Ax80eeq_HyNOTrKjGjm0rMm9n2uHwu4cui7kU9SQbKpjq05yMxvZtiVJ-wqXkaIvdElTIRPX5YIXhHPPdrhD0ps9Jpe0gWOum5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnBMQ9AJP0bcf6A42Ny8ACvK1B7Zupzmv5_kQK2BQ2kiKtZr5TIbVAkWb7EdlCL9odw&lib=M6M4rDSUdsI5SHeTHAHDgJhdLP8qXaVb3
//https://script.googleusercontent.com/macros/echo?user_content_key=pIBV6s6guTuol17OicmIgx-rO4gwunJnNVM9LrjdwSED1SCOzPqvYC3WpVH1wYn58u438JdFF5po_f5WdyYmgEMQZp4Na7e6m5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnBMQ9AJP0bcf6A42Ny8ACvK1B7Zupzmv5_kQK2BQ2kiKtZr5TIbVAkWb7EdlCL9odw&lib=M6M4rDSUdsI5SHeTHAHDgJhdLP8qXaVb3
//https://script.google.com/macros/s/AKfycbz5975d5935O8BiFdiiUf_lnNq0dj1HQ3iwr9mEBITURGT919HW/exec


//Google Sheet
//https://docs.google.com/spreadsheets/d/18ZMzFzEZUYBeotZDz6HtAGh5Ox-8z4oWPFQNTjjYn8Y/edit?usp=sharing

//Yemisi Photo Folder
//https://drive.google.com/drive/folders/1Jsf_810gIdfxmIYJJnesOeUupt_Oam5v?usp=sharing