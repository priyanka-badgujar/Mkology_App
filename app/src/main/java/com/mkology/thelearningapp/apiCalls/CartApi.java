package com.mkology.thelearningapp.apiCalls;

public class CartApi {

    private String chapterId;
    private String chapterName;
    private int chapterPrice;
    private String chapterDescription;
    private boolean chapterVisible;
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

    public String getChapterDuration() {
        return chapterDuration;
    }

}
