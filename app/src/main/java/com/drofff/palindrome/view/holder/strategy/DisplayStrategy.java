package com.drofff.palindrome.view.holder.strategy;

import android.view.View;

import com.drofff.palindrome.type.Displayable;

public interface DisplayStrategy {

    void setRootView(View view);

    void display(Displayable displayable);

}
