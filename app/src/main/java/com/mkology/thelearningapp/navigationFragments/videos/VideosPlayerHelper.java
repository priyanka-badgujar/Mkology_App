package com.mkology.thelearningapp.navigationFragments.videos;

import android.content.Context;
import android.widget.Toast;

import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.helper.ApplicationConstants;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.PurchaseChaptersHelper;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideosPlayerHelper {

    private Context context;
    private PurchaseChaptersHelper purchaseChaptersHelper;
    private int watchCounter;
    private ErrorHelper errorHelper;

    public VideosPlayerHelper (Context context) {
        this.context = context;
        purchaseChaptersHelper = new PurchaseChaptersHelper(context);
        errorHelper = new ErrorHelper(context);
    }

    public void reduceWatchCount(String chapterId, int purchaseId, int watchCount) {
        watchCounter = watchCount;
        if (chapterId != ApplicationConstants.EMPTY && purchaseId != 0) {
            if (watchCount <=  1) {
                deleteChapter(chapterId);
            } else {
                updateWatchCount(chapterId, purchaseId);
            }
        }
    }

    private void deleteChapter(String chapterId) {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);

        Call<String> call = jsonPlaceHolderApi.deletePurchaseChapter(
                SaveSharedPreference.getEmail(context), chapterId);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context,"You won't be able to view this chapter next time unless you buy it again" , Toast.LENGTH_LONG).show();
                    purchaseChaptersHelper.deletePurchasedChapter(chapterId);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                errorHelper.errorMessage();
            }
        });
    }

    private void updateWatchCount(String chapterId, int purchaseId) {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);

        Call<String> call = jsonPlaceHolderApi.updateWatchCount(
                SaveSharedPreference.getEmail(context), chapterId, purchaseId);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context,"You can watch this chapter " + watchCounter +" times" , Toast.LENGTH_LONG).show();
                    purchaseChaptersHelper.updateWatchCount(chapterId);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                errorHelper.errorMessage();
            }
        });
    }

}
