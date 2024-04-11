package com.mkology.thelearningapp.loginPage;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mkology.thelearningapp.HomeActivity;
import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.apiCalls.UserProfile;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPresenter implements MainContract.MainPresenter {

    private final MainContract.MainView view;
    private Context context;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;

    public MainPresenter(MainContract.MainView view, Context context) {
        this.view = view;
        this.view.setPresenter(this);
        this.context = context;
        progressbarLoading = new ProgressbarLoading(context);
        errorHelper = new ErrorHelper(context);
    }

    public void checkUserAuthentication(final String mail, String pwd,
                                        final TextView errorText) {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mail);
        params.put("password", pwd);
        final String pass = pwd;
        progressbarLoading.showProgressBar();
        jsonPlaceHolderApi.getUserProfile(params).enqueue( new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                progressbarLoading.hideProgressBar();
               if (response.isSuccessful()) {
                   List<UserProfile> userProfiles = response.body();
                   UserProfile userProfile = userProfiles.get(0);
                   if (userProfile.getError() == null) {
                       SaveSharedPreference.setUserData(context, userProfile);
                       SaveSharedPreference.setLoggedIn(context, true);
                       Intent intent = new Intent(context, HomeActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       context.startActivity(intent);
                   } else if (pass == "") {
                       goForMobileAuthentication(context);
                   } else {
                       errorText.setVisibility(View.VISIBLE);
                       errorText.setText(userProfile.getError());
                   }
               }
               else {
                   progressbarLoading.hideProgressBar();
                   errorHelper.errorMessage();
               }
            }

            @Override
            public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
            }
        });
    }

    @Override
    public void goForMobileAuthentication(Context context) {
        Intent intent = new Intent(context, MobileAuthActivity.class);
        intent.putExtra("email", SaveSharedPreference.getEmail(context));
        intent.putExtra("name", SaveSharedPreference.getName(context));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
