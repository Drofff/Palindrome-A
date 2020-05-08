package com.drofff.palindrome.view.holder.strategy;

import android.view.View;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class CollapseDisplayStrategy implements DisplayStrategy {

    private View rootView;

    private int collapseState = GONE;

    @Override
    public void setRootView(View view) {
        rootView = view;
        registerCollapseListenerAt(view);
        updateCollapseView(collapseState);
    }

    private void registerCollapseListenerAt(View view) {
        view.setOnClickListener(v -> {
            switchCollapseState();
            updateCollapseView(collapseState);
        });
    }

    private void switchCollapseState() {
        collapseState = collapseState == VISIBLE ? GONE : VISIBLE;
    }

    protected abstract void updateCollapseView(int collapseState);

    View getRootView() {
        return rootView;
    }

}
