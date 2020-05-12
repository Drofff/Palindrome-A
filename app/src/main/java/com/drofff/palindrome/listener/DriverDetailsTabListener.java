package com.drofff.palindrome.listener;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.drofff.palindrome.adapter.ArrayViewAdapter;
import com.drofff.palindrome.dto.CarDto;
import com.drofff.palindrome.dto.ViolationDto;
import com.drofff.palindrome.type.Displayable;
import com.google.android.material.tabs.TabLayout;

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
