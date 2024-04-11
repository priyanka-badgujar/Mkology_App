package com.mkology.thelearningapp.navigationFragments.home.chapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.mkology.thelearningapp.apiCalls.ChaptersApi;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChaptersFragment extends Fragment {

    private RecyclerView chaptersRecyclerView;
    private ChaptersRecyclerviewAdapter chaptersAdapter;
    private String courseId, subjectId, courseName, subjectName;
    private SearchView searchView;
    private TextView countView;
    private int cartCount;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chapters, container, false);
        chaptersRecyclerView = root.findViewById(R.id.chapters_recyclerview);
        searchView = root.findViewById(R.id.action_search);
        progressbarLoading = new ProgressbarLoading(getContext());
        errorHelper = new ErrorHelper(getContext());

        setHasOptionsMenu(true);

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            courseId = bundle.getString("course_id");
            subjectId = bundle.getString("subject_id");
            courseName = bundle.getString("course_name");
            subjectName = bundle.getString("subject_name");
        }
        //getChaptersApi();
        return root;
    }

    private void getChaptersApi() {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("subject_id", subjectId);
        progressbarLoading.showProgressBar();

        jsonPlaceHolderApi.getChaptersApi(params).enqueue(new Callback<List<ChaptersApi>>() {
            @Override
            public void onResponse(Call<List<ChaptersApi>> call, Response<List<ChaptersApi>> response) {
                progressbarLoading.hideProgressBar();
                if (response.isSuccessful()) {
                    createChaptersRecyclerView(response);
                } else {
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<List<ChaptersApi>> call, Throwable t) {
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
            }
        });
    }

    private void createChaptersRecyclerView(Response<List<ChaptersApi>> response) {
        List<ChaptersApi> chaptersApiResponse = response.body();
        chaptersRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        chaptersAdapter = new ChaptersRecyclerviewAdapter(chaptersApiResponse, courseId, subjectId,
                countView, cartCount, getActivity());
        chaptersAdapter.setClickListener();
        chaptersRecyclerView.setAdapter(chaptersAdapter);
        chaptersAdapter.notifyDataSetChanged();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chaptersAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem filter = menu.findItem(R.id.title_bar);
        filter.setTitle(courseName + " - " + subjectName);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        CartHelper cartHelper = new CartHelper(getContext());
        cartCount = cartHelper.getCartCount();
        MenuItem filter = menu.findItem(R.id.title_bar);
        filter.setTitle(courseName + " - " + subjectName);

        countView = menu.getItem(1).getActionView().findViewById(R.id.cartCount);
        if (cartCount > 0) {
            countView.setVisibility(View.VISIBLE);
            countView.setText(String.valueOf(cartCount));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getChaptersApi();
    }
}
