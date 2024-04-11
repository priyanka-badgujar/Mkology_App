package com.mkology.thelearningapp.helper;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.mkology.thelearningapp.apiCalls.CartApi;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;

public class CartHelper {

    private Context context;
    private List<CartApi> cartListData, cartApiResponse;
    private int cartCount = 0, cartTotal = 0;
    private boolean isEmpty;

    public CartHelper (Context context) {
        this.context = context;
    }

    public void saveCartData(List<CartApi> cartApiList) {
        Gson gson = new Gson();
        String json = gson.toJson(cartApiList);
        SaveSharedPreference.setCartData(context, json);
        if (cartApiList != null) {
            cartCount= (cartApiList.size() > 0) ? cartApiList.size() : 0;
        }
        Intent intent = new Intent(ApplicationConstants.CART_BROADCAST);
        intent.putExtra(ApplicationConstants.CART_COUNT, String.valueOf(cartCount));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public List<CartApi> getCartData() {
        Gson gson = new Gson();
        String json = SaveSharedPreference.getCartData(context);

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<CartApi>>() {
            }.getType();
            cartListData = gson.fromJson(json, type);
        }
        return cartListData;
    }

    public int getCartTotal() {
        cartTotal = 0;
        cartApiResponse = getCartData();
        for (int i=0; i<cartApiResponse.size(); i++) {
            cartTotal = cartTotal + cartApiResponse.get(i).getChapterPrice();
        }
        return cartTotal;
    }

    public String getChapterNames() {
        cartApiResponse = getCartData();
        String chapterNames = cartApiResponse.get(0).getChapterId();
        for (int i=1; i<cartApiResponse.size(); i++) {
            chapterNames = chapterNames + ":" + cartApiResponse.get(i).getChapterId();
        }
        return chapterNames;
    }

    public int getCartCount() {
        Gson gson = new Gson();
        String json = SaveSharedPreference.getCartData(context);

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<CartApi>>() {
            }.getType();
            cartListData = gson.fromJson(json, type);
        }
        if (cartListData != null) {
            cartCount= (cartListData.size() > 0) ? cartListData.size() : 0;
        }
        return cartCount;
    }

    public boolean setOrRemoveFromCart(CartApi cartApi, boolean isAddData) {

        Gson gson = new Gson();
        String json = SaveSharedPreference.getCartData(context);

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<CartApi>>() {
            }.getType();
            cartListData = gson.fromJson(json, type);
        }

        if (isAddData) {
            if (cartListData != null) {
                cartListData.add(cartApi);
                json = gson.toJson(cartListData);
            }
            else {
                List<CartApi> cartArray = new ArrayList<>();
                cartArray.add(cartApi);
                json = gson.toJson(cartArray);
            }
        } else {
            for (int i=0; i<cartListData.size(); i++) {
                if (cartListData.get(i).getChapterId().equals(cartApi.getChapterId())) {
                    cartListData.remove(i);
                    if (cartListData.size()<1)
                        isEmpty = true;
                }
            }
            json = gson.toJson(cartListData);
        }

        if (cartListData != null) {
            cartCount= (cartListData.size() > 0) ? cartListData.size() : 0;
        } else {
            cartCount = 0;
        }

        Intent intent = new Intent(ApplicationConstants.CART_BROADCAST);
        intent.putExtra(ApplicationConstants.CART_COUNT, String.valueOf(cartCount));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        SaveSharedPreference.setCartData(context, json);
        return isEmpty;
    }


    public void removeCartData() {
        Intent intent = new Intent(ApplicationConstants.CART_BROADCAST);
        intent.putExtra(ApplicationConstants.CART_COUNT, String.valueOf(0));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        SaveSharedPreference.setCartData(context, "");
    }
}
