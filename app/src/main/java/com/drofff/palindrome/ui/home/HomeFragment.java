package com.drofff.palindrome.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.drofff.palindrome.R;
import com.drofff.palindrome.SearchActivity;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static com.drofff.palindrome.constants.ParameterConstants.SEARCH_QUERY;

public class HomeFragment extends Fragment {

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        this.context = root.getContext();
        EditText searchField = root.findViewById(R.id.search_field);
        registerSearchListenerAt(searchField);
        return root;
    }

    private void registerSearchListenerAt(EditText searchField) {
        searchField.setOnKeyListener((view, keyCode, keyEvent) -> {
            processSearchEvent(searchField, keyEvent);
            return true;
        });
    }

    private void processSearchEvent(EditText searchField, KeyEvent keyEvent) {
        if(isSearchSubmittedEvent(keyEvent)) {
            String searchQuery = searchField.getText().toString();
            startSearchBy(searchQuery);
        }
    }

    private boolean isSearchSubmittedEvent(KeyEvent keyEvent) {
        return keyEvent.getAction() == ACTION_DOWN &&
                keyEvent.getKeyCode() == KEYCODE_ENTER;
    }

    private void startSearchBy(String query) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(SEARCH_QUERY, query);
        context.startActivity(intent);
    }

}