package com.mkology.thelearningapp.navigationFragments.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.helper.ApplicationConstants;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.helper.CustomizeAlertDialogs;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import com.mkology.thelearningapp.loginPage.MainActivity;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private Button logout;
    private TextView name, email, mobile, place, updatePass;
    private AlertDialog dialog;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;
    private CustomizeAlertDialogs customizeAlertDialogs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        logout = root.findViewById(R.id.log_out);
        name = root.findViewById(R.id.name);
        email = root.findViewById(R.id.email);
        mobile = root.findViewById(R.id.mobile);
        place = root.findViewById(R.id.place);
        updatePass = root.findViewById(R.id.passwordReset);
        progressbarLoading = new ProgressbarLoading(getContext());
        errorHelper = new ErrorHelper(getContext());
        customizeAlertDialogs = new CustomizeAlertDialogs();
        setHasOptionsMenu(true);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               customizeAlertDialogs.showLogoutPopup(getContext(), "Log out", "Do you want to logout?");
            }
        });

        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog();
            }
        });

        setProfileData();
        return root;
    }

    public void showPasswordDialog() {
        LayoutInflater li = LayoutInflater.from(getContext());
        final View promptsView = li.inflate(R.layout.reset_password_layout, null);
        final EditText password = promptsView.findViewById(R.id.dialog_pass);
        final EditText confirmPassword = promptsView.findViewById(R.id.dialog_confirmPass);
        final TextView updatePassErrorMsg = promptsView.findViewById(R.id.err_msg);

        dialog = new AlertDialog.Builder(getContext())
                .setView(promptsView)
                .setPositiveButton("Update", null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button n = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                b.setTextColor(getResources().getColor(R.color.colorPrimary));
                b.setTypeface(null, Typeface.BOLD);

                n.setTextColor(getResources().getColor(R.color.colorPrimary));
                n.setTypeface(null, Typeface.BOLD);

                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        String pass = (password.getText()).toString();
                        String confirmPass = (confirmPassword.getText()).toString();

                        if (pass.equals(ApplicationConstants.EMPTY)
                                || confirmPass.equals(ApplicationConstants.EMPTY)) {
                            updatePassErrorMsg.setVisibility(View.VISIBLE);
                            updatePassErrorMsg.setText(getString(R.string.empty_field_error));
                        } else if (!pass.equals(confirmPass)) {
                            updatePassErrorMsg.setVisibility(View.VISIBLE);
                            updatePassErrorMsg.setText(getString(R.string.password_error));
                        } else if (pass.length() <= 6) {
                            updatePassErrorMsg.setVisibility(View.VISIBLE);
                            updatePassErrorMsg.setText(getString(R.string.password_length_error));
                        } else {
                            updatePassword(pass);
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void setProfileData() {
        name.setText(SaveSharedPreference.getName(getContext()));
        email.setText(SaveSharedPreference.getEmail(getContext()));
        place.setText(SaveSharedPreference.getPlace(getContext()));
        mobile.setText(SaveSharedPreference.getMobile(getContext()));
    }

    private void updatePassword(final String password) {

        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);

        Call<String> call = jsonPlaceHolderApi.updatePassword(
                SaveSharedPreference.getEmail(getContext()),
                password);

        progressbarLoading.showProgressBar();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressbarLoading.hideProgressBar();
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Password updated!", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    errorHelper.errorMessage();
                    Toast.makeText(getContext(), "Password updation failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
                Toast.makeText(getContext(), "Password updation failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        CartHelper cartHelper = new CartHelper(getContext());
        int cartCount = cartHelper.getCartCount();
        MenuItem filter = menu.findItem(R.id.title_bar);
        filter.setTitle(getResources().getString(R.string.profile_page));
        if (cartCount > 0) {
            TextView textView = menu.getItem(1).getActionView().findViewById(R.id.cartCount);
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(cartCount));
        }
    }
}
