package com.mkology.thelearningapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.CartApi;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.apiCalls.PurchaseChaptersApi;
import com.mkology.thelearningapp.helper.ApplicationConstants;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.helper.CustomizeAlertDialogs;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import com.mkology.thelearningapp.helper.PurchaseChaptersHelper;
import com.mkology.thelearningapp.loginPage.MainActivity;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnBackPressedListener {

    private TextView cartCount;
    private NavController navController;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;

    private final BroadcastReceiver cartDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String countData = intent.getStringExtra(ApplicationConstants.CART_COUNT);
            if (cartCount != null) {
                cartCount.setVisibility(View.VISIBLE);
                cartCount.setText(countData);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);
        if(!SaveSharedPreference.getLoggedStatus(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0,0);
        } else {
            setContentView(R.layout.activity_home);
            LocalBroadcastManager.getInstance(this).registerReceiver(cartDataReceiver,
                    new IntentFilter(ApplicationConstants.CART_BROADCAST));
            progressbarLoading = new ProgressbarLoading(HomeActivity.this);
            errorHelper = new ErrorHelper(HomeActivity.this);
            progressbarLoading.showProgressBar();
            getVideosPages();
        }
        cartCount = findViewById(R.id.cartCount);
    }

    private void getHomePage() {

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration.Builder builder = new AppBarConfiguration.Builder(
                R.id.navigation_courses, R.id.navigation_videos, R.id.navigation_about, R.id.navigation_profile);
        builder.setFallbackOnNavigateUpListener(new AppBarConfiguration.OnNavigateUpListener() {
            @Override
            public boolean onNavigateUp() {
                return onSupportNavigateUp();
            }
        });
        AppBarConfiguration appBarConfiguration = builder.build();

        progressbarLoading.hideProgressBar();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.popBackStack();
        return true;
    }


    private void getVideosPages() {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", SaveSharedPreference.getEmail(getApplicationContext()));
        jsonPlaceHolderApi.getVideosData(params).enqueue( new Callback<List<PurchaseChaptersApi>>() {
            @Override
            public void onResponse(Call<List<PurchaseChaptersApi>> call, Response<List<PurchaseChaptersApi>> response) {
                if (response.isSuccessful()) {
                    List<PurchaseChaptersApi> purchaseChaptersList = response.body();
                    PurchaseChaptersHelper purchaseChaptersHelper = new PurchaseChaptersHelper(getApplicationContext());
                    purchaseChaptersHelper.updateVideosData(purchaseChaptersList);
                    getCartPage();
                } else {
                    progressbarLoading.hideProgressBar();
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<List<PurchaseChaptersApi>> call, Throwable t) {
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
            }
        });
    }

    private void getCartPage() {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", SaveSharedPreference.getEmail(getApplicationContext()));
        jsonPlaceHolderApi.getCartChaptersData(params).enqueue( new Callback<List<CartApi>>() {
            @Override
            public void onResponse(Call<List<CartApi>> call, Response<List<CartApi>> response) {
                if (response.isSuccessful()) {
                    List<CartApi> cartApiList = response.body();
                    CartHelper cartHelper = new CartHelper(getApplicationContext());
                    cartHelper.saveCartData(cartApiList);
                    getHomePage();
                } else {
                    progressbarLoading.hideProgressBar();
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<List<CartApi>> call, Throwable t) {
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.header_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            final MenuItem item = menu.getItem(i);
            View actionView = MenuItemCompat.getActionView(item);
            if (actionView != null) {
                actionView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        onOptionsItemSelected(item);
                    }
                });
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_cart:
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.navigation_cart);
                return true;
            default:
                break;
        }

        return false;
    }
}
