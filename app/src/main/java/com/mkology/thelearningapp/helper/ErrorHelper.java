package com.mkology.thelearningapp.helper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mkology.thelearningapp.errorScreen.ErrorActivity;

public class ErrorHelper {

    private Context context;

    public ErrorHelper(Context context) {
        this.context = context;

    }

    private boolean hasInternetConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }


    public void errorMessage() {
        if (hasInternetConnectivity()) {
            //something went wrong
//            customizeAlertDialogs.showErrorMsg(context, null, context.getString(R.string.server_error_msg));
            Intent intent = new Intent(context, ErrorActivity.class);
            intent.putExtra("errorType", ApplicationConstants.SERVER_ERROR);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } else {
            //Please check your internet connection
//            customizeAlertDialogs.showErrorMsg(context,null,
//                    context.getString(R.string.internet_error_msg));
            Intent intent = new Intent(context, ErrorActivity.class);
            intent.putExtra("errorType", ApplicationConstants.INTERNET_ERROR);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }
}
