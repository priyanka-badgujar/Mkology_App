package com.mkology.thelearningapp.navigationFragments.videos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.apiCalls.PurchaseChaptersApi;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import com.mkology.thelearningapp.helper.PurchaseChaptersHelper;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideosFragment extends Fragment {

    private RecyclerView videosRecyclerView;
    private VideosRecyclerviewAdapter videosAdapter;
    private SearchView searchView;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;
    private RelativeLayout emptyVideos;
    private PurchaseChaptersHelper purchaseChaptersHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragments_videos, container, false);
        videosRecyclerView = root.findViewById(R.id.videos_recyclerview);
        searchView = root.findViewById(R.id.action_search);
        progressbarLoading = new ProgressbarLoading(getContext());
        emptyVideos = root.findViewById(R.id.empty_videos);
        purchaseChaptersHelper = new PurchaseChaptersHelper(getContext());
        errorHelper = new ErrorHelper(getContext());
        setHasOptionsMenu(true);
        getVideosApi();
        return root;
    }

    private void getVideosApi() {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", SaveSharedPreference.getEmail(getContext()));
        progressbarLoading.showProgressBar();
        jsonPlaceHolderApi.getVideosData(params).enqueue( new Callback<List<PurchaseChaptersApi>>() {
            @Override
            public void onResponse(Call<List<PurchaseChaptersApi>> call, Response<List<PurchaseChaptersApi>> response) {
                progressbarLoading.hideProgressBar();
                if (response.isSuccessful()) {
                    List<PurchaseChaptersApi> purchaseChaptersList = response.body();
                    purchaseChaptersHelper.updateVideosData(purchaseChaptersList);
                    if (purchaseChaptersList.size() > 0) {
                        purchaseChaptersList = purchaseChaptersHelper.getVideosData();
                        createVideosRecyclerView(purchaseChaptersList);
                        emptyVideos.setVisibility(View.GONE);
                    } else {
                        searchView.setVisibility(View.GONE);
                        emptyVideos.setVisibility(View.VISIBLE);
                    }
                } else {
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<List<PurchaseChaptersApi>> call, Throwable t) {
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
            }
        });
    }

    private void createVideosRecyclerView(List<PurchaseChaptersApi> purchaseChaptersList) {
        videosRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        videosAdapter = new VideosRecyclerviewAdapter(purchaseChaptersList, getActivity());
        videosAdapter.setClickListener();
        videosRecyclerView.setAdapter(videosAdapter);
        videosAdapter.notifyDataSetChanged();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                videosAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        CartHelper cartHelper = new CartHelper(getContext());
        int cartCount = cartHelper.getCartCount();
        MenuItem filter = menu.findItem(R.id.title_bar);
        filter.setTitle(getResources().getString(R.string.videos_page));
        if (cartCount > 0) {
            TextView textView = menu.getItem(1).getActionView().findViewById(R.id.cartCount);
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(cartCount));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        List<PurchaseChaptersApi> purchaseChapters = purchaseChaptersHelper.getVideosData();
        startVideoFlow(purchaseChapters);
    }

    private void startVideoFlow(List<PurchaseChaptersApi> purchaseChapters) {
        if (purchaseChapters != null && purchaseChapters.size() > 0) {
            emptyVideos.setVisibility(View.GONE);
            createVideosRecyclerView(purchaseChapters);
        } else {
            searchView.setVisibility(View.GONE);
            emptyVideos.setVisibility(View.VISIBLE);
            videosRecyclerView.setVisibility(View.GONE);
        }
    }
}
