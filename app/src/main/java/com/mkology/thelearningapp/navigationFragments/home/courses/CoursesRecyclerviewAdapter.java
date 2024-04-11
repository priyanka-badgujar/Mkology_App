package com.mkology.thelearningapp.navigationFragments.home.courses;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.mkology.thelearningapp.R;
import com.mkology.thelearningapp.apiCalls.CoursesApi;
import com.mkology.thelearningapp.helper.ApplicationConstants;
import com.mkology.thelearningapp.payment.PaymentActivity;

public class CoursesRecyclerviewAdapter extends RecyclerView.Adapter<CoursesRecyclerviewAdapter.ViewHolder>
        implements Filterable {

    private List<CoursesApi> mData;
    private List<CoursesApi> coursesListFull;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private int resourceId;
    private Resources resources;

    CoursesRecyclerviewAdapter(List<CoursesApi> data) {
        this.mData = data;
        coursesListFull = new ArrayList<>(mData);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        this.mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.recyclerview_courses_api, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final CoursesApi coursesApi = mData.get(position);

        holder.courseName.setText(coursesApi.getCourseName());
        holder.coursePrice.setText(context.getResources().getText(R.string.rs_symbol) + String.valueOf(coursesApi.getCoursePrice()));

        resources = context.getResources();
        resourceId = resources.getIdentifier(coursesApi.getCourseId(), "drawable",
                context.getPackageName());
        holder.courseIcon.setImageDrawable(ContextCompat.getDrawable(context, resourceId));

        holder.apiInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("course_id", coursesApi.getCourseId());
                bundle.putString("course_name", coursesApi.getCourseName());
                final NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.navigation_subjects, bundle);
            }
        });

        holder.buyCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.buyCourse.setClickable(false);
                Intent intent = new Intent(context, PaymentActivity.class);
                intent.putExtra("buyType", ApplicationConstants.COURSE_BUY);
                intent.putExtra("courseId", coursesApi.getCourseId());
                intent.putExtra("totalPrice", Integer.toString(coursesApi.getCoursePrice()));
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
    public Filter getFilter() {
        return dataFilter;
    }

    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CoursesApi> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(coursesListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CoursesApi item : coursesListFull) {
                    if (item.getCourseName().toLowerCase().contains(filterPattern)) {
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
        private TextView courseName, coursePrice;
        private RelativeLayout apiInfoContainer;
        private Button buyCourse;
        private ImageView courseIcon;

        ViewHolder(View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            coursePrice = itemView.findViewById(R.id.course_price);
            apiInfoContainer = itemView.findViewById(R.id.api_info_container);
            buyCourse = itemView.findViewById(R.id.course_buy);
            courseIcon = itemView.findViewById(R.id.courses_icon);
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

