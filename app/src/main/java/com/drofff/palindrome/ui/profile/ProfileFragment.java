package com.drofff.palindrome.ui.profile;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.drofff.palindrome.R;
import com.drofff.palindrome.entity.Police;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.view.View.VISIBLE;
import static com.drofff.palindrome.utils.AuthenticationUtils.getCurrentUser;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;

public class ProfileFragment extends Fragment {

    private static final Executor PROFILE_EXECUTOR = Executors.newSingleThreadExecutor();

    private View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        showProgressBar();
        PROFILE_EXECUTOR.execute(this::displayPoliceProfile);
        return root;
    }

    private void showProgressBar() {
        ProgressBar progressBar = root.findViewById(R.id.profile_loader);
        progressBar.setVisibility(VISIBLE);
    }

    private void displayPoliceProfile() {
        Police police = getCurrentUser();
        Activity activity = getActivity();
        validateNotNull(activity, "Activity is null");
        activity.runOnUiThread(() -> displayUserData(police));
    }

    private void displayUserData(Police police) {
        setTextIntoViewWithId(police.getFullName(), R.id.profile_full_name);
        setTextIntoViewWithId(police.getPosition(), R.id.position);
        setTextIntoViewWithId(police.getTokenNumber(), R.id.token_number_value);
        setTextIntoViewWithId(police.getDepartment(), R.id.department_value);
        displayUserPhoto(police.getPhotoUrl());
        hideProgressBar();
    }

    private void setTextIntoViewWithId(String text, int id) {
        TextView view = root.findViewById(id);
        view.setText(text);
    }

    private void displayUserPhoto(String photoUrl) {
        ImageView imageView = root.findViewById(R.id.profile_image);
        Glide.with(this)
                .load(photoUrl)
                .into(imageView);
    }

    private void hideProgressBar() {
        ProgressBar progressBar = root.findViewById(R.id.profile_loader);
        progressBar.setVisibility(View.INVISIBLE);
    }

}
