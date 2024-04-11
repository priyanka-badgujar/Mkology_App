package com.mkology.thelearningapp.navigationFragments.home.subjects;

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
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.apiCalls.SubjectsApi;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectsFragment extends Fragment {

    private RecyclerView subjectsRecyclerView;
    private SubjectsRecyclerviewAdapter subjectsAdapter;
    private String courseId, courseName;
    private SearchView searchView;
    private View root;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_subjects, container, false);
        subjectsRecyclerView = root.findViewById(R.id.subjects_recyclerview);
        searchView = root.findViewById(R.id.action_search);
        progressbarLoading = new ProgressbarLoading(getContext());
        errorHelper = new ErrorHelper(getContext());
        setHasOptionsMenu(true);

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            courseId  = bundle.getString("course_id");
            courseName = bundle.getString("course_name");
        }
        getSubjectsApi();
        return root;
    }

    private void getSubjectsApi() {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("course_id", courseId);
        progressbarLoading.showProgressBar();
        jsonPlaceHolderApi.getSubjectsApi(params).enqueue( new Callback<List<SubjectsApi>>() {
            @Override
            public void onResponse(Call<List<SubjectsApi>> call, Response<List<SubjectsApi>> response) {
                progressbarLoading.hideProgressBar();
                if (response.isSuccessful()) {
                    createSubjectsRecyclerView(response);
                } else {
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<List<SubjectsApi>> call, Throwable t) {
                progressbarLoading.hideProgressBar();
                progressbarLoading.hideProgressBar();
            }
        });
    }

    private void createSubjectsRecyclerView(Response<List<SubjectsApi>> response) {
        List<SubjectsApi> subjectsApiResponse = response.body();
        subjectsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        subjectsAdapter = new SubjectsRecyclerviewAdapter(subjectsApiResponse, courseId, courseName);
        subjectsAdapter.setClickListener();
        subjectsRecyclerView.setAdapter(subjectsAdapter);
        subjectsAdapter.notifyDataSetChanged();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                subjectsAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        CartHelper cartHelper = new CartHelper(getContext());
        int cartCount = cartHelper.getCartCount();
        MenuItem filter = menu.findItem(R.id.title_bar);
        filter.setTitle(courseName);

        if (cartCount > 0) {
            TextView textView = menu.getItem(1).getActionView().findViewById(R.id.cartCount);
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(cartCount));
        }
    }
}
