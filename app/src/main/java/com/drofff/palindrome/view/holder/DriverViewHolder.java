package com.drofff.palindrome.view.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.drofff.palindrome.DriverActivity;
import com.drofff.palindrome.R;
import com.drofff.palindrome.entity.Driver;

import static com.drofff.palindrome.constants.ParameterConstants.DRIVER;
import static com.drofff.palindrome.constants.ParameterConstants.SEARCH_QUERY;
import static com.drofff.palindrome.utils.StringUtils.joinNonNullPartsWith;

public class DriverViewHolder extends RecyclerView.ViewHolder {

    private final Context context;

    private final ImageView photoView;
    private final TextView nameView;
    private final TextView licenceNumberView;

    private final String searchQuery;

    private Driver displayedDriver;

    public DriverViewHolder(@NonNull View itemView, String searchQuery) {
        super(itemView);
        this.context = itemView.getContext();
        this.searchQuery = searchQuery;
        photoView = itemView.findViewById(R.id.driver_photo);
        nameView = itemView.findViewById(R.id.driver_name);
        licenceNumberView = itemView.findViewById(R.id.driver_licence_number);
        registerViewDriverListenerAt(itemView);
    }

    private void registerViewDriverListenerAt(View view) {
        view.setOnClickListener(v -> viewDriver());
    }

    private void viewDriver() {
        if(displayedDriver != null) {
            Intent intent = new Intent(context, DriverActivity.class);
            intent.putExtra(DRIVER, displayedDriver);
            intent.putExtra(SEARCH_QUERY, searchQuery);
            context.startActivity(intent);
        }
    }

    public void displayDriver(Driver driver) {
        displayedDriver = driver;
        String fullName = getDriverFullName(driver);
        nameView.setText(fullName);
        String licenceNumber = driver.getLicenceNumber();
        licenceNumberView.setText(licenceNumber);
        Glide.with(context).load(driver.getPhotoUrl())
                .into(photoView);
    }

    private String getDriverFullName(Driver driver) {
        String firstName = driver.getFirstName();
        String lastName = driver.getLastName();
        return joinNonNullPartsWith(" ", firstName, lastName);
    }

}
