package com.mkology.thelearningapp.loginPage;

import android.content.Context;
import android.widget.TextView;

public interface MainContract {

    interface MainPresenter {
        void checkUserAuthentication(String mail, String pwd, final TextView errorText);

        void goForMobileAuthentication(Context context);
    }

    interface MainView {
        void setPresenter(MainContract.MainPresenter presenter);
    }

}
