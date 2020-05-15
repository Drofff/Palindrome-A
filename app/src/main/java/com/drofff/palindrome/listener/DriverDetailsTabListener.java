package com.drofff.palindrome.listener;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.drofff.palindrome.adapter.ArrayViewAdapter;
import com.drofff.palindrome.dto.CarDto;
import com.drofff.palindrome.dto.ViolationDto;
import com.drofff.palindrome.type.Displayable;
import com.google.android.material.tabs.TabLayout;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.drofff.palindrome.R.id.no_details_text_view;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;

public class DriverDetailsTabListener implements TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {

    private static final String LOG_TAG = DriverDetailsTabListener.class.getName();

    private final RecyclerView detailsView;

    private final ArrayViewAdapter<CarDto> carsViewAdapter;
    private final ArrayViewAdapter<ViolationDto> violationsViewAdapter;

    public DriverDetailsTabListener(RecyclerView detailsView, ArrayViewAdapter<CarDto> carsViewAdapter,
                                    ArrayViewAdapter<ViolationDto> violationsViewAdapter) {
        this.detailsView = detailsView;
        this.carsViewAdapter = carsViewAdapter;
        this.violationsViewAdapter = violationsViewAdapter;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        ArrayViewAdapter<? extends Displayable> selectedAdapter = tab.getPosition() == 0 ? carsViewAdapter :
                violationsViewAdapter;
        detailsView.setAdapter(selectedAdapter);
        updateNoDetailsTextViewWithAdapter(selectedAdapter);
    }

    private void updateNoDetailsTextViewWithAdapter(ArrayViewAdapter<?> adapter) {
        int visibility = isEmptyAdapter(adapter) ? VISIBLE : INVISIBLE;
        TextView noDetailsTextView = getNoDetailsTextView();
        noDetailsTextView.setVisibility(visibility);
    }

    private boolean isEmptyAdapter(ArrayViewAdapter<?> adapter) {
        return adapter.getItemCount() == 0;
    }

    private TextView getNoDetailsTextView() {
        View rootView = detailsView.getRootView();
        return rootView.findViewById(no_details_text_view);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        String tabLabel = getTabLabel(tab);
        Log.d(LOG_TAG, "Tab " + tabLabel + " unselected");
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        String tabLabel = getTabLabel(tab);
        Log.d(LOG_TAG, "Tab " + tabLabel + " reselected");
    }

    private String getTabLabel(TabLayout.Tab tab) {
        CharSequence tabText = tab.getText();
        validateNotNull(tabText, "Tab text should not be null");
        return tabText.toString();
    }

}
