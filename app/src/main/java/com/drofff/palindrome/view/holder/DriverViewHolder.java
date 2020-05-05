package com.drofff.palindrome.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.drofff.palindrome.R;
import com.drofff.palindrome.entity.Driver;

import static com.drofff.palindrome.utils.StringUtils.joinNonNullPartsWith;

public class DriverViewHolder extends RecyclerView.ViewHolder {

    private Context context;

    private ImageView photoView;
    private TextView nameView;
    private TextView licenceNumberView;

    public DriverViewHolder(@NonNull View itemView) {
        super(itemView);
        this.context = itemView.getContext();
        photoView = itemView.findViewById(R.id.driver_photo);
        nameView = itemView.findViewById(R.id.driver_name);
        licenceNumberView = itemView.findViewById(R.id.driver_licence_number);
    }

    public void displayDriver(Driver driver) {
        String driverName = getFullDriverName(driver);
        nameView.setText(driverName);
        String licenceNumber = driver.getLicenceNumber();
        licenceNumberView.setText(licenceNumber);
        String photoUrl = driver.getPhotoUrl();
        Glide.with(context).load(photoUrl)
                .into(photoView);
    }

    private String getFullDriverName(Driver driver) {
        String firstName = driver.getFirstName();
        String lastName = driver.getLastName();
        return joinNonNullPartsWith(" ", firstName, lastName);
    }

}
