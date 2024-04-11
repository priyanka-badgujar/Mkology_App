package com.mkology.thelearningapp.helper;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.mkology.thelearningapp.apiCalls.CartApi;
import com.mkology.thelearningapp.apiCalls.PurchaseChaptersApi;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;

public class PurchaseChaptersHelper {

    private Context context;
    private List<PurchaseChaptersApi> purchaseChaptersList;
    private String data;
    private SaveSharedPreference saveSharedPreference;
    private boolean isRepeat;

    public PurchaseChaptersHelper (Context context) {
        this.context = context;
        saveSharedPreference = new SaveSharedPreference();
    }

    public List<PurchaseChaptersApi> getVideosData() {
        Gson gson = new Gson();
        String json = saveSharedPreference.getVideosData(context);

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<PurchaseChaptersApi>>() {
            }.getType();
            purchaseChaptersList = gson.fromJson(json, type);
        }
        return purchaseChaptersList;
    }

    public void updateVideosData(List<PurchaseChaptersApi> chaptersList) {
        List<PurchaseChaptersApi> chaptersArray = new ArrayList<>();
        if (chaptersList.size() > 0) {
            chaptersArray.add(chaptersList.get(0));
            int indexCount = 1;
            for(int i=1; i<chaptersList.size(); i++) {
                isRepeat = false;
                for (int j =0; j<indexCount; j++) {
                    if (chaptersArray.get(j).getChapterId().equals(chaptersList.get(i).getChapterId())) {
                        int count  = chaptersArray.get(j).getWatchCount() + 3;
                        chaptersArray.get(j).setWatchCount(count);
                        isRepeat = true;
                    }
                }
                if (!isRepeat) {
                    chaptersArray.add(chaptersList.get(i));
                    indexCount++;
                }
            }
            Gson gson = new Gson();
            data = gson.toJson(chaptersArray);
            if (context != null) {
                saveSharedPreference.setVideosData(context, data);
            }
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(chaptersList);
            SaveSharedPreference.setVideosData(context, json);
        }
    }

    public void insertNewPurchased(List<CartApi> chaptersList) {
        Gson gson = new Gson();
        String json = saveSharedPreference.getVideosData(context);

        if (!json.isEmpty()) {
            Type type = new TypeToken<List<PurchaseChaptersApi>>() {
            }.getType();
            purchaseChaptersList = gson.fromJson(json, type);
        }

        for (int i=0; i<chaptersList.size(); i++) {
            PurchaseChaptersApi purchaseChaptersApi = new PurchaseChaptersApi();
            purchaseChaptersApi.setChapterId(chaptersList.get(i).getChapterId());
            purchaseChaptersList.add(purchaseChaptersApi);
        }

        data = gson.toJson(purchaseChaptersList);
        saveSharedPreference.setVideosData(context, data);
    }

    public void updateWatchCount (String chapterId) {
        List<PurchaseChaptersApi> chaptersArray = getVideosData();
        for (int i=0; i<chaptersArray.size(); i++) {
            if (chapterId.equals(chaptersArray.get(i).getChapterId())) {
                int watchCount = chaptersArray.get(i).getWatchCount() -1;
                chaptersArray.get(i).setWatchCount(watchCount);
                break;
            }
        }
        Gson gson = new Gson();
        data = gson.toJson(chaptersArray);
        if (context != null) {
            saveSharedPreference.setVideosData(context, data);
        }
    }

    public void deletePurchasedChapter (String chapterId) {
        List<PurchaseChaptersApi> chaptersArray = getVideosData();
        for (int i=0; i<chaptersArray.size(); i++) {
            if (chapterId.equals(chaptersArray.get(i).getChapterId())) {
                chaptersArray.remove(i);
                break;
            }
        }
        Gson gson = new Gson();
        data = gson.toJson(chaptersArray);
        if (context != null) {
            saveSharedPreference.setVideosData(context, data);
        }
    }

}
