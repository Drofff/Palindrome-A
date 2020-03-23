package com.drofff.palindrome.ui.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drofff.palindrome.R;
import com.drofff.palindrome.entity.Police;

import static com.drofff.palindrome.utils.AuthenticationUtils.getCurrentUser;

public class ProfileFragment extends Fragment {

    private View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        initUiComponents();
        Police currentUser = getCurrentUser();
        displayUserData(currentUser);
        return root;
    }

    private void initUiComponents() {
        EditText tokenNumberInput = root.findViewById(R.id.token_number_value);
        makeNonEditable(tokenNumberInput);
        EditText departmentInput = root.findViewById(R.id.department_value);
        makeNonEditable(departmentInput);
    }

    private void makeNonEditable(EditText editText) {
        editText.setKeyListener(null);
    }

    private void displayUserData(Police police) {
        setTextIntoViewWithId(police.getFullName(), R.id.profile_full_name);
        setTextIntoViewWithId(police.getPosition(), R.id.position);
        setTextIntoViewWithId(police.getTokenNumber(), R.id.token_number_value);
        setTextIntoViewWithId(police.getDepartment(), R.id.department_value);
        displayUserPhoto(police.getPhoto());
    }

    private void setTextIntoViewWithId(String text, int id) {
        TextView view = root.findViewById(id);
        view.setText(text);
    }

    private void displayUserPhoto(byte[] photo) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        ImageView imageView = root.findViewById(R.id.profile_image);
        imageView.setImageBitmap(bitmap);
    }

}
