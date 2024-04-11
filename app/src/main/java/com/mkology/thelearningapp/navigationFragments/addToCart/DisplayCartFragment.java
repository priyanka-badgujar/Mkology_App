package com.mkology.thelearningapp.navigationFragments.addToCart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.CartApi;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.helper.ApplicationConstants;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import com.mkology.thelearningapp.payment.PaymentActivity;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayCartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartRecyclerViewAdapter cartRecyclerViewAdapter;
    private Button buyNow;
    private RelativeLayout emptyCart;
    private View root;
    private List<CartApi> purchasedList;
    private List<CartApi> cartItemList;
    private SearchView searchView;
    private TextView countView, cartValue;
    private int cartTotal = 0, cartCount;
    private LinearLayout orderLayout;
    private CartHelper cartHelper;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_cart, container, false);
        cartRecyclerView = root.findViewById(R.id.cart_recyclerview);
        buyNow = root.findViewById(R.id.buy_now);
        emptyCart = root.findViewById(R.id.empty_cart);
        searchView = root.findViewById(R.id.action_search);
        cartValue = root.findViewById(R.id.cart_value);
        orderLayout = root.findViewById(R.id.order_layout);
        cartHelper = new CartHelper(getContext());
        progressbarLoading = new ProgressbarLoading(getContext());
        errorHelper = new ErrorHelper(getContext());
        setHasOptionsMenu(true);
        getCart();
        return root;
    }

    private void getCart() {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", SaveSharedPreference.getEmail(getContext()));
        progressbarLoading.showProgressBar();
        jsonPlaceHolderApi.getCartPageData(params).enqueue( new Callback<List<CartApi>>() {
            @Override
            public void onResponse(Call<List<CartApi>> call, Response<List<CartApi>> response) {
                progressbarLoading.hideProgressBar();
                if (response.isSuccessful()) {
                    List<CartApi> cartApiList = response.body();
                    cartHelper.removeCartData();
                    cartHelper.saveCartData(cartApiList);
                    if (cartApiList != null && cartApiList.size() > 0) {
                        emptyCart.setVisibility(View.GONE);
                        createCartRecyclerView(cartApiList);
                    } else {
                        searchView.setVisibility(View.GONE);
                        orderLayout.setVisibility(View.GONE);
                    }
                } else {
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

    private void createCartRecyclerView(final List<CartApi> cartApiResponse) {
        cartTotal = 0;
        for (int i=0; i<cartApiResponse.size(); i++) {
            cartTotal = cartTotal + cartApiResponse.get(i).getChapterPrice();
        }
        cartValue.setText(getContext().getResources().getText(R.string.rs_symbol)
                + String.valueOf(cartTotal));
        cartCount = cartApiResponse.size();
        purchasedList = cartApiResponse;
        cartRecyclerView.setVisibility(View.VISIBLE);
        cartRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        cartRecyclerViewAdapter = new CartRecyclerViewAdapter(cartApiResponse, emptyCart, buyNow,
                countView, cartCount, searchView, cartTotal, cartValue, orderLayout);
        cartRecyclerViewAdapter.setClickListener();
        cartRecyclerView.setAdapter(cartRecyclerViewAdapter);
        cartRecyclerViewAdapter.notifyDataSetChanged();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                cartRecyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });

        orderLayout.setVisibility(View.VISIBLE);
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyNow.setClickable(false);
                int total = cartHelper.getCartTotal();
                Intent intent = new Intent(getContext(), PaymentActivity.class);
                intent.putExtra("buyType", ApplicationConstants.CART_BUY);
                intent.putExtra("totalPrice", Integer.toString(total));
                intent.putExtra("chapterId", cartHelper.getChapterNames());
                startActivity(intent);
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        int cartCount = cartHelper.getCartCount();
        MenuItem filter = menu.findItem(R.id.title_bar);
        filter.setTitle(getResources().getString(R.string.cart_page));

        MenuItem cart = menu.getItem(1);
        cart.getActionView().setClickable(false);
        countView = menu.getItem(1).getActionView().findViewById(R.id.cartCount);

        if (cartCount > 0) {
            countView.setVisibility(View.VISIBLE);
            countView.setText(String.valueOf(cartCount));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<CartApi> cartApiList = cartHelper.getCartData();
        if (cartApiList != null && cartApiList.size() > 0) {
            emptyCart.setVisibility(View.GONE);
            createCartRecyclerView(cartApiList);
        } else {
            cartRecyclerView.setVisibility(View.GONE);
            buyNow.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
            orderLayout.setVisibility(View.GONE);
            if(countView != null)
                countView.setVisibility(View.GONE);
        }
    }
}
