package com.mkology.thelearningapp.apiCalls;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface JsonPlaceHolderApi {

    //Get Courses
    @GET("CoursesApi")
    Call<List<CoursesApi>> getCoursesApi();


    //Get Subjects
    @GET("SubjectsApi")
    Call<List<SubjectsApi>> getSubjectsApi(@QueryMap Map<String, String> params);

    //Get chapters
    @GET("ChaptersApi")
    Call<List<ChaptersApi>> getChaptersApi(@QueryMap Map<String, String> params);

    //Get User data
    @Headers("Content-Type: application/json")
    @GET("UserProfile")
    Call<List<UserProfile>> getUserProfile(@QueryMap Map<String, String> params);


    //Create user and insert data
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("UserProfile")
    Call<String> insertUser(
            @Field("email") String email,
            @Field("password") String password,
            @Field("name") String name,
            @Field("mobile") String mobile,
            @Field("place") String place
    );


    //get data for forgot password
    @Headers("Content-Type: application/json")
    @GET("ForgotPasswordApi")
    Call<List<UserProfile>> getDataForgotPass(@QueryMap Map<String, String> params);


    //update password
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("ForgotPasswordApi")
    Call<String> updatePassword(
            @Field("mail") String email,
            @Field("pass") String password
    );

    //get chapterList for Cart Page
    @Headers("Content-Type: application/json")
    @GET("CartApi")
    Call<List<CartApi>> getCartChaptersData(@QueryMap Map<String, String> params);

    //get data for Cart Page
    @Headers("Content-Type: application/json")
    @GET("CartApi")
    Call<List<CartApi>> getCartPageData(@QueryMap Map<String, String> params);

    //post data to cart table
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("CartApi")
    Call<String> insertCartData(
            @Field("email") String email,
            @Field("chapterId") String chapterId
    );

    //post data to delete a chapter from cart
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("RemoveCartItem")
    Call<String> removeCartData(
            @Field("email") String email,
            @Field("chapterId") String chapterId
    );

    //post data to Delete cart
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("DeleteCartApi")
    Call<String> deleteCartChapters (
            @Field("email") String email,
            @Field("chapterIds") String[] chapterId
    );

    //post data to Purchased chapters table
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("PurchasedChaptersApi")
    Call<String> insertPurchasedChapters(
            @Field("email") String email,
            @Field("chapterIds") String[] chapterId
    );

    //get data for Purchased videos Page
    @Headers("Content-Type: application/json")
    @GET("PurchasedChaptersApi")
    Call<List<PurchaseChaptersApi>> getVideosData(@QueryMap Map<String, String> params);

    //get chapterList for Purchased Page
    @Headers("Content-Type: application/json")
    @GET("PurchasedItemList")
    Call<List<PurchaseChaptersApi>> getPurchaseChaptersData(@QueryMap Map<String, String> params);

    //get Video url for Purchased Page
    @Headers("Content-Type: application/json")
    @GET("PurchasedItemUrl")
    Call<VideoUrlApi> getChapterUrl(@QueryMap Map<String, String> params);

    //post subject data to purchase table
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("SubjectPurchased")
    Call<String> purchaseSubject(
            @Field("email") String email,
            @Field("subjectId") String subjectId
    );

    //post chapter data to purchase table
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("CoursePurchased")
    Call<String> purchaseCourse(
            @Field("email") String email,
            @Field("courseId") String courseId
    );

    //update watch count in videos
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("UpdateWatchCount")
    Call<String> updateWatchCount (
            @Field("email") String email,
            @Field("chapterId") String chapterId,
            @Field("purchaseId") int purchaseId
    );

    //delete chapter from purchase table
    @FormUrlEncoded
    @Headers({
            "Content-Type: application/x-www-form-urlencoded"
    })
    @POST("DeletePurchaseChapter")
    Call<String> deletePurchaseChapter (
            @Field("email") String email,
            @Field("chapterId") String chapterId
    );

    //Get Our Info
    @GET("OurInfo")
    Call<List<OurInfoApi>> getOurInfo();
}
