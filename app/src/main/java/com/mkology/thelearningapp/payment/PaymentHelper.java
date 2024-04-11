package com.mkology.thelearningapp.payment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import java.util.List;

import com.mkology.thelearningapp.HomeActivity;
import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.CartApi;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.helper.ApplicationConstants;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.helper.CustomizeAlertDialogs;
import com.mkology.thelearningapp.helper.PurchaseChaptersHelper;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentHelper {

    //private Context context;
    private Activity activity;
    private String[] chapterIds;
    private CustomizeAlertDialogs customizeAlertDialogs;
    private CardView successCard;

    public PaymentHelper(Activity activity, CardView successCard) {
        this.activity = activity;
        customizeAlertDialogs = new CustomizeAlertDialogs();
        this.successCard = successCard;
    }

    public void buyChapters() {
        CartHelper cartHelper = new CartHelper(activity);
        List<CartApi> chaptersList = cartHelper.getCartData();

        chapterIds = new String[chaptersList.size()];
        for (int i = 0; i < chaptersList.size(); i++) {
            chapterIds[i] = chaptersList.get(i).getChapterId();
        }

        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Call<String> call = jsonPlaceHolderApi.insertPurchasedChapters(
                SaveSharedPreference.getEmail(activity), chapterIds);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    PurchaseChaptersHelper purchaseChaptersHelper = new PurchaseChaptersHelper(activity);
                    purchaseChaptersHelper.insertNewPurchased(chaptersList);
                    deleteCartChapters();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteCartChapters() {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Call<String> call = jsonPlaceHolderApi.deleteCartChapters(
                SaveSharedPreference.getEmail(activity), chapterIds);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    CartHelper cartHelper = new CartHelper(activity);
                    cartHelper.removeCartData();
                    goToSuccessPage();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void purchaseSubject(String subjectId) {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);

        Call<String> call = jsonPlaceHolderApi.purchaseSubject(
                SaveSharedPreference.getEmail(activity), subjectId);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    goToSuccessPage();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void purchaseCourses(String courseId) {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);

        Call<String> call = jsonPlaceHolderApi.purchaseCourse(
                SaveSharedPreference.getEmail(activity), courseId);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    goToSuccessPage();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goToSuccessPage() {
        successCard.setVisibility(View.VISIBLE);
        Button backButton = successCard.findViewById(R.id.successButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("successPurchase", ApplicationConstants.SUCCESS_PURCHASE);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
            }
        });
    }

}
