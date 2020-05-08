package com.drofff.palindrome.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drofff.palindrome.entity.Driver;
import com.drofff.palindrome.view.holder.DriverViewHolder;

import java.util.List;

import static android.view.LayoutInflater.from;
import static com.drofff.palindrome.R.layout.driver_view;

public class DriversViewAdapter extends RecyclerView.Adapter<DriverViewHolder> {

    private final List<Driver> drivers;
    private final String searchQuery;

    public DriversViewAdapter(List<Driver> drivers, String searchQuery) {
        this.drivers = drivers;
        this.searchQuery = searchQuery;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View driverView = from(context).inflate(driver_view, parent, false);
        return new DriverViewHolder(driverView, searchQuery);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        Driver driver = drivers.get(position);
        holder.displayDriver(driver);
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

}
