package com.mkology.thelearningapp.helper;

import android.app.ProgressDialog;
import android.content.Context;
import com.mkology.thelearningapp.R;

public class ProgressbarLoading {

    private ProgressDialog mProgressDialog;
    private Context context;

    public ProgressbarLoading(Context context) {
        this.context = context;
    }

    public void showProgressBar() {

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.progress_dialog_layout);
        mProgressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    public void hideProgressBar() { mProgressDialog.hide();
    }
}
