package com.mkology.thelearningapp.navigationFragments.videos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.PurchaseChaptersApi;
import com.mkology.thelearningapp.helper.PurchaseChaptersHelper;

public class VideosRecyclerviewAdapter extends RecyclerView.Adapter<VideosRecyclerviewAdapter.ViewHolder>
        implements Filterable {

    private List<PurchaseChaptersApi> mData;
    private List<PurchaseChaptersApi> purchaseListFull;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private FragmentActivity activity;

    VideosRecyclerviewAdapter(List<PurchaseChaptersApi> data, FragmentActivity activity) {
        this.mData = data;
        purchaseListFull = new ArrayList<>(mData);
        this.activity = activity;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        this.mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.recyclerview_videos_api, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final PurchaseChaptersApi purchaseChaptersApi = mData.get(position);
        if (purchaseChaptersApi.getWatchCount() == 0) {
            PurchaseChaptersHelper purchaseChaptersHelper = new PurchaseChaptersHelper(context);
            purchaseChaptersHelper.deletePurchasedChapter(purchaseChaptersApi.getChapterId());
        }
        String price = context.getResources().getText(R.string.rs_symbol) + String.valueOf(purchaseChaptersApi.getChapterPrice());
        holder.itemName.setText(purchaseChaptersApi.getChapterName());
        holder.itemPrice.setText(price);
        holder.itemDescription.setText(purchaseChaptersApi.getChapterDescription());
        holder.itemDuration.setText(context.getResources().getText(R.string.durationText) + purchaseChaptersApi.getChapterDuration());
        holder.itemWatchCount.setText("Watch count - "+ purchaseChaptersApi.getWatchCount());
        int count = 1;
        if (count == purchaseChaptersApi.getWatchCount()) {
            holder.itemWatchCount.setTextColor(context.getResources().getColor(R.color.colorRed));
        } else {
            holder.itemWatchCount.setTextColor(context.getResources().getColor(R.color.colorText));
        }

        holder.playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo(purchaseChaptersApi.getChapterLink(), purchaseChaptersApi.getChapterId(),
                        purchaseChaptersApi.getPurchaseId(), purchaseChaptersApi.getWatchCount(),
                        purchaseChaptersApi.getChapterName());
            }
        });
    }

    private void playVideo(String videoUrl, String chapterId, int purchaseId, int watchCount, String chapterName) {
        Intent intent = new Intent(activity, VideosPlayer.class);
        intent.putExtra("VideoUrl", videoUrl);
        intent.putExtra("chapterId", chapterId);
        intent.putExtra("purchaseId", purchaseId);
        intent.putExtra("watchCount", watchCount);
        intent.putExtra("chapterName", chapterName);
        activity.startActivity(intent);
        //activity.startActivityForResult(intent, ApplicationConstants.LAUNCH_SECOND_ACTIVITY);
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
            List<PurchaseChaptersApi> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(purchaseListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (PurchaseChaptersApi item : purchaseListFull) {
                    if (item.getChapterName().toLowerCase().contains(filterPattern)
                        || item.getChapterDescription().toLowerCase().contains(filterPattern) ) {
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
        private TextView itemName, itemPrice, itemDescription, itemDuration, itemWatchCount;
        private Button playVideo;
        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            playVideo = itemView.findViewById(R.id.play_video);
            itemDescription = itemView.findViewById(R.id.item_description);
            itemDuration = itemView.findViewById(R.id.item_duration);
            itemWatchCount = itemView.findViewById(R.id.item_watch_count);
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
