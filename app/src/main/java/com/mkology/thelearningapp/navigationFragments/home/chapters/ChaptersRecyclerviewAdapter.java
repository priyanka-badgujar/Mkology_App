package com.mkology.thelearningapp.navigationFragments.home.chapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.CartApi;
import com.mkology.thelearningapp.apiCalls.ChaptersApi;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.apiCalls.PurchaseChaptersApi;
import com.mkology.thelearningapp.apiCalls.VideoUrlApi;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.helper.ErrorHelper;
import com.mkology.thelearningapp.helper.ProgressbarLoading;
import com.mkology.thelearningapp.helper.PurchaseChaptersHelper;
import com.mkology.thelearningapp.navigationFragments.videos.VideosPlayer;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChaptersRecyclerviewAdapter extends RecyclerView.Adapter<ChaptersRecyclerviewAdapter.ViewHolder>
        implements Filterable {

    private List<ChaptersApi> mData;
    private List<CartApi> cartListData;
    private List<ChaptersApi> chapterListFull;

    private List<PurchaseChaptersApi> purchaseChaptersList;
    private CartHelper cartHelper;
    private PurchaseChaptersHelper purchaseChaptersHelper;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String courseId;
    private String subjectId;
    private Context context;
    private int cartListSize = 0;
    private int purchaseChapterSize = 0;
    private TextView countView;
    private int cartCount = 0;
    private FragmentActivity activity;
    private ProgressbarLoading progressbarLoading;
    private ErrorHelper errorHelper;

    ChaptersRecyclerviewAdapter(List<ChaptersApi> data,
                                String courseId,
                                String subjectId,
                                TextView countView,
                                int cartCount,
                                FragmentActivity activity) {
        this.mData = data;
        this.courseId = courseId;
        this.subjectId = subjectId;
        this.countView = countView;
        this.cartCount = cartCount;
        this.activity = activity;
        chapterListFull = new ArrayList<>(mData);
        progressbarLoading = new ProgressbarLoading(activity);
        errorHelper = new ErrorHelper(activity);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        this.mInflater = LayoutInflater.from(context);
        cartHelper = new CartHelper(context);
        purchaseChaptersHelper = new PurchaseChaptersHelper(context);
        cartListData = cartHelper.getCartData();
        purchaseChaptersList = purchaseChaptersHelper.getVideosData();

        if (cartListData != null) {
            cartListSize = (cartListData.size() > 0) ? cartListData.size() : 0;
        }
        if (purchaseChaptersList != null) {
            purchaseChapterSize = (purchaseChaptersList.size() > 0) ? purchaseChaptersList.size() : 0;
        }
        for (int i = 0; i < mData.size(); i++) {
            for (int k = 0; k < purchaseChapterSize; k++) {
                if (mData.get(i).getChapterId().equals(purchaseChaptersList.get(k).getChapterId())) {
                    mData.get(i).setPurchased(true);
                    mData.get(i).setWatchCount(purchaseChaptersList.get(k).getWatchCount());
                    mData.get(i).setPurchaseId(purchaseChaptersList.get(k).getPurchaseId());
                    //mData.get(i).setChapterLink(purchaseChaptersList.get(k).getChapterLink());
                }
            }

            if (!mData.get(i).isPurchased()) {
                for (int j = 0; j < cartListSize; j++) {
                    if (mData.get(i).getChapterId().equals(cartListData.get(j).getChapterId())) {
                        mData.get(i).setInCart(true);
                    }
                }
            }
        }
        View view = mInflater.inflate(R.layout.recyclerview_chapters_api, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ChaptersApi chaptersApi = mData.get(position);
        holder.chapterName.setText(chaptersApi.getChapterName());
        holder.chapterPrice.setText(context.getResources().getText(R.string.rs_symbol) + String.valueOf(chaptersApi.getChapterPrice()));
        holder.chapterDuration.setText(context.getResources().getText(R.string.durationText) + String.valueOf(chaptersApi.getChapterDuration()));
        if (chaptersApi.isPurchased()) {
            holder.watchNow.setVisibility(View.VISIBLE);
            holder.chaptersAddToCart.setVisibility(View.GONE);
            holder.chapterRemoveFromCart.setVisibility(View.GONE);
            holder.chapterWatchCount.setText("Watch count - "+chaptersApi.getWatchCount());
        } else if (chaptersApi.isInCart()) {
            holder.watchNow.setVisibility(View.GONE);
            holder.chaptersAddToCart.setVisibility(View.GONE);
            holder.chapterRemoveFromCart.setVisibility(View.VISIBLE);
            holder.chapterWatchCount.setText("Watch count - 3");
        } else {
            holder.watchNow.setVisibility(View.GONE);
            holder.chaptersAddToCart.setVisibility(View.VISIBLE);
            holder.chapterRemoveFromCart.setVisibility(View.GONE);
            holder.chapterWatchCount.setText("Watch count - 3");
        }

        holder.chaptersAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.chaptersAddToCart.setVisibility(View.GONE);
                holder.chapterRemoveFromCart.setVisibility(View.VISIBLE);
                if (cartCount == 0)
                    countView.setVisibility(View.VISIBLE);
                cartCount = cartCount + 1;
                countView.setText(String.valueOf(cartCount));
                addChapterToCart(chaptersApi, holder, position);
            }
        });

        holder.chapterRemoveFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.chaptersAddToCart.setVisibility(View.VISIBLE);
                holder.chapterRemoveFromCart.setVisibility(View.GONE);
                cartCount = cartCount - 1;
                countView.setText(String.valueOf(cartCount));
                removeChapterFromCart(chaptersApi, holder, position);
            }
        });

        holder.watchNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchChapterNow(chaptersApi.getChapterId(), chaptersApi.getPurchaseId(),
                        chaptersApi.getWatchCount(), chaptersApi.getChapterName());
            }
        });
    }

    private void watchChapterNow(String chapterId, int purchaseId, int watchCount, String chapterName) {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("chapterId", chapterId);
        progressbarLoading.showProgressBar();
        jsonPlaceHolderApi.getChapterUrl(params).enqueue( new Callback<VideoUrlApi>() {
            @Override
            public void onResponse(Call<VideoUrlApi> call, Response<VideoUrlApi> response) {
                progressbarLoading.hideProgressBar();
                if (response.isSuccessful()) {
                    VideoUrlApi videoUrlApi = response.body();
                    String url = videoUrlApi.getVideoUrl();
                    Intent intent = new Intent(activity, VideosPlayer.class);
                    intent.putExtra("VideoUrl", url);
                    intent.putExtra("chapterId", chapterId);
                    intent.putExtra("purchaseId", purchaseId);
                    intent.putExtra("watchCount", watchCount);
                    intent.putExtra("chapterName", chapterName);
                    activity.startActivity(intent);
                } else {
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<VideoUrlApi> call, Throwable t) {
                progressbarLoading.hideProgressBar();
                errorHelper.errorMessage();
            }
        });
    }

    private void removeChapterFromCart(final ChaptersApi chaptersApi, final ViewHolder holder, final int position) {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);

        Call<String> call = jsonPlaceHolderApi.removeCartData(
                SaveSharedPreference.getEmail(context), chaptersApi.getChapterId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    mData.get(position).setInCart(false);
                    CartApi cartApi = new CartApi();
                    cartApi.setChapterId(chaptersApi.getChapterId());
                    cartHelper.setOrRemoveFromCart(cartApi, false);
                } else {
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                holder.chaptersAddToCart.setVisibility(View.GONE);
                holder.chapterRemoveFromCart.setVisibility(View.GONE);
                cartCount = cartCount + 1;
                countView.setText(String.valueOf(cartCount));
                errorHelper.errorMessage();
            }
        });
    }

    private void addChapterToCart(final ChaptersApi chaptersApi, final ViewHolder holder, final int position) {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);

        Call<String> call = jsonPlaceHolderApi.insertCartData(
                SaveSharedPreference.getEmail(context), chaptersApi.getChapterId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    mData.get(position).setInCart(true);
                    CartApi cartApi = new CartApi();
                    cartApi.setChapterId(chaptersApi.getChapterId());
                    cartHelper.setOrRemoveFromCart(cartApi, true);
                } else {
                    errorHelper.errorMessage();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                holder.chaptersAddToCart.setVisibility(View.VISIBLE);
                holder.chapterRemoveFromCart.setVisibility(View.GONE);
                cartCount = cartCount - 1;
                countView.setText(String.valueOf(cartCount));
                if (cartCount == 0)
                    countView.setVisibility(View.GONE);
                errorHelper.errorMessage();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setClickListener() {
    }


    @Override
    public Filter getFilter() {
        return dataFilter;
    }

    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ChaptersApi> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(chapterListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ChaptersApi item : chapterListFull) {
                    if (item.getChapterName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                mData.clear();
                mData.addAll((List) results.values);
                notifyDataSetChanged();
            }
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView chapterName, chapterPrice, chapterDuration, chapterWatchCount;
        private Button chaptersAddToCart, chapterRemoveFromCart, watchNow;
        ViewHolder(View itemView) {
            super(itemView);
            chapterName = itemView.findViewById(R.id.chapter_name);
            chapterPrice = itemView.findViewById(R.id.chapter_price);
            chaptersAddToCart = itemView.findViewById(R.id.chapters_add_to_cart);
            chapterRemoveFromCart = itemView.findViewById(R.id.chapter_remove_from_cart);
            watchNow = itemView.findViewById(R.id.watch_now);
            chapterDuration = itemView.findViewById(R.id.chapter_duration);
            chapterWatchCount = itemView.findViewById(R.id.chapter_watch_count);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
