package com.mkology.thelearningapp.navigationFragments.home.courses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.CoursesApi;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoursesFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_courses, container, false);
        recyclerView = root.findViewById(R.id.courses_recyclerview);
        searchView = root.findViewById(R.id.action_search);
        searchView.bringToFront();
        progressbarLoading = new ProgressbarLoading(getContext());
        errorHelper = new ErrorHelper(getContext());
        setHasOptionsMenu(true);
        getCoursesApi();
        return root;
    }

    private void getCoursesApi() {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Call<List<CoursesApi>> call = jsonPlaceHolderApi.getCoursesApi();
        progressbarLoading.showProgressBar();

        call.enqueue(new Callback<List<CoursesApi>>() {
            @Override
            public void onResponse(Call<List<CoursesApi>> call, Response<List<CoursesApi>> response) {
                progressbarLoading.hideProgressBar();
                if (response.isSuccessful()) {
                    createRecyclerView(response);
                } else {
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<List<CoursesApi>> call, Throwable t) {
                //showErrorMsg();
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
            }
        });
    }

    private void createRecyclerView(Response<List<CoursesApi>> response) {
        List<CoursesApi> coursesApiResponse = response.body();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        final CoursesRecyclerviewAdapter adapter = new CoursesRecyclerviewAdapter(coursesApiResponse);
        adapter.setClickListener();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        CartHelper cartHelper = new CartHelper(getContext());
        int cartCount = cartHelper.getCartCount();
        if (cartCount > 0) {
            TextView textView = menu.getItem(1).getActionView().findViewById(R.id.cartCount);
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(cartCount));
        }
    }
}
