package com.mkology.thelearningapp.apiCalls;

public class PurchaseChaptersApi {

    private String chapterId;
    private String chapterName;
    private int chapterPrice;
    private String chapterDescription;
    private boolean chapterVisible;
    private String chapterLink;
    private int watchCount;
    private int purchaseId;
    private String chapterDuration;

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public int getChapterPrice() {
        return chapterPrice;
    }

    public String getChapterDescription() {
        return chapterDescription;
    }

    public String getChapterLink() {
        return chapterLink;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public String getChapterDuration() {
        return chapterDuration;
    }

}
