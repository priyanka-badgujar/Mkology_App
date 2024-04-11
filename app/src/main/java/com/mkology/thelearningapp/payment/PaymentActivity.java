package com.mkology.thelearningapp.payment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.helper.ApplicationConstants;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
    //declare paymentParam object
    PayUmoneySdkInitializer.PaymentParam paymentParam = null;
    String TAG = "PaymentActivity";

    private String merchantId = "7233513";
    private String merchantkey = "OHSERnAO";
    private String firstname;
    private String amount;
    private String email;
    private String phone;
    private String productname;
    private String txnid;
    private String buyType;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;
    private CardView successCard, failureCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        successCard = findViewById(R.id.paymentSuccess);
        failureCard = findViewById(R.id.paymentFailure);

        progressbarLoading = new ProgressbarLoading(PaymentActivity.this);
        errorHelper = new ErrorHelper(getApplicationContext());
        Intent intent = getIntent();
        if (intent != null) {
            amount = intent.getStringExtra("totalPrice");
            buyType = intent.getStringExtra("buyType");
            if (buyType.equals(ApplicationConstants.SUBJECT_BUY)) {
                productname = intent.getStringExtra("subjectId");
            } else if (buyType.equals(ApplicationConstants.COURSE_BUY)) {
                productname = intent.getStringExtra("courseId");
            } else if (buyType.equals(ApplicationConstants.CART_BUY)) {
                productname = intent.getStringExtra("chapterId");
            }
        }
        email = SaveSharedPreference.getEmail(getApplicationContext());
        firstname = SaveSharedPreference.getName(getApplicationContext());
        phone = SaveSharedPreference.getMobile(getApplicationContext());
        txnid = getTxnId();
        startpay();
    }

    public void startpay() {

        builder.setAmount(amount)                    // Payment amount
                .setTxnId(txnid)                     // Transaction ID
                .setPhone(phone)                     // User Phone number
                .setProductName(productname)            // Product Name or description
                .setFirstName(firstname)             // User First name
                .setEmail(email)                     // User Email ID
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")     // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")     //Failure URL (furl)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(true)                              // Integration environment - true (Debug)/ false(Production)
                .setKey(merchantkey)                        // Merchant key
                .setMerchantId(merchantId);


        try {
            paymentParam = builder.build();
            getHashkey();
        } catch (Exception e) {
            Log.e(TAG, " error s " + e.toString());
            errorHelper.errorMessage();
        }

    }

    private String getTxnId() {
        return ("0nf7mk56" + System.currentTimeMillis());
    }

    public void getHashkey() {
        ServiceWrapper serviceWrapper = new ServiceWrapper(null);
        progressbarLoading.showProgressBar();
        serviceWrapper.newHashCall(merchantkey, txnid, amount, productname,
                firstname, email).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressbarLoading.hideProgressBar();
                Log.e(TAG, "hash res " + response.body());
                String merchantHash = response.body();
                if (merchantHash.isEmpty() || merchantHash.equals("")) {
                    Toast.makeText(PaymentActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "hash empty");
                    errorHelper.errorMessage();
                } else {
                    paymentParam.setMerchantHash(merchantHash);
                    PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam,
                            PaymentActivity.this, R.style.PaymentTheme_Theme, true);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "hash error " + t.toString());
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("StartPaymentActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    PaymentHelper paymentHelper = new PaymentHelper(PaymentActivity.this, successCard);
                    if (ApplicationConstants.CART_BUY.equals(buyType)) {
                        paymentHelper.buyChapters();
                    } else if (ApplicationConstants.SUBJECT_BUY.equals(buyType)) {
                        paymentHelper.purchaseSubject(productname);
                    } else if (ApplicationConstants.COURSE_BUY.equals(buyType)) {
                        paymentHelper.purchaseCourses(productname);
                    }
                } else {
                    //Failure Transaction
                    failureCard.setVisibility(View.VISIBLE);
                    Button back = findViewById(R.id.failureButton);
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
//                    finish();
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                Log.e(TAG, "tran " + payuResponse + "---" + merchantResponse);
            }
        } else {
            finish();
        }
    }


}
