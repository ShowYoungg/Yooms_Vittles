package com.soyombo.yoomsvittles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.soyombo.yoomsvittles.ProductAdapter.cart;

public class CheckOutActivity extends AppCompatActivity {

    private TextView cartList;
    private Button transfer;
    private Button ussd;
    private Button card;
    private final int CALL_REQUEST_CODE = 100;
    private String accountNumber = "0153303426";
    private String code;
    private ArrayList<String> codeList;
    private int position;
    private RadioGroup radioGroup;
    private RadioButton radioButton, defaultRadioButton;
    private EditText addressText;
    private EditText emailText;
    private EditText phoneNumberText;
    private int deliveryFee = 0;
    private int total2 = 0;
    private String customerEmail;
    private String customerPhone;
    private String deliveryAddress;

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
        setContentView(R.layout.activity_check_out);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        codeList = new ArrayList<>();
        cartList = findViewById(R.id.cart_list);
        transfer = findViewById(R.id.button2);
        ussd = findViewById(R.id.button3);
        card = findViewById(R.id.button4);
        radioGroup = findViewById(R.id.address);
        addressText = findViewById(R.id.address_text);
        emailText = findViewById(R.id.contact_email);
        phoneNumberText = findViewById(R.id.contact_phone);
        defaultRadioButton = findViewById(R.id.pick_up_location);
        defaultRadioButton.isChecked();

