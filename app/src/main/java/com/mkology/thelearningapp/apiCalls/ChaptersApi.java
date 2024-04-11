package com.mkology.thelearningapp.apiCalls;

public class ChaptersApi {

    private String subjectId;
    private String chapterId;
    private String chapterName;
    private int chapterPrice;
    private String chapterDescription;
    private boolean isInCart;
    private boolean isPurchased;
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

    public boolean isInCart() {
        return isInCart;
    }

    public void setInCart(boolean inCart) {
        this.isInCart = inCart;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean isPurchased) {
        this.isPurchased = isPurchased;
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

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getChapterDuration() {
        return chapterDuration;
    }
}
