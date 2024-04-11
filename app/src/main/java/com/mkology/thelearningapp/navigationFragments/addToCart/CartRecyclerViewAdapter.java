package com.mkology.thelearningapp.navigationFragments.addToCart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.APIClient;
import com.mkology.thelearningapp.apiCalls.CartApi;
import com.mkology.thelearningapp.apiCalls.JsonPlaceHolderApi;
import com.mkology.thelearningapp.helper.CartHelper;
import com.mkology.thelearningapp.saveUserData.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder>
        implements Filterable {

    private List<CartApi> mData;
    private List<CartApi> cartListFull;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private CartHelper cartHelper;
    private Context context;
    private RelativeLayout emptyCart;
    private Button buyNow;
    private TextView countView;
    private int cartCount = 0;
    private SearchView searchView;
    private TextView cartValue;
    private int cartTotal;
    private LinearLayout orderLayout;

    CartRecyclerViewAdapter(List<CartApi> data,
                            RelativeLayout emptyCart,
                            Button buyNow,
                            TextView countView,
                            int cartCount,
                            SearchView searchView,
                            int cartTotal,
                            TextView cartValue,
                            LinearLayout orderLayout) {
        this.mData = data;
        this.emptyCart = emptyCart;
        this.buyNow = buyNow;
        this.searchView = searchView;
        this.countView = countView;
        this.cartCount = cartCount;
        this.cartValue = cartValue;
        this.cartTotal = cartTotal;
        this.orderLayout = orderLayout;
        cartListFull = new ArrayList<>(mData);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        this.mInflater = LayoutInflater.from(context);
        cartHelper = new CartHelper(context);

        View view = mInflater.inflate(R.layout.recyclerview_cart_api, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final CartApi cartApi = mData.get(position);
        holder.chapterName.setText(cartApi.getChapterName());
        holder.chapterPrice.setText(context.getResources().getText(R.string.rs_symbol) + String.valueOf(cartApi.getChapterPrice()));
        holder.chapterDescription.setText(cartApi.getChapterDescription());
        holder.chapterDuration.setText(context.getResources().getText(R.string.durationText) + String.valueOf(cartApi.getChapterDuration()));

        holder.removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeChapterFromCart(cartApi, holder, position);
            }
        });
    }

    private void removeChapterFromCart(final CartApi cartApi, final ViewHolder holder, final int position) {
        JsonPlaceHolderApi jsonPlaceHolderApi = APIClient.getClient().create(JsonPlaceHolderApi.class);

        Call<String> call = jsonPlaceHolderApi.removeCartData(
                SaveSharedPreference.getEmail(context), cartApi.getChapterId());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    mData.remove(position);
                    cartCount = cartCount - 1;
                    countView.setText(String.valueOf(cartCount));

                    boolean isEmptyCart = cartHelper.setOrRemoveFromCart(cartApi, false);
                    if (isEmptyCart) {
                        emptyCart.setVisibility(View.VISIBLE);
                        buyNow.setVisibility(View.GONE);
                        countView.setVisibility(View.GONE);
                        searchView.setVisibility(View.GONE);
                        orderLayout.setVisibility(View.GONE);
                    }

                    cartTotal = cartTotal - cartApi.getChapterPrice();
                    cartValue.setText(context.getResources().getText(R.string.rs_symbol)
                            + String.valueOf(cartTotal));
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context.getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
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
            List<CartApi> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(cartListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CartApi item : cartListFull) {
                    if (item.getChapterName().toLowerCase().contains(filterPattern)
                     || item.getChapterDescription().toLowerCase().contains(filterPattern)) {
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
        private TextView chapterName, chapterPrice, chapterDescription, chapterDuration;
        private ImageView removeFromCart;
        private CardView cartInfoContainer;

        ViewHolder(View itemView) {
            super(itemView);
            chapterName = itemView.findViewById(R.id.cart_chapter_name);
            chapterPrice = itemView.findViewById(R.id.cart_chapter_price);
            removeFromCart = itemView.findViewById(R.id.remove_from_cart);
            chapterDescription = itemView.findViewById(R.id.cart_chapter_description);
            chapterDuration = itemView.findViewById(R.id.cart_chapter_duration);
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
