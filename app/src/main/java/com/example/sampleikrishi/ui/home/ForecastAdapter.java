package com.example.sampleikrishi.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampleikrishi.R;
import com.squareup.picasso.Picasso;

import java.util.List;/**/

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private List<ForecastItem> forecastItems;
    private Context context;

    public ForecastAdapter(List<ForecastItem> forecastItems, Context context) {
        this.forecastItems = forecastItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastItem item = forecastItems.get(position);
        holder.dateTextView.setText(item.getDate());
        holder.tempTextView.setText("Max: " + item.getMaxTemp() + "°C, Min: " + item.getMinTemp() + "°C");
        holder.conditionTextView.setText(item.getCondition());

        // Load weather icon using Picasso library
        Picasso.get()
                .load(item.getIconUrl())
                .placeholder(R.drawable.placeholder_icon) // Placeholder image while loading
                .error(R.drawable.error_icon) // Error image if loading fails
                .into(holder.weatherIconImageView);
    }

    @Override
    public int getItemCount() {
        return forecastItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView, tempTextView, conditionTextView;
        public ImageView weatherIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            conditionTextView = itemView.findViewById(R.id.conditionTextView);
            weatherIconImageView = itemView.findViewById(R.id.weatherIconImageView);
        }
    }
}
