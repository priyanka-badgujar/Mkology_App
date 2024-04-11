package com.mkology.thelearningapp.navigationFragments.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.apiCalls.OurInfoApi;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutFragment extends Fragment {

    private ProgressbarLoading progressbarLoading;
    private TextView name;
    private TextView info, contact, email;
    private ImageView photo;
    private ErrorHelper errorHelper;
    private LinearLayout contact_us_layout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragments_about, container, false);
        name = root.findViewById(R.id.name);
        info = root.findViewById(R.id.info);
        contact = root.findViewById(R.id.contact);
        photo = root.findViewById(R.id.photo);
        email = root.findViewById(R.id.email_contact);
        contact_us_layout = root.findViewById(R.id.contact_us_layout);

        setHasOptionsMenu(true);
        progressbarLoading = new ProgressbarLoading(getContext());
        errorHelper = new ErrorHelper(getContext());
        getInfo();
        return root;
    }

    private void getInfo() {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Call<List<OurInfoApi>> call = jsonPlaceHolderApi.getOurInfo();
        progressbarLoading.showProgressBar();
        call.enqueue(new Callback<List<OurInfoApi>>() {
            @Override
            public void onResponse(Call<List<OurInfoApi>> call, Response<List<OurInfoApi>> response) {
                progressbarLoading.hideProgressBar();
                if (response.isSuccessful()) {
                    List<OurInfoApi> ourInfo = response.body();
                    contact_us_layout.setVisibility(View.VISIBLE);
                    Picasso.get()
                            .load(ourInfo.get(0).getLink())
                            .transform(new ImageTrans_CircleTransform())
                            .into(photo);
                    name.setText(ourInfo.get(0).getName());
                    info.setText(ourInfo.get(0).getInfo());
                    contact.setText(ourInfo.get(0).getContact());
                    email.setText(ourInfo.get(0).getEmail());

                } else {
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<List<OurInfoApi>> call, Throwable t) {
                //showErrorMsg();
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        CartHelper cartHelper = new CartHelper(getContext());
        int cartCount = cartHelper.getCartCount();
        MenuItem filter = menu.findItem(R.id.title_bar);
        filter.setTitle(getResources().getString(R.string.about));
        if (cartCount > 0) {
            TextView textView = menu.getItem(1).getActionView().findViewById(R.id.cartCount);
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(cartCount));
        }
    }

}
