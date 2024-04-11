package com.mkology.thelearningapp.navigationFragments.home.subjects;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.SubjectsApi;
import com.mkology.thelearningapp.helper.ApplicationConstants;
import com.mkology.thelearningapp.payment.PaymentActivity;

public class SubjectsRecyclerviewAdapter extends RecyclerView.Adapter<SubjectsRecyclerviewAdapter.ViewHolder>
        implements Filterable {

    private List<SubjectsApi> mData;
    private List<SubjectsApi> subjectListFull;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String courseId, courseName;
    private Context context;

    SubjectsRecyclerviewAdapter(List<SubjectsApi> data, String courseId, String courseName) {
        this.mData = data;
        this.courseId = courseId;
        this.courseName = courseName;
        subjectListFull = new ArrayList<>(mData);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        this.mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.recyclerview_subjects_api, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final SubjectsApi subjectsApi = mData.get(position);
        holder.subjectName.setText(subjectsApi.getSubjectName());
        holder.subjectPrice.setText(context.getResources().getText(R.string.rs_symbol) + String.valueOf(subjectsApi.getSubjectPrice()));

        holder.apiInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("course_id", courseId);
                bundle.putString("course_name", courseName);
                bundle.putString("subject_id", subjectsApi.getSubjectId());
                bundle.putString("subject_name", subjectsApi.getSubjectName());
                final NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.navigation_chapters, bundle);
            }
        });

        holder.buySubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.buySubject.setClickable(false);
                Intent intent = new Intent(context, PaymentActivity.class);
                intent.putExtra("totalPrice", Integer.toString(subjectsApi.getSubjectPrice()));
                intent.putExtra("buyType", ApplicationConstants.SUBJECT_BUY);
                intent.putExtra("subjectId", subjectsApi.getSubjectId());
                context.startActivity(intent);
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
    public Filter getFilter()
    {
        return dataFilter;
    }

    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SubjectsApi> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(subjectListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SubjectsApi item : subjectListFull) {
                    if (item.getSubjectName().toLowerCase().contains(filterPattern)) {
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
        private TextView subjectName, subjectPrice;
        private LinearLayout apiInfoContainer;
        private Button buySubject;
        ViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subject_name);
            subjectPrice = itemView.findViewById(R.id.subject_price);
            apiInfoContainer = itemView.findViewById(R.id.api_info_container);
            buySubject = itemView.findViewById(R.id.subject_buy);
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