        int totalPrice = 0;

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = (RadioButton) findViewById(checkedId);
                if (radioButton.getText().toString().startsWith("Choose")) {
                    addressText.setVisibility(View.VISIBLE);
                    addressText.setText("");
                    deliveryFee += 1500;
                    populateCodeList(total2);
                    //deliveryAddress = addressText.getText().toString();
                    cartList.append("Delivery: " + deliveryFee + "\n");
                    cartList.append("Total: " + (deliveryFee + total2));

                } else {
                    addressText.setVisibility(View.GONE);
                    cartList.setText("");
                    deliveryFee = 0;
                    //deliveryAddress = "Our pick up station at Akute B/Stop, Opp. First Bank, Akute";
                    rePopulateCartList(total2);
                    populateCodeList(total2);
                }
            }
        });

        for (CartItem c : cart) {
            cartList.append(c.getProduct().getProductName() + " : " + c.getProduct().getPrice() + " X " + c.getNumber()
                    + " = " + (c.getProduct().getPrice() * c.getNumber()) + "\n");

            totalPrice += (c.getProduct().getPrice() * c.getNumber());
            total2 = totalPrice;
        }

        cartList.append("Sub Total: " + String.valueOf(totalPrice) + "\n");

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CheckOutActivity.this, "Payment by card not available at the moment", Toast.LENGTH_SHORT).show();
            }
        });

        transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerEmail = emailText.getText().toString();
                customerPhone = phoneNumberText.getText().toString().trim();

                if (addressText.getVisibility() == View.VISIBLE && addressText.getText().toString().equals("")) {

                    Toast.makeText(CheckOutActivity.this, "Please input a valid and accurate contact address", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkInputDialog(customerEmail, customerPhone);

                if (isEmailValid(customerEmail) && isPhoneValid(customerPhone)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CheckOutActivity.this);
                    dialog.setTitle(getResources().getString(R.string.app_name));
                    dialog.setMessage("Please make a transfer into: " + "\n" + "BANK: " + "\n" + "GUARANTEE TRUST BANK \n" +
                            "ACCOUNT NUMBER: " + "\n" + "0153303426" + "\n"
                            + "ACCOUNT NAME: " + "\n" + "OLAOSEBIKAN YEMISI MARIA");
                    dialog.setPositiveButton("COPY ACCOUNT NUMBER", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendMail(customerEmail, customerPhone);
                            copyToClipboard();
                            Toast.makeText(CheckOutActivity.this, "Account number copied", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });

        populateCodeList(totalPrice);

        ussd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerEmail = emailText.getText().toString();
                customerPhone = phoneNumberText.getText().toString().trim();

                if (addressText.getVisibility() == View.VISIBLE && addressText.getText().toString().equals("")) {
                    Toast.makeText(CheckOutActivity.this, "Please input a valid and accurate contact address", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkInputDialog(customerEmail, customerPhone);
                if (isEmailValid(customerEmail) && isPhoneValid(customerPhone)) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckOutActivity.this);
                    alertDialog.setTitle(getResources().getString(R.string.app_name));
                    String[] banks = {"GT Bank", "FCMB", "Access Bank", "Zenith Bank", "UBA", "Fidelity Bank", "Polaris Bank", "Keystone Bank", "Stanbic IBTC"};
                    alertDialog.setItems(banks, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            position = which;
                            Toast.makeText(CheckOutActivity.this, codeList.get(which), Toast.LENGTH_SHORT).show();
                            dialNumber(codeList.get(which));
                            sendMail(customerEmail, customerPhone);
                        }
                    });

                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALL_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dialNumber(codeList.get(position));
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void rePopulateCartList(int totalPrice) {
        totalPrice = 0;
        for (CartItem c : cart) {
            cartList.append(c.getProduct().getProductName() + " : " + c.getProduct().getPrice() + " X " + c.getNumber()
                    + " = " + (c.getProduct().getPrice() * c.getNumber()) + "\n");

            totalPrice += (c.getProduct().getPrice() * c.getNumber());
            total2 = totalPrice;
        }
        cartList.append("Sub Total: " + String.valueOf(totalPrice) + "\n");
    }


    private void dialNumber(String code) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + code));
        if (ActivityCompat.checkSelfPermission(CheckOutActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CheckOutActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST_CODE);
            return;
        }
        startActivity(callIntent);
    }

    private void populateCodeList(int totalPrice) {
        if (codeList.size() > 0) {
            codeList.clear();
            codeList = new ArrayList<>();
        }

        codeList.add("*737*2*" + (totalPrice + deliveryFee) + "*" + accountNumber + Uri.encode("#"));
        codeList.add("*329*" + (totalPrice + deliveryFee) + "*" + accountNumber + Uri.encode("#"));
        codeList.add("*901*" + (totalPrice + deliveryFee) + "*" + accountNumber + Uri.encode("#"));
        codeList.add("*939*" + (totalPrice + deliveryFee) + "*" + accountNumber + Uri.encode("#"));
        codeList.add("*919*4*" + accountNumber + "*" + (totalPrice + deliveryFee) + Uri.encode("#"));
        codeList.add("*770*" + accountNumber + "*" + (totalPrice + deliveryFee) + Uri.encode("#"));
        codeList.add("*833*" + (totalPrice + deliveryFee) + "*" + accountNumber + Uri.encode("#"));
        codeList.add("*7111*" + (totalPrice + deliveryFee) + "*" + accountNumber + Uri.encode("#"));
        codeList.add("*909*22*" + (totalPrice + deliveryFee) + "*" + accountNumber + Uri.encode("#"));
    }

    public void sendMail(final String email, final String phone) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (addressText.getVisibility() == View.VISIBLE){
                        deliveryAddress = addressText.getText().toString();
                    } else {
                        deliveryAddress = "Our pick up station at Akute B/Stop, Opp. First Bank, Akute";
                    }
                    MailSender sender = new MailSender(Config.EMAIL,
                            Config.PASSWORD);
                    //A mail to be sent to the admin
                    sender.sendMail("YOOM'S VITTLES PRODUCT ORDER", "Below are the details of products ordered by "
                                    + emailText.getText().toString() + " " + phoneNumberText.getText().toString() + "\n" + "\n"
                                    + cartList.getText().toString() + "\n" + "\n" + "Contact Address: \n" + deliveryAddress + "\n\n" + "Please check your account statement, reconcile, confirm the payment" +
                                    " in like sum has been remitted into your account and follow up on the customer by a call or email on " + email + " or " + phone +
                                    " to confirm the order. " +
                                    "The customer has been notified the delivery fee could change due to differences in weights and locations; hence, review the " +
                                    "delivery fee and communicate any change in fee, if any, to the customer",
                            Config.EMAIL, "mydove.enterprise@gmail.com");

                    //A mail to be sent to the customer;
                    sender.sendMail("NOTIFICATION OF ORDERS MADE AT YOOM'S VITTLES", "Thanks for doing business with us, kindly find " +
                                    "below the details of the products ordered \n \n" +
                                    cartList.getText().toString() + "\n" + "\n" + "The ordered items would be delivered to " +
                                    deliveryAddress + " if your transaction is approved. " + "You will be contacted by our representative if your transaction is approved " +
                                    "or delivery fee changes. Delivery fee could change due to differences in weights and locations \n\n" + "Best Regards",
                            Config.EMAIL, emailText.getText().toString());
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }

    private boolean isEmailValid(String email) {
        if (email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);

        if (pat.matcher(email).matches()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isPhoneValid(String phone) {

        //Toast.makeText(this, ""+phone.length(), Toast.LENGTH_SHORT).show();
        if (phone.length() != 11) return false;
        for (int i = 0; i < phone.length(); i++) {
            Boolean flag = Character.isDigit(phone.charAt(i));
            if (!flag) return false;
        }
        return true;
    }

    private void checkInputDialog(String email, String phone) {
        if (!isEmailValid(email) || !isPhoneValid(phone)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CheckOutActivity.this);
            dialog.setTitle(getResources().getString(R.string.app_name));
            if (!isEmailValid(email) && !isPhoneValid(phone)) dialog.setMessage("Invalid email and phone");
            if (!isEmailValid(email)) dialog.setMessage("Invalid email");
            if (!isPhoneValid(phone)) dialog.setMessage("Invalid phone. Contact phone number has to mobile e.g 0X012354578");
            //if (phone.length() != 11) dialog.setMessage("Contact phone number has to mobile e.g 0X012354578");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void copyToClipboard(){
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Account Number", "0153303426");
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }
}
