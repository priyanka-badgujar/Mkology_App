package com.mkology.thelearningapp.payment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import com.mkology.thelearningapp.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.common.util.CollectionUtils.listOf;

public class ServiceWrapper {

    private ServiceInterface mServiceInterface;
    private static final long API_CONNECTION_TIMEOUT = 1201;
    private static final long API_READ_TIMEOUT = 901;
    private static final String PAYMENT_BASE_URL = "http://15.207.190.21/";   // https://www your domain .com/
    private static Retrofit retrofit = null;

    public ServiceWrapper(Interceptor mInterceptorheader) {
        mServiceInterface = getRetrofit(mInterceptorheader).create(ServiceInterface.class);
    }

    private Retrofit getRetrofit(Interceptor mInterceptorheader) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient mOkHttpClient = null;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(API_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        mOkHttpClient = builder.build();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PAYMENT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(mOkHttpClient)
                .build();
        return retrofit;
    }

    public Call<String> newHashCall(String key, String txtid, String amount, String productinfo,
                                    String fullname, String email){
        return mServiceInterface.getHashCall(
                convertPlainString(key),   convertPlainString(txtid),convertPlainString(amount),
                convertPlainString(productinfo), convertPlainString( fullname),  convertPlainString(email));
    }

    // convert aa param into plain text
    private RequestBody convertPlainString(String data){
        RequestBody plainString = RequestBody.create(MediaType.parse("text/plain"), data);
        return  plainString;
    }
}
